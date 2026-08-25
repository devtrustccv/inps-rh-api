package cv.inps.rh.shared.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamNotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Envio e registo de notificações. Cada envio grava uma linha em RH_T_NOTIFICACAO, mesmo quando
 * o email falha — é isso que permite ao RH ver depois quem ficou por notificar.
 *
 * <p>Os destinatários chegam aqui já resolvidos em endereços por
 * {@link NotificacaoDestinatarioResolver}; este service não conhece as regras de quem recebe o quê.</p>
 */
@Service
@RequiredArgsConstructor
public class NotificacaoDispatchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoDispatchService.class);

  private final ParamNotificacaoEntityRepository paramNotificacaoRepository;
  private final NotificacaoEntityRepository notificacaoRepository;
  private final EmailService emailService;

  public void enviar(String tipoNotificacao, String emailDestino, String nomeReceptor,
                     Long referenciaId, String referenciaName, UUID referenciaUuid,
                     FuncionarioEntity funId, Map<String, String> vars) {

    var conteudo = conteudoDe(tipoNotificacao, vars, null);

    gravarEEnviar(tipoNotificacao, emailDestino, nomeReceptor, null,
        referenciaId, referenciaName, referenciaUuid, funId,
        conteudo.assunto(), conteudo.corpo());
  }

  /**
   * Envio a partir do ecrã de notificação: um email — e um registo em RH_T_NOTIFICACAO — por
   * cada destinatário resolvido, como a spec exige.
   *
   * <p>A {@code mensagemCustom} é o campo "Mensagem da notificação" escrito pelo utilizador.
   * Quando vem preenchida substitui o corpo do template; quando vem vazia usa-se o template de
   * RH_T_PARAM_NOTIFICACAO. O assunto vem sempre do template — o ecrã não o expõe.</p>
   *
   * <p>Uma falha de envio a um destinatário não interrompe os restantes: cada um é gravado com o
   * seu próprio estado (Enviado/Erro), que é o que permite ao RH ver depois quem ficou por
   * notificar.</p>
   */
  public void enviarParaDestinatarios(String tipoNotificacao,
                                      Collection<NotificacaoDestinatarioResolver.Destinatario> destinatarios,
                                      String mensagemCustom,
                                      Long referenciaId, String referenciaName, UUID referenciaUuid,
                                      Map<String, String> vars) {

    if (destinatarios == null || destinatarios.isEmpty()) {
      LOGGER.warn("Notificação {} sem destinatários resolvidos — nada a enviar", tipoNotificacao);
      return;
    }

    var conteudo = conteudoDe(tipoNotificacao, vars, mensagemCustom);

    for (var destinatario : destinatarios) {
      gravarEEnviar(tipoNotificacao, destinatario.email(), destinatario.nome(), destinatario.tipo(),
          referenciaId, referenciaName, referenciaUuid, destinatario.funcionario(),
          conteudo.assunto(), conteudo.corpo());
    }
  }

  private record Conteudo(String assunto, String corpo) {}

  private Conteudo conteudoDe(String tipoNotificacao, Map<String, String> vars, String mensagemCustom) {
    var paramOpt = paramNotificacaoRepository.findByTipoNotificacao(tipoNotificacao);
    if (paramOpt.isEmpty()) {
      // Template não configurado: a notificação é sempre gravada (auditoria obrigatória),
      // mas sem assunto/corpo definidos — RH deve configurar o template no backoffice.
      LOGGER.warn("Template de notificação não encontrado para tipo '{}' — registo gravado sem conteúdo", tipoNotificacao);
    }

    String assunto = paramOpt.map(p -> substituir(p.getAssunto(), vars)).orElse(null);
    String corpo = StringUtils.hasText(mensagemCustom)
        ? substituir(mensagemCustom, vars)
        : paramOpt.map(p -> substituir(p.getCorpo(), vars)).orElse(null);

    return new Conteudo(assunto, corpo);
  }

  private void gravarEEnviar(String tipoNotificacao, String emailDestino, String nomeReceptor,
                             String destinatario, Long referenciaId, String referenciaName,
                             UUID referenciaUuid, FuncionarioEntity funId,
                             String assunto, String corpo) {

    String estado = "Pendente";
    if (StringUtils.hasText(emailDestino)) {
      try {
        emailService.sendEmail(emailDestino, assunto != null ? assunto : "", corpo != null ? corpo : "");
        estado = "Enviado";
      } catch (Exception e) {
        LOGGER.error("Erro ao enviar email notificação {} para {}: {}", tipoNotificacao, emailDestino, e.getMessage());
        estado = "Erro";
      }
    }

    var notificacao = new NotificacaoEntity();
    notificacao.setTipoNotificacao(tipoNotificacao);
    notificacao.setReferenciaId(referenciaId);
    notificacao.setReferenciaName(referenciaName);
    notificacao.setReferenciaUuid(referenciaUuid);
    notificacao.setAssunto(assunto);
    notificacao.setMessage(corpo);
    notificacao.setEmail(emailDestino);
    notificacao.setNomeReceptor(nomeReceptor);
    notificacao.setDestinatario(destinatario);
    notificacao.setDataEnvio(LocalDate.now());
    notificacao.setEstado(estado);
    notificacao.setUuid(UuidCreator.getTimeOrderedEpoch());
    notificacao.setFunId(funId);
    notificacaoRepository.save(notificacao);
  }

  private String substituir(String template, Map<String, String> vars) {
    if (template == null || vars == null || vars.isEmpty()) return template;
    for (var entry : vars.entrySet()) {
      template = template.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
    }
    return template;
  }
}

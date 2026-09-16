package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.application.constants.TipoDestinatarioNotificacao;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.domain.service.NotificacaoDestinatarioResolver;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Notificações da missão ao próprio colaborador. O email vem dos contactos do funcionário; sem
 * email a notificação fica só gravada ("Pendente") e aparece no portal. Uma falha de envio não
 * interrompe o fluxo: fica gravada com estado "Erro".
 *
 * <p>A referência é a linha do colaborador na missão (RH_T_MISSAO_COLABORADOR), a mesma do aviso de
 * logística, para o ecrã "Ver Notificação" as encontrar.
 */
@Component
@RequiredArgsConstructor
public class MissaoNotificacaoColaborador {

  public static final String TIPO_CONFIRMACAO_PEDIDO = "MISSAO_CONFIRMACAO_PEDIDO";
  public static final String TIPO_AJUDA_CUSTO = "MISSAO_AJUDA_CUSTO";
  public static final String TIPO_ALTERACAO = "MISSAO_ALTERACAO";

  private static final Logger LOGGER = LoggerFactory.getLogger(MissaoNotificacaoColaborador.class);

  private final NotificacaoDestinatarioResolver destinatarioResolver;
  private final EmailService emailService;
  private final NotificacaoEntityRepository notificacaoRepository;

  public void enviar(MissaoColaboradorEntity colab, String tipoNotificacao, MissaoProcessoSupport.Conteudo conteudo) {
    var funcionario = colab.getFunId();
    if (funcionario == null)
      return;

    var email = destinatarioResolver.resolver(funcionario, List.of(TipoDestinatarioNotificacao.COLABORADOR)).stream()
        .findFirst()
        .map(NotificacaoDestinatarioResolver.Destinatario::email)
        .orElse(null);

    var estado = "Pendente";
    if (email != null) {
      try {
        emailService.sendEmail(email, conteudo.assunto(), conteudo.corpo());
        estado = "Enviado";
      } catch (Exception ex) {
        LOGGER.warn("Erro ao enviar notificação {} para {}: {}", tipoNotificacao, email, ex.getMessage());
        estado = "Erro";
      }
    }

    var n = new NotificacaoEntity();
    n.setUuid(UuidCreator.getTimeOrderedEpoch());
    n.setTipoNotificacao(tipoNotificacao);
    n.setReferenciaId(colab.getId());
    n.setReferenciaName(TableName.RH_T_MISSAO_COLABORADOR.name());
    n.setReferenciaUuid(colab.getUuid());
    n.setAssunto(conteudo.assunto());
    n.setMessage(conteudo.corpo());
    n.setEmail(email);
    n.setNomeReceptor(funcionario.getNome());
    n.setDestinatario(TipoDestinatarioNotificacao.COLABORADOR.name());
    n.setFunId(funcionario);
    n.setDataEnvio(LocalDate.now());
    n.setEstado(estado);
    notificacaoRepository.save(n);
  }
}

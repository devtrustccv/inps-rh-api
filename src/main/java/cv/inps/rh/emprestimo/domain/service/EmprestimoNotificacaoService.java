package cv.inps.rh.emprestimo.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.domain.service.constants.EtapaEmprestimo;
import cv.inps.rh.emprestimo.application.constants.ParecerProcesso;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoNotificacao;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.IAMUserProfileEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoDecisaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Notificações do processo de empréstimo. Decisão da Comissão Executiva → email a quem fez a
 * Validação Responsável na Análise RH (RH_T_PEDIDO_DECISAO.UTILIZADOR_OBS_RESPONSAVEL = sub do JWT;
 * o email vem do perfil IAM). Fica registada em RH_T_NOTIFICACAO, como as restantes notificações.
 * Uma falha no envio nunca impede a gravação da decisão.
 */
@RequiredArgsConstructor
@Service
public class EmprestimoNotificacaoService {

  public static final String TIPO_DECISAO_AUTORIZACAO = "EMPRESTIMO_DECISAO_AUTORIZACAO";
  private static final String DESTINATARIO_RESPONSAVEL_ANALISE_RH = "RESPONSAVEL_ANALISE_RH";

  private static final Logger LOGGER = LoggerFactory.getLogger(EmprestimoNotificacaoService.class);

  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final IAMUserProfileEntityRepository iamUserProfileEntityRepository;
  private final EmailService emailService;
  private final NotificacaoEntityRepository notificacaoRepository;

  public void notificarDecisaoAutorizacao(EmprestimoEntity loan, EtapaEmprestimo etapaAnaliseRh,
                                          ParecerProcesso parecer, String observacao) {
    try {
      enviar(loan, etapaAnaliseRh, parecer, observacao);
    } catch (Exception ex) {
      LOGGER.warn("Notificação da decisão da Comissão Executiva não enviada (empréstimo {}): {}",
          loan.getUuid(), ex.getMessage());
    }
  }

  private void enviar(EmprestimoEntity loan, EtapaEmprestimo etapaAnaliseRh, ParecerProcesso parecer, String observacao) {

    var sub = pedidoDecisaoEntityRepository
        .findByPedidoAndEtapaAndEstado(loan.getPedido(), etapaAnaliseRh.name(), Estado.A.name())
        .map(PedidoDecisaoEntity::getUtilizadorObservacaoResponsavel)
        .filter(StringUtils::hasText)
        .orElse(null);
    if (sub == null) {
      LOGGER.info("Empréstimo {} sem Validação Responsável na Análise RH — sem notificação", loan.getUuid());
      return;
    }

    var perfil = iamUserProfileEntityRepository.findBySub(sub);
    var email = perfil.map(IAMUserProfileEntity::getEmail).filter(StringUtils::hasText).map(String::trim)
        .orElse(sub.contains("@") ? sub : null);
    var nomeResponsavel = perfil
        .map(p -> StringUtils.hasText(p.getFullName()) ? p.getFullName() : p.getUsername())
        .orElse(sub);

    var funcionario = loan.getTiprel() != null ? loan.getTiprel().getFunId() : null;
    var colaborador = funcionario != null ? funcionario.getNome() : "-";
    var resultado = switch (parecer) {
      case FAVORAVEL -> "autorizado";
      case DESFAVORAVEL -> "não autorizado";
      case RETIFICACAO -> "devolvido para retificação";
      default -> parecer.name();
    };

    var assunto = "Empréstimo de " + colaborador + " " + resultado + " pela Comissão Executiva";
    var corpo = "Caro(a) " + nomeResponsavel + ",\n\n"
        + "O pedido de empréstimo do(a) colaborador(a) " + colaborador
        + ", em que fez a Validação Responsável na Análise RH, foi " + resultado
        + " pela Comissão Executiva."
        + (StringUtils.hasText(observacao) ? "\n\nObservação: " + observacao : "");

    var estado = EstadoNotificacao.POR_ENVIAR.name();
    if (email != null) {
      try {
        emailService.sendEmail(email, assunto, org.springframework.web.util.HtmlUtils.htmlEscape(corpo).replace("\n", "<br/>"));
        estado = EstadoNotificacao.ENVIADO.name();
      } catch (Exception ex) {
        LOGGER.warn("Erro ao enviar notificação {} para {}: {}", TIPO_DECISAO_AUTORIZACAO, email, ex.getMessage());
        estado = EstadoNotificacao.ERRO.name();
      }
    } else {
      LOGGER.warn("Responsável {} sem email no perfil IAM — notificação {} fica por enviar", sub, TIPO_DECISAO_AUTORIZACAO);
    }

    var n = new NotificacaoEntity();
    n.setUuid(UuidCreator.getTimeOrderedEpoch());
    n.setTipoNotificacao(TIPO_DECISAO_AUTORIZACAO);
    n.setReferenciaId(loan.getId());
    n.setReferenciaName(TableName.RH_T_EMPRESTIMO.name());
    n.setReferenciaUuid(UUID.fromString(loan.getUuid()));
    n.setAssunto(assunto);
    n.setMessage(corpo);
    n.setEmail(email);
    n.setNomeReceptor(nomeResponsavel);
    n.setDestinatario(DESTINATARIO_RESPONSAVEL_ANALISE_RH);
    n.setFunId(funcionario);
    n.setDataEnvio(LocalDate.now());
    n.setEstado(estado);
    notificacaoRepository.save(n);
  }
}

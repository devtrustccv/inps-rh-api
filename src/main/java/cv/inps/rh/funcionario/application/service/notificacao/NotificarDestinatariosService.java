package cv.inps.rh.funcionario.application.service.notificacao;

import cv.inps.rh.shared.application.constants.TipoDestinatarioNotificacao;
import cv.inps.rh.shared.application.dto.NotificarEnvioRequestDTO;
import cv.inps.rh.shared.domain.service.NotificacaoDestinatarioResolver;
import cv.inps.rh.shared.domain.service.NotificacaoDispatchService;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Envio de notificações a partir do ecrã: recebe as escolhas do utilizador, resolve os
 * destinatários e delega o envio.
 *
 * <p>É o ponto de entrada único do bloco de notificação — os módulos de negócio não precisam de
 * o embutir nos seus próprios fluxos, porque o frontend chama este endpoint directamente depois
 * de gravar o registo de origem.</p>
 */
@Service
@RequiredArgsConstructor
public class NotificarDestinatariosService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificarDestinatariosService.class);

  private final FuncionarioEntityRepository funcionarioRepository;
  private final NotificacaoDestinatarioResolver destinatarioResolver;
  private final NotificacaoDispatchService notificacaoDispatchService;

  @Transactional
  public Map<String, ?> notificar(NotificarEnvioRequestDTO request) {

    // "Notificar? = Não" é um pedido legítimo, não um erro: o ecrã pode enviar sempre o bloco e
    // deixar o backend decidir. Responde-se com enviados=0 para o frontend poder distinguir de
    // um envio que não encontrou destinatários.
    if (!request.deveNotificar()) {
      return Map.of("enviados", 0, "message", "Notificação não solicitada.");
    }

    var funcionario = funcionarioRepository.findByUuidOrThrow(UUID.fromString(request.getFuncionarioId()));

    var tipos = new LinkedHashSet<TipoDestinatarioNotificacao>();
    if (request.getDestinatarios() != null) {
      request.getDestinatarios().stream()
          .filter(StringUtils::hasText)
          .map(TipoDestinatarioNotificacao::fromValorOrThrow)
          .forEach(tipos::add);
    }

    var destinatarios = destinatarioResolver.comEmailsAdicionais(
        destinatarioResolver.resolver(funcionario, tipos),
        request.getEmailsAdicionais());

    if (destinatarios.isEmpty()) {
      LOGGER.warn("Notificação {} para funcionário {} sem destinatários com email",
          request.getTipoNotificacao(), request.getFuncionarioId());
      return Map.of("enviados", 0, "message", "Nenhum destinatário com email foi encontrado.");
    }

    var vars = Map.of(
        "nome", funcionario.getNome() != null ? funcionario.getNome() : "");

    notificacaoDispatchService.enviarParaDestinatarios(
        request.getTipoNotificacao(),
        destinatarios,
        request.getMensagem(),
        request.getReferenciaId(),
        request.getReferenciaName(),
        StringUtils.hasText(request.getReferenciaUuid()) ? UUID.fromString(request.getReferenciaUuid()) : null,
        vars);

    return Map.of(
        "enviados", destinatarios.size(),
        "message", "Notificação enviada para " + destinatarios.size() + " destinatário(s).");
  }
}

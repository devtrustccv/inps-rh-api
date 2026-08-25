package cv.inps.rh.funcionario.application.service.notificacao;

import cv.inps.rh.shared.application.constants.TipoDestinatarioNotificacao;
import cv.inps.rh.shared.application.dto.NotificarEnvioRequestDTO;
import cv.inps.rh.shared.domain.service.NotificacaoDestinatarioResolver;
import cv.inps.rh.shared.domain.service.NotificacaoDispatchService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Envio de notificações a partir do ecrã: recebe as escolhas do utilizador, resolve os
 * destinatários e delega o envio.
 *
 * <p>É o ponto de entrada único do bloco de notificação — os módulos de negócio não precisam de
 * o embutir nos seus próprios fluxos, porque o frontend chama este endpoint directamente depois
 * de gravar o registo de origem.</p>
 *
 * <p>O ecrã escolhe apenas <em>tipos</em> de destinatário; os endereços nunca vêm no pedido, são
 * sempre resolvidos aqui.</p>
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

    var tipos = tiposPedidos(request);

    var funcionario = funcionarioRepository.findByUuidOrThrow(UUID.fromString(request.getFuncionarioId()));

    var destinatarios = destinatarioResolver.resolver(funcionario, tipos);

    // Tipos que ficaram sem endereço: o utilizador já não escolhe emails, por isso tem de saber
    // qual dos destinatários que pediu não foi notificado — e porquê — em vez de ver só um total.
    var omitidos = tipos.stream()
        .map(Enum::name)
        .filter(tipo -> destinatarios.stream().noneMatch(d -> tipo.equals(d.tipo())))
        .toList();

    if (destinatarios.isEmpty()) {
      LOGGER.warn("Notificação {} para funcionário {} sem destinatários com email — tipos pedidos: {}",
          request.getTipoNotificacao(), request.getFuncionarioId(), omitidos);
      return resposta(0, "Nenhum destinatário com email foi encontrado.", List.of(), omitidos);
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

    // Sem Map.of: o nome de um destinatário pode ser nulo e Map.of não aceita nulos.
    List<Map<String, String>> enviadosPara = destinatarios.stream()
        .map(d -> {
          var linha = new LinkedHashMap<String, String>();
          linha.put("tipo", d.tipo());
          linha.put("nome", d.nome());
          linha.put("email", d.email());
          return (Map<String, String>) linha;
        })
        .toList();

    return resposta(destinatarios.size(),
        "Notificação enviada para " + destinatarios.size() + " destinatário(s).",
        enviadosPara, omitidos);
  }

  /**
   * Converte as etiquetas do ecrã nos valores do enum, rejeitando qualquer outra coisa. A lista
   * é obrigatória aqui — e não por bean validation — porque com {@code notificar = false} o
   * pedido é legítimo sem destinatários e nem chega a este ponto.
   */
  private LinkedHashSet<TipoDestinatarioNotificacao> tiposPedidos(NotificarEnvioRequestDTO request) {
    var tipos = new LinkedHashSet<TipoDestinatarioNotificacao>();
    if (request.getDestinatarios() != null) {
      request.getDestinatarios().stream()
          .filter(StringUtils::hasText)
          .map(TipoDestinatarioNotificacao::fromValorOrThrow)
          .forEach(tipos::add);
    }

    if (tipos.isEmpty()) {
      throw IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST,
          "Indique pelo menos um destinatário da notificação.");
    }

    return tipos;
  }

  private Map<String, ?> resposta(int enviados, String message,
                                  List<Map<String, String>> destinatarios, List<String> semEmail) {
    // LinkedHashMap e não Map.of: a resposta tem de manter a ordem e aceitar listas vazias.
    var body = new LinkedHashMap<String, Object>();
    body.put("enviados", enviados);
    body.put("message", message);
    body.put("destinatarios", destinatarios);
    body.put("semEmail", semEmail);
    return body;
  }
}

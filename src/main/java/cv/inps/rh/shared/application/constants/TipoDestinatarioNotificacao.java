package cv.inps.rh.shared.application.constants;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;

/**
 * Destinatários possíveis do multiselect "Destinatários da notificação".
 *
 * <p>Os valores espelham as linhas do domínio {@link Domains#DESTINATARIO_NOTIFICACAO} em
 * RH_T_DOMAINS — é de lá que o frontend obtém a lista e as descrições. Este enum existe apenas
 * para o backend poder decidir <em>como</em> resolver cada valor num email; acrescentar aqui um
 * valor sem a linha correspondente no domínio deixa-o invisível no ecrã, e vice-versa.</p>
 */
public enum TipoDestinatarioNotificacao {

  /** Email do próprio colaborador, obtido dos contactos do funcionário. */
  COLABORADOR,

  /** Responsável da direção/secção onde o colaborador está colocado (RH_T_RESPONSAVEL). */
  RESPONSAVEL_COLABORADOR,

  /** Utilizador autenticado que criou o registo. Ver o TODO em NotificacaoDispatchService. */
  RESPONSAVEL_REGISTO;

  public static Optional<TipoDestinatarioNotificacao> fromValor(String valor) {
    if (valor == null) return Optional.empty();
    var chave = valor.trim().toUpperCase();
    return Arrays.stream(values()).filter(v -> v.name().equals(chave)).findFirst();
  }

  public static TipoDestinatarioNotificacao fromValorOrThrow(String valor) {
    return fromValor(valor).orElseThrow(() -> IgrpResponseStatusException.of(
        HttpStatus.BAD_REQUEST, "Destinatário de notificação inválido: " + valor));
  }
}

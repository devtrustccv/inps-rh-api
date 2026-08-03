package cv.inps.rh.shared.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import jakarta.persistence.EntityManager;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ValidationUtil {

  private ValidationUtil() {
  }

  private static final List<String> DECISOES_APROVACAO = List.of("S", "SIM");
  private static final List<String> DECISOES_REJEICAO = List.of("N", "NAO", "NÃO");

  /**
   * Valida a decisão de validação/aprovação. Aceita, sem distinção de maiúsculas,
   * tanto a convenção curta ({@code "S"}/{@code "N"}) como a convenção do frontend
   * ({@code "SIM"}/{@code "NAO"}).
   */
  public static void validateDecision(String decision) {
    if (!isDecisaoValida(decision))
      throw IgrpResponseStatusException.badRequest("Decisão inválida: " + decision);
  }

  /**
   * Valida a decisão e devolve {@code true} se for uma aprovação ({@code "S"}/{@code "SIM"}),
   * {@code false} se for uma rejeição ({@code "N"}/{@code "NAO"}).
   *
   * @throws IgrpResponseStatusException se a decisão não for reconhecida
   */
  public static boolean isAprovado(String decision) {
    validateDecision(decision);
    return DECISOES_APROVACAO.contains(normalizeDecision(decision));
  }

  private static boolean isDecisaoValida(String decision) {
    String normalized = normalizeDecision(decision);
    return DECISOES_APROVACAO.contains(normalized) || DECISOES_REJEICAO.contains(normalized);
  }

  private static String normalizeDecision(String decision) {
    return decision == null ? null : decision.trim().toUpperCase();
  }

  public static <E extends Enum<E>> Optional<E> getEnum(Class<E> enumClass, String value) {
    if (value == null) return Optional.empty();

    String normalized = value.trim().toUpperCase();

    try {
      return Optional.of(Enum.valueOf(enumClass, normalized));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public static boolean isValidNumberId(Long number) {
    return number != null && number > 0;
  }

  public static <T> T ref(EntityManager em, Class<T> type, Long id) {
    return isValidNumberId(id) ? em.getReference(type, id) : null;
  }

  public static String trimToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }

  /**
   * Converte um identificador de recurso em UUID, devolvendo 400 quando é inválido.
   *
   * <p>Um identificador malformado é erro de quem chama, não do servidor: sem isto
   * {@code UUID.fromString} lança {@code IllegalArgumentException} e o pedido termina
   * em 500, ou — pior — é apanhado por um {@code catch} que devolve 200 com corpo
   * vazio, deixando o cliente sem saber o que correu mal.
   *
   * @param nomeCampo nome do campo tal como o cliente o conhece, para a mensagem de erro
   */
  public static UUID parseUuid(String valor, String nomeCampo) {
    var v = trimToNull(valor);
    if (v == null)
      throw IgrpResponseStatusException.badRequest(nomeCampo + " é obrigatório");
    try {
      return UUID.fromString(v);
    } catch (IllegalArgumentException e) {
      throw IgrpResponseStatusException.badRequest(
          nomeCampo + " inválido: '" + valor + "' não é um identificador válido");
    }
  }

  /**
   * Versão para filtros opcionais: devolve vazio quando o valor não vem ou está
   * malformado, para o filtro ser simplesmente ignorado em vez de derrubar a listagem.
   */
  public static Optional<UUID> parseUuidOpcional(String valor) {
    var v = trimToNull(valor);
    if (v == null)
      return Optional.empty();
    try {
      return Optional.of(UUID.fromString(v));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private static final int NIB_LENGTH = 21;
  private static final java.util.regex.Pattern DIGITS_ONLY = java.util.regex.Pattern.compile("^\\d+$");
  private static final java.util.regex.Pattern PHONE_PATTERN = java.util.regex.Pattern.compile("^\\+?\\d{7,15}$");
  private static final java.util.regex.Pattern EMAIL_PATTERN = java.util.regex.Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  public static String sanitizeNib(String nib) {
    String trimmed = trimToNull(nib);
    if (trimmed != null) {
      if (!DIGITS_ONLY.matcher(trimmed).matches()) {
        throw IgrpResponseStatusException.badRequest(
            "O NIB introduzido é inválido. O NIB deve conter apenas dígitos.");
      }
      if (trimmed.length() != NIB_LENGTH) {
        throw IgrpResponseStatusException.badRequest(
            "O NIB introduzido é inválido. O NIB deve ter exactamente " + NIB_LENGTH + " dígitos.");
      }
    }
    return trimmed;
  }

  public static void validateContacto(String tipoContacto, String valor) {
    if (tipoContacto == null || valor == null) return;
    String tipo = tipoContacto.trim().toUpperCase();
    switch (tipo) {
      case "CORREIO_ELETRONICO", "EMAIL" -> {
        if (!EMAIL_PATTERN.matcher(valor).matches()) {
          throw IgrpResponseStatusException.badRequest(
              "O endereço de correio eletrónico '" + valor + "' é inválido.");
        }
      }
      case "TELEFONE", "TELEMOVEL", "FAX" -> {
        String cleaned = valor.replaceAll("[\\s\\-()]", "");
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
          throw IgrpResponseStatusException.badRequest(
              "O número de " + tipoContacto.toLowerCase() + " '" + valor + "' é inválido. Deve conter entre 7 e 15 dígitos.");
        }
      }
      default -> { /* tipos desconhecidos: sem validação de formato */ }
    }
  }

  public static void validateValorNaoNegativo(BigDecimal valor) {
    if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
      throw IgrpResponseStatusException.badRequest("O valor não pode ser negativo.");
    }
  }

  public static void validatePercentagem(BigDecimal percentagem) {
    if (percentagem == null) return;
    if (percentagem.compareTo(BigDecimal.ZERO) < 0) {
      throw IgrpResponseStatusException.badRequest("A percentagem não pode ser negativa.");
    }
    if (percentagem.compareTo(BigDecimal.valueOf(100)) > 0) {
      throw IgrpResponseStatusException.badRequest("A percentagem não pode ser superior a 100.");
    }
  }

  public static void validateIntervaloData(LocalDate dataInicio, LocalDate dataFim) {
    if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
      throw IgrpResponseStatusException.badRequest(
          "A Data de Início não pode ser posterior à Data de Fim.");
    }
  }

}

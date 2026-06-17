package cv.inps.rh.shared.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import jakarta.persistence.EntityManager;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class ValidationUtil {

  private ValidationUtil() {
  }

  public static void validateDecision(String decision) {
    if (!List.of("S", "N").contains(decision))
      throw IgrpResponseStatusException.badRequest("Decisão inválida: " + decision);
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

  private static final int NIB_MAX_LENGTH = 21;
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
      if (trimmed.length() > NIB_MAX_LENGTH) {
        throw IgrpResponseStatusException.badRequest(
            "O NIB introduzido é inválido. O NIB não pode ter mais de " + NIB_MAX_LENGTH + " dígitos.");
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

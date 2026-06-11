package cv.inps.rh.shared.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import jakarta.persistence.EntityManager;
import org.springframework.util.StringUtils;

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

  public static String sanitizeNib(String nib) {
    String trimmed = trimToNull(nib);
    if (trimmed != null && trimmed.length() > NIB_MAX_LENGTH) {
      throw IgrpResponseStatusException.badRequest(
          "NIB não pode ter mais de " + NIB_MAX_LENGTH + " caracteres (recebido: " + trimmed.length() + ")");
    }
    return trimmed;
  }

}

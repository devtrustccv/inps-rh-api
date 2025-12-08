package cv.inps.rh.shared.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

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


}

package cv.inps.rh.shared.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.util.List;

public class ValidationUtil {

  private ValidationUtil() {
  }

  public static void validateDecision(String decision) {
    if (!List.of("S", "N").contains(decision))
      throw IgrpResponseStatusException.badRequest("Decisão inválida: " + decision);
  }

}

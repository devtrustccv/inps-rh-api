package cv.inps.rh.missaoservico.application.constants;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.util.Arrays;

/** Domínio PARECER. */
public enum Parecer {

  FAVORAVEL,
  DESFAVORAVEL;

  public static Parecer fromCodeOrThrow(String code) {
    return Arrays.stream(values())
        .filter(p -> p.name().equals(code))
        .findFirst()
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Parecer inválido: " + code));
  }
}

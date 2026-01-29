/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.constants;

import cv.igrp.framework.core.domain.IgrpEnum;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;


public enum AssiduidadeDiariaEstado implements IgrpEnum<String> {

  AUSENTE("AUSENTE", "ausente"),
    PENDENTE("PENDENTE", "pendente"),
    INJUSTIFICADA("INJUSTIFICADA", "injustificada"),
    JUSTIFICADA("JUSTIFICADA", "justificada"),
    CONFORME("CONFORME", "Em conformidade")
  ;

  private final String code;
  private final String description;

  AssiduidadeDiariaEstado(String code, String description) {
    this.code = code;
    this.description = description;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
  * Pre-built maps for fast lookup.
  */
  private static final Map<String, AssiduidadeDiariaEstado> CODE_MAP = Arrays.stream(values())
          .collect(Collectors.toMap(AssiduidadeDiariaEstado::getCode, Function.identity()));

  /**
  * Attempts to find the enum value associated with the given code.
  * @param code The code to look up
  * @return An Optional containing the enum value if found, empty Optional otherwise
  */
  public static Optional<AssiduidadeDiariaEstado> fromCode(String code) {
    return Optional.ofNullable(CODE_MAP.get(code));
  }

  /**
  * Finds the enum value associated with the given code or throws an exception if not found.
  * @param code The code to look up
  * @return The enum value for the given code
  * @throws IllegalArgumentException if no enum value exists for the given code
  */
  public static AssiduidadeDiariaEstado fromCodeOrThrow(String code) {
    return fromCode(code).orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST, "Invalid AssiduidadeDiariaEstado for this code: " + code));
  }

  /**
  * Returns a map of code to description.
  */
  public static Map<String, String> codeDescriptionMap() {
    return CODE_MAP.values().stream().collect(Collectors.toMap(AssiduidadeDiariaEstado::getCode, AssiduidadeDiariaEstado::getDescription));
  }

}

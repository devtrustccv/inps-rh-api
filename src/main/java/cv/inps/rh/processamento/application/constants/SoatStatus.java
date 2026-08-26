/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.constants;

import cv.igrp.framework.core.domain.IgrpEnum;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SoatStatus implements IgrpEnum<String> {

  F("F", "Finalizado"),
  A("A", "Activo");

  /**
   * Pre-built maps for fast lookup.
   */
  private static final Map<String, SoatStatus> CODE_MAP = Arrays.stream(values())
      .collect(Collectors.toMap(SoatStatus::getCode, Function.identity()));
  private final String code;
  private final String description;

  SoatStatus(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Attempts to find the enum value associated with the given code.
   *
   * @param code The code to look up
   * @return An Optional containing the enum value if found, empty Optional otherwise
   */
  public static Optional<SoatStatus> fromCode(String code) {
    return Optional.ofNullable(CODE_MAP.get(code));
  }

  /**
   * Finds the enum value associated with the given code or throws an exception if not found.
   *
   * @param code The code to look up
   * @return The enum value for the given code
   * @throws IllegalArgumentException if no enum value exists for the given code
   */
  public static SoatStatus fromCodeOrThrow(String code) {
    return fromCode(code).orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST, "Invalid SoatStatus for this code: " + code));
  }

  /**
   * Returns a map of code to description.
   */
  public static Map<String, String> codeDescriptionMap() {
    return CODE_MAP.values().stream().collect(Collectors.toMap(SoatStatus::getCode, SoatStatus::getDescription));
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getDescription() {
    return description;
  }

}

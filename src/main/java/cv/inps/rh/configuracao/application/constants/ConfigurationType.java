/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.constants;

import cv.igrp.framework.core.domain.IgrpEnum;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


public enum ConfigurationType implements IgrpEnum<String> {

  PARAM_VINCULO("param_vinculo_type", "PARAM_VINCULO"),
  PARAM_SITUACAO_LABORAL("situacao_laboral_type", "PARAM_SITUACAO_LABORAL"),
  PARAM_TIPO_CONTRATO_LABORAL("tipo_contrato_laboral_type", "PARAM_TIPO_CONTRATO_LABORAL"),
  PARAM_CARREIRA("carreira_type", "PARAM_CARREIRA"),
  PARAM_CARGO("cargo_type", "PARAM_CARGO"),
  PARAM_ESCALAO("escalao_type", "PARAM_ESCALAO"),
  PARAM_LOCAL_TRABALHO("local_trabalho_type", "PARAM_LOCAL_TRABALHO"),
  PARAM_SECCAO("seccao_type", "PARAM_SECCAO"),
  PARAM_TIPO_DOCUMENTO("tipo_documento_type", "PARAM_TIPO_DOCUMENTO"),
  PARAM_NOTIFICACAO("notificacao_type", "PARAM_NOTIFICACAO"),
  PARAM_TIPO_FALTA_AUSENCIA("tipo_falta_ausencia_type", "PARAM_TIPO_FALTA_AUSENCIA");

  private final String code;
  private final String description;

  ConfigurationType(String code, String description) {
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
  private static final Map<String, ConfigurationType> CODE_MAP = Arrays.stream(values())
      .collect(Collectors.toMap(ConfigurationType::getCode, Function.identity()));

  /**
   * Attempts to find the enum value associated with the given code.
   * @param code The code to look up
   * @return An Optional containing the enum value if found, empty Optional otherwise
   */
  public static Optional<ConfigurationType> fromCode(String code) {
    return Optional.ofNullable(CODE_MAP.get(code));
  }

  /**
   * Finds the enum value associated with the given code or throws an exception if not found.
   * @param code The code to look up
   * @return The enum value for the given code
   * @throws IllegalArgumentException if no enum value exists for the given code
   */
  public static ConfigurationType fromCodeOrThrow(String code) {
    return fromCode(code).orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST, "Invalid ConfigurationType for this code: " + code));
  }

  /**
   * Returns a map of code to description.
   */
  public static Map<String, String> codeDescriptionMap() {
    return CODE_MAP.values().stream().collect(Collectors.toMap(ConfigurationType::getCode, ConfigurationType::getDescription));
  }

}

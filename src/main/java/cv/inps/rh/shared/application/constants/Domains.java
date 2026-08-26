/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.application.constants;

import cv.igrp.framework.core.domain.IgrpEnum;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


public enum Domains implements IgrpEnum<String> {

  SIM_NAO_NUMBER("SIM_NAO_NUMBER", "SIM_NAO_NUMBER"),
  NATUREZA_VINCULO("NATUREZA_VINCULO", "NATUREZA_VINCULO"),
  SITUACAO_LABORAL("SITUACAO_LABORAL", "SITUACAO_LABORAL"),
  ESTADO_CONTRATO("ESTADO_CONTRATO", "ESTADO_CONTRATO"),
  TP_PROCESSO_DISC("TP_PROCESSO_DISC", "TP_PROCESSO_DISC"),
  CLASSIFICACAO_SITUACAO("CLASSIFICACAO_SITUACAO", "CLASSIFICACAO_SITUACAO"),
  TIPO_ALERTA_NOTIFICACAO("TIPO_ALERTA_NOTIFICACAO", "TIPO_ALERTA_NOTIFICACAO"),
  ACCAO_REFERENTE("ACCAO_REFERENTE", "ACCAO_REFERENTE"),
  TIPO_MOV_LABORAL("TIPO_MOV_LABORAL", "TIPO_MOV_LABORAL"),
  TIPO_SALARIO_VINCULO("TIPO_SALARIO_VINCULO", "TIPO_SALARIO_VINCULO"),
  DESTINATARIO_NOTIFICACAO("DESTINATARIO_NOTIFICACAO", "DESTINATARIO_NOTIFICACAO");

  private final String code;
  private final String description;

  Domains(String code, String description) {
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
  private static final Map<String, Domains> CODE_MAP = Arrays.stream(values())
      .collect(Collectors.toMap(Domains::getCode, Function.identity()));

  /**
   * Attempts to find the enum value associated with the given code.
   *
   * @param code The code to look up
   * @return An Optional containing the enum value if found, empty Optional otherwise
   */
  public static Optional<Domains> fromCode(String code) {
    return Optional.ofNullable(CODE_MAP.get(code));
  }

  /**
   * Finds the enum value associated with the given code or throws an exception if not found.
   *
   * @param code The code to look up
   * @return The enum value for the given code
   * @throws IllegalArgumentException if no enum value exists for the given code
   */
  public static Domains fromCodeOrThrow(String code) {
    return fromCode(code).orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST, "Invalid Domains for this code: " + code));
  }

  /**
   * Returns a map of code to description.
   */
  public static Map<String, String> codeDescriptionMap() {
    return CODE_MAP.values().stream().collect(Collectors.toMap(Domains::getCode, Domains::getDescription));
  }

}

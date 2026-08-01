package cv.inps.rh.shared.application.constants;

import cv.igrp.framework.core.domain.IgrpEnum;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Domínio {@code TP_DESCONTO_FALTA} — campo "Deduzir Falta Em" do formulário de
 * marcação/justificação de falta. Grava em {@code RH_T_FALTA.FLG_DESCONTO_FALTA}.
 *
 * <p>Determina onde a falta é abatida quando o tipo de justificação implica desconto:
 * nos dias de férias por gozar ({@code RH_T_FERIAS_GOZADAS}) ou nas horas de dispensa
 * ({@code RH_T_DISPENSA}). O desconto no salário é independente deste campo — depende
 * de {@code RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL}.
 */
public enum TipoDescontoFalta implements IgrpEnum<String> {

  FERIAS("FERIAS", "Férias"),
  DISPENSA("DISPENSA", "Dispensa")
  ;

  private final String code;
  private final String description;

  TipoDescontoFalta(String code, String description) {
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

  private static final Map<String, TipoDescontoFalta> CODE_MAP = Arrays.stream(values())
      .collect(Collectors.toMap(TipoDescontoFalta::getCode, Function.identity()));

  public static Optional<TipoDescontoFalta> fromCode(String code) {
    return code == null ? Optional.empty() : Optional.ofNullable(CODE_MAP.get(code.trim().toUpperCase()));
  }

  public static TipoDescontoFalta fromCodeOrThrow(String code) {
    return fromCode(code).orElseThrow(() -> IgrpResponseStatusException.of(
        HttpStatus.BAD_REQUEST, "Valor inválido para 'Deduzir Falta Em': " + code));
  }

  public static Map<String, String> codeDescriptionMap() {
    return CODE_MAP.values().stream()
        .collect(Collectors.toMap(TipoDescontoFalta::getCode, TipoDescontoFalta::getDescription));
  }
}

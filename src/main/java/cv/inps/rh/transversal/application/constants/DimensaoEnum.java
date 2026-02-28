package cv.inps.rh.transversal.application.constants;

import cv.igrp.framework.core.domain.IgrpEnum;

public enum DimensaoEnum implements IgrpEnum<String> {

    DIRECAO,
    SECCAO,
    CARGO,
    GENERO,
    LOCAL_TRABALHO,
    CARREIRA,
    ESCALAO,
    CATEGORIA,
    VINCULO,
    SITUACAO_LABORAL,
    MOBILIDADE,
    GRAU_ESCOLARIDADE,
    IDADE,
    ANTIGUIDADE,
    FAIXA_ETARIA,
    ESTRUTURA_REMUNERATORIA;

    public static boolean exists(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            DimensaoEnum.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

  @Override
  public String getCode() {
    return null;
  }

  @Override
  public String getDescription() {
    return "";
  }
}

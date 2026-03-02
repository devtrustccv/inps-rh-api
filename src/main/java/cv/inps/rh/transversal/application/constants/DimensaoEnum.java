package cv.inps.rh.transversal.application.constants;

import cv.igrp.framework.core.domain.IgrpEnum;

public enum DimensaoEnum implements IgrpEnum<String> {

  DIRECAO("Direção"),
  SECCAO("Secção"),
  CARGO("Cargo"),
  GENERO("Género"),
  LOCAL_TRABALHO("Local de Trabalho"),
  CARREIRA("Carreira"),
  ESCALAO("Escalão"),
  CATEGORIA("Categoria"),
  VINCULO("Vínculo"),
  SITUACAO_LABORAL("Situação Laboral"),
  MOBILIDADE("Mobilidade"),
  GRAU_ESCOLARIDADE("Grau de Escolaridade"),
  IDADE("Idade"),
  ANTIGUIDADE("Antiguidade"),
  FAIXA_ETARIA("Faixa Etária"),
  ESTRUTURA_REMUNERATORIA("Estrutura Remuneratória");

  private final String description;

  DimensaoEnum(String description) {
    this.description = description;
  }

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
    return name();
  }

  @Override
  public String getDescription() {
    return description;
  }
}

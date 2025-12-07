/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.constants;

import cv.igrp.framework.core.domain.IgrpEnum;


public enum ProcessamentoSalarialAction implements IgrpEnum<String> {

  ELIMINAR_PROCESSAMENTO("PROV", "ELIMINAR_PROCESSAMENTO"),
  VALIDAR("PROV", "VALIDAR"),
  CABIMENTAR("VALIDADO", "CABIMENTAR"),
  ELIMINAR_CABIMENTO("DEV", "ELIMINAR_CABIMENTO"),
  AUTORIZAR("CABIMENTADO", "AUTORIZAR");

  private final String code;
  private final String description;

  ProcessamentoSalarialAction(String code, String description) {
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
}

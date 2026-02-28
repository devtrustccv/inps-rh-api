package cv.inps.rh.transversal.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioAssiduidadeQuery implements Query {

  private Long direccaoId;

  private Long seccaoId;

  private String colaborador;

  @NotBlank(message = "The field <tipoAssiduidade> is required")
  @Pattern(regexp = "FERIAS|FALTA|HORA_EXTRA|DISPENSA", message = "Invalid tipoAssiduidade. Must be one of: FERIAS, FALTA, HORA_EXTRA, DISPENSA")
  private String tipoAssiduidade;

  private String dataInicio;

  private String dataFim;

  private String pageNumber;

  private String pageSize;

}

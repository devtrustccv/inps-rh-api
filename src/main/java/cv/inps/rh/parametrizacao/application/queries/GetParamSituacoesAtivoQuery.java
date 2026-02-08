package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetParamSituacoesAtivoQuery implements Query {

  @NotNull(message = "The field <flgSituacaoLaboral> is required")
  private Integer flgSituacaoLaboral;
  @NotBlank(message = "The field <flgAusencia> is required")
  private String flgAusencia;
  @NotBlank(message = "The field <flgFalta> is required")
  private String flgFalta;
  @NotBlank(message = "The field <tipoFalta> is required")
  private String tipoFalta;

}

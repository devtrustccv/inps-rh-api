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

}
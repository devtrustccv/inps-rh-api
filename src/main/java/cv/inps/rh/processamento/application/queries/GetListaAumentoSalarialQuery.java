package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaAumentoSalarialQuery implements Query {

  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotNull(message = "The field <page> is required")
  private Integer page;
  @NotNull(message = "The field <size> is required")
  private Integer size;

}
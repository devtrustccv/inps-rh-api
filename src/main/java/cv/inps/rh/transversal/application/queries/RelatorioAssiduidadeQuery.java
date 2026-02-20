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

  @NotNull(message = "The field <search> is required")
  private boolean search;
  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;
  @NotBlank(message = "The field <dataFim> is required")
  private String dataFim;
  @NotBlank(message = "The field <direccaoId> is required")
  private String direccaoId;
  @NotBlank(message = "The field <seccaoId> is required")
  private String seccaoId;
  @NotBlank(message = "The field <tipoRelatorio> is required")
  private String tipoRelatorio;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;

}
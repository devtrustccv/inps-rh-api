package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProcessamentoSalarialQuery implements Query {

  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;
  @NotBlank(message = "The field <dataFim> is required")
  private String dataFim;
  @NotBlank(message = "The field <direcaoId> is required")
  private String direcaoId;
  @NotBlank(message = "The field <tipo> is required")
  private String tipo;
  @NotBlank(message = "The field <estado> is required")
  private String estado;
  @NotBlank(message = "The field <page> is required")
  private String page;
  @NotBlank(message = "The field <size> is required")
  private String size;

}
package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcularBaixaMedicaQuery implements Query {

  @NotBlank(message = "The field <colaborador> is required")
  private String colaborador;
  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;
  @NotBlank(message = "The field <dataFim> is required")
  private String dataFim;
  @NotBlank(message = "The field <tipoLicenca> is required")
  private String tipoLicenca;
  @NotBlank(message = "The field <dataInicioFalta> is required")
  private String dataInicioFalta;

}

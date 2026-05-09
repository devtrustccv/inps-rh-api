package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListAlertaQuery implements Query {

  @NotBlank(message = "The field <referencia> is required")
  private String referencia;
  @NotBlank(message = "The field <tipoAlerta> is required")
  private String tipoAlerta;
  @NotBlank(message = "The field <nomeColaborador> is required")
  private String nomeColaborador;
  @NotNull(message = "The field <direcaoId> is required")
  private Long direcaoId;
  @NotNull(message = "The field <seccaoId> is required")
  private Long seccaoId;
  @NotBlank(message = "The field <estado> is required")
  private String estado;
  @NotBlank(message = "The field <dataRegistoDe> is required")
  private String dataRegistoDe;
  @NotBlank(message = "The field <dataRegistoAte> is required")
  private String dataRegistoAte;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;

}

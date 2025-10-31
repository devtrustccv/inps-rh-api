package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListFuncionariosQuery implements Query {

  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;
  @NotBlank(message = "The field <nome> is required")
  private String nome;
  @NotNull(message = "The field <direccao> is required")
  private Long direccao;
  @NotNull(message = "The field <seccao> is required")
  private Long seccao;
  @NotNull(message = "The field <tipoVinculoLaboral> is required")
  private Long tipoVinculoLaboral;
  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;
  @NotBlank(message = "The field <dataFim> is required")
  private String dataFim;
  @NotBlank(message = "The field <estado> is required")
  private String estado;

}
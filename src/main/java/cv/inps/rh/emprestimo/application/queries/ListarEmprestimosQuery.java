package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListarEmprestimosQuery implements Query {

  @NotBlank(message = "The field <tipoEmprestimo> is required")
  private String tipoEmprestimo;
  @NotBlank(message = "The field <direccaoId> is required")
  private String direccaoId;
  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;
  @NotBlank(message = "The field <dataFim> is required")
  private String dataFim;
  @NotBlank(message = "The field <estadoEmprestimo> is required")
  private String estadoEmprestimo;
  @NotBlank(message = "The field <page> is required")
  private String page;
  @NotBlank(message = "The field <size> is required")
  private String size;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;
  @NotBlank(message = "The field <estado> is required")
  private String estado;

}
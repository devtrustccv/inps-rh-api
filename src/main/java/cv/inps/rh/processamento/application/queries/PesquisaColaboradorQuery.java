package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PesquisaColaboradorQuery implements Query {

  @NotBlank(message = "The field <uuidFuncionario> is required")
  private String uuidFuncionario;
  @NotBlank(message = "The field <nome> is required")
  private String nome;
  @NotBlank(message = "The field <direccao> is required")
  private String direccao;
  @NotBlank(message = "The field <centroCusto> is required")
  private String centroCusto;
  @NotBlank(message = "The field <page> is required")
  private String page;
  @NotBlank(message = "The field <size> is required")
  private String size;

}
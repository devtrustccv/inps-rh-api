package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPedidoDeclaracoesQuery implements Query {

  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;
  @NotBlank(message = "The field <tipoDeclaracao> is required")
  private String tipoDeclaracao;
  @NotBlank(message = "The field <dataPedidoDe> is required")
  private String dataPedidoDe;
  @NotBlank(message = "The field <dataPedidoAte> is required")
  private String dataPedidoAte;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;

}
package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaNotificacoesQuery implements Query {

  @NotBlank(message = "The field <tipoNotificacao> is required")
  private String tipoNotificacao;
  @NotBlank(message = "The field <dataEnvioDe> is required")
  private String dataEnvioDe;
  @NotBlank(message = "The field <dataEnvioAte> is required")
  private String dataEnvioAte;
  @NotBlank(message = "The field <estado> is required")
  private String estado;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;

}
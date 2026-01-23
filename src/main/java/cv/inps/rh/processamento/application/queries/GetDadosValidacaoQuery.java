package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDadosValidacaoQuery implements Query {

  @NotBlank(message = "The field <tipoValidacao> is required")
  private String tipoValidacao;
  @NotBlank(message = "The field <mesAtual> is required")
  private String mesAtual;
  @NotBlank(message = "The field <mesAnterior> is required")
  private String mesAnterior;

}

package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDetalhesProcessamentoQuery implements Query {

  @NotBlank(message = "The field <tipoMovimento> is required")
  private String tipoMovimento;

  @NotBlank(message = "The field <procSalId> is required")
  private String procSalId;

  @NotBlank(message = "The field <tipoDetalhe> is required")
  private String tipoDetalhe;
}

package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetHistoricoLaboralQuery implements Query {

  @NotBlank(message = "The field <referencia> is required")
  private String referencia;
  @NotBlank(message = "The field <tipoSituacao> is required")
  private String tipoSituacao;
  @NotBlank(message = "The field <situacaoLaboral> is required")
  private String situacaoLaboral;
  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;
  @NotBlank(message = "The field <dataFim> is required")
  private String dataFim;
  @NotBlank(message = "The field <tamanho> is required")
  private String tamanho;
  @NotBlank(message = "The field <pagina> is required")
  private String pagina;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}

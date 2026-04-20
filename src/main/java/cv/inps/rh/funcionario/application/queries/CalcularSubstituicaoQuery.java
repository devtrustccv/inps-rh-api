package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcularSubstituicaoQuery implements Query {

  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;

  @NotBlank(message = "The field <dataFim> is required")
  private String dataFim;

  @NotBlank(message = "The field <funcionarioDeId> is required")
  private String funcionarioDeId;

  @NotBlank(message = "The field <funcionarioParaId> is required")
  private String funcionarioParaId;

}

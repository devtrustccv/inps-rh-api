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
public class CalcularSubstituicaoQuery implements Query {

  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;

  @NotBlank(message = "The field <dataFim> is required")
  private String dataFim;

  @NotNull(message = "The field <tiprelDeId> is required")
  private Long tiprelDeId;

  @NotNull(message = "The field <tiprelParaId> is required")
  private Long tiprelParaId;

}

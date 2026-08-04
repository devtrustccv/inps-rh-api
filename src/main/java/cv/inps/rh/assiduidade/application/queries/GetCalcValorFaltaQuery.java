package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCalcValorFaltaQuery implements Query {

  @NotBlank(message = "The field funcionarioUuid is required")
  private String funcionarioUuid;

  @NotBlank(message = "The field dataInicio is required")
  private String dataInicio;

  @NotBlank(message = "The field dataFim is required")
  private String dataFim;

  /** Total do período. Aceita {@code "8"}, {@code "8.5"} ou {@code "08:30"}. */
  @NotBlank(message = "The field totalDeHorasAusentes is required")
  private String totalDeHorasAusentes;

}

package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSubsidioFeriasQuery implements Query {

  @NotNull(message = "The field <direcaoId> is required")
  private Long direcaoId;
  @NotNull(message = "The field <funcionarioId> is required")
  private Long funcionarioId;
  @NotBlank(message = "The field <dataProcessamento> is required")
  private String dataProcessamento;

}

package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSubsidioNatalQuery implements Query {

  private Long direcaoId;

  @NotNull(message = "The field <funcionarioId> is required")
  private Long funcionarioId;

  private Long valorBrinde;

  @NotNull(message = "The field <anoProcessamento> is required")
  private Long anoProcessamento;
}

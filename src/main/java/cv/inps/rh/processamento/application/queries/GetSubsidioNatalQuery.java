package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSubsidioNatalQuery implements Query {

  @NotNull(message = "The field <direcaoId> is required")
  private Long direcaoId;
  @NotNull(message = "The field <funcionarioId> is required")
  private Long funcionarioId;
  @NotNull(message = "The field <valorBrinde> is required")
  private Long valorBrinde;
  @NotNull(message = "The field <anoProcessamento> is required")
  private Long anoProcessamento;

}
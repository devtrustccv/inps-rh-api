package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetParamContratosAtivosQuery implements Query {

  @NotNull(message = "The field <paramVinculoId> is required")
  private Long paramVinculoId;

}
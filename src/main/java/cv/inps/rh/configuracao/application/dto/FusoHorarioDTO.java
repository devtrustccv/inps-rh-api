/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@IgrpDTO
public record FusoHorarioDTO (
  @NotNull(message = "The field <upsId> is required")
  Long upsId,
  @NotBlank(message = "The field <fuso> is required")
  String fuso
){}

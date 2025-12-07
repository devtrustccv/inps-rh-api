/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@IgrpDTO
public record ValidarProcessamentoRequestDTO(
    @NotNull(message = "The field <idsProcessamento> is required")
    @NotEmpty(message = "The field <idsProcessamento> must not be empty")
    List<Long> idsProcessamento
) {
}

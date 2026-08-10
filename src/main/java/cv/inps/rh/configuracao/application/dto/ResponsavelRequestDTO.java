/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@IgrpDTO
@Valid
public record ResponsavelRequestDTO(

    Long idResponsavel,

    UUID idFuncionario,

    UUID idSeccao,

    @NotBlank
    String nomeSeccao
) {
}

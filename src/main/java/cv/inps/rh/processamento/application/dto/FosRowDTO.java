/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;

import java.time.LocalDate;

@IgrpDTO
public record FosRowDTO(

    Long xmlFosId,

    LocalDate dataEntrega,

    String tipoEntrega,

    String tipoEntregaDesc,

    String mesReferencia,

    String totalRemuneracao,

    Long totalContribuicao,

    String observacao,

    String numeroDocumento
) {
}

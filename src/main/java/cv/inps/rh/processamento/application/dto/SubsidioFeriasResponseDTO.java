/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;

import java.util.List;

@IgrpDTO
public record SubsidioFeriasResponseDTO(

    String nome,

    String funId,

    String anoReferente,

    String estado,

    String valorSubsidio,

    String diasTotal,

    String mesTotal,

    String descSalBase,

    String valorSalBase,

    String descSubsidio,

    String totalRemun,

    String diasFeria,

    String id,

    List<SubsidioFeriasDetalheDTO> detalhes
) {
}

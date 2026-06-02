/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;


@IgrpDTO
public record SubsidioFeriasDetalheDTO(

    String numero,

    String dataInicio,

    String dataFim,

    String escalao,

    String escalaoDesc,

    String valorEscalao,

    String meses,

    String dias,

    String mesesValor,

    String diasValor,

    String totalParcial,

    String id,

    String funIdFilho
) {
}

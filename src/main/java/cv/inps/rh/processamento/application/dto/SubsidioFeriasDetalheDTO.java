/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

@IgrpDTO
public record SubsidioFeriasDetalheDTO(

    String nome,

    Long funId,

    LocalDate dataInicio,

    String dataFim,

    String escalaoDesc,

    BigDecimal valorEscalao,

    Long mesesTrabalhados,

    Long valorMes,

    Long diasTrabalhados,

    Long valorDias,

    Long valorEscalaotempo,

    String situacao
) {
}

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;

@IgrpDTO
public record SubsidioFeriasResponseDTO(

    Long subsidioId,

    String nome,

    Long funId,

    Long salarioBase,

    String estado,

    String estadoSubsidio,

    String mesDiasTrabalho,

    Long diasferias,

    Long valorSubsidio,

    /** FLG_ATIVO_INACTIVO_DESC: Ativo, Inativo ou Processado. */
    String estadoDesc,

    /** ESTADO_DESC: Validado ou Pendente. */
    String estadoSubsidioDesc
) {
}

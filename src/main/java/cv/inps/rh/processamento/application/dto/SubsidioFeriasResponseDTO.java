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

    Long valorSubsidio
) {
}

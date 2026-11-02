package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;

@IgrpDTO
public record SubsidioNatalResponseDTO(

    String nome,

    Long salario,

    Long mesesTrabalho,

    Long percSalario,

    Long faltas,

    Long percFalta,

    Long valorSubsidio,

    Long valorChequeBrinde,

    Long valorPrendaNatal,

    String estado,

    Long subsidioId
) {
}

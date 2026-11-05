package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;

/**
 * Linha da Lista Subsídio de Natal (RH_T_SUBSIDIOS, TIPO_SUBSIDIO = NATAL).
 * {@code estado} = FLG_ATIVO_INACTIVO (A/I); {@code estadoSubsidio} = ESTADO de validação (P/A),
 * como no Subsídio de Férias. {@code observacao} = motivo da última alteração (ou do cálculo).
 * {@code estadoDesc} e {@code estadoSubsidioDesc} = descrições iguais às da view RH_V_SUBSIDIO_NATAL.
 */
@IgrpDTO
public record SubsidioNatalResponseDTO(
    Long subsidioId,
    Long funId,
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
    String estadoSubsidio,
    String observacao,
    String estadoDesc,
    String estadoSubsidioDesc
) {
}

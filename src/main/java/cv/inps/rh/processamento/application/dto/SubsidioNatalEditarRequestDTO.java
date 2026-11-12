package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Editar um Subsídio de Natal: novos valores (só os alterados contam) e o motivo da alteração,
 * obrigatório quando um deles muda.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class SubsidioNatalEditarRequestDTO {

  private Long valorSubsidio;

  private Long valorPrendaNatal;

  private String observacao;
}

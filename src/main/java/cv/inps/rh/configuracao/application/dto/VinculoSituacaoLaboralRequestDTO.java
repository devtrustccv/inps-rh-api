package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class VinculoSituacaoLaboralRequestDTO {

  private Long id;

  @NotNull(message = "The field <situacaoId> is required")
  private String situacaoId;

}

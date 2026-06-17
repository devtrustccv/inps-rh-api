package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class VinculoSituacaoLaboralResponseDTO {

  private Long id;
  private Long situacaoId;
  private String situacaoUuid;
  private String situacaoDescricao;
  private String estado;

}

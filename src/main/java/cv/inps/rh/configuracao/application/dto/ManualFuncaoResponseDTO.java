package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ManualFuncaoResponseDTO {

  private Long id;
  private String uuid;
  private Long institId;
  private Long seccaoId;
  private Long cargoId;
  private Long carrPccsId;
  private String descricao;
  private String estado;
}


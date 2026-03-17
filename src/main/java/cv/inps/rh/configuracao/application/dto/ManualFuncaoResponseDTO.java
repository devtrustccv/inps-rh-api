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
  private String Instituicao;
  private Long seccaoId;
  private String seccao;
  private Long cargoId;
  private String cargo;
  private Long carrPccsId;
  private String carreira;
  private String descricao;
  private String estado;
}


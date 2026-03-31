package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaTecnicaLinhaResponseDTO extends ParamLinhaBaseResponseDTO {

  private String abrangencia;
  private Integer numeroOrdem;
}


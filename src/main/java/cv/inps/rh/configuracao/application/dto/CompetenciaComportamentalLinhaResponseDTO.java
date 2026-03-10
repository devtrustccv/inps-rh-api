package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaComportamentalLinhaResponseDTO extends ParamLinhaBaseResponseDTO {

  private Integer numeroOrdem;
}


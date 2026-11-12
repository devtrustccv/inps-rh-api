package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaComportamentalLinhaResponseDTO extends ParamLinhaBaseResponseDTO {

  private String abrangencia;
  private Integer numeroOrdem;
}


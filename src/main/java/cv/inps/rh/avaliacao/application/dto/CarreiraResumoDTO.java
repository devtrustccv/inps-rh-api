package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Uma Carreira/Cargo (RH_T_PARAM_CARREIRA) para a definição de objectivos: id e nome. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CarreiraResumoDTO {

  private Long id;
  private String nome;

}

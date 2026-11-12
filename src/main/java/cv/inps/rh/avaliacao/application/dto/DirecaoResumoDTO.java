package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Uma direção (RH_T_DIRECAO) para os objectivos comuns: id e nome. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class DirecaoResumoDTO {

  private Long id;
  private String nome;

}

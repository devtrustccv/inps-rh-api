package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Um período do ciclo: o código do domínio PERIODICIDADE e o rótulo que o ecrã mostra. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class PeriodoAvaliadoDTO {

  private String periodicidade;
  private String descricao;

}

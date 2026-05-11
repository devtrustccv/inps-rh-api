/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AvaliacaoFinalDTO  {

  private SemestreDTO primeiroSemestre;
  private SemestreDTO segundoSemestre;

  private String avaliacaoExpressivaQuantitativa;
  private String avaliacaoExpressivaQualitativa;

}

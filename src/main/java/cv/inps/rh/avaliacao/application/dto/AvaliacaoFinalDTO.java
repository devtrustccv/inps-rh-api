/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Avaliação final do ano.
 *
 * <p>Deixou de ser "primeiro semestre + segundo semestre": o ciclo pode ser semestral,
 * trimestral ou anual, por isso os contributos vêm numa lista, um por período, pela ordem
 * cronológica.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoFinalDTO  {

  /** Um por período do ciclo, pela ordem cronológica. */
  private List<PonderacaoPeriodoDTO> periodos = new ArrayList<>();

  private String avaliacaoExpressivaQuantitativa ;

  private String avaliacaoExpressivaQualitativa ;

}

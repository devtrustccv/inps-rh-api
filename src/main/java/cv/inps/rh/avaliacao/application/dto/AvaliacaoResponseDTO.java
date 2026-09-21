/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Avaliação de um período: as componentes com as suas medições, mais o resultado do período.
 *
 * <p>A observação geral, o parecer do colaborador e a observação da comissão executiva
 * deixaram de estar à raiz e passaram para dentro de {@link #periodo}, porque são por
 * período e não por ano.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class AvaliacaoResponseDTO extends AvaliacaoDTO {

  /** Resultado e pareceres do período pedido. Nulo enquanto não houver avaliação lançada. */
  private PeriodoAvaliacaoDTO periodo;

}

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pedido de clonagem de uma parametrização de componentes de avaliação.
 *
 * <p>O clone copia os pesos, ponderações e todas as linhas de objectivos e competências
 * da origem; só o ano é novo, porque RH_T_PARAM_OBJETIVO_DET tem um ciclo por ano.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ClonarComponenteAvaliacaoRequestDTO {

  /** Ano do novo ciclo. Tem de estar livre. */
  @NotNull
  private Integer ano;

  /**
   * Periodicidade do novo ciclo. Opcional — em branco herda a da origem.
   */
  private String periodicidade;

}

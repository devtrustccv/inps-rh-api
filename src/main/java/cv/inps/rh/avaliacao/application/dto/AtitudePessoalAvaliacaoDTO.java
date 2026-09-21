package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Medição do período para esta componente. Os valores vivem em RH_T_AVD_PERIODICIDADE.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class AtitudePessoalAvaliacaoDTO extends AtitudePessoalDTO {

  private BigDecimal avaliacao;
  private BigDecimal autoAvaliacao;

  /** avaliacao x ponderacao / 100. Só de leitura — calculado pelo backend. */
  private BigDecimal resultado;

  /** autoAvaliacao x ponderacao / 100. Só de leitura. */
  private BigDecimal autoResultado;

}

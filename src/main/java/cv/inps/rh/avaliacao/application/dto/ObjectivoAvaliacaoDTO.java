package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Objectivo com a medição do período.
 *
 * <p>Os valores medidos vivem em RH_T_AVD_PERIODICIDADE, não na linha do objectivo — o mesmo
 * objectivo é medido uma vez por período do ciclo.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class ObjectivoAvaliacaoDTO extends ObjectivoDTO {

  private String realizado;
  private BigDecimal avaliacao;

  private String autoRealizado;
  private BigDecimal autoAvaliacao;

  /** avaliacao x ponderacao / 100. Só de leitura — calculado pelo backend. */
  private BigDecimal resultado;

  /** autoAvaliacao x ponderacao / 100. Só de leitura. */
  private BigDecimal autoResultado;

}

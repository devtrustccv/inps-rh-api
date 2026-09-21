package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Contributo de um período para a nota do ano.
 *
 * <p>Substitui o antigo {@code SemestreDTO}, que só sabia falar de dois semestres. A
 * {@link #ponderacao} vem do domínio AVD_PONDERACAO_FINAL, indexada por {@link #periodicidade}.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class PonderacaoPeriodoDTO {

  /** SEMESTRE1, TRIMESTRE3, ANUAL, ... */
  private String periodicidade;

  /** Rótulo legível do período, vindo do domínio (ex.: "Semestre 1"). */
  private String descricao;

  /** Nota final do período (RH_T_AVD_DETALHE.AVALIACAO_FINAL). */
  private BigDecimal avaliacaoFinal;

  /** Peso do período no ano, em percentagem. */
  private BigDecimal ponderacao;

  /** avaliacaoFinal x ponderacao / 100. */
  private BigDecimal contributo;

}

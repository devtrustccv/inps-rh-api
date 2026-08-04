package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Pré-cálculo do valor de uma falta, para os campos "Valor Diário (calculado)" e
 * "Valor Total (calculado)" dos formulários de marcação e justificação.
 *
 * <p>Fórmula da especificação (linhas 415-428): {@code CALCULO_FALTA_DIARIO} devolve o
 * valor à hora; o valor do dia é esse valor pelas horas de ausência; o total é o valor
 * diário pelo número de dias.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CalcValorFaltaDTO {

  /** Valor de uma hora de ausência. */
  private BigDecimal valorHora;

  /** Valor de um dia = valorHora × horas de ausência do dia. */
  private BigDecimal valorDiario;

  /** valorDiario × totalDias. */
  private BigDecimal valorTotal;

  private Integer totalDias;

  /** Horas de ausência por dia, normalizadas em {@code HH:MM}. */
  private String horasAusenciaPorDia;
}

/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FaltaItemDTO  {


  private boolean selecionar ;


  private Long id ;


  private String data ;


  private String tipoFalta ;


  private String horasAusencia ;


  /** Valor da falta do dia. BigDecimal: como Integer truncava os cêntimos (6344 em vez de 6344,56). */
  private BigDecimal valorAusencia ;


  /** Só de resposta — o motivo grava-se no cabeçalho (aplica-se a todas as seleccionadas). */
  private String motivo ;


  /** Só de resposta — o radio "Com Justificativo?" é do cabeçalho. */
  private String comJustificativo ;

  /**
   * Estado da falta: {@code P} (pendente), {@code A} (justificada) ou {@code I}
   * (rejeitada). Só de resposta — alimenta a coluna "Estado" do resumo de faltas.
   * Vem nulo quando o dia ainda não tem registo em RH_T_FALTA.
   */
  private String estado ;

  /** Descrição legível de {@link #estado}: Pendente / Justificada / Rejeitada. */
  private String estadoDesc ;

}

/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


  private Integer valorAusencia ;


  private String motivo ;


  private String comJustificativo ;

  /**
   * Estado da falta: {@code P} (pendente), {@code A} (justificada) ou {@code I}
   * (rejeitada). Só de resposta — alimenta a coluna "Estado" do resumo de faltas.
   * Vem nulo quando o dia ainda não tem registo em RH_T_FALTA.
   */
  private String estado ;

  /** Descrição legível de {@link #estado}: Pendente / Justificada / Rejeitada. */
  private String estadoDesc ;

  @Valid
  private AnexoReqDTO documento ;

}

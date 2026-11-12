/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AssiduidadeListDTO  {


  private Long id ;


  private UUID uuid ;


  private UUID uuidFuncionario ;


  private String nomeColaborador ;


  private String direcao ;


  /**
   * Todos os dias de falta do mes (spec: SUM(SINTESE_DIARIA.FALTA)), menos os de ausencia sem falta
   * registada. = totalFaltasJustificadas + totalFaltasInjustificadas + totalFaltasPendentes.
   */
  private Integer totalFalta ;


  /** Dias de falta a aguardar despacho (RH_T_FALTA P) ou devolvidos para correcao (C). */
  private Integer totalFaltasPendentes ;


  /** Dias de falta aprovados com justificativo (RH_T_FALTA A + FLG_JUSTIFICATIVO = 'SIM'). */
  private Integer totalFaltasJustificadas ;


  /** Dias de falta injustificados: sem falta viva registada (ou rejeitada), ou aprovada com 'NAO'. */
  private Integer totalFaltasInjustificadas ;


  private Integer totalDias ;


  private String totalHorasTrabalhadas ;


  private String totalHorasAusentes ;


  private String totalHoraExtra ;


  private String totalHoraAlmoco ;


  private String estado ;


  private String estadoDesc ;


  private String motivoAusencia ;


  private String mesReferencia ;


  private Integer ano ;


  private Integer mes ;

}

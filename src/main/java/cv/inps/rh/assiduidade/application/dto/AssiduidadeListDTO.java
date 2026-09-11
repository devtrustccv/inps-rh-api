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


  /** Dias de falta do mes ainda POR justificar (sem registo em RH_T_FALTA). */
  private Integer totalFalta ;


  /** Dias de falta ja registados e a aguardar despacho (RH_T_FALTA.ESTADO = 'P'). */
  private Integer totalFaltasPendentes ;


  /** Dias de falta ja registados e despachados (RH_T_FALTA.ESTADO = 'A'). */
  private Integer totalFaltasJustificadas ;


  private Integer totalDias ;


  private String totalHorasTrabalhadas ;


  private String totalHorasAusentes ;


  private String totalHoraExtra ;


  private String totalHoraAlmoco ;


  private String estado ;


  private String estadoDesc ;


  private String mesReferencia ;


  private Integer ano ;


  private Integer mes ;

}

/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class SubstituicaoSumaryDTO  {



  private Long id ;


  private String uuidSubstituicao ;


  private String estado ;


  private String estadoDesc ;


  private String colaboradorSustituido ;


  private String cargo ;


  private String colaboradorSustituto ;


  private String dataInicio ;


  private String dataFim ;


  private String motivo ;


  private String obs ;

}

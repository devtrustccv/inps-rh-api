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
public class RegimeListDTO  {



  private Long id ;


  private String uuid ;


  private Long idFuncionario ;


  private String uuidFuncionario ;


  private String tipoRegime ;


  private String dataInicio ;


  private String dataFim ;


  private String modalidade ;


  private String numHoras ;


  private String estado ;


  private String estadoDesc ;

}

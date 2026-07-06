/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MobilidadeListDTO  {


  private Long id ;


  private Long idFuncionario ;


  private String uuid ;


  private String uuidFuncionario ;


  private String direccao ;


  private String seccao ;


  private String localTrabalho ;


  private String dataInicio ;


  private String dataFim ;


  // true se a mobilidade (o seu tiprel) já tem processamento salarial (RH_T_PROC_FUNCIONARIOS)
  private boolean processamento ;


  private String estado ;


  private String estadoDesc ;


  private String tipoMobilidade ;


  private String tipoMobilidadeDesc ;

}

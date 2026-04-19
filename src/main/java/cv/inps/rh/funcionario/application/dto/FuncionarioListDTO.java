/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FuncionarioListDTO  {

  
  
  private Long id ;
  
  
  private String uuid ;
  
  
  private String nome ;
  
  
  private String cargo ;
  
  
  private String dataInicio ;
  
  
  private String direccao ;
  
  
  private String seccao ;
  
  
  private String carreiraCategoria ;
  
  
  private String estadoRegisto ;
  
  
  private String estadoRegistoDesc ;
  
  
  private String estadoColaborador ;
  
  
  private String estadoColaboradorDesc ;
  
  
  private Long vinculoId ;
  
  
  private String numColaborador ;

}
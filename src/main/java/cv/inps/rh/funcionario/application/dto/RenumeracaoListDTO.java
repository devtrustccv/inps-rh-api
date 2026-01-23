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
public class RenumeracaoListDTO  {

  
  
  private Long id ;
  
  
  private String uuid ;
  
  
  private String estado ;
  
  
  private String estadoDesc ;
  
  
  private String movimento ;
  
  
  private String valor ;
  
  
  private String ultimoPRoc ;
  
  
  private String dataInicio ;
  
  
  private String dataFim ;

}
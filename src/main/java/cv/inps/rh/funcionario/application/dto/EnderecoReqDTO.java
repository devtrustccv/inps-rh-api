/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

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
public class EnderecoReqDTO  {

  
  
  private Long id ;
  
  
  private Long paisId ;
  
  
  private Long ilhaId ;
  
  
  private Long concelhoId ;
  
  
  private Long freguesiaId ;
  
  
  private Long zonaId ;
  
  
  private String morada ;

}
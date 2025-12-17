/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.parametrizacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import lombok.EqualsAndHashCode;
@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class LocalTrabalhoDTO extends ParametrizacaoDTO {

  
  
  private Long paisId ;
  
  
  private String paisDesc ;
  
  
  private Long ilhaId ;
  
  
  private String ilhaDesc ;

}
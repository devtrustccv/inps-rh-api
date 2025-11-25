/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class CalculoRenumeracaoDTO  {

  
  
  private String mes ;
  
  
  private Integer numeroDias ;
  
  
  private BigDecimal valorSubstituto ;
  
  
  private BigDecimal valorSubstituido ;

}
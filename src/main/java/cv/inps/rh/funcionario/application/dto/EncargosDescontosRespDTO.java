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
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class EncargosDescontosRespDTO  {

  
  
  private Long id ;
  
  
  private Long tipoEncargoId ;
  
  
  private String tipoEncargoDesc ;
  
  
  private BigDecimal valor ;
  
  
  private LocalDate dataInicio ;
  
  
  private LocalDate dataFim ;

}
/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


  private BigDecimal percentagem ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private String observacoes ;

}

/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

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
public class FaltaListDTO  {

  
  
  private Long id ;
  
  
  private String uuid ;
  
  
  private String nomeColaborador ;
  
  
  private String direcao ;
  
  
  private String categoria ;
  
  
  private String dataIntervalo ;
  
  
  private String motivo ;
  
  
  private Integer totalHorasAusente ;
  
  
  private Integer numFalta ;
  
  
  private BigDecimal valorADescontar ;
  
  
  private boolean descontoRenumeracao ;
  
  
  private String estadoProcessamento ;
  
  
  private String estado ;
  
  
  private String estadoDesc ;

}
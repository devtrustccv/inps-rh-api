/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

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
public class MapaFeriaListDTO  {

  
  
  private Long id ;
  
  
  private String uuid ;
  
  
  private String anoReferente ;
  
  
  private String direcao ;
  
  
  private Integer totalColaborador ;
  
  
  private Integer totalFeriasAgendadas ;
  
  
  private Integer totalFeriasPorAgendar ;
  
  
  private String estado ;
  
  
  private String estadoDesc ;

}
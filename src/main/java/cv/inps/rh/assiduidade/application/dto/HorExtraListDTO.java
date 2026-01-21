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
public class HorExtraListDTO  {

  
  
  private Long id ;
  
  
  private String uuid ;
  
  
  private String direcao ;
  
  
  private Long direcaoId ;
  
  
  private String nomeColaborador ;
  
  
  private String categoria ;
  
  
  private Long categoriaId ;
  
  
  private String vinculo ;
  
  
  private Long vinculoId ;
  
  
  private String intervaloData ;
  
  
  private String horasContratato ;
  
  
  private String horasTrabalho ;
  
  
  private String salarioMensal ;
  
  
  private String valorHorasMensal ;
  
  
  private String valorHorasDiario ;
  
  
  private String estado ;
  
  
  private String estadoDesc ;

}
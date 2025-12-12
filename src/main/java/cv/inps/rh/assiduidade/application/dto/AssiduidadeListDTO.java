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
public class AssiduidadeListDTO  {

  
  
  private String id ;
  
  
  private String uuid ;
  
  
  private String nomeColaborador ;
  
  
  private String direcao ;
  
  
  private Integer totalFalta ;
  
  
  private Integer totalDias ;
  
  
  private Integer totalHorasTrabalhadas ;
  
  
  private Integer totalHorasAusentes ;
  
  
  private Integer totalHoraExtra ;
  
  
  private Integer totalHoraAlmoco ;
  
  
  private String estado ;
  
  
  private String estadoDesc ;

}
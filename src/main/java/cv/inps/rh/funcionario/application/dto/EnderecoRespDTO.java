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
public class EnderecoRespDTO  {

  
  
  private Long id ;
  
  
  private Integer pais ;
  
  
  private String paisDesc ;
  
  
  private Integer ilha ;
  
  
  private String ilhaDesc ;
  
  
  private Integer concelho ;
  
  
  private String concelhoDesc ;
  
  
  private Integer freguesia ;
  
  
  private String freguesiaDesc ;
  
  
  private Integer zona ;
  
  
  private String zonaDesc ;
  
  
  private String morada ;

}
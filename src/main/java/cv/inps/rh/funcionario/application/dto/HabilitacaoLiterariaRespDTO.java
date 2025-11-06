/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class HabilitacaoLiterariaRespDTO  {

  
  
  private Long id ;
  
  
  private Integer paisId ;
  
  
  private String paisDesc ;
  
  
  private String estabelecimento ;
  
  
  private String area ;
  
  
  private String curso ;
  
  
  private String grauAcademico ;
  
  
  private LocalDate dataInicio ;
  
  
  private LocalDate dataTermino ;
  
  
  private Integer concluido ;

}
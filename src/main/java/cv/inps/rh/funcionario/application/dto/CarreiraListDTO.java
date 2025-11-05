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
public class CarreiraListDTO  {

  
  
  private Long id ;
  
  
  private String uuid ;
  
  
  private Long idFuncionario ;
  
  
  private String uuidFuncionario ;
  
  
  private String tipoCarreira ;
  
  
  private String vinculo ;
  
  
  private String carreira ;
  
  
  private String cargo ;
  
  
  private String escalao ;
  
  
  private String salario ;
  
  
  private String situacaoLaboral ;
  
  
  private LocalDate dataInicio ;
  
  
  private LocalDate dataFim ;
  
  
  private String processamento ;
  
  
  private String estado ;
  
  
  private String estadoDesc ;

}
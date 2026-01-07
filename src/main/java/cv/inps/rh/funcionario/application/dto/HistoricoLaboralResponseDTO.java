/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

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
public class HistoricoLaboralResponseDTO  {

  
  
  private Long id ;
  
  
  private String uuid ;
  
  
  private String uuidFuncionario ;
  
  
  private boolean ultimoMovimento ;
  
  
  private String tipoSituacao ;
  
  
  private String tipoContrato ;
  
  
  private String vinculo ;
  
  
  private String direcao ;
  
  
  private String seccao ;
  
  
  private String carreira ;
  
  
  private String referenciaEscalao ;
  
  
  private String cargo ;
  
  
  private String situacaoLaboral ;
  
  
  private String dataInicioFim ;

}
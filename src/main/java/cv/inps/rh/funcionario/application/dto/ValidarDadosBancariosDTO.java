/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.DadosBancariosReqDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ValidarDadosBancariosDTO  {

  
  
  private EstadoValidacao validar ;
  
  @Valid
  private DadosBancariosReqDTO dadosBancarios ;

}
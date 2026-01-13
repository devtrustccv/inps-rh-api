/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.funcionario.application.dto.NovoPagamentoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ValidarPagamentoRequestDTO  {

  @NotBlank(message = "The field <validacao> is required")
  
  private String validacao ;
  
  @Valid
  private NovoPagamentoRequestDTO dados ;

}
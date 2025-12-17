/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ValidarRemuneracaoRequestDTO  {

  @NotBlank(message = "The field <validacao> is required")

  private String validacao ;
  @NotBlank(message = "The field <validacaoId> is required")

  private String validacaoId ;
  @NotBlank(message = "The field <remuneracaoId> is required")

  private String remuneracaoId ;

  @Valid
  private NovoRemuneracaoRequestDTO dados ;

}

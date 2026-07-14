/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ValidacaoCarreiraDTO  {

  @NotBlank(message = "The field <validacao> is required")

  private String validacao ;
  // Opcional: a validação (aprovar/rejeitar) não precisa dos dados contratuais; exigi-los fazia o
  // endpoint devolver 400 quando o frontend enviava só {validacao}. Mantido opcional.
  private DadosContratuaisReqDTO dados ;

}

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

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class RenovarLoteItemReqDTO  {


  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId ;


  @NotBlank(message = "The field <contratoId> is required")
  private String contratoId ;


  private UUID alertaId ;

  @Valid
  @NotNull(message = "The field <dadosRenovacao> is required")
  private RenovarContratoReqDTO dadosRenovacao ;

}

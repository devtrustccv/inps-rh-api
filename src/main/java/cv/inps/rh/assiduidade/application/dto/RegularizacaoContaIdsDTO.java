/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class RegularizacaoContaIdsDTO {

  @NotNull(message = "The field <processamentoFunId> is required")
  private Long processamentoFunId;

  @NotNull(message = "The field <abonoBeneficioId> is required")
  private Long abonoBeneficioId;
}

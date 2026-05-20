/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

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
public class AtivarInativarSubsidioNatalRowDTO {

  @NotNull(message = "The field <subsidioId> is required")

  private Long subsidioId;
  @NotBlank(message = "The field <funcionarioId> is required")

  private String funcionarioId;
  @NotBlank(message = "The field <status> is required")

  private String status;
  @NotNull(message = "The field <data> is required")
  @Valid
  private SubsidioResponseNatalDTO data;

}

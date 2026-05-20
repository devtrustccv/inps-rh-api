/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AtivarInativarSubsidioNatalDTO {

  @NotNull(message = "The field <ano> is required")

  private Long ano;
  @NotNull(message = "The field <rows> is required")
  @NotEmpty(message = "The field <rows> must not be empty")
  @Valid
  private List<AtivarInativarSubsidioNatalRowDTO> rows = new ArrayList<>();

}

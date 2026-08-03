/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

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
public class RegularizacaoContaListRequestDTO {

  @NotNull(message = "The field <ids> is required")
  @Valid
  private RegularizacaoContaIdsDTO ids;

  @NotNull(message = "The field <data> is required")
  @NotEmpty(message = "The field <data> must not be empty")
  @Valid
  private List<RegularizacaoContaRequestDTO> data = new ArrayList<>();
}

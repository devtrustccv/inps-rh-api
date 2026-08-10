/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AssociarResponsaveisRequestDTO {

  @NotNull(message = "The field <direcaoData> is required")
  private DirecaoData direcaoData;

  @NotNull(message = "The field <responsaveis> is required")
  @NotEmpty(message = "The field <responsaveis> must not be empty")
  @Valid
  private List<ResponsavelRequestDTO> sessaoData = new ArrayList<>();

  public record DirecaoData(
      @NotNull
      Long direcaoId,

      UUID funcionarioResponsavelId
  ) {
  }

}

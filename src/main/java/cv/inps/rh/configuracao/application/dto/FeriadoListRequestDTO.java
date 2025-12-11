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

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FeriadoListRequestDTO {

  @NotNull(message = "The field <anoReferente> is required")

  private Integer anoReferente;
  @NotNull(message = "The field <feriados> is required")
  @NotEmpty(message = "The field <feriados> must not be empty")
  @Valid
  private List<FeriadoDTO> feriados = new ArrayList<>();

}

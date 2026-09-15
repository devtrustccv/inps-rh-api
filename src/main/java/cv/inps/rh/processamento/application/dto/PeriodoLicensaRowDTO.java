/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class PeriodoLicensaRowDTO {

  @NotBlank(message = "The field <continuidade> is required")

  private String continuidade;
  @NotNull(message = "The field <dataInicio> is required")

  private LocalDate dataInicio;
  @NotNull(message = "The field <dataFim> is required")

  private LocalDate dataFim;

  private String id;

}

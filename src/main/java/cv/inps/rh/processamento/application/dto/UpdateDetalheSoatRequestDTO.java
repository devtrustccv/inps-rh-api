/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class UpdateDetalheSoatRequestDTO {

  @NotBlank(message = "The field <detalheSoatId> is required")

  private String detalheSoatId;
  @NotNull(message = "The field <diasTrabalho> is required")

  private Long diasTrabalho;
  @NotNull(message = "The field <remuneracao> is required")

  private BigDecimal remuneracao;

}

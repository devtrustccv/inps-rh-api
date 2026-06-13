/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

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
public class VinculoMovimentoRequestDTO  {

  @NotNull(message = "The field <tipoMovimentoId> is required")

  private Long tipoMovimentoId ;
  @NotBlank(message = "The field <tipo> is required")

  private String tipo ;

  private Integer percentagem ;

  private BigDecimal valor ;

}

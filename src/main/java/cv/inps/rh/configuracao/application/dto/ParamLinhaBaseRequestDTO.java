/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ParamLinhaBaseRequestDTO  {

  private Boolean aplicarATodos;          // true → cargoId deve ser null

  private Long cargoId;                   // obrigatório se aplicarATodos = false

  private Long carrPccsId;

  @NotNull @DecimalMin("0") @DecimalMax("100")
  private BigDecimal ponderacao;

}

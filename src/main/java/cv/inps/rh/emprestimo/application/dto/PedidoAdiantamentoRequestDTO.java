/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

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
public class PedidoAdiantamentoRequestDTO  {

  @NotBlank(message = "The field <emprestimoId> is required")

  private String emprestimoId ;
  @NotNull(message = "The field <valorAdiantamento> is required")

  private BigDecimal valorAdiantamento ;

}

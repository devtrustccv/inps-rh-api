/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class PedidoReforcoRequestDTO  {

  @NotBlank(message = "The field <emprestimoId> is required")

  private String emprestimoId ;


  private BigDecimal valorReforco ;


  private Long numeroPrestacao ;
  @NotBlank(message = "The field <tipoRenegociacao> is required")

  private String tipoRenegociacao ;

}

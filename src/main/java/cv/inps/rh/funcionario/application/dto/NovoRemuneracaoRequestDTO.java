/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

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
public class NovoRemuneracaoRequestDTO  {

  @NotBlank(message = "The field <movimentoId> is required")

  private Long movimentoId ;
  @NotNull(message = "The field <valor> is required")

  private BigDecimal valor ;
  @NotNull(message = "The field <percentagem> is required")

  private BigDecimal percentagem ;
  @NotBlank(message = "The field <moeda> is required")

  private String moeda ;
  @NotBlank(message = "The field <dataInicio> is required")

  private String dataInicio ;
  @NotBlank(message = "The field <dataFim> is required")

  private String dataFim ;


  private String observacao ;

}

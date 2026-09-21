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

  /**
   * Cargos a que a linha se aplica. O ecrã tem o cargo em multiselect, e como
   * RH_T_PARAM_OBJETIVO.CARGO_ID só guarda um, cada cargo escolhido dá origem à sua
   * própria linha. Alternativa ao {@code cargoId} singular; ignorado quando
   * {@code aplicarATodos} é verdadeiro.
   */
  private java.util.List<Long> cargoIds;

  private Long carrPccsId;

  @NotNull @DecimalMin("0") @DecimalMax("100")
  private BigDecimal ponderacao;

}

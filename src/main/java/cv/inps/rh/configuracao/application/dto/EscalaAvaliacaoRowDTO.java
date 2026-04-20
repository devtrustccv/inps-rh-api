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
public class EscalaAvaliacaoRowDTO  {


  private Long id;

  @NotNull
  private Integer nivel;

  @NotBlank
  private String qualitativa;

  @NotBlank
  private String descricao;

  @NotNull
  private BigDecimal quantitativaDe;

  @NotNull
  private BigDecimal quantitativaAte;

}

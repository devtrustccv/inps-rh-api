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
public class InformacaoEmprestimoRequestDTO  {

  @NotBlank(message = "The field <carreiraId> is required")

  private String carreiraId ;
  @NotNull(message = "The field <valorLimiteEmprestimo> is required")

  private BigDecimal valorLimiteEmprestimo ;


  private Long numeroLimitePrestacaoMeses ;


  private String estado ;


  private String id ;

}

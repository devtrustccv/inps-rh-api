/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class PedidoDeclaracaoDTO  {

  @NotBlank(message = "The field <tipoDeclaracao> is required")

  private String tipoDeclaracao ;
  @NotBlank(message = "The field <finalidade> is required")

  private String finalidade ;
  @NotBlank(message = "The field <entidadeDestinataria> is required")

  private String entidadeDestinataria ;

}

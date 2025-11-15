/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class SituacaoLaboralRequestDTO  {

  @NotBlank(message = "The field <codigo> is required")

  private String codigo ;
  @NotBlank(message = "The field <descricao> is required")

  private String descricao ;
  @NotBlank(message = "The field <tipo> is required")

  private String tipo ;
  @NotBlank(message = "The field <estadoContrato> is required")

  private String estadoContrato ;
  @NotBlank(message = "The field <remuneracao> is required")

  private String remuneracao ;
  @NotBlank(message = "The field <carreira> is required")

  private String carreira ;
  @NotBlank(message = "The field <tempoServico> is required")

  private String tempoServico ;
  @NotBlank(message = "The field <cessaVinculo> is required")

  private String cessaVinculo ;
  @NotBlank(message = "The field <progressaoPromocao> is required")

  private String progressaoPromocao ;


  private String estado ;

}

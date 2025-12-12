/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.Estado;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class TipoFaltaAusenciaDTO {


  private UUID id;
  @NotBlank(message = "The field <situacao> is required")

  private String situacao;
  @NotBlank(message = "The field <codigo> is required")

  private String codigo;
  @NotBlank(message = "The field <descricao> is required")

  private String descricao;


  private Long associacao;
  @NotBlank(message = "The field <descontoRemuneracao> is required")

  private String descontoRemuneracao;


  private Estado estado;

}

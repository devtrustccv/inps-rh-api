/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ResponsavelRequestDTO {


  private Long idResponsavel;
  @NotNull(message = "The field <idDirecao> is required")

  private Long idDirecao;
  @NotBlank(message = "The field <IdFuncionario> is required")

  private String IdFuncionario;
  @NotBlank(message = "The field <email> is required")

  private String email;


  private String idSeccao;

}

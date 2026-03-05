/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DocOutputResponseDTO  {

  private Long id;
  private String tipoDocumento;
  private String titulo;
  private String corpo;
  private String assinadoPor;
  private Long responsavelId;
  private String responsavelNome;
  private String estado;

}

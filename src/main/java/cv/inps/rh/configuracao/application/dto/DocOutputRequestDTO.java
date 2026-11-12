/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DocOutputRequestDTO  {

  private String tipoDocumento;
  private String titulo;
  private String corpo;
  private String assinadoPor;
  /** RH_T_RESPONSAVEL.ID — o `responsavelId` devolvido por GET configuracao/responsaveis/search (spec: RESPONSAVEL_ID). */
  private Long responsavelId;
  /** A (ativo) ou I (inativo). Vazio = A. */
  private String estado;

}

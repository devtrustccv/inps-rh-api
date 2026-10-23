/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DocOutputResponseDTO  {

  private Long id;
  private UUID uuid;
  private String tipoDocumento;
  private String tipoDocumentoDesc;
  private String titulo;
  private String corpo;
  private String assinadoPor;
  /** RH_T_RESPONSAVEL.ID (o mesmo valor que se envia no registo/edição). */
  private Long responsavelId;
  /** UUID do funcionário responsável. */
  private UUID funcionarioId;
  private String responsavelNome;
  private String estado;


}

/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoColaboradorResponseDTO  {

  private Long id;
  private UUID uuid;
  private Long funId;
  private UUID funUuid;
  private String nomeColaborador;
  private String numDocumento;
  private String estado;

  // Prestador a que o colaborador ficou associado na etapa de emissão de requisição.
  // Na logística, uma linha de bilhete/seguro só pode agrupar colaboradores do mesmo prestador —
  // o frontend usa isto para agrupar o multiselect e não oferecer combinações inválidas.
  private Long missaoPrestId;
  private String nomePrestador;
}

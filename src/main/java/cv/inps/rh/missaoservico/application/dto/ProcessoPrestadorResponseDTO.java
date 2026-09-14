/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoPrestadorResponseDTO {

  private Long id;                        // RH_T_MISSAO_PRESTADOR.ID
  private UUID uuid;                      // RH_T_MISSAO_PRESTADOR.UUID
  private UUID paramPrestUuid;            // prestador parametrizado
  private Long entId;
  private String nome;
  private String email;                   // email principal
  private List<String> emails;            // todos os emails activos que recebem a notificação
  private String estado;

}

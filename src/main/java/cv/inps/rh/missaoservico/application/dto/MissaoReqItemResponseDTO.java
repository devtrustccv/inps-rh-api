/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

@IgrpDTO
public class MissaoReqItemResponseDTO {

  private Long id;
  private UUID uuid;
  private Long missaoPrestId;
  private String nomePrestador;
  private String emailPrestador;
  private List<MissaoColaboradorResponseDTO> colaboradores;
  private AnexoRespDTO proposta;
  private Boolean selecionado;
  private String estado;

}

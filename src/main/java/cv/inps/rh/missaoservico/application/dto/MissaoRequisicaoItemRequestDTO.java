/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoRequisicaoItemRequestDTO  {

  private Long missaoPrestId;             // MissaoPrestadorEntity.id
  private Boolean selecionado;            // checkbox — se deve emitir requisição para este prestador
  private List<UUID> missaoColabIds;      // colaboradores associados (multiselect)
  private AnexoReqDTO documentoProposta;

}

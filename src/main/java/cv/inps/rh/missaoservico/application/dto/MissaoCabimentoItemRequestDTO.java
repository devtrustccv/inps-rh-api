/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoCabimentoItemRequestDTO  {

  private Long logisticaId;               // MissaoLogisticaEntity.id
  private Boolean selecionado;            // checkbox
  private Long cabId;                     // ID do cabimento gerado no SGAL
  private AnexoReqDTO anexo;         // documento de fatura anexado

}

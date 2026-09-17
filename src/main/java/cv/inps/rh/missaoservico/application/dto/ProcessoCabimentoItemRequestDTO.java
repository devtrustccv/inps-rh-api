/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoCabimentoItemRequestDTO {

  private UUID logisticaUuid;             // linha de logística (GET .../cabimento → itens[].logisticaUuid)
  private Boolean selecionado;            // cabimentar esta linha no NEXT
  private Long cabId;                     // só nos cabimentos manuais/internacionais (financeiro)
  private AnexoReqDTO anexo;              // documento de suporte (ex.: nota de transferência)

}

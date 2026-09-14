/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoRequisicaoItemRequestDTO {

  private UUID missaoPrestUuid;           // uuid do prestador do processo (GET .../prestadores → prestadores[].uuid)
  private Boolean selecionado;            // emitir requisição para este prestador
  private List<UUID> funcionarioUuids;    // uuid do FUNCIONÁRIO (colaboradoresMissao[].funUuid)
  private BigDecimal valorTotal;          // null = não mexer
  private AnexoReqDTO proposta;           // fatura proforma recebida por email

}

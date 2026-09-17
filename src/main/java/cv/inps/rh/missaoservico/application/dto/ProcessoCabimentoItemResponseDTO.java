/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
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
public class ProcessoCabimentoItemResponseDTO {

  private UUID logisticaUuid;
  private String referencia;              // tipo de serviço: BILHETE_PASSAGEM | SEGURO_VIAGEM | ALOJAMENTO | AJUDA_CUSTO
  private String nome;                    // prestador, seguradora ou — na ajuda de custo — colaborador
  private BigDecimal valorTotal;
  private String moeda;
  private Long cabId;                     // nº de cabimento (null enquanto o SGAL não estiver integrado)
  private String estadoCabimento;         // null | CABIMENTADO | AUTORIZADO
  private List<MissaoLogisticaDetResponseDTO> colaboradores;
  private AnexoRespDTO documento;         // documento da linha (fatura/comprovativo)

}

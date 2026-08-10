/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoCabimentoItemResponseDTO  {


  private Long logisticaId;
  private UUID logisticaUuid;
  private String referencia;              // tipo de serviço
  private String nome;                    // nome do prestador ou do colaborador (ajuda de custo)
  private BigDecimal valorTotal;
  private Long cabId;
  private String estadoCabimento;         // CABIMENTADO
  private AnexoRespDTO fatura;    // fatura anexada

  // colaboradores afetos a esta linha — distingue linhas com o mesmo prestador, tipo e valor
  // (ex.: dois seguros da mesma seguradora, um por colaborador)
  private java.util.List<MissaoLogisticaDetResponseDTO> colaboradores;

}

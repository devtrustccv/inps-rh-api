/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AjudaCustoResponseDTO  {

  private Long id;
  private UUID uuid;
  private MissaoLogisticaDetResponseDTO colaborador;
  private Boolean flgAlojamento;
  private Integer numeroDiasAlojamento;
  private BigDecimal valorDiario;         // calculado pelo service
  private BigDecimal valorTotal;          // calculado: valorDiario × numeroDiasAlojamento
  private BigDecimal valorDiarioBase;     // MS-013: base antes da fracção (repõe o "Valor Diário (base)" do ecrã)
  private String fracaoAplicada;          // MS-013: "1", "2/3" ou "1/3"
  private String flgAlimentacao;          // MS-013: SIM/NAO do alojamento do colaborador (decide 2/3 vs 1/3); null sem alojamento
  private String estado;
  // referencia = 'AJUDA_CUSTO' (interno)
  // sem moeda, sem documento

}

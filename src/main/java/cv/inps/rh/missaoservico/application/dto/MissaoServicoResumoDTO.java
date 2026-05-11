/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class MissaoServicoResumoDTO  {

  private Long id;
  private UUID uuid;
  private Long nrMissao;
  private String destino;                 // descricaoDestino
  private String nacionalInternacional;   // "Nacional" | "Internacional" via flgDestino
  private LocalDate dataMissao;           // dataInicio
  private String estado;                  // badge: PENDENTE_FATURA | POR_PAGAR | PAGO | PENDENTE_REQUISICAO
  private String etapa;                   // etapa atual do processo
  private BigDecimal valorAC;             // soma VALOR_TOTAL onde REFERENCIA='AJUDA_CUSTO'
  private BigDecimal valorBP;             // REFERENCIA='BILHETE_PASSAGEM'
  private BigDecimal valorAlojamento;     // REFERENCIA='ALOJAMENTO'
  private BigDecimal valorSeguro;         // REFERENCIA='SEGURO_VIAGEM'

}

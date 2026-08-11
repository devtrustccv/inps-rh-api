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
  private Long nrMissao;                  // sequencial dentro do ano
  private Integer ano;                    // ano de criação
  private String nrMissaoFormatado;       // "nr/ano" — o que a listagem deve mostrar
  private String destino;                 // descricaoDestino
  private String nacionalInternacional;   // "Nacional" | "Internacional" via flgDestino
  private LocalDate dataMissao;           // dataInicio
  // Estado do registo: "A" (activa) | "I" (cancelada) — RH_T_MISSAO_SERVICO.ESTADO
  private String estado;
  private String estadoDesc;              // "Activo" | "Cancelado"

  // Situação do processo, derivada da etapa: o que falta fazer
  private String situacao;                // PENDENTE_REQUISICAO | PENDENTE_FATURA | POR_PAGAR | PAGO
  private String situacaoDesc;

  private String etapa;                   // etapa atual do processo
  private String etapaDesc;                   // etapa atual do processo desc
  private BigDecimal valorAC;             // soma VALOR_TOTAL onde REFERENCIA='AJUDA_CUSTO'
  private BigDecimal valorBP;             // REFERENCIA='BILHETE_PASSAGEM'
  private BigDecimal valorAlojamento;     // REFERENCIA='ALOJAMENTO'
  private BigDecimal valorSeguro;         // REFERENCIA='SEGURO_VIAGEM'

}

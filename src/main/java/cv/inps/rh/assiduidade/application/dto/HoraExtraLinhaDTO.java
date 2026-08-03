package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Nível 2 da lista de hora extra: uma linha por <strong>colaborador × mês</strong>.
 *
 * <p>Um pedido de 20/01/2026 a 10/03/2026 gera três destas por colaborador — 202601,
 * 202602 e 202603 — cada uma com os seus dias úteis/não úteis e valor acumulado,
 * conforme o exemplo da especificação.
 *
 * <p>As datas vêm recortadas às fronteiras do mês; o período integral do registo está
 * em {@code periodoInicio}/{@code periodoFim}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class HoraExtraLinhaDTO {

  private Long horaExtraId;

  private String horaExtraUuid;

  private String funcionarioUuid;

  private String nomeColaborador;

  private String cargo;

  private Long direcaoId;

  private String direcao;

  private Long seccaoId;

  private String seccao;

  private Long ilhaId;

  private String ilha;

  /** Mês de referência no formato {@code YYYYMM}. */
  private String mes;

  /** Mês por extenso, ex.: "Janeiro/2026". */
  private String mesDesc;

  /** Início do período dentro deste mês. */
  private String dataInicio;

  /** Fim do período dentro deste mês. */
  private String dataFim;

  /** Início do período integral do registo (todos os meses). */
  private String periodoInicio;

  /** Fim do período integral do registo (todos os meses). */
  private String periodoFim;

  private Integer diasUteis;

  private Integer diasNaoUteis;

  private String horasContratadaDiaria;

  private String horasContratadaMensal;

  private String horasExtraDiarias;

  private String horasTrabalho;

  private BigDecimal salarioMensal;

  private String percentagemReferente;

  private BigDecimal percentagemUtil;

  private BigDecimal percentagemNaoUtil;

  private BigDecimal valorDiarioUtil;

  private BigDecimal valorDiarioNaoUtil;

  /** Valor acumulado neste mês. Já é o somatório — não multiplicar. */
  private BigDecimal valorAcumuladoMes;

  private String estado;

  private String estadoDesc;

  private AnexoReqDTO documento;
}

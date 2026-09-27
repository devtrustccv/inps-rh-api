/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)
@IgrpDTO
public class DetalhesEmprestimoDTO extends PedidoEmprestimoDTO {


  private LocalDate dataInicio;


  private LocalDate dataFim;


  private BigDecimal valorPrestacao;


  private String cabimentacaoOrcamental;


  private String avaliacaoTaxaEsforco;


  private BigDecimal valorAdiantamento;


  private String tipoSituacao;

  private String nib;

  private Long numeroContaBanco;

  private String swift;

  private String motivo;

  private String estado;
  private String estadoDesc;

  private String etapa;
  private String etapaDesc;

  // Fundo Social / Recuperação (validação no frontend): tipoPedido vem de
  // RH_T_PEDIDO (RECUPERACAO distingue a Recuperação de um Fundo Social
  // normal), tipoEmprestimo de RH_T_EMPRESTIMO; nrPrestacaoPaga = linhas do
  // plano com FLG_PAGO = 'PAGO'.
  private String tipoPedido;

  private String tipoEmprestimo;

  private Long tipoMovimentoId;

  private String finalidade;

  private Long nrPrestacaoPaga;

  @Valid
  private List<OutrosEmprestimosDTO> emprestimos = new ArrayList<>();

  @Valid
  private List<OutrosEmprestimosDTO> outrosEmprestimos = new ArrayList<>();

  @Valid
  private List<DocumentoDTO> documentos = new ArrayList<>();

  @Valid
  private DecisaoEmprestimoDTO decisao;

}

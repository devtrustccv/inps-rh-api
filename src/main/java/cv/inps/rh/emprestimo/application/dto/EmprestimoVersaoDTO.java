/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Uma versão de um empréstimo — o empréstimo inicial ou um subprocesso (Amortização / Renegociação)
 * criado sobre ele. Todas as versões partilham o mesmo pedido (RH_T_PEDIDO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class EmprestimoVersaoDTO {

  private String emprestimoId;

  private Long versao;

  private String tipoSituacao;

  private String tipoSituacaoDesc;

  private String estado;

  private String estadoDesc;

  private BigDecimal valorConcedido;

  private BigDecimal valorReforco;

  private BigDecimal valorAdiantamento;

  private Long numeroTotalPrestacoes;

  private Long numeroPrestacoesPagas;

  private BigDecimal valorPrestacao;

  private LocalDate dataInicio;

  private LocalDate dataRegisto;

  // true na versão que a lista de empréstimos mostra (a mais recente do pedido)
  private Boolean atual;

}

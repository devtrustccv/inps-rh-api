/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

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
public class EmprestimoListRowDTO  {

  private UUID pedidoId;

  private UUID funcionarioId;

  private String tipoSituacao ;

  private String estado ;


  private String estadoDesc ;


  private String nomeColaborador ;


  private String tipoEmprestimo ;

  private String tipoEmprestimoDesc;


  private String renegociacaoDivida ;


  private String emprestimoId ;


  private BigDecimal valorConcedido ;
  private BigDecimal valorAdiantamento;
  private BigDecimal valorReforco;


  private Long numeroTotalPrestacoes;
  private Long numeroPrestacoesPagas ;


  private BigDecimal valorPago ;


  private BigDecimal saldoEmDivida ;


  private LocalDate dataInicioEmprestimo ;


  private String etapa ;
  private String etapaDesc;

  // Versão mais recente do pedido (pode ser um subprocesso ainda em curso) — é a que a etapa
  // do pedido descreve; o ▷ da lista abre esta. Igual a emprestimoId quando não há subprocesso.
  private String emprestimoIdEmCurso;

  private String tipoSituacaoEmCurso;

  // Nº de versões do pedido (empréstimo inicial + subprocessos); > 1 => linha expansível.
  private Long numeroVersoes;

}

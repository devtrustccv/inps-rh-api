/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class EmprestimoListRowDTO  {



  private String funcionarioId ;


  private String estado ;


  private String estadoDesc ;


  private String nomeColaborador ;


  private String tipoEmprestimo ;


  private String renegociacaoDivida ;


  private String emprestimoId ;


  private Long valorConcedido ;


  private Long numeroPrestacoesPagas ;


  private Long valorPago ;


  private Long saldoEmDivida ;


  private LocalDate dataInicioEmprestimo ;


  private String etapa ;

}

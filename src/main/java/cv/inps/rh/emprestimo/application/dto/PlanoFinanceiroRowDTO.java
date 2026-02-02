/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;

import java.time.LocalDate;

@IgrpDTO
public record PlanoFinanceiroRowDTO (

  Long numero,

  LocalDate dataPagamento,

  Long saldoInicial,

  Long pagamento,

  Long principal,

  Long juros,

  Long saldoFinal
){}

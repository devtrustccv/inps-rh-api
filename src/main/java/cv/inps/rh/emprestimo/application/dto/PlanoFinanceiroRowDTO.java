/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

@IgrpDTO
public record PlanoFinanceiroRowDTO (

  Long numero,

  LocalDate dataPagamento,

  BigDecimal saldoInicial,

  BigDecimal pagamento,

  BigDecimal principal,

  BigDecimal juros,

  BigDecimal saldoFinal
){}

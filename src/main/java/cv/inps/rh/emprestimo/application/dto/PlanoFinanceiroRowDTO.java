package cv.inps.rh.emprestimo.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.emprestimo.application.dto.serializer.PlanoFinanceiroSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

@IgrpDTO
public record PlanoFinanceiroRowDTO(

    Long numero,

    String estado,

    String flagPago,

    LocalDate dataPagamento,

    @JsonSerialize(using = PlanoFinanceiroSerializer.class)
    BigDecimal saldoInicial,

    @JsonSerialize(using = PlanoFinanceiroSerializer.class)
    BigDecimal pagamento,

    @JsonSerialize(using = PlanoFinanceiroSerializer.class)
    BigDecimal principal,

    @JsonSerialize(using = PlanoFinanceiroSerializer.class)
    BigDecimal juros,

    @JsonSerialize(using = PlanoFinanceiroSerializer.class)
    BigDecimal saldoFinal,
    // Ponto 33: só nas linhas de um subprocesso (Amortização / Renegociação) no plano — data em que
    // foi feito e a descrição (ex.: "Amortizado Parcial").
    LocalDate dataEstado,
    String renegociacao
) {
  public PlanoFinanceiroRowDTO(Long numero, String estado, String flagPago, LocalDate dataPagamento,
      BigDecimal saldoInicial, BigDecimal pagamento, BigDecimal principal, BigDecimal juros,
      BigDecimal saldoFinal) {
    this(numero, estado, flagPago, dataPagamento, saldoInicial, pagamento, principal, juros, saldoFinal,
        null, null);
  }
}

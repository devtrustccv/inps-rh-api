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
    BigDecimal saldoFinal
) {
}

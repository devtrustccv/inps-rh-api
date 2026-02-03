package cv.inps.rh.emprestimo.domain.service;

import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroRowDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancialPlanHelper {

  private static final int SCALE = 2;
  private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

  /**
   * Simula um plano financeiro usando o Sistema Francês (Price)
   */
  public static List<PlanoFinanceiroRowDTO> generateFinancialPlan(BigDecimal valorEmprestimo, BigDecimal taxaAnual, int prazoMeses, LocalDate dataInicio
  ) {

    var taxaMensal = taxaAnual
        .divide(BigDecimal.valueOf(12), MC);

    // prestacao = PV * (i / (1 - (1 + i)^-n))
    var umMaisTaxa = BigDecimal.ONE.add(taxaMensal, MC);
    var potencia = umMaisTaxa.pow(prazoMeses, MC);

    var prestacao = valorEmprestimo.multiply(taxaMensal, MC)
        .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(potencia, MC), MC), MC)
        .setScale(SCALE, RoundingMode.HALF_UP);

    var saldoInicial = valorEmprestimo.setScale(SCALE, RoundingMode.HALF_UP);

    List<PlanoFinanceiroRowDTO> plano = new ArrayList<>();

    for (int i = 1; i <= prazoMeses; i++) {

      var dataPagamento = dataInicio.plusMonths(i);

      var juros = saldoInicial
          .multiply(taxaMensal, MC)
          .setScale(SCALE, RoundingMode.HALF_UP);

      var principal = prestacao
          .subtract(juros)
          .setScale(SCALE, RoundingMode.HALF_UP);

      // Ajuste no último mês
      if (i == prazoMeses) {
        principal = saldoInicial;
        prestacao = principal.add(juros).setScale(SCALE, RoundingMode.HALF_UP);
      }

      var saldoFinal = saldoInicial
          .subtract(principal)
          .setScale(SCALE, RoundingMode.HALF_UP);

      plano.add(
          new PlanoFinanceiroRowDTO(
              (long) i,
              dataPagamento,
              saldoInicial,
              prestacao,
              principal,
              juros,
              saldoFinal
          )
      );

      saldoInicial = saldoFinal;
    }

    return plano;
  }
}

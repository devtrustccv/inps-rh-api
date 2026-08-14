package cv.inps.rh.emprestimo.domain.service;

import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroRowDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancialPlanHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(FinancialPlanHelper.class);
  private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

  private FinancialPlanHelper() {
  }

  public static List<PlanoFinanceiroRowDTO> generateFinancialPlan(
      BigDecimal valorEmprestimo,
      BigDecimal taxaAnual,
      int prazoMeses,
      LocalDate dataInicio
  ) {

    LOGGER.debug("VALOR EMPRESTIMO : {}", valorEmprestimo);
    LOGGER.debug("TAXA ANUAL : {}", taxaAnual);
    LOGGER.debug("PRAZO MESES : {}", prazoMeses);
    LOGGER.debug("DATA INICIO : {}", dataInicio);

    var taxaMensal = taxaAnual.divide(BigDecimal.valueOf(12), MC);
    LOGGER.debug("TAXA MENSAL : {}", taxaMensal);

    var umMaisTaxa = BigDecimal.ONE.add(taxaMensal, MC);
    var potencia = umMaisTaxa.pow(prazoMeses, MC);
    var prestacaoFixa = valorEmprestimo
        .multiply(taxaMensal, MC)
        .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(potencia, MC), MC), MC);

    LOGGER.debug("PRESTACAO FIXA : {}", prestacaoFixa);

    var saldoInicial = valorEmprestimo;

    var plano = new ArrayList<PlanoFinanceiroRowDTO>();

    for (int i = 1; i <= prazoMeses; i++) {

      var dataPagamento = dataInicio.plusMonths(i);
      var juros = saldoInicial.multiply(taxaMensal, MC);
      var principal = prestacaoFixa.subtract(juros);
      var pagamentoAtual = prestacaoFixa;
      // Ajuste no último mês para zerar saldo
      if (i == prazoMeses) {
        principal = saldoInicial;
        pagamentoAtual = principal.add(juros);
      }

      var saldoFinal = saldoInicial.subtract(principal);

      LOGGER.debug("MÊS {} | DATA: {} | SALDO INICIAL: {} | JUROS: {} | PRINCIPAL: {} | PAGAMENTO ATUAL: {} | SALDO FINAL: {}",
          i, dataPagamento, saldoInicial, juros, principal, pagamentoAtual, saldoFinal);

      plano.add(
          new PlanoFinanceiroRowDTO(
              (long) i,
              dataPagamento,
              saldoInicial.setScale(0, RoundingMode.HALF_UP),
              pagamentoAtual.setScale(0, RoundingMode.HALF_UP),
              principal.setScale(0, RoundingMode.HALF_UP),
              juros.setScale(0, RoundingMode.HALF_UP),
              saldoFinal.setScale(0, RoundingMode.HALF_UP)
          )
      );

      saldoInicial = saldoFinal;
    }

    return plano;
  }

  public static List<PlanoFinanceiroRowDTO> generateFinancialPlanForSocialFund(
      BigDecimal valorEmprestimo,
      BigDecimal taxaAnual,
      int prazoMeses,
      LocalDate dataInicio
  ) {

    LOGGER.debug("VALOR EMPRESTIMO FUNDO SOCIAL: {}", valorEmprestimo);
    LOGGER.debug("TAXA ANUAL FUNDO SOCIAL : {}", taxaAnual);
    LOGGER.debug("PRAZO MESES FUNDO SOCIAL : {}", prazoMeses);
    LOGGER.debug("DATA INICIO FUNDO SOCIAL: {}", dataInicio);

    var taxaMensal = taxaAnual.divide(BigDecimal.valueOf(12), MC);
    LOGGER.debug("TAXA MENSAL FUNDO SOCIAL: {}", taxaMensal);

    var umMaisTaxa = BigDecimal.ONE.add(taxaMensal, MC);
    var potencia = umMaisTaxa.pow(prazoMeses, MC);
    var prestacaoFixa = valorEmprestimo
        .multiply(taxaMensal, MC)
        .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(potencia, MC), MC), MC);

    LOGGER.debug("PRESTACAO FUNDO SOCIAL FIXA : {}", prestacaoFixa);

    var saldoInicial = valorEmprestimo;

    var plano = new ArrayList<PlanoFinanceiroRowDTO>();

    for (int i = 1; i <= prazoMeses; i++) {

      var dataPagamento = dataInicio.plusMonths(i);
      var principal = prestacaoFixa;
      var pagamentoAtual = prestacaoFixa;
      // Ajuste no último mês para zerar saldo
      if (i == prazoMeses) {
        principal = saldoInicial;
        pagamentoAtual = principal;
      }

      var saldoFinal = saldoInicial.subtract(principal);

      LOGGER.debug("MÊS {} | DATA: {} | SALDO INICIAL: {} | PRINCIPAL: {} | PAGAMENTO ATUAL: {} | SALDO FINAL: {}",
          i, dataPagamento, saldoInicial, principal, pagamentoAtual, saldoFinal);

      plano.add(
          new PlanoFinanceiroRowDTO(
              (long) i,
              dataPagamento,
              saldoInicial.setScale(0, RoundingMode.HALF_UP),
              pagamentoAtual.setScale(0, RoundingMode.HALF_UP),
              principal.setScale(0, RoundingMode.HALF_UP),
              null,
              saldoFinal.setScale(0, RoundingMode.HALF_UP)
          )
      );

      saldoInicial = saldoFinal;
    }

    return plano;
  }
}

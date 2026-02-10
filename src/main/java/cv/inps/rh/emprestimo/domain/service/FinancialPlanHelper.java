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

  private static final int SCALE = 2;
  private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

  /**
   * Simula um plano financeiro usando o Sistema Francês (Price)
   *
   * @param valorEmprestimo Valor do empréstimo
   * @param taxaAnual       Taxa de juros anual (ex: 0.035 = 3.5%)
   * @param prazoMeses      Número de meses
   * @param dataInicio      Data do primeiro pagamento
   * @return Lista de linhas do plano financeiro
   */
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
        .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(potencia, MC), MC), MC)
        .setScale(SCALE, RoundingMode.HALF_UP);

    LOGGER.debug("PRESTACAO FIXA : {}", prestacaoFixa);

    var saldoInicial = valorEmprestimo.setScale(SCALE, RoundingMode.HALF_UP);

    var plano = new ArrayList<PlanoFinanceiroRowDTO>();

    for (int i = 1; i <= prazoMeses; i++) {

      var dataPagamento = dataInicio.plusMonths(i);

      var juros = saldoInicial.multiply(taxaMensal, MC)
          .setScale(SCALE, RoundingMode.HALF_UP);

      var principal = prestacaoFixa.subtract(juros)
          .setScale(SCALE, RoundingMode.HALF_UP);

      var pagamentoAtual = prestacaoFixa;

      // Ajuste no último mês para zerar saldo
      if (i == prazoMeses) {
        principal = saldoInicial;
        pagamentoAtual = principal.add(juros).setScale(SCALE, RoundingMode.HALF_UP);
      }

      var saldoFinal = saldoInicial.subtract(principal)
          .setScale(SCALE, RoundingMode.HALF_UP);

      LOGGER.debug("MÊS {} | DATA: {} | SALDO INICIAL: {} | JUROS: {} | PRINCIPAL: {} | PAGAMENTO ATUAL: {} | SALDO FINAL: {}",
          i, dataPagamento, saldoInicial, juros, principal, pagamentoAtual, saldoFinal);

      plano.add(
          new PlanoFinanceiroRowDTO(
              (long) i,
              dataPagamento,
              saldoInicial,
              pagamentoAtual,
              principal,
              juros,
              saldoFinal
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
        .divide(BigDecimal.ONE.subtract(BigDecimal.ONE.divide(potencia, MC), MC), MC)
        .setScale(SCALE, RoundingMode.HALF_UP);

    LOGGER.debug("PRESTACAO FUNDO SOCIAL FIXA : {}", prestacaoFixa);

    var saldoInicial = valorEmprestimo.setScale(SCALE, RoundingMode.HALF_UP);

    var plano = new ArrayList<PlanoFinanceiroRowDTO>();

    for (int i = 1; i <= prazoMeses; i++) {

      var dataPagamento = dataInicio.plusMonths(i);

      var principal = prestacaoFixa.setScale(SCALE, RoundingMode.HALF_UP);

      var pagamentoAtual = prestacaoFixa;

      // Ajuste no último mês para zerar saldo
      if (i == prazoMeses) {
        principal = saldoInicial;
        pagamentoAtual = principal.setScale(SCALE, RoundingMode.HALF_UP);
      }

      var saldoFinal = saldoInicial.subtract(principal)
          .setScale(SCALE, RoundingMode.HALF_UP);

      LOGGER.debug("MÊS {} | DATA: {} | SALDO INICIAL: {} | PRINCIPAL: {} | PAGAMENTO ATUAL: {} | SALDO FINAL: {}",
          i, dataPagamento, saldoInicial, principal, pagamentoAtual, saldoFinal);

      plano.add(
          new PlanoFinanceiroRowDTO(
              (long) i,
              dataPagamento,
              saldoInicial,
              pagamentoAtual,
              principal,
              null,
              saldoFinal
          )
      );

      saldoInicial = saldoFinal;
    }

    return plano;
  }
}

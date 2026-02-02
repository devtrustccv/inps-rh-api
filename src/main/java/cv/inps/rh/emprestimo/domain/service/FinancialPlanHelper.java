package cv.inps.rh.emprestimo.domain.service;

import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroRowDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancialPlanHelper {

  /**
   * Simula um plano financeiro usando o Sistema Francês (Price)
   *
   * @param valorEmprestado valor do empréstimo (PV)
   * @param taxaAnual       taxa de juro anual (ex: 0.035 = 3.5%)
   * @param prazoMeses      número total de meses
   * @param dataInicio      data base para o primeiro pagamento
   * @return list de linhas do plano financeiro
   */
  public static List<PlanoFinanceiroRowDTO> generateFinancialPlan(double valorEmprestado, double taxaAnual, int prazoMeses, LocalDate dataInicio){

    double taxaMensal = taxaAnual / 12;

    double prestacao = valorEmprestado * (taxaMensal / (1 - Math.pow(1 + taxaMensal, -prazoMeses)));

    double saldoInicial = valorEmprestado;

    List<PlanoFinanceiroRowDTO> plano = new ArrayList<>();

    for (int i = 1; i <= prazoMeses; i++) {

      LocalDate dataPagamento = dataInicio.plusMonths(i);

      double juros = saldoInicial * taxaMensal;
      double principal = prestacao - juros;

      // Ajuste no último mês para evitar resíduos
      if (i == prazoMeses) {
        principal = saldoInicial;
        prestacao = principal + juros;
      }

      double saldoFinal = saldoInicial - principal;

      plano.add(
          new PlanoFinanceiroRowDTO(
              (long) i,
              dataPagamento,
              Math.round(saldoInicial),
              Math.round(prestacao),
              Math.round(principal),
              Math.round(juros),
              Math.round(saldoFinal)
          )
      );

      saldoInicial = saldoFinal;
    }

    return plano;
  }
}

package cv.inps.rh.emprestimo.domain.service;

import cv.inps.rh.emprestimo.application.dto.FundoSocialRequestDTO;
import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroRowDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancialPlanHelperTest {

  @Test
  void shouldGeneratePlanStartingAfterPaidInstallments() {
    var plan = FinancialPlanHelper.generateFinancialPlan(
        BigDecimal.valueOf(1_000),
        BigDecimal.valueOf(0.12),
        3,
        LocalDate.of(2026, 1, 1),
        3L
    );

    assertEquals(List.of(3L, 4L, 5L), plan.stream()
        .map(PlanoFinanceiroRowDTO::numero)
        .toList());
  }

  @Test
  void shouldStartAtOneWhenInitialNumberIsNotProvided() {
    var plan = FinancialPlanHelper.generateFinancialPlan(
        BigDecimal.valueOf(1_000),
        BigDecimal.valueOf(0.12),
        3,
        LocalDate.of(2026, 1, 1)
    );

    assertEquals(List.of(1L, 2L, 3L), plan.stream()
        .map(PlanoFinanceiroRowDTO::numero)
        .toList());
  }

  @Test
  void shouldMarkExactlyTheInstallmentsAlreadyPaidInRecuperacao() {
    var request = new FundoSocialRequestDTO();
    request.setDataInicio(LocalDate.of(2026, 1, 1));
    request.setValorTotalEmprestimo(BigDecimal.valueOf(6_000));
    request.setValorPrestacaoMensal(BigDecimal.valueOf(1_000));
    request.setNrPrestacao(6L);
    request.setNrPrestacaoPaga(2L);

    var plan = FinancialPlanHelper.generateFinancialPlanForRecuperacao(request);

    assertEquals(List.of("PAGO", "PAGO", "-", "-", "-", "-"), plan.stream()
        .map(row -> row.flagPago() == null ? "-" : row.flagPago())
        .toList());
  }

  @Test
  void shouldNotMarkAnyInstallmentWhenNoneWasPaid() {
    var request = new FundoSocialRequestDTO();
    request.setDataInicio(LocalDate.of(2026, 1, 1));
    request.setValorTotalEmprestimo(BigDecimal.valueOf(3_000));
    request.setValorPrestacaoMensal(BigDecimal.valueOf(1_000));
    request.setNrPrestacao(3L);

    var plan = FinancialPlanHelper.generateFinancialPlanForRecuperacao(request);

    assertEquals(0, plan.stream().filter(row -> row.flagPago() != null).count());
  }
}

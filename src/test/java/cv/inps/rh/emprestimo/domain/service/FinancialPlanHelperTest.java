package cv.inps.rh.emprestimo.domain.service;

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
}

package cv.inps.rh.emprestimo.domain.service.process;

import cv.inps.rh.emprestimo.domain.service.constants.TipoSituacao;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PlanoFinanceiroEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PlanoFinanceiroEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoHelperTest {

  @Mock
  private EmprestimoEntityRepository emprestimoEntityRepository;

  @Mock
  private PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;

  @InjectMocks
  private EmprestimoHelper emprestimoHelper;

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"})
  void shouldCopyPaidPlansAndContinuePlanNumbering() {
    var initialLoan = new EmprestimoEntity();
    initialLoan.setId(10L);

    var newLoan = new EmprestimoEntity();
    newLoan.setId(11L);
    newLoan.setEmprestimo(initialLoan);
    newLoan.setNrPrestacao(5L);
    newLoan.setValorDivida(BigDecimal.valueOf(1_000));
    newLoan.setJuro(BigDecimal.valueOf(12));
    newLoan.setDataInicio(LocalDate.of(2026, 1, 1));

    var paidPlan = plan(1L, "PAGO");
    var secondPaidPlan = plan(2L, "PAGO");
    var unpaidPlan = plan(3L, null);
    when(planoFinanceiroEntityRepository.findAllByEmprestimo(initialLoan))
        .thenReturn(List.of(paidPlan, secondPaidPlan, unpaidPlan));

    emprestimoHelper.saveByTipoSituacao(
        TipoSituacao.REFORCO_AUMENTO_VALOR,
        newLoan,
        BigDecimal.valueOf(200),
        5L
    );

    var batchesCaptor = (ArgumentCaptor<Iterable<PlanoFinanceiroEntity>>) (ArgumentCaptor)
        ArgumentCaptor.forClass(Iterable.class);
    verify(planoFinanceiroEntityRepository, times(2)).saveAll(batchesCaptor.capture());

    var savedBatches = batchesCaptor.getAllValues().stream()
        .map(batch -> StreamSupport.stream(batch.spliterator(), false).toList())
        .toList();
    var copiedPaidPlan = savedBatches.getFirst().getFirst();
    var regeneratedPlans = savedBatches.get(1);

    assertAll(
        () -> assertEquals(2, savedBatches.getFirst().size()),
        () -> assertSame(newLoan, copiedPaidPlan.getEmprestimo()),
        () -> assertNotEquals(paidPlan.getUuid(), copiedPaidPlan.getUuid()),
        () -> assertEquals(Estado.A.name(), copiedPaidPlan.getEstado()),
        () -> assertEquals(paidPlan.getNrOrdemPrestacao(), copiedPaidPlan.getNrOrdemPrestacao()),
        () -> assertEquals(paidPlan.getDataPagamento(), copiedPaidPlan.getDataPagamento()),
        () -> assertEquals(paidPlan.getValorPrincipal(), copiedPaidPlan.getValorPrincipal()),
        () -> assertEquals(paidPlan.getValorJuros(), copiedPaidPlan.getValorJuros()),
        () -> assertEquals(paidPlan.getFlgPago(), copiedPaidPlan.getFlgPago()),
        () -> assertEquals(paidPlan.getValorPago(), copiedPaidPlan.getValorPago()),
        () -> assertEquals(paidPlan.getDefpId(), copiedPaidPlan.getDefpId()),
        () -> assertEquals(paidPlan.getSaldoInicial(), copiedPaidPlan.getSaldoInicial()),
        () -> assertEquals(paidPlan.getSaldoFinal(), copiedPaidPlan.getSaldoFinal()),
        () -> assertEquals(List.of(3L, 4L, 5L), regeneratedPlans.stream()
            .map(PlanoFinanceiroEntity::getNrOrdemPrestacao)
            .toList())
    );

    verify(planoFinanceiroEntityRepository).inativarPlanos(initialLoan.getId());
  }

  private PlanoFinanceiroEntity plan(Long order, String paymentFlag) {
    var plan = new PlanoFinanceiroEntity();
    plan.setUuid("plan-" + order);
    plan.setNrOrdemPrestacao(order);
    plan.setDataPagamento(LocalDate.of(2026, 1, 1).plusMonths(order));
    plan.setValorPrincipal(BigDecimal.valueOf(100));
    plan.setValorJuros(BigDecimal.TEN);
    plan.setFlgPago(paymentFlag);
    plan.setValorPago(BigDecimal.valueOf(110));
    plan.setDefpId(99L);
    plan.setSaldoInicial(BigDecimal.valueOf(1_000));
    plan.setSaldoFinal(BigDecimal.valueOf(900));
    plan.setEstado(Estado.A.name());
    return plan;
  }
}

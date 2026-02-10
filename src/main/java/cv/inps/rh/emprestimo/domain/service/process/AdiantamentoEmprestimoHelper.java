package cv.inps.rh.emprestimo.domain.service.process;

import cv.inps.rh.emprestimo.domain.service.FinancialPlanHelper;
import cv.inps.rh.emprestimo.domain.service.constants.TipoSituacao;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PlanoFinanceiroEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PlanoFinanceiroEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Transactional
@RequiredArgsConstructor
@Service
public class AdiantamentoEmprestimoHelper {

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;

  public void saveByTipoSituacao(TipoSituacao tipoSituacao, EmprestimoEntity loan, BigDecimal value, Long newNumeroPrestacao) {

    var allPlans = planoFinanceiroEntityRepository.findAllByEmprestimo(loan);

    // TODO 09/02/2026 19:01 inactivate not payed plans
    var notPayedPlans = allPlans.stream().filter(obj -> !obj.getFlgPago().equals("PAGO")).toList();

    var paidValue = allPlans.stream()
        .filter(obj -> obj.getFlgPago().equals("PAGO"))
        .map(PlanoFinanceiroEntity::getValorPago)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var numberOfPaidPrestations = allPlans.stream()
        .filter(obj -> obj.getFlgPago().equals("PAGO"))
        .count();

    // adiantamento valor, valor em divida diminui , valor pago , valor adiantado
    // adiantamento na prestacao, diminuir numero de prestacao e valor prestacao

    // reforço mexe com valor em divida e com valor reforço
    // reforço sobre prestacao mexe com valor prestacao e com numero de pretacao

    var numeroPrestacoes = (loan.getNrPrestacao() - numberOfPaidPrestations);

    switch (tipoSituacao) {
      case REFORCO_AUMENTO_VALOR -> {

        loan.setValorReforco(value);
        loan.setValorDivida(loan.getValorDivida().subtract(value));
        emprestimoEntityRepository.save(loan);

        FinancialPlanHelper.generateFinancialPlan(
            loan.getValorDivida(),
            loan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            (int) numeroPrestacoes,
            loan.getDataInicio() != null ? loan.getDataInicio() : LocalDate.now()
        );
      }
      case REFORCO_AUMENTO_PRESTACAO -> {

        loan.setNrPrestacao(newNumeroPrestacao);
        emprestimoEntityRepository.save(loan);

        FinancialPlanHelper.generateFinancialPlan(
            loan.getValorDivida(),
            loan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            newNumeroPrestacao.intValue(),
            loan.getDataInicio() != null ? loan.getDataInicio() : LocalDate.now()
        );
      }
      case REFORCO_AUMENTO_VALOR_AUMENTO_PRESTACAO -> {

        loan.setNrPrestacao(newNumeroPrestacao);
        loan.setValorReforco(value);
        loan.setValorDivida(loan.getValorDivida().subtract(value));
        emprestimoEntityRepository.save(loan);

        FinancialPlanHelper.generateFinancialPlan(
            loan.getValorDivida(),
            loan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            newNumeroPrestacao.intValue(),
            loan.getDataInicio() != null ? loan.getDataInicio() : LocalDate.now()
        );

      }

    }

  }

}

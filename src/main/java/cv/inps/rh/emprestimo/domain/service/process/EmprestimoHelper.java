package cv.inps.rh.emprestimo.domain.service.process;

import cv.inps.rh.emprestimo.domain.service.FinancialPlanHelper;
import cv.inps.rh.emprestimo.domain.service.constants.TipoSituacao;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PlanoFinanceiroEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;

@Transactional
@RequiredArgsConstructor
@Service
public class EmprestimoHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmprestimoHelper.class);

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;

  public void saveByTipoSituacao(TipoSituacao tipoSituacao, EmprestimoEntity loan, BigDecimal value, Long newNumeroPrestacao) {

    // TODO 21/08/2026 17:29 registar planos pagos em caso de haver para o novo emprestimo adinatamento ou reforço

    LOGGER.debug("TIPO SITUACAO: {}, LOAN ID: {}, VALUE: {}, NUMERO PRESTACOES: {}", tipoSituacao, loan.getId(), value, newNumeroPrestacao);

    var allPlans = planoFinanceiroEntityRepository.findAllByEmprestimo(loan);

    var numberOfPaidPrestations = allPlans.stream()
        .filter(obj -> "PAGO".equals(obj.getFlgPago()))
        .count();

    // adiantamento valor, valor em divida diminui , valor pago , valor adiantado
    // adiantamento na prestacao, diminuir numero de prestacao e valor prestacao

    // reforço mexe com valor em divida e com valor reforço
    // reforço sobre prestacao mexe com valor prestacao e com numero de pretacao

    var startDate = loan.getDataInicio() != null ? loan.getDataInicio() : LocalDate.now(ZoneId.systemDefault());

    switch (tipoSituacao) {
      case REFORCO_AUMENTO_VALOR -> {

        var numeroPrestacoes = (loan.getNrPrestacao() - numberOfPaidPrestations);

        loan.setValorReforco(value);
        loan.setValorDivida(loan.getValorDivida().add(value));
        emprestimoEntityRepository.save(loan);

        FinancialPlanHelper.generateFinancialPlan(
            loan.getValorDivida(),
            loan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            (int) numeroPrestacoes,
            startDate
        );
      }
      case REFORCO_AUMENTO_PRESTACAO, ADIANTAMENTO_DIMINUICAO_PRESTACAO -> {

        loan.setNrPrestacao(newNumeroPrestacao);
        emprestimoEntityRepository.save(loan);

        FinancialPlanHelper.generateFinancialPlan(
            loan.getValorDivida(),
            loan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            newNumeroPrestacao.intValue(),
            startDate
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
            startDate
        );
      }
      case ADIANTAMENTO_PAGAMENTO_ANTECIPADO -> {

        var numeroPrestacoes = (loan.getNrPrestacao() - numberOfPaidPrestations);

        loan.setValorAdiantado(value);
        loan.setValorDivida(loan.getValorDivida().subtract(value));
        emprestimoEntityRepository.save(loan);

        FinancialPlanHelper.generateFinancialPlan(
            loan.getValorDivida(),
            loan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            (int) numeroPrestacoes,
            startDate
        );
      }
      case ADIANTAMENTO_PAGAMENTO_ANTECIPADO_DIMINUICAO_PRESTACAO -> {

        loan.setValorAdiantado(value);
        loan.setNrPrestacao(newNumeroPrestacao);
        loan.setValorDivida(loan.getValorDivida().subtract(value));
        emprestimoEntityRepository.save(loan);

        FinancialPlanHelper.generateFinancialPlan(
            loan.getValorDivida(),
            loan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            newNumeroPrestacao.intValue(),
            startDate
        );
      }
    }
  }
}

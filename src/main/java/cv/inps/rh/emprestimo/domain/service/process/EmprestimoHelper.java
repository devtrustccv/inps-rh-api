package cv.inps.rh.emprestimo.domain.service.process;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroRowDTO;
import cv.inps.rh.emprestimo.domain.service.FinancialPlanHelper;
import cv.inps.rh.emprestimo.domain.service.constants.TipoSituacao;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PlanoFinanceiroEntity;
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
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class EmprestimoHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmprestimoHelper.class);

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;

  public void saveByTipoSituacao(TipoSituacao tipoSituacao, EmprestimoEntity newLoan, BigDecimal value, Long newNumeroPrestacao) {

    var initialLoan = newLoan.getEmprestimo();

    LOGGER.debug("TIPO SITUACAO: {}, LOAN ID: {}, VALUE: {}, NUMERO PRESTACOES: {}", tipoSituacao, newLoan.getId(), value, newNumeroPrestacao);

    var allInitialPlans = planoFinanceiroEntityRepository.findAllByEmprestimo(initialLoan);
    var allInitialPaidPlans = allInitialPlans.stream()
        .filter(obj -> "PAGO".equals(obj.getFlgPago()))
        .toList();
    var numberOfPaidPlans = allInitialPaidPlans.size();
    var firstNewPlanNumber = numberOfPaidPlans + 1L;

    copyPaidPlans(allInitialPaidPlans, newLoan);

    planoFinanceiroEntityRepository.inativarPlanos(initialLoan.getId());

    // adiantamento valor, valor em divida diminui , valor pago , valor adiantado
    // adiantamento na prestacao, diminuir numero de prestacao e valor prestacao
    // reforço mexe com valor em divida e com valor reforço
    // reforço sobre prestacao mexe com valor prestacao e com numero de prestacao

    var startDate = newLoan.getDataInicio() != null ? newLoan.getDataInicio() : LocalDate.now(ZoneId.systemDefault());

    List<PlanoFinanceiroRowDTO> newPlans = new ArrayList<>();

    switch (tipoSituacao) {
      case REFORCO_AUMENTO_VALOR -> {

        var numeroPrestacoes = (newLoan.getNrPrestacao() - numberOfPaidPlans);

        newLoan.setValorReforco(value);
        newLoan.setValorDivida(newLoan.getValorDivida().add(value));
        emprestimoEntityRepository.save(newLoan);

        newPlans = FinancialPlanHelper.generateFinancialPlan(
            newLoan.getValorDivida(),
            newLoan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            (int) numeroPrestacoes,
            startDate,
            firstNewPlanNumber
        );
      }
      case REFORCO_AUMENTO_PRESTACAO, ADIANTAMENTO_DIMINUICAO_PRESTACAO -> {

        newLoan.setNrPrestacao(newNumeroPrestacao);
        emprestimoEntityRepository.save(newLoan);

        newPlans = FinancialPlanHelper.generateFinancialPlan(
            newLoan.getValorDivida(),
            newLoan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            newNumeroPrestacao.intValue(),
            startDate,
            firstNewPlanNumber
        );
      }
      case REFORCO_AUMENTO_VALOR_AUMENTO_PRESTACAO -> {

        newLoan.setNrPrestacao(newNumeroPrestacao);
        newLoan.setValorReforco(value);
        newLoan.setValorDivida(newLoan.getValorDivida().subtract(value));
        emprestimoEntityRepository.save(newLoan);

        newPlans = FinancialPlanHelper.generateFinancialPlan(
            newLoan.getValorDivida(),
            newLoan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            newNumeroPrestacao.intValue(),
            startDate,
            firstNewPlanNumber
        );
      }
      case ADIANTAMENTO_PAGAMENTO_ANTECIPADO -> {

        var numeroPrestacoes = (newLoan.getNrPrestacao() - numberOfPaidPlans);

        newLoan.setValorAdiantado(value);
        newLoan.setValorDivida(newLoan.getValorDivida().subtract(value));
        emprestimoEntityRepository.save(newLoan);

        newPlans = FinancialPlanHelper.generateFinancialPlan(
            newLoan.getValorDivida(),
            newLoan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            (int) numeroPrestacoes,
            startDate,
            firstNewPlanNumber
        );
      }
      case ADIANTAMENTO_PAGAMENTO_ANTECIPADO_DIMINUICAO_PRESTACAO -> {

        newLoan.setValorAdiantado(value);
        newLoan.setNrPrestacao(newNumeroPrestacao);
        newLoan.setValorDivida(newLoan.getValorDivida().subtract(value));
        emprestimoEntityRepository.save(newLoan);

        newPlans = FinancialPlanHelper.generateFinancialPlan(
            newLoan.getValorDivida(),
            newLoan.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
            newNumeroPrestacao.intValue(),
            startDate,
            firstNewPlanNumber
        );
      }
    }

    savePlans(newLoan, newPlans);
  }

  public void savePlans(EmprestimoEntity entity, List<PlanoFinanceiroRowDTO> plan) {
    var plans = plan.stream()
        .map(obj -> {
          var newPlan = new PlanoFinanceiroEntity();
          newPlan.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          newPlan.setEstado(Estado.A.name());
          newPlan.setEmprestimo(entity);
          newPlan.setDataPagamento(obj.dataPagamento());
          newPlan.setNrOrdemPrestacao(obj.numero());
          newPlan.setValorPrincipal(obj.principal());
          newPlan.setValorJuros(obj.juros());
          newPlan.setSaldoInicial(obj.saldoInicial());
          newPlan.setSaldoFinal(obj.saldoFinal());
          return newPlan;
        })
        .toList();
    planoFinanceiroEntityRepository.saveAll(plans);
  }

  private void copyPaidPlans(List<PlanoFinanceiroEntity> allInitialPaidPlans, EmprestimoEntity newLoan) {
    var paidPlans = allInitialPaidPlans.stream()
        .map(obj -> {
          var copiedPlan = new PlanoFinanceiroEntity();
          copiedPlan.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          copiedPlan.setEstado(Estado.A.name());
          copiedPlan.setEmprestimo(newLoan);
          copiedPlan.setNrOrdemPrestacao(obj.getNrOrdemPrestacao());
          copiedPlan.setDataPagamento(obj.getDataPagamento());
          copiedPlan.setValorPrincipal(obj.getValorPrincipal());
          copiedPlan.setValorJuros(obj.getValorJuros());
          copiedPlan.setFlgPago(obj.getFlgPago());
          copiedPlan.setValorPago(obj.getValorPago());
          copiedPlan.setDefpId(obj.getDefpId());
          copiedPlan.setSaldoInicial(obj.getSaldoInicial());
          copiedPlan.setSaldoFinal(obj.getSaldoFinal());
          return copiedPlan;
        })
        .toList();

    planoFinanceiroEntityRepository.saveAll(paidPlans);
  }
}

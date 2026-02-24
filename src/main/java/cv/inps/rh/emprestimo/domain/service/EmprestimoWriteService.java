package cv.inps.rh.emprestimo.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.commands.SaveConfiguracaoInfoEmprestimoCommand;
import cv.inps.rh.emprestimo.application.dto.FundoSocialRequestDTO;
import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroRowDTO;
import cv.inps.rh.emprestimo.domain.service.constants.EtapaEmprestimo;
import cv.inps.rh.emprestimo.domain.service.constants.TipoPedido;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class EmprestimoWriteService {

  private final ParamEmprestimoEntityRepository paramEmprestimoEntityRepository;
  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final ParamCarreiraEntityRepository paramCarreiraEntityRepository;
  private final PedidoEntityRepository pedidoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final EmprestimoDocumentService documentService;

  public void saveConfiguracaoEmprestimo(SaveConfiguracaoInfoEmprestimoCommand command) {

    var entities = new ArrayList<ParamEmprestimoEntity>();

    for (var row : command.getInformacaoemprestimorequest()) {

      final ParamEmprestimoEntity entity;

      if (StringUtils.hasText(row.getId())) {
        entity = paramEmprestimoEntityRepository.findByUuidOrThrow(row.getId());
      } else {
        entity = new ParamEmprestimoEntity();
        entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      }

      entity.setCarrPccs(paramCarreiraEntityRepository.findByIdOrThrow(row.getCarreiraId()));
      entity.setValorLimite(row.getValorLimiteEmprestimo());
      entity.setNumeroLimite(row.getNumeroLimitePrestacaoMeses());
      entity.setEstado(row.getEstado());
      entities.add(entity);
    }

    paramEmprestimoEntityRepository.saveAll(entities);
  }

  public void saveFundoSocial(List<FundoSocialRequestDTO> requests) {

    for (var request : requests) {

      var currentRelation = funcionarioRules.getTipoRelacionamentoAtual(UUID.fromString(request.getFuncionarioId()));

      var entity = new EmprestimoEntity();
      entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      entity.setTmId(request.getTipoMovimentoId());
      entity.setDataInicio(request.getDataInicio());
      entity.setDataFim(request.getDataFim());
      entity.setEstado(Estado.A.name());
      entity.setValorPrestacao(request.getValorPrestacaoMensal());
      entity.setValorEmprestimo(request.getValorTotalEmprestimo());
      entity.setFinalidade(request.getFinalidade());
      entity.setTipoEmprestimo(TipoPedido.FUNDO_SOCIAL.name());
      entity.setTipoSituacao(TipoPedido.FUNDO_SOCIAL.name());
      entity.setVersao(1L);
      entity.setTiprel(currentRelation);
      entity.setNrPrestacao(DateFormatter.monthsBetween(request.getDataInicio(), request.getDataFim()));

      var funId = currentRelation.getFunId();

      var orderOP = pedidoEntityRepository.findByFunIdAndTipoPedidoAndEstado(funId, TipoPedido.FUNDO_SOCIAL.name(), Estado.A.name());
      if (orderOP.isEmpty()) {
        var order = new PedidoEntity();
        order.setFunId(funId);
        order.setUuid(UuidCreator.getTimeOrderedEpoch());
        order.setTipoPedido(TipoPedido.FUNDO_SOCIAL.name());
        order.setOrigem("RH");
        order.setEtapa(EtapaEmprestimo.PEDIDO.name());
        order.setEstado(Estado.A.name());
        var savedOrder = pedidoEntityRepository.save(order);
        entity.setPedido(savedOrder);
      }

      var savedEntity = emprestimoEntityRepository.save(entity);

      generateFinancialPlanForFundoSocial(savedEntity);

      documentService.saveDocuments(
          request.getDocumentos(),
          funId,
          savedEntity.getUuid(),
          TipoPedido.FUNDO_SOCIAL.name()
      );

      var defPagamentoEntity = new DefPagamentoEntity();
      defPagamentoEntity.setTmId(tipoMovimentoEntityRepository.getReferenceById(request.getTipoMovimentoId()));
      defPagamentoEntity.setValor(entity.getValorPrestacao());
      defPagamentoEntity.setDataInicio(entity.getDataInicio());
      defPagamentoEntity.setDataFim(entity.getDataFim());
      defPagamentoEntity.setEstado(Estado.A);
      defPagamentoEntity.setUuid(UuidCreator.getTimeOrderedEpoch());
      defPagamentoEntity.setFunId(funId);
      var savedDefPag = defPagamentoEntityRepository.save(defPagamentoEntity);

      var tipoRel = new TipoRelRemPagEntity();
      tipoRel.setTiprelId(currentRelation);
      tipoRel.setPagId(savedDefPag);
      tipoRelRemPagEntityRepository.save(tipoRel);
    }
  }

  public List<PlanoFinanceiroRowDTO> generateFinancialPlan(String uuid) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    return generateFinancialPlan(entity);
  }

  public List<PlanoFinanceiroRowDTO> generateFinancialPlan(EmprestimoEntity entity) {

    var plan = FinancialPlanHelper.generateFinancialPlan(
        entity.getValorEmprestimo(),
        entity.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
        entity.getNrPrestacao().intValue(),
        entity.getDataInicio() != null ? entity.getDataInicio() : LocalDate.now()
    );

    savePlans(entity, plan);

    return plan;
  }

  public void generateFinancialPlanForFundoSocial(EmprestimoEntity entity) {

    var plan = FinancialPlanHelper.generateFinancialPlanForSocialFund(
        entity.getValorEmprestimo(),
        entity.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
        entity.getNrPrestacao().intValue(),
        entity.getDataInicio() != null ? entity.getDataInicio() : LocalDate.now()
    );

    savePlans(entity, plan);
  }

  private void savePlans(EmprestimoEntity entity, List<PlanoFinanceiroRowDTO> plan) {
    for (var obj : plan) {
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
      planoFinanceiroEntityRepository.save(newPlan);
    }
  }
}


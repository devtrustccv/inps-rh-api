package cv.inps.rh.emprestimo.domain.service;

import cv.inps.rh.emprestimo.application.constants.ParecerProcesso;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.application.queries.ListarEmprestimosQuery;
import cv.inps.rh.emprestimo.domain.service.constants.EtapaEmprestimo;
import cv.inps.rh.emprestimo.domain.service.constants.ReferenceName;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.NumberUtils;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EmprestimoReadService {

  private final ParamEmprestimoEntityRepository paramEmprestimoEntityRepository;
  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;
  private final RhPagamentoEntityRepository rhPagamentoEntityRepository;
  private final EmprestimoDocumentService documentService;
  private final EmprestimoWriteService emprestimoWriteService;

  public List<InformacaoEmprestimoRequestDTO> getAllConfiguracaoEmprestimo() {
    return paramEmprestimoEntityRepository.findAll()
        .stream()
        .map(entity -> new InformacaoEmprestimoRequestDTO(
            entity.getCarrPccs().getId(),
            entity.getValorLimite(),
            entity.getNumeroLimite(),
            entity.getEstado(),
            entity.getUuid(),
            entity.getCarrPccs().getUuid().toString()
        ))
        .toList();
  }

  public DetalhesEmprestimoDTO getEmprestimoByUuid(String uuid) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var funId = entity.getTiprel().getFunId();

    var dto = new DetalhesEmprestimoDTO();
    dto.setDataInicio(entity.getDataInicio());
    dto.setDataFim(entity.getDataFim());
    dto.setValorPrestacao(entity.getValorPrestacao());
    dto.setMarca(entity.getMarca());
    dto.setAnoFabrico(entity.getAnoFabrico());
    dto.setCilindrada(entity.getCilincrada());
    dto.setTipoviatura(entity.getTipoViatura());
    dto.setCombustivel(entity.getCombustivel());
    dto.setEstadoViatura(entity.getEstadoViatura());
    dto.setValorEmprestimo(entity.getValorEmprestimo());
    dto.setNumeroPrestacoes(entity.getNrPrestacao());
    dto.setJuros(entity.getJuro());
    dto.setFuncionarioId(funId.getUuid().toString());
    dto.setCabimentacaoOrcamental(entity.getDescCabimentacaoOrcamental());
    dto.setAvaliacaoTaxaEsforco(entity.getDescTaxaEsforco());
    dto.setTipoSituacao(entity.getTipoSituacao());
    dto.setValorAdiantamento(entity.getValorAdiantado());
    dto.setNib(entity.getNib());
    dto.setSwift(entity.getSwift());
    dto.setMotivo(entity.getMotivo());
    ofNullable(entity.getBanco()).ifPresent(o -> {
      dto.setBancoId(o.getId());
      dto.setNumeroContaBanco(o.getNuConta());
    });

    var another = emprestimoEntityRepository.findByUuidNotAndTiprel_FunId(entity.getUuid(), funId)
        .stream()
        .map(obj -> new OutrosEmprestimosDTO(
            obj.getTipoEmprestimo(),
            obj.getDataInicio(),
            obj.getDataFim(),
            obj.getValorEmprestimo(),
            obj.getValorPrestacao()
        ))
        .toList();
    dto.setOutrosEmprestimos(another);

    var order = entity.getPedido();

    final var allDecisions = new DecisaoEmprestimoDTO();

    var steps = List.of(
        EtapaEmprestimo.ANALISE_RH_PEDIDO.name(),
        EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name(),
        EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name(),
        EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name(),
        EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name(),
        EtapaEmprestimo.ANALISE_RH_REFORCO.name(),
        EtapaEmprestimo.ANALISE_FINANCEIRA_REFORCO.name(),
        EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_REFORCO.name()
    );

    var decisions = pedidoDecisaoEntityRepository
        .findByPedidoAndEtapaInAndEstado(order, steps, Estado.A.name()).stream()
        .collect(Collectors.toMap(
            PedidoDecisaoEntity::getEtapa,
            this::buildDecisionData
        ));

    ofNullable(decisions.get(EtapaEmprestimo.ANALISE_RH_PEDIDO.name())).ifPresent(allDecisions::setAnaliseRhPedido);
    ofNullable(decisions.get(EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name())).ifPresent(allDecisions::setAnaliseFinanceiroPedido);
    ofNullable(decisions.get(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name())).ifPresent(allDecisions::setAutorizacaoComissaoExecutivaPedido);
    ofNullable(decisions.get(EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name())).ifPresent(allDecisions::setAnaliseRhAdiantamento);
    ofNullable(decisions.get(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name())).ifPresent(allDecisions::setVerificacaoAdiantamento);
    ofNullable(decisions.get(EtapaEmprestimo.ANALISE_RH_REFORCO.name())).ifPresent(allDecisions::setAnaliseRhRenegociacao);
    ofNullable(decisions.get(EtapaEmprestimo.ANALISE_FINANCEIRA_REFORCO.name())).ifPresent(allDecisions::setAnaliseFinanceiroRenegociacao);
    ofNullable(decisions.get(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_REFORCO.name())).ifPresent(allDecisions::setAutorizacaoComissaoExecutivaRenegociacao);

    dto.setDecisao(allDecisions);

    var docCodes = List.of(
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.PEDIDO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANALISE_RH_PEDIDO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO.name()
    );

    var docs = documentService.getDocuments(funId, docCodes, entity.getUuid());
    dto.setDocumentos(docs);

    return dto;
  }

  private BaseDecisaoDTO buildDecisionData(PedidoDecisaoEntity obj) {
    var baseDecision = new BaseDecisaoDTO();
    baseDecision.setParecer(ParecerProcesso.fromCode(obj.getDecisao()).orElse(null));
    baseDecision.setObservacao(obj.getObs());
    baseDecision.setData(obj.getCreatedDate().toLocalDate());
    return baseDecision;
  }

  public EmprestimoListDTO listLoans(ListarEmprestimosQuery query) {

    var page = Integer.parseInt(query.getPage());
    var size = Integer.parseInt(query.getSize());

    var pageable = PageRequest.of(page, size, Sort.by("dataInicio").descending());

    Specification<EmprestimoEntity> specification = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();

      if (StringUtils.hasText(query.getTipoEmprestimo()))
        predicates.add(cb.equal(root.get("tipoEmprestimo"), query.getTipoEmprestimo()));

      var status = StringUtils.hasText(query.getEstado()) ? query.getEstado() : Estado.A.name();
      predicates.add(cb.equal(root.get("estado"), status));

      if (StringUtils.hasText(query.getDataInicio()) && StringUtils.hasText(query.getDataFim()))
        predicates.add(cb.between(root.get("dataInicio"), LocalDate.parse(query.getDataInicio()), LocalDate.parse(query.getDataFim()))
        );

      if (StringUtils.hasText(query.getDireccaoId())) {
        var relacionamento = root.join("tiprel");
        predicates.add(
            cb.equal(relacionamento.get("mobId").get("instidId").get("id"), Long.valueOf(query.getDireccaoId()))
        );
      }

      if (StringUtils.hasText(query.getFuncionarioId())) {
        var relacionamento = root.join("tiprel");
        predicates.add(
            cb.equal(relacionamento.get("funId").get("uuid"), UUID.fromString(query.getFuncionarioId()))
        );
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var pageResult = emprestimoEntityRepository.findAll(specification, pageable);

    var etapaMap = EtapaEmprestimo.descriptionMap();

    var response = new EmprestimoListDTO();
    PageMapper.fillPagination(pageResult, response);
    response.setContent(pageResult.getContent()
        .stream()
        .map(e -> {
          var dto = new EmprestimoListRowDTO();
          dto.setEstado(e.getEstado());
          dto.setEstadoDesc(Estado.codeDescriptionMap().get(e.getEstado()));
          dto.setTipoEmprestimo(e.getTipoEmprestimo());
          dto.setRenegociacaoDivida(e.getRenogociacao());
          dto.setValorConcedido(e.getValorEmprestimo());
          dto.setNumeroPrestacoesPagas(e.getNrPrestacao());
          dto.setTipoSituacao(e.getTipoSituacao());
          dto.setValorPago(e.getValorPago());
          dto.setDataInicioEmprestimo(e.getDataInicio());
          dto.setEmprestimoId(e.getUuid());
          dto.setSaldoEmDivida(e.getValorDivida());
          dto.setDataInicioEmprestimo(e.getDataInicio());
          var funId = e.getTiprel().getFunId();
          dto.setFuncionarioId(funId.getUuid().toString());
          dto.setNomeColaborador(funId.getNome());
          PedidoEntity order = e.getPedido();
          ofNullable(order).ifPresent(o -> {
            dto.setEtapa(o.getEtapa());
            dto.setEtapaDesc(etapaMap.getOrDefault(o.getEtapa(), o.getEtapa()));
          });
          return dto;
        })
        .toList());

    return response;
  }

  public PlanoFinanceiroDTO getPlanoFinanceiro(String uuid) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var plan = new PlanoFinanceiroDTO();
    plan.setValorEmprestimo(loan.getValorEmprestimo());
    plan.setTaxaJuroAnual(loan.getJuro());
    plan.setPeriodoEmprestimo(loan.getNrPrestacao() != null ? (loan.getNrPrestacao() / 12) : null);
    plan.setDataInicio(loan.getDataInicio());
    plan.setNumeroPagamento(loan.getNrPrestacao());
    plan.setJurosTotal(loan.getValorJuroTotal());
    plan.setCustoTotalEmprestimo(NumberUtils.sum(loan.getValorJuroTotal(), loan.getValorEmprestimo()));
    plan.setPagamentoMensal(loan.getValorPrestacao());

    if (plan.getDataInicio() == null) {
      plan.setRows(emprestimoWriteService.generateMockFinancialPlan(loan));
      return plan;
    }

    var rows = planoFinanceiroEntityRepository.findAllByEmprestimo(loan)
        .stream()
        .map(obj -> new PlanoFinanceiroRowDTO(
            obj.getNrOrdemPrestacao(),
            obj.getDataPagamento(),
            obj.getSaldoInicial(),
            NumberUtils.sum(obj.getValorPrincipal(), obj.getValorJuros()),
            obj.getValorPrincipal(),
            obj.getValorJuros(),
            obj.getSaldoFinal()
        )).toList();
    plan.setRows(rows);

    return plan;
  }

  public HistoricoPagamentoDTO getPaymentHistory(String uuid) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var history = new HistoricoPagamentoDTO();

    var rows = rhPagamentoEntityRepository.findByEstadoAndDefp_FunId(Estado.A.name(), loan.getTiprel().getFunId())
        .stream()
        .map(p -> new HistoricoPagamentoRowDTO(p.getDataRef(), p.getValor()))
        .toList();

    history.setPagamentos(rows);
    history.setValorTotalPago(loan.getValorPago());
    history.setSaldoDivida(loan.getValorDivida());

    return history;
  }
}


package cv.inps.rh.emprestimo.domain.service;

import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.application.queries.ListarEmprestimosQuery;
import cv.inps.rh.emprestimo.domain.service.constants.EtapaEmprestimo;
import cv.inps.rh.emprestimo.domain.service.constants.ReferenceName;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhPagamentoEntity;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

  public List<InformacaoEmprestimoRequestDTO> getAllConfiguracaoEmprestimo() {
    return paramEmprestimoEntityRepository.findAll()
        .stream()
        .map(entity -> new InformacaoEmprestimoRequestDTO(
            entity.getCarrPccs().getUuid().toString(),
            entity.getValorLimite(),
            entity.getNumeroLimite(),
            entity.getEstado(),
            entity.getUuid()
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

    final var decision = new DecisaoEmprestimoDTO();
    getDecision(order, EtapaEmprestimo.ANALISE_RH_PEDIDO).map(this::buildDecisionData).ifPresent(decision::setAnaliseRhPedido);
    getDecision(order, EtapaEmprestimo.ANALISE_FINANCEIRA).map(this::buildDecisionData).ifPresent(decision::setAnaliseFinanceiro);
    getDecision(order, EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA).map(this::buildDecisionData).ifPresent(decision::setAutorizacaoComissaoExecutiva);
    getDecision(order, EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO).map(this::buildDecisionData).ifPresent(decision::setAnaliseRhAdiantamento);
    getDecision(order, EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO).map(this::buildDecisionData).ifPresent(decision::setVerificacaoAdiantamento);
    dto.setDecisao(decision);

    var docCodes = List.of(
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.PEDIDO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANALISE_RH_PEDIDO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name()
    );

    var docs = documentService.getDocuments(
        funId,
        docCodes,
        entity.getUuid());
    dto.setDocumentos(docs);

    return dto;
  }

  private Optional<PedidoDecisaoEntity> getDecision(PedidoEntity order, EtapaEmprestimo etapa) {
    return pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        etapa.name(),
        Estado.A.name()
    );
  }

  private BaseDecisaoDTO buildDecisionData(PedidoDecisaoEntity obj) {
    var baseDecision = new BaseDecisaoDTO();
    baseDecision.setParecer(obj.getDecisao());
    baseDecision.setObservacao(obj.getObs());
    baseDecision.setData(obj.getCreatedDate().toLocalDate());
    return baseDecision;
  }

  public EmprestimoListDTO listarEmprestimos(ListarEmprestimosQuery query) {

    var page = Integer.parseInt(query.getPage());
    var size = Integer.parseInt(query.getSize());

    var pageable = PageRequest.of(page, size, Sort.by("dataInicio").descending());

    Specification<EmprestimoEntity> specification = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();

      if (StringUtils.hasText(query.getTipoEmprestimo()))
        predicates.add(cb.equal(root.get("tipoEmprestimo"), query.getTipoEmprestimo()));

      if (StringUtils.hasText(query.getEstado()))
        predicates.add(cb.equal(root.get("estado"), query.getEstado()));
      else
        predicates.add(cb.equal(root.get("estado"), Estado.A.name()));

      if (StringUtils.hasText(query.getEstadoEmprestimo()))
        predicates.add(cb.equal(root.get("estado"), query.getEstadoEmprestimo()));


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
          dto.setEtapa(e.getPedido().getEtapa());
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

  public HistoricoPagamentoDTO getHistoricoPagamento(String uuid) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var payments = rhPagamentoEntityRepository.findByEstadoAndDefp_FunId(Estado.A.name(), loan.getTiprel().getFunId());

    var history = new HistoricoPagamentoDTO();

    var rows = payments.stream()
        .map(p -> new HistoricoPagamentoRowDTO(p.getDataRef(), p.getValor()))
        .toList();
    history.setPagamentos(rows);

    var totalPago = payments.stream()
        .map(RhPagamentoEntity::getValor)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    history.setPagamentos(rows);
    history.setValorTotalPago(totalPago);
    history.setSaldoDivida(
        loan.getValorEmprestimo().subtract(totalPago)
    );

    return history;
  }
}


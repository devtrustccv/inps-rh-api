package cv.inps.rh.emprestimo.domain.service;

import cv.inps.rh.emprestimo.domain.service.process.EmprestimoHelper;
import cv.inps.rh.emprestimo.application.constants.ParecerProcesso;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.application.queries.ListarEmprestimosQuery;
import cv.inps.rh.emprestimo.domain.service.constants.EtapaEmprestimo;
import cv.inps.rh.emprestimo.domain.service.constants.ReferenceName;
import cv.inps.rh.emprestimo.domain.service.constants.StatusEmprestimo;
import cv.inps.rh.emprestimo.domain.service.constants.TipoPedido;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PlanoFinanceiroEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.NumberUtils;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
  private final EmprestimoOutroEntityRepository emprestimoOutroEntityRepository;
  private final EmprestimoDocumentService documentService;
  private final EmprestimoWriteService emprestimoWriteService;

  public List<InformacaoEmprestimoRequestDTO> getAllConfiguracaoEmprestimo() {
    return paramEmprestimoEntityRepository.listAll();
  }

  public DetalhesEmprestimoDTO getEmprestimoByUuid(String uuid) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var funId = entity.getTiprel().getFunId();

    var dto = new DetalhesEmprestimoDTO();
    dto.setNomeFornecedor(entity.getNomeFornecedor());
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
    dto.setValorReforco(entity.getValorReforco());
    dto.setSwift(entity.getSwift());
    dto.setMotivo(entity.getMotivo());
    dto.setNib(entity.getNib());
    dto.setNif(entity.getNif());
    dto.setEstado(entity.getEstado());
    dto.setEstadoDesc(StatusEmprestimo.codeDescriptionMap().getOrDefault(entity.getEstado(), entity.getEstado()));

    var order = entity.getPedido();
    dto.setEtapa(order.getEtapa());
    dto.setEtapaDesc(EtapaEmprestimo.descriptionMap().getOrDefault(order.getEtapa(), order.getEtapa()));

    ofNullable(entity.getBanco()).ifPresent(o -> {
      dto.setBancoId(o.getId());
      dto.setNumeroContaBanco(o.getNuConta());
    });

    dto.setTipoPedido(order.getTipoPedido());
    dto.setTipoEmprestimo(entity.getTipoEmprestimo());
    dto.setTipoMovimentoId(entity.getTmId());
    dto.setFinalidade(entity.getFinalidade());
    dto.setNrPrestacaoPaga(
        planoFinanceiroEntityRepository.findAllByEmprestimo(entity)
            .stream()
            .filter(p -> FinancialPlanHelper.FLG_PAGO.equalsIgnoreCase(p.getFlgPago()))
            .count()
    );

    var otherLoans = emprestimoOutroEntityRepository.findByReferenciaOrigemAndFunAndEstado(order, order.getFunId(), Estado.A.name())
        .stream()
        .map(obj -> new OutrosEmprestimosDTO(
            obj.getUuid(),
            obj.getTiposEmprestimo(),
            obj.getDataInicio(),
            obj.getDataFim(),
            obj.getValorEmprestimo(),
            obj.getValorPrestacao()
        ))
        .toList();
    dto.setOutrosEmprestimos(otherLoans);

    var another = emprestimoEntityRepository.findByTiprel_FunIdAndEstado(funId, StatusEmprestimo.ATIVO.name())
        .stream()
        .filter(obj -> obj.getPedido() != null && !obj.getPedido().getId().equals(order.getId()))
        .collect(Collectors.toMap(
            obj -> obj.getPedido().getId(),
            obj -> obj,
            (a, b) -> (b.getVersao() != null && a.getVersao() != null && b.getVersao() > a.getVersao()) ? b : a))
        .values()
        .stream()
        .map(obj -> new OutrosEmprestimosDTO(
            obj.getUuid(),
            obj.getTipoEmprestimo(),
            obj.getDataInicio(),
            obj.getDataFim(),
            obj.getValorEmprestimo(),
            obj.getValorPrestacao()
        ))
        .toList();
    dto.setEmprestimos(another);

    var hoje = LocalDate.now(ZoneId.systemDefault());
    var outrosAnteriores = emprestimoOutroEntityRepository.findByFunAndEstado(funId, Estado.A.name())
        .stream()
        .filter(obj -> obj.getReferenciaOrigem() == null || !obj.getReferenciaOrigem().getId().equals(order.getId()))
        .filter(obj -> obj.getDataFim() == null || !obj.getDataFim().isBefore(hoje))
        .map(obj -> new OutrosEmprestimosDTO(
            obj.getUuid(),
            obj.getTiposEmprestimo(),
            obj.getDataInicio(),
            obj.getDataFim(),
            obj.getValorEmprestimo(),
            obj.getValorPrestacao()
        ))
        .toList();
    dto.setOutrosEmprestimosAnteriores(outrosAnteriores);

    final var allDecisions = new DecisaoEmprestimoDTO();

    var pedidoExecucao = new BaseDecisaoDTO();
    pedidoExecucao.setExecutadoPor(entity.getCreatedBy());
    ofNullable(entity.getCreatedDate()).ifPresent(d -> pedidoExecucao.setData(d.toLocalDate()));
    allDecisions.setPedido(pedidoExecucao);

    var steps = List.of(
        EtapaEmprestimo.ANALISE_RH_PEDIDO.name(),
        EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name(),
        EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name(),
        EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name(),
        EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name(),
        EtapaEmprestimo.ANALISE_RH_REFORCO.name(),
        EtapaEmprestimo.ANALISE_FINANCEIRA_REFORCO.name(),
        EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_REFORCO.name(),
        EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO.name(),
        EtapaEmprestimo.ELABORAR_CONTRATO_REFORCO.name(),
        EtapaEmprestimo.PAGAMENTO.name()
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
    ofNullable(decisions.get(EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO.name())).ifPresent(allDecisions::setElaboracaoContratoPedido);
    ofNullable(decisions.get(EtapaEmprestimo.ELABORAR_CONTRATO_REFORCO.name())).ifPresent(allDecisions::setElaboracaoContratoRenegociacao);
    ofNullable(decisions.get(EtapaEmprestimo.PAGAMENTO.name())).ifPresent(allDecisions::setPagamento);

    dto.setDecisao(allDecisions);

    var docCodes = List.of(
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.PEDIDO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANALISE_RH_PEDIDO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANALISE_RH_REFORCO.name(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ELABORAR_CONTRATO_REFORCO.name(),
        // Fundo Social / Recuperação gravam os documentos com o tipo do pedido como referência —
        // sem estes não apareciam no Ver Detalhe nem na Validação.
        TipoPedido.FUNDO_SOCIAL.name(),
        TipoPedido.RECUPERACAO.name()
    );

    var docs = documentService.getDocuments(funId, docCodes, entity.getUuid());
    dto.setDocumentos(docs);

    dto.setObservacaoEstado(entity.getObservacao());
    dto.setDocumentosEstado(documentService.getDocuments(funId,
        List.of(ReferenceName.RH_T_EMPRESTIMO + "_CHANGE_STATUS"), entity.getUuid()));

    return dto;
  }

  private BaseDecisaoDTO buildDecisionData(PedidoDecisaoEntity obj) {
    var baseDecision = new BaseDecisaoDTO();
    baseDecision.setParecer(ParecerProcesso.fromCode(obj.getDecisao()).orElse(null));
    baseDecision.setObservacao(obj.getObs());
    // Data escolhida no parecer; registos antigos (sem ela) mostram a data de registo, como antes.
    baseDecision.setData(obj.getDataParecer() != null
        ? obj.getDataParecer()
        : obj.getCreatedDate().toLocalDate());
    baseDecision.setDataExecucao((obj.getLastModifiedDate() != null
        ? obj.getLastModifiedDate()
        : obj.getCreatedDate()).toLocalDate());
    // Cada gravação cria uma decisão nova, e o utilizador de alteração só é preenchido quando ela é
    // alterada depois; sem o de registo, "Executado Por" ficava vazio (ex.: após uma retificação).
    baseDecision.setExecutadoPor(obj.getLastModifiedBy() != null ? obj.getLastModifiedBy() : obj.getCreatedBy());

    var responsavel = new BaseDecisaoDTO.Responsavel(
        obj.getParecerResponsavel(),
        obj.getObservacaoResponsavel(),
        obj.getDataObservacaoResponsavel(),
        obj.getUtilizadorObservacaoResponsavel(),
        obj.getDataParecerResponsavel()
    );

    baseDecision.setResponsavel(responsavel);

    return baseDecision;
  }

  public EmprestimoListDTO listLoans(ListarEmprestimosQuery query) {

    var page = Integer.parseInt(query.getPage());
    var size = Integer.parseInt(query.getSize());
    var pageable = PageRequest.of(page, size, Sort.by("id").descending());

    var pageData = emprestimoEntityRepository.listLoans(
        StringUtils.hasText(query.getTipoEmprestimo()) ? query.getTipoEmprestimo() : null,
        StringUtils.hasText(query.getEstadoEmprestimo()) ? query.getEstadoEmprestimo() : null,
        StringUtils.hasText(query.getDataInicio()) ? LocalDate.parse(query.getDataInicio()) : null,
        StringUtils.hasText(query.getDataFim()) ? LocalDate.parse(query.getDataFim()) : null,
        StringUtils.hasText(query.getDireccaoId()) ? Long.valueOf(query.getDireccaoId()) : null,
        StringUtils.hasText(query.getFuncionarioId()) ? UUID.fromString(query.getFuncionarioId()) : null,
        pageable
    );

    var estadoMap = StatusEmprestimo.codeDescriptionMap();
    var etapaMap = EtapaEmprestimo.descriptionMap();
    var tipoEmprestimoMap = TipoPedido.descriptionMap();

    pageData.getContent().forEach(dto -> {
      dto.setEstadoDesc(estadoMap.getOrDefault(dto.getEstado(), dto.getEstado()));
      dto.setEtapaDesc(etapaMap.getOrDefault(dto.getEtapa(), dto.getEtapa()));
      dto.setTipoEmprestimoDesc(tipoEmprestimoMap.getOrDefault(dto.getTipoEmprestimo(), dto.getTipoEmprestimo()));
    });

    var response = new EmprestimoListDTO();
    PageMapper.fillPagination(pageData, response);
    response.setContent(pageData.getContent());
    return response;
  }

  public PlanoFinanceiroDTO getPlanoFinanceiro(String uuid) {

    var formatter = NumberUtils.spaceDecimalFormat();

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var plan = new PlanoFinanceiroDTO();
    plan.setValorEmprestimo(loan.getValorEmprestimo() == null ? "" : formatter.format(loan.getValorEmprestimo()));
    plan.setTaxaJuroAnual(loan.getJuro());
    plan.setPeriodoEmprestimo(loan.getNrPrestacao() != null ? (loan.getNrPrestacao() / 12) : null);
    plan.setDataInicio(loan.getDataInicio());
    plan.setNumeroPagamento(loan.getNrPrestacao());

    // Plano gravado sempre que exista (os subprocessos não têm data de início, mas gravam o plano
    // no NEXT do pedido); só sem plano gravado é que se calcula uma simulação.
    var guardados = planoFinanceiroEntityRepository.findAllByEmprestimo(loan);
    List<PlanoFinanceiroRowDTO> rows;
    if (guardados.isEmpty()) {
      rows = emprestimoWriteService.generateFinancialPlan(loan, LocalDate.now(ZoneId.systemDefault()));
    } else {
      rows = guardados
          .stream()
          .sorted(java.util.Comparator.comparing(PlanoFinanceiroEntity::getNrOrdemPrestacao))
          .map(obj -> new PlanoFinanceiroRowDTO(
              obj.getNrOrdemPrestacao(),
              obj.getEstado(),
              obj.getFlgPago(),
              obj.getDataPagamento(),
              obj.getSaldoInicial(),
              NumberUtils.sum(obj.getValorPrincipal(), obj.getValorJuros()),
              obj.getValorPrincipal(),
              obj.getValorJuros(),
              obj.getSaldoFinal()
          )).toList();
    }
    plan.setRows(rows);

    // Juros Total / Pagamento Mensal: a entidade só os tem depois do
    // contrato (VALOR_JURO_TOTAL, VALOR_PRESTACAO); até lá derivam-se do
    // próprio plano. Juros = total a pagar - capital (vale também para o
    // Fundo Social, cujas linhas trazem juros = null com o juro embutido na
    // prestação).
    var jurosTotal = loan.getValorJuroTotal();
    if (jurosTotal == null && !rows.isEmpty() && loan.getValorEmprestimo() != null) {
      var totalPago = rows.stream()
          .map(PlanoFinanceiroRowDTO::pagamento)
          .filter(Objects::nonNull)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      jurosTotal = totalPago.subtract(loan.getValorEmprestimo()).max(BigDecimal.ZERO);
    }
    var pagamentoMensal = loan.getValorPrestacao() != null
        ? loan.getValorPrestacao()
        : rows.stream().map(PlanoFinanceiroRowDTO::pagamento).filter(Objects::nonNull).findFirst().orElse(null);

    plan.setJurosTotal(jurosTotal);
    plan.setCustoTotalEmprestimo(formatter.format(NumberUtils.sum(jurosTotal, loan.getValorEmprestimo())));
    plan.setPagamentoMensal(pagamentoMensal == null ? "" : formatter.format(pagamentoMensal));

    return plan;
  }

  private static final Map<String, String> TIPO_SITUACAO_DESC = Map.ofEntries(
      Map.entry("EMPRESTIMO", "Empréstimo"),
      Map.entry("FUNDO_SOCIAL", "Fundo Social"),
      Map.entry("RECUPERACAO", "Recuperação"),
      Map.entry("AQUISICAO_VIATURA", "Aquisição Viatura"),
      Map.entry("ADIANTAMENTO_PAGAMENTO_ANTECIPADO", "Amortização — pagamento antecipado"),
      Map.entry("ADIANTAMENTO_DIMINUICAO_PRESTACAO", "Amortização — diminuição da prestação"),
      Map.entry("ADIANTAMENTO_PAGAMENTO_ANTECIPADO_DIMINUICAO_PRESTACAO", "Amortização — pagamento antecipado e diminuição da prestação"),
      Map.entry("REFORCO_AUMENTO_VALOR", "Renegociação — aumento do valor"),
      Map.entry("REFORCO_AUMENTO_PRESTACAO", "Renegociação — aumento da prestação"),
      Map.entry("REFORCO_AUMENTO_VALOR_AUMENTO_PRESTACAO", "Renegociação — aumento do valor e da prestação"),
      Map.entry("AUMENTO_PRESTACAO", "Renegociação — aumento da prestação"),
      Map.entry("REDUCAO_PRESTACAO", "Renegociação — redução da prestação"),
      Map.entry("REFORCO_CAPITAL", "Renegociação — reforço de capital")
  );

  /**
   * Versões do empréstimo (inicial + subprocessos) para a lista expansível: todas as entradas do mesmo
   * pedido, da mais antiga para a mais recente; {@code atual} marca a mais recente não cancelada.
   */
  public List<EmprestimoVersaoDTO> getVersoes(String uuid) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    var versoes = emprestimoEntityRepository.findAllByPedido_IdOrderByVersaoAscIdAsc(loan.getPedido().getId());
    // "Atual" = a versão mais recente não cancelada (a que está em curso, ou a em vigor)
    var idAtual = versoes.stream()
        .filter(v -> !EmprestimoHelper.isSubprocessoTerminado(v.getEstado()))
        .map(EmprestimoEntity::getId)
        .max(Long::compareTo)
        .orElse(null);
    var estadoMap = StatusEmprestimo.codeDescriptionMap();

    return versoes.stream()
        .map(v -> new EmprestimoVersaoDTO(
            v.getUuid(),
            v.getVersao(),
            v.getTipoSituacao(),
            TIPO_SITUACAO_DESC.getOrDefault(v.getTipoSituacao(), v.getTipoSituacao()),
            v.getEstado(),
            estadoMap.getOrDefault(v.getEstado(), v.getEstado()),
            v.getValorEmprestimo(),
            v.getValorReforco(),
            v.getValorAdiantado(),
            v.getNrPrestacao(),
            planoFinanceiroEntityRepository.findAllByEmprestimo(v).stream()
                .filter(p -> "A".equals(p.getEstado()) && FinancialPlanHelper.FLG_PAGO.equalsIgnoreCase(p.getFlgPago()))
                .count(),
            v.getValorPrestacao(),
            v.getDataInicio(),
            v.getCreatedDate() != null ? v.getCreatedDate().toLocalDate() : null,
            v.getId().equals(idAtual)
        ))
        .toList();
  }

  public HistoricoPagamentoDTO getPaymentHistory(String uuid) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var usDecimalFormatter = NumberUtils.spaceDecimalFormat();

    var rows = rhPagamentoEntityRepository.findByEstadoAndDefp_FunId(
            Estado.A.name(),
            loan.getTiprel().getFunId()
        )
        .stream()
        .map(p -> new HistoricoPagamentoRowDTO(
            p.getDataRef(),
            formatOrZero(usDecimalFormatter, p.getValor())
        ))
        .toList();

    var history = new HistoricoPagamentoDTO();
    history.setPagamentos(rows);
    history.setValorTotalPago(formatOrZero(usDecimalFormatter, loan.getValorPago()));
    history.setSaldoDivida(formatOrZero(usDecimalFormatter, loan.getValorDivida()));
    return history;
  }

  // DecimalFormat.format(null) lança "Cannot format given Object as a Number"
  // — empréstimos ainda sem pagamentos têm VALOR_PAGO a null.
  private static String formatOrZero(DecimalFormat formatter, BigDecimal value) {
    return formatter.format(value == null ? BigDecimal.ZERO : value);
  }
}


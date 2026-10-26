package cv.inps.rh.emprestimo.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.commands.SaveConfiguracaoInfoEmprestimoCommand;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;
import cv.inps.rh.emprestimo.application.dto.FundoSocialRequestDTO;
import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroRowDTO;
import cv.inps.rh.emprestimo.application.dto.ValidarEmprestimoRequestDTO;
import cv.inps.rh.emprestimo.domain.service.constants.EtapaEmprestimo;
import cv.inps.rh.emprestimo.domain.service.constants.ReferenceName;
import cv.inps.rh.emprestimo.domain.service.constants.StatusEmprestimo;
import cv.inps.rh.emprestimo.domain.service.constants.TipoPedido;
import cv.inps.rh.emprestimo.domain.service.process.EmprestimoHelper;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
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
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final EmprestimoDocumentService documentService;
  private final EmprestimoHelper emprestimoHelper;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;

  public void saveConfiguracaoEmprestimo(SaveConfiguracaoInfoEmprestimoCommand command) {

    var entities = new ArrayList<ParamEmprestimoEntity>();

    for (var row : command.getInformacaoemprestimorequest()) {

      final ParamEmprestimoEntity entity;

      if (StringUtils.hasText(row.getId()))
        entity = paramEmprestimoEntityRepository.findByUuidOrThrow(row.getId());
      else {
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

  public void saveOutroEmprestimo(TipoPedido tipoPedido, TipoPedido tipoEmprestimo, List<FundoSocialRequestDTO> requests) {

    for (var request : requests) {

      var currentRelation = funcionarioRules.getTipoRelacionamentoAtual(UUID.fromString(request.getFuncionarioId()));

      var entity = new EmprestimoEntity();
      entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      entity.setEstado(StatusEmprestimo.POR_SUBMETER.name());
      entity.setTipoEmprestimo(tipoEmprestimo.name());
      entity.setTipoSituacao(tipoEmprestimo.name());
      // RH_T_EMPRESTIMO.NIF é NOT NULL na BD (pensado para o fornecedor da
      // viatura em Aquisição Viatura) mas não se aplica a Fundo
      // Social/Recuperação — sem isto o INSERT falha com ORA-01400. String
      // vazia não serve: o Oracle trata VARCHAR2 '' como NULL.
      entity.setNif("N/A");
      entity.setVersao(1L);
      entity.setTiprel(currentRelation);

      var funId = currentRelation.getFunId();

      var order = new PedidoEntity();
      order.setFunId(funId);
      order.setUuid(UuidCreator.getTimeOrderedEpoch());
      order.setTipoPedido(tipoPedido.name());
      order.setOrigem("RH");
      order.setEtapa(EtapaEmprestimo.PEDIDO.name());
      order.setEstado(StatusEmprestimo.POR_SUBMETER.name());
      order = pedidoEntityRepository.save(order);
      entity.setPedido(order);

      aplicarDadosOutroEmprestimo(entity, tipoPedido, request);

      var validacao = new ValidacaoEntity();
      validacao.setUuid(UUID.randomUUID());
      validacao.setTipoAccao(TipoAcao.INSERT.name());
      validacao.setReferenciaId(entity.getId());
      validacao.setReferenciaName(Referencia.EMPRESTIMO.name());
      validacao.setReferenciaUuid(UUID.fromString(entity.getUuid()));
      validacao.setTiprelId(null);
      validacao.setFunId(funId);
      validacao.setEstado(Estado.P);
      validacaoEntityRepository.save(validacao);
    }
  }

  /**
   * Dados do formulário de Fundo Social / Recuperação → empréstimo, documentos e plano (pendente).
   * Usado no registo, na correção (reenvio) e na validação, onde o validador pode corrigir os dados.
   * O colaborador e o tipo de pedido não mudam depois do registo.
   */
  private void aplicarDadosOutroEmprestimo(EmprestimoEntity entity, TipoPedido tipoPedido, FundoSocialRequestDTO request) {

    entity.setTmId(request.getTipoMovimentoId());
    entity.setDataInicio(request.getDataInicio());
    entity.setDataFim(request.getDataFim());
    entity.setValorPrestacao(request.getValorPrestacaoMensal());
    entity.setValorEmprestimo(request.getValorTotalEmprestimo());
    entity.setValorDivida(request.getValorTotalEmprestimo());
    entity.setFinalidade(request.getFinalidade());
    entity.setNrPrestacao(
        request.getNrPrestacao() != null
            ? request.getNrPrestacao()
            : DateFormatter.monthsBetween(request.getDataInicio(), request.getDataFim())
    );
    entity.setJuro(request.getJuro());
    entity = emprestimoEntityRepository.save(entity);

    documentService.saveDocuments(
        request.getDocumentos(),
        entity.getTiprel().getFunId(),
        entity.getUuid(),
        tipoPedido.name()
    );

    // Corrigido na validação/reenvio: o plano (ainda pendente) é gerado de novo com os dados atuais.
    planoFinanceiroEntityRepository.apagarPlanos(entity.getId());
    if (tipoPedido == TipoPedido.RECUPERACAO) {
      generateSaveFinancialPlanForRecuperacao(entity, request);
    } else if (entity.getJuro() != null && entity.getNrPrestacao() != null) {
      generateSaveFinancialPlanForFundoSocial(entity, request.getNrPrestacaoPaga());
    }
  }

  /**
   * Desconto do Fundo Social / Recuperação no processamento (RH_T_DEF_PAGAMENTO), criado só quando o
   * empréstimo é validado — antes era criado no registo, ainda por validar (e ficava ativo mesmo
   * que a validação o recusasse).
   */
  private void criarDefPagamento(EmprestimoEntity entity) {
    if (entity.getTmId() == null)
      return;
    var defPagamentoEntity = new DefPagamentoEntity();
    defPagamentoEntity.setTmId(tipoMovimentoEntityRepository.getReferenceById(entity.getTmId()));
    defPagamentoEntity.setValor(entity.getValorPrestacao());
    defPagamentoEntity.setDataInicio(entity.getDataInicio());
    defPagamentoEntity.setDataFim(entity.getDataFim());
    defPagamentoEntity.setEstado(Estado.A);
    defPagamentoEntity.setUuid(UuidCreator.getTimeOrderedEpoch());
    defPagamentoEntity.setFunId(entity.getTiprel().getFunId());
    var savedDefPag = defPagamentoEntityRepository.save(defPagamentoEntity);

    var tipoRel = new TipoRelRemPagEntity();
    tipoRel.setTiprelId(entity.getTiprel());
    tipoRel.setPagId(savedDefPag);
    tipoRelRemPagEntityRepository.save(tipoRel);
  }

  public List<PlanoFinanceiroRowDTO> generateSaveFinancialPlan(String uuid) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    if (entity.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("Para gerar o plano financeiro deve ter uma data de início de empréstimo");

    return generateSaveFinancialPlan(entity);
  }

  public List<PlanoFinanceiroRowDTO> generateSaveFinancialPlan(EmprestimoEntity entity) {

    var plan = generateFinancialPlan(
        entity,
        entity.getDataInicio() != null ? entity.getDataInicio() : LocalDate.now(ZoneId.systemDefault())
    );

    // Pode correr mais de uma vez (Gravar e Seguinte do Contrato): substitui o plano anterior em
    // vez de acrescentar outro — antes ficava duplicado (2 linhas por prestação, ambas ativas).
    planoFinanceiroEntityRepository.apagarPlanos(entity.getId());
    emprestimoHelper.savePlans(entity, plan);

    // Prestação mensal e juros totais do plano no próprio empréstimo (lista, Pagamento, resumo).
    if (!plan.isEmpty()) {
      entity.setValorPrestacao(plan.getFirst().pagamento());
      entity.setValorJuroTotal(plan.stream()
          .map(PlanoFinanceiroRowDTO::juros)
          .filter(java.util.Objects::nonNull)
          .reduce(BigDecimal.ZERO, BigDecimal::add));
      // A Viatura não recebe Data Fim no pedido (só Fundo Social/Recuperação): é a data da
      // última prestação — o Pagamento/Fim de Processo mostrava "-".
      entity.setDataFim(plan.getLast().dataPagamento());
      emprestimoEntityRepository.save(entity);
    }

    return plan;
  }

  public List<PlanoFinanceiroRowDTO> generateFinancialPlan(EmprestimoEntity entity, LocalDate startDate) {
    // Antes da Análise RH (ou sem taxa de juro definida) não há plano a simular: antes dava
    // NullPointerException e o "Ver Plano Financeiro" ficava vazio com erro 500.
    if (entity.getValorEmprestimo() == null || entity.getJuro() == null
        || entity.getNrPrestacao() == null || entity.getNrPrestacao() <= 0) {
      return List.of();
    }
    return FinancialPlanHelper.generateFinancialPlan(
        entity.getValorEmprestimo(),
        entity.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
        entity.getNrPrestacao().intValue(),
        startDate
    );
  }

  public void generateSaveFinancialPlanForFundoSocial(EmprestimoEntity entity, Long nrPrestacaoPaga) {

    var plan = FinancialPlanHelper.generateFinancialPlanForSocialFund(
        entity.getValorEmprestimo(),
        entity.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
        entity.getNrPrestacao().intValue(),
        entity.getDataInicio() != null ? entity.getDataInicio() : LocalDate.now(ZoneId.systemDefault())
    );

    // "Total Prestações Pagas" do registo: as primeiras N prestações ficam pagas (FLG_PAGO/VALOR_PAGO).
    var pagas = nrPrestacaoPaga != null ? nrPrestacaoPaga : 0L;
    plan = plan.stream()
        .map(row -> row.numero() <= pagas
            ? new PlanoFinanceiroRowDTO(row.numero(), row.estado(), FinancialPlanHelper.FLG_PAGO, row.dataPagamento(),
                row.saldoInicial(), row.pagamento(), row.principal(), row.juros(), row.saldoFinal())
            : row)
        .toList();

    emprestimoHelper.savePlans(entity, plan);

    // Prestação arredondada às unidades, como no plano (e na Aquisição de Viatura) — antes ficava a
    // do ecrã com cêntimos (ex.: 22138,98 em vez de 22139).
    if (!plan.isEmpty()) {
      entity.setValorPrestacao(plan.get(0).pagamento());
      emprestimoEntityRepository.save(entity);
    }
  }

  public void generateSaveFinancialPlanForRecuperacao(EmprestimoEntity entity, FundoSocialRequestDTO request) {

    var plan = FinancialPlanHelper.generateFinancialPlanForRecuperacao(request);

    emprestimoHelper.savePlans(entity, plan);
  }

  /**
   * Pagamento confirmado: Cabimentado (contrato feito, à espera do pagamento) → Ativo (pago, em
   * reembolso). Hoje é manual; o job de confirmação de pagamento deve chamar este mesmo método.
   * Regista em RH_T_PEDIDO_DECISAO (etapa PAGAMENTO) quem confirmou, a data do pagamento e a
   * observação, e guarda o comprovativo.
   */
  public void confirmarPagamento(String uuid, cv.inps.rh.emprestimo.application.dto.ConfirmarPagamentoData data) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    if (!StatusEmprestimo.CABIMENTADO.name().equals(entity.getEstado())
        && !StatusEmprestimo.AGUARDA_PAGAMENTO.name().equals(entity.getEstado())) {
      throw IgrpResponseStatusException.badRequest(
          "Só é possível confirmar o pagamento de um empréstimo Cabimentado (à espera de pagamento).");
    }

    entity.setEstado(StatusEmprestimo.ATIVO.name());
    emprestimoEntityRepository.save(entity);

    emprestimoHelper.registarExecucaoEtapa(
        entity.getPedido(), EtapaEmprestimo.PAGAMENTO, "PAGO", data.dataPagamento(), data.observacao());

    if (!CollectionUtils.isEmpty(data.files())) {
      documentService.addDocuments(
          data.files(),
          entity.getTiprel().getFunId(),
          entity.getUuid(),
          ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.PAGAMENTO.name()
      );
    }
  }

  public void mudarEstadoEmprestimo(String uuid, String estado, String observacao, List<DocumentoDTO> files) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    entity.setEstado(estado);
    entity.setObservacao(observacao);
    emprestimoEntityRepository.save(entity);

    if (entity.getEmprestimo() != null
        && (StatusEmprestimo.CANCELADO.name().equals(estado) || StatusEmprestimo.DESISTENCIA.name().equals(estado))) {
      emprestimoHelper.reporEmprestimoAnterior(entity);
    }

    if (CollectionUtils.isEmpty(files))
      return;

    documentService.addDocuments(
        files,
        entity.getTiprel().getFunId(),
        entity.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_CHANGE_STATUS"
    );
  }

  /**
   * Validação de Fundo Social / Recuperação, com o ciclo das outras validações ("edita-no-validar",
   * mesmo endpoint):
   *   CORRIGIR       validador devolve: validação P -> C, empréstimo Em Correção (dados não aplicados);
   *   validar vazio  quem registou corrige e reenvia: aplica os dados e reabre a validação C -> P;
   *   SIM / NAO      só sobre um empréstimo por validar; aplica as correções do validador e decide.
   */
  public void validarEmprestimo(String uuid, ValidarEmprestimoRequestDTO request) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    // Desde saveOutroEmprestimo(tipoPedido, tipoEmprestimo), uma Recuperação
    // grava o tipo real do empréstimo (AQUISICAO_VIATURA | FUNDO_SOCIAL) em
    // TIPO_EMPRESTIMO — o que a distingue é o tipo do pedido.
    var tipoPedidoCodigo = entity.getPedido() != null ? entity.getPedido().getTipoPedido() : null;
    if (!TipoPedido.FUNDO_SOCIAL.name().equals(tipoPedidoCodigo)
        && !TipoPedido.RECUPERACAO.name().equals(tipoPedidoCodigo))
      throw IgrpResponseStatusException.badRequest("Validação só se aplica a Fundo Social ou Recuperação");
    var tipoPedido = TipoPedido.valueOf(tipoPedidoCodigo);
    var referenciaUuid = UUID.fromString(entity.getUuid());

    var validar = StringUtils.hasText(request.getValidar()) ? request.getValidar().toUpperCase() : null;
    var emCorrecao = StatusEmprestimo.EM_CORRECAO.name().equals(entity.getEstado());

    if ("CORRIGIR".equals(validar)) {
      if (!StatusEmprestimo.POR_SUBMETER.name().equals(entity.getEstado()))
        throw IgrpResponseStatusException.badRequest("Só é possível devolver para correção um empréstimo por validar.");
      var validacao = funcionarioRules.devolverParaCorrecao(referenciaUuid, Estado.P, Referencia.EMPRESTIMO);
      validacao.setObs(request.getObservacao());
      validacaoEntityRepository.save(validacao);
      entity.setEstado(StatusEmprestimo.EM_CORRECAO.name());
      emprestimoEntityRepository.save(entity);
      return;
    }

    if (validar == null) {
      if (!emCorrecao)
        throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
      if (request.getDados() == null)
        throw IgrpResponseStatusException.badRequest("Os dados corrigidos são obrigatórios no reenvio.");
      aplicarCorrecoes(entity, tipoPedido, request);
      validacaoEntityRepository.save(funcionarioRules.reabrirParaValidacao(referenciaUuid, Referencia.EMPRESTIMO));
      entity.setEstado(StatusEmprestimo.POR_SUBMETER.name());
      emprestimoEntityRepository.save(entity);
      return;
    }

    if (emCorrecao)
      throw IgrpResponseStatusException.badRequest("Empréstimo em correção: não pode ser validado. Corrija e reenvie primeiro.");
    if (!StatusEmprestimo.POR_SUBMETER.name().equals(entity.getEstado()))
      throw IgrpResponseStatusException.badRequest("Só é possível validar empréstimos em estado Por Submeter");

    // Dominio VALIDAR_REGISTO (validacao-with-os-form.tsx): SIM | NAO | CORRIGIR.
    final Estado validacaoEstado;
    switch (validar) {
      case "SIM" -> {
        if (request.getDados() != null)
          aplicarCorrecoes(entity, tipoPedido, request);
        entity.setEstado(StatusEmprestimo.ATIVO.name());
        planoFinanceiroEntityRepository.ativarPlanosPendentes(entity.getId());
        criarDefPagamento(entity);
        validacaoEstado = Estado.A;
      }
      case "NAO" -> {
        entity.setEstado(StatusEmprestimo.CANCELADO.name());
        validacaoEstado = Estado.I;
      }
      default ->
          throw IgrpResponseStatusException.badRequest("Valor de validação inválido: %s".formatted(request.getValidar()));
    }
    emprestimoEntityRepository.save(entity);

    var pendingValidacao = validacaoEntityRepository.findByReferenciaUuidAndEstadoAndTipoAccaoAndReferenciaName(
        referenciaUuid, Estado.P, TipoAcao.INSERT.name(), Referencia.EMPRESTIMO.name());

    pendingValidacao.ifPresentOrElse(
        v -> {
          v.setEstado(validacaoEstado);
          v.setObs(request.getObservacao());
          validacaoEntityRepository.save(v);
        },
        () -> {
          var newValidacao = new ValidacaoEntity();
          newValidacao.setUuid(UUID.randomUUID());
          newValidacao.setTipoAccao(TipoAcao.INSERT.name());
          newValidacao.setReferenciaName(Referencia.EMPRESTIMO.name());
          newValidacao.setReferenciaId(entity.getId());
          newValidacao.setReferenciaUuid(referenciaUuid);
          newValidacao.setFunId(entity.getTiprel().getFunId());
          newValidacao.setTiprelId(null);
          newValidacao.setEstado(validacaoEstado);
          newValidacao.setObs(request.getObservacao());
          validacaoEntityRepository.save(newValidacao);
        }
    );
  }

  /** Correções do formulário (validador ou reenvio); na Recuperação o tipo de empréstimo também. */
  private void aplicarCorrecoes(EmprestimoEntity entity, TipoPedido tipoPedido, ValidarEmprestimoRequestDTO request) {
    if (tipoPedido == TipoPedido.RECUPERACAO && StringUtils.hasText(request.getTipoEmprestimo())) {
      entity.setTipoEmprestimo(request.getTipoEmprestimo());
      entity.setTipoSituacao(request.getTipoEmprestimo());
    }
    aplicarDadosOutroEmprestimo(entity, tipoPedido, request.getDados());
  }
}


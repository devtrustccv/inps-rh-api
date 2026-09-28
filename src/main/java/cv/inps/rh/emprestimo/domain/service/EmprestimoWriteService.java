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

    var isRecuperacao = tipoPedido == TipoPedido.RECUPERACAO;

    for (var request : requests) {

      var currentRelation = funcionarioRules.getTipoRelacionamentoAtual(UUID.fromString(request.getFuncionarioId()));

      var entity = new EmprestimoEntity();
      entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      entity.setTmId(request.getTipoMovimentoId());
      entity.setDataInicio(request.getDataInicio());
      entity.setDataFim(request.getDataFim());
      entity.setEstado(StatusEmprestimo.POR_SUBMETER.name());
      entity.setValorPrestacao(request.getValorPrestacaoMensal());
      entity.setValorEmprestimo(request.getValorTotalEmprestimo());
      entity.setValorDivida(request.getValorTotalEmprestimo());
      entity.setFinalidade(request.getFinalidade());
      entity.setTipoEmprestimo(tipoEmprestimo.name());
      entity.setTipoSituacao(tipoEmprestimo.name());
      // RH_T_EMPRESTIMO.NIF é NOT NULL na BD (pensado para o fornecedor da
      // viatura em Aquisição Viatura) mas não se aplica a Fundo
      // Social/Recuperação — sem isto o INSERT falha com ORA-01400. String
      // vazia não serve: o Oracle trata VARCHAR2 '' como NULL.
      entity.setNif("N/A");
      entity.setVersao(1L);
      entity.setTiprel(currentRelation);
      entity.setNrPrestacao(
          request.getNrPrestacao() != null
              ? request.getNrPrestacao()
              : DateFormatter.monthsBetween(request.getDataInicio(), request.getDataFim())
      );
      entity.setJuro(request.getJuro());

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
      entity = emprestimoEntityRepository.save(entity);

      documentService.saveDocuments(
          request.getDocumentos(),
          funId,
          entity.getUuid(),
          tipoPedido.name()
      );

      if (isRecuperacao) {
        generateSaveFinancialPlanForRecuperacao(entity, request);
      } else {
        if (entity.getJuro() != null && entity.getNrPrestacao() != null) {
          generateSaveFinancialPlanForFundoSocial(entity);
        }
      }

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

      TipoMovimentoEntity tipoMovimento = request.getTipoMovimentoId() != null ? tipoMovimentoEntityRepository.getReferenceById(request.getTipoMovimentoId()) : null;
      if (tipoMovimento != null) {
        var defPagamentoEntity = new DefPagamentoEntity();
        defPagamentoEntity.setTmId(tipoMovimento);
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

    emprestimoHelper.savePlans(entity, plan);

    return plan;
  }

  public List<PlanoFinanceiroRowDTO> generateFinancialPlan(EmprestimoEntity entity, LocalDate startDate) {
    return FinancialPlanHelper.generateFinancialPlan(
        entity.getValorEmprestimo(),
        entity.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
        entity.getNrPrestacao().intValue(),
        startDate
    );
  }

  public void generateSaveFinancialPlanForFundoSocial(EmprestimoEntity entity) {

    var plan = FinancialPlanHelper.generateFinancialPlanForSocialFund(
        entity.getValorEmprestimo(),
        entity.getJuro().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP),
        entity.getNrPrestacao().intValue(),
        entity.getDataInicio() != null ? entity.getDataInicio() : LocalDate.now(ZoneId.systemDefault())
    );

    emprestimoHelper.savePlans(entity, plan);
  }

  public void generateSaveFinancialPlanForRecuperacao(EmprestimoEntity entity, FundoSocialRequestDTO request) {

    var plan = FinancialPlanHelper.generateFinancialPlanForRecuperacao(request);

    emprestimoHelper.savePlans(entity, plan);
  }

  public void mudarEstadoEmprestimo(String uuid, String estado, String observacao, List<DocumentoDTO> files) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    entity.setEstado(estado);
    entity.setObservacao(observacao);
    emprestimoEntityRepository.save(entity);

    if (CollectionUtils.isEmpty(files))
      return;

    documentService.saveDocuments(
        files,
        entity.getTiprel().getFunId(),
        entity.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_CHANGE_STATUS"
    );
  }

  // Fundo Social / Recuperação: decide o pedido criado em saveFundoSocial
  // (que fica sempre em POR_SUBMETER com o plano financeiro pendente 'P').
  public void validarEmprestimo(String uuid, ValidarEmprestimoRequestDTO request) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    if (!StatusEmprestimo.POR_SUBMETER.name().equals(entity.getEstado()))
      throw IgrpResponseStatusException.badRequest("Só é possível validar empréstimos em estado Por Submeter");

    if (!TipoPedido.FUNDO_SOCIAL.name().equals(entity.getTipoEmprestimo())
        && !TipoPedido.RECUPERACAO.name().equals(entity.getTipoEmprestimo()))
      throw IgrpResponseStatusException.badRequest("Validação só se aplica a Fundo Social ou Recuperação");

    // Dominio VALIDAR_REGISTO (já existente e usado noutros módulos, ex.
    // validacao-with-os-form.tsx): SIM | NAO | CORRIGIR.
    final Estado validacaoEstado;
    switch (request.getValidar().toUpperCase()) {
      case "SIM" -> {
        entity.setEstado(StatusEmprestimo.ATIVO.name());
        planoFinanceiroEntityRepository.ativarPlanosPendentes(entity.getId());
        validacaoEstado = Estado.A;
      }
      case "CORRIGIR" -> {
        entity.setEstado(StatusEmprestimo.EM_CORRECAO.name());
        validacaoEstado = Estado.I;
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
        UUID.fromString(entity.getUuid()), Estado.P, TipoAcao.INSERT.name(), Referencia.EMPRESTIMO.name());

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
          newValidacao.setReferenciaUuid(UUID.fromString(entity.getUuid()));
          newValidacao.setFunId(entity.getTiprel().getFunId());
          newValidacao.setTiprelId(null);
          newValidacao.setEstado(validacaoEstado);
          newValidacao.setObs(request.getObservacao());
          validacaoEntityRepository.save(newValidacao);
        }
    );
  }
}


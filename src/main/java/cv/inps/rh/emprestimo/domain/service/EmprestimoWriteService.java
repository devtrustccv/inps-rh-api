package cv.inps.rh.emprestimo.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.commands.SaveConfiguracaoInfoEmprestimoCommand;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.domain.service.constants.TipoPedido;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class EmprestimoWriteService {

  private final ParamEmprestimoEntityRepository paramEmprestimoEntityRepository;
  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final ParamCarreiraEntityRepository paramCarreiraEntityRepository;
  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final PedidoEntityRepository pedidoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final TipoDocumentoEntityRepository tipoDocumentoEntityRepository;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;

  public void saveConfiguracaoEmprestimo(SaveConfiguracaoInfoEmprestimoCommand command) {

    var entities = new ArrayList<ParamEmprestimoEntity>();

    for (var row : command.getInformacaoemprestimorequest()) {

      ParamEmprestimoEntity entity;

      if (StringUtils.hasText(row.getId())) {
        entity = paramEmprestimoEntityRepository.findByUuidOrThrow(row.getId());
      } else {
        entity = new ParamEmprestimoEntity();
        entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      }

      entity.setCarrPccs(paramCarreiraEntityRepository.findByUuidOrThrow(UUID.fromString(row.getCarreiraId())));
      entity.setValorLimite(row.getValorLimiteEmprestimo());
      entity.setNumeroLimite(row.getNumeroLimitePrestacaoMeses());
      entity.setEstado(row.getEstado());
      entities.add(entity);
    }

    paramEmprestimoEntityRepository.saveAll(entities);
  }

  public IdDTO saveUpdatePedidoEmprestimo(String uuid, PedidoEmprestimoDTO request) {

    var currentRelation = funcionarioRules.getTipoRelacionamentoAtual(UUID.fromString(request.getFuncionarioId()));

    final EmprestimoEntity entity;

    if (StringUtils.hasText(uuid))
      entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    else {
      entity = new EmprestimoEntity();
      entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      entity.setEstado(Estado.A.name());
      entity.setTipoEmprestimo(TipoPedido.AQUISICAO_VIATURA.name());
      entity.setFinalidade(TipoPedido.AQUISICAO_VIATURA.name());
      entity.setVersao(1L);
    }

    entity.setTiprel(currentRelation);
    entity.setMarca(request.getMarca());
    entity.setAnoFabrico(request.getAnoFabrico());
    entity.setCilincrada(request.getCilindrada());
    entity.setTipoViatura(request.getTipoviatura());
    entity.setCombustivel(request.getCombustivel());
    entity.setEstadoViatura(request.getEstadoViatura());
    entity.setValorEmprestimo(request.getValorEmprestimo());
    entity.setNrPrestacao(request.getNumeroPrestacoes());

    var funId = currentRelation.getFunId();

    var orderOP = pedidoEntityRepository.findByFunIdAndTipoPedidoAndEstado(
        funId,
        TipoPedido.AQUISICAO_VIATURA.name(),
        Estado.A.name()
    );
    if (orderOP.isEmpty()) {
      var order = new PedidoEntity();
      order.setFunId(funId);
      order.setUuid(UuidCreator.getTimeOrderedEpoch());
      order.setTipoPedido(TipoPedido.AQUISICAO_VIATURA.name());
      order.setOrigem("RH");
      order.setEtapa(EtapaEmprestimo.PEDIDO.name());
      order.setEstado(Estado.A.name());
      var savedOrder = pedidoEntityRepository.save(order);
      entity.setPedido(savedOrder);
    }

    var response = new IdDTO(emprestimoEntityRepository.save(entity).getUuid());

    saveDocuments(request.getDocumentos(), funId, response.getId());

    return response;
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
      entity.setNrPrestacao(15L); // TODO 02/02/2026 21:52 validate this

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

      var uuid = emprestimoEntityRepository.save(entity).getUuid();

      saveDocuments(request.getDocumentos(), funId, uuid);

      // TODO 02/02/2026 22:06 more saves
    }
  }

  public void saveUpdateDecisaoAnaliseRh(String uuid, AnaliseRhRequestDTO request) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    entity.setNrPrestacao(request.getNumeroPrestacao());
    entity.setValorEmprestimo(request.getValorEmprestimo());
    entity.setJuro(request.getJuros());
    emprestimoEntityRepository.save(entity);

    var funId = entity.getTiprel().getFunId();

    var order = pedidoEntityRepository.findByFunIdAndTipoPedidoAndEstado(funId, "EMPRESTIMO", Estado.A.name()).orElseThrow();
    order.setEtapa(EtapaEmprestimo.ANALISE_RH.name());
    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_RH.name(),
        Estado.A.name()
    );

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer());
          obj.setObs(request.getObservacao());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer());
          newObj.setObs(request.getObservacao());
          newObj.setEtapa(EtapaEmprestimo.ANALISE_RH.name());
          newObj.setReferencia("EMPRESTIMO");
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    saveDocuments(request.getDocumentos(), funId, entity.getUuid());
  }

  public void saveUpdateDecisaoAnaliseFinanceira(String uuid, AnaliseFinanceiroRequestDTO request) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    entity.setDescCabimentacaoOrcamental(request.getCabimentacaoOrcamental());
    entity.setDescTaxaEsforco(request.getAvaliacaoTaxaEsforco());
    emprestimoEntityRepository.save(entity);

    var funId = entity.getTiprel().getFunId();

    var order = pedidoEntityRepository.findByFunIdAndTipoPedidoAndEstado(funId, "EMPRESTIMO", Estado.A.name()).orElseThrow();
    order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA.name());
    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_FINANCEIRA.name(),
        Estado.A.name()
    );

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer());
          obj.setObs(request.getObservacao());
          obj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer());
          newObj.setObs(request.getObservacao());
          newObj.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA.name());
          newObj.setReferencia("EMPRESTIMO");
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          newObj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(newObj);
        });
  }

  public void autorizarComissaoExecutiva(String uuid, AutorizacaoComissaoExecutivaDTO request) {

    var funId = emprestimoEntityRepository.findByUuidOrThrow(uuid).getTiprel().getFunId();

    var order = pedidoEntityRepository.findByFunIdAndTipoPedidoAndEstado(funId, "EMPRESTIMO", Estado.A.name()).orElseThrow();
    order.setEtapa(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA.name());
    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA.name(),
        Estado.A.name()
    );

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer());
          obj.setObs(request.getObservacao());
          obj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer());
          newObj.setObs(request.getObservacao());
          newObj.setEtapa(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA.name());
          newObj.setReferencia("EMPRESTIMO");
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          newObj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(newObj);
        });
  }

  public void elaborarContrato(String uuid, ElaboracaoContratoRequestDTO request) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var funId = entity.getTiprel().getFunId();

    var order = pedidoEntityRepository.findByFunIdAndTipoPedidoAndEstado(funId, "EMPRESTIMO", Estado.A.name()).orElseThrow();
    order.setEtapa(EtapaEmprestimo.ELABORAR_CONTRATO.name());
    pedidoEntityRepository.save(order);

    saveDocuments(request.getDocumentos(), funId, entity.getUuid());
  }

  private void saveDocuments(List<DocumentoDTO> documentos, FuncionarioEntity funId, String referenceId) {

    if (Objects.isNull(documentos) || documentos.isEmpty())
      return;

    var docs = new ArrayList<DocumentoEntity>();

    documentos.forEach(doc -> {

      final DocumentoEntity newDoc;

      if (StringUtils.hasText(doc.getId())) {
        newDoc = documentoEntityRepository.findByUuidOrThrow(UUID.fromString(doc.getId()));
      } else {
        newDoc = new DocumentoEntity();
        newDoc.setEstado(Estado.A);
        newDoc.setReferenciaName(TableName.RH_T_EMPRESTIMO.name());
        newDoc.setReferenciaId(referenceId);
        newDoc.setUuid(UuidCreator.getTimeOrderedEpoch());
        newDoc.setDocId(1L);
      }

      newDoc.setTpDocumentoId(tipoDocumentoEntityRepository.findByUuidOrThrow(UUID.fromString(doc.getTipoDocumentoId())));
      newDoc.setFunId(funId);
      newDoc.setUrl(doc.getUrl());

      docs.add(newDoc);
    });

    documentoEntityRepository.saveAll(docs);
  }

  public List<PlanoFinanceiroRowDTO> generateFinancialPlan(String uuid) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var plan = FinancialPlanHelper.generateFinancialPlan(
        Double.parseDouble(entity.getValorEmprestimo().toString()),
        Double.parseDouble("0.035"), // TODO 02/02/2026 21:11 valor
        Integer.parseInt(entity.getNrPrestacao().toString()),
        entity.getDataInicio() != null ? entity.getDataInicio() : LocalDate.now()
    );

    var plans = new ArrayList<PlanoFinanceiroEntity>();

    plan.forEach(obj -> {
      var newPlan = new PlanoFinanceiroEntity();
      newPlan.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      newPlan.setEmprestimo(entity);
      newPlan.setNrOrdemPrestacao(obj.numero());
      newPlan.setDataPagamento(obj.dataPagamento());
      newPlan.setValorPrincipal(obj.principal());
      newPlan.setValorJuros(obj.juros());
      newPlan.setEstado(Estado.A.name());
      newPlan.setSaldoInicial(obj.saldoInicial());
      newPlan.setSaldoFinal(obj.saldoFinal());
      plans.add(newPlan);
    });

    planoFinanceiroEntityRepository.saveAll(plans);

    return plan;
  }
}


package cv.inps.rh.emprestimo.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.commands.SaveConfiguracaoInfoEmprestimoCommand;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
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

// TODO 01/02/2026 14:29 validate the cases where the orders are returned, decision is not positive

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

    if (StringUtils.hasText(uuid)) {
      entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    } else {
      entity = new EmprestimoEntity();
      entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      entity.setEstado(Estado.A.name());
      entity.setDataInicio(LocalDate.now());
      entity.setTipoEmprestimo("AQUISICAO_VIATURA");
      entity.setFinalidade("AQUISICAO_VIATURA");
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

    var orderOP = pedidoEntityRepository.findByFunIdAndTipoPedidoAndEstado(funId, "EMPRESTIMO", Estado.A.name());
    if (orderOP.isEmpty()) {
      var order = new PedidoEntity();
      order.setFunId(funId);
      order.setUuid(UuidCreator.getTimeOrderedEpoch());
      order.setTipoPedido("EMPRESTIMO");
      order.setOrigem("RH");
      order.setEtapa(EtapaEmprestimo.PEDIDO.name());
      order.setEstado(Estado.A.name());
      var savedOrder = pedidoEntityRepository.save(order);
      entity.setPedido(savedOrder);
    }

    var response = new IdDTO(emprestimoEntityRepository.save(entity).getUuid());

    var docs = new ArrayList<DocumentoEntity>();

    request.getDocumentos().forEach(doc -> {
      var newDoc = new DocumentoEntity();
      newDoc.setUuid(UuidCreator.getTimeOrderedEpoch());
      newDoc.setTpDocumentoId(tipoDocumentoEntityRepository.findByUuidOrThrow(UUID.fromString(doc.getTipoDocumentoId())));
      newDoc.setEstado(Estado.A);
      newDoc.setFunId(funId);
      newDoc.setReferenciaId(response.getId());
      newDoc.setReferenciaName("RH_T_EMPRESTIMO");
      newDoc.setDocId(1L);
      newDoc.setUrl(doc.getUrl());
      newDoc.setEstado(Estado.A);
      docs.add(newDoc);
    });

    documentoEntityRepository.saveAll(docs);

    saveDocuments(request.getDocumentos(), funId, response.getId());

    return response;
  }

  public void saveUpdateDecisaoAnaliseRh(String uuid, AnaliseRhRequestDTO request) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    entity.setNrPrestacao(request.getNumeroPrestacao());
    entity.setValorEmprestimo(request.getValorEmprestimo());
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

    // TODO 01/02/2026 14:52 CALCULA O NUMERO DE PRESTACAO MENSAL E DATA INICIO

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
        newDoc.setReferenciaName("RH_T_EMPRESTIMO");
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
}


package cv.inps.rh.emprestimo.domain.service.process;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.domain.service.EmprestimoDocumentService;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import cv.inps.rh.emprestimo.domain.service.constants.*;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.BancoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoDecisaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class PedidoAquisicaoViaturaService {

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final PedidoEntityRepository pedidoEntityRepository;
  private final BancoEntityRepository bancoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final EmprestimoDocumentService documentService;
  private final EmprestimoWriteService emprestimoWriteService;

  public IdDTO saveUpdatePedidoEmprestimo(String emprestimoId, PedidoEmprestimoRequestDTO request) {

    var currentRelation = funcionarioRules.getTipoRelacionamentoAtual(UUID.fromString(request.getFuncionarioId()));
    var funId = currentRelation.getFunId();

    final var isLoanUpdate = StringUtils.hasText(emprestimoId);

    EmprestimoEntity entity;

    if (isLoanUpdate)
      entity = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);
    else {
      entity = new EmprestimoEntity();
      entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      entity.setEstado(StatusEmprestimo.POR_SUBMETER.name());
      entity.setTipoEmprestimo(TipoPedido.AQUISICAO_VIATURA.name());
      entity.setFinalidade(TipoPedido.AQUISICAO_VIATURA.name());
      entity.setTipoSituacao(request.getTipoSituacao());
      entity.setVersao(1L);
    }

    entity.setBanco(Objects.nonNull(request.getBancoId()) ? bancoEntityRepository.findById(request.getBancoId()).orElseThrow() : null);
    entity.setTiprel(currentRelation);
    entity.setMarca(request.getMarca());
    entity.setAnoFabrico(request.getAnoFabrico());
    entity.setCilincrada(request.getCilindrada());
    entity.setTipoViatura(request.getTipoviatura());
    entity.setCombustivel(request.getCombustivel());
    entity.setEstadoViatura(request.getEstadoViatura());
    entity.setValorEmprestimo(request.getValorEmprestimo());
    entity.setValorDivida(request.getValorEmprestimo());
    entity.setNrPrestacao(request.getNumeroPrestacoes());
    entity.setJuro(request.getJuros());
    entity.setNib(request.getNib());
    entity.setNif(request.getNif());

    PedidoEntity order;

    if (!isLoanUpdate) {
      order = new PedidoEntity();
      order.setFunId(funId);
      order.setUuid(UuidCreator.getTimeOrderedEpoch());
      order.setTipoPedido(TipoPedido.AQUISICAO_VIATURA.name());
      order.setOrigem("RH");
      order.setEtapa(EtapaEmprestimo.PEDIDO.name());
      order.setEstado(Estado.A.name());

    } else {

      order = entity.getPedido();

      if (request.getAction().equals(ProcessStepAction.NEXT)) {
        order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
        entity.setEstado(StatusEmprestimo.SUBMETIDO.name());
      }
    }

    order = pedidoEntityRepository.save(order);
    entity.setPedido(order);
    entity = emprestimoEntityRepository.save(entity);

    var response = new IdDTO(entity.getUuid());

    documentService.saveDocuments(
        request.getDocumentos(),
        funId,
        response.getId(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.PEDIDO.name()
    );

    return response;
  }

  public void saveUpdateDecisaoAnaliseRh(String uuid, AnaliseRhRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    loan.setNrPrestacao(request.getNumeroPrestacao());
    loan.setValorEmprestimo(request.getValorEmprestimo());
    loan.setJuro(request.getJuros());

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());

    if (request.getAction().equals(ProcessStepAction.NEXT)) {
      switch (request.getParecer()) {
        case FAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.VALIDADO_RH.name());
        }
        case DESFAVORAVEL -> loan.setEstado(StatusEmprestimo.VALIDADO_RH.name());
        case RETIFICACAO -> {
          order.setEtapa(EtapaEmprestimo.PEDIDO.name());
          loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
        }
      }
    }

    emprestimoEntityRepository.save(loan);
    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_RH_PEDIDO.name(),
        Estado.A.name()
    );

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer().name());
          obj.setObs(request.getObservacao());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer().name());
          newObj.setObs(request.getObservacao());
          newObj.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    documentService.saveDocuments(
        request.getDocumentos(),
        loan.getTiprel().getFunId(),
        loan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANALISE_RH_PEDIDO.name()
    );
  }

  public void saveUpdateDecisaoAnaliseFinanceira(String uuid, AnaliseFinanceiroRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    loan.setDescCabimentacaoOrcamental(request.getCabimentacaoOrcamental());
    loan.setDescTaxaEsforco(request.getAvaliacaoTaxaEsforco());

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name());
    pedidoEntityRepository.save(order);

    if (request.getAction().equals(ProcessStepAction.NEXT)) {
      loan.setEstado(StatusEmprestimo.VALIDADO_DFI.name());
      switch (request.getParecer()) {
        case FAVORAVEL -> order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name());
        case DESFAVORAVEL -> order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
        default ->
            throw IgrpResponseStatusException.badRequest("Invalid decison for this step %s".formatted(request.getParecer()));
      }
    }

    pedidoEntityRepository.save(order);
    emprestimoEntityRepository.save(loan);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name(),
        Estado.A.name()
    );

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer().name());
          obj.setObs(request.getObservacao());
          obj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer().name());
          newObj.setObs(request.getObservacao());
          newObj.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          newObj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    emprestimoEntityRepository.save(loan);
  }

  public void autorizarComissaoExecutiva(String uuid, AutorizacaoComissaoExecutivaDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name());

    if (request.getAction().equals(ProcessStepAction.NEXT)) {
      switch (request.getParecer()) {
        case FAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.AUTORIZADO.name());
        }
        case DESFAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.NAO_AUTORIZADO.name());
        }
        case RETIFICACAO -> {
          order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
        }
      }
    }

    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name(),
        Estado.A.name()
    );

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer().name());
          obj.setObs(request.getObservacao());
          obj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer().name());
          newObj.setObs(request.getObservacao());
          newObj.setEtapa(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          newObj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(newObj);
        });
  }

  @Transactional
  public void elaborarContrato(String uuid, ElaboracaoContratoRequestDTO request) {

    var isNext = request.getAction().equals(ProcessStepAction.NEXT);

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    if (Objects.nonNull(request.getDataInicioEmprestimo())) {
      loan.setDataInicio(request.getDataInicioEmprestimo());
      emprestimoEntityRepository.save(loan);
    }

    loan.setEstado(isNext ? StatusEmprestimo.CABIMENTADO.name() : loan.getEstado());

    emprestimoWriteService.generateSaveFinancialPlan(loan);

    var step = isNext ?
        EtapaEmprestimo.PAGAMENTO :
        EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO;

    var order = loan.getPedido();
    order.setEtapa(step.name());
    pedidoEntityRepository.save(order);

    documentService.saveDocuments(
        request.getDocumentos(),
        loan.getTiprel().getFunId(),
        loan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO.name()
    );
  }
}

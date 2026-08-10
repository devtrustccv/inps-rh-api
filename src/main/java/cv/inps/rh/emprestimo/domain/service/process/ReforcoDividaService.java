package cv.inps.rh.emprestimo.domain.service.process;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.domain.service.EmprestimoDocumentService;
import cv.inps.rh.emprestimo.domain.service.constants.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoDecisaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PlanoFinanceiroEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ReforcoDividaService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReforcoDividaService.class);

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final PedidoEntityRepository pedidoEntityRepository;
  private final EmprestimoDocumentService documentService;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;
  private final AdiantamentoEmprestimoHelper adiantamentoEmprestimoHelper;

  public String saveUpdatePedidoReforco(PedidoReforcoRequestDTO obj) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(obj.getEmprestimoId());

    var tipoSituacao = TipoSituacao.valueOf(obj.getTipoRenegociacao());

    var newLoan = new EmprestimoEntity();
    BeanUtils.copyProperties(loan, newLoan);
    newLoan.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    newLoan.setValorAdiantado(obj.getValorReforco());
    newLoan.setTipoEmprestimo(TipoPedido.AQUISICAO_VIATURA.name());
    newLoan.setVersao(loan.getVersao() + 1);
    newLoan.setTipoSituacao(tipoSituacao.name());
    newLoan.setValorPago(null);
    newLoan.setEmprestimo(loan);
    newLoan.setNrPrestacao(obj.getNumeroPrestacao());
    newLoan.setValorEmprestimo(obj.getValorReforco());
    newLoan.setValorDivida(obj.getValorReforco());
    newLoan.setMotivo(obj.getMotivoReforco());
    newLoan.setEstado(StatusEmprestimo.POR_SUBMETER.name());
    var saved = emprestimoEntityRepository.save(loan);

    documentService.saveDocuments(
        obj.getDocumentos(),
        loan.getTiprel().getFunId(),
        saved.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.PEDIDO.name()
    );

    if (obj.getAction().equals(ProcessStepAction.NEXT)) {

      newLoan.setEstado(StatusEmprestimo.SUBMETIDO.name());
      newLoan = emprestimoEntityRepository.save(newLoan);

      var rowsInactivated = planoFinanceiroEntityRepository.inativarPlanosNaoPagos(loan.getId());
      LOGGER.debug("INACTIVATED {} ROWS FOR LOAN ID <{}> : ", rowsInactivated, loan.getId());

      adiantamentoEmprestimoHelper.saveByTipoSituacao(
          tipoSituacao,
          newLoan,
          obj.getValorReforco(),
          obj.getNumeroPrestacao()
      );
    }

    return saved.getUuid();
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
          order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_REFORCO.name());
          loan.setEstado(StatusEmprestimo.VALIDADO_RH.name());
        }
        case DESFAVORAVEL -> loan.setEstado(StatusEmprestimo.VALIDADO_RH.name());
        case RETIFICACAO -> {
          order.setEtapa(EtapaEmprestimo.PEDIDO.name());
          loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
        }
      }
    }

    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_RH_REFORCO.name(),
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
          newObj.setEtapa(EtapaEmprestimo.ANALISE_RH_REFORCO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    documentService.saveDocuments(
        request.getDocumentos(),
        loan.getTiprel().getFunId(),
        loan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANALISE_RH_REFORCO.name()
    );
  }

  public void saveUpdateDecisaoAnaliseFinanceira(String uuid, AnaliseFinanceiroRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    loan.setDescCabimentacaoOrcamental(request.getCabimentacaoOrcamental());
    loan.setDescTaxaEsforco(request.getAvaliacaoTaxaEsforco());
    emprestimoEntityRepository.save(loan);

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_REFORCO.name());
    pedidoEntityRepository.save(order);

    if (request.getAction().equals(ProcessStepAction.NEXT)) {
      loan.setEstado(StatusEmprestimo.VALIDADO_DFI.name());
      switch (request.getParecer()) {
        case FAVORAVEL -> order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_REFORCO.name());
        case DESFAVORAVEL -> order.setEtapa(EtapaEmprestimo.ANALISE_RH_REFORCO.name());
        default ->
            throw IgrpResponseStatusException.badRequest("Invalid decison for this step %s".formatted(request.getParecer()));
      }
    }

    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_FINANCEIRA_REFORCO.name(),
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
          newObj.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_REFORCO.name());
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
    order.setEtapa(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_REFORCO.name());

    if (request.getAction().equals(ProcessStepAction.NEXT)) {
      switch (request.getParecer()) {
        case FAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ELABORAR_CONTRATO_REFORCO.name());
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
        EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_REFORCO.name(),
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
          newObj.setEtapa(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_REFORCO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          newObj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(newObj);
        });
  }

  public void elaborarContrato(String uuid, ElaboracaoContratoRequestDTO request) {

    var isNext = request.getAction().equals(ProcessStepAction.NEXT);

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    loan.setEstado(isNext ? StatusEmprestimo.CABIMENTADO.name() : loan.getEstado());

    var step = isNext ?
        EtapaEmprestimo.PAGAMENTO :
        EtapaEmprestimo.ELABORAR_CONTRATO_REFORCO;

    var order = loan.getPedido();
    order.setEtapa(step.name());
    pedidoEntityRepository.save(order);

    documentService.saveDocuments(
        request.getDocumentos(),
        loan.getTiprel().getFunId(),
        loan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ELABORAR_CONTRATO_REFORCO.name()
    );
  }
}

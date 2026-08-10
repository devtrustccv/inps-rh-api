package cv.inps.rh.emprestimo.domain.service.process;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.emprestimo.application.dto.AnaliseRhAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.VerificarAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoDocumentService;
import cv.inps.rh.emprestimo.domain.service.constants.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AdiantamentoEmprestimoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdiantamentoEmprestimoService.class);

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final PedidoEntityRepository pedidoEntityRepository;
  private final BancoEntityRepository bancoEntityRepository;
  private final EmprestimoDocumentService documentService;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;
  private final AdiantamentoEmprestimoHelper adiantamentoEmprestimoHelper;

  @Transactional
  public String saveUpdatePedidoAdiantamento(PedidoAdiantamentoRequestDTO obj) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(obj.getEmprestimoId());

    var tipoSituacao = TipoSituacao.valueOf(obj.getTipoSituacao());

    var newLoan = new EmprestimoEntity();
    BeanUtils.copyProperties(loan, newLoan);
    newLoan.setId(null);
    newLoan.setValorPago(null);
    newLoan.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    newLoan.setValorAdiantado(obj.getValorAdiantamento());
    newLoan.setTipoEmprestimo(TipoPedido.AQUISICAO_VIATURA.name());
    newLoan.setTipoSituacao(tipoSituacao.name());
    newLoan.setVersao(loan.getVersao() + 1);
    newLoan.setValorDivida(obj.getValorAdiantamento());
    newLoan.setValorEmprestimo(obj.getValorAdiantamento());
    newLoan.setEmprestimo(loan);
    newLoan.setNrPrestacao(obj.getNumeroPrestacao());
    newLoan.setEstado(StatusEmprestimo.POR_SUBMETER.name());
    newLoan.setPedido(loan.getPedido());
    newLoan.setJuro(loan.getJuro());
    newLoan = emprestimoEntityRepository.save(loan);

    documentService.saveDocuments(
        obj.getDocumentos(),
        loan.getTiprel().getFunId(),
        newLoan.getUuid(),
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
          obj.getValorAdiantamento(),
          obj.getNumeroPrestacao()
      );
    }

    return newLoan.getUuid();
  }

  public void saveAnaliseRh(String emprestimoId, AnaliseRhAdiantamentoRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);
    loan.setBanco(request.getBancoId() != null ? bancoEntityRepository.findById(request.getBancoId()).orElseThrow() : null);
    loan.setNib(ValidationUtil.sanitizeNib(request.getNib()));
    loan.setSwift(request.getSwift());

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name());

    if (request.getAction().equals(ProcessStepAction.NEXT)) {
      switch (request.getParecer()) {
        case FAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name());
          loan.setEstado(StatusEmprestimo.VALIDADO_RH.name());
        }
        case DESFAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.PEDIDO.name());
          loan.setEstado(StatusEmprestimo.VALIDADO_RH.name());
        }
        case RETIFICACAO -> loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
      }
    }

    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name(),
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
          newObj.setEtapa(EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    emprestimoEntityRepository.save(loan);
  }

  public void anexarComprovativo(String emprestimoId, DocumentoDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);

    documentService.saveDocuments(
        List.of(request),
        loan.getTiprel().getFunId(),
        loan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name()
    );
  }

  public void verificar(String emprestimoId, VerificarAdiantamentoRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());

    if (request.getAction().equals(ProcessStepAction.NEXT)) {

      switch (request.getParecer()) {
        case FAVORAVEL -> order.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());
        case RETIFICACAO -> order.setEtapa(EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name());
        default ->
            throw IgrpResponseStatusException.badRequest("Invalid action for this step %s", request.getAction());
      }
    }

    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name(),
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
          newObj.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    emprestimoEntityRepository.save(loan);
  }
}

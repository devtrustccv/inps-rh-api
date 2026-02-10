package cv.inps.rh.emprestimo.domain.service.process;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.dto.AnaliseRhAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.BaseDecisaoDTO;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoDocumentService;
import cv.inps.rh.emprestimo.domain.service.constants.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.BancoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoDecisaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PlanoFinanceiroEntityRepository;
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
  private final BancoEntityRepository bancoEntityRepository;
  private final EmprestimoDocumentService documentService;
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;
  private final AdiantamentoEmprestimoHelper adiantamentoEmprestimoHelper;

  public String saveUpdatePedidoAdiantamento(PedidoAdiantamentoRequestDTO obj) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(obj.getEmprestimoId());
    var rowsInactivated = planoFinanceiroEntityRepository.inativarPlanosNaoPagos(loan.getId());
    LOGGER.debug("INACTIVATED {} ROWS FOR LOAN ID <{}> : ", rowsInactivated, loan.getId());

    var tipoSituacao = TipoSituacao.valueOf(obj.getTipoSituacao());

    var newLoan = new EmprestimoEntity();
    BeanUtils.copyProperties(loan, newLoan);
    newLoan.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    newLoan.setValorAdiantado(obj.getValorAdiantamento()); // TODO 09/02/2026 19:12 so se for adiantamento ADIANTAMENTO_PAGAMENTO_ANTECIPADO ou
    newLoan.setTipoEmprestimo(TipoPedido.AQUISICAO_VIATURA.name());
    newLoan.setTipoSituacao(tipoSituacao.name());
    newLoan.setVersao(loan.getVersao() + 1);
    newLoan.setValorPago(null);
    newLoan.setEmprestimo(loan);
    var saved = emprestimoEntityRepository.save(loan);

    adiantamentoEmprestimoHelper.saveByTipoSituacao(tipoSituacao, newLoan, obj.getValorAdiantamento(), obj.getNumeroPrestacao());

    return saved.getUuid();
  }

  public void saveAnaliseRh(String emprestimoId, AnaliseRhAdiantamentoRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);
    loan.setBanco(request.getBancoId() != null ? bancoEntityRepository.findById(request.getBancoId()).orElseThrow() : null);
    loan.setNib(request.getNib());
    loan.setSwift(request.getSwift());

    var order = loan.getPedido();

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name(),
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
          newObj.setEtapa(EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    if ("DESFAVORAVEL".equals(request.getParecer())) {

      loan.setEstado(Estado.I.name());

      var rowsActivatedFromLoan = planoFinanceiroEntityRepository.inativarPlanosNaoPagos(loan.getId());
      LOGGER.debug("INACTIVATED {} ROWS FOR CURRENT LOAN ID <{}> : ", rowsActivatedFromLoan, loan.getId());

      var father = loan.getEmprestimo();
      var rowsActivated = planoFinanceiroEntityRepository.ativarPlanosNaoPagos(father.getId());
      LOGGER.debug("ACTIVATED {} ROWS FOR LOAN FATHER ID <{}> : ", rowsActivated, father.getId());
    }

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

  public void verificar(String emprestimoId, BaseDecisaoDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);

    var order = loan.getPedido();

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name(),
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
          newObj.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    emprestimoEntityRepository.save(loan);
  }
}

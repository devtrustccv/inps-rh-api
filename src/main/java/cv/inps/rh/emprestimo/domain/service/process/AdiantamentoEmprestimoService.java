package cv.inps.rh.emprestimo.domain.service.process;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.dto.AnaliseRhAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.BaseDecisaoDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.domain.service.constants.EtapaEmprestimo;
import cv.inps.rh.emprestimo.domain.service.constants.ProcessType;
import cv.inps.rh.emprestimo.domain.service.constants.TipoPedido;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.BancoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoDecisaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class AdiantamentoEmprestimoService {

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final BancoEntityRepository bancoEntityRepository;

  public void saveUpdatePedidoAdiantamento(List<PedidoAdiantamentoRequestDTO> request) {
    for (var obj : request) {
      var loan = emprestimoEntityRepository.findByUuidOrThrow(obj.getEmprestimoId());
      loan.setValorAdiantado(obj.getValorAdiantamento());
      loan.setTipoEmprestimo(TipoPedido.AQUISICAO_VIATURA.name());
      loan.setTipoSituacao(obj.getTipoSituacao());
      emprestimoEntityRepository.save(loan);
    }
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

    if ("DESFAVORAVEL".equals(request.getParecer())) // TODO 04/02/2026 22:02 get real code
      loan.setEstado(Estado.I.name());

    emprestimoEntityRepository.save(loan);
  }

  public void anexarComprovativo() {
    // TODO 04/02/2026 22:06 implement
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

    if ("RETIFICAR".equals(request.getParecer())) { // TODO 04/02/2026 22:02 get real code

      return;
    }

    if ("DESFAVORAVEL".equals(request.getParecer())) {// TODO 04/02/2026 22:02 get real code
      loan.setEstado(Estado.I.name());
      loan.setMotivoFecho("ADIANTAMENTO PAGAMENTO DIVIDA NAO ACEITE");
    }

    if ("FAVORAVEL".equals(request.getParecer())) {// TODO 04/02/2026 22:02 get real code

    }

    emprestimoEntityRepository.save(loan);
  }
}

package cv.inps.rh.emprestimo.domain.service.process;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.domain.service.DocumentService;
import cv.inps.rh.emprestimo.domain.service.constants.EtapaEmprestimo;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoDecisaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
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
  private final PedidoEntityRepository pedidoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DocumentService documentService;

  public void saveUpdatePedidoAdiantamento(List<PedidoAdiantamentoRequestDTO> request) {
    for (var obj : request) {
      var entity = emprestimoEntityRepository.findByUuidOrThrow(obj.getEmprestimoId());
      entity.setValorAdiantado(obj.getValorAdiantamento());
      emprestimoEntityRepository.save(entity);
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

    documentService.saveDocuments(request.getDocumentos(), funId, entity.getUuid());
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

    documentService.saveDocuments(request.getDocumentos(), funId, entity.getUuid());
  }
}

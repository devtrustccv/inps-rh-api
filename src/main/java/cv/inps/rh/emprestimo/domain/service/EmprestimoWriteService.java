package cv.inps.rh.emprestimo.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.commands.SaveConfiguracaoInfoEmprestimoCommand;
import cv.inps.rh.emprestimo.application.dto.AnaliseRhRequestDTO;
import cv.inps.rh.emprestimo.application.dto.IdDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoEmprestimoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
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

    var orderOP = pedidoEntityRepository.findByFunIdAndEtapaAndEstado(funId, EtapaEmprestimo.PEDIDO.name(), Estado.A.name());
    if (orderOP.isEmpty()) {
      var order = new PedidoEntity();
      order.setFunId(funId);
      order.setUuid(UuidCreator.getTimeOrderedEpoch());
      order.setTipoPedido("EMPRESTIMO");
      order.setOrigem("RH");
      order.setEtapa(EtapaEmprestimo.PEDIDO.name());
      order.setEstado(Estado.A.name());
      pedidoEntityRepository.save(order);
    }

    // TODO 29/01/2026 21:23 save documentos

    return new IdDTO(emprestimoEntityRepository.save(entity).getUuid());
  }

  public void saveDecisaoAnaliseEmprestimo(String uuid, AnaliseRhRequestDTO request) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    entity.setNrPrestacao(request.getNumeroPrestacao());
    entity.setValorEmprestimo(request.getValorEmprestimo());
    emprestimoEntityRepository.save(entity);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(null, EtapaEmprestimo.ANALISE_RH.name(), Estado.A.name());

    decisionOP.ifPresentOrElse(obj -> {
          obj.setDecisao(request.getParecer());
          obj.setObs(request.getObservacao());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {

          var order = pedidoEntityRepository.findByFunIdAndEtapaAndEstado(
                  entity.getTiprel().getFunId(),
                  EtapaEmprestimo.PEDIDO.name(),
                  Estado.A.name())
              .orElseThrow();

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
  }
}


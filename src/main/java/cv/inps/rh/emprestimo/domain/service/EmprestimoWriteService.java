package cv.inps.rh.emprestimo.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.commands.SaveConfiguracaoInfoEmprestimoCommand;
import cv.inps.rh.emprestimo.application.dto.IdDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoEmprestimoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
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

    final EmprestimoEntity entity;

    if (StringUtils.hasText(uuid)) {
      entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    } else {
      entity = new EmprestimoEntity();
      entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      entity.setEstado(Estado.A.name());

      // TODO 29/01/2026 21:10 validate this fields
      entity.setDataInicio(LocalDate.now());
      entity.setDataFim(LocalDate.now());
      entity.setTipoEmprestimo("AQUISICAO_VIATURA");
      entity.setFinalidade("AQUISICAO_VIATURA");
    }

    // TODO 29/01/2026 21:28 optimize this code
    entity.setTiprel(funcionarioRules.getTipoRelacionamentoAtual(UUID.fromString(request.getFuncionarioId())));
    entity.setMarca(request.getMarca());
    entity.setAnoFabrico(request.getAnoFabrico());
    entity.setCilincrada(request.getCilindrada());
    entity.setTipoViatura(request.getTipoviatura());
    entity.setCombustivel(request.getCombustivel());
    entity.setEstadoViatura(request.getEstadoViatura());
    entity.setValorEmprestimo(request.getValorEmprestimo());
    entity.setNrPrestacao(request.getNumeroPrestacoes());

    // TODO 29/01/2026 21:10 validate this fields
    entity.setValorPrestacao(request.getValorEmprestimo());
    entity.setPedido(pedidoEntityRepository.findByIdOrThrow(13L));
    entity.setValorDivida(request.getValorEmprestimo());
    entity.setValorDivida(request.getValorEmprestimo());
    entity.setVersao(1L);
    entity.setTipoSituacao("XQWERTY");

    // TODO 29/01/2026 21:23 save documentos

    return new IdDTO(emprestimoEntityRepository.save(entity).getUuid());
  }
}


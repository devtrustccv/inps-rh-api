package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.TipoAccao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FuncionarioRules {

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ContratoEntityRepository contratoEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;


  public boolean temValidacaoPendente(UUID funUuid, TipoAcao tipoAccao, Referencia referenciaName) {
    return validacaoEntityRepository.existsByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(funUuid, Estado.P, tipoAccao.name(),
        referenciaName.name());
  }

  public TiposRelacionamentoEntity getTipoRelacionamentoAtual(UUID funUuid) {
    return tiposRelacionamentoEntityRepository.findAtualByFuncionarioUuid(funUuid)
        .orElseThrow(()-> IgrpResponseStatusException.badRequest("Funcionario sem tipo de relacionamento atual"));
  }


  public ContratoEntity getContratoComMaiorVersao(UUID funUuid) {
    return contratoEntityRepository.findTopByFunId_UuidOrderByVersaoDesc(funUuid);
  }


  public ContratoEntity getPrimeiroContrato(UUID funUuid) {
    return contratoEntityRepository.findPrimeiroContratoFuncionario(funUuid);
  }


  public TiposRelacionamentoEntity getTipoRelacionamentoByContratoId(UUID funId, UUID contratoId) {
    if (funId == null || contratoId == null) return null;
    return tiposRelacionamentoEntityRepository.findByFunUuidAndContratoUuid(funId, contratoId);
  }


    public List<DefinicaoRemuneracaoEntity> getRemuneracoesAssociadosAtivas(Long tipoRelacionamentoId) {

      return tipoRelRemPagEntityRepository
          .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.A)
          .stream()
          .map(TipoRelRemPagEntity::getRemId)
          .filter(Objects::nonNull)
          //.distinct()
          .collect(Collectors.toList());

  }

  public List<DefPagamentoEntity> getPagamentosDescontosAssociadosAtivas(Long tipoRelacionamentoId) {

    return tipoRelRemPagEntityRepository
        .findByTiprelIdAndEstado(tipoRelacionamentoId, Estado.A)
        .stream()
        .map(TipoRelRemPagEntity::getPagId)
        .filter(Objects::nonNull)
        //.distinct()
        .collect(Collectors.toList());

  }


  public Optional<ValidacaoEntity> getValidacaoPendente(UUID funUuid, TipoAcao tipoAccao, Referencia referenciaName) {

    return validacaoEntityRepository
        .findByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(funUuid, Estado.P, tipoAccao.name(), referenciaName.name());
  }



}

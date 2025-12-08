package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.TipoAccao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FuncionarioRules {

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ContratoEntityRepository contratoEntityRepository;


  public boolean temValidacaoPendente(UUID funUuid, TipoAcao tipoAccao, Referencia referenciaName) {
    return validacaoEntityRepository.existsByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(funUuid, Estado.P, tipoAccao.name(),
        referenciaName.name());
  }

  public TiposRelacionamentoEntity getTipoRelacionamentoAtual(UUID funUuid) {
    return tiposRelacionamentoEntityRepository.findAtualByFuncionarioUuid(funUuid)
        .orElseThrow(()-> IgrpResponseStatusException.badRequest("Funcionario sem tipo de relacionamento atual"));
  }

 /* public TiposRelacionamentoEntity getTipoRelacionamentoAtual(FuncionarioEntity entity) {
    return entity.getTiposrelacionamentos().stream()
        .filter(t -> t.getEstActAdm() != null && t.getEstActAdm() == 1)
        .max(Comparator.comparing(TiposRelacionamentoEntity::getDataInicio))
        .orElse(null);
  }*/

  public ContratoEntity getContratoComMaiorVersao(FuncionarioEntity entity) {

    if (entity.getContratos() == null || entity.getContratos().isEmpty())
      return null;

    return entity.getContratos().stream()
        .filter(c -> c.getVersao() != null)
        .max(Comparator.comparing(ContratoEntity::getVersao))
        .orElse(null);
  }

  public ContratoEntity getContratoComMaiorVersao(UUID funUuid) {
    return contratoEntityRepository.findTopByFunId_UuidOrderByVersaoDesc(funUuid);
  }

  public ContratoEntity getPrimeiroContrato(FuncionarioEntity entity) {

    if (entity.getContratos() == null || entity.getContratos().isEmpty())
      return null;

    return entity.getContratos().stream()
        .filter(c -> c.getVersao() != null && c.getVersao() == 1)
        .findFirst()
        .orElse(null);
  }

  public ContratoEntity getPrimeiroContrato(UUID funUuid) {
    return contratoEntityRepository.findPrimeiroContratoFuncionario(funUuid);
  }

  public TiposRelacionamentoEntity getTipoRelacionamentoByContratoId(FuncionarioEntity fun, UUID contratoId) {

    if (fun == null || contratoId == null) return null;

    return fun.getTiposrelacionamentos().stream()
        .filter(tr -> tr.getContrVinculoId() != null
                      && tr.getContrVinculoId().getUuid().equals(contratoId))
        .findFirst()
        .orElse(null);
  }

  public TiposRelacionamentoEntity getTipoRelacionamentoByContratoId(UUID funId, UUID contratoId) {
    if (funId == null || contratoId == null) return null;
    return tiposRelacionamentoEntityRepository.findByFunUuidAndContratoUuid(funId, contratoId);
  }



  public boolean temValidacaoPendente(FuncionarioEntity fun, String tipoAccao, String referenciaName) {

    if (fun == null || fun.getValidacoes() == null || fun.getValidacoes().isEmpty())
      return false;

    return fun.getValidacoes().stream()
        .anyMatch(v ->
            v.getEstado() == Estado.P &&
                tipoAccao.equals(v.getTipoAccao()) &&
                referenciaName.equals(v.getReferenciaName())
        );
  }

}

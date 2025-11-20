package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.UUID;

@Component
public class FuncionarioRules {


  public TiposRelacionamentoEntity getTipoRelacionamentoAtual(FuncionarioEntity entity) {

    return entity.getTiposrelacionamentos().stream()
        .filter(t -> t.getEstActAdm() != null && t.getEstActAdm() == 1)
        .max(Comparator.comparing(TiposRelacionamentoEntity::getDataInicio))
        .orElse(null);

  }


  public ContratoEntity getContratoComMaiorVersao(FuncionarioEntity entity) {
    if (entity.getContratos() == null || entity.getContratos().isEmpty())
      return null;

    return entity.getContratos().stream()
        .filter(c -> c.getVersao() != null)
        .max((a, b) -> a.getVersao().compareTo(b.getVersao()))
        .orElse(null);
  }

  public ContratoEntity getPrimeiroContrato(FuncionarioEntity entity) {
    if (entity.getContratos() == null || entity.getContratos().isEmpty())
      return null;

    return entity.getContratos().stream()
        .filter(c -> c.getVersao() != null && c.getVersao() == 1)
        .findFirst()
        .orElse(null);
  }

  public TiposRelacionamentoEntity getTipoRelacionamentoByContratoId(FuncionarioEntity fun, UUID contratoId) {
    if (fun == null || contratoId == null) return null;

    return fun.getTiposrelacionamentos().stream()
        .filter(tr -> tr.getContratoId() != null
            && tr.getContratoId().getUuid().equals(contratoId))
        .findFirst()
        .orElse(null);
  }



}

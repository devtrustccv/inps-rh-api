package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.Contrato;
import cv.inps.rh.funcionario.domain.models.SituacaoLaboral;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SituacaoLaboralMapper {

  private final ContratoMapper contratoMapper;

  // Entity -> Domain
  public SituacaoLaboral toDomain(SituacaoLaboralEntity entity) {
    if (entity == null) return null;

    Contrato contrato = null;
    if (entity.getContratoId() != null) {
      contrato = contratoMapper.toDomain(entity.getContratoId());
    }

    return SituacaoLaboral.rebuild(
        entity.getId(),
        entity.getSituacaoLaboral(),
        entity.getMotivoSitLab(),
        entity.getDataInicio(),
        entity.getDataFim(),
        contrato,
        entity.getEstado(),
        entity.getObs(),
        entity.getUuid()
    );
  }

  public SituacaoLaboralEntity toEntity(SituacaoLaboral domain) {
    if (domain == null) return null;

    SituacaoLaboralEntity entity = new SituacaoLaboralEntity();
    entity.setId(domain.getId());
    entity.setSituacaoLaboral(domain.getSituacaoLaboral());
    entity.setMotivoSitLab(domain.getMotivoSitLab());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());
    entity.setUuid(domain.getUuid() != null ? domain.getUuid().getValor() : null);
    //entity.setContratoId(); sera setado no agregaddo pai

    return entity;
  }
}

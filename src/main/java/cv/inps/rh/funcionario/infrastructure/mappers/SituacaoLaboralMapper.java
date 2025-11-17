package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.Contrato;
import cv.inps.rh.funcionario.domain.models.SituacaoLaboral;
import cv.inps.rh.parametrizacao.domain.models.ParamSitLaboral;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamSitLaboralMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSitLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SituacaoLaboralMapper {

  private final ParamSitLaboralMapper paramSitLaboralMapper;
  private final EntityManager entityManager;

  // Entity -> Domain
  public SituacaoLaboral toDomain(SituacaoLaboralEntity entity) {
    if (entity == null) return null;


    return SituacaoLaboral.rebuild(
        entity.getId(),
        paramSitLaboralMapper.toDomain(entity.getSituacaoLaboralId()),
        entity.getMotivoSitLab(),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getEstado(),
        entity.getObs(),
        entity.getUuid()
    );
  }

  public SituacaoLaboralEntity toEntity(SituacaoLaboral domain) {
    if (domain == null) return null;

    SituacaoLaboralEntity entity = new SituacaoLaboralEntity();
    entity.setId(domain.getId());
    entity.setSituacaoLaboralId(entityManager.getReference(ParamSitLaboralEntity.class, domain.getParamSitLaboral().getId()));
    entity.setMotivoSitLab(domain.getMotivoSitLab());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());
    entity.setUuid(domain.getUuid() != null ? domain.getUuid().getValor() : null);

    return entity;
  }
}

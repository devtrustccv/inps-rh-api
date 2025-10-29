package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.Mobilidade;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamLocalTrabMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.SecaoMapper;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MobilidadeMapper {

  private final ContratoMapper contratoMapper;
  private final SecaoMapper secaoMapper;
  private final InstituicaoMapper instituicaoMapper;
  private final ParamLocalTrabMapper paramLocalTrabMapper;
  private final EntityManager entityManager;

  // Entity -> Domain
  public Mobilidade toDomain(MobilidadeEntity entity) {
    if (entity == null) return null;

    return Mobilidade.rebuild(
        entity.getId(),
        entity.getUuid(),
        contratoMapper.toDomain(entity.getContratoId()),
        paramLocalTrabMapper.toDomain(entity.getLocalTrabId()),
        entity.getTipoSituacao(),
        secaoMapper.toDomain(entity.getSecaoId()),
        instituicaoMapper.toDomain(entity.getInstidId()),
        entity.getEstado(),
        entity.getObs()
    );
  }

  // Domain -> Entity
  public MobilidadeEntity toEntity(Mobilidade domain) {
    if (domain == null) return null;

    MobilidadeEntity entity = new MobilidadeEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setLocalTrabId(entityManager.getReference(
        ParamLocalTrabEntity.class,
        domain.getLocalTrab().getId()
    ));
    entity.setTipoSituacao(domain.getTipoSituacao());
    entity.setSecaoId(entityManager.getReference(
        SecaoEntity.class,
        domain.getSecao().getId()
    ));
    entity.setInstidId(entityManager.getReference(
        cv.inps.rh.shared.infrastructure.persistence.entity.InstituicaoEntity.class,
        domain.getInstituicao().getId()
    ));
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());
    return entity;
  }

}

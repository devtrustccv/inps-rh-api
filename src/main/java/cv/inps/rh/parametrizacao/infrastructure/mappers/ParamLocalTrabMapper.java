package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.domain.models.ParamLocalTrab;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParamLocalTrabMapper {

  private GeografiaMapper geografiaMapper;
  private final EntityManager entityManager;


  public ParamLocalTrab toDomain(ParamLocalTrabEntity entity) {
    if (entity == null) return null;

    return ParamLocalTrab.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getNome(),
        geografiaMapper.toDomain(entity.getPaisId()),
        geografiaMapper.toDomain(entity.getIlhaId()),
        entity.getUps(),
        entity.getEstado()
    );
  }

  public ParamLocalTrabEntity toEntity(ParamLocalTrab domain) {
    if (domain == null) return null;

    ParamLocalTrabEntity entity = new ParamLocalTrabEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid() != null ? domain.getUuid().getValor() : null);
    entity.setNome(domain.getNome());

    // Lazy referência — só obtém se existir ID
    if (domain.getPais() != null && domain.getPais().getId() != null) {
      entity.setPaisId(entityManager.getReference(GeografiaEntity.class, domain.getPais().getId()));
    }

    if (domain.getIlha() != null && domain.getIlha().getId() != null) {
      entity.setIlhaId(entityManager.getReference(GeografiaEntity.class, domain.getIlha().getId()));
    }

    entity.setUps(domain.getUps());
    entity.setEstado(domain.getEstado());

    return entity;
  }

  public ParamLocalTrab toDomain(Long id) {
    if (id == null || id < 0) return null;
    return ParamLocalTrab.rebuild(id);
  }
}

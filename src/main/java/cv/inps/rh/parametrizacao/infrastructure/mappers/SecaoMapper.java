package cv.inps.rh.parametrizacao.infrastructure.mappers;

import cv.inps.rh.parametrizacao.domain.models.Secao;
import cv.inps.rh.shared.infrastructure.mappers.InstituicaoMapper;
import cv.inps.rh.shared.infrastructure.mappers.UpsMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.InstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecaoMapper {

  private final EntityManager entityManager;
  private final InstituicaoMapper instituicaoMapper;

  public Secao toDomain(SecaoEntity entity) {
    if (entity == null) return null;

    return Secao.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getNome(),
        instituicaoMapper.toDomain(entity.getInstId()),
        entity.getEstado()
    );
  }

  public SecaoEntity toEntity(Secao domain) {
    if (domain == null) return null;

    SecaoEntity entity = new SecaoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setNome(domain.getNome());
    entity.setEstado(domain.getEstado());

    if (domain.getInstId() != null && domain.getInstId().getId() != null) {
      entity.setInstId(entityManager.getReference(InstituicaoEntity.class,
          domain.getInstId().getId()));
    }

    return entity;
  }

  // Referência mínima
  public Secao toDomain(Long id) {
    if (id == null || id < 0) return null;
    return Secao.rebuild(id);
  }

}

package cv.inps.rh.shared.infrastructure.mappers;

import cv.inps.rh.shared.domain.models.Entidade;
import cv.inps.rh.shared.infrastructure.persistence.entity.EntidadeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntidadeMapper {

  public Entidade toDomain(EntidadeEntity entity) {
    if (entity == null) return null;

    return Entidade.rebuild(
        entity.getId(),
        entity.getNome()
    );
  }

  public Entidade toDomain(Long id) {
    if (id == null) return null;
    return Entidade.rebuild(id, null);
  }

}


package cv.inps.rh.shared.infrastructure.mappers;

import cv.inps.rh.shared.domain.models.Ups;
import cv.inps.rh.shared.infrastructure.persistence.entity.UpsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpsMapper {

  public Ups toDomain(UpsEntity upsEntity) {
    if (upsEntity == null) return null;
    return Ups
        .rebuild(upsEntity.getId(), upsEntity.getNome());
  }

  public Ups toDomain(Long idUps) {
    if (idUps == null) return null;
    return Ups
        .rebuild(idUps, null);
  }
}

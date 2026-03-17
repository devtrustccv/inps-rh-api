package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaAvaliacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParamEscalaAvaliacaoEntityRepository extends
    JpaRepository<ParamEscalaAvaliacaoEntity, Long>,
    JpaSpecificationExecutor<ParamEscalaAvaliacaoEntity> {

  Optional<ParamEscalaAvaliacaoEntity> findByUuid(UUID uuid);

  default ParamEscalaAvaliacaoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamEscalaAvaliacaoEntity not found for id: " + uuid));
  }
}


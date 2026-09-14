package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamPrestadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParamPrestadorEntityRepository extends
    JpaRepository<ParamPrestadorEntity, Long>,
    JpaSpecificationExecutor<ParamPrestadorEntity> {

  Optional<ParamPrestadorEntity> findByUuid(UUID uuid);

  boolean existsByEntIdAndUuidNot(Long entId, UUID uuid);

  boolean existsByEntId(Long entId);

  default ParamPrestadorEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "ParamPrestadorEntity not found for id: " + uuid));
  }
}

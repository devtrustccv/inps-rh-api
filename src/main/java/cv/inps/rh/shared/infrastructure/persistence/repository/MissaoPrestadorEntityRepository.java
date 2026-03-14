package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoPrestadorEntityRepository extends
    JpaRepository<MissaoPrestadorEntity, Long>,
    JpaSpecificationExecutor<MissaoPrestadorEntity> {

  Optional<MissaoPrestadorEntity> findByUuid(UUID uuid);

  default MissaoPrestadorEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MissaoPrestadorEntity not found for id: " + uuid));
  }
}


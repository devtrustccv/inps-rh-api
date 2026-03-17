package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoColaboradorEntityRepository extends
    JpaRepository<MissaoColaboradorEntity, Long>,
    JpaSpecificationExecutor<MissaoColaboradorEntity> {

  Optional<MissaoColaboradorEntity> findByUuid(UUID uuid);

  default MissaoColaboradorEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MissaoColaboradorEntity not found for id: " + uuid));
  }
}


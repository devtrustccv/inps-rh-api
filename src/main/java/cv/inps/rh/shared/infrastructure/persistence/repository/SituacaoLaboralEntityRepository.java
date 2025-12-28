package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface SituacaoLaboralEntityRepository extends
    JpaRepository<SituacaoLaboralEntity, Long>,
    JpaSpecificationExecutor<SituacaoLaboralEntity> {

  default SituacaoLaboralEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "SituacaoLaboralEntity not found for id: " + id));
  }

  Optional<SituacaoLaboralEntity> findByUuid(UUID uuid);

  default SituacaoLaboralEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "SituacaoLaboralEntity not found for id: " + uuid));
  }


}

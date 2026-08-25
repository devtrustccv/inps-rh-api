package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SoatDetalheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SoatDetalheEntityRepository extends
    JpaRepository<SoatDetalheEntity, Long>,
    JpaSpecificationExecutor<SoatDetalheEntity> {

  default SoatDetalheEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "SoatDetalheEntity not found for id: " + id));
  }

  Optional<SoatDetalheEntity> findByUuid(String uuid);

  default SoatDetalheEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SoatDetalheEntity not found for uuid: " + uuid));
  }

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoFaltaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TipoFaltaEntityRepository extends
    JpaRepository<TipoFaltaEntity, Long>,
    JpaSpecificationExecutor<TipoFaltaEntity> {

  default TipoFaltaEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "TipoFaltaEntity not found for id: " + id));
  }

  Optional<TipoFaltaEntity> findByUuid(String uuid);

  default TipoFaltaEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("TipoFaltaEntity not found for id: " + uuid));
  }

  boolean existsByTipo(String tipo);

  boolean existsByTipoAndUuidNot(String tipo, String uuid);

}

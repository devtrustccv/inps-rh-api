package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamLocalTrabEntityRepository extends
    JpaRepository<ParamLocalTrabEntity, Long>,
    JpaSpecificationExecutor<ParamLocalTrabEntity> {

  default ParamLocalTrabEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamLocalTrabEntity not found for id: " + id));
  }

  List<ParamLocalTrabEntity> findAllByEstado(Estado estado);

  Optional<ParamLocalTrabEntity> findByUuid(UUID uuid);

  default ParamLocalTrabEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("ParamLocalTrabEntity not found for id: " + uuid));
  }
}

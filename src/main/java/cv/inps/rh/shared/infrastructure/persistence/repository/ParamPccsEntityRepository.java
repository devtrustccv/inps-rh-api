package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamPccsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParamPccsEntityRepository extends
    JpaRepository<ParamPccsEntity, Long>,
    JpaSpecificationExecutor<ParamPccsEntity> {

  Optional<ParamPccsEntity> findByUuid(UUID uuid);

  default ParamPccsEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("ParamPccsEntity not found for id: " + uuid));
  }

  Optional<ParamPccsEntity> findFirstByEstadoOrderByDataInicioDesc(Estado estado);
}

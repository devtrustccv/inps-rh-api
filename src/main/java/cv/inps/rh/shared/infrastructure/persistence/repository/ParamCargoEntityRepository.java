package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParamCargoEntityRepository extends
    JpaRepository<ParamCargoEntity, Long>,
    JpaSpecificationExecutor<ParamCargoEntity> {

  List<ParamCargoEntity> findAllByEstado(Estado estado);

  List<ParamCargoEntity> findAllByParamCarrId(cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity paramCarrId);

  Optional<ParamCargoEntity> findByUuid(UUID uuid);

  default ParamCargoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamCargoEntity not found for id: " + uuid));
  }

}

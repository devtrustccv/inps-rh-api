package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamCarreiraEntityRepository extends
    JpaRepository<ParamCarreiraEntity, Long>,
    JpaSpecificationExecutor<ParamCarreiraEntity> {

  default ParamCarreiraEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamCarreiraEntity not found for id: " + id));
  }

  List<ParamCarreiraEntity> findAllByEstado(Estado estado);

  List<ParamCarreiraEntity> findAllByPccsId(cv.inps.rh.shared.infrastructure.persistence.entity.ParamPccsEntity pccsId);

  Optional<ParamCarreiraEntity> findByUuid(UUID uuid);

  default ParamCarreiraEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("ParamCarreiraEntity not found for id: " + uuid));
  }
}

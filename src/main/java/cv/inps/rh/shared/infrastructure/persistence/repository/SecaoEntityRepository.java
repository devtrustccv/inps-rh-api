package cv.inps.rh.shared.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;


@Repository
public interface SecaoEntityRepository extends
    JpaRepository<SecaoEntity, Long>,
    JpaSpecificationExecutor<SecaoEntity> {

  default SecaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SecaoEntity not found for id: " + id));
  }

  List<SecaoEntity> findAllByEstado(Estado estado);

  List<SecaoEntity> findAllByEstadoAndInstId_Id(Estado estado, Long institutoId);

  Optional<SecaoEntity> findByUuid(UUID uuid);

  default SecaoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SecaoEntity not found for id: " + uuid));
  }

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamContratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamContratoEntityRepository extends
    JpaRepository<ParamContratoEntity, Long>,
    JpaSpecificationExecutor<ParamContratoEntity> {

  default ParamContratoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamContratoEntity not found for id: " + id));
  }

  List<ParamContratoEntity> findAllByEstado(Estado estado);

  //List<ParamContratoEntity> findAllByEstadoAndParamVinculoId_Id(Estado estado, Long paramVinculoId);

  Optional<ParamContratoEntity> findByUuid(UUID uuid);

  default ParamContratoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamVinculoEntity not found for id: " + uuid));
  }
}

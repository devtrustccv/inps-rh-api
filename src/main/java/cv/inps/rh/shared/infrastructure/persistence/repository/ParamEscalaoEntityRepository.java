package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamEscalaoEntityRepository extends
    JpaRepository<ParamEscalaoEntity, Long>,
    JpaSpecificationExecutor<ParamEscalaoEntity> {

  default ParamEscalaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamEscalaoEntity not found for id: " + id));
  }

  List<ParamEscalaoEntity> findAllByEstado(Estado estado);

  List<ParamEscalaoEntity> findAllByEstadoAndParamCarrId_Id(Estado estado, Long carreiraId);

  List<ParamEscalaoEntity> findAllByParamCarrId(cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity paramCarrId);

  Optional<ParamEscalaoEntity> findByUuid(UUID uuid);

  default ParamEscalaoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("ParamEscalaoEntity not found for id: " + uuid));
  }

  Optional<ParamEscalaoEntity> findByNivelReferenciaAndEscalaoAndEstado(
      Integer nivelReferencia,
      String escalao,
      Estado estado
  );

}

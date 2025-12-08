package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSitLaboralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamSitLaboralEntityRepository extends JpaRepository<ParamSitLaboralEntity, Long>, JpaSpecificationExecutor<ParamSitLaboralEntity> {

  default ParamSitLaboralEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamSitLaboralEntity not found for id: " + id));
  }

  List<ParamSitLaboralEntity> findAllByEstado(Estado estado);

  Optional<ParamSitLaboralEntity> findByUuid(UUID uuid);

  default ParamSitLaboralEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamSitLaboralEntity not found for id: " + uuid));
  }

  List<ParamSitLaboralEntity> findAllByNome(final String nome);

  Optional<ParamSitLaboralEntity> findByCodigo(final String codigo);

}

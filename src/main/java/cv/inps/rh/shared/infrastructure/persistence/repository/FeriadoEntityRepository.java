package cv.inps.rh.shared.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriadoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;


@Repository
public interface FeriadoEntityRepository extends
    JpaRepository<FeriadoEntity, Long>,
    JpaSpecificationExecutor<FeriadoEntity> {

  default FeriadoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "FeriadoEntity not found for id: " + id));
  }

  Optional<FeriadoEntity> findByUuid(String uuid);

  default FeriadoEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("FeriadoEntity not found for id: " + uuid));
  }

  List<FeriadoEntity> findAllByAnoReferenteAndEstado(Integer anoReferente, Estado estado);

  List<FeriadoEntity> findAllByGeogrId(GeografiaEntity geogrId);

  List<FeriadoEntity> findAllByTipoFeriado(String tipoFeriado);

  List<FeriadoEntity> findAllByFixoAno(String fixoAno);

  Estado Estado(Estado estado);
}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface TipoDocumentoEntityRepository extends
    JpaRepository<TipoDocumentoEntity, Long>,
    JpaSpecificationExecutor<TipoDocumentoEntity> {

  default TipoDocumentoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "TipoDocumentoEntity not found for id: " + id));
  }

  List<TipoDocumentoEntity> findAllByEstado(Estado estado);

  List<TipoDocumentoEntity> findAllByReferenciaAndEstado(String referencia, Estado estado);

  Optional<TipoDocumentoEntity> findByUuid(UUID uuid);

  default TipoDocumentoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("TipoDocumentoEntity not found for id: " + uuid));
  }


}

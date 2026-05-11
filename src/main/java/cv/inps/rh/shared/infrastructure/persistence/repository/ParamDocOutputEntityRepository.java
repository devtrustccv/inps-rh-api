package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamDocOutputEntityRepository extends
    JpaRepository<ParamDocOutputEntity, Long>,
    JpaSpecificationExecutor<ParamDocOutputEntity>
{

      default ParamDocOutputEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ParamDocOutputEntity not found for id: " + id));
      }

  Optional<ParamDocOutputEntity> findByUuid(UUID uuid);
  Optional<ParamDocOutputEntity> findByTipoDocumentoAndEstado(String tipoDocumento, String estado);

}

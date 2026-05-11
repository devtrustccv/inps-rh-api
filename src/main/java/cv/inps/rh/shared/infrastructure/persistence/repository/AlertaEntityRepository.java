package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AlertaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AlertaEntityRepository extends
    JpaRepository<AlertaEntity, Long>,
    JpaSpecificationExecutor<AlertaEntity>
{

      default AlertaEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound( "AlertaEntity not found for id: " + id));
  }

  Optional<AlertaEntity> findByUuid(UUID uuid);

  boolean existsByReferenciaIdAndTipoAlerta(Long referenciaId, String tipoAlerta);

}

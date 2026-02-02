package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.DispensaEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface DispensaEntityRepository extends
    JpaRepository<DispensaEntity, Long>,
    JpaSpecificationExecutor<DispensaEntity>
{

      default DispensaEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"DispensaEntity not found for id: " + id));
      }

  Optional<DispensaEntity> findByUuid(UUID uuid);

  Optional<DispensaEntity> findByPedidoId_Uuid(UUID uuid);
}

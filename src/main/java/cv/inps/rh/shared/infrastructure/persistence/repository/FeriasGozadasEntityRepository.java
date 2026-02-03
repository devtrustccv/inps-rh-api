package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasGozadasEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface FeriasGozadasEntityRepository extends
    JpaRepository<FeriasGozadasEntity, Long>,
    JpaSpecificationExecutor<FeriasGozadasEntity>
{

      default FeriasGozadasEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"FeriasGozadasEntity not found for id: " + id));
      }

  Optional<FeriasGozadasEntity> findByPedidoId_Uuid(UUID uuid);
}

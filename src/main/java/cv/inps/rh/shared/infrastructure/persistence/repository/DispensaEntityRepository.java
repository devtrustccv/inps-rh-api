package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DispensaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

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

  java.util.List<DispensaEntity> findAllByPedidoId_FunId_UuidAndDataBetween(
      UUID funUuid,
      java.time.LocalDate dataInicio,
      java.time.LocalDate dataFim
  );
}

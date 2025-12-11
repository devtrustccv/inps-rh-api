package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EquipamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipamentoEntityRepository extends
    JpaRepository<EquipamentoEntity, Long>,
    JpaSpecificationExecutor<EquipamentoEntity> {

  default EquipamentoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "EquipamentoEntity not found for id: " + id));
  }

  List<EquipamentoEntity> findAllByIdLocalTrabalho_UuidAndEstado(UUID localId, Estado estado);

  Optional<EquipamentoEntity> findByUuid(UUID uuid);

  default EquipamentoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("EquipamentoEntity not found for id: " + uuid));
  }

}

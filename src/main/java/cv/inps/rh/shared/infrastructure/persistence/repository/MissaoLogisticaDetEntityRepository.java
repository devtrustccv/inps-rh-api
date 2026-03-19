package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MissaoLogisticaDetEntityRepository extends
    JpaRepository<MissaoLogisticaDetEntity, Long>,
    JpaSpecificationExecutor<MissaoLogisticaDetEntity> {

  List<MissaoLogisticaDetEntity> findAllByMissaoLogistId_MissaoServId_Uuid(UUID missaoUuid);

  default MissaoLogisticaDetEntity findByIdOrThrow(Long id) {
    return findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "MissaoLogisticaDetEntity not found for id: " + id));
  }
}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AbonosBeneficiosDetalheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface AbonosBeneficiosDetalheEntityRepository extends
    JpaRepository<AbonosBeneficiosDetalheEntity, Long>,
    JpaSpecificationExecutor<AbonosBeneficiosDetalheEntity> {

  default AbonosBeneficiosDetalheEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "AbonosBeneficiosDetalheEntity not found for id: " + id));
  }

  Optional<AbonosBeneficiosDetalheEntity> findByUuid(String uuid);

  default AbonosBeneficiosDetalheEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("AbonosBeneficiosDetalheEntity not found for uuid: " + uuid));
  }

  List<AbonosBeneficiosDetalheEntity> findByAbonoBenef_Uuid(UUID uuid);

}

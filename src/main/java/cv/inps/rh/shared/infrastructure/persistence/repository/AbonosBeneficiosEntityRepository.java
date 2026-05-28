package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AbonosBeneficiosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AbonosBeneficiosEntityRepository extends
    JpaRepository<AbonosBeneficiosEntity, Long>,
    JpaSpecificationExecutor<AbonosBeneficiosEntity>
{

    default AbonosBeneficiosEntity findByIdOrThrow(Long id) {
        return this.findById(id)
            .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "AbonosBeneficiosEntity not found for id: " + id));
    }

    Optional<AbonosBeneficiosEntity> findByUuid(UUID uuid);

    default AbonosBeneficiosEntity findByUuidOrThrow(UUID uuid) {
        return findByUuid(uuid)
            .orElseThrow(() -> IgrpResponseStatusException.notFound("AbonosBeneficiosEntity not found for uuid: " + uuid));
    }

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AnoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeriasEntityRepository extends
        JpaRepository<FeriasEntity, Long>,
        JpaSpecificationExecutor<FeriasEntity> {

    default FeriasEntity findByIdOrThrow(Long id) {
        return this.findById(id)
                .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
                        "FeriasEntity not found for id: " + id));
    }

  Optional<FeriasEntity> findByFunId_UuidAndAnoId(UUID funIdUuid, AnoEntity anoId);

    @Query("SELECT SUM(f.numDia) FROM FeriasEntity f WHERE f.funId.uuid = :funcionarioId")
    Integer sumNumDiaByFuncionarioId(@Param("funcionarioId") UUID funcionarioId);

}

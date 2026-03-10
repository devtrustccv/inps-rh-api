package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoCompetenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoCompetenciaEntityRepository extends
        JpaRepository<AvaliacaoCompetenciaEntity, Long>,
        JpaSpecificationExecutor<AvaliacaoCompetenciaEntity> {

    Optional<AvaliacaoCompetenciaEntity> findByUuid(UUID uuid);
}

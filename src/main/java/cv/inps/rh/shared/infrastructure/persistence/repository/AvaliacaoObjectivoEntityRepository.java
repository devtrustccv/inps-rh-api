package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoObjectivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoObjectivoEntityRepository extends
        JpaRepository<AvaliacaoObjectivoEntity, Long>,
        JpaSpecificationExecutor<AvaliacaoObjectivoEntity> {

    Optional<AvaliacaoObjectivoEntity> findByUuid(UUID uuid);
}

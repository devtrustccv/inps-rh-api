package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.RhVMapaPessoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RhVMapaPessoalEntityRepository extends
    JpaRepository<RhVMapaPessoalEntity, Long>,
    JpaSpecificationExecutor<RhVMapaPessoalEntity> {
}

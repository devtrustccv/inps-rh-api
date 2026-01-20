package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ProcSalCcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface ProcSalCcEntityRepository extends
    JpaRepository<ProcSalCcEntity, Long>,
    JpaSpecificationExecutor<ProcSalCcEntity> {
}

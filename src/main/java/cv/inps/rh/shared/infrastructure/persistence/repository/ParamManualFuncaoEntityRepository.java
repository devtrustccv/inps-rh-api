package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ParamManualFuncaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParamManualFuncaoEntityRepository extends
        JpaRepository<ParamManualFuncaoEntity, Long>,
        JpaSpecificationExecutor<ParamManualFuncaoEntity> {

    Optional<ParamManualFuncaoEntity> findByUuid(UUID uuid);
}

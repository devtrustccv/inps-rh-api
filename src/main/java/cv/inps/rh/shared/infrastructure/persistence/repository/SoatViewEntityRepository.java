package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.SoatViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SoatViewEntityRepository extends
    JpaRepository<SoatViewEntity, Long>,
    JpaSpecificationExecutor<SoatViewEntity> {

  List<SoatViewEntity> findBySoatUuid(String uuid);

}

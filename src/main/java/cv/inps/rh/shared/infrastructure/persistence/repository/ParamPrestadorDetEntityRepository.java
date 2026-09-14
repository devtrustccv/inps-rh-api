package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ParamPrestadorDetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ParamPrestadorDetEntityRepository extends
    JpaRepository<ParamPrestadorDetEntity, Long>,
    JpaSpecificationExecutor<ParamPrestadorDetEntity> {

  List<ParamPrestadorDetEntity> findAllByParamPrestId_IdOrderByIdAsc(Long paramPrestId);

  List<ParamPrestadorDetEntity> findAllByParamPrestId_IdInAndEstado(Collection<Long> paramPrestIds, String estado);
}

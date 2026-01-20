package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ProcSalCcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProcSalCcEntityRepository extends
    JpaRepository<ProcSalCcEntity, Long>,
    JpaSpecificationExecutor<ProcSalCcEntity> {

  List<ProcSalCcEntity> findByProcSalIdAndTipo(Long procSalId, String tipo);

  List<ProcSalCcEntity> findByProcSalIdAndTipoAndShortDesc(Long procSalId, String tipo, String shortDesc);

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ProcSalCcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcSalCcEntityEntityRepository extends
    JpaRepository<ProcSalCcEntity, Long>,
    JpaSpecificationExecutor<ProcSalCcEntity> {

  @Query("""
      SELECT p
      FROM ProcSalCcEntity p
      WHERE (:procSalId IS NULL OR p.procSalId = :procSalId)
        AND (:tipo IS NULL OR p.tipo = :tipo)
        AND (:funId IS NULL OR p.funId = :funId)
      """)
  List<ProcSalCcEntity> findAllByFilters(
      @Param("procSalId") Long procSalId,
      @Param("tipo") String tipo,
      @Param("funId") Long funId
  );

}

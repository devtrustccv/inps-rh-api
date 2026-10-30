package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubsidioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubsidioEntityRepository extends
    JpaRepository<SubsidioEntity, Long>,
    JpaSpecificationExecutor<SubsidioEntity> {

  default SubsidioEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SubsidioEntity not found for id: " + id));
  }

  @Modifying
  @Query("""
          UPDATE SubsidioEntity s
          SET s.estado = :estado
          WHERE s.id = :subsidioId
      """)
  void updateEstadoById(@Param("subsidioId") Long subsidioId, @Param("estado") String estado);

}

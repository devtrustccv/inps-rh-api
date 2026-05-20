package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubsidioNatalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubsidioNatalEntityRepository extends
    JpaRepository<SubsidioNatalEntity, Long>,
    JpaSpecificationExecutor<SubsidioNatalEntity> {

  default SubsidioNatalEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SubsidioNatalEntity not found for id: " + id));
  }

  @Modifying
  @Query("""
          UPDATE SubsidioNatalEntity s
          SET s.estado = :estado
          WHERE s.id = :subsidioId
      """)
  void updateEstadoById(@Param("subsidioId") Long subsidioId, @Param("estado") String estado);

}

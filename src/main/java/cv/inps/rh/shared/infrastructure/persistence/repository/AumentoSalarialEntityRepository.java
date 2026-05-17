package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.AumentoRowDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AumentoSalarialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;


@Repository
public interface AumentoSalarialEntityRepository extends
    JpaRepository<AumentoSalarialEntity, Long>,
    JpaSpecificationExecutor<AumentoSalarialEntity> {

  default AumentoSalarialEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("AumentoSalarialEntity not found for id: " + id));
  }

  Optional<AumentoSalarialEntity> findByUuid(String uuid);

  @Query("""
      SELECT new cv.inps.rh.processamento.application.dto.AumentoRowDTO(
         a.descricao,
         a.motivo,
         a.dataReferente,
         a.percentagem,
         a.createdDate,
         a.uuid,
         a.estado
      )
      FROM AumentoSalarialEntity a
      WHERE (
            :startDate IS NULL
            OR a.dataReferente BETWEEN :startDate AND :endDate
        )
      """)
  Page<AumentoRowDTO> list(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      Pageable pageable
  );
}

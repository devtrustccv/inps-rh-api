package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.FosRowDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.XmlFosEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FosEntityRepository extends JpaRepository<XmlFosEntity, Long>, JpaSpecificationExecutor<XmlFosEntity> {

  default XmlFosEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("RhXmlFosEntity not found for id: " + id));
  }

  @Query("""
      SELECT new cv.inps.rh.processamento.application.dto.FosRowDTO(
          f.id,
          f.dtEntrega,
          f.tpEntrega,
          CASE
              WHEN f.tpEntrega = 'PRIME' THEN 'Primeira'
              WHEN f.tpEntrega = 'SUBST' THEN 'Substituição'
              WHEN f.tpEntrega = 'CORRE' THEN 'Correção'
              ELSE f.tpEntrega
          END,
          f.ano || f.mes,
          f.ttRemuneracao,
          f.ttContribCalc,
          f.obs,
          f.numDc
      )
      FROM XmlFosEntity f
      WHERE (:startDate IS NULL OR f.createdDate >= :startDate)
        AND (:endDate IS NULL OR f.createdDate <= :endDate)
      ORDER BY f.ano ASC, f.mes ASC
      """)
  Page<FosRowDTO> findFosProjected(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable
  );

}

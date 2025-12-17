package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.ProcessamentoSalarialDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoSalarialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface ProcessamentoSalarialEntityRepository extends
    JpaRepository<ProcessamentoSalarialEntity, Long>,
    JpaSpecificationExecutor<ProcessamentoSalarialEntity> {

  default ProcessamentoSalarialEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("ProcessamentoSalarialEntity not found for id: " + id));
  }

  @Query("""
      SELECT new cv.inps.rh.processamento.application.dto.ProcessamentoSalarialDTO(
         p.id,
         p.estado,
         null,
         p.ccId,
         null,
         p.obs,
         COUNT(f.id),
         p.cab1Id,
         SUM(f.totalRemuneracoes)
      )
      FROM ProcessamentoSalarialEntity p
             LEFT JOIN ProcessamentoFuncionarioEntity f
                       ON f.prsals.id = p.id
                      AND f.estado <> 'E'
      WHERE ((:estado IS NULL AND p.estado <> 'E') OR (:estado IS NOT NULL AND p.estado = :estado))
           AND (:startDate IS NULL OR p.dataDe >= :startDate)
           AND (:endDate IS NULL OR p.dataAte <= :endDate)
           AND (:directionId IS NULL OR p.ccId = :directionId)
           AND (:type IS NULL OR p.tipoProcessamento = :type)
      """)
    // TODO 07/12/2025 14:30 validate null fields and Centro custo table
  Page<ProcessamentoSalarialDTO> list(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("directionId") Long directionId,
      @Param("type") String type,
      @Param("estado") String estado,
      Pageable pageable
  );
}


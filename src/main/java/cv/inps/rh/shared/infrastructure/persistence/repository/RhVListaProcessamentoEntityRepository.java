package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.ProcessamentoSalarialDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVListaProcessamentoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RhVListaProcessamentoEntityRepository extends JpaRepository<RhVListaProcessamentoEntity, Long>, JpaSpecificationExecutor<RhVListaProcessamentoEntity> {

  @Query("""
      SELECT new cv.inps.rh.processamento.application.dto.ProcessamentoSalarialDTO(
         p.id,
         p.estado,
         p.mesReferencia,
         p.codigoCc,
         p.direcao,
         p.obs,
         p.quantidade,
         p.cabimento,
         p.total,
         p.tipoProcessamento
      )
      FROM RhVListaProcessamentoEntity p
      WHERE ((:estado IS NULL AND p.estado <> 'E') OR (:estado IS NOT NULL AND p.estado = :estado))
           AND (:startDate IS NULL OR p.dataDe >= :startDate)
           AND (:endDate IS NULL OR p.dataAte <= :endDate)
           AND (:directionId IS NULL OR p.codigoCc = :directionId)
           AND (:type IS NULL OR p.tipoProcessamento = :type)
      """)
  Page<ProcessamentoSalarialDTO> list(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("directionId") Long directionId,
      @Param("type") String type,
      @Param("estado") String estado,
      Pageable pageable
  );
}

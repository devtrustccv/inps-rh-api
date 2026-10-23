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
import java.util.List;

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
          WHERE (:ano IS NULL OR (CAST(f.ano AS Integer) = :ano AND CAST(f.mes AS Integer) = :mes))
            AND (:startDate IS NULL OR f.createdDate >= :startDate)
            AND (:endDate IS NULL OR f.createdDate < :endDate)
            AND (
                  f.tpEntrega <> 'PRIME'
                  OR NOT EXISTS (
                      SELECT 1
                      FROM XmlFosEntity f2
                      WHERE f2.ano = f.ano
                        AND f2.mes = f.mes
                        AND f2.tpEntrega = 'SUBST'
                  )
            )
          ORDER BY CAST(f.ano AS Integer) DESC, CAST(f.mes AS Integer) DESC, f.id DESC
      """)
  Page<FosRowDTO> getFos(
      @Param("ano") Integer ano,
      @Param("mes") Integer mes,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable
  );

  /**
   * FOS mais recente de um mês de referência (o MES pode estar gravado com ou sem zero à esquerda).
   */
  @Query("""
          SELECT f
          FROM XmlFosEntity f
          WHERE CAST(f.ano AS Integer) = :ano
            AND CAST(f.mes AS Integer) = :mes
          ORDER BY f.id DESC
      """)
  List<XmlFosEntity> findByMesReferencia(@Param("ano") Integer ano, @Param("mes") Integer mes);
}

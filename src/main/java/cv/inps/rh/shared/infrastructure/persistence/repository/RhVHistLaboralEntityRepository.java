package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.RhVHistLaboralEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public interface RhVHistLaboralEntityRepository extends
    JpaRepository<RhVHistLaboralEntity, Long>,
    JpaSpecificationExecutor<RhVHistLaboralEntity>
{

  @Query(value = """
      SELECT * FROM RH_V_HIST_LABORAL
      WHERE FUN_UUID = :funUuid
        AND TIPREL_ESTADO IN (:tiprelEstados)
        AND (:referencia IS NULL OR REFERENCIA = :referencia)
        AND (:tipoSituacao IS NULL OR LOWER(TIPO_SITUACAO_DESC) LIKE LOWER('%' || :tipoSituacao || '%'))
        AND (:situacaoLaboral IS NULL OR LOWER(SITUACAO_LABORAL_DESC) LIKE LOWER('%' || :situacaoLaboral || '%'))
        AND (:dataInicio IS NULL OR DATA_INICIO >= :dataInicio)
        AND (:dataFim IS NULL OR DATA_FIM <= :dataFim)
      ORDER BY DATA_INICIO DESC
      """, countQuery = """
      SELECT COUNT(*) FROM RH_V_HIST_LABORAL
      WHERE FUN_UUID = :funUuid
        AND TIPREL_ESTADO IN (:tiprelEstados)
        AND (:referencia IS NULL OR REFERENCIA = :referencia)
        AND (:tipoSituacao IS NULL OR LOWER(TIPO_SITUACAO_DESC) LIKE LOWER('%' || :tipoSituacao || '%'))
        AND (:situacaoLaboral IS NULL OR LOWER(SITUACAO_LABORAL_DESC) LIKE LOWER('%' || :situacaoLaboral || '%'))
        AND (:dataInicio IS NULL OR DATA_INICIO >= :dataInicio)
        AND (:dataFim IS NULL OR DATA_FIM <= :dataFim)
      """, nativeQuery = true)
  Page<RhVHistLaboralEntity> findByFunUuidWithFilters(
      @Param("funUuid") String funUuid,
      @Param("tiprelEstados") Collection<String> tiprelEstados,
      @Param("referencia") String referencia,
      @Param("tipoSituacao") String tipoSituacao,
      @Param("situacaoLaboral") String situacaoLaboral,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      Pageable pageable);

}

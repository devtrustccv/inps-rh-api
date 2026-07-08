package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.RhVMobilidadeEntity;
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
public interface RhVMobilidadeEntityRepository extends
    JpaRepository<RhVMobilidadeEntity, Long>,
    JpaSpecificationExecutor<RhVMobilidadeEntity>
{

  @Query(value = """
      SELECT * FROM RH_V_MOBILIDADE
      WHERE FUN_UUID = :funUuid
        AND ESTADO IN (:estados)
        AND (:tipoSituacao IS NULL OR TIPO_SITUACAO = :tipoSituacao)
        AND (:dataInicio IS NULL OR DATA_INICIO >= :dataInicio)
        AND (:dataFim IS NULL OR DATA_FIM <= :dataFim)
      ORDER BY DATA_INICIO DESC
      """, countQuery = """
      SELECT COUNT(*) FROM RH_V_MOBILIDADE
      WHERE FUN_UUID = :funUuid
        AND ESTADO IN (:estados)
        AND (:tipoSituacao IS NULL OR TIPO_SITUACAO = :tipoSituacao)
        AND (:dataInicio IS NULL OR DATA_INICIO >= :dataInicio)
        AND (:dataFim IS NULL OR DATA_FIM <= :dataFim)
      """, nativeQuery = true)
  Page<RhVMobilidadeEntity> findByFunUuidWithFilters(
      @Param("funUuid") String funUuid,
      @Param("estados") Collection<String> estados,
      @Param("tipoSituacao") String tipoSituacao,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      Pageable pageable);

}

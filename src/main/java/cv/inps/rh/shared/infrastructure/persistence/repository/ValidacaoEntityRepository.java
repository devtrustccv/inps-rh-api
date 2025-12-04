package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ValidacaoEntityRepository extends
    JpaRepository<ValidacaoEntity, Long>,
    JpaSpecificationExecutor<ValidacaoEntity> {

  default ValidacaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ValidacaoEntity not found for id: " + id));
  }

  @Query(value = """
      SELECT *
      FROM (
          SELECT
              v.*,
              ROW_NUMBER() OVER (ORDER BY v.id) AS rn
          FROM rh_t_validacao v
          LEFT JOIN rh_t_funcionarios f ON f.id = v.fun_id
          WHERE (:nomeColaborador IS NULL OR LOWER(f.nome) LIKE LOWER('%' || :nomeColaborador || '%'))
            AND (:tipoAccao IS NULL OR v.tipo_accao = :tipoAccao)
            AND (:referenciaName IS NULL OR v.referencia_name = :referenciaName)
            AND (:dataInicio IS NULL OR v.data_registo >= :dataInicio)
            AND (:dataFim IS NULL OR v.data_registo <= :dataFim)
      )
      WHERE rn BETWEEN :startRow AND :endRow
      ORDER BY rn
      """,
      nativeQuery = true)
  List<ValidacaoEntity> findAllWithFilters(
      @Param("nomeColaborador") String nomeColaborador,
      @Param("tipoAccao") String tipoAccao,
      @Param("referenciaName") String referenciaName,
      @Param("dataInicio") LocalDateTime dataInicio,
      @Param("dataFim") LocalDateTime dataFim,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );

  ValidacaoEntity findByTiprelIdAndEstadoAndReferenciaName(TiposRelacionamentoEntity relacionamento, Estado estado, String referenciaName);

  Optional<ValidacaoEntity> findByUuid(UUID uuid);

  default ValidacaoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.notFound("ValidacaoEntity not found for id: " + uuid));
  }

  boolean existsByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(UUID funIdUuid, Estado estado, String tipoAccao, String referenciaName);
  
}

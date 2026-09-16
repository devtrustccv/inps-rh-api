package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface DefinicaoRemuneracaoEntityRepository extends
    JpaRepository<DefinicaoRemuneracaoEntity, Long>,
    JpaSpecificationExecutor<DefinicaoRemuneracaoEntity> {

  default DefinicaoRemuneracaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "DefinicaoRemuneracaoEntity not found for id: " + id));
  }

  @Query(value = """
      SELECT * FROM (
          SELECT dr.*, ROWNUM rnum
          FROM (
              SELECT dr.*
              FROM rh_t_def_remuneracoes dr
              LEFT JOIN rh_t_funcionarios f ON f.id = dr.fun_id
              LEFT JOIN rh_tipo_movimentos tm ON tm.id = dr.tm_id
              WHERE tm.tipo = 'REM'
                        and f.uuid = :idFuncionario
                AND (:estado IS NULL OR dr.estado = :estado)
                AND (:dataInicio IS NULL OR dr.data_inicio >= :dataInicio)
                AND (:dataFim IS NULL OR dr.data_fim <= :dataFim)
              ORDER BY dr.data_inicio DESC
          ) dr
          WHERE ROWNUM <= :endRow
      )
      WHERE rnum >= :startRow
      """, nativeQuery = true)
  List<DefinicaoRemuneracaoEntity> findAllWithFilter(
      @Param("idFuncionario") String idFuncionario,
      @Param("estado") String estado,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );

  List<DefinicaoRemuneracaoEntity> findByFunIdAndEstadoAndDataFimIsNull(FuncionarioEntity fun, Estado estado);
  List<DefinicaoRemuneracaoEntity> findByFunIdAndEstado(FuncionarioEntity fun, Estado estado);


  Optional<DefinicaoRemuneracaoEntity> findByUuid(UUID uuid);

  default DefinicaoRemuneracaoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "DefinicaoRemuneracaoEntity not found for id: " + uuid));
  }

  List<DefinicaoRemuneracaoEntity> findByFunIdAndTmIdAndEstado(FuncionarioEntity funId, TipoMovimentoEntity tmId, Estado estado);


}

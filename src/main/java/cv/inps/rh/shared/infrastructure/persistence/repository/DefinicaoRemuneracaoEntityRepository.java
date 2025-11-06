package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface DefinicaoRemuneracaoEntityRepository extends
    JpaRepository<DefinicaoRemuneracaoEntity, Long>,
    JpaSpecificationExecutor<DefinicaoRemuneracaoEntity>
{

      default DefinicaoRemuneracaoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"DefinicaoRemuneracaoEntity not found for id: " + id));
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
      @Param("estado") String estado,
      @Param("dataInicio") java.sql.Date dataInicio,
      @Param("dataFim") java.sql.Date dataFim,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );

}

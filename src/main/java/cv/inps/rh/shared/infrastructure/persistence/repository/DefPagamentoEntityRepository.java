package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
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
public interface DefPagamentoEntityRepository extends
    JpaRepository<DefPagamentoEntity, Long>,
    JpaSpecificationExecutor<DefPagamentoEntity>
{

      default DefPagamentoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"DefPagamentoEntity not found for id: " + id));
      }

  @Query(value = """
        SELECT * FROM (
            SELECT dp.*, ROWNUM rnum
            FROM (
                SELECT dp.*
                FROM RH_T_DEF_PAGAMENTOS dp
                LEFT JOIN rh_t_funcionarios f ON f.id = dp.fun_id
                LEFT JOIN rh_tipo_movimentos tm ON tm.id = dp.tm_id
                LEFT JOIN RH_T_CONTRATO c ON c.id = dp.contrato_id
                WHERE tm.tipo = 'PAG'
                  AND (:estado IS NULL OR dp.estado = :estado)
                  AND (:dataInicio IS NULL OR dp.data_inicio >= :dataInicio)
                  AND (:dataFim IS NULL OR dp.data_fim <= :dataFim)
                ORDER BY dp.data_inicio DESC
            ) dp
            WHERE ROWNUM <= :endRow
        )
        WHERE rnum >= :startRow
        """, nativeQuery = true)
  List<DefPagamentoEntity> findAllWithFilter(
      @Param("estado") String estado,
      @Param("dataInicio") java.sql.Date dataInicio,
      @Param("dataFim") java.sql.Date dataFim,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );

}

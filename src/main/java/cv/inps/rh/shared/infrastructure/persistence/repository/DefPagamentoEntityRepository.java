package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
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
public interface DefPagamentoEntityRepository extends
    JpaRepository<DefPagamentoEntity, Long>,
    JpaSpecificationExecutor<DefPagamentoEntity> {

  default DefPagamentoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "DefPagamentoEntity not found for id: " + id));
  }

  @Query(value = """
      SELECT * FROM (
          SELECT dp.*, ROWNUM rnum
          FROM (
              SELECT dp.*
              FROM RH_T_DEF_PAGAMENTOS dp
              LEFT JOIN rh_t_funcionarios f ON f.id = dp.fun_id
              LEFT JOIN rh_tipo_movimentos tm ON tm.id = dp.tm_id
              WHERE tm.tipo = 'PAG'
                AND f.uuid = :idFuncionario
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
      @Param("idFuncionario") String idFuncionario,
      @Param("estado") String estado,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );

  List<DefPagamentoEntity> findByFunIdAndEstadoAndDataFimIsNull(FuncionarioEntity fun, Estado estado);
  List<DefPagamentoEntity> findByFunIdAndEstado(FuncionarioEntity fun, Estado estado);

  Optional<DefPagamentoEntity> findByUuid(UUID uuid);

  default DefPagamentoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.notFound("DefPagamentoEntity not found for id: " + uuid));
  }

}

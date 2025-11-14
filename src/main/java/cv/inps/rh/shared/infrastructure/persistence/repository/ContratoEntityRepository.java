package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamContratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ContratoEntityRepository extends
    JpaRepository<ContratoEntity, Long>,
    JpaSpecificationExecutor<ContratoEntity>
{

      default ContratoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ContratoEntity not found for id: " + id));
      }

  @Query(value = """
        SELECT * FROM (
            SELECT ce.*, ROWNUM rnum
            FROM (
                SELECT ce.*
                FROM rh_t_contrato ce
                LEFT JOIN rh_t_funcionarios fi ON fi.id = ce.fun_id
                LEFT JOIN rh_t_param_contrato tc ON tc.id = ce.tp_contrato_id
                LEFT JOIN rh_t_param_vinculo vi ON vi.id = ce.vinculo_id
                WHERE (:vinculo IS NULL OR ce.vinculo_id = :vinculo)
                ORDER BY ce.data_inicio DESC
            ) ce
            WHERE ROWNUM <= :endRow
        )
        WHERE rnum >= :startRow
        """, nativeQuery = true)
  List<ContratoEntity> findAllWithPagination(
      @Param("vinculo") Long vinculo,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );

  boolean existsByTpContratoId(ParamContratoEntity tipoContrato);

}

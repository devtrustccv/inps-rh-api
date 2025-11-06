package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;



@Repository
public interface RegimeTrabalhoEntityRepository extends
    JpaRepository<RegimeTrabalhoEntity, Long>,
    JpaSpecificationExecutor<RegimeTrabalhoEntity>
{

      default RegimeTrabalhoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"RegimeTrabalhoEntity not found for id: " + id));
      }

  @Query(value = """
        SELECT * FROM (
            SELECT rt.*, ROWNUM rnum
            FROM (
                SELECT rt.*
                FROM rh_t_regime_trab rt
                LEFT JOIN rh_t_funcionarios f ON f.id = rt.fun_id
                WHERE (:tipoRegime IS NULL OR rt.tipo_regime = :tipoRegime)
                  AND (:estado IS NULL OR rt.estado = :estado)
                ORDER BY rt.id ASC
            ) rt
            WHERE ROWNUM <= :endRow
        )
        WHERE rnum >= :startRow
        """, nativeQuery = true)
  List<RegimeTrabalhoEntity> findAllWithFilter(
      @Param("tipoRegime") String tipoRegime,
      @Param("estado") String estado,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );


}

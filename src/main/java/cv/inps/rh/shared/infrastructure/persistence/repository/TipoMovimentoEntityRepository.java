package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
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
public interface TipoMovimentoEntityRepository extends
    JpaRepository<TipoMovimentoEntity, Long>,
    JpaSpecificationExecutor<TipoMovimentoEntity>
{

      default TipoMovimentoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"TipoMovimentoEntity not found for id: " + id));
      }

  @Query(
      value = """
            SELECT * FROM (
              SELECT tm.*
              FROM RH_TIPO_MOVIMENTOS tm
              ORDER BY tm.ID
            )
            WHERE ROWNUM <= :limit
            """,
      nativeQuery = true
  )
  List<TipoMovimentoEntity> findLimited(@Param("limit") int limit);


  List<TipoMovimentoEntity> findAllByTipo(String tipo);

  Optional<TipoMovimentoEntity> findByShortDescAndAmbAplId(String shortDesc, Long ambApplId);

}

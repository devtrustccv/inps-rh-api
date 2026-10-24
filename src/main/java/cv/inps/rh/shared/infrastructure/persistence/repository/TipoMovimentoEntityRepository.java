package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

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

  List<TipoMovimentoEntity> findAllByTipoIn(List<String> tipos);

  List<TipoMovimentoEntity> findAllByTipoInAndEstadoAndAmbAplId(List<String> tipos, String estado, Long ambAplId);

  List<TipoMovimentoEntity> findAllByTipoInAndEstadoAndAmbAplIdAndShortDescNot(List<String> tipos, String estado, Long ambAplId, String shortDesc);

  Optional<TipoMovimentoEntity> findByShortDescAndAmbAplId(String shortDesc, Long ambApplId);

  /** Tipos de movimento cujo SHORT_DESC é um valor ativo do domínio indicado (RH_T_DOMAINS). */
  @Query("""
      SELECT tm FROM TipoMovimentoEntity tm
      WHERE tm.shortDesc IN (
        SELECT d.valor FROM DomainEntity d WHERE d.dominio = :dominio AND d.estado = cv.inps.rh.shared.application.constants.Estado.A
      )
      ORDER BY tm.descricao
      """)
  List<TipoMovimentoEntity> findAllByShortDescNoDominio(@Param("dominio") String dominio);

}

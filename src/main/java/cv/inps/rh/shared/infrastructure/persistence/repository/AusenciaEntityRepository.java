package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AusenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AusenciaEntityRepository extends
    JpaRepository<AusenciaEntity, Long>,
    JpaSpecificationExecutor<AusenciaEntity>
{

      default AusenciaEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"AusenciaEntity not found for id: " + id));
      }

      List<AusenciaEntity> findAllByReferenciaNameAndReferenciaId(String referenciaName, Long referenciaId);

      /**
       * O dia está coberto por uma ausência (licença, situação laboral) activa ou pendente?
       * Uma ausência rejeitada ou eliminada já não cobre o dia.
       */
      @org.springframework.data.jpa.repository.Query(value = """
          SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
            FROM RH_T_AUSENCIA a
           WHERE a.FUN_ID = :funId
             AND a.ESTADO IN ('A', 'P')
             AND :data BETWEEN TRUNC(a.DATA_INICIO) AND TRUNC(NVL(a.DATA_FIM, a.DATA_INICIO))
          """, nativeQuery = true)
      int contaAusenciaNoDia(@org.springframework.data.repository.query.Param("funId") Long funId,
                             @org.springframework.data.repository.query.Param("data") java.time.LocalDate data);

      default boolean existeAusenciaNoDia(Long funId, java.time.LocalDate data) {
          return contaAusenciaNoDia(funId, data) > 0;
      }

}

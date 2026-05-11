package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.VMapaFeriasDetalheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VMapaFeriasDetalheEntityRepository extends
    JpaRepository<VMapaFeriasDetalheEntity, Long>,
    JpaSpecificationExecutor<VMapaFeriasDetalheEntity>
{

      default VMapaFeriasDetalheEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"VMapaFeriasDetalheEntity not found for id: " + id));
      }


  @Query("""
        SELECT f
        FROM VMapaFeriasDetalheEntity f
        WHERE f.anoId = :ano
          AND f.direcaoId = :direcao
          AND f.dataInicioMapa IS NOT NULL
    """)
  List<VMapaFeriasDetalheEntity> findFeriasAgendadas(@Param("ano") Integer ano,
                                                    @Param("direcao") Long direcao);

  @Query("""
        SELECT f
        FROM VMapaFeriasDetalheEntity f
        WHERE f.anoId = :ano
          AND f.direcaoId = :direcao
          AND f.dataInicioMapa IS NULL
    """)
  List<VMapaFeriasDetalheEntity> findFeriasPorAgendar(@Param("ano") Integer ano,
                                                      @Param("direcao") Long direcao);

}

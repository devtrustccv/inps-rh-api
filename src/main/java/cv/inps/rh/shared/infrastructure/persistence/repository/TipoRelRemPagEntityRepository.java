package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoRelRemPagEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
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
public interface TipoRelRemPagEntityRepository extends
        JpaRepository<TipoRelRemPagEntity, Long>,
        JpaSpecificationExecutor<TipoRelRemPagEntity> {

    default TipoRelRemPagEntity findByIdOrThrow(Long id) {
        return this.findById(id)
                .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
                        "TipoRelRemPagEntity not found for id: " + id));
    }

    boolean existsByTiprelIdAndRemId(TiposRelacionamentoEntity tipRelId, DefinicaoRemuneracaoEntity remId);

    boolean existsByTiprelIdAndPagId(TiposRelacionamentoEntity tipRelId, DefPagamentoEntity pagId);

    List<TipoRelRemPagEntity> findByTiprelId_Id(Long tiprelId);

  @Query("""
    SELECT trrp
    FROM TipoRelRemPagEntity trrp
      LEFT JOIN trrp.remId rem
      LEFT JOIN trrp.pagId pag
    WHERE trrp.tiprelId.id = :tiprelId
      AND (
           (rem IS NOT NULL
             AND rem.estado = :estado
             /*AND (rem.dataFim IS NULL OR rem.dataFim >= CURRENT_DATE)*/
           )
        OR
           (pag IS NOT NULL
             AND pag.estado = :estado
             /*AND (pag.dataFim IS NULL OR pag.dataFim >= CURRENT_DATE)*/
           )
      )
""")
  List<TipoRelRemPagEntity> findByTiprelIdAndEstado(
      @Param("tiprelId") Long tiprelId,
      @Param("estado") Estado estado
  );





}

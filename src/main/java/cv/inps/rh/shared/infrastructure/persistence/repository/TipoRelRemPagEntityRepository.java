package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoRelRemPagEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query("""
        select trrp
        from TipoRelRemPagEntity trrp
          left join fetch trrp.remId rem
          left join fetch rem.tmId
          left join fetch trrp.pagId pag
          left join fetch pag.tmId
        where trrp.tiprelId.id = :tiprelId
        """)
    List<TipoRelRemPagEntity> findByTiprelId_Id(@Param("tiprelId") Long tiprelId);

  @Query("""
    SELECT trrp
    FROM TipoRelRemPagEntity trrp
      LEFT JOIN FETCH trrp.remId rem
      LEFT JOIN FETCH rem.tmId
      LEFT JOIN FETCH trrp.pagId pag
      LEFT JOIN FETCH pag.tmId
    WHERE trrp.tiprelId.id = :tiprelId
      AND (
           (rem IS NOT NULL AND rem.estado = :estado)
        OR (pag IS NOT NULL AND pag.estado = :estado)
      )
""")
  List<TipoRelRemPagEntity> findByTiprelIdAndEstado(
      @Param("tiprelId") Long tiprelId,
      @Param("estado") Estado estado
  );





}

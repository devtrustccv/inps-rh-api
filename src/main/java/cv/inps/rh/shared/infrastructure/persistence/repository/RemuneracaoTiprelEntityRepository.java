package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RemuneracaoTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RemuneracaoTiprelEntityRepository extends
    JpaRepository<RemuneracaoTiprelEntity, Long>,
    JpaSpecificationExecutor<RemuneracaoTiprelEntity>
{

      default RemuneracaoTiprelEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"RemuneracaoTiprelEntity not found for id: " + id));
      }

  List<RemuneracaoTiprelEntity> findByTiprelIdAndEstado(TiposRelacionamentoEntity relacionamento, Estado estado);

  @Query("""
        select r.remId
        from RemuneracaoTiprelEntity r
        where r.tiprelId.id = :tiprelId
    """)
  List<DefinicaoRemuneracaoEntity> findRenumeracoesByTiprelId(Long tiprelId);
}

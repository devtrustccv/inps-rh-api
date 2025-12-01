package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.PagTiprelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface PagTiprelEntityRepository extends
    JpaRepository<PagTiprelEntity, Long>,
    JpaSpecificationExecutor<PagTiprelEntity>
{

  default PagTiprelEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"PagTiprelEntity not found for id: " + id));
      }

  List<PagTiprelEntity> findByTiprelIdAndEstado(TiposRelacionamentoEntity tiprelId, Estado estado);

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.RemuneracaoTiprelEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface RemuneracaoTiprelEntityRepository extends
    JpaRepository<RemuneracaoTiprelEntity, Long>,
    JpaSpecificationExecutor<RemuneracaoTiprelEntity>
{

      default RemuneracaoTiprelEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"RemuneracaoTiprelEntity not found for id: " + id));
      }

}
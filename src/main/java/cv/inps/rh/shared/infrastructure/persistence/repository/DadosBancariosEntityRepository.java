package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;


@Repository
public interface DadosBancariosEntityRepository extends
    JpaRepository<DadosBancariosEntity, Long>,
    JpaSpecificationExecutor<DadosBancariosEntity>
{

      default DadosBancariosEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"DadosBancariosEntity not found for id: " + id));
      }

}

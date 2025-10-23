package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.Optional;



@Repository
public interface ProcessoDisciplinarEntityRepository extends
    JpaRepository<ProcessoDisciplinarEntity, Long>,
    JpaSpecificationExecutor<ProcessoDisciplinarEntity>
{

      default ProcessoDisciplinarEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ProcessoDisciplinarEntity not found for id: " + id));
      }

}
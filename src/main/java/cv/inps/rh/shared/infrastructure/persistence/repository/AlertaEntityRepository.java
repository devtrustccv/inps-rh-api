package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AlertaEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.Optional;



@Repository
public interface AlertaEntityRepository extends
    JpaRepository<AlertaEntity, Long>,
    JpaSpecificationExecutor<AlertaEntity>
{

      default AlertaEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"AlertaEntity not found for id: " + id));
      }

}
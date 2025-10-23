package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface FamiliarEntityRepository extends
    JpaRepository<FamiliarEntity, Long>,
    JpaSpecificationExecutor<FamiliarEntity>
{

      default FamiliarEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"FamiliarEntity not found for id: " + id));
      }

}
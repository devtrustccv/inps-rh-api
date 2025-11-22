package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface ProcessoDisciplinarEntityRepository extends
    JpaRepository<ProcessoDisciplinarEntity, Long>,
    JpaSpecificationExecutor<ProcessoDisciplinarEntity>
{

      default ProcessoDisciplinarEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ProcessoDisciplinarEntity not found for id: " + id));
      }

      List<ProcessoDisciplinarEntity> findByFunId_Uuid(UUID uuid);

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.VHoraExtraMensalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;


@Repository
public interface VHoraExtraMensalEntityRepository extends
    JpaRepository<VHoraExtraMensalEntity, Long>,
    JpaSpecificationExecutor<VHoraExtraMensalEntity>
{

      default VHoraExtraMensalEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"VHoraExtraMensalEntity not found for id: " + id));
      }

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoSalarialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessamentoSalarialEntityRepository extends
    JpaRepository<ProcessamentoSalarialEntity, Long>,
    JpaSpecificationExecutor<ProcessamentoSalarialEntity> {

  default ProcessamentoSalarialEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("ProcessamentoSalarialEntity not found for id: " + id));
  }

  List<ProcessamentoSalarialEntity> findAllByCcIdIn(List<Long> ccId);
}


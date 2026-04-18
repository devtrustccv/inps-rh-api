package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DetalheXmlFosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface DetalheXmlFosEntityRepository extends
    JpaRepository<DetalheXmlFosEntity, Long>,
    JpaSpecificationExecutor<DetalheXmlFosEntity> {

  default DetalheXmlFosEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("DetalheXmlFosEntity not found for id: " + id));
  }
}

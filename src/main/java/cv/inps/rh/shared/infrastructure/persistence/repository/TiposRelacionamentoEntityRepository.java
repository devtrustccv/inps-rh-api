package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;


@Repository
public interface TiposRelacionamentoEntityRepository extends
    JpaRepository<TiposRelacionamentoEntity, Long>,
    JpaSpecificationExecutor<TiposRelacionamentoEntity> {

  default TiposRelacionamentoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "TiposRelacionamentoEntity not found for id: " + id));
  }

  boolean existsByVinculoId(ParamVinculoEntity vinculoId);

}

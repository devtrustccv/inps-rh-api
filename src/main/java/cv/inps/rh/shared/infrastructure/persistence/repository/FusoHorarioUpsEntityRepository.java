package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FusoHorarioUpsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FusoHorarioUpsEntityRepository extends
    JpaRepository<FusoHorarioUpsEntity, Long>,
    JpaSpecificationExecutor<FusoHorarioUpsEntity> {

  default FusoHorarioUpsEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "FusoHorarioUpsEntity not found for id: " + id));
  }

  List<FusoHorarioUpsEntity> findByIdParametrizacao(Long idParametrizacao);

}

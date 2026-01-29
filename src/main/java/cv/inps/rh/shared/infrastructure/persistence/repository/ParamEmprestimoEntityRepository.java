package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEmprestimoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ParamEmprestimoEntityRepository extends
    JpaRepository<ParamEmprestimoEntity, Long>,
    JpaSpecificationExecutor<ParamEmprestimoEntity> {

  default ParamEmprestimoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamEmprestimoEntity not found for id: " + id));
  }

  Optional<ParamEmprestimoEntity> findByUuid(String uuid);

  default ParamEmprestimoEntity findByUuidOrThrow(String uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(
            HttpStatus.NOT_FOUND,
            "ParamEmprestimoEntity not found for uuid: " + uuid
        ));
  }

}

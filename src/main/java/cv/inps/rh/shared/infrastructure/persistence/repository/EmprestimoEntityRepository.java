package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EmprestimoEntityRepository extends
    JpaRepository<EmprestimoEntity, Long>,
    JpaSpecificationExecutor<EmprestimoEntity> {

  default EmprestimoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "EmprestimoEntity not found for id: " + id));
  }

  Optional<EmprestimoEntity> findByUuid(String uuid);

  default EmprestimoEntity findByUuidOrThrow(String uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(
            HttpStatus.NOT_FOUND,
            "EmprestimoEntity not found for uuid: " + uuid
        ));
  }

}

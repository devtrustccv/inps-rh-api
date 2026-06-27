package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EstabelecimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstabelecimentoEntityRepository extends
    JpaRepository<EstabelecimentoEntity, Long>,
    JpaSpecificationExecutor<EstabelecimentoEntity> {

  default EstabelecimentoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "EstabelecimentoEntity not found for id: " + id));
  }

  Optional<EstabelecimentoEntity> findByUuid(String uuid);

  default EstabelecimentoEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("EstabelecimentoEntity not found for uuid: " + uuid));
  }

  List<EstabelecimentoEntity> findByPais_Id(Long id);

}

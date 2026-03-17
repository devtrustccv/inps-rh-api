package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoRequisicaoEntityRepository extends
    JpaRepository<MissaoRequisicaoEntity, Long>,
    JpaSpecificationExecutor<MissaoRequisicaoEntity> {

  Optional<MissaoRequisicaoEntity> findByUuid(UUID uuid);

  default MissaoRequisicaoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MissaoRequisicaoEntity not found for id: " + uuid));
  }
}


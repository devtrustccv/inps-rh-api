package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoServicoEntityRepository extends
    JpaRepository<MissaoServicoEntity, Long>,
    JpaSpecificationExecutor<MissaoServicoEntity> {

  @Query("select max(m.nrMissao) from MissaoServicoEntity m")
  Long findMaxNrMissao();

  Optional<MissaoServicoEntity> findByUuid(UUID uuid);

  default MissaoServicoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "MissaoServicoEntity not found for id: " + uuid));
  }
}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface OrdemServicoEntityRepository extends
    JpaRepository<OrdemServicoEntity, Long>,
    JpaSpecificationExecutor<OrdemServicoEntity>
{

      default OrdemServicoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"OrdemServicoEntity not found for id: " + id));
      }

      List<OrdemServicoEntity> findByFunId_Uuid(UUID funcionarioUuid);

      Optional<OrdemServicoEntity> findByUuid(UUID uuid);

      default OrdemServicoEntity findByUuidOrThrow(UUID uuid) {
          return this.findByUuid(uuid)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "OrdemServicoEntity not found for uuid: " + uuid));
      }

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ResponsavelEntityRepository extends
    JpaRepository<ResponsavelEntity, Long>,
    JpaSpecificationExecutor<ResponsavelEntity> {

  default ResponsavelEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ResponsavelEntity not found for id: " + id));
  }

  List<ResponsavelEntity> findAllByInstitId_id(Long institutoId);

  List<ResponsavelEntity> findAllByInstitId_idAndSecaoId_uuid(Long institutoId, UUID seccaoUuid);

  Optional<ResponsavelEntity> findByFunId_Uuid(UUID funcionarioUuid);
}

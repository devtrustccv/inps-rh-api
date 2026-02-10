package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ImportacaoMovimentoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ImportacaoMovimentoEntityRepository extends
    JpaRepository<ImportacaoMovimentoEntity, Long>,
    JpaSpecificationExecutor<ImportacaoMovimentoEntity> {

  Page<ImportacaoMovimentoEntity> findByCreatedDateBetween(
      LocalDateTime startDate,
      LocalDateTime endDate,
      Pageable pageable
  );

  Optional<ImportacaoMovimentoEntity> findByUuid(UUID uuid);

  default ImportacaoMovimentoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("ImportacaoMovimentoEntity not found for id: " + uuid));
  }
}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoDetalheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoDetalheEntityRepository extends
    JpaRepository<AvaliacaoDetalheEntity, Long>,
    JpaSpecificationExecutor<AvaliacaoDetalheEntity> {

  Optional<AvaliacaoDetalheEntity> findByUuid(UUID uuid);

  /** O detalhe de um período concreto de uma avaliação. Há no máximo um. */
  Optional<AvaliacaoDetalheEntity> findByAvaliacao_IdAndPeriodicidade(Long avdId, String periodicidade);

  List<AvaliacaoDetalheEntity> findAllByAvaliacao_Id(Long avdId);

  List<AvaliacaoDetalheEntity> findAllByAvaliacao_IdIn(List<Long> avdIds);

}

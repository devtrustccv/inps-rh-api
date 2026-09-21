package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoPeriodicidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoPeriodicidadeEntityRepository extends
    JpaRepository<AvaliacaoPeriodicidadeEntity, Long>,
    JpaSpecificationExecutor<AvaliacaoPeriodicidadeEntity> {

  Optional<AvaliacaoPeriodicidadeEntity> findByUuid(UUID uuid);

  /** Todas as medições de um período — uma por linha de componente. */
  List<AvaliacaoPeriodicidadeEntity> findAllByAvaliacao_IdAndPeriodicidade(Long avdId, String periodicidade);

  List<AvaliacaoPeriodicidadeEntity> findAllByAvaliacao_Id(Long avdId);

  /**
   * A medição de uma componente concreta num período. A ligação é polimórfica
   * ({@code referencia} + {@code referenciaId}), por isso não há navegação por FK.
   */
  Optional<AvaliacaoPeriodicidadeEntity> findByAvaliacao_IdAndPeriodicidadeAndReferenciaAndReferenciaId(
      Long avdId, String periodicidade, String referencia, Long referenciaId);

}

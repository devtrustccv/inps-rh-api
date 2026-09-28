package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoPeriodicidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  /** As medições de um período em várias avaliações — um só select para os blocos de um ano. */
  List<AvaliacaoPeriodicidadeEntity> findAllByAvaliacao_IdInAndPeriodicidade(List<Long> avdIds, String periodicidade);

  /**
   * Os períodos já avaliados nos objectivos comuns de cada ano. Só existem linhas em
   * RH_T_AVD_PERIODICIDADE depois de uma avaliação, por isso são estes os filhos da lista
   * "Objectivos / Avaliação Comuns".
   */
  @Query("""
      SELECT DISTINCT a.ano AS ano, p.periodicidade AS periodicidade
        FROM AvaliacaoPeriodicidadeEntity p
        JOIN p.avaliacao a
       WHERE a.ano IN :anos
         AND a.funcionario IS NULL
         AND UPPER(a.abrangencia) IN ('INPS', 'DIRECAO')
         AND (a.estado IS NULL OR a.estado <> 'E')
         AND p.referencia = 'OBJECTIVO'
         AND p.tipoProcesso = 'AVALIACAO'
      """)
  List<AnoPeriodicidade> findPeriodosAvaliadosComuns(@Param("anos") List<Integer> anos);

  interface AnoPeriodicidade {
    Integer getAno();

    String getPeriodicidade();
  }

  /**
   * A medição de uma componente concreta num período. A ligação é polimórfica
   * ({@code referencia} + {@code referenciaId}), por isso não há navegação por FK.
   */
  Optional<AvaliacaoPeriodicidadeEntity> findByAvaliacao_IdAndPeriodicidadeAndReferenciaAndReferenciaId(
      Long avdId, String periodicidade, String referencia, Long referenciaId);

}

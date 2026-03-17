package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EvolucaoCarreiraEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EvolucaoCarreiraEntityRepository extends JpaRepository<EvolucaoCarreiraEntity, Long>, JpaSpecificationExecutor<EvolucaoCarreiraEntity> {

  Optional<EvolucaoCarreiraEntity> findByUuid(String uuid);

  default EvolucaoCarreiraEntity findByUuidOrThrow(String id) {
    return this.findByUuid(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("OrdemServicoEntity not found for id: " + id));
  }

  @Query("""
          SELECT e
          FROM EvolucaoCarreiraEntity e
          WHERE e.carreiraIdDe.id = :carreiraId
          ORDER BY e.dataReferente DESC
      """)
  List<EvolucaoCarreiraEntity> findUltimaEvolucao(
      @Param("carreiraId") Long carreiraId,
      Pageable pageable
  );

  boolean existsByCarreiraIdDeId(Long carreiraId);

}

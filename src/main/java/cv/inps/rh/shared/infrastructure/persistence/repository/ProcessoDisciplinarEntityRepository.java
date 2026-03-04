package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ProcessoDisciplinarEntityRepository extends
    JpaRepository<ProcessoDisciplinarEntity, Long>,
    JpaSpecificationExecutor<ProcessoDisciplinarEntity> {

  default ProcessoDisciplinarEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ProcessoDisciplinarEntity not found for id: " + id));
  }

  List<ProcessoDisciplinarEntity> findByFunId_UuidAndEstadoNot(UUID uuid, String estado);

  Optional<ProcessoDisciplinarEntity> findByUuid(UUID uuid);

  default ProcessoDisciplinarEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.notFound("ProcessoDisciplinarEntity not found for id: " + uuid));
  }

  @Query("""
          SELECT COUNT(p)
          FROM ProcessoDisciplinarEntity p
          WHERE p.funId.id = :funId
          AND p.dateInicPd <= :fim
          AND (p.dateFimPd IS NULL OR p.dateFimPd >= :inicio)
      """)
  Long existeCondenacaoPeriodo(
      @Param("funId") Long funId,
      @Param("inicio") LocalDate inicio,
      @Param("fim") LocalDate fim
  );

}

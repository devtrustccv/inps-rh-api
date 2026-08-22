package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@JaversSpringDataAuditable
public interface SituacaoLaboralEntityRepository extends
    JpaRepository<SituacaoLaboralEntity, Long>,
    JpaSpecificationExecutor<SituacaoLaboralEntity> {

  default SituacaoLaboralEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "SituacaoLaboralEntity not found for id: " + id));
  }

  Optional<SituacaoLaboralEntity> findByUuid(UUID uuid);

  default SituacaoLaboralEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "SituacaoLaboralEntity not found for id: " + uuid));
  }

  @Query("""
      SELECT s FROM SituacaoLaboralEntity s
      JOIN FETCH s.contrVinculoId c
      JOIN FETCH c.funId f
      WHERE s.tipoSituacao = :tipoSituacao
        AND s.estado = :estado
        AND s.dataFim BETWEEN :dataInicio AND :dataFim
      """)
  List<SituacaoLaboralEntity> findLicencasAExpirar(
      @Param("tipoSituacao") String tipoSituacao,
      @Param("estado") Estado estado,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim
  );

  @Query("""
      SELECT s FROM SituacaoLaboralEntity s
      JOIN FETCH s.contrVinculoId c
      JOIN FETCH c.funId f
      WHERE s.tipoSituacao = :tipoSituacao
        AND s.estado = :estado
        AND s.dataFim < :dataLimite
      """)
  List<SituacaoLaboralEntity> findLicencasExpiradas(
      @Param("tipoSituacao") String tipoSituacao,
      @Param("estado") Estado estado,
      @Param("dataLimite") LocalDate dataLimite
  );

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamAjudaCustoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParamAjudaCustoEntityRepository extends
    JpaRepository<ParamAjudaCustoEntity, Long>,
    JpaSpecificationExecutor<ParamAjudaCustoEntity> {

  Optional<ParamAjudaCustoEntity> findByUuid(String uuid);

  default ParamAjudaCustoEntity findByUuidOrThrow(String uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(
            HttpStatus.NOT_FOUND, "ParamAjudaCustoEntity not found for uuid: " + uuid));
  }

  /** Lista de configuração (ecrã): tudo menos os eliminados, por carreira e data de início. */
  @Query("""
      SELECT p FROM ParamAjudaCustoEntity p JOIN FETCH p.carrPccs c
       WHERE p.estado <> 'E'
       ORDER BY c.nome, p.dataInicio DESC
      """)
  List<ParamAjudaCustoEntity> findAllParaConfiguracao();

  /** Parametrizações activas das carreiras indicadas em vigor na data. */
  @Query("""
      SELECT p FROM ParamAjudaCustoEntity p
       WHERE p.estado = 'A'
         AND p.carrPccs.id IN :carreiras
         AND p.dataInicio <= :data
         AND (p.dataFim IS NULL OR p.dataFim >= :data)
      """)
  List<ParamAjudaCustoEntity> findEmVigor(@Param("carreiras") Collection<Long> carreiras, @Param("data") LocalDate data);
}

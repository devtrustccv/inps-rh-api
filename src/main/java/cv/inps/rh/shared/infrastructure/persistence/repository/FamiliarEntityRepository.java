package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
@JaversSpringDataAuditable
public interface FamiliarEntityRepository extends
    JpaRepository<FamiliarEntity, Long>,
    JpaSpecificationExecutor<FamiliarEntity>
{

      default FamiliarEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"FamiliarEntity not found for id: " + id));
      }


  @Query("""
  SELECT f
  FROM FamiliarEntity f
  WHERE f.funId.uuid = :uuid
    AND f.estado IN (:estados)
""")
  List<FamiliarEntity> findByFuncionarioIdAndEstados(
      UUID uuid,
      List<Estado> estados
  );

  List<FamiliarEntity> findByNumDocumentoAndEstadoIn(String numDocumento, List<Estado> estados);

}

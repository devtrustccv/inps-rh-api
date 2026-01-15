package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ExperienciaProfEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface ExperienciaProfEntityRepository extends
    JpaRepository<ExperienciaProfEntity, Long>,
    JpaSpecificationExecutor<ExperienciaProfEntity>
{

      default ExperienciaProfEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ExperienciaProfEntity not found for id: " + id));
      }

  @Query("""
  SELECT e
  FROM ExperienciaProfEntity e
  WHERE e.funId.uuid = :uuid
    AND e.estado IN (:estados)
""")
  List<ExperienciaProfEntity> findByFuncionarioIdAndEstados(
      UUID uuid,
      List<Estado> estados
  );


}

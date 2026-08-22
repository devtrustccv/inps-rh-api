package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity;
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
public interface DadosBancariosEntityRepository extends
    JpaRepository<DadosBancariosEntity, Long>,
    JpaSpecificationExecutor<DadosBancariosEntity>
{

      default DadosBancariosEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"DadosBancariosEntity not found for id: " + id));
      }

  @Query("""
  SELECT b
  FROM DadosBancariosEntity b
  WHERE b.funId.uuid = :uuid
    AND b.estado IN (:estados)
""")
  List<DadosBancariosEntity> findByFuncionarioIdAndEstados(
      UUID uuid,
      List<Estado> estados
  );


}

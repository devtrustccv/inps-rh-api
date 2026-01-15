package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FormacaoFeitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface FormacaoFeitaEntityRepository extends
    JpaRepository<FormacaoFeitaEntity, Long>,
    JpaSpecificationExecutor<FormacaoFeitaEntity>
{

      default FormacaoFeitaEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"FormacaoFeitaEntity not found for id: " + id));
      }

  @Query("""
  SELECT f
  FROM FormacaoFeitaEntity f
  WHERE f.funId.uuid = :uuid
    AND f.estado IN (:estados)
""")
  List<FormacaoFeitaEntity> findByFuncionarioIdAndEstados(
      UUID uuid,
      List<Estado> estados
  );


}

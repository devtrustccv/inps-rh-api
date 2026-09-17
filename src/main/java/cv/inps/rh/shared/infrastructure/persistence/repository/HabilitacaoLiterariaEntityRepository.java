package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface HabilitacaoLiterariaEntityRepository extends
    JpaRepository<HabilitacaoLiterariaEntity, Long>,
    JpaSpecificationExecutor<HabilitacaoLiterariaEntity>
{

      default HabilitacaoLiterariaEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"HabilitacaoLiterariaEntity not found for id: " + id));
      }

  @Query("""
  SELECT h
  FROM HabilitacaoLiterariaEntity h
  WHERE h.funId.uuid = :uuid
    AND h.estado IN (:estados)
""")
  List<HabilitacaoLiterariaEntity> findByFuncionarioIdAndEstados(
      UUID uuid,
      List<Estado> estados
  );



}

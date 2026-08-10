package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.configuracao.application.services.model.SectionData;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface SecaoEntityRepository extends
    JpaRepository<SecaoEntity, Long>,
    JpaSpecificationExecutor<SecaoEntity> {

  default SecaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SecaoEntity not found for id: " + id));
  }

  List<SecaoEntity> findAllByEstado(Estado estado);

  List<SecaoEntity> findAllByEstadoAndInstId_Id(Estado estado, Long institutoId);

  Optional<SecaoEntity> findByUuid(UUID uuid);

  default SecaoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SecaoEntity not found for id: " + uuid));
  }

  @Query(
      """
          SELECT NEW cv.inps.rh.configuracao.application.services.model.SectionData(
            s.uuid,
            s.nome,
            s.estado,
            s.instId.id,
            s.instId.nome,
            s.instId.estado
          )
          FROM SecaoEntity s
          WHERE s.estado = 'A' AND (:direcaoId IS NULL OR s.instId.id = :direcaoId)
          """
  )
  List<SectionData> getAllData(
      @Param("direcaoId") Long direcaoId
  );

}

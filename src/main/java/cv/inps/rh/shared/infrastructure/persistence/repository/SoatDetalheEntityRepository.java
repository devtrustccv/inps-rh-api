package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.application.dto.SoatDetalheDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SoatDetalheEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SoatDetalheEntityRepository extends
    JpaRepository<SoatDetalheEntity, Long>,
    JpaSpecificationExecutor<SoatDetalheEntity> {

  default SoatDetalheEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "SoatDetalheEntity not found for id: " + id));
  }

  Optional<SoatDetalheEntity> findByUuid(String uuid);

  default SoatDetalheEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SoatDetalheEntity not found for uuid: " + uuid));
  }

  @Query("""
      SELECT NEW cv.inps.rh.processamento.application.dto.SoatDetalheDTO(
            s.uuid,
            s.soat.uuid,
            s.fun.uuid,
            s.prRem.id,
            s.dirServId,
            s.nuTrabAuto,
            s.nuTrabMan,
            s.vlRemunAuto,
            s.vlRemunMan,
            s.obs,
            s.estado
      )
      FROM SoatDetalheEntity s
      WHERE s.soat.uuid = :soatUuid
      """
  )
  Page<SoatDetalheDTO> findAllBySoatId(
      @Param("soatUuid") String soatUuid,
      Pageable page
  );
}

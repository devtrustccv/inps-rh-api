package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.processamento.domain.service.model.SoatAggregateDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SoatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SoatEntityRepository extends
    JpaRepository<SoatEntity, Long>,
    JpaSpecificationExecutor<SoatEntity> {

  default SoatEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "SoatEntity not found for id: " + id));
  }

  Optional<SoatEntity> findByUuid(String uuid);

  default SoatEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("SoatEntity not found for uuid: " + uuid));
  }

  @Query("""
      select s
      from SoatEntity s
      where (:anoReferente is null or s.anoReferente = :anoReferente)
        and (:mesReferente is null or s.mesReferente = :mesReferente)
      order by s.createdDate desc
      """)
  Page<SoatEntity> findSoatPage(
      @Param("anoReferente") Integer anoReferente,
      @Param("mesReferente") Integer mesReferente,
      Pageable pageable);

  @Query("""
      select new cv.inps.rh.processamento.domain.service.model.SoatAggregateDTO(
          s.id,
          coalesce(sum(d.vlRemunMan), 0),
          count(distinct d.fun.id)
      )
      from SoatEntity s
      left join SoatDetalheEntity d on d.soat = s
      where s.id in :ids
      group by s.id
      """)
  List<SoatAggregateDTO> findAgregadosByIds(@Param("ids") List<Long> ids);
}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.parametrizacao.application.dto.EstabelecimentoComboDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EstabelecimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstabelecimentoEntityRepository extends
    JpaRepository<EstabelecimentoEntity, Long>,
    JpaSpecificationExecutor<EstabelecimentoEntity> {

  default EstabelecimentoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "EstabelecimentoEntity not found for id: " + id));
  }

  Optional<EstabelecimentoEntity> findByUuid(String uuid);

  default EstabelecimentoEntity findByUuidOrThrow(String uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("EstabelecimentoEntity not found for uuid: " + uuid));
  }

  @Query("""
      SELECT new cv.inps.rh.parametrizacao.application.dto.EstabelecimentoComboDTO(
            e.pais.id,
            e.pais.nome,
            e.nome,
            e.id,
            e.uuid
      )
      FROM EstabelecimentoEntity e
      WHERE :countryIds IS NULL OR e.pais.id IN (:countryIds) AND e.estado = 'A'
      """)
  List<EstabelecimentoComboDTO> findByPaisId(@Param("countryIds") List<Long> paisId);

  @Query("""
      SELECT e
      FROM EstabelecimentoEntity e
      WHERE :countryIds IS NULL OR e.pais.id IN (:countryIds) AND e.estado = 'A'
      """)
  List<EstabelecimentoEntity> findEntityByPaisId(@Param("countryIds") List<Long> paisId);

}

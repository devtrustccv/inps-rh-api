package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamSituacaoEntityRepository extends JpaRepository<ParamSituacaoEntity, Long>, JpaSpecificationExecutor<ParamSituacaoEntity> {

  default ParamSituacaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamSitLaboralEntity not found for id: " + id));
  }

  List<ParamSituacaoEntity> findAllByEstado(Estado estado);

  Optional<ParamSituacaoEntity> findByUuid(UUID uuid);

  default ParamSituacaoEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamSitLaboralEntity not found for id: " + uuid));
  }

  List<ParamSituacaoEntity> findAllByNome(final String nome);

  Optional<ParamSituacaoEntity> findByCodigo(final String codigo);

}

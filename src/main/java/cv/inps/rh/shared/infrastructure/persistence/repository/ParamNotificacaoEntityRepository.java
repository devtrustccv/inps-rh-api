package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamNotificacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamNotificacaoEntityRepository extends
    JpaRepository<ParamNotificacaoEntity, Long>,
    JpaSpecificationExecutor<ParamNotificacaoEntity> {

  default ParamNotificacaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ParamNotificacaoEntity not found for id: " + id));
  }

  Optional<ParamNotificacaoEntity> findByUuid(UUID uuid);

  Optional<ParamNotificacaoEntity> findByTipoNotificacao(String tipoNotificacao);

  default ParamNotificacaoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("ParamNotificacaoEntity não encontrada para id: " + uuid));
  }

}

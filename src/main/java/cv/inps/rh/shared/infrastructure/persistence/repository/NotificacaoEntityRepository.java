package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AlertaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificacaoEntityRepository extends
    JpaRepository<NotificacaoEntity, Long>,
    JpaSpecificationExecutor<NotificacaoEntity> {

  default NotificacaoEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(
            () -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "NotificacaoEntity not found for id: " + id));
  }

  Optional<NotificacaoEntity> findByReferenciaNameAndReferenciaId(String referenciaName, Long referenciaId);

  Optional<NotificacaoEntity> findByReferenciaNameAndReferenciaUuid(String referenciaName, UUID referenciaUuid);

  List<NotificacaoEntity> findAllByReferenciaNameAndReferenciaUuid(String referenciaName, UUID referenciaUuid);

  List<NotificacaoEntity> findByAlertaId(AlertaEntity alertaId);

  Optional<NotificacaoEntity> findByUuid(UUID uuid);
}

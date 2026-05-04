package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoHistoricoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ContratoHistoricoEntityRepository extends
    JpaRepository<ContratoHistoricoEntity, Long>,
    JpaSpecificationExecutor<ContratoHistoricoEntity>
{

      default ContratoHistoricoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ContratoHistoricoEntity not found for id: " + id));
      }

  Optional<ContratoHistoricoEntity> findByUuid(UUID uuid);

  List<ContratoHistoricoEntity> findByContratoId_IdOrderByVersaoDesc(Long contratoId);

  Optional<ContratoHistoricoEntity> findTopByContratoId_IdOrderByVersaoDesc(Long contratoId);

  Optional<ContratoHistoricoEntity> findFirstByContratoId_IdAndEstadoOrderByVersaoDesc(Long contratoId, Estado estado);

}

package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoHistoricoEntity;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@JaversSpringDataAuditable
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

  List<ContratoHistoricoEntity> findByContratoId_FunId_IdAndEstActAdm(Long funId, Integer estActAdm);

  Optional<ContratoHistoricoEntity> findFirstByContratoId_IdAndEstActAdmOrderByVersaoDesc(Long contratoId, Integer estActAdm);

  List<ContratoHistoricoEntity> findByContratoId_IdInOrderByContratoId_IdAscVersaoDesc(Collection<Long> contratoIds);

}

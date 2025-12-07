package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoDetalheEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ParamSituacaoDetalheEntityRepository extends
    JpaRepository<ParamSituacaoDetalheEntity, Long>,
    JpaSpecificationExecutor<ParamSituacaoDetalheEntity>
{

      default ParamSituacaoDetalheEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ParamSituacaoDetalheEntity not found for id: " + id));
      }

  List<ParamSituacaoDetalheEntity> findAllBySituacaoLaboralId_IdAndEstado(Long situacaoLaboralIdId, Estado estado);

}

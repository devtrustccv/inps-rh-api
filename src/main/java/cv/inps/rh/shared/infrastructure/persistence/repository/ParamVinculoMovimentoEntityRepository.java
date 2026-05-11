package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoMovimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ParamVinculoMovimentoEntityRepository extends
    JpaRepository<ParamVinculoMovimentoEntity, Long>,
    JpaSpecificationExecutor<ParamVinculoMovimentoEntity>
{

      default ParamVinculoMovimentoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ParamVinculoMovimentoEntity not found for id: " + id));
      }

      List<ParamVinculoMovimentoEntity> findByVinculoId_IdAndTipo(Long vinculoId, String tipo);

  //Optional<ParamVinculoMovimentoEntity> findByVinculoId_IdAndTipo(Long vinculoId, String tipo);


}

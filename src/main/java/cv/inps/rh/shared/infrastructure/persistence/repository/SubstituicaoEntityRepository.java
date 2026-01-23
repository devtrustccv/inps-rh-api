package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface SubstituicaoEntityRepository extends
    JpaRepository<SubstituicaoEntity, Long>,
    JpaSpecificationExecutor<SubstituicaoEntity>
{

      default SubstituicaoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"SubstituicaoEntity not found for id: " + id));
      }

    Optional<SubstituicaoEntity> findByUuid(UUID idSusbtituicao);

  Page<SubstituicaoEntity> findBySubstituidoTiprelId_FunId_Uuid(UUID funUUid, Pageable pageable);

  Page<SubstituicaoEntity> findBySubstituidoTiprelId_FunId_Uuid_AndEstadoIn(
      UUID substituidoTiprelIdFunIdUuid,
      List<Estado> estados,
      Pageable pageable
  );


}

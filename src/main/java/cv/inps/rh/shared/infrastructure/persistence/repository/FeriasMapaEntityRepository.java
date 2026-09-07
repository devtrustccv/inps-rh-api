package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasMapaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;


@Repository
public interface FeriasMapaEntityRepository extends
    JpaRepository<FeriasMapaEntity, Long>,
    JpaSpecificationExecutor<FeriasMapaEntity>
{

      default FeriasMapaEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"FeriasMapaEntity not found for id: " + id));
      }

      /**
       * Mapas activos de um colaborador num ano. Usado na importação para inactivar o mapa anterior
       * antes de gravar o novo — regra da spec: importar um mapa que já existe inactiva o outro.
       */
      java.util.List<FeriasMapaEntity> findByFunId_IdAndAnoId_IdAndEstado(
          Long funId, Long anoId, Estado estado);

}

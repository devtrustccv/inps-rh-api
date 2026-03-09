package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface CarreiraEntityRepository extends
    JpaRepository<CarreiraEntity, Long>,
    JpaSpecificationExecutor<CarreiraEntity> {

  default CarreiraEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "CarreiraEntity not found for id: " + id));
  }

  CarreiraEntity findByContrVinculoIdFunIdAndEstadoAndDataFimIsNull(FuncionarioEntity fun, Estado estado);

  boolean existsByContrVinculoIdFunIdAndEstado(FuncionarioEntity fun, Estado estado);

  Optional<CarreiraEntity> findByUuid(UUID uuid);

  default CarreiraEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.notFound("CarreiraEntity not found for id: " + uuid));
  }

  @Query("""
          SELECT c
          FROM CarreiraEntity c
          WHERE c.estado = cv.inps.rh.shared.application.constants.Estado.A
          AND c.dataInicio IS NOT NULL AND c.dataFim IS NULL AND c.contrVinculoId IS NOT NULL
      """)
  List<CarreiraEntity> findCarreirasAtivas();

}




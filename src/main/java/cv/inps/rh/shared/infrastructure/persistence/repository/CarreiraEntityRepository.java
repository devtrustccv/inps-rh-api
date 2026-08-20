package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@JaversSpringDataAuditable
public interface CarreiraEntityRepository extends
    JpaRepository<CarreiraEntity, Long>,
    JpaSpecificationExecutor<CarreiraEntity> {

  default CarreiraEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "CarreiraEntity not found for id: " + id));
  }

  CarreiraEntity findByContrVinculoIdFunIdAndEstadoAndDataFimIsNull(FuncionarioEntity fun, Estado estado);

  /** Carreira pendente do funcionário independentemente de DATA_FIM (a pendente pode ter data fim do contrato). */
  CarreiraEntity findByContrVinculoIdFunIdAndEstado(FuncionarioEntity fun, Estado estado);

  boolean existsByContrVinculoIdFunIdAndEstado(FuncionarioEntity fun, Estado estado);

  // Carreiras actualmente activas do funcionário (estado A e ainda em vigor: data fim nula).
  List<CarreiraEntity> findAllByContrVinculoIdFunIdAndEstadoAndDataFimIsNull(FuncionarioEntity fun, Estado estado);

  /**
   * Carreiras "em vigor" do funcionário: estado A e ainda não terminadas (DATA_FIM nula OU futura).
   * É o critério correto para "carreiras activas" — uma carreira activa pode ter DATA_FIM = fim do
   * contrato (futuro); só está fechada quando DATA_FIM já passou. Usado no guard das 2 carreiras.
   */
  @Query("""
          SELECT c FROM CarreiraEntity c
          WHERE c.contrVinculoId.funId = :fun
            AND c.estado = cv.inps.rh.shared.application.constants.Estado.A
            AND (c.dataFim IS NULL OR c.dataFim >= :hoje)
      """)
  List<CarreiraEntity> findEmVigorByFuncionario(FuncionarioEntity fun, LocalDate hoje);

  Optional<CarreiraEntity> findByUuid(UUID uuid);

  default CarreiraEntity findByUuidOrThrow(UUID uuid) {
    return this.findByUuid(uuid).orElseThrow(() -> IgrpResponseStatusException.notFound("CarreiraEntity not found for id: " + uuid));
  }

  @Query("""
          SELECT c
          FROM CarreiraEntity c
          WHERE c.estado = cv.inps.rh.shared.application.constants.Estado.A
          AND c.dataInicio IS NOT NULL AND c.dataFim IS NULL AND c.contrVinculoId IS NOT NULL
          ORDER BY
              CASE WHEN c.cargoId IS NULL THEN 1 ELSE 0 END,
              c.cargoId.id DESC
      """)
  List<CarreiraEntity> findCarreirasAtivas();

}




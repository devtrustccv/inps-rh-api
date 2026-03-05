package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FaltaEntityRepository extends
    JpaRepository<FaltaEntity, Long>,
    JpaSpecificationExecutor<FaltaEntity> {

  default FaltaEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "FaltaEntity not found for id: " + id));
  }

  Optional<FaltaEntity> findByUuid(UUID uuid);

  List<FaltaEntity> findAllByPedidoId(PedidoEntity pedidoId);

  @Query("""
          SELECT f
          FROM FaltaEntity f
          JOIN f.sinteseDiarioId s
          JOIN s.funcionarioId func
          WHERE func.uuid = :funcionarioUuid
            AND s.data BETWEEN :dataInicio AND :dataFim
          ORDER BY f.dataInicio
      """)
  List<FaltaEntity> findAllByFuncionarioAndPeriodo(
      @Param("funcionarioUuid") UUID funcionarioUuid,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim
  );

  boolean existsBySinteseDiarioId(AssiduidadeSinteseDiarioEntity sintese);

  @Query("""
          SELECT COUNT(f)
          FROM FaltaEntity f
          WHERE f.tiprelId.funId.id = :funId
          AND YEAR(f.dataInicio) = :ano
          AND f.estado = cv.inps.rh.shared.application.constants.Estado.A
      """)
  Long countFaltasPorAno(
      @Param("funId") Long funId,
      @Param("ano") Integer ano
  );
}

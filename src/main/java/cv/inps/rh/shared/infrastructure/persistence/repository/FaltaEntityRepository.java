package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;

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

}

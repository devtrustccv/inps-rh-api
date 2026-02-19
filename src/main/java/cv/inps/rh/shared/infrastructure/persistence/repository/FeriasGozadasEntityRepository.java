package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasGozadasEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeriasGozadasEntityRepository extends
        JpaRepository<FeriasGozadasEntity, Long>,
        JpaSpecificationExecutor<FeriasGozadasEntity> {

    default FeriasGozadasEntity findByIdOrThrow(Long id) {
        return this.findById(id)
                .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
                        "FeriasGozadasEntity not found for id: " + id));
    }

    Optional<FeriasGozadasEntity> findByPedidoId_Uuid(UUID uuid);

    @Query("SELECT COALESCE(SUM(fg.numDia), 0) FROM FeriasGozadasEntity fg" +
        " WHERE fg.funId = :funcionarioId AND fg.anoId = :anoId AND fg.estado = 'A'")
    Integer sumNumDiaByFuncionarioIdAndAno(@Param("funcionarioId") UUID funcionarioId, @Param("anoId") Long anoId);

    @Query("SELECT COALESCE(SUM(fg.numDia), 0) FROM FeriasGozadasEntity fg WHERE " +
        "fg.funId = :funcionarioId AND fg.estado = 'A'")
    Integer sumNumDiaByFuncionarioId(@Param("funcionarioId") UUID funcionarioId);
}

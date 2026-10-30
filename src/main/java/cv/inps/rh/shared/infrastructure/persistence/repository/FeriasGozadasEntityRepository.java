package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasGozadasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

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

    Optional<FeriasGozadasEntity> findFirstByPedidoId_UuidAndPedidoId_TipoPedidoOrderByIdAsc(UUID uuid, String tipoPedido);

    /**
     * Férias gozadas de um pedido de FÉRIAS. As deduções de uma justificação de falta também
     * geram férias gozadas (uma por dia) com o uuid do pedido da justificação; procurar só por
     * uuid dava NonUniqueResult (500) e abria no ecrã de férias um pedido que não é de férias.
     */
    default Optional<FeriasGozadasEntity> findPedidoFeria(UUID uuid) {
        return findFirstByPedidoId_UuidAndPedidoId_TipoPedidoOrderByIdAsc(uuid, "FERIA");
    }

    @Query("SELECT COALESCE(SUM(fg.numDia), 0) FROM FeriasGozadasEntity fg" +
        " WHERE fg.funId.uuid = :funcionarioId AND fg.anoId.id = :anoId AND fg.estado = 'A'")
    Integer sumNumDiaByFuncionarioIdAndAno(@Param("funcionarioId") UUID funcionarioId, @Param("anoId") Long anoId);

    @Query("SELECT COALESCE(SUM(fg.numDia), 0) FROM FeriasGozadasEntity fg WHERE " +
        "fg.funId.uuid = :funcionarioId AND fg.estado = 'A'")
    Integer sumNumDiaByFuncionarioId(@Param("funcionarioId") UUID funcionarioId);

    /**
     * Já há férias vivas (A, P ou C) do colaborador que tocam no intervalo? Inclui os dias de falta
     * deduzidos em férias, que também são dias de férias gozadas.
     */
    @Query("SELECT COUNT(fg) FROM FeriasGozadasEntity fg WHERE fg.funId.id = :funId"
        + " AND fg.estado IN (cv.inps.rh.shared.application.constants.Estado.A,"
        + " cv.inps.rh.shared.application.constants.Estado.P, cv.inps.rh.shared.application.constants.Estado.C)"
        + " AND fg.dataInicio <= :fim AND fg.dataFim >= :inicio")
    long countFeriasVivasNoIntervalo(@Param("funId") Long funId,
                                     @Param("inicio") java.time.LocalDate inicio,
                                     @Param("fim") java.time.LocalDate fim);

    /** Férias gozadas geradas por um dia concreto de um pedido de falta — usado pelo reverter. */
    java.util.List<FeriasGozadasEntity> findAllByPedidoId_IdAndDataInicio(
        Long pedidoId, java.time.LocalDate dataInicio);
}

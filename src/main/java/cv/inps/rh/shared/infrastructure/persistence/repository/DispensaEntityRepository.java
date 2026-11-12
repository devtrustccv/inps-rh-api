package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DispensaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface DispensaEntityRepository extends
    JpaRepository<DispensaEntity, Long>,
    JpaSpecificationExecutor<DispensaEntity>
{

      default DispensaEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"DispensaEntity not found for id: " + id));
      }

  Optional<DispensaEntity> findByUuid(UUID uuid);

  Optional<DispensaEntity> findByPedidoId_Uuid(UUID uuid);

  /**
   * Dispensas de um colaborador cujo INÍCIO cai no intervalo. Serve o saldo mensal de horas, que é
   * contado pelo mês em que a dispensa começa.
   */
  java.util.List<DispensaEntity> findAllByPedidoId_FunId_UuidAndDataInicioBetweenAndEstado(
      UUID funUuid,
      java.time.LocalDate dataInicio,
      java.time.LocalDate dataFim,
      cv.inps.rh.shared.application.constants.Estado estado
  );

  /**
   * Soma de TOTAL_HORA (minutos) das dispensas de um funcionário com início no intervalo e num dos
   * estados — a "Hora Direito" da Lista Pedido Dispensa (spec 20/10: SUM(RH_T_DISPENSA.TOTAL_HORA)).
   */
  @Query("select coalesce(sum(d.totalHora), 0) from DispensaEntity d "
      + "where d.tiprelId.funId.id = :funId and d.dataInicio between :inicio and :fim "
      + "and d.estado in :estados")
  Long somarTotalHoraNoPeriodo(
      @Param("funId") Long funId,
      @Param("inicio") java.time.LocalDate inicio,
      @Param("fim") java.time.LocalDate fim,
      @Param("estados") java.util.Collection<cv.inps.rh.shared.application.constants.Estado> estados);

  /** Dispensas geradas por um dia concreto de um pedido de falta — usado pelo reverter. */
  java.util.List<DispensaEntity> findAllByPedidoId_IdAndDataInicio(
      Long pedidoId, java.time.LocalDate dataInicio);
}

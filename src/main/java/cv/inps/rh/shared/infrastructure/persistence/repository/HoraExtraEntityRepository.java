package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.HoraExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface HoraExtraEntityRepository extends
    JpaRepository<HoraExtraEntity, Long>,
    JpaSpecificationExecutor<HoraExtraEntity>
{

      default HoraExtraEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"HoraExtraEntity not found for id: " + id));
      }

    Optional<HoraExtraEntity> findByUuid(UUID uuid);


  List<HoraExtraEntity> findAllByPedidoId_Uuid(UUID pedidoIdUuid);

  List<HoraExtraEntity> findAllByPedidoId_UuidAndEstado(UUID pedidoUuid, Estado estado);

  /** Soma das horas diárias das horas extra do funcionário que cobrem o dia, fora dos estados indicados. */
  @Query("""
      select coalesce(sum(h.horasDiarias), 0) from HoraExtraEntity h
       where h.tiprelId.funId.id = :funId
         and h.dataInicio <= :dia and h.dataFim >= :dia
         and h.estado not in :excluidos
      """)
  Long somaHorasDiariasNoDia(@Param("funId") Long funId, @Param("dia") LocalDate dia,
                             @Param("excluidos") Collection<Estado> excluidos);
}

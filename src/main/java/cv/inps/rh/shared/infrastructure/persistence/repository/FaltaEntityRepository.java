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

  List<FaltaEntity> findAllByPedidoIdOrderByDataInicioAsc(PedidoEntity pedidoId);

  /**
   * Dias de falta VIVOS do colaborador no período — a contagem que decide se o pedido vai a
   * despacho ("mais de 3 faltas no mês", spec 09/09 :493).
   *
   * <p>Conta por mês e não por pedido: com a contagem por pedido, registar 2 dias hoje e 2
   * amanhã nunca chegava a validação, enquanto registar os mesmos 4 de uma vez chegava — o
   * controlo dependia de o RH ter carregado no botão uma ou duas vezes.
   *
   * <p>Só faltas vivas ({@code A} pendente de nada, {@code P} à espera de despacho): uma falta
   * rejeitada ({@code I}) ou eliminada ({@code E}) não é falta e não deve pesar no limite.
   */
  @Query("""
          SELECT COUNT(f)
          FROM FaltaEntity f
          JOIN f.sinteseDiarioId s
          JOIN s.funcionarioId func
          WHERE func.uuid = :funcionarioUuid
            AND s.data BETWEEN :dataInicio AND :dataFim
            AND f.estado IN (cv.inps.rh.shared.application.constants.Estado.A,
                             cv.inps.rh.shared.application.constants.Estado.P)
      """)
  long countFaltasVivasNoPeriodo(
      @Param("funcionarioUuid") UUID funcionarioUuid,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim);

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

  /**
   * Já existe falta viva para este colaborador neste dia?
   *
   * <p>Verifica pelo <strong>dia</strong> e não pela síntese: um mesmo dia pode ter mais
   * do que uma síntese (importada e manual), e a verificação por síntese deixava passar
   * duas faltas para a mesma data — apesar de a mensagem prometer o contrário.
   *
   * <p>Faltas em {@code I} são ignoradas: uma justificação recusada não pode bloquear o
   * dia para sempre.
   */
  @Query("""
          SELECT COUNT(f) > 0
          FROM FaltaEntity f
          JOIN f.sinteseDiarioId s
          WHERE s.funcionarioId.id = :funcionarioId
            AND s.data = :data
            AND f.estado <> cv.inps.rh.shared.application.constants.Estado.I
      """)
  boolean existeFaltaVivaNoDia(
      @Param("funcionarioId") Long funcionarioId,
      @Param("data") LocalDate data);

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

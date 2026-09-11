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
   * O pedido já foi apanhado pelo processamento salarial? Basta uma falta para bloquear o pedido
   * inteiro (regra do utilizador, 10/09).
   *
   * <p>O critério é {@code RH_T_FALTA.DEF_REM_ID} preenchido, e já não "a definição foi paga numa
   * {@code RH_T_REMUNERACOES}" (decisão de 11/09). Quem preenche o {@code DEF_REM_ID} é o
   * procedimento do DBA, ao criar a definição de remuneração — e a partir daí o desconto é dele.
   * Esperar pelo fecho da folha deixava uma janela em que se eliminava a falta e a definição criada
   * pelo procedimento continuava viva, a descontar uma falta que já não existe.
   *
   * <p>Faltas já eliminadas ({@code E}) não contam. Uma falta coberta a 100% por férias ou
   * dispensa ({@code VALOR_DESCONTO = 0}) nunca recebe {@code DEF_REM_ID} e não tranca o pedido:
   * aí a folha não é tocada, só se consumiu saldo.
   */
  @Query(value = """
      SELECT COUNT(a.ID)
        FROM RH_T_FALTA a
       WHERE a.PEDIDO_ID = :pedidoId
         AND a.ESTADO <> 'E'
         AND a.DEF_REM_ID IS NOT NULL
      """, nativeQuery = true)
  long countFaltasNoProcessamento(@Param("pedidoId") Long pedidoId);

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
   * <p>Faltas em {@code I} e {@code E} sao ignoradas: uma justificacao recusada ou um pedido
   * eliminado nao podem bloquear o dia para sempre. O {@code E} faltava, e a consequencia era
   * visivel: depois de eliminar um pedido, os dias voltavam ao painel "por justificar" (essa
   * consulta ja exclui {@code E}) mas o justificar recusava-os com "Ja existe uma falta
   * associada a data" — o ecra prometia o que a escrita negava, e o caminho "eliminaste, faz
   * nova marcacao" ficava fechado a chave. Provado live a 11/09.
   *
   * <p>Nao abre porta a duplicados: {@code A} e {@code P} — os unicos estados que produzem
   * efeitos financeiros — continuam a bloquear.
   */
  @Query("""
          SELECT COUNT(f) > 0
          FROM FaltaEntity f
          JOIN f.sinteseDiarioId s
          WHERE s.funcionarioId.id = :funcionarioId
            AND s.data = :data
            AND f.estado NOT IN (cv.inps.rh.shared.application.constants.Estado.I,
                                 cv.inps.rh.shared.application.constants.Estado.E)
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

  /**
   * Dias já comprometidos em férias por faltas ainda <b>pendentes de despacho</b>.
   *
   * <p>Reserva de saldo sem criar linhas: a falta em {@code P} já diz, no
   * {@code FLG_DESCONTO_FALTA}, que tenciona deduzir em férias, por isso o saldo pode
   * descontá-la sem que seja preciso gravar {@code RH_T_FERIAS_GOZADAS} antecipadamente.
   * Assim o segundo pedido do mês vê o saldo que sobra de verdade, em vez de descobrir no
   * despacho que as férias já tinham sido gastas — e não há linhas em {@code P} para
   * desfazer quando o pedido é rejeitado: a falta passa a {@code I} e deixa de contar.
   *
   * <p>{@code pedidoIdExcluir} serve o momento do despacho: a falta que está a ser aprovada
   * ainda está em {@code P} e descontar-se-ia a si própria. Sem pedido a excluir, passar
   * {@code -1}.
   */
  @Query("""
          SELECT COUNT(f)
          FROM FaltaEntity f
          JOIN f.sinteseDiarioId s
          JOIN s.funcionarioId func
          WHERE func.uuid = :funcionarioUuid
            AND f.estado = cv.inps.rh.shared.application.constants.Estado.P
            AND f.flgDescontoFalta = 'FERIAS'
            AND f.pedidoId.id <> :pedidoIdExcluir
      """)
  long countDiasPendentesDeducaoFerias(
      @Param("funcionarioUuid") UUID funcionarioUuid,
      @Param("pedidoIdExcluir") Long pedidoIdExcluir);

  /** Igual a {@link #countDiasPendentesDeducaoFerias}, restrito ao ano do direito. */
  @Query("""
          SELECT COUNT(f)
          FROM FaltaEntity f
          JOIN f.sinteseDiarioId s
          JOIN s.funcionarioId func
          WHERE func.uuid = :funcionarioUuid
            AND f.estado = cv.inps.rh.shared.application.constants.Estado.P
            AND f.flgDescontoFalta = 'FERIAS'
            AND YEAR(f.dataInicio) = :ano
            AND f.pedidoId.id <> :pedidoIdExcluir
      """)
  long countDiasPendentesDeducaoFeriasNoAno(
      @Param("funcionarioUuid") UUID funcionarioUuid,
      @Param("ano") Integer ano,
      @Param("pedidoIdExcluir") Long pedidoIdExcluir);

  /**
   * Faltas pendentes que tencionam deduzir em dispensa no período — a reserva do saldo de
   * horas, pelo mesmo princípio das férias.
   *
   * <p>Devolve as faltas em vez da soma porque {@code HORAS_AUSENCIA} é
   * {@code INTERVAL DAY TO SECOND} e só é somável em Java ({@code TimeUtils}).
   */
  @Query("""
          SELECT f
          FROM FaltaEntity f
          JOIN f.sinteseDiarioId s
          JOIN s.funcionarioId func
          WHERE func.uuid = :funcionarioUuid
            AND f.estado = cv.inps.rh.shared.application.constants.Estado.P
            AND f.flgDescontoFalta = 'DISPENSA'
            AND s.data BETWEEN :dataInicio AND :dataFim
            AND f.pedidoId.id <> :pedidoIdExcluir
      """)
  List<FaltaEntity> findPendentesDeducaoDispensaNoPeriodo(
      @Param("funcionarioUuid") UUID funcionarioUuid,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      @Param("pedidoIdExcluir") Long pedidoIdExcluir);
}

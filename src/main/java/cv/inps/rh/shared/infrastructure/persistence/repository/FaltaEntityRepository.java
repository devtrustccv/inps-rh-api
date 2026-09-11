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
   * O pedido já foi processado em folha? Basta uma falta processada para bloquear o pedido
   * inteiro (regra do utilizador, 10/09).
   *
   * <p>Uma falta está processada quando o seu desconto ({@code RH_T_DEF_REMUNERACOES}) já foi
   * apanhado por uma remuneração efectiva ({@code RH_T_REMUNERACOES.REM_1_ID}). É o critério
   * exacto — olha para a linha concreta, não para o mês — e é o que impede editar ou eliminar
   * uma falta cujo dinheiro já saiu.
   *
   * <p>Só remunerações activas contam: uma remuneração anulada deixa de ser dinheiro pago e não
   * deve bloquear o pedido. Faltas já eliminadas ({@code E}) também não contam.
   *
   * <p>Nota: uma falta coberta a 100% por férias ou dispensa não tem {@code DEF_REM_ID} e não é
   * apanhada por esta query — de propósito. Aí a folha nunca foi tocada, só se consumiu saldo,
   * e devolver esse saldo não mexe em histórico de pagamentos.
   *
   * <p>Nativa: {@code RH_T_REMUNERACOES} está mapeada como {@code RhTRemuneracoe} mas sem o
   * {@code REM_1_ID}, e não tem repositório.
   */
  @Query(value = """
      SELECT COUNT(a.ID)
        FROM RH_T_FALTA a
        JOIN RH_T_DEF_REMUNERACOES b ON b.ID = a.DEF_REM_ID
        JOIN RH_T_REMUNERACOES c ON c.REM_1_ID = b.ID
       WHERE a.PEDIDO_ID = :pedidoId
         AND a.ESTADO <> 'E'
         AND c.ESTADO = 'A'
      """, nativeQuery = true)
  long countFaltasProcessadasEmFolha(@Param("pedidoId") Long pedidoId);

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

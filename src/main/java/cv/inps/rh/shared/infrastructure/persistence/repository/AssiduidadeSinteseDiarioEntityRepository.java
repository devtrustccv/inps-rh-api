package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssiduidadeSinteseDiarioEntityRepository extends
    JpaRepository<AssiduidadeSinteseDiarioEntity, Long>,
    JpaSpecificationExecutor<AssiduidadeSinteseDiarioEntity> {

  default AssiduidadeSinteseDiarioEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "AssiduidadeSinteseDiarioEntity not found for id: " + id));
  }

  List<AssiduidadeSinteseDiarioEntity> findAllByFuncionarioIdAndDataBetweenOrderByDataAsc(FuncionarioEntity funcionarioId, LocalDate dataAfter, LocalDate dataBefore);

  /**
   * Dias do mês que estão por justificar: são ausência e ainda não têm falta associada.
   *
   * <p>Nativa por causa do critério de ausência. {@code HORAS_AUSENCIA} é
   * {@code INTERVAL DAY(0) TO SECOND(0)} na base e está mapeado como {@code String} na
   * entidade, pelo que a comparação com zero só tem semântica de intervalo em SQL.
   *
   * <p>{@code FALTA = 1} sozinho não chega: a spec (09/09, :522) só o liga quando as horas
   * trabalhadas são zero, portanto uma ausência parcial — saiu três horas mais cedo — tem
   * {@code FALTA = 0} e continua a ser justificável.
   *
   * <p>Uma falta ELIMINADA não bloqueia o dia: depois de o pedido ser eliminado, o dia volta a
   * estar por justificar e tem de reaparecer no painel.
   *
   * <p>O filtro tem de viver aqui e não em memória: o relógio de ponto cria uma síntese por
   * cada dia trabalhado, logo trazer o mês inteiro para deitar quase tudo fora enchia o ecrã
   * de dias que não são falta nenhuma.
   */
  @Query(value = """
      SELECT s.*
        FROM RH_ASSIDUIDADE_SINTESE_DIARIA s
       WHERE s.FUNCIONARIO_ID = :funcionarioId
         AND s.DATA BETWEEN :inicio AND :fim
         AND (s.FALTA = 1 OR s.HORAS_AUSENCIA > INTERVAL '0' SECOND)
         AND NOT EXISTS (SELECT 1
                           FROM RH_T_FALTA f
                          WHERE f.SINTESE_DIARIO_ID = s.ID
                            AND f.ESTADO <> 'E')
       ORDER BY s.DATA
      """, nativeQuery = true)
  List<AssiduidadeSinteseDiarioEntity> findAusenciasPorJustificar(
      @Param("funcionarioId") Long funcionarioId,
      @Param("inicio") LocalDate inicio,
      @Param("fim") LocalDate fim);

  /**
   * Síntese de um colaborador num dia.
   *
   * <p>Devolve lista porque a tabela não tem constraint única em (FUNCIONARIO_ID, DATA)
   * e existem dias com mais do que uma — marcar uma falta criava sempre um registo novo,
   * mesmo que o relógio já tivesse importado esse dia.
   */
  List<AssiduidadeSinteseDiarioEntity> findAllByFuncionarioIdAndData(
      FuncionarioEntity funcionarioId, LocalDate data);
}

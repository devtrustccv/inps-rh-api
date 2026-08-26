package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamContratoEntity;
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
public interface ContratoEntityRepository extends
    JpaRepository<ContratoEntity, Long>,
    JpaSpecificationExecutor<ContratoEntity>
{

      default ContratoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"ContratoEntity not found for id: " + id));
      }

  @Query(value = """
        SELECT * FROM (
            SELECT ce.*, ROWNUM rnum
            FROM (
                SELECT ce.*
                FROM rh_t_contrato ce
                LEFT JOIN rh_t_funcionarios fi ON fi.id = ce.fun_id
                LEFT JOIN rh_t_param_contrato tc ON tc.id = ce.tp_contrato_id
                LEFT JOIN rh_t_param_vinculo vi ON vi.id = ce.vinculo_id
                WHERE (:vinculo IS NULL OR ce.vinculo_id = :vinculo)
                and fi.uuid = :funcionarioId
                ORDER BY ce.data_inicio DESC
            ) ce
            WHERE ROWNUM <= :endRow
        )
        WHERE rnum >= :startRow
        """, nativeQuery = true)
  List<ContratoEntity> findAllWithPagination(
      @Param("vinculo") Long vinculo,
      @Param("funcionarioId") String funcionarioId,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );

  boolean existsByTpContratoId(ParamContratoEntity tipoContrato);

  Optional<ContratoEntity> findByUuid(UUID uuid);

  boolean existsByFunIdAndEstado(FuncionarioEntity fun, Estado estado);

  /**
   * Existe um contrato "em vigor" do funcionário — estado A e ainda dentro do prazo (sem data de fim
   * ou com data de fim ainda não ultrapassada). Usado no guard do Novo Contrato (DOSSIÊ: "o botão só
   * fica visível caso NÃO exista um contrato ativo"): a alteração de um contrato em vigor é feita
   * pela Renovação de Contrato, não por um novo contrato.
   */
  @Query("""
      SELECT COUNT(c) > 0 FROM ContratoEntity c
      WHERE c.funId = :fun AND c.estado = :estado
        AND (c.dataFim IS NULL OR c.dataFim >= :hoje)
      """)
  boolean existeContratoEmVigor(@Param("fun") FuncionarioEntity fun,
                                @Param("estado") Estado estado,
                                @Param("hoje") LocalDate hoje);


  @Query("""
       select c
       from ContratoEntity c
       where c.funId.uuid = :funUuid
         and c.versao = 1
       """)
  ContratoEntity findPrimeiroContratoFuncionario(@Param("funUuid") UUID funUuid);

  ContratoEntity findTopByFunId_UuidOrderByVersaoDesc(UUID funUuid);

  /**
   * Último contrato do funcionário = o de maior id (o último criado). Usado pelo guard de ATIVAÇÃO
   * de contrato: só se pode reativar o último contrato — nunca um contrato antigo já superado por
   * outro. A versão não serve para isto (é sempre 1 por cadeia de contrato).
   */
  ContratoEntity findTopByFunId_UuidOrderByIdDesc(UUID funUuid);

  @Query("""
      SELECT c FROM ContratoEntity c
      JOIN FETCH c.tpContratoId tp
      JOIN FETCH c.funId f
      WHERE tp.flgRenovavel = 1
        AND c.estado = :estado
        AND c.dataFim BETWEEN :dataInicio AND :dataFim
      """)
  List<ContratoEntity> findRenovaveisProximosAoFim(
      @Param("estado") Estado estado,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim
  );

  @Query("""
      SELECT c FROM ContratoEntity c
      JOIN FETCH c.tpContratoId tp
      JOIN FETCH c.funId f
      WHERE c.estado = :estado
        AND c.contratoId IS NULL
        AND c.dataInicio BETWEEN :dataInicioMin AND :dataInicioMax
      """)
  List<ContratoEntity> findParaConversao(
      @Param("estado") Estado estado,
      @Param("dataInicioMin") LocalDate dataInicioMin,
      @Param("dataInicioMax") LocalDate dataInicioMax
  );

}

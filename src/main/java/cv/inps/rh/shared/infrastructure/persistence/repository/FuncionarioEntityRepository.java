package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.projections.FuncionarioList;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface FuncionarioEntityRepository extends
    JpaRepository<FuncionarioEntity, Long>,
    JpaSpecificationExecutor<FuncionarioEntity>
{

      default FuncionarioEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"FuncionarioEntity not found for id: " + id));
      }


  @Query(
      value = """
            SELECT * FROM (
                SELECT
                    F.ID AS id,
                    F.UUID AS uuid,
                    F.NOME AS nome,
                    CAR.NOME AS cargo,
                    C.DATA_INICIO AS dataInicio,
                    DIR.NOME AS direccao,
                    SEC.NOME AS seccao,
                    CC.NOME || '/' || CAT.NOME AS carreiraCategoria,
                    F.ESTADO AS estadoRegisto,
                    F.ESTADO_VALIDACAO AS estadoColaborador,
                    COUNT(*) OVER() AS total_count,
                    ROW_NUMBER() OVER(ORDER BY C.DATA_INICIO DESC) AS rn
                FROM rh_t_funcionarios F
                JOIN rh_t_tipos_relacionamento TR ON TR.fun_id = F.ID
                JOIN rh_t_contrato C ON C.ID = TR.contrato_id
                JOIN rh_t_param_cargo CAR ON CAR.ID = TR.cargo_id
                JOIN instituicoes DIR ON DIR.ID = TR.instit_id
                JOIN rh_t_secao SEC ON SEC.ID = TR.seccao_id
                JOIN rh_t_param_carreira CC ON CC.ID = TR.carr_pcc_id
                LEFT JOIN rh_t_param_categoria CAT ON CAT.ID = TR.categoria_id
                WHERE 1=1
                  AND (:nome IS NULL OR F.NOME LIKE '%' || :nome || '%')
                  AND (:direccaoId IS NULL OR DIR.ID = :direccaoId)
                  AND (:seccaoId IS NULL OR SEC.ID = :seccaoId)
                  AND (:vinculoId IS NULL OR TR.vinculo_id = :vinculoId)
                  AND (:estado IS NULL OR F.ESTADO_VALIDACAO = :estado)
                  AND (:dataInicio IS NULL OR C.DATA_INICIO >= :dataInicio)
                  AND (:dataFim IS NULL OR C.DATA_INICIO <= :dataFim)
            )
            WHERE rn BETWEEN :startRow AND :endRow
        """,
      nativeQuery = true
  )
  List<FuncionarioList> findFuncionariosWithFilters(
      @Param("nome") String nome,
      @Param("direccaoId") Long direcaoId,
      @Param("seccaoId") Long seccaoId,
      @Param("vinculoId") Long vinculoId,
      @Param("estado") String estado,
      @Param("dataInicio") LocalDateTime dataInicio,
      @Param("dataFim") LocalDateTime dataFim,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );


}




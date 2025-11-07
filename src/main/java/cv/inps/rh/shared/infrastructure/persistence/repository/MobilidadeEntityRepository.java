package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.projections.MobilidadeList;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface MobilidadeEntityRepository extends
    JpaRepository<MobilidadeEntity, Long>,
    JpaSpecificationExecutor<MobilidadeEntity> {

  default MobilidadeEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MobilidadeEntity not found for id: " + id));
  }


  @Query(value = """
      SELECT * FROM (
          SELECT
              M.ID AS id,
              F.ID AS idFuncionario,
              M.UUID AS uuid,
              F.UUID AS uuidFuncionario,
              CAR.NOME AS cargo,
              DIR.NOME AS direccao,
              SEC.NOME AS seccao,
              LOC.NOME AS localTrabalho,
              TO_CHAR(M.DATA_INICIO, 'YYYY-MM-DD') AS dataInicio,
              TO_CHAR(M.DATA_FIM, 'YYYY-MM-DD') AS dataFim,
              TR.FLG_PROCESSA AS processamento,
              COUNT(*) OVER() AS total_count,
              M.ESTADO AS estado,
              ROW_NUMBER() OVER(ORDER BY M.DATA_INICIO DESC) AS rn
          FROM RH_T_MOBILIDADE M
              JOIN RH_T_FUNCIONARIOS F ON F.ID = M.FUN_ID
              LEFT JOIN RH_T_TIPOS_RELACIONAMENTO TR ON TR.MOB_ID = M.ID
              LEFT JOIN INSTITUICOES DIR ON DIR.ID = TR.INSTIT_ID
              LEFT JOIN RH_T_SECAO SEC ON SEC.ID = TR.SECCAO_ID
              LEFT JOIN RH_T_PARAM_LOCAL_TRAB LOC ON LOC.ID = TR.LOC_TRAB_ID
              LEFT JOIN RH_T_PARAM_CARGO CAR ON CAR.ID = TR.CARGO_ID
          WHERE 1=1
              AND (:tipoMobilidade IS NULL OR M.TIPO_SITUACAO = :tipoMobilidade)
              AND (:dataInicio IS NULL OR M.DATA_INICIO >= :dataInicio)
              AND (:dataFim IS NULL OR M.DATA_FIM <= :dataFim)
      )
      WHERE rn BETWEEN :startRow AND :endRow
      """,
      nativeQuery = true)
  List<MobilidadeList> findAllMobilidades(
      @Param("tipoMobilidade") String tipoMobilidade,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      @Param("startRow") int startRow,
      @Param("endRow") int endRow
  );

  Optional<MobilidadeEntity> findByUuid(UUID uuid);

}



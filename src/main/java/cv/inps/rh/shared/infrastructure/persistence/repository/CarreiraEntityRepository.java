package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.projections.CarreiraList;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface CarreiraEntityRepository extends
    JpaRepository<CarreiraEntity, Long>,
    JpaSpecificationExecutor<CarreiraEntity>
{

      default CarreiraEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"CarreiraEntity not found for id: " + id));
      }


  @Query(value = """
        SELECT * FROM (
            SELECT
                C.ID AS id,
                C.UUID AS uuid,
                F.ID AS idFuncionario,
                F.UUID AS uuidFuncionario,
                C.TIPO_SITUACAO AS tipoCarreira,
                VIN.NOME AS vinculo,
                CARR.NOME AS carreira,
                CAR.NOME AS cargo,
                ESC.ESCALAO AS escalao,
                C.SALARIO AS salario,
                TR.SITUAC_LABORAL_ID AS situacaoLaboral,
                TR.DATA_INICIO AS dataInicio,
                TR.DATA_FIM AS dataFim,
                TR.FLG_PROCESSA AS processamento,
                C.ESTADO AS estado,
                COUNT(*) OVER() AS total_count,
                ROW_NUMBER() OVER(ORDER BY TR.DATA_INICIO DESC NULLS LAST) AS rn
            FROM RH_T_CARREIRA C
            JOIN RH_T_FUNCIONARIOS F ON F.ID = C.FUN_ID
            LEFT JOIN RH_T_TIPOS_RELACIONAMENTO TR ON TR.CARREIRA_ID = C.ID
            LEFT JOIN RH_T_PARAM_CARGO CAR ON CAR.ID = TR.CARGO_ID
            LEFT JOIN RH_T_PARAM_ESCALAO ESC ON ESC.ID = TR.ESCALAO_ID
            LEFT JOIN RH_T_PARAM_VINCULO VIN ON VIN.ID = TR.VINCULO_ID
            LEFT JOIN RH_T_PARAM_CARREIRA CARR ON CARR.ID = TR.CARR_PCC_ID
            WHERE (:tipoCarreira IS NULL OR C.TIPO_SITUACAO = :tipoCarreira)
              AND (:dataInicio IS NULL OR TR.DATA_INICIO >= :dataInicio)
              AND (:dataFim IS NULL OR TR.DATA_FIM <= :dataFim)
              AND F.UUID = :uuid
        ) tmp
        WHERE rn BETWEEN :startRow AND :endRow
        """, nativeQuery = true)
  List<CarreiraList> findAllCarreiras(
      @Param("tipoCarreira") String tipoCarreira,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      @Param("startRow") Long startRow,
      @Param("endRow") Long endRow,
      @Param("uuid")String idFuncionario
  );

  @Query(value = """
    SELECT * FROM (
        SELECT
            c.*,
            ROW_NUMBER() OVER(ORDER BY tr.DATA_INICIO DESC) AS rn,
            COUNT(*) OVER() AS total_count
        FROM RH_T_CARREIRA c
        JOIN RH_T_FUNCIONARIOS f ON f.ID = c.FUN_ID
        LEFT JOIN RH_T_TIPOS_RELACIONAMENTO tr ON tr.CARREIRA_ID = c.ID
        LEFT JOIN RH_T_PARAM_CARGO car ON car.ID = tr.CARGO_ID
        LEFT JOIN RH_T_PARAM_ESCALAO esc ON esc.ID = tr.ESCALAO_ID
        LEFT JOIN RH_T_PARAM_VINCULO vin ON vin.ID = tr.VINCULO_ID
        LEFT JOIN RH_T_PARAM_CARREIRA carr ON carr.ID = tr.CARR_PCC_ID
        WHERE (:tipoCarreira IS NULL OR c.TIPO_SITUACAO = :tipoCarreira)
          AND (:dataInicio IS NULL OR tr.DATA_INICIO >= :dataInicio)
          AND (:dataFim IS NULL OR tr.DATA_FIM <= :dataFim)
          AND f.UUID = :uuid
    ) tmp
    WHERE rn BETWEEN :startRow AND :endRow
    """, nativeQuery = true)
  List<CarreiraEntity> findCarreirasNative(
      @Param("tipoCarreira") String tipoCarreira,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      @Param("startRow") Long startRow,
      @Param("endRow") Long endRow,
      @Param("uuid") String idFuncionario
  );



  CarreiraEntity findByFunIdAndEstadoAndDataFimIsNull(FuncionarioEntity fun, Estado estado);

}




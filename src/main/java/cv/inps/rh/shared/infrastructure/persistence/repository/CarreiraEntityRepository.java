package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.funcionario.domain.projections.CarreiraList;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.data.domain.Pageable;
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
                RAWTOHEX(C.UUID) AS uuid,
                F.ID AS idFuncionario,
                RAWTOHEX(F.UUID) AS uuidFuncionario,
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
                ROW_NUMBER() OVER(ORDER BY TR.DATA_INICIO DESC) AS rn
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
        ) tmp
        WHERE rn BETWEEN :startRow AND :endRow
        """, nativeQuery = true)
  List<CarreiraList> findAllCarreiras(
      @Param("tipoCarreira") String tipoCarreira,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      @Param("startRow") Long startRow,
      @Param("endRow") Long endRow
  );


  }




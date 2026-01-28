package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.assiduidade.infrastructure.persistence.projections.AssiduidadeResumoViewRow;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Repository
public interface AssiduidadeSinteseDiarioEntityRepository extends
        JpaRepository<AssiduidadeSinteseDiarioEntity, Long>,
        JpaSpecificationExecutor<AssiduidadeSinteseDiarioEntity> {

    default AssiduidadeSinteseDiarioEntity findByIdOrThrow(Long id) {
        return this.findById(id)
                .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
                        "AssiduidadeSinteseDiarioEntity not found for id: " + id));
    }

  @Query(value = """
    SELECT
        f.uuid AS uuidFuncionairio,
        f.nome AS nomeColaborador,
        inst.nome AS direcao,
        s.mes AS mes,
        s.ano AS ano,

        COUNT(s.id) AS totalDias,

        SUM(CASE WHEN s.falta IS NOT NULL THEN 1 ELSE 0 END) AS totalFalta,

        SUM(
            EXTRACT(DAY    FROM s.horas_trabalhadas) * 86400 +
            EXTRACT(HOUR   FROM s.horas_trabalhadas) * 3600 +
            EXTRACT(MINUTE FROM s.horas_trabalhadas) * 60 +
            EXTRACT(SECOND FROM s.horas_trabalhadas)
        ) AS totalHorasTrabalhadas,

        SUM(
            EXTRACT(DAY    FROM s.horas_ausencia) * 86400 +
            EXTRACT(HOUR   FROM s.horas_ausencia) * 3600 +
            EXTRACT(MINUTE FROM s.horas_ausencia) * 60 +
            EXTRACT(SECOND FROM s.horas_ausencia)
        ) AS totalHorasAusentes,

        SUM(
            EXTRACT(DAY    FROM s.horas_extras) * 86400 +
            EXTRACT(HOUR   FROM s.horas_extras) * 3600 +
            EXTRACT(MINUTE FROM s.horas_extras) * 60 +
            EXTRACT(SECOND FROM s.horas_extras)
        ) AS totalHoraExtra,

        SUM(
            EXTRACT(DAY    FROM s.horas_almoco) * 86400 +
            EXTRACT(HOUR   FROM s.horas_almoco) * 3600 +
            EXTRACT(MINUTE FROM s.horas_almoco) * 60 +
            EXTRACT(SECOND FROM s.horas_almoco)
        ) AS totalHoraAlmoco



    FROM RH_ASSIDUIDADE_SINTESE_DIARIA s
    JOIN RH_T_FUNCIONARIOS f ON f.id = s.funcionario_id
    JOIN RH_T_TIPOS_RELACIONAMENTO tr ON tr.fun_id = f.id
    LEFT JOIN RH_T_MOBILIDADE m ON m.id = tr.mob_id
    LEFT JOIN INPSSIGOF.INSTITUICOES inst ON inst.id = m.instit_id
    LEFT JOIN RH_T_PARAM_LOCAL_TRAB plt ON plt.id = m.local_trab_id

    WHERE tr.est_act_adm = 1
      AND (:colaborador IS NULL OR LOWER(f.nome) LIKE LOWER('%' || :colaborador || '%'))
      AND (:dataInicio IS NULL OR s.data >= :dataInicio)
      AND (:dataFim IS NULL OR s.data <= :dataFim)
      AND (:estado IS NULL OR s.estado = :estado)
      AND (:direcao IS NULL OR inst.id = :direcao)
      AND (:seccao IS NULL OR m.secao_id = :seccao)
      AND (:ilha IS NULL OR plt.ilha_id = :ilha)

    GROUP BY
        f.uuid,
        f.nome,
        inst.nome,
        s.mes,
        s.ano

    ORDER BY
        s.ano DESC,
        s.mes DESC,
        f.nome ASC
    """,

      countQuery = """
        SELECT COUNT(*)
        FROM (
            SELECT f.uuid, s.mes, s.ano
            FROM RH_ASSIDUIDADE_SINTESE_DIARIA s
            JOIN RH_T_FUNCIONARIOS f ON f.id = s.funcionario_id
            JOIN RH_T_TIPOS_RELACIONAMENTO tr ON tr.fun_id = f.id
            LEFT JOIN RH_T_MOBILIDADE m ON m.id = tr.mob_id
            LEFT JOIN INPSSIGOF.INSTITUICOES inst ON inst.id = m.instit_id
            LEFT JOIN RH_T_PARAM_LOCAL_TRAB plt ON plt.id = m.local_trab_id
            WHERE tr.est_act_adm = 1
              AND (:colaborador IS NULL OR LOWER(f.nome) LIKE LOWER('%' || :colaborador || '%'))
              AND (:dataInicio IS NULL OR s.data >= :dataInicio)
              AND (:dataFim IS NULL OR s.data <= :dataFim)
              AND (:estado IS NULL OR s.estado = :estado)
              AND (:direcao IS NULL OR inst.id = :direcao)
              AND (:seccao IS NULL OR m.secao_id = :seccao)
              AND (:ilha IS NULL OR plt.ilha_id = :ilha)
            GROUP BY f.uuid, s.mes, s.ano
        )
    """,
      nativeQuery = true
  )
  Page<AssiduidadeResumoViewRow> listarResumoMensal(
      @Param("colaborador") String colaborador,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim,
      @Param("estado") String estado,
      @Param("direcao") Long direcao,
      @Param("seccao") Long seccao,
      @Param("ilha") Long ilha,
      Pageable pageable
  );


}

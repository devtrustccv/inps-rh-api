-- =====================================================================
-- RH_V_RESUMO_ASSIDUIDADE  (alterada)
--
-- 1. Acrescenta UPS (ID_UPS / NOME_UPS), exigida como filtro pela especificacao
--    na lista de Gestao de Falta. Caminho:
--      TIPREL.MOB_ID -> RH_T_MOBILIDADE.LOCAL_TRAB_ID
--                    -> RH_T_PARAM_LOCAL_TRAB.UPS_ID
--                    -> SIPSGLOBAL.GLB_T_UPS.NOME
--
-- 2. Alarga o dominio de ESTADO. Antes so produzia CONFORME | INJUSTIFICADA.
--    A especificacao pede tambem JUSTIFICADA e PENDENTE:
--
--      INJUSTIFICADA - ha dias com falta sem RH_T_FALTA e sem ausencia activa
--      PENDENTE      - as faltas do mes existem mas aguardam validacao (estado P)
--      JUSTIFICADA   - todas as faltas do mes estao justificadas e activas
--      CONFORME      - nao ha faltas no mes
--
--    A precedencia e essa: basta um dia por justificar para o mes ser
--    INJUSTIFICADA; havendo justificacoes pendentes, PENDENTE ganha a JUSTIFICADA.
-- =====================================================================

CREATE OR REPLACE VIEW RH_V_RESUMO_ASSIDUIDADE AS
WITH resumo AS (
    SELECT
        s.FUNCIONARIO_ID,
        f.UUID                 AS FUNCIONARIO_UUID,
        f.nome                 AS nome_funcionario,
        i.id                   AS id_direcao,
        i.nome                 AS nome_direcao,
        sec.id                 AS id_secao,
        sec.nome               AS nome_secao,
        il.id                  AS id_ilha,
        il.nome                AS nome_ilha,
        ups.id                 AS id_ups,
        ups.nome               AS nome_ups,
        EXTRACT(YEAR  FROM s.data)  AS ano,
        EXTRACT(MONTH FROM s.data)  AS mes,

        COUNT(DISTINCT s.data) AS total_dias,

        -- Faltas nao justificadas: sem RH_T_FALTA E sem ausencia activa nesse dia
        COUNT(
            CASE
                WHEN f2.id IS NULL AND aus.id IS NULL
                    THEN 1
            END
        ) AS total_faltas,

        -- Faltas ja registadas mas ainda por validar
        COUNT(CASE WHEN f2.ESTADO = 'P' THEN 1 END) AS total_faltas_pendentes,

        -- Faltas registadas e validadas
        COUNT(CASE WHEN f2.ESTADO = 'A' THEN 1 END) AS total_faltas_justificadas,

        SUM(
            EXTRACT(DAY FROM s.horas_trabalhadas) * 24 +
            EXTRACT(HOUR FROM s.horas_trabalhadas) +
            EXTRACT(MINUTE FROM s.horas_trabalhadas) / 60
        ) AS horas_trabalhadas,

        SUM(
            EXTRACT(DAY FROM s.horas_almoco) * 24 +
            EXTRACT(HOUR FROM s.horas_almoco) +
            EXTRACT(MINUTE FROM s.horas_almoco) / 60
        ) AS horas_almoco,

        SUM(
            EXTRACT(DAY FROM s.horas_extras) * 24 +
            EXTRACT(HOUR FROM s.horas_extras) +
            EXTRACT(MINUTE FROM s.horas_extras) / 60
        ) AS horas_extras,

        SUM(
            EXTRACT(DAY FROM s.horas_ausencia) * 24 +
            EXTRACT(HOUR FROM s.horas_ausencia) +
            EXTRACT(MINUTE FROM s.horas_ausencia) / 60
        ) AS horas_ausencia

    FROM RH_ASSIDUIDADE_SINTESE_DIARIA s
        JOIN RH_T_FUNCIONARIOS f
            ON f.id = s.funcionario_id
        JOIN RH_T_TIPOS_RELACIONAMENTO r
            ON r.fun_id = s.funcionario_id
            AND r.est_act_adm = 1
        LEFT JOIN RH_T_MOBILIDADE m
            ON m.id = r.mob_id
        LEFT JOIN RH_T_DIRECAO i
            ON i.id = m.instit_id
        LEFT JOIN RH_T_SECAO sec
            ON sec.id = m.secao_id
        LEFT JOIN RH_T_PARAM_LOCAL_TRAB il
            ON il.id = m.local_trab_id
        LEFT JOIN SIPSGLOBAL.GLB_T_UPS ups
            ON ups.id = il.ups_id
        LEFT JOIN RH_T_FALTA f2
            ON f2.SINTESE_DIARIO_ID = s.id
        LEFT JOIN RH_T_AUSENCIA aus
            ON aus.FUN_ID = f.id
            AND aus.ESTADO = 'A'
            AND s.data BETWEEN aus.DATA_INICIO AND aus.DATA_FIM

    GROUP BY
        s.FUNCIONARIO_ID,
        f.UUID,
        f.nome,
        i.id, i.nome,
        sec.id, sec.nome,
        il.id, il.nome,
        ups.id, ups.nome,
        EXTRACT(YEAR  FROM s.data),
        EXTRACT(MONTH FROM s.data)
)
SELECT
    (funcionario_id * 10000 + ano * 100 + mes) AS id,
    resumo."FUNCIONARIO_ID",
    resumo."FUNCIONARIO_UUID",
    resumo."NOME_FUNCIONARIO",
    resumo."ID_DIRECAO",
    resumo."NOME_DIRECAO",
    resumo."ID_SECAO",
    resumo."NOME_SECAO",
    resumo."ID_ILHA",
    resumo."NOME_ILHA",
    resumo."ID_UPS",
    resumo."NOME_UPS",
    resumo."ANO",
    resumo."MES",
    resumo."TOTAL_DIAS",
    resumo."TOTAL_FALTAS",
    resumo."TOTAL_FALTAS_PENDENTES",
    resumo."TOTAL_FALTAS_JUSTIFICADAS",
    resumo."HORAS_TRABALHADAS",
    resumo."HORAS_ALMOCO",
    resumo."HORAS_EXTRAS",
    resumo."HORAS_AUSENCIA",
    CASE
        WHEN total_faltas > 0              THEN 'INJUSTIFICADA'
        WHEN total_faltas_pendentes > 0    THEN 'PENDENTE'
        WHEN total_faltas_justificadas > 0 THEN 'JUSTIFICADA'
        ELSE 'CONFORME'
    END AS estado
FROM resumo

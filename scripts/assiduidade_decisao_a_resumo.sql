-- =====================================================================
-- Assiduidade — decisão A (21/10/2026): RH_V_RESUMO_ASSIDUIDADE (Gestão Falta)
-- Recria a vista a partir do DDL em vigor (scripts/assiduidade_ciclo_corrigir_resumo.sql):
--   * Justificada/Injustificada pela justificação (RH_T_FALTA.FLG_JUSTIFICATIVO), não
--     por "estar aprovada"; falta rejeitada/eliminada não conta — o dia volta a
--     injustificado (por registar), como um dia que nunca foi justificado.
--   * TOTAL_FALTAS = todas as faltas do mês (spec: SUM(SINTESE_DIARIA.FALTA)), menos os
--     dias de ausência sem falta registada; = justificadas + injustificadas + pendentes.
--   * Nova coluna TOTAL_FALTAS_INJUSTIFICADAS (no fim, as outras mantêm a ordem).
--   * Contagem por dia: deixa de contar a dobrar quando um dia tem duas RH_T_FALTA
--     (rejeitada + nova) ou duas ausências — também nas horas.
-- ATENÇÃO: o backend passa a mapear TOTAL_FALTAS_INJUSTIFICADAS — correr ANTES da API.
-- =====================================================================
CREATE OR REPLACE FORCE VIEW "INPSRH"."RH_V_RESUMO_ASSIDUIDADE" ("ID", "FUNCIONARIO_ID", "FUNCIONARIO_UUID", "NOME_FUNCIONARIO", "ID_DIRECAO", "NOME_DIRECAO", "ID_SECAO", "NOME_SECAO", "ID_ILHA", "NOME_ILHA", "ID_UPS", "NOME_UPS", "ANO", "MES", "TOTAL_DIAS", "TOTAL_FALTAS", "TOTAL_FALTAS_PENDENTES", "TOTAL_FALTAS_JUSTIFICADAS", "HORAS_TRABALHADAS", "HORAS_ALMOCO", "HORAS_EXTRAS", "HORAS_AUSENCIA", "ESTADO", "MOTIVO_AUSENCIA", "TOTAL_FALTAS_INJUSTIFICADAS") AS
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
),
-- Decisão A (21/10/2026): cada DIA de falta tem uma só classe.
--   * há falta viva (A/P/C) nesse dia -> pela falta: P/C = PENDENTE;
--     A com FLG_JUSTIFICATIVO = 'SIM' = JUSTIFICADA; A com 'NAO' = INJUSTIFICADA;
--   * sem falta viva (nunca registada, ou registo rejeitado/eliminado):
--     ausência activa no dia -> AUSENTE (não conta); senão INJUSTIFICADA.
-- Assim justificadas + injustificadas + pendentes = total de faltas do mês.
-- Por dia (e não por síntese/join), para não contar a dobrar: um dia rejeitado e
-- justificado de novo tem duas RH_T_FALTA, e um dia pode ter mais de uma síntese.
dias_falta AS (
    SELECT s.FUNCIONARIO_ID, TRUNC(s.DATA) AS dia
    FROM RH_ASSIDUIDADE_SINTESE_DIARIA s
    WHERE NVL(s.FALTA, 0) = 1
    GROUP BY s.FUNCIONARIO_ID, TRUNC(s.DATA)
),
dias_classe AS (
    SELECT
        d.FUNCIONARIO_ID,
        EXTRACT(YEAR  FROM d.dia) AS ano,
        EXTRACT(MONTH FROM d.dia) AS mes,
        CASE
            WHEN fv.prio = 3 THEN 'PENDENTE'
            WHEN fv.prio = 2 THEN 'JUSTIFICADA'
            WHEN fv.prio = 1 THEN 'INJUSTIFICADA'
            WHEN EXISTS (
                SELECT 1 FROM RH_T_AUSENCIA a
                WHERE a.FUN_ID = d.FUNCIONARIO_ID
                  AND a.ESTADO = 'A'
                  AND d.dia BETWEEN TRUNC(a.DATA_INICIO) AND TRUNC(NVL(a.DATA_FIM, a.DATA_INICIO))
            ) THEN 'AUSENTE'
            ELSE 'INJUSTIFICADA'
        END AS classe
    FROM dias_falta d
        LEFT JOIN (
            SELECT sd.FUNCIONARIO_ID, TRUNC(sd.DATA) AS dia,
                   MAX(CASE
                           WHEN fa.ESTADO IN ('P', 'C') THEN 3
                           WHEN NVL(fa.FLG_JUSTIFICATIVO, 'SIM') = 'SIM' THEN 2
                           ELSE 1
                       END) AS prio
            FROM RH_T_FALTA fa
                JOIN RH_ASSIDUIDADE_SINTESE_DIARIA sd ON sd.ID = fa.SINTESE_DIARIO_ID
            WHERE fa.ESTADO IN ('A', 'P', 'C')
            GROUP BY sd.FUNCIONARIO_ID, TRUNC(sd.DATA)
        ) fv
            ON fv.FUNCIONARIO_ID = d.FUNCIONARIO_ID AND fv.dia = d.dia
),
faltas_mes AS (
    SELECT
        FUNCIONARIO_ID, ano, mes,
        COUNT(CASE WHEN classe <> 'AUSENTE' THEN 1 END)        AS total_faltas,
        COUNT(CASE WHEN classe = 'PENDENTE' THEN 1 END)        AS total_faltas_pendentes,
        COUNT(CASE WHEN classe = 'JUSTIFICADA' THEN 1 END)     AS total_faltas_justificadas,
        COUNT(CASE WHEN classe = 'INJUSTIFICADA' THEN 1 END)   AS total_faltas_injustificadas
    FROM dias_classe
    GROUP BY FUNCIONARIO_ID, ano, mes
),
-- Faltas registadas (pendentes ou validadas) por tipo, no m�s
motivos_tipo AS (
    SELECT
        sd.FUNCIONARIO_ID,
        EXTRACT(YEAR  FROM sd.data) AS ano,
        EXTRACT(MONTH FROM sd.data) AS mes,
        ps.NOME                     AS nome_tipo,
        COUNT(*)                    AS dias
    FROM RH_T_FALTA fa
        JOIN RH_ASSIDUIDADE_SINTESE_DIARIA sd
            ON sd.id = fa.SINTESE_DIARIO_ID
        JOIN RH_T_PARAM_SITUACAO ps
            ON ps.id = fa.PARAM_SIT_ID
    WHERE fa.ESTADO IN ('A', 'P', 'C')
    GROUP BY
        sd.FUNCIONARIO_ID,
        EXTRACT(YEAR  FROM sd.data),
        EXTRACT(MONTH FROM sd.data),
        ps.NOME
),
motivos AS (
    SELECT
        FUNCIONARIO_ID,
        ano,
        mes,
        LISTAGG(nome_tipo || ' (' || dias || 'd)', ', ')
            WITHIN GROUP (ORDER BY nome_tipo) AS motivo_ausencia
    FROM motivos_tipo
    GROUP BY FUNCIONARIO_ID, ano, mes
)
SELECT
    (resumo.funcionario_id * 10000 + resumo.ano * 100 + resumo.mes) AS id,
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
    NVL(fm.total_faltas, 0)                AS total_faltas,
    NVL(fm.total_faltas_pendentes, 0)      AS total_faltas_pendentes,
    NVL(fm.total_faltas_justificadas, 0)   AS total_faltas_justificadas,
    resumo."HORAS_TRABALHADAS",
    resumo."HORAS_ALMOCO",
    resumo."HORAS_EXTRAS",
    resumo."HORAS_AUSENCIA",
    CASE
        WHEN NVL(fm.total_faltas_injustificadas, 0) > 0 THEN 'INJUSTIFICADA'
        WHEN NVL(fm.total_faltas_pendentes, 0) > 0      THEN 'PENDENTE'
        WHEN NVL(fm.total_faltas_justificadas, 0) > 0   THEN 'JUSTIFICADA'
        ELSE 'CONFORME'
    END AS estado,
    mo.motivo_ausencia,
    NVL(fm.total_faltas_injustificadas, 0) AS total_faltas_injustificadas
FROM resumo
    LEFT JOIN faltas_mes fm
        ON fm.FUNCIONARIO_ID = resumo.FUNCIONARIO_ID
        AND fm.ano = resumo.ano
        AND fm.mes = resumo.mes
    LEFT JOIN motivos mo
        ON mo.FUNCIONARIO_ID = resumo.FUNCIONARIO_ID
        AND mo.ano = resumo.ano
        AND mo.mes = resumo.mes

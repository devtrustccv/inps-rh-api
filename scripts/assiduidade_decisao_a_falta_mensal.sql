-- =====================================================================
-- Assiduidade — decisão A (21/10/2026): RH_V_FALTA_MENSAL (Falta Justificada/Injustificada)
-- Recria a vista a partir de scripts/assiduidade_bug_report_AS_2.sql (em vigor), alterando só:
--   * EST_MENSAL, TOT_INJ e TOT_JUS passam a vir de RH_T_FALTA.FLG_JUSTIFICATIVO: o mês é
--     INJUSTIFICADA se tiver alguma falta "Com Justificativo? = NÃO". Antes vinham do flag de
--     desconto no salário do tipo — uma licença de paternidade justificada saía "Injustificada"
--     (QA AS-002 OBS, AS-008 OBS, AS-016).
--   * O desconto continua só em FLG_DESC_SAL / TOT_VAL_DESC (coluna "Desconto na Remuneração?").
-- =====================================================================
CREATE OR REPLACE FORCE VIEW "INPSRH"."RH_V_FALTA_MENSAL" ("ID", "FUNCIONARIO_ID", "FUNCIONARIO_UUID", "NOME_FUNCIONARIO", "CARGO_ID", "NOME_CARGO", "ID_DIRECAO", "NOME_DIRECAO", "ID_SECAO", "NOME_SECAO", "ID_ILHA", "NOME_ILHA", "ANO", "MES", "DATA_INICIO", "DATA_FIM", "TOT_FALTAS", "TOT_HOR_AUS", "TOT_VAL_DESC", "TOT_INJ", "TOT_JUS", "FLG_DESC_SAL", "EST_MENSAL", "EST_PROC", "MOTIVO") AS
WITH base AS (
    SELECT
        trf.FUN_ID                 AS FUNCIONARIO_ID,
        fun.UUID                   AS FUNCIONARIO_UUID,
        fun.NOME                   AS NOME_FUNCIONARIO,

        NVL(tr.CARGO_ID, trf.CARGO_ID) AS CARGO_ID,
        cg.NOME                    AS NOME_CARGO,

        inst.ID                    AS ID_DIRECAO,
        inst.NOME                  AS NOME_DIRECAO,

        sec.ID                     AS ID_SECAO,
        sec.NOME                   AS NOME_SECAO,

        il.ID                      AS ID_ILHA,
        il.NOME                    AS NOME_ILHA,

        EXTRACT(YEAR  FROM f.DATA_INICIO) AS ANO,
        EXTRACT(MONTH FROM f.DATA_INICIO) AS MES,

        f.DATA_INICIO,
        f.DATA_FIM,

        f.HORAS_AUSENCIA,
        -- AS-007/AS-018: líquido (VALOR_DESCONTO), não o bruto (VALOR)
        NVL(f.VALOR_DESCONTO, 0)   AS VALOR_DESC,

        ps.FLG_FALTA_DECONTO_SAL   AS FLG_DESC_SAL,
        -- Decisão A: justificada/injustificada pela justificação, não pelo desconto
        NVL(f.FLG_JUSTIFICATIVO, 'SIM') AS FLG_JUST,
        ps.NOME                    AS NOME_TIPO,
        f.DEF_REM_ID DEF_REM_ID

    FROM RH_T_FALTA f
             -- vínculo em que a falta foi registada (pode já não ser o actual)
             JOIN RH_T_TIPOS_RELACIONAMENTO trf
                  ON trf.ID = f.TIPREL_ID
             -- vínculo actual do funcionário, só para a colocação
             LEFT JOIN RH_T_TIPOS_RELACIONAMENTO tr
                  ON tr.FUN_ID = trf.FUN_ID
                      AND tr.EST_ACT_ADM = 1
             JOIN RH_T_FUNCIONARIOS fun
                  ON fun.ID = trf.FUN_ID
             LEFT JOIN RH_T_PARAM_CARGO cg
                       ON cg.ID = NVL(tr.CARGO_ID, trf.CARGO_ID)
             LEFT JOIN RH_T_MOBILIDADE m
                       ON m.ID = NVL(tr.MOB_ID, trf.MOB_ID)
             LEFT JOIN rh_t_direcao inst
                       ON inst.ID = m.INSTIT_ID
             LEFT JOIN RH_T_SECAO sec
                       ON sec.ID = m.SECAO_ID
             LEFT JOIN RH_T_PARAM_LOCAL_TRAB il
                       ON il.ID = m.LOCAL_TRAB_ID
             -- LEFT: há faltas sem tipo (PARAM_SIT_ID nulo) que também têm de aparecer
             LEFT JOIN RH_T_PARAM_SITUACAO ps
                  ON ps.ID = f.PARAM_SIT_ID
    WHERE f.ESTADO = 'A'
),
-- AS-016: tipos de falta do mês, com o número de faltas de cada um
motivos_tipo AS (
    SELECT FUNCIONARIO_ID, ANO, MES, NOME_TIPO, COUNT(*) AS DIAS
    FROM base
    WHERE NOME_TIPO IS NOT NULL
    GROUP BY FUNCIONARIO_ID, ANO, MES, NOME_TIPO
),
motivos AS (
    SELECT
        FUNCIONARIO_ID, ANO, MES,
        LISTAGG(NOME_TIPO || ' (' || DIAS || 'd)', ', ')
            WITHIN GROUP (ORDER BY NOME_TIPO) AS MOTIVO
    FROM motivos_tipo
    GROUP BY FUNCIONARIO_ID, ANO, MES
),
mensal AS (
    SELECT
        /* ID tecnico mensal */
        (FUNCIONARIO_ID * 10000 + ANO * 100 + MES) AS ID,
        FUNCIONARIO_ID,
        FUNCIONARIO_UUID,
        NOME_FUNCIONARIO,
        CARGO_ID,
        NOME_CARGO,
        ID_DIRECAO,
        NOME_DIRECAO,
        ID_SECAO,
        NOME_SECAO,
        ID_ILHA,
        NOME_ILHA,
        ANO,
        MES,
        MIN(DATA_INICIO) AS DATA_INICIO,
        MAX(DATA_FIM)    AS DATA_FIM,
        COUNT(*) AS TOT_FALTAS,
        SUM(
                EXTRACT(DAY FROM HORAS_AUSENCIA) * 24 +
                EXTRACT(HOUR FROM HORAS_AUSENCIA) +
                EXTRACT(MINUTE FROM HORAS_AUSENCIA) / 60
        ) AS TOT_HOR_AUS,
        SUM(CASE WHEN FLG_DESC_SAL = '1' THEN VALOR_DESC ELSE 0 END) AS TOT_VAL_DESC,
        SUM(CASE WHEN FLG_JUST = 'SIM' THEN 0 ELSE 1 END) AS TOT_INJ,
        SUM(CASE WHEN FLG_JUST = 'SIM' THEN 1 ELSE 0 END) AS TOT_JUS,
        /* FLAG mensal de desconto */
        CASE
            WHEN SUM(CASE WHEN FLG_DESC_SAL = '1' THEN 1 ELSE 0 END) > 0
                THEN 'S'
            ELSE 'N'
            END AS FLG_DESC_SAL,
        CASE
            WHEN SUM(CASE WHEN FLG_JUST = 'SIM' THEN 0 ELSE 1 END) > 0
                THEN 'INJUSTIFICADA'
            ELSE 'JUSTIFICADA'
            END AS EST_MENSAL,
        CASE
            WHEN MAX(DEF_REM_ID) IS NOT NULL
                THEN 'PROC'
            ELSE 'PEND'
            END AS EST_PROC
    FROM base
    GROUP BY
        FUNCIONARIO_ID,
        FUNCIONARIO_UUID,
        NOME_FUNCIONARIO,
        CARGO_ID,
        NOME_CARGO,
        ID_DIRECAO,
        NOME_DIRECAO,
        ID_SECAO,
        NOME_SECAO,
        ID_ILHA,
        NOME_ILHA,
        ANO,
        MES
)
SELECT
    me.ID, me.FUNCIONARIO_ID, me.FUNCIONARIO_UUID, me.NOME_FUNCIONARIO,
    me.CARGO_ID, me.NOME_CARGO, me.ID_DIRECAO, me.NOME_DIRECAO,
    me.ID_SECAO, me.NOME_SECAO, me.ID_ILHA, me.NOME_ILHA,
    me.ANO, me.MES, me.DATA_INICIO, me.DATA_FIM,
    me.TOT_FALTAS, me.TOT_HOR_AUS, me.TOT_VAL_DESC, me.TOT_INJ, me.TOT_JUS,
    me.FLG_DESC_SAL, me.EST_MENSAL, me.EST_PROC,
    mo.MOTIVO
FROM mensal me
    LEFT JOIN motivos mo
        ON mo.FUNCIONARIO_ID = me.FUNCIONARIO_ID
       AND mo.ANO = me.ANO
       AND mo.MES = me.MES

-- RH_V_FALTA_MENSAL — correcção de 11/09/2026
--
-- Defeito: as agregações comparavam FLG_DESC_SAL com a string 'S', mas a coluna de origem
-- (RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL) é VARCHAR2(1) — vale '0' ou '1', nunca 'S'.
-- Consequência: TOT_INJ era sempre 0, TOT_JUS contava tudo, FLG_DESC_SAL saía sempre 'N' e
-- EST_MENSAL sempre 'JUSTIFICADA'. No ecrã de Gestão de Falta, uma falta injustificada com
-- desconto no salário aparecia como justificada e sem desconto.
--
-- Alteração: as 4 comparações internas passam a = '1' / <> '1'. A SAÍDA mantém-se 'S'/'N',
-- porque FaltaReadService:125 lê Objects.equals("S", e.getFlgDescSal()).
--
-- Backup do texto anterior: docs/sql/rh_v_falta_mensal_BACKUP_11-09.sql

CREATE OR REPLACE VIEW RH_V_FALTA_MENSAL AS
WITH base AS (
    SELECT
        tr.FUN_ID                  AS FUNCIONARIO_ID,
        fun.UUID                   AS FUNCIONARIO_UUID,
        fun.NOME                   AS NOME_FUNCIONARIO,

        tr.CARGO_ID,
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
        NVL(f.VALOR, 0)            AS VALOR_DESC,

        ps.FLG_FALTA_DECONTO_SAL   AS FLG_DESC_SAL,
        f.DEF_REM_ID DEF_REM_ID

    FROM RH_T_FALTA f
             JOIN RH_T_TIPOS_RELACIONAMENTO tr
                  ON tr.ID = f.TIPREL_ID
                      AND tr.EST_ACT_ADM = 1
             JOIN RH_T_FUNCIONARIOS fun
                  ON fun.ID = tr.FUN_ID
             LEFT JOIN RH_T_PARAM_CARGO cg
                       ON cg.ID = tr.CARGO_ID
             LEFT JOIN RH_T_MOBILIDADE m
                       ON m.ID = tr.MOB_ID
             LEFT JOIN rh_t_direcao inst
                       ON inst.ID = m.INSTIT_ID
             LEFT JOIN RH_T_SECAO sec
                       ON sec.ID = m.SECAO_ID
             LEFT JOIN RH_T_PARAM_LOCAL_TRAB il
                       ON il.ID = m.LOCAL_TRAB_ID
             JOIN RH_T_PARAM_SITUACAO ps
                  ON ps.ID = f.PARAM_SIT_ID
    WHERE f.ESTADO = 'A'
)
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
    SUM(VALOR_DESC) AS TOT_VAL_DESC,
    SUM(CASE WHEN FLG_DESC_SAL = '1' THEN 1 ELSE 0 END) AS TOT_INJ,
    SUM(CASE WHEN FLG_DESC_SAL <> '1' OR FLG_DESC_SAL IS NULL THEN 1 ELSE 0 END) AS TOT_JUS,
   /* FLAG mensal de desconto */
    CASE
        WHEN SUM(CASE WHEN FLG_DESC_SAL = '1' THEN 1 ELSE 0 END) > 0
            THEN 'S'
        ELSE 'N'
        END AS FLG_DESC_SAL,
   CASE
        WHEN SUM(CASE WHEN FLG_DESC_SAL = '1' THEN 1 ELSE 0 END) > 0
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

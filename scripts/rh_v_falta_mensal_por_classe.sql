-- =====================================================================
-- RH_V_FALTA_MENSAL — justificadas e injustificadas do mesmo mês em linhas separadas
-- (QA reteste 1.1, AS-008 e exceção do AS-016)
--
-- Antes: uma linha por colaborador e mês; bastava uma falta não justificada para o mês inteiro
-- aparecer "INJUSTIFICADA" (Wilson 11/2026: 4 faltas justificadas e validadas + 2 por justificar
-- numa só linha "Injustificada", 01–06/11, 6 faltas).
-- Agora: uma linha por (colaborador, mês, classe), classe = justificada (FLG_JUSTIFICATIVO SIM)
-- ou não. O ID técnico passa a (FUN_ID*10000+ANO*100+MES)*10+CLASSE (não é exposto na API).
-- Mesmas colunas da versão anterior (VfaltaMensalEntity não muda). Recriada do DDL de 30/10/2026.
-- =====================================================================
CREATE OR REPLACE VIEW RH_V_FALTA_MENSAL AS
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
        -- AS-008 (reteste 1.1): justificadas e injustificadas do mesmo mês em linhas separadas
        CASE WHEN NVL(f.FLG_JUSTIFICATIVO, 'SIM') = 'SIM' THEN 1 ELSE 0 END AS CLASSE,
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
    SELECT FUNCIONARIO_ID, ANO, MES, CLASSE, NOME_TIPO, COUNT(*) AS DIAS
    FROM base
    WHERE NOME_TIPO IS NOT NULL
    GROUP BY FUNCIONARIO_ID, ANO, MES, CLASSE, NOME_TIPO
),
motivos AS (
    SELECT
        FUNCIONARIO_ID, ANO, MES, CLASSE,
        LISTAGG(NOME_TIPO || ' (' || DIAS || 'd)', ', ')
            WITHIN GROUP (ORDER BY NOME_TIPO) AS MOTIVO
    FROM motivos_tipo
    GROUP BY FUNCIONARIO_ID, ANO, MES, CLASSE
),
mensal AS (
    SELECT
        /* ID tecnico mensal */
        ((FUNCIONARIO_ID * 10000 + ANO * 100 + MES) * 10 + CLASSE) AS ID,
        CLASSE,
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
        MES,
        CLASSE
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
       AND mo.CLASSE = me.CLASSE

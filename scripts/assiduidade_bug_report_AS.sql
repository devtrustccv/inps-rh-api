-- =====================================================================
-- Bug report SIPS-RH 1.0 — Assiduidade (AS-001, AS-007, AS-008, AS-011)
-- Executar pelo DBA/utilizador. As views são recriadas a partir do DDL
-- actual (DBMS_METADATA, 14/10/2026), alterando só o indicado em cada bloco.
--
-- ATENÇÃO: o backend passa a mapear RH_V_RESUMO_ASSIDUIDADE.MOTIVO_AUSENCIA.
-- Correr este script ANTES de pôr no ar a nova versão da API, senão a lista
-- de Gestão Falta dá erro (coluna inexistente).
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1) RH_V_FALTA_MENSAL
--    AS-008: a falta deixava de aparecer quando o vínculo (TIPREL) em que foi
--            registada já não era o actual (join com EST_ACT_ADM = 1).
--            Agora conta todas as faltas do funcionário; a colocação
--            (cargo/direcção/secção/local) vem do vínculo actual e, se não
--            houver, do vínculo da própria falta.
--    AS-007: TOT_VAL_DESC só soma o valor das faltas cujo tipo desconta no
--            salário (FLG_FALTA_DECONTO_SAL = '1'); antes somava todas, e a
--            lista mostrava "Desconto Remuneração = Não" com valor > 0.
-- ---------------------------------------------------------------------
CREATE OR REPLACE FORCE VIEW "INPSRH"."RH_V_FALTA_MENSAL" ("ID", "FUNCIONARIO_ID", "FUNCIONARIO_UUID", "NOME_FUNCIONARIO", "CARGO_ID", "NOME_CARGO", "ID_DIRECAO", "NOME_DIRECAO", "ID_SECAO", "NOME_SECAO", "ID_ILHA", "NOME_ILHA", "ANO", "MES", "DATA_INICIO", "DATA_FIM", "TOT_FALTAS", "TOT_HOR_AUS", "TOT_VAL_DESC", "TOT_INJ", "TOT_JUS", "FLG_DESC_SAL", "EST_MENSAL", "EST_PROC") AS
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
        NVL(f.VALOR, 0)            AS VALOR_DESC,

        ps.FLG_FALTA_DECONTO_SAL   AS FLG_DESC_SAL,
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
    SUM(CASE WHEN FLG_DESC_SAL = '1' THEN VALOR_DESC ELSE 0 END) AS TOT_VAL_DESC,
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
    MES;


-- ---------------------------------------------------------------------
-- 2) RH_V_RESUMO_ASSIDUIDADE
--    AS-001: nova coluna MOTIVO_AUSENCIA — tipos de falta registados no mês
--            com o n.º de dias, ex.: "Doença (2d), Luto (1d)".
--            Resto da view inalterado.
-- ---------------------------------------------------------------------
CREATE OR REPLACE FORCE VIEW "INPSRH"."RH_V_RESUMO_ASSIDUIDADE" ("ID", "FUNCIONARIO_ID", "FUNCIONARIO_UUID", "NOME_FUNCIONARIO", "ID_DIRECAO", "NOME_DIRECAO", "ID_SECAO", "NOME_SECAO", "ID_ILHA", "NOME_ILHA", "ID_UPS", "NOME_UPS", "ANO", "MES", "TOTAL_DIAS", "TOTAL_FALTAS", "TOTAL_FALTAS_PENDENTES", "TOTAL_FALTAS_JUSTIFICADAS", "HORAS_TRABALHADAS", "HORAS_ALMOCO", "HORAS_EXTRAS", "HORAS_AUSENCIA", "ESTADO", "MOTIVO_AUSENCIA") AS
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
                WHEN NVL(s.FALTA, 0) = 1 AND f2.id IS NULL AND aus.id IS NULL
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
),
-- Faltas registadas (pendentes ou validadas) por tipo, no mês
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
    WHERE fa.ESTADO IN ('A', 'P')
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
    resumo."TOTAL_FALTAS",
    resumo."TOTAL_FALTAS_PENDENTES",
    resumo."TOTAL_FALTAS_JUSTIFICADAS",
    resumo."HORAS_TRABALHADAS",
    resumo."HORAS_ALMOCO",
    resumo."HORAS_EXTRAS",
    resumo."HORAS_AUSENCIA",
    CASE
        WHEN resumo.total_faltas > 0              THEN 'INJUSTIFICADA'
        WHEN resumo.total_faltas_pendentes > 0    THEN 'PENDENTE'
        WHEN resumo.total_faltas_justificadas > 0 THEN 'JUSTIFICADA'
        ELSE 'CONFORME'
    END AS estado,
    mo.motivo_ausencia
FROM resumo
    LEFT JOIN motivos mo
        ON mo.FUNCIONARIO_ID = resumo.FUNCIONARIO_ID
        AND mo.ano = resumo.ano
        AND mo.mes = resumo.mes;


-- ---------------------------------------------------------------------
-- 3) AS-011: validações de HORA_EXTRA gravadas sem funcionário/vínculo
--    (a API passa a preenchê-los; isto corrige as já existentes — em QA,
--    14/10/2026, só a validação 1232 / pedido 285).
--    Usa o 1.º registo de hora extra do pedido, como a API.
-- ---------------------------------------------------------------------
UPDATE RH_T_VALIDACAO val
   SET (val.TIPREL_ID, val.FUN_ID) = (
         SELECT he.TIPREL_ID, tr.FUN_ID
           FROM RH_T_HORA_EXTRA he
           JOIN RH_T_TIPOS_RELACIONAMENTO tr ON tr.ID = he.TIPREL_ID
          WHERE he.ID = (SELECT MIN(he2.ID) FROM RH_T_HORA_EXTRA he2 WHERE he2.PEDIDO_ID = val.REFERENCIA_ID)
       )
 WHERE val.REFERENCIA_NAME = 'HORA_EXTRA'
   AND (val.FUN_ID IS NULL OR val.TIPREL_ID IS NULL)
   AND EXISTS (SELECT 1 FROM RH_T_HORA_EXTRA he3 WHERE he3.PEDIDO_ID = val.REFERENCIA_ID);

COMMIT;

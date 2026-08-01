-- =====================================================================
-- RH_V_HORA_EXTRA_MENSAL  (reescrita)
--
-- ANTES: grao = he.ID (uma linha por registo de hora extra), apesar do nome
--        "MENSAL". Nao havia GROUP BY nenhum.
--
-- AGORA: grao = (registo de hora extra x mes de referencia).
--        Um pedido de 20/01/2026 a 10/03/2026 produz 3 linhas: 202601, 202602,
--        202603 — conforme o exemplo da especificacao.
--
-- NOTA SOBRE O VALOR
--   RH_T_HORA_EXTRA.VALOR_DIARIO guarda o que CALCULO_HORA_EXTRA devolve, que e
--   o v_valor_total do PERIODO INTEIRO (ver package body, linha 2197) — nao um
--   valor diario, apesar do nome. Para repartir por mes a vista reaplica a mesma
--   formula da funcao (linhas 2111-2179):
--
--     valor_hora      = salario / 30 / jornada_diaria
--     valor_dia_util  = valor_hora * horas_diarias * HE_VALOR_DUTIL  / 100
--     valor_dia_nutil = valor_hora * horas_diarias * HE_VALOR_DNUTIL / 100
--     valor_mes       = dias_uteis * valor_dia_util + dias_nao_uteis * valor_dia_nutil
--
--   respeitando PERCENTAGEM_REFERENTE (DIAS_UTEIS / DIAS_NAO_UTEIS / ambos).
--
--   Usa-se T.SALARIO em vez de GET_SALARIO_BASE porque essa funcao rebenta com
--   ORA-01422 para colaboradores com mais do que um salario activo e uma vista
--   nao tem como apanhar a excepcao (ver assiduidade_ddl_pendente.sql, seccao 4).
-- =====================================================================

CREATE OR REPLACE VIEW RH_V_HORA_EXTRA_MENSAL AS
WITH parametro AS (
    -- Jornada diaria e percentagens de hora extra em vigor
    SELECT TO_NUMBER(SUBSTR(p.DIARIA, 1, 2)) + TO_NUMBER(SUBSTR(p.DIARIA, 4, 2)) / 60 AS jornada_horas,
           p.DIARIA                                            AS jornada_hhmm,
           NVL(p.HE_VALOR_DUTIL, 0)                            AS pct_util,
           NVL(p.HE_VALOR_DNUTIL, 0)                           AS pct_nao_util
      FROM RH_T_ASSIDUIDADE_PARAMETRO p
     WHERE p.ESTADO = 'A'
       AND ROWNUM = 1
),
gerador AS (
    SELECT LEVEL AS n FROM dual CONNECT BY LEVEL <= 120
),
-- Um registo de hora extra por cada mes que atravessa
he_mes AS (
    SELECT he.ID                                                        AS hora_extra_id,
           ADD_MONTHS(TRUNC(he.DATA_INICIO, 'MM'), g.n - 1)             AS mes_ini,
           GREATEST(he.DATA_INICIO,
                    ADD_MONTHS(TRUNC(he.DATA_INICIO, 'MM'), g.n - 1))   AS data_inicio_mes,
           LEAST(he.DATA_FIM,
                 LAST_DAY(ADD_MONTHS(TRUNC(he.DATA_INICIO, 'MM'), g.n - 1))) AS data_fim_mes
      FROM RH_T_HORA_EXTRA he
      JOIN gerador g
        ON g.n <= MONTHS_BETWEEN(TRUNC(he.DATA_FIM, 'MM'), TRUNC(he.DATA_INICIO, 'MM')) + 1
),
-- Expande dia a dia dentro de cada mes para classificar util / nao util
dias AS (
    SELECT m.hora_extra_id,
           m.mes_ini,
           m.data_inicio_mes,
           m.data_fim_mes,
           SUM(CASE WHEN RH_PROCESSAMENTO_SALARIAL_DB.IS_DIA_UTEL(m.data_inicio_mes + g.n - 1, NULL) = 'S'
                    THEN 1 ELSE 0 END) AS dias_uteis,
           SUM(CASE WHEN RH_PROCESSAMENTO_SALARIAL_DB.IS_DIA_UTEL(m.data_inicio_mes + g.n - 1, NULL) = 'S'
                    THEN 0 ELSE 1 END) AS dias_nao_uteis
      FROM he_mes m
      JOIN gerador g
        ON m.data_inicio_mes + g.n - 1 <= m.data_fim_mes
     GROUP BY m.hora_extra_id, m.mes_ini, m.data_inicio_mes, m.data_fim_mes
)
SELECT
    -- Chave sintetica estavel: um registo de hora extra tem no maximo uma linha por mes
    (d.hora_extra_id * 1000000 + TO_NUMBER(TO_CHAR(d.mes_ini, 'YYYYMM'))) AS ID,

    he.ID                                        AS HORA_EXTRA_ID,
    CAST(he.UUID AS VARCHAR2(100))               AS HORA_EXTRA_UUID,

    t.FUN_ID                                     AS FUNCIONARIO_ID,
    f.UUID                                       AS FUNCIONARIO_UUID,
    f.NOME                                       AS NOME_FUNCIONARIO,
    t.CARGO_ID,
    cg.NOME                                      AS NOME_CARGO,
    inst.ID                                      AS ID_DIRECAO,
    inst.NOME                                    AS NOME_DIRECAO,
    sec.ID                                       AS ID_SECAO,
    sec.NOME                                     AS NOME_SECAO,
    il.ID                                        AS ID_ILHA,
    il.NOME                                      AS NOME_ILHA,

    TO_CHAR(d.mes_ini, 'YYYYMM')                 AS MES,
    EXTRACT(YEAR  FROM d.mes_ini)                AS ANO,
    EXTRACT(MONTH FROM d.mes_ini)                AS MES_NUMERO,
    d.data_inicio_mes                            AS DATA_INICIO,
    d.data_fim_mes                               AS DATA_FIM,
    he.DATA_INICIO                               AS PERIODO_INICIO,
    he.DATA_FIM                                  AS PERIODO_FIM,

    d.dias_uteis                                 AS DIAS_UTEIS,
    d.dias_nao_uteis                             AS DIAS_NAO_UTEIS,

    par.jornada_hhmm                             AS HORAS_CONTRATADO_DIARIO,
    he.HORAS_DIARIAS                             AS HORAS_EXTRA_DIARIAS,
    he.HORAS_DIARIAS * 12                        AS HORAS_CONTRATADO_MENSAL,
    (
        EXTRACT(DAY    FROM NVL(sd.HORAS_EXTRAS, INTERVAL '+0 00:00:00' DAY TO SECOND)) * 24 +
        EXTRACT(HOUR   FROM NVL(sd.HORAS_EXTRAS, INTERVAL '+0 00:00:00' DAY TO SECOND)) +
        EXTRACT(MINUTE FROM NVL(sd.HORAS_EXTRAS, INTERVAL '+0 00:00:00' DAY TO SECOND)) / 60
    )                                            AS HORAS_TRABALHO,

    t.SALARIO                                    AS SALARIO_MENSAL,
    par.pct_util                                 AS PERCENTAGEM_UTIL,
    par.pct_nao_util                             AS PERCENTAGEM_NAO_UTIL,
    he.PERCENTAGEM                               AS PERCENTAGEM,
    he.PERCENTAGEM_REFERENTE                     AS PERCENTAGEM_REFERENTE,

    -- valor_hora * horas extra diarias * percentagem
    ROUND(NVL(t.SALARIO, 0) / 30 / NULLIF(par.jornada_horas, 0)
          * NVL(he.HORAS_DIARIAS, 0) * par.pct_util / 100, 2)      AS VALOR_DIARIO_UTIL,
    ROUND(NVL(t.SALARIO, 0) / 30 / NULLIF(par.jornada_horas, 0)
          * NVL(he.HORAS_DIARIAS, 0) * par.pct_nao_util / 100, 2)  AS VALOR_DIARIO_NAO_UTIL,

    ROUND(
        CASE he.PERCENTAGEM_REFERENTE
            WHEN 'DIAS_UTEIS' THEN
                d.dias_uteis * NVL(t.SALARIO, 0) / 30 / NULLIF(par.jornada_horas, 0)
                * NVL(he.HORAS_DIARIAS, 0) * par.pct_util / 100
            WHEN 'DIAS_NAO_UTEIS' THEN
                d.dias_nao_uteis * NVL(t.SALARIO, 0) / 30 / NULLIF(par.jornada_horas, 0)
                * NVL(he.HORAS_DIARIAS, 0) * par.pct_nao_util / 100
            ELSE
                d.dias_uteis * NVL(t.SALARIO, 0) / 30 / NULLIF(par.jornada_horas, 0)
                * NVL(he.HORAS_DIARIAS, 0) * par.pct_util / 100
              + d.dias_nao_uteis * NVL(t.SALARIO, 0) / 30 / NULLIF(par.jornada_horas, 0)
                * NVL(he.HORAS_DIARIAS, 0) * par.pct_nao_util / 100
        END, 2)                                  AS VALOR_ACUMULADO_MES,

    -- Total do periodo tal como gravado por CALCULO_HORA_EXTRA
    he.VALOR_DIARIO                              AS VALOR_PERIODO,

    he.ESTADO,
    CASE he.ESTADO
        WHEN 'P' THEN 'Pendente'
        WHEN 'A' THEN 'Ativo'
        WHEN 'I' THEN 'Inativo'
        ELSE 'Desconhecido'
    END                                          AS ESTADO_DESC,
    p.ID                                         AS PEDIDO_ID,
    CAST(p.UUID AS VARCHAR2(100))                AS PEDIDO_UUID,
    p.DATA_REGISTO                               AS DATA_PEDIDO

FROM dias d
    JOIN RH_T_HORA_EXTRA he            ON he.ID = d.hora_extra_id
    CROSS JOIN parametro par
    JOIN RH_T_TIPOS_RELACIONAMENTO t   ON t.ID = he.TIPREL_ID
    JOIN RH_T_FUNCIONARIOS f           ON f.ID = t.FUN_ID
    LEFT JOIN RH_T_PARAM_CARGO cg      ON cg.ID = t.CARGO_ID
    LEFT JOIN RH_T_MOBILIDADE m        ON m.ID = t.MOB_ID
    LEFT JOIN RH_T_DIRECAO inst        ON inst.ID = m.INSTIT_ID
    LEFT JOIN RH_T_SECAO sec           ON sec.ID = m.SECAO_ID
    LEFT JOIN RH_T_PARAM_LOCAL_TRAB il ON il.ID = m.LOCAL_TRAB_ID
    LEFT JOIN RH_ASSIDUIDADE_SINTESE_DIARIA sd ON sd.ID = he.SINTESE_DIARIO_ID
    LEFT JOIN RH_T_PEDIDO p            ON p.ID = he.PEDIDO_ID
WHERE he.ESTADO IN ('P', 'A', 'I');

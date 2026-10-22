-- =====================================================================
-- Assiduidade — ciclo CORRIGIR (21/10/2026), RH_V_HORA_EXTRA_MENSAL
-- Recria a vista a partir do DDL em vigor (USER_VIEWS, 21/10/2026), alterando só:
--   * a hora extra em correção (C) passa a aparecer, com ESTADO_DESC 'Em correção'.
-- Sem isto um registo devolvido para correção (ESTADO = 'C') desaparecia do ecrã.
-- =====================================================================
CREATE OR REPLACE FORCE VIEW "INPSRH"."RH_V_HORA_EXTRA_MENSAL" ("ID", "HORA_EXTRA_ID", "HORA_EXTRA_UUID", "FUNCIONARIO_ID", "FUNCIONARIO_UUID", "NOME_FUNCIONARIO", "CARGO_ID", "NOME_CARGO", "ID_DIRECAO", "NOME_DIRECAO", "ID_SECAO", "NOME_SECAO", "ID_ILHA", "NOME_ILHA", "MES", "ANO", "MES_NUMERO", "DATA_INICIO", "DATA_FIM", "PERIODO_INICIO", "PERIODO_FIM", "DIAS_UTEIS", "DIAS_NAO_UTEIS", "HORAS_CONTRATADO_DIARIO", "HORAS_EXTRA_DIARIAS", "HORAS_CONTRATADO_MENSAL", "HORAS_TRABALHO", "SALARIO_MENSAL", "PERCENTAGEM_UTIL", "PERCENTAGEM_NAO_UTIL", "PERCENTAGEM", "PERCENTAGEM_REFERENTE", "VALOR_DIARIO_UTIL", "VALOR_DIARIO_NAO_UTIL", "VALOR_ACUMULADO_MES", "VALOR_PERIODO", "ESTADO", "ESTADO_DESC", "PEDIDO_ID", "PEDIDO_UUID", "DATA_PEDIDO") AS
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
dias_base AS (
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
),
-- Peso de cada mes no total do periodo, para repartir o valor gravado.
-- O peso e a soma dos dias ponderados pela percentagem que lhes cabe.
dias AS (
    SELECT db.hora_extra_id,
           db.mes_ini,
           db.data_inicio_mes,
           db.data_fim_mes,
           db.dias_uteis,
           db.dias_nao_uteis,
           CASE he2.PERCENTAGEM_REFERENTE
               WHEN 'DIAS_UTEIS'     THEN db.dias_uteis     * par2.pct_util
               WHEN 'DIAS_NAO_UTEIS' THEN db.dias_nao_uteis * par2.pct_nao_util
               ELSE db.dias_uteis * par2.pct_util + db.dias_nao_uteis * par2.pct_nao_util
           END AS peso_mes,
           SUM(
               CASE he2.PERCENTAGEM_REFERENTE
                   WHEN 'DIAS_UTEIS'     THEN db.dias_uteis     * par2.pct_util
                   WHEN 'DIAS_NAO_UTEIS' THEN db.dias_nao_uteis * par2.pct_nao_util
                   ELSE db.dias_uteis * par2.pct_util + db.dias_nao_uteis * par2.pct_nao_util
               END
           ) OVER (PARTITION BY db.hora_extra_id) AS peso_total
      FROM dias_base db
      JOIN RH_T_HORA_EXTRA he2 ON he2.ID = db.hora_extra_id
      CROSS JOIN parametro par2
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

    -- Reparte o valor GRAVADO (he.VALOR_DIARIO, que � o total do per�odo devolvido por
    -- CALCULO_HORA_EXTRA) pelos meses, proporcionalmente ao peso de cada m�s.
    --
    -- N�o recalcula: o que a lista mostra tem de ser o que o processamento vai pagar,
    -- sen�o o RH valida um valor e o colaborador recebe outro. O peso respeita a
    -- mistura de dias �teis/n�o �teis e a percentagem aplic�vel.
    ROUND(
        NVL(he.VALOR_DIARIO, 0)
        * CASE WHEN NVL(d.peso_total, 0) = 0 THEN 0 ELSE d.peso_mes / d.peso_total END
    , 2)                                         AS VALOR_ACUMULADO_MES,

    -- Total do periodo tal como gravado por CALCULO_HORA_EXTRA
    he.VALOR_DIARIO                              AS VALOR_PERIODO,

    he.ESTADO,
    CASE he.ESTADO
        WHEN 'P' THEN 'Pendente'
        WHEN 'A' THEN 'Ativo'
        WHEN 'I' THEN 'Inativo'
        WHEN 'C' THEN 'Em correção'
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
WHERE he.ESTADO IN ('P', 'A', 'I', 'C')

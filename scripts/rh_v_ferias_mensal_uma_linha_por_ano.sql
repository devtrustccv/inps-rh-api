-- =====================================================================
-- RH_V_FERIAS_MENSAL — uma linha por colaborador e ano (QA reteste 1.1, RG-010)
--
-- A versão anterior fazia LEFT JOIN a RH_T_FERIAS_GOZADAS e a RH_T_FERIAS_MAPA sem agregar:
-- cada férias gozada (incluindo as deduções de uma justificação de falta, uma por dia) e cada
-- linha do mapa davam uma linha nova com o mesmo ID (FUN_ID*10000+ANO). O Hibernate devolvia N
-- vezes a primeira, e o TOTAL_DIREITO (SUM OVER) somava o direito N vezes (Wilson: 22 x 6 = 132).
-- O UUID_PEDIDO vinha de qualquer férias gozada, incluindo pedidos de justificação, e o
-- "Editar Pedido" rebentava (500).
--
-- Agora:
--   TOTAL_GOZADO   = SUM(FERIAS_GOZADAS.NUM_DIA) em estado A (a mesma regra do GET feria/saldo);
--   TOTAL_PLANEADO = dias do mapa de férias do ano;
--   PEDIDO_ID / UUID_PEDIDO = o último pedido de FÉRIAS do ano (null se não houver);
--   TOTAL_DIREITO  = direito acumulado até ao ano (como antes, agora sem duplicação).
-- Mesmas colunas e nomes da versão anterior (VFeriasMensalEntity não muda).
-- =====================================================================
CREATE OR REPLACE FORCE VIEW RH_V_FERIAS_MENSAL AS
WITH base AS (
    SELECT
        fun.id          AS funcionario_id,
        fun.uuid        AS uuid_funcionario,
        fun.nome        AS nome_colaborador,
        fun.id_colaborador AS id_colaborador,
        inst.id         AS direcao_id,
        inst.nome       AS direcao,
        inst.SIGA       AS codigo_direcao,
        sec.id          AS secao_id,
        sec.nome        AS secao,
        pv.nome         AS vinculo,
        cg.nome         AS categoria,
        f.estado        AS estado,
        a.ano           AS ano,
        NVL(f.num_dia, 0) AS dias_direito,
        (SELECT NVL(SUM(fg.num_dia), 0)
           FROM rh_t_ferias_gozadas fg
          WHERE fg.fun_id = fun.id AND fg.ano_id = f.ano_id AND fg.estado = 'A') AS dias_gozados,
        (SELECT NVL(SUM(fm.data_fim - fm.data_inicio + 1), 0)
           FROM rh_t_ferias_mapa fm
          WHERE fm.fun_id = fun.id AND fm.ano_id = f.ano_id) AS dias_planeados,
        (SELECT MAX(p.id)
           FROM rh_t_ferias_gozadas fg
           JOIN rh_t_pedido p ON p.id = fg.pedido_id
          WHERE fg.fun_id = fun.id AND fg.ano_id = f.ano_id AND p.tipo_pedido = 'FERIA') AS pedido_id
    FROM rh_t_ferias f
             JOIN rh_t_funcionarios fun    ON fun.id = f.fun_id
             JOIN rh_t_ano a               ON a.id = f.ano_id
             JOIN rh_t_tipos_relacionamento tr ON tr.fun_id = fun.id AND tr.est_act_adm = 1
             LEFT JOIN rh_t_param_cargo cg ON cg.id = tr.cargo_id
             LEFT JOIN rh_t_mobilidade m   ON m.id = tr.mob_id
             LEFT JOIN RH_T_DIRECAO inst   ON inst.id = m.instit_id
             LEFT JOIN rh_t_secao sec      ON sec.id = m.secao_id
             LEFT JOIN rh_t_contrato_vinculo cv ON cv.id = tr.contr_vinculo_id
             LEFT JOIN rh_t_param_vinculo pv    ON pv.id = cv.vinculo_id
)
SELECT
    (b.funcionario_id * 10000 + b.ano)  AS id,
    b.pedido_id,
    p.uuid                              AS uuid_pedido,
    b.uuid_funcionario,
    b.nome_colaborador,
    b.id_colaborador,
    b.direcao_id,
    b.direcao,
    b.codigo_direcao,
    b.secao_id,
    b.secao,
    b.vinculo,
    b.categoria,
    b.dias_direito                      AS total_direito_ano,
    b.dias_planeados                    AS total_planeado,
    b.dias_gozados                      AS total_gozado,
    b.ano,
    b.estado,
    CASE b.estado
        WHEN 'P' THEN 'Pendente'
        WHEN 'A' THEN 'Ativo'
        WHEN 'I' THEN 'Inativo'
        ELSE 'Desconhecido'
    END                                 AS estado_desc,
    SUM(b.dias_direito) OVER (PARTITION BY b.funcionario_id ORDER BY b.ano) AS total_direito
FROM base b
LEFT JOIN rh_t_pedido p ON p.id = b.pedido_id

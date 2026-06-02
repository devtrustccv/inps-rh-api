CREATE OR REPLACE VIEW VW_FUNC_AVALIACOES AS
SELECT fun_id,
       AVG(CASE WHEN rn <= 3 THEN AVALIACAO_FINAL END) media_3anos,
       AVG(CASE WHEN rn <= 2 THEN AVALIACAO_FINAL END) media_2anos
FROM (SELECT fun_id,
             AVALIACAO_FINAL,
             ROW_NUMBER() OVER (PARTITION BY fun_id ORDER BY ano DESC) rn
      FROM RH_T_AVD)
GROUP BY fun_id;
----------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_PROC_DISCIPLINAR_2ANOS AS
WITH processos_por_ano AS (SELECT pd.id                              AS processo_id,
                                  tr.fun_id,
                                  EXTRACT(YEAR FROM pd.date_inic_pd) AS ano_processo
                           FROM RH_T_PROCESSO_DISCIPLINAR pd
                                    JOIN RH_T_TIPOS_RELACIONAMENTO tr ON tr.id = pd.tiprel_id
                           WHERE pd.estado = 'A'
                             AND EXTRACT(YEAR FROM pd.date_inic_pd) IN
                                 (EXTRACT(YEAR FROM SYSDATE), EXTRACT(YEAR FROM SYSDATE) - 1))
SELECT f.id        AS fun_id,
       COALESCE(MAX(CASE WHEN p.ano_processo = EXTRACT(YEAR FROM SYSDATE) THEN 1 ELSE 0 END),
                0) AS proc_ano_atual,
       COALESCE(MAX(CASE WHEN p.ano_processo = EXTRACT(YEAR FROM SYSDATE) - 1 THEN 1 ELSE 0 END),
                0) AS proc_ano_anterior,
       CASE
           WHEN COALESCE(MAX(CASE WHEN p.ano_processo = EXTRACT(YEAR FROM SYSDATE) THEN 1 ELSE 0 END), 0) = 0
               AND COALESCE(MAX(CASE WHEN p.ano_processo = EXTRACT(YEAR FROM SYSDATE) - 1 THEN 1 ELSE 0 END), 0) = 0
               THEN 1
           ELSE 0
           END     AS apto_proc_disciplinar
FROM RH_T_FUNCIONARIOS f
         LEFT JOIN processos_por_ano p ON p.FUN_ID = f.id
GROUP BY f.id;
----------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_FUNC_FALTAS_2_ANOS_AG AS
WITH faltas_por_ano AS (SELECT p.fun_id                         AS funcionario_id,
                               EXTRACT(YEAR FROM f.data_inicio) AS ano_faltas,
                               COUNT(*)                         AS total_faltas
                        FROM RH_T_FALTA f
                                 JOIN RH_T_PEDIDO p ON p.id = f.pedido_id
                        WHERE f.estado = 'A'
                          AND EXTRACT(YEAR FROM f.data_inicio) IN
                              (EXTRACT(YEAR FROM SYSDATE), EXTRACT(YEAR FROM SYSDATE) - 1)
                        GROUP BY p.fun_id, EXTRACT(YEAR FROM f.data_inicio))
SELECT f.id        AS fun_id,
       COALESCE(MAX(CASE WHEN fa.ano_faltas = EXTRACT(YEAR FROM SYSDATE) THEN fa.total_faltas END),
                0) AS faltas_ano_atual,
       COALESCE(MAX(CASE WHEN fa.ano_faltas = EXTRACT(YEAR FROM SYSDATE) - 1 THEN fa.total_faltas END),
                0) AS faltas_ano_anterior,
       CASE
           WHEN COALESCE(MAX(CASE WHEN fa.ano_faltas = EXTRACT(YEAR FROM SYSDATE) THEN fa.total_faltas END), 0) > 6
               OR
                COALESCE(MAX(CASE WHEN fa.ano_faltas = EXTRACT(YEAR FROM SYSDATE) - 1 THEN fa.total_faltas END), 0) > 6
               THEN 0
           ELSE 1
           END     AS apto_por_faltas
FROM RH_T_FUNCIONARIOS f
         LEFT JOIN faltas_por_ano fa ON fa.funcionario_id = f.id
GROUP BY f.id;
--select * from VW_FUNC_FALTAS_2_ANOS_AG;
----------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_CARREIRA_TIPO AS
WITH funcionarios_diretores AS (SELECT DISTINCT cv.fun_id
                                FROM RH_T_CARREIRA c
                                         JOIN RH_T_CONTRATO_VINCULO cv
                                              ON cv.id = c.contr_vinculo_id
                                WHERE c.estado = 'A'
                                  AND c.cargo_id IS NOT NULL)
SELECT c.id    AS carreira_id,
       cv.fun_id,
       c.cargo_id,

       CASE
           WHEN c.cargo_id IS NOT NULL THEN 'DIRECTOR'
           WHEN c.cargo_id IS NULL AND fd.fun_id IS NOT NULL THEN 'DIRECTOR_BASE'
           ELSE 'NORMAL'
           END AS tipo_carreira,
       CASE
           WHEN c.cargo_id IS NOT NULL THEN 4
           WHEN c.cargo_id IS NULL AND fd.fun_id IS NOT NULL THEN 2
           ELSE 3
           END AS tempo_min_progressao_anos

FROM RH_T_CARREIRA c
         JOIN RH_T_CONTRATO_VINCULO cv
              ON cv.id = c.contr_vinculo_id
         LEFT JOIN funcionarios_diretores fd
                   ON fd.fun_id = cv.fun_id
WHERE c.estado = 'A';
------------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_FUNC_ULTIMA_EVOLUCAO AS
SELECT cv.fun_id,
       MAX(ev.tipo) KEEP (DENSE_RANK LAST ORDER BY ev.id) AS ultima_evolucao
FROM RH_T_EVOLUCAO_CARREIRA ev
         JOIN RH_T_CARREIRA c ON c.id = ev.carreira_id_de
         JOIN RH_T_CONTRATO_VINCULO cv ON cv.id = c.contr_vinculo_id
GROUP BY cv.fun_id;
--SELECT * FROM VW_FUNC_ULTIMA_EVOLUCAO;
--------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_FUNC_RELACIONAMENTO_ATIVO AS
SELECT fun_id,
       MAX(id) AS relacionamento_id
FROM RH_T_TIPOS_RELACIONAMENTO
WHERE est_act_adm = 1
GROUP BY fun_id;
--------------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW VW_PROGRESSAO_PROMOCAO AS
SELECT c.id               AS carreira_id,
       f.id                  funcionario_id,
       f.nome,
       c.data_inicio,
       e.nivel_referencia,
       e.escalao,
       av.media_3anos,
       av.media_2anos,
       fr.relacionamento_id,
       fd.tipo_carreira,
       fd.tempo_min_progressao_anos,
       ue.ultima_evolucao AS evolucao_atual,
       fa.apto_por_faltas,
       fa.faltas_ano_atual,
       fa.faltas_ano_anterior,
       fpd.apto_proc_disciplinar,
       fpd.proc_ano_atual,
       fpd.proc_ano_anterior
FROM RH_T_CARREIRA c
         JOIN RH_T_CONTRATO_VINCULO cv ON cv.id = c.contr_vinculo_id
         JOIN RH_T_FUNCIONARIOS f ON f.id = cv.fun_id
         JOIN RH_T_PARAM_ESCALAO e ON e.id = c.escalao_id
         LEFT JOIN VW_FUNC_AVALIACOES av ON av.fun_id = f.id
         LEFT JOIN VW_CARREIRA_TIPO fd ON fd.fun_id = f.id
         LEFT JOIN VW_FUNC_ULTIMA_EVOLUCAO ue ON ue.fun_id = f.id
         LEFT JOIN VW_FUNC_RELACIONAMENTO_ATIVO fr ON fr.fun_id = f.id
         LEFT JOIN VW_FUNC_FALTAS_2_ANOS_AG fa ON fa.fun_id = f.id
         LEFT JOIN VW_PROC_DISCIPLINAR_2ANOS fpd ON fpd.fun_id = f.id

WHERE c.estado = 'A'
  AND c.data_inicio IS NOT NULL;
--SELECT * FROM VW_PROGRESSAO_PROMOCAO;

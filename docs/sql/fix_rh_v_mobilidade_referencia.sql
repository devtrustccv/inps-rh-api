-- ---------------------------------------------------------------------------
-- Fix RH_V_MOBILIDADE: TIPO_SITUACAO_DESC — filtrar por REFERENCIA
--
-- Mesmo problema de RH_V_CARREIRA: o mesmo VALOR (ex.: 'INICIO') existe em
-- RH_T_DOMAINS sob várias REFERENCIA (CARREIRA, MOBILIDADE, SITUACAO_LABORAL).
-- O LISTAGG da descrição, sem filtro de REFERENCIA, concatenava as descrições
-- de todos os contextos para o mesmo VALOR.
--
-- Fix: filtrar por REFERENCIA LIKE 'MOBILIDADE%' + ESTADO='A' na subquery da
-- descrição, para trazer apenas a descrição do contexto de mobilidade.
--
-- Aplicado no ambiente de desenvolvimento; replicar nos restantes.
-- ---------------------------------------------------------------------------
CREATE OR REPLACE VIEW RH_V_MOBILIDADE AS
SELECT A.ID MOB_ID, A.FUN_ID, d.uuid FUN_UUID, b.nome UNIDADE_DESC, B.ID UNIDADE_ID,
c.nome LOCAL_TRAB_NOME, C.ID LOCAL_TRAB_ID,E.NOME DIRECAO_NOME ,E.ID DIRECAO_ID,
e.siga DIRECAO_CODIGO,D.NOME NOME_FUNCIONARIO, a.data_inicio, a.data_FIM,
a.estado, a.obs, a.uuid MOB_UUID, a.tipo_situacao, (
           SELECT LISTAGG(G.descricao, ',') WITHIN GROUP (ORDER BY G.descricao)
           FROM rh_t_domains G
           WHERE UPPER(TRIM(G.DOMINIO)) = 'TIPO_MOV_LABORAL'
             AND UPPER(TRIM(G.REFERENCIA)) LIKE 'MOBILIDADE%'
             AND G.ESTADO = 'A'
             AND G.valor IN (
                    SELECT TRIM(REGEXP_SUBSTR(A.tipo_situacao, '[^,]+', 1, LEVEL))
                    FROM dual
                    CONNECT BY REGEXP_SUBSTR(A.tipo_situacao, '[^,]+', 1, LEVEL) IS NOT NULL
             )
       ) AS tipo_situacao_desc,
(SELECT COUNT(1) FROM rh_t_tipos_relacionamento TP WHERE TP.MOB_ID =A.ID AND EXISTS (SELECT 1 FROM rh_t_proc_funcionarios PF WHERE PF.TIPREL_ID = TP.ID)) PROCESSAMENTO
FROM RH_T_MOBILIDADE A, rh_t_secao B, rh_t_param_local_trab C, RH_T_FUNCIONARIOS D, rh_t_direcao E
WHERE a.secao_id = B.ID(+)
AND a.local_trab_id = C.ID(+)
AND a.instit_id = e.id (+)
AND d.id = a.fun_id;

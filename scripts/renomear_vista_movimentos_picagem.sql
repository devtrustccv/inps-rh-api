-- =====================================================================
-- Renomear VW_RH_MOVIMENTOS_PICAGEM -> RH_V_MOVIMENTOS_PICAGEM (convenção RH_V_*)
--
-- 1) Criar a vista nova (mesmo SELECT da antiga, DDL de 29/10/2026).
-- 2) Depois de o Java novo (MovimentoEntity @Table RH_V_MOVIMENTOS_PICAGEM) estar
--    em produção nesse ambiente, apagar a antiga (passo 2, comentado).
-- A vista antiga não tem dependentes, grants nem sinónimos.
-- =====================================================================
CREATE OR REPLACE VIEW RH_V_MOVIMENTOS_PICAGEM AS
SELECT
    m.id,
    m.dt_movimento,
    m.hora_movimento,
    m.id_colaborador,
    m.nome_colaborador,
    '' as funcionario_uuid,
    m.tp_movimento,
    m.tp_movimento_desc,
    m.verify_mode,
    m.in_out_mode,
    m.work_code,
    m.dt_registo,
    m.usr_registo,
    m.data_hora,
    m.processado,
    m.tp_movimento_maquina,
    m.tp_movimento_maquina_desc,
    m.id_equip_contr_acesso,
    getLocalUserMovimento(m.id_equip_contr_acesso) AS local_movimento
FROM RH_MOVIMENTOS m
;

-- Passo 2 (só depois do deploy):
-- DROP VIEW VW_RH_MOVIMENTOS_PICAGEM;

-- =====================================================================
-- RH_V_CARREIRA  (lista de carreira, 1 linha por carreira)
-- =====================================================================
-- Base: RH_T_CARREIRA (A). Granularidade = 1 linha por registo de carreira
-- (CARREIRA_ID = A.ID e chave unica -> @Id da entidade RhVCarreiraEntity).
--
-- Sincronizado com a definicao REAL da BD (que evoluiu face a versoes
-- anteriores): CASE de ESTADO_CARREIRA, joins de situacao laboral (SIT/psit)
-- e o filtro REFERENCIA LIKE 'CARREIRA%' no TIPO_SITUACAO_DESC.
--
-- FIX (ORA-01427): a subquery escalar TIPO_SITUACAO_DESC usa MAX(dm.DESCRICAO).
-- Sem o MAX, quando um VALOR (ex.: 'INICIO') existe em >1 referencia CARREIRA%
-- no dominio (INICIO/CARREIRA + INICIO/CARREIRA_EDITAR, ambas 'Contrato
-- Inicial'), a subquery devolvia 2 linhas -> ORA-01427 em QUALQUER carreira
-- 'INICIO' (afetava 38 colaboradores). MAX colapsa para 1 (descricoes iguais)
-- e blinda a vista contra futuros duplicados do dominio.
-- =====================================================================
CREATE OR REPLACE VIEW RH_V_CARREIRA AS
SELECT A.DATA_INICIO, A.DATA_FIM, C.ID FUN_ID, C.UUID FUN_UUID, B.ID CONTRATO_ID, D.NOME TP_CONTRATO,
DECODE(A.CARGO_ID, NULL,'CATEGORIA','CARGO') TIPO_CARREIRA, E.NOME VINCULO_DESC, E.ID VINCULO_ID, F.NOME CARREIRA_DESC,
A.ID CARREIRA_ID, A.UUID CARREIRA_UUID, G.NOME CARGO_DESC, G.ID CARGO_ID, h.nivel_referencia||h.escalao ESCALAO_DESC, H.ID ESCALAO_ID,
H.VALOR SALARIO,  CASE
        WHEN a.estado = 'I'
             OR (a.data_fim IS NOT NULL
                 AND TRUNC(a.data_fim) < TRUNC(SYSDATE))
        THEN 'I'
        WHEN a.estado = 'P' THEN 'P'
        ELSE 'A'
    END AS ESTADO_CARREIRA,
(SELECT MAX(dm.DESCRICAO) FROM RH_T_DOMAINS dm WHERE dm.DOMINIO='TIPO_MOV_LABORAL' AND dm.VALOR = A.tipo_situacao AND dm.REFERENCIA LIKE 'CARREIRA%'  AND dm.ESTADO='A')
TIPO_SITUACAO_DESC, A.tipo_situacao TIPO_SITUACAO,
(SELECT COUNT(1) FROM rh_t_tipos_relacionamento TP WHERE TP.CARREIRA_ID =A.ID AND EXISTS (SELECT 1 FROM rh_t_proc_funcionarios PF WHERE PF.TIPREL_ID = TP.ID)) PROCESSAMENTO,
A.flg_processa, A.est_act_adm ultima_carreira,  sit.id situacao_laboral_id, psit.nome situacao_laboral_DESC
FROM RH_T_CARREIRA A, RH_T_CONTRATO_VINCULO B , RH_T_FUNCIONARIOS C, RH_T_PARAM_CONTRATO D,
RH_T_PARAM_VINCULO E, rh_t_param_carreira F, rh_t_param_cargo G, RH_T_PARAM_ESCALAO H,
rh_t_situacao_laboral SIT, rh_t_param_situacao psit
WHERE a.contr_vinculo_id = B.ID
AND B.FUN_ID = C.ID
AND D.ID = b.tp_contrato_id
AND E.ID = b.vinculo_id
AND f.id = a.carr_pccs_id
AND A.CARGO_ID = G.ID(+)
AND a.escalao_id = H.ID
AND a.contr_vinculo_id  = SIT.contr_vinculo_id
AND sit.situacao_laboral_id = PSIT.ID
AND SIT.ESTADO = 'A';

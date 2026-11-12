-- ---------------------------------------------------------------------------
-- Fix RH_V_CARREIRA: ORA-01427 (single-row subquery returns more than one row)
--
-- Causa: a subquery de TIPO_SITUACAO_DESC filtrava só por DOMINIO + VALOR. Como
-- RH_T_DOMAINS tem o mesmo VALOR (ex.: 'INICIO') em vários contextos distintos
-- via a coluna REFERENCIA (CARREIRA, MOBILIDADE, SITUACAO_LABORAL, ...), a
-- subquery escalar devolvia >1 linha para qualquer carreira com tipo_situacao
-- que exista em mais do que uma REFERENCIA -> ORA-01427 na lista de carreira.
--
-- Fix: filtrar também por REFERENCIA LIKE 'CARREIRA%' (cobre CARREIRA,
-- CARREIRA_NOVO, CARREIRA_PROG_PROMO, CARREIRA_EDITAR) + ESTADO='A'. Assim a
-- subquery devolve exatamente a descrição do contexto de carreira.
--
-- Aplicado no ambiente de desenvolvimento; replicar nos restantes.
-- ---------------------------------------------------------------------------
CREATE OR REPLACE VIEW RH_V_CARREIRA AS
SELECT A.DATA_INICIO, A.DATA_FIM, C.ID FUN_ID, C.UUID FUN_UUID, B.ID CONTRATO_ID, D.NOME TP_CONTRATO,
DECODE(A.CARGO_ID, NULL,'CATEGORIA','CARGO') TIPO_CARREIRA, E.NOME VINCULO_DESC, E.ID VINCULO_ID, F.NOME CARREIRA_DESC,
A.ID CARREIRA_ID, A.UUID CARREIRA_UUID, G.NOME CARGO_DESC, G.ID CARGO_ID, h.nivel_referencia||h.escalao ESCALAO_DESC, H.ID ESCALAO_ID,
H.VALOR SALARIO, A.ESTADO ESTADO_CARREIRA,
(SELECT dm.DESCRICAO FROM RH_T_DOMAINS dm WHERE dm.DOMINIO ='TIPO_MOV_LABORAL' AND dm.VALOR = A.tipo_situacao AND dm.REFERENCIA LIKE 'CARREIRA%' AND dm.ESTADO='A') TIPO_SITUACAO_DESC, A.tipo_situacao TIPO_SITUACAO,
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

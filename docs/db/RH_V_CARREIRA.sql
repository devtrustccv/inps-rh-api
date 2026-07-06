-- =====================================================================
-- RH_V_CARREIRA  (lista de carreira, 1 linha por carreira)
-- =====================================================================
-- Base: RH_T_CARREIRA (A). Granularidade = 1 linha por registo de carreira
-- (CARREIRA_ID = A.ID e chave unica -> @Id da entidade RhVCarreiraEntity).
--
-- Colunas novas face a versao anterior:
--   FUN_UUID       <- C.UUID  (uuidFuncionario; filtro da lista)
--   CARREIRA_UUID  <- A.UUID  (uuid; navegacao p/ detalhe)
--
-- Nota: `situacaoLaboral` (campo do DTO) NAO esta nesta vista -- vem do
-- tiprel (RH_T_TIPOS_RELACIONAMENTO -> RH_T_SITUACAO_LABORAL). Fica null na
-- lista ate ser adicionado a vista; nessa altura mapear no RhVCarreiraEntity.
-- ULTIMA_CARREIRA (= A.est_act_adm) marca a carreira corrente, mas nao e
-- exposta no DTO (o frontend so precisa de `estado`).
-- =====================================================================
CREATE OR REPLACE VIEW RH_V_CARREIRA AS
SELECT A.DATA_INICIO, A.DATA_FIM, C.ID FUN_ID, C.UUID FUN_UUID, B.ID CONTRATO_ID, D.NOME TP_CONTRATO,
       DECODE(A.CARGO_ID, NULL,'CATEGORIA','CARGO') TIPO_CARREIRA, E.NOME VINCULO_DESC, E.ID VINCULO_ID, F.NOME CARREIRA_DESC,
       A.ID CARREIRA_ID, A.UUID CARREIRA_UUID, G.NOME CARGO_DESC, G.ID CARGO_ID,
       h.nivel_referencia||h.escalao ESCALAO_DESC, H.ID ESCALAO_ID, H.VALOR SALARIO, A.ESTADO ESTADO_CARREIRA,
       (SELECT dm.DESCRICAO FROM RH_T_DOMAINS dm WHERE dm.DOMINIO ='TIPO_MOV_LABORAL' AND dm.VALOR = A.tipo_situacao) TIPO_SITUACAO_DESC,
       A.tipo_situacao TIPO_SITUACAO,
       (SELECT COUNT(1) FROM rh_t_tipos_relacionamento TP WHERE TP.CARREIRA_ID = A.ID AND EXISTS (SELECT 1 FROM rh_t_proc_funcionarios PF WHERE PF.TIPREL_ID = TP.ID)) PROCESSAMENTO,
       A.flg_processa, A.est_act_adm ULTIMA_CARREIRA
FROM RH_T_CARREIRA A, RH_T_CONTRATO_VINCULO B, RH_T_FUNCIONARIOS C, RH_T_PARAM_CONTRATO D,
     RH_T_PARAM_VINCULO E, rh_t_param_carreira F, rh_t_param_cargo G, RH_T_PARAM_ESCALAO H
WHERE a.contr_vinculo_id = B.ID
  AND B.FUN_ID = C.ID
  AND D.ID = b.tp_contrato_id
  AND E.ID = b.vinculo_id
  AND f.id = a.carr_pccs_id
  AND A.CARGO_ID = G.ID(+)
  AND a.escalao_id = H.ID;

-- =====================================================================
-- RH_V_CONTRATO  (lista de contrato "linha-a-linha")
-- =====================================================================
-- Granularidade: 1 linha por registo de RH_T_CONTRATO_HISTORICO (d).
--
-- Regras de origem das colunas:
--   HISTORICO_ID  <- d.id     CHAVE UNICA POR LINHA (@Id da entidade JPA;
--                             sem ela o Hibernate colapsa linhas com o mesmo
--                             CONTRATO_ID). Nao exposta ao frontend.
--   Campos por SITUACAO (do historico d): DATA_INICIO, DATA_FIM, ESTADO,
--     DURACAO, VERSAO, EST_ACT_ADM, TIPO_SITUACAO(=d.obs).
--   Campos de IDENTIDADE do contrato (de a/b/c/f): CONTRATO_ID, CONTRATO_UUID,
--     TIPO_CONTRATO, VINCULO, VINCULO_ID, FUN_ID, FUN_UUID, FLG_RENOVAVEL.
--
--   EST_ACT_ADM = 1 marca o historico ACTUAL (a situacao corrente); todos os
--   outros = 0. Deriva o campo `atual` da lista. Mantido pelo fluxo de escrita
--   (novo contrato / renovacao+validacao / mudanca de vinculo).
-- =====================================================================
create or replace view RH_V_CONTRATO as
select d.id                  HISTORICO_ID,
       b.nome                TIPO_CONTRATO,
       a.id                  CONTRATO_ID,
       a.uuid                CONTRATO_UUID,
       c.nome                VINCULO,
       c.id                  VINCULO_ID,
       d.DATA_INICIO         DATA_INICIO,
       d.DATA_FIM            DATA_FIM,
       a.fun_id              FUN_ID,
       f.uuid                FUN_UUID,
       d.estado              ESTADO,
       d.duracao             DURACAO,
       d.versao              VERSAO,
       d.est_act_adm         EST_ACT_ADM,
       b.flg_renovavel       FLG_RENOVAVEL,
       d.obs                 TIPO_SITUACAO,
       (SELECT dm.DESCRICAO
          FROM RH_T_DOMAINS dm
         WHERE dm.DOMINIO = 'TIPO_MOV_LABORAL'
           AND dm.VALOR = d.obs)                              TIPO_SITUACAO_DESC,
       (SELECT COUNT(1)
          FROM rh_t_tipos_relacionamento TP
         WHERE TP.CONTR_VINCULO_ID = a.ID
           AND EXISTS (SELECT 1
                         FROM rh_t_proc_funcionarios PF
                        WHERE PF.TIPREL_ID = TP.ID))          PROCESSAMENTO
  from rh_t_contrato_vinculo    a,
       rh_t_param_contrato      b,
       rh_t_param_vinculo       c,
       rh_t_contrato_historico  d,
       rh_t_funcionarios        f
 where a.tp_contrato_id = b.id
   and a.vinculo_id     = c.id
   and a.id             = d.contrato_id
   and a.fun_id         = f.id;

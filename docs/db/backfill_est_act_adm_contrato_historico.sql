-- =====================================================================
-- Backfill de EST_ACT_ADM em RH_T_CONTRATO_HISTORICO (dados existentes)
-- =====================================================================
-- Todos os historicos ficaram a 0 (default) ao adicionar a coluna.
-- Regra: marcar como ACTUAL (est_act_adm=1) o historico de MAIOR versao
-- de cada contrato que seja o contrato corrente do funcionario -- i.e. que
-- tenha um tipo_relacionamento com est_act_adm=1. Os restantes ficam 0.
-- Assim: um unico historico activo por funcionario (o do contrato corrente),
-- alinhado com o marcador de "corrente" ja existente no tipo_relacionamento.
-- =====================================================================
UPDATE rh_t_contrato_historico h
   SET h.est_act_adm = 1
 WHERE h.versao = (SELECT MAX(h2.versao)
                     FROM rh_t_contrato_historico h2
                    WHERE h2.contrato_id = h.contrato_id)
   AND EXISTS (SELECT 1
                 FROM rh_t_tipos_relacionamento tp
                WHERE tp.contr_vinculo_id = h.contrato_id
                  AND tp.est_act_adm = 1);

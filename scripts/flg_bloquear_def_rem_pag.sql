-- =====================================================================
-- Melhoria 25 — Bloqueio de remunerações/pagamentos do PCCS
--
-- FLG_BLOQUEAR = 1 nos movimentos fixos do vínculo quando o salário vem do PCCS
-- (RH_T_PARAM_VINCULO.FLG_SALARIO = 'SIM_PCCS'):
--   RH_T_DEF_REMUNERACOES : movimentos do vínculo com TIPO = 'REM' (vencimento)
--   RH_T_DEF_PAGAMENTOS   : movimentos do vínculo com TIPO = 'PAG' (IUR, INPS, valor líquido)
-- Registos bloqueados (ou já processados, DATA_ULTIMO_PROC preenchida) só permitem
-- alterar Data Fim e Observações.
-- =====================================================================

ALTER TABLE RH_T_DEF_REMUNERACOES ADD (FLG_BLOQUEAR NUMBER(1) DEFAULT 0 NOT NULL);

ALTER TABLE RH_T_DEF_PAGAMENTOS ADD (FLG_BLOQUEAR NUMBER(1) DEFAULT 0 NOT NULL);

-- Registos existentes (activos/pendentes) do vínculo actual PCCS
UPDATE RH_T_DEF_REMUNERACOES r
   SET r.FLG_BLOQUEAR = 1
 WHERE r.ESTADO IN ('A', 'P')
   AND EXISTS (
         SELECT 1
           FROM RH_T_TIPOS_RELACIONAMENTO tr
           JOIN RH_T_CONTRATO_VINCULO cv ON cv.ID = tr.CONTR_VINCULO_ID
           JOIN RH_T_PARAM_VINCULO pv ON pv.ID = cv.VINCULO_ID
           JOIN RH_T_PARAM_VINCULO_MOV m ON m.VINCULO_ID = pv.ID AND m.TIPO = 'REM' AND m.ESTADO = 'A'
          WHERE tr.FUN_ID = r.FUN_ID
            AND tr.EST_ACT_ADM = 1
            AND pv.FLG_SALARIO = 'SIM_PCCS'
            AND m.TM_ID = r.TM_ID);

UPDATE RH_T_DEF_PAGAMENTOS p
   SET p.FLG_BLOQUEAR = 1
 WHERE p.ESTADO IN ('A', 'P')
   AND EXISTS (
         SELECT 1
           FROM RH_T_TIPOS_RELACIONAMENTO tr
           JOIN RH_T_CONTRATO_VINCULO cv ON cv.ID = tr.CONTR_VINCULO_ID
           JOIN RH_T_PARAM_VINCULO pv ON pv.ID = cv.VINCULO_ID
           JOIN RH_T_PARAM_VINCULO_MOV m ON m.VINCULO_ID = pv.ID AND m.TIPO = 'PAG' AND m.ESTADO = 'A'
          WHERE tr.FUN_ID = p.FUN_ID
            AND tr.EST_ACT_ADM = 1
            AND pv.FLG_SALARIO = 'SIM_PCCS'
            AND m.TM_ID = p.TM_ID);

-- Limpeza: o vínculo 10 (Comissão de Serviço) tinha o PAG tm 1940 associado duas vezes
-- (IDs 51 e 52). Mantém-se o primeiro e o repetido passa a eliminado (soft delete).
UPDATE RH_T_PARAM_VINCULO_MOV m
   SET m.ESTADO = 'E'
 WHERE m.ESTADO = 'A'
   AND EXISTS (
         SELECT 1 FROM RH_T_PARAM_VINCULO_MOV o
          WHERE o.ESTADO = 'A'
            AND o.VINCULO_ID = m.VINCULO_ID
            AND o.TM_ID = m.TM_ID
            AND o.TIPO = m.TIPO
            AND o.ID < m.ID);

COMMIT;

-- =====================================================================
-- Backfill de ESTADO em RH_T_CONTRATO_HISTORICO (dados existentes)
-- =====================================================================
-- Alinha o `estado` com o `est_act_adm`: os historicos ja substituidos
-- (est_act_adm=0) que ainda estavam 'A' passam a 'I' ("Inactivo"), para a
-- lista nao mostrar versoes antigas / contratos desactivados como "Ativo".
-- Nao toca em historicos pendentes ('P') nem no historico corrente
-- (est_act_adm=1, que fica 'A').
-- =====================================================================
UPDATE rh_t_contrato_historico h
   SET h.estado = 'I'
 WHERE h.est_act_adm = 0
   AND h.estado = 'A';

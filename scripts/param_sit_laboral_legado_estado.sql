-- =====================================================================
-- RH_T_PARAM_SIT_LABORAL — associações vínculo↔situação do seed inicial com ESTADO NULL (23/10/2026)
-- =====================================================================
-- As linhas 2–12 foram inseridas por SQL directo (sem DATA_REGISTO nem utilizador), antes de a app
-- gerir as associações; a coluna ESTADO não tem default e ficaram a NULL. Nem o ecrã Parametrização →
-- Vínculo nem o combo do Dossiê as mostram (filtro ESTADO <> 'E' exclui NULL), e o RH reconfigurou os
-- vínculos sem elas — incluem ATIVO repetido 3× no Estágio Curricular.
-- Passam a 'E' (removidas): não muda nada no ecrã, só limpa a tabela.
-- PKG_IMPORTAR_DADOS (l.980) lê a tabela sem filtrar ESTADO — não é afectado.
-- Em dev/QA, a 23/10: ids 2, 4, 5, 6, 7, 8, 9, 10, 11, 12.
UPDATE RH_T_PARAM_SIT_LABORAL
   SET ESTADO = 'E', DATA_ALTERACAO = SYSTIMESTAMP, USER_ALTERACAO_NAME = 'script'
 WHERE ESTADO IS NULL

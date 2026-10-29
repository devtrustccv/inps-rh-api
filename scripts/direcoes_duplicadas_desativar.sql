-- Direções duplicadas em RH_T_DIRECAO
--
-- As 8 direções foram carregadas duas vezes no mesmo dia (10/06/2026): primeiro com os ids
-- reais (1000109xx, 16:39) e depois com ids 1 a 8 (16:51), mesmo NOME e SIGA, ambas ativas.
-- Por isso os combos de Direção mostram cada nome duas vezes e, escolhendo a cópia, a Unidade
-- não carrega (as unidades estão ligadas à direção real).
--
-- Referências às cópias (verificado em 29/10/2026 em todas as colunas INSTIT_ID / ID_DIRECAO /
-- DIRECAO_ID): só RH_T_SECAO (3 unidades) e RH_T_DIRECAO_CENTRO_CUSTO (8 linhas que repetem os
-- centros de custo das reais). Sem mobilidades, avaliações, manuais nem processamentos.
--
-- Correção (reversível): as 3 unidades passam para a direção real e as cópias ficam inativas.
-- As linhas de centro de custo das cópias ficam como estão (direção inativa).
--
-- Cópia -> real:  1 -> 100010979 (DPD)   2 -> 100010978 (DAD)   3 -> 100010973 (DARH)
--                 4 -> 100010972 (DFI)   5 -> 100010974 (DUPSN) 6 -> 100010976 (DCC)
--                 7 -> 100010971 (GSI)   8 -> 100010975 (DUPSS)

-- 1) Unidades presas às cópias (esperado: 3 linhas — ids 9 e 13 da DPD, 21 do GSI)
UPDATE RH_T_SECAO
   SET INSTIT_ID = CASE INSTIT_ID WHEN 1 THEN 100010979 WHEN 7 THEN 100010971 END
 WHERE INSTIT_ID IN (1, 7);

-- 2) Desativar as cópias (esperado: 8 linhas)
UPDATE RH_T_DIRECAO
   SET ESTADO = 'I'
 WHERE ID BETWEEN 1 AND 8
   AND ESTADO = 'A';

COMMIT;

-- Reverter, se necessário:
-- UPDATE RH_T_SECAO SET INSTIT_ID = 1 WHERE ID IN (9, 13);
-- UPDATE RH_T_SECAO SET INSTIT_ID = 7 WHERE ID = 21;
-- UPDATE RH_T_DIRECAO SET ESTADO = 'A' WHERE ID BETWEEN 1 AND 8;
-- COMMIT;

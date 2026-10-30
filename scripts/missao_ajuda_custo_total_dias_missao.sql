-- =====================================================================
-- MS-013 (decisão de 29/10/2026, opção 1): Valor Total da ajuda de custo = valor diário × nº de dias DA MISSÃO
-- (spec). Até aqui multiplicava pelos "Número Dias Alojamento" (RH_T_MISSAO_LOGISTICA.NR_DIAS).
-- Recalcula as linhas já gravadas. NÃO CORRIDO — rever antes: as linhas de processos já cabimentados,
-- autorizados ou pagos mudam de valor.
-- =====================================================================

-- 1) Pré-visualização: linhas cujo total muda
SELECT l.ID, m.NR_MISSAO || '/' || m.ANO NR, p.ETAPA, l.NR_DIAS DIAS_ALOJ, m.NR_DIAS DIAS_MISSAO,
       l.VALOR_DIARIO, l.VALOR_TOTAL TOTAL_ATUAL, l.VALOR_DIARIO * m.NR_DIAS TOTAL_NOVO
  FROM RH_T_MISSAO_LOGISTICA l
  JOIN RH_T_MISSAO_SERVICO m ON m.ID = l.MISSAO_SERV_ID
  JOIN RH_T_MISSAO_PROCESSO p ON p.ID = l.MISSAO_PROCESSO_ID
 WHERE l.REFERENCIA = 'AJUDA_CUSTO' AND l.ESTADO = 'A'
   AND NVL(l.VALOR_TOTAL, -1) <> l.VALOR_DIARIO * m.NR_DIAS
 ORDER BY l.ID;

-- 2) Recálculo
UPDATE RH_T_MISSAO_LOGISTICA l
   SET l.VALOR_TOTAL = l.VALOR_DIARIO * (SELECT m.NR_DIAS FROM RH_T_MISSAO_SERVICO m WHERE m.ID = l.MISSAO_SERV_ID)
 WHERE l.REFERENCIA = 'AJUDA_CUSTO' AND l.ESTADO = 'A'
   AND NVL(l.VALOR_TOTAL, -1) <> l.VALOR_DIARIO * (SELECT m.NR_DIAS FROM RH_T_MISSAO_SERVICO m WHERE m.ID = l.MISSAO_SERV_ID);

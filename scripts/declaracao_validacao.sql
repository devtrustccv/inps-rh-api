-- =====================================================================
-- Pedido Declaração: o pedido passa a entrar na lista de Validação (RH_T_VALIDACAO,
-- REFERENCIA_NAME = DECLARACAO). Acrescenta o valor ao domínio do filtro "Referente".
-- =====================================================================
INSERT INTO RH_T_DOMAINS (DOMINIO, VALOR, DESCRICAO, REFERENCIA, ESTADO, DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
SELECT 'ACCAO_REFERENTE', 'DECLARACAO', TO_CHAR(UNISTR('Declara\00E7\00E3o')), 'VALIDACAO', 'A', SYSDATE, 1, 'script' FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM RH_T_DOMAINS WHERE DOMINIO = 'ACCAO_REFERENTE' AND REFERENCIA = 'VALIDACAO' AND VALOR = 'DECLARACAO');

COMMIT;

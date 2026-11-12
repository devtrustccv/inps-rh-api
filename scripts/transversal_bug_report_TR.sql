-- =====================================================================
-- Bug report QA Transversal (OUTUBRO, 23/10/2026) — dados. Cada bloco é um statement.
-- Correr ANTES da API nova (o estado das notificações passa a ser o código do domínio).
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) TR-22: estado das notificações = valor do domínio ESTADO_NOTIFICACAO.
--    O domínio só tinha POR_ENVIAR / ENVIADO; acrescenta-se ERRO (falha no envio do email).
-- ---------------------------------------------------------------------
INSERT INTO RH_T_DOMAINS (DOMINIO, VALOR, DESCRICAO, REFERENCIA, ESTADO, DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
SELECT 'ESTADO_NOTIFICACAO', 'ERRO', 'Erro', 'ESTADO_NOTIFICACAO', 'A', SYSDATE, 1, 'script' FROM DUAL
 WHERE NOT EXISTS (SELECT 1 FROM RH_T_DOMAINS WHERE DOMINIO = 'ESTADO_NOTIFICACAO' AND VALOR = 'ERRO');

-- Texto livre gravado até agora → código do domínio. Em dev/QA, a 23/10: Pendente 58, Erro 62.
UPDATE RH_T_NOTIFICACAO
   SET ESTADO = CASE ESTADO WHEN 'Pendente' THEN 'POR_ENVIAR' WHEN 'Enviado' THEN 'ENVIADO' WHEN 'Erro' THEN 'ERRO' END
 WHERE ESTADO IN ('Pendente', 'Enviado', 'Erro');

-- ---------------------------------------------------------------------
-- 2) TR-20/24: tipos gravados pelo JOB Alerta e pela missão que faltavam em TIPO_ALERTA_NOTIFICACAO
--    (sem eles a lista mostra o código e o filtro Tipo não os oferece).
-- ---------------------------------------------------------------------
INSERT INTO RH_T_DOMAINS (DOMINIO, VALOR, DESCRICAO, REFERENCIA, ESTADO, DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
SELECT 'TIPO_ALERTA_NOTIFICACAO', v.valor, v.descricao, v.referencia, 'A', SYSDATE, 1, 'script'
  FROM (SELECT 'CONVERSAO_CONTRATO' valor, TO_CHAR(UNISTR('Convers\00E3o de Contrato')) descricao, 'CONTRATO' referencia FROM DUAL
        UNION ALL SELECT 'RENOVACAO_CONTRATO', TO_CHAR(UNISTR('Renova\00E7\00E3o de Contrato')), 'CONTRATO' FROM DUAL
        UNION ALL SELECT 'LICENCA_S_VENCIMENTO', TO_CHAR(UNISTR('Licen\00E7a sem Vencimento')), 'CONTRATO' FROM DUAL
        UNION ALL SELECT 'MISSAO_CANCELAMENTO', TO_CHAR(UNISTR('Miss\00E3o - Cancelamento')), TO_CHAR(UNISTR('MISS\00C3O')) FROM DUAL) v
 WHERE NOT EXISTS (SELECT 1 FROM RH_T_DOMAINS d WHERE d.DOMINIO = 'TIPO_ALERTA_NOTIFICACAO' AND d.VALOR = v.valor);

-- ---------------------------------------------------------------------
-- 3) Dados de teste do QA (só dev/QA — NÃO correr noutros ambientes sem rever os ids).
-- ---------------------------------------------------------------------
-- Dois documentos de saída ativos para TRANSFERENCIA (a OS de Transferência rebentava): o 32 é teste
-- do QA (corpo "tess22"); o 15 é o template real e fica.
UPDATE RH_T_PARAM_DOC_OUTPUT SET ESTADO = 'I', DATA_ALTERACAO = SYSDATE, USER_ALTERACAO_NAME = 'script'
 WHERE ID = 32 AND TIPO_DOCUMENTO = 'TRANSFERENCIA' AND ESTADO = 'A';

-- Pedido de declaração 15 gravado com JUSTIFICAR_FALTA (domínio TIPO_PEDIDO). Finalidade = crédito ou
-- empréstimo, entidade = banco → PEDIDO_CREDITO_BANCARIO, para poder ser reaberto e ter PDF.
UPDATE RH_T_DECLARACAO SET TIPO_DECLARACAO = 'PEDIDO_CREDITO_BANCARIO'
 WHERE ID = 15 AND TIPO_DECLARACAO = 'JUSTIFICAR_FALTA';

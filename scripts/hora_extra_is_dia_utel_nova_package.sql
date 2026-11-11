-- =====================================================================
-- Hora Extra — RH_V_HORA_EXTRA_MENSAL / RH_V_HORA_EXTRA_PEDIDO inválidas (07/11/2026)
--
-- Causa: a 04/11/2026 IS_DIA_UTEL saiu de RH_PROCESSAMENTO_SALARIAL_DB e passou para
-- RH_PK_FUNCOES_PROC_GERAL_DB (o próprio corpo da package já a chama lá). A vista mensal
-- ainda chamava RH_PROCESSAMENTO_SALARIAL_DB.IS_DIA_UTEL -> ORA-00904, e a vista do pedido,
-- que depende dela, ficou inválida em cascata (ORA-04063). Todos os endpoints de hora extra
-- davam 500.
--
-- Correção: recriar a vista mensal a partir do DDL EM VIGOR, trocando só o nome da package,
-- e recompilar a vista do pedido. Não mexe em packages nem procedures.
-- =====================================================================

-- 1. Confirmar que a função é pública na nova package (deve devolver 1 linha)
SELECT object_name, procedure_name
  FROM user_procedures
 WHERE object_name = 'RH_PK_FUNCOES_PROC_GERAL_DB'
   AND procedure_name = 'IS_DIA_UTEL';

-- 2. Recriar a vista mensal com o nome da package corrigido
DECLARE
  v_ddl CLOB;
BEGIN
  DBMS_METADATA.SET_TRANSFORM_PARAM(DBMS_METADATA.SESSION_TRANSFORM, 'SQLTERMINATOR', FALSE);
  v_ddl := DBMS_METADATA.GET_DDL('VIEW', 'RH_V_HORA_EXTRA_MENSAL');
  v_ddl := REPLACE(v_ddl, 'RH_PROCESSAMENTO_SALARIAL_DB.IS_DIA_UTEL', 'RH_PK_FUNCOES_PROC_GERAL_DB.IS_DIA_UTEL');
  v_ddl := REPLACE(v_ddl, '"RH_PROCESSAMENTO_SALARIAL_DB"."IS_DIA_UTEL"', '"RH_PK_FUNCOES_PROC_GERAL_DB"."IS_DIA_UTEL"');
  EXECUTE IMMEDIATE v_ddl;
END;
/

-- 3. Recompilar a vista que depende dela
ALTER VIEW RH_V_HORA_EXTRA_PEDIDO COMPILE;

-- 4. Verificar: as duas devem ficar VALID e o COUNT não deve dar erro
SELECT object_name, status
  FROM user_objects
 WHERE object_name IN ('RH_V_HORA_EXTRA_MENSAL', 'RH_V_HORA_EXTRA_PEDIDO');

SELECT COUNT(*) FROM RH_V_HORA_EXTRA_MENSAL;
SELECT COUNT(*) FROM RH_V_HORA_EXTRA_PEDIDO;

-- =====================================================================
-- Parametrização de Bancos — acertar SEQ_RH_T_BANCO (09/11/2026)
--
-- A sequência está em LAST_NUMBER = 1, mas a RH_T_BANCO já tem os ids 1, 2 e 6
-- (inseridos à mão). O primeiro banco criado pela aplicação daria ORA-00001 em
-- PK_RH_T_BANCO. Oracle 11g não tem ALTER SEQUENCE ... RESTART, por isso avança-se
-- a sequência com um incremento temporário até MAX(id). Não mexe em dados.
-- =====================================================================

DECLARE
  v_max  NUMBER;
  v_next NUMBER;
BEGIN
  SELECT NVL(MAX(id), 0) INTO v_max FROM RH_T_BANCO;
  SELECT SEQ_RH_T_BANCO.NEXTVAL INTO v_next FROM dual;
  IF v_next < v_max THEN
    EXECUTE IMMEDIATE 'ALTER SEQUENCE SEQ_RH_T_BANCO INCREMENT BY ' || (v_max - v_next);
    SELECT SEQ_RH_T_BANCO.NEXTVAL INTO v_next FROM dual;
    EXECUTE IMMEDIATE 'ALTER SEQUENCE SEQ_RH_T_BANCO INCREMENT BY 1';
  END IF;
  DBMS_OUTPUT.PUT_LINE('MAX(id) = ' || v_max || ', último valor da sequência = ' || v_next);
END;
/

-- Verificar: LAST_NUMBER deve ficar acima do MAX(id)
SELECT last_number, increment_by FROM user_sequences WHERE sequence_name = 'SEQ_RH_T_BANCO';
SELECT MAX(id) FROM RH_T_BANCO;

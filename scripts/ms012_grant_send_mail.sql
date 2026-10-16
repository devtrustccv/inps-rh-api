-- =====================================================================
-- MS-012 — Emails da Missão de Serviço ficam em "Erro"
--
-- A API envia email com:  BEGIN sipsv0.SEND_MAIL_V1(p_from_name, p_to_name, p_subject,
--                                                    p_message_body, p_cc_name, p_message_type); END;
-- (OracleEmailService). No ambiente QA o utilizador INPSRH não vê este procedimento
-- (ALL_OBJECTS sem SEND_MAIL_V1, sem privilégios em USER_TAB_PRIVS), por isso todos os
-- envios falham. Em RH_T_NOTIFICACAO nunca houve um registo "Enviado" neste ambiente.
--
-- Executar como DBA ou como SIPSV0.
-- =====================================================================

-- 1) O procedimento existe neste ambiente?
SELECT OWNER, OBJECT_NAME, OBJECT_TYPE, STATUS
  FROM DBA_OBJECTS
 WHERE OWNER = 'SIPSV0' AND OBJECT_NAME = 'SEND_MAIL_V1';
-- Sem linhas: o procedimento tem de ser criado em SIPSV0 (copiar de produção) antes do passo 2.
-- STATUS = INVALID: recompilar (ALTER PROCEDURE SIPSV0.SEND_MAIL_V1 COMPILE;).

-- 2) Dar ao INPSRH permissão para o executar.
GRANT EXECUTE ON SIPSV0.SEND_MAIL_V1 TO INPSRH;

-- 3) O SEND_MAIL_V1 usa UTL_SMTP: o dono (SIPSV0) precisa de EXECUTE em UTL_SMTP e de uma ACL
--    de rede para o servidor SMTP. Confirmar (como DBA):
SELECT ACL, HOST, LOWER_PORT, UPPER_PORT FROM DBA_NETWORK_ACLS;
SELECT ACL, PRINCIPAL, PRIVILEGE, IS_GRANT FROM DBA_NETWORK_ACL_PRIVILEGES WHERE PRINCIPAL = 'SIPSV0';
-- Se faltar (Oracle 11g), por exemplo:
-- BEGIN
--   DBMS_NETWORK_ACL_ADMIN.CREATE_ACL(acl => 'smtp_sipsv0.xml', description => 'SMTP SIPSV0',
--                                     principal => 'SIPSV0', is_grant => TRUE, privilege => 'connect');
--   DBMS_NETWORK_ACL_ADMIN.ASSIGN_ACL(acl => 'smtp_sipsv0.xml', host => '<servidor_smtp>',
--                                     lower_port => 25, upper_port => 25);
--   COMMIT;
-- END;
-- /

-- 4) Verificação (como INPSRH): deve devolver 1 linha.
-- SELECT OWNER, OBJECT_NAME FROM ALL_OBJECTS WHERE OWNER = 'SIPSV0' AND OBJECT_NAME = 'SEND_MAIL_V1';

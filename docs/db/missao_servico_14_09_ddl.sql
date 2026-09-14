-- =============================================================================
-- Missão de Serviço — modelo por processo (spec 14/09/2026)
--
-- Base: "Especificação Tecnica Funcional - MISSÃO SERVIÇO_14_09_26.md"
--       "Especificação Tecnica Funcional - BASE DADOS_14_09_26.md"
-- Schema: INPSRH (Oracle 11g XE — sem IDENTITY: sequência + trigger)
--
-- APLICADO EM: desenvolvimento (INPSRH).
-- POR APLICAR EM: staging, produção.
--
-- Flyway desligado: correr à mão, pela ordem. Cada statement termina com uma
-- linha "/" (compatível com SQL*Plus / SQL Developer).
--
-- ALTERAÇÕES SÓ ADITIVAS. As colunas do modelo antigo (MISSAO_SERV_ID na
-- logística e no prestador, ENT_ID/NOME/EMAIL no prestador, MISSAO_COLAB_ID na
-- requisição) mantêm-se enquanto o código não migrar; são removidas num script
-- posterior.
--
-- Desvios deliberados ao documento de BD 14/09 (a spec funcional prevalece):
--   * RH_T_PARAM_PRESTADOR ganha NOME, NIF, TELEFONE — usados no registo de
--     prestador e na nota de encomenda, ausentes no doc de BD.
--   * RH_T_MISSAO_REQUISICAO_COLAB — na spec, ausente no doc de BD.
--   * Nomes normalizados: RH_T_MISSAO_PROCESSO_DET (doc: MISSSAO),
--     RESPONSAVEL (doc: REPONSAVEL), MISSAO_SERV_ID (doc: MISSA_SERVICO_ID),
--     MISSAO_PREST_ID na avaliação (doc: MISSAO_PRESTADOR_ID) e
--     MISSAO_PROCESSO_ID no prestador (doc: MISSAO_PREST_ID).
--   * RH_T_MISSAO_PRESTADOR_AVAL.TOTAL passa a NUMBER (doc: VARCHAR(50)) — é
--     uma soma de pontos.
--   * RH_T_MISSAO_REQUISICAO.ANO — suporta o nº de requisição sequencial anual.
-- =============================================================================


-- -----------------------------------------------------------------------------
-- 1. RH_T_PARAM_PRESTADOR — registo de prestadores de serviço (menu próprio)
-- -----------------------------------------------------------------------------
CREATE TABLE RH_T_PARAM_PRESTADOR (
  ID                  NUMBER         NOT NULL,
  ENT_ID              NUMBER         NOT NULL,
  NOME                VARCHAR2(200)  NOT NULL,
  NIF                 VARCHAR2(20),
  EMAIL               VARCHAR2(200)  NOT NULL,
  TELEFONE            VARCHAR2(30),
  ILHA_ID             NUMBER,
  MORADA              VARCHAR2(300),
  ESTADO              VARCHAR2(1)    NOT NULL,
  DATA_REGISTO        DATE           NOT NULL,
  USER_REGISTO_ID     NUMBER         NOT NULL,
  USER_REGISTO_NAME   VARCHAR2(200)  NOT NULL,
  USER_ALTERACAO_ID   NUMBER,
  USER_ALTERACAO_NAME VARCHAR2(200),
  DATA_ALTERACAO      DATE,
  UUID                VARCHAR2(36)   NOT NULL,
  CONSTRAINT PK_PARAM_PRESTADOR PRIMARY KEY (ID)
) TABLESPACE TBSINPSDB
/
COMMENT ON TABLE RH_T_PARAM_PRESTADOR IS 'Prestadores de serviço (agências de viagem) — ENT_ID refere INPSSIGOF.ENTIDADES.ID'
/
CREATE INDEX IX_PARAM_PRESTADOR_UUID ON RH_T_PARAM_PRESTADOR (UUID) TABLESPACE TBSINPSDB
/
CREATE SEQUENCE SEQ_PARAM_PRESTADOR START WITH 1 INCREMENT BY 1 NOCACHE
/
CREATE OR REPLACE TRIGGER TRG_PARAM_PRESTADOR
  BEFORE INSERT ON RH_T_PARAM_PRESTADOR
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT SEQ_PARAM_PRESTADOR.NEXTVAL INTO :NEW.ID FROM DUAL;
  END IF;
  :NEW.DATA_REGISTO := SYSDATE;
END;
/


-- -----------------------------------------------------------------------------
-- 2. RH_T_PARAM_PRESTADOR_DET — emails adicionais do prestador
-- -----------------------------------------------------------------------------
CREATE TABLE RH_T_PARAM_PRESTADOR_DET (
  ID                  NUMBER         NOT NULL,
  PARAM_PREST_ID      NUMBER         NOT NULL,
  EMAIL               VARCHAR2(200)  NOT NULL,
  ESTADO              VARCHAR2(1)    NOT NULL,
  DATA_REGISTO        DATE           NOT NULL,
  USER_REGISTO_ID     NUMBER         NOT NULL,
  USER_REGISTO_NAME   VARCHAR2(200)  NOT NULL,
  USER_ALTERACAO_ID   NUMBER,
  USER_ALTERACAO_NAME VARCHAR2(200),
  DATA_ALTERACAO      DATE,
  UUID                VARCHAR2(36)   NOT NULL,
  CONSTRAINT PK_PARAM_PRESTADOR_DET PRIMARY KEY (ID),
  CONSTRAINT FK_PARAM_PRESTADOR_DET FOREIGN KEY (PARAM_PREST_ID) REFERENCES RH_T_PARAM_PRESTADOR (ID)
) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_PARAM_PRESTADOR_DET_UUID ON RH_T_PARAM_PRESTADOR_DET (UUID) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_PARAM_PRESTADOR_DET_PREST ON RH_T_PARAM_PRESTADOR_DET (PARAM_PREST_ID) TABLESPACE TBSINPSDB
/
CREATE SEQUENCE SEQ_PARAM_PRESTADOR_DET START WITH 1 INCREMENT BY 1 NOCACHE
/
CREATE OR REPLACE TRIGGER TRG_PARAM_PRESTADOR_DET
  BEFORE INSERT ON RH_T_PARAM_PRESTADOR_DET
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT SEQ_PARAM_PRESTADOR_DET.NEXTVAL INTO :NEW.ID FROM DUAL;
  END IF;
  :NEW.DATA_REGISTO := SYSDATE;
END;
/


-- -----------------------------------------------------------------------------
-- 3. RH_T_MISSAO_PROCESSO — os 4 processos de cada missão, cada um com a sua etapa
--
-- TIPO_PROCESSO: BILHETE_PASSAGEM | SEGURO_VIAGEM | AJUDA_CUSTO | ALOJAMENTO
-- Um processo de cada tipo por missão (UX_MISSAO_PROCESSO_TIPO).
-- -----------------------------------------------------------------------------
CREATE TABLE RH_T_MISSAO_PROCESSO (
  ID                  NUMBER         NOT NULL,
  MISSAO_SERV_ID      NUMBER         NOT NULL,
  TIPO_PROCESSO       VARCHAR2(100)  NOT NULL,
  ETAPA               VARCHAR2(100)  NOT NULL,
  ESTADO              VARCHAR2(1)    NOT NULL,
  DATA_REGISTO        DATE           NOT NULL,
  USER_REGISTO_ID     NUMBER         NOT NULL,
  USER_REGISTO_NAME   VARCHAR2(200)  NOT NULL,
  USER_ALTERACAO_ID   NUMBER,
  USER_ALTERACAO_NAME VARCHAR2(200),
  DATA_ALTERACAO      DATE,
  UUID                VARCHAR2(36)   NOT NULL,
  CONSTRAINT PK_MISSAO_PROCESSO PRIMARY KEY (ID),
  CONSTRAINT FK_MISSAO_PROCESSO_SERV FOREIGN KEY (MISSAO_SERV_ID) REFERENCES RH_T_MISSAO_SERVICO (ID)
) TABLESPACE TBSINPSDB
/
CREATE UNIQUE INDEX UX_MISSAO_PROCESSO_TIPO ON RH_T_MISSAO_PROCESSO (MISSAO_SERV_ID, TIPO_PROCESSO) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_MISSAO_PROCESSO_UUID ON RH_T_MISSAO_PROCESSO (UUID) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_MISSAO_PROCESSO_ETAPA ON RH_T_MISSAO_PROCESSO (ETAPA, TIPO_PROCESSO) TABLESPACE TBSINPSDB
/
CREATE SEQUENCE SEQ_MISSAO_PROCESSO START WITH 1 INCREMENT BY 1 NOCACHE
/
CREATE OR REPLACE TRIGGER TRG_MISSAO_PROCESSO
  BEFORE INSERT ON RH_T_MISSAO_PROCESSO
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT SEQ_MISSAO_PROCESSO.NEXTVAL INTO :NEW.ID FROM DUAL;
  END IF;
  :NEW.DATA_REGISTO := SYSDATE;
END;
/


-- -----------------------------------------------------------------------------
-- 4. RH_T_MISSAO_PROCESSO_DET — pareceres (Validação UGAL, Aprovação RH)
--
-- RESPONSAVEL: UGAL | COORDENADOR_RH | DIRECTOR_RH
-- -----------------------------------------------------------------------------
CREATE TABLE RH_T_MISSAO_PROCESSO_DET (
  ID                  NUMBER         NOT NULL,
  MISSAO_PROCESSO_ID  NUMBER         NOT NULL,
  PARECER             VARCHAR2(50)   NOT NULL,
  OBSERVACAO          VARCHAR2(500),
  RESPONSAVEL         VARCHAR2(50)   NOT NULL,
  ESTADO              VARCHAR2(1)    NOT NULL,
  DATA_REGISTO        DATE           NOT NULL,
  USER_REGISTO_ID     NUMBER         NOT NULL,
  USER_REGISTO_NAME   VARCHAR2(200)  NOT NULL,
  USER_ALTERACAO_ID   NUMBER,
  USER_ALTERACAO_NAME VARCHAR2(200),
  DATA_ALTERACAO      DATE,
  UUID                VARCHAR2(36)   NOT NULL,
  CONSTRAINT PK_MISSAO_PROCESSO_DET PRIMARY KEY (ID),
  CONSTRAINT FK_MISSAO_PROCESSO_DET FOREIGN KEY (MISSAO_PROCESSO_ID) REFERENCES RH_T_MISSAO_PROCESSO (ID)
) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_MISSAO_PROCESSO_DET_UUID ON RH_T_MISSAO_PROCESSO_DET (UUID) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_MISSAO_PROCESSO_DET_PROC ON RH_T_MISSAO_PROCESSO_DET (MISSAO_PROCESSO_ID) TABLESPACE TBSINPSDB
/
CREATE SEQUENCE SEQ_MISSAO_PROCESSO_DET START WITH 1 INCREMENT BY 1 NOCACHE
/
CREATE OR REPLACE TRIGGER TRG_MISSAO_PROCESSO_DET
  BEFORE INSERT ON RH_T_MISSAO_PROCESSO_DET
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT SEQ_MISSAO_PROCESSO_DET.NEXTVAL INTO :NEW.ID FROM DUAL;
  END IF;
  :NEW.DATA_REGISTO := SYSDATE;
END;
/


-- -----------------------------------------------------------------------------
-- 5. RH_T_MISSAO_REQUISICAO_COLAB — colaboradores de cada requisição
--
-- A requisição passa a ser uma por prestador (com N colaboradores), em vez de
-- uma linha por par prestador × colaborador.
-- -----------------------------------------------------------------------------
CREATE TABLE RH_T_MISSAO_REQUISICAO_COLAB (
  ID                    NUMBER         NOT NULL,
  MISSAO_REQUISICAO_ID  NUMBER         NOT NULL,
  MISSAO_COLAB_ID       NUMBER         NOT NULL,
  ESTADO                VARCHAR2(1)    NOT NULL,
  DATA_REGISTO          DATE           NOT NULL,
  USER_REGISTO_ID       NUMBER         NOT NULL,
  USER_REGISTO_NAME     VARCHAR2(200)  NOT NULL,
  USER_ALTERACAO_ID     NUMBER,
  USER_ALTERACAO_NAME   VARCHAR2(200),
  DATA_ALTERACAO        DATE,
  UUID                  VARCHAR2(36)   NOT NULL,
  CONSTRAINT PK_MISSAO_REQUISICAO_COLAB PRIMARY KEY (ID),
  CONSTRAINT FK_MIS_REQ_COLAB_REQ FOREIGN KEY (MISSAO_REQUISICAO_ID) REFERENCES RH_T_MISSAO_REQUISICAO (ID),
  CONSTRAINT FK_MIS_REQ_COLAB_COLAB FOREIGN KEY (MISSAO_COLAB_ID) REFERENCES RH_T_MISSAO_COLABORADOR (ID)
) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_MIS_REQ_COLAB_UUID ON RH_T_MISSAO_REQUISICAO_COLAB (UUID) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_MIS_REQ_COLAB_REQ ON RH_T_MISSAO_REQUISICAO_COLAB (MISSAO_REQUISICAO_ID) TABLESPACE TBSINPSDB
/
CREATE SEQUENCE SEQ_MISSAO_REQUISICAO_COLAB START WITH 1 INCREMENT BY 1 NOCACHE
/
CREATE OR REPLACE TRIGGER TRG_MISSAO_REQUISICAO_COLAB
  BEFORE INSERT ON RH_T_MISSAO_REQUISICAO_COLAB
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT SEQ_MISSAO_REQUISICAO_COLAB.NEXTVAL INTO :NEW.ID FROM DUAL;
  END IF;
  :NEW.DATA_REGISTO := SYSDATE;
END;
/


-- -----------------------------------------------------------------------------
-- 6. RH_T_MISSAO_PRESTADOR_AVAL — avaliação do prestador numa missão
--
-- Critérios guardam a avaliação (domínio AVALIACAO_FORNECEDOR): MUITO_BOM,
-- BOM, SATISFAZ, MAU. TOTAL = soma ponderada (0–100). DESIGNACAO = classe A–D.
-- -----------------------------------------------------------------------------
CREATE TABLE RH_T_MISSAO_PRESTADOR_AVAL (
  ID                  NUMBER         NOT NULL,
  MISSAO_PREST_ID     NUMBER         NOT NULL,
  SISTEMA_QUALIDADE   VARCHAR2(50)   NOT NULL,
  PRAZO_FORNECIMENTO  VARCHAR2(50)   NOT NULL,
  QUALIDADE_PRODUTO   VARCHAR2(50)   NOT NULL,
  CAPACIDADE_RESPOSTA VARCHAR2(50)   NOT NULL,
  PRECO               VARCHAR2(50)   NOT NULL,
  TOTAL               NUMBER(5,2)    NOT NULL,
  DESIGNACAO          VARCHAR2(50)   NOT NULL,
  ESTADO              VARCHAR2(1)    NOT NULL,
  DATA_REGISTO        DATE           NOT NULL,
  USER_REGISTO_ID     NUMBER         NOT NULL,
  USER_REGISTO_NAME   VARCHAR2(200)  NOT NULL,
  USER_ALTERACAO_ID   NUMBER,
  USER_ALTERACAO_NAME VARCHAR2(200),
  DATA_ALTERACAO      DATE,
  UUID                VARCHAR2(36)   NOT NULL,
  CONSTRAINT PK_MISSAO_PREST_AVAL PRIMARY KEY (ID),
  CONSTRAINT FK_MISSAO_PREST_AVAL FOREIGN KEY (MISSAO_PREST_ID) REFERENCES RH_T_MISSAO_PRESTADOR (ID)
) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_MISSAO_PREST_AVAL_UUID ON RH_T_MISSAO_PRESTADOR_AVAL (UUID) TABLESPACE TBSINPSDB
/
CREATE INDEX IX_MISSAO_PREST_AVAL_PREST ON RH_T_MISSAO_PRESTADOR_AVAL (MISSAO_PREST_ID) TABLESPACE TBSINPSDB
/
CREATE SEQUENCE SEQ_MISSAO_PRESTADOR_AVAL START WITH 1 INCREMENT BY 1 NOCACHE
/
CREATE OR REPLACE TRIGGER TRG_MISSAO_PRESTADOR_AVAL
  BEFORE INSERT ON RH_T_MISSAO_PRESTADOR_AVAL
  FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT SEQ_MISSAO_PRESTADOR_AVAL.NEXTVAL INTO :NEW.ID FROM DUAL;
  END IF;
  :NEW.DATA_REGISTO := SYSDATE;
END;
/


-- -----------------------------------------------------------------------------
-- 7. RH_T_MISSAO_PRESTADOR — liga ao prestador parametrizado e ao processo
--
-- Nullable nesta fase: as linhas existentes são do modelo antigo (por missão).
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_MISSAO_PRESTADOR ADD (PARAM_PREST_ID NUMBER, MISSAO_PROCESSO_ID NUMBER)
/
ALTER TABLE RH_T_MISSAO_PRESTADOR ADD CONSTRAINT FK_MIS_PRESTADOR_PARAM
  FOREIGN KEY (PARAM_PREST_ID) REFERENCES RH_T_PARAM_PRESTADOR (ID)
/
ALTER TABLE RH_T_MISSAO_PRESTADOR ADD CONSTRAINT FK_MIS_PRESTADOR_PROC
  FOREIGN KEY (MISSAO_PROCESSO_ID) REFERENCES RH_T_MISSAO_PROCESSO (ID)
/
CREATE INDEX IX_MIS_PRESTADOR_PROC ON RH_T_MISSAO_PRESTADOR (MISSAO_PROCESSO_ID) TABLESPACE TBSINPSDB
/


-- -----------------------------------------------------------------------------
-- 8. RH_T_MISSAO_LOGISTICA — liga ao processo
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_MISSAO_LOGISTICA ADD (MISSAO_PROCESSO_ID NUMBER)
/
ALTER TABLE RH_T_MISSAO_LOGISTICA ADD CONSTRAINT FK_MIS_LOGIST_PROC
  FOREIGN KEY (MISSAO_PROCESSO_ID) REFERENCES RH_T_MISSAO_PROCESSO (ID)
/
CREATE INDEX IX_MIS_LOGIST_PROC ON RH_T_MISSAO_LOGISTICA (MISSAO_PROCESSO_ID) TABLESPACE TBSINPSDB
/


-- -----------------------------------------------------------------------------
-- 9. RH_T_MISSAO_REQUISICAO — ano do nº de requisição (sequencial anual)
--
-- NR_REQUISACAO e VALOR_TOTAL já existem (aplicados pelo DBA a 11/09).
-- A tabela está vazia, por isso não há backfill.
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_MISSAO_REQUISICAO ADD (ANO NUMBER(4))
/


-- -----------------------------------------------------------------------------
-- 10. RH_T_MISSAO_SERVICO.ESTADO — cabe 'FINALIZADO'
--
-- A spec escreve ESTADO = 'FINALIZADO' quando o pagamento fecha o processo;
-- a coluna era VARCHAR2(1) ('A' activa / 'I' cancelada).
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_MISSAO_SERVICO MODIFY (ESTADO VARCHAR2(20))
/


-- -----------------------------------------------------------------------------
-- 11. RH_T_MISSAO_LOGISTICA.MISSAO_PREST_ID passa a opcional
--
-- Decisão D2: seguro de viagem e ajuda de custo não passam por Prestadores
-- Serviço nem por Emissão de Requisição — a linha de logística não tem prestador.
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_MISSAO_LOGISTICA MODIFY (MISSAO_PREST_ID NULL)
/


-- -----------------------------------------------------------------------------
-- 12. RH_T_MISSAO_REQUISICAO — uma requisição por prestador
--
-- Os colaboradores passam para RH_T_MISSAO_REQUISICAO_COLAB, por isso
-- MISSAO_COLAB_ID deixa de ser obrigatório (removido no script de limpeza).
-- Com uma linha por prestador, o nº de requisição passa a ser único no ano.
-- A tabela estava vazia quando isto foi aplicado.
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_MISSAO_REQUISICAO MODIFY (MISSAO_COLAB_ID NULL)
/
CREATE UNIQUE INDEX UX_MISSAO_REQUIS_NR_ANO ON RH_T_MISSAO_REQUISICAO (ANO, NR_REQUISACAO) TABLESPACE TBSINPSDB
/


-- =============================================================================
-- 13. LIMPEZA DO MODELO ANTIGO — POR APLICAR (não executar ainda)
--
-- Só depois de o frontend migrar para os endpoints por processo e de os endpoints
-- antigos (/analise, /emissao-requisicao, /logistica, /cabimento, /autorizacao)
-- serem removidos do MissaoController.
--
-- MANTÊM-SE de propósito (usados pelo modelo novo como atalho para a missão):
--   RH_T_MISSAO_PRESTADOR.MISSAO_SERV_ID, RH_T_MISSAO_LOGISTICA.MISSAO_SERV_ID,
--   RH_T_MISSAO_PRESTADOR.ENT_ID/NOME/EMAIL (fotografia do prestador na selecção).
--
-- Candidatos:
--   ALTER TABLE RH_T_MISSAO_REQUISICAO DROP CONSTRAINT FK_MIS_REQUIS_COLAB;
--   ALTER TABLE RH_T_MISSAO_REQUISICAO DROP COLUMN MISSAO_COLAB_ID;
--   ALTER TABLE RH_T_MISSAO_LOGISTICA MODIFY (MISSAO_PROCESSO_ID NOT NULL);
--   ALTER TABLE RH_T_MISSAO_PRESTADOR MODIFY (MISSAO_PROCESSO_ID NOT NULL, PARAM_PREST_ID NOT NULL);
-- =============================================================================


-- =============================================================================
-- Verificação
-- =============================================================================
-- SELECT object_type, object_name, status FROM user_objects
--  WHERE object_name LIKE '%PARAM_PREST%' OR object_name LIKE '%MISSAO_PROCESSO%'
--     OR object_name LIKE '%PREST_AVAL%' OR object_name LIKE '%REQUISICAO_COLAB%'
--  ORDER BY object_type, object_name;


-- =============================================================================
-- ROLLBACK (só se for preciso desfazer — ordem inversa)
-- =============================================================================
-- ALTER TABLE RH_T_MISSAO_REQUISICAO DROP COLUMN ANO;
-- ALTER TABLE RH_T_MISSAO_LOGISTICA DROP CONSTRAINT FK_MIS_LOGIST_PROC;
-- ALTER TABLE RH_T_MISSAO_LOGISTICA DROP COLUMN MISSAO_PROCESSO_ID;
-- ALTER TABLE RH_T_MISSAO_PRESTADOR DROP CONSTRAINT FK_MIS_PRESTADOR_PROC;
-- ALTER TABLE RH_T_MISSAO_PRESTADOR DROP CONSTRAINT FK_MIS_PRESTADOR_PARAM;
-- ALTER TABLE RH_T_MISSAO_PRESTADOR DROP (PARAM_PREST_ID, MISSAO_PROCESSO_ID);
-- DROP TABLE RH_T_MISSAO_PRESTADOR_AVAL;   DROP SEQUENCE SEQ_MISSAO_PRESTADOR_AVAL;
-- DROP TABLE RH_T_MISSAO_REQUISICAO_COLAB; DROP SEQUENCE SEQ_MISSAO_REQUISICAO_COLAB;
-- DROP TABLE RH_T_MISSAO_PROCESSO_DET;     DROP SEQUENCE SEQ_MISSAO_PROCESSO_DET;
-- DROP TABLE RH_T_MISSAO_PROCESSO;         DROP SEQUENCE SEQ_MISSAO_PROCESSO;
-- DROP TABLE RH_T_PARAM_PRESTADOR_DET;     DROP SEQUENCE SEQ_PARAM_PRESTADOR_DET;
-- DROP TABLE RH_T_PARAM_PRESTADOR;         DROP SEQUENCE SEQ_PARAM_PRESTADOR;

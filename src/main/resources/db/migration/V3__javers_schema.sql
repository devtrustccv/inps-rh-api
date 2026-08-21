-- JaVers audit schema (tabelas JV_*).
--
-- Porquê aqui e não gerido pelo JaVers: o schema-management do JaVers/polyjdbc no Oracle não deteta
-- tabelas já existentes (verifica DatabaseMetaData.getTables com o nome em minúsculas 'jv_global_id',
-- mas o Oracle guarda identificadores não-citados em MAIÚSCULAS), pelo que reemitia CREATE TABLE em
-- cada arranque -> ORA-00955. Por isso JaversAuditConfig usa withSchemaManagementEnabled(false) e o
-- schema passa a ser criado por esta migration.
--
-- DEFENSIVA / IDEMPOTENTE de propósito: o Oracle não tem "CREATE ... IF NOT EXISTS", por isso cada
-- objecto é criado dentro de um bloco PL/SQL que engole ORA-00955 ("name is already used"). Assim a
-- migration:
--   * numa BD vazia -> cria tudo;
--   * numa BD onde o JaVers (auto-DDL, sessoes antigas) ja criou as JV_* -> nao falha, ignora.
-- Isto e necessario porque em varios ambientes as tabelas ja existem antes de o Flyway ser ligado.
--
-- DDL extraido do Oracle real (INPSRH) via DBMS_METADATA, normalizado (sem STORAGE/TABLESPACE) para
-- ser portavel. Reproduz o que o JaVers criaria por si.

-- Tabelas -------------------------------------------------------------------------------------------

BEGIN
  EXECUTE IMMEDIATE q'[
    CREATE TABLE jv_global_id (
        global_id_pk NUMBER        NOT NULL,
        local_id     VARCHAR2(191),
        fragment     VARCHAR2(200),
        type_name    VARCHAR2(200),
        owner_id_fk  NUMBER,
        CONSTRAINT jv_global_id_pk PRIMARY KEY (global_id_pk),
        CONSTRAINT jv_global_id_owner_id_fk FOREIGN KEY (owner_id_fk) REFERENCES jv_global_id (global_id_pk)
    )]';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF;
END;
/

BEGIN
  EXECUTE IMMEDIATE q'[
    CREATE TABLE jv_commit (
        commit_pk           NUMBER        NOT NULL,
        author              VARCHAR2(200),
        commit_date         TIMESTAMP(6),
        commit_date_instant VARCHAR2(30),
        commit_id           NUMBER(22, 2),
        CONSTRAINT jv_commit_pk PRIMARY KEY (commit_pk)
    )]';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF;
END;
/

BEGIN
  EXECUTE IMMEDIATE q'[
    CREATE TABLE jv_commit_property (
        property_name  VARCHAR2(191) NOT NULL,
        property_value VARCHAR2(600),
        commit_fk      NUMBER,
        CONSTRAINT jv_commit_property_pk PRIMARY KEY (commit_fk, property_name),
        CONSTRAINT jv_commit_property_commit_fk FOREIGN KEY (commit_fk) REFERENCES jv_commit (commit_pk)
    )]';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF;
END;
/

BEGIN
  EXECUTE IMMEDIATE q'[
    CREATE TABLE jv_snapshot (
        snapshot_pk        NUMBER        NOT NULL,
        type               VARCHAR2(200),
        version            NUMBER,
        state              CLOB,
        changed_properties CLOB,
        managed_type       VARCHAR2(200),
        global_id_fk       NUMBER,
        commit_fk          NUMBER,
        CONSTRAINT jv_snapshot_pk PRIMARY KEY (snapshot_pk),
        CONSTRAINT jv_snapshot_global_id_fk FOREIGN KEY (global_id_fk) REFERENCES jv_global_id (global_id_pk),
        CONSTRAINT jv_snapshot_commit_fk FOREIGN KEY (commit_fk) REFERENCES jv_commit (commit_pk)
    )]';
EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF;
END;
/

-- Sequences (o JaVers pede o proximo valor a estas ao inserir) ---------------------------------------

BEGIN EXECUTE IMMEDIATE 'CREATE SEQUENCE jv_global_id_pk_seq'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'CREATE SEQUENCE jv_commit_pk_seq';    EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'CREATE SEQUENCE jv_snapshot_pk_seq';  EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/

-- Indices de suporte as queries do JaVers (FKs e lookups mais usados) --------------------------------

BEGIN EXECUTE IMMEDIATE 'CREATE INDEX jv_commit_commit_id_idx        ON jv_commit (commit_id)';                          EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'CREATE INDEX jv_commit_property_commit_fk_i ON jv_commit_property (commit_fk)';                 EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'CREATE INDEX jv_commit_property_property_na ON jv_commit_property (property_name, property_value)'; EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'CREATE INDEX jv_global_id_local_id_idx      ON jv_global_id (local_id)';                        EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'CREATE INDEX jv_global_id_owner_id_fk_idx   ON jv_global_id (owner_id_fk)';                     EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'CREATE INDEX jv_snapshot_commit_fk_idx      ON jv_snapshot (commit_fk)';                        EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/
BEGIN EXECUTE IMMEDIATE 'CREATE INDEX jv_snapshot_global_id_fk_idx   ON jv_snapshot (global_id_fk)';                     EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF; END;
/

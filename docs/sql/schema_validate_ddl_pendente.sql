-- =============================================================================
-- ddl-auto=validate — DDL que falta na BD para o validate passar inteiro (2026-11-25)
--
-- Os tipos incompativeis ficaram todos resolvidos do lado do Java (blocos 1 a 6, ver
-- schema_validate_dev_2026-11-25.sql). O que sobra e da BD: 3 colunas e 1 sequencia.
--
-- POR APLICAR EM: desenvolvimento, staging, producao (Flyway desligado).
-- Nota: a BD de desenvolvimento e a mesma do QA.
-- =============================================================================


-- -----------------------------------------------------------------------------
-- 1. RH_T_VAL_EVOLUCAO_CARREIRA.ORDEM_SERVICO_ID  (ValEvolucaoCarreiraEntity)
--
-- JA TEM SCRIPT PROPRIO: progressao_promocao_ordem_servico_ddl.sql (2026-11-20, coluna + FK para
-- RH_T_ORDEM_SERVICO). Aplicar esse; nao esta repetido aqui para nao correr duas vezes.
-- Em uso: o ProgressaoPromocaoWriteService grava ValEvolucaoCarreiraEntity e as leituras da
-- progressao/promocao tambem; sem a coluna, a entity rebenta com ORA-00904 (a tabela esta vazia em dev).
-- -----------------------------------------------------------------------------


-- -----------------------------------------------------------------------------
-- 2. RH_T_EVOLUCAO_CARREIRA — colunas de auditoria (EvolucaoCarreiraEntity extends AuditEntity)
--
-- A tabela ja tem DATA_REGISTO, USER_REGISTO_ID/NAME e USER_ALTERACAO_ID; faltam estas duas.
-- Tabela vazia em dev e o repositorio nao e usado hoje; os tipos seguem as outras tabelas auditadas.
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_EVOLUCAO_CARREIRA ADD (DATA_ALTERACAO DATE);
ALTER TABLE RH_T_EVOLUCAO_CARREIRA ADD (USER_ALTERACAO_NAME VARCHAR2(200));


-- -----------------------------------------------------------------------------
-- 3. SEQ_DIRECAO  (DirecaoEntity, @SequenceGenerator sequenceName = "SEQ_DIRECAO")
--
-- O Java nao cria direcoes hoje (so le), mas o validate exige a sequencia.
-- NAO usar START WITH 1: RH_T_DIRECAO ja tem ids baixos (1..8 em dev) e ids altos (ate 100011079);
-- a sequencia comeca acima do maior id existente em cada ambiente.
-- -----------------------------------------------------------------------------
DECLARE
    v_inicio NUMBER;
BEGIN
    SELECT NVL(MAX(ID), 0) + 1 INTO v_inicio FROM RH_T_DIRECAO;
    EXECUTE IMMEDIATE 'CREATE SEQUENCE SEQ_DIRECAO START WITH ' || v_inicio || ' INCREMENT BY 1 NOCACHE';
END;
/

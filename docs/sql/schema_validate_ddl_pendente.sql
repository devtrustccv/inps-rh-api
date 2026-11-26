-- =============================================================================
-- ddl-auto=validate — DDL que falta na BD para o validate passar inteiro (2026-11-25)
--
-- Os tipos incompativeis ficaram todos resolvidos do lado do Java (blocos 1 a 6, ver
-- schema_validate_dev_2026-11-25.sql). O que sobra e da BD: 3 colunas.
--
-- POR APLICAR EM: staging, producao (Flyway desligado). Em desenvolvimento ja aplicado (2026-11-26).
-- Nota: a BD de desenvolvimento e a mesma do QA.
-- =============================================================================


-- -----------------------------------------------------------------------------
-- 1. RH_T_VAL_EVOLUCAO_CARREIRA.ORDEM_SERVICO_ID  (ValEvolucaoCarreiraEntity)
--
-- JA TEM SCRIPT PROPRIO: progressao_promocao_ordem_servico_ddl.sql (2026-11-20, coluna + FK para
-- RH_T_ORDEM_SERVICO). Aplicar esse; nao esta repetido aqui para nao correr duas vezes.
-- Em uso: o ProgressaoPromocaoWriteService grava ValEvolucaoCarreiraEntity e as leituras da
-- progressao/promocao tambem; sem a coluna, a entity rebenta com ORA-00904 (a tabela esta vazia em dev).
-- ATENCAO: a coluna e NUMBER (id de RH_T_ORDEM_SERVICO). Em dev chegou a ser criada como VARCHAR2(100) e foi
-- corrigida (2026-11-26) com MODIFY (ORDEM_SERVICO_ID NUMBER); a FK desse script ainda nao foi criada em dev.
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
-- 3. SEQ_DIRECAO — JA NAO E PRECISA (2026-11-26)
-- A DirecaoEntity ficou sem gerador de id: nem o RH nem os packages da BD criam direcoes.
-- -----------------------------------------------------------------------------

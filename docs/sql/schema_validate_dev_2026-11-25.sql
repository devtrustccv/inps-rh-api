-- =============================================================================
-- Schema validate (simulado) — entities vs BD de dev (schemas INPSRH, INPSSIGOF, SIPSGLOBAL)
-- Gerado a 2026-11-25 (develop 885d2b9d + blocos 1 a 4 dos tipos). Equivale aos erros que ddl-auto=validate daria, todos de uma vez
-- (o validate do Hibernate para no 1.o erro que encontrar).
-- Como foi gerado: DDL esperado pelas entities (schema-generation scripts.action=create, sem tocar na BD)
-- comparado com ALL_TAB_COLUMNS / ALL_SEQUENCES. Regras de tipo confirmadas com o driver e o
-- OracleLegacyDialect reais (DATE aceita timestamp e date; CHAR vs varchar2, INTERVAL vs varchar2,
-- NUMBER vs float/varchar2 e VARCHAR2 vs clob/number dao erro).
-- RH_T_PAG_TIPREL / RH_T_REMUN_TIPREL ja nao aparecem: as entities foram removidas (910b3fe3);
-- a tabela usada e RH_T_TIPREL_REM_PAG.
-- Bloco 1 resolvido (11 colunas de horas INTERVAL): IntervalDsStringJdbcType nas entities
-- AssiduidadeSinteseDiario, Dispensa e Falta; validate real das 3 tabelas passou.
-- Bloco 2 resolvido (7 colunas CHAR(n)): columnDefinition = "char(n)" em Substituicao, RhVCarreira,
-- RhVMobilidade, VfaltaMensal e ProcSalCcPag/Remun (o Java continua String/enum); validate real passou.
-- Bloco 3 resolvido (8 colunas VARCHAR2 com Integer no Java): columnDefinition = "varchar2(n)" nas 4 flags
-- de ParamSituacao e no ano das 4 vistas de ferias (o Java continua Integer); validate real passou.
-- Bloco 4 resolvido (5 colunas NUMBER com String no Java): passaram a Long em RhVDossie (duracaoContrato, nif),
-- VFeriasMensal.idColaborador, VHoraExtraMensal.funcionarioId e TipoMovimento.entId (nenhum era lido pelo Java);
-- validate real passou.
-- Tabelas esperadas: 147 | sequencias esperadas: 113
-- Erros: 0 tabela(s) em falta, 3 coluna(s) em falta, 10 tipo(s) incompativel(is), 1 sequencia(s) em falta
-- NAO APLICADO. Rever antes de correr; os tipos seguem a convencao da casa (DATE, NUMBER, VARCHAR2(n)).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Tabelas em falta
-- -----------------------------------------------------------------------------
-- -----------------------------------------------------------------------------
-- 2. Colunas em falta
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_EVOLUCAO_CARREIRA ADD (DATA_ALTERACAO DATE);  -- entity: EvolucaoCarreiraEntity
ALTER TABLE RH_T_EVOLUCAO_CARREIRA ADD (USER_ALTERACAO_NAME VARCHAR2(200));  -- entity: EvolucaoCarreiraEntity
ALTER TABLE RH_T_VAL_EVOLUCAO_CARREIRA ADD (ORDEM_SERVICO_ID NUMBER);  -- entity: ValEvolucaoCarreiraEntity

-- -----------------------------------------------------------------------------
-- 3. Sequencias em falta
-- -----------------------------------------------------------------------------
CREATE SEQUENCE SEQ_DIRECAO START WITH 1 INCREMENT BY 1 NOCACHE;

-- -----------------------------------------------------------------------------
-- 4. Tipos incompativeis (validate rebenta: 'wrong column type encountered')
--    Decidir caso a caso: mudar a BD ou o mapeamento da entity. Sem DDL automatico.
-- -----------------------------------------------------------------------------
-- RH_T_AVD.AVALIACAO_FINAL: BD=NUMBER(5,2), entity espera float(53)  (AvaliacaoEntity)
-- RH_T_IAM_USER_PROFILE.ID: BD=VARCHAR2(36), entity espera raw(16)  (IAMUserProfileEntity)
-- RH_V_HIST_LABORAL.ULTIMO_PROC: BD=CHAR(0), entity espera number(10,0)  (RhVHistLaboralEntity)
-- RH_V_MOVIMENTOS_PICAGEM.FUNCIONARIO_UUID: BD=CHAR(0), entity espera varchar2(255 char)  (MovimentoEntity)
-- RH_V_PROC_SAL_CC_PAG.OBS: BD=VARCHAR2(0), entity espera clob  (ProcSalCcPagEntity)
-- RH_V_PROC_SAL_CC_REMUN.ENT_REF: BD=VARCHAR2(0), entity espera clob  (ProcSalCcRemunEntity)
-- RH_V_PROC_SAL_CC_REMUN.ENTIDADE_REF: BD=VARCHAR2(0), entity espera clob  (ProcSalCcRemunEntity)
-- RH_V_PROC_SAL_CC_REMUN.NIB_REF: BD=VARCHAR2(0), entity espera clob  (ProcSalCcRemunEntity)
-- RH_V_SOAT.DIAS_TRAB_SEMANA: BD=VARCHAR2(0), entity espera clob  (SoatViewEntity)
-- RH_V_SOAT.SITUACAO: BD=VARCHAR2(0), entity espera clob  (SoatViewEntity)

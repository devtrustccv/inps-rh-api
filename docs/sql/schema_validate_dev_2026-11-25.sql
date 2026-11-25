-- =============================================================================
-- Schema validate (simulado) — entities vs BD de dev (schemas INPSRH, INPSSIGOF, SIPSGLOBAL)
-- Gerado a 2026-11-25 (develop ab2bf9dc). Equivale aos erros que ddl-auto=validate daria, todos de uma vez
-- (o validate do Hibernate para no 1.o: aqui foi INPSSIGOF.RH_TIPO_MOVIMENTOS.ENT_ID, que esta na seccao 4).
-- Como foi gerado: DDL esperado pelas entities (schema-generation scripts.action=create, sem tocar na BD)
-- comparado com ALL_TAB_COLUMNS / ALL_SEQUENCES. Regras de tipo confirmadas com o driver e o
-- OracleLegacyDialect reais (DATE aceita timestamp e date; CHAR vs varchar2, INTERVAL vs varchar2,
-- NUMBER vs float/varchar2 e VARCHAR2 vs clob/number dao erro).
-- Tabelas esperadas: 149 | sequencias esperadas: 115
-- Erros: 2 tabela(s) em falta, 3 coluna(s) em falta, 41 tipo(s) incompativel(is), 1 sequencia(s) em falta
-- NAO APLICADO. Rever antes de correr; os tipos seguem a convencao da casa (DATE, NUMBER, VARCHAR2(n)).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Tabelas em falta
-- -----------------------------------------------------------------------------
-- entity: PagTiprelEntity
CREATE TABLE RH_T_PAG_TIPREL (
    DATA_ALTERACAO DATE,
    DATA_REGISTO DATE NOT NULL,
    ID NUMBER NOT NULL,
    PAG_ID NUMBER NOT NULL,
    TIPREL_ID NUMBER,
    USER_ALTERACAO_ID NUMBER(16),
    USER_REGISTO_ID NUMBER(16) NOT NULL,
    ESTADO VARCHAR2(255),
    OBS VARCHAR2(4000),
    USER_ALTERACAO_NAME VARCHAR2(200),
    USER_REGISTO_NAME VARCHAR2(200) NOT NULL,
    UUID VARCHAR2(255)
);

-- entity: RemuneracaoTiprelEntity
CREATE TABLE RH_T_REMUN_TIPREL (
    DATA_ALTERACAO DATE,
    DATA_REGISTO DATE NOT NULL,
    ID NUMBER NOT NULL,
    REM_ID NUMBER,
    TIPREL_ID NUMBER,
    USER_ALTERACAO_ID NUMBER(16),
    USER_REGISTO_ID NUMBER(16) NOT NULL,
    ESTADO VARCHAR2(255) NOT NULL,
    OBS VARCHAR2(4000),
    USER_ALTERACAO_NAME VARCHAR2(200),
    USER_REGISTO_NAME VARCHAR2(200) NOT NULL,
    UUID VARCHAR2(255)
);

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
-- RH_ASSIDUIDADE_SINTESE_DIARIA.HORA_PRIMEIRA_ENTRADA: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (AssiduidadeSinteseDiarioEntity)
-- RH_ASSIDUIDADE_SINTESE_DIARIA.HORA_PRIMEIRA_SAIDA_ALMOCO: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (AssiduidadeSinteseDiarioEntity)
-- RH_ASSIDUIDADE_SINTESE_DIARIA.HORA_ULTIMA_ENTRADA_ALMOCO: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (AssiduidadeSinteseDiarioEntity)
-- RH_ASSIDUIDADE_SINTESE_DIARIA.HORA_ULTIMA_SAIDA: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (AssiduidadeSinteseDiarioEntity)
-- RH_ASSIDUIDADE_SINTESE_DIARIA.HORAS_ALMOCO: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (AssiduidadeSinteseDiarioEntity)
-- RH_ASSIDUIDADE_SINTESE_DIARIA.HORAS_AUSENCIA: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (AssiduidadeSinteseDiarioEntity)
-- RH_ASSIDUIDADE_SINTESE_DIARIA.HORAS_EXTRAS: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (AssiduidadeSinteseDiarioEntity)
-- RH_ASSIDUIDADE_SINTESE_DIARIA.HORAS_TRABALHADAS: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (AssiduidadeSinteseDiarioEntity)
-- RH_T_AVD.AVALIACAO_FINAL: BD=NUMBER(5,2), entity espera float(53)  (AvaliacaoEntity)
-- RH_T_DISPENSA.HORA_FIM: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (DispensaEntity)
-- RH_T_DISPENSA.HORA_INICIO: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (DispensaEntity)
-- RH_T_FALTA.HORAS_AUSENCIA: BD=INTERVAL DAY(0) TO SECOND(0), entity espera varchar2(255 char)  (FaltaEntity)
-- RH_T_IAM_USER_PROFILE.ID: BD=VARCHAR2(36), entity espera raw(16)  (IAMUserProfileEntity)
-- RH_T_PARAM_SITUACAO.FLG_ABONO_BENEFICIO: BD=VARCHAR2(1), entity espera number(10,0)  (ParamSituacaoEntity)
-- RH_T_PARAM_SITUACAO.FLG_AUSENCIA: BD=VARCHAR2(1), entity espera number(10,0)  (ParamSituacaoEntity)
-- RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL: BD=VARCHAR2(1), entity espera number(10,0)  (ParamSituacaoEntity)
-- RH_T_PARAM_SITUACAO.FLG_SITUACAO_LABORAL: BD=VARCHAR2(1), entity espera number(10,0)  (ParamSituacaoEntity)
-- RH_T_SUBSTITUICAO.ESTADO: BD=CHAR(1), entity espera varchar2(255 char)  (SubstituicaoEntity)
-- RH_V_CARREIRA.ESTADO_CARREIRA: BD=CHAR(1), entity espera varchar2(255 char)  (RhVCarreiraEntity)
-- RH_V_DOSSIE.DURACAO_CONTRATO: BD=NUMBER, entity espera varchar2(255 char)  (RhVDossieEntity)
-- RH_V_DOSSIE.NIF: BD=NUMBER, entity espera varchar2(255 char)  (RhVDossieEntity)
-- RH_V_FALTA_MENSAL.EST_PROC: BD=CHAR(4), entity espera varchar2(255 char)  (VfaltaMensalEntity)
-- RH_V_FALTA_MENSAL.FLG_DESC_SAL: BD=CHAR(1), entity espera varchar2(255 char)  (VfaltaMensalEntity)
-- RH_V_FERIAS_MAPA.ANO_REFERENTE: BD=VARCHAR2(4), entity espera number(10,0)  (VMapaFeriaEntity)
-- RH_V_FERIAS_MENSAL.ANO: BD=VARCHAR2(4), entity espera number(10,0)  (VFeriasMensalEntity)
-- RH_V_FERIAS_MENSAL.ID_COLABORADOR: BD=NUMBER, entity espera varchar2(255 char)  (VFeriasMensalEntity)
-- RH_V_FERIAS_VER_MAPA.ANO_REFERENTE: BD=VARCHAR2(4), entity espera number(10,0)  (VFeriasDetalheColaboradorEntity)
-- RH_V_HIST_LABORAL.ULTIMO_PROC: BD=CHAR(0), entity espera number(10,0)  (RhVHistLaboralEntity)
-- RH_V_HORA_EXTRA_MENSAL.FUNCIONARIO_ID: BD=NUMBER, entity espera varchar2(255 char)  (VHoraExtraMensalEntity)
-- RH_V_MAPA_FERIAS_DETALHE.ANO_REFERENTE: BD=VARCHAR2(4), entity espera number(10,0)  (VMapaFeriasDetalheEntity)
-- RH_V_MOBILIDADE.ESTADO: BD=CHAR(1), entity espera varchar2(255 char)  (RhVMobilidadeEntity)
-- RH_V_MOVIMENTOS_PICAGEM.FUNCIONARIO_UUID: BD=CHAR(0), entity espera varchar2(255 char)  (MovimentoEntity)
-- RH_V_PROC_SAL_CC_PAG.TIPO: BD=CHAR(3), entity espera varchar2(3 char)  (ProcSalCcPagEntity)
-- RH_V_PROC_SAL_CC_PAG.OBS: BD=VARCHAR2(0), entity espera clob  (ProcSalCcPagEntity)
-- RH_V_PROC_SAL_CC_REMUN.TIPO: BD=CHAR(4), entity espera varchar2(4 char)  (ProcSalCcRemunEntity)
-- RH_V_PROC_SAL_CC_REMUN.ENT_REF: BD=VARCHAR2(0), entity espera clob  (ProcSalCcRemunEntity)
-- RH_V_PROC_SAL_CC_REMUN.ENTIDADE_REF: BD=VARCHAR2(0), entity espera clob  (ProcSalCcRemunEntity)
-- RH_V_PROC_SAL_CC_REMUN.NIB_REF: BD=VARCHAR2(0), entity espera clob  (ProcSalCcRemunEntity)
-- RH_V_SOAT.DIAS_TRAB_SEMANA: BD=VARCHAR2(0), entity espera clob  (SoatViewEntity)
-- RH_V_SOAT.SITUACAO: BD=VARCHAR2(0), entity espera clob  (SoatViewEntity)
-- INPSSIGOF.RH_TIPO_MOVIMENTOS.ENT_ID: BD=NUMBER, entity espera varchar2(255 char)  (TipoMovimentoEntity)

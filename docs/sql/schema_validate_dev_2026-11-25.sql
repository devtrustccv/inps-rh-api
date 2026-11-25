-- =============================================================================
-- Schema validate (simulado) — entities vs BD de dev (schemas INPSRH, INPSSIGOF, SIPSGLOBAL)
-- Gerado a 2026-11-25 (develop 885d2b9d + blocos 1 a 6 dos tipos). Equivale aos erros que ddl-auto=validate daria, todos de uma vez
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
-- Bloco 5 resolvido (8 colunas que as vistas preenchem com NULL/'' fixo, CHAR(0)/VARCHAR2(0)): sem @Lob em
-- ProcSalCcPag.obs, ProcSalCcRemun.nibRef/entidadeRef/entRef e SoatView.diasTrabSemana/situacao; columnDefinition
-- char(1) em RhVHistLaboral.ultimoProc e char(36) em Movimento.funcionarioUuid; validate real passou.
-- Nota: RH_V_MOVIMENTOS_PICAGEM devolve '' em FUNCIONARIO_UUID, por isso o filtro das picagens por
-- funcionarioUuid nunca encontra nada; corrige-se na vista (ir buscar o uuid do funcionario).
-- Bloco 6 resolvido: RH_T_AVD.AVALIACAO_FINAL passa a BigDecimal(5,2) (nao era usado no Java);
-- RH_T_IAM_USER_PROFILE.ID passa a String. O UUIDCharConverter nao se aplica a @Id, por isso o id ia como
-- raw(16): gravava em hex sem hifenes e era lido trocado, e o save do perfil (sync do JWT) fazia INSERT em
-- vez de UPDATE. Com String le o valor gravado e faz UPDATE; perfis novos ficam com id = sub. Sem mexer
-- nos dados (os perfis em hex continuam validos). validate real das 2 tabelas passou.
-- Tabelas esperadas: 147 | sequencias esperadas: 113
-- Erros: 0 tabela(s) em falta, 3 coluna(s) em falta, 0 tipo(s) incompativel(is), 1 sequencia(s) em falta
-- NAO APLICADO. Rever antes de correr; os tipos seguem a convencao da casa (DATE, NUMBER, VARCHAR2(n)).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Tabelas em falta
-- -----------------------------------------------------------------------------
-- -----------------------------------------------------------------------------
-- 2. Colunas em falta
-- -----------------------------------------------------------------------------
-- Script para o DBA: schema_validate_ddl_pendente.sql (ponto 1 remete para progressao_promocao_ordem_servico_ddl.sql).
ALTER TABLE RH_T_EVOLUCAO_CARREIRA ADD (DATA_ALTERACAO DATE);  -- entity: EvolucaoCarreiraEntity
ALTER TABLE RH_T_EVOLUCAO_CARREIRA ADD (USER_ALTERACAO_NAME VARCHAR2(200));  -- entity: EvolucaoCarreiraEntity
ALTER TABLE RH_T_VAL_EVOLUCAO_CARREIRA ADD (ORDEM_SERVICO_ID NUMBER);  -- entity: ValEvolucaoCarreiraEntity (+ FK, ver progressao_promocao_ordem_servico_ddl.sql)

-- -----------------------------------------------------------------------------
-- 3. Sequencias em falta
-- -----------------------------------------------------------------------------
-- SEQ_DIRECAO: nao comecar em 1 (RH_T_DIRECAO ja tem ids 1..8 e ate 100011079); comeca em MAX(ID)+1,
-- ver schema_validate_ddl_pendente.sql.

-- -----------------------------------------------------------------------------
-- 4. Tipos incompativeis (validate rebenta: 'wrong column type encountered')
--    Decidir caso a caso: mudar a BD ou o mapeamento da entity. Sem DDL automatico.
-- -----------------------------------------------------------------------------

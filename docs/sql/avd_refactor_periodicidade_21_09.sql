-- ============================================================================
-- Refactor Avaliacao de Desempenho - spec 21/09/2026
-- Alinhamento do esquema e dos dominios com o novo modelo de PERIODICIDADE.
-- Executado em DEV a 2026-09-21. Sem Flyway: correr a mao nos outros ambientes.
--
-- Pre-requisito: RH_T_AVD_PERIODICIDADE, RH_T_AVD_DETALHE,
-- RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE e as respectivas sequences/triggers
-- ja foram criadas pelo DBA.
-- ============================================================================

-- 1) Abrangencia na tabela mae.
--    A spec grava RH_T_AVD.ABRAGENCIA (INPS | DIRECAO | INDIVIDUAL, dominio
--    ABRANGENCIA_AVD) mas a coluna nao existia; as filhas ja a tinham.
ALTER TABLE RH_T_AVD ADD ABRAGENCIA VARCHAR2(100);

-- 2) SEMESTRE fica orfao: o eixo temporal passa para
--    RH_T_AVD_DETALHE.PERIODICIDADE / RH_T_AVD_PERIODICIDADE.PERIODICIDADE.
--    Tabela estava vazia (0 linhas) quando isto correu.
ALTER TABLE RH_T_AVD DROP COLUMN SEMESTRE;

-- 3) PERIODICIDADE na parametrizacao guardava '2' (lixo da versao anterior),
--    que nao corresponde a nenhum valor do dominio. Passa a guardar o TIPO.
UPDATE RH_T_PARAM_OBJETIVO_DET SET PERIODICIDADE = 'SEMESTRAL' WHERE PERIODICIDADE = '2';

-- 4) Grafia: o dominio vinha com SEMESTRO/TRIMESTRO, que nao e portugues.
--    Corrigido para SEMESTRE/TRIMESTRE, e alinhado com as referencias de
--    AVD_PONDERACAO_FINAL (que ja usavam SEMESTRE1/SEMESTRE2). Feito agora
--    porque nada consome ainda estes valores: RH_T_AVD, RH_T_AVD_DETALHE e
--    RH_T_AVD_PERIODICIDADE estao todas com 0 linhas.
--    Idempotente: nada a fazer num ambiente que ja esteja correcto.
UPDATE RH_T_DOMAINS
   SET VALOR = REPLACE(VALOR, 'SEMESTRO', 'SEMESTRE'),
       DESCRICAO = REPLACE(DESCRICAO, 'Semestro', 'Semestre')
 WHERE DOMINIO = 'PERIODICIDADE' AND VALOR LIKE 'SEMESTRO%';
UPDATE RH_T_DOMAINS
   SET VALOR = REPLACE(VALOR, 'TRIMESTRO', 'TRIMESTRE'),
       DESCRICAO = REPLACE(DESCRICAO, 'Trimestro', 'Trimestre')
 WHERE DOMINIO = 'PERIODICIDADE' AND VALOR LIKE 'TRIMESTRO%';
UPDATE RH_T_DOMAINS
   SET REFERENCIA = REPLACE(REPLACE(REFERENCIA, 'SEMESTRO', 'SEMESTRE'),
                            'TRIMESTRO', 'TRIMESTRE')
 WHERE DOMINIO = 'AVD_PONDERACAO_FINAL';

-- 5) Completar o dominio: faltava o 4o trimestre.
INSERT INTO RH_T_DOMAINS (DOMINIO, REFERENCIA, VALOR, DESCRICAO, ESTADO,
                          DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
VALUES ('PERIODICIDADE', 'TRIMESTRAL', 'TRIMESTRE4', 'Trimestre 4', 'A',
        SYSDATE, 1, 'local');

-- 6) Ponderacao final para trimestral e anual (so existia para semestral).
--    Cada tipo de periodicidade soma 100%.
INSERT INTO RH_T_DOMAINS (DOMINIO, REFERENCIA, VALOR, DESCRICAO, ESTADO,
                          DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
VALUES ('AVD_PONDERACAO_FINAL', 'TRIMESTRE1', '25',
        UNISTR('Pondera\00E7\00E3o Final 1\00BA Trimestre'), 'A', SYSDATE, 1, 'local');
INSERT INTO RH_T_DOMAINS (DOMINIO, REFERENCIA, VALOR, DESCRICAO, ESTADO,
                          DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
VALUES ('AVD_PONDERACAO_FINAL', 'TRIMESTRE2', '25',
        UNISTR('Pondera\00E7\00E3o Final 2\00BA Trimestre'), 'A', SYSDATE, 1, 'local');
INSERT INTO RH_T_DOMAINS (DOMINIO, REFERENCIA, VALOR, DESCRICAO, ESTADO,
                          DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
VALUES ('AVD_PONDERACAO_FINAL', 'TRIMESTRE3', '25',
        UNISTR('Pondera\00E7\00E3o Final 3\00BA Trimestre'), 'A', SYSDATE, 1, 'local');
INSERT INTO RH_T_DOMAINS (DOMINIO, REFERENCIA, VALOR, DESCRICAO, ESTADO,
                          DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
VALUES ('AVD_PONDERACAO_FINAL', 'TRIMESTRE4', '25',
        UNISTR('Pondera\00E7\00E3o Final 4\00BA Trimestre'), 'A', SYSDATE, 1, 'local');
INSERT INTO RH_T_DOMAINS (DOMINIO, REFERENCIA, VALOR, DESCRICAO, ESTADO,
                          DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
VALUES ('AVD_PONDERACAO_FINAL', 'ANUAL', '100',
        UNISTR('Pondera\00E7\00E3o Final Anual'), 'A', SYSDATE, 1, 'local');

-- 7) Typo antigo nas descricoes da ponderacao semestral ("Semetre").
UPDATE RH_T_DOMAINS SET DESCRICAO = REPLACE(DESCRICAO, 'Semetre', 'Semestre')
 WHERE DOMINIO = 'AVD_PONDERACAO_FINAL' AND DESCRICAO LIKE '%Semetre%';

COMMIT;

-- ============================================================================
-- Estado final do dominio (verificado em DEV):
--
--   DOMINIO = PERIODICIDADE          (hierarquia de 2 niveis)
--     REFERENCIA = 'PERIODICIDADE' -> SEMESTRAL | TRIMESTRAL | ANUAL   (tipos)
--     REFERENCIA = 'SEMESTRAL'     -> SEMESTRE1 | SEMESTRE2           (periodos)
--     REFERENCIA = 'TRIMESTRAL'    -> TRIMESTRE1..TRIMESTRE4
--     REFERENCIA = 'ANUAL'         -> ANUAL
--
--   DOMINIO = AVD_PONDERACAO_FINAL   (peso de cada periodo na nota do ano)
--     SEMESTRE1=50  SEMESTRE2=50
--     TRIMESTRE1..4=25
--     ANUAL=100
--
--   RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE guarda o TIPO   (ex.: SEMESTRAL)
--   RH_T_AVD_DETALHE.PERIODICIDADE       guarda o PERIODO (ex.: SEMESTRE1)
--   RH_T_AVD_PERIODICIDADE.PERIODICIDADE guarda o PERIODO (ex.: SEMESTRE1)
-- ============================================================================

-- NAO EXECUTADO: limpeza total da parametrizacao de teste.
-- As 2 linhas de RH_T_PARAM_OBJETIVO_DET tem 14 objectivos filhos em
-- RH_T_PARAM_OBJETIVO, uteis para testar o refactor. Correr so se se quiser
-- recomecar a parametrizacao do zero:
--
--   DELETE FROM RH_T_PARAM_OBJETIVO WHERE PARAM_OBJ_DET_ID IN (1, 2);
--   DELETE FROM RH_T_PARAM_OBJETIVO_DET WHERE ID IN (1, 2);
--   COMMIT;

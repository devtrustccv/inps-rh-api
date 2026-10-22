-- =============================================================================
-- Missão de Serviço — alterações de esquema (2026-08-11)
--
-- APLICADO EM: desenvolvimento (INPSRH).
-- POR APLICAR EM: staging, produção.
--
-- Estas tabelas não constam de src/main/resources/db/migration e o Flyway está
-- desligado (spring.flyway.enabled=false), por isso o script é executado à mão.
-- Correr pela ordem indicada, com o serviço parado ou fora de horas.
-- =============================================================================


-- -----------------------------------------------------------------------------
-- 1. Âmbito da missão — texto livre do ecrã de Submissão
--
-- Campo obrigatório no formulário ("Âmbito da Missão"), descreve o objetivo da
-- missão. Não confundir com DESCRICAO_DESTINO (o local) nem com FLG_DESTINO
-- (nacional=1 / estrangeiro=2, derivado do país).
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_MISSAO_SERVICO ADD (ambito_missao VARCHAR2(2000));


-- -----------------------------------------------------------------------------
-- 2. Ano da missão — suporta a numeração sequencial anual
--
-- O nº de missão passa a reiniciar a 1 em cada ano civil e é apresentado como
-- "nr/ano" (ex.: 1/2027). NR_MISSAO continua NUMBER, para a ordenação e o
-- filtro se manterem numéricos; o ano fica em coluna própria.
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_MISSAO_SERVICO ADD (ano NUMBER(4));


-- -----------------------------------------------------------------------------
-- 3. Backfill do ano nas missões existentes
--
-- Usa o ano de criação do registo (DATA_REGISTO), com DATA_INICIO como recurso
-- para registos antigos sem data de registo. É idempotente — só toca em linhas
-- com ANO a null, por isso pode ser repetido sem risco.
-- -----------------------------------------------------------------------------
UPDATE RH_T_MISSAO_SERVICO
   SET ano = EXTRACT(YEAR FROM NVL(data_registo, data_inicio))
 WHERE ano IS NULL;

COMMIT;

-- Verificação: não deve sobrar nenhuma linha sem ano.
-- SELECT COUNT(*) FROM RH_T_MISSAO_SERVICO WHERE ano IS NULL;


-- -----------------------------------------------------------------------------
-- 4. Índice único (ANO, NR_MISSAO)
--
-- Impede dois números iguais no mesmo ano. Como a criação de missões calcula
-- MAX(nr_missao)+1 dentro do ano, duas criações simultâneas poderiam apanhar o
-- mesmo número; com o índice, a segunda falha em vez de duplicar.
--
-- ATENÇÃO — verificar duplicados ANTES de criar o índice:
--
--   SELECT ano, nr_missao, COUNT(*)
--     FROM RH_T_MISSAO_SERVICO
--    GROUP BY ano, nr_missao
--   HAVING COUNT(*) > 1;
--
-- Se devolver linhas, é preciso renumerar antes de continuar. Note-se que a
-- numeração anterior era global (MAX+1 sobre toda a tabela), pelo que os pares
-- (ano, nr_missao) resultantes do backfill são únicos por construção — só há
-- duplicados se o NR_MISSAO já estivesse repetido, por corrida na criação ou
-- por dados migrados de outro sistema.
-- -----------------------------------------------------------------------------
CREATE UNIQUE INDEX ux_missao_nr_ano ON RH_T_MISSAO_SERVICO (ano, nr_missao);


-- -----------------------------------------------------------------------------
-- 5. Nº de documento do colaborador — NUMBER para VARCHAR2
--
-- A coluna era NUMBER e o valor vinha de RH_T_FUNCIONARIOS.NUM_DOCUMENTO, que é
-- VARCHAR2. Documentos alfanuméricos — passaportes como "PA466262", os
-- relevantes numa missão internacional — não convertiam e ficavam gravados a
-- null, sem erro. A spec descreve o campo como TEXT.
--
-- O Oracle recusa converter NUMBER -> VARCHAR2 com dados na tabela (ORA-01439),
-- daí a coluna temporária. Os valores existentes são preservados.
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_MISSAO_COLABORADOR ADD (num_documento_tmp VARCHAR2(255 CHAR));

UPDATE RH_T_MISSAO_COLABORADOR
   SET num_documento_tmp = TO_CHAR(num_documento)
 WHERE num_documento IS NOT NULL;

ALTER TABLE RH_T_MISSAO_COLABORADOR DROP COLUMN num_documento;

ALTER TABLE RH_T_MISSAO_COLABORADOR RENAME COLUMN num_documento_tmp TO num_documento;

COMMIT;


-- -----------------------------------------------------------------------------
-- Verificação final
-- -----------------------------------------------------------------------------
-- SELECT column_name, data_type, data_length
--   FROM user_tab_columns
--  WHERE table_name = 'RH_T_MISSAO_SERVICO'
--    AND column_name IN ('AMBITO_MISSAO', 'ANO');
--
-- SELECT column_name, data_type, data_length
--   FROM user_tab_columns
--  WHERE table_name = 'RH_T_MISSAO_COLABORADOR'
--    AND column_name = 'NUM_DOCUMENTO';
--
-- SELECT ano, COUNT(*) total, MIN(nr_missao) menor, MAX(nr_missao) maior
--   FROM RH_T_MISSAO_SERVICO
--  GROUP BY ano
--  ORDER BY ano;

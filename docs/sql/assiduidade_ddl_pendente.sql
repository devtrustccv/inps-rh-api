-- =====================================================================
-- GESTAO ASSIDUIDADE — alteracoes de BD pendentes
-- Base: "Especificacao Tecnica Funcional - GESTAO_ASSIDUIDADE_01_08_26.md"
-- Schema: INPSRH
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1. Expor CALCULO_FALTA_DIARIO na spec do package   [FEITO]
-- ---------------------------------------------------------------------
-- APLICADO: declaracao esta agora na PACKAGE spec (linha 175); package e
-- body a VALID; a funcao ja aparece em ALL_PROCEDURES.
--
-- ATENCAO: expor a funcao NAO a torna utilizavel. Verificado em BD:
--
--   TIPREL   GET_SALARIO_BASE   CALCULO_FALTA_DIARIO
--   173124   186980             NULL
--   173138   259218.04          NULL
--   173187   271819             NULL
--
-- Devolve sempre NULL por causa do bug da seccao 2 (por corrigir).
-- Esperado para o tiprel 173124: 186980 / 30 / 8 = 779.08 CVE/hora.

-- A funcao JA EXISTE no PACKAGE BODY RH_PROCESSAMENTO_SALARIAL_DB (linha 2204)
-- mas NAO esta declarada na spec, logo e privada: so CALCULO_FALTA_LICENCA
-- a consegue chamar (linha 2370). Nao e invocavel de fora nem aparece em
-- ALL_PROCEDURES.
--
-- COMO APLICAR: editar a spec do package e acrescentar a linha abaixo
-- logo a seguir a declaracao de CALCULO_HORA_EXTRA (linha 154).
-- Depois recompilar spec E body (a spec invalida o body):
--     ALTER PACKAGE RH_PROCESSAMENTO_SALARIAL_DB COMPILE SPECIFICATION;
--     ALTER PACKAGE RH_PROCESSAMENTO_SALARIAL_DB COMPILE BODY;

/*  --> acrescentar na PACKAGE SPEC, a seguir a linha 154:

  FUNCTION CALCULO_FALTA_DIARIO (P_TIPREL_ID NUMBER, P_DATA_INICIO DATE) RETURN NUMBER;

*/


-- ---------------------------------------------------------------------
-- 2. BUG no corpo de CALCULO_FALTA_DIARIO — devolve sempre NULL
-- ---------------------------------------------------------------------
-- RH_T_ASSIDUIDADE_PARAMETRO.DIARIA e VARCHAR2 no formato 'HH:MM' (ex.: '08:00').
-- O corpo actual faz  SELECT DIARIA INTO v_jorn_diaria (NUMBER)  -> ORA-06502,
-- apanhado pelo  EXCEPTION WHEN OTHERS THEN NULL  -> v_jorn_diaria fica NULL
-- -> v_sal_hra = NULL -> a funcao devolve NULL.
--
-- CALCULO_HORA_EXTRA (linha 2092) ja faz a conversao correcta e serve de modelo:
--     TO_NUMBER(SUBSTR(DIARIA,1,2)) + TO_NUMBER(SUBSTR(DIARIA,4,2))/60
--
-- Corpo corrigido (substituir linhas 2204-2231 do PACKAGE BODY):

/*
   FUNCTION CALCULO_FALTA_DIARIO (P_TIPREL_ID NUMBER, P_DATA_INICIO DATE)
   RETURN NUMBER
   IS
     v_salario     NUMBER;
     v_sal_mes     NUMBER;
     v_sal_hra     NUMBER;
     v_jorn_diaria NUMBER;
   BEGIN
     BEGIN
       SELECT TO_NUMBER(SUBSTR(DIARIA, 1, 2)) + TO_NUMBER(SUBSTR(DIARIA, 4, 2)) / 60
         INTO v_jorn_diaria
         FROM RH_T_ASSIDUIDADE_PARAMETRO
        WHERE ESTADO = 'A';
     EXCEPTION WHEN OTHERS THEN
       v_jorn_diaria := NULL;
     END;

     IF NVL(v_jorn_diaria, 0) = 0 THEN
       RAISE_APPLICATION_ERROR(-20001, 'Jornada diaria nao parametrizada em RH_T_ASSIDUIDADE_PARAMETRO.');
     END IF;

     v_salario := GET_SALARIO_BASE(P_TIPREL_ID, P_DATA_INICIO);
     v_sal_mes := v_salario / v_divisor_falta;   -- v_divisor_falta = 30 (constante, linha 16)
     v_sal_hra := v_sal_mes / v_jorn_diaria;

     RETURN v_sal_hra;                           -- valor por HORA (apesar do nome "DIARIO")
   END;
*/


-- ---------------------------------------------------------------------
-- 3. Colunas em falta exigidas pela especificacao
-- ---------------------------------------------------------------------

-- 3.1 RH_ASSIDUIDADE_SINTESE_DIARIA.FORMA
--     Spec, "Marcar Falta / Ausencia" > Gravacao: FORMA = 'MANUAL'.
--     Distingue registos criados a mao pelo RH dos importados do relogio.
ALTER TABLE RH_ASSIDUIDADE_SINTESE_DIARIA ADD (FORMA VARCHAR2(20));

COMMENT ON COLUMN RH_ASSIDUIDADE_SINTESE_DIARIA.FORMA IS
  'Origem do registo: MANUAL (marcado pelo RH) | AUTOMATICO (importado do relogio de ponto)';

-- Retroactivo: tudo o que existe hoje veio da importacao.
UPDATE RH_ASSIDUIDADE_SINTESE_DIARIA SET FORMA = 'AUTOMATICO' WHERE FORMA IS NULL;


-- 3.1.1 PENDENTE: IMPORT_DADOS_CONTR_ACESSO nao preenche FORMA
--
--   A coluna foi criada depois da procedure de importacao, que por isso a ignora
--   (verificado: zero referencias a FORMA em ALL_SOURCE).
--
--   Consequencia: as sinteses vindas do relogio ficam com FORMA a NULL, nao a
--   'AUTOMATICO'. As 113 linhas retro-preenchidas acima sao uma suposicao — de
--   fiavel, so o valor 'MANUAL', que e escrito pelo nosso codigo.
--
--   Enquanto nao for corrigido, ler NULL como automatico. Corrigir seria
--   acrescentar a atribuicao no INSERT da procedure:
--       FORMA = 'AUTOMATICO'
--
--   Hoje nao afecta nada: nem a reutilizacao da sintese do dia nem a verificacao
--   de falta duplicada leem este campo. Mas afecta qualquer regra futura que
--   precise de distinguir importado de marcado a mao.


-- 3.2 RH_T_FERIAS_GOZADAS.TIPO_ALTERACAO
--     Spec, "Pedido Ferias / Alteracao de ferias":
--       RH_T_FERIAS_GOZADA.TIPO_ALTERACAO + FERIAS_GOZADA_ID
--     (FERIAS_GOZADAS_ID e MOTIVO_ALTERACAO ja existem; falta so o tipo.)
ALTER TABLE RH_T_FERIAS_GOZADAS ADD (TIPO_ALTERACAO VARCHAR2(30));

COMMENT ON COLUMN RH_T_FERIAS_GOZADAS.TIPO_ALTERACAO IS
  'Natureza da alteracao do pedido de ferias (ex.: ALTERACAO_DATA, ANULACAO). Preenchido apenas em registos de alteracao, a par de FERIAS_GOZADAS_ID.';


-- 3.3 RH_T_FALTA.TIPO
--     Spec, "Marcar Falta" > 2.2 Registo na tabela RH_T_FALTA: TIPO = 'FALTA'.
ALTER TABLE RH_T_FALTA ADD (TIPO VARCHAR2(30));

COMMENT ON COLUMN RH_T_FALTA.TIPO IS
  'Tipo de registo. Valor previsto pela especificacao: FALTA.';

UPDATE RH_T_FALTA SET TIPO = 'FALTA' WHERE TIPO IS NULL;

COMMIT;


-- ---------------------------------------------------------------------
-- 4. BUG — GET_SALARIO_BASE sem tratamento de TOO_MANY_ROWS
-- ---------------------------------------------------------------------
-- Linha 2703: o SELECT ... INTO so tem handler para NO_DATA_FOUND.
-- Quando um tiprel tem mais do que uma remuneracao SAL/SBNT activa e
-- vigente na data, rebenta com ORA-01422 sem tratamento, e o erro propaga-se
-- a tudo o que dependa dela (CALCULO_FALTA_DIARIO, CALCULO_HORA_EXTRA,
-- CALCULO_FALTA_LICENCA e o proprio processamento).
--
-- Levantamento em BD (a data de hoje): 54 tiprels com 1 salario activo,
-- 3 tiprels com 2 -- ou seja, ja ha casos reais a rebentar.
--
-- Sugestao: acrescentar o handler e decidir o criterio de desempate
-- (o comentario "tirei o max" na linha 2703 sugere que ja houve um MAX).
--
--     EXCEPTION
--         WHEN NO_DATA_FOUND THEN
--             v_salario := 0;
--         WHEN TOO_MANY_ROWS THEN
--             SELECT MAX(r.valor) KEEP (DENSE_RANK LAST ORDER BY r.data_inicio),
--                    MAX(r.moeda) KEEP (DENSE_RANK LAST ORDER BY r.data_inicio)
--               INTO v_salario, v_cambio_de
--               FROM ...  -- mesmo FROM/WHERE
--
-- Enquanto nao for corrigido, o lado Java trata a excepcao e cai no
-- calculo proprio, para nao derrubar o pedido do utilizador.


-- ---------------------------------------------------------------------
-- 5. BUG a reportar (fora do ambito do fix Java) — PROCESSA_FALTA / PROCESSA_HORA
-- ---------------------------------------------------------------------
-- Em PROCESSA_FALTA (linha 2450) e PROCESSA_HORA (linha 2526) o WHERE usa
-- V_DATA_INICIO_ANT / V_DATA_FIM_ANT ANTES de essas variaveis serem atribuidas
-- (a atribuicao so acontece nas linhas 2456 / 2534, mais abaixo).
--
-- Consequencia: as variaveis estao NULL no filtro, o SUM devolve NULL,
-- o  IF V_VALOR > 0  nunca e verdadeiro e NENHUMA falta ou hora extra chega
-- a ser lancada no processamento salarial.
--
-- Ou seja: mesmo depois de o lado Java passar a gravar correctamente em
-- RH_T_DEF_PAGAMENTOS / RH_T_DEF_REMUNERACOES, o processamento nao os apanha
-- enquanto isto nao for corrigido. Mover o bloco SELECT ... INTO V_DATA_INICIO_ATUAL,
-- V_DATA_FIM_ATUAL, V_DATA_INICIO_ANT, V_DATA_FIM_ANT, V_ANO_MES_ANT para ANTES
-- do SELECT SUM(...).

-- =============================================================================
-- Melhorias Dossiê — Break change FLG_SALARIO + escalão no tiprel
-- Aplicar QUANDO a BD estiver acessível. Idempotente onde possível.
-- Autor: implementação worktree feat/dossier-melhorias (2026-08-26)
--
-- PRÉ-REQUISITO: o domínio TIPO_SALARIO_VINCULO existe em RH_T_DOMAINS, mas foi
-- criado com VALOR numérico (0/1/2) em vez dos códigos string que o documento
-- (docs/MELHORIAS_DOSSIER.md, linhas 52/65) e o código (enum TipoSalarioVinculo)
-- exigem. O passo 0 abaixo corrige as 3 linhas para NAO/SIM_PCCS/SIM_FORA_PCCS.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 0) RH_T_DOMAINS : alinhar VALOR do domínio TIPO_SALARIO_VINCULO ao documento.
--    Mapeamento (descrição mantida): 0->NAO, 1->SIM_PCCS, 2->SIM_FORA_PCCS.
-- -----------------------------------------------------------------------------
UPDATE RH_T_DOMAINS SET VALOR = 'NAO'           WHERE DOMINIO = 'TIPO_SALARIO_VINCULO' AND VALOR = '0';
UPDATE RH_T_DOMAINS SET VALOR = 'SIM_PCCS'      WHERE DOMINIO = 'TIPO_SALARIO_VINCULO' AND VALOR = '1';
UPDATE RH_T_DOMAINS SET VALOR = 'SIM_FORA_PCCS' WHERE DOMINIO = 'TIPO_SALARIO_VINCULO' AND VALOR = '2';

-- -----------------------------------------------------------------------------
-- 1) RH_T_PARAM_VINCULO.FLG_SALARIO : NUMBER(0/1) -> VARCHAR2 (TIPO_SALARIO_VINCULO)
--    Estratégia: coluna temporária -> backfill -> drop -> rename (Oracle não
--    converte NUMBER->VARCHAR2 in-place com dados).
-- -----------------------------------------------------------------------------
ALTER TABLE RH_T_PARAM_VINCULO ADD (FLG_SALARIO_TMP VARCHAR2(20));

-- Backfill:
--   0                       -> NAO            (claro)
--   1 e flg_carreira = 1    -> SIM_PCCS       (tem carreira => PCCS)
--   1 e flg_carreira <> 1   -> SIM_FORA_PCCS  (DEFAULT CONSERVADOR — ver nota)
--
-- NOTA (ambiguidade): vínculos com salário mas SEM carreira (1 & flg_carreira=0)
-- não se distinguem automaticamente entre PCCS e FORA_PCCS. Assumimos
-- SIM_FORA_PCCS (salário manual, sem escalão) para não inventar escalão onde não
-- existe e preservar o comportamento anterior. >>> REVISÃO MANUAL: promover a
-- SIM_PCCS os vínculos que forem mesmo do PCCS (query de apoio no fim). <<<
UPDATE RH_T_PARAM_VINCULO
   SET FLG_SALARIO_TMP =
       CASE
         WHEN FLG_SALARIO = 0 THEN 'NAO'
         WHEN FLG_SALARIO = 1 AND FLG_CARREIRA = 1 THEN 'SIM_PCCS'
         WHEN FLG_SALARIO = 1 THEN 'SIM_FORA_PCCS'
         ELSE NULL
       END;

ALTER TABLE RH_T_PARAM_VINCULO DROP COLUMN FLG_SALARIO;
ALTER TABLE RH_T_PARAM_VINCULO RENAME COLUMN FLG_SALARIO_TMP TO FLG_SALARIO;

-- -----------------------------------------------------------------------------
-- 2) RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID (nullable) — garantir que existe.
--    Usado para gravar o escalão de vínculos SEM carreira (SIM_PCCS). Adiciona
--    só se ainda não existir; NÃO força NOT NULL (a maioria dos tiprels — os com
--    carreira — mantém-no null).
-- -----------------------------------------------------------------------------
DECLARE
  v_cnt NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_cnt
    FROM USER_TAB_COLUMNS
   WHERE TABLE_NAME = 'RH_T_TIPOS_RELACIONAMENTO'
     AND COLUMN_NAME = 'ESCALAO_ID';
  IF v_cnt = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE RH_T_TIPOS_RELACIONAMENTO ADD (ESCALAO_ID NUMBER)';
    EXECUTE IMMEDIATE 'ALTER TABLE RH_T_TIPOS_RELACIONAMENTO ADD CONSTRAINT FK_TIPREL_ESCALAO '
                    || 'FOREIGN KEY (ESCALAO_ID) REFERENCES RH_T_PARAM_ESCALAO (ID)';
  END IF;
END;
/

COMMIT;

-- -----------------------------------------------------------------------------
-- QUERIES DE APOIO À REVISÃO MANUAL (não alteram dados)
-- -----------------------------------------------------------------------------
-- Vínculos que ficaram SIM_FORA_PCCS por defeito (1 & sem carreira) — reveja e
-- promova a SIM_PCCS os que forem do PCCS:
--   SELECT ID, CODIGO, NOME, FLG_CARREIRA, FLG_SALARIO
--     FROM RH_T_PARAM_VINCULO
--    WHERE FLG_SALARIO = 'SIM_FORA_PCCS';
--
-- Para promover um vínculo a PCCS:
--   UPDATE RH_T_PARAM_VINCULO SET FLG_SALARIO = 'SIM_PCCS' WHERE ID = :id;
--   COMMIT;

-- Preenche o UUID dos pagamentos/descontos e remunerações importados sem UUID.
-- Sintoma: na lista "Rendimentos e Encargos" o botão Editar abre
--   .../registarrendimentosencargos?uuidPagamento=null  ->  400 "Invalid UUID string: null".
-- Causa: TRG_DEF_PAGAMENTOS / TRG_DEF_REMUNERACAO só preenchem ID e DATA_REGISTO; o UUID é
-- gerado pela aplicação ao gravar, por isso as linhas importadas ficaram com UUID a NULL.
-- Formato: SYS_GUID em minúsculas com hífens (8-4-4-4-12), igual aos UUID dos funcionários importados.
-- Schema: INPSRH. Não executar sem autorização.

-- 1) Diagnóstico
SELECT 'RH_T_DEF_PAGAMENTOS' tabela, COUNT(*) total, SUM(CASE WHEN uuid IS NULL THEN 1 ELSE 0 END) sem_uuid
  FROM rh_t_def_pagamentos
UNION ALL
SELECT 'RH_T_DEF_REMUNERACOES', COUNT(*), SUM(CASE WHEN uuid IS NULL THEN 1 ELSE 0 END)
  FROM rh_t_def_remuneracoes;

-- 2) Correção
UPDATE rh_t_def_pagamentos
   SET uuid = LOWER(REGEXP_REPLACE(RAWTOHEX(SYS_GUID()),
                    '^(.{8})(.{4})(.{4})(.{4})(.{12})$', '\1-\2-\3-\4-\5'))
 WHERE uuid IS NULL;

UPDATE rh_t_def_remuneracoes
   SET uuid = LOWER(REGEXP_REPLACE(RAWTOHEX(SYS_GUID()),
                    '^(.{8})(.{4})(.{4})(.{4})(.{12})$', '\1-\2-\3-\4-\5'))
 WHERE uuid IS NULL;

-- 3) Verificação (deve dar sem_uuid = 0 e nenhum duplicado)
SELECT 'RH_T_DEF_PAGAMENTOS' tabela, SUM(CASE WHEN uuid IS NULL THEN 1 ELSE 0 END) sem_uuid,
       COUNT(*) - COUNT(DISTINCT uuid) duplicados
  FROM rh_t_def_pagamentos
UNION ALL
SELECT 'RH_T_DEF_REMUNERACOES', SUM(CASE WHEN uuid IS NULL THEN 1 ELSE 0 END),
       COUNT(*) - COUNT(DISTINCT uuid)
  FROM rh_t_def_remuneracoes;

-- COMMIT;

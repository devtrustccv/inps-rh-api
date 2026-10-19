-- =====================================================================
-- Ordens de Serviço — alinhamento dos templates (RH_T_PARAM_DOC_OUTPUT)
--
-- O tipo escolhido no ecrã vem do domínio ORDEM_SERVICO e o backend procura o template por
-- TIPO_DOCUMENTO = esse valor. Dois templates tinham outro nome e davam erro:
--   LICENSA_SEM_VENCIMENTO -> LICENCA_S_VENCIMENTO  (domínio e enum Java)
--   NOMEACAO_COORDENADOR   -> NOMEACAO
-- Também: templates de teste inactivados e texto do Fim de Comissão generalizado
-- (tinha fixo "Coordenador das UPS do Fogo e Brava").
-- =====================================================================

UPDATE RH_T_PARAM_DOC_OUTPUT SET TIPO_DOCUMENTO = 'LICENCA_S_VENCIMENTO'
 WHERE TIPO_DOCUMENTO = 'LICENSA_SEM_VENCIMENTO';

UPDATE RH_T_PARAM_DOC_OUTPUT SET TIPO_DOCUMENTO = 'NOMEACAO'
 WHERE TIPO_DOCUMENTO = 'NOMEACAO_COORDENADOR';

-- Registos de teste ("DELC" com corpo vazio de sentido)
UPDATE RH_T_PARAM_DOC_OUTPUT SET ESTADO = 'I'
 WHERE TIPO_DOCUMENTO = 'DELC' AND ESTADO = 'A';

UPDATE RH_T_PARAM_DOC_OUTPUT
   SET CORPO = 'A pedido do interessado, é dada por finda a comissão de serviço do colaborador ${nomeColaborador}, ${cargoColaborador}.' || CHR(10) || CHR(10) ||
               'Esta Ordem de Serviço produz efeitos a partir do dia ${dataEfeito}.'
 WHERE TIPO_DOCUMENTO = 'FIM_COMISSAO_SERVICO' AND ESTADO = 'A';

COMMIT;

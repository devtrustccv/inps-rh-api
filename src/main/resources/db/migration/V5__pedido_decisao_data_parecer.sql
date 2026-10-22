-- Data do parecer escolhida no formulário (técnico e Validação Responsável). Até aqui o campo
-- "Data" do parecer não era guardado (o ecrã mostrava a data de registo); a data de execução
-- continua a ser DATA_REGISTO / DATA_ALTERACAO / DATA_OBSERVACAO_RESPONSAVEL.
ALTER TABLE RH_T_PEDIDO_DECISAO ADD (
  DATA_PARECER DATE,
  DATA_PARECER_RESPONSAVEL DATE
);

-- ---------------------------------------------------------------------------
-- RH_T_FUNCIONARIOS: nova coluna LOCALIDADE (texto livre)
-- Guarda a localidade do colaborador (campo adicional aos dados pessoais,
-- distinto da Naturalidade que é LOC_NASC_ID -> geografia).
-- Aplicado no ambiente de desenvolvimento; replicar nos restantes.
-- ---------------------------------------------------------------------------
ALTER TABLE RH_T_FUNCIONARIOS ADD (LOCALIDADE VARCHAR2(200));

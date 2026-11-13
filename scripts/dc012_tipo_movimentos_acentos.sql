-- DC-012: repõe acentos perdidos em INPSSIGOF.RH_TIPO_MOVIMENTOS.DESCRICAO
-- (os caracteres acentuados foram gravados como espaços; ver DUMP). Só movimentos
-- ACTIVOS de AMB_APL_ID = 30 (os que aparecem nos combos de Remunerações/Descontos).
-- Cada UPDATE só aplica se a descrição ainda for a corrompida (idempotente).
-- NÃO EXECUTADO. Tabela do schema INPSSIGOF: confirmar com o dono antes de correr.
-- Nota: 'Antendedores' corrigido para 'Atendedores'.

UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Diferença De Vencimentos Pessoal Quadro' WHERE ID = 1688 AND DESCRICAO = 'Diferen a De Vencimentos Pessoal Quadro';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Reposição De Acerto Iur Retido' WHERE ID = 1700 AND DESCRICAO = 'Reposi  o De Acerto Iur Retido';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Subsídio Isenção De Horário' WHERE ID = 1715 AND DESCRICAO = 'Subsidio Isen  o De Hor rio';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção IUR_Terceiros' WHERE ID = 1719 AND DESCRICAO = 'Reten  o IUR_Terceiros';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Pensão Alimentícia' WHERE ID = 1721 AND DESCRICAO = 'Retencao Pens o Alimenticia';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Iur Pensionistas' WHERE ID = 1725 AND DESCRICAO = 'Reten  o Iur Pensionistas';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção de Aquisição de Viatura' WHERE ID = 1726 AND DESCRICAO = 'Reten ao de Aquisi  o de Viatura';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Descontos Judiciais' WHERE ID = 1727 AND DESCRICAO = 'Reten  o Descontos Judiciais';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Restituição de Vencimentos' WHERE ID = 1728 AND DESCRICAO = 'Reten  o Restituicao de  Vencimentos';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Juros Crédito Habitação' WHERE ID = 1729 AND DESCRICAO = 'Reten ao Juros Credito Habita  o';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Reforma Antecipada (IUR)' WHERE ID = 1730 AND DESCRICAO = 'Reten  o Reforma Antecipada (IUR)';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Previdência Social' WHERE ID = 1741 AND DESCRICAO = 'Reten  o Previdencia Social';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Senha De Presença' WHERE ID = 1843 AND DESCRICAO = 'Senha De  Presen a';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Cantina - Praia' WHERE ID = 1887 AND DESCRICAO = 'Reten ao Cantina - Praia';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenções Iur Predial' WHERE ID = 1888 AND DESCRICAO = 'Reten oes Iur Predial';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Cantina - S.Vicente' WHERE ID = 1895 AND DESCRICAO = 'Reten ao Cantina - S.Vicente';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Quotas Sindicais (STCS)' WHERE ID = 1933 AND DESCRICAO = 'Reten  o Quotas Sindicais ( STCS)';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Quotas Sindicais (STIF)' WHERE ID = 1935 AND DESCRICAO = 'Reten  o Quotas Sindicais ( STIF )';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Iur - (Pessoal INPS)' WHERE ID = 1940 AND DESCRICAO = 'Reten  o Iur - (Pessoal INPS)';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Fundo Social - Fins Diversos' WHERE ID = 11000 AND DESCRICAO = 'Reten ao Fundo Social - Fins Diversos';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Diferença De Vencimentos Pessoal Contratado' WHERE ID = 11121 AND DESCRICAO = 'Diferen a De Vencimentos Pessoal Contratado';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagamento Retenções Iur' WHERE ID = 11122 AND DESCRICAO = 'Pagamento Reten  es Iur';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Reforma Antecipada (STIF)' WHERE ID = 11125 AND DESCRICAO = 'Reten  o Reforma Antecipada (STIF)';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Fundo Social (Ginásio)' WHERE ID = 11181 AND DESCRICAO = 'Fundo Social (Gin sio)';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Penhora Vencimento_INPS' WHERE ID = 11300 AND DESCRICAO = 'Reten  o Penhora Vencimento_INPS';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Fundo Social - Construção' WHERE ID = 11981 AND DESCRICAO = 'Retencao Fundo Social - Constru  o';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagamento Retenções Iur Pensionista' WHERE ID = 12001 AND DESCRICAO = 'Pagamento Reten  es Iur Pensionista';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção IRS_CADE' WHERE ID = 12300 AND DESCRICAO = 'Reten  o IRS_CADE';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Subsídio De Natal' WHERE ID = 12321 AND DESCRICAO = 'Subs dio De Natal';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Juros BT' WHERE ID = 12322 AND DESCRICAO = 'Reten  o Juros BT';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Subsídio De Risco Fiscais' WHERE ID = 12344 AND DESCRICAO = 'Subs dio De Risco Fiscais';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Previdência Social - Regularização' WHERE ID = 12362 AND DESCRICAO = 'Reten  o Previdencia Social - Regulariza  o';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Gratificações Eventuais' WHERE ID = 12380 AND DESCRICAO = 'Gratifica  es Eventuais';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Conversão De Férias - Pessoal Quadro' WHERE ID = 12382 AND DESCRICAO = 'Convers o De F rias - Pessoal Quadro';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Subsídio Atendedores Ups' WHERE ID = 12402 AND DESCRICAO = 'Subs dio Antendedores Ups';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Subsídio Atendedores Balcões' WHERE ID = 12403 AND DESCRICAO = 'Subs dio Antendedores Balc es';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Subsídio Atendedores Retroactivos' WHERE ID = 12404 AND DESCRICAO = 'Subs dio Antendedores Retroactivos';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Conversão De Férias - Pessoal Contratado' WHERE ID = 12422 AND DESCRICAO = 'Convers o De F rias - Pessoal Contratado';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Penhora Vencimento_Mf' WHERE ID = 12482 AND DESCRICAO = 'Reten  o Penhora Vencimento_Mf';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagam. Restituição Vencimento' WHERE ID = 12502 AND DESCRICAO = 'Pagam. Restitui  o Vencimento';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagam. Retenção Stcs' WHERE ID = 12503 AND DESCRICAO = 'Pagam. Reten  o Stcs';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagam. Retenção Cantina Praia (Fundo Social)' WHERE ID = 12505 AND DESCRICAO = 'Pagam. Reten ao Cantina Praia (Fundo Social)';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagam. Retenção Fs- Fins Diversos' WHERE ID = 12506 AND DESCRICAO = 'Pagam. Reten ao Fs- Fins Diversos';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagam. Retenção Saúde' WHERE ID = 12522 AND DESCRICAO = 'Pagam. Reten ao Saude';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagamento Retenções Iur Terceiros' WHERE ID = 12562 AND DESCRICAO = 'Pagamento Reten  es Iur Terceiros';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Taxa Teu' WHERE ID = 12603 AND DESCRICAO = 'Reten  o Taxa Teu';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Regularização Reforma Antecipada (Stif)' WHERE ID = 12642 AND DESCRICAO = 'Regulariza  o Reforma Antecipada (Stif)';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagamento Retenções Teu' WHERE ID = 12682 AND DESCRICAO = 'Pagamento Reten  es Teu';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagam. Pensão Alimentícia' WHERE ID = 12743 AND DESCRICAO = 'Pagam. Pens o Aliment cia';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Retenção Previdência Social CADE' WHERE ID = 12801 AND DESCRICAO = 'Reten  o Previdencia Social CADE';
UPDATE INPSSIGOF.RH_TIPO_MOVIMENTOS SET DESCRICAO = 'Pagam. Retenção Cantina S. Vicente' WHERE ID = 12843 AND DESCRICAO = 'Pagam. Reten  o Cantina S. Vicente';

COMMIT;

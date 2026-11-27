-- Idempotente: rótulos do dominio PARECER, referencia PARECER (Análise Financeira, Verificação RH)
-- estavam sem acento ("Favoravel"/"Desfavoravel"). Os códigos (VALOR) não mudam; a Análise RH e a
-- Autorização CE têm rótulos próprios (referencias ANALISE_RH e AUTORIZACAO_CE).
UPDATE RH_T_DOMAINS
   SET DESCRICAO = 'Favorável'
 WHERE DOMINIO = 'PARECER'
   AND REFERENCIA = 'PARECER'
   AND VALOR = 'FAVORAVEL';

UPDATE RH_T_DOMAINS
   SET DESCRICAO = 'Desfavorável'
 WHERE DOMINIO = 'PARECER'
   AND REFERENCIA = 'PARECER'
   AND VALOR = 'DESFAVORAVEL';

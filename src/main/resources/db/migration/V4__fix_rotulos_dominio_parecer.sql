-- Idempotente: rótulos do dominio PARECER sem referencia (Análise Financeira, Autorização CE,
-- Verificação RH) estavam sem acento ("Favoravel"/"Desfavoravel"). Os códigos (VALOR) não mudam;
-- a Análise RH usa os seus próprios rótulos (referencia ANALISE_RH, ver V3).
UPDATE RH_T_DOMAINS
   SET DESCRICAO = 'Favorável'
 WHERE DOMINIO = 'PARECER'
   AND REFERENCIA IS NULL
   AND VALOR = 'FAVORAVEL';

UPDATE RH_T_DOMAINS
   SET DESCRICAO = 'Desfavorável'
 WHERE DOMINIO = 'PARECER'
   AND REFERENCIA IS NULL
   AND VALOR = 'DESFAVORAVEL';

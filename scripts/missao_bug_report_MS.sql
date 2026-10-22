-- =====================================================================
-- Bug report SIPS-RH Missão de Serviço 1.0 (QA 14/10/2026) — correcção de dados
-- Acompanha o branch fix/missao-bug-report-qa (22/10/2026). Cada bloco é um statement.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) MS-014 ("cabimento gerado sem número"): o ecrã mandava cabId = 0 por defeito e gravava-se 0.
--    0 não é nº de cabimento — passa a NULL (o backend agora ignora/limpa o 0).
--    Em dev/QA, a 22/10: linhas 281–287, 293, 294.
-- ---------------------------------------------------------------------
UPDATE RH_T_MISSAO_LOGISTICA SET CAB_ID = NULL WHERE CAB_ID = 0;

-- ---------------------------------------------------------------------
-- 2) MS-017: alojamento gravado com VALOR_TOTAL 0 antes do fix de 16/10 (568695b7).
--    Total = valor diário × nº de dias. Em dev/QA, a 22/10: linha 290 (17/2026), 10000 × 3.
-- ---------------------------------------------------------------------
UPDATE RH_T_MISSAO_LOGISTICA
   SET VALOR_TOTAL = VALOR_DIARIO * NR_DIAS
 WHERE REFERENCIA = 'ALOJAMENTO' AND NVL(VALOR_TOTAL, 0) = 0
   AND NVL(VALOR_DIARIO, 0) > 0 AND NR_DIAS > 0;

-- ---------------------------------------------------------------------
-- 3) MS-029 / D8: o nome do prestador é sempre o da entidade (INPSSIGOF.ENTIDADES).
--    ATENÇÃO: muda o que o ecrã mostra nos prestadores de teste (ex.: "TAP" está ligado à
--    entidade "SINTAP - SINDICATO ..."). Rever as ligações ENT_ID antes de correr.
--    RH_T_MISSAO_PRESTADOR.NOME (fotografia na missão) não muda.
-- ---------------------------------------------------------------------
-- UPDATE RH_T_PARAM_PRESTADOR p
--    SET NOME = (SELECT e.NOME FROM INPSSIGOF.ENTIDADES e WHERE e.ID = p.ENT_ID)
--  WHERE EXISTS (SELECT 1 FROM INPSSIGOF.ENTIDADES e WHERE e.ID = p.ENT_ID AND NVL(e.NOME,'#') <> NVL(p.NOME,'#'));

-- ---------------------------------------------------------------------
-- 4) Fora deste script (DBA / analista):
--    - MS-012: GRANT EXECUTE ON SIPSV0.SEND_MAIL_V1 TO INPSRH (ou sinónimo) — sem isto nenhum
--      email sai (todas as notificações ficam "Erro").
--    - MS-022: tipos de documento da missão (RH_T_TIPOS_DOCUMENTOS, REFERENCIA = 'MISSAO_SERVICO');
--      os nomes dos tipos têm de vir do analista.
--    - Ajudas de custo 291/292 (19/2026) gravadas a 0 antes do fix: só o RH sabe o valor certo —
--      regravar pela logística.
-- ---------------------------------------------------------------------

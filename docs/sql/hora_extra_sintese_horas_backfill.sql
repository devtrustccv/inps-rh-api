-- =============================================================================
-- Hora extra — corrigir HORAS_EXTRAS das sinteses gravadas antes da correcao (2026-11-25)
--
-- Ate agora o registo de hora extra gravava RH_T_HORA_EXTRA.HORAS_DIARIAS (horas) como MINUTOS na
-- sintese diaria (2 h -> 00:02) e so no 1.o dia do periodo. O Java ja grava as horas certas em todos
-- os dias; este script corrige o que ficou para tras: a sintese ligada a cada hora extra passa a ter
-- as horas diarias.
--
-- Horas extra rejeitadas (I) ou eliminadas (E) deixam de contar na sintese: o Java ja limpa ao rejeitar;
-- aqui limpa-se o que foi rejeitado antes (passo 2).
-- Os outros dias do periodo (que nunca tiveram sintese) nao sao criados aqui: ficam como estavam.
-- POR APLICAR EM: desenvolvimento, staging, producao. Em dev: passo 1 afecta 1 linha (hora extra 93 -> sintese 1280), passo 2 nenhuma.
-- =============================================================================

-- Passo 1: horas extra validas (P, C, A) gravadas como minutos -> horas
-- Conferir antes: o que vai mudar
SELECT h.ID hora_extra, h.HORAS_DIARIAS, s.ID sintese, s.HORAS_EXTRAS
  FROM RH_T_HORA_EXTRA h
  JOIN RH_ASSIDUIDADE_SINTESE_DIARIA s ON s.ID = h.SINTESE_DIARIO_ID
 WHERE h.HORAS_DIARIAS BETWEEN 1 AND 23
   AND h.ESTADO NOT IN ('I', 'E')
   AND s.HORAS_EXTRAS = NUMTODSINTERVAL(h.HORAS_DIARIAS, 'MINUTE');

UPDATE RH_ASSIDUIDADE_SINTESE_DIARIA s
   SET s.HORAS_EXTRAS = (SELECT NUMTODSINTERVAL(h.HORAS_DIARIAS, 'HOUR')
                           FROM RH_T_HORA_EXTRA h
                          WHERE h.SINTESE_DIARIO_ID = s.ID AND ROWNUM = 1)
 WHERE EXISTS (SELECT 1 FROM RH_T_HORA_EXTRA h
                WHERE h.SINTESE_DIARIO_ID = s.ID
                  AND h.HORAS_DIARIAS BETWEEN 1 AND 23
                  AND h.ESTADO NOT IN ('I', 'E')
                  AND s.HORAS_EXTRAS = NUMTODSINTERVAL(h.HORAS_DIARIAS, 'MINUTE'));

-- Passo 2: horas extra rejeitadas/eliminadas que ficaram a contar na sintese (gravadas como minutos)
SELECT h.ID hora_extra, h.ESTADO, s.ID sintese, s.HORAS_EXTRAS
  FROM RH_T_HORA_EXTRA h
  JOIN RH_ASSIDUIDADE_SINTESE_DIARIA s ON s.ID = h.SINTESE_DIARIO_ID
 WHERE h.ESTADO IN ('I', 'E')
   AND s.HORAS_EXTRAS = NUMTODSINTERVAL(h.HORAS_DIARIAS, 'MINUTE');

UPDATE RH_ASSIDUIDADE_SINTESE_DIARIA s
   SET s.HORAS_EXTRAS = NULL
 WHERE EXISTS (SELECT 1 FROM RH_T_HORA_EXTRA h
                WHERE h.SINTESE_DIARIO_ID = s.ID
                  AND h.ESTADO IN ('I', 'E')
                  AND s.HORAS_EXTRAS = NUMTODSINTERVAL(h.HORAS_DIARIAS, 'MINUTE'));

COMMIT;

-- RH_T_FUNCIONARIOS: o id da nacionalidade passa para NACION_ID (pedido do analista, 11/11/2026).
-- Até agora o frontend mandava o id da geografia (ex. 1238) e ele ia parar a NACIONALIDADE.
-- Depois disto: NACION_ID = id em SIPSGLOBAL.GLB_T_GEOGRAFIA, NACIONALIDADE = texto da geografia (ex. CABOVERDIANO).
-- Só toca nas linhas em que NACIONALIDADE é numérica e NACION_ID ainda está vazio.

UPDATE RH_T_FUNCIONARIOS f
   SET f.NACION_ID     = TO_NUMBER(TRIM(f.NACIONALIDADE)),
       f.NACIONALIDADE = (SELECT NVL(TRIM(g.NACIONALIDADE), g.NOME)
                            FROM SIPSGLOBAL.GLB_T_GEOGRAFIA g
                           WHERE g.ID = TO_NUMBER(TRIM(f.NACIONALIDADE)))
 WHERE f.NACION_ID IS NULL
   AND REGEXP_LIKE(TRIM(f.NACIONALIDADE), '^[0-9]+$')
   AND EXISTS (SELECT 1 FROM SIPSGLOBAL.GLB_T_GEOGRAFIA g WHERE g.ID = TO_NUMBER(TRIM(f.NACIONALIDADE)));

COMMIT;

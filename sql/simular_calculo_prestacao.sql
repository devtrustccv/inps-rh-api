WITH params AS (
    SELECT
        CAST(6000000 AS NUMBER(18,2))     AS valor_emprestado,
        CAST(0.035   AS NUMBER(18,10))    AS taxa_anual,
        CAST(12     AS NUMBER(10,0))     AS prazo_meses,
        CAST(DATE '2030-01-01' AS DATE)   AS data_inicio
    FROM dual
),
     rec_cte (
              numero,
              data_pagamento,
              saldo_inicial,
              prestacao,
              juros,
              principal,
              saldo_final,
              prazo_meses,
              taxa_mensal
         ) AS (
         -- first row
         SELECT
             CAST(1 AS NUMBER(10,0)) AS numero,
             CAST(ADD_MONTHS(p.data_inicio, 1) AS DATE) AS data_pagamento,
             CAST(p.valor_emprestado AS NUMBER(18,2)) AS saldo_inicial,
             CAST(
                     ROUND(
                             p.valor_emprestado * (p.taxa_anual / 12)
                                 / (1 - POWER(1 + p.taxa_anual / 12, -p.prazo_meses)),
                             2
                     ) AS NUMBER(18,2)
             ) AS prestacao,

             CAST(ROUND(p.valor_emprestado * (p.taxa_anual / 12), 2) AS NUMBER(18,2)) AS juros,

             CAST(
                     ROUND(
                             ROUND(
                                     p.valor_emprestado * (p.taxa_anual / 12)
                                         / (1 - POWER(1 + p.taxa_anual / 12, -p.prazo_meses)),
                                     2
                             ) - (p.valor_emprestado * (p.taxa_anual / 12)),
                             2
                     ) AS NUMBER(18,2)
             ) AS principal,

             CAST(
                     ROUND(
                             p.valor_emprestado - (
                                 ROUND(
                                         p.valor_emprestado * (p.taxa_anual / 12)
                                             / (1 - POWER(1 + p.taxa_anual / 12, -p.prazo_meses)),
                                         2
                                 ) - (p.valor_emprestado * (p.taxa_anual / 12))
                                 ),
                             2
                     ) AS NUMBER(18,2)
             ) AS saldo_final,

             CAST(p.prazo_meses AS NUMBER(10,0)) AS prazo_meses,
             CAST(p.taxa_anual / 12 AS NUMBER(18,10)) AS taxa_mensal
         FROM params p

         UNION ALL

         -- next rows
         SELECT
             CAST(r.numero + 1 AS NUMBER(10,0)) AS numero,
             CAST(ADD_MONTHS(r.data_pagamento, 1) AS DATE) AS data_pagamento,

             CAST(r.saldo_final AS NUMBER(18,2)) AS saldo_inicial,
             CAST(r.prestacao   AS NUMBER(18,2)) AS prestacao,

             CAST(ROUND(r.saldo_final * r.taxa_mensal, 2) AS NUMBER(18,2)) AS juros,

             CAST(ROUND(r.prestacao - ROUND(r.saldo_final * r.taxa_mensal, 2), 2) AS NUMBER(18,2)) AS principal,

             CAST(
                     ROUND(r.saldo_final - (r.prestacao - ROUND(r.saldo_final * r.taxa_mensal, 2)), 2)
                 AS NUMBER(18,2)
             ) AS saldo_final,

             CAST(r.prazo_meses AS NUMBER(10,0)) AS prazo_meses,
             CAST(r.taxa_mensal AS NUMBER(18,10)) AS taxa_mensal
         FROM rec_cte r
         WHERE r.numero < r.prazo_meses
     )
SELECT
    numero,
    TO_CHAR(data_pagamento, 'DD/MM/YYYY') AS data_pagamento,
    saldo_inicial,
    prestacao AS pagamento,
    principal,
    juros,
    saldo_final
FROM rec_cte
ORDER BY numero;



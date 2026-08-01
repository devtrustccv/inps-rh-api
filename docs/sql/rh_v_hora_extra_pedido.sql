-- =====================================================================
-- RH_V_HORA_EXTRA_PEDIDO  (nova)
--
-- Nivel 1 da lista de hora extra: uma linha por PEDIDO.
--
-- Porque: a validacao e por pedido (POST hora-extra/{pedidoId} valida todos os
-- registos de uma vez). A lista antiga mostrava uma linha por registo, ficando
-- num grao diferente da accao — o RH via N linhas e so conseguia agir sobre o
-- pedido inteiro.
--
-- O detalhe (colaborador x mes) vive em RH_V_HORA_EXTRA_MENSAL e e servido
-- como `itens` aninhados dentro de cada pedido.
--
-- DIRECAO/SECCAO: so vem preenchida quando e unica em todo o pedido; se houver
-- mais do que uma, o ID fica NULL (a aplicacao mostra "Varias"). Para filtrar,
-- usa-se sempre a direccao do item, nao a do pedido.
-- =====================================================================

CREATE OR REPLACE VIEW RH_V_HORA_EXTRA_PEDIDO AS
SELECT
    p.ID                                        AS ID,
    p.ID                                        AS PEDIDO_ID,
    CAST(p.UUID AS VARCHAR2(100))               AS PEDIDO_UUID,
    p.ESTADO,
    CASE p.ESTADO
        WHEN 'P' THEN 'Pendente'
        WHEN 'A' THEN 'Ativo'
        WHEN 'I' THEN 'Inativo'
        ELSE 'Desconhecido'
    END                                         AS ESTADO_DESC,
    p.ETAPA,
    p.DATA_REGISTO                              AS DATA_PEDIDO,

    MIN(v.PERIODO_INICIO)                       AS PERIODO_INICIO,
    MAX(v.PERIODO_FIM)                          AS PERIODO_FIM,

    COUNT(DISTINCT v.FUNCIONARIO_ID)            AS TOTAL_COLABORADORES,
    COUNT(DISTINCT v.HORA_EXTRA_ID)             AS TOTAL_REGISTOS,
    COUNT(DISTINCT v.MES)                       AS TOTAL_MESES,
    SUM(v.VALOR_ACUMULADO_MES)                  AS VALOR_TOTAL,

    -- Direccao/seccao/ilha apenas quando unicas no pedido
    CASE WHEN COUNT(DISTINCT v.ID_DIRECAO) = 1 THEN MIN(v.ID_DIRECAO) END   AS ID_DIRECAO,
    CASE WHEN COUNT(DISTINCT v.ID_DIRECAO) = 1 THEN MIN(v.NOME_DIRECAO) END AS NOME_DIRECAO,
    CASE WHEN COUNT(DISTINCT v.ID_SECAO)   = 1 THEN MIN(v.ID_SECAO) END     AS ID_SECAO,
    CASE WHEN COUNT(DISTINCT v.ID_SECAO)   = 1 THEN MIN(v.NOME_SECAO) END   AS NOME_SECAO,
    CASE WHEN COUNT(DISTINCT v.ID_ILHA)    = 1 THEN MIN(v.ID_ILHA) END      AS ID_ILHA,
    CASE WHEN COUNT(DISTINCT v.ID_ILHA)    = 1 THEN MIN(v.NOME_ILHA) END    AS NOME_ILHA

FROM RH_T_PEDIDO p
JOIN RH_V_HORA_EXTRA_MENSAL v ON v.PEDIDO_ID = p.ID
GROUP BY p.ID, p.UUID, p.ESTADO, p.ETAPA, p.DATA_REGISTO;

-- ============================================================
-- BACKUP das views originais ANTES da migracao INSTITUICOES -> DIRECAO
-- Data: 2026-06-24
-- ============================================================

-- RH_V_FERIAS_VER_MAPA (ORIGINAL - usava inpssigof.instituicoes)
-- ============================================================
CREATE OR REPLACE VIEW INPSRH.RH_V_FERIAS_VER_MAPA (
    ID, FUNCIONARIO_ID, UUID_FUNCIONARIO, NOME_COLABORADOR,
    DIRECAO_ID, DIRECAO, ANO_ID, ANO_REFERENTE,
    FERIAS_MARCADAS_INICIO, FERIAS_MARCADAS_FIM,
    FERIAS_GOZADAS_INICIO, FERIAS_GOZADAS_FIM
) AS
SELECT
    ROW_NUMBER() OVER (ORDER BY f.id, fgm.data_inicio, fg.data_inicio) AS id,
    f.id AS funcionario_id,
    f.uuid AS uuid_funcionario,
    f.nome AS nome_colaborador,
    inst.id AS direcao_id,
    inst.nome AS direcao,
    fgm.ano_id AS ano_id,
    a.ano AS ano_referente,
    fgm.data_inicio AS ferias_marcadas_inicio,
    fgm.data_fim AS ferias_marcadas_fim,
    fg.data_inicio AS ferias_gozadas_inicio,
    fg.data_fim AS ferias_gozadas_fim
FROM rh_t_funcionarios f
         LEFT JOIN rh_t_tipos_relacionamento tr
                   ON tr.fun_id = f.id
                       AND tr.est_act_adm = 1
         LEFT JOIN rh_t_mobilidade m
                   ON m.id = tr.mob_id
         LEFT JOIN inpssigof.instituicoes inst
                   ON inst.id = m.instit_id
         LEFT JOIN rh_t_ferias_mapa fgm
                   ON fgm.fun_id = f.id
         LEFT JOIN rh_t_ferias_gozadas fg
                   ON fg.fun_id = f.id
                       AND fg.data_inicio <= fgm.data_fim
                       AND fg.data_fim >= fgm.data_inicio
         JOIN rh_t_ano a
              ON a.id = fgm.ano_id;

-- GET_NOME_CENTRO_CUSTO (ORIGINAL - usava INPSSIGOF.CENTROS_CUSTO directamente)
-- ============================================================
CREATE OR REPLACE FUNCTION GET_NOME_CENTRO_CUSTO (
    P_INSTIT_ID IN NUMBER
)
RETURN VARCHAR2
IS
    V_NOME INPSSIGOF.ENTIDADES.NOME%TYPE;
BEGIN
    SELECT nome INTO V_NOME
    FROM (
        SELECT b.nome
        FROM INPSSIGOF.CENTROS_CUSTO a
        JOIN INPSSIGOF.ENTIDADES b ON a.ent_id = b.id
        WHERE a.instit_id = P_INSTIT_ID
        ORDER BY a.id
    )
    WHERE ROWNUM = 1;
    RETURN V_NOME;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN NULL;
END;
/

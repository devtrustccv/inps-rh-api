-- =============================================================================
-- Melhorias Dossiê (FASE 6 / 2.2.1) — RH_V_RELACAO_LABORAL expõe o escalão de
-- vínculos PCCS SEM carreira (escalão gravado no tiprel, não na carreira).
--
-- Mudanças vs. versão original:
--  1) ESCALAO_ID  = COALESCE(escalão-da-carreira j.ID, escalão-do-tiprel jt.ID)
--  2) ESCALAO_DESC= COALESCE por campo (NIVEL_REFERENCIA/ESCALAO) — NÃO concatenar
--     primeiro senão o literal '/' impede o COALESCE de cair para jt.
--  3) +RH_T_PARAM_ESCALAO jt  e  AND REL.ESCALAO_ID = JT.ID(+)
--  4) filtro final: AND (D.ESTADO in ('A','P') OR REL.CARREIRA_ID IS NULL)
--     — sem isto, o outer-join de carreira nula excluía toda a linha sem-carreira.
-- Verificado: linhas COM carreira idênticas (42→42); +sem-carreira passam a aparecer.
-- =============================================================================
CREATE OR REPLACE VIEW RH_V_RELACAO_LABORAL AS
select  fi.NOME   NOME_COLABORADOR ,
    fi.UUID                               AS FUNCIONARIO_UUID,
    b.nome                                   CONTRATO_DESC,
    a.ID                                              CONTRATO_ID,
    c.NOME                                            VINCULO_DESC,
    c.ID                                              VINCULO_ID,
    c.FLG_SALARIO                                      FLG_SALARIO,
    d1.NOME                                            DIRECAO_DESC,
    d1.ID                                              DIRECAO_ID,
    g.NOME                                            SECCAO_DESC,
    g.ID                                              SECCAO_ID,
    e.NOME                                            CARREIRA_DESC,
    d.ID                                              CARREIRA_ID,
    d.UUID                                          CARREIRA_UUID,
    REL.EST_ACT_ADM                                    EST_ACT_ADM,
    COALESCE(j.NIVEL_REFERENCIA, jt.NIVEL_REFERENCIA) || '/' || COALESCE(j.ESCALAO, jt.ESCALAO)  ESCALAO_DESC,
    COALESCE(j.ID, jt.ID)                             ESCALAO_ID,
    d.DATA_INICIO || ' / ' || d.DATA_FIM               DATA_CARREIRA,
    a.DATA_INICIO || ' / ' || a.DATA_FIM               DATA_CONTRATO,
    i.NOME                                            CARGO_DESC,
    i.ID                                              CARGO_ID,
    f.NOME                                            SITUACAO_LABORAL_DESC,
    f.ID                                              SITUACAO_LABORAL_ID,
    e1.DATA_INICIO       DATA_INICIO_SITUACAO,
    e1.DATA_FIM          DATA_FIM_SITUACAO,
     c1.DATA_INICIO  DATA_INICIO_MOBILIDADE,
    C1.DATA_FIM DATA_FIM_MOBILIDADE,
    C1.TIPO_SITUACAO   TIPO_SITUACAO_MOBILIDADE,
    e1.TIPO_SITUACAO   TIPO_SITUACAO_LABORAL,
    D.TIPO_SITUACAO  TIPO_SITUACAO_CARREIRA,
    GL.NOME LOCAL_TRAB_ILHA,
    UPS.ID_GEOGRAFIA       lOCAL_UPS,
    glb.concelho CONCELHO_UPS,
    glb.nivel_detalhe NIVEL_DETALHE,
    REL.tipo_situacao tipo_situacao_relac,
    (
           SELECT LISTAGG(GG.descricao, ',') WITHIN GROUP (ORDER BY GG.descricao)
           FROM rh_t_domains GG
           WHERE UPPER(TRIM(GG.DOMINIO)) = 'TIPO_MOV_LABORAL'
             AND GG.valor IN (
                    SELECT TRIM(REGEXP_SUBSTR(REL.tipo_situacao, '[^,]+', 1, LEVEL))
                    FROM dual
                    CONNECT BY REGEXP_SUBSTR(REL.tipo_situacao, '[^,]+', 1, LEVEL) IS NOT NULL
             )
       ) AS tipo_situacao_relac_desc,
(SELECT COUNT(ID) FROM rh_t_proc_funcionarios PF WHERE PF.TIPREL_ID = REL.ID) PROCESSAMENTO,
rel.id TIPREL_ID,
REL.UUID TIPREL_UUID
from RH_T_FUNCIONARIOS     fi,
     RH_T_CONTRATO_VINCULO  a,
     RH_T_PARAM_CONTRATO  b,
     RH_T_PARAM_VINCULO  c,
     RH_T_CARREIRA  d,
     RH_T_PARAM_CARREIRA        e,
     RH_T_PARAM_ESCALAO         j,
     RH_T_PARAM_ESCALAO         jt,
     RH_T_PARAM_CARGO           i,
     RH_T_MOBILIDADE            c1,
     RH_T_DIRECAO     d1,
     RH_T_SECAO                 g,
     RH_T_TIPOS_RELACIONAMENTO REL,
     RH_T_SITUACAO_LABORAL      e1,
     RH_T_PARAM_SITUACAO        f,
     RH_T_PARAM_LOCAL_TRAB  H,
     SIPSGLOBAL.glb_t_geografia GL,
     SIPSGLOBAL.glb_t_ups ups,
     SIPSGLOBAL.glb_t_geografia GLB
WHERE fi.ID = a.fun_id
AND a.TP_CONTRATO_ID = B.ID
AND A.VINCULO_ID = C.ID
AND A.ID = d.contr_vinculo_id(+)
AND D.CARR_PCCS_ID = E.ID(+)
AND D.ESCALAO_ID = J.ID(+)
AND REL.ESCALAO_ID = JT.ID(+)
AND D.CARGO_ID = I.ID(+)
AND Fi.ID = C1.FUN_ID
AND C1.INSTIT_ID = D1.ID
AND C1.SECAO_ID = G.ID(+)
AND e1.contr_vinculo_id = A.ID
AND ((rel.carreira_id IS NOT NULL AND rel.carreira_id = D.ID AND D.ESTADO ='A')  OR (rel.carreira_id IS NULL AND rel.EST_ACT_ADM = 1 ))
AND Fi.id = rel.fun_id
and rel.situac_laboral_id = e1.id
AND e1.situacao_laboral_id = F.ID
AND C1.LOCAL_TRAB_ID = H.ID(+)
AND H.ILHA_ID = GL.ID(+)
AND H.UPS_ID = ups.ID (+)
AND ups.ID_GEOGRAFIA = GLB.ID(+)
AND C1.ESTADO in  ('A', 'P')
AND e1.ESTADO in  ('A', 'P')
AND (D.ESTADO in  ('A', 'P') OR REL.CARREIRA_ID IS NULL)

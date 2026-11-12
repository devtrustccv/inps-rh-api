-- =====================================================================
-- Relação Laboral: vínculo sem carreira mostrava uma carreira ELIMINADA
--
-- Quando o tiprel não tem carreira (REL.CARREIRA_ID IS NULL), a carreira D era ligada só pelo
-- contrato (A.ID = D.CONTR_VINCULO_ID(+)) sem filtrar o estado. Uma carreira eliminada (ESTADO=E)
-- do mesmo contrato aparecia então como CARREIRA_ID/CARREIRA_UUID — o ecrã escondia o botão
-- "Alterar Escalão/Cargo" (que só aparece em SIM_PCCS sem carreira). Caso: Joana Nascimento Lima,
-- Comissão de Serviço, carreira 853 eliminada.
-- Recriada a partir do DDL actual (23/10/2026), só acrescenta D.ESTADO(+) = 'A'.
-- =====================================================================
CREATE OR REPLACE FORCE VIEW "INPSRH"."RH_V_RELACAO_LABORAL" ("FUN_ID", "NOME_COLABORADOR", "FUNCIONARIO_UUID", "CONTRATO_DESC", "CONTRATO_ID", "VINCULO_DESC", "VINCULO_ID", "FLG_SALARIO", "DIRECAO_DESC", "DIRECAO_ID", "SECCAO_DESC", "SECCAO_ID", "CARREIRA_DESC", "CARREIRA_ID", "CARREIRA_UUID", "EST_ACT_ADM", "ESCALAO_DESC", "ESCALAO_ID", "DATA_CARREIRA", "DATA_CONTRATO", "CARGO_DESC", "CARGO_ID", "SITUACAO_LABORAL_DESC", "SITUACAO_LABORAL_ID", "DATA_INICIO_SITUACAO", "DATA_FIM_SITUACAO", "DATA_INICIO_MOBILIDADE", "DATA_FIM_MOBILIDADE", "TIPO_SITUACAO_MOBILIDADE", "TIPO_SITUACAO_LABORAL", "TIPO_SITUACAO_CARREIRA", "LOCAL_TRAB_ILHA", "LOCAL_UPS", "CONCELHO_UPS", "NIVEL_DETALHE", "TIPO_SITUACAO_RELAC", "TIPO_SITUACAO_RELAC_DESC", "PROCESSAMENTO", "TIPREL_ID", "TIPREL_UUID") AS 
select  fi.id FUN_ID,fi.NOME   NOME_COLABORADOR ,
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
    -- cargo da carreira OU, quando o v�nculo n�o tem carreira, do pr�prio tiprel (como o escal�o)
    COALESCE(i.NOME, it.NOME)                         CARGO_DESC,
    COALESCE(i.ID, it.ID)                             CARGO_ID,
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
     RH_T_PARAM_CARGO           it,
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
-- 23/10/2026: a carreira ligada pelo contrato só conta se estiver activa (uma eliminada
-- aparecia em vínculos sem carreira e escondia o botão Alterar Escalão/Cargo)
AND D.ESTADO(+) = 'A'
AND D.CARR_PCCS_ID = E.ID(+)
AND D.ESCALAO_ID = J.ID(+)
AND REL.ESCALAO_ID = JT.ID(+)
AND D.CARGO_ID = I.ID(+)
AND REL.CARGO_ID = IT.ID(+)
AND Fi.ID = C1.FUN_ID
AND C1.INSTIT_ID = D1.ID
AND C1.SECAO_ID = G.ID(+)
AND e1.contr_vinculo_id = A.ID
--AND ((rel.carreira_id IS NOT NULL AND rel.carreira_id = D.ID AND D.ESTADO ='A')  OR (rel.carreira_id IS NULL AND rel.EST_ACT_ADM = 1 ))--dinora comentou em 15/10
AND ((rel.carreira_id IS NOT NULL AND rel.carreira_id = D.ID AND D.ESTADO ='A')  OR (rel.carreira_id IS NULL AND rel.EST_ACT_ADM = 1 ) OR 
(rel.carreira_id IS NULL AND rel.EST_ACT_ADM = 0 AND REL.ESTADO = 'P' ))
AND Fi.id = rel.fun_id
and rel.situac_laboral_id = e1.id
AND e1.situacao_laboral_id = F.ID
AND C1.LOCAL_TRAB_ID = H.ID(+)
AND H.ILHA_ID = GL.ID(+)
AND H.UPS_ID = ups.ID (+)
AND ups.ID_GEOGRAFIA = GLB.ID(+)
--AND C1.ESTADO in  ('A', 'P')
AND C1.ESTADO = 'A'
AND e1.ESTADO in  ('A', 'P')
--AND (D.ESTADO in  ('A', 'P') OR REL.CARREIRA_ID IS NULL)
AND (D.ESTADO = 'A' OR REL.CARREIRA_ID IS NULL);

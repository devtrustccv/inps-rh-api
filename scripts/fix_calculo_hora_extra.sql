CREATE OR REPLACE PACKAGE BODY INPSRH.RH_PROCESSAMENTO_SALARIAL_DB AS

    g_total_funcionarios           NUMBER := 0;
    g_total_remun_colectavel       NUMBER;
    g_total_remun_social           NUMBER;
    g_total_remun                  NUMBER;
    g_total_remun_bruto            NUMBER;
    g_total_pagamentos             NUMBER;
    g_total_liquido                NUMBER;
    g_registos_remun               NUMBER;
    g_liquido_pagamento_id         NUMBER;
    g_tipo_iur                     VARCHAR2 (15);
    g_sal_base                     NUMBER;
    g_abonos_nao_base_incidencia   NUMBER;
    g_valor_substituicao  NUMBER;
    v_divisor_falta      NUMBER := 30;
   -- g_percentagem_inps NUMBER(4,1) := 8.5;

----------------------------------------------------------------------------------------------
 function get_total_dias_n_trab(p_id_funcionario number, p_mes_ref varchar2) return number
    is
        v_count_falta number;    
    begin

        ----Faltas
      /*  SELECT count(*) into v_count_falta
          FROM rh_assiduidades a, rh_tipo_faltas b, rh_tipos_relacionamento rel, rh_funcionarios fun
         WHERE   --  (a.tiprel_id = p_tip_rel)
            rel.id = a.tiprel_id
           and rel.fun_id = fun.id 
           and fun.id = p_id_funcionario 
           AND a.tf_id = b.id
           AND (B.TIPO !=  'FJ_MATDS')
           AND (B.TIPO in  ( 'FJ_PROF','FJ_BM','FI_FI'))
           AND (to_char(a.data_de,'yyyymm') =  (p_mes_ref-1));*/

           SELECT count(*) into v_count_falta 
           FROM rh_t_falta a, rh_t_param_situacao b, rh_t_tipos_relacionamento C
           WHERE a.param_sit_id = b.id
           AND (B.codigo !=  'FJ_MATDS')
           AND (B.CODIGO in  ( 'FJ_PROF','FJ_BM','FI_FI'))
           AND (to_char(a.DATA_INICIO,'yyyymm') =  (p_mes_ref-1))
           AND C.TIPREL_ID = C.ID
           AND c.fun_id = p_id_funcionario;


        return v_count_falta;

    EXCEPTION WHEN OTHERS THEN  
        return 0;
    end;
-------------------------------------------------------------------------------------

 FUNCTION GET_TOTAL_FALTA(p_fun_id number,  p_dt_inicio varchar2 default null,p_dt_fim varchar2 default null) 
 RETURN NUMBER
    is
        v_count_falta number;    
    begin

           SELECT count(*) into v_count_falta 
           FROM rh_t_falta a, rh_t_param_situacao b, rh_t_tipos_relacionamento C
           WHERE a.param_sit_id = b.id
           -- AND (B.codigo !=  'FJ_MATDS')
           --AND (B.CODIGO in  ( 'FJ_PROF','FJ_BM','FI_FI'))
           AND C.TIPREL_ID = C.ID
           AND c.FUN_ID = P_FUN_ID
           AND B.TIPO_FALTA = 'FALTA_INJUSTIFICAD'
           AND (p_dt_inicio is null  or trunc(a.data_inicio) >= to_date(p_dt_inicio,'dd-mm-yyyy'))
           AND (p_dt_fim is null   or trunc(a.data_fim) <= to_date(p_dt_fim,'dd-mm-yyyy'));



        return v_count_falta;

    EXCEPTION WHEN OTHERS THEN  
        return 0;
    end;
--------------------------------------------------------------------------------------

FUNCTION da_imposto (p_imposto VARCHAR2, p_situacao VARCHAR2, p_valor NUMBER)
      RETURN NUMBER
   IS
      v_resp       NUMBER        := 0;
      v_situacao   VARCHAR2 (20);
   BEGIN

      IF p_situacao = 'PENSAO'
      THEN
         v_situacao := 'OUTRO';
      ELSE
         v_situacao := p_situacao;
      END IF;

      SELECT c.valor
      INTO   v_resp
      FROM   impostos_sit b
            ,impostos_sit_det c
            ,impostos a
      WHERE  (b.imp_tipo = a.tipo)
      AND    (c.imps_id = b.ID)
      AND    (a.tipo = p_imposto)
      AND    (b.short_desc = v_situacao)
      AND    c.de <= p_valor
      AND    c.ate >= p_valor;


      RETURN v_resp;
   END;

-------------------------------------------------------------------------------------------------

FUNCTION fn_iur_regra_range (
  p_ano_mes   IN NUMBER,    -- YYYYMM
  p_regime    IN VARCHAR2,  -- 'PENSAO' | 'OUTRO'
  p_sal_base  IN NUMBER,    -- salário base (mensal) quando aplicável
  p_sal_inc   IN NUMBER,    -- base de incidência ajustada (mensal) quando aplicável
  p_valor     IN NUMBER     -- 'p_valor' usado nos casos 2018+ (pensionista/outros)
) RETURN NUMBER
IS
  -- estrutura local para percorrer as regras elegíveis do período regime
  CURSOR c_regras IS
    SELECT VAR_X_COND, VAR_X_CALC,
           BASE_MIN, BASE_MAX,
           FORMULA, NVL(A,0) A, NVL(B,0) B,
           NVL(VALOR_FIXO,0) VALOR_FIXO,
           MIN_RESULT, MAX_RESULT
      FROM RH_T_IUR_RANGE
     WHERE ESTADO = 'A'
       AND p_ano_mes BETWEEN ANO_MES_INI AND ANO_MES_FIM
       AND REGIME = p_regime
     ORDER BY NVL(BASE_MAX, 9e18);  -- garante que encontraremos o 1º ¿encaixe¿

  v_x_cond   NUMBER;
  v_x_calc   NUMBER;
  v_out      NUMBER;
BEGIN
  FOR r IN c_regras LOOP
    -- escolhe a variável para CONDIÇÃO (faixas)
    IF r.VAR_X_COND = 'SAL_BASE' THEN
      v_x_cond := NVL(p_sal_base,0);
    ELSIF r.VAR_X_COND = 'SAL_INC' THEN
      v_x_cond := NVL(p_sal_inc,0);
    ELSE
      v_x_cond := NVL(p_valor,0);  -- 'P_VALOR'
    END IF;

    -- verifica se v_x_cond está dentro da faixa
    IF v_x_cond >= r.BASE_MIN AND v_x_cond <= NVL(r.BASE_MAX, 9e18) THEN
      -- escolhe a variável para CÁLCULO
      IF r.VAR_X_CALC = 'SAL_BASE' THEN
        v_x_calc := NVL(p_sal_base,0);
      ELSIF r.VAR_X_CALC = 'SAL_INC' THEN
        v_x_calc := NVL(p_sal_inc,0);
      ELSE
        v_x_calc := NVL(p_valor,0);
      END IF;

      -- calcula
      IF r.FORMULA = 'FIXO' THEN
        v_out := r.VALOR_FIXO;
      ELSE
        v_out := r.A * v_x_calc + r.B;
      END IF;

      -- aplica clamps (mínimo máximo), se houver
      IF r.MIN_RESULT IS NOT NULL AND v_out < r.MIN_RESULT THEN
        v_out := r.MIN_RESULT;
      END IF;
      IF r.MAX_RESULT IS NOT NULL AND v_out > r.MAX_RESULT THEN
        v_out := r.MAX_RESULT;
      END IF;

      -- nunca negativo
      IF v_out < 0 THEN v_out := 0; END IF;

      RETURN v_out;
    END IF;
  END LOOP;

  -- nenhuma regra encontrada
  RETURN 0;
END;

 ----------------------------NORMALIZA-------------------------------------------------------------

 FUNCTION normaliza (frase IN VARCHAR2)
      RETURN VARCHAR2
   IS
      de           VARCHAR2 (30)  := 'ÃÂÁÀÄÊÉÈËÎÍÌÏÕÔÓÒÖÛÚÙÜÇ''¿&#' || CHR (09);
      para         VARCHAR2 (30)  := 'AAAAAEEEEIIIIOOOOOUUUUC_    ';
      nova_frase   VARCHAR2 (500);
   BEGIN
      SELECT TRANSLATE (UPPER (frase), de, para)
      INTO   nova_frase
      FROM   DUAL;
      RETURN TRIM (nova_frase);
   END normaliza;

FUNCTION normaliza_primeiras_letras (frase IN VARCHAR2)
      RETURN VARCHAR2
    IS
      -- Mapa de acentos (e tabapóstrofo no fim) -> sem acento substitutos
      de    VARCHAR2(100) := 'ãâáàäêéèëîíìïõôóòöûúùüçÃÂÁÀÄÊÉÈËÎÍÌÏÕÔÓÒÖÛÚÙÜÇ''' || CHR(9);
      para  VARCHAR2(100) := 'aaaaaeeeeiiiiooooouuuucAAAAAEEEEIIIIOOOOOUUUUC_ ';
      out_s VARCHAR2(4000) := '';
      ch    CHAR(1);
      start_of_word BOOLEAN := TRUE;  -- início da próxima palavra
    BEGIN
      IF frase IS NULL THEN
        RETURN NULL;
      END IF;

      FOR pos IN 1 .. LENGTH(frase) LOOP
        ch := SUBSTR(frase, pos, 1);

        IF ch = ' ' OR ch = CHR(9) THEN
          -- separador: mantém e marca próximo como início de palavra
          out_s := out_s || ch;
          start_of_word := TRUE;
        ELSE
          IF start_of_word THEN
            -- primeiro carácter da palavra: normaliza com TRANSLATE
            out_s := out_s || TRANSLATE(ch, de, para);
            start_of_word := FALSE;
          ELSE
            -- restantes caracteres: mantêm-se
            out_s := out_s || ch;
          END IF;
        END IF;
      END LOOP;

      RETURN RTRIM(out_s);
    END;


------------------DEVOLVE O TIPO DE PROCESSAMENTO--------------------------------------

  PROCEDURE pr_tipo_processamento (
            p_tipo             IN  VARCHAR2,
            p_flg_faltas OUT VARCHAR2,
            p_obs              OUT VARCHAR2
    )
    IS
    BEGIN
        SELECT PROCESSAR_FALTAS,
               OBS
          INTO p_flg_faltas,
               p_obs
          FROM RH_T_TIPO_PROCESSAMENTO
         WHERE TIPO = p_tipo
           AND ESTADO = 'A';

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_flg_faltas := 'N';
            p_obs := NULL;
      END;
-------------------------MARCADOS PARA NÃO PROCESSAR ------------------------------------

 FUNCTION da_nao_processar (p_cc_id VARCHAR2, p_ano_mes VARCHAR2)
        RETURN NUMBER
    IS
        v_count   NUMBER := 0;
    BEGIN
        SELECT COUNT (DISTINCT a.id)
          INTO v_count
          FROM                                     
              rh_t_tipos_relacionamento a,
              rh_t_def_remuneracoes c,
              rh_t_tiprel_rem_pag cc,
              rh_t_mobilidade mob
         WHERE                                        
               (   mob.INSTIT_ID = p_cc_id)
               AND MOB.ID = A.MOB_ID
               AND (TO_CHAR (
                        DECODE (a.data_fim,
                                NULL, SYSDATE,
                                a.data_fim),
                        'yyyymm') >= p_ano_mes)
               AND (cc.tiprel_id = a.id)
               AND (CC.REM_ID = C.ID)
               AND (p_ano_mes >= TO_CHAR (C.data_inicio, 'yyyymm'))
               AND (p_ano_mes <=
                        TO_CHAR (
                            DECODE (c.data_fim, NULL, SYSDATE, c.data_fim),
                            'yyyymm'))

               AND (a.flg_processa = 0)
               AND C.ESTADO = 'A'
               AND a.est_act_adm = 1;

        RETURN v_count;
    END;

---------------------------------------------------------------------------------------------

 PROCEDURE da_mais_que_um (p_cc_id VARCHAR2, p_ano_mes VARCHAR2, p_msg OUT VARCHAR2)
    IS
        v_count     NUMBER := 0;
        v_nome_cc   VARCHAR2 (200);
    BEGIN
        SELECT normaliza(i.nome)
          INTO v_nome_cc
          FROM inpssigof.instituicoes i, inpssigof.centros_custo cc
         WHERE cc.id = p_cc_id AND cc.instit_id = i.id;

        -- HTP.P('<div class="alert_error"> ERRO :: CENTROS_CUSTO ORC_CC_ID</div>');

        FOR rec IN (SELECT  normaliza(x.nome) nome, tr.id
                      FROM rh_t_funcionarios x, rh_t_tipos_relacionamento tr
                     WHERE     x.id = tr.fun_id
                           AND tr.id IN (SELECT id
                                           FROM (  SELECT a.id, COUNT (*) tot
                                                     FROM inpssigof.centros_custo cc,
                                                          rh_t_tipos_relacionamento a,
                                                          rh_t_def_remuneracoes c,
                                                          rh_t_tiprel_rem_pag cr,
                                                          rh_t_proc_funcionarios y,
                                                          rh_t_proc_salarios z,
                                                          RH_T_MOBILIDADE MOB
                                                    WHERE 
                                                          (   cc.id = p_cc_id)
                                                          AND MOB.INSTIT_ID  = cc.INSTIT_ID
                                                          AND MOB.ID = A.MOB_ID
                                                          AND (cr.tiprel_id = A.id)
                                                          AND (cr.rem_id = c.id)
                                                          AND (p_ano_mes >=TO_CHAR (C.data_inicio,'yyyymm'))
                                                          AND (p_ano_mes <= TO_CHAR (DECODE (c.data_fim,NULL, SYSDATE,c.data_fim),'yyyymm'))
                                                          AND a.est_act_adm = 1
                                                          AND C.ESTADO = 'A'
                                                          AND a.id = y.tiprel_id
                                                          AND y.prsals_id =z.id
                                                          AND TO_CHAR (z.data_de,'yyyymm') = p_ano_mes
                                                 GROUP BY a.id)
                                          WHERE tot > 0))
        LOOP
            FOR rec_proc
                IN (SELECT ps.id proc_sal_id,
                           pf.data_processamento proc_data,
                           ps.data_de,
                           ps.cc_id,
                           i.codigo || '-' || normaliza(i.nome) cc
                      FROM rh_t_proc_funcionarios pf,
                           rh_t_proc_salarios ps,
                           inpssigof.centros_custo cc,
                           inpssigof.instituicoes i
                     WHERE     pf.tiprel_id = rec.id
                           AND TO_CHAR (pf.data_processamento, 'yyyymm') =
                                   p_ano_mes
                           AND pf.prsals_id(+) = ps.id
                           AND ps.cc_id = cc.id(+)
                           AND cc.instit_id = i.id(+))
            LOOP
                HTP.p (
                       'DUP(tr_id='
                    || rec.id
                    || '; em '
                    || TO_CHAR (rec_proc.proc_data, 'dd-mm-yyyy hh24:mi:ss')
                    || ' com data_de='
                    || TO_CHAR (rec_proc.data_de, 'dd-mm-yyyy')
                    || ' no '
                    || rec_proc.cc
                    || '): '
                    || rec.nome);
            END LOOP;

            v_count := v_count + 1;
        END LOOP;

       p_msg := v_nome_cc
            || 'TOTAL DE DUPLICADOS: '
            || v_count;

    END;
------------------------DOTAÇÃO ORÇAMENTAL--------------------------------------------------
 FUNCTION da_duodecimo_actual (p_item_acto    NUMBER,
                                  p_cc           NUMBER,
                                  p_data         DATE)
        RETURN NUMBER
    IS
        v_valor   NUMBER;
    BEGIN
        --foi mudado por dinora pq seg o larangeiro indo a parcela n?o muito seguro
        SELECT DISTINCT (d.total_actual / 12)
          INTO v_valor
          FROM inpssigof.itens_actos a,
               inpssigof.actos b  ,                     -- ,inpssigof.parcelas e
               inpssigof.economicas c,
               inpssigof.centros_custo f,
               inpssigof.orcamentos d
         WHERE     (a.actos_cod_acto = b.cod_acto)
               AND (b.econ_id = c.id)
               AND (d.econ_id = c.id)
               --AND (E.ORC_ID = D.ID)
               AND (d.cc_id = f.id)
               AND f.id = p_cc
               AND a.iac_id = p_item_acto
               AND d.ano_orcamento = TO_CHAR (p_data, 'yyyy');

        --AND p_data BETWEEN e.de_data AND e.ate_data;
        RETURN v_valor;
    EXCEPTION
        WHEN NO_DATA_FOUND
        THEN
            RETURN -1;
    END;

--------------VERIFICAR DUODECIOMO----------------------------------------------------------
 FUNCTION disp_orcamentais_validas (sessid           VARCHAR2,
                                       p_proc_sal_id    VARCHAR2)
        RETURN BOOLEAN
    IS
        v_cc           NUMBER;
        v_data         DATE;
        v_disponivel   NUMBER;
        v_campo_1      VARCHAR2 (500);
        v_campo_2      VARCHAR2 (500);
        erro           BOOLEAN;
    BEGIN
        SELECT cc_id, data_de
          INTO v_cc, v_data
          FROM rh_t_proc_salarios
         WHERE id = p_proc_sal_id;

        FOR rec_reg IN (  SELECT /*+ RULE*/
                                item_acto, descricao, SUM (valor) total
                            FROM RH_V_proc_sal_cc_remun
                           WHERE proc_sal_id = p_proc_sal_id
                        GROUP BY item_acto, descricao)
        LOOP
            v_disponivel :=
                da_duodecimo_actual (rec_reg.item_acto, v_cc, v_data);

            IF rec_reg.total > v_disponivel
            THEN
                RETURN FALSE;
            END IF;
        END LOOP;

        RETURN TRUE;
    END;


------------------PROCESSAR SALARIO----------------------------------------------------------
 PROCEDURE processar (p_dt_inicio varchar2, 
                        p_dt_fim varchar2, 
                        p_cc_id number default null,
                        p_tiprel_id number default null,
                        p_tipo varchar2,
                        P_user_name VARCHAR2,
                        p_user_id NUMBER,
                        p_msg OUT VARCHAR2)
    IS
     v_obs         varchar2(100);
     v_processar_faltas  varchar2(1);
     v_dt_inicio   date;   
     v_dt_fim      date; 
     v_data_atual  date;
    BEGIN
    v_dt_inicio := TO_DATE(p_dt_inicio, 'DD-MM-YYYY');
    v_dt_fim := TO_DATE(p_dt_fim, 'DD-MM-YYYY');

    v_dt_inicio := TRUNC(v_dt_inicio, 'MM');
    v_dt_fim    := TRUNC(v_dt_fim, 'MM');

    
--1 verificar o tipo de Processamento 
      pr_tipo_processamento (p_tipo => p_tipo,
      p_flg_faltas => v_processar_faltas,
      p_obs  =>  v_obs);


               --    REGISTA_FERIAS_NATAL (p_cc_id, P_TIPO , TRUNC(SYSDATE, 'MM') , LAST_DAY(SYSDATE),P_user_name, p_user_id );

--2 Processar 1 centro de custo ou varios 
         FOR i IN 0 .. MONTHS_BETWEEN(v_dt_fim, v_dt_inicio) LOOP

           v_data_atual := LAST_DAY(ADD_MONTHS(v_dt_inicio, i));


  
          IF p_cc_id is not null  or  p_tiprel_id is not null  THEN


                 getao_process_salario_provi (
                            p_cc_id              => p_cc_id,
                            p_data_ref        => v_data_atual,
                            p_processa_faltas => v_processar_faltas,
                            p_tipo => p_tipo,
                            p_tiprel_id     => p_tiprel_id,
                            P_OBS             => v_obs,
                            p_user_id => p_user_id,
                            P_user_name => P_user_name,
                            p_msg => p_msg
                    ); 
           ELSE
             FOR  rec IN ( select distinct c.id 
                            from rh_t_tipos_relacionamento a, 
                            inpssigof.instituicoes b, 
                            inpssigof.centros_custo c,
                            rh_t_mobilidade mob
                            where a.est_act_adm = 1
                            and mob.id =a.mob_id
                            and mob.INSTIT_ID = b.id 
                            and c.instit_id = b.id
                            and a.flg_processa != 0
                            and replace(b.nome,'Processar Remunerações ','') not in ('DAD','DAF')) 
             LOOP



                    getao_process_salario_provi (
                            p_cc_id              => rec.id,
                            p_data_ref        => v_data_atual,
                            p_processa_faltas => v_processar_faltas,
                            p_tipo => p_tipo,
                            p_tiprel_id     => p_tiprel_id,
                            P_OBS             => v_obs,
                            p_user_id => p_user_id,
                            P_user_name => P_user_name,
                            p_msg => p_msg
                    ); 
              END LOOP;
           END IF;

        END LOOP;  
        
EXCEPTION
    WHEN OTHERS THEN
      p_msg := 'ERRO: Código do erro:' || SQLCODE||', Mensagem: ' || SQLERRM;
      ROLLBACK;
    END;

----------------------------------------------------------------------------------------

PROCEDURE getao_process_salario_provi (
        p_cc_id          VARCHAR2 DEFAULT NULL,
        p_data_ref    DATE,
        p_processa_faltas varchar2,
        p_tipo varchar2,
        p_tiprel_id VARCHAR2 DEFAULT NULL,
        p_obs         VARCHAR2,
        p_user_id NUMBER,
        P_user_name VARCHAR2,
        p_msg OUT VARCHAR2
        )
    IS
        resp              NUMBER;
        v_id              NUMBER;
        proc_sal_id       NUMBER;
        v_imagem          VARCHAR2 (1000);
        v_valor_tran      VARCHAR2 (1000);
        V_rh_proc_salarios rh_t_proc_salarios%ROWTYPE;
        V_MSG_ERROR VARCHAR2 (1000);
    BEGIN
    
   V_rh_proc_salarios := null;

-- 1 MOSTRAR OS QUE ESTAO MARCADOS PARA NÃO PROCESSAR (da_nao_processar)
-- 2 MOSTRAR OS QUE ESTAO DUPLICADOS (da_mais_que_um)
-- 3 LISTA COLABORADOR SEM NIB
-- 3 VER SE FAZ SENTIDO VERIFICAR SE O PROCESSAMENTO ESTA FECHADO

-- 3 deve registar o salario proc para te id e registar em outras tabelas 
           g_total_funcionarios := 0;
           V_rh_proc_salarios.cc_id:= TO_NUMBER (p_cc_id);
           V_rh_proc_salarios.data_de:= TRUNC(p_data_ref);--TO_DATE (p_data_ref, 'dd-mm-yyyy');
           V_rh_proc_salarios.estado:= 'PROCESSADO';--'PROV';
           V_rh_proc_salarios.data_proc_provisorio:= SYSDATE;
           v_rh_proc_salarios.obs := p_obs||' (' || TO_CHAR(p_data_ref,'DD-MM-YYYY') || ')';   
           V_rh_proc_salarios.DATA_REGISTO := SYSDATE;        
           V_rh_proc_salarios.USER_REGISTO_ID := p_user_id;   
           V_rh_proc_salarios.USER_REGISTO_NAME := P_user_name;
           V_rh_proc_salarios.TIPO_PROCESSAMENTO := p_tipo;

           INSERT INTO rh_t_proc_salarios VALUES V_rh_proc_salarios RETURNING ID INTO proc_sal_id;
        
        --CASO FOR PROCESSAMENTO DE SUBSIDIO DE FERIAS OU NATAL
        IF P_TIPO IN ('SUBFER', 'SUBNAT') THEN 
          REGISTA_FERIAS_NATAL (proc_sal_id,p_cc_id,p_tiprel_id, P_TIPO , TRUNC(SYSDATE, 'MM') , LAST_DAY(SYSDATE),P_user_name, p_user_id,V_MSG_ERROR );
        END IF;


          -- Agora Vou Processar todos os funcionarios do Centro de Custo em Causa tendo em conta
                resp :=
                    processa_todos_func (
                        p_proc_salarios_id   => proc_sal_id,
                        p_cc_id              => p_cc_id,
                        p_data_de            => p_data_ref,--TO_DATE (p_data_ref,'dd-mm-yyyy'),
                        p_tipo => p_tipo,
                        p_tiprel_id => p_tiprel_id,
                        p_processa_faltas => p_processa_faltas,
                        p_user_id => p_user_id,
                        P_user_name => P_user_name,
                        p_msg => p_msg
                        );


        -- Vou Validar se a Dotac?o Orcamental esta Valida
                IF NOT disp_orcamentais_validas (1, proc_sal_id)
                THEN
                    UPDATE rh_t_proc_salarios
                       SET estado = 'ERRO_PROCESSO',
                        OBS = OBS || ' ,(Sem Dotacão Orcamental)'
                     WHERE id = proc_sal_id;
                 p_msg :=  p_msg ||' Sem Dotacão Orcamental'; 
                END IF;


    END;
---------------------------------------------------------------------------------------

 FUNCTION processa_todos_func (p_proc_salarios_id    NUMBER,
                               p_cc_id          VARCHAR2 DEFAULT NULL,
                               p_data_de             DATE,
                               p_processa_faltas varchar2,
                               p_tipo varchar2,
                               p_tiprel_id VARCHAR2 DEFAULT NULL,
                               p_user_id NUMBER,
                               P_user_name VARCHAR2,
                               p_msg OUT VARCHAR2)
        RETURN NUMBER

    IS
        proc_funcionario_id   NUMBER;
        resp                  NUMBER;
        ano_mes               VARCHAR2 (6);
        v_amb_apl_id          NUMBER := 30;          
        v_val_reman_ds        NUMBER := 0; 
        V_msg_error VARCHAR2 (1000);
    BEGIN

        ano_mes := TO_CHAR (p_data_de, 'yyyymm');

        FOR rec_reg
            IN (SELECT DISTINCT cc.id fun_cc_id --,cc.orc_cc_id cc_id -- B.CC_ID -------------  TRUQUE
                                               ,
                                cc.id cc_id,
                                a.id tipo_rel_id,
                                a.fun_id
                  FROM inpssigof.centros_custo cc,
                       rh_t_tipos_relacionamento a,
                       rh_t_def_remuneracoes c,
                       rh_t_tiprel_rem_pag d,
                       rh_t_mobilidade mob
                 WHERE                              
                       (p_cc_id is null or   cc.id = p_cc_id)
                       AND (MOB.ID = A.MOB_ID)
                       AND (mob.instit_id = cc.instit_id)
                       AND (d.tiprel_id = a.id)
                       and (d.rem_id = c.id)
                       AND (ano_mes >= TO_CHAR (c.data_inicio, 'yyyymm'))
                       AND (ano_mes <=
                                TO_CHAR (
                                    DECODE (c.data_fim,
                                            NULL, SYSDATE,
                                            c.data_fim),
                                    'yyyymm'))

                       AND (a.FLG_PROCESSA = 1)    
                       AND a.est_act_adm = 1 
                       and (p_tiprel_id is null or a.id = p_tiprel_id)
                       AND NOT EXISTS
                                   (SELECT rdr.id --rdrr.tiprel_id
                                      FROM rh_t_proc_salarios rps,
                                           rh_t_proc_funcionarios rpf,
                                           rh_t_def_remuneracoes rdr,
                                           rh_t_tiprel_rem_pag rdrr,
                                           rh_t_remuneracoes rr
                                     WHERE     (rpf.prsals_id = rps.id)
                                           AND (rr.prsal_id = rpf.id)
                                           AND (rr.rem_1_id = rdr.id)
                                           AND a.ID = rdrr.TIPREL_ID
                                           and rdr.id = rdrr.rem_id
                                           AND rps.cc_id = p_cc_id
                                           AND TO_CHAR (
                                                   rpf.data_referencia_de,
                                                   'yyyymm') = ano_mes))
        LOOP

    --return 0;
            g_total_funcionarios := g_total_funcionarios + 1;
            g_total_remun_colectavel := 0;
            g_total_remun_social := 0;
            g_total_pagamentos := 0;
            g_total_remun := 0;
            g_registos_remun := 0;

            g_liquido_pagamento_id := NULL; 

            ------DINORA PENSAR ISSO ----
           /* if p_processa_faltas then
                processa_horas_faltas (rec_reg.tipo_rel_id,
                                       p_data_de,
                                       v_amb_apl_id,
                                       p_proc_salarios_id,
                                       p_fun_id => rec_reg.fun_id);
            end if;*/
           -------------------------
       
            proc_funcionario_id :=
                processa_remuneracoes_func (
                    p_tiprel_id           => rec_reg.tipo_rel_id,
                    p_cc_id               => rec_reg.cc_id,
                    p_proc_salarios_id    => p_proc_salarios_id,
                    p_data_de             => p_data_de,
                    p_estado              => 'A',
                    p_tipo => p_tipo,
                    p_processa_faltas => p_processa_faltas,
                    p_user_id => p_user_id,
                    P_user_name => P_user_name,
                    p_msg_error => p_msg
                    );



            IF g_total_remun < 0.5
            THEN
            p_msg:= p_msg ||' Total de remunerações menor que 0.5,';
                g_total_remun_colectavel := 0;
                g_total_remun_social := 0;
                g_total_pagamentos := 0;
                g_total_remun := 0;
                g_registos_remun := 0;

                BEGIN
                    DELETE rh_t_remuneracoes
                     WHERE id IN
                               (SELECT b.id
                                  FROM rh_t_def_remuneracoes a,
                                       rh_t_tiprel_rem_pag aa,
                                       rh_t_remuneracoes b
                                 WHERE     aa.tiprel_id = rec_reg.tipo_rel_id
                                       AND aa.rem_id = a.id
                                       AND b.rem_1_id = a.id
                                       AND b.prsal_id = p_proc_salarios_id);

                    DELETE FROM rh_t_proc_funcionarios
                          WHERE     tiprel_id = rec_reg.tipo_rel_id
                                AND prsals_id = p_proc_salarios_id;
                EXCEPTION
                    WHEN OTHERS
                    THEN
                        NULL;
                END;
            END IF;


            IF g_registos_remun > 0
            THEN
            
            --REGISTA LINHA DE EMPRESTIMO 
            PROCESSAR_EMPRESTIMO(P_ACCAO  =>'INSERT', 
                              P_TIPREL_ID => p_tiprel_id, 
                              P_PROC_SAL_ID =>NULL,
                              P_PROC_FUN_ID =>NULL,
                              P_USER_ID => p_user_id,
                              P_USER_NAME => P_user_name);    


                resp :=
                    processa_pagamentos_func (
                        p_proc_funcionario_id   => proc_funcionario_id,
                        p_tiprel_id             => rec_reg.tipo_rel_id,
                        p_funcionario_cc_id     => rec_reg.fun_cc_id,
                        p_proc_salarios_id      => p_proc_salarios_id,
                        p_cc_id                 => rec_reg.cc_id,
                        p_data_de               => p_data_de,
                        p_estado                => 'A',
                        p_tipo => p_tipo,
                        p_user_id => p_user_id,
                        P_user_name => P_user_name
                        );


            IF g_liquido_pagamento_id IS NOT NULL
                THEN                                          
                    UPDATE rh_t_pagamentos
                       SET valor = v_val_reman_ds + g_total_remun - g_total_pagamentos
                     WHERE id = g_liquido_pagamento_id;
                ELSE
                    null;

                END IF;

                UPDATE rh_t_proc_funcionarios
                   SET total_remuneracoes = NVL (g_total_remun, 0),
                       total_pagamentos = NVL (g_total_pagamentos, 0),
                       tot_remun_collect = g_total_remun_colectavel,
                       tot_remun_social = g_total_remun_social,
                       tot_liquido = (g_total_remun - g_total_pagamentos) + v_val_reman_ds
                 WHERE id = proc_funcionario_id;

            --ATULAIZAD LINHA DE EMPRESTIMO 
            PROCESSAR_EMPRESTIMO(P_ACCAO  =>'UPDATE', 
                              P_TIPREL_ID => p_tiprel_id, 
                              P_PROC_SAL_ID =>p_proc_salarios_id,
                              P_PROC_FUN_ID => proc_funcionario_id,
                              P_USER_ID => p_user_id,
                              P_USER_NAME => P_user_name);

                resp :=
                    detalha_remuneracoes_func (
                        p_proc_funcionario_id => proc_funcionario_id);
            ELSE
                NULL;
            END IF;
        END LOOP;

        RETURN 0;
    END;

----------------------------------------------------------------------------------------
FUNCTION processa_remuneracoes_func (
        p_tiprel_id            NUMBER,
        p_cc_id    NUMBER,
        p_proc_salarios_id     NUMBER,
        p_data_de              DATE,
        p_estado               VARCHAR2 DEFAULT 'A',
        p_tipo  VARCHAR2,
        p_processa_faltas VARCHAR2,
        p_user_id NUMBER,
        P_user_name VARCHAR2,
        p_msg_error  OUT VARCHAR2 )
        RETURN NUMBER
    IS
        v_proc_funcionario_id   NUMBER;
        v_id                    NUMBER;
        v_valor                 NUMBER;
        v_ord_base              NUMBER;
        v_horas                 NUMBER;
        v_faltas                NUMBER;
        ano_mes                 VARCHAR2 (6);
        v_observacao            VARCHAR2 (500);
        v_cambio_de             VARCHAR2 (15) := NULL;
        v_cambio_para           VARCHAR2 (15) := 'CVE';
        v_cambio                NUMBER := 0;
        v_vl_cambio             NUMBER := 0;

        v_rhb_id                NUMBER;
        v_nu_conta              NUMBER;
        v_nib                   VARCHAR2 (21);
        v_nome_func             VARCHAR2 (300);
        v_rh_proc_funcionarios rh_t_proc_funcionarios%ROWTYPE;
        v_rh_remuneracoes rh_t_remuneracoes%ROWTYPE;
    BEGIN

        SELECT c.rhb_id,
               c.num_conta,
               c.nib,
               a.nome
          INTO v_rhb_id,
               v_nu_conta,
               v_nib,
               v_nome_func
          FROM rh_t_funcionarios a, rh_t_tipos_relacionamento b, rh_t_dados_bancarios c
         WHERE b.id = p_tiprel_id 
         AND a.id = b.fun_id
         and a.id = c.fun_id;

        g_total_remun_colectavel := 0;
        g_total_remun_social := 0;
        g_total_pagamentos := 0;
        g_total_remun := 0;
        g_registos_remun := 0;
        g_sal_base := 0;


        --IF V_NU_CONTA IS NULL
       IF v_nib IS NULL
        THEN
          p_msg_error := p_msg_error||' O Funcionari(o a) '|| v_nome_func||' não nib...,';
           RETURN NULL;
      END IF;


          v_rh_proc_funcionarios.data_processamento := SYSDATE;
          v_rh_proc_funcionarios.data_referencia_de:= p_data_de;
          v_rh_proc_funcionarios.estado:= p_estado;
          v_rh_proc_funcionarios.total_remuneracoes:= 0;
          v_rh_proc_funcionarios.total_pagamentos:= 0;
          v_rh_proc_funcionarios.tot_remun_collect:= 0;
          v_rh_proc_funcionarios.prsals_id:= p_proc_salarios_id;
          v_rh_proc_funcionarios.tiprel_id:= p_tiprel_id;
          v_rh_proc_funcionarios.rhb_id:= v_rhb_id;
          v_rh_proc_funcionarios.nu_conta:= v_nu_conta;
          v_rh_proc_funcionarios.nib:= v_nib;

         INSERT INTO rh_t_proc_funcionarios VALUES v_rh_proc_funcionarios RETURNING ID into  v_proc_funcionario_id;

        g_tipo_iur := NULL;
        g_abonos_nao_base_incidencia := 0;

        ano_mes := TO_CHAR (p_data_de, 'yyyymm');


         BEGIN
                  SELECT SUM (dr.valor), dr.moeda
                    INTO v_ord_base, v_cambio_de
                    FROM rh_tipo_movimentos tm,
                         rh_t_def_remuneracoes dr,
                         rh_t_tiprel_rem_pag drr
                   WHERE                   --TM.SHORT_DESC = 'SAL'  --jmdupret
                        tm.short_desc IN ('SAL', 'SBNT'
                            , 'HEXT' --Introduzido por Jean Dupret no dia 15-02-2019
                            , 'RPC', 'RPQ' --Introduzido por Jean Dupret no dia 08-11-2024
                            )         --jmdupret
                         AND dr.tm_id = tm.id
                         AND dr.estado = 'A'
                         AND (TO_CHAR (dr.data_inicio, 'YYYYMM') <=
                                  TO_CHAR (p_data_de, 'YYYYMM'))
                         AND (   dr.data_fim IS NULL
                              OR TO_CHAR (dr.data_fim, 'YYYYMM') >=
                                     TO_CHAR (p_data_de, 'YYYYMM'))
                         AND drr.tiprel_id = p_tiprel_id
                         and dr.id = drr.rem_id
                         and tm.estado='ACTIVO'
                GROUP BY dr.moeda;
            EXCEPTION
                WHEN OTHERS
                THEN
                  --  htp.p('Funcionario com problema de salario');
                  --  htp.br;
                    v_ord_base := 0;
                    v_cambio_de := 0;
            END;

            IF v_cambio_de IS NOT NULL AND v_cambio_de <> 'CVE'
            THEN
                v_cambio :=
                    inpssigof.orc_financ_projectos.da_cambio_dia (
                        v_cambio_de,
                        v_cambio_para,
                        TO_CHAR (p_data_de, 'dd-mm-yyyy'));

                IF v_cambio IS NOT NULL
                THEN
                    v_ord_base := v_ord_base * v_cambio;
                ELSE
                    v_ord_base := v_ord_base;
                END IF;
            END IF;

            g_sal_base := v_ord_base;


   IF g_sal_base > 0 THEN

        FOR rec_reg
            IN (SELECT /*+ RULE*/
                      c.valor,
                       c.moeda,
                       d.percentagem,
                       d.cobre_imp colectavel,
                       c.id def_remun_id,
                       d.short_desc,
                       d.valor valor_tipo_mov,
                       d.id tipo_mov_id,
                       d.social social,
                       d.tipo_iur,
                       d.acumulado
                  FROM rh_t_def_remuneracoes c,rh_t_tiprel_rem_pag cc, rh_tipo_movimentos d
                 WHERE     (cc.tiprel_id = p_tiprel_id)
                 and (c.id = cc.rem_id)
                  AND (ano_mes >= TO_CHAR (c.data_inicio, 'yyyymm'))
                       AND (ano_mes <=
                                TO_CHAR (
                                    DECODE (c.data_fim, NULL, SYSDATE, c.data_fim),
                                    'yyyymm'))
                       AND (c.estado = 'A')
                        and (D.estado = 'ACTIVO')
                       AND (c.tm_id = d.id))
        LOOP

            -- transforma o cambio logo
            IF rec_reg.moeda IS NOT NULL AND rec_reg.moeda <> 'CVE'
            THEN

                v_cambio_de := rec_reg.moeda;

                v_cambio :=
                    inpssigof.orc_financ_projectos.da_cambio_dia (
                        v_cambio_de,
                        v_cambio_para,
                        TO_CHAR (p_data_de, 'dd-mm-yyyy'));


            IF v_cambio IS NOT NULL
                THEN
                    v_vl_cambio := NVL (rec_reg.valor, 0) * v_cambio;
                ELSE
                    v_vl_cambio := 0;
                END IF;
            ELSE
                v_vl_cambio := rec_reg.valor;
            END IF;


                -- Vou Ver se a Remuneracao a ser tratada e para se ter em conta neste processamento

                IF     rec_reg.colectavel = 'SIM'
                   AND (   rec_reg.short_desc = 'SAL'
                        OR rec_reg.short_desc = 'SBNT'
                        OR rec_reg.short_desc = 'HEXT' --Introduzido por Jean Dupret no dia 15-02-2019
                        )
                THEN
                    g_tipo_iur := rec_reg.tipo_iur;
                END IF;


                    -- Vou Calcular o Valor da Remuneracao tendo em conta o que se encontra na Tabela
                    IF     rec_reg.percentagem IS NULL
                       AND (   rec_reg.valor_tipo_mov IS NULL
                            OR rec_reg.valor_tipo_mov = '0')
                    THEN
                        v_valor := v_vl_cambio;
                    ELSIF rec_reg.percentagem IS NOT NULL
                    THEN                               
                        -- o ordenado base   calculado de fora para poder ser usado nos pagamentos_func, casio seja necess rio...
                        v_valor := v_ord_base * (rec_reg.percentagem / 100);
                    ELSIF     rec_reg.valor_tipo_mov IS NOT NULL
                          AND rec_reg.valor_tipo_mov <> '0'
                    THEN
                        v_valor := rec_reg.valor_tipo_mov;
                    ELSE
                        v_valor := v_vl_cambio;
                    END IF;

                    v_valor := ROUND (v_valor);

                    IF v_valor IS NULL
                    THEN
                        v_valor := 0;
                    END IF;

                    --- dinora paraa rever depois---
                    /*IF rec_reg.short_desc IN ('HEXT', 'FALTAQ', 'FALTAC', 'RPQ', 'RPC')
                    THEN
                        v_observacao :=
                            rh_diversos.da_obs_falta (rec_reg.short_desc,
                                                      p_tiprel_id,
                                                      p_data_de);
                    ELSE
                        v_observacao := NULL;
                    END IF;*/

                    IF rec_reg.acumulado = 'SIM'
                    THEN                             
                        g_abonos_nao_base_incidencia :=
                            g_abonos_nao_base_incidencia + v_valor;
                    END IF;


                    v_rh_remuneracoes.valor := v_valor;
                    v_rh_remuneracoes.data_ref:= p_data_de;
                    v_rh_remuneracoes.estado:= 'A';
                    v_rh_remuneracoes.prsal_id:= v_proc_funcionario_id;
                    v_rh_remuneracoes.rem_1_id:= rec_reg.def_remun_id ;
                    v_rh_remuneracoes.obs := v_observacao;

                   INSERT INTO rh_t_remuneracoes VALUES v_rh_remuneracoes RETURNING ID INTO v_id;


                    IF rec_reg.colectavel = 'SIM'
                    THEN
                        g_total_remun_colectavel :=
                            g_total_remun_colectavel + v_valor;
                    END IF;

                    IF rec_reg.social = 'SIM'
                    THEN
                        g_total_remun_social := g_total_remun_social + v_valor;
                    END IF;

                    g_total_remun := g_total_remun + v_valor;

                    g_registos_remun := g_registos_remun + 1;

        END LOOP;

       END IF;


        RETURN v_proc_funcionario_id;
    END;

 ---------------------------------------------------------------------------------------

FUNCTION processa_pagamentos_func (
  p_proc_funcionario_id NUMBER,
  p_tiprel_id           NUMBER,
  p_funcionario_cc_id   NUMBER,
  p_proc_salarios_id    NUMBER,
  p_cc_id               NUMBER,
  p_data_de             DATE,
  p_estado              VARCHAR2 DEFAULT 'A',
  p_user_id NUMBER,
  P_user_name VARCHAR2,
  p_tipo  VARCHAR2
) RETURN NUMBER
IS

  v_id                     NUMBER;
  v_perc_imposto           NUMBER;
  v_imposto                NUMBER;
  v_situacao               VARCHAR2(10);
  v_a_cobrar               NUMBER;
  v_base_iur               NUMBER;

  v_ano_mes                NUMBER(6);
  v_sal_acum               NUMBER;
  v_meses_acum             NUMBER;
  v_sal_medio_a_data       NUMBER;

  v_instituicao_encargos_comuns VARCHAR2(50);

  v_acum_iur               NUMBER;
  v_iur_bruto              NUMBER;
  v_taxa_media_iur         NUMBER;
  v_iur_diferenca          NUMBER;
  v_sal_inc                NUMBER;
  v_taxa                   NUMBER;
  v_pa                     NUMBER;

  v_vl_salario             NUMBER := 0;
  v_amb_apl_id             NUMBER := 30;

  -- Parametrização (tabelas novas)
  v_minimo_existencia      NUMBER;
  v_arroba                 NUMBER;
  v_taxa_arroba            NUMBER;   -- pode vir de escala anual
  v_isencao_anual          NUMBER;   -- ex.: 960000 (<=2012), 1120000 (2013), etc.
  v_arredonda_dez          CHAR(1) := 'N'; -- 'S' arredonda a 10; 'N' round normal
  v_regime                 VARCHAR2(20);   -- 'NORMAL' ou 'PENSAO'

  v_salario_base           NUMBER;
  v_valor_fn               NUMBER := 0; -- Férias/Natal, etc.
  V_rh_pagamentos rh_t_pagamentos%ROWTYPE;




  BEGIN
  /* =======================
     Início: parâmetros base
  ======================= */
  v_ano_mes := TO_NUMBER(TO_CHAR(p_data_de,'YYYYMM'));

     v_salario_base       := g_sal_base;
     g_total_remun_bruto  := v_salario_base;



   FOR rec_reg
            IN (SELECT /*+ RULE*/
                      c.valor,
                       d.percentagem,
                       c.id def_pagamentos_id,
                       d.short_desc tipo_pagamento,
                       d.valor valor_tipo_mov,
                       d.id tipo_mov_id,
                       d.social social,
                       d.tipo_iur,
                       d.tipo,
                       b.fun_id,
                       a.estado_civil,
                     --  a.num_titular,
                       d.calculo
                  FROM rh_t_tipos_relacionamento b,
                       rh_t_def_pagamentos c,
                       rh_t_tiprel_rem_pag cc,
                       INPSSIGOF.rh_tipo_movimentos d,
                       rh_t_funcionarios a
                 WHERE     (cc.tiprel_id = p_tiprel_id)
                       AND (cc.tiprel_id = b.id)
                       AND (c.id = cc.pag_id)
                       AND (v_ano_mes >= TO_CHAR (c.data_inicio, 'yyyymm'))
                       AND (v_ano_mes <=
                                TO_CHAR (
                                    DECODE (c.data_fim, NULL, SYSDATE, c.data_fim),
                                    'yyyymm'))
                       AND (c.estado = 'A')
                       AND (c.tm_id = d.id)
                       AND a.id = b.fun_id)
        LOOP
    v_perc_imposto := NULL;
    v_imposto      := 0;


    IF UPPER(rec_reg.tipo) = 'IMP' THEN 

          IF rec_reg.tipo_pagamento = 'IUR' THEN
                    IF g_tipo_iur IS NULL
                    THEN
                        v_situacao := 'OUTRO';
                    ELSE
                        v_situacao := g_tipo_iur;
                    END IF;

                    v_a_cobrar := g_total_remun_colectavel;
                ELSE
                    v_situacao := 'NORM';
                    v_a_cobrar := g_total_remun_social;
          END IF;


      IF rec_reg.tipo_pagamento = 'IUR' AND v_situacao in ('PENSAO','OUTRO') THEN

       IF v_situacao ='PENSAO' THEN
          v_imposto := inpssigof.iur.calcula_iur_pensionista(g_sal_base, null);
       ELSE

            SELECT SUM (valor), COUNT (DISTINCT mes)
                          INTO v_sal_acum, v_meses_acum
                          FROM (SELECT DISTINCT
                                       a.tm_id,
                                       a.valor,
                                       TO_CHAR (a.data_processamento,
                                                'yyyymm')
                                           mes
                                  FROM RH_V_proc_sal_cc_remun a,
                                       rh_tipo_movimentos tm
                                 WHERE     a.fun_id = rec_reg.fun_id
                                       AND a.tributavel = 'SIM'
                                       AND A.tiprel_id = p_tiprel_id
                                       AND a.tm_id = tm.id
                                       AND NVL (tm.tipo_iur, 'XXX') <>
                                               'PENSAO'
                                       AND TO_CHAR (a.data_processamento,
                                                    'yyyymm') =
                                               TO_CHAR (p_data_de, 'yyyymm'));

                        v_sal_medio_a_data := v_sal_acum;
                        v_sal_inc := nvl(v_sal_medio_a_data,0);

                       -- IF existe_cod_relacao(p_codigo =>'1.4', p_cod_relacao => p_cod_relacao) = -1 THEN

                            v_imposto := inpssigof.iur.calcula_iur(v_sal_inc, null);     


                        --END IF;

                  END IF;
       -- v_imposto:= fn_iur_regra_range (p_ano_mes => v_ano_mes,    
                           --      p_regime => v_situacao,
                            --     p_sal_base => g_sal_base, 
                             --    p_sal_inc => v_sal_inc,
                             --    p_valor  => v_a_cobrar);


     ELSE
        /*IF v_situacao = 'NORM'
                   THEN
                         v_perc_imposto :=
                            da_imposto (
                                p_imposto    => rec_reg.tipo_pagamento,
                                p_situacao   => v_situacao,
                                p_valor      => v_a_cobrar * 14);       --12);
                    ELSIF v_situacao = 'OUTRO'
                    THEN
                        v_perc_imposto :=
                            da_imposto (
                                p_imposto    => rec_reg.tipo_pagamento,
                                p_situacao   => v_situacao,
                                p_valor      => v_a_cobrar);
                    ELSIF v_situacao = 'LIBER'
                    THEN
                        v_perc_imposto :=
                            da_imposto (
                                p_imposto    => rec_reg.tipo_pagamento,
                                p_situacao   => v_situacao,
                                p_valor      => v_a_cobrar);
                    ELSE
                        v_perc_imposto := 0;
                    END IF;*/
                    IF v_situacao = 'NORM' THEN
                     v_perc_imposto := g_percentagem_inps;
                     v_imposto := v_a_cobrar * (v_perc_imposto / 100);
                     END IF;
                    -- DBMS_OUTPUT.PUT_LINE('v_imposto1: ' || v_imposto);
                    -- DBMS_OUTPUT.PUT_LINE('v_situacao1: ' || v_situacao);

        END IF;

    ELSE

            IF rec_reg.tipo_pagamento = 'SALL'
                THEN
                    v_imposto := 0;
                ELSIF rec_reg.percentagem IS NOT NULL
                THEN
                    v_imposto := (g_sal_base * rec_reg.percentagem) / 100;
                ELSE                                    --   porque   valor...
                    v_imposto := rec_reg.valor;
                END IF;

            END IF;


          IF UPPER (rec_reg.calculo) = 'BASE'
            THEN
                v_imposto := (v_salario_base * rec_reg.percentagem) / 100;
            END IF;

             v_imposto := ROUND (v_imposto);


           -- if rec_reg.tipo_pagamento in ('IUR', 'INPS') and existe_cod_relacao(p_codigo => '1.4', p_cod_relacao => p_cod_relacao) = 1 and rec_reg.valor > 0 then
            --    v_imposto := rec_reg.valor;
           -- end if;


             v_rh_pagamentos.valor := v_imposto;
             v_rh_pagamentos.data_ref:= p_data_de; 
             v_rh_pagamentos.estado:= 'A';  
             v_rh_pagamentos.prsal_id:= p_proc_funcionario_id ;  
             v_rh_pagamentos.defp_id:= rec_reg.def_pagamentos_id; 
             v_rh_pagamentos.percentagem := v_perc_imposto; 

        INSERT INTO rh_t_pagamentos VALUES v_rh_pagamentos RETURNING ID INTO v_id;

            g_total_pagamentos := g_total_pagamentos + v_imposto;

            IF rec_reg.tipo_pagamento = 'SALL' THEN
              g_liquido_pagamento_id := v_id;
            END IF;

    END LOOP; -- FOR rec_reg

  RETURN 0;

--EXCEPTION
--  WHEN OTHERS THEN
  --  RETURN 0;
END;

--------------------------------------------------------------------------------
PROCEDURE CalcularDesAtual ( 
                             ----SUBSISDIO-------
                             p_tm_id_subsidio    OWA.vc_arr DEFAULT tab,
                             p_valor_subsidio    OWA.vc_arr DEFAULT tab,
                             ----DESCONTO------
                             p_tm_id_desconto    OWA.vc_arr DEFAULT tab,
                             p_valor_desconto   OWA.vc_arr DEFAULT tab,
                             p_tipo_remuneracao OWA.vc_arr DEFAULT tab,
                             p_valor_base NUMBER,
                             P_moeda    VARCHAR2,
                             p_data_de DATE,
                             p_total_remun OUT NUMBER,
                             P_total_pagamentos OUT NUMBER
                           ) 
IS
 v_sal_acum   NUMBER;
 v_imposto_iur NUMBER;
 v_imposto_inps NUMBER;
 v_valor_base NUMBER;
 v_rh_tipo_movimentos rh_tipo_movimentos%ROWTYPE;   -- coleção dos movimentos
 v_ord_base NUMBER:=0;
 v_cambio_para           VARCHAR2 (15) := 'CVE';
 v_total_remun_colect NUMBER :=0;
 v_total_remun_social NUMBER :=0;
 v_tm_id_subsidio  NUMBER;
 v_tm_id_desconto NUMBER;
 v_cambio NUMBER;
 v_valor NUMBER;
 v_a_cobrar  NUMBER :=0;
 v_sal_inc NUMBER :=0;
 v_imposto NUMBER;
 v_ano_mes VARCHAR2 (6);
 v_perc_imposto number;
 BEGIN
  p_total_remun :=0;
  P_total_pagamentos :=0;
 v_ano_mes :=  TO_CHAR (p_data_de, 'yyyymm');


   IF p_tm_id_subsidio.COUNT != p_valor_subsidio.COUNT THEN
    RAISE_APPLICATION_ERROR(-20001, 'tm_id e valor com tamanhos diferentes');
  END IF;


  v_tm_id_subsidio := GREATEST(
                          p_tm_id_subsidio.COUNT,
                          p_valor_subsidio.COUNT,
                         0);


  v_ord_base := p_valor_base;
   IF P_moeda <> 'CVE'
            THEN
                v_cambio :=inpssigof.orc_financ_projectos.da_cambio_dia ( P_moeda,v_cambio_para,TO_CHAR (p_data_de, 'dd-mm-yyyy'));

                IF v_cambio IS NOT NULL
                THEN
                    v_ord_base := p_valor_base * v_cambio;
                ELSE
                    v_ord_base := p_valor_base;
                END IF;
       END IF;

  -- 1 Somar valor base 

 IF v_ord_base > 0 THEN

  FOR I IN 1..v_tm_id_subsidio LOOP

    IF p_tm_id_subsidio.COUNT >= i AND p_tm_id_subsidio(i) IS NOT NULL THEN

        SELECT a.* INTO v_rh_tipo_movimentos
        FROM rh_tipo_movimentos a
        WHERE a.id = p_tm_id_subsidio(i);



        IF v_rh_tipo_movimentos.percentagem IS NOT NULL THEN
          v_valor := v_ord_base * (v_rh_tipo_movimentos.percentagem / 100);

        ELSIF v_rh_tipo_movimentos.valor IS NOT NULL
           AND v_rh_tipo_movimentos.valor <> '0' THEN
           v_valor := v_rh_tipo_movimentos.valor;

        ELSE
           v_valor := p_valor_subsidio(i);
        END IF;

       IF p_valor_subsidio(i) IS NULL THEN
         v_valor := 0;

       END IF;

         v_valor := ROUND (v_valor);

         IF v_rh_tipo_movimentos.cobre_imp = 'SIM' THEN
             v_total_remun_colect := v_total_remun_colect + v_valor;
        END IF;

        IF v_rh_tipo_movimentos.social = 'SIM' THEN
           v_total_remun_social := v_total_remun_social + v_valor;
        END IF;



          p_total_remun := p_total_remun + v_valor;



   END IF; 
   v_total_remun_colect := v_total_remun_colect + v_ord_base;
   v_total_remun_social := v_total_remun_social + v_ord_base;
    p_total_remun := p_total_remun + v_ord_base;

  END LOOP;

       v_tm_id_desconto := GREATEST(
                          p_tm_id_desconto.COUNT,
                          p_valor_desconto.COUNT,
                         0);


      FOR I IN 1..v_tm_id_desconto LOOP

         IF p_tm_id_desconto.COUNT >= i AND p_tm_id_desconto(i) IS NOT NULL THEN

            SELECT a.*  INTO v_rh_tipo_movimentos
            FROM rh_tipo_movimentos a
            WHERE a.id = p_tm_id_desconto(i);

             IF UPPER (v_rh_tipo_movimentos.tipo) = 'IMP'
            THEN

                IF v_rh_tipo_movimentos.short_desc = 'IUR'
                THEN

                     v_imposto := inpssigof.iur.calcula_iur(v_total_remun_colect, null);
                    -- dbms_output.put_line('iur: '||v_imposto);

                  ELSE



                     IF v_rh_tipo_movimentos.short_desc = 'INPS' THEN
                          v_perc_imposto := g_percentagem_inps;
                         v_imposto := v_total_remun_social * (v_perc_imposto / 100);
                    END IF;


                  END IF;
           ELSE
                 IF v_rh_tipo_movimentos.short_desc = 'SALL'
                THEN
                    v_imposto := 0;
                ELSIF v_rh_tipo_movimentos.percentagem IS NOT NULL
                THEN
                    v_imposto := (v_ord_base * v_rh_tipo_movimentos.percentagem) / 100;
                ELSE                                    --   porque   valor...
                    v_imposto :=  p_valor_desconto(i);
                END IF;
        END IF;

                IF UPPER (v_rh_tipo_movimentos.calculo) = 'BASE'
                THEN
                    v_imposto := (v_ord_base * v_rh_tipo_movimentos.percentagem) / 100;
                END IF;

                v_imposto := ROUND (v_imposto);


        END IF;


        P_total_pagamentos := P_total_pagamentos + v_imposto;
      END LOOP;
  END IF;
END;

-----------------------------------------------------------------------------------
    PROCEDURE CALCULAR_SUBSTITUICAO ( P_NR_DIAS IN NUMBER,
                                     P_VALOR_TIPREL_DE IN NUMBER, 
                                     P_VALOR_TIPREL_PARA IN NUMBER,
                                     P_VALOR_RECEBER OUT NUMBER
                                     )
    IS
    V_DIFERENCA_MES NUMBER := 0;
    V_DIFERENCA_DIARIA  NUMBER := 0;
    BEGIN

       IF P_VALOR_TIPREL_PARA > P_VALOR_TIPREL_DE THEN
            V_DIFERENCA_MES := P_VALOR_TIPREL_PARA - P_VALOR_TIPREL_DE;
            V_DIFERENCA_DIARIA := V_DIFERENCA_MES / 30;
            P_VALOR_RECEBER := V_DIFERENCA_DIARIA * P_NR_DIAS;
       END IF;
    END;

-----------------------------------------------------------------------------------------
 PROCEDURE ELIMINAR_PROCESSAMENTO (P_PROC_SAL_ID OWA.vc_arr,
                                      P_MSG OUT VARCHAR2)

    IS
    V_ESTADO VARCHAR2(100);
    BEGIN

    FOR i in 1..p_proc_sal_id.COUNT 
    LOOP
    SELECT ESTADO
    INTO V_ESTADO
    FROM rh_t_proc_salarios WHERE ID = p_proc_sal_id(i);
    
    IF V_ESTADO IN ('PROCESSADO','ERRO_PROCESSO') THEN 
     DELETE_FERIAS_NATAL (p_proc_sal_id(i));
     PROCESSAR_EMPRESTIMO ( P_ACCAO  =>'DELETE', 
                            P_TIPREL_ID => NULL, 
                            P_PROC_SAL_ID => p_proc_sal_id(i),
                            P_PROC_FUN_ID => NULL,
                            P_USER_ID => NULL,
                            P_USER_NAME => NULL);

      DELETE FROM   rh_t_remuneracoes
            WHERE   prsal_id IN (SELECT   id
                                   FROM   rh_t_proc_funcionarios
                                  WHERE   prsals_id = p_proc_sal_id(i));

      -- ACRESCENTADO AOS 12-06-2007 PARA RESOLVER O PROBLEMA DE DUPLICACAO DE FALTAS -- ARMANDINA
      DELETE FROM   rh_t_def_remuneracoes a
            WHERE   a.id IN
                          (SELECT   c.id
                             FROM   rh_t_proc_funcionarios b, rh_t_def_remuneracoes c, rh_t_tiprel_rem_pag E
                            WHERE   b.tiprel_id = E.tiprel_id  AND C.ID = E.REM_ID
                                  AND c.tm_id IN (1706, 1705)
                                    -- Faltas Pessoal Contratado, Faltas Pessoal Do Quadro
                                    AND TO_CHAR (b.data_processamento, 'yyyymm') =
                                          TO_CHAR (c.DATA_FIM, 'yyyymm')
                                    AND b.prsals_id = p_proc_sal_id(i));

      DELETE FROM   rh_t_pagamentos
            WHERE   prsal_id IN (SELECT   id
                                   FROM   rh_t_proc_funcionarios
                                  WHERE   prsals_id = p_proc_sal_id(i));

      DELETE FROM   rh_t_proc_funcionarios
            WHERE   prsals_id = p_proc_sal_id(i);

      DELETE FROM   rh_t_proc_salarios
            WHERE   id = p_proc_sal_id(i);
     ELSE
       P_MSG := 'Erro: Não se pode eliminar processamento em estado diferente de "Processado" -'||p_proc_sal_id(i);
     END IF;
            
     END LOOP;
     
    END;
-------------------------------------------------------------------------------------------
 PROCEDURE VALIDAR (p_proc_sal_id OWA.vc_arr,
                        P_MSG OUT VARCHAR2)

    IS
    BEGIN
        FOR i in 1..p_proc_sal_id.COUNT LOOP
           UPDATE RH_T_PROC_SALARIOS
              SET ESTADO = 'VALIDADO'
            WHERE ID = p_proc_sal_id(i);
        END LOOP;
    END; 
------------------------------------------------------------------------------------------
PROCEDURE CABIMENTAR (P_PROC_SAL_ID OWA.vc_arr,
                      P_MSG OUT VARCHAR2,
                      p_ano_orcamento number,
                      p_qnt  owa.vc_arr
                     )

IS
   v_data_pag varchar2(20);
   v_id_cab      number;
   v_ano_orc     number := p_ano_orcamento;
BEGIN
    FOR i in 1..p_proc_sal_id.COUNT LOOP

         SELECT to_char(last_day(data_de) -2,'dd-mm-yyyy'), cab_1_id
         INTO v_data_pag, v_id_cab
         FROM rh_t_proc_salarios WHERE id = p_proc_sal_id(i);

         --se ano de orcamento for diferente do ano de processamento
            IF v_ano_orc <> to_number(to_char(to_date(v_data_pag,'dd-mm-yyyy'),'yyyy')) then
               raise_application_error(-20001, 'O ano de or amento n o corresponde ao ano de processamento. Favor verificar.');
            END IF;

            IF v_id_cab IS NULL AND p_qnt(i) > 0 THEN
             null;

               /*inpsrh.grh_processamento.gesta_porcessamento_def(
                   p_proc_sal_id    => p_proc_sal_id(i),
                   p_data_pag       => v_data_pag,
                   sessid           => null
                );*/
           END IF;

            v_id_cab := NULL;
        END LOOP;

  EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('Erro ao processar colaborador: ' || SQLERRM);
  END CABIMENTAR;

-------------------------------------------------------------------------------------------
PROCEDURE ELIMINAR_CABIMENTO(P_CAB_ID NUMBER,
                             P_MSG OUT VARCHAR2)
IS
BEGIN
   ---- Pendente disponibilizacao de serviço para eliminacao de cabimento
   NULL;
END;
-------------------------------------------------------------------------------------------
PROCEDURE AUTORIZAR (p_qnt owa.vc_arr, 
                     p_cabimento_id owa.vc_arr)

IS
v_autorizado NUMBER;
BEGIN
      FOR i IN 1..p_cabimento_id.COUNT LOOP


           /* SELECT COUNT(1) INTO v_autorizado
            FROM   inpssigof.compromissos a,
                   inpssigof.pagamentos b, 
                   inpssigof.estados_pagamento c
            WHERE  cab_id = p_cabimento_id (i)
            AND b.compr_id = a.id
            AND b.id = c.pag_id AND c.estado= 'AUT';*/

            IF v_autorizado = 0 AND p_qnt(i) > 0 then
                 inpssigof.glo_egov.etapa_autoriza_x_trata(
                               p_codigo       => p_cabimento_id(i),
                               p_visa         => 'SIM',
                               p_obs          => 'Autoriza  o em lote',
                               sessid         => NULL,
                               p_transaccao   => 'AUTET0001',
                               p_utilizador   => NULL
                            );
            END IF;
        END LOOP;
  EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('Erro ao processar colaborador: ' || SQLERRM);
  END;
-------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------
PROCEDURE REGISTA_FERIAS_NATAL (P_proc_sal_id NUMBER,P_CC_ID NUMBER default null,p_tiprel_id NUMBER default null,P_TIPO VARCHAR2, P_DE DATE, P_ATE DATE,P_user_name VARCHAR2,p_user_id NUMBER, P_MSG_ERROR OUT VARCHAR2)
IS
    v_salario_bruto number := 0;
    v_salario_total number := 0;
    v_meses_total number := 0;
    v_perc_salario number := 0;
    v_perc_salario_falta number := 0;
    v_salario_desconto_mes number := 0;
    V_RH_DEF_REMUNERACOES RH_T_DEF_REMUNERACOES%ROWTYPE;
    v_rh_t_tiprel_rem_pag rh_t_tiprel_rem_pag%ROWTYPE; 
    v_id_movimento NUMBER;
    v_rem_id NUMBER;
BEGIN
  /* FOR REC IN (SELECT B.* FROM RH_T_TIPOS_RELACIONAMENTO B, rh_t_carreira c
             WHERE B.EST_ACT_ADM = 1 
             AND B.CARREIRA_ID = C.ID
             AND C.CARR_PCCS_ID IS NOT NULL )
  LOOP*/
     V_RH_DEF_REMUNERACOES:= null;

    V_RH_DEF_REMUNERACOES.DATA_INICIO := TO_DATE (P_DE, 'dd-mm-yyyy');
    V_RH_DEF_REMUNERACOES.DATA_FIM := TO_DATE (P_ATE, 'dd-mm-yyyy');
    V_RH_DEF_REMUNERACOES.ESTADO := 'A';
    V_RH_DEF_REMUNERACOES.DATA_ESTADO := SYSDATE;
    V_RH_DEF_REMUNERACOES.MOEDA := 'CVE';
    V_RH_DEF_REMUNERACOES.USER_REGISTO_ID := p_user_id;             
    V_RH_DEF_REMUNERACOES.USER_REGISTO_NAME := P_user_name;
    


  IF P_TIPO = 'SUBFER' THEN
    FOR rec IN (SELECT * FROM rh_t_subsidio_ferias WHERE ESTADO = 'A')
     LOOP
     FOR REC2 IN (SELECT F.VINCULO_ID, B.* FROM RH_T_TIPOS_RELACIONAMENTO B, RH_T_MOBILIDADE c, INPSSIGOF.INSTITUICOES D, INPSSIGOF.CENTROS_CUSTO E, 
                  rh_t_contrato_vinculo F
             WHERE B.EST_ACT_ADM = 1 
             AND B.MOB_ID = C.ID
             AND C.INSTIT_ID = D.ID
             AND D.ID =E.INSTIT_ID
             AND ( P_CC_ID IS NULL OR E.ID = P_CC_ID)
             AND ( p_tiprel_id IS NULL OR B.ID = p_tiprel_id)
             AND B.FUN_ID = REC.FUN_ID
             AND F.ID = B.CONTR_VINCULO_ID 
             )
    LOOP
    
    SELECT TM_ID INTO v_id_movimento  FROM rh_t_param_vinculo_mov WHERE TIPO = 'FERIAS' AND VINCULO_ID = REC2.VINCULO_ID;
    
 
            V_RH_DEF_REMUNERACOES.VALOR := round(REC.VALOR_SUBSIDIO);
            V_RH_DEF_REMUNERACOES.TM_ID := v_id_movimento;
            V_RH_DEF_REMUNERACOES.UUID := GEN_UUID();
            V_RH_DEF_REMUNERACOES.TIPO := P_TIPO;
            V_RH_DEF_REMUNERACOES.OBS := 'Criado automatico, atraves s da gestão processamento de subsidios';
    
           INSERT INTO RH_T_DEF_REMUNERACOES  VALUES  V_RH_DEF_REMUNERACOES RETURNING ID INTO v_rem_id;
    
           v_rh_t_tiprel_rem_pag.rem_id := v_rem_id;
           v_rh_t_tiprel_rem_pag.TIPREL_ID := rec2.id;
           INSERT INTO rh_t_tiprel_rem_pag  VALUES  v_rh_t_tiprel_rem_pag;
           
       END LOOP;
               UPDATE rh_t_subsidio_NATAL SET ESTADO = 'PROCESADO' , PROC_SAL_ID = P_proc_sal_id WHERE ID = REC.ID;

    END LOOP;

  ELSIF P_TIPO = 'SUBNAT' THEN
   FOR rec IN (SELECT * FROM rh_t_subsidio_NATAL WHERE ESTADO = 'A')
    LOOP
      FOR REC2 IN (SELECT F.VINCULO_ID, B.* FROM RH_T_TIPOS_RELACIONAMENTO B, RH_T_MOBILIDADE c, INPSSIGOF.INSTITUICOES D, INPSSIGOF.CENTROS_CUSTO E, 
                  rh_t_contrato_vinculo F
             WHERE B.EST_ACT_ADM = 1 
             AND B.MOB_ID = C.ID
             AND C.INSTIT_ID = D.ID
             AND D.ID =E.INSTIT_ID
             AND ( P_CC_ID IS NULL OR E.ID = P_CC_ID)
             AND ( p_tiprel_id IS NULL OR B.ID = p_tiprel_id)
             AND B.FUN_ID = REC.FUN_ID
             AND F.ID = B.CONTR_VINCULO_ID 
             )
    LOOP
    
    SELECT TM_ID INTO v_id_movimento  FROM rh_t_param_vinculo_mov WHERE TIPO = 'SBNT' AND VINCULO_ID = REC2.VINCULO_ID;


        V_RH_DEF_REMUNERACOES.VALOR := round(REC.VALOR_SUBSIDIO);
        V_RH_DEF_REMUNERACOES.TM_ID := v_id_movimento;
        V_RH_DEF_REMUNERACOES.UUID := GEN_UUID();
        V_RH_DEF_REMUNERACOES.OBS := 'Criado automatico, atraves s da gestão processamento de subsidios';

       INSERT INTO RH_T_DEF_REMUNERACOES  VALUES  V_RH_DEF_REMUNERACOES RETURNING ID INTO v_rem_id;

       v_rh_t_tiprel_rem_pag.rem_id := v_rem_id;
       v_rh_t_tiprel_rem_pag.TIPREL_ID := rec2.id;

       INSERT INTO rh_t_tiprel_rem_pag  VALUES  v_rh_t_tiprel_rem_pag;

        -- cheque brinde
     --IF v_meses_total = 12 THEN
     IF REC.CHEQUE_BRINDE > 0 THEN 
          SELECT TM_ID INTO v_id_movimento  FROM rh_t_param_vinculo_mov WHERE TIPO = 'CHBR' AND VINCULO_ID = REC2.VINCULO_ID;
    
            V_RH_DEF_REMUNERACOES.VALOR := round(REC.CHEQUE_BRINDE);
            V_RH_DEF_REMUNERACOES.TM_ID := v_id_movimento;
            V_RH_DEF_REMUNERACOES.UUID := GEN_UUID();
            V_RH_DEF_REMUNERACOES.OBS := 'Criado automatico, atraves s da gestão processamento de subsidios';
            
            INSERT INTO RH_T_DEF_REMUNERACOES  VALUES  V_RH_DEF_REMUNERACOES RETURNING ID INTO v_rem_id;
    
             v_rh_t_tiprel_rem_pag.rem_id := v_rem_id;
             v_rh_t_tiprel_rem_pag.TIPREL_ID := rec2.id;
    
           INSERT INTO rh_t_tiprel_rem_pag  VALUES  v_rh_t_tiprel_rem_pag;
      
       END IF;
       
       IF REC.PRENDA_NATAL > 0 THEN 
       
          SELECT TM_ID INTO v_id_movimento  FROM rh_t_param_vinculo_mov WHERE TIPO = 'PNPF' AND VINCULO_ID = REC2.VINCULO_ID;
    
            V_RH_DEF_REMUNERACOES.VALOR := round(REC.PRENDA_NATAL);
            V_RH_DEF_REMUNERACOES.TM_ID := v_id_movimento;
            V_RH_DEF_REMUNERACOES.UUID := GEN_UUID();
            V_RH_DEF_REMUNERACOES.OBS := 'Criado automatico, atraves da gestão processamento de subsidios';
            
            INSERT INTO RH_T_DEF_REMUNERACOES  VALUES  V_RH_DEF_REMUNERACOES RETURNING ID INTO v_rem_id;
    
             v_rh_t_tiprel_rem_pag.rem_id := v_rem_id;
             v_rh_t_tiprel_rem_pag.TIPREL_ID := rec2.id;
    
           INSERT INTO rh_t_tiprel_rem_pag  VALUES  v_rh_t_tiprel_rem_pag;
      
       END IF;
       
       END LOOP;
       
        UPDATE rh_t_subsidio_NATAL SET ESTADO = 'PROCESADO' , PROC_SAL_ID = P_proc_sal_id WHERE ID = REC.ID;
    END LOOP;
  END IF;
  EXCEPTION
   WHEN OTHERS THEN
      ROLLBACK;

      P_MSG_ERROR := 'Erro: ' || SQLERRM ||
                ' | Código: ' || SQLCODE ||
                ' | Linha: ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE;

      RAISE_APPLICATION_ERROR(-20001, P_MSG_ERROR);
  
END;

--------------------------------------------------------------------------------------------------------------------------------
 PROCEDURE DELETE_FERIAS_NATAL (P_PROC_SAL_ID NUMBER)
 IS
 V_COUNT NUMBER:= 0;
 V_TIPO VARCHAR2(10);
 BEGIN
 
 SELECT COUNT(*) INTO V_COUNT FROM rh_t_subsidio_ferias WHERE PROC_SAL_ID = P_PROC_SAL_ID;
 
 IF V_COUNT > 0  THEN 
 
     V_TIPO := 'SUBFER';
     
     FOR REC IN (SELECT C.ID FROM rh_t_proc_funcionarios A, rh_t_remuneracoes B , rh_t_def_remuneracoes C
     WHERE A.ID = B.PRSAL_ID AND A.PRSALS_ID = P_PROC_SAL_ID  AND B.REM_1_ID = C.ID AND C.TIPO = V_TIPO)
    LOOP
         --NULL; -- DINORA COLOCAR AQUI O DELETE 
         DELETE rh_t_tiprel_rem_pag WHERE REM_ID = REC.ID;
         DELETE rh_t_def_remuneracoes WHERE ID = REC.ID;
    END LOOP;
    
    UPDATE rh_t_subsidio_ferias SET ESTADO = 'A' , PROC_SAL_ID = NULL WHERE PROC_SAL_ID = P_PROC_SAL_ID;
 END IF;
 
    
    
     SELECT COUNT(*) INTO V_COUNT FROM rh_t_subsidio_NATAL WHERE PROC_SAL_ID = P_PROC_SAL_ID;
     
     IF V_COUNT > 0  THEN 
       V_TIPO := 'SUBNAT';
       
      FOR REC IN (SELECT B.ID FROM rh_t_proc_funcionarios A, rh_t_remuneracoes B , rh_t_def_remuneracoes C
      WHERE A.ID = B.PRSAL_ID AND A.PRSALS_ID = P_PROC_SAL_ID  AND B.REM_1_ID = C.ID AND C.TIPO = V_TIPO)
       LOOP
            --NULL;-- DINORA COLOCAR AQUI O DELETE 
         DELETE rh_t_tiprel_rem_pag WHERE REM_ID = REC.ID;
         DELETE rh_t_def_remuneracoes WHERE ID = REC.ID;
         
       END LOOP;
        
         UPDATE rh_t_subsidio_NATAL SET ESTADO = 'A' , PROC_SAL_ID = NULL WHERE PROC_SAL_ID = P_PROC_SAL_ID;
    END IF;
    
   
 END;
--------------------------------------------------------------------------------------------------------------------------------
 FUNCTION detalha_remuneracoes_func (p_proc_funcionario_id NUMBER)
        RETURN NUMBER
    IS
        v_id                 NUMBER;
        v_valor              NUMBER;
        v_ord_base           NUMBER;
        v_valor_referencia   NUMBER;
        v_processa           BOOLEAN;
        v_total_tributavel   NUMBER;
        v_chave_sall         NUMBER;
    BEGIN
        -- vou consultar as remuneracoes a pagar e distribuilas pelos Pagamentos
        FOR rec_reg IN (SELECT /*+ RULE*/
                              a.remuneracao_tributavel,
                               a.total_seg_social,
                               a.short_desc,
                               a.det_tipo,
                               a.tributavel,
                               a.social,
                               a.proc_sal_id,
                               a.valor,
                               a.proc_func_id,
                               a.registo_id
                          FROM RH_V_proc_sal_cc_remun a
                         WHERE a.proc_func_id = p_proc_funcionario_id)
        LOOP
            -- agora vou ler os Pagamentos de impostos efectuados para determinar a percentagem a abater
            v_total_tributavel := 0;

            FOR rec_reg2
                IN (SELECT /*+ RULE*/
                          a.remuneracao_tributavel,
                           a.total_seg_social,
                           a.short_desc,
                           a.det_tipo,
                           a.tributavel,
                           a.social,
                           a.proc_sal_id,
                           a.valor,
                           a.proc_func_id,
                           a.registo_id,
                           a.favor_estado
                      FROM RH_V_proc_sal_cc_pag a
                     WHERE     a.proc_func_id = p_proc_funcionario_id
                           AND UPPER (a.det_tipo) IN ('IMP', 'PAG'))
                LOOP

                        IF rec_reg2.favor_estado = 'SIM'
                        THEN
                            v_total_tributavel := v_total_tributavel + v_valor;
                        END IF;

               END LOOP;

            UPDATE rh_T_remuneracoes
               SET valor_real = valor - v_total_tributavel
             WHERE id = rec_reg.registo_id;
        END LOOP;


        RETURN 0;

    END;
  ----------------------------------------------------------------------------  
  FUNCTION CALCULO_HORA_EXTRA (P_TIPREL_ID NUMBER,P_DATA_INICIO DATE,P_DATA_FIM DATE,P_DIAS_APLICADA VARCHAR2, P_HORAS_DIARIA NUMBER)
   RETURN NUMBER
   IS

     V_LOCAL           NUMBER;
     V_HE_VALOR_DUTIL  NUMBER;
     V_HE_VALOR_DNUTIL NUMBER;
     v_geo             SIPSGLOBAL.GLB_T_GEOGRAFIA%ROWTYPE;
     V_COUNT           NUMBER;
     v_data            DATE := P_DATA_INICIO;
     V_SALARIO         NUMBER;
     V_HORAS_DIARIAS   NUMBER;
     V_SAL_DIARIO      NUMBER;
     V_VALOR_HORAS     NUMBER;
     v_valor_mes       NUMBER := 0;
     v_valor_total     NUMBER := 0;
     v_mes_atual       VARCHAR2(10);
     V_DIAS_UTES       VARCHAR2(1);
 BEGIN

     v_mes_atual := TO_CHAR(v_data, 'YYYYMM');

     -- FIX 1+2: buscar parametros ANTES de calcular V_SAL_DIARIO;
     --           converter DIARIA 'HH:MM' (VARCHAR2) para horas (NUMBER)
     BEGIN
         SELECT HE_VALOR_DUTIL,
                HE_VALOR_DNUTIL,
                TO_NUMBER(SUBSTR(DIARIA, 1, 2)) + TO_NUMBER(NVL(SUBSTR(DIARIA, 4, 2), '0')) / 60
         INTO   V_HE_VALOR_DUTIL, V_HE_VALOR_DNUTIL, V_HORAS_DIARIAS
         FROM   RH_T_ASSIDUIDADE_PARAMETRO
         WHERE  ESTADO = 'A'
         AND    ROWNUM = 1;
     EXCEPTION WHEN OTHERS THEN
         V_HORAS_DIARIAS   := 8;
         V_HE_VALOR_DUTIL  := 50;
         V_HE_VALOR_DNUTIL := 75;
     END;

     V_SALARIO    := GET_SALARIO_BASE (P_TIPREL_ID, P_DATA_INICIO);
     V_SAL_DIARIO := V_SALARIO / NULLIF(V_HORAS_DIARIAS, 0);

     BEGIN
         SELECT E.*
          INTO  v_geo
          FROM  rh_t_tipos_relacionamento A,
                RH_T_MOBILIDADE B,
                rh_t_param_local_trab C,
                SIPSGLOBAL.GLB_T_UPS D,
                SIPSGLOBAL.GLB_T_GEOGRAFIA E
          WHERE A.MOB_ID = B.ID
          AND   B.LOCAL_TRAB_ID = C.ID
          AND   C.UPS_ID = D.ID
          AND   D.ID_GEOGRAFIA = E.ID
          AND   A.ID = P_TIPREL_ID;
     EXCEPTION WHEN OTHERS THEN
         NULL;
     END;

  WHILE v_data <= P_DATA_FIM
   LOOP
     -- FIX 5: resetar V_VALOR_HORAS e V_COUNT em cada iteracao
     V_VALOR_HORAS := 0;
     V_COUNT       := 0;

     -- FIX 6+7+8: usar v_data (nao SYSDATE), ano de v_data, exception handler
     BEGIN
          SELECT COUNT(1)
          INTO V_COUNT
          FROM RH_T_PARAM_FERIADO A
          WHERE ((FIXO_ANO = 'NAO' AND DATA_ESPECIFICA = TRUNC(v_data))
             OR  (FIXO_ANO = 'SIM' AND DIA = TO_CHAR(v_data,'DD') AND MES = TO_CHAR(v_data,'MM')))
          AND (A.ANO_REFERENTE = 'TODOS' OR A.ANO_REFERENTE = TO_CHAR(v_data,'YYYY'))
          AND (A.GEOGR_ID IS NULL OR A.GEOGR_ID = v_geo.CONCELHO);
     EXCEPTION WHEN OTHERS THEN V_COUNT := 0;
     END;

     V_DIAS_UTES := IS_DIA_UTEL(v_data, NULL);

     -- FIX 9: dividir HE_VALOR_DUTIL/DNUTIL por 100 (sao percentagens: 50 = 50%)
     IF  P_DIAS_APLICADA = 'DIAS_NAO_UTEIS' THEN
       IF V_DIAS_UTES = 'N' OR V_COUNT > 0 THEN
         V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DNUTIL / 100) * NVL(P_HORAS_DIARIA, 0));
       END IF;
     ELSIF P_DIAS_APLICADA = 'DIAS_UTEIS_NAO_UTEIS' THEN
       IF V_DIAS_UTES = 'N' OR V_COUNT > 0 THEN
         V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DNUTIL / 100) * NVL(P_HORAS_DIARIA, 0));
       ELSE
         V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DUTIL / 100) * NVL(P_HORAS_DIARIA, 0));
       END IF;
     ELSIF P_DIAS_APLICADA = 'DIAS_UTEIS' THEN
       IF V_DIAS_UTES = 'S' AND V_COUNT = 0 THEN
         V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DUTIL / 100) * NVL(P_HORAS_DIARIA, 0));
       END IF;
     END IF;

         IF TO_CHAR(v_data,'YYYYMM') <> v_mes_atual THEN
             v_mes_atual := TO_CHAR(v_data,'YYYYMM');
             v_valor_mes := 0;
          END IF;

          v_valor_mes   := v_valor_mes   + V_VALOR_HORAS;
          v_valor_total := v_valor_total + V_VALOR_HORAS;

     v_data := v_data + 1;
   END LOOP;

    -- FIX 10: RETURN em falta
    RETURN NVL(v_valor_total, 0);

    END CALCULO_HORA_EXTRA;

---  
   FUNCTION CALCULO_FALTA_DIARIO (P_TIPREL_ID  NUMBER, p_data_inicio DATE)
   RETURN NUMBER
   IS
   v_salario NUMBER;
   v_ausencia  NUMBER;
   v_sal_mes NUMBER;
   v_sal_hra NUMBER;
   v_jorn_diaria NUMBER;
  
    BEGIN
      NULL;
      
       BEGIN
        SELECT  DIARIA
        INTO  v_jorn_diaria
        FROM RH_T_ASSIDUIDADE_PARAMETRO
        WHERE ESTADO = 'A';
    EXCEPTION WHEN OTHERS THEN 
      NULL;
    END;
      
      --  v_ausencia := TRUNC(GREATEST(p_data_inicio, p_data_fim)) - TRUNC(LEAST(p_data_inicio, p_data_fim)) + 1;
        v_salario:= GET_SALARIO_BASE (P_TIPREL_ID, P_DATA_INICIO);
        v_sal_mes := v_salario / v_divisor_falta;
        v_sal_hra := v_sal_mes / v_jorn_diaria;
        
   RETURN v_sal_hra;
       
    /*



        ----------------Calculo jornada salario mensal e salario por hr


        -----Faltas justificadas por BAIXA MEDICA que devem ser procerssadas -- cj 17/12/2020
        v_faltas_baixa_med := da_assiduidade_mensal (inpsrh.RH_CONST_ASSIDUIDADE.c_code_falta_baixa_medica, p_tiprel_id, p_data_de, p_proc_salarios_id);

        v_faltas_baixa_med := RH_hr_falta_mes(  P_hr_falta_mes => nvl(v_faltas_baixa_med,0), p_data_de => p_data_de);


        --v_valor_falta_baixa_med := ((v_salario * 12 / v_divisor_hora) * nvl(v_faltas_baixa_med,0));
        v_valor_falta_baixa_med :=  ((v_salario * nvl(v_faltas_baixa_med,0)/  hr_mes) );

         -----Faltas justificadas que devem ser procerssadas -- NV 17/05/2021
        v_faltas_iso_prof := da_assiduidade_mensal (inpsrh.RH_CONST_ASSIDUIDADE.c_code_falta_Iso_prof, p_tiprel_id, p_data_de, p_proc_salarios_id);

        v_faltas_iso_prof := RH_hr_falta_mes(  P_hr_falta_mes => nvl(v_faltas_iso_prof,0), p_data_de => p_data_de);

        --v_valor_faltas_iso_prof := ((v_salario * 12 / v_divisor_hora) * nvl(v_faltas_iso_prof,0));
        v_valor_faltas_iso_prof := ((v_salario * nvl(v_faltas_iso_prof,0)/ hr_mes) );


        ---Maternidade
        v_faltas_mat := da_assiduidade_mensal (inpsrh.RH_CONST_ASSIDUIDADE.c_code_falta_just_c_desc_Maternidade, p_tiprel_id, p_data_de, p_proc_salarios_id);

        v_faltas_mat := RH_hr_falta_mes(  P_hr_falta_mes => nvl(v_faltas_mat,0), p_data_de => p_data_de);

        v_valor_faltas_mat := ((v_salario * nvl(v_faltas_mat,0)/ hr_mes) );

        v_valor_faltas := (-1) * ((v_salario * nvl(v_faltas,0)/ hr_mes) 
                                + (sal_hra * NVL (v_ausencia, 0)) 

        ----CJ acrescentado 17/12/2020
        + v_valor_falta_baixa_med--((v_salario * 12 / v_divisor_hora) * nvl(v_faltas_baixa_med,0))
        + v_valor_faltas_iso_prof
        + v_valor_faltas_mat
        ------
        ) ;*/
    END;
---------------------------------------------------------------------------------------------------------------------------    
    
PROCEDURE CALCULO_FALTA_LICENCA(P_TIPREL_ID  NUMBER,P_DATA_INICIO DATE , P_DATA_FIM DATE, P_TIPO_LICENCA NUMBER,
                  p_desc_sobre OUT varchar2, 
                  p_dias_Direito OUT number,
                  p_dias_desc_rh OUT number,
                  p_dias_ndesc_rh OUT number,
                  p_meses OUT OWA.vc_arr,
                  p_dias_falta OUT OWA.vc_arr,
                  p_valor_desc OUT OWA.vc_arr,
                  p_valor_salario OUT OWA.vc_arr,
                  p_data_ini_falta OUT OWA.vc_arr,
                  p_data_fim_falta  OUT OWA.vc_arr,
                  p_msg_error  OUT varchar2
                  )
IS
    V_COUNT_DIA NUMBER := 0;
    V_COUNT_FALTA NUMBER := 0;
    V_FERIADO NUMBER;
    V_COUNT_FERIADO NUMBER;
    DIAS_CORRIDO VARCHAR2(20):= 'DIAS_CORRIDO';
    DIAS_UTEIS VARCHAR2(20):='DIAS_UTEIS';
    V_TIPO_CONTAGEM_DIAS VARCHAR2(20);  
    --V_NUM_DIAS_ABONOS NUMBER:=0;        
    V_COUNT_NUTEIS NUMBER := 0;
    V_SALARIO_BASE NUMBER;
    v_DIAS_FALTAS NUMBER;
    V_LOCAL_ID NUMBER;
    V_NUM_DIAS_ABONOS NUMBER;
    V_NUM_DIAS_DESCONTO_RH NUMBER;
    V_NUM_DIAS_NDESCONTO_RH NUMBER;
    v_data_inicio DATE;
    v_data_fim    DATE;
    v_mes_inicio  DATE;
    v_mes_fim     DATE;
    v_dias        NUMBER;
    v_idx         NUMBER := 1;
    V_SALARIO_DIARIO  NUMBER := 0;
    v_ini_periodo DATE;
    v_fim_periodo DATE;
BEGIN
-- VERIFICO OS DIAS CORRIDOS E OS DIAS FERIADOS NESSA DATA 
   IF p_data_inicio IS NULL OR p_data_fim IS NULL THEN
       p_msg_error:= 'Data início e data fim são obrigatórias.';
       return;
    END IF;
    
   IF p_data_inicio > p_data_fim THEN
        p_msg_error:= 'Data início não pode ser maior que data fim.';
        return;
    END IF;
    

   --3 Contar numero de dias não uteis 

      BEGIN
        SELECT  TIPO_CONTAGEM_DIAS, NVL(NUM_DIAS_ABONOS,0),NVL( NUM_DIAS_DESCONTO_RH,0), NVL(NUM_DIAS_NDESCONTO_RH,0)
        INTO V_TIPO_CONTAGEM_DIAS, V_NUM_DIAS_ABONOS,V_NUM_DIAS_DESCONTO_RH,V_NUM_DIAS_NDESCONTO_RH
        FROM  RH_T_PARAM_SITUACAO A , RH_T_FALTA B 
        WHERE FLG_ABONO_BENEFICIO = 1 
        AND A.ESTADO = 'A' AND A.ID = B.PARAM_SIT_ID
        AND A.ID = P_TIPO_LICENCA;
     EXCEPTION WHEN OTHERS THEN 
        V_TIPO_CONTAGEM_DIAS := 'TIPO_CONTAGEM_DIAS';
     END;
 
 p_desc_sobre:= V_TIPO_CONTAGEM_DIAS;
 p_dias_Direito := V_NUM_DIAS_ABONOS;
 p_dias_desc_rh := V_NUM_DIAS_DESCONTO_RH;
 p_dias_ndesc_rh := V_NUM_DIAS_NDESCONTO_RH;
 


    v_data_inicio := TRUNC(p_data_inicio);
    v_data_fim    := TRUNC(p_data_fim);
    
    v_mes_inicio := TRUNC(v_data_inicio, 'MM');

    WHILE v_mes_inicio <= TRUNC(v_data_fim, 'MM') LOOP

        v_mes_fim := LAST_DAY(v_mes_inicio);


           -- calcula inicio real do periodo no mes
        v_ini_periodo :=
            GREATEST(v_data_inicio, v_mes_inicio);

        -- calcula fim real do periodo no mes
        v_fim_periodo :=
            LEAST(v_data_fim, v_mes_fim);

        -- quantidade de dias
        v_dias :=
            v_fim_periodo - v_ini_periodo + 1;
            
         V_SALARIO_BASE:= GET_SALARIO_BASE (P_TIPREL_ID =>P_TIPREL_ID, p_data_de =>v_ini_periodo);
        V_SALARIO_DIARIO:= CALCULO_FALTA_DIARIO (P_TIPREL_ID =>P_TIPREL_ID, P_DATA_INICIO =>v_ini_periodo);    
        
    BEGIN 
        SELECT C.LOCAL_TRAB_ID
          INTO V_LOCAL_ID
        FROM  rh_t_tipos_relacionamento B, RH_T_MOBILIDADE C
         WHERE B.ID = P_TIPREL_ID AND B.MOB_ID = C.ID; 
     EXCEPTION WHEN OTHERS THEN NULL; END;
     
     
         IF V_TIPO_CONTAGEM_DIAS = DIAS_UTEIS THEN
         
           V_FERIADO := FN_CONTA_FERIADOS(p_geogr_id  => V_LOCAL_ID,
                                         p_data_inicio => p_data_inicio,
                                         p_data_fim  => p_data_fim);
                                         
           V_COUNT_NUTEIS:= FN_CONTA_DIAS_NAO_UTEIS(p_data_inicio  => v_data_inicio,
                                               p_data_fim => v_data_fim);
  
            
          ELSE
           V_FERIADO:= 0;
           V_COUNT_NUTEIS:= 0;
        END IF;
      --2 Contar numero de feriados;   
      
      V_COUNT_DIA := V_COUNT_DIA - V_FERIADO - V_COUNT_NUTEIS - V_NUM_DIAS_NDESCONTO_RH;
        
        
        p_meses(v_idx) := TO_CHAR(v_mes_inicio, 'MM/YYYY');
        
        
        p_dias_falta(v_idx)  := V_COUNT_DIA;--TO_CHAR(v_dias);
        
        p_valor_desc(v_idx) := V_SALARIO_DIARIO * V_COUNT_DIA;
        
        p_valor_salario (v_idx):= V_SALARIO_BASE;
        
        p_data_ini_falta(v_idx) :=
            TO_CHAR(v_ini_periodo, 'DD/MM/YYYY');

        p_data_fim_falta(v_idx) :=
            TO_CHAR(v_fim_periodo, 'DD/MM/YYYY');
        
        v_idx := v_idx + 1;
        v_mes_inicio := ADD_MONTHS(v_mes_inicio, 1);
        

    END LOOP;
----DEVOLVER DIAS DESCONTADO

 -- V_COUNT_DIA:= TRUNC(GREATEST(p_data_inicio, p_data_fim)) - TRUNC(LEAST(p_data_inicio, p_data_fim)) + 1;
  
 
END;
----------------------------------------------------------------------------------------------------------------------------- 
   FUNCTION GET_SALARIO_BASE (P_TIPREL_ID  NUMBER, p_data_de date)
   RETURN NUMBER
   IS
   v_salario NUMBER;
   v_cambio_de          VARCHAR2 (15) := NULL;
    v_cambio_para        VARCHAR2 (15) := 'CVE';
    v_cambio             NUMBER;
    BEGIN
    null;

       BEGIN
            SELECT r.valor, r.moeda                             -- tirei o max
              INTO v_salario, v_cambio_de
              FROM rh_t_def_remuneracoes r, 
                   rh_t_tiprel_rem_pag rr,
                  rh_tipo_movimentos tm
             WHERE     rr.tiprel_id = p_tiprel_id
                   AND r.tm_id = tm.id
                   AND RR.REM_ID = R.ID
                   --AND TM.SHORT_DESC = 'SAL'             --comentado por jmdupret
                   AND tm.short_desc IN ('SAL', 'SBNT') --acrescentado por jmdupret
                   AND tm.tipo = 'REM'          
                   AND r.estado = 'ACT'
                   AND p_data_de >= r.DATA_INICIO
                   AND (p_data_de <= r.DATA_FIM OR r.DATA_FIM IS NULL);     
        EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
                v_salario := 0;
        END;

        -- armandina 15-04-2006 pagamento em outra moeda

        IF v_cambio_de IS NOT NULL AND v_cambio_de <> 'CVE'
        THEN
            v_cambio :=
                inpssigof.orc_financ_projectos.da_cambio_dia (
                    v_cambio_de,
                    v_cambio_para,
                    TO_CHAR (p_data_de, 'dd-mm-yyyy'));

            IF v_cambio IS NOT NULL
            THEN
                v_salario := v_salario * v_cambio;
            ELSE
                v_salario := 0;
            END IF;
        END IF;

        RETURN v_salario;

    END;     
-----------------------------------------------------------------------------------
    FUNCTION IS_DIA_UTEL(p_data DATE, P_GEOGR_ID NUMBER DEFAULT NULL)
    RETURN VARCHAR2
        IS
          v_dia VARCHAR2(3);
          v_qtd NUMBER;
        BEGIN

          v_dia := UPPER(TO_CHAR(p_data, 'DY', 'NLS_DATE_LANGUAGE=ENGLISH'));

          IF v_dia NOT IN ('MON','TUE','WED','THU','FRI') THEN
            RETURN 'N';
          END IF;

          SELECT COUNT(*)
          INTO v_qtd
          FROM RH_T_PARAM_FERIADO 
          WHERE ((FIXO_ANO = 'NAO' AND DATA_ESPECIFICA = TRUNC(p_data))OR (FIXO_ANO = 'SIM'  AND DIA = TO_CHAR(p_data ,'DD') AND MES = TO_CHAR(p_data ,'MM')))
          AND GEOGR_ID IS NULL OR GEOGR_ID = P_GEOGR_ID;


          IF v_qtd > 0 THEN
            RETURN 'N';
          END IF;

          RETURN 'S';
    END;
    ---------------------------EMPRESTIMO ------------------------------------------------------
PROCEDURE PROCESSAR_EMPRESTIMO (P_ACCAO VARCHAR2, 
                                P_TIPREL_ID NUMBER DEFAULT NULL, 
                                P_DATA_INICIO DATE DEFAULT NULL, 
                                P_PROC_SAL_ID NUMBER DEFAULT NULL,
                                P_PROC_FUN_ID NUMBER DEFAULT NULL,
                                P_USER_ID NUMBER DEFAULT NULL,
                                P_USER_NAME VARCHAR2 DEFAULT NULL)
IS
 
  V_RH_T_PLANO_FINANCEIRO RH_T_PLANO_FINANCEIRO%ROWTYPE;
  V_RH_T_DEF_PAGAMENTOS RH_T_DEF_PAGAMENTOS%ROWTYPE; 
  V_rh_t_tiprel_rem_pag rh_t_tiprel_rem_pag%ROWTYPE;
  V_PAG_ID NUMBER;
  V_DATA_PAGAMENTO DATE;
  V_COUNT NUMBER;
  V_TM_ID NUMBER;
  v_tipo_pedido VARCHAR2(200);
  V_VALOR_PAGO NUMBER;
  V_FUN_ID NUMBER;
BEGIN
V_RH_T_DEF_PAGAMENTOS := NULL;
V_rh_t_tiprel_rem_pag := NULL;
V_RH_T_PLANO_FINANCEIRO := NULL;

-- 1 VERIFICA SE TEM DIVIDA 
--VERFICA O MIN ESMPRESTIMO QUE NÃO FOI PAGO AINDA 
 
      SELECT  COUNT(C.ID)
      INTO V_COUNT
      FROM RH_T_PEDIDO A , RH_T_EMPRESTIMO B , RH_T_PLANO_FINANCEIRO C, RH_T_TIPOS_RELACIONAMENTO D 
      WHERE A.ID = B.PEDIDO_ID AND B.ESTADO = 'A' AND A.FUN_ID =D.FUN_ID
      AND B.ID = P_TIPREL_ID
      AND C.EMPRESTIMO_ID = B.ID AND C.VALOR_PAGO IS NULL;
  
  
  IF V_COUNT > 0  THEN
  
       SELECT D.TM_ID 
      INTO V_TM_ID FROM RH_T_TIPOS_RELACIONAMENTO A, RH_T_CONTRATO_VINCULO B, rh_t_param_vinculo C, rh_t_param_vinculo_mov D
       WHERE A.ID = P_TIPREL_ID
       AND A.CONTR_VINCULO_ID = B.ID
       AND B.VINCULO_ID = C.ID
       AND C.ID = D.VINCULO_ID
       AND D.TIPO = 'EMPRESTIMO';
  
    IF P_ACCAO = 'INSERT' THEN
    
      SELECT MIN(DATA_PAGAMENTO),A.TIPO_PEDIDO,D.FUN_ID INTO V_DATA_PAGAMENTO ,v_tipo_pedido ,V_FUN_ID 
       FROM RH_T_PEDIDO A , RH_T_EMPRESTIMO B , RH_T_PLANO_FINANCEIRO C, RH_T_TIPOS_RELACIONAMENTO D 
      WHERE A.ID = B.PEDIDO_ID AND B.ESTADO = 'A' AND A.FUN_ID =D.FUN_ID
      AND B.ID = P_TIPREL_ID
      AND C.EMPRESTIMO_ID = B.ID AND C.VALOR_PAGO IS NULL
      GROUP BY A.TIPO_PEDIDO;
      
      SELECT C.*
      INTO V_RH_T_PLANO_FINANCEIRO
      FROM RH_T_PEDIDO A , RH_T_EMPRESTIMO B , RH_T_PLANO_FINANCEIRO C
      WHERE A.ID = B.PEDIDO_ID AND B.ESTADO = 'A' AND A.FUN_ID = V_FUN_ID
      AND C.EMPRESTIMO_ID = B.ID AND C.VALOR_PAGO IS NULL AND C.DATA_PAGAMENTO = V_DATA_PAGAMENTO;
   
        V_RH_T_DEF_PAGAMENTOS.TM_ID :=V_TM_ID;           
        V_RH_T_DEF_PAGAMENTOS.VALOR  := V_RH_T_PLANO_FINANCEIRO.VALOR_PRINCIPAL + V_RH_T_PLANO_FINANCEIRO.VALOR_JUROS;       
        V_RH_T_DEF_PAGAMENTOS.DATA_INICIO :=TRUNC(P_DATA_INICIO, 'MM');             
        V_RH_T_DEF_PAGAMENTOS.DATA_FIM :=  LAST_DAY(P_DATA_INICIO);              
        V_RH_T_DEF_PAGAMENTOS.ESTADO :='A';  
        V_RH_T_DEF_PAGAMENTOS.DATA_REGISTO :=SYSDATE;               
        V_RH_T_DEF_PAGAMENTOS.USER_REGISTO_ID :=P_USER_ID;              
        V_RH_T_DEF_PAGAMENTOS.USER_REGISTO_NAME :=P_USER_NAME;    
        V_RH_T_DEF_PAGAMENTOS.FUN_ID  :=V_FUN_ID;           
        V_RH_T_DEF_PAGAMENTOS.OBS  :='Pagamento Emprestimo viatura referente a ' ||v_tipo_pedido||' numero '||V_RH_T_PLANO_FINANCEIRO.NR_ORDEM_PRESTACAO; 
        V_RH_T_DEF_PAGAMENTOS.UUID  := GEN_UUID;      
        V_RH_T_DEF_PAGAMENTOS.DATA_ULTIMO_PROC :=P_DATA_INICIO;             
        V_RH_T_DEF_PAGAMENTOS.MOEDA  :='CVE'; 
        
        INSERT INTO RH_T_DEF_PAGAMENTOS VALUES V_RH_T_DEF_PAGAMENTOS RETURNING ID INTO V_PAG_ID;
        
        V_rh_t_tiprel_rem_pag.TIPREL_ID :=P_TIPREL_ID; 
        V_rh_t_tiprel_rem_pag.REM_ID :=NULL;
        V_rh_t_tiprel_rem_pag.PAG_ID := V_PAG_ID;
        
       INSERT INTO rh_t_tiprel_rem_pag VALUES V_rh_t_tiprel_rem_pag;
       --- ATUALIZA O DEFP_ID DO PLANO;
        UPDATE RH_T_PLANO_FINANCEIRO
       SET DEFP_ID  = V_PAG_ID
        WHERE ID = V_RH_T_PLANO_FINANCEIRO.ID;
       
    ELSIF P_ACCAO = 'DELETE' THEN
    
    FOR REC IN (
             SELECT B.DEFP_ID, B.VALOR  FROM rh_t_proc_funcionarios A, RH_T_PAGAMENTOS  B, RH_T_DEF_PAGAMENTOS C
             WHERE A.ID = B.PRSAL_ID 
             AND A.PRSALS_ID = P_PROC_SAL_ID
             AND C.TM_ID = V_TM_ID AND B.DEFP_ID = C.ID)
    LOOP
           
     IF REC.DEFP_ID IS NOT NULL THEN 
    
      DELETE FROM  rh_t_tiprel_rem_pag WHERE PAG_ID = V_PAG_ID;
      DELETE FROM  RH_T_DEF_PAGAMENTOS WHERE ID = V_PAG_ID;
      
        BEGIN 
          SELECT * INTO V_RH_T_PLANO_FINANCEIRO
          FROM RH_T_PLANO_FINANCEIRO WHERE DEFP_ID = REC.DEFP_ID;
        EXCEPTION WHEN OTHERS THEN V_RH_T_PLANO_FINANCEIRO:= NULL; END;
      
      IF V_RH_T_PLANO_FINANCEIRO.EMPRESTIMO_ID IS NOT NULL THEN 
          UPDATE RH_T_PLANO_FINANCEIRO
          SET VALOR_PAGO = NULL ,
          DEFP_ID  = NULL
          WHERE DEFP_ID = V_PAG_ID; 
          
          UPDATE RH_T_EMPRESTIMO
          SET VALOR_PAGO = NVL(VALOR_PAGO,V_VALOR_PAGO) - V_VALOR_PAGO
          WHERE ID = V_RH_T_PLANO_FINANCEIRO.EMPRESTIMO_ID;
     END IF; 
    END IF;
    END LOOP;
    
    ELSIF P_ACCAO = 'UPDATE 'THEN 
      BEGIN
         SELECT B.DEFP_ID , b.valor INTO V_PAG_ID, V_VALOR_PAGO FROM rh_t_proc_funcionarios A, RH_T_PAGAMENTOS  B, RH_T_DEF_PAGAMENTOS C
         WHERE A.ID = B.PRSAL_ID 
         AND A.PRSALS_ID = P_PROC_SAL_ID
         AND A.ID = P_PROC_FUN_ID
         AND C.TM_ID = V_TM_ID AND B.DEFP_ID = C.ID;
       EXCEPTION WHEN OTHERS THEN 
         V_PAG_ID := NULL;
     END;
    
        IF V_PAG_ID IS NOT NULL THEN 
            SELECT * INTO V_RH_T_PLANO_FINANCEIRO FROM RH_T_PLANO_FINANCEIRO WHERE DEFP_ID = V_PAG_ID;
             
            UPDATE RH_T_PLANO_FINANCEIRO
           SET FLG_PAGO ='SIM',
             VALOR_PAGO =  V_VALOR_PAGO
            WHERE ID = V_RH_T_PLANO_FINANCEIRO.ID;
            
            UPDATE RH_T_EMPRESTIMO
            SET VALOR_PAGO = VALOR_PAGO + V_VALOR_PAGO
            WHERE ID = V_RH_T_PLANO_FINANCEIRO.EMPRESTIMO_ID;
        END IF;
   END IF;
END IF;


END;
------------------------------------------------------------------------------------------------------------------------------
FUNCTION RH_FN_EH_FERIADO (
    p_geogr_id     IN NUMBER,
    p_data_inicio  IN DATE,
    p_data_fim     IN DATE
)
RETURN VARCHAR2
IS
    v_existe NUMBER := 0;
BEGIN

    SELECT COUNT(1)
      INTO v_existe
      FROM RH_T_PARAM_FERIADO f
     WHERE (f.GEOGR_ID = p_geogr_id OR f.GEOGR_ID IS NULL)
     AND (/* FERIADO VARIÁVEL */
              (UPPER(f.FIXO_ANO) = 'NAO' AND TRUNC(f.DATA_ESPECIFICA) BETWEEN TRUNC(p_data_inicio) AND TRUNC(p_data_fim)) 
           /* FERIADO FIXO */ OR
              (UPPER(f.FIXO_ANO) = 'SIM'
        AND EXISTS (SELECT 1
                          FROM (SELECT EXTRACT(YEAR FROM p_data_inicio) + LEVEL - 1 ano
                                  FROM dual
                               CONNECT BY LEVEL <=
                                    EXTRACT(YEAR FROM p_data_fim)
                                  - EXTRACT(YEAR FROM p_data_inicio) + 1
                               ) x
                         WHERE TO_DATE(
                                   LPAD(f.DIA,2,'0') || '/' ||
                                   LPAD(f.MES,2,'0') || '/' ||
                                   x.ano,
                                   'DD/MM/YYYY'
                               )
                               BETWEEN TRUNC(p_data_inicio)
                                   AND TRUNC(p_data_fim)
                  )
              )

           );

    IF v_existe > 0 THEN
        RETURN 'S';
    ELSE
        RETURN 'N';
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        RETURN 'N';
END;
-----------------------------------------------------------------------------------------------------------------
-- ============================================================
-- FUNÇÃO EXTENDIDA: FN_CONTA_FERIADOS
-- DESCRIÇÃO: Versão avançada que devolve o NÚMERO de dias
--            feriados num intervalo (sem contar duplicados)
--
-- Útil para cálculo de dias úteis e folhas de ponto.
-- ============================================================
 FUNCTION FN_CONTA_FERIADOS(
    p_geogr_id    IN NUMBER,
    p_data_inicio IN DATE,
    p_data_fim    IN DATE
) RETURN NUMBER
IS
    v_total       NUMBER := 0;
    v_data        DATE;
    v_data_fim    DATE;
 
BEGIN
    IF p_data_inicio IS NULL OR p_data_fim IS NULL THEN
        RETURN 0;
    END IF;
 
    v_data     := TRUNC(LEAST(p_data_inicio, p_data_fim));
    v_data_fim := TRUNC(GREATEST(p_data_inicio, p_data_fim));
 
    -- Usa SELECT DISTINCT sobre datas concretas para evitar
    -- contar o mesmo dia feriado mais de uma vez
    SELECT COUNT(DISTINCT data_feriado)
      INTO v_total
      FROM (
            -- ------------------------------------------------
            -- Feriados variáveis: usa DATA_ESPECIFICA directa
            -- ------------------------------------------------
            SELECT TRUNC(f.DATA_ESPECIFICA) AS data_feriado
              FROM RH_T_PARAM_FERIADO f
             WHERE f.FIXO_ANO = 'N'
               AND f.DATA_ESPECIFICA IS NOT NULL
               AND TRUNC(f.DATA_ESPECIFICA) BETWEEN v_data AND v_data_fim
               AND f.ANO_REFERENTE = TO_NUMBER(
                       TO_CHAR(f.DATA_ESPECIFICA, 'YYYY'))
               AND (f.GEOGR_ID IS NULL OR f.GEOGR_ID = p_geogr_id)
 
            UNION
 
            -- ------------------------------------------------
            -- Feriados fixos: reconstrói a data para cada ano
            -- do intervalo solicitado
            -- ------------------------------------------------
            SELECT TRUNC(TO_DATE(
                       LPAD(f.DIA, 2, '0') || '/' ||
                       LPAD(f.MES, 2, '0') || '/' ||
                       anos.ano,
                       'DD/MM/YYYY'
                   )) AS data_feriado
              FROM RH_T_PARAM_FERIADO f,
                   (SELECT EXTRACT(YEAR FROM v_data) + LEVEL - 1 AS ano
                      FROM DUAL
                    CONNECT BY LEVEL <=
                           EXTRACT(YEAR FROM v_data_fim)
                         - EXTRACT(YEAR FROM v_data) + 1
                   ) anos
             WHERE f.FIXO_ANO = 'S'
               AND f.DIA IS NOT NULL
               AND f.MES IS NOT NULL
               AND TRUNC(TO_DATE(
                       LPAD(f.DIA, 2, '0') || '/' ||
                       LPAD(f.MES, 2, '0') || '/' ||
                       anos.ano,
                       'DD/MM/YYYY'
                   )) BETWEEN v_data AND v_data_fim
               AND (f.GEOGR_ID IS NULL OR f.GEOGR_ID = p_geogr_id)
           );
 
    RETURN v_total;
 
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
 
END FN_CONTA_FERIADOS;


-----------------------------------------------------------------------------------------------------------
FUNCTION FN_CONTA_DIAS_NAO_UTEIS(
    p_data_inicio IN DATE,
    p_data_fim    IN DATE
) RETURN NUMBER
IS
    v_inicio      DATE;
    v_fim         DATE;
    v_data        DATE;
    v_dias_nao_uteis NUMBER := 0;
    v_dia_sem     NUMBER;
BEGIN
    IF p_data_inicio IS NULL OR p_data_fim IS NULL THEN
        RETURN 0;
    END IF;

    v_inicio := TRUNC(LEAST(p_data_inicio, p_data_fim));
    v_fim    := TRUNC(GREATEST(p_data_inicio, p_data_fim));
    v_data   := v_inicio;

    LOOP
        EXIT WHEN v_data > v_fim;

        v_dia_sem := TO_NUMBER(TO_CHAR(v_data, 'D'));

        -- 1 = Domingo, 7 = Sábado
        IF v_dia_sem IN (1, 7) THEN
            v_dias_nao_uteis := v_dias_nao_uteis + 1;
        END IF;

        v_data := v_data + 1;
    END LOOP;

    RETURN v_dias_nao_uteis;

EXCEPTION
    WHEN OTHERS THEN RETURN 0;
END FN_CONTA_DIAS_NAO_UTEIS;


-------------------------------------------------------------------------
PROCEDURE prc_dias_por_mes (
    p_data_inicio IN  DATE,
    p_data_fim    IN  DATE,

    p_meses       OUT OWA.vc_arr,
    p_dias        OUT OWA.vc_arr,
    p_data_ini    OUT OWA.vc_arr,
    p_data_fim_m  OUT OWA.vc_arr
)
IS
    v_data_inicio DATE;
    v_data_fim    DATE;

    v_mes_inicio  DATE;
    v_mes_fim     DATE;

    v_ini_periodo DATE;
    v_fim_periodo DATE;

    v_dias        NUMBER;
    v_idx         NUMBER := 1;
BEGIN

    IF p_data_inicio IS NULL OR p_data_fim IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001,
            'Data início e data fim são obrigatórias.');
    END IF;

    IF p_data_inicio > p_data_fim THEN
        RAISE_APPLICATION_ERROR(-20002,
            'Data início não pode ser maior que data fim.');
    END IF;

    v_data_inicio := TRUNC(p_data_inicio);
    v_data_fim    := TRUNC(p_data_fim);

    v_mes_inicio := TRUNC(v_data_inicio, 'MM');

    WHILE v_mes_inicio <= TRUNC(v_data_fim, 'MM') LOOP

        v_mes_fim := LAST_DAY(v_mes_inicio);

        -- calcula inicio real do periodo no mes
        v_ini_periodo :=
            GREATEST(v_data_inicio, v_mes_inicio);

        -- calcula fim real do periodo no mes
        v_fim_periodo :=
            LEAST(v_data_fim, v_mes_fim);

        -- quantidade de dias
        v_dias :=
            v_fim_periodo - v_ini_periodo + 1;

        -- arrays
        p_meses(v_idx) :=
            TO_CHAR(v_mes_inicio, 'MM/YYYY');

        p_dias(v_idx) :=
            TO_CHAR(v_dias);

        p_data_ini(v_idx) :=
            TO_CHAR(v_ini_periodo, 'DD/MM/YYYY');

        p_data_fim_m(v_idx) :=
            TO_CHAR(v_fim_periodo, 'DD/MM/YYYY');

        v_idx := v_idx + 1;

        v_mes_inicio := ADD_MONTHS(v_mes_inicio, 1);

    END LOOP;

END;

END RH_PROCESSAMENTO_SALARIAL_DB;
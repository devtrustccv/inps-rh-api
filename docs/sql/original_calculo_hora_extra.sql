FUNCTION CALCULO_HORA_EXTRA (P_TIPREL_ID NUMBER,P_DATA_INICIO DATE,P_DATA_FIM DATE,P_DIAS_APLICADA VARCHAR2, P_HORAS_DIARIA NUMBER)
   RETURN NUMBER
   IS

     V_LOCAL    NUMBER;
     V_HE_VALOR_DUTIL NUMBER;
     V_HE_VALOR_DNUTIL NUMBER;
    v_geo SIPSGLOBAL.GLB_T_GEOGRAFIA%ROWTYPE;
    V_COUNT NUMBER;  
    v_data date:= P_DATA_INICIO;
    V_SALARIO NUMBER;
    V_HORAS_DIARIAS NUMBER;
    V_SAL_DIARIO NUMBER;
    V_VALOR_HORAS NUMBER;
    v_valor_mes NUMBER;
    v_valor_total NUMBER;
    v_mes_atual VARCHAR2(10);
    V_DIAS_UTES NUMBER;
BEGIN

    -- TENHO QUE SABER PERCENTAGEM DE HORA 
    -- v_horas_50 := da_assiduidade_mensal (inpsrh.RH_CONST_ASSIDUIDADE.c_code_hora_extra_justificado50, p_tiprel_id, p_data_de, p_proc_salarios_id);
    --  v_horas_100 := da_assiduidade_mensal (inpsrh.RH_CONST_ASSIDUIDADE.c_code_hora_extra_justificado100, p_tiprel_id, p_data_de, p_proc_salarios_id);

     v_mes_atual := TO_CHAR(v_data, 'YYYYMM');

    -- 1.3 DEVO SABER QUANTAS ORAS POR DIA 
     -- V_HORAS_DIARIAS := 8;
    -- 1.2. SABER HORAS HORAS DIARIA 
      V_SALARIO := GET_SALARIO_BASE (P_TIPREL_ID, P_DATA_INICIO);
      V_SAL_DIARIO := V_SALARIO / V_HORAS_DIARIAS;

    --1 Devo primer ir buscar o codigo de hora extra (se 'e 50 ou 100 %), ou seja devo saber qual o codigo de o colaborador tem
    BEGIN
        SELECT HE_VALOR_DUTIL, HE_VALOR_DNUTIL, DIARIA
        INTO V_HE_VALOR_DUTIL, V_HE_VALOR_DNUTIL, V_HORAS_DIARIAS
        FROM RH_T_ASSIDUIDADE_PARAMETRO
        WHERE ESTADO = 'A';
    EXCEPTION WHEN OTHERS THEN 
      NULL;
    END;

    BEGIN
        SELECT E.*
         INTO  v_geo
         FROM 
             rh_t_tipos_relacionamento A, 
             RH_T_MOBILIDADE B,
             rh_t_param_local_trab C,
             SIPSGLOBAL.GLB_T_UPS D,
             SIPSGLOBAL.GLB_T_GEOGRAFIA E
         WHERE A.MOB_ID = B.ID
         AND B.LOCAL_TRAB_ID = C.ID
         AND C.UPS_ID = D.ID
         AND D.ID_GEOGRAFIA = E.ID
         AND A.ID = P_TIPREL_ID;
    EXCEPTION WHEN OTHERS THEN
      NULL;
    END;



 WHILE v_data <= P_DATA_FIM
  LOOP
   BEGIN 
         SELECT COUNT(1)
         INTO V_COUNT
         FROM RH_T_PARAM_FERIADO A
         WHERE ((FIXO_ANO = 'NAO' AND DATA_ESPECIFICA = SYSDATE)OR (FIXO_ANO = 'SIM'  AND DIA = TO_CHAR(SYSDATE ,'DD') AND MES = TO_CHAR(SYSDATE ,'MM')))
         AND (A.ANO_REFERENTE = 'TODOS'  OR  A.ANO_REFERENTE  = TO_CHAR(P_DATA_INICIO ,'YYYY'))
         AND (A.GEOGR_ID IS NULL OR GEOGR_ID = v_geo.CONCELHO);
    END;

    V_DIAS_UTES :=IS_DIA_UTEL(v_data,  NULL);

     IF  P_DIAS_APLICADA =  'DIAS_NAO_UTEIS'    
     THEN
       IF V_DIAS_UTES = 'N' OR V_COUNT >0 THEN
         V_VALOR_HORAS :=(V_SAL_DIARIO * V_HE_VALOR_DNUTIL * NVL (P_HORAS_DIARIA, 0));
       END IF;    
    ELSIF P_DIAS_APLICADA =  'DIAS_UTEIS_NAO_UTEIS' THEN 
      IF V_DIAS_UTES = 'N'  OR V_COUNT >0   THEN
        V_VALOR_HORAS :=(V_SAL_DIARIO * V_HE_VALOR_DNUTIL * NVL (P_HORAS_DIARIA, 0));
      ELSE 
         V_VALOR_HORAS :=(V_SAL_DIARIO * V_HE_VALOR_DUTIL * NVL (P_HORAS_DIARIA, 0));
      END IF;
    ELSIF P_DIAS_APLICADA =  'DIAS_UTEIS' THEN 
      IF V_DIAS_UTES = 'S'  AND  V_COUNT = 0   THEN
       V_VALOR_HORAS :=(V_SAL_DIARIO * V_HE_VALOR_DUTIL * NVL (P_HORAS_DIARIA, 0));
       END IF;
    END IF;

        IF TO_CHAR(v_data,'YYYYMM') <> v_mes_atual THEN

            v_mes_atual := TO_CHAR(v_data,'YYYYMM');
            v_valor_mes := 0;
         END IF;

         v_valor_mes  := v_valor_mes + V_VALOR_HORAS;
         v_valor_total := v_valor_total + V_VALOR_HORAS;


    v_data := v_data + 1;
  END LOOP;

    END;
    
    
--------------------------------------------------------------------------------------------------------  
   
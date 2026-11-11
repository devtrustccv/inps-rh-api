-- =====================================================================
-- Subsídio de Férias — REVERTER scripts/subsidio_ferias_calculo_correcao.sql (08/11/2026)
-- Repõe o package body e as vistas como estavam antes da correção (a vista da lista fica com o
-- filtro ESTADO IN ('P','A') aplicado a 07/11/2026). Os períodos gravados em RH_T_SUBSIDIO_DET
-- são apagados e a coluna SITUACAO removida.
-- =====================================================================

CREATE OR REPLACE PACKAGE BODY  RH_PK_SUBSISIO_NATAL_F_DB IS

-- REGRAS DE SUBSIDIO DE FERIAS 
    --1 DEVE SER SOMENTE PESSOAS CUJO ATIVOS NO INPS QUE NAO SEJAM PROJECTOS NEM ESTAGIARIOS
    --2 VEGIFICAR O SALARIO QUE O COLABORADOR TEVE DURANTE O ANO.
    --2.1 AS FALTAS ENTRAM NA CONTAGEM DE DIAS DE TRABALHO (VER PARAMETRIZACAO)
    --2.2 AS LICENÇAS SEM VENCIMENTO NÃO ENTRAM NA CONTAGEM DE DIAS DE TRABALHO(VER PARAMETRIZACAO)
    --2.3 SALARIO BASE ENTRA A DIFENÇA SALARIAL REFERENTE A SUBSTITUICAO 

-- REGRAS DE NATAL
    --1 Salário de referência: O valor é calculado sobre o vencimento da função existente em 15 de dezembro. 
    --2 O COLABORADOR NAO DEVE ESTAR TRABALHANDO ATE 15 DE DEZEMBRO
            -- no quadro do INPS; 
            -- em efetividade de funções; 
            -- Contratos a prazo : Também têm direito desde que o contrato preveja esse benefício
    --3 Percentagem conforme tempo de serviço
            --≥ 1 ano	100%
            --≥ 6 meses e < 1 ano	75%
            --≥3 meses e < 6 meses	50%
    --4 Penalizações disciplinares
            --Não recebem subsídio os colaboradores que:
            --•	tenham punição disciplinar prevista; 
            --•	ou tenham pena igual ou superior a 5 dias.
    
    --6 Faltas injustificadas
          --Com 6 ou mais faltas injustificadas, perde totalmente o direito ao subsídio.
            --0 100%
            --1	83,3%
            --2	66,6%
            --3	50%
            --4	33,3%
            --5	16,6%
            --6 ou mais	0%
    
    --7. Ausências que reduzem o tempo de serviço
            --Não contam para o tempo de serviço:
            --•	licença sem vencimento; 
            --•	faltas injustificadas; 
            --•	dispensas superiores a 30 dias.
 --- 8 prenda natal  .... 
          --- tem um limete de valor -- 4500
          --- somente colabordor com filho ate 12 anos
          --- deve ser responsavel fo filho
 --- 9  cheque brinde 
         --- limete de valor --7000
         --- para colaboresde que comprir com 22 dias 
          
  


------------------------------------------------------------------------------------------------------------------------------

PROCEDURE CALCULO_DIAS_FERIA (P_ANO_REFERENTE VARCHAR2,P_USER_REGISTO_ID NUMBER,P_USER_REGISTO_NAME VARCHAR2 )

IS 
V_RH_T_SUBSIDIO_FERIAS RH_T_SUBSIDIOS%ROWTYPE;
V_VALOR_MES NUMBER := 0;
V_VALOR_DIA NUMBER := 0;
V_VALOR_MES_TOTAL  NUMBER := 0;
V_COUNT NUMBER:= 0;
V_ID NUMBER;
V_ANO_REFERENTE VARCHAR2(100);
BEGIN

V_ANO_REFERENTE := P_ANO_REFERENTE;
 
    FOR REC IN (SELECT FUN_ID, ANO, DATA_INICIO,DATA_FIM,SALARIO,MESES,DIAS,SITUACAO,ESCALAO_ID
                      FROM RH_V_CALC_SUBSIDIO_FERIAS F
                     WHERE ANO = V_ANO_REFERENTE
                     ORDER BY DATA_INICIO)
   LOOP
        V_VALOR_MES := REC.MESES * REC.SALARIO;
       V_VALOR_DIA := REC.MESES / 30;
       V_VALOR_DIA := REC.DIAS * V_VALOR_DIA;

V_RH_T_SUBSIDIO_FERIAS := NULL;
       
SELECT COUNT(1) INTO V_COUNT FROM RH_T_SUBSIDIOS WHERE  FUN_ID = REC.FUN_ID AND ANO_REFERENTE =  P_ANO_REFERENTE; 

IF  V_COUNT > 0 THEN 
  SELECT * INTO V_RH_T_SUBSIDIO_FERIAS FROM RH_T_SUBSIDIOS WHERE  FUN_ID = REC.FUN_ID AND ANO_REFERENTE =  P_ANO_REFERENTE; 

END IF;

       
      --V_RH_T_SUBSIDIO_FERIAS.ID := 1;    
      V_RH_T_SUBSIDIO_FERIAS.ANO_REFERENTE := P_ANO_REFERENTE;       
      V_RH_T_SUBSIDIO_FERIAS.ESCALAO_ID  :=   REC.ESCALAO_ID;      
      V_RH_T_SUBSIDIO_FERIAS.MES_TRAB  := REC.MESES;       
      V_RH_T_SUBSIDIO_FERIAS.DIAS_TRAB  :=  REC.DIAS;     
      V_RH_T_SUBSIDIO_FERIAS.ESTADO  := 'P';  
      V_RH_T_SUBSIDIO_FERIAS.FUN_ID := REC.FUN_ID;       
      V_RH_T_SUBSIDIO_FERIAS.DATA_REGISTO := SYSDATE;        
      V_RH_T_SUBSIDIO_FERIAS.USER_REGISTO_ID  :=  P_USER_REGISTO_ID;      
      V_RH_T_SUBSIDIO_FERIAS.USER_REGISTO_NAME  := P_USER_REGISTO_NAME; 
      V_RH_T_SUBSIDIO_FERIAS.USER_ALTERACAO_ID  := NULL;       
      V_RH_T_SUBSIDIO_FERIAS.USER_ALTERACAO_NAME := NULL;
      V_RH_T_SUBSIDIO_FERIAS.DATA_ALTERACAO := NULL;         
      V_RH_T_SUBSIDIO_FERIAS.UUID  := GEN_UUID;
      V_RH_T_SUBSIDIO_FERIAS.DATA_INICIO :=  REC.DATA_INICIO;        
      V_RH_T_SUBSIDIO_FERIAS.DATA_FIM := REC.DATA_FIM;  
      V_RH_T_SUBSIDIO_FERIAS.VALOR_MES := V_VALOR_MES;    
      V_RH_T_SUBSIDIO_FERIAS.VALOR_DIA := V_VALOR_DIA;     
      V_RH_T_SUBSIDIO_FERIAS.SITUACAO  := REC.SITUACAO;
      V_RH_T_SUBSIDIO_FERIAS.VALOR_SUBSIDIO:= 0; 
      V_RH_T_SUBSIDIO_FERIAS.FLG_ATIVO_INACTIVO := 'A';
      V_RH_T_SUBSIDIO_FERIAS.TIPO_SUBSIDIO := 'FERIA';
       
   
    IF V_COUNT = 0 THEN
        INSERT INTO RH_T_SUBSIDIOS VALUES V_RH_T_SUBSIDIO_FERIAS RETURNING ID INTO V_ID;
      ELSE
        UPDATE  RH_T_SUBSIDIOS SET  ROW = V_RH_T_SUBSIDIO_FERIAS WHERE FUN_ID = REC.FUN_ID AND ANO_REFERENTE = V_ANO_REFERENTE;
      END IF;
 END LOOP;
   
END;

------------------------------------------------------------------------------------------------------------------------
PROCEDURE CALCULO_DIAS_NATAL(P_ANO_REFERENTE VARCHAR2,P_USER_REGISTO_ID NUMBER,P_USER_REGISTO_NAME VARCHAR2, P_MSG_ERRO OUT VARCHAR2)

IS 

    V_DATA_REFERENCIA DATE;
    V_SALARIO              NUMBER := 0;
    V_MESES_SERVICO        NUMBER := 0;
    V_PERCENT_TEMPO        NUMBER := 0;
    V_QTD_FALTAS           NUMBER := 0;
    V_FALTAS_CALCULO       NUMBER := 0;
    V_PERCENT_FALTA        NUMBER := 100;
    V_VALOR_SUBSIDIO       NUMBER := 0;
    V_ELEGIVEL             NUMBER(1) := 1;
    V_MOTIVO_NAO_ELEGIVEL  VARCHAR2(500);
    V_MULT_FALTA_INJUST    NUMBER := 1;
    V_LIMITE_DISPENSA      NUMBER := NULL; 
    V_TEMPO_SERVICO NUMBER := 0;
    V_COUNT_DISPENSA NUMBER := 0;
    V_COUNT_PD NUMBER := 0;
    V_COUNT_LICENCA NUMBER := 0;
    V_COUNT_FALTA NUMBER := 0;
    v_rh_t_subsidio_natal RH_T_SUBSIDIOS%rowtype;
    V_ANO_REFERENTE VARCHAR2(100);
    V_COUNT NUMBER:= 0;
    V_COUNT_POCESSAMENTO  NUMBER := 0;
    V_DIAS_FALTA_INJUST  NUMBER := 0;
    V_DIAS_LICENCA  NUMBER := 0;
    V_DATA_INICIO DATE;
    V_DATA_FIM DATE;
    V_DIAS_TOTAIS NUMBER:=365;
    V_DIAS_EFETIVOS  NUMBER := 0;
    V_MSG VARCHAR2(500);
    V_ESTADO VARCHAR2(20):= 'A';
BEGIN
 --NULL;
 ---SALARIO_BASE: aqui devo pegar o ultimo salario que o colaborador recebeu até 15 de janeiro
 --- CASO O TRABALHADOR RECEERR ENTRE 
 --- * 4 A 6  75%
 --- * 7 A 10 50 %
 ---NO SEU SALARIO QUE TIVER ATIVO atºe 25 de dezem
 SELECT COUNT(ID) INTO V_COUNT_POCESSAMENTO FROM RH_T_SUBSIDIOS WHERE PROC_SAL_ID IS NOT NULL AND ANO_REFERENTE = P_ANO_REFERENTE AND TIPO_SUBSIDIO = 'NATAL'; 
 
 IF V_COUNT_POCESSAMENTO > 0 THEN 
 
   P_MSG_ERRO := 'Subsidio de Natal desse ano:'||P_ANO_REFERENTE||' já foi Processado';
 
 ELSE 
 
  V_ANO_REFERENTE := P_ANO_REFERENTE;
  V_DATA_INICIO := TO_DATE('01/01/' || V_ANO_REFERENTE, 'DD/MM/YYYY');
  V_DATA_FIM :=TO_DATE('15/12/' || V_ANO_REFERENTE,'DD/MM/YYYY');
  


 --1  VERIFICAR COLBORADOR COM SALARIO ATIVOS  até 15  DE DESEMBRO
  FOR REC IN (SELECT C.ID,
       A.FUN_ID,
       C.DATA_INICIO,
       C.DATA_FIM,
       C.VALOR
            FROM RH_T_TIPOS_RELACIONAMENTO A,
                 RH_T_TIPREL_REM_PAG B,
                 RH_T_DEF_REMUNERACOES C,
                 INPSSIGOF.RH_TIPO_MOVIMENTOS D,
                 RH_T_CONTRATO_VINCULO E,
                 RH_T_PARAM_VINCULO F
            WHERE A.FUN_ID IS NOT NULL
              AND B.TIPREL_ID = A.ID
              AND C.ID = B.REM_ID
              AND D.ID = C.TM_ID
              AND D.SHORT_DESC = 'SAL'
              AND E.ID = A.CONTR_VINCULO_ID
              AND E.VINCULO_ID = F.ID
              AND EXISTS (SELECT 1 FROM RH_T_PARAM_SUBSIDIOS SUB WHERE SUB.CODIGO_REFERENTE = F.CODIGO)
              AND C.DATA_INICIO <= V_DATA_FIM 
              AND NVL(C.DATA_FIM, DATE '9999-12-31') >= V_DATA_INICIO
              AND C.ID = (
                  SELECT MAX(C2.ID)
                  FROM RH_T_TIPREL_REM_PAG B2,
                       RH_T_DEF_REMUNERACOES C2,
                       INPSSIGOF.RH_TIPO_MOVIMENTOS D2
                  WHERE B2.TIPREL_ID = A.ID
                    AND C2.ID = B2.REM_ID
                    AND D2.ID = C2.TM_ID
                    AND D2.SHORT_DESC = 'SAL'
                    AND C2.DATA_INICIO <= V_DATA_FIM
                    AND NVL(C2.DATA_FIM, DATE '9999-12-31') >= V_DATA_INICIO))
    LOOP
    
    v_rh_t_subsidio_natal := NULL;
    V_SALARIO := REC.VALOR;
    
 -- 1.1 VERIFICO SE JA TENHO UM REGISTO DESSE COLABORADOR, PARA EVITAR DUPLICACAO 
  SELECT COUNT(1) INTO V_COUNT FROM RH_T_SUBSIDIOS WHERE  FUN_ID = REC.FUN_ID AND ANO_REFERENTE =  P_ANO_REFERENTE AND TIPO_SUBSIDIO = 'NATAL'; 
 
  IF V_COUNT > 0 THEN
   SELECT * INTO v_rh_t_subsidio_natal FROM RH_T_SUBSIDIOS WHERE  FUN_ID = REC.FUN_ID AND ANO_REFERENTE =  P_ANO_REFERENTE AND TIPO_SUBSIDIO = 'NATAL'; 
  END IF;
  
    
-- 1.3 VERIFICO O TEMPO DE SERVICO NESSE ANO  EM FORMATO MESES   
-- 1.3.1 VERIFICAO DIAS FALTA INJUSTIFICADA NESSE PERIODO
    BEGIN
     V_DIAS_FALTA_INJUST := RH_PK_FUNCOES_PROC_GERAL_DB.GET_DIAS_FALTAS_INJUSTIFICADAS(REC.FUN_ID,V_DATA_INICIO,V_DATA_FIM);
    EXCEPTION WHEN OTHERS THEN
      V_DIAS_FALTA_INJUST := 0;
    END;
    
-- 1.3.2 VERIFICO DIAS LICENCA  SEM VENCIMENTO
  BEGIN
     V_DIAS_LICENCA := RH_PK_FUNCOES_PROC_GERAL_DB.GET_DIAS_LICENCA_S_RETRIB(REC.FUN_ID,V_DATA_INICIO,V_DATA_FIM);
    EXCEPTION WHEN OTHERS THEN
      V_DIAS_FALTA_INJUST := 0;
    END;
    

 --  1.3.3 CALCULA OS MESES  QUE O COLABORADOR  TRABALHOU
     V_DIAS_EFETIVOS := V_DIAS_TOTAIS - V_DIAS_FALTA_INJUST - V_DIAS_LICENCA;

    -- 5. Converter dias em meses (30 dias = 1 mês)
     V_TEMPO_SERVICO := V_DIAS_EFETIVOS / 30;
     V_TEMPO_SERVICO := ROUND(V_TEMPO_SERVICO, 2);
  
    
   FOR REC1 IN  (SELECT *
          FROM RH_T_PARAM_SUBSIDIOS
         WHERE TIPO_SITUACAO = 'SUSPENSAO_DIREITO'
         AND ESTADO = 'A' AND TIPO_SUBSIDIO = 'NATAL') 
   
  LOOP
   
   IF REC1.SITUACAO = 'LICENCA_S_VENCIMENTO' THEN
       
   
   V_COUNT_LICENCA := RH_PK_FUNCOES_PROC_GERAL_DB.GET_N_LICENCA_S_VENCIMENTO(REC.FUN_ID,V_DATA_INICIO,V_DATA_FIM);
   
    IF V_COUNT_LICENCA > 0 THEN
     V_MSG := V_MSG|| 'Suspenso por motivo de Licença S/ Retribuição ';
     V_ESTADO := 'I';
    END IF;
   END IF;
   
   IF REC1.SITUACAO = 'PROCESSO_DISCIPLINAR' THEN
   
    V_COUNT_PD := RH_PK_FUNCOES_PROC_GERAL_DB.GET_N_PROCESSO_DISCIPLINAR(REC.FUN_ID,V_DATA_INICIO,V_DATA_FIM);
     
     IF V_COUNT_PD > 0 THEN
       V_MSG := V_MSG|| ' Suspenso por motivo de processo Disciplinar ';
       V_ESTADO := 'I';
     END IF;  
   END IF;
   
   IF REC1.SITUACAO = 'DISPENSA' THEN
        
        V_COUNT_LICENCA := RH_PK_FUNCOES_PROC_GERAL_DB.GET_N_DISPENSA(REC.FUN_ID,V_DATA_INICIO,V_DATA_FIM);
      
      IF V_COUNT_LICENCA > 0 THEN
       V_MSG := V_MSG|| ' Suspenso por motivo de dispensa maior que limite ';
       V_ESTADO := 'I';
     END IF;    
   END IF;
   
   IF REC1.SITUACAO = 'FALTAS_INJUSTIFICADA' THEN
    V_COUNT_FALTA := V_DIAS_FALTA_INJUST; 
    
    IF V_COUNT_FALTA > 0 THEN
       V_MSG := V_MSG|| ' Suspenso por motivo de falta maior que limite ';
       V_ESTADO := 'I';
     END IF; 
   END IF;
   
  END LOOP;
  

    BEGIN  
     SELECT PERCENTAGEM
      INTO V_PERCENT_TEMPO
      FROM RH_T_PARAM_SUBSIDIOS
     WHERE TIPO_SITUACAO = 'TEMPO_SERVICO'
     AND V_TEMPO_SERVICO >= LIMITE_INFERIOR
     AND (V_TEMPO_SERVICO <= LIMITE_SUPERIOR OR LIMITE_SUPERIOR IS NULL);
    EXCEPTION WHEN OTHERS THEN
     V_PERCENT_TEMPO := 0;
    END;  

  V_VALOR_SUBSIDIO := V_SALARIO * V_PERCENT_TEMPO / 100;
  
  V_QTD_FALTAS := NULL; -- PEGAR NUMERO DE FALTAS;
  IF V_COUNT_FALTA > 0 THEN 
  
  BEGIN  
     SELECT PERCENTAGEM
      INTO V_PERCENT_FALTA
      FROM RH_T_PARAM_SUBSIDIOS
     WHERE TIPO_SITUACAO = 'PERCENTAGEM_FALTA'
     AND V_COUNT_FALTA >= LIMITE_INFERIOR
     AND (V_COUNT_FALTA <= LIMITE_SUPERIOR OR LIMITE_SUPERIOR IS NULL);
    EXCEPTION WHEN OTHERS THEN
     V_PERCENT_TEMPO := 0;
    END; 
    
    V_VALOR_SUBSIDIO := V_VALOR_SUBSIDIO * V_PERCENT_FALTA / 100;
END IF;
       
    v_rh_t_subsidio_natal.FUN_ID  := REC.FUN_ID;  
    v_rh_t_subsidio_natal.ANO_REFERENTE  := P_ANO_REFERENTE;     
    v_rh_t_subsidio_natal.VALOR_SALARIO_BASE := V_SALARIO;         
    v_rh_t_subsidio_natal.MES_TRAB := NULL;   
    v_rh_t_subsidio_natal.PERC_SALARIO := V_PERCENT_TEMPO;        
    v_rh_t_subsidio_natal.FALTAS  := V_COUNT_FALTA;       
    v_rh_t_subsidio_natal.PERC_FALTA := V_PERCENT_FALTA;       
    v_rh_t_subsidio_natal.VALOR_SUBSIDIO := V_VALOR_SUBSIDIO;         
    v_rh_t_subsidio_natal.VALOR_CHEQUE_BRINDE := NULL;        
    v_rh_t_subsidio_natal.VALOR_PRENDA_NATAL := NULL;         
    v_rh_t_subsidio_natal.ESTADO  := 'P';    
    v_rh_t_subsidio_natal.UUID := GEN_UUID; 
    v_rh_t_subsidio_natal.PROC_SAL_ID := NULL;     
    v_rh_t_subsidio_natal.REFERENCIA_ID := NULL;  
    v_rh_t_subsidio_natal.FLG_ATIVO_INACTIVO := V_ESTADO;
    v_rh_t_subsidio_natal.TIPO_SUBSIDIO := 'NATAL';
    v_rh_t_subsidio_natal.OBSERVACAO := V_MSG;
    
    
    IF V_COUNT = 0 THEN
        INSERT INTO RH_T_SUBSIDIOS VALUES v_rh_t_subsidio_natal ;
      ELSE
        UPDATE  RH_T_SUBSIDIOS SET  ROW = v_rh_t_subsidio_natal WHERE FUN_ID = REC.FUN_ID AND ANO_REFERENTE = V_ANO_REFERENTE;
      END IF;

 END LOOP;
END IF;
END;

--------------------------------------------------------------------------------------------------------------------------------

END RH_PK_SUBSISIO_NATAL_F_DB;
/

CREATE OR REPLACE VIEW RH_V_SUBSIDIO_FERIAS_DET AS
SELECT a.ANO_REFERENTE,a.ESCALAO_ID, a.MES_TRAB, a.DIAS_TRAB,a.VALOR_SUBSIDIO,
a.estado,a.FUN_ID, B.NOME FUN_NOME, a.UUID,a.DATA_INICIO, a.DATA_FIM, a.VALOR_MES,a.VALOR_DIA,        
a.SITUACAO, c.NIVEL_REFERENCIA||c.ESCALAO  ESCALACAO_DESC, C.VALOR  VALOR_ESCALAO,
(A.MES_TRAB * 30) + A.DIAS_TRAB AS DIAS_TOTAL
FROM  rh_t_subsidios A, rh_t_funcionarios b, rh_t_param_escalao c
WHERE a.fun_id = B.ID
AND A.ESCALAO_ID = C.ID
AND a.tipo_subsidio = 'FERIA'
/

CREATE OR REPLACE VIEW RH_V_SUBSIDIO_FERIAS AS
SELECT
       X.SUBSIDIO_ID,
       X.FUN_ID,
       X.ANO_REFERENTE,
       X.FUN_NOME,
       /* Valores provenientes da vista base */
       X.VALOR_TOTAL,
       X.MESES_TOTAL,
       X.DIAS_TOTAL,
       X.INSTIT_ID,
       X.ESTADO,
       x.ESTADO_DESC,
       X.FLG_ATIVO_INACTIVO,
       x.FLG_ATIVO_INACTIVO_DESC,
       /* Total de dias considerados */
       (X.MESES_TOTAL * 30) + X.DIAS_TOTAL AS SUMATORIA_DIAS_TOTAL,

       /* Valor mensal médio */
       CASE
           WHEN ((X.MESES_TOTAL * 30) + X.DIAS_TOTAL) > 0
           THEN
               (X.VALOR_TOTAL /
                ((X.MESES_TOTAL * 30) + X.DIAS_TOTAL)) * 30
           ELSE 0
       END AS VALOR_MES_TOTAL,

       /* Dias de férias proporcionais
          360 dias trabalhados = 22 dias de férias */
       CASE
           WHEN ((X.MESES_TOTAL * 30) + X.DIAS_TOTAL) > 0
           THEN
               (((X.MESES_TOTAL * 30) + X.DIAS_TOTAL) * 22) / 360
           ELSE 0
       END AS DIAS_SUBSIDIO,

       /* Valor do subsídio de férias */
       CASE
           WHEN ((X.MESES_TOTAL * 30) + X.DIAS_TOTAL) > 0
           THEN
               (
                   (
                       (X.VALOR_TOTAL /
                        ((X.MESES_TOTAL * 30) + X.DIAS_TOTAL)) * 30
                   ) / 22
               )
               *
               (
                   (((X.MESES_TOTAL * 30) + X.DIAS_TOTAL) * 22) / 360
               )
           ELSE 0
       END AS VALOR_SUBSIDIO

FROM (
       SELECT
              A.ID AS SUBSIDIO_ID,
              A.FUN_ID,
              b.nome FUN_NOME, 
              ANO_REFERENTE,
              D.INSTIT_ID,
              A.ESTADO,
              DECODE(A.ESTADO, 'A','Validado','P', 'Pendente') ESTADO_DESC,
              a.FLG_ATIVO_INACTIVO,
              DECODE(A.FLG_ATIVO_INACTIVO,'A','Ativo','I','Inativo','PR','Processado' )FLG_ATIVO_INACTIVO_DESC,
              SUM(NVL(VALOR_MES,0) + NVL(VALOR_DIA,0)) AS VALOR_TOTAL,
              SUM(NVL(MES_TRAB,0))                         AS MESES_TOTAL,
              SUM(NVL(VALOR_DIA,0))                          AS DIAS_TOTAL
       FROM RH_T_SUBSIDIOS A, RH_T_FUNCIONARIOS B, RH_T_TIPOS_RELACIONAMENTO C,
       RH_T_MOBILIDADE D
       WHERE A.FUN_ID = B.ID
       AND B.ID = C.FUN_ID
       AND C.MOB_ID = D.ID
       AND EST_ACT_ADM = 1
       AND A.TIPO_SUBSIDIO = 'FERIA' 
       AND A.ESTADO IN ('P','A')
       GROUP BY A.ID, A.FUN_ID,b.NOME, ANO_REFERENTE, D.INSTIT_ID,A.ESTADO, a.FLG_ATIVO_INACTIVO  
     ) X
/

DELETE FROM RH_T_SUBSIDIO_DET;
COMMIT;

ALTER TABLE RH_T_SUBSIDIO_DET DROP COLUMN SITUACAO;

# Correcções Oracle — Package RH_PROCESSAMENTO_SALARIAL_DB

**Data:** 2026-05-28  
**Contexto:** Implementação do cálculo do valor diário de hora extra (secção Hora Extra da Especificação de Gestão Assiduidade). O package existia mas as funções `CALCULO_HORA_EXTRA` e `GET_SALARIO_BASE` estavam com bugs que impediam o funcionamento em runtime.

---

## 1. Package Spec — adicionada declaração de `CALCULO_HORA_EXTRA`

### Problema
A função `CALCULO_HORA_EXTRA` estava implementada no PACKAGE BODY mas **não declarada no PACKAGE SPEC**. Em Oracle, só as funções declaradas no Spec são visíveis e chamáveis externamente. Qualquer chamada externa lançava:

```
ORA-06550: PLS-00302: component 'CALCULO_HORA_EXTRA' must be declared
```

### Fix — linha adicionada ao Spec (antes do `END`)
```sql
FUNCTION CALCULO_HORA_EXTRA (P_TIPREL_ID NUMBER, P_DATA_INICIO DATE,
    P_DATA_FIM DATE, P_DIAS_APLICADA VARCHAR2, P_HORAS_DIARIA NUMBER)
RETURN NUMBER;
```

---

## 2. `GET_SALARIO_BASE` — estado `'ACT'` → `'A'`

### Problema
A função filtrava remunerações activas com `r.estado = 'ACT'`. O sistema Java usa o enum `Estado.A` que grava `'A'` na base de dados. Não existe nenhum registo com `estado = 'ACT'`, logo a função **retornava sempre 0**.

### Antes
```sql
AND r.estado = 'ACT'
```

### Depois
```sql
AND r.estado = 'A'
```

---

## 3. `CALCULO_HORA_EXTRA` — 10 bugs corrigidos

O ficheiro com a função original (antes de qualquer correcção) está em:  
`docs/original_calculo_hora_extra.sql`

### Fix 1 + 2 — Ordem de execução invertida + conversão de DIARIA

**Problema:**  
`V_SAL_DIARIO := V_SALARIO / V_HORAS_DIARIAS` era calculado **antes** de `V_HORAS_DIARIAS` ser carregado. No momento do cálculo `V_HORAS_DIARIAS = NULL`, resultando em divisão por NULL (resultado NULL).

Adicionalmente, a coluna `DIARIA` em `RH_T_ASSIDUIDADE_PARAMETRO` é `VARCHAR2` com formato `'08:00'`. Atribuir esse valor directamente a uma variável `NUMBER` causava `ORA-01722: invalid number`, silenciado pelo `EXCEPTION WHEN OTHERS THEN NULL`, deixando `V_HORAS_DIARIAS = NULL`.

**Antes:**
```sql
-- V_HORAS_DIARIAS ainda é NULL aqui!
V_SALARIO    := GET_SALARIO_BASE(P_TIPREL_ID, P_DATA_INICIO);
V_SAL_DIARIO := V_SALARIO / V_HORAS_DIARIAS;  -- NULL / NULL = NULL

BEGIN
    SELECT HE_VALOR_DUTIL, HE_VALOR_DNUTIL, DIARIA  -- DIARIA é '08:00' (VARCHAR2)
    INTO V_HE_VALOR_DUTIL, V_HE_VALOR_DNUTIL, V_HORAS_DIARIAS  -- erro silenciado
    FROM RH_T_ASSIDUIDADE_PARAMETRO WHERE ESTADO = 'A';
EXCEPTION WHEN OTHERS THEN NULL;
END;
```

**Depois:**
```sql
-- 1. Buscar parametros PRIMEIRO, converter DIARIA 'HH:MM' para horas numéricas
BEGIN
    SELECT HE_VALOR_DUTIL,
           HE_VALOR_DNUTIL,
           TO_NUMBER(SUBSTR(DIARIA, 1, 2)) + TO_NUMBER(NVL(SUBSTR(DIARIA, 4, 2), '0')) / 60
    INTO   V_HE_VALOR_DUTIL, V_HE_VALOR_DNUTIL, V_HORAS_DIARIAS
    FROM   RH_T_ASSIDUIDADE_PARAMETRO
    WHERE  ESTADO = 'A' AND ROWNUM = 1;
EXCEPTION WHEN OTHERS THEN
    V_HORAS_DIARIAS := 8; V_HE_VALOR_DUTIL := 50; V_HE_VALOR_DNUTIL := 75;
END;

-- 2. Agora V_HORAS_DIARIAS está populado
V_SALARIO    := GET_SALARIO_BASE(P_TIPREL_ID, P_DATA_INICIO);
V_SAL_DIARIO := V_SALARIO / NULLIF(V_HORAS_DIARIAS, 0);
```

---

### Fix 3 — `V_DIAS_UTES NUMBER` → `VARCHAR2(1)`

**Problema:**  
`IS_DIA_UTEL` retorna `VARCHAR2` com valor `'S'` ou `'N'`. A variável `V_DIAS_UTES` estava declarada como `NUMBER`. A atribuição `V_DIAS_UTES := IS_DIA_UTEL(...)` causava `ORA-01722` em runtime. Consequência: a comparação `V_DIAS_UTES = 'S'` nunca era verdadeira.

**Antes:**
```sql
V_DIAS_UTES NUMBER;
```
**Depois:**
```sql
V_DIAS_UTES VARCHAR2(1);
```

---

### Fix 4 — Inicialização dos acumuladores

**Problema:**  
`v_valor_mes` e `v_valor_total` declarados sem valor inicial. Em PL/SQL, variáveis NUMBER não inicializadas são `NULL`. `NULL + x = NULL`, logo os acumuladores nunca acumulavam nada.

**Antes:**
```sql
v_valor_mes   NUMBER;
v_valor_total NUMBER;
```
**Depois:**
```sql
v_valor_mes   NUMBER := 0;
v_valor_total NUMBER := 0;
```

---

### Fix 5 — Reset de `V_VALOR_HORAS` e `V_COUNT` a cada iteração do loop

**Problema:**  
Dentro do loop, `V_VALOR_HORAS` não era resetado a 0 no início de cada dia. Se um dia não satisfazia nenhuma das condições `IF P_DIAS_APLICADA = ...`, o valor do dia anterior era re-adicionado ao total. Resultado: valores acumulados incorrectos (inflados).

**Antes:** *(sem reset no início do loop)*

**Depois:**
```sql
WHILE v_data <= P_DATA_FIM LOOP
    V_VALOR_HORAS := 0;   -- reset por iteração
    V_COUNT       := 0;
    ...
```

---

### Fix 6 + 7 — `SYSDATE` → `v_data` na query de feriados

**Problema:**  
A query de verificação de feriados usava `SYSDATE` (data actual do sistema) em vez de `v_data` (a data da iteração do loop). Qualquer dia passado ou futuro era verificado como se fosse hoje, dando resultados completamente errados.

Também o ano de referência usava `P_DATA_INICIO` em vez de `v_data`, tornando a filtragem por `ANO_REFERENTE` incorrecta para períodos que atravessam anos.

**Antes:**
```sql
WHERE ((FIXO_ANO = 'NAO' AND DATA_ESPECIFICA = SYSDATE)
   OR  (FIXO_ANO = 'SIM' AND DIA = TO_CHAR(SYSDATE,'DD') AND MES = TO_CHAR(SYSDATE,'MM')))
AND (A.ANO_REFERENTE = 'TODOS' OR A.ANO_REFERENTE = TO_CHAR(P_DATA_INICIO,'YYYY'))
```

**Depois:**
```sql
WHERE ((FIXO_ANO = 'NAO' AND DATA_ESPECIFICA = TRUNC(v_data))
   OR  (FIXO_ANO = 'SIM' AND DIA = TO_CHAR(v_data,'DD') AND MES = TO_CHAR(v_data,'MM')))
AND (A.ANO_REFERENTE = 'TODOS' OR A.ANO_REFERENTE = TO_CHAR(v_data,'YYYY'))
```

---

### Fix 8 — EXCEPTION handler no bloco BEGIN de feriados

**Problema:**  
O `BEGIN` que faz a query de feriados não tinha `EXCEPTION` handler. Se a query falhasse (ex: tabela vazia, erro de acesso), o erro propagava e a função falhava silenciosamente.

**Antes:**
```sql
BEGIN
    SELECT COUNT(1) INTO V_COUNT FROM RH_T_PARAM_FERIADO A WHERE ...;
END;
```

**Depois:**
```sql
BEGIN
    SELECT COUNT(1) INTO V_COUNT FROM RH_T_PARAM_FERIADO A WHERE ...;
EXCEPTION WHEN OTHERS THEN V_COUNT := 0;
END;
```

---

### Fix 9 — `HE_VALOR_DUTIL` e `HE_VALOR_DNUTIL` divididos por 100

**Problema:**  
Os factores de percentagem estão guardados como números inteiros (ex: `50` = 50%, `75` = 75%). O cálculo usava-os directamente sem dividir por 100, resultando em valores **100× superiores** ao correcto.  
Exemplo: `salario/8 * 50 * 2 horas` em vez de `salario/8 * 0.50 * 2 horas`.

**Antes:**
```sql
V_VALOR_HORAS := (V_SAL_DIARIO * V_HE_VALOR_DNUTIL * NVL(P_HORAS_DIARIA, 0));
V_VALOR_HORAS := (V_SAL_DIARIO * V_HE_VALOR_DUTIL  * NVL(P_HORAS_DIARIA, 0));
```

**Depois:**
```sql
V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DNUTIL / 100) * NVL(P_HORAS_DIARIA, 0));
V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DUTIL  / 100) * NVL(P_HORAS_DIARIA, 0));
```

---

### Fix 10 — `RETURN` em falta

**Problema:**  
A função não tinha instrução `RETURN`. Uma função PL/SQL que chega ao `END` sem executar `RETURN` lança `ORA-06503: PL/SQL: Function returned without value`. O package compilava como VALID (Oracle não verifica coverage de RETURN em tempo de compilação) mas falhava sempre que era chamada.

**Antes:** *(END sem RETURN)*
```sql
  END LOOP;

    END;
```

**Depois:**
```sql
  END LOOP;

    RETURN NVL(v_valor_total, 0);

    END CALCULO_HORA_EXTRA;
```

---

## Resumo das alterações

| Componente | Tipo | Descrição |
|---|---|---|
| Package Spec | Adição | Declaração pública de `CALCULO_HORA_EXTRA` |
| `GET_SALARIO_BASE` | Fix | `estado='ACT'` → `estado='A'` |
| `CALCULO_HORA_EXTRA` | Fix 1+2 | Ordem de fetch de parâmetros; conversão DIARIA VARCHAR2→NUMBER |
| `CALCULO_HORA_EXTRA` | Fix 3 | `V_DIAS_UTES NUMBER` → `VARCHAR2(1)` |
| `CALCULO_HORA_EXTRA` | Fix 4 | Inicialização dos acumuladores a 0 |
| `CALCULO_HORA_EXTRA` | Fix 5 | Reset de `V_VALOR_HORAS`/`V_COUNT` em cada iteração |
| `CALCULO_HORA_EXTRA` | Fix 6+7 | `SYSDATE` → `v_data`; ano usa `v_data` em vez de `P_DATA_INICIO` |
| `CALCULO_HORA_EXTRA` | Fix 8 | EXCEPTION handler no bloco de feriados |
| `CALCULO_HORA_EXTRA` | Fix 9 | `/100` nos factores de percentagem |
| `CALCULO_HORA_EXTRA` | Fix 10 | `RETURN NVL(v_valor_total, 0)` adicionado |

## Efeito após correcções (teste com Wilson Cabral, tiprel 172772, sal 271.819 CVE)

| Parâmetros | Resultado |
|---|---|
| Abr 14–30, DIAS_UTEIS, 2h/dia | 441.705,88 CVE |
| Abr 14–30, DIAS_UTEIS_NAO_UTEIS, 2h/dia | 645.570,13 CVE |
| Abr 14–30, DIAS_NAO_UTEIS, 2h/dia | 203.864,25 CVE |

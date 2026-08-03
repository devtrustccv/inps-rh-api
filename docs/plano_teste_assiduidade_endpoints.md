# Plano de Teste Manual — Módulo Assiduidade (todos os endpoints)

Cobre os **30 endpoints** de `AssiduidadeController`, verificando (a) que respondem sem
erro e (b) que o resultado obedece à regra de negócio da
*Especificação Técnica Funcional — GESTÃO_ASSIDUIDADE (01/08/2026)*.

- **Base URL:** `http://localhost:8089/api/v1/assiduidade`
- **Autenticação:** não necessária (`SecurityConfig` linha 71 — `permitAll()`)
- **Perfil:** `development`

> Cada caso tem **Verificação em BD** porque um `200 OK` não prova que a regra de
> negócio foi cumprida — a maioria dos defeitos que corrigimos devolvia `200` e
> gravava na tabela errada.

---

## 0. Fixtures (dados reais já preparados em BD)

| Fixture | Valor |
|---|---|
| **Colaborador A** — Wilson Cabral Tavares (**Efetivo**) | `019f6181-1114-70c1-8eb1-44ec5a8f261a` · tiprel `173114` · salário `156.590` |
| **Colaborador B** — Auta Da Costa Semedo OLD (**Contratado**) | `019f8611-6709-7b58-a692-f49bf701da24` · tiprel `173170` · salário `190.336` |
| Responsável (fun uuid) | `019f8611-6709-7b58-a692-f49bf701da24` |
| Direcção / Secção / Ilha / UPS | `100010973` / `12` / `3` / `41` |
| Tipo justificação **com** desconto salarial | `17` — Motivo Pessoal |
| Tipo justificação **sem** desconto salarial | `15` — Falecimento de Familiares |
| Tipo documento JUSTIFICACAO_FALTA / HORA_EXTRA / FERIAS / DISPENSA | `26` / `5` / `8` / `27` |
| Ano de referência | `2026` (ano_id `2`) |
| Sínteses livres do Colaborador A | `573`(27/07) `574`(28/07) `575`(29/07) `576`(01/08) `577`(02/08) `578`(03/08) |
| Período com dados | 2026-07 (43 sínteses) e 2026-08 (70) |

**Direito de férias:** 22 dias/2026 para os 45 colaboradores activos (semeado).

### Valor de falta esperado (para conferir os cálculos)

`valor/hora = salário ÷ 30 ÷ 8`

| Colaborador | Valor/hora | 5h20 de ausência (5,333h) |
|---|---|---|
| A — Wilson | 652,46 | ≈ 3.479,78 |
| B — Auta | 793,07 | ≈ 4.229,71 |

---

## 1. Movimento de Picagem

### T1.1 — Lista de picagens
```bash
curl "http://localhost:8089/api/v1/assiduidade/picagens?pageSize=20&pageNumber=0"
```
**Esperado:** `200`, `content: []`.
⚠️ `RH_MOVIMENTOS` está vazia — a lista **tem de vir vazia, não em erro**. Um `500`
aqui é defeito.

### T1.2 — Lista com filtros
```bash
curl "http://localhost:8089/api/v1/assiduidade/picagens?direcao=100010973&ups=41&dataInicio=2026-07-01&dataFim=2026-08-31"
```
**Esperado:** `200`, vazio. Verifica que os filtros não rebentam com tabela vazia.

### T1.3 — Importar dados do relógio
```bash
curl -X POST "http://localhost:8089/api/v1/assiduidade/picagens/importar?dataInicio=2026-07-01&dataFim=2026-07-31"
```
**Esperado:** `200` com `status: "importado"`.
⚠️ Depende de `INPSRH.IMPORT_DADOS_CONTR_ACESSO` e do relógio externo. O bloco PL/SQL
tem `EXCEPTION WHEN OTHERS THEN NULL` (conforme a spec), por isso **devolve sucesso mesmo
que não importe nada**. Confirmar em BD:
```sql
SELECT COUNT(*) FROM rh_movimentos WHERE TRUNC(dt_movimento) BETWEEN DATE '2026-07-01' AND DATE '2026-07-31';
```

### T1.4 — Importar com datas invertidas
```bash
curl -X POST "http://localhost:8089/api/v1/assiduidade/picagens/importar?dataInicio=2026-07-31&dataFim=2026-07-01"
```
**Esperado:** `400` — "Data fim não pode ser anterior à data início".

---

## 2. Gestão de Falta — lista (movimento-resumos)

### T2.1 — Lista base
```bash
curl "http://localhost:8089/api/v1/assiduidade/movimento-resumos?mes=8&ano=2026"
```
**Esperado:** `200`. Cada linha traz `estado` ∈ {`CONFORME`, `INJUSTIFICADA`,
`JUSTIFICADA`, `PENDENTE`}.
**Regra:** os dois últimos são **novos** — se só aparecerem `CONFORME`/`INJUSTIFICADA`,
a vista não foi actualizada.

### T2.2 — Filtro UPS (novo)
```bash
curl "http://localhost:8089/api/v1/assiduidade/movimento-resumos?ups=41&mes=8&ano=2026"
```
**Esperado:** `200`, só colaboradores da UPS 41. Antes este filtro não existia.

### T2.3 — Filtro por intervalo de datas (novo)
```bash
curl "http://localhost:8089/api/v1/assiduidade/movimento-resumos?dataInicio=2026-07-01&dataFim=2026-08-31"
```
**Esperado:** `200`, meses 07 **e** 08. As datas têm precedência sobre `mes`/`ano`.

### T2.4 — Colaborador ausente não conta falta
**Pré:** garantir uma ausência activa que cubra um dia com síntese.
```sql
SELECT a.fun_id, a.data_inicio, a.data_fim FROM rh_t_ausencia a WHERE a.estado='A';
```
**Esperado:** nesse período o colaborador **não soma faltas** (`totalFaltas` não conta
esses dias) — regra "quem está de férias/missão tem a falta justificada automaticamente".

### T2.5 — Filtros combinados
```bash
curl "http://localhost:8089/api/v1/assiduidade/movimento-resumos?direcao=100010973&seccao=12&ilha=3&estado=INJUSTIFICADA&mes=8&ano=2026"
```
**Esperado:** `200`, resultados coerentes ou lista vazia — nunca erro.

---

## 3. Marcar Falta

### T3.1 — 🔑 Falta curta (2 dias) com desconto salarial → **NÃO** vai a validação
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/falta \
 -H "Content-Type: application/json" -d '{
  "colaboradorId":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
  "dataInicio":"2026-09-01","dataFim":"2026-09-02",
  "totalDeHorasAusentes":"16:00","justificar":"SIM",
  "motivoAusencia":"Teste T3.1 — 2 dias com desconto",
  "tipoJustificacao":17,"deduzirFaltaEm":null
 }'
```
**Esperado:** `200` com `"requerValidacao": false`, `"estado": "A"`, `totalRegistos: 2`,
`valorDiario` ≈ 5.219,67 (8h × 652,46) e `valorTotal` ≈ 10.439,33.

**Verificação em BD:**
```sql
SELECT id, estado, tipo, valor, flg_desconto_falta, def_pag_id FROM rh_t_falta
 WHERE data_inicio >= DATE '2026-09-01' ORDER BY id;
-- estado='A', tipo='FALTA', def_pag_id PREENCHIDO (desconta salário)
SELECT COUNT(*) FROM rh_t_validacao WHERE referencia_name='FALTA' AND estado='P';
-- NÃO deve haver validação nova
SELECT p.tipo, p.valor, p.fun_id, tm.short_desc FROM rh_t_def_pagamentos p
 JOIN rh_tipo_movimentos tm ON tm.id=p.tm_id WHERE p.user_registo_name IS NOT NULL
 ORDER BY p.id DESC FETCH FIRST 3 ROWS ONLY;
-- tipo='FALTA', short_desc='FALTAQ' (Efetivo), fun_id preenchido
SELECT * FROM rh_t_tiprel_rem_pag WHERE pag_id IS NOT NULL ORDER BY id DESC FETCH FIRST 3 ROWS ONLY;
-- associação criada
```

### T3.2 — 🔑 Falta longa (5 dias) com desconto salarial → **VAI** a validação
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/falta \
 -H "Content-Type: application/json" -d '{
  "colaboradorId":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
  "dataInicio":"2026-09-10","dataFim":"2026-09-14",
  "totalDeHorasAusentes":"40:00","justificar":"SIM",
  "motivoAusencia":"Teste T3.2 — 5 dias com desconto","tipoJustificacao":17
 }'
```
**Esperado:** `"requerValidacao": true`, `"estado": "P"`, `totalRegistos: 5`.
**Guardar o `pedidoUuid`** para T3.5.

**BD:** `rh_t_falta.estado='P'`, `def_pag_id` **NULL** (só na validação), e existe
`rh_t_validacao` com `referencia_name='FALTA'`, `estado='P'`.

### T3.3 — 🔑 Falta longa (5 dias) **sem** desconto salarial → **NÃO** vai a validação
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/falta \
 -H "Content-Type: application/json" -d '{
  "colaboradorId":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
  "dataInicio":"2026-09-20","dataFim":"2026-09-24",
  "totalDeHorasAusentes":"40:00","justificar":"SIM",
  "motivoAusencia":"Teste T3.3 — 5 dias sem desconto","tipoJustificacao":15
 }'
```
**Esperado:** `"requerValidacao": false`, `"estado": "A"`.
**Regra crítica:** as duas condições são **cumulativas**. 5 dias sem desconto salarial
não vai a validação. Se for a validação, a regra está mal implementada.

### T3.4 — Falta sem justificação (só síntese)
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/falta \
 -H "Content-Type: application/json" -d '{
  "colaboradorId":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
  "dataInicio":"2026-10-01","dataFim":"2026-10-01",
  "totalDeHorasAusentes":"08:00","justificar":"NAO"
 }'
```
**Esperado:** `200`, `totalRegistos: 1`, **sem** `pedidoId`.
**BD:** cria `rh_assiduidade_sintese_diaria` com `forma='MANUAL'`, `falta=1`,
`horas_trabalhadas='+0 00:00:00'`; **não** cria `rh_t_falta` nem `rh_t_pedido`.

### T3.5 — Validar a falta pendente do T3.2
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/falta/{PEDIDO_UUID_T3.2} \
 -H "Content-Type: application/json" -d '{
  "validar":"SIM","parecer":"FAVORAVEL","observacao":"Aprovado no teste",
  "responsavel":"019f8611-6709-7b58-a692-f49bf701da24","tipoJustificacao":17
 }'
```
**Esperado:** `200`, `estado: "A"`, `totalFaltas: 5`.
**BD:** as 5 faltas passam a `A` e ganham `def_pag_id`; `rh_t_pedido.etapa='FINALIZADO'`;
`rh_t_validacao.estado='A'`; 5 registos em `rh_t_def_pagamentos` com `tipo='FALTA'`.

### T3.6 — Rejeitar uma falta
Repetir T3.2 e validar com `"validar":"NAO"`.
**Esperado:** faltas a `I`, **sem** `def_pag_id`, **sem** registo em `def_pagamentos`.

### T3.7 — Deduzir em FÉRIAS
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/falta \
 -H "Content-Type: application/json" -d '{
  "colaboradorId":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
  "dataInicio":"2026-10-05","dataFim":"2026-10-06",
  "totalDeHorasAusentes":"16:00","justificar":"SIM",
  "motivoAusencia":"Teste T3.7 — deduzir em ferias",
  "tipoJustificacao":15,"deduzirFaltaEm":"FERIAS"
 }'
```
**Esperado:** `200`, estado `A` (tipo 15 não desconta salário).
**BD:** `rh_t_falta.flg_desconto_falta='FERIAS'` e **novo registo em
`rh_t_ferias_gozadas`** (2 dias). Confirmar que o saldo baixou:
```bash
curl "http://localhost:8089/api/v1/assiduidade/feria/saldo/019f6181-1114-70c1-8eb1-44ec5a8f261a?ano=2026"
```

### T3.8 — Deduzir em DISPENSA
Igual ao T3.7 com `"deduzirFaltaEm":"DISPENSA"` e datas 2026-10-08/09.
**BD:** novo registo em `rh_t_dispensa` com as horas de ausência.

### T3.9 — Deduzir em FÉRIAS **sem saldo** → deve falhar
Consumir os 22 dias e tentar de novo.
**Esperado:** `400` — "o colaborador tem N dia(s) por gozar e seriam necessários M".
**Regra:** a spec exige validar o saldo antes de descontar.

### T3.10 — `deduzirFaltaEm` inválido
`"deduzirFaltaEm":"XPTO"` → **Esperado:** `400` — "Valor inválido para 'Deduzir Falta Em'".

### T3.11 — Validações de entrada
| Caso | Esperado |
|---|---|
| sem `colaboradorId` | `400` "Colaborador obrigatório" |
| sem datas | `400` "Intervalo de datas obrigatório" |
| `dataFim` < `dataInicio` | `400` "Data fim não pode ser anterior" |
| `totalDeHorasAusentes` > 23h/dia | `400` "não pode exceder 23h/dia" |
| `colaboradorId` inexistente | `404`/`400`, nunca `500` |

### T3.12 — Obter falta por pedido
```bash
curl http://localhost:8089/api/v1/assiduidade/falta/{PEDIDO_UUID}
```
**Esperado:** `200` com os dados do pedido, incluindo `deduzirFaltaEm`, `valorDiario` e
`valorTotal`.

---

## 4. Justificar Falta (ecrã Resumo de Faltas)

### T4.1 — 🔑 Resumo do mês traz **estado por dia**
```bash
curl "http://localhost:8089/api/v1/assiduidade/falta/justificar/019f6181-1114-70c1-8eb1-44ec5a8f261a?ano=2026&mes=8"
```
**Esperado:** `200`, `itensFalta[]` com `id`, `data`, `horasAusencia` e — **novo** —
`estado` e `estadoDesc`.

**Regra:** dias sem falta registada → `estado: null`, `estadoDesc: "Por justificar"`.
Dias com falta → `P`/`A`/`I` e `Pendente`/`Justificada`/`Rejeitada`, mais `motivo`,
`tipoFalta` e `comJustificativo` preenchidos.
Se `estadoDesc` vier sempre nulo, a correcção não está activa.

### T4.2 — 🔑 Justificar 2 dias com desconto → **não** vai a validação
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/falta/justificar/019f6181-1114-70c1-8eb1-44ec5a8f261a \
 -H "Content-Type: application/json" -d '{
  "tipoJustificacao":17,
  "itensFalta":[
    {"id":573,"selecionar":true,"motivo":"Consulta medica","comJustificativo":"SIM"},
    {"id":574,"selecionar":true,"motivo":"Consulta medica","comJustificativo":"SIM"}
  ],
  "parecerResponsavel":"FAVORAVEL","obsResponsavel":"Teste T4.2"
 }'
```
**Esperado:** `200`, `requerValidacao: false`, `estado: "A"`, `totalRegistos: 2`,
`valorDiario` ≈ 3.479,78, `valorTotal` ≈ 6.959,56.

### T4.3 — 🔑 Justificar 4 dias com desconto → **vai** a validação
Usar `id` 575, 576, 577, 578 com `tipoJustificacao: 17`.
**Esperado:** `requerValidacao: true`, `estado: "P"`. Guardar `pedidoUuid`.

### T4.4 — 🔑 `comJustificativo` é respeitado
Enviar dois itens, um com `"comJustificativo":"SIM"` e outro `"NAO"`.
**BD:**
```sql
SELECT id, flg_justificativo FROM rh_t_falta ORDER BY id DESC FETCH FIRST 2 ROWS ONLY;
```
**Esperado:** valores **diferentes** (`SIM` e `NAO`).
**Regra:** antes gravava sempre `SIM`. Se os dois vierem `SIM`, a correcção não está activa.

### T4.5 — 🔑 Vários documentos
```bash
... -d '{ "tipoJustificacao":15, "itensFalta":[{"id":NNN,"selecionar":true}],
 "documentos":[
   {"tipoDocumentoId":26,"documento":"https://exemplo/doc1.pdf"},
   {"tipoDocumentoId":26,"documento":"https://exemplo/doc2.pdf"}
 ]}'
```
**BD:** `SELECT COUNT(*) FROM rh_t_documento WHERE referencia_name='RH_T_FALTA';` → **2**
novos, com `referencia_id` a apontar para `RH_T_FALTA` (não para o pedido).

### T4.6 — Falta duplicada na mesma síntese
Repetir T4.2 com os mesmos `id`.
**Esperado:** `400` — "Já existe uma falta associada à data ...".

### T4.7 — Nenhum item seleccionado
Todos com `"selecionar": false` → **Esperado:** `400` "Nenhuma falta marcada para justificação".

### T4.8 — Tipo de justificação inválido para falta
`tipoJustificacao: 16` (Férias Anuais, `tipo_falta` nulo) → **Esperado:** `400`
"Tipo justificativo não permitido para falta".

### T4.9 — Obter justificação por pedido
```bash
curl http://localhost:8089/api/v1/assiduidade/falta/justificar/pedido/{PEDIDO_UUID_T4.3}
```
**Esperado:** `200` com os itens e o estado do pedido.

### T4.10 — Validar justificação (T4.3)
```bash
curl -X PUT http://localhost:8089/api/v1/assiduidade/falta/justificar/validar/{PEDIDO_UUID_T4.3} \
 -H "Content-Type: application/json" -d '{
  "validar":"SIM","tipoJustificacao":17,
  "itensFalta":[{"id":575,"selecionar":true},{"id":576,"selecionar":true},
                {"id":577,"selecionar":true},{"id":578,"selecionar":true}],
  "obsResponsavel":"Validado no teste"
 }'
```
**Esperado:** `200`, `estado: "A"`.
**BD:** faltas a `A` com `def_pag_id`; 4 registos em `rh_t_def_pagamentos`
(`tipo='FALTA'`, `FALTAQ`); `rh_t_validacao` a `A`; notificação em `rh_t_notificacao`.

### T4.11 — Lista Falta Justificada / Injustificada
```bash
curl "http://localhost:8089/api/v1/assiduidade/falta?pageSize=20&estado=A&direcao=100010973"
```
**Esperado:** `200`. Colunas: estado, direcção, colaborador, categoria, datas, motivo,
horas ausente, valor a descontar.

---

## 5. Dispensa

### T5.1 — Saldo de horas
```bash
curl "http://localhost:8089/api/v1/assiduidade/dispensa/saldo/019f6181-1114-70c1-8eb1-44ec5a8f261a?data=2026-09-15"
```
**Esperado:** `200` com `horasDisponiveis` / `horasUsadas`.
⚠️ `RH_T_ASSIDUIDADE_PARAMETRO.T_DISPENSA` está a **0** no registo activo — o saldo
disponível será 0 e **qualquer dispensa será recusada**. Se quiseres testar o caminho
feliz, actualizar para (p.ex.) 4 horas.

### T5.2 — Criar dispensa
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/dispensa \
 -H "Content-Type: application/json" -d '{
  "colaborador":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
  "dataDispensa":"2026-09-15","horaSaida":"14:00","horaEntrada":"16:00",
  "tipoMotivo":"PESSOAL","motivo":"Teste T5.2",
  "responsavel":"019f8611-6709-7b58-a692-f49bf701da24"
 }'
```
**Esperado:** `200` com `dispensaId`/`pedidoId`, ou `400` de saldo (ver T5.1).
**BD:** `rh_t_dispensa.total_hora=120` (minutos), estado `P`.
🔑 **`rh_t_validacao` NÃO deve ter registo novo de DISPENSA** — a spec riscou esse passo.

### T5.3 — Lista
```bash
curl "http://localhost:8089/api/v1/assiduidade/dispensa?pageSize=20&estado=P&direcao=100010973"
```

### T5.4 — Obter por id e por pedido
```bash
curl http://localhost:8089/api/v1/assiduidade/dispensa/{DISPENSA_UUID}
curl http://localhost:8089/api/v1/assiduidade/dispensa/{PEDIDO_UUID}/validacao
```

### T5.5 — Editar
```bash
curl -X PUT http://localhost:8089/api/v1/assiduidade/dispensa/{DISPENSA_UUID} \
 -H "Content-Type: application/json" -d '{
  "colaborador":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
  "dataDispensa":"2026-09-16","horaSaida":"09:00","horaEntrada":"11:00","motivo":"Alterado"
 }'
```
**BD:** `total_hora` recalculado; **sem** novo `rh_t_validacao`.

### T5.6 — Validar → cria ausência
```bash
curl -X PUT http://localhost:8089/api/v1/assiduidade/dispensa/{PEDIDO_UUID}/validar \
 -H "Content-Type: application/json" -d '{"validar":"SIM","observacaoRh":"Aprovado"}'
```
**BD:** 🔑 novo `rh_t_ausencia` com `referencia_name='RH_T_DISPENSA'`, `estado='A'` e
`hora` = minutos da dispensa. `rh_t_dispensa.decisao_rh='SIM'`.

### T5.7 — Rejeitar
`"validar":"NAO"` → dispensa a `I`, **sem** ausência criada.

---

## 6. Hora Extra  🔴 *contrato alterado*

### T6.1 — Calcular valor sem gravar
```bash
curl "http://localhost:8089/api/v1/assiduidade/hora-extra/calculo-valor?funcionarioUuid=019f6181-1114-70c1-8eb1-44ec5a8f261a&dataInicio=2026-01-20&dataFim=2026-03-10&percentagemReferente=DIAS_UTEIS_NAO_UTEIS&horasDiaria=2"
```
**Esperado:** `200` com valor > 0.
**Verificar nos logs:** se aparecer `WARN CALCULO_HORA_EXTRA ... a usar cálculo Java`,
o fallback entrou — resultado válido, mas o procedimento Oracle falhou.

### T6.2 — 🔑 Criar hora extra que atravessa 3 meses
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/hora-extra \
 -H "Content-Type: application/json" -d '{
  "horaExtra":[{
    "colaborador":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
    "dataInicio":"2026-01-20","dataFim":"2026-03-10",
    "horasDiaria":2,"percentagemReferente":"DIAS_UTEIS_NAO_UTEIS"
  }]}'
```
**Esperado:** `200`, `totalRegistos: 1`. Guardar `pedidoUuid`.

### T6.3 — 🔑🔑 Lista agrupada por pedido com repartição mensal
```bash
curl "http://localhost:8089/api/v1/assiduidade/hora-extra?pageSize=20"
```
**Esperado — é o teste mais importante do módulo:**
- `content[]` = **uma entrada por PEDIDO** (não por registo)
- `mesesReferencia: ["202601","202602","202603"]`
- `itens[]` com **3 entradas** — uma por mês
- Em cada item: `mes`, `mesDesc` ("Janeiro/2026"), `dataInicio`/`dataFim` **recortadas
  ao mês** (202601 → 20/01 a 31/01), `diasUteis`, `diasNaoUteis`, `valorAcumuladoMes`
- `valorTotal` do pedido = soma dos `valorAcumuladoMes`

**Falha se:** vier uma linha por registo, ou `itens` só tiver 1 entrada, ou faltarem
`diasUteis`/`valorAcumuladoMes`.

### T6.4 — Filtros novos
```bash
curl "http://localhost:8089/api/v1/assiduidade/hora-extra?estado=P"
curl "http://localhost:8089/api/v1/assiduidade/hora-extra?colaborador=Wilson"
curl "http://localhost:8089/api/v1/assiduidade/hora-extra?mes=202602"
```
**Esperado:** os três filtram. `estado`, `colaborador` e `mes` são **novos**.

### T6.5 — 🔑 Datas por sobreposição
```bash
curl "http://localhost:8089/api/v1/assiduidade/hora-extra?dataInicio=2026-02-01&dataFim=2026-02-28"
```
**Esperado:** o pedido de 20/01 a 10/03 **aparece**.
**Regra:** antes usava contenção e este pedido ficava escondido.

### T6.6 — Detalhe do pedido
```bash
curl http://localhost:8089/api/v1/assiduidade/hora-extra/{PEDIDO_UUID}
```
**Esperado:** `{ "horaExtra": [...] }` com **3 entradas** (uma por mês) e os campos
`mes`, `diasUteis`, `diasNaoUteis`, `valorAcumuladoMes`.

### T6.7 — 🔑 Validar → regista remuneração
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/hora-extra/{PEDIDO_UUID} \
 -H "Content-Type: application/json" -d '{"validar":"SIM"}'
```
**BD — verificação crítica (antes não acontecia de todo):**
```sql
SELECT r.id, r.valor, r.estado, r.tipo, r.moeda, r.fun_id, tm.short_desc
  FROM rh_t_def_remuneracoes r JOIN rh_tipo_movimentos tm ON tm.id=r.tm_id
 ORDER BY r.id DESC FETCH FIRST 3 ROWS ONLY;
-- estado='A', tipo='HORA_EXTRA', moeda='CVE', short_desc='HEXT', fun_id preenchido
SELECT * FROM rh_t_tiprel_rem_pag WHERE rem_id IS NOT NULL ORDER BY id DESC FETCH FIRST 3 ROWS ONLY;
SELECT id, estado, def_rem_id FROM rh_t_hora_extra ORDER BY id DESC FETCH FIRST 3 ROWS ONLY;
-- def_rem_id PREENCHIDO
```

### T6.8 — Rejeitar
`"validar":"NAO"` → hora extra a `I`, **sem** registo em `def_remuneracoes`.

### T6.9 — Validações
| Caso | Esperado |
|---|---|
| `horaExtra: []` | `400` "Dados de hora extra ausentes" |
| sem `colaborador` | `400` "Colaborador obrigatório" |
| `dataFim` < `dataInicio` | `400` |
| validar pedido já validado | `400` "já validado ou sem registos pendentes" |

---

## 7. Férias

### T7.1 — Saldo
```bash
curl "http://localhost:8089/api/v1/assiduidade/feria/saldo/019f6181-1114-70c1-8eb1-44ec5a8f261a?ano=2026"
```
**Esperado:** `22` (direito semeado). Se vier `0`, `RH_T_FERIAS` não foi semeada.

### T7.2 — Lista
```bash
curl "http://localhost:8089/api/v1/assiduidade/feria?anoReferente=2026&pageSize=20"
```
**Esperado:** `200` com `totalDireito`, `totalPlaneado`, `totalGozado` (0 quando não há dados).

### T7.3 — Pedido **sem** substituto → fica activo de imediato
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/feria \
 -H "Content-Type: application/json" -d '{
  "colaborador":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
  "dataInicio":"2026-11-02","dataFim":"2026-11-06","numDias":5,
  "obsConvinienciaServico":"Teste T7.3"
 }'
```
**BD:** `rh_t_ferias_gozadas.estado='A'`, pedido `A`/`FINALIZADO`, **sem** validação, e
🔑 **`rh_t_ausencia` criada** com `referencia_name='RH_T_FERIAS_GOZADAS'`.

### T7.4 — Pedido **com** substituto (≥15 dias) → vai a validação
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/feria \
 -H "Content-Type: application/json" -d '{
  "colaborador":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
  "dataInicio":"2026-12-01","dataFim":"2026-12-15","numDias":15,
  "substituidoPor":"019f8611-6709-7b58-a692-f49bf701da24",
  "responsavel":"019f8611-6709-7b58-a692-f49bf701da24"
 }'
```
**BD:** `rh_t_substituicao` criada; férias e pedido a `P`; validação `FERIA` pendente.

### T7.5 — Substituição com < 15 dias → recusa
Igual com `numDias: 5` e substituto → **`400`** "Substituição só aplicável a férias com
duração igual ou superior a 15 dias".

### T7.6 — Sem saldo
`numDias: 30` (> 22) → **`400`** "Funcionario não tem saldo de ferias suficiente".

### T7.7 — Validar
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/feria/{PEDIDO_UUID_T7.4} \
 -H "Content-Type: application/json" -d '{"validar":"SIM","obsValidacao":"Aprovado"}'
```
**BD:** férias a `A`, ausência criada, notificação enviada.

### T7.8 — 🔑 Alterar pedido
```bash
curl -X PUT http://localhost:8089/api/v1/assiduidade/feria/{PEDIDO_UUID_T7.4} \
 -H "Content-Type: application/json" -d '{
  "feria":{"colaborador":"019f6181-1114-70c1-8eb1-44ec5a8f261a",
           "dataInicio":"2026-12-01","dataFim":"2026-12-15","numDias":15,
           "substituidoPor":"019f8611-6709-7b58-a692-f49bf701da24"},
  "novaDataFim":"2026-12-20","motivo":"Teste T7.8 — prolongamento"
 }'
```
**BD — três verificações novas:**
```sql
SELECT id, estado, ferias_gozadas_id, tipo_alteracao, motivo_alteracao FROM rh_t_ferias_gozadas ORDER BY id DESC FETCH FIRST 2 ROWS ONLY;
-- antigo a 'I'; novo com ferias_gozadas_id preenchido e tipo_alteracao='ALTERACAO_DATA'
SELECT id, estado, etapa FROM rh_t_pedido ORDER BY id DESC FETCH FIRST 2 ROWS ONLY;
-- 🔑 pedido ANTERIOR a 'I' (antes ficava activo em paralelo)
SELECT * FROM rh_t_substituicao ORDER BY id DESC FETCH FIRST 2 ROWS ONLY;
-- 🔑 nova substituição criada (antes não era criada na alteração)
SELECT id, estado FROM rh_t_ausencia WHERE referencia_name='RH_T_FERIAS_GOZADAS' ORDER BY id DESC;
-- ausência anterior a 'I'
```

### T7.9 — Obter pedido
```bash
curl http://localhost:8089/api/v1/assiduidade/feria/{PEDIDO_UUID}
```

### T7.10 — Enviar direito por email
```bash
curl -X POST http://localhost:8089/api/v1/assiduidade/feria/{PEDIDO_UUID}/direito-ferias
```
**Esperado:** `200` `{"enviado": true}`. **BD:** registo em `rh_t_notificacao`.
⚠️ Se o colaborador não tiver contacto EMAIL, fica só um `WARN` no log — não é erro.

### T7.11 — Exportar direitos (Excel)
```bash
curl -o direitos.xlsx "http://localhost:8089/api/v1/assiduidade/feria/exportar-direito?anoReferente=2026&direcaoId=100010973"
```
**Esperado:** `200`, ficheiro `.xlsx` válido com CODIGO_DIRECAO, NOME_DIRECAO,
ID_COLABORADOR, NOME_COLABORADOR, TOTAL_DIREITO, TOTAL_DIREITO_ANO.

---

## 8. Mapa de Férias

### T8.1 — Lista
```bash
curl "http://localhost:8089/api/v1/assiduidade/mapa-feria?anoReferente=2026&pageSize=20"
```
**Esperado:** `200`. `RH_T_FERIAS_MAPA` está vazia → lista vazia, **não erro**.

### T8.2 — Ver mapa
```bash
curl "http://localhost:8089/api/v1/assiduidade/mapa-feria-view?ano=2026&direcaoId=100010973"
```

### T8.3 — Detalhe (agendadas + por agendar)
```bash
curl "http://localhost:8089/api/v1/assiduidade/mapa-feria/detalhe?ano=2026&direcao=100010973"
```
**Regra:** "Férias por Agendar" = colaboradores da direcção **sem** registo no mapa.
Como o mapa está vazio, **todos** devem aparecer em "por agendar".

### T8.4 — Exportar mapa
```bash
curl -o mapa.xlsx "http://localhost:8089/api/v1/assiduidade/mapa-feria/exportar?ano=2026&direcao=100010973"
```
**Esperado:** `.xlsx` com duas folhas — "Ferias Agendadas" e "Ferias por Agendar".

---

## 9. Matriz de cobertura

| # | Endpoint | Método | Casos |
|---|---|---|---|
| 1 | `picagens` | GET | T1.1, T1.2 |
| 2 | `picagens/importar` | POST | T1.3, T1.4 |
| 3 | `movimento-resumos` | GET | T2.1–T2.5 |
| 4 | `falta` | POST | T3.1–T3.4, T3.7–T3.11 |
| 5 | `falta/{pedidoId}` | POST | T3.5, T3.6 |
| 6 | `falta` | GET | T4.11 |
| 7 | `falta/{pedidoId}` | GET | T3.12 |
| 8 | `falta/justificar/{funcionarioId}` | GET | T4.1 |
| 9 | `falta/justificar/{funcionarioId}` | POST | T4.2–T4.8 |
| 10 | `falta/justificar/pedido/{pedidoId}` | GET | T4.9 |
| 11 | `falta/justificar/validar/{pedidoId}` | PUT | T4.10 |
| 12 | `dispensa` | GET | T5.3 |
| 13 | `dispensa` | POST | T5.2 |
| 14 | `dispensa/{dispensaId}` | GET | T5.4 |
| 15 | `dispensa/{dispensaId}` | PUT | T5.5 |
| 16 | `dispensa/{pedidoId}/validacao` | GET | T5.4 |
| 17 | `dispensa/{pedidoId}/validar` | PUT | T5.6, T5.7 |
| 18 | `dispensa/saldo/{funcionarioId}` | GET | T5.1 |
| 19 | `hora-extra` | GET | T6.3–T6.5 |
| 20 | `hora-extra` | POST | T6.2, T6.9 |
| 21 | `hora-extra/{pedidoId}` | GET | T6.6 |
| 22 | `hora-extra/{pedidoId}` | POST | T6.7, T6.8 |
| 23 | `hora-extra/calculo-valor` | GET | T6.1 |
| 24 | `feria` | GET | T7.2 |
| 25 | `feria` | POST | T7.3–T7.6 |
| 26 | `feria/{pedidoId}` | GET | T7.9 |
| 27 | `feria/{pedidoId}` | PUT | T7.8 |
| 28 | `feria/{pedidoId}` | POST | T7.7 |
| 29 | `feria/saldo/{funcionarioId}` | GET | T7.1 |
| 30 | `feria/{pedidoId}/direito-ferias` | POST | T7.10 |
| 31 | `feria/exportar-direito` | GET | T7.11 |
| 32 | `mapa-feria` | GET | T8.1 |
| 33 | `mapa-feria-view` | GET | T8.2 |
| 34 | `mapa-feria/detalhe` | GET | T8.3 |
| 35 | `mapa-feria/exportar` | GET | T8.4 |

---

## 10. Limitações conhecidas — não são defeitos do módulo

| Área | Situação |
|---|---|
| **Picagens** | `RH_MOVIMENTOS` vazia e importação depende do relógio externo. Listas vêm vazias por falta de dados. |
| **Dispensa** | `RH_T_ASSIDUIDADE_PARAMETRO.T_DISPENSA = 0` → saldo zero, dispensas recusadas. Actualizar o parâmetro para testar o caminho feliz. |
| **Mapa de férias** | `RH_T_FERIAS_MAPA` vazia; a importação de mapa está "pendente — RH envia modelo" na própria spec. |
| **Valor da falta** | `CALCULO_FALTA_DIARIO` devolve `NULL` (bug no corpo); usa-se o fallback Java, correcto, mas com `WARN` por cálculo. |
| **Processamento salarial** | 🔴 `PROCESSA_FALTA`/`PROCESSA_HORA` usam variáveis de data antes de as atribuir → **nada do que gravarmos chega ao salário** até isso ser corrigido. Ver `docs/sql/assiduidade_ddl_pendente.sql`. |
| **Licença/Baixa Médica** e **Regularização de Contas** | Não implementados neste módulo. |

---

## 11. Limpeza após os testes

```sql
-- Faltas e sínteses criadas pelos testes (datas de Setembro/Outubro 2026)
DELETE FROM rh_t_documento WHERE referencia_name='RH_T_FALTA'
  AND referencia_id IN (SELECT id FROM rh_t_falta WHERE data_inicio >= DATE '2026-09-01');
DELETE FROM rh_t_tiprel_rem_pag WHERE pag_id IN
  (SELECT id FROM rh_t_def_pagamentos WHERE tipo='FALTA' AND data_registo >= TRUNC(SYSDATE));
DELETE FROM rh_t_def_pagamentos WHERE tipo='FALTA' AND data_registo >= TRUNC(SYSDATE);
DELETE FROM rh_t_falta WHERE data_inicio >= DATE '2026-09-01';
DELETE FROM rh_assiduidade_sintese_diaria WHERE forma='MANUAL' AND data >= DATE '2026-09-01';

-- Hora extra
DELETE FROM rh_t_tiprel_rem_pag WHERE rem_id IN
  (SELECT id FROM rh_t_def_remuneracoes WHERE tipo='HORA_EXTRA');
DELETE FROM rh_t_def_remuneracoes WHERE tipo='HORA_EXTRA';
DELETE FROM rh_t_hora_extra;

-- Dispensa / férias / ausências de teste
DELETE FROM rh_t_ausencia WHERE referencia_name IN ('RH_T_DISPENSA','RH_T_FERIAS_GOZADAS');
DELETE FROM rh_t_dispensa;
DELETE FROM rh_t_substituicao WHERE data_inicio >= DATE '2026-11-01';
DELETE FROM rh_t_ferias_gozadas;
DELETE FROM rh_t_validacao WHERE referencia_name IN ('FALTA','JUSTIFICAR_FALTA','HORA_EXTRA','FERIA');
DELETE FROM rh_t_pedido WHERE tipo_pedido IN ('JUSTIFICACAO_FALTA','HORA_EXTRA','DISPENSA','FERIA');

-- Dados de apoio semeados (manter se forem úteis)
-- DELETE FROM rh_t_ferias WHERE user_registo_name='SEED_TESTE';
-- DELETE FROM rh_t_tipos_documentos WHERE user_registo_name='SEED_TESTE';

COMMIT;
```

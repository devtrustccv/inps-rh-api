# Avaliação de Desempenho — alterações de API

## 2026-09-21 — Refactor SEMESTRE → PERIODICIDADE (breaking)

Spec de referência: `docs/Especificação Tecnica Funcional - AVALIAÇÃO DESEMPENHO_21_09_2026.md`
(HTML navegável em `docs/spec_avaliacao_desempenho_21_09_2026.html`).
Plano: `docs/plano_refactor_avaliacao_desempenho_21_09.md`.

> **Breaking.** O eixo temporal deixa de ser `semestre` (`"1"` | `"2"`) e passa a ser
> `periodicidade`, um código do domínio. O ciclo pode agora ser semestral, trimestral ou anual.

### 1. O modelo novo em duas linhas

```
Avaliacao (um colaborador num ano)
 └─ periodos[]           → um por período do ciclo: notas, entrevista, pareceres
     └─ componentes      → objectivos / competências / atitude pessoal
         └─ medição      → realizado, avaliacao, autoRealizado, autoAvaliacao
```

Antes havia **uma avaliação por semestre**. Agora há **uma avaliação por ano**, com N períodos
dentro. Um `uuid` de avaliação que antes identificava "o 1º semestre do João" passa a
identificar "o ano do João" — o período vai à parte, em `periodicidade`.

### 2. Vocabulário da periodicidade

O domínio `PERIODICIDADE` tem dois níveis. Obtém-se em `api/v1/enums` / nos domínios:

| Nível | Onde | Valores |
|---|---|---|
| **Tipo** do ciclo | `REFERENCIA = 'PERIODICIDADE'` | `SEMESTRAL`, `TRIMESTRAL`, `ANUAL` |
| **Período** concreto | `REFERENCIA = <tipo>` | `SEMESTRE1`/`SEMESTRE2`, `TRIMESTRE1`..`TRIMESTRE4`, `ANUAL` |

O **tipo** escolhe-se uma vez, na parametrização do ano. O **período** é o que se envia em
todas as chamadas de avaliação. O peso de cada período na nota do ano está no domínio
`AVD_PONDERACAO_FINAL` (`SEMESTRE1`/`SEMESTRE2` = 50, `TRIMESTRE1..4` = 25, `ANUAL` = 100).

> Enviar um período que não pertence ao tipo parametrizado no ano dá **400** com a lista
> dos valores aceites.

### 3. Parametrização de componentes — `configuracao/avaliacao-desempenho/componentes`

**Campo novo, obrigatório na criação e edição:**

```diff
  {
    "ano": 2027,
+   "periodicidade": "SEMESTRAL",
    "pesoComportamentais": 50,
    ...
  }
```

**`GET` da lista** ganha filtros e dois campos por linha:

| Novo | Onde | Notas |
|---|---|---|
| `?ano=` `?estado=` | query param | opcionais |
| `periodicidade` | cada linha | o tipo do ciclo |
| `versao` | cada linha | |
| `podeInativar` | cada linha | `false` quando já há objectivos definidos no ano — esconder a ação |

A lista passa a vir ordenada por **ano descendente**.

**Dois endpoints novos:**

| Método | Rota | Corpo | Devolve |
|---|---|---|---|
| `POST` | `.../componentes/{id}/clonar` | `{ "ano": 2027, "periodicidade": "SEMESTRAL" }` | `201` `SuccessResponse` |
| `PATCH` | `.../componentes/{id}/inativar` | — | `200` `SuccessResponse` |

`periodicidade` na clonagem é opcional: em branco herda a da origem. Clonar funciona mesmo
com a origem inativa — é a forma normal de reaproveitar o ano anterior. Inativar dá **409**
se já existirem objectivos definidos no ano.

### 4. Definição de objectivos — `POST avaliacao-desempenho/avaliacoes/objectivos`

```diff
  {
    "ano": 2027,
-   "semestre": "1",
+   "periodicidade": "SEMESTRE1",
+   "abrangencia": "INDIVIDUAL",
    "institId": 12,
    "funUuids": ["..."],
    ...
  }
```

**`abrangencia` é nova** e muda o que é obrigatório:

| `abrangencia` | `funUuids` | `institId` | Resultado |
|---|---|---|---|
| `INPS` | ignorado | ignorado | 1 avaliação comum a toda a instituição |
| `DIRECAO` | ignorado | **obrigatório** | 1 avaliação comum à direção |
| `INDIVIDUAL` (default) | **obrigatório** | herdado | 1 avaliação por colaborador |

Em branco assume `INDIVIDUAL`, que é o comportamento anterior.

**A resposta deixou de ser um `Map`:**

```diff
- { "ids": ["uuid-1", "uuid-2"] }
+ { "sucesso": true, "id": "uuid-1,uuid-2", "mensagem": "...", "alertas": [] }
```

Quando um colaborador já tinha definição nesse ano, ele **não é ignorado em silêncio** como
antes: acrescenta-se o período novo à avaliação existente e o nome aparece em `alertas`.

### 5. Avaliação e autoavaliação

`PUT .../processos-avaliacao/{uuid}` e `PUT .../{uuid}/auto-avaliacao` passam a exigir
`periodicidade` **no corpo** (herdado de `BaseAvaliacaoObjetivo`):

```diff
  {
+   "periodicidade": "SEMESTRE1",
    "objectivos": [ ... ],
    ...
  }
```

Nas linhas de cada componente, as notas **mudaram de tipo e ganharam campos calculados**:

```diff
  {
    "numero": 1,
    "ponderacao": 20,
-   "avaliacao": 4,          // Integer
-   "autoAvaliacao": 3,      // Integer
+   "avaliacao": 4.00,       // BigDecimal
+   "autoAvaliacao": 3.00,   // BigDecimal
+   "resultado": 0.80,       // read-only: avaliacao x ponderacao / 100
+   "autoResultado": 0.60    // read-only
  }
```

`resultado` e `autoResultado` são **só de leitura** — enviá-los não tem efeito.

### 6. Observação geral, parecer e comissão executiva

Os três deixaram de ser do ano e passaram a ser **do período**. Ganham um query param
obrigatório:

```diff
- PUT .../processos-avaliacao/{uuid}/observacao-geral
+ PUT .../processos-avaliacao/{uuid}/observacao-geral?periodicidade=SEMESTRE1
```

Idem para `/parecer-colaborador` e `/comissao-executiva`. O corpo não muda.
Os três passam a devolver `SuccessResponse` em vez de `Map`.

### 7. Detalhe da avaliação — `GET avaliacao-desempenho/avaliacoes/{uuid}`

Novo query param **opcional** `?periodicidade=SEMESTRE1`:

- **com** período — as componentes vêm com as medições desse período, e vem `periodo` preenchido
- **sem** período — as componentes vêm **sem notas** (é o que a definição de objectivos precisa)

```diff
  {
-   "semestre": "1",
+   "periodicidade": "SEMESTRE1",
+   "abrangencia": "INDIVIDUAL",
    "objectivos": [ ... ],
-   "observacaoGeral": { ... },
-   "parecerColaborador": { ... },
-   "comissaoExecutiva": { ... }
+   "periodo": {
+     "uuid": "...",
+     "periodicidade": "SEMESTRE1",
+     "descricao": "Semestre 1",
+     "avaliacaoObjectivo": 3.20,
+     "avaliacaoCompetencia": 1.80,
+     "avaliacaoAtitudePessoal": 0.90,
+     "avaliacaoFinal": 5.90,
+     "avaliacaoQualitativa": "Bom",
+     "estado": "A",
+     "observacaoGeral": { ... },
+     "parecerColaborador": { ... },
+     "comissaoExecutiva": { ... }
+   }
  }
```

`periodo` vem `null` enquanto não houver avaliação lançada nesse período.

### 8. Grelha de avaliação — `GET avaliacao-desempenho/avaliacoes`

Filtro `?semestre=` → `?periodicidade=`. A linha passa a ser **pai/filho**:

```diff
  {
    "uuid": "...",
+   "ano": 2027,
+   "abrangencia": "INDIVIDUAL",
    "nomeColaborador": "...",
    "estado": "P",
-   "semestreNota": "1º Sem: 8.5 / 2º Sem: 7.2",
-   "avaliacaoFinalSemestre1": 8.5,
-   "avaliacaoFinalSemestre2": 7.2,
+   "periodos": [
+     { "uuid": "...", "periodicidade": "SEMESTRE1", "descricao": "Semestre 1",
+       "avaliacaoFinal": 8.50, "avaliacaoQualitativa": "Bom", "estado": "A" },
+     { "uuid": "...", "periodicidade": "SEMESTRE2", "descricao": "Semestre 2",
+       "avaliacaoFinal": 7.20, "avaliacaoQualitativa": "Suficiente", "estado": "A" }
+   ],
    "notaFinal": 7.85,
    "notaFinalQualitativa": "Bom"
  }
```

`periodos` vem pela **ordem cronológica do ciclo** — é a ordem para desdobrar as linhas-filho.
`notaFinal` é a soma dos períodos já pesada por `AVD_PONDERACAO_FINAL`.

**`estado`** deixou de ser "semestre 2 concluído ⇒ C": passa a `C` quando **todos** os períodos
do ciclo têm nota, `P` enquanto faltar algum.

### 9. Grelha de definição — `GET avaliacao-desempenho/avaliacoes/objectivos`

Filtro `?semestre=` → `?periodicidade=`, mais `?abrangencia=`. Nas linhas,
`semestre` → `abrangencia`.

### 10. Avaliação final — `GET avaliacao-desempenho/avaliacoes/{uuid}/avaliacao-final`

Os dois campos fixos de semestre deram lugar a uma lista:

```diff
  {
-   "primeiroSemestre": { "avaliacaoFinal": "8.50", "ponderacao": "50" },
-   "segundoSemestre":  { "avaliacaoFinal": "7.20", "ponderacao": "50" },
+   "periodos": [
+     { "periodicidade": "SEMESTRE1", "descricao": "Semestre 1",
+       "avaliacaoFinal": 8.50, "ponderacao": 50, "contributo": 4.25 },
+     { "periodicidade": "SEMESTRE2", "descricao": "Semestre 2",
+       "avaliacaoFinal": 7.20, "ponderacao": 50, "contributo": 3.60 }
+   ],
    "avaliacaoExpressivaQuantitativa": "7.85",
    "avaliacaoExpressivaQualitativa": "Bom"
  }
```

`avaliacaoFinal` e `ponderacao` passaram de `String` para número. Vêm **sempre todos os
períodos do ciclo**, com `avaliacaoFinal: null` nos que ainda não foram avaliados.

### 11. Resumo do que parte

| O que | Antes | Agora |
|---|---|---|
| Campo temporal | `semestre: "1"\|"2"` | `periodicidade: "SEMESTRE1"...` |
| Respostas de escrita | `Map` (`{"id": ...}`) | `SuccessResponse` |
| Notas das componentes | `Integer` | `BigDecimal` + `resultado` |
| Pareceres/observação | à raiz, por ano | dentro de `periodo`, por período |
| Grelha | colunas fixas de semestre | `periodos[]` pai/filho |
| Avaliação final | `primeiroSemestre`/`segundoSemestre` | `periodos[]` |
| `SemestreDTO` | existia | removido → `PonderacaoPeriodoDTO` |

### 12. Enums novos

Expostos pelo enum exposer em `api/v1/enums`:

- `TipoPeriodicidade` — `SEMESTRAL | TRIMESTRAL | ANUAL`
- `AbrangenciaAvaliacao` — `INPS | DIRECAO | INDIVIDUAL`
- `ComponenteAvaliacaoRef` — `OBJECTIVO | COMPETENCIA_COMPORTAMENTAIS | COMPETENCIA_TECNICA | ATITUDE_PESSOAL`
- `TipoProcessoAvaliacao` — `DEFINICAO | AVALIACAO`

---

## Histórico anterior

As entradas anteriores a 2026-09-21 descreviam o modelo por semestre e foram substituídas
por este refactor.

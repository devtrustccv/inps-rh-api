# Relatório de Assiduidade — Regras

## 1. Visão Geral

O relatório de **Assiduidade** permite consultar registos de assiduidade dos colaboradores com base em filtros. O resultado é uma **lista paginada de registos** onde os campos preenchidos dependem do tipo de assiduidade selecionado.

---

## 2. Endpoint

```
GET /api/v1/relatorios/assiduidade
```

- Usa **RequestParam**
- Action: `RelatorioAssiduidadeQuery`

---

## 3. Parâmetros de Entrada — `RelatorioAssiduidadeQuery`

| Parâmetro | Tipo | Obrigatório | Default | Descrição |
|---|---|---|---|---|
| `direccaoId` | `Long` | Não | — | Filtro por direção |
| `seccaoId` | `Long` | Não | — | Filtro por secção |
| `colaborador` | `String` | Não | — | Pesquisa por nome (LIKE) |
| `tipoAssiduidade` | `String` | **Sim** | — | Tipo: `FERIAS`, `FALTA`, `HORA_EXTRA`, `DISPENSA` |
| `dataInicio` | `String` | Não | Data atual | Data início do período |
| `dataFim` | `String` | Não | 31/12 do ano atual | Data fim do período |
| `pageNumber` | `String` | Não | `0` | Número da página |
| `pageSize` | `String` | Não | `20` | Tamanho da página |

---

## 4. DTO de Saída

### `AssiduidadeListDTO`
Estende `PageDTO` — paginação tratada pela classe pai.

| Campo | Tipo | Descrição |
|---|---|---|
| `content` | `List<AssiduidadeRowDTO>` | Lista de registos |

### `AssiduidadeRowDTO`

| Campo | Tipo | Tipo Assiduidade | Descrição |
|---|---|---|---|
| `direccao` | `String` | Todos | Nome da direção |
| `seccao` | `String` | Todos | Nome da secção |
| `colaborador` | `String` | Todos | Nome do colaborador |
| `numDiasFerias` | `Integer` | FERIAS | Nº de dias de férias |
| `periodoFerias` | `String` | FERIAS | Período das férias (mês/ano) |
| `numFaltas` | `Integer` | FALTA | Nº de faltas |
| `numHorasExtras` | `Long` | HORA_EXTRA | Nº de horas extras |
| `horasDispensaGozadas` | `Long` | DISPENSA | Horas de dispensa gozadas |
| `horasDispensaPorGozar` | `Long` | DISPENSA | Horas de dispensa por gozar |

> Os campos não relevantes para o tipo selecionado vêm `null` — o frontend trata de esconder as colunas correspondentes.

---

## 5. Regras de Negócio

### 5.1 Tipo de Assiduidade
- É **obrigatório** — sem ele não é possível pesquisar
- Valores válidos: `FERIAS`, `FALTA`, `HORA_EXTRA`, `DISPENSA`
- Determina quais campos do resultado são preenchidos

### 5.2 Datas
- Se `dataInicio` não for preenchida → default **data atual**
- Se `dataFim` não for preenchida → default **31 de Dezembro do ano atual**
- `dataFim` não pode ser anterior a `dataInicio`
- Os defaults são aplicados no **QueryHandler**

### 5.3 Colaborador
- Pesquisa por **LIKE** — pesquisa parcial por nome
- É opcional

---

## 6. Validações

| Prioridade | Validação | Mensagem |
|---|---|---|
| 🔴 Alta | `tipoAssiduidade` nulo ou vazio | "Tipo de assiduidade é obrigatório" |
| 🔴 Alta | `tipoAssiduidade` valor inválido | "Tipo de assiduidade inválido. Valores aceites: FERIAS, FALTA, HORA_EXTRA, DISPENSA" |
| 🔴 Alta | `dataFim` anterior a `dataInicio` | "Data fim não pode ser anterior à data início" |
| 🟡 Média | `dataInicio` com formato inválido | "Formato de data inválido. Use o formato dd-MM-yyyy" |
| 🟡 Média | `dataFim` com formato inválido | "Formato de data inválido. Use o formato dd-MM-yyyy" |

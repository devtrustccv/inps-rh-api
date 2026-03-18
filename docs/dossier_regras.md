# Dossier do Colaborador — Regras

## 1. Visão Geral

O relatório **Dossier do Colaborador** permite consultar a distribuição de colaboradores com base em filtros e agrupamentos dinâmicos. A métrica é sempre fixa: **número de colaboradores**.

O utilizador define:
- **Filtros** — para restringir o universo de colaboradores
- **Agrupadores** — para organizar o resultado em grupos

O resultado é sempre **agrupado e hierárquico** — nunca lista colaboradores individuais.

---

## 2. Endpoint

```
POST api/v1/relatorios/funcionarios
```

- Action: `obterDossierColaborador`
- Usa **body** — permite enviar objetos complexos e arrays

Handler : ObterDossierColaboradorCommand, ObterDossierColaboradorCommandHandler
---

## 3. DTO de Entrada — `DossierRequestDTO`

```json
{
  "filtros": {
    "sexo": ["Masculino"],
    "idade": ["35"]
  },
  "agrupadores": ["direcao", "seccao"]
}
```

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `filtros` | `Map<String, List<String>>` | Não | Dimensões e valores para restringir o universo |
| `agrupadores` | `List<String>` | Sim | Dimensões para organizar o resultado |

---

## 4. DTO de Saída — `DossierResponseDTO`

```json
{
  "totalGeral": 30,
  "agrupadores": ["direcao", "seccao"],
  "resultado": [
    {
      "dimensao": "direcao",
      "valor": "Direção A",
      "total": 14,
      "subAgrupamentos": [
        {
          "dimensao": "seccao",
          "valor": "Secção X",
          "total": 8,
          "subAgrupamentos": []
        },
        {
          "dimensao": "seccao",
          "valor": "Secção Y",
          "total": 6,
          "subAgrupamentos": []
        }
      ]
    },
    {
      "dimensao": "direcao",
      "valor": "Direção B",
      "total": 16,
      "subAgrupamentos": [
        {
          "dimensao": "seccao",
          "valor": "Secção X",
          "total": 10,
          "subAgrupamentos": []
        },
        {
          "dimensao": "seccao",
          "valor": "Secção Y",
          "total": 6,
          "subAgrupamentos": []
        }
      ]
    }
  ]
}
```

### `DossierResponseDTO`

| Campo | Tipo | Descrição |
|---|---|---|
| `totalGeral` | `Integer` | Total de colaboradores que satisfazem os filtros |
| `agrupadores` | `List<String>` | Dimensões usadas no agrupamento |
| `resultado` | `List<AgrupamentoDTO>` | Resultado hierárquico recursivo |

### `AgrupamentoDTO`

| Campo | Tipo | Descrição |
|---|---|---|
| `dimensao` | `String` | Nome da dimensão (ex: "direcao") |
| `valor` | `String` | Valor do grupo (ex: "Direção A") |
| `total` | `Integer` | Total de colaboradores neste grupo |
| `subAgrupamentos` | `List<AgrupamentoDTO>` | Subníveis do agrupamento (recursivo) |

---

## 5. Regras de Negócio

### 5.1 Métrica
> Sempre fixa — **número de colaboradores**

### 5.2 Filtro
> Restringe o universo de colaboradores que entram no cálculo

- É **opcional** — se não enviado, todos os colaboradores são considerados
- Cada dimensão aceita **um ou múltiplos valores**
- Uma dimensão usada como filtro **não pode ser usada como agrupador** ao mesmo tempo

### 5.3 Agrupador
> Organiza o resultado em grupos com totais

- É **obrigatório** — tem que ter pelo menos 1 agrupador
- Mostra **sempre todos os valores** da dimensão para o universo filtrado
- O número de agrupadores é **livre**
- A **ordem importa** — define a hierarquia do resultado
- Uma dimensão usada como agrupador **não pode ser usada como filtro** ao mesmo tempo

### 5.4 Regra de Exclusão Mútua
> **Uma dimensão não pode ser filtro e agrupador ao mesmo tempo**

- Se está como agrupador → fica bloqueada na lista de filtros
- Se está como filtro → fica bloqueada na lista de agrupadores

### 5.5 Resultado
- A última coluna é sempre **Nº Colaboradores** — fixa
- O resultado é **sempre agrupado** — nunca individual
- **Total geral** sempre presente
- **Subtotais** por cada nível de agrupamento

### 5.6 Validações

| Prioridade | Validação | Mensagem |
|---|---|---|
| 🔴 Alta | Agrupadores nulo ou vazio | "Agrupadores não pode ser vazio" |
| 🔴 Alta | Dimensão inválida nos agrupadores | "Agrupador inválido: '{nome}'" |
| 🔴 Alta | Dimensão inválida nos filtros | "Filtro inválido: '{nome}'" |
| 🔴 Alta | Mesma dimensão em filtros e agrupadores | "A dimensão '{nome}' não pode ser filtro e agrupador ao mesmo tempo" |
| 🟡 Média | Agrupadores repetidos | "Agrupadores não podem ser repetidos" |
| 🟡 Média | Lista de valores de filtro vazia | "Valores do filtro '{nome}' não podem ser vazios" |
| 🟡 Média | Valor de filtro nulo ou vazio | "Valor do filtro '{nome}' não pode ser nulo ou vazio" |
| 🟢 Baixa | `idade` e `faixa_etaria` ao mesmo tempo | "Não é possível usar 'idade' e 'faixa_etaria' ao mesmo tempo" |
| 🟢 Baixa | `carreira` e `estrutura_remuneratoria` ao mesmo tempo | "Não é possível usar 'carreira' e 'estrutura_remuneratoria' ao mesmo tempo" |

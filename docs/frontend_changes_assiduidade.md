# Frontend changes — Assiduidade

Alterações de API do módulo de Assiduidade decorrentes da
*Especificação Técnica Funcional — GESTÃO_ASSIDUIDADE (01/08/2026)*.

> **Estado:** em implementação. As secções marcadas com 🔴 são **breaking** e exigem
> adaptação do frontend antes do deploy.

---

## 🔴 1. Lista de Hora Extra — nova estrutura (breaking)

### Porquê

Hoje `GET /api/v1/assiduidade/hora-extra` devolve **uma linha por registo de hora
extra**. Mas a validação é **por pedido** (`POST hora-extra/{pedidoId}` valida todos
os registos de uma vez). O RH via 8 linhas e só conseguia agir sobre o pedido inteiro
— a lista estava num grão diferente da acção.

Além disso, a especificação exige que um pedido que atravessa vários meses seja
mostrado **repartido por mês**, com dias úteis/não úteis e valor acumulado mensal:

| Data início | Data fim | Mês | Dias úteis | Dias não úteis | % aplicado | Valor diário | Valor acumulado no mês |
|---|---|---|---|---|---|---|---|
| 20/01/2026 | 31/01/2026 | 202601 | 9 | 3 | U=50% / N=100% | U=1.111,11 N=2.222,22 | 14.999,98 |
| 01/02/2026 | 28/02/2026 | 202602 | 20 | 8 | U=50% / N=100% | U=1.111,11 N=2.222,22 | 31.111,08 |
| 01/03/2026 | 10/03/2026 | 202603 | 8 | 2 | U=50% / N=100% | U=1.111,11 N=2.222,22 | 11.111,10 |

A lista passa por isso a ter **dois níveis**: pedido → itens (colaborador × mês).

> Não há dados em produção em `RH_T_HORA_EXTRA`, logo não há migração envolvida.

### `GET /api/v1/assiduidade/hora-extra`

#### Filtros

| Parâmetro | Tipo | Obrig. | Notas |
|---|---|---|---|
| `pageNumber` | int | não | defeito `0` |
| `pageSize` | int | não | defeito `20`. **Pagina sobre pedidos**, não sobre itens |
| `estado` | string | não | **novo** — `P` \| `A` \| `I`. Sem valor = todos |
| `colaborador` | string | não | **novo** (estava desactivado) — pesquisa parcial no nome |
| `funcionarioUuid` | uuid | não | selecção exacta de um colaborador |
| `direcao` | long | não | |
| `seccao` | long | não | |
| `ilha` | long | não | |
| `mes` | string | não | **novo** — `YYYYMM`. Atalho para o mês de referência |
| `dataInicio` | date | não | `YYYY-MM-DD` |
| `dataFim` | date | não | `YYYY-MM-DD` |

**Semântica dos filtros — importante:**

1. **Datas usam sobreposição, não contenção.** Antes era
   `dataInicio >= X AND dataFim <= Y`, o que escondia pedidos que só se cruzavam
   parcialmente com o intervalo. Passa a `dataInicio <= Y AND dataFim >= X`: um
   pedido de 20/01 a 10/03 aparece ao filtrar por Fevereiro.
2. **Filtros de pessoa/estrutura filtram também os itens.** Num pedido com vários
   colaboradores, o pedido aparece se *algum* item corresponder, e `itens` traz
   **apenas as linhas que correspondem**. Por isso há dois contadores:
   `totalColaboradores` (o que passou o filtro) e `totalColaboradoresPedido` (o real).
   `valorTotal` acompanha o filtro; `valorTotalPedido` é o valor íntegro.
3. **Sem filtros devolve tudo**, ordenado por `dataPedido` descendente. Não há
   filtro implícito escondido.

#### Resposta

```jsonc
{
  "content": [
    {
      "pedidoId": 412,
      "pedidoUuid": "01890f3a-...",
      "estado": "P",
      "estadoDesc": "Pendente",
      "etapa": "VALIDACAO",
      "dataPedido": "2026-01-20",

      "periodoInicio": "2026-01-20",
      "periodoFim": "2026-03-10",
      "mesesReferencia": ["202601", "202602", "202603"],

      "direcaoId": 12,
      "direcao": "Direção dos Recursos Humanos",
      "seccaoId": null,
      "seccao": "Várias",

      "totalColaboradores": 2,
      "totalColaboradoresPedido": 3,
      "totalRegistos": 6,
      "valorTotal": 38148.10,
      "valorTotalPedido": 57222.16,

      "itens": [
        {
          "horaExtraId": 88,
          "horaExtraUuid": "01890f3b-...",
          "funcionarioUuid": "7c2a91d4-...",
          "nomeColaborador": "Gertrudes Helena",
          "cargo": "Técnica Superior",

          "direcaoId": 12,
          "direcao": "Direção dos Recursos Humanos",
          "seccaoId": 34,
          "seccao": "Formação",
          "ilhaId": 2,
          "ilha": "Santiago",

          "mes": "202601",
          "mesDesc": "Janeiro/2026",
          "dataInicio": "2026-01-20",
          "dataFim": "2026-01-31",
          "diasUteis": 9,
          "diasNaoUteis": 3,

          "horasContratadaDiaria": "08:00",
          "horasContratadaMensal": "96:00",
          "horasTrabalho": "24:00",

          "salarioMensal": 80000.00,
          "percentagemReferente": "DIAS_UTEIS_NAO_UTEIS",
          "percentagemUtil": 50,
          "percentagemNaoUtil": 75,

          "valorDiario": 1111.11,
          "valorAcumuladoMes": 14999.98,

          "estado": "P",
          "documento": { "tipoDocumentoId": 4, "documento": "https://..." }
        }
        // ... uma entrada por (colaborador × mês)
      ]
    }
  ],
  "totalElements": 37,
  "totalPages": 2,
  "pageNumber": 0,
  "pageSize": 20
}
```

#### Notas de leitura

- `dataInicio`/`dataFim` **do item** vêm recortadas às fronteiras do mês. O período
  integral do pedido está em `periodoInicio`/`periodoFim`.
- `mesesReferencia` é a união dos meses dos itens — serve para chips/badges na linha
  colapsada sem ter de percorrer `itens`.
- `direcao`/`seccao` **do pedido** só vêm preenchidos quando são únicos em todos os
  itens; caso contrário `id = null` e o texto é `"Várias"`. Para filtrar/agrupar,
  usar sempre a direcção **do item**.
- `percentagemUtil`/`percentagemNaoUtil` vêm da parametrização activa
  (`RH_T_ASSIDUIDADE_PARAMETRO.HE_VALOR_DUTIL` / `HE_VALOR_DNUTIL`), não do registo.
- `valorAcumuladoMes` já é o somatório do mês. **Não multiplicar por nada no front.**
- ⚠️ `horasContratadaMensal` = `horasDiárias × 12`, tal como a especificação
  determina. Fica a nota de que 12 é pouco intuitivo para um valor "mensal" —
  a confirmar com o RH.

### `GET /api/v1/assiduidade/hora-extra/{pedidoId}` — mantido

Continua a existir e **não muda de forma** (`{ "horaExtra": [...] }`). Ganha os
mesmos campos mensais dos `itens` acima (`mes`, `dataInicio`/`dataFim` recortadas,
`diasUteis`, `diasNaoUteis`, `valorAcumuladoMes`, `percentagemUtil`,
`percentagemNaoUtil`).

Assim o front tem as duas opções: expandir a linha com o que já veio na lista, ou ir
buscar o detalhe fresco — recomendado no ecrã de validação, onde o dado deve estar
actualizado no momento em que se assina.

---

## 🔴 2. Resumo de Faltas — estado por dia (breaking por omissão)

`GET /api/v1/assiduidade/falta/justificar/{funcionarioId}?ano=&mes=`

O ecrã de resumo mostra o estado de cada dia (Pendente / Justificada / Rejeitada), mas
a API devolvia esses campos **sempre nulos** — nunca ligava a síntese diária à falta
registada. Corrigido. Cada entrada de `itensFalta` passa a trazer:

| Campo | Valores |
|---|---|
| `estado` | `P` \| `A` \| `I` \| `null` (dia ainda sem pedido) |
| `estadoDesc` | `Pendente` \| `Justificada` \| `Rejeitada` \| `Por justificar` |

E passam também a vir preenchidos, quando existe falta registada para o dia: `motivo`,
`comJustificativo`, `tipoFalta` e `valorAusencia`.

> `I` lê-se **Rejeitada** neste ecrã: o estado inactivo resulta de o RH ter recusado a
> justificação.

## 3. Justificar Faltas Selecionadas — vários documentos

O formulário permite "Adicionar outro documento", mas o DTO só aceitava **um documento
por falta**. `JustificarFaltaDTO` ganha:

```jsonc
{
  "documentos": [                        // novo — aplica-se a todas as faltas seleccionadas
    { "tipoDocumentoId": 4, "documento": "https://..." },
    { "tipoDocumentoId": 7, "documento": "https://..." }
  ],
  "itensFalta": [
    { "id": 91, "selecionar": true, "documento": { … } }   // continua a servir o anexo de um dia
  ]
}
```

## 4. Falta — campos novos

### `POST /falta` · `POST /falta/{pedidoId}` · `POST /falta/justificar/{funcionarioId}` · `PUT /falta/justificar/validar/{pedidoId}`

#### Pedido (novos campos)

| Campo | Tipo | Notas |
|---|---|---|
| `deduzirFaltaEm` | string | **novo** — `FERIAS` \| `DISPENSA`. Domínio `TP_DESCONTO_FALTA`. Grava em `RH_T_FALTA.FLG_DESCONTO_FALTA` |

Antes o destino do desconto era **inferido** do tipo de justificação. Passa a ser
escolha explícita do RH. Só é lido quando o tipo de justificação implica desconto.

Em `justificarFalta`, o campo `comJustificativo` de cada item de `itensFalta` passa a
ser **respeitado** — antes era ignorado e assumido `"SIM"` para todos.

#### Resposta (novos campos)

| Campo | Tipo | Notas |
|---|---|---|
| `valorDiario` | decimal | Valor por dia de falta |
| `valorTotal` | decimal | `valorDiario × totalDias` |

### 🔴 Regra nova de encaminhamento para validação

> Só vai a validação se o número de faltas for **superior a 3 dias** **e** o tipo de
> justificação estiver sujeito a desconto no salário
> (`RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL = 1`). Caso contrário fica logo **ATIVO**.

Impacto no front: a resposta de `POST /falta` e `POST /falta/justificar/{funcionarioId}`
passa a trazer o estado resultante, que pode ser `A` (finalizado, **sem** passo de
validação) ou `P` (aguarda validação). O ecrã não deve assumir que há sempre validação
a seguir.

```jsonc
{
  "pedidoId": 512,
  "pedidoUuid": "0189...",
  "estado": "A",
  "requerValidacao": false,   // novo — evita o front ter de reimplementar a regra
  "totalRegistos": 2
}
```

---

## 5. Dispensa — sem registo de validação

Deixa de ser criado registo em `RH_T_VALIDACAO` para dispensa (a especificação riscou
esse passo). Sem impacto no contrato; muda apenas o que aparece nas listas de
pendências de validação.

---

## 6. Lista de Gestão de Falta — filtros novos

`GET /api/v1/assiduidade/movimento-resumos` ganha:

| Parâmetro | Tipo | Notas |
|---|---|---|
| `ups` | long | **novo** — exigido pela especificação |
| `dataInicio` | date | **novo** — alternativa a `mes`/`ano` |
| `dataFim` | date | **novo** |

E o campo `estado` passa a admitir `JUSTIFICADA` e `PENDENTE`, além dos actuais
`CONFORME` e `INJUSTIFICADA`.

---

## Notas técnicas (contexto, sem impacto directo no front)

- `dataPedido` é servido a partir de `RH_T_PEDIDO.DATA_REGISTO` — a coluna
  `DATA_PEDIDO` referida na especificação não existe na base de dados.
- O cálculo do valor de falta usa `RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_FALTA_DIARIO`
  quando disponível, com **fallback automático** para cálculo equivalente em Java
  (`salário base ÷ 30 ÷ jornada diária`) e registo em log quando o procedimento falha.
  O mesmo padrão de fallback é aplicado a `CALCULO_HORA_EXTRA`. O resultado é o mesmo
  em ambos os caminhos — o front não distingue.

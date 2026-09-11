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

---

## 🔴 7. Justificar Falta — cabeçalho do pedido e anexos de grupo (09/09/2026)

### Porquê

O ecrã de Justificar Falta ganhou as acções **Editar** e **Eliminar** por grupo
(spec 09/09, *"agrupados por `RH_T_FALTA.PEDIDO_ID`"*). Para o Editar reabrir o
formulário era preciso que a leitura devolvesse o que lá tinha sido gravado — e não
devolvia: o cabeçalho vinha quase todo a `null`, e o próprio `pedidoId` não existia
no contrato.

### `GET /api/v1/assiduidade/falta/justificar/pedido/{pedidoUuid}`

Campos **novos ou que deixaram de vir `null`** no cabeçalho:

| Campo | Origem | Notas |
|---|---|---|
| `pedidoId` | `RH_T_PEDIDO.UUID` | **novo** — chave para o Editar/Eliminar do grupo |
| `comJustificativo` | `RH_T_FALTA.FLG_JUSTIFICATIVO` | **novo** — repõe o radio obrigatório do formulário |
| `deduzirFaltaEm` | `RH_T_FALTA.FLG_DESCONTO_FALTA` | estava gravado mas não era lido |
| `valorDiario` | `RH_T_FALTA.VALOR` da 1.ª falta | |
| `valorTotal` | soma do `VALOR` das faltas do pedido | igual ao que o POST devolve |
| `ano` / `mes` | data mais antiga do pedido | permite voltar à lista do mês certo |
| `documentos` | anexos do pedido | ver ponto seguinte |

O `GET .../falta/justificar/{funcionarioUuid}?ano=&mes=` **não muda**: aí o cabeçalho
não representa um pedido (o mês pode conter vários), pelo que continua vazio.

### Anexos do bloco "Justificar Faltas Selecionadas"

Passam a ser gravados com `REFERENCIA_NAME = 'RH_T_PEDIDO'` e `REFERENCIA_ID/UUID` do
pedido, em vez de presos à primeira falta do grupo. **Diverge da especificação**
(que diz `'RH_T_FALTA'`), por decisão de negócio: o anexo aplica-se a todas as faltas
seleccionadas, e prendê-lo a um dia tornava-o indistinguível do anexo desse dia — a
leitura devolvia um e escondia o outro, e eliminar esse dia deixava-o órfão.

O anexo **de um dia** (`itensFalta[].documento`) mantém-se em `'RH_T_FALTA'`.

> Sem migração: `RH_T_DOCUMENTO` não tinha nenhum anexo de falta gravado.

### `POST /api/v1/assiduidade/falta/justificar/{funcionarioUuid}` e `PUT .../validar/{pedidoUuid}`

`responsavelId` deixa de ser ignorado — é gravado em `RH_T_FALTA.RESPONSAVEL_ID`.
Espera a **PK de `RH_T_RESPONSAVEL`**, a mesma que a leitura devolve. Na validação só
sobrepõe o responsável se vier no payload.

## 🔴 8. Marcar Falta — `despachoRh` removido, `tipoJustificacao` obrigatório

`POST /api/v1/assiduidade/falta` e `POST .../falta/{pedidoUuid}`:

- **`despachoRh` foi removido do contrato** (`FaltaReqDTO`). O formulário Marcar Falta /
  Ausência não tem esse campo em nenhum dos modos (spec 09/09, ecrã 3.2.2 — só Parecer,
  Responsável e Observação). Deixa também de ser devolvido na leitura da falta.
- **`tipoJustificacao` passa a ser obrigatório** quando `justificar = "SIM"` → `400`
  *"Tipo de falta é obrigatório quando a falta é marcada com justificativo"*. Sem ele o
  `PARAM_SIT_ID` ficava nulo e a regra dos 3 dias nunca disparava: uma falta longa
  entrava directamente a `A`, saltando o maker-checker.

### Notas para o produto (sem alteração de contrato)

- `RH_T_FALTA.DESPACHO_RH` é `VARCHAR2(3)`, mas o domínio que a documentação de base de
  dados lhe atribui (`PARECER_DECISAO` / referência `DESPACHO_RH` = `JUSTIFICADA` |
  `INJUSTIFICADA`) **não cabe na coluna**. O campo continua no contrato do ecrã de
  Justificar; enviar um valor do domínio dá erro de base de dados. Precisa de decisão:
  alargar a coluna ou retirar o campo.
- O domínio `PARECER_DECISAO` só tem um registo em dev (`VALOR='TETS'`), sem
  `FAVORAVEL`/`DESFAVORAVEL`. `parecer` continua **texto livre** no backend — a
  parametrização e a validação da lista são responsabilidade do cliente.
- Uma falta com tipo que desconta salário **e** `deduzirFaltaEm` preenchido já **não**
  aplica os dois efeitos por inteiro: o saldo cobre o que consegue e só o que sobrar vai
  ao vencimento. Ver a secção 9. *(Esta nota perguntava se o duplo efeito era intencional;
  o negócio decidiu a 10/09 que não.)*


---

## 🔴 9. Regra de desconto da falta — o saldo cobre o que consegue (10/09/2026)

Decisão de negócio de 10/09. **O saldo e o vencimento não são alternativas — são duas fases
da mesma cobrança.** Uma falta de 4 dias com 2 dias de saldo de férias dá 2 dias gozados e
2 dias descontados no vencimento. A cobertura faz-se pelos **primeiros dias** (ordem
cronológica); na dispensa, que conta em horas, pode ser **parcial** — 8h de ausência com 4h
de saldo consomem as 4h e descontam o valor das outras 4h.

### O `400` de saldo insuficiente desapareceu

`POST /falta/justificar/{funcionarioId}` e `PUT /falta/justificar/validar/{pedidoId}`
**deixaram de rejeitar** um pedido cujo saldo não cobre o período. Antes:

```json
{ "status": 400, "title": "Funcionario não tem saldo de ferias suficiente" }
```

Agora o pedido passa sempre, e a parte não coberta gera desconto no vencimento. **Se o
frontend trata esse 400 com uma mensagem própria, esse ramo passa a ser código morto.**

### `estado` pode vir `P` onde antes vinha `A`

A regra dos "mais de 3 dias vai a despacho" passou a contar-se **por mês**, e não por
pedido: às faltas do pedido novo somam-se as que o colaborador já tem vivas (`A` ou `P`)
nesse mês. Registar 2 dias hoje e 2 amanhã já não escapa ao maker-checker.

Consequência prática: **o mesmo payload pode devolver `A` ou `P` consoante o histórico do
mês**. O ecrã não pode assumir que um pedido pequeno fica logo activo.

```jsonc
{ "estado": "P", "requerValidacao": true, "pedidoId": 225, "pedidoUuid": "01a08c75-…" }
```

Sem retroactividade: só o pedido novo vai a despacho, os anteriores ficam como estavam.

### `valorAusencia` passou a decimal

`FaltaItemDTO.valorAusencia` era `Integer` e truncava os cêntimos (`6344` em vez de
`6344.56`). É agora `BigDecimal`. **O front tem de formatar casas decimais** — o mesmo se
aplica a `valorDiario` e `valorTotal` da resposta.

---

## 🔴 10. Editar e Eliminar pedido de justificação — endpoints novos

Dois endpoints novos, que agem sobre o **pedido inteiro** (todos os dias do grupo):

| Método | Rota |
|---|---|
| `PUT` | `/api/v1/assiduidade/falta/justificar/pedido/{pedidoUuid}` |
| `DELETE` | `/api/v1/assiduidade/falta/justificar/pedido/{pedidoUuid}` |

- O `PUT` tem **corpo próprio** (`EditarPedidoJustificacaoDTO`) — **já não é o
  `JustificarFaltaDTO`** do registo, e **não tem `itensFalta`**:

  ```jsonc
  {
    "motivo": "…",              "comJustificativo": "SIM",
    "tipoJustificacao": 18,     "deduzirFaltaEm": "FERIAS",
    "parecerResponsavel": "…",  "responsavelId": 23,  "obsResponsavel": "…",
    "documentos": [ … ]         // null preserva, sem id cria, omitido fica 'E'
  }
  ```

  **O editar mexe no pedido, não na composição dele.** Os dias que o compõem descobrem-se
  pelo próprio pedido — tal como no Eliminar — e **mantêm-se todos**. Para deixar cair um
  dia, elimina-se o pedido e voltam a justificar-se os dias certos.

  > **Correcção de comportamento (10/09).** Numa versão anterior o `PUT` partilhava o DTO do
  > registo e um dia omitido do `itensFalta` era **retirado** do pedido (`ESTADO='E'`) com o
  > desconto revertido. Como o `FaltaItemDTO` tem `selecionar`, um ecrã que enviasse só as
  > linhas marcadas apagava silenciosamente as restantes faltas e mexia em dinheiro sem erro
  > à vista. Um `itensFalta` que ainda venha no corpo é **aceite e ignorado**, para o
  > frontend poder ser adaptado sem pressa.
- **Editar não volta a validação** — grava directamente, e reverte e reaplica os efeitos
  financeiros em vez de os actualizar (trocar `FERIAS` por `DISPENSA` deixava as férias
  gozadas lá e criava a dispensa por cima). Cada edição cria linhas de desconto **novas** em
  `RH_T_DEF_REMUNERACOES` e põe as anteriores a `E`; é intencional, para o histórico
  financeiro não ser reescrito por cima.
- **Eliminar é soft-delete** (`ESTADO='E'`) e **desfaz os efeitos financeiros** — sem isso o
  colaborador ficava descontado por uma falta que já não existe.

### `400` novo — falta já processada em folha

Os dois devolvem `400` quando **pelo menos uma** falta do pedido já foi apanhada por uma
remuneração activa:

```json
{ "status": 400,
  "title": "Não é possível editar este pedido: 1 falta(s) já foram processadas em folha." }
```

(Com `eliminar` no lugar de `editar`, conforme a operação.) Uma remuneração **anulada** não
bloqueia. Uma falta coberta a 100% por férias ou dispensa nunca bloqueia — não tocou na
folha, só consumiu saldo.

---

## 🔴 11. Saldos passam a reservar as faltas pendentes (10/09/2026)

Uma falta ainda **por despachar** (`P`) que tenciona deduzir em férias ou dispensa passa a
**reservar** esse saldo. Antes, dois pedidos pendentes viam ambos o saldo cheio e o segundo
a ser despachado descobria que já não havia nada — o desconto ia todo ao vencimento sem
ninguém ter sido avisado no registo.

**O número muda sem o utilizador ter feito nada de novo** — é a alteração desta lista com
maior probabilidade de ser reportada como bug.

### `GET /api/v1/assiduidade/feria/saldo/{funcionarioId}`

O `saldo` vem agora **líquido das reservas**. Colaborador com direito a 2 dias e um pedido
de 4 dias pendente com `deduzirFaltaEm: "FERIAS"`:

```jsonc
{ "funcionarioUuid": "01a085fa-…", "anoReferencia": null, "saldo": 0 }   // antes: 2
```

A reserva desaparece sozinha se o pedido for rejeitado ou eliminado. O contrato não muda —
muda o valor.

### `GET /api/v1/assiduidade/dispensa/saldo/{funcionarioId}?data=YYYY-MM-DD`

Dois campos **novos** (aditivos):

| Campo | Notas |
|---|---|
| `horasReservadas` | `HH:MM` comprometidas por faltas pendentes |
| `horasReservadasMinutos` | o mesmo em minutos |

`horasUsadas` continua a ser **só o já consumido** por dispensas aprovadas; `horasRestantes`
vem **líquido das duas**. Reserva-se a ausência inteira do dia — é o máximo que a falta pode
vir a consumir.

```jsonc
{ "horasDisponiveis": "04:00", "horasUsadas": "00:00",
  "horasReservadas": "32:00",  "horasRestantes": "00:00",
  "horasDisponiveisMinutos": 240, "horasUsadasMinutos": 0,
  "horasReservadasMinutos": 1920, "horasRestantesMinutos": 0 }
```

### Mensagem de erro da dispensa

O `400` de horas insuficientes (`POST /dispensa`, `PUT /dispensa/{id}`, validação) passa a
mencionar as reservadas:

> Horas de dispensa insuficientes: o colaborador tem direito a 04:00 por mês, já usou 00:00,
> tem 32:00 reservadas por faltas pendentes e está a pedir 02:00 (total 34:00).

**Se o frontend faz *match* no texto da mensagem, esse match parte.**

---

## 🔴 12. Regra dos 3 dias deixa de acumular o mês (11/09/2026)

A decisão de um pedido ir a despacho conta agora **os dias desse registo**, não as faltas vivas
do mês inteiro. É o que a spec diz (`:494`, `:796`): *"somente deve ir para validação caso o
número de falta for maior que 3 dias"*, no contexto de *"o número de registo na tabela
RH_T_FALTA dependerá do número de dias de falta"*.

A acumulação mensal tinha sido acrescentada a 10/09 e **não tem suporte na spec**.

**O que muda no ecrã:** 2 dias hoje e 2 dias amanhã, em pedidos separados, ficam agora **ambos
activos**. Antes o segundo ia a despacho. O `requerValidacao` da resposta passa a ser `false`
nesses casos.

As duas condições continuam cumulativas: **mais de 3 dias** no registo **e** tipo de
justificação que desconta salário (`RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL`).

---

## 🔴 13. Tipo de falta: o backend recusa o que o combo não oferece (11/09/2026)

O guard do tipo de justificação exigia `TIPO_FALTA IS NOT NULL`, que é a **classificação** da
falta e não "serve para faltas". Deixava passar as duas dispensas, a suspensão disciplinar e uma
parametrização inactiva — **três delas a descontar salário**. O ecrã de Marcar Falta não validava
nada: aceitava "Férias Anuais" ou "Baixa médica" como tipo de falta.

### Como o combo deve ser filtrado

```
GET /api/v1/parametrizacao/param-situacoes/ativos?flgAusencia=1&tipoAusencia=FALTA
```

**Não usar `tipoFalta`** neste filtro — traz dispensas e a suspensão disciplinar à mistura.
É o mesmo par que férias (`tipoAusencia=FERIAS`) e dispensa (`tipoAusencia=DISPENSA`) já usam.

Devolve 7 tipos activos: 15 Falecimento de Familiares, 17 Motivo Pessoal, 18 Falta
Injustificada, 20 Doença do Trabalhador, 39 Isolamento Profilático, 40 Maternidade,
41 Licença Paternidade.

O endpoint passou também a filtrar `ESTADO='A'` por si próprio — **nenhum combo servido por ele
volta a trazer parametrizações desactivadas**.

### `400` novo

Enviar um `tipoJustificacao` fora dessa lista, no registo **ou** no despacho, dá:

> Tipo de falta inválido: `<nome>` não é um tipo de falta activo.

Nenhuma falta gravada usa um tipo que o guard novo recuse (verificado em BD).

### Marcar sem justificativo

Com `justificar: "NAO"` o `tipoJustificacao` deixou de ser validado — não é usado (não nasce
falta nem pedido, só a síntese diária). A spec (`:355`) diz que esses campos nem aparecem nesse
caso.

---

## 🔴 14. `deduzirFaltaEm` no editar: vazio agora **limpa** (11/09/2026)

`PUT /api/v1/assiduidade/falta/justificar/pedido/{pedidoUuid}`

| Payload | Antes | Agora |
|---|---|---|
| `"FERIAS"` / `"DISPENSA"` | troca | troca |
| ausente / `null` / `""` | **preservava** o valor gravado | **limpa a dedução** |
| `"NENHUM"` | limpava | **400** — não existe no domínio |

O ecrã de edição envia o estado completo do formulário: um combo vazio é o utilizador a
**retirar** a dedução, não um campo por preencher. E `NENHUM` não existe em
`TP_DESCONTO_FALTA` — era uma sentinela nossa.

**O formulário tem de reenviar `deduzirFaltaEm` sempre que o quiser manter.**

---

## 🔴 15. Despacho decide o pedido inteiro — as checkboxes deixam de ter efeito (11/09/2026)

`PUT /api/v1/assiduidade/falta/justificar/validar/{pedidoUuid}`

`SIM` aprova **todos** os dias do pedido, `NAO` rejeita **todos**. O `itensFalta` continua a ser
aceite no corpo, por compatibilidade, mas **deixou de comandar**: o `selecionar` de cada item é
ignorado e um array truncado já não tem efeito destrutivo.

**Antes:** um dia não marcado ficava **órfão em `P`** — o pedido ia a `A`/`I`, a etapa a
`FINALIZADO` e a validação fechava, mas o dia ficava pendente para sempre, invisível em todos os
ecrãs e a reservar saldo. Um payload com tudo a `false` órfãos os dias todos, sem erro nenhum.

**O ecrã de validação deve deixar de mostrar as checkboxes** — prometem uma escolha que não
existe. O checker que discorde de um dia rejeita o pedido e o maker volta a justificar os dias
certos, que é o caminho que a spec prevê (`:655-668`).

### `400` novo — despacho repetido

> Só é possível despachar um pedido de justificação pendente de validação.

Dado quando o pedido não está em `P` **ou** já não tem linha pendente em `RH_T_VALIDACAO`.
Antes, dois `PUT` seguidos criavam `DEF_REMUNERACOES` **a dobrar** — dinheiro a mais no
vencimento. **Um frontend que reenvie despachos idempotentes parte aqui.**

### Resposta enriquecida

```jsonc
{ "pedidoId": 244, "pedidoUuid": "…", "estado": "A",
  "etapa": "FINALIZADO",      // novo
  "totalRegistos": 4 }        // novo — dias decididos, não itens enviados
```

---

## 🔴 16. `valorDescontado` e `valorCoberto` — quanto saiu mesmo do vencimento (11/09/2026)

`valorTotal` é o **bruto** da ausência (valor diário × dias), como a spec define. Mas com dedução
em férias ou dispensa o saldo cobre parte e só o resto vai ao vencimento — e o ecrã mostrava
sempre o bruto. Um pedido de 25 378,24 podia ter descontado 22 205,96 sem ninguém ver.

Dois campos novos em **`GET falta/justificar/pedido/{pedidoUuid}`**, em cada elemento de
`pedidos` do **`GET falta/justificar/{funcionarioId}`**, e em **`GET falta/{pedidoId}`**:

| Campo | Significado |
|---|---|
| `valorDescontado` | soma das `RH_T_DEF_REMUNERACOES` vivas — o que saiu do vencimento |
| `valorCoberto` | o que o **saldo** (férias/dispensa) absorveu |

Ambos a **zero** enquanto o pedido não for despachado, e ambos com 2 casas decimais.

**`valorCoberto` não é `valorTotal - valorDescontado`.** É somado por dia e só conta os dias cujo
tipo desconta salário: um tipo que **não** desconta nunca ia cobrar nada, logo nada foi coberto —
a subtracção dava o bruto inteiro e um pedido de "Doença do Trabalhador" aparecia com 12 689,12
cobertos por um saldo que nunca foi tocado.

Exemplos reais (6 344,56/dia):

| Cenário | `valorTotal` | `valorDescontado` | `valorCoberto` |
|---|---|---|---|
| 4 dias, tipo desconta, férias com 2 dias de saldo | 25 378,24 | 12 689,12 | 12 689,12 |
| 4 dias, tipo desconta, dispensa com 4h de saldo | 25 378,24 | 22 205,96 | 3 172,28 |
| 4 dias, tipo desconta, saldo esgotado | 25 378,24 | 25 378,24 | 0,00 |
| 2 dias, **tipo que não desconta salário** | 12 689,12 | 0,00 | **0,00** |

Na última linha `valorTotal` continua a mostrar o bruto da ausência — é o que a spec define para
esse campo —, mas nem um cêntimo foi cobrado nem coberto.

---

## 🔴 17. `GET falta/{pedidoId}` devolvia campos vazios que já tinha gravados (11/09/2026)

O ecrã que o checker abre para despachar uma **marcação de falta** perdia quatro campos:

| Campo | Antes | Agora |
|---|---|---|
| `deduzirFaltaEm` | **`null`** | `"FERIAS"` / `"DISPENSA"` |
| `valorDiario` | `null` | valor do dia |
| `valorTotal` | `null` | soma dos dias |
| `totalDeHorasAusentes` | `"0 8:0:0.0"` — intervalo Oracle cru de **um** dia | **`"32:00"`** — total do período |

O `deduzirFaltaEm` era o mais caro: o despacho aplica o que o formulário enviar, logo um combo
carregado a `null` reenviava `null` e **a dedução em férias desaparecia no despacho**, com os
dias a irem todos a desconto no salário.

**`totalDeHorasAusentes` mudou de formato** — é agora `HH:MM` e reenviável tal e qual no `POST`.

`validar` e `tipoOrdemServico` continuam `null`: não são lidos de volta (decisão de negócio).

---

## 🔴 18. Saldo de férias devolve as parcelas (11/09/2026)

`GET /api/v1/assiduidade/feria/saldo/{funcionarioId}[?ano=]`

Um `saldo: 0` não distinguia *"não tem direito"* de *"já gastou tudo"*, e o `anoReferencia`
limitava-se a ecoar o parâmetro de entrada — vinha `null` sem explicação.

```jsonc
{ "funcionarioUuid": "…",
  "anoReferencia": null,        // 2026 quando se passa ?ano=
  "ambito": "ACUMULADO",        // novo — ANUAL quando se passa ?ano=
  "direito": 2,                 // novo
  "gozado": 2,                  // novo
  "reservado": 0,               // novo — faltas em P que tencionam deduzir em férias
  "saldo": 0 }
```

`anoReferencia: null` significa **acumulado de todos os anos**, que é o que o cálculo faz quando
não se pede ano — é para isso que serve o `ambito`. **O valor de `saldo` não mudou.**

---

## 🔴 19. Resumo mensal de assiduidade — contadores e um falso positivo corrigido (11/09/2026)

`GET /api/v1/assiduidade/movimento-resumos`

**`totalFalta` não é "total de faltas do mês"** — é **dias por justificar** (sem registo em
`RH_T_FALTA`). Com as faltas todas registadas dá `0`, o que parecia um bug. Dois campos novos
explicam o resto:

```jsonc
{ "totalFalta": 0,                  // por justificar
  "totalFaltasPendentes": 0,        // novo — registadas, à espera de despacho
  "totalFaltasJustificadas": 4 }    // novo — registadas e despachadas
```

**Correcção na vista `RH_V_RESUMO_ASSIDUIDADE`:** o `totalFalta` contava **todos** os dias sem
registo em `RH_T_FALTA`, sem verificar se o dia era sequer uma falta — e um dia normal de
trabalho também não tem essa linha. Um colaborador com um mês de picagens normais aparecia com
*"N faltas injustificadas"* e `estado: "INJUSTIFICADA"`. Verificado em dev: 5 dias normais davam
`totalFalta: 5`; agora dão `0` e `estado: "CONFORME"`.

**Correcção na vista `RH_V_FALTA_MENSAL`** (lista de Gestão de Falta): comparava o flag de
desconto com a string `'S'`, mas a coluna é `VARCHAR2` com `'0'`/`'1'` — a comparação era sempre
falsa. Consequência: `descontoRenumeracao` vinha **sempre `false`** e o estado mensal **sempre
`JUSTIFICADA`**, mesmo com faltas injustificadas com desconto. Em dev, um colaborador com 9
faltas injustificadas aparecia como justificadas.

> **Os dois DDL já estão aplicados em dev** (`docs/sql/rh_v_resumo_assiduidade_fix.sql` e
> `rh_v_falta_mensal_fix.sql`, com os textos anteriores nos ficheiros `*_BACKUP_11-09.sql`).
> **Têm de ser replicados em staging e produção pelo DBA.**

---

## 🔴 20. Justificar guarda o que vier no array — `selecionar` deixa de ser lido (11/09/2026)

`POST /api/v1/assiduidade/falta/justificar/{funcionarioId}`

**Justifica-se tudo o que vier em `itensFalta`.** O campo `selecionar` de cada item deixou de ser
lido: pertencer ao array já é o sinal. Ter duas formas de dizer "este dia não" — não o mandar, ou
mandá-lo com `selecionar: false` — era ambiguidade sem ganho, e contraria a convenção da casa,
onde é a presença no array que manda.

**O ecrã tem de enviar só as linhas marcadas.** Um payload com a grelha inteira justifica agora
todos os dias, não apenas os que o utilizador escolheu.

### Um `400` desapareceu

> ~~Nenhuma falta marcada para justificação~~

Deixou de existir. **Mantém-se** o guard de array vazio ou nulo:

> Nenhuma falta informada para justificar

Na prática perde-se uma rede: antes, um array com tudo a `false` dava erro; agora justifica tudo.

### O campo continua no DTO

`selecionar` **não foi removido** de `FaltaItemDTO` e as leituras continuam a emiti-lo a `false`.
Está por decidir o que fazer dele do lado do **validar**, que desde a secção 15 também o ignora —
ou seja, hoje o campo não tem comportamento em endpoint nenhum.

Considerou-se separar num DTO base mais um `FaltaItemSelecionavelDTO`, mas isso obriga a um
wrapper próprio para o validar (Java não estreita o tipo de um campo numa subclasse), **tira o
campo das respostas dos `GET`** e acrescenta divergência ao manifesto IGRP — tudo para preservar
um campo sem comportamento. Decide-se o validar primeiro.

### Porque é que o editar continua diferente

No `PUT falta/justificar/pedido/{pedidoUuid}` o `itensFalta` **continua ignorado** e não vai
passar a valer como aqui. A assimetria é de propósito: no justificar, pertencer ao array **cria**,
e omitir um dia não destrói nada — ele fica onde estava, por justificar. No editar seria o
inverso: um frontend que enviasse só as linhas marcadas **apagava as restantes faltas e revertia
o dinheiro delas**, em silêncio.

---

## 🔴 21. Eliminar um pedido volta a permitir justificar esses dias (11/09/2026)

`existeFaltaVivaNoDia` ignorava apenas as faltas em `I`, pelo que uma falta em `E` continuava a
prender o dia. Mas o painel "por justificar" exclui `E` e **oferece** esse dia.

Resultado, antes: depois de eliminar um pedido, os dias reapareciam no ecrã e o
`POST falta/justificar` recusava-os com **`400`** *"Já existe uma falta associada à data"*. O ecrã
prometia o que a escrita negava, e o caminho que a spec prevê para quem se enganou — eliminar e
voltar a justificar — estava fechado à chave. O mesmo acontecia a uma **nova marcação com
justificativo** nesse dia.

Passa a ignorar `I` **e** `E`. Não abre porta a duplicados: `A` e `P`, os únicos estados com
efeitos financeiros, continuam a bloquear.

### O dia não aparece duas vezes

Depois de rejustificar, o dia tem **duas** faltas na base — a eliminada (`E`) e a nova (`A`) —,
mas o ecrã mostra-o uma só vez. A leitura já filtrava `E` e agrupa por síntese num `Map`, portanto
há no máximo uma entrada por dia. Verificado em dev com os 14 dias de Outubro de um colaborador:
todos a `1x`, apesar de quatro deles terem duas faltas gravadas.

Cada dia está sempre num de dois sítios, nunca nos dois: em `itensFalta` (por justificar) **ou**
dentro de **um** grupo de `pedidos`.

### O que o eliminar desfaz, e o que não

| Tabela | Representa | Efeito do eliminar |
|---|---|---|
| `RH_ASSIDUIDADE_SINTESE_DIARIA` | "neste dia houve ausência" | **nada** — fica intacta |
| `RH_T_FALTA` | "esta ausência foi justificada assim" | `E` |
| `RH_T_DEF_REMUNERACOES` | desconto no vencimento | `E`, e a associação em `RH_T_TIPREL_REM_PAG` é **apagada** |
| `RH_T_FERIAS_GOZADAS` / `RH_T_DISPENSA` | saldo consumido | `E` — **o saldo é devolvido** |
| `RH_T_VALIDACAO` | o despacho | `E` **só se ainda estiver pendente** |

O eliminar mata a **justificação**, não a **ausência** — é por isso que o dia volta ao painel e
pode ser justificado de novo, sobre a **mesma** síntese diária.

A validação já despachada **fica como está** (`A` ou `I`): é o registo histórico de que alguém
decidiu aquele pedido naquela data. Só uma validação ainda `P` passa a `E`, porque o pedido que
lhe deu origem desapareceu antes de ser decidido.

Verificado em dev, ponta a ponta: eliminar um pedido com dedução em dispensa devolveu as 4h
(`horasUsadas` 04:00 → 00:00, `horasRestantes` 00:00 → 04:00) e pôs os 4 descontos em `E`.

---

## 🔴 22. Marcar falta recusa um dia que já tem falta viva (11/09/2026)

`POST /api/v1/assiduidade/falta`

O guard de "já existe falta neste dia" só corria com **Com Justificativo = SIM**. No ramo `NAO`
não verificava nada, e como a síntese diária é reaproveitada e as horas substituídas, marcar 4h
num dia que já tinha uma falta viva de 8h estragava o dia sem aviso:

- a síntese passava a dizer 4h e a falta continuava a dizer 8h, e o desconto cobrava o dobro do
  que o dia registava;
- o dia desaparecia dos ecrãs que filtram por falta, mas o desconto continuava na folha.

Passa a valer para **qualquer** marcação:

> Já existe uma falta associada à data 2026-10-26

Só bloqueiam as faltas **vivas** (`A` ou `P`). Os dias libertados por um eliminar continuam a poder
ser marcados (ver secção 21).

**Consequência:** corrigir as horas de um dia que já tem falta viva deixa de ser possível por
este ecrã. Quem se enganou elimina o pedido e volta a marcar.

---

## 🔴 23. Marcar Falta: `motivoAusencia` passa a chamar-se `motivo` (11/09/2026) — QUEBRA DE CONTRATO

`POST /api/v1/assiduidade/falta` e `GET /api/v1/assiduidade/falta/{pedidoId}`

| Antes | Agora |
|---|---|
| `"motivoAusencia": "..."` | `"motivo": "..."` |

Era o único sítio da aplicação com esse nome: o Justificar Falta, a dispensa e as férias usam
todos `motivo` para a mesma coluna (`RH_T_FALTA.DESCRICAO_MOTIVO`).

⚠️ **O nome antigo não dá erro.** Um frontend que continue a enviar `motivoAusencia` recebe `200` e
o motivo **não é gravado**, porque o campo desconhecido é ignorado em silêncio. Actualizar os dois
lados, o envio e a leitura.

---

## 🔴 24. Despacho: o checker pode retirar a dedução e dar o parecer (11/09/2026)

`PUT /api/v1/assiduidade/falta/justificar/validar/{pedidoId}`

O despacho usa o mesmo corpo do justificar, e o ecrã monta-o a partir do `GET` do pedido. Três
campos mudaram de comportamento:

| Campo | Antes | Agora |
|---|---|---|
| `deduzirFaltaEm` | vazio **mantinha** a dedução do maker, e não havia forma de a tirar | vazio **retira** a dedução, como no registo e no editar |
| `parecerResponsavel` | **ignorado** no despacho | gravado em `RH_T_FALTA.DECISAO_RESPONSAVEL` (spec `:762`); só substitui se vier |
| `tipoJustificacao` | trocar o tipo não actualizava a indicação "desconta salário" na falta | a indicação segue o tipo efectivo |

⚠️ **Enviar o estado completo — responsabilidade do cliente.** O backend não distingue "campo
omitido" de "`null`": os dois retiram a dedução. O ecrã tem de mandar sempre `deduzirFaltaEm` com
o que está no combo — o valor que o `GET` devolveu, se o checker não lhe mexeu. Um corpo parcial
sem este campo retira a dedução do maker. Decidido a 11/09: não se protege isto no backend.

| O checker no ecrã | O ecrã envia | Resultado |
|---|---|---|
| não mexe no combo | o valor do `GET` | mantém a dedução do maker |
| escolhe outra | `"FERIAS"` / `"DISPENSA"` | aplica a nova |
| limpa o combo | `null` | retira a dedução — o dia vai todo ao salário, se o tipo descontar |

O dinheiro já saía certo quando o checker trocava o tipo, porque o desconto decide pelo tipo. A
correcção é na coluna, que dizia o contrário do desconto aplicado.

Provado em dev: o checker trocou o tipo 18 por 20, retirou a dedução em férias e deu parecer
`SIM`. Nenhum desconto nem férias consumidas, as faltas ficaram com `FLG_DESCONTO_SAL=0`,
`FLG_DESCONTO_FALTA=null` e `DECISAO_RESPONSAVEL='SIM'`, e o `GET` devolve
`parecerResponsavel: "SIM"`.

### O que mantém

`itensFalta` **continua obrigatório**, com pelo menos um item, apesar de o despacho decidir o
pedido inteiro. Um corpo sem itens dá `400` *"Nenhuma falta selecionada para validação"*. Fica
assim de propósito, para uma futura remoção de itens por checkbox.

---

## 🔴 25. `valorCoberto` sai `0` enquanto o pedido não for despachado (11/09/2026)

`GET falta/justificar/pedido/{pedidoId}`, `GET falta/justificar/{funcionarioId}` e
`GET falta/{pedidoId}`

Um pedido em `P` (à espera de despacho) mostrava o **bruto inteiro** em `valorCoberto`, como se o
saldo o tivesse coberto:

```json
{ "estado": "P", "deduzirFaltaEm": null, "valorTotal": 25378.24, "valorDescontado": 0.00, "valorCoberto": 25378.24 }
```

Não houve saldo nenhum a cobrir nada. Passa a:

```json
{ "estado": "P", "deduzirFaltaEm": null, "valorTotal": 25378.24, "valorDescontado": 0.00, "valorCoberto": 0.00 }
```

`valorCoberto` só conta as faltas **despachadas** (`A`) que tenham dedução em férias ou dispensa,
porque é o único caso em que um saldo pode ter coberto alguma coisa. Nos pedidos em `A` os valores
não mudam; verificado em dev nos três pedidos de teste (3 172,28 com dispensa, 12 689,12 com
férias, 0,00 sem dedução).

---

## 🔴 26. `documentos` omitido deixa de apagar os anexos (11/09/2026)

`PUT falta/justificar/pedido/{pedidoId}` (editar) e `PUT falta/justificar/validar/{pedidoId}`
(despacho)

Um corpo **sem** o campo `documentos` chegava como lista vazia, e a sincronização lia-o como "o
utilizador retirou todos os anexos": os anexos existentes passavam a `E`.

Passa a seguir a convenção dos arrays da casa:

| `documentos` | Efeito |
|---|---|
| omitido ou `null` | **preserva** os anexos |
| `[]` | retira **todos** os anexos |
| com itens | item com `id` mantém, item sem `id` cria, anexo existente que não vier fica `E` |

Os `GET` continuam a devolver `documentos: []` quando não há anexos.

---

## Por decidir com o analista

| # | Assunto |
|---|---|
| 1 | **Ordem de Serviço** — criada ao aprovar uma falta, mas "ordem de serviço" não aparece uma única vez na spec de assiduidade |
| 2 | **A spec contradiz-se nos 3 dias** — a secção REGRA (`:494`, `:796`) diz "mais de 3 dias **e** desconta salário"; a secção Ações, nos **dois** ecrãs (`:553`, `:825`), diz que a linha em `RH_T_VALIDACAO` nasce **só** quando o tipo desconta salário, sem falar em dias |
| 3 | **Eliminar desfaz os descontos** — a spec só manda pôr `RH_T_FALTA.ESTADO='E'`, nada sobre devolver férias, dispensa ou salário |
| 4 | **Fuga dos 3 dias** — um dia rejeitado (`I`) deixa de contar no limite; rejeitar 4 e rejustificar um a um deixa passar 3 sem despacho |
| 5 | **`estadoDesc` inconsistente** — o mesmo estado `A` sai como `"Justificada"` nos itens e `"Ativo"` no pedido, no mesmo payload |
| 6 | **`RH_T_DISPENSA.TIPO_DISPENSA` fica `null`** nas linhas criadas por dedução de falta — ecrãs que filtrem por tipo não as vêem |
| 7 | **`selecionar` não tem comportamento em endpoint nenhum** — fica no DTO à espera da decisão do lado do validar (ver secções 15 e 20) |
| 8 | **`itensFalta` obrigatório mas ignorado no despacho** — mantido de propósito, a pensar numa futura remoção de itens por checkbox (ver secção 24) |

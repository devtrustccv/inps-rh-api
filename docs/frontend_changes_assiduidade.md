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

# Alterações Front-End — Missão de Serviço

**Data:** 2026-08-10
**Branch:** develop
**Commits:** `a16c0989`, `dca10c31`

Base de todos os endpoints: `/api/v1/missao-servico`

---

## 1. Correção — `cabId` deixou de ser obrigatório

`PUT /{uuid}/cabimento` rejeitava com **400 `cabId é obrigatório`** qualquer item selecionado sem `cabId`.

Estava errado: segundo a Especificação Técnica Funcional, o número de cabimento é **gerado**, não introduzido pelo utilizador — na etapa de Autorização o campo é descrito como *"Mostra o numero de Cabimento Gerado na Etapa Anterior"*. O formulário da Cabimentação só tem selecionar, tipo de serviço, nome, valor e anexo.

### Antes
```jsonc
// 400 — cabId é obrigatório
{ "itens": [ { "logisticaId": 101, "selecionado": true, "anexo": { ... } } ],
  "processoEtapaAction": "SAVE" }
```

### Depois
```jsonc
// 200 — cabId é opcional
{ "itens": [ { "logisticaId": 101, "selecionado": true, "anexo": { ... } } ],
  "processoEtapaAction": "SAVE" }
```

> `cabId` continua a ser aceite no payload — serve os **cabimentos manuais/internacionais**, que a spec descreve como preenchidos à mão pelo departamento financeiro.

**Nota:** a geração automática do `cabId` via SGAL **ainda não está implementada** (sem endpoint nem contrato definidos). As linhas ficam `CABIMENTADO` com `cabId: null`.

---

## 2. `SAVE` vs `NEXT` — comportamento corrigido em todas as etapas

Todos os `PUT` de etapa aceitam `processoEtapaAction` com dois valores: `"SAVE"` (Gravar) e `"NEXT"` (Avançar/Cabimentar/Autorizar).

| Ação | O que faz |
|---|---|
| `SAVE` | Grava os dados do formulário. **Não** avança a etapa, **não** muda estados, **não** envia notificações. |
| `NEXT` | Grava, avança a etapa, muda estados e envia as notificações da etapa. |

### O que mudou

| Endpoint | Antes | Depois |
|---|---|---|
| `PUT /{uuid}/cabimento` | `SAVE` marcava `CABIMENTADO` e avançava a etapa | `SAVE` só grava anexos/seleção |
| `PUT /{uuid}/autorizacao` | `SAVE` marcava `AUTORIZADO` | `SAVE` só valida; só `NEXT` autoriza |
| Todos os `PUT` de etapa | `SAVE` reescrevia a etapa da missão | `SAVE` nunca toca na etapa |

---

## 3. A etapa nunca retrocede

Cada `salvar*` escrevia a etapa do seu ecrã de forma incondicional. Numa missão em `PAGAMENTO`, gravar no ecrã de submissão devolvia-a a `SUBMISSAO`, reabrindo etapas já concluídas.

Agora a transição é **monotónica**: a etapa só avança, nunca recua. Gravar num ecrã de uma etapa já ultrapassada continua a ser permitido (correções), mas não puxa o processo para trás.

Ordem das etapas: `SUBMISSAO` → `ANALISE` → `EMISSAO_REQUISICAO` → `LOGISTICA` → `CABIMENTO` → `PAGAMENTO`

---

## 4. Guarda de ordem — `NEXT` fora de sequência dá 400

| Ação | Etapa à frente da atual | Etapa já ultrapassada |
|---|---|---|
| `SAVE` | grava (só regista aviso no log) | grava |
| `NEXT` | **400** | avança (sem retroceder) |

```jsonc
// PUT /{uuid}/cabimento com NEXT numa missão em SUBMISSAO
{
  "status": 400,
  "title": "A missão encontra-se na etapa 'SUBMISSAO' — esta operação exige que já tenha atingido a etapa 'CABIMENTO'"
}
```

`PUT /{uuid}/pagamento` exige sempre que a etapa `PAGAMENTO` tenha sido atingida (não tem Gravar/Avançar).

---

## 5. Gravações idempotentes

Gravar duas vezes o mesmo formulário deixou de ter efeitos colaterais.

**Logística** — o maior problema: cada gravação **inativava e recriava** as linhas. Consequências, agora resolvidas:

| | Antes | Depois |
|---|---|---|
| `logisticaId` após re-gravar | mudavam (ex.: 132→137) | estáveis |
| Anexos | perdidos (ficavam na linha inativa) | preservados |
| Linhas em `RH_T_MISSAO_LOGISTICA` | +N por gravação | sem crescimento |

> **Importante:** os `logisticaId` que o ecrã de Cabimentação envia deixam de ser invalidados por uma gravação da Logística noutro separador.

**Autorização** — gravar duas vezes dava `400 Item sem cabimento`. Agora aceita itens já `AUTORIZADO` (idempotente).

**Anexos** — ao reenviar um anexo já existente, incluir sempre o `id` que veio no GET:

```jsonc
"anexo": { "id": 349, "tipoDocumentoId": 21, "documento": "bilhete.pdf" }  // atualiza
"anexo": { "tipoDocumentoId": 21, "documento": "bilhete.pdf" }             // cria novo e marca o anterior como eliminado
```

---

## 6. Novo campo — `colaboradoresMissao`

**Endpoints:** `GET /{uuid}/emissao-requisicao` e `GET /{uuid}/logistica`

Devolve o universo de colaboradores afetos à missão, para popular os multiselects **sem uma segunda chamada** ao `/submissao`.

```jsonc
"colaboradoresMissao": [
  {
    "id": 31,                                            // RH_T_MISSAO_COLABORADOR.id
    "uuid": "019fed6d-ae2a-755d-812b-4efb450802b0",      // uuid da linha missão-colaborador
    "funId": 958873,                                     // RH_T_FUNCIONARIOS.id
    "funUuid": "019fd75f-0b61-7406-a256-a709912e8b51",   // uuid do funcionário  ← usar este
    "nomeColaborador": "Wilson Cabral Tavares",
    "numDocumento": "19940819M002H",
    "estado": "A",
    "missaoPrestId": 17,                                 // só em /logistica
    "nomePrestador": "Halcyon Viagens"                   // só em /logistica
  }
]
```

> ⚠️ **Armadilha:** os campos `missaoColabIds`, `colaboradorIds` e `colaboradorId` dos payloads esperam o **`funUuid`** (uuid do funcionário), apesar do nome. Os outros três identificadores devolvem `400 Colaborador inválido`.

### `missaoPrestId` / `nomePrestador` (só na Logística)

Uma linha de **bilhete** ou **seguro** só pode agrupar colaboradores do **mesmo prestador** — a associação é feita na etapa de Emissão de Requisição. Agrupar colaboradores de agências diferentes devolve:

```jsonc
{ "status": 400, "title": "Prestador inconsistente para os colaboradores selecionados" }
```

Com estes campos, o multiselect pode agrupar por agência e evitar a seleção inválida:

```
▾ Colaboradores
  ── Halcyon Viagens ──────────
     ☐ Wilson Cabral Tavares
  ── Cabo Verde Travel ────────
     ☐ Tatiana Delgado Barbosa
```

Colaborador com `missaoPrestId: null` ficou de fora da Emissão de Requisição — qualquer linha de logística para ele dá `400 Requisição não encontrada para colaborador`. Deve ser desativado na UI.

---

## 7. Novo campo — `colaboradores` por linha

**Endpoints:** `GET /{uuid}/cabimento` e `GET /{uuid}/autorizacao`

O campo `nome` mostra o **prestador** (ou o colaborador, no caso da ajuda de custo), o que torna linhas distintas indistinguíveis:

```
│ 144 │ SEGURO_VIAGEM │ Impar Seguros │ 15.000 │   ← de quem?
│ 145 │ SEGURO_VIAGEM │ Impar Seguros │ 15.000 │   ← de quem?
```

Cada item passa a incluir os seus colaboradores:

```jsonc
{
  "logisticaId": 144,
  "referencia": "SEGURO_VIAGEM",
  "nome": "Impar Seguros",
  "valorTotal": 15000,
  "colaboradores": [
    { "id": 165,
      "missaoColabUuid": "019fed6d-ae2a-755d-812b-4efb450802b0",
      "funcionarioUuid": "019fd75f-0b61-7406-a256-a709912e8b51",
      "nomeColaborador": "Wilson Cabral Tavares",
      "estado": "A" }
  ]
}
```

---

## 8. `numDocumento` — passaportes deixaram de se perder

Números de documento **alfanuméricos** (ex.: passaporte `PA466262`) eram convertidos para `null` sem erro, por a coluna `RH_T_MISSAO_COLABORADOR.NUM_DOCUMENTO` ser `NUMBER`. Passou a `VARCHAR2`, e a leitura usa o funcionário como fonte de verdade.

Aplica-se retroativamente: missões já gravadas passam a mostrar o valor correto.

> **Ops:** o `ALTER TABLE` foi aplicado diretamente na BD (o Flyway está desligado e esta tabela não consta das migrações). **Tem de ser repetido noutros ambientes.**

---

## Fluxo completo — o que enviar em cada etapa

Percurso real de uma missão com 2 colaboradores repartidos por 2 agências.

### Etapa 1 · Submissão

**Lookups do ecrã:**
```
GET /api/v1/parametrizacao/geografias?nivelDetalhe=1   → países  [{label, value}]
GET /api/v1/funcionarios?pageNumber=0&pageSize=10      → colaboradores
```

**Criar:** `POST /submissao`
```jsonc
{
  "paisDestinoId": 1033,
  "descricaoDestino": "Paris - conferência CIPRES",
  "ambitoMissao": "INTERNACIONAL",
  "dataInicio": "2026-10-05",
  "dataFim": "2026-10-09",
  "autorizadoPor": "Wilson",
  "dataAutorizacao": "2026-08-10",
  "colaboradores": [
    { "colaboradorId": "019fd75f-0b61-7406-a256-a709912e8b51" },
    { "colaboradorId": "019fd759-2039-74b2-a587-cd2b5f131ae0" }
  ],
  "documentos": [ { "tipoDocumentoId": 20, "documento": "convite_cipres.pdf" } ],
  "processoEtapaAction": "SAVE"
}
```
→ `{ "nrMissao": 3, "id": "<uuid>" }`

**Editar:** `PUT /{uuid}/submissao` com o mesmo corpo.

- **Não enviar `estado`** — o backend põe `"A"`.
- `nrDias` é calculado a partir das datas.
- `documentos[].documento` é o **nome do ficheiro já carregado** via `POST /api/v1/documento/private`, não o binário.

### Etapa 2 · Análise

`GET /{uuid}/analise` → `{ missaoId, etapaAtual, prestadores: [], notificacao }`

`PUT /{uuid}/analise`
```jsonc
{
  "prestadores": [
    { "entId": 11, "nome": "Halcyon Viagens",   "email": "reservas@halcyon.cv" },
    { "entId": 12, "nome": "Cabo Verde Travel", "email": "geral@cvtravel.cv" }
  ],
  "processoEtapaAction": "SAVE"
}
```

- Máximo **3 prestadores**.
- A lista é sincronizada por inteiro: quem não vier é inativado.
- `NEXT` → envia o pedido de proposta às agências.

### Etapa 3 · Emissão de Requisição

`GET /{uuid}/emissao-requisicao` → `requisicoes[]` + `colaboradoresMissao[]`

`PUT /{uuid}/emissao-requisicao`
```jsonc
{
  "requisicoes": [
    { "missaoPrestId": 17, "selecionado": true,
      "missaoColabIds": ["019fd75f-0b61-7406-a256-a709912e8b51"],
      "documentoProposta": { "tipoDocumentoId": 19, "documento": "proposta_halcyon.pdf" } },
    { "missaoPrestId": 18, "selecionado": true,
      "missaoColabIds": ["019fd759-2039-74b2-a587-cd2b5f131ae0"],
      "documentoProposta": { "tipoDocumentoId": 19, "documento": "proposta_cvtravel.pdf" } }
  ],
  "processoEtapaAction": "SAVE"
}
```

- `missaoColabIds` = **`funUuid`**.
- Cria uma requisição por par prestador × colaborador.
- **Esta associação determina o que é possível agrupar na Logística.**
- `NEXT` → notifica os prestadores selecionados.

### Etapa 4 · Logística

`GET /{uuid}/logistica` → 4 secções + `colaboradoresMissao[]` (com prestador)

`PUT /{uuid}/logistica`
```jsonc
{
  "bilhetesPassagem": [
    { "colaboradorIds": ["<funUuid>"], "valor": 90000,
      "anexo": { "tipoDocumentoId": 21, "documento": "bilhete_wilson.pdf" } }
  ],
  "segurosViagem": [
    { "entId": 12, "nomeSeguradora": "Impar Seguros",
      "colaboradorIds": ["<funUuid>"], "valor": 15000 }
  ],
  "alojamentos": [
    { "colaboradorId": "<funUuid>", "flgAlimentacao": "NAO",
      "lugarHospedagem": "Ibis Paris Gare du Nord",
      "valorDiario": 12000, "valorTotal": 60000, "moeda": "CVE",
      "dataInicio": "2026-10-05", "dataFim": "2026-10-09" }
  ],
  "ajudasCusto": [
    { "colaboradorId": "<funUuid>", "flgAlojamento": true,
      "numeroDiasAlojamento": 5, "valorDiario": 12000 }
  ],
  "processoEtapaAction": "SAVE"
}
```

| Secção | Colaboradores | Campos obrigatórios |
|---|---|---|
| `bilhetesPassagem` | lista (mesmo prestador) | `colaboradorIds`, `valor` |
| `segurosViagem` | lista (mesmo prestador) | `entId`, `colaboradorIds`, `valor` |
| `alojamentos` | **um por linha** | `colaboradorId`, `flgAlimentacao`, `lugarHospedagem`, `valorDiario`, `valorTotal` |
| `ajudasCusto` | **um por linha** | `colaboradorId`, `flgAlojamento`, `numeroDiasAlojamento`, `valorDiario` |

**Cálculo da ajuda de custo** — o backend aplica a fração ao `valorDiario` enviado:

| Situação | Fração | Exemplo (base 12.000) |
|---|---|---|
| `flgAlojamento: false` (alojamento próprio) | 100% | 12.000/dia |
| `flgAlojamento: true` + alojamento com `flgAlimentacao: "NAO"` | ⅔ | 8.000/dia |
| `flgAlojamento: true` + alojamento com `flgAlimentacao: "SIM"` | ⅓ | 4.000/dia |

`valorTotal = valorDiário calculado × numeroDiasAlojamento`, feito no backend.

> ⚠️ O **`valorDiario` base vem do cliente** e não é validado contra nenhuma tabela de preços. A spec prevê que dependa da função do colaborador e de missão nacional/internacional, mas essa parametrização não existe. Só omitir uma secção do payload a deixa intacta.

`NEXT` → notifica os colaboradores com os detalhes de viagem.

### Etapa 5 · Cabimentação

`GET /{uuid}/cabimento` → uma linha por serviço, com `colaboradores` e `fatura`

`PUT /{uuid}/cabimento`
```jsonc
{
  "itens": [
    { "logisticaId": 142, "selecionado": true,
      "anexo": { "id": 360, "tipoDocumentoId": 21, "documento": "bilhete_wilson.pdf" } },
    { "logisticaId": 148, "selecionado": true }
  ],
  "processoEtapaAction": "SAVE"
}
```

- `SAVE` → grava anexos e seleção; `estadoCabimento` continua `null`.
- `NEXT` (**Cabimentar**) → `estadoCabimento: "CABIMENTADO"` nos selecionados.
- Pelo menos um item selecionado, senão `400 Selecione pelo menos um item`.

### Etapa 6 · Autorização

`GET /{uuid}/autorizacao` → linhas com `estadoCabimento` e `numeroCabimento`

`PUT /{uuid}/autorizacao`
```jsonc
{
  "itens": [
    { "logisticaId": 142, "autorizado": true },
    { "logisticaId": 143, "autorizado": true }
  ],
  "processoEtapaAction": "NEXT"
}
```

- Item não `CABIMENTADO` → `400 Item sem cabimento: {id}`.
- `SAVE` **não** autoriza; só `NEXT`.
- `NEXT` → `AUTORIZADO` + etapa `PAGAMENTO`.

### Etapa 7 · Pagamento

`PUT /{uuid}/pagamento`
```jsonc
{ "referenciaPagamento": "TRF-2026-0003", "dataPagamento": "2026-08-10" }
```
Sem `processoEtapaAction`.

---

## Como saber que ecrã mostrar

**Não existe etapa `AUTORIZACAO`.** Depois de cabimentar, `etapaAtual` continua `CABIMENTO` — conforme a spec, que manda escrever `'CABIMENTO'` no fim da Cabimentação e `'PAGAMENTO'` no fim da Autorização.

O frontend distingue os dois ecrãs pelo **`estadoCabimento` dos itens**:

| `etapaAtual` | `estadoCabimento` dos itens | Ecrã |
|---|---|---|
| `SUBMISSAO` | — | Submissão |
| `ANALISE` | — | Análise |
| `EMISSAO_REQUISICAO` | — | Emissão de Requisição |
| `LOGISTICA` | — | Logística |
| `CABIMENTO` | `null` | **Cabimentação** (por cabimentar) |
| `CABIMENTO` | `CABIMENTADO` | **Autorização** (por autorizar) |
| `PAGAMENTO` | `AUTORIZADO` | Pagamento / concluído |

Sequência de estados de cada linha: `null` → `CABIMENTADO` (Cabimentar) → `AUTORIZADO` (Autorizar).

### Autorização parcial

É possível autorizar apenas alguns itens. **Mas o `NEXT` avança para `PAGAMENTO` de qualquer forma**, deixando os restantes em `CABIMENTADO`. Esses itens podem ser autorizados mais tarde (a guarda de etapa permite gravar em etapas já ultrapassadas), mas **nada na UI os sinaliza** — a missão aparece como concluída.

Recomendação para o frontend: só permitir Autorizar quando todos os itens estiverem selecionados, ou avisar explicitamente sobre os que ficam por autorizar.

---

## Limitações conhecidas

| Tema | Situação |
|---|---|
| `cabId` (SGAL) | Não gerado — integração por definir (sem endpoint nem contrato). Linhas ficam `CABIMENTADO` com `cabId: null`. |
| `valorDiario` | Vem do cliente, sem validação. Tabela de preços da ajuda de custo não existe. |
| `entId` | Aceite sem validação — não há lookup de entidades (agências/seguradoras). |
| Alojamento em grupo | O DTO força uma linha por colaborador; a spec admite cabimento único para o mesmo hotel. |
| Autorização parcial | Avança para `PAGAMENTO` mesmo com itens por autorizar. |
| Encoding | Enviar `Content-Type: application/json; charset=utf-8` — há nomes com mojibake vindos de `/funcionarios`. |

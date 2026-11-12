# Alterações Front-End — Transversal (JOB Alerta → Processar Renovação em Lote)

**Data:** 2026-08-29
**Branch:** develop
**Âmbito:** Grelha de Alertas (`RH_T_ALERTA`) → ação "Processar" para alertas do tipo `RENOVACAO_CONTRATO`.
**Referência:** Especificação Técnica Funcional — TRANSVERSAL (29_08_26), secção 3.4.2 → 1) Tipo_alerta Renovação.

---

## 1. Novo endpoint — Processar renovação em lote

`POST /api/v1/funcionarios/renovacao-contrato/lote`

Renova **um grupo de colaboradores ou apenas um** (um lote de 1 cobre o caso individual), a partir dos
alertas selecionados na grelha. É **atómico**: se algum colaborador falhar a validação, **nenhum** é
renovado e são devolvidos **todos os erros de uma vez**.

### Request body — `RenovarLoteReqDTO`

```jsonc
{
  "itens": [
    {
      "funcionarioId": "uuid-do-colaborador",   // obrigatório (UUID)
      "contratoId":    "uuid-do-contrato",        // obrigatório (UUID)
      "alertaId":      "uuid-do-alerta",          // opcional (UUID) — uuid do alerta de origem
      "dadosRenovacao": {                          // obrigatório
        "dataInicio":   "2026-09-01",             // obrigatório (a duração é opcional)
        "dataFim":      "2027-08-31",
        "duracaoMeses": 12
      }
    }
    // ... mais colaboradores
  ]
}
```

- `alertaId` é o **uuid** do alerta que despoletou o "Processar" (o `uuid` da lista de alertas, não o `id`). Quando presente, o backend marca esse alerta
  como tratado (`flg_tratamento='S'`) e ele **sai da grelha "por tratar"**. Se for omitido, a renovação é
  processada na mesma, mas nenhum alerta é marcado.
- `dataFim`/`duracaoMeses` são **form-driven** (o front calcula `dataFim = dataInicio + duração`, como já
  hoje na renovação individual). O backend valida apenas que `dataInicio` existe e que `dataFim`, se
  enviada, não é anterior a `dataInicio`.

### Resposta 200 — sucesso (`SuccessResponseDTO`)

```json
{ "sucesso": true, "id": null, "mensagem": "Renovação em lote processada: 5 colaborador(es) enviados para validação.", "alertas": [] }
```

### Resposta 400 — erros agregados

Se ≥1 colaborador falhar, **rollback total** e o `ProblemDetail` traz a lista completa em `details`:

```jsonc
{
  "title": "Não foi possível processar a renovação em lote. Corrija os seguintes colaboradores:",
  "status": 400,
  "details": [
    "Colaborador uuid-A: O tipo de contrato 'Estágio' não é renovável.",
    "Colaborador uuid-B: Foi atingido o número máximo de renovações (2) para este contrato."
  ]
}
```

**Ação front:** mostrar a lista `details` ao utilizador, ele desmarca os colaboradores problemáticos na
grelha e reenvia. Nenhuma renovação foi aplicada enquanto houver erros.

> É maker-checker: este "Processar" **não renova de imediato** — cria uma validação pendente por
> colaborador. A renovação só se efetiva quando o checker validar (SIM) via
> `POST .../validar-renovacao-contrato/{contratoId}` (inalterado).

---

## 2. Ciclo de vida do alerta de renovação (grelha "por tratar")

A grelha "por tratar" deve filtrar `estado='P'` **E** `flg_tratamento='N'`.

| Momento | `flg_tratamento` | `estado` |
|---|---|---|
| Alerta criado pelo JOB | `N` | `P` |
| Processar (este endpoint) | **`S`** | `P` |
| Checker valida **SIM** | `S` | **`I`** |
| Checker valida **NÃO** | **`N`** (volta à grelha) | `P` |

> **Nota de implementação:** o campo `flg_tratamento` **não consta da spec** (que só descreve o `estado`
> `P → I`). Foi adotado para modelar a janela maker-checker (sair da grelha ao processar, voltar se
> rejeitado). O checker localiza o alerta pelo `referencia_id` (= id do contrato) + `tipo_alerta`.

---

## 3b. Processar CONVERSÃO — campo `alertaId` opcional no Novo Contrato

Alerta `CONVERSAO_CONTRATO` (§3.4.2 §2): o "Processar" abre o **Novo Contrato pré-preenchido** (Dossiê). Reutiliza o endpoint existente `POST /api/v1/funcionarios/{idFuncionario}/contratos` — **sem endpoint novo**.

- `NovoContratoDTO` ganha um campo **opcional** `alertaId` (`UUID` — o `uuid` da lista de alertas). Envia-se **apenas** quando o Novo Contrato foi aberto a partir do "Processar" de um alerta de conversão; num Novo Contrato normal, omitir.
- Ao gravar (maker) com `alertaId` presente, o backend marca o alerta `flg_tratamento='S'` (sai da grelha "por tratar").
- Na validação (checker, `PUT .../{idFuncionario}/contratos/{contratoId}`): **SIM** → alerta `estado='I'` (convertido); **NÃO** → `flg_tratamento='N'` (volta à grelha). O checker localiza o alerta pelo `referencia_id` do **contrato anterior** (o que foi convertido) — não é preciso reenviar `alertaId` na validação.

> Confirmado na BD viva: `RH_T_ALERTA.FLG_TRATAMENTO`/`ESTADO` **não têm CHECK constraint**; `'S'/'N'` e `estado='I'` são válidos.

## 3. Sem alterações de contrato existente

O endpoint individual de renovação (`POST .../{idFuncionario}/renovacao-contrato/{contratoId}`) e o de
validação **não mudaram**. O `RenovacaoContratoDTO` e o request de `validar` ficam iguais — o conceito de
alerta vive só no request do lote.

---

# Bug report QA Transversal (OUTUBRO) — alterações de API (23/10/2026)

Correr antes `scripts/transversal_bug_report_TR.sql` (domínios + estado das notificações).

## 4. Documentos de Saída — `configuracao/documentos-output` (sem `api/v1`)

**Responsável passa a ser o id de `RH_T_RESPONSAVEL`** (spec: `RESPONSAVEL_ID`) — ⚠️ muda o contrato (TR-03/TR-04):

| | Antes | Agora |
|---|---|---|
| Registo/edição (POST/PUT) | `responsavel`: UUID do funcionário | **`responsavelId`**: número = o `responsavelId` de `GET configuracao/responsaveis/search` |
| Resposta (lista/GET) | `responsavelId`: UUID do funcionário | `responsavelId`: número (RH_T_RESPONSAVEL.ID) · `funcionarioId`: UUID · `responsavelNome` |

Quem é responsável da direção e de uma secção tem duas linhas na pesquisa; agora a escolhida é a que fica.
Na **edição sem `responsavelId`** mantém-se o responsável anterior (antes perdia-se).

- Campo novo **`tipoDocumentoDesc`** (descrição do domínio `ORDEM_SERVICO_DECLARACAO`) — mostrar na lista (TR-01).
- **Um só documento ativo por tipo**: registar ou editar para um tipo que já tem outro ativo → 400
  *"Já existe um documento ativo para o tipo X. Edite esse documento."* (havia dois de TRANSFERENCIA e a OS rebentava).
- `responsavelId` inexistente → 404; inativo → 400.
- **Eliminar não existe** (a spec só prevê Editar): retirar o botão (TR-05).

**Ação front:** combo Responsável com label `funcionario` e valor `responsavelId` da pesquisa; no Editar, pré-preencher
com `responsavelId`/`responsavelNome` do GET; título "Registar Documento" no registo (TR-02).

## 5. Tipos de Documento — `configuracao?configurationType=PARAM_TIPO_DOCUMENTO`

- **Editar só altera o Estado** (spec Parametrização: "Somente deve deixar editar Estado"): código, descrição e
  referência enviados no PUT são ignorados. O ecrã deve pô-los só-leitura na edição e refazer o GET ao abrir (TR-08).
- `referencia` passa a obrigatória no registo (400 *"referencia: The field <referencia> is required"*) (TR-07).
- Filtro: `descricao=<texto>`; `estado=` vazio conta como não indicado (antes 400) (TR-06).

## 6. Pedido de Declaração — `api/v1/funcionarios/pedidos/declaracoes`

- **Todos os `{id}` são o `uuid`** do pedido — ⚠️ `PUT {id}/analise` e `PUT {id}/validacao` usavam o id numérico.
  Id numérico → 400 *"Identificador do pedido inválido…"* (TR-17).
- **Tipo de Declaração** tem de ser um `value` de `GET api/v1/parametrizacao/dominios?dominio=ORDEM_SERVICO_DECLARACAO&referencia=DECLARACAO`
  (`AQUISICAO_DISTICO`, `CREDITO_HABITACAO`, `PEDIDO_CREDITO_BANCARIO`, `EFEITO_VISTOS`); outro valor → 400. Usar o mesmo
  domínio no filtro da lista (TR-09/TR-14).
- Campos novos: lista e GET `tipoDeclaracaoDesc`, `estadoPedidoDesc`; GET também `finalidadeDesc`; anexos com
  `estado`/`estadoDesc` preenchidos (TR-15/TR-16).
- O PDF (`GET {uuid}/declaracao`) passa a funcionar para os 4 tipos (procurava um template que não existia).

**Em espera (spec nova):** retirar Analisar/Validar e passar o pedido pela lista de validação (TR-18) e o separador
Análise sem "Em conformidade" no registo (TR-13) — a spec de 29/08 não os prevê.

## 7. Notificações — `api/v1/funcionarios/notificacoes`

- **Estado = código do domínio `ESTADO_NOTIFICACAO`**: `POR_ENVIAR`, `ENVIADO`, `ERRO` (novo). Filtrar com o código;
  mostrar o campo novo **`estadoDescricao`** (TR-20/TR-22). Igual em `missao-servico/{uuid}/notificacoes`.
- Datas `dataEnvioDe`/`dataEnvioAte`: aceitam `yyyy-MM-dd` ou data com hora; o **Até inclui o próprio dia**; data
  inválida ou Até < De → 400 (antes 500 / dia excluído) (TR-19).
- Tipo: combo de `dominios?dominio=TIPO_ALERTA_NOTIFICACAO` (ganhou CONVERSAO_CONTRATO, RENOVACAO_CONTRATO,
  LICENCA_S_VENCIMENTO, MISSAO_CANCELAMENTO).
- Mês corrente por defeito: o ecrã pré-preenche as datas (a spec não o define no backend) (TR-21).

## 8. Alertas — `api/v1/funcionarios/alertas`

- Campos novos: **`referencia`** (RH_T_ALERTA.REFERENCIA — mostrar na coluna Referência), **`tipoAlertaDescricao`**,
  **`flgNotificacao`** (S/N — "Ver Notificação" só ativo com S) (TR-23/24/25).
- Filtro `referencia` passa a comparar com RH_T_ALERTA.REFERENCIA (ex.: `CONTRATO`).
- Filtros `nomeColaborador`, `direcaoId`, `seccaoId` passam a funcionar (eram ignorados).
- "Ver Notificação": `GET alertas/{uuid}/notificacoes` com o **uuid**. Hoje vem sempre vazio: o JOB ainda não gera
  notificações (destinatários por definir).

## 9. Documentos de Saída — marcadores `${…}` (23/10/2026)

Só acréscimos (os endpoints existentes não mudam). Mesmo caminho `configuracao/documentos-output`:

- `GET configuracao/documentos-output/{uuid}/marcadores` → `{ tipoDocumento, marcadores: ["nomeColaborador", …] }`
  — os `${…}` que o texto usa (título, corpo, "Assinado por").
- `POST configuracao/documentos-output/pre-visualizar` → `{ titulo, corpo, corpoHtml, assinadoPor, responsavel, marcadoresEmFalta }`
  — body `{ tipoDocumento, valores: {nome: valor} }` (documento ativo) ou `{ titulo?, corpo, assinadoPor?, valores }` (texto por gravar).
  Não grava nada. Sem tipo nem corpo → 400; tipo sem documento ativo → 404.

No texto: `${nome}`, `${nome:-valor por defeito}`, `$${nome}` (literal); marcador sem valor sai "—".
As declarações (PDF) passam a substituir os marcadores. Guia para programadores: `docs/guia_documentos_saida_marcadores.html`.

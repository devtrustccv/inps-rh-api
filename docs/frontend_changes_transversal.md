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

# Alterações Front-End — Hora Extra (Cálculo Valor Diário)

**Data:** 2026-05-28  
**Branch:** develop

---

## 1. Campo renomeado e alterado no `HoraExtraDTO`

Este DTO é usado em **duas situações**:
- Corpo do `POST /api/v1/assiduidade/hora-extra` (marcar hora extra)
- Resposta do `GET /api/v1/assiduidade/hora-extra/{pedidoId}` (detalhe de um pedido)
- Ajuste no `POST /api/v1/assiduidade/hora-extra/{pedidoId}` (validar / ajustar)

### 1.1 Campo `percentagemHora` → `percentagemReferente`

| | Antes | Depois |
|---|---|---|
| **Nome** | `percentagemHora` | `percentagemReferente` |
| **Tipo** | `Integer` (ex: `50`, `100`) | `String` (código do domínio) |
| **Valores válidos** | Qualquer inteiro | Ver domínio `DIAS_PERCENTAGEM_HORA` abaixo |

**Domínio `DIAS_PERCENTAGEM_HORA`** — alimentar este campo com um dos seguintes valores:

| Valor a enviar | Descrição exibida |
|---|---|
| `DIAS_UTEIS` | Dias Úteis |
| `DIAS_UTEIS_NAO_UTEIS` | Dias Úteis e Não Úteis |
| `DIAS_NAO_UTEIS` | Dias Não Úteis |

> Os valores estão registados em `RH_T_DOMAINS` com `DOMINIO = 'DIAS_PERCENTAGEM_HORA'` e podem ser consultados via o endpoint de domínios do IGRP (`api/v1/enums`).

### 1.2 Campo `valorDiario` — tipo alterado

| | Antes | Depois |
|---|---|---|
| **Tipo** | `Integer` (sem casas decimais) | `BigDecimal` (2 casas decimais) |
| **Exemplo** | `441705` | `441705.88` |

O valor é **calculado automaticamente** pelo backend ao gravar — o formulário deve mostrar este campo como DISABLED e preencher via o novo endpoint de preview (ver secção 3).

---

## 2. Endpoints existentes — o que não mudou

| Endpoint | Mudança no contrato |
|---|---|
| `GET /api/v1/assiduidade/hora-extra` (lista) | **Sem alteração.** O campo `percentagem: Integer` na resposta continua igual (vem da view, representa o valor numérico armazenado). |
| `POST /api/v1/assiduidade/hora-extra/{pedidoId}` (validar) | O campo de ajuste `percentagemReferente` (era `percentagemHora`) aceita agora `String`. Se enviado, o backend recalcula `valorDiario` automaticamente. |

---

## 3. Novo endpoint — Preview do valor diário (sem gravar)

```
GET /api/v1/assiduidade/hora-extra/calculo-valor
```

Calcula o valor diário de hora extra **sem criar nenhum registo**. Usar para preencher o campo "Valor Diário" no formulário (campo DISABLED) antes do utilizador submeter.

### Parâmetros (query string)

| Parâmetro | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `funcionarioUuid` | `String` (UUID) | ✅ | UUID do colaborador |
| `dataInicio` | `String` (`yyyy-MM-dd`) | ✅ | Data início do período |
| `dataFim` | `String` (`yyyy-MM-dd`) | ✅ | Data fim do período |
| `percentagemReferente` | `String` | ✅ | Código do domínio `DIAS_PERCENTAGEM_HORA` |
| `horasDiaria` | `Long` | ✅ | Número de horas extra por dia |

### Resposta `200 OK`

```json
{
  "valorDiario": 441705.88
}
```

### Exemplo de chamada

```
GET /api/v1/assiduidade/hora-extra/calculo-valor
    ?funcionarioUuid=019d77f8-e847-797a-b8fd-69ffeeccaf4c
    &dataInicio=2026-04-14
    &dataFim=2026-04-30
    &percentagemReferente=DIAS_UTEIS
    &horasDiaria=2

→ { "valorDiario": 441705.88 }
```

---

## 4. Exemplo completo — POST marcar hora extra (corpo actualizado)

```json
POST /api/v1/assiduidade/hora-extra

{
  "horaExtra": [
    {
      "colaborador": "019d77f8-e847-797a-b8fd-69ffeeccaf4c",
      "dataInicio": "2026-04-14",
      "dataFim":    "2026-04-30",
      "horasDiaria": 2,
      "percentagemReferente": "DIAS_UTEIS"
    }
  ]
}
```

**Resposta:**
```json
{
  "pedidoId": 72,
  "pedidoUuid": "019e6f9d-d94a-74e2-ba28-86fc29296f6f",
  "totalRegistos": 1
}
```

> O `valorDiario` é calculado automaticamente pelo backend (chama o procedimento Oracle `RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_HORA_EXTRA`). Não é necessário enviá-lo no corpo — se enviado, é ignorado.

---

## 5. Fluxo recomendado no formulário

```
1. Utilizador preenche: colaborador, dataInicio, dataFim, horasDiaria, percentagemReferente
2. Frontend chama GET /hora-extra/calculo-valor com esses valores
3. Preenche campo "Valor Diário" (DISABLED) com a resposta
4. Utilizador confirma → Frontend envia POST /hora-extra
```

---

## 6. Resumo das alterações por endpoint

| Endpoint | Campo | Antes | Depois |
|---|---|---|---|
| `POST /hora-extra` (body) | `percentagemHora` | `Integer` | ❌ removido |
| `POST /hora-extra` (body) | `percentagemReferente` | ❌ não existia | `String` (domínio `DIAS_PERCENTAGEM_HORA`) |
| `POST /hora-extra` (body) | `valorDiario` | `Integer` (opcional) | `BigDecimal` (calculado pelo backend, ignorado no input) |
| `GET /hora-extra/{pedidoId}` (response) | `percentagemHora` | `Integer` | ❌ removido |
| `GET /hora-extra/{pedidoId}` (response) | `percentagemReferente` | ❌ não existia | `String` |
| `GET /hora-extra/{pedidoId}` (response) | `valorDiario` | `Integer` | `BigDecimal` |
| `GET /hora-extra/calculo-valor` | — | ❌ não existia | **Novo endpoint** |

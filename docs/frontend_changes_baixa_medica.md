# Nova Funcionalidade Front-End — Baixa Médica (Licença)

**Data:** 2026-05-28  
**Branch:** develop  
**Documento de referência:** `docs/Especificação Tecnica Funcional - PROCESSAMENTO SALARIAL.md` — secção "Baixa Médica / Licença"

---

## 1. Visão geral

A funcionalidade de **Baixa Médica / Licença** permite registar, calcular e validar licenças médicas de colaboradores. O cálculo é feito pelo procedimento Oracle `RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_FALTA_LICENCA`, que devolve os dados mês a mês.

**Base path do controller:** `/colaborador` (sem `api/v1`)

---

## 2. Endpoints disponíveis

### 2.1 Preview do cálculo (sem gravar)

```
GET /colaborador/baixa-medica/calculo
```

Calcular os dados da licença antes de gravar. Usar para preencher a secção "Informações Definidas no Regulamento" no formulário (campos desactivados/read-only).

**Query params:**

| Parâmetro | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `colaborador` | UUID | ✅ | UUID do colaborador |
| `dataInicio` | `yyyy-MM-dd` | ✅ | Data de início da licença |
| `dataFim` | `yyyy-MM-dd` | ✅ | Data de fim da licença |
| `tipoLicenca` | Long | ✅ | ID de `RH_T_PARAM_SITUACAO` onde `FLG_ABONO_BENEFICIO=1` |
| `dataInicioFalta` | `yyyy-MM-dd` | ❌ | Data início da falta (parâmetro opcional do regulamento) |

**Resposta `200 OK`:**

```json
{
  "descSobre": "DIAS_CORRIDO",
  "diasDireito": "0",
  "diasDescRh": "0",
  "diasNdescRh": "3",
  "msgError": null,
  "faltasMensais": [
    {
      "mes": "04/2026",
      "dataInicioFalta": "14/04/2026",
      "dataFimFalta": "30/04/2026",
      "diasFalta": "-3",
      "valorSalario": "271819",
      "valorDesc": null
    }
  ]
}
```

> **Nota:** `msgError` não nulo indica erro de validação do regulamento (ex: datas inválidas). Mostrar ao utilizador antes de permitir gravar.

---

### 2.2 Criar baixa médica

```
POST /colaborador/baixa-medica
Content-Type: application/json
```

**Corpo da request:**

```json
{
  "colaborador": "019d77f8-e847-797a-b8fd-69ffeeccaf4c",
  "tipoLicenca": 24,
  "motivo": null,
  "dataInicio": "2026-04-14",
  "dataFim": "2026-04-30",
  "dataInicioFalta": null,
  "observacao": "Internamento hospitalar",
  "documentos": [
    {
      "tipoDocumentoId": 1,
      "documento": "url-do-ficheiro-no-minio"
    }
  ]
}
```

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `colaborador` | UUID | ✅ | UUID do colaborador |
| `tipoLicenca` | Long | ✅ | ID de `RH_T_PARAM_SITUACAO` (ver tipos abaixo) |
| `motivo` | Long | ❌ | ID de `RH_T_PARAM_SITUACAO_DET` (motivo opcional) |
| `dataInicio` | `yyyy-MM-dd` | ✅ | Data início |
| `dataFim` | `yyyy-MM-dd` | ✅ | Data fim |
| `dataInicioFalta` | `yyyy-MM-dd` | ❌ | Passado ao procedure |
| `observacao` | String | ❌ | Observações livres |
| `documentos` | Array | ❌ | Documentos comprovativos (upload para MinIO primeiro) |

**Resposta `200 OK`:**

```json
{
  "pedidoId": 80,
  "pedidoUuid": "019e6ff3-48dd-7c47-acf7-c405d1f3ffab",
  "totalRegistos": 1
}
```

> Guardar o `pedidoUuid` — é necessário para GET detalhe e validação.

---

### 2.3 Detalhe de uma baixa médica (para editar/validar)

```
GET /colaborador/baixa-medica/{pedidoId}
```

Retorna os dados de um pedido existente para preencher o formulário de edição/validação.

**Resposta `200 OK`:**

```json
{
  "pedidoId": 80,
  "pedidoUuid": "019e6ff3-48dd-7c47-acf7-c405d1f3ffab",
  "estado": "P",
  "tipoLicencaId": 24,
  "tipoLicencaNome": "Baixa médica",
  "motivoId": null,
  "motivoNome": null,
  "dataInicio": "2026-04-14",
  "dataFim": "2026-04-30",
  "observacao": "Internamento hospitalar",
  "calculo": {
    "descSobre": "DIAS_CORRIDO",
    "diasDireito": "0",
    "diasDescRh": "0",
    "diasNdescRh": "3",
    "msgError": null,
    "faltasMensais": [ ... ]
  }
}
```

---

### 2.4 Validar / Desvalidar

```
POST /colaborador/baixa-medica/{pedidoId}?validar=SIM|NAO
Content-Type: application/json  (body opcional)
```

| `validar` | Efeito |
|---|---|
| `SIM` | Aprova — todos os registos passam para `estado=A` |
| `NAO` | Rejeita — todos os registos passam para `estado=I` |

**Body opcional** — enviar apenas se houver ajustes durante a validação (campos editáveis + novos documentos):

```json
{
  "observacao": "Confirmado por médico",
  "documentos": [
    {
      "tipoDocumentoId": 1,
      "documento": "url-novo-documento"
    }
  ]
}
```

**Resposta `200 OK`:**

```json
{
  "pedidoId": 80,
  "pedidoUuid": "019e6ff3-48dd-7c47-acf7-c405d1f3ffab",
  "totalRegistos": 1,
  "estado": "A"
}
```

---

## 3. Tipos de licença disponíveis

Buscar via `RH_T_PARAM_SITUACAO` onde `FLG_ABONO_BENEFICIO = 1` e `ESTADO = 'A'`.

Exemplos actuais na BD:

| ID | Nome | FLG_AUSENCIA | Contagem dias |
|---|---|---|---|
| 24 | Baixa médica | 1 | DIAS_CORRIDO |
| 40 | Maternidade | 1 | DIAS_CORRIDO |
| 41 | Licença Paternidade | 1 | DIAS_UTEIS |
| 39 | Isolamento Profilático | 1 | DIAS_CORRIDO |

---

## 4. Fluxo recomendado no formulário

```
1. Utilizador selecciona: colaborador, tipo licença, datas
2. Front-end chama GET /baixa-medica/calculo com esses valores
3. Preenche secção "Informações do Regulamento" (read-only) com a resposta
4. Utilizador preenche observação, motivo, anexa documentos
5. Submete → POST /baixa-medica
6. Guardar pedidoUuid para posterior validação

Para validar:
7. GET /baixa-medica/{pedidoUuid} → preencher formulário de validação
8. POST /baixa-medica/{pedidoUuid}?validar=SIM com body opcional de ajustes
```

---

## 5. Tabelas gravadas e estados

| Tabela | Criar (estado) | Validar SIM (estado) | Validar NAO (estado) |
|---|---|---|---|
| `RH_T_PEDIDO` | `P` | `A` | `I` |
| `RH_T_ABONOS_BENEFICIOS` | `P` | — | — |
| `RH_T_AUSENCIA` | `P` | — | — |
| `RH_T_FALTA` (por mês) | `P` | `A` | `I` |
| `RH_T_VALIDACAO` | `P` | `A` | `I` |
| `RH_T_DOCUMENTO` | `P` | `A` | `I` |

---

## 6. Ajustes de BD aplicados durante implementação

| Alteração | Tabela | Motivo |
|---|---|---|
| `CK_ABONO_ESTADO` alargado: `IN ('A','I','P')` | `RH_T_ABONOS_BENEFICIOS` | Constraint original não permitia estado pendente `'P'` |
| `SINTESE_DIARIO_ID` tornado nullable | `RH_T_FALTA` | Baixa médica não tem síntese diária associada |
| `FUN_ID` adicionado a `AusenciaEntity` | `RH_T_AUSENCIA` | Campo obrigatório na BD não estava mapeado em Java |
| Sequência `SEQ_ABONOS_BENEFICIOS` criada | — | Não existia; necessária para gerar IDs da nova tabela |

---

## 7. Campos do formulário → mapeamento BD

| Campo formulário | Tabela.Coluna |
|---|---|
| Tipo Licença | `RH_T_ABONOS_BENEFICIOS.PARAM_SIT_ID` |
| Motivo | `RH_T_ABONOS_BENEFICIOS.PARAM_SIT_DET_ID` |
| Data Início Licença | `RH_T_ABONOS_BENEFICIOS.DATA_INICIO`, `RH_T_AUSENCIA.DATA_INICIO` |
| Data Fim Licença | `RH_T_ABONOS_BENEFICIOS.DATA_FIM`, `RH_T_AUSENCIA.DATA_FIM` |
| Observação | `RH_T_ABONOS_BENEFICIOS.OBS` |
| Documento | `RH_T_DOCUMENTO` (REFERENCIA_NAME = 'RH_T_ABONOS_BENEFICIOS') |
| Desconto Sobre (read-only) | `p_desc_sobre` do procedure |
| Dias Direito Licença (read-only) | `p_dias_Direito` do procedure |
| Dias Descontado RH (read-only) | `p_dias_desc_rh` do procedure |
| Dias Não Descontado RH (read-only) | `p_dias_ndesc_rh` do procedure |
| Falta Mensal — Mês (read-only) | `p_meses[]` do procedure |
| Falta Mensal — Dias (read-only) | `p_dias_falta[]` do procedure |
| Falta Mensal — Salário Base (read-only) | `p_valor_salario[]` do procedure |
| Falta Mensal — Valor Descontado (read-only) | `p_valor_desc[]` / `RH_T_FALTA.VALOR` |

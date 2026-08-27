# Alterações Front-End — Melhorias Dossiê do Colaborador

**Data:** 2026-08-26
**Branch:** feat/dossier-melhorias (merged → develop @ merge `de698d2a`)
**Âmbito:** Dossiê / Funcionário + Parametrização de Vínculo. Testado live (FASES 0–9 verdes; T7.8 adiado).

> ⚠️ **Contém um break change** (secção 1). Ler antes de subir o front contra develop.

---

## 1. BREAK CHANGE — `flgSalario` / salário do vínculo passa a String

O campo que antes vinha como inteiro `0/1/2` passa a ser um **literal de domínio** (`TIPO_SALARIO_VINCULO`):

| Valor (String) | Significado |
|---|---|
| `SIM_PCCS` | Tem salário, tabela PCCS (escalão manda) |
| `SIM_FORA_PCCS` | Tem salário, fora do PCCS (valor manual) |
| `NAO` | Sem salário |

Aplica-se a:

| Endpoint | Campo | Antes | Agora |
|---|---|---|---|
| `GET`/`POST` parametrização de vínculo (`VinculoDTO`) | `flgSalario` | `Integer` (0/1/2) | `String` (literal acima) |
| `GET` vínculo laboral (`VinculoLaboralResponseDTO`) | `salario` | `Integer` | `String` (literal); + `remuneracaoDesc` com a descrição do domínio |

**Ação front:** enviar/ler os literais String (não 0/1/2). Selects de vínculo devem usar o domínio
`TIPO_SALARIO_VINCULO` (exposto pelo enum-exposer em `api/v1/enums`). Enviar `flg_salario=1` (inteiro)
passa a ser rejeitado na gravação do vínculo.

---

## 2. Escalão no vínculo (tiprel) para PCCS **sem carreira**

Para vínculos `SIM_PCCS` **com `flg_carreira=0`**, o escalão passa a viver no próprio vínculo e o
**salário é derivado do escalão** (não é manual).

- **Registo de Colaborador** e **Novo Contrato** (gravar/validar): enviar `escalaoReferenciaId` (`Long`)
  no bloco de dados contratuais (`DadosContratuaisReqDTO`). O backend grava o escalão no tiprel e usa o
  **valor do escalão** como salário — não é preciso enviar salário à mão.
- **Get-by-id** (colaborador e contrato, `DadosContratuaisRespDTO`): passa a devolver
  `escalaoReferenciaId` preenchido para estes casos (antes vinha `null`), para o formulário reabrir com o
  escalão certo.

> Vínculos **com carreira** (`flg_carreira=1`) mantêm-se iguais: o escalão vem da carreira, não se envia
> `escalaoReferenciaId` no tiprel.

---

## 3. Lista "Gestão Laboral" — campo `categoria` → `escalao`

Resposta da lista de relação laboral (`GET .../funcionarios/{funcionarioId}/relacao-laboral`,
`RelacaoLaboralSumaryDTO`):

| Antes | Agora |
|---|---|
| `categoria` | **removido** |
| — | `escalao` (`String`, ex.: `"16/A"`) |

Preenchido tanto para vínculos com carreira (escalão da carreira) como sem carreira (escalão do tiprel).
**Ação front:** ler `escalao`; remover a coluna/binding de `categoria`.

---

## 4. Novo fluxo "Alterar Escalão / Cargo" (Gestão Laboral)

Só para colaboradores **PCCS sem carreira** (para carreira usa-se Progressão/Promoção).

| Ação | Método + Endpoint |
|---|---|
| Registar alteração | `POST .../funcionarios/{funcionarioId}/relacao-laboral/alterar-escalao-cargo` |
| Validar alteração | `PUT .../funcionarios/{funcionarioId}/relacao-laboral/alterar-escalao-cargo/{tiprelUuid}` |

**Body (`AlterarEscalaoCargoDTO`)** — igual no POST e no PUT:

| Campo | Tipo | Notas |
|---|---|---|
| `tipoAlteracao` | `String` | multiselect enviado como **CSV** (grava tal-e-qual); domínio `TIPO_MOV_LABORAL` / ref `GESTAO_LABORAL` |
| `novoEscalaoId` | `Long` | opcional |
| `novoCargoId` | `Long` | opcional |
| `dataInicio` | `date` | efetividade |
| `dataFim` | `date` | opcional |
| `observacao` | `String` | opcional |
| `validar` | `String` | só no PUT: `SIM` / `NAO` / `CORRIGIR` |

**Comportamento:**
- **Só cargo** (sem `novoEscalaoId`) → aplicado **imediatamente** no vínculo atual, **sem** ir a validação.
- **Escalão** (com ou sem cargo) → cria um movimento **pendente (P)** e entra na **lista de validações**
  com o rótulo **"Gestão Laboral"**.
- Validação: `SIM` consolida (fecha o vencimento antigo por data, cria o novo com o valor do escalão e as
  datas do form, ativa o novo vínculo); `NAO` rejeita (`I`); `CORRIGIR` devolve ao maker (`C`) e o re-POST
  reabre para `P` sem duplicar.
- Enviar sem `novoEscalaoId` **nem** `novoCargoId` → `400` ("Indique o novo escalão e/ou o novo cargo").
- Já existe alteração pendente sobre o mesmo vínculo → `409` ("Existe uma alteração … por validar").

**Erros de guard (HTTP 400):**
- Vínculo **com carreira**: *"Alterar Escalão/Cargo só se aplica a vínculos sem carreira. Para carreira use Progressão/Promoção."*
- Vínculo **não-PCCS**: *"Alterar Escalão/Cargo só se aplica a vínculos com salário do PCCS (SIM_PCCS)."*

---

## 5. Remunerações — dois filtros novos

`GET .../funcionarios/renumeracoes` ganha dois query params opcionais:

| Param | Tipo | Filtra por |
|---|---|---|
| `situacaoLaboral` | `Long` (id) | Situação laboral |
| `contrVinculo` | `Long` (id) | Contrato/Vínculo |

Valores não numéricos são ignorados silenciosamente. Sem `tiprelUuid`, a lista continua a mostrar **só o
vínculo ativo** (`est_act_adm=1`); os filtros combinam-se por cima disso.

---

## 6. Pendente conhecido — grelha de detalhe da "Gestão Laboral"

`GET .../funcionarios/validacoes/{tiprelUuid}/detalhes` para uma **alteração de escalão** (Gestão Laboral)
devolve por agora `[]` (HTTP 200) — o "antes → depois" ainda não é montado para este fluxo. As grelhas de
detalhe de **Mobilidade, Carreira, Registo, Renovação**, etc. **não mudam** e continuam a funcionar.

**Ação front:** não assumir conteúdo na grelha de detalhe **especificamente para Gestão Laboral**; tratar
lista vazia sem erro. (Será preenchida numa iteração seguinte.)

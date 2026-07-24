# Plano de Testes — Carreira + Mobilidade (composição na validação, `def` por associação)

**Contexto:** valida as alterações commitadas (`23457064`, `0d4d250c`):
- Carreira: **contentor no registo**; na validação **relê as dimensões partilhadas do atual** (composição) e activa/rejeita os `def` **pela associação** do tiprel.
- Mobilidade: **sem contentor** — **clona o atual na validação**.
- `getMobilidade` "antes": pelo tiprel **introdutor** (pai com `mob` diferente).

**Regra de ouro a verificar em todos:** no fim, **exatamente 1** tiprel com `est_act_adm=1` por funcionário (invariante do "atual").

## Pré-condições
- App a correr (perfil `development`, porta 8089, auth aberta).
- `dataInicio` **≥ hoje** (registo rejeita datas no passado) e **dentro do contrato**.
- Colaboradores **limpos** = 1 atual (`est_act_adm=1`, estado A), carreira A, `n_pend=0`. Escolher no momento via query.
- Rotas:
  - Carreira: `POST .../funcionarios/{fun}/carreiras` · validar `POST .../carreiras/{carrUuid}/validar` · detalhe `GET .../funcionarios/carreiras/{carrUuid}`
  - Mobilidade: `POST .../funcionarios/{fun}/mobilidades` · validar `PUT .../funcionarios/{fun}/mobilidades/{mobUuid}` · detalhe `GET .../funcionarios/mobilidades/{mobUuid}`

---

## C1 — Concorrência: 2 dimensões, 2 ordens *(regressão — já passou)*
**Objetivo:** carreira + mobilidade pendentes ao mesmo tempo compõem em qualquer ordem.
- **C1.a (mob→carreira):** registar carreira (novo escalão) + mobilidade; validar **mobilidade e depois carreira**.
- **C1.b (carreira→mob):** noutro colaborador; validar **carreira e depois mobilidade**.

**Esperado:** atual único com **`carr=novo` E `mob=novo`**; sem erro 500.
**Verificação:** `SELECT id, est_act_adm, carreira_id, mob_id FROM RH_T_TIPOS_RELACIONAMENTO WHERE fun_id=? AND est_act_adm=1` → 1 linha, ambos novos.

---

## C2 — 2 carreiras: **acumular** (CARGO + CATEGORIA)  ⚠️ prioridade
**Objetivo:** garantir que o *refresh* das partilhadas + `def` por associação não partem o acumular.
**Setup:** colaborador com atual **CARGO** (cargo≠null).
**Passos:**
1. Registar carreira **CATEGORIA** (`cargoPosicaoId=null`, `categoriaId=…`), `flgProcessa=0`.
2. `GET` detalhe da carreira pendente → confirma escalão/salário/subsídios.
3. Validar **SIM**.

**Esperado:**
- **2 carreiras activas** (A): a CARGO (atual, `flg_processa=1`, `est_act_adm=1`) + a CATEGORIA nova (A, `est_act_adm=0`, `flg_processa=0`) **sem `data_fim`**.
- Invariante: só 1 `est_act_adm=1`.
- A CATEGORIA nova traz os **seus** `def` (pela associação do seu contentor); a CARGO mantém os dela — **sem cruzamento**.

**Verificação:** listar tiprels + `def` associados de cada contentor (`TIPREL_REM_PAG`).

---

## C3 — 2 carreiras: **progressão** (mesmo tipo)
**Objetivo:** a nova do mesmo tipo substitui a anterior; `def` transferidos/fechados corretamente.
**Setup:** colaborador com atual CARGO.
**Passos:** registar carreira CARGO com escalão diferente (`PROMOCAO`, `flgProcessa=1`); validar **SIM**.

**Esperado:**
- Track anterior (mesmo tipo) **fecha**: tiprel `est_act_adm=0`, `data_fim=hoje`, `flg=0`, `def` antigos → `I`.
- Nova CARGO passa a atual (`est_act_adm=1`, `flg=1`) com os seus `def` (A) pela associação.
- Invariante: 1 `est_act_adm=1`.

---

## C4 — 2 carreiras: **despromoção** (a nova assume o processamento)
**Objetivo:** ao acumular com `flgProcessa=1`, a outra em vigor perde `flg`/`est_act_adm` **sem `data_fim`**.
**Setup:** colaborador com atual CARGO (`flg=1`).
**Passos:** registar CATEGORIA com `flgProcessa=1`; validar **SIM**.

**Esperado:**
- Nova CATEGORIA: `est_act_adm=1`, `flg=1` (assume o atual/processa).
- CARGO anterior: `flg=0`, `est_act_adm=0`, **`data_fim=null`** (fica activa/parqueada → 2 activas).
- Invariante: 1 `est_act_adm=1`.

---

## C5 — Rejeição (**NÃO**)  ⚠️ prioridade (código mudou)
**Objetivo:** a rejeição usa `def` pela associação e não toca no atual.
- **C5.a Carreira NÃO:** registar carreira; validar **NÃO** → carreira + contentor + `def` (pela associação) → **`I`**; atual **intacto** (`est_act_adm=1` na carreira antiga).
- **C5.b Mobilidade NÃO:** registar mobilidade; validar **NÃO** → mobilidade → `I`; **nenhum** tiprel criado; atual intacto.

**Verificação:** atual não muda; nada em `A/P` órfão.

---

## C6 — `def` **não se misturam**  ⚠️ prioridade (motivo da mudança)
**Objetivo:** provar que a validação da carreira só mexe nos `def` **dela**.
**Setup:** colaborador limpo.
**Passos:**
1. Registar um **rendimento/desconto** avulso (fica `def` em `P`, associado ao seu próprio tiprel/fluxo).
2. Registar uma **carreira** (fica `def` `P` associados ao contentor da carreira).
3. Validar a **carreira NÃO**.

**Esperado:** os `def` do **rendimento** continuam `P` (intactos); só os `def` da **carreira** vão a `I`.
**Verificação:** `SELECT id, estado, obs FROM RH_T_DEF_REMUNERACOES WHERE fun_id=? AND estado IN ('P','I')` — o rendimento fica `P`.

---

## C7 — `getById` (detalhe) — pendente e após validar
- **Carreira:** pendente (`P`) e após validar (`A`) → escalão/salário/subsídios/encargos corretos, lidos pela associação; nunca mistura `def` de outro pendente.
- **Mobilidade:** "antes" correto após validar (tiprel introdutor); **no pendente vem `null`** (sem contentor — comportamento esperado/documentado).

---

## Cobertura vs alterações
| Alteração | Cenários que a cobrem |
|---|---|
| Refresh das partilhadas na validação (composição) | C1, C2, C3, C4 |
| `def` por associação (activar/rejeitar/detalhe) | C2, C3, C5, C6, C7 |
| Mobilidade sem contentor / clone na validação | C1, C5.b |
| "antes" pelo introdutor | C1, C7 |

**Ordem sugerida de execução:** C5 → C6 → C2 → C4 → C3 → C1 (regressão) → C7.
(Prioridade ao que o código mudou: rejeição e não-mistura de `def`.)

# Novo Contrato + ciclo CORRIGIR — testado live a 2026-08-21 (HTTP 200 end-to-end)

Fluxo do **Novo Contrato** (não confundir com Renovação) e do ciclo **CORRIGIR** maker-checker
sobre o contrato. Provado contra a Oracle de dev com o colaborador de teste criado nesta sessão.
Corre por cima de um colaborador **já ativo** (registo validado) — ver `registo-colaborador.md`.

## DTO e endpoints

`NovoContratoDTO = { validar: EstadoValidacao, dadosContratuais: DadosContratuaisReqDTO, tipoOrdemServico: String }`

- **Registar** novo contrato: `POST /api/v1/funcionarios/{idFuncionario}/contratos` — body `NovoContratoDTO`
  **sem** `validar`. Devolve `DadosContratuaisRespDTO` (o contrato fica em **P**).
- **GET by id** (LER antes de validar): `GET /api/v1/funcionarios/{id}/contratos/{contratoId}`
  (`contratoId` = **uuid** do contrato; aceita `?versao=`).
- **Validar / corrigir / reenviar**: `PUT /api/v1/funcionarios/{idFuncionario}/contratos/{contratoId}`
  — mesmo body `NovoContratoDTO`, o campo `validar` decide a ação (ver ciclo abaixo).
- Lista: `GET /api/v1/funcionarios/contratos?idFuncionario=...`.

`idFuncionario`/`id` = **uuid** do funcionário; `contratoId` = **uuid** do contrato.

## Pré-condição (guard D2 — DOSSIÊ "Novo Contrato")

`NovoContratoService` bloqueia se `existeContratoEmVigor` = contrato **estado A** com
`data_fim IS NULL OR data_fim >= hoje`. Um colaborador acabado de registar tem o contrato v1
indeterminado (data_fim null) → **em vigor para sempre** → o Novo Contrato dá 400
("já possui um contrato ativo... use a Renovação"). Alterar um contrato em vigor faz-se pela
**Renovação**, não por Novo Contrato.

Para TESTAR o Novo Contrato é preciso que o contrato atual já **não** esteja em vigor. Como o
registo proíbe `dataInicio` no passado (e a constraint `CK_CONTR_PERIODO` exige
`data_fim >= data_inicio`), a forma limpa de montar a pré-condição é expirar o v1 via SQL directo,
recuando AMBAS as datas (ver `reference_db_helpers` / `DbExec`):

```
UPDATE RH_T_CONTRATO_VINCULO SET data_inicio=DATE '2026-08-19', data_fim=DATE '2026-08-20' WHERE id=<idContratoV1>;
```

Outras armadilhas do registo do novo contrato:
- **`dataInicio` NÃO pode ser futura** (`"A data de início não pode ser uma data no futuro."`) → usar hoje.
- Não pode haver **validação de contrato pendente** para o funcionário.

## Ciclo CORRIGIR (maker-checker) — padrão "edita-no-validar" (sem endpoint separado)

Implementado em `ValidarContratoService.validar`. A validação alvo é a `INSERT/CONTRATO` cujo
`referencia_uuid` = uuid do contrato novo. Estados no par (contrato, validação):

1. **CORRIGIR** (checker devolve ao maker): `PUT` com `validar="CORRIGIR"`.
   `P → C`. NÃO aplica o payload nem toca nos def. Msg "Contrato devolvido para correção.".
   Exige contrato em **P** (senão 400 "Não há contrato pendente para devolver para correção.").
2. **Reenviar** (maker corrige): `PUT` com `validar` **ausente/null** + `dadosContratuais` corrigidos.
   `C → P`. Aplica as edições (mappers) e reabre para validação. Msg "Contrato corrigido e reenviado
   para validação.". Se `validar` vier preenchido enquanto está em C → 400 ("Contrato em correção:
   não pode ser validado.").
3. **SIM** (checker valida): `PUT` com `validar="SIM"`. `P → A`. Consolida (encerra def do contrato
   anterior, reconcilia fixos, ordem de serviço). Msg "Contrato validado.". Contrato anterior fica **I**.
   **NAO** = `P → I` com revert do registo (reativa o contrato anterior se ainda em vigor).

## Sequência de teste provada (2026-08-21)

Colaborador novo (ver `registo-colaborador.md`) → validar SIM (fica A com contrato v1) → expirar v1
por SQL → e então:

1. `POST {idFunc}/contratos` (payload deste dir, sem `validar`) → 200, contrato **P** (v1 vai a I).
2. `GET {idFunc}/contratos/{uuidContrato}` → 200 (LER antes de escrever).
3. `PUT ... validar=CORRIGIR` → contrato+validação **C**.
4. `PUT ... {sem validar, cargoPosicaoId alterado}` → **P**, edição aplicada (cargo mudou no GET).
5. `PUT ... validar=SIM` → contrato **A**, validação **A**, contrato anterior **I**.

## Dados de referência (Oracle dev — INPSRH)

Iguais ao registo de colaborador. `cargoPosicaoId`: 2 (Secretariado do CA), 3 (Diretor de Gabinete).
`tipoOrdemServico` usado: `"NOMEACAO"`. Vínculo 1 (Efetivo, flgCarreira=1 → salário automático 186980).

## SQL de verificação (DbExec — alias != v; usar `val`/`c`)

```
-- contratos do funcionario
SELECT c.id, c.uuid, c.estado, TO_CHAR(c.data_inicio,'YYYY-MM-DD') di, TO_CHAR(c.data_fim,'YYYY-MM-DD') df, c.versao
  FROM RH_T_CONTRATO_VINCULO c JOIN RH_T_FUNCIONARIOS f ON f.id=c.fun_id WHERE f.uuid='<uuidFunc>' ORDER BY c.id;
-- validacoes (colunas: tipo_accao, referencia_name, referencia_uuid, estado)
SELECT val.id, val.tipo_accao, val.referencia_name, val.estado, TO_CHAR(val.referencia_uuid) ref
  FROM RH_T_VALIDACAO val JOIN RH_T_FUNCIONARIOS f ON f.id=val.fun_id WHERE f.uuid='<uuidFunc>' ORDER BY val.id;
```

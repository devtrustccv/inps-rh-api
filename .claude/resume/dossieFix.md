> Updated: 2026-07-30

## Goal

Dossier def-doc handling + a pending request: at progression, call an Oracle stored procedure passing the NEW and PREVIOUS carreira ids.

## Current state

Two commits on branch `fix/dossier-def-doc`:
- `2210f9a9` — vencimento superseded stays `A` (close by DATA_FIM, `I` only for rejection); getById filters def by `estadoAlvo=(tiprel P ? P : A)`.
- `0baf4749` — novo contrato = tudo novo (`encerrarEExcluirDefsContratoAnterior`: close old defs by DATA_FIM keep A + `removeIf` scoping); tms fixos por vínculo filter `Estado.A` (8 queries); `getContratoAtual` (via est_act_adm=1) + findEmVigor scoped to contract.
Tested on fresh funcionarios. Guard restored. Not committed: procedure call (below).

## Decisions made — do not re-litigate

- Old contract defs: close by DATA_FIM keep A, NOT touch the shared sync — user rejected sync param.
- Contract selection multi-contract: est_act_adm=1, not versão (ties at 1).

## Constraints

- NEVER commit `Db*.java/.class` or `.claude/settings.json` (hardcoded DB password).
- Test from a FRESH funcionario; don't touch funcionário 958807.

## Relevant files

- `CarreiraWriteService.java:441-514` (validarCarreira) — `carreira`=new carreira, `carreiraMesmoTipo`=old (null if not progression). Both ids available; call procedure after activation (line 514).
- `CalcularRemuneracaoRepositoryImpl.java:40-63` — project's stored-proc pattern (EntityManager→Session.doReturningWork→CallableStatement).

## Open questions

- Procedure name/schema, params (order/types/IN-OUT), timing (in-tx w/ flush vs post-commit), failure behavior. User has NOT provided these yet.

## Next step

Get the procedure signature from the user, then wire the call into `validarCarreira` after line 514, guarded by `carreiraMesmoTipo != null`.

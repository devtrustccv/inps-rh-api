> Updated: 2026-08-20 21:45

## Goal

Grelha "Detalhe de alterações" (antes→novo) servida pelo **JaVers** (auto-audit), extensível a todos os módulos de validação. Depois: implementar **CORRIGIR** na mobilidade.

## Current state

- **Mobilidade + Carreira** provados end-to-end pelo fluxo de ecrã (app 8089, Oracle real). Endpoint oficial `GET .../validacoes/{uuid}/detalhes` já serve JaVers (front intacto).
- **Extensível**: `ValidacaoDetalheDescriptor` (bean por módulo) + `ReferenciaNomeResolver` (FK→nome, cascata `getNome…`+overrides). Ligar módulo novo = @JaversSpringDataAuditable no repo + 1 descriptor + carimbar o 1º save auditado.
- **Performance resolvida**: entidades de referência como Shallow Reference no `JaversAuditConfig` → commit caiu de ~48s (snapshots:9) para ~0.2s (snapshots:1).
- Commits: 24c8dfa9, 7664657a, 543764ce, 6702303c (em `develop`).

## Decisions made — do not re-litigate

- Nome ATUAL da referência (read-time), não histórico.
- Semântica por `tipoAccao`: INSERT=valores iniciais; UPDATE=diff.
- Allow-list de campos de negócio (fora estado/estActAdm/funId/mobId/contrVinculoId/created*).
- Baseline: carimbar o **PRIMEIRO** `repository.save()` auditado (UUID de validação pré-gerado quando a validação só existe depois).

## Constraints

- Entidades geradas DO-NOT-MODIFY → shallow-ref e afins configurados programaticamente.
- Oracle XE antigo; schema-mgmt do JaVers OFF; JDK 23 (`.../jdk-23.0.2.7-hotspot`).
- Segurança OFF em dev. Helpers BD: `DbQuery` (ver [[reference_db_helpers]]).

## Open questions

- 500 na validação de carreira do MESMO tipo = bug pré-existente do `PKG_AUMENTO_SALARIAL` (usa id de carreira como id de tiprel → ORA-01403). Não é nosso.
- `obs`="CARREIRA"/"MOBILIDADE" (marcador) aparece na grelha — tirar da whitelist?

## Next step

**Fase 3 — CORRIGIR na mobilidade**: tirar o NO-OP em `MobilidadeWriteService.validarMobilidade` (~166); replicar o CORRIGIR state-driven do registo colaborador (estado "Em correção" + get-by-id state-driven). Provar por fluxo de ecrã.

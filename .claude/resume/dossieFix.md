> Updated: 2026-08-20 21:55

## Goal

Grelha "Detalhe de alterações" (antes→novo) via **JaVers**, extensível a todos os módulos. Depois: implementar **CORRIGIR** na mobilidade (Fase 3, EM CURSO — interrompido no arranque).

## Current state

- **Fases 0–2 concluídas e commitadas** (`develop`: 24c8dfa9, 7664657a, 543764ce, 6702303c, 72ee4489). Mobilidade **e** Carreira provadas end-to-end pelo fluxo de ecrã; endpoint oficial `.../detalhes` serve JaVers.
- Extensível: `ValidacaoDetalheDescriptor` (bean/módulo) + `ReferenciaNomeResolver`. Shallow-ref no `JaversAuditConfig` (commit 48s→0.2s).
- **Fase 3 apenas começada**: li `ValidarRegistoColaboradorService` para copiar a mecânica CORRIGIR. Nada escrito ainda na mobilidade.

## Decisions made — do not re-litigate

- CORRIGIR = maker-checker por **estado**: checker faz `P→C` (Estado.C, "em correção") SEM aplicar payload; maker reenvia `C→P` (validar tem de vir null). Ver `ValidarRegistoColaboradorService:74-103,234-237`.
- Baseline JaVers: carimbar o **1º** save auditado (UUID de validação pré-gerado).

## Relevant files

- `funcionario/application/service/MobilidadeWriteService.java:166` — NO-OP do CORRIGIR a substituir.
- `funcionario/application/service/ValidarRegistoColaboradorService.java:74` — modelo state-driven a replicar.
- `shared/application/constants/Estado.java` — confirmar Estado.C ("em correção").

## Open questions

- Mobilidade não tem fluxo maker-checker completo como o colaborador; decidir se CORRIGIR só muda estado da mobilidade P→C e reabre edição, ou replica o ciclo inteiro.
- 500 na validação de carreira mesmo-tipo = bug pré-existente `PKG_AUMENTO_SALARIAL` (id carreira usado como id tiprel → ORA-01403). Não é nosso.

## Next step

Ler `MobilidadeWriteService.validarMobilidade` (~156-281) e `Estado.C`, depois substituir o NO-OP do CORRIGIR (linha ~166) por `P→C` state-driven; provar por fluxo de ecrã.

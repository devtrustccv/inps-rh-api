> Updated: 2026-08-21 14:20

## Goal

Replicar o ciclo **CORRIGIR** (maker-checker por estado) e a grelha **JaVers** a todos os serviços do dossiê. Tema paralelo (procedure de progressão) já isolado em `.claude/resume/procedure-progressao.md`.

## Current state

- **JaVers (Fases 0–2)** + **CORRIGIR Mobilidade** (`58bf6cbf`) e **Carreira** (`309ab41f`): feito, commitado. Helper partilhado `FuncionarioRules`.
- **Gate do procedure REGISTO_SALARIO** (só progressão/promoção): commitado `b2821f68`. Resto do tema procedure em handoff próprio.
- **CORRIGIR em `ValidarContratoService`**: IMPLEMENTADO e **TESTADO LIVE end-to-end** (2026-08-21): registo→GET→CORRIGIR (P→C)→reenvia c/ edição (C→P, edição aplicada)→SIM (A, anterior→I). Guia + payload em `.claude/resume/novo-contrato.md`. **A commitar.**

## Decisions made — do not re-litigate

- Contrato usa forma "edita-no-validar" (sem endpoint separado): maker reenvia por `PUT/POST validar` com `validar=null` quando contrato está em C.
- Validação CONTRATO tem `referenciaUuid=contrato.getUuid()` → `devolverParaCorrecao`/`reabrirParaValidacao` encaixam.

## Relevant files

- ValidarContratoService.java:62-96 — checker CORRIGIR + deteção `estaPorCorrigir`
- ValidarContratoService.java:~190 — ramo maker `else if (estaPorCorrigir)` (C→P)
- NovoContratoService.java:165-168 — cria validação CONTRATO (referenciaUuid=contrato.uuid)
- FuncionarioRules.java:230/248 — helpers devolver/reabrir

## Next step

CORRIGIR do contrato TESTADO e commitado. Seguir para próximos serviços NO-OP (AlterarSituacaoLaboralWriteService:65, ValidarDadosBancariosService:51, etc.) e 2ª passagem JaVers (incl. ValidarRegistoColaboradorService). Notas: colaboradores de teste 958890/958892 têm dinheiro duplicado (limpar). Colaborador de teste desta sessão: uuid 01a0252c-4b19-7028-bcee-448e9491c95a (contrato v1 693=I expirado por SQL, novo 694=A).

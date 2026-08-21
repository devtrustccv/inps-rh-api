> Updated: 2026-08-21 17:05

## Goal

Replicar o ciclo **CORRIGIR** (maker-checker por estado) e a grelha **JaVers** a todos os serviços do dossiê. Tema paralelo (procedure de progressão) em `.claude/resume/procedure-progressao.md`.

## Current state

- **JaVers (Fases 0–2)** + **CORRIGIR** em Mobilidade (`58bf6cbf`), Carreira (`309ab41f`) e **Contrato** (`fc69e68b`, testado live): feito e commitado. Helper partilhado `FuncionarioRules`.
- **Gate do procedure REGISTO_SALARIO** (`b2821f68`) e **progressão não duplica dinheiro** (`402e2b4d`, testado live): Java só estrutura, proc dono do dinheiro. Ver `procedure-progressao.md`.
- Guias + payloads: `.claude/resume/novo-contrato.md`, `registo-colaborador.md`.

## Decisions made — do not re-litigate

- Contrato/carreira/mobilidade CORRIGIR: forma "edita-no-validar" (sem endpoint separado), validar=null reenvia C→P.
- Na progressão o Java NÃO escreve def de dinheiro; o proc faz tudo. Ver [[project_progressao_java_vs_proc]].
- Editar/validar via API: SEMPRE GET by id antes e reenviar cada elemento dos arrays com o seu `id` (senão duplica). Ver [[feedback_get_before_put_ids]].

## Relevant files

- ValidarContratoService.java:76-98,210-215 — checker/maker CORRIGIR contrato
- carreira/CarreiraWriteService.java — gate progressão (criarPendenteContentor + validarCarreira)

## Next step

Próximos serviços NO-OP para CORRIGIR: `AlterarSituacaoLaboralWriteService:65`, `ValidarDadosBancariosService:51`, etc. + 2ª passagem JaVers (incl. `ValidarRegistoColaboradorService`). Limpar colaboradores de teste (958885+, 958894/95/97). App a correr JDK23 porta 8089.

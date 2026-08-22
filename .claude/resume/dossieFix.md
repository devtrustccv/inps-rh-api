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

**EM CURSO (branch `feat/dossie-corrigir-javers-restantes`)**: CORRIGIR + JaVers nos 7 pontos NO-OP
restantes. Plano/checklist em `.claude/resume/dossie-corrigir-javers-plan.md`; observações e armadilhas
em `.claude/resume/dossie-corrigir-javers-obs.md`.

- **FASE 1 (CORRIGIR) COMPLETA** nos 7: DadosBancarios, SituacaoLaboral, RenovacaoContrato, Substituicao,
  Rendimento, Desconto, ProcessoDisciplinar. Compila (EXIT=0). Padrão "edita-no-validar" (validar=null
  reenvia C→P). Regime/Declaração/RelacaoLaboral ficaram FORA (sem caminho de validação).
- **FASE 2 (JaVers auto-audit)**: pendente — anotar repositórios + baseline/carimbo nas mesmas entidades.
- **FASE 3 (teste live)**: pendente — porta 8089, JDK23. Regra: ler antes de escrever; arrays com id;
  colaborador novo validado antes de ação nos filhos.
- **FASE 3 (teste live) COMPLETA**: 7/7 fluxos provados (5 com grelha JaVers). Bug corrigido:
  ValidationUtil.isCorrigir(null) NPE.
- **FASE 4 (pedido do user) COMPLETA E PROVADA**: JaVers para DadosBancarios (NIB 77777→66666) +
  Renovação (Data fim/Duração) via `matchByTypeOnly`; motivo da substituição corrigível (FERIAS→DOENCA);
  colaborador substituído na grelha por NOME ("Colab PROGRESSAO Teste") via resolver override. Compila,
  app arranca, grelha ativa p/ 9 referências.
- **FASE 5 (bug reportado pelo frontend — Mobilidade CORRIGIR "não muda estado")**: DIAGNOSTICADO —
  o CORRIGIR grava `C` na tabela (funciona), mas a vista `RH_V_MOBILIDADE` CALCULA o estado por datas
  (A/I) e ignora a coluna de workflow → a lista mostrava "A". FIX no `MobilidadeReadService.getListMobilidade`:
  sobrepõe o estado da vista com o da TABELA quando é P/C + inclui `C` no filtro. Compila. A testar live.
- Ao verde: pedir autorização para merge com develop (após verificação do user). NÃO limpar colaboradores
  de teste (instrução do user).

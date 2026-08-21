> Updated: 2026-08-21 10:30

## Goal

Grelha "Detalhe de alterações" (antes→novo) via **JaVers** + ciclo **CORRIGIR** (maker-checker por estado), a replicar a todos os serviços do dossiê. Tema paralelo: **procedure PKG_AUMENTO_SALARIAL** (progressão de carreira) — a aguardar DBA.

## Current state

- **JaVers (Fases 0–2)**: concluído/commitado. Endpoint `.../validacoes/{id}/detalhes` serve a grelha. JaVers ligado em Mobilidade + Carreira.
- **CORRIGIR — Mobilidade**: feito e provado, commitado (`58bf6cbf`).
- **CORRIGIR — Carreira**: feito e provado, commitado (`309ab41f`). Extraído helper partilhado.
- **Procedure**: root cause confirmado; log de diagnóstico ativo; **a aguardar DBA**.

## Ciclo CORRIGIR — helper partilhado (usar nos próximos serviços)

`FuncionarioRules`:
- `devolverParaCorrecao(referenciaUuid, estadoEntidade, referencia)` — CHECKER: guard "entidade P + tem validação pendente" → passa validação a C (INSERT precede UPDATE); devolve-a. Caller põe a entidade em C e grava.
- `reabrirParaValidacao(referenciaUuid, referencia)` — MAKER: acha validação em C → passa a P; devolve-a. Caller põe a entidade em P e grava.
- `getValidacaoByReferenciaUuid(uuid, estado, tipoAccao, ref)` — variante por estado.

Padrão por serviço: (1) checker no `validar*` chama `devolverParaCorrecao`; (2) maker no endpoint de edição — se a entidade está em C, aplica payload, chama `reabrirParaValidacao`, põe entidade P, e carimba a validação reactivada no `ValidacaoAuditContext` (se o serviço tiver JaVers).

**Carreira (referência do padrão "editar rico"):** um registo em C **salta** o roteamento progressão/processada (guards `!correcaoRegisto`) e cai no editar-in-place; no fim reactiva a validação INSERT em vez de criar UPDATE. Caminho normal byte-idêntico. Ver `CarreiraWriteService.validarCarreira:401` e `atualizarCarreira:648`.

## CORRIGIR — serviços que FALTAM (NO-OP `MSG_CORRIGIR_NAO_IMPLEMENTADO`)

Decisão: **só CORRIGIR primeiro**, JaVers como 2ª passagem. Ordem sugerida: família contrato a seguir.
- AlterarSituacaoLaboralWriteService:65
- ValidarDadosBancariosService:51
- ValidarContratoService:72
- ValidacaoRenovacaoContratoService:51
- SubstituicaoWriteService:162
- RenumeracoesWriteService:137 (remuneração) + :183 (pagamento)
- RegimeWriteService:90 (edita no próprio validar — forma diferente)
- ProcessoDisciplinarWriteService:76
- PedidoDeclaracaoWriteService:136

Nota: nem todos têm endpoint de edição separado; a metade maker (C→P) tem de respeitar a forma de cada um (uns editam no validar).

## JaVers — serviços que FALTAM (2ª passagem)

Só Mobilidade+Carreira têm `ValidacaoAuditContext`. Falta ligar aos restantes, **incl. ValidarRegistoColaboradorService** (já tem CORRIGIR completo, só falta a grelha).

## Procedure PKG_AUMENTO_SALARIAL — para o DBA (a aguardar)

- Erro: `ORA-01403 / ORA-06512 at "INPSRH.PKG_AUMENTO_SALARIAL", line 695`.
- **Progressão REAL** (mesma pessoa, fun 958881): `REGISTO_SALARIO(724, 726, 1, 'SYSTEM')` — 724 (cargo 2, A, contrato 679) e 726 (cargo 2, P, contrato 679). Substitui o caso 720/730 (pessoas diferentes) que o DBA assinalou.
- Root cause: linhas 693/700 filtram `RH_T_TIPOS_RELACIONAMENTO WHERE ID = P_CARREIRA_ID_*` → deviam ser `WHERE CARREIRA_ID = ...` (prov. `AND EST_ACT_ADM=1`). `MAX(ID)` NULL → linha 695 `WHERE ID=NULL` → 0 linhas.
- Bug latente: linha 694 usa `V_RH_T_CARREIRA.DATA_INICIO` antes de carregada (só na 698).
- Log de captura em `registarSalarioProgressao` (re-lança; **manter** até correção — foi ele que apanhou 724→726).
- Sequência pós-fix: DBA corrige → remover redundância Java↔proc na progressão (`transferirParaNovoTipoRelacionamento` + fecho/registo do vencimento no caminho `carreiraMesmoTipo`) → provar gravação única. NUNCA remover Java antes.

## Ambiente de teste

- App corre com **JDK 23** (`Eclipse Adoptium/jdk-23.0.2.7-hotspot`); `spring-boot:run` com JDK 21 falha (class 67 vs 65). Endpoints sem token no profile development.
- BD via `DbQuery.java`/`DbProc.java` no root (untracked; DbProc faz rollback). ojdbc11 23.7 no ~/.m2.

## Next step

1. Quando o DBA corrigir o proc: validar `REGISTO_SALARIO(724,726,...)` + testar progressão pelo ecrã (o SIM da carreira 726 passará a ativar).
2. Replicar CORRIGIR ao próximo serviço (família contrato) usando os helpers.
3. 2ª passagem JaVers (incl. colaborador).

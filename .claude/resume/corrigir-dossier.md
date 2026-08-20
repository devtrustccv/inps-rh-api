> Updated: 2026-08-20 11:30

## Goal

Implementar a opção CORRIGIR da validação do dossiê (3º caminho: SIM/NAO/CORRIGIR), começando pelo REGISTO_COLABORADOR, com transições de estado limpas e state-driven. Depois de validado, replicar o padrão para os restantes serviços de validação do dossiê.

## Current state

- **REGISTO_COLABORADOR: CORRIGIR completo e validado end-to-end na BD viva.** Commit `40af492d`.
- **Fluxo testado (funcionário 958887):** `P → CORRIGIR → C → reenvio → P → SIM → A`. Grafo inteiro move em lockstep; `reconciliar` no SIM criou DEF_REM=1 + DEF_PAG=3; histórico A, est_act_adm=1, obs=INICIO.
- **get-by-id state-driven + lista inclui C** — commit `ce8b84a6` (esta sessão): filhos no estado do pai; `?validacao` removido (controller/query/manifesto); REM/PAG via `...AssociadosPorEstado`; lista default inclui `C`.
- Compila e passou nos testes manuais. Payload de teste em scratchpad (`create.json`).

## Decisions made — do not re-litigate

- **State-driven, sem booleano**: o estado da entidade decide o snapshot e a transição; origem derivada da própria entidade (evita bug "origem errada").
- **CORRIGIR (checker) não edita**: só P→C.
- **`tipoOrdemServico` é escolha do frontend no SIM** — get-by-id não o expõe; ordem de serviço só é criada se enviado.

## Constraints

- Oracle 11g XE (62.84.179.137:xe) — sem `FETCH FIRST`, usar ROWNUM; DbExec p/ DDL, DbQuery p/ ler.
- Ficheiros gerados (.igrpstudio + Java) — alterar manifesto em par com o Java.

## Relevant files

- `ValidarRegistoColaboradorService.java` — routing por estado + `mudaEstado`.
- `ContratoHistoricoWriteService.java:61` — `transicionarEstado` origem-aware.
- `FuncionarioMapper.java:68` / `GetFuncionarioByIdQueryHandler.java` — snapshot state-driven.

## Next step

Replicar o caminho CORRIGIR nas outras lógicas de validação do dossiê. Se confirmarmos que é boa lógica, aplicar também o padrão `mudaEstado`/`transicionarEstado` (origem-aware, lockstep) a esses serviços — por enquanto passou nos testes no REGISTO_COLABORADOR.

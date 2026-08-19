> Updated: 2026-08-19

## Goal

Implementar a opção CORRIGIR da validação do dossiê (3º caminho: SIM/NAO/CORRIGIR) — começando pelo REGISTO_COLABORADOR — com transições de estado limpas e rigorosas. **OBS: depois de testar este fluxo/método, replicá-lo para os restantes serviços de validação do dossiê, juntamente com o CORRIGIR (claro).**

## Current state

- **REGISTO_COLABORADOR: CORRIGIR feito, compila (`mvn -o compile` EXIT=0), NÃO commitado.** Modelo **state-driven** (o estado do registo decide, sem flag do cliente):
  - `P`+SIM→A ; `P`+NAO→I ; `P`+CORRIGIR→C (ignora payload) ; `C`+null→P (maker corrige e reenvia) ; `C`+validar→erro.
- **Enum `Estado` +`C`** ("Em correção") em `Estado.json` + `Estado.java`.
- **Refactor origem-aware**: `transicionarEstado`/`mudaEstado` derivam a origem da própria entidade (capturam `getEstado()` antes de mutar) — sem parâmetro redundante. `aplicarEstado` agora `private`, recebe origem por dentro.

## Decisions made — do not re-litigate

- **State-driven, sem booleano `emCorrecao`**: rascunho em C é problema do frontend, não do domínio.
- **Origem derivada da entidade, não passada pelo caller**: evita a classe de bug "origem errada".
- **CORRIGIR (checker) não edita**: só P→C.

## Constraints

- Oracle 11g XE (62.84.179.137:xe); DbExec p/ DDL, DbQuery p/ ler. 22 tabelas do dossiê já aceitam `C`.
- Ficheiros gerados (Estado.java, DTOs) — alterar via manifesto `.igrpstudio` + Java.

## Relevant files

- `ValidarRegistoColaboradorService.java` — routing por estado + guards + `mudaEstado`.
- `ContratoHistoricoWriteService.java:61` — `transicionarEstado` origem-aware.
- `FuncionarioRules.java` — `temValidacaoPorCorrigir` + `getValidacao(estado)`.

## Next step

Testar o fluxo real na BD viva: P→(CORRIGIR)→C→(reenvio)→P→(SIM)→A, confirmando contrato-histórico e validação nas duas direções.
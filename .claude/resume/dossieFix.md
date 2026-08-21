> Updated: 2026-08-21 10:05

## Goal

Grelha "Detalhe de alterações" (antes→novo) via **JaVers**, extensível a todos os módulos. Depois: **CORRIGIR** na mobilidade (Fase 3). Tema paralelo: **procedure PKG_AUMENTO_SALARIAL** (progressão de carreira).

## Current state

- **Fases 0–2 concluídas e commitadas** (`develop`). JaVers extensível; endpoint `.../validacoes/{id}/detalhes` serve a grelha.
- **Fase 3 (CORRIGIR mobilidade) CONCLUÍDA e PROVADA end-to-end** — ainda **NÃO commitada**.
- **Procedure**: root cause do ORA-01403 confirmado e entregue ao DBA (a aguardar correção na BD).

## Fase 3 — CORRIGIR mobilidade (feito, por commitar)

Ciclo maker-checker por **estado**, espelhando `ValidarRegistoColaboradorService`:

- `FuncionarioRules.getValidacaoByReferenciaUuid(uuid, estado, tipoAccao, ref)` — novo helper (variante por estado do `getValidacaoPendenteByReferenciaUuid`, que fixava P).
- `MobilidadeWriteService.validarMobilidade` — NO-OP removido:
  - **CORRIGIR (checker)**: guard "mobilidade P + tem validação pendente" → `mobilidade P→C` + `validação P→C`, sem aplicar payload/tocar vínculo.
  - **SIM/NAO**: novo guard "só sobre P" (uma em C dá 400 até reenvio).
- `MobilidadeWriteService.editar` — novo ramo **maker reenvia (C→P)**: aplica payload, **reactiva a validação que estava em C** (não cria nova), com auditoria JaVers da correção.

**Teste HTTP provado** (func `01a00595-...404d`, mob `01a023fa-...1f04`): save→P; CORRIGIR→C (mob+valid); SIM-em-C→400; editar→C→P (direção corrigida 7); SIM→A + tiprel criado (est_act_adm=1); `detalhes` mostra o diff da correção com nomes legíveis. App corre com **JDK 23** (`Eclipse Adoptium/jdk-23.0.2.7-hotspot`) — o `spring-boot:run` com JDK 21 falha (class version 67 vs 65).

## Procedure PKG_AUMENTO_SALARIAL — para o DBA (a aguardar)

- **Erro reproduzido**: `ORA-01403: no data found` / `ORA-06512: at "INPSRH.PKG_AUMENTO_SALARIAL", line 695`.
- **Params da chamada que falhou**: `REGISTO_SALARIO(720, 730, 1, 'SYSTEM')` — 720/730 são `RH_T_CARREIRA.ID` (old/new).
- **Root cause (fonte confirmado via all_source)**: linhas **693 e 700** filtram `RH_T_TIPOS_RELACIONAMENTO WHERE ID = P_CARREIRA_ID_*` — deviam ser `WHERE CARREIRA_ID = P_CARREIRA_ID_*` (provavelmente `AND EST_ACT_ADM=1`). `MAX(ID)` dá NULL → linha 695 `SELECT INTO ... WHERE ID=NULL` → 0 linhas → ORA-01403.
- **Bug latente extra**: linha 694 usa `V_RH_T_CARREIRA.DATA_INICIO` antes de a variável ser carregada (só na 698).
- **Log de captura** em `CarreiraWriteService.registarSalarioProgressao` (~604): loga o stack Oracle e re-lança (comportamento inalterado). Temporário — remover após correção.
- Utilitários no root (untracked): `DbQuery.java`, `DbProc.java` (chamam a BD; DbProc faz rollback).

## Decisions made — do not re-litigate

- CORRIGIR = maker-checker por estado (checker P→C sem payload; maker C→P via editar, validar null).
- Progressão: **o procedure é o dono** de subsídios/vencimento/retroativos. Sequência: DBA corrige proc → só depois remover a redundância no Java (`transferirParaNovoTipoRelacionamento` + fecho/registo do vencimento no caminho `carreiraMesmoTipo`) → provar gravação única. NUNCA remover Java antes do proc funcionar.
- Fallback Java-shadow do proc: **rejeitado** (duplicaria matemática de dinheiro; fallback dispara em erro real; dois donos). Ponte = tornar chamada não-fatal + log, não reimplementar.

## Next step

1. **Commitar a Fase 3** (CORRIGIR mobilidade) — `MobilidadeWriteService`, `FuncionarioRules`.
2. Decidir se o **log de diagnóstico** do proc fica ou reverte.
3. Quando o DBA corrigir o proc: remover a redundância Java↔proc na progressão e provar.

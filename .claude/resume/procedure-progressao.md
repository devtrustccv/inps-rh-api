> Updated: 2026-08-21 16:45

## Goal

Dividir progressão de carreira: **proc** `PKG_AUMENTO_SALARIAL.REGISTO_SALARIO` = dono do dinheiro (`RH_T_DEF_*`); **Java** = dono da estrutura (carreira/tiprel). Confirmar que o ecrã de carreira volta a mostrar os def após progressão.

## Current state

- **DBA lançou NOVA versão do procedure** — `PKG_AUMENTO_SALARIAL` PACKAGE BODY recompilado **2026-08-21 16:01**, status VALID. Fonte extraída para `scratchpad/REGISTO_SALARIO_new.txt` (2230 linhas).
- **Pendência do DBA RESOLVIDA**: `REGISTA_REMUNERACOES`/`REGISTA_PAGAMENTO` agora recebem `P_TIPREL_ID` (assinatura linha 802) e **inserem em `rh_t_tiprel_rem_pag`** após cada def (linhas 1092, 1150, 1196, 1242, 1310 do dump). Antes ignoravam o tiprel → ecrã vazio.
- **Gate no Java implementado e provado**: proc só corre em Progressão/Promoção. Em `carreira/CarreiraWriteService.java:612-614` (`carreiraMesmoTipo != null && ehProgressaoPromocao(carreira)` → `registarSalarioProgressao`). Helper `ehProgressaoPromocao` lê `tipo_situacao`→`CARREIRA_PROG_PROMO`.

## Decisions made — do not re-litigate

- Proc não faz commit autónomo (testado: DbProc+rollback→0 registos).
- Proc é dono do dinheiro na progressão (RETROATIVOS/câmbio/IUR-INPS que o Java não faz). Java mantém máquina de estados/estrutura.
- Java NÃO chama o proc em "carreira nova do mesmo tipo" (só progressão/promoção real).

## Relevant files

- carreira/CarreiraWriteService.java:612-614 — gate do proc
- carreira/CarreiraWriteService.java:~97 — helper `ehProgressaoPromocao`
- CarreiraReadService.java:194-218 — read via TIPREL_REM_PAG (a prova de que o fix do DBA resolve o ecrã)
- scratchpad/REGISTO_SALARIO_new.txt — corpo actual do proc (nova versão DBA)

## Correção FEITA e PROVADA (2026-08-21)

Java deixou de escrever dinheiro na progressão (gate `ehProgressaoPromocao`). 4 edições em `carreira/CarreiraWriteService.java`:
- `criarPendenteContentor`: não cria def (vencimento/subsídios/descontos) na progressão.
- `validarCarreira`: bloco mesmo-tipo só corre dinheiro/fecho na NÃO-progressão; ativação de def gated; **proc chamado ANTES de fechar o tiprel/carreira antigos** (o proc precisa do antigo est_act_adm=1).

Teste live end-to-end (colaborador novo 958895, escalão F 146505 → A 186980): preview do pendente VAZIO (Java não escreve); após SIM, **1 único vencimento** (1422=186980) no tiprel novo, antigo (1421) fechado por data_fim; fixos INPS/IUR/VL recriados pelo proc no tiprel novo. **Zero duplicação.** Ver [[project_progressao_java_vs_proc]].

## Next step

Commitar. Depois: (1) retroativos IUR/INPS estão comentados no proc — DBA. (2) Preview do checker na progressão mostra só cabeçalho (sem linhas) — decidir se é preciso "simular" (SIMULAR_AUMENTO) ou aceitar. (3) Retomar CORRIGIR+JaVers restantes (dossieFix.md).

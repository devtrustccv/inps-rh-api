> Updated: 2026-08-29 ~15:30

## Goal

Testar/afinar em live **Ativar/Desativar Contrato** (`AlterarEstadoContrato`, PATCH de estado) no
dossiê do colaborador **C**. **Ciclo negativos → DESATIVAR (A→I) → REATIVAR (I→A) do 717: CONCLUÍDO e
validado end-to-end.** A sessão descobriu um **defeito de modelação** (`est_act_adm` sobrecarregado) que
fica documentado como task própria — ver Findings + [[project_est_act_adm_sobrecarga]].

## Current state

`develop` HEAD `c4438d25` (16 commits à frente do handoff anterior `705a2576`, todos de OUTRAS features —
o toggle `AlterarEstadoContratoService`/`ContratoController` NÃO foi tocado; **sem alterações de código
nesta sessão**). **App UP** na 8089 (profile development, boot via `scratchpad/run_develop_8089.sh`; REST
passa sem token). Build limpo (JDK23).

**Ciclo de teste COMPLETO (tudo capturado em `scratchpad/ad_*`):**
- Negativos N1–N4: todos 400, sem escrita (ver secção abaixo).
- **DESATIVAR 717 (A→I)**: 200 "Contrato desativado."; cascata das 9 tabelas para I confirmada
  (contrato 717→I, tiprel 173371→I/est_act_adm=**0**, histórico 264→I/est_act_adm=**1** breadcrumb,
  mob 684/carreira 780/regime 620/situação 666→I, def rem 1459-1460 + pag 1626-1630→I).
- **REATIVAR 717 (I→A)**: 200 "Contrato ativado."; tudo repõe A, tiprel est_act_adm→1, histórico A/1,
  invariante = 1 tiprel corrente.
- **C == baseline** (diff vs `rollback_C.sql` vazio) — dossiê limpo, rollback nunca foi preciso.

**Open question do desativar RESOLVIDA:** `RH_V_CONTRATO.atual` deriva do **histórico**
(`d.est_act_adm`, mantém 1) → 717 fica `atual:true` mesmo desativado. Já `RH_V_RELACAO_LABORAL` e os
finders leem o **tiprel** (=0) → **split-brain** (ver Findings).

**Rede de rollback CONSTRUÍDA e VALIDADA** (`scratchpad/`):
- `snapshot_C.sh` — query que emite os próprios UPDATEs de reposição (estado + est_act_adm) das 9 tabelas
  que o service muta, scoped a `fun_id=958913`.
- `rollback_C.sql` — **baseline atual do C**, 29 UPDATEs capturados live. Âncora fixa (não regenerar
  durante o ciclo de teste — decisão do user).
- `restore_C.sh` — executa o rollback num bloco PL/SQL atómico via DbExec. Smoke-test idempotente passou
  (diff pós-restore == baseline, byte-a-byte). **Botão de pânico: `bash scratchpad/restore_C.sh`.**

**Negativos (passo 2) — TODOS 400, sem escrita** (estado confirmado intacto após): N1 ativar 717 já-A →
"Só é possível ativar um contrato inativo (estado I). Estado atual: A."; N2 estado `X` → "Estado inválido:
use 'A'…'I'."; N3 desativar 716 (antigo) → "Só é possível desativar o contrato atual do funcionário.";
N4 estado vazio → validação `@NotBlank`.

## Findings desta sessão (NOVO — decisão de negócio/arquitetura pendente)

**`est_act_adm=1` do tiprel está SOBRECARREGADO com dois conceitos: "relação CORRENTE" E "relação ATIVA".**
Ao desativar, o service põe o tiprel a 0 (`AlterarEstadoContratoService.java:156`), o que **órfã o
funcionário de relação corrente** (fica Ativo mas com 0 tiprels est_act_adm=1). Consequências:

- **Split-brain:** `RH_V_CONTRATO.atual` (histórico) diz `true`; `RH_V_RELACAO_LABORAL` + ~9 finders
  (tiprel) fazem o funcionário evaporar de listas/pesquisas/abonos/empréstimos enquanto desativado.
- **Levantamento (memória [[project_est_act_adm_sobrecarga]]):** Grupo A "corrente" (perde o funcionário):
  `TiposRelacionamentoEntityRepository` L91/163/172/185/246/291/381, `Abonos` L52/95, `Emprestimo` L81,
  toda a `FuncionarioRules.getTipoRelacionamentoAtual*/getContratoAtual`. Grupo B "ativo" (vazio é correto):
  `TiposRel` L117 `findAtivaByCarreiraUuid` (já tem par `...OrderByIdDesc`).
- **Invariante "≤1 tiprel est_act_adm=1 por funcionário": SEGURO** — validado empírico (39=39, zero
  violações) e continuaria seguro sob "manter 1" (os fluxos fecham o anterior via est_act_adm=1).
- **Porque NÃO é fix de 1 linha:** `NovoContratoService.java:100-110` foi escrito a contar com desativar→0
  ("desativado ⇒ contrato fresco, sem anterior a fechar" via `getTipoRelacionamentoAtualOrNull`). Mudar
  para "manter 1" inverte esse branch E faz o contrato desativado mascarar-se de "corrente ativo" p/ todos
  os callers de `getTipoRelacionamentoAtual` — problema atual ao contrário — a menos que se acrescente
  `AND estado='A'` onde se quer "ativo".
- **Fix correto (task própria, c/ testes + negócio):** separação de modelo — ponteiro "corrente" que
  sobrevive à desativação + `estado` a carregar ativo/inativo + `estado='A'` nos sítios "ativo". NÃO hackear
  o toggle. Comportamento atual é coerente e reversível.
- **Sobre reutilizar `AlterarEstadoContratoService` dentro do Novo Contrato: NÃO** (intenções diferentes —
  data_fim, histórico breadcrumb=1, filhos reutilizados). Partilhável só a mecânica de baixo nível
  "cascatear estado aos filhos/def" (candidato a helper privado), não o service inteiro.

## Decisions made — do not re-litigate

- **NÃO refactorar o finder** `findFirstByContrVinculoId_UuidOrderByIdDesc`. Chegámos a propor trocá-lo por
  `...AndEstActAdm(uuid,1)` mas isso **parte a REATIVAÇÃO**: no desativar, o `est_act_adm` do **tiprel** vai
  a **0** (`AlterarEstadoContratoService.java:156` → `alvo==A?1:0`), logo ao reativar não haveria tiprel
  est_act_adm=1 nesse contrato → erro falso. O breadcrumb `est_act_adm=1` que sobrevive ao desativar está no
  **HISTÓRICO** (`RH_T_CONTRATO_HISTORICO`), não no tiprel; o guard de ativação usa o histórico. O
  `ORDER BY id DESC` é intencional: única âncora estável de "tiprel corrente do contrato" (est_act_adm do
  tiprel não serve). Ver comentário do repo `TiposRelacionamentoEntityRepository.java:36-40`.
- **Correção ao modelo mental** (handoff anterior tinha ERRADO): "contrato desativado mantém est_act_adm=1"
  é verdade só para o **histórico**; o **tiprel** vai a 0 no desativar.
- Baseline atual = âncora fixa durante o ciclo (desativar→inspecionar→restore→fix→re-testar sem regenerar).
- Âmbito da sessão: ciclo completo negativos→desativar→reativar. Passo "Novo Contrato após desativar"
  fica **para depois**.
- Funcionário NÃO é tocado no toggle (`funcionario.estado` intacto); só o contrato + filhos do tiprel
  (mob/carreira/regime/situação) + situação-laboral do contrato + histórico + def associadas.
- No GET lista, `atual` = `est_act_adm=1` da vista (`ContratoMapper.java:48`), **não** do estado. Verificar
  empiricamente no desativar: como o tiprel vai a 0 MAS o histórico mantém 1, confirmar de qual coluna a
  vista `RH_V_CONTRATO` deriva o `atual` (define se 717 continua `atual:true` após desativar — ainda por
  confirmar; a expectativa depende da vista).

## Constraints

- Compilar com `JAVA_HOME=.../Eclipse Adoptium/jdk-23.0.2.7-hotspot`. SQL direto: helpers em `tools/db/`
  (correr de LÁ). **ojdbc11 `23.7.0.25.01`** (o do q.sh/x.sh dá "No suitable driver"). **DbExec** para DML
  (DbUpdate rebenta ORA-17273). Oracle XE: sem `FETCH FIRST` → `ROWNUM`. `-cp` com path Windows (`C:\...`).
- GET antes de cada escrita; **ids em todos os arrays**; pedir autorização por cada fluxo de escrita;
  negativos antes do happy path. contratoId no path = UUID. Resposta crua (JSON+HTTP), **identada**.
  Manter `scratchpad/` como referência viva. Ver [[feedback_fluxo_validacao_teste]].

## Blockers & risks

- App UP; BD acessível (62.84.179.137:1521:xe). Se app cair, rearrancar (How-to).
- Fragilidade conhecida-e-aceite: `ORDER BY id DESC` assume "maior id = tiprel corrente do contrato".
  Válido hoje (renovação/mudança de situação clonam o tiprel pondo o novo como corrente). Não mexer.
- Backdating do C por SQL é artefacto de teste (encargos INICIAIS com data_fim=2025-08-28 em vez de 2024).

## Relevant files

- `funcionario/interfaces/rest/ContratoController.java:206-235` — PATCH `{idFunc}/contratos/{contratoId}/estado`,
  body `{ "estado": "A"|"I" }`. idFunc=UUID funcionário, contratoId=UUID contrato.
- `funcionario/application/service/AlterarEstadoContratoService.java` — alvo. Finder tiprel L74-77; guards
  L99-140; `aplicarEstado` L146-174 (**L156 tiprel est_act_adm→0 no desativar**; L170-173 histórico mantém 1).
- `funcionario/application/service/ContratoHistoricoWriteService.java:132-185` — `transicionarEstado`
  (contrato+situação+histórico; est_act_adm do histórico só sobe na ativação).
- `funcionario/infrastructure/mappers/ContratoMapper.java:48` — `atual = est_act_adm==1` (da vista).

## How to verify / resume

- App: `bash scratchpad/run_develop_8089.sh > scratchpad/boot_8089.log 2>&1 &`; UP quando
  `curl -s -o /dev/null -w "%{http_code}" http://localhost:8089/swagger-ui.html` = **302** (~90s).
- SQL: `cd tools/db && OJ='C:\Users\ivanick.santos\.m2\repository\com\oracle\database\jdbc\ojdbc11\23.7.0.25.01\ojdbc11-23.7.0.25.01.jar' && java -cp ".;$OJ" DbQuery "<SQL>"`
- **Rollback a qualquer momento: `bash scratchpad/restore_C.sh`** (repõe baseline; corre com app parada).
- Verificar estado == baseline: `bash scratchpad/snapshot_C.sh 958913 | grep -v '^LINHA$' > /tmp/v.sql && diff scratchpad/rollback_C.sql /tmp/v.sql`.
- IDs do C: fun_id **958913**, uuid funcionário `01a04336-6953-7e81-9a15-7aee349dd6c7`. Contrato atual
  **717** uuid `1f1a24ec-6815-64a1-a339-bd34151ea9fa`, tiprel **173371** (est_act_adm=1, estado A). Antigos:
  contrato 716 uuid `1f1a2139-0f5e-6d49-8d3f-7be00928ad97` (estado I), tiprels 173366/173369 (A, est_act_adm=0).
- Pretty-print: `curl … -w "\n__HTTP__:%{http_code}"` → `python -c` que faz `rpartition("__HTTP__:")` +
  `json.dumps(indent=2, ensure_ascii=False)`.

## Test / validation plan — DESATIVAR/REATIVAR 717 (próximo passo)

Baseline confirmado (`scratchpad/ad_lista_antes.json`): 717 A/atual=true/processamento=false; 716 v2 e v1 I.

1. **GET lista** `…/contratos?idFuncionario=01a04336-6953-7e81-9a15-7aee349dd6c7` → confirmar 717 A/atual (identado).
2. **Negativos** (JÁ FEITOS — `scratchpad/ad_*` / secção Current state). Refazer só se re-arrancar do zero.
3. **DESATIVAR (A→I)** — pedir autorização: PATCH `…/958913uuid/contratos/{717uuid}/estado` body `{"estado":"I"}`
   → esperar 200 "Contrato desativado.". Verificar em BD (`DbQuery`):
   - `rh_t_contrato_vinculo` 717 → **I**.
   - `rh_t_tipos_relacionamento` 173371 → estado **I** e **est_act_adm=0** (confirmar a queda a 0).
   - `rh_t_contrato_historico` 264 → **I**, **est_act_adm continua 1** (breadcrumb).
   - filhos do 173371: mob 684? / carreira 780 / regime 620 / situação (665/666) → **I**; def de 717
     (rem 1459-1460, pag 1626-1630 ou o subconjunto associado) → **I**.
   - **GET lista** → confirmar o que acontece ao flag `atual` do 717 (depende da coluna que a vista usa —
     ver Open questions). Capturar JSON+HTTP em `scratchpad/ad_desativar_*.json`.
4. **REATIVAR (I→A)** — pedir autorização: PATCH `…/{717uuid}/estado` body `{"estado":"A"}` → esperar 200
   "Contrato ativado." (guard: falha se existir outro em vigor — não deve, 716 está I). Verificar BD: 717→A,
   173371 estado A + est_act_adm=1, histórico 264 A/1, filhos+def → A. GET lista → 717 volta A/atual.
5. **Reconfirmar coerência** INICIAL (`716uuid?versao=1`) vs ATUAL (`717uuid`) após o ciclo.
6. Se qualquer passo divergir → `bash scratchpad/restore_C.sh`, investigar, (fix), re-testar.

## Open questions

- ~~Após desativar, `RH_V_CONTRATO.est_act_adm` deriva do tiprel ou do histórico?~~ **RESOLVIDO:** deriva do
  **histórico** (`d.est_act_adm`), fica `atual:true` mesmo desativado. Gera split-brain — ver Findings.
- "Processado em folha" (guard que bloqueia desativar): 717 tem processamento=false, logo passa; como
  forçar `RH_T_PROC_FUNCIONARIOS` para testar o negativo fica para depois, se necessário.

## Next step

Ciclo de teste do toggle **fechado**. Decisão pendente do user/negócio: avançar (ou não) com a **task de
separação de modelo** do `est_act_adm` (ver Findings) — começaria por esboçar o helper de cascata + auditar
os finders do Grupo A/B e acrescentar `estado='A'` onde se quer "ativo". Passo "Novo Contrato após
desativar" continua **para depois**. Nada em aberto no dossiê do C (== baseline).

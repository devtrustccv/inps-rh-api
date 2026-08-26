# Handoff — Melhorias Dossiê do Colaborador

> Updated: 2026-08-26.
> HANDOFF DETALHADO desta sessão (o `/resume dossier_melhorias` lê ISTO). Fonte única.
> Paths de código são relativos ao worktree: `.claude/worktrees/dossier-melhorias/`.

---

## 0. Estado live (2026-08-26, sessão de testes) — BD DE VOLTA

**BD Oracle** `62.84.179.137:1521` (user `INPSRH`) **acessível de novo**. Migração de schema
**APLICADA** e app **a correr no 8089** (BD ligada). Progresso das fases:

| Fase | Estado |
|---|---|
| 0 Pré-voo (T0.1–T0.5) | ✅ |
| Fix domínio `TIPO_SALARIO_VINCULO` (BD tinha 0/1/2, doc pede literais) | ✅ aplicado |
| 1 Backfill `FLG_SALARIO`→String + reshape checks + FK escalão | ✅ aplicado |
| 2 Boot 8089 (Hikari liga, sem ORA-00904) | ✅ |
| 3 GET vínculos/ativos → `flgSalario` domínio String | ✅ verde |
| 4 (T4.1) Registo PCCS sem carreira → tiprel.escalao_id + salário do escalão | ✅ verde |
| 4 (T4.2) Validar registo SIM → tiprel A + REM (DEF_REMUNERACOES) = valor do escalão | ✅ verde |
| 4 (T4.3) SIM_FORA_PCCS (vínc 9) → tiprel escalao_id=null + salário manual | ✅ verde |
| 4 (T4.4) COM carreira (vínc 1) → escalão na carreira, tiprel.escalao_id=null | ✅ verde |
| 5 Novo contrato sem-carreira PCCS → tiprel novo escalao_id=21 + REM=escalão; antigo fecha por data (fica A, EST_ACT_ADM=0) | ✅ verde |
| 6 Lista Gestão Laboral → `escalao` preenchido (carreira E sem-carreira via vista corrigida) | ✅ verde total |
| 7 Alterar Escalão/Cargo — T7.3 cargo-só imediato | ✅ verde |
| 7 T7.4 escalão → tiprel P + validação P (aparece na lista GESTAO_LABORAL) | ✅ verde |
| 7 T7.5 validar SIM → antigo I, novo A, REM antiga fechada por data, nova=escalão, TIPREL_REM_PAG reassociado | ✅ verde |
| 7 T7.6 validar NAO → tiprel pendente I, salário ativo intacto | ✅ verde |
| 7 T7.7 CORRIGIR (P→C) + re-POST reabre (C→P) sem duplicar | ✅ verde |
| 7 T7.8 Detalhe de alterações (JaVers) | 🔴 GAP — grelha vazia (ver §5) |
| 8 Remunerações filtros | ⏳ |
| 9 Regressão | ⏳ |

NOTA: "antigo I" no plano era impreciso — o modelo mantém A e fecha por data_fim (REM antiga
2024-01-01→2024-12-31 fechada, nova 2026-08-26 aberta). Colaborador FASE 5: uuid
`01a03f90-fbf3-7924-ad2c-4b5d599a79b3` (fixture). Helper `scratchpad/validar_registo.py` faz o
round-trip get→validar preservando ids dos arrays.

Fixes commitados: **32afd029** (worktree feat/dossier-melhorias).

**GOTCHAS live (importantes):**
- **`.env` (raiz) tem password que `source` do bash corrompe** (chars especiais) → ORA-01017.
  Arrancar via loader Python que lê `.env` LITERAL: `scratchpad/launch8089.py` (força `SERVICE_PORT=8089`,
  JDK23). NÃO usar `set -a; source .env`.
- **Domínio drift:** `RH_T_DOMAINS.TIPO_SALARIO_VINCULO` estava com VALOR `0/1/2`; corrigido para
  `NAO/SIM_PCCS/SIM_FORA_PCCS` (doc `MELHORIAS_DOSSIER.md` L52/65 manda literais). IDs 361→SIM_PCCS,
  362→SIM_FORA_PCCS, 363→NAO.
- **FIXES de código aplicados** (caminho PCCS-sem-carreira não funcionava — descobertos no teste live):
  1. `ValidarDadosContratuaisService` — derivar `dc.salario` do escalão também para **PCCS SEM
     carreira** (antes só `flgCarreira==1`), senão "valor do salário é obrigatório".
  2. `DadosContratuaisMapper.dadosContratuaisRespDTO` — expor `escalaoReferenciaId` a partir do
     `tiprel.escalaoId` quando não há carreira (senão o get-by-id devolve null e a validação rejeita
     "Escalão é obrigatório"). **Cobre get-funcionário E get-contrato** (ambos usam este mapper).
  3. `HistoricoLaboralReadService` (2 reads de exibição) — mesmo fallback de escalão (histórico/detalhe).
  4. `ValidarRegistoColaboradorService:189` — null-guard `if (carreira != null)` antes de
     `carreiraMapper.toUpdateEntity` (NPE na validação sem carreira).
  - Campo `validar` (EstadoValidacao SIM/NAO/CORRIGIR) é **top-level** no FuncionarioRequestDTO; sem ele
    a validação só "atualiza" (fica P), não transita A. Recompilar+reiniciar após tocar código.

**FIXTURES DE TESTE criados (LIMPAR no fim / antes do merge):**
- `RH_T_PARAM_VINCULO` id **17** ('TST_PCCS_SC', SIM_PCCS, flg_carreira=0).
- `RH_T_PARAM_VINCULO_MOV` para vínculo 17: 1×REM (tm 1681) + 3×PAG (tm 1940/1741/1680).
- Colaborador de teste FASE 4: funcionário uuid `01a03f71-8385-72e3-a37f-7a0c8f94bdbb`
  (tiprel 173356, escalao_id=21, salario=186980, estado P).

Confirmar BD: `java -cp ".;C:\Users\ivanick.santos\.m2\repository\com\oracle\database\jdbc\ojdbc11\21.9.0.0\ojdbc11-21.9.0.0.jar" DbQuery "SELECT 1 FROM dual"` (da raiz do repo).

## 1. Goal

Implementar melhorias de `docs/MELHORIAS_DOSSIER.md` (secção 2 — Dossiê), cruzadas com a spec
`docs/Especificação Tecnica Funcional - DOSSIÊ DO COLABORADOR_19_08_26.md` e prints. Isolado no worktree
`feat/dossier-melhorias` (base develop@aaaffb46) para não colidir com a app no 8088.

## 2. Current state — TESTADO LIVE FASES 0-7 (ver §0 para a tabela)

Commits no branch `feat/dossier-melhorias`: `5462d631`, `a7a47659`, `7fb1a871`, `58aaf43d` (handoff),
`32afd029` (**fixes PCCS sem carreira** — registo/validação), `f21aa798` (**vista relacao-laboral**).
Handoff (este doc) commitado no repo principal `develop`: `da22e328`.

| Item | Estado |
|---|---|
| 2.2.2 Regime / 2.4 Detalhe (JaVers) | ✅ já conforme (nada a fazer) |
| 2.5 Conversão/Renovação | ⏭️ job do transversal |
| Break change `FLG_SALARIO` Integer→String (`TIPO_SALARIO_VINCULO`) | ✅ |
| 2.1 Escalão no tiprel ao registar/validar colaborador **e contrato** | ✅ |
| 2.2.1 Lista Gestão Laboral `categoria`→`escalao` | ✅ |
| 2.2.1 "Alterar Escalão/Cargo" (validação + CORRIGIR C↔P + JaVers) | ✅ |
| 2.3 Remunerações filtros `situacaoLaboral` + `contrVinculo` | ✅ |
| Manifestos IGRP `.igrpstudio/**.json` | ✅ |
| Script SQL backfill | ✅ escrito, por aplicar |

## 3. Decisões — NÃO re-litigar

1. "Alterar Escalão" sem carreira → **o Java escreve o salário** (`PKG_AUMENTO_SALARIAL` filtra por
   CARREIRA_ID, rebentaria — ORA-01403).
2. `flg_salario` = **String** + enum `TipoSalarioVinculo` validado no service (não `@Enumerated`).
3. Backfill ambíguo (`flg_salario=1 & flg_carreira=0`) → default **`SIM_FORA_PCCS`**; promover PCCS à
   mão depois. No código, legado/desconhecido nunca conta como PCCS (só o literal `SIM_PCCS`).
4. "Tipo Alteração" multiselect: front envia **CSV**, grava tal-e-qual em `TIPO_SITUACAO`; decidir o
   que alterar **só** por `novoEscalaoId`/`novoCargoId` (comparar códigos por string foi **rejeitado,
   frágil**).

## 4. Implementação (por feature)

### 4.1 Break change `FLG_SALARIO` → `TIPO_SALARIO_VINCULO` (SIM_PCCS/SIM_FORA_PCCS/NAO)
- Novo `shared/application/constants/custom/TipoSalarioVinculo.java` (helpers `isValido`,
  `temSalario`=!NAO tolerando "0"/"1", `ehPccs`=SIM_PCCS).
- `Domains.java` +`TIPO_SALARIO_VINCULO`; `ParamVinculoEntity.flgSalario` Integer→String.
- `configuracao/ParamVinculoService` valida ao gravar + `remuneracaoDesc` do domínio novo;
  `VinculoLaboralResponseDTO.salario` Integer→String.
- `parametrizacao/ParamVinculo` + `VinculoDTO`: `flgSalario` Integer→String.
- Consumidores "tem salário" → `TipoSalarioVinculo.temSalario(...)`: `ColaboradorValidationRules`,
  `ValidarDadosContratuaisService`, `ValidarContratoService`(2×), `RegistarColaboradorService`,
  `NovoContratoService`(2×). ⚠️ `Objects.equals(1, String)` compila mas é sempre falso — todos trocados.

### 4.2 Escalão no tiprel (2.1)
- `TiposRelacionamentoEntity` +`escalaoId` (`@ManyToOne`→`RH_T_PARAM_ESCALAO`, col `escalao_id`);
  `DadosContratuaisMapper.clone()` carrega-o.
- Helper `ColaboradorValidationRules.aplicarEscalaoTiprelSemCarreira(...)` (+overload por vinculoId):
  sem carreira + SIM_PCCS + escalão → grava `tr.escalaoId` e `tr.salario = escalão.valor`.
- Chamado em: `RegistarColaboradorService`, `NovoContratoService`(2×), `ValidarRegistoColaboradorService`
  (deriva salário do escalão p/ reconciliar), `ValidarContratoService`.

### 4.3 Lista Gestão Laboral (2.2.1)
- `RelacaoLaboralSumaryDTO`: `categoria`→`escalao`; `HistoricoLaboralReadService.getRelacaoLaboral`
  preenche de `RH_V_RELACAO_LABORAL.ESCALAO_DESC`.

### 4.4 "Alterar Escalão/Cargo" (2.2.1)
- `dto/AlterarEscalaoCargoDTO` (tipoAlteracao String CSV, novoEscalaoId, novoCargoId, dataInicio,
  dataFim, observacao, validar); commands+handlers Alterar/Validar; endpoints no `HistoricoLaboralController`
  (`POST .../alterar-escalao-cargo`, `PUT .../alterar-escalao-cargo/{tiprelUuid}`).
- `service/historicolaboral/AlterarEscalaoCargoService`:
  - Guard: só `ehPccs` + **sem** carreira.
  - Só Cargo → imediato no tiprel atual (cargo+datas+tipoSituacao); sem validação.
  - Escalão(±cargo) → tiprel pendente P (clone do atual), datas do form, TIPO_SITUACAO=CSV, cria
    `ValidacaoEntity` P (ref `GESTAO_LABORAL`), carimba JaVers.
  - CORRIGIR C→P: se existir movimento em C derivado do atual, reabre-o (`reabrirParaValidacao`).
  - validar(): SIM consolida (fecha vencimento antigo por data_fim mantendo 'A'; novo `DEF_REMUNERACOES`
    = valor do escalão com data_inicio/data_fim do form; reassocia `TIPREL_REM_PAG` excluindo o fechado;
    fecha tiprel antigo I; ativa o novo A). NAO→I. CORRIGIR→C.
- `Referencia` +`GESTAO_LABORAL`; `TiposRelacionamentoEntityRepository` +`findFirstByTiprelId_IdAndEstado`
  +`@JaversSpringDataAuditable`; novo `GestaoLaboralValidacaoDetalheDescriptor`.
- Lista de validações (`ValidacoesReadService`) apanha-a (filtra só estado=P); rótulo "Gestão Laboral".

### 4.5 Remunerações (2.3)
- `RenumeracaoController`+`GetListRenumeracoesQuery`+`RenumeracoesReadService`: filtros `situacaoLaboral`
  e `contrVinculo` (IDs na vista). Default (sem tiprelUuid) = vínculo ativo (`estActAdm=1`). Escalão como
  filtro 🔴 não dá (vista sem coluna) → DBA.
- **FASE 6 RESOLVIDO na vista (não é DBA):** `RH_V_RELACAO_LABORAL` recriada
  (`docs/db/melhorias_dossier_view_relacao_laboral.sql`) para expor o escalão do tiprel em PCCS
  sem-carreira: COALESCE(escalão-carreira, escalão-tiprel) + filtro final tolera carreira null.
  ⚠️ ARMADILHA: no DESC, fazer COALESCE **campo-a-campo** (`COALESCE(j.NIVEL,jt.NIVEL)||'/'||
  COALESCE(j.ESC,jt.ESC)`) — concatenar primeiro dá `'/'` (não-null) e a COALESCE nunca cai para jt.
  Verificado: linhas com carreira 42→42 idênticas; sem-carreira 0→4; F4 escalao="13/A".
  Aplicar a vista via `DbExec` **antes** de confiar no endpoint relacao-laboral para sem-carreira.

### 4.6 Manifestos IGRP
`configuracao/dto/VinculoLaboralResponseDTO`(salario string), `funcionario/dto/RelacaoLaboralSumaryDTO`
(categoria→escalao), `parametrizacao/dto/VinculoDTO`(flgSalario string), `funcionario/dto/GetListRenumeracoesQuery`
(+2), `funcionario/controllers/RenumeracaoController`(+2), `shared/models/ParamVinculoEntity`(flg_salario
string), `shared/models/TiposRelacionamentoEntity`(+escalao_id), `shared/enum/Domains`(+TIPO_SALARIO_VINCULO),
`funcionario/dto/AlterarEscalaoCargoDTO`(novo)+2 ações no `HistoricoLaboralController`. Enums hand-written
(`TipoSalarioVinculo`, `Referencia`) sem manifesto.

## 5b. GAP T7.8 — Detalhe de alterações (JaVers) vazio para Gestão Laboral

**Sintoma:** `GET validacoes/{tiprelUuid}/detalhes` para uma alteração de escalão devolve `[]` (HTTP 200).
**Causa raiz (confirmada):** `JaversValidacaoDetalheReadService` (linha ~105-118) decide a semântica pela
`TipoAcao` da validação. A alteração de escalão regista-se como **UPDATE**, então o serviço **exclui os
`InitialValueChange`**. Mas o fluxo cria um **tiprel NOVO (clone)** — o JaVers só tem 1 snapshot (inicial),
logo TODAS as mudanças são `InitialValueChange` → todas filtradas → grelha vazia. (Confirmado: JV_SNAPSHOT
tem 1 linha p/ 173361 e 173363; descriptor GESTAO_LABORAL registado; validação 987 REFERENCIA_ID=173363.)
O piloto de mobilidade funcionou porque era UPDATE **in-place** (2+ snapshots, diff real).
**Opções de fix (decisão de negócio/design):**
  (a) Tratar GESTAO_LABORAL como "criação" na grelha (manter InitialValueChange) → mostra os valores NOVOS
      mas sem o "antes"; **ou**
  (b) diff cross-instância: comparar o snapshot do tiprel novo com o do tiprel ativo anterior (antes→depois
      reais) — mais trabalho, altera o serviço partilhado.
Também confirmar se o commit JaVers do clone é etiquetado com `PROP_VALIDACAO_UUID` (a query filtra por
isso; se não etiquetar, devolve vazio independentemente do (a)/(b)).

## 5. Blockers & riscos
- BD inacessível (§0).
- `RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID`: V1 schema (desatualizado) diz NOT NULL, mas a entidade não a
  mapeava e a app inseria tiprels → na BD real ausente ou nullable; a vista expõe ESCALAO_ID. O SQL
  adiciona (nullable) se faltar. **Correr o SQL ANTES de arrancar no 8089** (senão ORA-00904).
- Valores do domínio `TIPO_MOV_LABORAL`/`GESTAO_LABORAL` — investigar na BD (não na doc).
- `@JaversSpringDataAuditable` no tiprel = app-wide (custo de escrita extra; commits sem contexto
  inofensivos).
- `AlterarEscalaoCargoService`: na consolidação o "atual" vem de `pendente.getTiprelId()` (snapshot do
  registo); se outro movimento validar entre registo e validação, fica desatualizado (a carreira
  recompõe do atual do momento). Aceitável no caso simples.

## 6. Como compilar / correr
- **JDK 23 obrigatório** (Maven default usa 21 → falha): `export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"`.
- Compilar: `cd .claude/worktrees/dossier-melhorias && mvn -o -DskipTests compile` (só warnings Lombok).
- Correr no 8089: `SERVICE_PORT=8089 mvn spring-boot:run` (8088 é do utilizador).
- SQL direto: `DbQuery.java` (SELECT), `DbExec.java` (DDL/DML) na raiz. **Não usar `DbUpdate` (ORA-17273)**.
  Acentos: `UNISTR`.

## 7. Test / validation plan (executar quando a BD voltar) — EXEMPT do limite
> Regras do utilizador: imprimir sempre resposta crua (JSON+HTTP status); get-by-id ANTES; arrays com id;
> output pretty; pedir autorização por cada fluxo de escrita; uuid no path.

**FASE 0 — pré-voo:** T0.1 `DbQuery "SELECT 1 FROM dual"`. T0.2 col ESCALAO_ID do tiprel (existe/nullable).
T0.3 domínio `TIPO_SALARIO_VINCULO` (3 valores). T0.4 domínio `TIPO_MOV_LABORAL`+ref `GESTAO_LABORAL`
(descobrir nome exato da tabela de domínios). T0.5 `flg_salario`,`flg_carreira` counts pré-backfill.

**FASE 1 — backfill (com autorização):** aplicar `docs/db/melhorias_dossier_tipo_salario.sql` via `DbExec`
(ADD col, UPDATE, DROP, RENAME, bloco PL/SQL ESCALAO_ID, COMMIT). Verificar contagens; listar SIM_FORA_PCCS
para revisão manual.

**FASE 2 — boot 8089:** arrancar; confirmar Hibernate mapeia escalaoId + flgSalario String sem ORA-00904.

**FASE 3 — parametrização vínculo:** GET vínculo → remuneracao/salario = SIM_PCCS/... + desc do domínio novo;
POST criar com SIM_PCCS → ok; POST valor inválido → 400.

**FASE 4 — 2.1 colaborador:** T4.1 registar com vínculo flgCarreira=0+SIM_PCCS+escalaoReferenciaId →
tiprel.escalao_id + salario=escalão. T4.2 validar → REM com valor do escalão. T4.3 SIM_FORA_PCCS → sem
escalão, salário manual. T4.4 com carreira → via carreira (intacto).

**FASE 5 — 2.1 contrato:** novo contrato sem-carreira SIM_PCCS → escalao_id; validar → salário do escalão.

**FASE 6 — 2.2.1 lista:** GET `{funcionarioId}/relacao-laboral` → `escalao` preenchido, `categoria` ausente.

**FASE 7 — Alterar Escalão/Cargo:** T7.1 com carreira→400. T7.2 não-PCCS→400. T7.3 cargo só → imediato,
sem validação. T7.4 escalão → tiprel P + validação P, aparece na lista "Gestão Laboral". T7.5 validar SIM →
vencimento antigo fechado(data_fim, A), novo DEF_REMUNERACOES=escalão com datas do form, TIPREL_REM_PAG
reassociado, antigo I, novo A. T7.6 validar NAO → I, salário intacto. T7.7 CORRIGIR (P→C) → re-POST reabre
(C→P), não duplica. T7.8 Detalhe de alterações → grelha JaVers com escalão/cargo/salário/datas (rótulos PT).

**FASE 8 — 2.3:** sem filtros → só vínculo ativo; com `situacaoLaboral`/`contrVinculo` → filtra.

**FASE 9 — regressão:** registo COM carreira + progressão intactos; detalhe de mobilidade/carreira intacto.

**Evidências → HTML:** por teste, HTTP status + JSON cru + queries de verificação. Montar
`docs/evidencias_teste_live_dossier.html` explicando cada fluxo e resultado.

## 8. Next step (retomar aqui)
FASES 0-7 corridas live (0-6 e T7.3-T7.7 verdes). App a correr no 8089. Por fazer, por ordem:
1. **Guards T7.1/T7.2** de Alterar Escalão/Cargo: com carreira → 400; não-PCCS → 400 (rápidos).
2. **T7.8** (detalhe JaVers vazio) — decidir fix op.(a) ou (b) — ver §5b. (Utilizador escolheu parar antes.)
3. **FASE 8** (remunerações: filtros situacaoLaboral/contrVinculo) e **FASE 9** (regressão: registo COM
   carreira + progressão + detalhe mobilidade/carreira intactos).
4. Gerar `docs/evidencias_teste_live_dossier.html`, **limpar fixtures** (§0), e **pedir permissão p/ merge**
   (PR `feat/dossier-melhorias` → `develop`, não `main`).

## 9. Ficheiros-chave (relativos ao worktree)
- `docs/db/melhorias_dossier_tipo_salario.sql` — ALTER + backfill.
- `src/.../service/historicolaboral/AlterarEscalaoCargoService.java` — fluxo novo.
- `src/.../rules/ColaboradorValidationRules.java` — helper escalão.
- `src/.../constants/custom/TipoSalarioVinculo.java` — enum do domínio.
- `DbQuery.java` / `DbExec.java` (raiz) — SQL direto.

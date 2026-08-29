# Handoff — Melhorias Dossiê do Colaborador

> Updated: 2026-08-29 (T7.8 testado live + protótipo `javers.compare`→RH_T_VALIDACAO_DETALHE, §0e).
> HANDOFF DETALHADO desta sessão (o `/resume dossier_melhorias` lê ISTO). Fonte única.
> Paths de código são relativos ao worktree: `.claude/worktrees/dossier-melhorias/`.
> Nota: o trabalho pós-merge (§0b em diante) é **direto em `develop`** (raiz do repo), não no worktree.

---

## TODO — pendentes (TRANSVERSAL / JOB Alerta)

Contexto: o "Processar" dos alertas (doc TRANSVERSAL 3.4.2) foi ligado aos fluxos do Dossiê. Detalhe
completo em `.claude/resume/alerta-transversal.md` e `docs/frontend_changes_transversal.md`.

**FEITO (commits em `develop`):**
- `5632df70` — Processar RENOVAÇÃO em lote (`POST .../renovacao-contrato/lote`, atómico, erros agregados).
- `866ef431` — Processar CONVERSÃO via Novo Contrato (`alertaId` opcional UUID no `NovoContratoDTO`;
  maker marca `flg_tratamento='S'`; checker fecha `estado='I'`/repõe `'N'` por `referencia_id`). Lookup
  do alerta por **UUID** (`findByUuid`) nos dois fluxos.

**POR FAZER (só transversal — NÃO é Dossiê):**
1. **JOB → notificação/email** — o JOB (`AlertaWriteService`) só CRIA o alerta (`flg_notificacao='N'`);
   nunca gera notificação nem envia email. Ver **TODO no código** em
   `src/main/java/cv/inps/rh/shared/domain/service/AlertaWriteService.java` (no `executarJobAlertas`).
   Infra pronta a reutilizar: `NotificacaoDispatchService` + `NotificacaoDestinatarioResolver` +
   `OracleEmailService`. **BLOQUEIO DE NEGÓCIO**: a spec **não diz quem recebe** (só define destinatários
   p/ envio manual). Decisão do utilizador (supôs, não fechou): **admin do sistema configurável** +
   talvez **colaborador** e/ou **responsável**. Implementar CONFIGURÁVEL: admin via env/`RH_T_DOMINIO`;
   destinatários por tipo via `RH_T_DOMINIO`; **sem config → não envia** (`flg_notificacao` fica `'N'`).
2. **Gerar alertas em falta no JOB** (hoje só gera renovação/conversão/licença-s-venc): **Doença**
   (`LICENCA_C_VENCIMENTO`) e **Empréstimo** (pagamento atrasado; cessado com dívida). Doc 3.4 linhas ~1185/1211/1247.
3. **Reconciliação `P→I`** do alerta de empréstimo quando a dívida é resolvida (doc ~1224/1257).

**FORA DE ÂMBITO (Processamento Salarial, outra equipa/doc):** Processar `LICENCA_S_VENCIMENTO` e
`LICENCA_C_VENCIMENTO` (abrem ecrãs do Proc. Salarial). Missão Serviço: doc marca "pendente / ver se faz sentido".

**Convenções fixadas (não re-litigar):** alerta identificado por **UUID** (o `uuid` da lista, não o `id`
Long); `flg_tratamento` estende a spec (S ao processar / N se rejeitado; grelha "por tratar" =
`estado='P' AND flg_tratamento='N'`); `RH_T_ALERTA.FLG_TRATAMENTO`/`ESTADO` **sem CHECK** na BD viva
(`'S'/'N'/'I'` seguros).

---

## 0e. PROTÓTIPO — detalhe via `javers.compare` + persistência em RH_T_VALIDACAO_DETALHE (2026-08-29, NÃO commitado)

Decisão do utilizador (discussão arquitetural): em vez de JaVers-histórico OU do reader on-the-fly, usar o
**motor de diff** do JaVers (`javers.compare`, modalidade leve, sem histórico SQL) para **gravar** o
detalhe na tabela que a spec exige, lido pela via comum. Prototipado e **testado live no 8089** (não
commitado — a aguardar decisão de adoção). Ficheiros:
- **NOVO** `service/historicolaboral/EscalaoDetalheDiffWriter` — `javers.compare(snapshotAntes,
  snapshotDepois)` onde `Snapshot` é uma classe com **`@Id` constante** (`org.javers...annotation.Id`)
  para o JaVers comparar duas linhas de tiprel como MESMA instância → `ValueChange` campo-a-campo (senão
  daria NewObject/ObjectRemoved). `persistir()` grava 1 linha/campo em `RH_T_VALIDACAO_DETALHE`;
  `comparar()` devolve DTO (leitura on-the-fly); `limpar()` apaga p/ reenvio de correção. Formatação
  (escalão codigo|nível/escala, salário, datas) centralizada aqui (fonte única).
- `AlteracaoEscalaoDetalheReadService` — refatorado: delega o diff ao `EscalaoDetalheDiffWriter.comparar`
  (usa `javers.compare` também na leitura); só resolve antes/depois e carimba autor/data. Passa a ser
  **fallback** para movimentos antigos sem linhas persistidas.
- `AlterarEscalaoCargoService` — chama `persistir(validacao, atual, novoTiprel)` no registo e
  `limpar()+persistir()` no reenvio de correção (C→P).
- `GetDetalheAlteracoesQueryHandler` — router: ALTERACAO_ESCALAO → se `existsByValidacaoId_Uuid` na tabela
  lê via `ValidacaoDetalheReadService` (comum); senão fallback on-the-fly. **Zero regressão.**

**Testado live (8089, BUILD SUCCESS após `clean`):**
- Registo antigo (val 1012, 0 linhas) → fallback on-the-fly, output idêntico ao de antes (prova refactor).
- POST novo (13/B→13/C, escalão 23) → **4 linhas persistidas** em `RH_T_VALIDACAO_DETALHE`
  (val `01a04cb3-8dc5-7b84-9d11-33bf8f83b50f`): Escalão SEC_CA_13_B→SEC_CA_13_C, Salário 178076→169595,
  Data início 26-08→30-08, Observações. `GET .../detalhes` lê da TABELA, HTTP 200, mesmo `ValidacaoDetalheDTO`.
- Mobilidade (JaVers-histórico) intacta, HTTP 200.

**Vantagens vs reader on-the-fly:** snapshot imutável no momento (robusto a alterações do predecessor);
popula a tabela que a spec manda; reusa o reader comum (sem 3º caminho de leitura).
**Pendente de decisão:** (a) commitar/adotar; (b) generalizar o padrão `compare→persistir` a outros fluxos
de clone; (c) opcional a longo prazo — aposentar o JaVers-histórico pesado. Gotcha Oracle: `target/` tinha
um `.class` corrompido (`ClassFormatError ...ParamLinhaBaseResponseDTO`) → **arrancar sempre após `mvn clean`**.

## 0d. T7.8 RESOLVIDO + ✅ TESTADO LIVE — detalhe de alterações da Alteração de Escalão (reader manual isolado, 2026-08-29)

Feito **direto em `develop`** (a seguir ao `90612692`). Compilado (BUILD SUCCESS, JDK23).
**✅ TESTADO LIVE end-to-end no 8089 (2026-08-29):**
- POST `.../alterar-escalao-cargo` (F4 uuid `01a03f71-...`, escalão 21=13/A) → pendente tiprel 173381,
  validação 1012 (uuid `01a04c8d-1918-7044-86ea-61ab77a007ef`), HTTP 200.
- `GET validacoes/{uuid}/detalhes` → linhas antes→depois com rótulos PT: **Escalão** SEC_CA_13_B→SEC_CA_13_A,
  **Salário** 178076→186980, **Data início** 26-08→29-08, **Observações**. Só campos com diff. HTTP 200.
- Regressão OK: MOBILIDADE (`RH_T_MOBILIDADE`) e CARREIRA (`RH_T_CARREIRA`) continuam via JaVers, HTTP 200.
- Nota: em dev sem auth `alteradoPor=anonymousUser`. Deixado 1 pendente (val. 1012) na BD como evidência.

**Diagnóstico do §5b estava ERRADO** (provado na BD `JV_SNAPSHOT`): não é o filtro `InitialValueChange`.
A causa real é que **`TiposRelacionamentoEntity` é Shallow Reference** (`JaversAuditConfig.REFERENCIAS_RASAS`,
adicionado na 2ª passagem do dossieFix). Consequência: todo commit do tiprel grava `STATE={}` / `CHANGED=[]`
(verificado: snapshots 173373-173380 e até os antigos 173361/173363 têm estado vazio). Logo o JaVers **nunca**
captura os campos do próprio tiprel → opções (a) e (b) do §5b **ambas inviáveis**.

**Porque o shallow tem de ficar:** o tiprel tem **auto-referência** `tiprelId` (cadeia de tiprels
anteriores) + é FK de muitos agregados auditados (Substituicao, ProcessoDisciplinar, EvolucaoCarreira…).
Des-shallow faria cada commit percorrer a cadeia recursiva → o problema de 20-50s que o shallow evita.

**Solução escolhida (decisão do utilizador): reader manual isolado — NÃO toca no JaVers nem nos outros fluxos.**
- Novo `service/historicolaboral/AlteracaoEscalaoDetalheReadService` — compara o tiprel **pendente**
  (`validacao.tiprelId` = "depois") com o **predecessor** (`pendente.tiprelId` = "antes"), ambos linhas
  reais. Diff só dos campos com diferença (semântica de EDIÇÃO). Reutiliza `rotulos()`/`camposNegocio()`
  do `GestaoLaboralValidacaoDetalheDescriptor` (fonte única). Campos: escalão (codigo|nível/escala),
  cargo (nome), salário, moeda, tipoSituacao, dataInicio, dataFim, obs. autor/data via auditoria JPA do
  pendente; tabelaName=RH_T_TIPOS_RELACIONAMENTO.
- `GetDetalheAlteracoesQueryHandler` — branch: se `referenciaName==ALTERACAO_ESCALAO` → reader dedicado;
  **senão → caminho JaVers intacto**. É a ÚNICA referência desviada. +`ValidacaoEntityRepository` para
  ler a referência.
- `JaversValidacaoDetalheReadService` — TODO T7.8 substituído por NOTA a explicar o desvio (nenhuma
  mudança de lógica). `GestaoLaboralValidacaoDetalheDescriptor` — javadoc nota que serve rótulos ao reader.
- ⚠️ **Verificação de "não quebrar os outros":** todos os outros descritores auditam entidades próprias
  (Carreira→CarreiraEntity, Situação→SituacaoLaboralEntity, Substituicao→SubstituicaoEntity,
  Mobilidade→MobilidadeEntity) — nenhum aponta ao tiprel. Só GESTAO_LABORAL apontava. Zero impacto.
- **Gotcha Oracle XE (11g legacy):** sem `FETCH FIRST` — usar `ROWNUM`. Colunas `JV_GLOBAL_ID`: `LOCAL_ID`
  (não `local_id_value`); `JV_SNAPSHOT`: `STATE`,`CHANGED_PROPERTIES`,`TYPE`,`GLOBAL_ID_FK`.

**Por testar live (8089):** registar alteração de escalão → `GET validacoes/{uuid}/detalhes` devolve linhas
antes→depois (escalão/salário/datas com rótulos PT); confirmar que os outros detalhes (mobilidade/carreira/
situação) continuam intactos.

## 0c. Referência de validação ALTERACAO_ESCALAO + TIPO_SITUACAO por domínio (2026-08-29)

Feito **direto em `develop`**, commit **`90612692`**. Verificado na BD live (`RH_T_DOMAINS`) e compilado
(BUILD SUCCESS, JDK23).

**Decisão do utilizador:** o fluxo "Alterar Escalão/Cargo" passa a usar a referência de validação
**`ALTERACAO_ESCALAO`** (não `GESTAO_LABORAL`). Regra: **acrescentar** o enum (NÃO renomear) e trocar o uso.

- `Referencia.java` — **+`ALTERACAO_ESCALAO("Alteração de Escalão")`**; `GESTAO_LABORAL` **mantido**
  (registos de teste antigos ainda resolvem via enum).
- `AlterarEscalaoCargoService` + `GestaoLaboralValidacaoDetalheDescriptor` — todo o uso de
  `Referencia.GESTAO_LABORAL` → `Referencia.ALTERACAO_ESCALAO` (referenciaName da validação, `referente`
  do tiprel, reabrir/devolver-correção, lookup da validação, descriptor JaVers).
- **`setReferente` guarda o NOME da Referencia** (padrão confirmado: Mobilidade/Carreira/Contrato/
  RegistoColaborador) — logo `referente` = `ALTERACAO_ESCALAO`, distinto do TIPO_SITUACAO.
- **TIPO_SITUACAO** (coluna do tiprel, domínio `TIPO_MOV_LABORAL`): deixou de gravar `"GESTAO_LABORAL"`
  (**valor inexistente no domínio** — nunca esteve em `RH_T_DOMAINS`). Passa aos valores válidos
  confirmados na BD: **`ESCALAO_NOVO`** (id 377, fluxo escalão) / **`CARGO_NOVO`** (ids 264/376, fluxo
  cargo-só), como default quando o form não envia `tipoAlteracao`.
- Comentários/javadoc alinhados (service, `AlterarEscalaoCargoDTO`, TODO T7.8 em `JaversValidacaoDetalheReadService`).

**Gotchas:** DbQuery/DbExec vivem em `tools/db/` (não na raiz); URL live `62.84.179.137:1521:xe`, user
`INPSRH`, pass no `tools/db/DbQuery.java`. Colunas do domínio: `DOMINIO/VALOR/DESCRICAO` (não `VALUE`).
Referência `Referencia` é enum app-side (NÃO validado contra domínio) — `ALTERACAO_ESCALAO` como
referenciaName não precisa de entrada em `RH_T_DOMAINS`. **Não testado live end-to-end** nesta sessão
(só compilado + verificação de domínio); testar o fluxo Alterar Escalão quando a app subir no 8089.
⚠️ Ainda **por fazer stage/commit por outra sessão**: `NovoContrato*`, `ValidarContratoService`,
`MarcarAlertaTratadoCommand` estavam modificados/untracked na árvore — NÃO incluídos neste commit.

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
| 7 T7.1 guard COM carreira → 400 | ✅ verde (live 2026-08-26) |
| 7 T7.2 guard não-PCCS → 400 | ✅ verde (live 2026-08-26) |
| 7 T7.8 Detalhe de alterações | ✅ RESOLVIDO + TESTADO LIVE (reader manual isolado, §0d) 2026-08-29 |
| 8 Remunerações filtros (situacaoLaboral/contrVinculo, +/-) | ✅ verde (live 2026-08-26) |
| 9 Regressão (relacao-laboral carreira + renumeracoes + JaVers mob/carr) | ✅ verde (live 2026-08-26) |

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

## 0b. Pós-merge — melhorias adicionais em `develop` (2026-08-27)

Duas melhorias pedidas depois do merge, feitas **direto em `develop`** (não no worktree), testadas live no 8089:

1. **`flgSalario` + `flgSalarioDesc` na lista relação-laboral** (commit **`cc6dfbfe`**). O
   `GET {funcionarioId}/relacao-laboral` passa a devolver o tipo de salário do vínculo:
   - `flgSalario` = valor cru do domínio `TIPO_SALARIO_VINCULO` (SIM_PCCS/SIM_FORA_PCCS/NAO);
   - `flgSalarioDesc` = descrição traduzida (ex.: "Salario do PCCS").
   - Cadeia: vista `RH_V_RELACAO_LABORAL` (+`c.FLG_SALARIO`, `c`=RH_T_PARAM_VINCULO) →
     query nativa `relacaoLaboralFromViewByFuncionario` (+`FLG_SALARIO AS flgSalario`) →
     projeção `RelacaoLaboralView.getFlgSalario()` → `HistoricoLaboralReadService.getRelacaoLaboral`
     (traduz via `DominioService`) → `RelacaoLaboralSumaryDTO` (+2 campos) + manifesto IGRP.
   - ⚠️ **Vista reaplicada na BD live** via `DbExec` (o `.sql` sozinho não chega). Verificado:
     colaborador F4 devolve `flgSalario=SIM_PCCS`, escalão 13/B.

2. **Refactor Regime Emprego — linguagem ubíqua** (commit **`aa000acb`**). Ver §10.

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

## 5b. GAP T7.8 — Detalhe de alterações (JaVers) vazio para Gestão Laboral — ✅ RESOLVIDO (ver §0d)

> ⚠️ O diagnóstico abaixo (InitialValueChange) revelou-se ERRADO. A causa real é o tiprel ser Shallow
> Reference (STATE={} no JaVers). Resolvido com reader manual isolado — ver §0d. Mantido por histórico.


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

## 8. Next step (retomar aqui) — ▶ DECIDIR o protótipo `javers.compare`→tabela (§0e)

**T7.8 ✅ testado live** (§0d, 2026-08-29) e **protótipo `javers.compare`→RH_T_VALIDACAO_DETALHE ✅
testado live** (§0e, **NÃO commitado**). App reiniciada com o build do protótipo (arrancar SEMPRE após
`mvn clean` — `target/` tinha um `.class` corrompido). Log: `scratchpad/boot_diffwriter_8089.log`.

**⇒ PRÓXIMO PASSO = decisão do utilizador sobre o protótipo (§0e):**
1. **Commitar/adotar** a via `compare→persistir` para o escalão? (4 ficheiros: novo
   `EscalaoDetalheDiffWriter` + refactor de `AlteracaoEscalaoDetalheReadService`, `AlterarEscalaoCargoService`,
   `GetDetalheAlteracoesQueryHandler`). Zero regressão comprovada (fallback on-the-fly p/ registos antigos).
2. Se sim → **generalizar** o padrão a outros fluxos de clone (mover writer p/ `shared`)? e/ou aposentar
   o JaVers-histórico pesado a longo prazo?
3. Se não → **reverter** o protótipo (não commitado; `git checkout` dos 4 ficheiros) e ficar no §0d.

Ficheiros-alvo do protótipo: `EscalaoDetalheDiffWriter` (novo), `AlteracaoEscalaoDetalheReadService`,
`AlterarEscalaoCargoService`, `GetDetalheAlteracoesQueryHandler` (todos NÃO commitados).
Evidências live em `scratchpad/t78_*.json` + BD (val `01a04cb3-8dc5-7b84-9d11-33bf8f83b50f` = 4 linhas persistidas).

---

**FASES 0-9 verdes** e **branch MERGED em `develop`** (local, sem push). T7.8 ✅ resolvido (§0d).
- Merge commit: **`de698d2a`** (`Merge branch 'feat/dossier-melhorias' into develop`). Verificado antes:
  `merge-tree` limpo (1 só ficheiro sobreposto — `TiposRelacionamentoEntityRepository`, auto-merge
  aditivo) + **compilação BUILD SUCCESS** num worktree descartável de develop.
- `docs/evidencias_teste_live_dossier.html` ✅ gerado. **Fixtures ficam na BD** (decisão: não limpar).
- `docs/frontend_changes_dossier.md` ✅ criado e commitado (`46819382`) — changelog de API p/ o front.
- TODO T7.8 em `JaversValidacaoDetalheReadService` (opções (a)/(b), ver §5b).

**Falta (controlo do utilizador):**
1. **`git push` de `develop`** (o merge foi só local; passo público por decidir).
2. **Aplicar migrações** (`docs/db/melhorias_dossier_*.sql`) em qualquer ambiente que corra develop e
   ainda não as tenha (a BD live já as tem).
3. **T7.8** — ✅ RESOLVIDO via reader manual isolado (§0d) e ✅ **testado live** no 8089.
5. **Protótipo §0e (`javers.compare`→RH_T_VALIDACAO_DETALHE)** — testado live, **NÃO commitado**: decidir
   adotar/generalizar ou reverter (4 ficheiros na árvore de trabalho).
4. Resíduo: pasta `.claude/worktrees/_mergecheck` ficou bloqueada por um handle no `target/` (o registo
   de worktree já foi prunado; apagar a pasta quando o processo largar / ao reiniciar).

## 9. Ficheiros-chave (relativos ao worktree)
- `docs/db/melhorias_dossier_tipo_salario.sql` — ALTER + backfill.
- `src/.../service/historicolaboral/AlterarEscalaoCargoService.java` — fluxo novo.
- `src/.../rules/ColaboradorValidationRules.java` — helper escalão.
- `src/.../constants/custom/TipoSalarioVinculo.java` — enum do domínio.
- `DbQuery.java` / `DbExec.java` (raiz) — SQL direto.

## 10. Refactor Regime Emprego (2.2.2) — linguagem ubíqua (2026-08-27, commit `aa000acb`)

Confirmado (spec `19_08_26`, secção "Alterar regime trabalho" L6333+): o fluxo de Regime **NÃO
escreve em tabela de validação** — o "Validar" só grava `RH_T_REGIME_TRAB.ESTADO` na própria linha.
Já estava assim; mantido.

Alterações (rotas HTTP **inalteradas**; só nomes de operações/classes):
- POST `{idFuncionario}/regimes`: `adicionarRegimeTrabalho` → **`registarRegimeTrabalho`**;
  `AdicionarRegimeTrabalhoCommand(+Handler)` → **`RegistarRegimeTrabalho...`**; service
  `alterarRegimeTrabalho(...)` → **`registar(...)`**.
- PUT `{idFuncionario}/regimes/{regimeId}`: `validarRegimeTrabalho` → **`alterarRegimeTrabalho`**;
  `ValidarRegimeTrabalhoCommand(+Handler)` → **`AlterarRegimeTrabalho...`**; service
  `validar(...)` → **`alterar(...)`**.
- `RegimeTrabalhoDTO`: **+`estado`** (SELECT, aplicado no PUT quando não vem `validar`; precedência
  do `validar` mantida), **`tipoRegime`/`dataInicio`/`dataFim` obrigatórios** (`@NotBlank`/`@NotNull`).
  `regimeModalidade` fica **opcional** (não quebrar front). Manifesto IGRP sincronizado (+`tipoOrdemServico`
  que faltava no JSON).

Testado live 8089 (colaborador `01a03f71-8385-72e3-a37f-7a0c8f94bdbb`): registar OK (regime 615);
400 com obrigatórios em falta; alterar `estado=I` (regime 609); registar + modalidade (regime 616 +
`RH_T_REGIME_MODAL`).

⚠️ **Gap (não bloqueante):** domínios `MODALIDADE_REGIME` e `DIAS_SEMANA` **não existem** em
`RH_T_DOMAINS` — modalidade/diasSemana aceitam qualquer string e `modalidadeDesc` cai no fallback.
Criar os domínios se o negócio quiser dropdowns validados.

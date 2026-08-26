# Handoff — Melhorias Dossiê do Colaborador

> Atualizado: 2026-08-26. Worktree: `feat/dossier-melhorias` (base: `develop`@aaaffb46).
> Documento vivo e auto-suficiente: escrito para uma sessão nova sem memória desta.

---

## 0. Bloqueio ativo (ler primeiro)

A **BD Oracle** `62.84.179.137:1521` (user `INPSRH`) está **inacessível** desta máquina:
ping 100% loss + JDBC timeout. Testado várias vezes ao longo da sessão. É rede/VPN, não código.
A app do utilizador corre no **8088** (segura um pool antigo); ligações novas falham.

**Decisão do utilizador:** implementar tudo agora, **testar live depois**. Tudo o que depende da BD
(backfill, boot no 8089, teste live, evidências HTML, merge) está **pendente**. O utilizador
**autorizou-me a aplicar o backfill** (via helpers `DbExec`/`DbQuery` na raiz do repo) quando houver
ligação. **Quando a BD voltar: `java -cp ".;<ojdbc>" DbQuery "SELECT 1 FROM dual"` para confirmar.**
ojdbc: `C:\Users\ivanick.santos\.m2\repository\com\oracle\database\jdbc\ojdbc11\21.9.0.0\ojdbc11-21.9.0.0.jar`.

---

## 1. Objetivo

Implementar as melhorias de `docs/MELHORIAS_DOSSIER.md` (secção 2 — Dossiê do Colaborador), cruzadas
com a spec `docs/Especificação Tecnica Funcional - DOSSIÊ DO COLABORADOR_19_08_26.md` e prints do
utilizador. Trabalho isolado num git worktree para não colidir com a app no 8088.

## 2. Estado — CÓDIGO COMPLETO E A COMPILAR (`mvn -o -DskipTests compile` = BUILD SUCCESS)

Commits no branch `feat/dossier-melhorias`: `5462d631` e `a7a47659`.

| Item (MELHORIAS_DOSSIER) | Estado |
|---|---|
| 2.2.2 Regime Emprego | ✅ já conforme doc (nada a fazer) |
| 2.4 Validação / "Detalhe de alterações" | ✅ já resolvido (JaVers) |
| 2.5 Conversão/Renovação | ⏭️ job do módulo **transversal** (fora deste âmbito) |
| **Break change** `FLG_SALARIO` Integer→String (`TIPO_SALARIO_VINCULO`) | ✅ código |
| **2.1** Escalão no tiprel ao registar/validar **colaborador e contrato** | ✅ código |
| **2.2.1** Lista Gestão Laboral: `categoria`→`escalao` | ✅ código |
| **2.2.1** "Alterar Escalão/Cargo" (fluxo novo + validação + CORRIGIR + JaVers) | ✅ código |
| **2.3** Remunerações: filtros `situacaoLaboral` + `contrVinculo` | ✅ código |
| Manifestos IGRP `.igrpstudio/**.json` | ✅ código |
| Script SQL (ALTER + backfill) | ✅ escrito, por aplicar |

## 3. Decisões de negócio — NÃO re-litigar

1. **Dono do salário no "Alterar Escalão" sem carreira → o Java** escreve o vencimento/subsídios.
   A `PKG_AUMENTO_SALARIAL` filtra por `CARREIRA_ID` e rebentaria (bug ORA-01403) — não se aplica a
   vínculos sem carreira.
2. **Modelo do `flg_salario` → String crua + validação no service** (não `@Enumerated`). Enum
   `TipoSalarioVinculo` valida no `ParamVinculoService` ao gravar.
3. **Backfill ambíguo** (`flg_salario=1 & flg_carreira=0`, salário sem carreira) → default
   **`SIM_FORA_PCCS`** (conservador; não inventa escalão). Promoção manual a `SIM_PCCS` depois.
   No código, valores legados/desconhecidos **nunca** contam como PCCS (só o literal `SIM_PCCS`).
4. **"Tipo Alteração" (multiselect)**: o front envia os valores **separados por vírgulas**, gravados
   tal-e-qual em `TIPOS_RELACIONAMENTO.TIPO_SITUACAO`. A decisão do que alterar usa **só** a presença
   de `novoEscalaoId`/`novoCargoId` — **NÃO** interpretar os códigos do domínio por string (tentativa
   `contains("ESCAL"/"CARGO")` foi **rejeitada por frágil** pelo utilizador e removida).

## 4. Detalhe da implementação

### 4.1 Break change `FLG_SALARIO` (domínio `TIPO_SALARIO_VINCULO` = SIM_PCCS / SIM_FORA_PCCS / NAO)
- Novo enum `shared/application/constants/custom/TipoSalarioVinculo.java` — helpers `isValido`,
  `temSalario` (=!NAO, tolera legado "0"/"1"), `ehPccs` (=SIM_PCCS literal).
- `shared/application/constants/Domains.java` — +`TIPO_SALARIO_VINCULO`.
- `shared/infrastructure/persistence/entity/ParamVinculoEntity.java` — `flgSalario` Integer→**String**.
- `configuracao/.../ParamVinculoService.java` — valida contra o enum ao gravar; `buildResponse` usa o
  domínio `TIPO_SALARIO_VINCULO` para `remuneracaoDesc`.
- `configuracao/.../dto/VinculoLaboralResponseDTO.java` — `salario` Integer→String.
- `parametrizacao/domain/models/ParamVinculo.java` + `dto/VinculoDTO.java` — `flgSalario` Integer→String.
- Consumidores "tem salário" → `TipoSalarioVinculo.temSalario(...)`: `ColaboradorValidationRules`,
  `ValidarDadosContratuaisService`, `ValidarContratoService` (2×), `RegistarColaboradorService`,
  `NovoContratoService` (2×). **Cuidado:** `Objects.equals(1, String)` compila mas é sempre falso —
  todos foram trocados (verificado).

### 4.2 Escalão no tiprel (2.1)
- `shared/infrastructure/persistence/entity/TiposRelacionamentoEntity.java` — +`escalaoId`
  (`@ManyToOne` → `RH_T_PARAM_ESCALAO`, coluna `escalao_id`). `DadosContratuaisMapper.clone()` carrega-o.
- Helper central `ColaboradorValidationRules.aplicarEscalaoTiprelSemCarreira(tr, carreira, vinculo,
  escalaoRefId)` (+ overload por vinculoId): se sem carreira + SIM_PCCS + escalão → grava `tr.escalaoId`
  e `tr.salario = escalão.valor`.
- Chamado em: `RegistarColaboradorService` (registo), `NovoContratoService` (registo contrato, 2×),
  `ValidarRegistoColaboradorService` (validação — deriva salário do escalão p/ reconciliar),
  `ValidarContratoService` (validação contrato).

### 4.3 Lista Gestão Laboral (2.2.1)
- `RelacaoLaboralSumaryDTO`: `categoria`→`escalao`. `HistoricoLaboralReadService.getRelacaoLaboral`
  preenche `escalao` de `RH_V_RELACAO_LABORAL.ESCALAO_DESC` (a vista já resolve escalão da carreira OU
  do tiprel).

### 4.4 "Alterar Escalão/Cargo" (2.2.1) — o fluxo novo
- `dto/AlterarEscalaoCargoDTO.java`: `tipoAlteracao` (String CSV), `novoEscalaoId`, `novoCargoId`,
  `dataInicio`, `dataFim`, `observacao`, `validar` (EstadoValidacao).
- Commands/handlers: `AlterarEscalaoCargoCommand(+Handler)`, `ValidarEscalaoCargoCommand(+Handler)`.
- Endpoints (`HistoricoLaboralController`): `POST {funcionarioId}/relacao-laboral/alterar-escalao-cargo`
  e `PUT {funcionarioId}/relacao-laboral/alterar-escalao-cargo/{tiprelUuid}`.
- `service/historicolaboral/AlterarEscalaoCargoService.java`:
  - **Guard**: só vínculo `SIM_PCCS` (`ehPccs`) e **sem** carreira.
  - **Só Cargo** → imediato no tiprel atual (cargo + datas + tipoSituacao); sem validação.
  - **Escalão** (±cargo) → tiprel pendente (P, estActAdm=0) clonando o atual; `data_inicio`/`data_fim`
    do formulário; `TIPO_SITUACAO` = CSV do multiselect; cria `ValidacaoEntity` P (referência
    `GESTAO_LABORAL`). Carimba o save com JaVers.
  - **CORRIGIR (C→P)**: se existir um movimento em C derivado do atual, reabre-o (`reabrirParaValidacao`)
    e reaplica campos, em vez de criar novo.
  - **validar()**: SIM consolida (fecha vencimento antigo por `data_fim` mantendo 'A'; abre novo
    `DEF_REMUNERACOES` = valor do escalão, com `data_inicio`/`data_fim` do formulário; reassocia
    `TIPREL_REM_PAG` excluindo o salário fechado; fecha tiprel antigo I/estActAdm=0; ativa o novo
    A/estActAdm=1). NAO rejeita (I). CORRIGIR devolve (C).
- `Referencia.java` — +`GESTAO_LABORAL("Gestão Laboral")` (rótulo na lista de validações).
- `TiposRelacionamentoEntityRepository` — +`findFirstByTiprelId_IdAndEstado` + **`@JaversSpringDataAuditable`**.
- `GestaoLaboralValidacaoDetalheDescriptor` (novo) — grelha "Detalhe de alterações".

### 4.5 Remunerações (2.3)
- `RenumeracaoController` + `GetListRenumeracoesQuery` + `RenumeracoesReadService` — filtros
  `situacaoLaboral` e `contrVinculo` (IDs, predicados na vista `RH_V_DEF_REMUNERACAO`). Default (sem
  tiprelUuid) já cai no vínculo ativo (`estActAdm=1`) = "último vínculo".
- **Escalão como filtro**: 🔴 não dá — a vista não tem coluna de escalão. Pedir à DBA se necessário.

### 4.6 Manifestos IGRP (`.igrpstudio/**.json`)
`configuracao/dto/VinculoLaboralResponseDTO` (salario string), `funcionario/dto/RelacaoLaboralSumaryDTO`
(categoria→escalao), `parametrizacao/dto/VinculoDTO` (flgSalario string), `funcionario/dto/GetListRenumeracoesQuery`
(+2 params), `funcionario/controllers/RenumeracaoController` (+2 params), `shared/models/ParamVinculoEntity`
(flg_salario string), `shared/models/TiposRelacionamentoEntity` (+escalao_id), `shared/enum/Domains`
(+TIPO_SALARIO_VINCULO), `funcionario/dto/AlterarEscalaoCargoDTO` (novo) + 2 ações no
`HistoricoLaboralController`. Enums hand-written (`TipoSalarioVinculo`, `Referencia`) não têm manifesto.

## 5. Blockers & riscos

- **BD inacessível** (secção 0) — bloqueia backfill, boot, teste live, evidências, merge.
- **`RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID`**: o `V1__init_schema.sql` (desatualizado) diz NOT NULL,
  mas a entidade real não a mapeia hoje e a app insere tiprels na mesma → na BD real é ausente ou
  nullable. A vista `RH_V_RELACAO_LABORAL` expõe `ESCALAO_ID`. O script SQL **adiciona a coluna
  (nullable) se faltar**. **Confirmar no boot** — se faltar e o ALTER não tiver corrido, o mapeamento
  novo rebenta as queries de tiprel (ORA-00904). Ação: correr o SQL ANTES de arrancar no 8089.
- **Valores do domínio `TIPO_MOV_LABORAL`/`GESTAO_LABORAL`**: não listados na doc; investigar na BD
  (`SELECT valor, descricao FROM <tabela_dominio> WHERE dominio='TIPO_MOV_LABORAL' AND referencia='GESTAO_LABORAL'`).
  O backend não depende dos códigos exatos (usa novoEscalaoId/novoCargoId), mas o frontend precisa deles.
- **`@JaversSpringDataAuditable` no tiprel = app-wide**: todas as escritas de tiprel passam a gerar
  commit JaVers (sem contexto ficam sem propriedades, inofensivos). Consistente com outros repos
  auditados, mas é custo de escrita extra em toda a app — vigiar em teste de carga.
- **AlterarEscalaoCargoService, ponto de fragilidade**: na consolidação, o "atual" vem de
  `pendente.getTiprelId()` (snapshot do registo). Se outro movimento validar ENTRE o registo e a
  validação, fica desatualizado. A carreira recompõe do atual do momento (`findAtualByFuncionarioUuid`);
  aqui não. Aceitável no caso simples; rever se houver concorrência de movimentos.

## 6. Como construir / correr

- **JDK**: o projeto exige `release 23`. O Maven default usa JDK 21 → **falha**. Usar:
  `export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"` antes do mvn.
- **Compilar**: `cd .claude/worktrees/dossier-melhorias && mvn -o -DskipTests compile`
  (`-o` offline; deps já em cache). Só warnings Lombok pré-existentes (equals/hashCode) — ignorar.
- **Correr no 8089** (não colidir com o 8088 do utilizador): `SERVICE_PORT=8089 mvn spring-boot:run`
  (ou `-Dspring-boot.run.arguments=--server.port=8089`). Perfil default `development` lê `.env`.
- **SQL direto**: helpers `DbQuery.java` (SELECT), `DbExec.java` (DDL/DML), `DbProc.java`, `DbUpdate.java`
  na raiz do repo. **`DbUpdate` rebenta com ORA-17273 depois de gravar — usar `DbExec`** (ver memória
  `reference_db_helpers`). Acentos: `UNISTR`.

## 7. PLANO DE TESTE LIVE (executar quando a BD voltar) — evidências → HTML

> Regras do utilizador (memória): imprimir sempre a resposta crua (JSON + HTTP status), não só resumo;
> get-by-id ANTES; arrays com id; output pretty; pedir autorização por cada fluxo de escrita;
> `contratoId`/uuid no path são UUID.

### FASE 0 — Pré-voo (BD + schema)
- T0.1 `DbQuery "SELECT 1 FROM dual"` → confirma ligação.
- T0.2 `DbQuery "SELECT column_name, nullable, data_type FROM user_tab_columns WHERE table_name='RH_T_TIPOS_RELACIONAMENTO' AND column_name='ESCALAO_ID'"` → existe? nullable?
- T0.3 `DbQuery "SELECT valor, descricao, referencia FROM <dominio> WHERE dominio='TIPO_SALARIO_VINCULO'"` → 3 linhas (SIM_PCCS/SIM_FORA_PCCS/NAO). (descobrir nome exato da tabela de domínios primeiro.)
- T0.4 `DbQuery "... WHERE dominio='TIPO_MOV_LABORAL' AND referencia='GESTAO_LABORAL'"` → valores do multiselect.
- T0.5 `DbQuery "SELECT flg_salario, flg_carreira, COUNT(*) FROM rh_t_param_vinculo GROUP BY flg_salario, flg_carreira"` → estado pré-backfill.

### FASE 1 — Backfill (com autorização do utilizador)
- T1.1 Aplicar `docs/db/melhorias_dossier_tipo_salario.sql` via `DbExec` (statement a statement: ADD col,
  UPDATE backfill, DROP, RENAME; bloco PL/SQL do ESCALAO_ID; COMMIT).
- T1.2 Verificar: `SELECT flg_salario, COUNT(*) FROM rh_t_param_vinculo GROUP BY flg_salario` → só
  SIM_PCCS/SIM_FORA_PCCS/NAO. Listar os `SIM_FORA_PCCS` (1+sem-carreira) para revisão manual.
- T1.3 Confirmar `ESCALAO_ID` no tiprel existe e é nullable (repetir T0.2).

### FASE 2 — Boot no 8089
- T2.1 Arrancar; confirmar que Hibernate mapeia `escalaoId` (tiprel) e `flgSalario` String sem ORA-00904.
- T2.2 Swagger em `/swagger-ui.html` (se `ENABLE_SWAGGER`).

### FASE 3 — Break change (Parametrização Vínculo)
- T3.1 GET lista/read de um vínculo → `remuneracao`/`salario` devolvem `SIM_PCCS/...` e `remuneracaoDesc`
  do domínio novo.
- T3.2 POST criar vínculo `remuneracao=SIM_PCCS` (flgCarreira=0) → 200; ler → guardado string.
- T3.3 POST `remuneracao="XPTO"` (inválido) → 400 (validação do enum).

### FASE 4 — 2.1 Registo/validação colaborador (sem carreira + SIM_PCCS)
- T4.1 Registar colaborador com vínculo flgCarreira=0 + SIM_PCCS + `escalaoReferenciaId`. get-by-id →
  tiprel tem `escalao_id` e `salario` = valor do escalão.
- T4.2 Validar (SIM) → reconciliação cria REM def com valor do escalão; estados A.
- T4.3 Negativo: vínculo `SIM_FORA_PCCS` → escalão NÃO gravado; salário manual respeitado.
- T4.4 Negativo: vínculo COM carreira → escalão via carreira (comportamento antigo intacto).

### FASE 5 — 2.1 Contrato (novo + validar)
- T5.1 Novo contrato num vínculo sem-carreira SIM_PCCS → tiprel `escalao_id` gravado.
- T5.2 Validar contrato → salário do escalão reconciliado.

### FASE 6 — 2.2.1 Lista Gestão Laboral
- T6.1 GET `{funcionarioId}/relacao-laboral` → campo `escalao` preenchido; `categoria` ausente.

### FASE 7 — 2.2.1 Alterar Escalão/Cargo
- T7.1 Guard: colaborador COM carreira → POST alterar-escalao-cargo → 400.
- T7.2 Guard: vínculo não-PCCS → 400.
- T7.3 **Cargo só**: POST `{novoCargoId, dataInicio}` → 200 imediato; get-by-id → cargo mudou no tiprel
  atual; **nenhuma** validação criada.
- T7.4 **Escalão**: POST `{novoEscalaoId, dataInicio, dataFim, tipoAlteracao:"...,..."}` → 200; existe
  tiprel P (estActAdm=0) + `ValidacaoEntity` P; aparece em GET validações (`getValicoesUtilizadores`)
  com rótulo "Gestão Laboral".
- T7.5 **Validar SIM**: PUT `.../{tiprelUuid}` `validar=SIM` → vencimento antigo fechado (data_fim,
  estado A); novo `DEF_REMUNERACOES` = valor do escalão com data_inicio/data_fim do form; `TIPREL_REM_PAG`
  reassociado (subsídios/descontos seguem, salário antigo não); tiprel antigo I/estActAdm=0; novo
  A/estActAdm=1. Verificar por get-by-id + queries à BD.
- T7.6 **Validar NAO**: repetir T7.4 e rejeitar → tiprel P→I; salário inalterado.
- T7.7 **CORRIGIR + reenviar**: T7.4 → PUT `validar=CORRIGIR` (tiprel P→C, validação C) → POST
  alterar-escalao-cargo de novo (novos valores) → **reabre o mesmo movimento** (C→P), NÃO duplica.
- T7.8 **Detalhe de alterações**: GET `getDetalheAlteracoes` da validação → grelha JaVers mostra
  escalão/cargo/salário/datas com rótulos PT.

### FASE 8 — 2.3 Remunerações
- T8.1 GET renumeracoes sem filtros → só do vínculo ativo (último).
- T8.2 GET com `situacaoLaboral=<id>` e `contrVinculo=<id>` → filtra corretamente.

### FASE 9 — Regressão (smoke)
- T9.1 Registo colaborador COM carreira + progressão (fluxo antigo) continua a funcionar.
- T9.2 Lista de validações e "Detalhe de alterações" de outros movimentos (mobilidade/carreira) intactos.

### Evidências → HTML
Para cada teste: capturar HTTP status + JSON cru (request e response) + queries de verificação à BD.
Montar `docs/evidencias_teste_live_dossier.html` (ou artifact) explicando cada fluxo e o resultado.

## 8. Próximos passos (ordem)
1. (BD de pé) T0 pré-voo → confirmar schema e descobrir nomes de domínio.
2. Pedir autorização e aplicar backfill (FASE 1).
3. Boot no 8089 (FASE 2) — **correr o SQL ANTES** (risco ESCALAO_ID).
4. Executar FASES 3–9, capturando evidências.
5. Gerar o HTML de evidências.
6. Se tudo verde → **pedir permissão para merge** (PR contra `develop`, não `main`).

## 9. Ficheiros-chave
- `.claude/worktrees/dossier-melhorias/` — o worktree (branch `feat/dossier-melhorias`).
- `docs/db/melhorias_dossier_tipo_salario.sql` — ALTER + backfill.
- `src/.../historicolaboral/AlterarEscalaoCargoService.java:1` — fluxo Alterar Escalão/Cargo.
- `src/.../rules/ColaboradorValidationRules.java` — helper `aplicarEscalaoTiprelSemCarreira`.
- `src/.../constants/custom/TipoSalarioVinculo.java` — enum do domínio.
- `DbQuery.java` / `DbExec.java` (raiz) — SQL direto (DbExec para DDL/DML).

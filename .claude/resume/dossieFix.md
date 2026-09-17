> Updated: 2026-09-16 20:50 -01:00

## Goal

Tirar o JaVers do projecto (código, `pom` e tabelas `JV_*`) sem perder a grelha "Detalhe de
alterações" das validações do dossiê. **Concluído.** Falta: levar a mudança a staging/produção e
tratar 3 problemas antigos que os testes expuseram (secção própria abaixo).

## Current state

Commits em `develop` (sem push feito nesta sessão):

| Commit | O quê |
|---|---|
| `a72a5472` | Motor novo + 11 módulos ligados + leitura pela tabela |
| `2f905960` | Correcções dos testes (validação transiente; grelha agrupada por linha; `tabelaId` no DTO) |
| `7550c00c` | Backfill temporário JaVers → tabela (endpoint `POST .../validacoes/detalhes/backfill`) |
| `4ca97639` | Remoção total do JaVers + `CLAUDE.md` (padrão + armadilha de build) |

- Motor: `src/main/java/cv/inps/rh/shared/application/detalhe/` (`Campos`, `CampoAlterado`,
  `DetalheAlteracoes`, `DetalheAlteracoesService`). Declarações: `funcionario/application/service/detalhe/DossierCampos.java`
  (16 entidades) + `MobilidadeCampos.java`. Registo de colaborador: `registodetalhe/RegistoDetalheCongelador.java`.
- 11 módulos ligados **e testados ao vivo** (lista → get-by-id → acção; registo/edição, CORRIGIR →
  reenvio, aprovar/rejeitar): mobilidade, processo disciplinar, rendimento, desconto, situação laboral,
  substituição, carreira, registo de colaborador, escalão/cargo, dados bancários, renovação de contrato.
- Dev (Oracle `62.84.179.137:xe`, user `INPSRH`): `RH_T_VALIDACAO_DETALHE` tem as 5 colunas novas;
  backfill feito (50 validações, 182 linhas, 0 erros, grelhas antes/depois 50/50 iguais);
  `JV_SNAPSHOT/JV_COMMIT/JV_COMMIT_PROPERTY/JV_GLOBAL_ID` + 3 sequências **largadas sem PURGE**
  (estão na recycle bin).
- Build: `mvn clean package` → BUILD SUCCESS, 0 classes `org.javers` no jar. App arranca em ~39 s na
  porta **8087**. Único ERROR no log = Keycloak indisponível em dev (esperado).
- Doc: `docs/detalhe_alteracoes_sem_tabelas_javers.html` (análise + estado final);
  `docs/frontend_changes_funcionario.md` (campos novos `campo`, `tipoAlteracao`, `tabelaId`).

## Decisions made — do not re-litigate

- **Sem JaVers, nem `javers-core`**: com valores já formatados, comparar é `Objects.equals`; o
  comparador próprio garante ordem de declaração e compara FKs por id.
- **Declaração tipada pelo metamodel** (`XEntity_.campo`), nunca nomes em string: apanhou
  `tipoCarreira` inexistente no descritor antigo.
- **Congelar na escrita, capturar ANTES do payload** (as edições são in place; depois o "antes" não existe).
- **FKs**: id via `PersistenceUnitUtil.getIdentifier()` (não inicializa o proxy) + nome por
  `ReferenciaNomeResolver.resolver(Class, id)`; gravar nome **e** id (`VALOR_*_ID`).
- **Reenvio de correcção funde** com o já congelado e mantém o valor anterior **aprovado**.
- **Colecções** (bancários, contactos, familiares…): `congelar(..., tabelaId, ...)` por linha.
- **Validação transiente** (criada por `funcionario.getValidacoes().add` + `save`, que faz MERGE):
  `congelar()` faz flush e recupera por uuid — **nunca `persist()`** (criou uma duplicada, 1156/1157, já apagada).
- **Sem migrations** (pedido explícito): esquema muda por SQL directo; `V3__javers_schema.sql` removida.
- `CAMPO_ALTERADO` continua a ser o rótulo → frontend não parte.

## Constraints

- Compilar com JDK 23 (`C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot`), **correr o jar com
  JDK 25** (`...\jdk-25.0.2.10-hotspot`) — o 23 crasha (0xC0000005).
- Testes ao vivo = como cliente do ecrã; mostrar payload e resposta crus e indentados; verificar na BD;
  não é preciso pedir autorização para escritas em dev.
- SQL directo: `tools/db/DbQuery.java` / `DbExec.java` (nunca `DbUpdate`), via PowerShell.
- Convenção de sync nos PUT: sem `id` cria, omitido apaga, `null` preserva, `[]` limpa tudo.

## Blockers & risks

- **Staging/produção ainda não migrados.** Antes de largar as `JV_*` lá: criar as colunas (SQL em
  *How to verify*) e correr o backfill do commit `7550c00c` (fazer checkout desse commit, arrancar,
  `POST .../backfill?dryRun=true` depois `false`). Sem isso as validações antigas ficam com grelha vazia.
- O backfill copiou os defeitos do JaVers tal e qual — ex.: linha fantasma "Data fim 2026-12-31 → null"
  na mobilidade (validação 1106 do Nuno). Não há como corrigir: o estado anterior já não existe.
- 16 validações antigas (12 `REGISTO_COLABORADOR`, 4 `RENOVACAO_CONTRATO`) já eram vazias no JaVers e continuam.
- `alteradoPor` do motor novo = `anonymousUser` (auditor do AuditEntity); o JaVers mostrava
  `system-bot@nosi.cv`. Combinado deixar assim por agora.
- Registo de mobilidade continua sem grelha (decisão antiga: o ecrã usa o `MOB_ID`).

## Problemas pré-existentes encontrados nos testes — a explicar noutra sessão

Nenhum vem da migração; nenhum foi corrigido. O utilizador pediu explicação detalhada numa sessão
seguinte.

1. **Rejeitar (`validacao:"NAO"`) a edição de uma carreira inactiva registos de outras validações.**
   `POST .../carreiras/{id}/validar` → `CarreiraWriteService.validarCarreira` (~linha 438). Com o
   Nuno: carreira 831 → `I` com o escalão rejeitado, tiprel 173442 → `I`, vencimento 1527 → `I`,
   e também o rendimento **1647** e o desconto **1745**, que pertenciam a OUTRAS validações pendentes.
   Foi preciso repor à mão (SQL em *How to verify*). Liga à memória "rejeição associa fixos a tiprel rejeitado".
2. **A aprovação da situação laboral grava o payload do validador em vez de aprovar o do maker.**
   `AlterarSituacaoLaboralWriteService.validar` (~linhas 177-179): `setMotivoSitLabId(motivo)`,
   `setSituacaoLaboralId(param)`, `setObs(dto.getObservacao())` com os dados do pedido do checker. Na
   reactivação do Nuno apagou a `obs` "Reativacao - teste"; com outro `situacaoLaboralId` consolidaria
   uma situação diferente da que o maker submeteu e da que a grelha mostra. Correcção provável: tirar
   as 3 linhas (a linha pendente já tem o proposto) — confirmar se o frontend reenvia o formulário no SIM.
3. **Mudar a situação laboral de um colaborador activo está quase sempre bloqueado.**
   `guardComboInativarAtivar` (~linha 503): `ativaContrato(param) && funcionario.estado == A` → 400
   "já está ativo". Em `RH_T_PARAM_SITUACAO` quase todas têm `FLG_ESTADO_CONTRATO='A'` (incl. licenças,
   baixas, férias); só **9 APOSENTADO ('C')** e **15 Falecimento ('S')** passam. Falta decidir se o
   erro está nos dados (flag mal preenchida) ou na regra (devia aplicar-se só à situação "ATIVO").

Relacionados (não são bugs novos): rejeitar edição in-place **não reverte valores** (documentado
como decisão para a mobilidade em `docs/frontend_changes_funcionario.md`); `ParamSituacaoDetalheEntity`
só resolvia nome depois de acrescentar `getMotivo` ao resolver (feito).

## Relevant files

- `shared/application/detalhe/DetalheAlteracoesService.java` — capturar/comparar/congelar/fundir; recuperação por uuid.
- `funcionario/application/service/detalhe/DossierCampos.java` — todas as declarações + nomes de tabela.
- `funcionario/application/service/registodetalhe/RegistoDetalheCongelador.java` — registo (13 secções).
- `shared/application/service/ReferenciaNomeResolver.java` — id → nome; `GETTERS_CANDIDATOS`, `OVERRIDES`.
- `shared/application/service/ValidacaoDetalheReadService.java` + `funcionario/application/queries/GetDetalheAlteracoesQueryHandler.java` — leitura (uma fonte).
- `CLAUDE.md` secção "Detalhe de alterações" — padrão de 3 passos para ligar módulos novos.
- `scratchpad/` (gitignored): `registo_detalhe_0916*.json`, `registo_semjavers*.json`, `backfill_antes.json`, `backfill_comparar.py`.

## How to verify / resume

```powershell
# build (fechar/ignorar o IDE; apagar o jar antes para não sair sem .properties)
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
Remove-Item target\rh-service-0.0.1-SNAPSHOT.jar* -ErrorAction SilentlyContinue
mvn -o clean package -DskipTests      # ler BUILD SUCCESS; não usar -q
jar tf target\rh-service-0.0.1-SNAPSHOT.jar | Select-String "classes/application.properties"   # tem de aparecer
& "C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe" -jar target\rh-service-0.0.1-SNAPSHOT.jar
# log deve dizer [RHINPSSERVICE] e "Tomcat started on port 8087"; se disser [core], o jar está sem .properties
```

- Grelhas antigas: `python scratchpad/backfill_comparar.py` → `50/50 grelhas com o MESMO conteudo`.
- Sem JaVers: `git grep -i javers -- src pom.xml` → só javadocs históricos em `DossierCampos`/`MobilidadeCampos`.
- SQL das colunas (para staging/produção):
  ```sql
  ALTER TABLE RH_T_VALIDACAO_DETALHE ADD (CAMPO VARCHAR2(100), ORDEM NUMBER(3), TIPO_ALTERACAO VARCHAR2(20), VALOR_ANTERIOR_ID NUMBER, VALOR_NOVO_ID NUMBER);
  ALTER TABLE RH_T_VALIDACAO_DETALHE MODIFY (VALOR_ANTERIOR VARCHAR2(2000), VALOR_NOVO VARCHAR2(2000));
  CREATE INDEX IX_VAL_DET_VALIDACAO ON RH_T_VALIDACAO_DETALHE (VALIDACAO_ID, ORDEM);
  ```
- Recuperar as JV_* em dev (se preciso): `FLASHBACK TABLE JV_SNAPSHOT TO BEFORE DROP;` (idem as outras 3).
- Estado de referência do Nuno (958937), para repor depois de testes de carreira:
  carreira 831 `A` escalão 16 salário 190336; tiprel 173442 `A` salário 190336; REM 1527 `A`; PAG 1736-1738 `A`;
  REM 1647 `P` e PAG 1745 `P` (rendimento/desconto de teste, validações pendentes).

## Test / validation plan

Dados de teste em dev: Nuno Teste Sync 958937 (`01a085fa-fc04-7f08-a5d9-75b4b8a886b7`, mobilidade 720,
carreira 831); **Teste Motor Detalhe Corrigido 958940** (`01a0abe6-8b2f-7365-b940-f1a5a6581705`,
vínculo 17 PCCS **sem carreira** — único para testar escalão; bancários 699/700/701);
**Teste Sem JaVers Corrigido 958941** (`01a0ac26-cbd0-7034-9cd1-96f74d479df4`, registo `P` pendente);
Adérito 958939 (contrato 748, renovação `P` pendente, validação `01a0abef-39c2-7c8e-9018-d30ed1b6668e`).
Pendentes deixados de propósito: rendimento 1647 e desconto 1745 (Nuno), registo do 958941, renovação do Adérito.

Para os 3 problemas pré-existentes (próxima sessão):
1. Carreira: fotografar Nuno (SQL acima) → `PUT .../{F}/carreiras/{C}` escalão 16→17 com `subsidios:null,encargosDescontos:null`
   → `POST .../carreiras/{C}/validar {"validacao":"NAO"}` → esperado hoje: 1647/1745 passam a `I` (bug) → repor.
2. Situação laboral: registar (`PATCH .../situacao-laboral`, `situacaoLaboralId:9, motivoId:23, observacao:"x"`)
   → aprovar com `{"validar":"SIM","situacaoLaboralId":9,"motivoId":23}` sem `observacao` → esperado hoje: `obs` fica `null` (bug). Repor Nuno a ATIVO (situação 699: `situacao_laboral_id=1, motivo_sit_lab_id=2, estado='A', obs=null`; funcionário `estado='A', estado_validacao='A'`; tiprel `estado='A'`).
3. Guard: `SELECT id,nome,flg_estado_contrato FROM RH_T_PARAM_SITUACAO WHERE estado='A'` e ler `ativaContrato/cessaContrato` — decidir com o negócio.

## Open questions

- Problemas 1–3 acima: corrigir código, dados ou regra? Decide o utilizador/analista.
- Alinhar `alteradoPor` (`anonymousUser` vs nome do utilizador IAM) — adiado pelo utilizador.
- Push para o remoto e PR contra `develop`: não pedido ainda.

## Next step

Explicar ao utilizador, em detalhe, os 3 problemas pré-existentes da secção própria (causa no código,
dados envolvidos, impacto e opções de correcção), antes de mexer em qualquer um.

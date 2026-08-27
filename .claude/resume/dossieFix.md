> Updated: 2026-08-27 ~20:10

## Goal

Testar/afinar em live os fluxos do **dossiê do colaborador**. Registo → validação → Renovação →
**Novo Contrato** estão validados end-to-end. **Próximo alvo: Ativar/Desativar Contrato**
(`AlterarEstadoContrato`, patch de estado), agora que o colaborador **C** está num estado limpo com
contrato **inicial** e **atual** distintos e identificáveis.

## Current state

Tudo committado em `develop` (HEAD `56fac1a1`, o commit docs de handoff; código em `32fe873d`). Build
compila limpo (JDK23). **App PARADA** (o user pediu; estava na 8089, profile development).

**Novo Contrato do C — VALIDADO live** (evidência em `scratchpad/nc_*.json`):
- POST `…/958913-uuid/contratos` → 200, contrato **717** (uuid `1f1a24ec-6815-64a1-a339-bd34151ea9fa`),
  CONTINUIDADE, estado P. Validado com **PUT** (não POST!) `…/contratos/{uuid717}` body `validar:"SIM"` → 200.
- Estados finais: **717 A/atual**; **716 v2 (renov) I**; **716 v1 (inicio) I**. Tiprel novo **173371**
  (est_act_adm=1); antigos 173366/173369 (est_act_adm=0).
- Encargo **fixo manual novo 1933 "Quotas Sindicais STCS" (1500)** adicionado só no 717 — é o único ADD
  real vs o contrato anterior (os automáticos 1681/1940/1741/1680 são re-derivados na validação, iguais).

**GET by versão validado**: `getById?versao=N` → se essa versão for `tipo_situacao='INICIO'` devolve a
vista INICIAL; senão faz fallback para ATUAL (est_act_adm=1). INICIAL do C = `716-uuid?versao=1`
(`1f1a2139-0f5e-6d49-8d3f-7be00928ad97`); ATUAL = `717-uuid` (sem versão).

## Decisions made — do not re-litigate

- **Não há edição de contrato — só Registo/Renovação/Novo Contrato** (regra de negócio confirmada pelo user).
  Renovação só altera **datas** (`RenovarContratoReqDTO`=dataInicio/dataFim/duracaoMeses); clona o tiprel e
  **não toca em def**. Logo os def partilhados nunca mudam de valor por fluxo de negócio → vista INICIAL é
  fiel a "como começou", ATUAL a "como está". Fonte do antes→depois é a auditoria JaVers (Detalhe de Alterações).
- def são keyed por `fun_id` (sem FK de contrato); o scoping por contrato é via `RH_T_TIPREL_REM_PAG`
  (tiprel↔def). def "estado=A" ≠ "pertence ao atual"; vigência = associação ao tiprel + est_act_adm.
- Novo Contrato cria def NOVAS de raiz (nada herdado): fixos do payload, automáticos re-derivados na validação.
- **fixo = veio no payload `encargosDescontos`**; **automático = derivado da parametrização do vínculo**
  (`ParamVinculoMovimento`, tipo PAG/REM) na validação. PAG≠manual (1680 Valor Líquido é PAG mas automático).

## Constraints

- Compilar com `JAVA_HOME=.../Eclipse Adoptium/jdk-23.0.2.7-hotspot`. SQL direto: helpers em `tools/db/`
  (gitignored) — correr de LÁ. **ojdbc11 que funciona: `23.7.0.25.01`** (a `23.2.0.0` do q.sh/x.sh deu
  "No suitable driver"). Usar **DbExec** para DML (DbUpdate rebenta ORA-17273). Oracle XE antigo: sem
  `FETCH FIRST` → usar `ROWNUM`. Path do jar no `-cp` tem de ser Windows-style (`C:\...`), não `/c/...`.
- GET antes de cada escrita; **ids em todos os arrays**; pedir autorização por cada fluxo de escrita;
  negativos antes do happy path. contratoId no path = UUID. Mostrar resposta crua (JSON+HTTP), **identado**
  (o user pediu explicitamente). Manter `scratchpad/` como referência viva. Ver [[feedback_fluxo_validacao_teste]].

## Blockers & risks

- App parada — arrancar antes de testar (ver How-to). BD acessível (62.84.179.137:1521:xe).
- Backdating do C por SQL é artefacto de teste out-of-band: os encargos INICIAIS mostram `data_fim=2025-08-28`
  em vez de 2024 — não é bug da app, é o backdating. Num colaborador criado só pela app não aconteceria.

## Relevant files

- `interfaces/rest/ContratoController.java:206-235` — **PATCH `{idFunc}/contratos/{contratoId}/estado`**,
  body `AlterarEstadoContratoDTO { String estado }` (="A" ativar / "I" desativar).
- `application/service/AlterarEstadoContratoService.java` — **alvo do próximo teste**. Regras:
  - **Desativar (A→I)**: só o contrato **ATUAL** (tiprel est_act_adm=1), tem de estar A e **não processado
    em folha**; baixa estado do contrato+cadeia de filhos (tiprel/mob/carreira/regime/situacao) + def para I;
    mantém est_act_adm=1 no histórico (breadcrumb do "atual" p/ reativar).
  - **Ativar (I→A)**: só o "último/atual" (histórico est_act_adm=1); contrato tem de estar I; **falha se já
    existe contrato em vigor** (`existeContratoEmVigor`). Repõe est_act_adm=1 (único histórico ativo).
  - Validação de `estado`: só aceita "A"/"I" (linha ~178-183).
- `application/queries/GetContratoByIdQueryHandler.java:58-116` — INICIAL vs ATUAL por `?versao=`.
- `shared/…/ContratoEntityRepository.java` — `existeContratoEmVigor` (guard partilhado com Novo Contrato).

## How to verify / resume

- Arrancar app: `cd <root> && bash scratchpad/run_develop_8089.sh > scratchpad/boot_8089.log 2>&1 &`;
  esperar `Tomcat started on port 8089` no log (≈30s) e `curl -s -o /dev/null -w "%{http_code}"
  http://localhost:8089/swagger-ui.html` = **302**. (Chamadas REST em dev passam **sem token**.)
- SQL direto (exemplo que funciona):
  `cd tools/db && OJ="C:\Users\ivanick.santos\.m2\repository\com\oracle\database\jdbc\ojdbc11\23.7.0.25.01\ojdbc11-23.7.0.25.01.jar" && java -cp ".;$OJ" DbQuery "<SQL>"`
- Estado atual do C (fun_id **958913**, uuid `01a04336-6953-7e81-9a15-7aee349dd6c7`):
  `SELECT id,estado,versao FROM rh_t_contrato_vinculo WHERE fun_id=958913` (só linha 717? conferir — o
  vínculo físico é 716/717) e `rh_t_tipos_relacionamento WHERE fun_id=958913` → tiprel 173371 est_act_adm=1.
- Pretty-print pattern usado: pipe `curl … -w "\n__HTTP__:%{http_code}"` para um `python -c` que faz
  `rpartition("__HTTP__:")` e `json.dumps(indent=2, ensure_ascii=False)`.

## Test / validation plan — ATIVAR/DESATIVAR CONTRATO do C (próximo passo)

Contrato atual do C = **717** (uuid `1f1a24ec-6815-64a1-a339-bd34151ea9fa`), estado A, tiprel 173371 est_act_adm=1.

1. **GET lista** `…/funcionarios/contratos?idFuncionario=01a04336-6953-7e81-9a15-7aee349dd6c7` → confirmar
   717 A/atual antes de mexer. (identado)
2. **Negativos primeiro** (esperar 400 com mensagem):
   - Ativar um já-ativo: PATCH `…/958913-uuid/contratos/{uuid717}/estado` body `{"estado":"A"}` → "Só é
     possível ativar um contrato inativo" (ou equivalente do service).
   - `{"estado":"X"}` → "Estado inválido: use 'A' … 'I'".
   - Desativar um contrato antigo/inativo (716) → "Só é possível desativar um contrato ativo."
3. **DESATIVAR (A→I)** — pedir autorização: PATCH `…/{uuid717}/estado` body `{"estado":"I"}` → 200
   "Contrato desativado." Verificar em BD: `rh_t_contrato_vinculo` 717→I; tiprel 173371 estado I mas
   **est_act_adm continua 1** (breadcrumb); def de 717 (1626-1630, 1459-1460) → I; carreira/regime/mob→I.
   GET lista → 717 deixa de ser atual/em-vigor.
4. **Novo Contrato com contrato desativado** (opcional, valida o ramo `tipoRelacionamentoAtual==null` de
   `NovoContratoService.java:98-104`): com 717 desativado, `existeContratoEmVigor`=false → um POST Novo
   Contrato deve cair no ramo "primeiroContrato/CONTINUIDADE" sem tiprel anterior para fechar. (decidir se testar)
5. **REATIVAR (I→A)** — pedir autorização: PATCH `…/{uuid717}/estado` body `{"estado":"A"}` → 200
   "Contrato ativado." Deve **falhar** se entretanto existir outro contrato em vigor; senão repõe 717 A e
   est_act_adm=1. Verificar BD + GET lista (717 volta a atual). Capturar JSON+HTTP de cada passo em `scratchpad/`.
6. Reconfirmar que INICIAL (`716-uuid?versao=1`) e ATUAL (`717-uuid`) continuam coerentes após o ciclo.

## Open questions

- Passo 4 (Novo Contrato após desativar) entra no âmbito desta sessão ou fica para depois? (decide o user)
- "Processado em folha" (guard que bloqueia desativar) — como forçar/limpar `flgProcessa` no C se precisarmos
  testar o negativo? (o 717 tem `processamento:false` na lista, portanto deve deixar desativar.)

## Next step

Arrancar a app, GET lista do C para confirmar 717 A/atual, e correr os **negativos** do plano (passo 2)
antes de pedir autorização para o **DESATIVAR** (passo 3).

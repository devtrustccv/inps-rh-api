> Updated: 2026-08-27 ~12:40

## Goal

Testar e afinar, em live, os fluxos do **dossiê do colaborador** (Registo → validação maker-checker →
Renovação → **Novo Contrato**) na BD de dev, com dados coerentes. O próximo teste imediato é o **Novo
Contrato** do colaborador **C** (já preparado). `AlterarEstadoContrato` continua em **stand-by**.

## Current state — tudo committado em `develop` (HEAD `32fe873d`)

Commits desta sessão (após `aa000acb`): `dcf2d231` docs · `fc8aa010` chore(gitignore: tools/db,
scratchpad, *.log) · `4e8fdbed` feat expor `estado/estadoDesc` (subsidios, encargos, anexos, funcionario
em dadosPessoais) no GET · `4b3cc2fa` fix normalizar def no registo (`obs="INICIO"` + período/moeda do
contrato, simétrico com validação → elimina diffs fantasma no Detalhe de Alterações) · `32fe873d`
refactor renovação: `RenovarContratoReqDTO` só tem `dataInicio/dataFim/duracaoMeses` (removidos
`tipoContratoId/tipoVinculoId`); `ContratoMapper` limpo (removido método morto `toRenovarContrato`).

Colaboradores de teste (todos ativos, validados SIM):
- **A** 958911 (`01a04309-097c-7112-95ff-41e3af4d034d`) — indeterminado, contrato 714.
- **B** 958912 (`01a04328-458e-7e15-ad7d-49072963f9ae`) — indeterminado; usado para validar o fix `4b3cc2fa`.
- **C** 958913 (`01a04336-6953-7e81-9a15-7aee349dd6c7`) — **determinado**, contrato **716**, já **renovado**
  (v2 atual). **Backdated por SQL** para 2023-08-27 → 2025-08-28 em TODAS as tabelas do vínculo, para o
  guard D2 (`existeContratoEmVigor`) passar. Verificado: `em_vigor=0`. Pronto para Novo Contrato.

App a correr na 8089 (profile development). Build compila limpo (JDK23).

## Decisions made — do not re-litigate

- def (`RH_T_DEF_*`) levam `obs="INICIO"` no registo E na validação, **sobrescrevendo** o ecrã — regra de
  negócio, não bug. Ver [[project_def_obs_inicio_simetria]].
- Renovação: tipo contrato/vínculo NÃO vêm no request (são os do contrato atual); ficam só no DTO de
  leitura (`RenovarContratoRespDTO`, `RenovacaoDetalheDTO`).
- Datas de renovação são **form-driven** (responsabilidade do user). Sobreposição de 1 dia (antigo fecha
  com dataFim = início do novo) fica como está; no futuro, opção de guard no `RenovacaoContratoService`.
- Backdating do C feito por SQL direto (não há endpoint) para exercitar Novo Contrato sem criar novo colaborador.

## Constraints

- Compilar com `JAVA_HOME=.../Eclipse Adoptium/jdk-23.0.2.7-hotspot`. SQL direto: helpers em `tools/db/`
  (gitignored) — correr de LÁ: `cd tools/db && java -cp ".;<ojdbc11 do .m2>" DbQuery "<SQL>"`. Usar
  **DbExec** para DML (DbUpdate rebenta ORA-17273). Oracle XE antigo: sem `FETCH FIRST` → usar `ROWNUM`.
  Ver [[reference_db_helpers]].
- GET antes de cada escrita; **ids em todos os arrays** do payload; pedir autorização por cada fluxo de
  escrita; testar negativos antes do happy path. contratoId no path = UUID. Ver [[feedback_fluxo_validacao_teste]].
- Mostrar sempre a resposta crua (JSON + HTTP). Manter a `scratchpad/` como referência viva. Documentar
  mudanças de API em `docs/frontend_changes_funcionario.md`.

## Blockers & risks

- Nenhum bloqueio. App de pé; BD acessível (62.84.179.137:xe).
- Risco: o backdating do C é dados-de-teste diretos na BD; se a app for reiniciada não afeta (persistido).
- `AnexoRespDTO` é partilhado — `estado/estadoDesc` agora aparecem noutros módulos (aditivo, vêm null).

## Relevant files

- `application/service/NovoContratoService.java:70-127` — guards D1 (dataInicio não futura), validação
  pendente, **D2 `existeContratoEmVigor`** (só lê `RH_T_CONTRATO_VINCULO`), e o fluxo que fecha tiprel/contrato atual.
- `shared/infrastructure/persistence/repository/ContratoEntityRepository.java:68-75` — query do guard D2.
- `application/service/RegistarColaboradorService.java:258-279` — normalização def (`obs=INICIO`+datas/moeda).
- `application/service/ValidarRegistoColaboradorService.java:270-279` — mesma normalização na validação.
- `application/service/RenovacaoContratoService.java` + `ContratoHistoricoWriteService.java:40-54` — renovação.
- `interfaces/rest/ContratoController.java` — endpoints contrato (novoContrato `POST {idFunc}/contratos`,
  renovacao `POST {idFunc}/renovacao-contrato/{contratoUuid}`, validar-renovacao, lista `GET contratos?idFuncionario=`).

## How to verify / resume

- Arrancar app (se caída): `cd <root> && bash scratchpad/run_develop_8089.sh > scratchpad/boot_8089.log 2>&1 &`
  e esperar `curl -s -o /dev/null -w "%{http_code}" http://localhost:8089/swagger-ui.html` = 302.
- Confirmar guard do C liberado: `cd tools/db && java -cp ".;<ojdbc11>" DbQuery "SELECT COUNT(*) FROM
  rh_t_contrato_vinculo WHERE fun_id=958913 AND estado='A' AND (data_fim IS NULL OR data_fim>=TRUNC(SYSDATE))"`
  → deve dar **0**.
- Estado dos contratos do C: `GET http://localhost:8089/api/v1/funcionarios/contratos?idFuncionario=01a04336-6953-7e81-9a15-7aee349dd6c7`.

## Test / validation plan — NOVO CONTRATO do C (próximo passo)

1. GET lista contratos do C (acima) → confirmar contrato 716 atual, dataFim 2025-08-28 (passado).
2. Montar payload `NovoContratoDTO` (endpoint `POST api/v1/funcionarios/01a04336-6953-7e81-9a15-7aee349dd6c7/contratos`).
   Reaproveitar a estrutura de `dadosContratuais` de `scratchpad/registo_C.json` (carreira 6, escalão 21,
   vínculo 1, salário 186980, INTEGRAL, subsídio tm 1684, encargo tm 1721). `dataInicio` **≤ hoje (2026-08-27)**
   para o guard D1 passar (ex.: `2026-08-27`). Escolher determinado (tipoContratoId 1 + dataFim/duracao) ou
   indeterminado (tipoContratoId 2) — perguntar ao user.
3. Disparar POST → esperar 200 e um novo contrato criado (novo `ContratoEntity`, `TIPO_SITUACAO=CONTINUIDADE`,
   versao=1); o contrato 716 deve ir a `ESTADO=I` e o tiprel 173369 fechar (`est_act_adm=0`, dataFim=início do novo).
4. GET lista contratos → deve mostrar o contrato novo (atual) + o 716 (inativo).
5. Validar SIM o novo contrato (fluxo próprio) e reconfirmar estados. Capturar JSON+HTTP de cada passo em `scratchpad/`.

## Open questions

- Novo contrato do C: determinado ou indeterminado? (decide o user)
- Guard de sobreposição de datas na renovação: implementar ou deixar form-driven? (deixado ao user por agora)

## Next step

Perguntar ao user o tipo do novo contrato do C, montar o payload do Novo Contrato (dataInicio ≤ hoje),
mostrar antes de disparar, e executar o Test plan acima.

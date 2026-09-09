> Updated: 2026-09-09 12:40 -01:00

## Goal

Validar, contra a Oracle de dev e com a app a correr, os fluxos do Dossiê do Colaborador
(registo → validação → contrato → mobilidade), corrigindo o que se desviar do caso de uso
`docs/caso_uso_teste_gravacao_04_09_26.md`. Sessão anterior fechou contrato e mobilidade; falta
continuar pelas restantes secções.

## Current state

4 commits em `develop` (sem push), todos com `mvn compile` limpo e verificados com a app a correr:

- `15d1c6de` ativar/desativar contrato deixa de dar 400 com progressão por validar (finder do tiprel
  passou a `est_act_adm=1`; desativação já não zera a marca)
- `7971d4ef` remove `inicial`/`atual` dos dois GET de detalhe (ninguém os lia; no dossiê vinham
  sempre `false`). A lista mantém-nos
- `4e14fd0d` editar mobilidade só vai a validação se mudar direção/unidade/local
- `20f780b1` rejeita `*Destino` de tipo não escolhido (400)

Changelog do front-end em `docs/frontend_changes_funcionario.md` (4 entradas novas, no fim).

Colaborador de teste criado e validado nesta sessão, em estado limpo:
`Nuno Teste Sync`, id **958937**, uuid **01a085fa-fc04-7f08-a5d9-75b4b8a886b7**,
contrato 746 (uuid `1f1ac43a-f979-63a4-a624-711ba536f6f7`), mobilidade 720
(uuid `01a085fa-fc0f-7780-9958-e2696024cbad`), tiprel 173442. Tudo `A`, sem validações pendentes.

Untracked/deleted em `docs/` no `git status` são anteriores a esta sessão — não mexer.

## Decisões tomadas — não re-litigar

- `est_act_adm=1` = **qual** é o vínculo corrente; `estado` = se está ativo. Dimensões independentes;
  um tiprel `I` + `est_act_adm=1` é legítimo (confirmado pelo utilizador).
- Nunca identificar o vínculo corrente por `max(id)` do tiprel — há sempre candidatos de workflow
  (progressão P/E/I) com id maior.
- `inicial`/`atual` só existem na **lista** de contratos. Não repor nos GET de detalhe.
- Editar mobilidade: só direção, unidade e local disparam validação. Datas e tipo gravam-se sempre
  mas não vão ao checker. O reenvio de correção (`C`) mantém-se e vai à fila mesmo sem diferenças.
- Contrato da API da mobilidade: enviar **apenas** o `*Destino` do tipo selecionado. Falta → 400;
  a mais → 400.
- No ecrã de mobilidade só interessam **DIRECAO, SECAO e LOCAL_TRABALHO**. Os restantes valores de
  `tipo_situacao` (`INICIO`, `NOVO_CONTRATO`, `RENOVACAO`) são carimbos deixados por outras ações —
  registo de colaborador, contrato — e não são para tratar aqui. Decidido pelo utilizador: fica como
  está, incluindo o `tipo_situacao` poder ficar híbrido (`INICIO,SECAO`) e aparecer na grelha do
  checker. **Não reabrir.**

## Constraints

- PR contra `develop`, nunca `main`. Conventional commits.
- Pedir autorização ao utilizador **antes de cada escrita** (POST/PUT/PATCH); GET são livres.
- Mostrar sempre o HTTP status + corpo cru indentado, não resumir.
- Arrays nos PUT: sempre completos e com `id` — ver memória `reference_sync_arrays_put` (sem id
  duplica; ausente do array é soft-deleted `E`; `null` preserva; `[]` limpa).
- `contratoId`, `idFuncionario`, `mobilidadeId` nos paths e query params são **UUID**, não id numérico.

## Blockers & risks

- Nenhum bloqueio. App a correr (task `bvylfuzcx`, porta **8087**).
- `docs/frontend_changes_funcionario.md` cresceu muito; considerar dividir por tema.
- Nada foi feito push — os 4 commits só existem localmente.

## Relevant files

- `src/main/java/cv/inps/rh/funcionario/application/service/MobilidadeWriteService.java:168` —
  `aplicarCamposMobilidade`, a regra tipos↔"(depois)" nos dois sentidos; `editar()` ~L390 tem a
  comparação que decide se vai a validação
- `src/main/java/cv/inps/rh/funcionario/application/service/AlterarEstadoContratoService.java:74` —
  finder do tiprel corrente
- `docs/caso_uso_teste_gravacao_04_09_26.md:352` — secção Mobilidade do caso de uso
- `tools/db/DbQuery.java`, `DbExec.java` — SQL directo (usar `DbExec` para escrita; `DbUpdate` dá
  ORA-17273 depois de gravar)

## How to verify / resume

Ambiente (2 armadilhas que custaram tempo):

- **JDK 23 obrigatório.** `JAVA_HOME` do sistema aponta para `jdk-21` e o arranque falha com
  `UnsupportedClassVersionError` (class file 67 vs 65). Usar
  `C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot`.
- **A app sobe na porta 8087**, não na 8089 do CLAUDE.md.
- O **Bash tool tem o PATH partido** (sem `ls`, `curl`, `git`, `python`). Usar PowerShell para
  comandos; as ferramentas Read/Grep/Glob funcionam normalmente.

Arrancar:

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE
mvn spring-boot:run          # ~1-4 min; esperar "Started RhInpsServiceApplication"
```

Chamadas HTTP (o `WebClient` com UTF-8 evita o mojibake que o `Invoke-WebRequest` produz nos acentos):

```powershell
$wc=New-Object System.Net.WebClient; $wc.Encoding=[System.Text.Encoding]::UTF8
($wc.DownloadString("http://localhost:8087/api/v1/funcionarios?pageNumber=0&pageSize=20")) |
  ConvertFrom-Json | ConvertTo-Json -Depth 12
```

SQL directo:

```powershell
cd tools\db
$cp=".;C:/Users/ivanick.santos/.m2/repository/com/oracle/database/jdbc/ojdbc11/23.7.0.25.01/ojdbc11-23.7.0.25.01.jar"
& "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot\bin\java.exe" -cp $cp DbQuery "SELECT ..."
```

Nomes reais de tabelas/colunas (vários palpites falharam com ORA-00942/00904):
`RH_T_FUNCIONARIOS`, `RH_T_SECAO`, `RH_T_MOBILIDADE` (coluna `INSTIT_ID`, não `INSTID_ID`),
`RH_T_VALIDACAO` (coluna `TIPO_ACCAO`, dois C), `RH_T_CARREIRA`, `RH_T_CONTRATO_VINCULO`,
`RH_T_TIPOS_RELACIONAMENTO`, `RH_V_CONTRATO`.

Confirmar o estado herdado:

```powershell
git log --oneline -4        # deve mostrar 20f780b1, 4e14fd0d, 7971d4ef, 15d1c6de
mvn -q compile -DskipTests  # EXIT=0
```

## Test / validation plan

Colaborador **958937** (uuid `01a085fa-fc04-7f08-a5d9-75b4b8a886b7`), mobilidade uuid
`01a085fa-fc0f-7780-9958-e2696024cbad`. Base: `PUT /api/v1/funcionarios/{funUuid}/mobilidades/{mobUuid}`.

Regressões já verificadas nesta sessão — repetir só se se mexer em `MobilidadeWriteService`:

1. **No-op** — payload igual ao que está na BD (`tipoMobilidade:"INICIO"`, `dataInicio:"2026-09-09"`).
   Esperado: 200 `"Sem alterações."`; mobilidade fica `A`; nenhuma validação nova.
2. **Só data** — `{"tipoMobilidade":"INICIO","dataInicio":"2026-09-09","dataFim":"2026-12-31"}`.
   Esperado: 200 `"Sem alterações."`; `data_fim` gravada; estado `A`.
3. **Alteração real** — `{"tipoMobilidade":"INICIO,SECAO","dataInicio":"2026-09-09","seccaoDestino":2}`.
   Esperado: 200 `"Mobilidade actualizada."`; mobilidade `P`; validação `UPDATE MOBILIDADE` `P`.
   Detalhe (`GET /api/v1/funcionarios/validacoes/{validacaoUuid}/detalhes`) tem de mostrar
   `valorAnterior` preenchido — se vier `null`, o baseline JaVers partiu-se.
4. **Falta o destino** — `{"tipoMobilidade":"SECAO","dataInicio":"2026-09-09"}`.
   Esperado: 400 `Escolheu mobilidade de Unidade: indique o campo "Unidade (depois)".`; rollback total.
5. **Destino a mais** — `{"tipoMobilidade":"SECAO",...,"seccaoDestino":12,"direcaoDestino":100010075}`.
   Esperado: 400 `Enviou "Direcção (depois)" mas não escolheu mobilidade de Direcção.`
6. **Herança** — depois do teste 3, confirmar por SQL que `instit_id` e `local_trab_id` ficaram
   inalterados (100010973 e 5).

Limpar depois de cada teste que deixe `P`: validar com
`PUT .../mobilidades/{mobUuid}/validar` e body `{"validar":"SIM","tipoMobilidade":...}` (esperado
200 `"Mobilidade validada."`, mobilidade volta a `A`, **tiprel não muda** — uma edição não cria nem
troca tiprel). Em alternativa, repor por SQL com `DbExec`.

Contrato — regressão do `15d1c6de`, no 958925 (uuid `01a061d6-f65c-74cd-a5e3-3c45f3de5734`,
contrato uuid `1f1abadd-e93d-63b2-a3bf-b3e110b02d03`, que tem uma progressão rejeitada):
`PATCH .../contratos/{contratoUuid}/estado` com `{"estado":"I"}` → 200; tiprel 173439 fica `I` com
`est_act_adm=1` e o candidato 173440 intacto; depois `{"estado":"A"}` → 200 e o get-by-id volta
idêntico. Num contrato **não** atual (731, uuid `1f1a6bff-07c9-698b-85ba-459f5766daec`) os dois
sentidos devem dar 400.

## Open questions

- Fazer push / abrir PR dos 4 commits — o utilizador ainda não pediu.

## Next step

Retomar a validação do Dossiê nas secções ainda não cobertas (carreira, regime, situação laboral,
dados bancários/familiares), seguindo `docs/caso_uso_teste_gravacao_04_09_26.md` e usando o 958937
como colaborador de teste.

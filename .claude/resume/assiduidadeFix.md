> Updated: 2026-09-09 21:20 -01:00

## Goal

Alinhar o backend de Assiduidade — ecrãs **3.2.2 Marcar Falta** e **3.2.3 Justificar Falta** —
com a spec `docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md`, validando
tudo contra a Oracle de dev com a app a correr. Falta implementar as duas acções novas da spec
09/09: **Editar** e **Eliminar** um pedido de justificação (por grupo `RH_T_FALTA.PEDIDO_ID`).

## Current state

6 commits em `develop`, sem push, todos com `mvn clean compile` limpo e verificados live:

- `4bbbdc67` cabeçalho do `GET .../pedido/{uuid}` (pedidoId, deduzirFaltaEm, valores, ano/mes) +
  anexos de grupo passam a `REFERENCIA_NAME='RH_T_PEDIDO'`
- `3e21a2e7` ordena os dias do painel (finders com `OrderBy...Asc`)
- `668ecdeb` **`ResumoFaltaMesDTO`** novo: o GET do mês deixa de reutilizar o DTO do pedido
- `6720ae74` anexo por dia removido do contrato (`FaltaItemDTO.documento`)
- `b87e121c` `estado`/`estadoDesc`/`etapa` do pedido em cada grupo + sync de anexos no validar
- (por commitar) **stubs de Editar/Eliminar** — ver *Next step*

Endpoints do fluxo, todos testados live excepto os dois últimos:

| Método | Rota | Estado |
|---|---|---|
| POST | `assiduidade/falta` | marcar, com e sem justificar ✅ |
| POST | `assiduidade/falta/justificar/{funUuid}` | criar ✅ |
| GET | `assiduidade/falta/justificar/{funUuid}?ano&mes` | mês: soltos + `pedidos[]` ✅ |
| GET | `assiduidade/falta/justificar/pedido/{pedidoUuid}` | pedido ✅ |
| PUT | `assiduidade/falta/justificar/validar/{pedidoUuid}` | validar ✅ |
| PUT | `assiduidade/falta/justificar/pedido/{pedidoUuid}` | **stub → 501** |
| DELETE | `assiduidade/falta/justificar/pedido/{pedidoUuid}` | **stub → 501** |

Changelog do frontend em `docs/frontend_changes_assiduidade.md`, secções 7 e 8.

## Decisões tomadas — não re-litigar

- **Anexos do bloco "Justificar Faltas Selecionadas" pertencem ao PEDIDO**
  (`REFERENCIA_NAME='RH_T_PEDIDO'`), **divergindo da spec** (que diz `'RH_T_FALTA'`): justificar
  cria sempre um pedido e o ecrã só tem um sítio de anexar. Decidido pelo utilizador. Não houve
  migração — `RH_T_DOCUMENTO` não tinha anexos de falta.
- **Não existe anexo por dia.** `FaltaItemDTO.documento` saiu do contrato, request e response.
- **`motivo` e `comJustificativo` são do cabeçalho**, aplicados a todas as faltas seleccionadas.
  Nos itens ficam só de resposta.
- **`despachoRh` saiu dos dois DTOs.** O campo não existe em nenhum dos dois ecrãs e a coluna
  `RH_T_FALTA.DESPACHO_RH` é `VARCHAR2(3)`, onde os valores do domínio (`JUSTIFICADA`/
  `INJUSTIFICADA`) não cabem. Escalado, não corrigido.
- **`parecer` fica texto livre.** O domínio `PARECER_DECISAO` está por povoar em dev (único
  registo: `VALOR='TETS'`); parametrizar é do lado do cliente, não nosso.
- **DTOs separados**: `ResumoFaltaMesDTO` (mês) vs `JustificarFaltaDTO` (pedido). Um mês não é
  um pedido e não tem cabeçalho de formulário.
- **Editar/Eliminar são endpoints próprios por `pedidoUuid`**, não flags no POST: as guardas de
  negócio são diferentes e um `pedidoId` esquecido no corpo criaria uma justificação duplicada.
- **Editar/Eliminar agem no pedido inteiro** (bloco), não em dias soltos.
- `tipoJustificacao` é obrigatório quando `justificar="SIM"` no Marcar Falta: sem ele o
  `PARAM_SIT_ID` ficava nulo e a regra dos 3 dias nunca disparava.

## Constraints

- PR contra `develop`, nunca `main`. Conventional commits.
- **Pedir autorização e mostrar o payload antes de cada escrita** (POST/PUT/PATCH/DELETE).
  GET são livres.
- **Mostrar sempre o corpo cru da resposta no chat** (HTTP status + JSON indentado), não
  resumir em tabela — o utilizador insistiu nisto mais do que uma vez.
- Arrays nos PUT: completos e com `id` — sem id cria, omitido fica `E`, `null` preserva.
- `pedidoId`, `funcionarioId` nos paths são **UUID**; `itensFalta[].id` é o **id da síntese
  diária** (`RH_ASSIDUIDADE_SINTESE_DIARIA.ID`), não o da falta.

## Blockers & risks

- Nenhum bloqueio. App a correr na **8087** com o código actual.
- `RH_PROCESSAMENTO_SALARIAL_DB` tem o *package body* inválido (ORA-04063) — o cálculo do valor
  cai no **fallback Java** e funciona; não confundir com bug nosso.
- **Efeitos colaterais são o risco central do Eliminar**: uma justificação validada cria
  `RH_T_DEF_REMUNERACOES` + `RH_T_TIPREL_REM_PAG` + `RH_T_DISPENSA` (ou abate férias). Um
  `ESTADO='E'` só na falta deixa o colaborador descontado por uma falta eliminada.
- Uma falta com tipo que desconta salário **e** `deduzirFaltaEm` preenchido aplica **os dois**
  efeitos (corte no vencimento *e* dispensa). Parece dupla penalização — por confirmar com o
  negócio.
- Menores, por corrigir: `FaltaItemDTO.valorAusencia` é `Integer` e trunca cêntimos (6344 vs
  6344,56); a leitura devolve documentos em estado `E`; `colaboradorId`/`nomeColaborador`
  repetem-se dentro de cada grupo do GET do mês.

## Relevant files

- `src/main/java/cv/inps/rh/assiduidade/application/services/JustificarFaltaWriteService.java` —
  fim do ficheiro: os dois **stubs com TODO** (`editarPedidoJustificacao`,
  `eliminarPedidoJustificacao`); `justificarFalta` ~L75; `validarFaltaJustificada` ~L260
- `src/main/java/cv/inps/rh/assiduidade/application/services/JustificarFaltaReadService.java` —
  `montarGrupo` é partilhado pelos dois GETs, por isso não podem divergir
- `src/main/java/cv/inps/rh/assiduidade/application/services/FaltaDescontoService.java:113` —
  `requerValidacao`: **>3 dias E tipo desconta salário**, cumulativo; `aplicar` (L79-95) é quem
  cria os efeitos financeiros
- `src/main/java/cv/inps/rh/assiduidade/application/services/FaltaServiceWrite.java` —
  Marcar Falta; `deveJustificar` (L80) decide se cria pedido+falta ou só síntese
- `docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md:655` — acções
  Editar/Eliminar (a única alteração de substância face à versão 07/09)
- `.igrpstudio/assiduidade/controllers/AssiduidadeController.json` — 39 actions

## How to verify / resume

Ambiente (duas armadilhas que custam tempo):

- **JDK 23 obrigatório** — `JAVA_HOME` do sistema aponta para outra versão.
- **Porta 8087**, não a 8089 do CLAUDE.md.
- Se o arranque falhar com `ClassFormatError` ou `NoClassDefFoundError`, é lixo de compilação
  incremental: `mvn clean compile` resolve.

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE
mvn -q clean compile -DskipTests          # EXIT=0
Start-Process mvn.cmd -ArgumentList "spring-boot:run" -RedirectStandardOutput "$env:TEMP\rh-app.log" -WindowStyle Hidden
# esperar "Started RhInpsServiceApplication" no log (~2 min)
git log --oneline -6                       # b87e121c no topo
```

HTTP (o `WebClient` evita o mojibake que o `Invoke-WebRequest` produz nos acentos):

```powershell
$wc=New-Object System.Net.WebClient; $wc.Encoding=[System.Text.Encoding]::UTF8
($wc.DownloadString("http://localhost:8087/api/v1/assiduidade/falta/justificar/01a085fa-fc04-7f08-a5d9-75b4b8a886b7?ano=2026&mes=9")) |
  ConvertFrom-Json | ConvertTo-Json -Depth 12
```

SQL directo (usar `DbExec` para escrita — `DbUpdate` dá ORA-17273; sem `FETCH FIRST`, o
`DbQuery` rebenta com ORA-00933: usar `WHERE ROWNUM<=n` numa subquery):

```powershell
cd tools\db
$cp=".;C:/Users/ivanick.santos/.m2/repository/com/oracle/database/jdbc/ojdbc11/23.7.0.25.01/ojdbc11-23.7.0.25.01.jar"
& "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot\bin\java.exe" -cp $cp DbQuery "SELECT ..."
```

Nomes reais (vários palpites falharam): `RH_T_FALTA`, `RH_T_PEDIDO`, `RH_T_DOCUMENTO`,
`RH_ASSIDUIDADE_SINTESE_DIARIA`, `RH_T_TIPOS_DOCUMENTOS` (não `RH_T_TIPO_DOCUMENTO`),
`RH_T_DOMAINS` (não `RH_T_DOMINIO`), `RH_V_RESUMO_ASSIDUIDADE`, `RH_V_FALTA_MENSAL`.
`RH_T_DISPENSA` **não tem** coluna `FUN_ID` (liga-se pelo `PEDIDO_ID`).

## Test / validation plan

**Colaborador de teste: Nuno Teste Sync, id 958937, uuid `01a085fa-fc04-7f08-a5d9-75b4b8a886b7`,
tiprel 173442.** Responsável de teste: `RH_T_RESPONSAVEL.ID=23` (João Carlos, fun 958934,
uuid `01a07627-dcdd-79a8-aebb-e4634ae8f01d`) — inserido à mão, a tabela estava vazia.
Tipos de falta: **17** Motivo Pessoal e **18** Falta Injustificada, ambos com
`FLG_FALTA_DECONTO_SAL=1`. Tipo de documento: **26**.

Estado deixado em setembro/2026 (confirmar antes de começar):

| Síntese | Data | Pedido | Estado |
|---|---|---|---|
| 817, 818 | 15, 16/09 | 193 `01a0875d-9a21-7593-ba33-bd9d215af381` | A |
| 819 | 17/09 | 195 `01a087f1-471c-79df-833c-bfd985acc772` | A |
| 821-824 | 21-24/09 | 196 `01a087f7-e167-72fb-8127-d20392ee158f` | A (validado) |
| 820, 825 | 18, 25/09 | — | por justificar |

**Regressões a repetir se se mexer em `JustificarFalta*Service`:**

1. **GET do mês** → `itensFalta` só com 820 e 825; `pedidos[]` com 3 grupos ordenados por data,
   cada um com cabeçalho completo e `estado`/`etapa` do pedido.
2. **GET por pedido** (196) → resposta **idêntica** ao `pedidos[2]` do GET do mês (é o mesmo
   `montarGrupo`).
3. **Criar sem validação**: justificar ≤3 dias com tipo 17/18 → nasce `A`, `ETAPA=FINALIZADO`,
   efeitos aplicados de imediato.
4. **Criar com validação**: justificar **≥4 dias** com tipo 18 → nasce `P`,
   `ETAPA=DESPACHO_RH`, `RH_T_VALIDACAO` `P`, e **sem** `DEF_REM_ID`/dispensas até validar.
5. **Validar** (`PUT .../validar/{uuid}`, `validar:"SIM"`) → faltas `P`→`A`, pedido
   `A`/`FINALIZADO`, validação `A`, e só então nascem `RH_T_DEF_REMUNERACOES` (6344,56/dia) +
   `RH_T_TIPREL_REM_PAG` + `RH_T_DISPENSA`. Anexo do maker em `P` passa a `A`; anexo enviado
   sem `id` é criado.
6. **Guarda de férias**: `deduzirFaltaEm:"FERIAS"` no 958937 → **400** *"tem 0 dia(s) por gozar"*,
   rollback total (o colaborador não tem saldo).
7. **Marcar sem justificar** (`justificar:"NAO"`) → só sínteses, zero linhas em `RH_T_PEDIDO` e
   `RH_T_FALTA`; `tipoJustificacao` não é exigido.

**Ainda por testar:** rejeição (`validar:"NAO"` → tudo `I`) e o ramo de **remoção** do sync de
anexos (omitir um documento do array → fica `E`).

**Limpeza** (a ordem importa, por causa das FKs — `FK_DISPENSA_PEDIDO` bloqueia o pedido):
`RH_T_TIPREL_REM_PAG` → `RH_T_DOCUMENTO` → `RH_T_FALTA` → `RH_T_DEF_REMUNERACOES` →
`RH_T_DISPENSA` → `RH_T_PEDIDO` → `RH_ASSIDUIDADE_SINTESE_DIARIA`.

## Open questions

- **`itensFalta` no PUT de editar**: o array é a lista final de dias do pedido (tirar um dia
  remove-o, semântica dos arrays da casa) ou os dias são fixos e só o cabeçalho se edita?
  Recomendei a primeira; **por decidir pelo utilizador**.
- **Editar volta a validação?** Proposto: muda tipo/dedução/conjunto de dias → volta a `P` e
  reabre a `RH_T_VALIDACAO`; muda só motivo/observação/anexos → grava direto. A spec deixa a
  célula de gravação vazia.
- **Editar/Eliminar num pedido já processado em folha** — bloquear com 400, como faz a carreira?
- **O que o Eliminar desfaz** além de `RH_T_FALTA.ESTADO='E'` (ver *Blockers*).
- `DESPACHO_RH VARCHAR2(3)` — alargar a coluna ou retirar o campo? Decisão do DBA/analista.
- Estado do mês na lista Gestão Falta dá `INJUSTIFICADA` mesmo com dias justificados; o
  utilizador ponderava um estado novo (parcial) ou as colunas Total Justificada/Injustificada.

## Next step

Implementar `editarPedidoJustificacao` e `eliminarPedidoJustificacao` em
`JustificarFaltaWriteService` (hoje `throw ... NOT_IMPLEMENTED` → 501), depois de o utilizador
responder às duas primeiras *Open questions*.

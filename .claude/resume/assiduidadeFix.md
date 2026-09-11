> Updated: 2026-09-11 10:05 -01:00

## Goal

Alinhar o backend de **Assiduidade** com a spec
`docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md`, provando tudo live
contra a Oracle de dev. Marcar, justificar, despachar e eliminar estão fechados e provados.
**Falta testar o Editar** — cenários E1 a E4 abaixo.

## Current state

Branch `develop`, HEAD **`15f367ad`**, **sincronizado com `origin/develop`** (push feito).
Árvore limpa (só `bash.exe.stackdump`, lixo untracked). `mvn clean compile` limpo; app arranca.

Commits desta sessão (11/09), todos provados live:

| Commit | O quê |
|---|---|
| `aa8136e0` | regra dos 3 dias conta os dias **do registo** (acumulação mensal removida — não está na spec) |
| `8940b3e4` | tipo de falta validado pelo critério do combo (`FLG_AUSENCIA=1 AND TIPO_AUSENCIA='FALTA' AND ESTADO='A'`); `deduzirFaltaEm` vazio limpa; `NENHUM` eliminado |
| `03c29e1d` | parametrização activa lida por `ESTADO='A' AND DT_FIM IS NULL`; fallback de jornada único |
| `4b4abf1b` | `GET falta/{pedidoId}` devolve `deduzirFaltaEm`/`valorDiario`/`valorTotal`; `totalDeHorasAusentes` passa a `HH:MM` |
| `4d21e723` | vista `RH_V_RESUMO_ASSIDUIDADE` deixa de contar dias normais como faltas; saldo de férias com parcelas |
| `1a78314b` | marcar sem justificativo não valida o tipo |
| `142095aa` | **despacho tudo-ou-nada** + guard de estado + `valorDescontado`/`valorCoberto` |
| `01966256` | `valorCoberto` só conta o que o saldo absorveu |
| `1506ca04` | justificar guarda o que vier no array (`selecionar` deixa de ser lido) |
| `7d495074` | falta em `E` deixa de bloquear o dia |
| `531af9f3` | marcar falta não passa por cima de falta viva |
| `15f367ad` | `motivoAusencia` → **`motivo`** no Marcar Falta |

**Dois DDL de vistas aplicados em dev**, por replicar em staging/produção:
`docs/sql/rh_v_falta_mensal_fix.sql` e `rh_v_resumo_assiduidade_fix.sql` (textos anteriores nos
`*_BACKUP_11-09.sql`).

Changelog em `docs/frontend_changes_assiduidade.md`, secções **12 a 21** + lista para o analista.
**Faltam lá as secções do `531af9f3` (guard do marcar) e do `15f367ad` (campo `motivo`).**

## Decisions made — do not re-litigate

- **Despacho é tudo-ou-nada.** `SIM` aprova todos os dias, `NAO` rejeita todos. `itensFalta` é
  aceite mas ignorado. Rejeitou-se a devolução por dia: o modelo é *um pedido → uma validação →
  uma etapa*, e a falta não tem identidade própria no despacho.
- **Guard do despacho verifica o que altera:** pedido em `P` **e** validação pendente — e é essa
  mesma linha, já em mão, que é fechada. Sem isto, dois `PUT` criavam `DEF_REMUNERACOES` a dobrar.
- **Justificar guarda o que vier no array.** O ecrã envia só as linhas marcadas. `selecionar` fica
  no DTO **sem `@Deprecated`** — está por decidir o lado do validar.
- **Editar não usa semântica de array** — lá, omitir destruiria faltas e reverteria dinheiro.
- **`deduzirFaltaEm` vazio limpa** (o formulário manda o estado completo). `NENHUM` não existe.
- **O editar não deve ser mais exigente do que o registo** — usa a mesma `requerValidacao`.
- **`FLG_DESCONTO_SAL` vem de `RH_T_PARAM_SITUACAO`**: `RH_T_FALTA` não tem `TF_ID` e
  `RH_T_TIPO_FALTAS` tem uma linha de teste eliminada.
- **`valorTotal` é o bruto**; `valorDescontado`/`valorCoberto` são os campos novos.
- Uma **validação já despachada fica como está** ao eliminar — é histórico. Só as `P` vão a `E`.
- **Responsabilidade do cliente — enviar o estado completo (decidido 11/09, não se protege no
  backend):**
  - **`documentos`**: se o pedido já tem anexos (gravados no `POST` do justificar ou num editar
    anterior), o cliente tem de os reenviar com os `id` do `GET`, no editar e no despacho. O fix
    `a6c232b5` só protege o campo **omitido** (`null` = preserva); um array sem os existentes (`[]`
    ou só anexos novos) põe-nos em `E`, sem erro. Changelog secção 26.
  - **`deduzirFaltaEm` no despacho**: vazio/`null` **retira** a dedução (fix `a6c232b5`, igual ao
    editar). O backend não distingue omitido de `null` — o cliente manda sempre o valor do combo.
    Changelog secção 24.
- **Despacho continua a exigir `itensFalta` não vazio** apesar de o ignorar — mantido de propósito
  (futura remoção por checkbox). Não mexer.

## Constraints

- PR contra `develop`, nunca `main`. Conventional commits.
- **Mostrar SEMPRE o payload completo antes de executar** e **pedir autorização antes de cada
  escrita** (POST/PUT/DELETE e SQL de escrita); GET livres.
- **Colar a resposta crua E INDENTADA no corpo da mensagem de chat**, não só no output da
  ferramenta. O utilizador teve de o pedir três vezes na sessão de 11/09.
- Filtros de leitura em SQL, não em memória.
- `pedidoId`/`funcionarioId` nos paths são **UUID**; `itensFalta[].id` é o id da **síntese**.

## Blockers & risks

- **Nenhum bloqueador.** App de pé, BD acessível.
- **`target/` corrompe-se** se a app for morta durante a compilação — o arranque falha com
  `ClassFormatError` ou `IllegalArgumentException` no plugin. Remédio: `mvn clean compile`.
  Aconteceu 3× em 11/09.
- **Matar a app pelo porto**, nunca pelo nome do processo. O maven antigo escreve
  `Process terminated with exit code: -1` no log novo — **não é falha do arranque novo**.
- **Manifesto IGRP dessincronizado**: correr o `igrp-spring-generator` neste módulo apaga campos
  em uso.
- **Campo desconhecido passa em silêncio** (Jackson): uma gralha no nome de um campo dá 200 com o
  dado perdido.

## Relevant files

- `.../assiduidade/application/services/JustificarFaltaWriteService.java` — `justificarFalta:99`,
  `validarFaltaJustificada:278` (guard em `:287`), `editarPedidoJustificacao:~560` (**alvo dos
  testes**), `eliminarPedidoJustificacao`, `garantirPedidoActivo`
- `.../assiduidade/application/services/FaltaDescontoService.java` — `requerValidacao:~170`
  (**a linha da decisão pendente**), `valorDescontado`, `valorCoberto`, `aplicar`, `reverter`
- `.../assiduidade/application/services/FaltaServiceWrite.java` — `marcarFalta`, guard em `:92`
- `.../shared/infrastructure/persistence/repository/FaltaEntityRepository.java` —
  `existeFaltaVivaNoDia:~99` (exclui `I` e `E`)
- `docs/frontend_changes_assiduidade.md` — secções 12–21

## How to verify / resume

**JDK 23 obrigatório**, porta **8087** (não a 8089 do CLAUDE.md).

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE
mvn -q clean compile -DskipTests      # EXIT=0

Get-NetTCPConnection -LocalPort 8087 -State Listen -ErrorAction SilentlyContinue |
  ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
Remove-Item "$env:TEMP\rh-app.log" -ErrorAction SilentlyContinue
Start-Process mvn.cmd -ArgumentList "spring-boot:run" `
  -WorkingDirectory "C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE" `
  -RedirectStandardOutput "$env:TEMP\rh-app.log" -WindowStyle Hidden
```

Esperar por `Started RhInpsServiceApplication`. As queries JPQL só são validadas **no arranque**.

HTTP (o `WebClient` evita o mojibake do `Invoke-WebRequest`); **novo objecto por chamada**, senão
o segundo dá 415:

```powershell
$wc = New-Object System.Net.WebClient
$wc.Encoding = [System.Text.Encoding]::UTF8
$wc.Headers.Add("Content-Type","application/json; charset=utf-8")
try { $r = $wc.UploadString($url,"PUT",$body); "HTTP 200"; $r | ConvertFrom-Json | ConvertTo-Json -Depth 12 }
catch [System.Net.WebException] { $resp=$_.Exception.Response; "HTTP " + [int]$resp.StatusCode
  (New-Object System.IO.StreamReader($resp.GetResponseStream(),[System.Text.Encoding]::UTF8)).ReadToEnd() }
```

DELETE precisa de `[System.Net.WebRequest]::Create($url)` com `.Method = "DELETE"`.

SQL directo — `DbExec` para escrita (`DbUpdate` dá ORA-17273), sem `FETCH FIRST`:

```bash
cd tools/db
cp=".;C:/Users/ivanick.santos/.m2/repository/com/oracle/database/jdbc/ojdbc11/23.7.0.25.01/ojdbc11-23.7.0.25.01.jar"
"C:/Program Files/Eclipse Adoptium/jdk-23.0.2.7-hotspot/bin/java.exe" -cp "$cp" DbQuery "SELECT ..."
```

**Heredocs `<<'EOF'` rebentam no Git Bash deste ambiente** (CRLF). Para blocos longos: escrever a
um ficheiro com a ferramenta Write e depois `cat ficheiro >> destino`, ou usar `python - <<'PY'`.

## Test / validation plan

**Fixture:** colaborador **Nuno Teste Sync**, id **958937**, uuid
`01a085fa-fc04-7f08-a5d9-75b4b8a886b7`, tiprel 173442.
Tipos: **18** Falta Injustificada (`desconta=1`), **20** Doença do Trabalhador (`desconta=0`).
Valor da falta: **6 344,56/dia** (8h). Jornada 8h. `RH_T_ANO`: id **2** = 2026.
**Saldos actuais: férias 2 de 2; dispensa 4h de 4h** (ambos devolvidos por eliminações).

**Pedidos vivos do Nuno (todos `A`/`FINALIZADO`):**

| id | uuid | dias | período | tipo | deduz | descontado |
|---|---|---|---|---|---|---|
| 244 | `01a08f86-9866-73cd-940c-429fd130f60c` | 4 | 16,19,20,21/10 | 18 | FERIAS | **25 378,24** |
| 245 | `01a08fe0-f4ee-7e55-be31-09b5a78768fc` | 2 | 26,27/10 | 20 | — | 0,00 |
| 247 | `01a08ff5-9f06-7a70-8253-2e12bc9f61de` | 4 | 12–15/10 | 20 | — | 0,00 |
| 248 | `01a09003-b104-71b8-adcf-ebdfe9c35105` | 4 | 05–08/10 | 20 | — | 0,00 |
| 249 | `01a0901b-ac0e-793f-a093-502444838ec5` | 1 | 02/11 | 20 | — | 0,00 |

Eliminados (invisíveis nos ecrãs): 242 e 243.

### Regra do editar, a que os cenários testam

```
requerValidacao = mudancaMaterial && requerValidacao(dias do pedido, tipo efectivo)
                                  //  > 3 dias      E   tipo desconta salario
```
**Material** = mudou `tipoJustificacao` **ou** `deduzirFaltaEm`. Cosmética (`motivo`, `parecer`,
`observacao`, `responsavelId`, anexos) grava directo e o `reverter()` **não corre**.

Endpoint: `PUT /api/v1/assiduidade/falta/justificar/pedido/{pedidoUuid}`
(DTO próprio `EditarPedidoJustificacaoDTO`, **sem `itensFalta`**).

### Cenários — correr por esta ordem, verificando a BD entre cada um

**E1 — edição cosmética (o mais importante).** Pedido **245** (`01a08fe0…`), 2 dias, tipo 20.
Enviar só `{"motivo": "E1 - so o motivo"}`.
Esperado: **200**, pedido fica `A`, etapa `FINALIZADO`, `requerValidacao:false`.
Evidência: `SELECT ID, ESTADO, PARAM_SIT_ID, FLG_DESCONTO_FALTA, DEF_REM_ID, DESCRICAO_MOTIVO FROM
RH_T_FALTA WHERE PEDIDO_ID=245` → motivo novo, **`DEF_REM_ID` continua `null`**, nada mais mudou.
Confirmar **0 linhas novas** em `RH_T_DEF_REMUNERACOES`.

**E2 — material que NÃO reabre despacho.** Pedido **245**, 2 dias.
Enviar `{"tipoJustificacao": 18, "comJustificativo": "SIM"}`.
Esperado: **200**, material mas **2 ≤ 3** → fica `A` **e cria 2 descontos** de 6 344,56.
Evidência: 2 linhas novas em `RH_T_DEF_REMUNERACOES` em `A`, `FLG_DESCONTO_SAL=1` nas faltas,
2 linhas em `RH_T_TIPREL_REM_PAG`. `GET .../pedido/{uuid}` → `valorDescontado: 12689.12`.

**E3 — material que REABRE despacho.** Pedido **247** (`01a08ff5…`), 4 dias, tipo 20.
Enviar `{"tipoJustificacao": 18, "comJustificativo": "SIM"}`.
Esperado: **200**, `requerValidacao:true`, pedido volta a **`P`**, etapa `DESPACHO_RH`,
**nova linha em `RH_T_VALIDACAO` em `P`** (`REFERENCIA_NAME='JUSTIFICAR_FALTA'`), faltas em `P`
com `DESPACHO_RH=null`, e **zero** `DEF_REMUNERACOES`.
Depois: `PUT .../validar/{uuid}` com `validar:SIM` → 4 descontos criados.

**E4 — trocar a dedução com dinheiro já emitido.** Pedido **244** (`01a08f86…`), 4 dias, tipo 18,
FERIAS, **25 378,24 já descontados**.
Enviar `{"deduzirFaltaEm": "DISPENSA"}`.
Esperado: **200**, material e 4 > 3 → volta a `P`; os **4 `DEF_REMUNERACOES` antigos passam a `E`**
e as associações em `RH_T_TIPREL_REM_PAG` são **apagadas**; `FLG_DESCONTO_FALTA='DISPENSA'`.
Ao despachar `SIM`: 4h de dispensa consumidas (1 linha de 240 min) + 28h ao salário — o mesmo
padrão já provado no pedido 243 (dia 1 desconta **3 172,28**, os outros 6 344,56).

**Regressões a repetir se se mexer no editar:** guards `P`/`I`/`E` do `garantirPedidoActivo`
(editar um pedido em `P` deve dar 400 *"está em validação"*), e o guard de folha
`garantirNaoProcessado`.

## Open questions

- **A regra de ir a validação deve ser `OU` em vez de `E`** (decisão do utilizador, 11/09, por
  confirmar com o analista): `dias > 3` **OU** `tipo desconta salário`. Hoje é `E`.
  É **uma linha** em `FaltaDescontoService.requerValidacao` e **não há regressão** — os dois
  caminhos (`P` e `A`) já estão exercitados; muda só a proporção. Consequências: muito mais
  pedidos em `P`, mais saldo reservado, e o `garantirPedidoActivo` a trancar pedidos de 1 dia.
  Sobra como excepção só 1–3 dias com tipo que não desconta.
  **A spec apoia esta leitura**: a secção *Ações* dos dois ecrãs (`:553`, `:825`) diz que a linha
  em `RH_T_VALIDACAO` nasce *"caso o tipo for falta tem salário"*, sem falar em dias — é a secção
  *REGRA* (`:494`, `:796`) que acrescenta o "> 3 dias".
- **`selecionar` não tem comportamento em endpoint nenhum** — apagar o campo, ou dar-lhe semântica
  (a "cisão do pedido" é a única opção estruturalmente limpa). Se for para dar semântica, a
  separação em DTOs precisa de **3 classes**, não 1 (Java não estreita o tipo de um campo numa
  subclasse).
- As 7 entradas da secção *Por decidir com o analista* do changelog — incluindo **Ordem de Serviço
  não existir na spec** e **eliminar desfazer os descontos**.

## Next step

Correr o **E1**: `PUT /api/v1/assiduidade/falta/justificar/pedido/01a08fe0-f4ee-7e55-be31-09b5a78768fc`
com `{"motivo": "E1 - so o motivo"}`, e confirmar em BD que `DEF_REM_ID` continua `null` nas duas
faltas e que nenhuma `RH_T_DEF_REMUNERACOES` nasceu.

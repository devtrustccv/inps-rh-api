> Updated: 2026-09-10 21:40 -01:00

## Goal

Alinhar o backend de **Assiduidade** com a spec
`docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md`, provando tudo live
contra a Oracle de dev. Fechado: lotes B, C, D1, D2, A1, D3 (reserva + lock) e o editar/eliminar
por pedido. **Em curso:** o despacho passar a decidir o pedido inteiro.

## Current state

**2 commits por enviar** (`54d4ea0e`, `c3d1a5f7`); os anteriores já estão em `origin/develop`.

Árvore de trabalho limpa (só `bash.exe.stackdump`, lixo untracked).

Commits da sessão, todos com `mvn clean compile` limpo e provados live:

| Commit | O quê |
|---|---|
| `fb8c5369` | **reserva de saldo** — o saldo desconta as faltas em `P`, por leitura |
| `5efc5a6d` | **lock** pessimista por colaborador nos 8 casos de uso que consomem saldo |
| `77ce36d6` | changelog de API para o frontend (secções 9–11) |
| `54d4ea0e` | **editar age no pedido** — DTO próprio, sem `itensFalta` |
| `c3d1a5f7` | **editar reavalia o despacho** + edições cosméticas deixam de mexer em dinheiro |

**BD de dev limpa** — pedidos, sínteses, `RH_T_FERIAS`, `DEF_REMUNERACOES` e validações de teste
apagados (confirmado a zeros). Último `RH_T_PEDIDO` de teste: 241. Última síntese real: 884.

**Plano da próxima tarefa** (discutido, por implementar, carece dos ajustes da última conversa):
`C:\Users\ivanick.santos\.claude\plans\planear-o-que-deve-sequential-thompson.md`.

## Decisões tomadas — não re-litigar

- **Editar age no pedido, não na composição dele.** `PUT falta/justificar/pedido/{uuid}` tem DTO
  próprio (`EditarPedidoJustificacaoDTO`) **sem `itensFalta`**; os dias descobrem-se pelo pedido,
  como no eliminar. Um `itensFalta` que ainda venha é aceite e ignorado. Anula o cenário A1.5.
- **Editar volta a despacho quando mexe em dinheiro** — muda `tipoJustificacao` ou
  `deduzirFaltaEm` **e** a regra do registo verifica-se (>3 dias no mês + tipo que desconta).
  Fechava-se assim a porta lateral: registar 4 dias com tipo que não desconta (fica `A`) e editar
  para um que desconta deixava 4 descontos sem despacho.
- **Edição cosmética não toca em nada financeiro.** O `reverter()` só corre com mudança material —
  antes matava e recriava as linhas de desconto a cada gravação (16 linhas para 4 dias, 12
  mortas), quebrando a ligação de remunerações já emitidas.
- **`deduzirFaltaEm`: `null` preserva, `"NENHUM"` limpa**, `FERIAS`/`DISPENSA` trocam.
- **Editar e eliminar só num pedido activo** (`garantirPedidoActivo`): `P` → "está em validação";
  `I` → "foi rejeitado"; `E` → "já foi eliminado".
- **Despacho é tudo-ou-nada** (decidido, **por implementar**): aprova ou rejeita o pedido inteiro;
  o `selecionar` dos itens deixa de comandar.
- **`I` e `E` são finais**: não se alteram; quem quiser corrigir faz **nova marcação**.
- **Saber se há algo por validar → tabela `RH_T_VALIDACAO`**, não o estado do pedido apenas.
- **Descartada a falta órfã sem pedido**: `PEDIDO_ID` é nullable, mas uma órfã em `P` fica
  invisível (não entra nos grupos, que filtram `pedidoId != null`, nem em "por justificar", que a
  vê como falta viva), sem endpoint que lhe toque, e ainda bloqueia o dia e conta nos 3 dias.
- **D3 — reserva por leitura, não por linhas em `P`**; o lock é complementar (fecha a janela de
  milissegundos; a reserva fecha a de dias).

## Constraints

- PR contra `develop`, nunca `main`. Conventional commits.
- **Mostrar SEMPRE o payload completo antes de executar** — mesmo com autorização dada e mesmo num
  retry corrigido. **Pedir autorização antes de cada escrita** (POST/PUT/DELETE e SQL de escrita);
  GET livres. **Mostrar o corpo cru da resposta**, não resumir.
- Filtros de leitura em SQL, não em memória.
- `pedidoId`/`funcionarioId` nos paths são **UUID**; `itensFalta[].id` é o id da **síntese**.

## Blockers & risks

- **`existeFaltaVivaNoDia` (`FaltaEntityRepository:125-135`) trata `E` como bloqueio** — só ignora
  `I`. Consequência viva hoje: depois de eliminar um pedido os dias reaparecem no painel
  (`findAusenciasPorJustificar` exclui `E`) mas o justificar recusa-os com *"Já existe uma falta
  associada à data"*, e uma **nova marcação com justificativo** também. Sem isto corrigido, o
  caminho "faz nova marcação" está fechado à chave.
- **Duplo despacho desconta a dobrar**: `validarFaltaJustificada` não verifica estado nenhum; dois
  `PUT` com `validar:SIM` chamam `aplicar()` duas vezes e criam `DEF_REMUNERACOES` duplicados.
- **Dia não seleccionado no despacho fica órfão em `P`** — o pedido vai a `A`/`I`+`FINALIZADO` e a
  validação fecha, mas o dia fica pendente para sempre, a reservar saldo. É o que a próxima tarefa
  resolve.
- **Fuga já existente**: um dia rejeitado (`I`) deixa de contar no limite dos 3 dias
  (`countFaltasVivasNoPeriodo` só conta `A`/`P`), logo rejeitar 4 e refazer um a um deixa passar 3
  sem despacho. Tecto: 3 dias/mês/colaborador. Decisão do analista.
- **`ORA-17002` intermitente** na ligação directa à BD (rede para 62.84.179.137) — repetir
  resolve; a app tem *pool* e não é afectada.
- **Manifesto IGRP dessincronizado do Java**: `JustificarFaltaDTO.json` desconhece `motivo`,
  `comJustificativo`, `pedidoId`, `estado`, `etapa`, `tipoOrdemServico` e ainda declara
  `despachoRh` (removido). **Correr o `igrp-spring-generator` neste módulo apaga campos em uso.**
- **`RH_T_ANO` só tem 2026**: dedução em férias noutro ano dá 404.

## Relevant files

- `.../assiduidade/application/services/JustificarFaltaWriteService.java` — `justificarFalta`,
  `validarFaltaJustificada` (~:282, alvo da próxima tarefa), `editarPedidoJustificacao` (~:526),
  `eliminarPedidoJustificacao` (~:749), `garantirPedidoActivo` (~:499), `reabrirDespacho` (~:720)
- `.../assiduidade/application/services/FaltaDescontoService.java` — `aplicar`, `reverter`,
  `requerValidacaoNoMes` (agora com exclusão de pedido)
- `.../shared/infrastructure/persistence/repository/FaltaEntityRepository.java` —
  `existeFaltaVivaNoDia:125` (a corrigir), `countFaltasVivasNoPeriodo`, queries da reserva
- `.../assiduidade/application/services/JustificarFaltaReadService.java:128` (resumo filtra `E`),
  `:187-212` (detalhe do pedido **não** filtra nada)
- `.../shared/domain/service/SaldoLockService.java` — lock por colaborador
- `.../funcionario/application/service/remuneracao/RenumeracoesWriteService.java:138-141` — o
  padrão do dossiê para o guard: estado da entidade **e** validação pendente
- `docs/frontend_changes_assiduidade.md` — secções 9–11 cobrem o que mudou a 10/09

## How to verify / resume

**JDK 23 obrigatório**, porta **8087** (não a 8089 do CLAUDE.md). Matar a app **pelo porto** — um
`Stop-Process` filtrado pelo nome deixou uma instância viva a responder com **código velho**.

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE
mvn -q clean compile -DskipTests      # EXIT=0

Get-NetTCPConnection -LocalPort 8087 -State Listen -ErrorAction SilentlyContinue |
  ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
Start-Process mvn.cmd -ArgumentList "spring-boot:run" -RedirectStandardOutput "$env:TEMP\rh-app.log" -WindowStyle Hidden
# esperar "Started RhInpsServiceApplication"; para ver locks juntar
# '-Dspring-boot.run.arguments=--spring.jpa.show-sql=true' e depois grep "for update"
```

As queries JPQL só são validadas **no arranque** — compilar não chega.

HTTP (o `WebClient` evita o mojibake do `Invoke-WebRequest`); **capturar a resposta antes de
imprimir o status**, senão o script mente quando falha:

```powershell
$wc=New-Object System.Net.WebClient; $wc.Encoding=[System.Text.Encoding]::UTF8
$wc.Headers.Add("Content-Type","application/json; charset=utf-8")
try { $r=$wc.UploadString($url,"PUT",$body); "HTTP 200"; $r }
catch [System.Net.WebException] { $resp=$_.Exception.Response; "HTTP " + [int]$resp.StatusCode;
  (New-Object System.IO.StreamReader($resp.GetResponseStream(),[System.Text.Encoding]::UTF8)).ReadToEnd() }
```

O `WebClient` **limpa o `Content-Type` depois de cada pedido** — criar um novo por chamada, senão
o segundo dá 415.

SQL directo — `DbExec` para escrita (`DbUpdate` dá ORA-17273), sem `FETCH FIRST`, com retry por
causa do ORA-17002:

```bash
cd tools/db
cp=".;C:/Users/ivanick.santos/.m2/repository/com/oracle/database/jdbc/ojdbc11/23.7.0.25.01/ojdbc11-23.7.0.25.01.jar"
"C:/Program Files/Eclipse Adoptium/jdk-23.0.2.7-hotspot/bin/java.exe" -cp "$cp" DbQuery "SELECT ..."
```

Nomes reais: `RH_ASSIDUIDADE_SINTESE_DIARIA` tem **`FUNCIONARIO_ID`**; `RH_T_ASSIDUIDADE_PARAMETRO`;
`RH_T_TIPOS_DOCUMENTOS`; `RH_T_DISPENSA` liga-se pelo `PEDIDO_ID`; `RH_T_PARAM_SITUACAO` **não tem**
`DESCRICAO`. Saldo de dispensa usa `?data=`, não `?dataReferencia=`.

## Test / validation plan

**Fixtures:** colaborador **Nuno Teste Sync**, id **958937**, uuid
`01a085fa-fc04-7f08-a5d9-75b4b8a886b7`, tiprel 173442. Responsável `RH_T_RESPONSAVEL.ID=23`.
Tipos: **18** Falta Injustificada (`SAL=1`), **20** Doença do Trabalhador (`SAL=0`), 17 Motivo
Pessoal. `RH_T_ANO`: id **2** = 2026. Dispensa: **4h/mês**. Valor da falta: **6 344,56/dia** (8h).
Meses limpos: Outubro, Novembro, Dezembro (Setembro já tem faltas — pedidos 193/195/196).
Atenção: >3 dias no mês com tipo que desconta manda o pedido a `P`.

Direito de férias (a tabela está vazia) e ausências têm de ser criados à mão:

```sql
INSERT INTO RH_T_FERIAS (ID, ANO_ID, FUN_ID, NUM_DIA, ESTADO, DATA_REGISTO,
                         USER_REGISTO_ID, USER_REGISTO_NAME, UUID)
VALUES (1, 2, 958937, 2, 'A', SYSDATE, 1, 'teste', 'teste-direito');

INSERT INTO RH_ASSIDUIDADE_SINTESE_DIARIA
  (ID, FUNCIONARIO_ID, DATA, MES, ANO, HORAS_TRABALHADAS, HORAS_AUSENCIA, FALTA,
   ESTADO, DATA_REGISTO, USER_REGISTO_ID, FLAG_RECECAO, USER_REGISTO_NAME, FORMA)
VALUES (930, 958937, DATE '2026-12-01', 12, 2026,
        INTERVAL '0 00:00:00' DAY TO SECOND, INTERVAL '0 08:00:00' DAY TO SECOND, 1,
        'A', SYSDATE, 1, '1', 'teste', 'MANUAL');
```

### Por provar (a próxima tarefa)

Pedido base: **4 dias** num mês limpo com tipo 18 → fica `P`.

| # | Acção | Esperado |
|---|---|---|
| W1 | despachar `SIM` com o 4.º item a `selecionar:false` | os **4** dias em `A` com desconto, pedido `A` (hoje o 4.º ficava órfão em `P`) |
| W2 | despachar mandando só 3 itens no array | os **4** dias decididos à mesma — o array já não manda |
| W3 | despachar `NAO` | 4 dias em `I`, zero efeitos, pedido `I`, validação fechada em `I` |
| W4 | repetir o despacho de um pedido já `A` | **400** *"Só é possível despachar um pedido de justificação pendente de validação."* e **nenhum** `DEF_REMUNERACOES` novo |
| W5 | despachar um pedido nascido `A` (≤3 dias) | mesmo 400 |
| W6 | eliminar um pedido e fazer **nova marcação** nesse dia (com justificativo) | **200** — hoje dá 400 *"Já existe uma falta associada à data"* |
| W7 | pedido com `FERIAS` e direito 2: rejeitar | saldo volta de **0** a **2** |

Evidência a capturar: corpo cru da resposta + `SELECT ID, ESTADO, DEF_REM_ID FROM RH_T_FALTA WHERE
PEDIDO_ID=...` e `SELECT ESTADO, COUNT(*) FROM RH_T_VALIDACAO WHERE REFERENCIA_ID=... GROUP BY
ESTADO`.

### Regressão a repetir (provada nesta sessão; repetir se se mexer no editar)

| # | Cenário | Esperado |
|---|---|---|
| V1b | 4 dias tipo 20 (`A`) → editar para tipo 18 | **`P`**, 0 descontos, **1** validação pendente |
| V2 | editar só o motivo | fica `A`, tipo e desconto preservados, **`DEF_REM_ID` inalterados** |
| V4 | 2 dias tipo 20 → 18 | continua `A` (2 ≤ 3) — prova a exclusão do próprio pedido |
| Reg-A | tipo 18 → 20 | descontos a `E`, nenhum novo, `FLG_DESCONTO_SAL=0` |
| Reg-B | `FERIAS` → `DISPENSA` | dia 1: dispensa 240 min + **3 172,28**; dia 2: **6 344,56**; saldo devolvido |
| V6a | editar só o motivo num pedido com dedução | dedução **não** é apagada (saldo continua consumido) |
| V6b | `deduzirFaltaEm:"NENHUM"` | férias devolvidas, desconto salarial criado |
| A1.7 | guard com remuneração `A` vs `I` | 400 com activa, 200 com anulada |
| Lock | 2 POST em paralelo, direito 1 dia | um gozado, outro `DEF_REM`; direito 1 / gozados 1 |

**Limpeza** (a ordem importa): `RH_T_REMUNERACOES` (do guard) → `RH_T_TIPREL_REM_PAG` →
`RH_T_FALTA` → `RH_T_DEF_REMUNERACOES` → `RH_T_FERIAS_GOZADAS` → `RH_T_DISPENSA` →
`RH_T_DOCUMENTO` → `RH_T_VALIDACAO` → `RH_T_PEDIDO` → `RH_ASSIDUIDADE_SINTESE_DIARIA` →
`RH_T_FERIAS`.

## Open questions

- **O dia de um pedido eliminado deve voltar ao painel "por justificar"?** Hoje volta (a consulta
  exclui `E`), o que contraria "E é final". Ou o painel deixa de o oferecer, ou aceita-se que
  eliminar liberta o dia — e aí basta corrigir o `existeFaltaVivaNoDia`. Recomendação: a segunda.
- **A fuga dos 3 dias por rejeição** (ver *Blockers*) — analista.
- **C9** `TIPO_CONTAGEM_DIAS` nunca lido no fluxo de falta (recomendação escrita: excluir só
  fim-de-semana, nulos = corridos, só no Marcar Falta); **C10** `NUM_DIAS_ABONOS` ignorado;
  **C5b** hora extra grava `ETAPA='VALIDACAO'`; **C4/C6** dependem do DBA.
- **18 faltas com desconto a dobrar** em BD (`FLG_DESCONTO_FALTA IS NOT NULL AND DEF_REM_ID IS NOT
  NULL`) — corrigir dados ou deixar histórico?

## Next step

Implementar o **despacho tudo-ou-nada**, começando pelo `existeFaltaVivaNoDia` (que hoje trata
`E` como bloqueio e fecha à chave o caminho "faz nova marcação"). Plano em
`~/.claude/plans/planear-o-que-deve-sequential-thompson.md`, a ajustar com as decisões da última
conversa. E fazer `git push` dos 2 commits por enviar.

> Updated: 2026-09-10 15:40 -01:00

## Goal

Alinhar o backend de **Assiduidade** com a spec
`docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md`, validando tudo
live contra a Oracle de dev. A auditoria da spec está feita (19 lacunas). O lote B+C está
fechado; falta **corrigir a regra de desconto da falta** (regra de negócio fixada a 10/09) e
**implementar Editar e Eliminar do pedido de justificação** (hoje 501).

## Current state

3 commits em `develop`, **sem push**, todos com `mvn clean compile` limpo e verificados live:

- `ed137ef0` — validar procurava a validação por `FUN_ID` mas grava-a por `REFERENCIA_UUID`;
  com dois pedidos pendentes do mesmo colaborador rebentava (non-unique). Falta + justificação.
- `e8b54afb` — lote B+C: anexos (marcar falta lia `RH_T_FALTA` e escrevia `RH_T_PEDIDO`;
  férias procurava por `FERIA`; hora extra por `pedido.uuid` em vez de `he.uuid`), validação de
  férias com fallback `INSERT`→`UPDATE`, remuneração da hora extra registada depois do
  recálculo, `valorAusencia` → `BigDecimal`, `DESPACHO_RH`=SIM/NAO, `ETAPA='FINALIZADO'`
  também ao rejeitar, `FLG_DESCONTO_SAL` gravado no registo.
- `1565e06c` — painel "por justificar" só oferece dias que são ausência, com o filtro em SQL
  (`findAusenciasPorJustificar`).

Working tree limpo (só `bash.exe.stackdump`, lixo untracked). BD de dev limpa: colaborador de
teste sem nada depois de Setembro/2026.

**Por fazer:**

| # | Item | Nota |
|---|---|---|
| **D1** | **Regra de desconto da falta** (saldo cobre / vencimento paga o resto) — regra fixada pelo utilizador, ver abaixo | é o próximo trabalho |
| **A1** | `editarPedidoJustificacao` / `eliminarPedidoJustificacao` | 501; a seguir ao D1 |
| C4 | Lista Gestão Falta: excluir quem tem ausência activa; estado do mês dá `INJUSTIFICADA` mesmo com dias justificados | vista `RH_V_RESUMO_ASSIDUIDADE` (DDL) |
| C5b | Hora extra grava `ETAPA='VALIDACAO'`, fora do domínio `ETAPA_PROCESSO`, já publicado ao frontend | breaking change |
| C6 | Coluna Motivo na lista de faltas | `RH_V_FALTA_MENSAL` não tem o campo e é agregada por mês |
| C9 | `TIPO_CONTAGEM_DIAS` (`DIAS_CORRIDO`/`DIAS_UTEIS`) nunca é lido — marcar falta conta sábados e domingos (01→14/10 gerou 14 sínteses) | lacuna nova |
| C10 | `NUM_DIAS_ABONOS` (dias de direito por tipo: Maternidade 90, Paternidade 10) ignorado no fluxo de falta | lacuna nova |
| — | Correcção dos dados das **18 faltas** com desconto a dobrar — **só depois de todos os cenários provados** | decidido pelo utilizador |
| — | Entrada do `valorAusencia` decimal em `docs/frontend_changes_assiduidade.md` | documentação |

**Fora do nosso âmbito** (outro programador): regularização de contas sem
`RH_T_DEF_REMUNERACOES` (TODO em `RegularizacaoService:137`), baixa médica sem
`RH_T_ABONOS_BENEFICIOS_DET`, ausência da baixa a apontar à tabela errada, continuidade de
licença.

## Decisões tomadas — não re-litigar

- **REGRA DE DESCONTO DA FALTA (fixada pelo utilizador a 10/09, é a decisão central):**
  o saldo cobre o que consegue e o **vencimento paga o resto**. Não é "ou um ou outro" —
  são duas fases da mesma cobrança. Exemplo dado pelo utilizador: 4 dias de falta com 2 dias
  de saldo → 2 dias saem das férias, 2 dias são descontados no salário.
  - **Cobre-se pelos primeiros dias** (hipótese A, ordem cronológica); o saldo esgota-se e os
    dias seguintes vão ao vencimento.
  - **Dispensa cobre parcialmente**: a unidade é a hora. 8h de ausência com 3h de saldo →
    consome 3h e desconta ao salário o valor de 5h (`valor à hora × horas não cobertas`; o
    `FaltaValorCalculator` já calcula à hora, não é preciso maquinaria nova).
  - **A dedução é escolha e responsabilidade do utilizador** — o backend não bloqueia nem
    ignora o campo, mesmo quando o tipo não desconta salário (`FLG_FALTA_DECONTO_SAL=0`).
  - **Consequência**: o guard de `FaltaDescontoService:180`, que hoje rejeita com 400 quando o
    saldo de férias não chega para todos os dias, **tem de desaparecer** — deve cobrir o que dá
    e mandar o resto para o vencimento. E o `DEF_REMUNERACOES` passa a nascer **só para os dias
    não cobertos**, em vez de para todos.
- **Editar e Eliminar são endpoints próprios por `pedidoUuid`**, agem no pedido inteiro.
- **Registos revertidos ficam em `E`** (não `I`) — decidido a 10/09. `RH_T_TIPREL_REM_PAG` não
  tem coluna de estado, aí a linha é apagada.
- **O editar NÃO volta a validação** por agora — decidido a 10/09. Grava direto, seja o que for
  que mude.
- **O finder do saldo de dispensa entra no lote do A1** — deixou de ser opcional: como os
  revertidos ficam em `E` e `DispensaHorasService:45` soma sem filtrar estado, sem esta
  correcção o eliminar não devolve as horas. Critério: somar só `A` e `P` (a pendente ainda
  reserva as horas; a rejeitada e a eliminada libertam-nas). Em BD só existem `A` e `P` hoje,
  logo não há dados a corrigir.
- **Eliminar = soft-delete**: `RH_T_FALTA.ESTADO='E'`. A spec di-lo explicitamente (`:665`).
- **Eliminar desfaz também os efeitos financeiros** e repõe saldo de férias/dispensa —
  decidido pelo utilizador. Sem isso o colaborador fica descontado por uma falta eliminada.
- **Editar e Eliminar bloqueiam (400) se já houver processamento associado** — decidido pelo
  utilizador; não vem da spec, vem do padrão do dossiê (`CarreiraWriteService:66`).
- **Desenho acordado**: escrever um `FaltaDescontoService.reverter(falta)` simétrico do
  `aplicar()`, partilhado pelos dois. O editar **não** é um update de campos: reverte os
  efeitos, aplica as alterações e reaplica. Ver *Open questions* antes de codificar.
- **C2 fechado sem alteração**: a spec diz `DEF_PAGAMENTOS`/`DEF_PAG_ID` na validação da
  justificação (`:857`), mas `RH_T_FALTA` **não tem** coluna `DEF_PAG_ID` — só `DEF_REM_ID`,
  com `FK_RH_FALTA_REM → RH_T_DEF_REMUNERACOES`. É erro do analista, o código está certo.
- **C3 resolvido**: `DESPACHO_RH` é `VARCHAR2(3)` **de propósito** — guarda SIM/NAO, como
  `DECISAO_RH` da dispensa e das férias. Não há nada a escalar ao DBA (contradiz o handoff
  anterior, que dizia que a coluna era pequena de mais).
- **Anexos da justificação pertencem ao PEDIDO** (`REFERENCIA_NAME='RH_T_PEDIDO'`), divergindo
  da spec. **Não existe anexo por dia.** `motivo`/`comJustificativo` são do cabeçalho.
- **Ciclo CORRIGIR (validar/rejeitar/**corrigir**) em standby** — verificado que não existe em
  assiduidade; decisão do utilizador a 10/09 para não o fazer agora.

## Constraints

- PR contra `develop`, nunca `main`. Conventional commits.
- **Mostrar SEMPRE o payload completo antes de o executar** — mesmo com autorização já dada e
  mesmo num retry corrigido. O utilizador insistiu nisto.
- **Pedir autorização antes de cada escrita** (POST/PUT/PATCH/DELETE e SQL de escrita). GET livres.
- **Mostrar o corpo cru da resposta** (HTTP status + JSON indentado), não resumir em tabela.
- **Filtros de leitura em SQL, não em memória** — correcção pedida pelo utilizador em `1565e06c`.
- Arrays nos PUT: completos e com `id` — sem id cria, omitido fica `E`, `null` preserva.
- `pedidoId`/`funcionarioId` nos paths são **UUID**; `itensFalta[].id` é o id da **síntese
  diária**, não o da falta.

## Blockers & risks

- Nenhum bloqueio técnico. O D1 (regra de desconto) está totalmente especificado e pode avançar
  já; o A1 espera pelas respostas das *Open questions*.
- **`RH_T_TIPREL_REM_PAG` não tem coluna ESTADO** — no reverter só resta apagar a linha.
- **O saldo de dispensa não filtra estado**: `DispensaHorasService:45` soma
  `findAllByPedidoId_FunId_UuidAndDataInicioBetween` sem olhar ao estado, logo uma dispensa
  posta a `E` **continua a consumir as horas do mês**. Sem corrigir isto o eliminar não repõe
  o saldo. (O de férias já filtra `estado='A'` — `FeriasGozadasEntityRepository:28`.)
- **18 faltas em BD com desconto a dobrar**: têm tipo com `FLG_FALTA_DECONTO_SAL=1` **e**
  `FLG_DESCONTO_FALTA='DISPENSA'`, cada uma com `DEF_REM_ID` preenchido *e* linha em
  `RH_T_DISPENSA` — levaram corte integral no vencimento e consumo de horas pelos mesmos dias.
  Pela regra fixada estão erradas. **A correcção dos dados fica para o fim, depois de todos os
  cenários estarem provados** (decisão do utilizador). SQL para as encontrar:
  `SELECT * FROM RH_T_FALTA WHERE FLG_DESCONTO_FALTA IS NOT NULL AND DEF_REM_ID IS NOT NULL`.
- `RH_PROCESSAMENTO_SALARIAL_DB` tem o package body inválido (ORA-04063): `CALCULO_FALTA_DIARIO`
  cai no fallback Java e funciona. Não confundir com bug nosso.
- `CALCULO_FALTA_DIARIO` não existe na BD; `PARECER_DECISAO` está por povoar em dev (só `TETS`).
- **`RH_T_ANO` só tem 2026**: qualquer falta com dedução em férias noutro ano rebenta com
  `404 "Ano de referência 2027 não encontrado em RH_T_ANO"` (`FaltaDescontoService.resolverAno`).
  É dados, não código, mas morde no virar do ano.

## Relevant files

- `src/main/java/cv/inps/rh/assiduidade/application/services/JustificarFaltaWriteService.java:454`
  e `:474` — os dois stubs com TODO (`editarPedidoJustificacao`, `eliminarPedidoJustificacao`);
  `justificarFalta` ~L76, `validarFaltaJustificada` ~L263
- `src/main/java/cv/inps/rh/assiduidade/application/services/FaltaDescontoService.java:79` —
  `aplicar()`: os três ramos a reverter. `:121` salário, `:173` férias, `:198` dispensa
- `src/main/java/cv/inps/rh/assiduidade/application/services/DispensaHorasService.java:45` — o
  saldo que ignora o estado
- `src/main/java/cv/inps/rh/funcionario/application/service/carreira/CarreiraWriteService.java:66` —
  padrão do guard "já processado" a replicar
- `src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/ProcessamentoFuncionarioRepository.java:45` —
  `existsByTiprel_IdAndDataReferenciaDeBetween`, candidato ao guard por mês
- `docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md:655-668` — Editar e
  Eliminar (o documento diz pouco: o Editar tem a coluna de gravação vazia e a descrição do
  Eliminar é copy-paste da do Editar)
- `.igrpstudio/assiduidade/controllers/AssiduidadeController.json` — 39 actions

## How to verify / resume

Duas armadilhas de ambiente que custam tempo: **JDK 23 obrigatório** (o `JAVA_HOME` do sistema
aponta para outra versão) e **porta 8087**, não a 8089 do CLAUDE.md. Se o arranque falhar com
`ClassFormatError`, é lixo incremental: `mvn clean compile` resolve.

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE
mvn -q clean compile -DskipTests      # EXIT=0
Start-Process mvn.cmd -ArgumentList "spring-boot:run" -RedirectStandardOutput "$env:TEMP\rh-app.log" -WindowStyle Hidden
# esperar "Started RhInpsServiceApplication" no log (~25s)
git log --oneline -4                  # 1565e06c no topo
```

Nota: `nohup ... &` pelo Bash **não** funciona (o processo morre com a shell); usar
`Start-Process`. A app pode estar já de pé — confirmar com `netstat -ano | grep ":8087"`.

HTTP (o `WebClient` evita o mojibake que o `Invoke-WebRequest` produz nos acentos):

```powershell
$wc=New-Object System.Net.WebClient; $wc.Encoding=[System.Text.Encoding]::UTF8
($wc.DownloadString("http://localhost:8087/api/v1/assiduidade/falta/justificar/01a085fa-fc04-7f08-a5d9-75b4b8a886b7?ano=2026&mes=9")) |
  ConvertFrom-Json | ConvertTo-Json -Depth 12
```

SQL directo — **`DbExec` para escrita** (`DbUpdate` dá ORA-17273) e sem `FETCH FIRST`
(`WHERE ROWNUM<=n` numa subquery, senão ORA-00933):

```powershell
cd tools\db
$cp=".;C:/Users/ivanick.santos/.m2/repository/com/oracle/database/jdbc/ojdbc11/23.7.0.25.01/ojdbc11-23.7.0.25.01.jar"
& "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot\bin\java.exe" -cp $cp DbQuery "SELECT ..."
```

Nomes reais (vários palpites falharam): `RH_T_FALTA`, `RH_T_PEDIDO`, `RH_T_DOCUMENTO`,
`RH_ASSIDUIDADE_SINTESE_DIARIA` (a coluna é **`FUNCIONARIO_ID`**, não `FUN_ID`),
`RH_T_TIPOS_DOCUMENTOS`, `RH_T_DOMAINS` (colunas `DOMINIO`/`VALOR`/`REFERENCIA`),
`RH_V_RESUMO_ASSIDUIDADE`, `RH_V_FALTA_MENSAL`. `RH_T_DISPENSA` **não tem** `FUN_ID` (liga-se
pelo `PEDIDO_ID`).

## Test / validation plan

**Fixtures reais (confirmados a 10/09):** colaborador **Nuno Teste Sync, id 958937, uuid
`01a085fa-fc04-7f08-a5d9-75b4b8a886b7`, tiprel 173442**. Responsável `RH_T_RESPONSAVEL.ID=23`.
Tipos de falta: **17** (Motivo Pessoal) e **18** (Falta Injustificada), ambos com
`FLG_FALTA_DECONTO_SAL=1`. Tipo de documento **26** (`REFERENCIA='JUSTIFICACAO_FALTA'`).
`RH_T_ANO`: id **2** = 2026. **`RH_T_FERIAS` está vazia** — ninguém tem direito de férias em
dev; para testar dedução em férias é preciso inserir o direito primeiro:

```sql
INSERT INTO RH_T_FERIAS (ID, ANO_ID, FUN_ID, NUM_DIA, ESTADO, DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME, UUID)
VALUES ((SELECT NVL(MAX(ID),0)+1 FROM RH_T_FERIAS), 2, 958937, 22, 'A', SYSDATE, 1, 'teste', 'teste-direito-2026');
```

Estado limpo deixado em Setembro/2026 (confirmar antes de começar): pedidos **193**, **195**,
**196** todos `A`/`FINALIZADO`; validação **1110** (pedido 196) `A`; nada depois de Setembro.

**Cenários da regra de desconto (D1) — provar TODOS antes de corrigir os dados das 18 linhas.**
Valor/dia em dev = 6344,56. Tipo 18 (`SAL=1`); tipo 20 Doença do Trabalhador (`SAL=0`).

| # | Setup | Acção | Esperado |
|---|---|---|---|
| D1.1 | saldo férias 4, falta 2 dias, `FERIAS` | validar | 2 gozados, saldo 2, **sem** `DEF_REM` |
| D1.2 | saldo férias 2, falta 4 dias, `FERIAS` | validar | 2 gozados (dias 1-2), saldo 0, `DEF_REM` de 2 dias = 12 689,12 nos dias 3-4. **Hoje dá 400** |
| D1.3 | saldo férias 0, falta 2 dias, `FERIAS` | validar | 0 gozados, `DEF_REM` de 2 dias. **Hoje dá 400** |
| D1.4 | falta 2 dias, campo vazio | validar | `DEF_REM` de 2 dias, férias intactas (comportamento actual, não pode regredir) |
| D1.5 | saldo dispensa 3h, falta 1 dia de 8h, `DISPENSA` | validar | `RH_T_DISPENSA` de 3h, `DEF_REM` = valor à hora × 5h |
| D1.6 | saldo dispensa ≥8h, falta 1 dia de 8h, `DISPENSA` | validar | dispensa de 8h, **sem** `DEF_REM` |
| D1.7 | tipo 20 (`SAL=0`), falta 2 dias, `FERIAS` | validar | 2 gozados (o utilizador escolheu e é responsabilidade dele), **sem** `DEF_REM` |
| D1.8 | tipo 20 (`SAL=0`), falta 2 dias, campo vazio | validar | nada: sem `DEF_REM`, sem gozados |

Evidência a capturar em cada um: resposta crua do endpoint + SQL de `RH_T_FALTA`
(`DEF_REM_ID`, `FLG_DESCONTO_SAL`), `RH_T_DEF_REMUNERACOES` (valor), `RH_T_FERIAS_GOZADAS` /
`RH_T_DISPENSA`, e o saldo antes/depois (`GET .../feria/saldo/{funUuid}`,
`GET .../dispensa/saldo/{funUuid}`).

Atenção: o mesmo `aplicar()` corre em **dois** caminhos — marcar falta com `justificar=SIM` e
≤3 dias (aplica de imediato) e validar (≥4 dias). Provar os dois.

**Testes a fazer quando o A1 estiver implementado:**

1. **Eliminar sem efeitos** — justificar ≤3 dias com tipo 17 (nasce `A`, sem validação) →
   `DELETE .../falta/justificar/pedido/{uuid}` → esperado: todas as `RH_T_FALTA` do pedido a
   `'E'`, pedido a `'E'`/`'I'`, anexos a `'E'`. Evidência: SQL das quatro tabelas.
2. **Eliminar com desconto salarial** — justificar ≥4 dias com tipo 18, validar `SIM` (nascem
   `DEF_REMUNERACOES` a 6344,56/dia + `TIPREL_REM_PAG`), depois eliminar → esperado: faltas
   `'E'`, `DEF_REMUNERACOES` revertido, `TIPREL_REM_PAG` sem linhas órfãs, validação fechada.
   Evidência: contagem das três tabelas antes e depois.
3. **Eliminar com dedução em férias** — inserir direito (SQL acima), justificar com
   `deduzirFaltaEm:"FERIAS"`, validar → nasce `FERIAS_GOZADAS`; eliminar → esperado: saldo
   volta ao valor inicial. Evidência: `GET .../feria/saldo/{funUuid}` antes e depois.
4. **Eliminar com dedução em dispensa** — idem com `"DISPENSA"` → nasce `RH_T_DISPENSA`;
   eliminar → esperado: horas do mês repostas. **Este é o que falha hoje** se o finder de
   `DispensaHorasService:45` não for corrigido. Evidência:
   `GET .../dispensa/saldo/{funUuid}` antes e depois.
5. **Guard de processado** — criar processamento do tiprel no mês da falta (ou preencher
   `DEF_REMUNERACOES.DATA_ULTIMO_PROC`, conforme o critério escolhido) → editar e eliminar →
   esperado: **400** nos dois, com mensagem explícita e **rollback total**.
6. **Editar só cabeçalho** (motivo/observação/anexos) → esperado: grava directo, efeitos
   financeiros intactos, `DEF_REM_ID` inalterado.
7. **Editar trocando `deduzirFaltaEm`** FERIAS → DISPENSA → esperado: `FERIAS_GOZADAS`
   revertida e saldo reposto, `RH_T_DISPENSA` nova criada, **sem desconto duplicado**.
8. **Editar trocando o tipo** de um que desconta salário para um que não desconta → esperado:
   `DEF_REMUNERACOES` + `TIPREL_REM_PAG` revertidos, `FLG_DESCONTO_SAL` a 0.
9. **Editar retirando um dia** do pedido → esperado: a falta desse dia fica `'E'` com os seus
   efeitos revertidos; as restantes mantêm-se; a síntese volta a aparecer no painel "por
   justificar" (`GET .../falta/justificar/{funUuid}?ano&mes`).

**Regressões a repetir se se mexer em `JustificarFalta*Service`:** GET do mês (soltos +
`pedidos[]` ordenados, cada grupo com cabeçalho, `estado`/`etapa`); GET por pedido idêntico ao
grupo correspondente (é o mesmo `montarGrupo`); criar ≤3 dias → `A`/`FINALIZADO` com efeitos
imediatos; criar ≥4 dias tipo 18 → `P`/`DESPACHO_RH` sem `DEF_REM_ID`; validar → `A` + efeitos;
`deduzirFaltaEm:"FERIAS"` sem saldo → **400** com rollback total.

**Limpeza** (a ordem importa, `FK_DISPENSA_PEDIDO` bloqueia o pedido):
`RH_T_TIPREL_REM_PAG` (por `REM_ID`) → `RH_T_DOCUMENTO` → `RH_T_FALTA` → `RH_T_HORA_EXTRA` →
`RH_T_DEF_REMUNERACOES` → `RH_T_AUSENCIA` → `RH_T_FERIAS_GOZADAS` → `RH_T_DISPENSA` →
`RH_T_VALIDACAO` → `RH_T_PEDIDO` → `RH_ASSIDUIDADE_SINTESE_DIARIA` → `RH_T_FERIAS`.

## Open questions

Resolvidas a 10/09 (ver *Decisões*): a regra de desconto, a ordem de cobertura, a cobertura
parcial da dispensa e a responsabilidade da dedução. Por responder:

**Bloqueia o Editar / Eliminar (A1) — a última**

- **Critério de "já processado"** que bloqueia os dois com 400:
  `existsByTiprel_IdAndDataReferenciaDeBetween` (o mês da falta já ter processamento — mais
  conservador, bloqueia mais cedo) ou `DEF_REMUNERACOES.DATA_ULTIMO_PROC` preenchido (mais
  preciso, mas só protege depois de a linha ter ido à folha)?

**Âmbito a agendar**

- **C4** — a lista de Gestão Falta deve excluir quem tem ausência activa (a spec manda, `:285`),
  e o estado do mês dá `INJUSTIFICADA` mesmo com dias já justificados (precedência do `CASE` em
  `docs/sql/rh_v_resumo_assiduidade.sql:137`). Mexe na vista.
- **C5b** — mudar `ETAPA='VALIDACAO'` da hora extra para `DESPACHO_RH` é breaking para o
  frontend (já documentado em `frontend_changes_assiduidade.md:75`).
- **C6** — coluna Motivo na lista de faltas: a vista é agregada por mês, logo há que decidir o
  que mostrar quando o mês tem motivos diferentes.
- **C9 / C10** — respeitar `TIPO_CONTAGEM_DIAS` (dias úteis vs corridos) e `NUM_DIAS_ABONOS`
  (dias de direito por tipo) no fluxo de falta. Hoje ambos ignorados; o modelo de dados já os
  suporta e a licença já usa o conceito via `CALCULO_FALTA_LICENCA`.

## Next step

**D1 — corrigir a regra de desconto** em `FaltaDescontoService.aplicar()`: cair o guard de
saldo insuficiente (`:180`), cobrir pelos primeiros dias, cobertura parcial na dispensa, e
`DEF_REMUNERACOES` só para o que o saldo não cobriu. Está totalmente especificado, não depende
de mais nenhuma resposta. Só depois o A1 (`reverter()` + editar + eliminar), que espera pelas
*Open questions*.

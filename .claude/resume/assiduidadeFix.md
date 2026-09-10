> Updated: 2026-09-10 17:25 -01:00

## Goal

Alinhar o backend de **Assiduidade** com a spec
`docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md`, validando tudo live
contra a Oracle de dev. A auditoria da spec está feita (19 lacunas). Os lotes B, C, D1, D2 e A1
estão fechados e provados. Falta a **reserva de saldo + concorrência (D3)** e cinco itens que
mexem em vistas Oracle ou em contrato já publicado.

## Current state

**7 commits de código em `develop`, sem push**, todos com `mvn clean compile` limpo e
verificados live contra a BD. Nada foi dado como feito sem prova em BD — a coluna "provado" da
tabela de cenários diz o que foi corrido e com que resultado.

| Commit | O quê |
|---|---|
| `ed137ef0` | validar procurava a validação por `FUN_ID` mas grava-a por `REFERENCIA_UUID` — com dois pedidos pendentes do mesmo colaborador rebentava (non-unique) |
| `e8b54afb` | lote B+C: anexos (3 casos de chave de leitura ≠ escrita), validação de férias com fallback `INSERT`→`UPDATE`, remuneração da hora extra depois do recálculo, `valorAusencia`→`BigDecimal`, `DESPACHO_RH`=SIM/NAO, `ETAPA='FINALIZADO'` também ao rejeitar, `FLG_DESCONTO_SAL` no registo |
| `1565e06c` | painel "por justificar" só oferece dias que são ausência, filtro em SQL |
| `ae561324` | **regra de desconto** (D1) + **contagem mensal** (D2) — breaking |
| `b66c317f` | **editar e eliminar** do pedido de justificação (A1) + `reverter()` + saldo de dispensa só conta aprovadas |
| `6d7f16a2` | **guard de processado** pela remuneração efectiva (`RH_T_REMUNERACOES.REM_1_ID`) |
| `9fa527ed`, `db75b544`, `5980efbc` | handoff e decisões |

Working tree limpo (só `bash.exe.stackdump`, lixo untracked). **BD de dev limpa**: colaborador
de teste sem pedidos, sínteses ou direitos de férias criados nos testes.

**Por fazer:**

| # | Item | Nota |
|---|---|---|
| **D3** | **Reserva de saldo + concorrência** | secção própria abaixo |
| C4 | Lista Gestão Falta: excluir quem tem ausência activa (spec `:285`); estado do mês dá `INJUSTIFICADA` mesmo com dias justificados | vista `RH_V_RESUMO_ASSIDUIDADE` (DDL) |
| C5b | Hora extra grava `ETAPA='VALIDACAO'`, fora do domínio `ETAPA_PROCESSO` e já publicado | breaking |
| C6 | Coluna Motivo na lista de faltas | `RH_V_FALTA_MENSAL` não tem o campo e é agregada por mês |
| C9 | `TIPO_CONTAGEM_DIAS` (`DIAS_CORRIDO`/`DIAS_UTEIS`) nunca é lido — marcar falta conta sábados e domingos | lacuna nova |
| C10 | `NUM_DIAS_ABONOS` (dias de direito por tipo: Maternidade 90, Paternidade 10) ignorado no fluxo de falta | lacuna nova |
| — | Corrigir os dados das **18 faltas** com desconto a dobrar | a condição era "depois de todos os cenários provados" — já estão |
| — | `docs/frontend_changes_assiduidade.md` | documentação, ver *Next step* |

**Fora do nosso âmbito** (outro programador): regularização de contas sem `RH_T_DEF_REMUNERACOES`
(TODO em `RegularizacaoService:137`), baixa médica sem `RH_T_ABONOS_BENEFICIOS_DET`, ausência da
baixa a apontar à tabela errada, continuidade de licença.

### D3 — reserva de saldo e concorrência (por fazer)

Dois problemas distintos, levantados pelo utilizador.

**Reserva.** Decidido que **só os aprovados contam** para o saldo. Consequência aceite: dois
pedidos pendentes podem consumir o mesmo saldo. A janela é longa — do pedido ao despacho, dias.

Mudar o filtro para `A`+`P` **não chega** no fluxo das faltas: as linhas de efeito
(`FERIAS_GOZADAS`, `DISPENSA`, `DEF_REMUNERACOES`) só nascem quando a falta fica `A`, logo
enquanto está `P` não há nada para contar. Reservar a sério implica:

1. Criar as linhas de efeito em `P` no registo e passá-las a `A` no despacho — muda o papel do
   `FaltaDescontoService.aplicar()`, que hoje assume que só corre para faltas activas;
2. A rejeição (`validar=NAO`) passar a pôr essas linhas em `I`/`E` — hoje não há nada a desfazer;
3. Os dois saldos passarem a contar `A`+`P` (`FeriasGozadasEntityRepository:29,33` e
   `DispensaHorasService:45`);
4. Repetir a bateria D1 inteira: o cálculo de cobertura passa a ver consumo pendente.

**Concorrência.** Mesmo com reserva, dois pedidos em simultâneo lêem o mesmo saldo antes de
qualquer um gravar. Não existe **um único lock em todo o projecto** (`@Lock`, `@Version`,
`PESSIMISTIC`: zero ocorrências), logo é lacuna transversal — férias, dispensa e falta.
`docs/plano_teste_carreira_mobilidade_concorrencia.md` não trata disto: ali "concorrência" é
"duas dimensões pendentes ao mesmo tempo".

## Decisões tomadas — não re-litigar

**Regra de desconto da falta** (a decisão central, fixada pelo utilizador a 10/09):

- O **saldo cobre o que consegue e o vencimento paga o resto**. Não são alternativas nem se
  acumulam — são duas fases da mesma cobrança. 4 dias de falta com 2 de saldo → 2 dias gozados
  e 2 dias descontados.
- **Cobre-se pelos primeiros dias** (ordem cronológica).
- **Dispensa cobre parcialmente**, por contar em horas: 8h de ausência com 4h de saldo consomem
  4h e descontam o valor das outras 4h.
- **A dedução é escolha e responsabilidade do utilizador**: um tipo que não desconta salário
  deduz na mesma se o campo vier preenchido, e nunca gera desconto.
- **Os 3 dias que mandam o pedido a despacho contam-se POR MÊS**, não por pedido. Só faltas
  vivas (`A` e `P`). Sem retroactividade: só o pedido novo vai a despacho.
- Consequência: o guard que rejeitava com 400 por saldo insuficiente **desapareceu**.

**Editar e Eliminar:**

- Endpoints próprios por `pedidoUuid`, agem no pedido inteiro.
- **Eliminar = soft-delete** (`RH_T_FALTA.ESTADO='E'`, spec `:665`) **e desfaz os efeitos
  financeiros** — sem isso o colaborador ficava descontado por uma falta que já não existe.
- **Editar reverte e reaplica**, não faz update dos efeitos: trocar "Deduzir em" de FERIAS para
  DISPENSA deixava as férias gozadas lá e criava a dispensa por cima.
- **Editar não volta a validação.** Grava direto.
- **Registos revertidos ficam em `E`**; `RH_T_TIPREL_REM_PAG` não tem estado, a linha é apagada.
- **Guard de processado** (critério dado pelo utilizador a 10/09, em SQL):

  ```sql
  SELECT COUNT(a.ID) FROM RH_T_FALTA a
    JOIN RH_T_DEF_REMUNERACOES b ON b.ID = a.DEF_REM_ID
    JOIN RH_T_REMUNERACOES     c ON c.REM_1_ID = b.ID
   WHERE a.PEDIDO_ID = :pedidoId AND a.ESTADO <> 'E';
  ```

  **Basta uma** falta processada para bloquear o pedido inteiro (400). Olha para a linha
  concreta — o desconto já ter sido apanhado por uma remuneração efectiva — e não para o mês.
  Substituiu um primeiro guard por `RH_T_PROC_FUNCIONARIOS`, que era mais conservador.
  Nativa porque `RH_T_REMUNERACOES` está mapeada (`RhTRemuneracoe`) **sem** o `REM_1_ID` e sem
  repositório — a coluna existe e está preenchida em todas as linhas.
- **Saldos só contam aprovados** (`A`). Ver D3 para a consequência.

**Da auditoria da spec:**

- **C2 fechado sem alteração**: a spec diz `DEF_PAGAMENTOS`/`DEF_PAG_ID` na validação da
  justificação (`:857`), mas `RH_T_FALTA` **não tem** essa coluna — só `DEF_REM_ID`, com
  `FK_RH_FALTA_REM → RH_T_DEF_REMUNERACOES`. Erro do analista; o código está certo.
- **C3 resolvido**: `DESPACHO_RH` é `VARCHAR2(3)` **de propósito** — guarda SIM/NAO, como o
  `DECISAO_RH` da dispensa e das férias. Nada a escalar ao DBA (contradiz um handoff anterior).
- **Anexos da justificação pertencem ao PEDIDO** (`REFERENCIA_NAME='RH_T_PEDIDO'`), divergindo
  da spec. Não existe anexo por dia. `motivo`/`comJustificativo` são do cabeçalho.
- **Ciclo CORRIGIR em standby** — verificado que assiduidade não o tem (só validar/rejeitar);
  decisão do utilizador para não o fazer agora.

## Constraints

- PR contra `develop`, nunca `main`. Conventional commits.
- **Mostrar SEMPRE o payload completo antes de o executar** — mesmo com autorização já dada e
  mesmo num retry corrigido. O utilizador insistiu nisto.
- **Pedir autorização antes de cada escrita** (POST/PUT/PATCH/DELETE e SQL de escrita). GET livres.
- **Mostrar o corpo cru da resposta** (HTTP status + JSON indentado), não resumir em tabela.
- **Filtros de leitura em SQL, não em memória.**
- Arrays nos PUT: completos e com `id` — sem id cria, omitido fica `E`, `null` preserva.
- `pedidoId`/`funcionarioId` nos paths são **UUID**; `itensFalta[].id` é o id da **síntese
  diária**, não o da falta.

## Blockers & risks

- Nenhum bloqueio. O D3 está especificado e pode arrancar; os restantes esperam decisão.
- **18 faltas em BD com desconto a dobrar**: tipo com `FLG_FALTA_DECONTO_SAL=1` **e**
  `FLG_DESCONTO_FALTA='DISPENSA'`, cada uma com `DEF_REM_ID` *e* linha em `RH_T_DISPENSA`. Pela
  regra fixada estão erradas. Encontrá-las:
  `SELECT * FROM RH_T_FALTA WHERE FLG_DESCONTO_FALTA IS NOT NULL AND DEF_REM_ID IS NOT NULL;`
- **O guard só apanha faltas com desconto salarial.** Uma falta coberta a 100% por férias ou
  dispensa não tem `DEF_REM_ID`, logo nunca casa com a query e continua editável e eliminável
  mesmo depois de a folha correr. Se isso for problema, o guard precisa de um segundo braço
  (pelas `FERIAS_GOZADAS`/`DISPENSA`, ou pelo mês). **Por confirmar com o utilizador.**
- O guard também não olha ao **estado** da `RH_T_REMUNERACOES`: uma remuneração anulada bloqueia
  na mesma.
- **`RH_T_ANO` só tem 2026**: qualquer falta com dedução em férias noutro ano rebenta com
  `404 "Ano de referência 2027 não encontrado"` (`FaltaDescontoService.resolverAno`). É dados,
  não código, mas morde no virar do ano.
- **`RH_T_TIPREL_REM_PAG` não tem coluna ESTADO** — o reverter apaga a linha.
- `RH_PROCESSAMENTO_SALARIAL_DB` tem o package body inválido (ORA-04063): `CALCULO_FALTA_DIARIO`
  cai no fallback Java e funciona. Não confundir com bug nosso.
- `PARECER_DECISAO` está por povoar em dev (só `VALOR='TETS'`).

## Relevant files

- `.../assiduidade/application/services/FaltaDescontoService.java` — o coração: `aplicar()` (a
  regra de cobertura), `reverter()` (o simétrico, usado pelo editar e eliminar),
  `requerValidacaoNoMes()` (contagem mensal), `valorPorCobrir()` (proporção da dispensa)
- `.../assiduidade/application/services/JustificarFaltaWriteService.java` —
  `editarPedidoJustificacao`, `eliminarPedidoJustificacao`, `garantirNaoProcessado`, `faltasVivas`.
  O `justificarFalta` (~L76) e o `validarFaltaJustificada` (~L263) são o resto do fluxo
- `.../assiduidade/application/services/DispensaHorasService.java:45` — saldo de horas, agora só
  conta `Estado.A`
- `.../shared/infrastructure/persistence/repository/FaltaEntityRepository.java` —
  `countFaltasVivasNoPeriodo` (contagem mensal dos 3 dias) e `countFaltasProcessadasEmFolha`
  (o guard, nativa)
- `.../shared/infrastructure/persistence/repository/AssiduidadeSinteseDiarioEntityRepository.java` —
  `findAusenciasPorJustificar` (nativa: `FALTA=1 OR HORAS_AUSENCIA > INTERVAL '0' SECOND`, e
  ignora faltas eliminadas)
- `docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md` — `:493` regra dos 3
  dias, `:578-596` efeitos do validar, `:655-668` Editar/Eliminar, `:857` o erro do `DEF_PAG_ID`

## How to verify / resume

Armadilhas de ambiente: **JDK 23 obrigatório** (o `JAVA_HOME` do sistema aponta para outra
versão) e **porta 8087**, não a 8089 do CLAUDE.md. `nohup ... &` pelo Bash **não** funciona (o
processo morre com a shell) — usar `Start-Process`. Se o arranque falhar com `ClassFormatError`,
é lixo incremental: `mvn clean compile`.

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE
mvn -q clean compile -DskipTests      # EXIT=0
Start-Process mvn.cmd -ArgumentList "spring-boot:run" -RedirectStandardOutput "$env:TEMP\rh-app.log" -WindowStyle Hidden
# esperar "Started RhInpsServiceApplication" (~25s); confirmar: netstat -ano | grep ":8087"
git log --oneline -8                  # 6d7f16a2 no topo
```

HTTP — o `WebClient` evita o mojibake que o `Invoke-WebRequest` produz nos acentos:

```powershell
$wc=New-Object System.Net.WebClient; $wc.Encoding=[System.Text.Encoding]::UTF8
($wc.DownloadString("http://localhost:8087/api/v1/assiduidade/falta/justificar/01a085fa-fc04-7f08-a5d9-75b4b8a886b7?ano=2026&mes=9")) |
  ConvertFrom-Json | ConvertTo-Json -Depth 12
```

Em PowerShell, **não** chamar a uma função `Del` (colide com o alias de `Remove-Item`).

SQL directo — **`DbExec` para escrita** (`DbUpdate` dá ORA-17273) e sem `FETCH FIRST`
(`WHERE ROWNUM<=n` numa subquery, senão ORA-00933):

```powershell
cd tools\db
$cp=".;C:/Users/ivanick.santos/.m2/repository/com/oracle/database/jdbc/ojdbc11/23.7.0.25.01/ojdbc11-23.7.0.25.01.jar"
& "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot\bin\java.exe" -cp $cp DbQuery "SELECT ..."
```

Nomes reais (vários palpites falharam): `RH_ASSIDUIDADE_SINTESE_DIARIA` tem **`FUNCIONARIO_ID`**,
não `FUN_ID`; o parâmetro de assiduidade é **`RH_T_ASSIDUIDADE_PARAMETRO`** (com `RH_T_`);
`RH_T_TIPOS_DOCUMENTOS` (plural); `RH_T_DOMAINS` com colunas `DOMINIO`/`VALOR`/`REFERENCIA`;
`RH_T_DISPENSA` **não tem** `FUN_ID` (liga-se pelo `PEDIDO_ID`).

## Test / validation plan

**Fixtures reais (confirmados a 10/09):** colaborador **Nuno Teste Sync, id 958937, uuid
`01a085fa-fc04-7f08-a5d9-75b4b8a886b7`, tiprel 173442**. Responsável `RH_T_RESPONSAVEL.ID=23`.
Tipos de falta: **17** Motivo Pessoal e **18** Falta Injustificada (`SAL=1`), **20** Doença do
Trabalhador (`SAL=0`). Tipo de documento **26**. `RH_T_ANO`: id **2** = 2026 (**só existe 2026**).
Crédito mensal de dispensa (`RH_T_ASSIDUIDADE_PARAMETRO.T_DISPENSA`, estado A) = **4 horas**.
Valor da falta em dev = **6344,56/dia** (8h), proporcional às horas de ausência.

**`RH_T_FERIAS` está vazia** — ninguém tem direito de férias em dev. Para qualquer teste com
dedução em férias, criar primeiro:

```sql
INSERT INTO RH_T_FERIAS (ID, ANO_ID, FUN_ID, NUM_DIA, ESTADO, DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME, UUID)
VALUES ((SELECT NVL(MAX(ID),0)+1 FROM RH_T_FERIAS), 2, 958937, 10, 'A', SYSDATE, 1, 'teste', 'teste-direito');
```

Estado limpo em Setembro/2026 (confirmar antes de começar): pedidos **193**, **195**, **196**
todos `A`/`FINALIZADO`; validação **1110** (pedido 196) `A`; nada depois de Setembro. Atenção:
Setembro **já tem faltas**, logo qualquer pedido novo nesse mês dispara a contagem mensal e sai
`P` — usar um mês limpo quando se quiserem efeitos aplicados de imediato.

### Cenários já provados (regressão — repetir se se mexer no `FaltaDescontoService`)

| # | Setup | Esperado | |
|---|---|---|---|
| D1.2 | 4 dias, saldo férias 2, `FERIAS` | 2 primeiros gozados, 2 últimos com `DEF_REM` de 6344,56 cada | ✅ antes dava 400 |
| D1.3 | 2 dias, saldo 0, `FERIAS` | 0 gozados, 2 descontados | ✅ antes dava 400 |
| D1.4 | 2 dias, campo vazio | 2 descontados, férias intactas | ✅ |
| D1.5 | 1 dia de 8h, saldo dispensa 4h, `DISPENSA` | dispensa 240 min + `DEF_REM` de **3172,28** | ✅ |
| D1.6 | 1 dia de **4h**, saldo 4h, `DISPENSA` | dispensa 240 min, **sem** `DEF_REM` | ✅ |
| D1.7 | tipo **20** (`SAL=0`) + `FERIAS`, com saldo | dias gozados, **zero** desconto | ✅ |
| D1.8 | tipo 20, campo vazio | nada | ✅ |
| D2 | 2 dias num mês limpo, depois mais 2 no mesmo mês | 1.º `A`, 2.º **`P`** | ✅ |
| — | justificar 4 dias com saldo 3 (itens **sem** campo `data`) | 3 gozados + 1 descontado | ✅ |
| A1.1 | eliminar pedido com dedução em férias | faltas/gozadas/anexos/pedido a `E`, saldo reposto | ✅ |
| A1.2 | eliminar pedido com desconto salarial | `DEF_REMUNERACOES` a `E`, `TIPREL_REM_PAG` apagada, `DEF_REM_ID` a null | ✅ |
| A1.3 | eliminar pedido com dispensa | dispensa a `E`, minutos usados 240 → 0 | ✅ |
| A1.4 | editar trocando `FERIAS` → `DISPENSA` | gozadas a `E` e saldo devolvido, dispensa nova activa, **sem** duplicar | ✅ |
| A1.5 | editar enviando só 1 dos 2 dias | dia retirado a `E` com o desconto revertido; o outro reaplicado (`DEF_REM` **nova**); o dia retirado **reaparece** no painel | ✅ |
| A1.6 | editar/eliminar com **uma** falta processada em folha | **400** nos dois: *"1 falta(s) já foram processadas em folha"* | ✅ |

Para o A1.6, ligar o `DEF_REM_ID` de UMA falta a uma remuneração e reverter no fim:

```sql
-- :defRemId = RH_T_FALTA.DEF_REM_ID de uma das faltas do pedido
INSERT INTO RH_T_REMUNERACOES (ID, VALOR, DATA_REF, ESTADO, PRSAL_ID, REM_1_ID)
VALUES ((SELECT MAX(ID)+1 FROM RH_T_REMUNERACOES), 6344, DATE '2026-11-30', 'A',
        (SELECT PRSAL_ID FROM RH_T_REMUNERACOES WHERE ROWNUM=1), :defRemId);

DELETE FROM RH_T_REMUNERACOES WHERE REM_1_ID = :defRemId;
```

Diagnóstico de "porque é que este pedido está bloqueado" — a mesma query do guard, detalhada:

```sql
SELECT a.ID falta, TO_CHAR(a.DATA_INICIO,'YYYY-MM-DD') dia, a.DEF_REM_ID, c.ID remuneracao
  FROM RH_T_FALTA a
  JOIN RH_T_DEF_REMUNERACOES b ON b.ID = a.DEF_REM_ID
  JOIN RH_T_REMUNERACOES     c ON c.REM_1_ID = b.ID
 WHERE a.PEDIDO_ID = :pedidoId AND a.ESTADO <> 'E';
```

### Por provar

- **Editar num pedido ainda `P`** (não validado): o `reverter()` não tem nada para reverter e o
  `aplicar()` não corre. Confirmar que grava os campos e não cria efeitos.
- **Editar acrescentando um dia** que não estava no pedido: hoje o array só retira, não
  acrescenta — os itens novos são ignorados. Confirmar se é o pretendido.
- **Rejeição** (`validar:"NAO"`) depois do D1: faltas a `I`, nenhum efeito criado.

**Limpeza** (a ordem importa, `FK_DISPENSA_PEDIDO` bloqueia o pedido):
`RH_T_TIPREL_REM_PAG` (por `REM_ID`) → `RH_T_FALTA` → `RH_T_DEF_REMUNERACOES` →
`RH_T_FERIAS_GOZADAS` → `RH_T_DISPENSA` → `RH_T_DOCUMENTO` → `RH_T_VALIDACAO` → `RH_T_PEDIDO` →
`RH_ASSIDUIDADE_SINTESE_DIARIA` → `RH_T_FERIAS` → `RH_T_REMUNERACOES` (a do guard).

## Open questions

- **O guard deve cobrir faltas sem desconto salarial?** Hoje uma falta 100% coberta por férias
  ou dispensa não tem `DEF_REM_ID` e escapa ao guard, mesmo com a folha já corrida.
- **As 18 faltas com desconto a dobrar**: corrigir os dados (reverter os `DEF_REMUNERACOES`
  indevidos) ou deixar como histórico? Os cenários já estão todos provados, que era a condição.
- **D3**: quando arranca, e reserva-se mesmo (criar efeitos em `P`) ou fica só o lock?
- **C5b**: mudar `ETAPA='VALIDACAO'` da hora extra para `DESPACHO_RH` é breaking para o frontend
  (documentado em `frontend_changes_assiduidade.md:75`).
- **C4 / C6**: mexem nas vistas `RH_V_RESUMO_ASSIDUIDADE` e `RH_V_FALTA_MENSAL` — passa pelo DBA.
- **C9 / C10**: respeitar `TIPO_CONTAGEM_DIAS` e `NUM_DIAS_ABONOS` no fluxo de falta. O modelo
  de dados já os suporta e a licença já usa o conceito via `CALCULO_FALTA_LICENCA`.

## Next step

Actualizar `docs/frontend_changes_assiduidade.md` — é a única coisa que falta do trabalho já
feito, e o frontend precisa dela:

- `valorAusencia` passou de inteiro a **decimal**;
- `estado` pode agora vir **`P`** onde antes vinha `A` (contagem mensal dos 3 dias);
- o **400 de saldo insuficiente desapareceu** — o pedido passa sempre, com a parte não coberta
  a ir ao vencimento;
- dois **endpoints novos**: `PUT` e `DELETE .../assiduidade/falta/justificar/pedido/{pedidoUuid}`,
  com o 400 do guard de processado.

Depois disso, decidir entre o D3 e a correcção dos dados das 18 faltas.

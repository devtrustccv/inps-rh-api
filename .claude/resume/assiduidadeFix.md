> Updated: 2026-09-10 20:20 -01:00

## Goal

Alinhar o backend de **Assiduidade** com a spec
`docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md`, validando tudo live
contra a Oracle de dev. A auditoria da spec está feita (19 lacunas). Os lotes B, C, D1, D2, A1
e **D3** estão fechados e provados. O que falta é sobretudo **decisão de negócio** ou passa
pelo **DBA** — só o C9 e o C10 são código à espera de ordem.

## Current state

**16+ commits em `develop`, ainda SEM PUSH**, todos com `mvn clean compile` limpo e verificados
live contra a BD. Nada foi dado como feito sem prova em BD.

| Commit | O quê |
|---|---|
| `ed137ef0` | validar procurava a validação por `FUN_ID` mas grava-a por `REFERENCIA_UUID` |
| `e8b54afb` | lote B+C: anexos, validação de férias com fallback, `valorAusencia`→`BigDecimal`, `DESPACHO_RH`=SIM/NAO, `ETAPA='FINALIZADO'` ao rejeitar |
| `1565e06c` | painel "por justificar" só oferece dias que são ausência, filtro em SQL |
| `ae561324` | **regra de desconto** (D1) + **contagem mensal** (D2) — breaking |
| `b66c317f` | **editar e eliminar** do pedido (A1) + `reverter()` |
| `6d7f16a2` | **guard de processado** pela remuneração efectiva |
| `8c995326` | guard só conta remunerações activas (`c.ESTADO='A'`) |
| **`fb8c5369`** | **D3 parte 1 — reserva**: saldo desconta as faltas em `P` |
| **`5efc5a6d`** | **D3 parte 2 — lock** pessimista por colaborador |
| **`77ce36d6`** | **changelog de API** para o frontend (secções 9 a 11) |
| `9fa527ed`, `db75b544`, `5980efbc`, `adb1f7f4`, `8497ba1e` | handoffs e decisões |

Working tree limpo (só `bash.exe.stackdump`, lixo untracked). **BD de dev limpa**: colaborador
de teste sem pedidos, sem sínteses de teste, `RH_T_FERIAS` vazia, `RH_T_REMUNERACOES` de volta
às 5 linhas originais (127–131). Último `RH_T_PEDIDO` usado nos testes: 232. Último
`RH_ASSIDUIDADE_SINTESE_DIARIA` real: 884.

### Fechado na sessão de 10/09 (tarde)

**A1.7 — o filtro `c.ESTADO='A'` do guard**, que estava commitado sem prova. Provado nos dois
sentidos: com remuneração activa ligada ao `DEF_REM_ID`, `PUT` e `DELETE` dão **400**
(*"1 falta(s) já foram processadas em folha"*); com a mesma linha anulada (`ESTADO='I'`), os
dois dão **200**. Armadilha de **desenho de teste** (não é da aplicação): o editar refaz as
linhas de desconto, por isso depois de um editar a remuneração de teste fica agarrada a uma
definição morta e o `DELETE` a seguir passa por não haver *join* — não pelo filtro. **Não
encadear**: testar cada um no seu pedido, ou o eliminar primeiro (que não cria definições
novas). Encadear editar→eliminar é perfeitamente legítimo na aplicação; o que não serve é
provar o guard assim.

**D3 — reserva de saldo**, implementada **por leitura**, não por criação de linhas em `P`:

```
saldo = direito − gozadas(A) − dias de faltas em P com FLG_DESCONTO_FALTA='FERIAS'
```

O plano original (criar `FERIAS_GOZADAS`/`DISPENSA`/`DEF_REMUNERACOES` em `P` e activá-las no
despacho) foi **descartado**: obrigava a inverter o papel do `aplicar()`, a rejeição a desfazer
linhas e a repetir a bateria D1 inteira. A versão por leitura dá o mesmo resultado, não toca no
caminho de escrita, e a rejeição/eliminação funcionam sozinhas — a falta sai de `P` e deixa de
contar.

**Subtileza obrigatória:** o `aplicar()` passa `pedido.getId()` como `pedidoIdExcluir`. No
`validar`, o estado das faltas só é gravado no `saveAll` **depois** do ciclo, por isso os dias
seguintes do próprio pedido ainda estão em `P` quando o primeiro dia calcula a cobertura — sem
a exclusão o pedido reserva contra si próprio e desconta tudo ao vencimento.

**D3 — lock**, `SaldoLockService.lockColaborador(uuid)` → `PESSIMISTIC_WRITE` na linha do
colaborador (`FuncionarioEntityRepository.lockByUuid`). Tomado **à cabeça** de 8 casos de uso:
justificar / validar / editar / eliminar falta, marcar e validar falta, 3 escritas de dispensa,
marcar / validar / alterar férias. Um só lock, sempre o primeiro da transacção → não há duas
ordens de aquisição possíveis, logo não há deadlock.

### Contrato do editar mudou (10/09, tarde)

O `PUT falta/justificar/pedido/{uuid}` deixou de partilhar o `JustificarFaltaDTO` e passou a
ter **`EditarPedidoJustificacaoDTO`**, sem `itensFalta` — o editar mexe no **pedido**, não na
composição dele, e os dias descobrem-se pelo pedido como no Eliminar (spec `:658` só dá ao
Eliminar o efeito `ESTADO='E'`). Antes, um dia omitido do array era retirado com o desconto
revertido; como o `FaltaItemDTO` tem `selecionar`, um ecrã que enviasse só as linhas marcadas
apagava faltas em silêncio. Um `itensFalta` que ainda venha é aceite e ignorado.

**⚠️ O manifesto IGRP está dessincronizado do Java.** O `JustificarFaltaDTO.json` não conhece
`motivo`, `comJustificativo`, `pedidoId`, `estado`, `estadoDesc`, `etapa` nem
`tipoOrdemServico` (todos em uso) e ainda declara `despachoRh` (removido do Java). **Correr o
`igrp-spring-generator` sobre este módulo apaga campos em uso.** Por isso o DTO novo foi
escrito à mão nas convenções do gerador e o manifesto actualizado à mão
(`.igrpstudio/assiduidade/dto/EditarPedidoJustificacaoDTO.json` + a acção a apontar-lhe).
Sincronizar o `JustificarFaltaDTO.json` com o Java é uma limpeza por fazer.

## Por fazer

| # | Item | Natureza |
|---|---|---|
| **1** | **`git push`** dos 15 commits | risco de perda — só existem numa máquina |
| **2** | **18 faltas com desconto a dobrar** | decisão: corrigir dados ou deixar histórico |
| **3** | **C9** `TIPO_CONTAGEM_DIAS` | código + decisão (ver secção própria) |
| **4** | **C10** `NUM_DIAS_ABONOS` | código + decisão |
| **5** | Edição ir a validação (proposta do analista) | decisão de negócio |
| **6** | **C5b** hora extra grava `ETAPA='VALIDACAO'` | decisão (breaking) |
| **7** | **C4 / C6** | DBA — vistas `RH_V_RESUMO_ASSIDUIDADE` e `RH_V_FALTA_MENSAL` |

**Fora do nosso âmbito** (outro programador): regularização sem `RH_T_DEF_REMUNERACOES`
(TODO em `RegularizacaoService:137`), baixa médica sem `RH_T_ABONOS_BENEFICIOS_DET`, ausência
da baixa a apontar à tabela errada, continuidade de licença.

### C9 — `TIPO_CONTAGEM_DIAS` (levantamento feito, decisões por tomar)

O campo (`DIAS_UTEIS` / `DIAS_CORRIDO`, domínio confirmado em `RH_T_DOMAINS`) é gravado e lido
pelo CRUD de configuração (`SituacaoLaboralService:96,182`) e **nunca lido no fluxo de falta**.
Quem expande o período é `FaltaServiceWrite.expandirDias` (~`:295`), com `plusDays(1)` até ao
fim, sem olhar ao tipo — marcar falta de segunda a segunda conta sábado e domingo.

**Prioridade real: baixa.** Dos 25 tipos, **20 têm o campo a null**, 3 a `DIAS_CORRIDO`, 1 a
`DIAS_UTEIS`, 1 com lixo (`'0'`) — e **nenhum dos 5 preenchidos tem uma única falta associada**.
Hoje não desconta a mais a ninguém; é mina por armar para quando a parametrização de produção
entrar.

**Recomendação dada ao utilizador (por aprovar):**

- **Excluir só fim-de-semana, não feriados.** `RH_T_PARAM_FERIADO` (6 registos) data de duas
  maneiras — `DATA_ESPECIFICA` para móveis, `FIXO_ANO`+`DIA`+`MES` para fixos — e tem
  `GEOGR_ID`, logo "é feriado para este colaborador" exige resolver feriados municipais, regra
  que ninguém especificou. Além disso a **hora extra já ignora feriados de propósito**
  (`HoraExtraServiceWrite:249`); contá-los só na falta criaria duas definições de "dia útil".
- **Nulos = `DIAS_CORRIDO`** — único caminho que não altera silenciosamente 20 tipos.
- **O dia excluído não gera falta** (o pedido passa a ter menos dias; o `dividirPorDias` das
  horas ajusta-se sozinho).
- **Aplica-se só ao Marcar Falta.** No Justificar os dias vêm de sínteses escolhidas à mão,
  cada uma uma ausência real — se o utilizador selecciona um sábado, é porque houve ausência.

**Aviso de teste:** como nenhum tipo com contagem tem faltas, provar isto ao vivo obriga a
criar um tipo de teste ou alterar um existente em dev.

### C10 — `NUM_DIAS_ABONOS`

Existe em `ParamSituacaoEntity:101` e **nada o lê em todo o projecto**. Maternidade (90 dias) e
Paternidade (10) não têm tecto: nada impede registar 120 dias de maternidade. Decisão em falta:
se o pedido excede o direito, **bloqueia com 400 ou passa e avisa?**

## Decisões tomadas — não re-litigar

**Regra de desconto da falta** (fixada pelo utilizador a 10/09):

- O **saldo cobre o que consegue e o vencimento paga o resto** — duas fases da mesma cobrança,
  não alternativas. 4 dias com 2 de saldo → 2 gozados e 2 descontados.
- **Cobre-se pelos primeiros dias** (ordem cronológica).
- **Dispensa cobre parcialmente**, por contar em horas.
- **A dedução é escolha e responsabilidade do utilizador.**
- **Os 3 dias que mandam o pedido a despacho contam-se POR MÊS**, só faltas vivas (`A` e `P`),
  sem retroactividade.
- Consequência: o guard que rejeitava com 400 por saldo insuficiente **desapareceu**.

**Editar e Eliminar:** endpoints próprios por `pedidoUuid`, agem no pedido inteiro; eliminar é
soft-delete (`E`) e desfaz os efeitos; editar reverte e reaplica (não faz update); editar não
volta a validação; `RH_T_TIPREL_REM_PAG` não tem estado, a linha é apagada.

**Guard de processado:** basta **uma** falta processada para bloquear o pedido (400). Só
remunerações **activas** contam. Uma falta coberta a 100% por saldo não tem `DEF_REM_ID` e
escapa **de propósito** — a folha nunca foi tocada.

**D3 — reserva por leitura, não por criação de linhas em `P`.** Ver *Current state*. Decidido
depois de o utilizador propor a ideia de contar `A`+`P`; a versão barata foi preferida à cara.

**D3 — o lock é complementar, não alternativo.** A reserva fecha a janela de **dias** entre o
pedido e o despacho; o lock fecha a de **milissegundos** entre duas escritas.

**Saldo de dispensa expõe `horasReservadas` separado de `horasUsadas`** — somadas, o ecrã
mostrava "32:00 usadas" de um direito de 4h. O `validarSaldo` soma as duas.

**Da auditoria da spec:** C2 fechado sem alteração (o `DEF_PAG_ID` da spec `:857` não existe na
tabela — erro do analista); C3 resolvido (`DESPACHO_RH` é `VARCHAR2(3)` de propósito, guarda
SIM/NAO); anexos da justificação pertencem ao **pedido**; ciclo CORRIGIR em standby.

## Constraints

- PR contra `develop`, nunca `main`. Conventional commits.
- **Mostrar SEMPRE o payload completo antes de o executar** — mesmo com autorização já dada e
  mesmo num retry corrigido.
- **Pedir autorização antes de cada escrita** (POST/PUT/PATCH/DELETE e SQL de escrita). GET livres.
- **Mostrar o corpo cru da resposta** (HTTP status + JSON indentado), não resumir em tabela.
- **Filtros de leitura em SQL, não em memória.**
- Arrays nos PUT: completos e com `id` — sem id cria, omitido fica `E`, `null` preserva.
- `pedidoId`/`funcionarioId` nos paths são **UUID**; `itensFalta[].id` é o id da **síntese
  diária**, não o da falta.

## Blockers & risks

- Nenhum bloqueio técnico. O que falta espera decisão de negócio ou DBA.
- **15 commits sem push.**
- **18 faltas em BD com desconto a dobrar** (`FLG_DESCONTO_FALTA IS NOT NULL AND DEF_REM_ID IS
  NOT NULL`) — todas `DISPENSA`, todas activas. Se alguma já foi paga, corrigir mexe em folha
  passada.
- **A corrida nunca foi vista a falhar sem o lock.** O teste em paralelo deu o resultado certo
  *com* o lock; não se correu o mesmo teste com o lock removido, para não gravar dados
  inconsistentes de propósito. O que sustenta a prova é o `for update` no log e o comportamento
  serializado observado.
- **`RH_T_ANO` só tem 2026**: falta com dedução em férias noutro ano dá
  `404 "Ano de referência 2027 não encontrado"`. É dados, não código.
- `RH_PROCESSAMENTO_SALARIAL_DB` tem o package body inválido (ORA-04063): `CALCULO_FALTA_DIARIO`
  cai no fallback Java e funciona. Não confundir com bug nosso.
- `PARECER_DECISAO` está por povoar em dev (só `VALOR='TETS'`).

## Relevant files

- `.../assiduidade/application/services/FaltaDescontoService.java` — `aplicar()` (cobertura),
  `reverter()`, `requerValidacaoNoMes()`, `valorPorCobrir()`. Passa `pedido.getId()` como
  exclusão de reserva nos dois pontos de saldo
- `.../assiduidade/application/services/SaldoFeriaService.java` — `getSaldo(uuid, ano,
  pedidoIdExcluir)`; a reserva nunca faz o saldo passar a negativo, e um saldo já negativo por
  dados antigos fica como está
- `.../assiduidade/application/services/DispensaHorasService.java` — `getHorasStatus(..., dispensaIdExcluir,
  pedidoIdExcluir)`; reserva a ausência inteira do dia (conservador, documentado)
- `.../shared/domain/service/SaldoLockService.java` — **novo**, o lock
- `.../shared/infrastructure/persistence/repository/FaltaEntityRepository.java` —
  `countFaltasProcessadasEmFolha` (guard, nativa), `countFaltasVivasNoPeriodo`,
  `countDiasPendentesDeducaoFerias[NoAno]`, `findPendentesDeducaoDispensaNoPeriodo`
- `.../shared/infrastructure/persistence/repository/FuncionarioEntityRepository.java` — `lockByUuid`
- `.../assiduidade/application/services/JustificarFaltaWriteService.java` — os 4 casos de uso
- `docs/frontend_changes_assiduidade.md` — secções **9 a 11** cobrem tudo o que mudou a 10/09
- `docs/Especificação Tecnica Funcional - GESTÃO ASSIDUIDADE_09_09_2026.md` — `:493` regra dos 3
  dias, `:578-596` efeitos do validar, `:655-668` Editar/Eliminar, `:857` o erro do `DEF_PAG_ID`

## How to verify / resume

Armadilhas de ambiente: **JDK 23 obrigatório** e **porta 8087**, não a 8089 do CLAUDE.md.
`nohup ... &` pelo Bash **não** funciona — usar `Start-Process`. **Matar a app pelo porto**, não
pelo nome do processo: um `Stop-Process` filtrado pela command line deixou uma instância viva a
segurar o 8087 e o arranque seguinte falhou com *"Port 8087 was already in use"* — e a app que
respondia era a **versão antiga**, o que é fácil de não notar.

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
cd C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE
mvn -q clean compile -DskipTests      # EXIT=0

# matar o que estiver no porto antes de arrancar
Get-NetTCPConnection -LocalPort 8087 -State Listen -ErrorAction SilentlyContinue |
  ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }

Start-Process mvn.cmd -ArgumentList "spring-boot:run" -RedirectStandardOutput "$env:TEMP\rh-app.log" -WindowStyle Hidden
# para ver o lock: acrescentar '-Dspring-boot.run.arguments=--spring.jpa.show-sql=true'
# e depois: grep "for update" $env:TEMP\rh-app.log
```

As queries JPQL novas só são validadas **no arranque** — compilar não chega, é preciso
reiniciar para saber se estão certas.

HTTP — o `WebClient` evita o mojibake que o `Invoke-WebRequest` produz nos acentos:

```powershell
$wc=New-Object System.Net.WebClient; $wc.Encoding=[System.Text.Encoding]::UTF8
($wc.DownloadString("http://localhost:8087/api/v1/assiduidade/falta/justificar/01a085fa-fc04-7f08-a5d9-75b4b8a886b7?ano=2026&mes=9")) |
  ConvertFrom-Json | ConvertTo-Json -Depth 12
```

Ao imprimir o resultado, **capturar antes de imprimir o status** (`$r=$wc.UploadString(...)`;
só depois `"HTTP 200"; $r`) — imprimir "HTTP 200" antes da chamada mente quando ela falha.

Em PowerShell, **não** chamar a uma função `Del` (colide com o alias de `Remove-Item`).

SQL directo — **`DbExec` para escrita** (`DbUpdate` dá ORA-17273) e sem `FETCH FIRST`
(`WHERE ROWNUM<=n` numa subquery, senão ORA-00933):

```powershell
cd tools\db
$cp=".;C:/Users/ivanick.santos/.m2/repository/com/oracle/database/jdbc/ojdbc11/23.7.0.25.01/ojdbc11-23.7.0.25.01.jar"
& "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot\bin\java.exe" -cp $cp DbQuery "SELECT ..."
```

Nomes reais (vários palpites falharam): `RH_ASSIDUIDADE_SINTESE_DIARIA` tem **`FUNCIONARIO_ID`**,
não `FUN_ID`; o parâmetro de assiduidade é **`RH_T_ASSIDUIDADE_PARAMETRO`**;
`RH_T_TIPOS_DOCUMENTOS` (plural); `RH_T_DOMAINS` com colunas `DOMINIO`/`VALOR`/`REFERENCIA`;
`RH_T_DISPENSA` **não tem** `FUN_ID` (liga-se pelo `PEDIDO_ID`); `RH_T_PARAM_SITUACAO` **não tem**
`DESCRICAO`. O endpoint de saldo de dispensa usa `?data=`, não `?dataReferencia=`.

## Test / validation plan

**Fixtures reais (confirmados a 10/09):** colaborador **Nuno Teste Sync, id 958937, uuid
`01a085fa-fc04-7f08-a5d9-75b4b8a886b7`, tiprel 173442**. Responsável `RH_T_RESPONSAVEL.ID=23`.
Tipos de falta: **17** Motivo Pessoal, **18** Falta Injustificada (`SAL=1`), **20** Doença do
Trabalhador (`SAL=0`). Tipo de documento **26**. `RH_T_ANO`: id **2** = 2026 (**só existe 2026**).
Crédito mensal de dispensa = **4 horas**. Valor da falta em dev = **6344,56/dia** (8h).

**`RH_T_FERIAS` está vazia** — para qualquer teste com dedução em férias, criar o direito:

```sql
INSERT INTO RH_T_FERIAS (ID, ANO_ID, FUN_ID, NUM_DIA, ESTADO, DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME, UUID)
VALUES (1, 2, 958937, 2, 'A', SYSDATE, 1, 'teste', 'teste-direito');
```

**Criar ausências** (o painel de justificar só oferece dias que são ausência):

```sql
INSERT INTO RH_ASSIDUIDADE_SINTESE_DIARIA
  (ID, FUNCIONARIO_ID, DATA, MES, ANO, HORAS_TRABALHADAS, HORAS_AUSENCIA, FALTA,
   ESTADO, DATA_REGISTO, USER_REGISTO_ID, FLAG_RECECAO, USER_REGISTO_NAME, FORMA)
VALUES (900, 958937, DATE '2026-12-01', 12, 2026,
        INTERVAL '0 00:00:00' DAY TO SECOND, INTERVAL '0 08:00:00' DAY TO SECOND, 1,
        'A', SYSDATE, 1, '1', 'teste', 'MANUAL');
```

Setembro **já tem faltas** (pedidos 193/195/196) — usar Outubro, Novembro ou Dezembro para
cenários que precisem de mês limpo. Atenção: >3 dias no mês manda o pedido a `P`.

### Cenários provados (regressão — repetir se se mexer no `FaltaDescontoService`)

| # | Setup | Esperado | |
|---|---|---|---|
| D1.2 | 4 dias, saldo férias 2, `FERIAS` | 2 primeiros gozados, 2 últimos com `DEF_REM` de 6344,56 | ✅ **re-provado a 10/09 pós-reserva** |
| D1.3 | 2 dias, saldo 0, `FERIAS` | 0 gozados, 2 descontados | ✅ |
| D1.4 | 2 dias, campo vazio | 2 descontados, férias intactas | ✅ |
| D1.5 | 1 dia de 8h, saldo dispensa 4h, `DISPENSA` | dispensa 240 min + `DEF_REM` de 3172,28 | ✅ |
| D1.6 | 1 dia de **4h**, saldo 4h, `DISPENSA` | dispensa 240 min, **sem** `DEF_REM` | ✅ **re-provado a 10/09 pós-reserva** |
| D1.7 | tipo **20** (`SAL=0`) + `FERIAS`, com saldo | dias gozados, **zero** desconto | ✅ |
| D1.8 | tipo 20, campo vazio | nada | ✅ |
| D2 | 2 dias num mês limpo, depois mais 2 no mesmo mês | 1.º `A`, 2.º **`P`** | ✅ |
| A1.1–A1.4 | eliminar/editar com férias, desconto, dispensa | ver handoff anterior | ✅ |
| ~~A1.5~~ | ~~editar enviando só 1 dos 2 dias → dia retirado a `E`~~ | **anulado a 10/09** — o editar já não retira dias | ⛔ |
| **E1** | editar mandando só 1 de 2 dias (ou nenhum) | os **dois** dias ficam `A`, com `DEF_REM` novo e motivo actualizado | ✅ **10/09** |
| A1.6 | editar/eliminar com **uma** falta processada | **400** nos dois | ✅ |
| **A1.7** | guard com remuneração **anulada** | 400 com `A`, **200** com `I` | ✅ **10/09** |
| **T1** | pedido de 4 dias `P` com `FERIAS`, direito 2 | `feria/saldo` passa de **2 → 0** | ✅ **10/09** |
| **T4** | pedido de 4 dias `P` com `DISPENSA` | `horasReservadas 32:00`, restantes `00:00`, **nada** aplicado em BD | ✅ **10/09** |
| **T5** | rejeitar (`validar:"NAO"`) esse pedido | reserva devolvida **sozinha**, sem código de compensação | ✅ **10/09** |
| **Lock** | 2 POST em paralelo, 1 dia cada, direito **1** | um leva o dia gozado, o outro `DEF_REM` 6344,56; direito 1 / gozados 1 | ✅ **10/09** |

Para o A1.6/A1.7, ligar o `DEF_REM_ID` de UMA falta a uma remuneração e reverter no fim:

```sql
INSERT INTO RH_T_REMUNERACOES (ID, VALOR, DATA_REF, ESTADO, PRSAL_ID, REM_1_ID)
VALUES ((SELECT MAX(ID)+1 FROM RH_T_REMUNERACOES), 6344, DATE '2026-11-30', 'A',
        (SELECT PRSAL_ID FROM RH_T_REMUNERACOES WHERE ROWNUM=1), :defRemId);

UPDATE RH_T_REMUNERACOES SET ESTADO='I' WHERE REM_1_ID = :defRemId;   -- anular
DELETE FROM RH_T_REMUNERACOES WHERE REM_1_ID = :defRemId;             -- limpar
```

### Por provar

- **Editar num pedido ainda `P`**: o `reverter()` não tem nada para reverter e o `aplicar()` não
  corre. Confirmar que grava os campos e não cria efeitos.
- **C9 / C10**, quando forem implementados.

**Limpeza** (a ordem importa, `FK_DISPENSA_PEDIDO` bloqueia o pedido):
`RH_T_REMUNERACOES` (a do guard, por causa da FK para o def) → `RH_T_TIPREL_REM_PAG` (por
`REM_ID`) → `RH_T_FALTA` → `RH_T_DEF_REMUNERACOES` → `RH_T_FERIAS_GOZADAS` → `RH_T_DISPENSA` →
`RH_T_DOCUMENTO` → `RH_T_VALIDACAO` → `RH_T_PEDIDO` → `RH_ASSIDUIDADE_SINTESE_DIARIA` →
`RH_T_FERIAS`.

## Open questions

1. **As 18 faltas com desconto a dobrar**: corrigir os dados ou deixar como histórico? (Se
   alguma já foi paga, corrigir mexe em folha passada.)
2. **C9**: aprovar a recomendação (fim-de-semana sim, feriados não; nulos = corridos; só no
   Marcar Falta)?
3. **C10**: exceder `NUM_DIAS_ABONOS` bloqueia com 400 ou passa e avisa?
4. **Edição ir a validação** (proposta do analista): vale a condição cumulativa dos 3 dias, ou
   só a do desconto salarial? Com a reserva já feita, uma edição em `P` passa a reservar em vez
   de antecipar — o custo desceu.
5. **C5b**: mudar `ETAPA='VALIDACAO'` da hora extra é breaking para o frontend
   (`frontend_changes_assiduidade.md:75`).
6. **C4 / C6**: passam pelo DBA.

## Next step

1. **`git push`** dos 15 commits — é o único item com risco de perda pura.
2. Levar as questões **1 e 4** ao negócio numa conversa só (dependem da mesma pessoa).
3. Com aprovação, **C9** e depois **C10** — os únicos que são código à espera de ordem.

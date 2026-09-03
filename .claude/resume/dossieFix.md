> Updated: 2026-09-03 (sessão 4)

## Goal

Fazer a **Lista de Funcionários** distinguir "Estado do Registo" de "Estado do Colaborador",
conforme a spec DOSSIÊ 02/09 — e garantir que uma alteração de **situação laboral por validar**
aparece como **Pendente** na grelha sem inativar quem continua a trabalhar.

**Estado: resolvido e validado live.** A open question das sessões anteriores está fechada.

## Current state

`develop` com os commits desta sessão (compilam, `mvn -o compile` exit 0, JDK23):

- **`379f4e66`** `fix(funcionario): estado do registo na lista vem de ESTADO_VALIDACAO` — entidade da view + read service.
- **`854ba650`** `fix(funcionario): situacao laboral pendente marca ESTADO_VALIDACAO=P` — ciclo de vida no write service.
- **`ee65e9a6`** `docs: specs DOSSIE (02/09) e PROCESSAMENTO SALARIAL (01/09)`.
- **`78e879f1`** `fix(funcionario): aprovar reativacao volta a por o colaborador ativo` — `aplicarEfeitosReativacao`.
- **`76402bb4`** `feat(funcionario): grelha mostra os dois estados Pendentes na validacao` — derivação na leitura + filtro alinhado.

### Ponto em aberto para o testeR/analista (decisão do utilizador: deixar como está)

`aplicarEfeitosReativacao` **reabre o contrato** (`contrato.setDataFim(null)`), simétrico ao
`aplicarEfeitosCessacao` que fecha as 6 coisas (tiprel, mobilidade, carreira, **contrato**, def de
remuneração e de pagamento). Cheguei a remover o contrato e o utilizador mandou reverter — fica simétrico
e o analista avalia. O trade-off, para essa conversa:

| | Estado resultante | `existeContratoEmVigor` | Renovação | Visibilidade |
|---|---|---|---|---|
| **Reabrir (atual)** | ativo, contrato **sem prazo** | `true` → bloqueia "Novo Contrato" | nunca alertado (`BETWEEN` não casa com null) | **silencioso** |
| Não tocar | ativo, contrato **expirado** | `false` → "Novo Contrato" aparece | nunca alertado (data no passado) | visível/acionável |

Nota: a renovação fica quebrada nas **duas** opções — não serve para desempatar. A raiz é que
`aplicarEfeitosCessacao` **sobrescreve** `contrato.DATA_FIM` com a data de cessação, destruindo o termo
original (ex.: 2028-01-01 do contrato 731). Nenhuma reativação o recupera. Terceira via possível, não
implementada: recalcular `data_fim = data_inicio + DURACAO` (a coluna `DURACAO` parece sobreviver).

**App UP** na 8089 (PID 22256, código destes commits). Reboot: `scratchpad/run_develop_8089.sh`.

### Alteração de BD JÁ APLICADA (⚠️ não versionada)

`RH_V_DOSSIE` foi recriada com `CREATE OR REPLACE VIEW` para expor **`A.ESTADO_VALIDACAO AS ESTADO_VALIDACAO`**
(a coluna já existia em `RH_T_FUNCIONARIOS`; a view é que não a selecionava). View ficou **VALID**, 15 linhas
(inalterado), e o literal `'Não'` do `DECODE` foi guardado como `UNISTR('N\00e3o')` — confirmado por
`ASCIISTR` = `N\00E3o`, `LENGTH`=3.

**O utilizador decidiu explicitamente NÃO versionar isto** (recusou o ficheiro de migration `V4__...`).
Logo a alteração **só existe na BD de dev** — quem fizer deploy noutro ambiente tem de a reaplicar à mão,
senão o Hibernate rebenta ao mapear `ESTADO_VALIDACAO`. O DDL completo está em
`scratchpad/rh_v_dossie_add_validacao.sql` (scratchpad é efémero — se for preciso, re-extrair com
`SELECT text FROM user_views WHERE view_name='RH_V_DOSSIE'`).

## Decisions made — do not re-litigate

- **A open question está respondida pela spec DOSSIÊ 02/09** (l.1826-1836): são duas colunas com duas fontes —
  `Estado do Registo` = `ESTADO_VALIDACAO`, `Estado do Colaborador` = `ESTADO_COLABORADOR`. O código colava
  as duas ao mesmo valor.
- **Os dois estados aparecem Pendentes na grelha — mas por DERIVAÇÃO na leitura, não por escrita.**
  O analista quer que ao enviar para validação tanto o "Estado do Registo" como o "Estado do Colaborador"
  mostrem Pendente. Resolvido em `FuncionarioReadService.estadoColaboradorExibido()` (commit `76402bb4`):
  se `ESTADO_VALIDACAO='P'`, a grelha mostra `P`; caso contrário mostra o `ESTADO` real. O filtro por
  estado acompanha a regra (`estado=P` também apanha `estadoValidacao='P'`; `estado=X` exclui esses),
  senão filtrar "Ativo" devolvia linhas a dizer "Pendente".
  **Feito em Java e não na view** — a `RH_V_DOSSIE` não está versionada, e no serviço a regra é visível.
- **`funcionario.estado` (a coluna gravada) NÃO vai a `P`.** É o mesmo requisito do ponto acima, resolvido
  sem tocar na BD. Pôr `estado='P'` gravado:
  1. destrói o estado de domínio (A/I) — no ramo não-processado a situação é editada *in place*, o valor
     anterior fica irrecuperável e **a rejeição não teria a que voltar**;
  2. abre os guards de `guardComboInativarAtivar` (testam `== A` / `== I`; com `P` ambos falham);
  3. esconderia da grelha por defeito (`estado='A'`, spec l.1909-1910) um colaborador que continua ativo;
  4. contraria `AlterarEstadoContratoService` ("SEM tocar em funcionario.estado").
  O estado de repouso do `estado_validacao` é **sempre `'A'`** → o rollback é um set incondicional.
- **Ciclo de vida do `estado_validacao`**: submissão (`registarNaoProcessado` / `registarProcessado` /
  `reenviarCorrecao`) → `P`; `devolverParaCorrecao` → `C`; `validar` **aprovado OU rejeitado** → `A`
  (rejeitar a *alteração* não invalida o *registo*, por isso nunca `I`).
- **O registo inicial continua em lockstep** (`estado` e `estado_validacao` ambos `P`) — isso está certo e
  não se mexeu: um colaborador por validar não tem estado de domínio válido.

## Constraints

- Compilar com `JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"`.
- SQL: correr de `tools/db`, ojdbc11 23.7.0.25.01. **`DbQuery`** = SELECT; **`DbExec`** = write (SQL em `args[0]`);
  **`DbExecFile`** = write a partir de ficheiro UTF-8 (**criado nesta sessão**, para DDL multilinha sem mangling
  de shell/encoding). `tools/db` parece estar gitignored — o `DbExecFile.java` não aparece no `git status`.
- Oracle XE sem `FETCH FIRST` → `ROWNUM`; `TO_CHAR(data,'YYYY-MM-DD')` (datas cruas dão erro no helper).
- Commits: `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`. PR contra `develop`.
- Fluxo de teste: GET antes, **pedir autorização por cada escrita**, resposta crua (JSON+HTTP).

## Blockers & risks

- **A tool Bash é POSIX, não PowerShell.** O here-string `@'...'@` entra **literal** e suja a mensagem de
  commit (aconteceu; corrigido com `--amend -F -`). Usar heredoc `<<'EOF'` com `-F -`.
- `boot.log` é partilhado por vários `mvn spring-boot:run` — um `BUILD FAILURE` no log pode ser do processo
  **anterior** que se matou, não do atual. Confirmar pelo PID em `netstat` + `StartTime` do processo.
- curl `-o` + `-w`: usar `curl ... -w "\n__HTTP__:%{http_code}" > ficheiro` (sem `-o`) e `rpartition('__HTTP__:')`.
- Nomes: situação = `RH_T_PARAM_SITUACAO` (`FLG_ESTADO_CONTRATO`: A/C/S); motivos = `RH_T_PARAM_SITUACAO_DET`
  (col **`MOTIVO`**). `RH_T_FUNCIONARIOS` **não** tem `estado_colaborador` — tem `estado` e `estado_validacao`.

## ✅ ACHADO CRÍTICO — "Ativar" não reativava — **CORRIGIDO** (`78e879f1`)

> **Estado: resolvido, validado live e com regressão confirmada.** Mantém-se aqui a descrição porque
> explica *porquê* a correção existe e serve de caso de regressão.

**Correção:** novo `aplicarEfeitosReativacao(funcionario, tiprelAtual)`, chamado em `validar()` quando a
validação é aprovada **e** `ativaContrato(param)` — põe `estado=A` e reabre a cadeia de datas
(tiprel, mobilidade, carreira, contrato, def de remuneração/pagamento). Como na cessação, **não** mexe em
`est_act_adm`. Fica dentro do `if (estado == Estado.A)`, por isso **não dispara na rejeição** (que corre
com `estado=I`) — verificado.

**Limitação documentada no método:** `aplicarEfeitosCessacao` sobrescreve `contrato.DATA_FIM` com a data
de cessação, pelo que o termo original de um contrato a prazo já se perdeu nesse momento e não é
recuperável na reativação. Reabrir (null) é o menos errado. É mais um sintoma da edição in-place (achado nº1).

### Descrição original do bug

**Aprovar uma reativação não alterava o estado do colaborador.** Provado live no 958930 (cenário 12):
submeteu-se `Ativar` (situação flag `A`), aprovou-se com `validar=SIM` → HTTP 200, situação e validação
ficaram `A`… e **`funcionario.estado` continuou `I`**, com `tiprel.data_fim` ainda fechado (2026-09-03).

Causa — `funcionario.setEstado(...)` só existe em 3 sítios:

| Linha | Onde | Efeito |
|---|---|---|
| 289 | `registarProcessado` (submissão, **só ramo processado**) | `estadoDoFuncionarioPara` → trata `A` e `I` |
| 306 | `aplicarEfeitosCessacao` (aprovação, **só cessação**) | põe `I` |
| 344 | `rollbackRejeicao` (rejeição, só se `fechadoPeloRegisto`) | põe `I` ou `A` |

No ramo **não-processado** (`ult_proc == null`) nada põe `A`: existe `aplicarEfeitosCessacao` mas **não existe
o simétrico de reativação**. Falta um `aplicarEfeitosReativacao` que ponha `estado=A` e reabra a cadeia de
datas (`tiprel`/mobilidade/carreira/contrato com `data_fim=null`).

**Impacto: metade da funcionalidade "Ativar/Inativar Colaborador" não funciona**, e o ramo não-processado é
o caminho comum (todos os colaboradores de teste têm `ult_proc=null`). NÃO é regressão da sessão 4 — os três
`setEstado` são pré-existentes; a sessão 4 só acrescentou linhas de `setEstadoValidacao`.

**Reprodução limpa** (colaborador **958931** `Colaborador Teste Reativar`,
uuid `01a06456-f966-7647-9bc9-259c4978e175`, criado de raiz só para isto — sem rejeições nem correções,
para excluir contaminação de estado):

| Passo | `f_est` | `f_val` | `tiprel.data_fim` | `sit` |
|---|---|---|---|---|
| 0. inicial | A | A | null | 1 |
| 1. inativar (sit 9) | A | **P** | null | 9 P |
| 2. aprovar | **I** | A | 2026-09-10 | 9 A |
| 3. ativar (sit 22) | I | **P** | 2026-09-10 | 22 P |
| 4. **aprovar** | **I** ❌ | A | **2026-09-10** ❌ | 22 A |

O `estado_validacao` comportou-se corretamente nos 4 passos; o que falhava era o `estado` do colaborador.

### Regressão confirmada DEPOIS da correção (mesmo colaborador 958931)

| Teste | Resultado |
|---|---|
| ativar + aprovar | `estado=A`, tiprel/mob/carreira/contrato `data_fim=null` ✅ |
| guard "já está ativo" | 400 ✅ |
| inativar (submeter) → aprovar | `A/P` → `I/A` com datas fechadas em 2026-09-25 ✅ |
| guard "já está inativo" | 400 ✅ |
| guard data fim obrigatória em ausência (sit 24 `tipo_ausencia=LICENCA`) | 400 ✅ |
| ativar (submeter) → **rejeitar** | fica `I`, `est_validacao`→`A` ✅ (o fix não dispara na rejeição) |
| ciclo `estado_validacao` (P/C/A) | intacto ✅ |

Os dois colaboradores que tinham ficado presos (**958930** e **958931**) foram recuperados com a correção
— ambos estão agora `A`/`A`.

## ⚠️ A edição in-place é REGRA DE NEGÓCIO — não a "corrijas" sem falar com o analista

Investigado no fim da sessão 4, depois de eu ter recomendado (erradamente) "deixar de editar in place e
criar linha nova". **Isso contrariaria uma regra escrita.** Origem: `docs/Caso de uso_teste_gravação.md`
(~l.588-602), tabela `RH_T_SITUACAO_LABORAL`, implementada pelo commit **`7518be97`** (13/07/2026), cuja
mensagem diz explicitamente *"Caso de teste (Gestao Laboral / Situacao Laboral)"*:

> **Regra de Registo e Atualização da Situação Laboral**
> - Criar registo **somente** quando houver alteração da Situação Laboral ou do Motivo, **desde que o
>   registo atual já tenha sido processado**.
> - Se houver alteração e o registo **ainda não tenha sido processado** → apenas **UPDATE do registo
>   existente**, não criar novo.
> - Se não houver alteração → nem novo registo nem atualização.

Isto explica os três ramos do dispatcher **e** confirma que o no-op do cenário 12b
("Situação laboral sem alterações.") é **comportamento intencional**, não um acidente.

Intenção de negócio plausível: uma situação nunca processada em folha ainda é quase um rascunho —
corrigir um motivo mal escolhido não deve deixar duas linhas no histórico.

**O verdadeiro buraco é mais estreito:** a regra fala do *maker a corrigir o seu próprio registo* e é
**silenciosa sobre a rejeição pelo checker**. Foi aí que se perdeu o APOSENTADO do 958930 — não por a
regra estar errada, mas por não cobrir esse caso.

**Pergunta para o analista** (não implementar antes da resposta):

> Quando uma alteração de situação laboral é **rejeitada** e o registo foi atualizado in-place (regra dos
> não-processados), a situação deve voltar aos valores anteriores? Se sim, esses valores têm de ser
> guardados em algum lado, porque o UPDATE apaga-os.

Terceira via que **respeita** a regra: guardar os valores anteriores no registo de **validação** (que já é
criado por cada alteração) e usá-los só na rejeição. Não cria linha em `RH_T_SITUACAO_LABORAL`, logo não
viola nada.


## Achados por resolver (NÃO são regressões desta sessão)

1. **Rejeição no ramo não-processado é destrutiva.** `validar()` faz `tiprelAtual.setEstado(I)` e
   `situacao.setEstado(I)`, e `rollbackRejeicao` faz `return` logo à entrada nesse ramo
   (`if (!fechadoPeloRegisto) return;`). Resultado observado no 958925: **tiprel corrente e situação ficaram
   `I` com `est_act_adm=1`**, apesar de o colaborador estar ativo — e a situação/motivo originais, sobrescritos
   *in place*, **perderam-se**. Bug pré-existente, agora com evidência reproduzível.
2. **O JaVers não serve de rede de segurança aqui.** O snapshot `INITIAL` (v1) da situação 684 **já é a versão
   alterada** — o audit só começou a segui-la quando foi tocada. Não há registo do estado anterior.
3. **`FLG_ESTADO_CONTRATO='S'` (Suspenso) está a meio caminho — existe no domínio e na BD, sem UI nem
   comportamento.** O ecrã "Ativar/Inativar Colaborador" tem um combo **Estado** (= `FLG_ESTADO_CONTRATO`)
   que filtra o combo **Situação Laboral** via `?flgEstadoContrato=` no endpoint de parametrização. Se o
   combo Estado só oferecer *Ativo* e *Cessado*, as situações com flag `S` ficam **inalcançáveis**
   (ex.: id 15 "Falecimento de Familiares"). E mesmo que fossem escolhidas, `cessaContrato()` e
   `ativaContrato()` devolvem ambos `false` para `S` → `funcionario.estado` não mudaria.
   **O utilizador levantou isto** ("a documentação não diz para filtrar só A e C") e ficou por decidir.
4. **`AlterarSituacaoLaboralRequest.estadoContrato` não é lido pelo backend — e está decidido que fica
   assim.** O ecrã envia-o (payload real: `{"estadoContrato":"C","situacaoLaboralId":9,...}` — o **código**,
   não o label), mas não há um único `getEstadoContrato()` no módulo `funcionario`: o estado deriva sempre
   da flag da situação escolhida.
   **Decisão do utilizador (sessão 4): NÃO acrescentar guard de coerência.** Chegou a ser implementado e
   foi revertido antes de commit. Razão, que é boa: o combo "Situação Laboral" é **populado a filtrar por**
   o "Estado" escolhido, logo uma divergência é estruturalmente impossível a partir da UI — o guard só
   apanharia chamadas feitas à mão. Não reabrir sem motivo novo.
5. **`estadoRegistoDesc` usa labels do colaborador.** Para "Estado do Registo", `A` lê-se **"Ativo"** quando
   devia provavelmente ler-se **"Validado"**. Cosmético, por confirmar com negócio.
6. **`registarProcessado` aplica o efeito antes da aprovação**: L280 faz
   `funcionario.setEstado(estadoDoFuncionarioPara(param, funcionario))` no **registo**, não na validação.
   Fora do âmbito desta sessão, mas cheira a bug.

## Dado de teste — REPOSTO (já resolvido)

**958925** (`Wilson Cabral Tavares`, uuid `01a061d6-f65c-74cd-a5e3-3c45f3de5734`) foi danificado pelo
achado nº1 durante o teste e **já foi reposto e verificado**. Todos os 5 colaboradores de teste
(958925-958929) foram criados hoje (02/09) por `anonymousUser` — são dados de dev, não produção.

Os valores originais da situação 684 tinham-se perdido (UPDATE in place, e o JaVers não tem snapshot
anterior). Foram **reconstruídos por comparação** com a situação 687 (958928, por tocar), que revelou o
padrão de uma situação `INICIO` acabada de criar: `situacao_laboral_id=1`, `motivo=null`, `obs='NOVO_CONTRATO'`,
`data_fim=null`, e **`data_inicio` = `data_inicio` do respetivo contrato** (687→734 ambos 2026-08-25).
Contrato do 958925 é o **731** (2026-01-01 → 2028-01-01), daí `data_inicio=2026-01-01`.

```sql
UPDATE rh_t_situacao_laboral SET estado='A', situacao_laboral_id=1, motivo_sit_lab_id=NULL,
       data_inicio=DATE '2026-01-01', data_fim=NULL, obs='NOVO_CONTRATO' WHERE id=684;
UPDATE rh_t_tipos_relacionamento SET estado='A' WHERE id=173407;
```

Nota: a `data_inicio` é a única peça **inferida** (bem fundamentada, mas inferida). O `tiprel 173407` é uma
**PROGRESSAO/CARREIRA** com `data_inicio=2028-11-01` — o ramo não-processado não lhe tocou nas datas, só no
`estado`. A validação **1063** ficou `I` na BD como histórico do teste (não foi apagada).

## Matriz de cenários validada (sessão 4)

Colaborador **958930** (`Colaborador Teste Cenarios`, uuid `01a06448-061d-744d-9c3a-ab005acc134b`),
criado de raiz: `POST` → `GET by id` → `PUT validar:SIM` → `A`/`A`, `ult_proc=null` (ramo não-processado).
Payloads em `scratchpad/c*.json`, com datas em `DD/MM/YYYY` como o frontend real.

| # | Cenário | Resultado | ✔ |
|---|---|---|---|
| 1 | `validar=SIM` sem pendente | 400 "não possui validação pendente" | ✅ |
| 3 | Ativar quem já está ativo (flag A) | 400 "já está ativo" | ✅ |
| 4 | Inativar (submissão, sit 9/motivo 23) | 200; `estado=A`, `est_validacao=P`, sit `P`, validação `P` | ✅ |
| 5 | Grelha com pendente | `estadoColaborador=A/Ativo` + `estadoRegisto=P/Pendente` | ✅ |
| 6 | `validar=CORRIGIR` | sit/tiprel/validação/`est_validacao` → `C`; `estado` fica `A` | ✅ |
| 7 | Reenviar correção | tudo volta a `P`; `data_inicio` aceitou `03/09/2026` (DD/MM/YYYY) | ✅ |
| 8 | `validar=SIM` (aprovar cessação) | `estado`→`I`, `est_validacao`→`A`; **cadeia toda fechada** (tiprel+mobilidade+carreira+contrato = 2026-09-03); ordem de serviço `CESSACAO` criada | ✅ |
| 9 | Inativar quem já está inativo | 400 "já está inativo" | ✅ |
| 10 | Ativar (submissão) | 200; `estado` fica `I`, `est_validacao=P` | ✅ |
| 11 | `validar=NAO` (rejeitar) | `est_validacao`→`A`, `estado` fica `I` | ✅ (mas ver achado nº1 abaixo) |
| 2 | No-op (mesma situação+motivo) | "Situação laboral sem alterações." | ✅ |
| 12a | `motivoId=null` | 400 "motivo é obrigatório" | ✅ |
| 12 | Ativar + aprovar | **FALHA — ver ACHADO CRÍTICO** | ❌ |

**Nota sobre o cenário 2 (no-op):** só é alcançável por acidente. Os guards `guardComboInativarAtivar`
correm **antes** do `mudouSituacaoOuMotivo`, e disparam para qualquer situação flag `A` (colaborador ativo)
ou flag `C` (colaborador inativo). Chegou-se lá porque a rejeição do cenário 11 deixou a situação corrente
em `1/2`, e reenviar `1/2` bateu no no-op — o que **bloqueia o caminho natural de reativação** ("ATIVO")
até se usar outra situação. Há outras situações flag `A` com motivo (18, 22, 23, 24, 25, 26, 32, 39), por
isso não é um beco definitivo, mas é uma armadilha real.

**Dano observado no cenário 11 (achado nº1 em ação):** a rejeição da reativação deixou `tiprel=I` e
`situacao=I`, e a situação corrente ficou `sit=1 (ATIVO)` — **a situação APOSENTADO (9), que era a realidade
aprovada, foi sobrescrita in place e perdeu-se**. O colaborador ficou inativo sem registo do motivo.
Consequência mais grave do que o caso do 958925.

## Evidência da validação live

Ramo exercitado: **`registarNaoProcessado`** (todos os colaboradores têm `ult_proc=null`).

| | `funcionario.estado` | `estado_validacao` | Lista |
|---|---|---|---|
| antes | A | A | `A/Ativo` + `A/Ativo` |
| após PATCH inativar (sit 9 / motivo 23) | **A** | **P** | `estadoColaborador=A/Ativo` + `estadoRegisto=P/Pendente` ✅ |
| após PATCH `validar=NAO` | **A** | **A** | volta a `A/A` ✅ |

Prova do desacoplamento na leitura — **958927** (`estado=I`, `estado_validacao=A`):
antes mostrava `estadoRegisto: I/Inactivo` (errado); agora `A/Ativo`.

## Estado dos colaboradores de teste

Estado verificado após a reposição (tudo consistente):

| fun | estado | est_validacao | sit | sit_estado | situação | tiprel |
|---|---|---|---|---|---|---|
| 958925 | A | A | 684 | A | 1 | A ← reposto |
| 958926 | A | A | 685 | A | 7 | A |
| 958927 | I | A | 686 | A | 9 | A |
| 958928 | P | P | 687 | P | 1 | P (registo pendente) |
| 958929 | A | A | 688 | P | 9 | A (pendente da sessão 3) |
| 958930 | A | A | 689 | A | 23 | A ← matriz de cenários; APOSENTADO perdido no cenário 11 (achado nº1) |
| 958931 | A | A | — | I | 9 | A ← repro do achado crítico, regressão do fix e teste do display |

Nota 958931: a situação corrente ficou `I` (situação 9) por ter sido usada na última rejeição — é o
achado nº1 outra vez. O `funcionario` está correto (`A`/`A`).

Ambos tinham ficado presos em `I` com uma reativação aprovada (o ACHADO CRÍTICO) e **foram recuperados
pela correção `78e879f1`** — servem agora de caso de regressão para o "Ativar".

## Next step

O `estado_validacao` e o "Ativar" estão ambos fechados, validados em todos os cenários e com regressão
confirmada. O que fica em aberto:

1. **Achado nº1 — a rejeição de um registo editado in-place.** É o mais grave por resolver, mas **não é um
   refactor**: ver a secção "A edição in-place é regra de negócio" abaixo. O que falta é uma **decisão do
   analista**, porque a regra escrita é silenciosa sobre a rejeição. **Prioridade 1.**
2. Levar os achados **3** (`S` sem UI nem comportamento) e **5** (label `estadoRegistoDesc`) a negócio.
3. Rever o achado **6** (`registarProcessado` aplica o efeito no registo, antes da aprovação) — não foi
   exercitado nesta sessão porque todos os colaboradores de teste têm `ult_proc=null`. **O ramo processado
   continua por testar** e é onde vive esse achado.
4. Se o `RH_V_DOSSIE` tiver de existir noutro ambiente, **reaplicar o DDL à mão** (ver "Current state").

## How to verify / resume

- App UP: `curl -s -o /dev/null -w "%{http_code}" http://localhost:8089/swagger-ui.html` = **302**.
  Se caída: `nohup bash scratchpad/run_develop_8089.sh > scratchpad/boot.log 2>&1 &` (~30-60s).
- Compilar: `export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot" && mvn -o compile`.
- SQL: `cd tools/db && OJ='C:\Users\ivanick.santos\.m2\repository\com\oracle\database\jdbc\ojdbc11\23.7.0.25.01\ojdbc11-23.7.0.25.01.jar' && java -cp ".;$OJ" DbQuery "<SQL>"`.
- Estado: `DbQuery "SELECT id, estado, estado_validacao FROM rh_t_funcionarios ORDER BY id"`.
- Lista: `curl -s "http://localhost:8089/api/v1/funcionarios?pageNumber=0&pageSize=20"` → comparar
  `estadoColaborador` vs `estadoRegisto`.
- Rota do write: `PATCH /api/v1/funcionarios/{uuid}/situacao-laboral`
  (`situacaoLaboralId`, `motivoId`, `dataInicio`, `dataFim`, `observacao`, `validar`).
- Registo: `POST /api/v1/funcionarios` → `GET /api/v1/funcionarios/{uuid}` → `PUT /api/v1/funcionarios/{uuid}`
  com `validar:SIM`. Payloads-modelo em `scratchpad/reg_m1.json`, `get_m1.json`, `validar_m1.json`
  (o `scratchpad/` do **projeto**, que persiste entre sessões — não o temporário).
- Helper de chamadas: `python scratchpad/call.py <METODO> <caminho> [body.json] [--save out.json]`
  — imprime sempre HTTP status + corpo identado.

### Formato de datas (importante)

O frontend envia **`DD/MM/YYYY`** (`"dataInicio":"31/08/2026"`), não ISO. O `DateFormatter.stringToLocalDate`
aceita ambos (ISO foi usado nos testes das sessões 3 e 4 sem erro), mas ao reproduzir o comportamento real
usar `DD/MM/YYYY`. Payload real capturado do ecrã:

```json
{"validar":null,"estadoContrato":"C","situacaoLaboralId":9,"motivoId":23,
 "observacao":"...","dataInicio":"31/08/2026","dataFim":"01/09/2026","tipoOrdemServico":""}
```

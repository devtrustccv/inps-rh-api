> Updated: 2026-09-16 13:10

## Goal

Fechar o módulo **Missão de Serviço** (spec 14/09/2026, 4 processos independentes). O fluxo está
implementado, testado ponta a ponta e no `origin/develop`. Falta o que depende de terceiros: tabela
de preços da ajuda de custo, integração SGAL, textos definitivos dos templates e anexos no email.

## Current state

`develop` = `origin/develop` = **`4117f38f`**, sem alterações por gravar. `mvn test`: **17 testes, 0
falhas**. Feito nesta sessão (8 commits, de `9eba4822` a `4117f38f`):

- **Endpoints antigos removidos** (10 rotas + commands/queries/18 DTOs/manifestos). Respondem 404.
- **Nenhuma gravação devolve `Map`**: `MissaoSubmissaoGravadaResponseDTO`,
  `ProcessoEtapaGravadaResponseDTO`, `AvaliacaoPrestadorGravadaResponseDTO` e `SuccessResponseDTO`.
- **Notificações novas ao colaborador**, por email: `MISSAO_CONFIRMACAO_PEDIDO` (submissão e
  colaborador acrescentado), `MISSAO_AJUDA_CUSTO` (1.º registo do pagamento), `MISSAO_ALTERACAO`
  (destino/datas). Logística e cancelamento passaram a enviar email (antes só gravavam).
- **Templates parametrizados** em dev por `docs/db/missao_servico_notificacoes_dml.sql` (7 tipos no
  domínio `TIPO_NOTIFICACAO`, 7 templates, textos **provisórios**). O aviso de logística procura
  `MISSAO_LOGISTICA_COLABORADOR_{TIPO}` antes do genérico.
- **PDF da requisição** alinhado com o modelo 3.2.4.2.2 (logótipo, caixas, `RMS-2026-0006`, ECV).
- **Vistas da missão inteira (novas, os individuais mantêm-se):** `GET /{uuid}/cabimentos` e
  `GET`/`PUT /{uuid}/avaliacoes`.

Missão de teste **7/2026** (`01a0a9fc-7372-7a29-be08-8150db869ed9`): fluxo completo dos 4 processos,
FINALIZADO e paga. **8/2026**: cancelada, para os testes de email.

## Decisions made — do not re-litigate

- **Alertas da missão e parametrização de destinatários: fora do âmbito** (analista, 16/09). Os
  destinatários são o próprio prestador e o próprio funcionário.
- **Avaliação é por prestador EM CADA PROCESSO**: avalia-se o serviço prestado, e é o que
  `RH_T_MISSAO_PRESTADOR_AVAL.MISSAO_PREST_ID` suporta.
- **Limiares A–D** (A>75, B ]40;75], C ]25;40], D [0;25]) **estão na spec**, na tabela "Intervalos
  (pontuação total %)". A dúvida 1.3 está fechada.
- **É UGAL**, não UPAL (UPAL é gralha do título da secção).
- **Uma notificação de logística por processo** (decisão 1.1 mantida; confirmada pelo ecrã 3.2.4.3.1).
- **Filtro `etapa` continua opcional** na API; a obrigatoriedade é do ecrã.
- **Vistas da missão não substituem os endpoints por processo.**

## Constraints

- **Compilar com JDK 23** (`$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"`),
  mas **correr o jar com o JDK 25** (`...\jdk-25.0.2.10-hotspot`): o JIT do 23 mata a JVM com
  `0xC0000005`, mesmo com `-XX:TieredStopAtLevel=1`.
- **Respostas nunca são `Map`** — DTO próprio ou `SuccessResponseDTO` do shared.
- Manifestos `.igrpstudio/**.json` acompanham DTOs e endpoints; editar só o bloco alterado (o
  `json.dumps` reformata ficheiros com objectos numa linha).
- Alterações de API vão a `docs/frontend_changes_missao_servico.md` **e** o HTML é regenerado.
- **A spec em HTML (`docs/spec_missao_servico_14_09.html`) está incompleta** — perdeu tabelas. A
  fonte é o `.md` e as **27 imagens em `docs/img/missao/`** (mapeadas às secções pelo `src="media/…"`
  do `.md`).

## Blockers & risks

- **Tabela de preços da ajuda de custo**: não existe na spec nem na BD; `valorDiario` vem do cliente
  sem validação — risco financeiro. Proposta de modelo e 4 perguntas na dúvida 5.2.
- **SGAL**: sem endpoint nem contrato; `cabId` fica `null`. O ecrã mostra `CAB/2026/001` (texto) mas
  `RH_T_MISSAO_LOGISTICA.CAB_ID` é **NUMBER** — ver dúvida 5.1.
- **Anexos no email**: `sipsv0.SEND_MAIL_V1` só aceita destinatário, assunto e corpo. Em dev nem
  está acessível ao INPSRH ("bad SQL grammar"), por isso os envios ficam `Erro` — é esperado.
- Textos dos templates são provisórios (falta o negócio rever, e os dos 3 processos sem template).
- DDL e DML por aplicar em staging/produção.

## Relevant files

- `docs/duvidas_analista_missao_servico.md` — **levar ao analista**; tem o estado de cada ponto.
- `docs/frontend_changes_missao_servico.md` — guia por ecrã (fonte); `docs/evidencias_missao.html` é gerado.
- `docs/db/missao_servico_notificacoes_dml.sql` e `docs/db/missao_servico_14_09_ddl.sql` — por aplicar noutros ambientes.
- `src/main/java/cv/inps/rh/missaoservico/application/services/MissaoProcessoServiceWrite.java:1089` — `gerarCabimentoAutomatico` (TODO SGAL).
- `src/main/java/cv/inps/rh/missaoservico/application/services/MissaoProcessoServiceWrite.java:722` — `calcularValorDiarioAjudaCusto` (base vem do cliente).
- `src/main/java/cv/inps/rh/missaoservico/application/services/MissaoNotificacaoColaborador.java` — envio ao colaborador.
- `tools/db/DbQuery.java`, `DbExecFile.java` — SQL directo (DbExecFile lê UTF-8; usar para PL/SQL).

## How to verify / resume

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
cd C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE
mvn -B test                       # esperado: Tests run: 17, Failures: 0
mvn -B package -DskipTests        # parar a app antes: ela segura o jar
Get-Content .env | ForEach-Object { if ($_ -match '^\s*([A-Z_]+)\s*=\s*(.*)$') { [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2].Trim(), 'Process') } }
& "C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe" -jar target\rh-service-0.0.1-SNAPSHOT.jar
```
Pronto quando o log diz `Started RhInpsServiceApplication` (porta 8087). O `ERROR` do Keycloak é
esperado e os endpoints respondem sem token.

SQL directo:
```powershell
$cp = "<repo>\tools\db;$env:USERPROFILE\.m2\repository\com\oracle\database\jdbc\ojdbc11\21.9.0.0\ojdbc11-21.9.0.0.jar"
& "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot\bin\java.exe" -cp $cp DbQuery "SELECT ..."
```

Cliente de teste que simula os ecrãs (GET → payload → PUT → resposta): foi escrito no scratchpad
desta sessão (`missao_client.py`); se não existir, são ~150 linhas com `urllib`.

## Test / validation plan

Só para o que **ainda não foi testado ao vivo** ou o que se voltar a mexer.

1. **Já validado nesta sessão, não repetir sem motivo:** fluxo completo da 7/2026 (4 processos até
   FINALIZADO e pago), as 3 notificações novas, email na logística e no cancelamento, avaliação
   (95/A do exemplo da spec, correcção sem duplicar, 2 caminhos de 400), PDF da requisição, vista dos
   cabimentos e vista das avaliações (incluindo atomicidade do PUT).
2. **Por testar — nº da missão no cancelamento.** `PATCH /{uuid}/cancelar` numa missão nova →
   esperado assunto "Cancelamento de Missão Nº **n/2026**" (antes só "n"). Evidência:
   `SELECT ASSUNTO, MESSAGE FROM RH_T_NOTIFICACAO WHERE TIPO_NOTIFICACAO='MISSAO_CANCELAMENTO' ORDER BY ID DESC`.
3. **Se mexer nos templates:** `GET .../processos/{TIPO}/logistica` → `notificacao.assunto` deve vir
   do template do tipo (bilhete: "Emissão de Bilhete de Viagem") e, nos outros, do genérico.
4. **Regra de ouro (memória):** depois de cada escrita, confirmar por SQL directo; e confirmar que os
   400 **não** gravaram nada.
5. **Pedir autorização antes de cada fluxo de escrita** e mostrar o payload completo antes de enviar.

## Open questions

Todas em `docs/duvidas_analista_missao_servico.md`. Por ordem: tabela de preços (5.2), SGAL (5.1,
mais o formato do nº e a coluna "Nome" do ecrã), anexos no email (7.1), textos dos templates (3.1),
colaborador retirado da missão (7.2). A avaliação por processo vs por missão pode ser confirmada com
o analista, embora esteja decidida e implementada.

## Next step

Levar `docs/duvidas_analista_missao_servico.md` ao analista. Enquanto não houver resposta, o único
trabalho sem dependências é substituir os textos provisórios dos templates quando o negócio os
enviar.

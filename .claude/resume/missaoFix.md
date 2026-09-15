> Updated: 2026-09-15 11:40

## Goal

Fechar o módulo **Missão de Serviço** segundo a spec de 14/09/2026, que reestruturou a missão em
4 processos independentes. O fluxo está implementado e validado. O que falta é sobretudo
**parametrização e decisões de negócio**, levantadas em `docs/duvidas_analista_missao_servico.md`.

## Current state

Tudo em `develop` (branch `feat/missao-servico-processos` já integrado por fast-forward). **Só o
commit `a31663c9` está por empurrar.**

Implementado e validado contra a API a correr:
- 4 processos por missão, cada um com a sua etapa; 25 endpoints em `/api/v1/missao-servico`.
- As 8 etapas de ponta a ponta, missão a chegar a `FINALIZADO` e pagamento registado.
- Gestão de prestadores, avaliação (pesos 5/15/40/20/20, classes A–D), PDF da requisição,
  cancelamento com renotificação, missão internacional.
- `GET /{uuid}/notificacoes` (ecrã "Ver Notificação"), filtros `referenciaName`/`referenciaUuid`
  no endpoint genérico de notificações.

Duas baterias de testes live (~70 + ~40 passos), com confirmação por SQL após cada escrita.
`mvn test`: **14 testes, 0 falhas**.

Documentação: [docs/frontend_changes_missao_servico.md](../../docs/frontend_changes_missao_servico.md)
é um **guia por ecrã** (não um changelog); [docs/evidencias_missao.html](../../docs/evidencias_missao.html)
é a versão HTML gerada dele. A spec `.docx` está convertida em
[docs/spec_missao_servico_14_09.html](../../docs/spec_missao_servico_14_09.html) com os 27 ecrãs do protótipo.

**[docs/duvidas_analista_missao_servico.md](../../docs/duvidas_analista_missao_servico.md) é o
documento a levar ao analista** — 6 temas, 8 pontos marcados como bloqueantes, cada um com citação
da spec, estado actual e a pergunta. Ler antes de retomar: evita reabrir o que já está levantado.

**Notificações são enviadas, não só gravadas.** O padrão é `try { emailService.sendEmail(...) }
catch { estado = "Erro" }` e grava sempre em `RH_T_NOTIFICACAO` com o estado que resultou. Em dev
não há SMTP, por isso ficam em `"Erro"` — o fluxo nunca parte por causa disso.

## Decisions made — do not re-litigate

- **Endpoints antigos ficam vivos e `deprecated`**: remover só quando o front-end migrar (Fase 11).
- **Notificações da missão têm endpoint próprio** (`/{uuid}/notificacoes`), e não filtro no
  genérico: as notificações ficam sob 4 referências diferentes; o filtro obrigaria a N chamadas
  por linha de lista. Segue o padrão já usado em `/alertas/{id}/notificacoes`.
- **Não mudar a referência com que a missão grava notificações**: obrigaria a migrar dados e
  partiria o cancelamento, que procura por prestador para saber quem já foi notificado.
- **`colaboradorId`/`colaboradorIds`/`funcionarioUuids` aceitam os dois uuids** (funcionário e
  colaborador-da-missão): o `GET` expõe ambos e o 400 era indecifrável.
- **Avaliação segue o texto da spec (total 0–100, classes A–D)**, não o protótipo (escala 1–5,
  "Muito Bom/Bom/Regular").
- **`alojamento` continua opcional** na submissão: ausente = `true`. Default deliberado.
- **Os pesos da avaliação NÃO estão errados na spec.** O `Total : 5+15+40+20+15 = 95%` é um
  *exemplo* (o Preço pontua "Bom": 20 × 75% = 15 pontos); os pesos são 5/15/40/20/**20** = 100% e
  conferem com o domínio. Já foi levantado como bug e é falso — não reabrir.
- **A notificação por processo na Logística é fiel à spec**, não um bug de âmbito: a secção da etapa
  manda notificar em cada `NEXT`. A secção de Notificações pede uma de síntese. A spec diz as duas
  coisas — está em aberto (dúvida 1.1), não corrigir por iniciativa própria.

## Constraints

- **JDK 23 obrigatório**: `$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"`.
  O `JAVA_HOME` do sistema aponta ao 21 e o build falha.
- **Oracle 11.2 em dev**: não aceita `FETCH FIRST` — usar `ROWNUM`.
- **DDL aplicado só em dev** (`docs/db/missao_servico_14_09_ddl.sql`). Flyway desligado;
  `staging`/`production` usam `ddl-auto=validate` — sem o DDL a app não arranca lá.
- Alterações à API têm de ficar em `docs/frontend_changes_missao_servico.md` **e** o HTML
  regenerado (ver "How to verify").
- Manifestos `.igrpstudio/**.json` têm de acompanhar DTOs e endpoints novos.

## Blockers & risks

- **Job de alertas: o âmbito é que está por decidir, não a infra.** A TRANSVERSAL (§JOB Alerta)
  especifica **2** alertas para a missão e marca-os *"Pendente: A por verificar se isso faz sentido
  ainda fazer"*; a spec da missão descreve **6**. A infra existe e funciona (`@Scheduled` diário,
  3 tipos noutros módulos, prazos por `CONFIGURACAO_PRAZO`). Dos 6, dois precisam de uma data de
  vencimento da fatura que não existe no modelo. Ver dúvidas 2.1, 2.2, 3.2.
- **Limiares das classes A–D sem origem.** `AvaliacaoPrestadorCalculo.designacao()` tem
  `A > 75, B ]40;75], C ]25;40], D [0;25]` fixos. Não estão na spec de 14/09, nem na de 19/08, nem
  no domínio `AVALIACAO_FORNECEDOR` (que só tem as designações). Ver dúvida 1.3.
- **Parametrização: 3 dos 5 mecanismos existem.** Templates ✅ (faltam os textos — 2 dos 3 tipos da
  missão nem estão registados e o que está diz "Polhover imoant"); tipos ✅; prazos ✅ (domínio
  vazio); destinatários ⚠️ (existe, mas só para envio manual — os papéis RH/SGAL/Financeiro/Agência
  não existem); gatilhos ❌ (não há tabela).
- **A JVM cai com `0xC0000005`** (JIT do JDK 23) a meio de sessões longas, mesmo com
  `-XX:TieredStopAtLevel=1`. Correr pelo jar, não por `spring-boot:run`.
- `cabId` do SGAL fica `null` — sem contrato de integração.
- `valorDiario` da ajuda de custo vem do cliente — a tabela de preços nunca foi especificada.
- Templates de notificação em dev são texto de preenchimento ("Polhover imoant"). É dado, não código.

## Relevant files

- `src/main/java/cv/inps/rh/shared/domain/service/AlertaWriteService.java:54` — `@Scheduled` do job;
  é aqui que entram os `processarX()` da missão, a seguir ao padrão dos 3 já existentes.
- `src/main/java/cv/inps/rh/missaoservico/application/services/MissaoProcessoServiceWrite.java` —
  escrita das etapas; a guarda de etapa só corre aqui, nunca nos `GET`.
- `src/main/java/cv/inps/rh/missaoservico/application/services/MissaoServicoServiceRead.java:293` —
  `getPagamento`, que deriva a etapa do processo mais atrasado.
- `docs/plano_implementacao_missao_servico_14_09.md` — secção "Fora deste plano" tem o levantamento
  alerta a alerta.
- `tools/db/DbQuery.java` — SQL directo; correr por PowerShell com classpath absoluto.

## How to verify / resume

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
cd C:\Users\ivanick.santos\Nick-personal\personal-workspace\projects\RH_INPS_SERVICE
mvn -B test                      # esperado: Tests run: 14, Failures: 0
mvn -B package -DskipTests
# arrancar (porta 8087, perfil development, .env na raiz)
Get-Content .env | ForEach-Object { if ($_ -match '^\s*([A-Z_]+)\s*=\s*(.*)$') { [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2].Trim(), 'Process') } }
java -XX:TieredStopAtLevel=1 -jar target\rh-service-0.0.1-SNAPSHOT.jar
```
Pronto quando o log diz `Started RhInpsServiceApplication`. O `ERROR` do Keycloak é esperado (sem
auth local) e não impede nada — os endpoints respondem sem token.

SQL directo:
```powershell
java -cp "<repo>\tools\db;$env:USERPROFILE\.m2\repository\com\oracle\database\jdbc\ojdbc11\21.9.0.0\ojdbc11-21.9.0.0.jar" DbQuery "SELECT ..."
```

Regenerar o HTML do guia após editar o Markdown (o script está no scratchpad da sessão anterior;
se não existir, é um conversor Markdown→HTML autónomo de ~200 linhas em Python):
o HTML **não** se edita à mão.

Estado de dados em dev: missão `3/2026` está `FINALIZADO` e paga; `6/2026` cancelada; `1/2026`
activa a meio do fluxo. 4 prestadores parametrizados.

## Test / validation plan

Para o alerta **"Missão próxima do início sem confirmação"** (o único construível):

1. **Setup** — criar missão com `dataInicio` dentro do limiar (ex.: hoje + 3 dias) via
   `POST /api/v1/missao-servico/submissao`, sem gravar logística em nenhum processo.
   Criar uma segunda missão igual **com** logística completa, como controlo negativo.
2. **Acção** — invocar `AlertaWriteService.executarJobAlertas()` (por teste de integração ou
   baixando temporariamente o cron), não esperar pelas 6h.
3. **Esperado** — 1 alerta novo em `RH_T_ALERTA` para a primeira missão, nenhum para a segunda.
4. **Evidência** —
   `DbQuery "SELECT ID, TIPO_ALERTA, REFERENCIA_ID, ESTADO FROM RH_T_ALERTA WHERE TIPO_ALERTA LIKE '%MISSAO%'"`
5. **Idempotência** — correr o job **segunda vez**: a contagem não pode aumentar (o padrão usa
   `existsByReferenciaIdAndTipoAlerta`). É o erro mais provável de quem copia os handlers existentes.
6. **Ver Alerta** — `GET /api/v1/funcionarios/alertas` passa a devolver linhas; hoje devolve 0.
   Confirmar que o alerta traz referência utilizável pelo ecrã da Lista Missão.

Caminhos não felizes a cobrir: missão **cancelada** (não deve gerar alerta), missão **finalizada**
(idem), processo `ALOJAMENTO` inactivo (não deve contar como logística em falta).

## Open questions

Todas levantadas, com citação da fonte, em
**[docs/duvidas_analista_missao_servico.md](../../docs/duvidas_analista_missao_servico.md)**.
As que mais desbloqueiam:

1. **Âmbito dos alertas** (dúvida 2.1) — decide se há job a fazer e qual.
2. **Destinatários por papel** (3.3) — desbloqueia 5 notificações internas de uma vez.
3. **Tabela de preços da ajuda de custo** (5.2) — é onde há risco financeiro hoje: o `valorDiario`
   vem do cliente sem validação.
4. **Contrato do SGAL** (5.1) — depende de terceiros, convém arrancar cedo.
5. **Notificação da logística: uma ou quatro** (1.1) — afecta o que o colaborador recebe já hoje.
6. **Limiares A–D** (1.3) — hoje fixos no código sem origem conhecida.

## Next step

Empurrar `a31663c9` e levar `docs/duvidas_analista_missao_servico.md` ao analista. Sem a decisão de
âmbito (2.1), implementar alertas é adivinhar.

Enquanto isso, há trabalho seguro que não depende de ninguém: registar os 2 templates em falta e
substituir o de teste em `RH_T_PARAM_NOTIFICACAO` (basta ter os textos), e carregar o domínio
`CONFIGURACAO_PRAZO`.

> Updated: 2026-09-15 11:05

## Goal

Fechar o módulo **Missão de Serviço** segundo a spec de 14/09/2026, que reestruturou a missão em
4 processos independentes. O fluxo está implementado e validado; falta o **job de alertas** e
algumas dependências de negócio/BD que o bloqueiam.

## Current state

Tudo em `develop` (branch `feat/missao-servico-processos` já integrado por fast-forward). **Só o
commit `d13f2df8` está por empurrar.**

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

- **Job de alertas: 5 dos 6 alertas não são construíveis.** Falta no modelo: data de vencimento da
  fatura (é um anexo em `RH_T_DOCUMENTO`, sem datas), conceito de "a agência confirmou a
  requisição", e a parametrização de prazos/limiares que a própria spec exige. Desbloqueia com
  decisão de negócio + DDL.
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

- **Negócio:** qual o prazo de "fatura em falta" e "requisição sem resposta"? Quais os documentos
  obrigatórios por etapa? Decide o RH.
- **DBA:** acrescentar data de vencimento à fatura e um estado de confirmação à requisição?
- **Negócio/arquitectura:** a parametrização de prazos e limiares **está na spec como requisito mas
  não está especificada** — a secção "Parametrizações" é uma frase por item, sem tabela, colunas
  nem valores, ao contrário do resto da spec, que mapeia campo a campo. A doc de BD também não a
  cobre: `RH_T_PARAM_NOTIFICACAO` só tem `TIPO_NOTIFICACAO`/`ASSUNTO`/`CORPO`/`ESTADO` (templates),
  sem prazo, limiar, gatilho ou destinatário. É decisão de desenho por tomar, não implementação
  pendente: que tabela, que colunas, que valor por alerta.
- A spec soma os pesos da avaliação como `5+15+40+20+15 = 95%`, mas usa 20 no preço (= 100).
  Seguimos 100; confirmar com o negócio.

## Next step

Empurrar `d13f2df8`. Depois, levar as perguntas acima ao RH/DBA — sem elas, 5 dos 6 alertas não
avançam. O alerta "missão próxima do início sem confirmação" pode ser implementado já, em paralelo.

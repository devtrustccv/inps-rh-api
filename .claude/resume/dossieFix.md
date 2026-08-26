> Updated: 2026-08-25 21:10

## Goal

Correções e validações no dossiê do colaborador (RH INPS): detalhe de alterações do registo, renovação
de contrato, novo contrato, datas da carreira e validação da mobilidade. Testar sempre live.

## Current state — tudo COMMITADO em `develop` e testado live

- `23e087c7` — detalhe do registo: snapshots funcionário/contrato via JaVers (RH_T_FUNCIONARIOS/RH_T_CONTRATO_VINCULO).
- `f9af9ed9` — renovação: `ContratoHistoricoWriteService.transicionarRenovacao` (proposta P→decisão; deixava de ficar presa em P).
- `4b962ff1` — carreira get-by-id: datas vêm de `car` (RH_T_CARREIRA), como a vista RH_V_CARREIRA; antes vinham do tiprel (dataFim null).
- `aaaffb46` — mobilidade: enum `TipoMobilidade` + validação em `MobilidadeWriteService.createMobilidade`.

App a correr na 8088 (recompilada com JDK23).

## Decisions made — do not re-litigate

- Não furar guards (D2 novo contrato; renovável só p/ renovação). Testar cenários válidos (ex.: contrato expirado p/ novo contrato).
- Mobilidade: multi-select = códigos CSV; tipo escolhido → "(depois)" obrigatório; tipo não escolhido → copia "antes".
- Manter JaVers no detalhe do registo; funcionário/contrato via snapshot dedicado.

## Constraints (preferências do utilizador — ver [[feedback-fluxo-validacao-teste]])

- Compilar SEMPRE `mvn clean compile` com `JAVA_HOME=.../Eclipse Adoptium/jdk-23.0.2.7-hotspot`; parar app antes (lock do target). App: `nohup mvn -DskipTests spring-boot:run > app_run.log 2>&1 &`, porta 8088.
- GET (leitura) SEMPRE antes de cada validação/escrita; arrays com `id`; resultados em pretty (`python -m json.tool`); pedir AUTORIZAÇÃO antes de cada escrita; testar casos NEGATIVOS antes do happy path. contratoId no path = UUID. Body de mobilidade = `MobilidadeDTO` direto (sem wrapper).

## Relevant files

- `funcionario/application/constants/TipoMobilidade.java` + `service/MobilidadeWriteService.java` (createMobilidade).
- `service/carreira/CarreiraReadService.java` (getCarreiraResponseDTO ~L196).
- `service/ContratoHistoricoWriteService.java` (transicionarRenovacao) + `service/ValidacaoRenovacaoContratoService.java`.
- `service/registodetalhe/*` + `RegistoColaboradorValidacaoDetalheDescriptor.java`.

## Open questions

- Uniformizar formato de data entre get-by-id (dd-MM-yyyy) e listas (yyyy-MM-dd) — adiado.
- Alinhar `MobilidadeWriteService.updateMobilidade` (editar/reenvio) com a mesma validação do enum — não feito.

## Next step

Nada pendente. Colaboradores de teste: 940 (`01a03aab-41c9-7412-a89f-0198e25319df`, com mobilidade H2 pendente por validar), 930, 926. Se quiser: validar a mobilidade H2 pendente, ou abrir PR contra `develop`.

# RH INPS Service

Serviço de Recursos Humanos do INPS (Instituto Nacional de Previdência Social — Cabo Verde). Spring Boot 3.5 + Java 23, sobre o framework **IGRP** (cv.igrp.framework), persistência Oracle via JPA/Hibernate, OAuth2 Resource Server.

Ver @README.md e @pom.xml.

## Comandos

- Build: `mvn clean package` (use `-DskipTests` para pular testes)
- Run local: `mvn spring-boot:run` (perfil default: `development`, porta `8089`)
- Testes: `mvn test` — rodar um teste isolado: `mvn test -Dtest=NomeDaClasse#metodo`
- Docker (Oracle + app): `docker-compose up -d`
- Variáveis: copiar `.env.example` → `.env` antes de rodar
- Profiles: `development`, `staging`, `production` (arquivos `application-<profile>.properties`)

## Arquitetura

Estrutura **hexagonal / Clean Architecture** por módulo de negócio. Cada módulo em `src/main/java/cv/inps/rh/<modulo>/` segue:

- `domain/` — entidades, value objects, regras de negócio puras
- `application/` — casos de uso, services, DTOs
- `infrastructure/` — repositórios JPA, adapters, integrações
- `interfaces/` — controllers REST, mappers de entrada/saída

Módulos de negócio: `assiduidade`, `avaliacao`, `configuracao`, `emprestimo`, `funcionario`, `missaoservico`, `parametrizacao`, `processamento`, `progressaopromocao`, `transversal`. Código compartilhado em `shared/` (config, security, util).

Documentação funcional do domínio em [docs/](docs/) — consultar antes de implementar regras de negócio (ex.: `assiduidade_regras.md`, `avaliacao_desempenho.md`, `dossier_regras.md`, `EspecificacaoTecnicaFuncional-MISSAOSERVICO.md`).

## Convenções

- **Auditoria JPA habilitada** globalmente (`@EnableJpaAuditing`) — entidades devem usar `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`. Auditor via `ApplicationAuditorAware`.
- **Lombok** em uso — preferir `@Getter/@Setter/@Builder/@RequiredArgsConstructor` em vez de boilerplate manual.
- **IDs de funcionário**: o refactor recente padronizou `funcionarioDeId` / `funcionarioParaId` (antes `tiprelDeId`/`tiprelParaId`) em DTOs de substituição. Seguir esse padrão em novos DTOs que envolvam relação entre funcionários.
- **Open-in-view desligado** (`spring.jpa.open-in-view=false`) — carregar associações explicitamente no service layer; não confiar em lazy loading no controller.
- **Batch inserts** configurados (batch_size=50, order_inserts/updates) — em operações bulk, usar `saveAll` e liberar a sessão periodicamente.
- **Dialecto Oracle** — evitar SQL ANSI-only; atenção a `LIMIT` (usar `FETCH FIRST n ROWS ONLY` ou `Pageable`).
- **Flyway desligado por padrão** (`spring.flyway.enabled=false`); migrations em `classpath:db/migration` quando habilitado.
- **Swagger UI** em `/swagger-ui.html` (controlado por `ENABLE_SWAGGER`).
- **Enum exposer do IGRP** expõe enums automaticamente em `api/v1/enums` (controlado por `igrp.enum.exposer.enabled`).

## Integrações externas

Credenciais/URLs via env vars — nunca hardcoded:

- **Oracle DB**: `ORACLE_HOST`, `ORACLE_PORT`, `ORACLE_SERVICE_NAME`, `ORACLE_USER`, `ORACLE_PASSWORD`
- **Auth JWT (OAuth2 RS)**: `AUTH_JWT_ISSUER`
- **MinIO (storage)**: `STORAGE_ENDPOINT`, `STORAGE_ACCESS_KEY`, `STORAGE_SECRET_KEY`, `STORAGE_BUCKET_NAME`
- **Processamento salarial**: `PROCESSAMENTO_SALARIAL_BASE_URL`
- **Serviços externos**: `EXTERNAL_NIF_URL`, `EXTERNAL_SNIAC_URL`, `EXTERNAL_BI_URL` (cada um com `_TOKEN` próprio)

## Workflow Git

- Branch principal: `main`. Branch de desenvolvimento: `develop`.
- Abrir PR contra `develop` (não `main`).
- Estilo de commit (ver histórico): prefixo Conventional Commits — `feat:`, `fix:`, `refactor:`, `chore:`. Mensagens podem ser em inglês ou português; ser consistente dentro da PR.

## Skills do projeto

Este repositório traz o skill **`igrp-spring-generator`** em [.claude/skills/igrp-spring-generator/](.claude/skills/igrp-spring-generator/). Invocar via `/igrp-spring-generator` (ou deixar Claude acionar automaticamente) quando o pedido envolver: criar projeto Spring Boot `newApi`, novos endpoints/rotas REST, ou gerar controller/action/DTO/model/enum/module a partir de manifestos IGRP. O skill mantém compatibilidade byte-a-byte com o gerador de referência e escreve em `.igrpstudio/**.json` + `src/main/java/**`.

## Armadilhas comuns

- Ao criar entidade nova, não esquecer `@EntityListeners(AuditingEntityListener.class)` — sem isso, os campos auditáveis ficam nulos.
- Oracle não aceita `boolean` nativo: mapear com `@Type` ou usar `NUMBER(1)` + converter.
- `java.version=23` no `pom.xml` — garantir JDK 23+ no ambiente local, senão o `maven-compiler-plugin` falha.
- Versões IGRP usam `-beta` — alinhar `igrp.version` no pom antes de bumps.

# Institution Data API Wiring Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:
> executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expose the institution-data versioned save and current-active read operations through the existing iGRP
command bus, query bus, and SOAT REST controller.

**Architecture:** A command carries the validated write DTO to a command handler that invokes
`SoatService.salvarDadosInstituicao`. A parameterless query is handled by a query handler that invokes
`SoatService.obterDadosInstituicaoAtual`; the controller delegates `POST` and `GET` requests at the same
`dados-instituicao` resource path to those buses.

**Tech Stack:** Java 23, Spring Boot 3.5 MVC, iGRP CommandBus/QueryBus, Jakarta Validation, OpenAPI, JUnit 5, Mockito.

---

### Task 1: Test application handlers and REST delegation

**Files:**

- Create: `src/test/java/cv/inps/rh/processamento/application/DadosInstituicaoApiTest.java`

- [x] **Step 1: Test command and query handlers**

```java
var commandResult = commandHandler.handle(new SalvarDadosInstituicaoCommand(request));
assertSame(response, commandResult.getBody());
verify(service).salvarDadosInstituicao(request);

var queryResult = queryHandler.handle(new GetDadosInstituicaoAtualQuery());
assertSame(response, queryResult.getBody());
verify(service).obterDadosInstituicaoAtual();
```

- [x] **Step 2: Test controller bus delegation**

```java
when(commandBus.send(any(SalvarDadosInstituicaoCommand.class)))
    .thenReturn(ResponseEntity.ok(response));
assertSame(response, controller.salvarDadosInstituicao(request).getBody());

when(queryBus.handle(any(GetDadosInstituicaoAtualQuery.class)))
    .thenReturn(ResponseEntity.ok(response));
assertSame(response, controller.getDadosInstituicaoAtual().getBody());
```

- [x] **Step 3: Run the tests before implementation**

Run: `mvn -Dtest=DadosInstituicaoApiTest test`

Expected: test compilation fails because the command, query, handlers, and controller methods do not exist.

### Task 2: Implement command and query handlers

**Files:**

- Create: `src/main/java/cv/inps/rh/processamento/application/commands/SalvarDadosInstituicaoCommand.java`
- Create: `src/main/java/cv/inps/rh/processamento/application/commands/SalvarDadosInstituicaoCommandHandler.java`
- Create: `src/main/java/cv/inps/rh/processamento/application/queries/GetDadosInstituicaoAtualQuery.java`
- Create: `src/main/java/cv/inps/rh/processamento/application/queries/GetDadosInstituicaoAtualQueryHandler.java`

- [x] **Step 1: Add the validated write command**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalvarDadosInstituicaoCommand implements Command {
  @Valid
  @NotNull(message = "The field <dadosInstituicaoRequest> is required")
  private DadosInstituicaoRequestDTO dadosInstituicaoRequest;
}
```

- [x] **Step 2: Add the write handler**

```java
@Component
public class SalvarDadosInstituicaoCommandHandler implements
    CommandHandler<SalvarDadosInstituicaoCommand, ResponseEntity<DadosInstituicaoResponseDTO>> {
  private final SoatService service;

  public SalvarDadosInstituicaoCommandHandler(SoatService service) {
    this.service = service;
  }

  @IgrpCommandHandler
  public ResponseEntity<DadosInstituicaoResponseDTO> handle(
      SalvarDadosInstituicaoCommand command) {
    return ResponseEntity.ok(
        service.salvarDadosInstituicao(command.getDadosInstituicaoRequest()));
  }
}
```

- [x] **Step 3: Add the current-active query and handler**

```java
public class GetDadosInstituicaoAtualQuery implements Query {
}

@Component
public class GetDadosInstituicaoAtualQueryHandler implements
    QueryHandler<GetDadosInstituicaoAtualQuery, ResponseEntity<DadosInstituicaoResponseDTO>> {
  private final SoatService service;

  public GetDadosInstituicaoAtualQueryHandler(SoatService service) {
    this.service = service;
  }

  @IgrpQueryHandler
  public ResponseEntity<DadosInstituicaoResponseDTO> handle(
      GetDadosInstituicaoAtualQuery query) {
    return ResponseEntity.ok(service.obterDadosInstituicaoAtual());
  }
}
```

### Task 3: Expose the REST resource

**Files:**

- Modify: `src/main/java/cv/inps/rh/processamento/interfaces/rest/SoatController.java`

- [x] **Step 1: Add the write endpoint**

```java
@PostMapping("dados-instituicao")
public ResponseEntity<DadosInstituicaoResponseDTO> salvarDadosInstituicao(
    @Valid @RequestBody DadosInstituicaoRequestDTO request) {
  return commandBus.send(new SalvarDadosInstituicaoCommand(request));
}
```

- [x] **Step 2: Add the current-active endpoint**

```java
@GetMapping("dados-instituicao")
public ResponseEntity<DadosInstituicaoResponseDTO> getDadosInstituicaoAtual() {
  return queryBus.handle(new GetDadosInstituicaoAtualQuery());
}
```

- [x] **Step 3: Verify tests and compilation**

Run: `mvn -Dtest=SoatServiceTest,DadosInstituicaoApiTest test`

Expected: all service, handler, and controller tests pass.

Run: `mvn -DskipTests compile`

Expected: Maven completes with `BUILD SUCCESS`.

# Institution Data Singleton Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:
> executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add transactional institution-data versioning in `SoatService`, retaining inactive history while exposing the
current active row.

**Architecture:** Treat `RH_T_DADOS_INSTITUICAO` as a versioned singleton with at most one active row. Every save
inactivates the current `A` row, when present, and inserts a new `A` row with a new UUID; reads select only the latest
active row and return a DTO without exposing persistence internals.

**Tech Stack:** Java 23, Spring Boot 3.5, Spring Data JPA, Jakarta Validation, Lombok, JUnit 5, Mockito.

---

### Task 1: Define create/update contracts

**Files:**

- Create: `src/main/java/cv/inps/rh/processamento/application/dto/DadosInstituicaoRequestDTO.java`
- Create: `src/main/java/cv/inps/rh/processamento/application/dto/DadosInstituicaoResponseDTO.java`

- [x] **Step 1: Add the request DTO**

Create a validated request containing `nome`, `nif`, `codCae`, `atividadeEconomica`, `numCertidaoComercial`,
`dataValidade`, `telefone`, `localidade`, `email`, `morada`, `concelhoId`, and `estado`. Use the same request for both
paths because the singleton resource has no client-selected identifier.

- [x] **Step 2: Add the response DTO**

Create a response containing the persisted `uuid` and all request fields so callers receive the complete stored
representation.

### Task 2: Test singleton create and update behavior

**Files:**

- Create: `src/test/java/cv/inps/rh/processamento/domain/service/SoatServiceTest.java`

- [x] **Step 1: Write the creation test**

Mock `findFirstByOrderByIdAsc()` to return `Optional.empty()`, invoke `salvarDadosInstituicao`, capture the saved
entity, and assert that all request fields and a newly generated UUID are persisted and returned.

- [x] **Step 2: Run the creation test and verify it fails**

Run: `mvn -Dtest=SoatServiceTest test`

Expected: compilation fails because the DTOs, repository lookup, and service method do not exist yet.

- [x] **Step 3: Write the update test**

Mock `findFirstByOrderByIdAsc()` with an existing entity, invoke the same operation, and assert that the existing entity
is saved with its ID and UUID unchanged and its editable fields replaced.

### Task 3: Implement the singleton upsert

**Files:**

- Modify: `src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/DadosInstituicaoEntityRepository.java`
- Modify: `src/main/java/cv/inps/rh/processamento/domain/service/SoatService.java`

- [x] **Step 1: Add the ordered singleton lookup**

Add `Optional<DadosInstituicaoEntity> findFirstByOrderByIdAsc()` so the service deterministically reuses the existing
row.

- [x] **Step 2: Add the transactional service operation**

Inject `DadosInstituicaoEntityRepository`. Implement
`public synchronized DadosInstituicaoResponseDTO salvarDadosInstituicao(DadosInstituicaoRequestDTO request)` with
`@Transactional`; generate a UUID only for a new entity, map every editable field, save once, and map the saved entity
to the response.

- [x] **Step 3: Run the focused tests**

Run: `mvn -Dtest=SoatServiceTest test`

Expected: both create and update tests pass.

- [x] **Step 4: Compile the project**

Run: `mvn -DskipTests compile`

Expected: Maven completes with `BUILD SUCCESS`.

### Task 4: Version rows and retrieve the current active row

**Files:**

- Modify: `src/main/java/cv/inps/rh/processamento/application/dto/DadosInstituicaoRequestDTO.java`
- Modify: `src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/DadosInstituicaoEntityRepository.java`
- Modify: `src/main/java/cv/inps/rh/processamento/domain/service/SoatService.java`
- Modify: `src/test/java/cv/inps/rh/processamento/domain/service/SoatServiceTest.java`

- [x] **Step 1: Change the tests to require system-managed status and versioning**

Use this test flow: mock `findFirstByEstadoOrderByIdDesc(Estado.A.getCode())`; for an empty result, assert one new
entity is saved with `estado = A`; for an existing active result, capture two saves and assert the first entity becomes
`I` while the second is a distinct entity with a new UUID and `estado = A`.

```java
when(repository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode()))
    .thenReturn(Optional.of(existing));
service.salvarDadosInstituicao(request);
verify(repository).saveAndFlush(existing);
verify(repository).save(captor.capture());
assertEquals(Estado.I.getCode(), existing.getEstado());
assertEquals(Estado.A.getCode(), captor.getValue().getEstado());
assertNotEquals(existing.getUuid(), captor.getValue().getUuid());
```

- [x] **Step 2: Remove status from the write request**

Delete the `estado` field and its validation annotations from `DadosInstituicaoRequestDTO`; status is derived entirely
by the service and remains present only in `DadosInstituicaoResponseDTO`.

- [x] **Step 3: Replace the singleton lookup with the active lookup**

```java
Optional<DadosInstituicaoEntity> findFirstByEstadoOrderByIdDesc(String estado);
```

- [x] **Step 4: Implement versioned save and current read**

```java
@Transactional
public synchronized DadosInstituicaoResponseDTO salvarDadosInstituicao(
    DadosInstituicaoRequestDTO request) {
  dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode())
      .ifPresent(atual -> {
        atual.setEstado(Estado.I.getCode());
        dadosInstituicaoRepository.saveAndFlush(atual);
      });

  var novo = new DadosInstituicaoEntity();
  novo.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
  novo.setEstado(Estado.A.getCode());
  apply(request, novo);
  return toResponse(dadosInstituicaoRepository.save(novo));
}

@Transactional(readOnly = true)
public DadosInstituicaoResponseDTO obterDadosInstituicaoAtual() {
  return dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode())
      .map(this::toResponse)
      .orElseThrow(() -> IgrpResponseStatusException.notFound(
          "Active institution data not found"));
}
```

- [x] **Step 5: Verify the implementation**

Run: `mvn -Dtest=SoatServiceTest test`

Expected: creation, versioning, active retrieval, and missing-active tests pass.

Run: `mvn -DskipTests compile`

Expected: Maven completes with `BUILD SUCCESS`.

### Configuração API — ConfiguracaoController

This document explains the generic configuration endpoints exposed by `ConfiguracaoController` and how to use them to manage multiple configuration types through a single engine. It includes the supported types, required query parameters, and the request/response JSON structures derived from the DTOs in the codebase.

#### Base path
```
/configuracao
```

#### Design overview (engine-based)
- All operations are routed through a single controller and delegated to a configuration engine (`IConfiguration`).
- The configuration type is selected via the `configurationType` query parameter. It must be one of the enum names from `ConfigurationType`.
- Internally, each enum maps to a stable engine code (shown below), but clients pass the enum name in `configurationType`.

#### Supported configuration types
Enum values from `cv.inps.rh.configuracao.application.constants.ConfigurationType` with their internal engine codes and descriptions:

- PARAM_VINCULO → code: `param_vinculo_type` — "PARAM_VINCULO"
- PARAM_SITUACAO_LABORAL → code: `situacao_laboral_type` — "PARAM_SITUACAO_LABORAL"
- PARAM_TIPO_CONTRATO_LABORAL → code: `tipo_contrato_laboral_type` — "PARAM_TIPO_CONTRATO_LABORAL"
- PARAM_CARREIRA → code: `carreira_type` — "PARAM_CARREIRA"
- PARAM_CARGO → code: `cargo_type` — "PARAM_CARGO"
- PARAM_ESCALAO → code: `escalao_type` — "PARAM_ESCALAO"
- PARAM_LOCAL_TRABALHO → code: `local_trabalho_type` — "PARAM_LOCAL_TRABALHO"
- PARAM_SECCAO → code: `seccao_type` — "PARAM_SECCAO"
- PARAM_TIPO_DOCUMENTO → code: `tipo_documento_type` — "PARAM_TIPO_DOCUMENTO"
- PARAM_NOTIFICACAO → code: `notificacao_type` — "PARAM_NOTIFICACAO"

Important: Use the enum name in requests (e.g., `configurationType=PARAM_CARGO`). The controller forwards the corresponding code to the engine internally.

---

### Endpoints

#### 1) Create configuration
- Method/Path: `POST /configuracao`
- Query: `configurationType={ENUM_NAME}` (required)
- Body: JSON according to the type-specific request DTO (see sections below)
- Responses:
  - `201 Created`
    - For most types: `{ "id": "<generated-id>" }` (`ConfigurationResponseIdDTO`)
    - For `PARAM_CARREIRA`: a full `CarreiraResponseDTO` object
  - `400 Bad Request` if validation fails
  - `500 Internal Server Error` on server errors

Example cURL (create Cargo):
```
curl -X POST "{HOST}/configuracao?configurationType=PARAM_CARGO" \
  -H "Content-Type: application/json" \
  -d '{
        "descricao": "Analista Sénior",
        "carreiraId": "car-123",
        "dirigente": "NAO",
        "estado": "ATIVO"
      }'
```

#### 2) Update configuration
- Method/Path: `PUT /configuracao/{id}`
- Query: `configurationType={ENUM_NAME}` (required)
- Body: JSON according to the type-specific request DTO
- Responses:
  - `200 OK` (no body)
  - `400 Bad Request` | `404 Not Found`

#### 3) Read configuration by id
- Method/Path: `GET /configuracao/{id}`
- Query: `configurationType={ENUM_NAME}` (required)
- Responses:
  - `200 OK` with a type-specific object (see response notes per type)
  - `404 Not Found`

#### 4) List configurations
- Method/Path: `GET /configuracao`
- Query: `configurationType={ENUM_NAME}` (required) + optional filter params (dynamic, engine-defined)
- Responses: `200 OK` with a JSON array of type-specific objects.

#### 5) Delete configuration
- Method/Path: `DELETE /configuracao/{id}`
- Query: `configurationType={ENUM_NAME}` (required)
- Responses:
  - `204 No Content`
  - `404 Not Found`

---

### Type-specific payloads (derived from DTOs)
Field requirements are based on bean validation annotations in the DTOs (`@NotBlank`, `@NotNull`). Types shown are the Java types inferred from the DTOs.

Note on responses:
- Create (POST): Most types return only `{ id }`. `PARAM_CARREIRA` returns a detailed object (`CarreiraResponseDTO`).
- Read/List (GET): Objects generally mirror the request DTO fields for the type. For `Carreira`, a specific `CarreiraResponseDTO` is used (see details below). Some implementations may also include additional computed fields (e.g., `estadoDescricao`).

#### PARAM_CARGO — `CargoRequestDTO`
Request body:
```
{
  "descricao": String,              // required
  "carreiraId": String,            // optional
  "dirigente": String,             // required (e.g., "SIM" | "NAO")
  "estado": String                 // optional
}
```
Create response: `{ "id": String }`
Read/List response: mirrors request fields (plus any backend-provided fields if applicable).

Example request:
```
{
  "descricao": "Analista Sénior",
  "carreiraId": "car-123",
  "dirigente": "NAO",
  "estado": "ATIVO"
}
```

#### PARAM_CARREIRA — `CarreiraRequestDTO` / `CarreiraResponseDTO`
Request body (`CarreiraRequestDTO`):
```
{
  "descricao": String,                         // required
  "estado": String,                           // optional
  "categorias": [                             // optional list; each item is CategoriaCarreiraResponseDTO
    {
      "id": String,                           // required
      "categoria": String,                    // optional
      "estado": "ATIVO" | "INATIVO"        // optional (enum Estado)
    }
  ]
}
```
Create response (`CarreiraResponseDTO`):
```
{
  "id": String,
  "descricao": String,
  "categorias": [
    { "id": String, "categoria": String, "estado": "ATIVO" | "INATIVO" }
  ],
  "estado": String,
  "estadoDescricao": String
}
```
Read/List response: same as the create response shape above.

#### PARAM_ESCALAO — `EscalaoRequestDTO`
Request body:
```
{
  "escalao": String,               // required
  "nivelReferencia": Integer,      // required
  "carreiraId": String,            // required
  "categoriaId": String,           // optional
  "salario": String,               // required (formatted string)
  "dataInicio": "YYYY-MM-DD",     // required
  "dataFim": "YYYY-MM-DD",        // optional
  "estado": String                 // optional
}
```
Create response: `{ "id": String }`
Read/List response: mirrors request fields.

#### PARAM_LOCAL_TRABALHO — `LocalTrabalhoRequestDTO`
Request body:
```
{
  "local": String,                 // required
  "pais": String,                  // required
  "ilha": String,                  // optional
  "ups": String,                   // required
  "estado": String                 // optional
}
```
Create response: `{ "id": String }`
Read/List response: mirrors request fields.

#### PARAM_SECCAO — `SeccaoRequestDTO`
Request body:
```
{
  "descricao": String,             // required
  "direcaoId": String,             // required
  "estado": String                 // optional
}
```
Create response: `{ "id": String }`
Read/List response: mirrors request fields.

#### PARAM_TIPO_DOCUMENTO — `TipoDocumentoRequestDTO`
Request body:
```
{
  "codigo": String,                // required
  "descricao": String,             // required
  "referencia": String,            // optional
  "estado": String                 // optional
}
```
Create response: `{ "id": String }`
Read/List response: mirrors request fields.

#### PARAM_TIPO_CONTRATO_LABORAL — `TipoContratoLaboralRequestDTO`
Request body:
```
{
  "codigo": String,                // required
  "descricao": String,             // required
  "natureza": String,              // required
  "renovavel": String,             // optional (e.g., "SIM" | "NAO")
  "duracao": Integer,              // optional
  "maxNumeroRenovacao": Integer,   // optional
  "prazo": String,                 // optional
  "estado": String                 // optional
}
```
Create response: `{ "id": String }`
Read/List response: mirrors request fields.

#### PARAM_SITUACAO_LABORAL — `SituacaoLaboralRequestDTO`
Request body:
```
{
  "codigo": String,                // required
  "descricao": String,             // required
  "tipo": String,                  // required
  "estadoContrato": String,        // required
  "remuneracao": String,           // required
  "carreira": String,              // required
  "tempoServico": String,          // required
  "cessaVinculo": String,          // required
  "progressaoPromocao": String,    // required
  "estado": String                 // optional
}
```
Create response: `{ "id": String }`
Read/List response: mirrors request fields.

#### PARAM_VINCULO — `VinculoLaboralRequestDTO`
Request body:
```
{
  "codigo": String,                // required
  "descricao": String,             // required
  "contrato": String,              // required
  "carreira": String,              // required
  "remuneracao": String,           // required
  "tempoServico": String,          // required
  "estado": String                 // optional
}
```
Create response: `{ "id": String }`
Read/List response: mirrors request fields.

#### PARAM_NOTIFICACAO — `NotificacaoRequestDTO`
Request body:
```
{
  "assunto": String,               // required
  "corpo": String,                 // required
  "referencia": String,            // optional
  "estado": String                 // optional
}
```
Create response: `{ "id": String }`
Read/List response: mirrors request fields.

---

### Common usage notes
- Always provide `configurationType` as the enum name (e.g., `PARAM_ESCALAO`).
- Date fields use ISO-8601 format (`YYYY-MM-DD`).
- Validation: fields annotated with `@NotBlank`/`@NotNull` are required; the API returns `400` if missing or invalid.
- Listing supports additional filter query parameters; they are forwarded to the engine and may vary by type.

### Quick reference: Create examples per type
Below are minimal payloads satisfying required fields.

```
# Cargo (PARAM_CARGO)
{ "descricao": "Analista", "dirigente": "NAO" }

# Carreira (PARAM_CARREIRA)
{ "descricao": "Carreira Técnica" }

# Escalão (PARAM_ESCALAO)
{ "escalao": "A1", "nivelReferencia": 1, "carreiraId": "car-1", "salario": "45000", "dataInicio": "2025-01-01" }

# Local de Trabalho (PARAM_LOCAL_TRABALHO)
{ "local": "Sede", "pais": "CV", "ups": "UPS-01" }

# Secção (PARAM_SECCAO)
{ "descricao": "Operações", "direcaoId": "dir-1" }

# Tipo Documento (PARAM_TIPO_DOCUMENTO)
{ "codigo": "BI", "descricao": "Bilhete de Identidade" }

# Tipo Contrato Laboral (PARAM_TIPO_CONTRATO_LABORAL)
{ "codigo": "CT1", "descricao": "Contrato a termo", "natureza": "TERMO" }

# Situação Laboral (PARAM_SITUACAO_LABORAL)
{ "codigo": "ATV", "descricao": "Ativo", "tipo": "ATIVO", "estadoContrato": "VIGENTE", "remuneracao": "SIM", "carreira": "SIM", "tempoServico": "SIM", "cessaVinculo": "NAO", "progressaoPromocao": "SIM" }

# Vínculo Laboral (PARAM_VINCULO)
{ "codigo": "VIN1", "descricao": "Efetivo", "contrato": "SIM", "carreira": "SIM", "remuneracao": "SIM", "tempoServico": "SIM" }

# Notificação (PARAM_NOTIFICACAO)
{ "assunto": "Aviso", "corpo": "Mensagem" }
```

---

### HTTP status summary
- POST: 201 (returns `id` or full object for Carreira)
- PUT: 200
- GET by id: 200 | 404
- GET list: 200
- DELETE: 204 | 404

# Alterações Front-End — Avaliação de Desempenho

**Data:** 2026-05-30  
**Branch:** develop

---

## 1. Novos parâmetros de filtro — `GET /avaliacao-desempenho/avaliacoes`

Foram adicionados 3 novos parâmetros opcionais de filtro à listagem de avaliações:

| Parâmetro | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `seccaoId` | `Long` | ❌ | Filtrar por secção (`RH_T_AVD.SECCAO_ID`) |
| `carreiraId` | `Long` | ❌ | Filtrar por carreira (`RH_T_AVD.CARR_PCCS_ID`) |
| `semestre` | `String` | ❌ | Filtrar por semestre (`"1"` ou `"2"`) |

### Antes
```
GET /avaliacao-desempenho/avaliacoes
    ?ano=2025
    &direcao=1
    &cargo=2
    &colaborador=<uuid|nome>
    &pageNumber=0
    &pageSize=20
```

### Depois
```
GET /avaliacao-desempenho/avaliacoes
    ?ano=2025
    &direcao=1
    &cargo=2
    &colaborador=<uuid|nome>
    &seccaoId=5
    &carreiraId=3
    &semestre=1
    &pageNumber=0
    &pageSize=20
```

> Todos os parâmetros existentes continuam a funcionar sem alteração. Os novos são completamente opcionais.

---

## 2. Novo parâmetro de filtro — `GET /avaliacao-desempenho/avaliacoes/objectivos`

Adicionado 1 novo parâmetro opcional de filtro à listagem de definição de objectivos:

| Parâmetro | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `carreiraId` | `Long` | ❌ | Filtrar por carreira (`RH_T_AVD.CARR_PCCS_ID`) |

### Antes
```
GET /avaliacao-desempenho/avaliacoes/objectivos
    ?ano=2025
    &semestre=1
    &estado=A
    &institId=1
    &cargoId=2
    &pageNumber=0
    &pageSize=20
```

### Depois
```
GET /avaliacao-desempenho/avaliacoes/objectivos
    ?ano=2025
    &semestre=1
    &estado=A
    &institId=1
    &cargoId=2
    &carreiraId=3
    &pageNumber=0
    &pageSize=20
```

---

## 3. Novos campos na resposta — Atitude Pessoal

Os objetos de Atitude Pessoal dentro das respostas de:
- `GET /avaliacao-desempenho/avaliacoes/{uuid}`
- `GET /avaliacao-desempenho/avaliacoes/objectivos/{uuid}`

passam a incluir dois campos novos (antes vinham sempre `null`):

| Campo | Tipo | Descrição |
|---|---|---|
| `numeroOrdem` | `Integer` | Número de ordem da atitude pessoal (ex: `1`, `2`) |
| `atitudePessoal` | `String` | Descrição da atitude pessoal |

> **Nota de migração:** Registos criados **antes** da migration `V3__avd_atitude_pessoal_add_columns.sql` ser executada na BD terão estes campos com fallback ao parâmetro de referência (`RH_T_PARAM_OBJETIVO`). Após a migration, os registos novos ficam com os valores guardados directamente.

---

## 4. Correcção — campo `PESO` nas Competências

O campo `peso` dos objectos `CompetenciaComportamental` e `CompetenciaTecnica` dentro da resposta do processo de avaliação (campo `RH_T_AVD_COMPETENCIA.PESO`) passa a ser correctamente preenchido ao criar a definição de objectivos:

- **Competências Comportamentais:** `peso = RH_T_PARAM_OBJETIVO_DET.PESO_COMPORTAMENTAIS`
- **Competências Técnicas:** `peso = RH_T_PARAM_OBJETIVO_DET.PESO_TECNICA`

> **Antes desta correcção:** o campo `peso` chegava sempre `null` no frontend. Agora chega com o valor percentual correcto (ex: `60.00` para 60%).

---

## 5. DB Migration necessária

Para que os pontos 3 e 4 funcionem correctamente em produção, é necessário executar a migration:

```sql
-- V3__avd_atitude_pessoal_add_columns.sql
ALTER TABLE RH_T_AVD_ATITUDE_PESSOAL ADD NUMERO_ORDEM NUMBER;
ALTER TABLE RH_T_AVD_ATITUDE_PESSOAL ADD DESCRICAO VARCHAR2(300);
```

O script encontra-se em `src/main/resources/db/migration/V3__avd_atitude_pessoal_add_columns.sql`.

---

## 6. Correcção crítica — campo `horaFim` no `ObservacaoGeralDTO`

**Endpoint afectado:** `PUT /avaliacao-desempenho/avaliacoes/processos-avaliacao/{uuid}/observacao-geral`

| Campo | Antes (errado) | Depois (correcto) |
|---|---|---|
| Hora de fim da entrevista | `"HoraFim"` (H maiúsculo) | `"horaFim"` (h minúsculo) |

Se o frontend enviava `"HoraFim"` com H maiúsculo, o campo era ignorado e ficava `null` na BD. **O campo correcto é `"horaFim"`** (camelCase standard).

```json
// ✅ Correcto
{
  "observacaoGeralAvaliacao": "...",
  "descPlanoDesenvolvimento": "...",
  "dataInicio": "2026-06-15",
  "horaInicio": "09:00",
  "horaFim": "10:30"
}
```

---

## 7. Resumo das alterações por endpoint

| Endpoint | Alteração |
|---|---|
| `GET /avaliacao-desempenho/avaliacoes` | Novos params opcionais: `seccaoId`, `carreiraId`, `semestre` |
| `GET /avaliacao-desempenho/avaliacoes/objectivos` | Novo param opcional: `carreiraId` |
| `GET /avaliacao-desempenho/avaliacoes/{uuid}` | Atitude Pessoal agora inclui `numeroOrdem` e `atitudePessoal` |
| `GET /avaliacao-desempenho/avaliacoes/objectivos/{uuid}` | Atitude Pessoal agora inclui `numeroOrdem` e `atitudePessoal` |
| `POST /avaliacao-desempenho/avaliacoes/objectivos` | Campo `peso` nas competências agora é preenchido correctamente |

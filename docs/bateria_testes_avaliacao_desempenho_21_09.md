# Bateria de testes — módulo Avaliação de Desempenho (21/09/2026)

Testes feitos **como cliente**: a consumir os endpoints pela ordem em que os ecrãs da spec
os usariam, confirmando contra as imagens em `docs/imagens de avaliacao de desempenho/`,
com casos positivos e negativos, e **verificando a BD por SQL directo depois de cada escrita**
(incluindo confirmar que os 400/409 não gravaram nada).

- **77 cenários**, **24/24 endpoints** do módulo cobertos.
- Ambiente: DEV, app em `localhost:8089`.
- Evidências em `scratchpad/bateria/` (um JSON por cenário + `_resumo.txt`).

## 1. Resultado

| | |
|---|---|
| Cenários que passaram à primeira | 75 |
| Bugs encontrados e corrigidos | 2 |
| Lacunas de contrato encontradas e corrigidas | 4 |
| Questões de negócio a decidir | 4 |

## 2. Bugs encontrados e corrigidos

### B1 — Editar parametrização com avaliações expunha um erro Oracle cru
`PUT /configuracao/avaliacao-desempenho/componentes/{id}` respondia **400** com
`ORA-02292: integrity constraint (INPSRH.FK_AVD_PARAM_OBJECTIVO) violated`.

A edição apaga e recria as linhas de `RH_T_PARAM_OBJETIVO`; assim que existam avaliações a
apontar para elas, a BD recusa. O cliente recebia uma mensagem de base de dados.

**Corrigido**: guard antes do delete, coerente com a regra já existente para inativar.
Agora **409**: *"Não é possível editar: já existem objectivos definidos para o ano 2030.
Clone a parametrização para um ano novo."*

### B2 — A escala aceitava intervalos sobrepostos
`POST /configuracao/avaliacao-desempenho/escala` com `[0–60]` e `[50–100]` respondia **200**
e substituía a escala inteira.

Com sobreposição, `resolveQualitativa` devolve o primeiro escalão que casa — **a mesma nota
podia sair com classificações diferentes** consoante a ordem de leitura.

**Corrigido**: validação de sobreposição antes de gravar. Agora **400** identificando os dois
intervalos em conflito.

## 3. Lacunas de contrato corrigidas (servir melhor o cliente)

Comparando as respostas com os ecrãs da spec:

### A1 — Cabeçalho do ecrã de avaliação não tinha os nomes
`image22.png` mostra **Direção, Unidade, Carreira, Cargo** por nome. A API devolvia só os ids:
`instituicaoNome` vinha sempre `null`, e `seccaoNome`/`cargoNome`/`carrPccsNome` estavam
**declarados como `Long`** (erro de tipo) e nunca eram preenchidos.

**Corrigido**: tipos passam a `String` e os quatro nomes são preenchidos. O padrão já existia
no projeto — o manual de funções expõe `instituicao`/`seccao`/`cargo`/`carreira`.

### A2 — Grelha de componentes só devolvia códigos
`image7.png` mostra **"Semestral"** e o badge **"Ativo"**; a API devolvia `"SEMESTRAL"` e `"A"`.

**Corrigido**: `periodicidadeDescricao` e `estadoDescricao` novos.

### A3 — Não havia como saber que períodos estão definidos
A grelha de definição e o detalhe devolviam `periodicidade: null`, porque o período vive agora
no detalhe e não na avaliação. O cliente não sabia que separadores mostrar.

**Corrigido**: `periodicidadesDefinidas` (lista) no detalhe.

### A4 — `paramId` vinha nulo, impedindo edição
Ao ler uma definição, os objectivos vinham sem `paramId` — mas a escrita exige-o. O cliente
não conseguia fazer round-trip de leitura→edição→gravação.

**Corrigido**: `paramId` preenchido na leitura.

## 4. Questões de negócio a decidir (não corrigidas)

### N1 — A escala 0–100 é incompatível com notas de 1 a 5 ⚠️
A escala parametrizada é `0–49 INSUFICIENTE`, `50–69 SUFICIENTE`, … `95–100 EXCELENTE`.
Mas as notas das componentes são 1–5, e a fórmula da spec
(`nota × ponderação da linha`, depois `× ponderação global`) devolve um valor **na mesma
ordem de grandeza das notas**.

Teste feito com **5 em todas as componentes** (o máximo possível):

| Período | Objectivos | Competências | Atitude | Final | Qualitativa |
|---|---|---|---|---|---|
| SEMESTRE1 (notas 3–5) | 1.01 | 1.88 | 0.56 | **3.45** | INSUFICIENTE |
| SEMESTRE2 (tudo a 5) | 1.25 | 2.50 | 0.63 | **4.38** | INSUFICIENTE |

**Nenhuma avaliação conseguirá alguma vez passar de INSUFICIENTE.** É preciso decidir: ou as
notas são 0–100, ou a escala passa a 0–5, ou a fórmula normaliza. O cálculo em si está correto
face à spec — verificado à mão — o problema é a incompatibilidade das duas réguas.

### N2 — Periodicidade na definição é multiselect no ecrã
`image14.png` mostra chips **"Semestre 1 ×" "Semestre 2 ×"** — definem-se vários períodos numa
gravação. A API aceita um período de cada vez. Definir os dois exige duas chamadas.

### N3 — Objectivos de Direção: várias direções numa gravação
`image14.png` tem *"+ Adicionar Direção à Lista"* e uma linha por direção. A API cria uma
avaliação de abrangência `DIRECAO` por chamada, com uma direção.

### N4 — O ecrã tem 4 estados, o modelo tem 3
`image16.png` mostra **RASCUNHO, PENDENTE, EM AVALIAÇÃO, CONCLUÍDO**. O modelo usa `A`/`P`/`C`.
Falta o mapeamento (e provavelmente um estado).

## 5. Observações menores

- Os endpoints de avaliação **não têm o prefixo `api/v1`**; os de missão têm.
- A escala devolve o wrapper em `row`; os outros endpoints paginados usam `content`.
- Nomes de campos inconsistentes entre módulos: `instituicao`/`seccao` (manual de funções)
  vs `nomeInstituicao`/`seccaoNome` (listagem) vs `institNome` (grelha de definição).
- Não há validação de que as ponderações das linhas somem 100 dentro de cada componente.
  Com 100 em cada uma das duas famílias de competências, o resultado do período chega a 6.0.

## 6. Cenários corridos

### Ecrã: Componentes de Avaliação — lista (`image7.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C1.1 | lista sem filtros | 200 | ✅ |
| C1.2 | filtro `ano=2026` | 200, 1 linha | ✅ |
| C1.3 | filtro `estado=A` | 200, 2 linhas | ✅ |
| C1.4 | filtro `estado=I` sem resultados | 200, vazio | ✅ |
| C1.5 | filtro ano inexistente | 200, vazio | ✅ |

### Ecrã: Registar Componentes (`image5.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C2.1 | sem `periodicidade` | 400 | ✅ |
| C2.2 | `periodicidade=QUINZENAL` | 400 | ✅ |
| C2.3 | ponderações ≠ 100 | 400 | ✅ |
| C2.4 | pesos ≠ 100 | 400 | ✅ |
| C2.5 | ano já parametrizado | 409 | ✅ |
| — | **BD após os 5 acima: nada gravado** | 2 linhas | ✅ |
| C2.6 | criar 2030 TRIMESTRAL | 201 + 4 linhas filhas | ✅ |

### Clonar / Inativar
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C3.1 | clonar sem ano | 400 | ✅ |
| C3.2 | clonar para ano ocupado | 409 | ✅ |
| C3.3 | clonar com periodicidade inválida | 400 | ✅ |
| C3.4 | clonar 2026 → 2031 ANUAL | 201, 10 linhas copiadas idênticas | ✅ |
| C3.5 | clonar id inexistente | 404 | ✅ |
| C3.6 | inativar 2031 (sem avaliações) | 200, cascata nas 10 linhas | ✅ |
| C3.7 | inativar já inativo | 409 | ✅ |
| C3.8 | inativar 2026 **com** avaliações | 409 | ✅ |
| — | `podeInativar` reage em tempo real | false | ✅ |

### Ecrã: Definição dos Objectivos (`image13/14/15.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C4.1 | abrangência `REGIONAL` | 400 | ✅ |
| C4.2 | `TRIMESTRE1` num ciclo SEMESTRAL | 400 | ✅ |
| C4.3 | `SEMESTRE9` inexistente | 400 | ✅ |
| C4.4 | `DIRECAO` sem `institId` | 400 | ✅ |
| C4.5 | `INDIVIDUAL` sem colaboradores | 400 | ✅ |
| C4.6 | ano sem parametrização | 404 | ✅ |
| — | **BD após os 6 acima: 0 avaliações** | 0 | ✅ |
| C4.7 | abrangência `INPS` | 200, `FUN_ID` e `INSTIT_ID` null | ✅ |
| C4.8 | abrangência `DIRECAO` | 200, `FUN_ID` null | ✅ |
| C4.9 | abrangência `INDIVIDUAL` | 200, ambos preenchidos | ✅ |
| C15.1 | com secção/cargo/carreira | 200, nomes todos devolvidos | ✅ |

### Ecrã: Lista Avaliação — pai/filho (`image16.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C5.1 | grelha com `periodos[]` e descrições | 200 | ✅ |
| C14.1–4 | filtros ano / abrangência | 200 | ✅ |

### Ecrã: Processo de Avaliação (`image22.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C6.1 | GET sem período (sem notas) | 200 | ✅ |
| C6.2 | GET com período fora do ciclo | 400 | ✅ |
| C6.3 | GET uuid inexistente | 404 | ✅ |
| C6.4 | GET uuid mal formado | 400 | ✅ |
| C6.5 | GET com `periodicidade=SEMESTRE1` | 200 + `periodo` | ✅ |
| C7.1 | gravar sem periodicidade | 400 | ✅ |
| C7.2 | gravar com período fora do ciclo | 400 | ✅ |
| C7.3 | gravar SEMESTRE1 | 200 | ✅ |
| — | **10 medições em `RH_T_AVD_PERIODICIDADE`** | polimórficas | ✅ |
| — | **cálculo conferido à mão**: 4.05×25% + 3.75×50% + 2.25×25% = 3.45 | 3.45 | ✅ |
| C7.4 | autoavaliação | 200, não toca na nota oficial nem no detalhe | ✅ |
| C7.5 | SEMESTRE2 fecha o ciclo | estado `C` | ✅ |

### Tabs do ecrã de avaliação
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C8.1 | observação geral sem periodicidade | 400 | ✅ |
| C8.2 | observação geral com período inválido | 400 | ✅ |
| C8.3 | observação geral SEMESTRE1 | 200 | ✅ |
| C8.4 | parecer colaborador | 200 | ✅ |
| C8.5 | comissão executiva | 200 | ✅ |
| — | **SEMESTRE2 ficou intocado** | isolamento por período | ✅ |

### Ecrã: Avaliação Final
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C9.1 | 2 semestres × 50% | 3.45→1.73 + 4.38→2.19 = 3.92 | ✅ |
| C10.7 | 4 trimestres × 25% | 6.00 | ✅ |

### Ciclo TRIMESTRAL (o que o refactor veio permitir)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C10.1 | definir objectivos num ciclo trimestral | 200 | ✅ |
| C10.2 | enviar `SEMESTRE1` num ciclo trimestral | 400 | ✅ |
| C10.3–5 | avaliar T1, T2, T3 | 200; estado `P` | ✅ |
| C10.6 | avaliar T4 | estado `C` | ✅ |

### Ecrã: Escala (`image12.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C12.1 | lista | 200 | ✅ |
| C12.2 | intervalos sobrepostos | 400 | ❌ → corrigido (B2) |
| C12.3 | intervalo invertido | 400 | ✅ |
| C12.4 | lista vazia | 400 | ✅ |
| C12.5 | repor escala válida | 200 | ✅ |

### Ecrã: Manual de Funções
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C13.1 | lista (com nomes resolvidos) | 200 | ✅ |
| C13.2 | sem cargo | 400 | ✅ |
| C13.3 | direção inexistente | 404 | ✅ |
| C13.4 | criar | 201 | ✅ |
| C13.5 | GET by id | 200 | ✅ |
| C13.6 | editar | 200 + BD confirmada | ✅ |
| C13.7 | editar id inexistente | 404 | ✅ |

### Outros endpoints de componentes
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| C11.1 | GET by id (detalhe para editar) | 200 | ✅ |
| C11.2 | `versao-atual` | 200, ano corrente | ✅ |
| C11.3 | GET by id inexistente | 404 | ✅ |
| C11.4 | GET by id mal formado | 400 | ✅ |
| C11.5 | editar com periodicidade inválida | 400 | ✅ |
| C11.6 | editar para ano ocupado | 409 | ✅ |
| C11.7 | editar ano com avaliações | 409 | ❌ ORA cru → corrigido (B1) |

## 7. Reteste das correções

| ID | Cenário | Antes | Depois |
|---|---|---|---|
| R11.7 | editar parametrização com avaliações | 400 `ORA-02292` | **409** com mensagem útil |
| R12.2 | escala com intervalos sobrepostos | 200 (gravava) | **400**, escala intacta |
| — | grelha de componentes | `"SEMESTRAL"` / `"A"` | + `"Semestral"` / `"Ativo"` |
| — | cabeçalho da avaliação | nomes a `null` | todos preenchidos |
| — | `paramId` na leitura | `null` | preenchido |
| — | períodos definidos | invisíveis | `periodicidadesDefinidas` |

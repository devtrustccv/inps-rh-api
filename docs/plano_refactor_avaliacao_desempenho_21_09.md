# Plano de refactor — Avaliação de Desempenho (spec 21/09/2026)

Fontes:
- `docs/Especificação Tecnica Funcional - AVALIAÇÃO DESEMPENHO_21_09_2026.md` (+ HTML navegável em `docs/spec_avaliacao_desempenho_21_09_2026.html`, imagens em `docs/imagens de avaliacao de desempenho/`)
- `docs/Especificação Tecnica Funcional - BASE DADOS_21_09_2026.md`
- Versões anteriores comparadas: spec sem data (30/05) e BASE DADOS 14/09.

## 1. O que mudou na spec

### 1.1 SEMESTRE → PERIODICIDADE (mudança estruturante)
O eixo temporal deixa de ser `1|2` e passa a ser um domínio `PERIODICIDADE`
(`RH_T_DOMINIO.REFERENCIA` onde `DOMINIO = PERIODICIDADE`). Consequências:

- `RH_T_AVD.SEMESTRE` deixa de ser o discriminador do ciclo.
- O ecrã "Avaliação Final" continua a falar de `SEMESTRE1`/`SEMESTRE2` via
  `DOMAIN = AVD_PONDERACAO_FINAL` — ou seja, semestre passa a ser **um caso
  particular** de periodicidade, usado na ponderação final.

### 1.2 Nova tabela `RH_T_AVD_PERIODICIDADE` (medições polimórficas)
Passa a concentrar os valores medidos por período, para as três componentes:

| Coluna | Tipo | Obrig. |
|---|---|---|
| ID | NUMBER | S |
| AVD_ID → RH_T_AVD.ID | NUMBER | S |
| PERIODICIDADE | VARCHAR(100) | S |
| REFERENCIA | VARCHAR(200) | S |
| REFERENCIA_ID | NUMBER | S |
| TIPO_PROCESSO | VARCHAR(100) | S |
| REALIZADO | VARCHAR(300) | |
| AVALIACAO | NUMBER | |
| AUTO_REALIZADO | VARCHAR(300) | |
| AUTO_AVALIACAO | NUMBER | |
| ESTADO + auditoria + UUID | | S |

`REFERENCIA` ∈ { `OBJECTIVO`, `COMPETENCIA_COMPORTAMENTAIS`, `COMPETENCIA_TECNICA`,
`ATITUDE_PESSOAL` }; `REFERENCIA_ID` aponta para `RH_T_AVD_OBJECTIVO.ID`,
`RH_T_AVD_COMPETENCIA.ID` ou `RH_T_AVD_ATITUDE_PESSOAL.ID`. `TIPO_PROCESSO = AVALIACAO`.

Efeito: `REALIZADO` / `AVALIACAO` / `AUTO_REALIZADO` / `AUTO_AVALIACAO` **saem** de
`RH_T_AVD_OBJECTIVO`, `RH_T_AVD_COMPETENCIA` e `RH_T_AVD_ATITUDE_PESSOAL`
(nas tabelas as colunas continuam a existir — ficam legado). A `PONDERACAO`
mantém-se na linha da componente.

### 1.3 Nova tabela `RH_T_AVD_DETALHE` (resultado por período)
Recebe tudo o que hoje está em `RH_T_AVD` a partir do cálculo:
`AVALIACAO_OBJECTIVO`, `AVALIACAO_COMPETENCIA`, `AVALIACAO_ATITUDE_PESS`,
`AVALIACAO_FINAL`, `AVALIACAO_QUALITATIVA`, `PERIODICIDADE` (obrigatório),
`OBSERVACAO_GERAL`, `DESCRICAO_PLANO`, `DATA_INICIO_ENTREVISTA`,
`HORA_INICIO_ENTREVISTA`, `HORA_FIM_ENTREVISTA`, `PARECER_COLABORADOR`,
`JUSTIFICACAO_MOTIVO`, `OBS_COMISSAO_EXEC` + estado/auditoria/UUID.

Ou seja: `RH_T_AVD` fica com a **identificação** (ano, colaborador, direção,
unidade, carreira, cargo, abrangência) e `RH_T_AVD_DETALHE` com o **resultado
de cada período**. Relação 1:N.

### 1.4 Abrangência: objectivos comuns (INSTITUIÇÃO / DIREÇÃO)
Novo ecrã "Objectivos Comuns / avaliação". O mesmo formulário serve definição
e avaliação (`Ponderação` e `Realizadas` só aparecem em avaliação). Gravação:

- Abrangência INPS: `RH_T_AVD.FUN_ID = null`, `INSTIT_ID = null`
- Abrangência DIREÇÃO: `RH_T_AVD.FUN_ID = null`, `INSTIT_ID = <direção>`
- Abrangência INDIVIDUAL: `FUN_ID = <colaborador>`, `INSTIT_ID` preenchido

`RH_T_AVD.FUN_ID` **já é nullable na BD** — é a entidade JPA que tem
`nullable = false` a mais e precisa de ser corrigida.

### 1.5 Parametrização
- Novo campo `Periodicidade` no registo de componentes →
  `RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE` (`DOMAIN = PERIODICIDADE`).
- Novo ecrã **"Lista componentes de Avaliação"**: filtros Ano/Estado, colunas
  ano, periodicidade, pesos e ponderações, e ações **criar / editar / clonar /
  inativar**. Clonar = novo registo com os dados do atual (independente do
  estado). Inativar só aparece se ainda não houver objectivo definido no ano
  em `RH_T_AVD`.

### 1.6 Listagens
- Grelha de definição de objectivos: passa a expor `Colaborador`, `Unidade`,
  `Carreira` no filtro; `Estado` muda de posição.
- Grelha de avaliação: `Semestre/Nota` passa a ler de
  `RH_T_AVD_DETALHE.SEMESTRE` / `AVALIACAO_FINAL`; grelha ganha estrutura
  **pai/filho** (pai = colaborador/ano, filho = períodos).
- Ações "Ver Avaliação Final" e "Autoavaliação" saem da grelha de definição.

## 2. Divergências spec ↔ BD — RESOLVIDAS (21/09)

A BD foi inspeccionada: as tabelas novas **já existem** (criadas pelo DBA, com
sequences `SEQ_AVD_DETALHE` / `SEQ_AVD_PERIODICIDADE` e triggers
`TRG_AVD_DETALHE` / `TRG_AVD_PERIODICIDADE`). Isso fechou D1–D6:

| # | Questão | Resolução |
|---|---|---|
| D1 | `RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE` | **Existe** na BD. Era lacuna da doc de Base de Dados, não do modelo. |
| D2 | `RH_T_AVD_DETALHE.SEMESTRE` | Não existe. É `PERIODICIDADE`. A spec funcional está errada nesse ponto. |
| D3 | `RH_T_AVD.PERIODICIDADE` | Não existe e não é preciso. `FUN_ID` **já era nullable**. |
| D4 | `RH_T_PARAM_MFUNCAO` | É `RH_T_PARAM_MANUAL_FUNC`. O nome alternativo na spec é erro de escrita. |
| D5 | Ponderação multi-período | Resolvido por dados: ver §2.2. |
| D6 | Colunas legado | Ficam nas filhas (`AVD_OBJECTIVO`, `AVD_COMPETENCIA`, `AVD_ATITUDE_PESSOAL`), sem uso. O Java deixa de as mapear. |

**F5 (backfill) foi cancelado**: `RH_T_AVD`, `RH_T_AVD_DETALHE` e
`RH_T_AVD_PERIODICIDADE` estão todas com **0 linhas**. Não há dados legado.

### 2.1 Correcções aplicadas à BD (DEV, 21/09)

SQL registado em `docs/sql/avd_refactor_periodicidade_21_09.sql`
(não há Flyway — correr à mão nos outros ambientes).

1. `ALTER TABLE RH_T_AVD ADD ABRAGENCIA VARCHAR2(100)` — a spec grava lá, a
   coluna não existia; as filhas já a tinham.
2. `ALTER TABLE RH_T_AVD DROP COLUMN SEMESTRE` — ficou órfã com a mudança de eixo.
3. `RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE`: `'2'` → `'SEMESTRAL'` nos 2 registos.
   Não se apagaram as linhas porque têm 14 objectivos filhos úteis para teste
   (o `DELETE` fica comentado no fim do SQL, caso se queira recomeçar do zero).
4. Grafia do domínio: `SEMESTRO`/`TRIMESTRO` → `SEMESTRE`/`TRIMESTRE`,
   alinhando com as referências de `AVD_PONDERACAO_FINAL`.
5. Domínio `PERIODICIDADE`: acrescentado `TRIMESTRE4` (faltava o 4º trimestre).
6. `AVD_PONDERACAO_FINAL`: acrescentados `TRIMESTRE1..4 = 25` e `ANUAL = 100`.

### 2.2 Contrato final da periodicidade

```
RH_T_DOMAINS, DOMINIO = 'PERIODICIDADE'   (hierarquia de 2 níveis)
  REFERENCIA='PERIODICIDADE' → SEMESTRAL | TRIMESTRAL | ANUAL     ← tipos
  REFERENCIA='SEMESTRAL'     → SEMESTRE1 | SEMESTRE2              ← períodos
  REFERENCIA='TRIMESTRAL'    → TRIMESTRE1 .. TRIMESTRE4
  REFERENCIA='ANUAL'         → ANUAL

RH_T_DOMAINS, DOMINIO = 'AVD_PONDERACAO_FINAL'   (peso do período no ano)
  SEMESTRE1=50  SEMESTRE2=50  TRIMESTRE1..4=25  ANUAL=100

RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE  → o TIPO     (ex.: SEMESTRAL)
RH_T_AVD_DETALHE.PERIODICIDADE         → o PERÍODO  (ex.: SEMESTRE1)
RH_T_AVD_PERIODICIDADE.PERIODICIDADE   → o PERÍODO  (ex.: SEMESTRE1)
```

A nota final do ano = Σ (`AVD_DETALHE.AVALIACAO_FINAL` × ponderação do período),
com a ponderação obtida por lookup directo da referência em `AVD_PONDERACAO_FINAL`.

### 2.3 Correcção ao desenho de DTOs

O domínio `ABRANGENCIA_AVD` usa **`INPS`**, não `INSTITUICAO`.
Enum correcto: `INPS | DIRECAO | INDIVIDUAL`.

## 3. Estado actual do código

- Entidades em `shared/infrastructure/persistence/entity/`: `AvaliacaoEntity`
  (`RH_T_AVD`, com `semestre` obrigatório e todos os campos de resultado),
  `AvaliacaoObjectivoEntity`, `AvaliacaoCompetenciaEntity`,
  `AvaliacaoAtitudePessoalEntity`, `ParamObjetivoEntity`,
  `ParamObjetivoDetEntity`, `ParamEscalaAvaliacaoEntity`, `ParamManualFuncaoEntity`.
- Módulo `avaliacao/`: 6 commands, 6 queries, 3 services, 21 DTOs,
  2 mappers, 1 controller (`avaliacao-desempenho/avaliacoes`).
- Parametrização vive em `configuracao/` (`ComponenteAvaliacaoController`,
  `EscalaAvaliacaoController`, `ManualFuncaoController`).
- **Dívida a resolver no refactor**: 6 endpoints do `AvaliacaoController`
  devolvem `Map<String, ?>` — trocar por `SuccessResponseDTO` do `shared`
  (o contrato não aparece no Swagger).

## 4. Desenho de DTOs proposto

### 4.1 Modelo mental para o cliente

```
Avaliacao (RH_T_AVD)            → identificação: ano, abrangência, colaborador?, direção?, unidade, carreira, cargo
 └─ periodos[] (AVD_DETALHE)    → 1 por período: resultados, entrevista, pareceres
     └─ componentes             → objectivos / competências (comp + téc) / atitude pessoal
         └─ medicao (AVD_PERIODICIDADE) → realizado, avaliacao, autoRealizado, autoAvaliacao
```

### 4.2 DTOs novos / alterados

**Enums** (expostos via enum exposer IGRP em `api/v1/enums`):
- `Abrangencia` — `INPS | DIRECAO | INDIVIDUAL` (valores do domínio `ABRANGENCIA_AVD`)
- `ComponenteAvaliacao` — `OBJECTIVO | COMPETENCIA_COMPORTAMENTAL | COMPETENCIA_TECNICA | ATITUDE_PESSOAL`
- `TipoProcessoAvaliacao` — `DEFINICAO | AVALIACAO`
- `Periodicidade` — vem do domínio, expor como lookup, não hardcoded

**Novos:**
- `PeriodicidadeDTO { String codigo; String descricao; }`
- `MedicaoDTO { UUID uuid; String realizado; BigDecimal avaliacao; String autoRealizado; BigDecimal autoAvaliacao; BigDecimal resultado; }`
  — `resultado` calculado (`avaliacao * ponderacao`), read-only.
- `PeriodoAvaliacaoDTO` (= `RH_T_AVD_DETALHE`):
  `{ UUID uuid; String periodicidade; BigDecimal avaliacaoObjectivo; BigDecimal avaliacaoCompetencia; BigDecimal avaliacaoAtitudePessoal; BigDecimal avaliacaoFinal; String avaliacaoQualitativa; ObservacaoGeralDTO observacaoGeral; ParecerColaboradorDTO parecerColaborador; ComissaoExecutivaDTO comissaoExecutiva; }`
- `AvaliacaoPeriodoResponseDTO` — um período completo com as componentes e as medições.
- `ComponenteAvaliacaoParamDTO` / `ComponenteAvaliacaoListItemDTO` — para a nova lista de componentes (com flags `podeInativar`, `podeClonar`).
- `ObjectivoComumDTO` — formulário partilhado definição/avaliação com abrangência INSTITUIÇÃO | DIREÇÃO.

**Alterados:**
- `AvaliacaoListagemResponseDTO`: remover `semestreNota`, `avaliacaoFinalSemestre1/2`;
  passar a `List<PeriodoResumoDTO> periodos` (pai/filho da grelha) + `notaFinal` / `notaFinalQualitativa`.
- `ObjectivoAvaliacaoDTO` / `CompetenciaComportAvaliacaoDTO` /
  `CompetenciaTecAvaliacaoDTO` / `AtitudePessoalAvaliacaoDTO`:
  os quatro campos de medição saem para `MedicaoDTO medicao`.
- `BaseAvaliacaoObjetivoDTO`: `semestre` → `periodicidade`; `abrangencia` novo;
  `funUuid` passa a opcional.
- `SemestreDTO` → `PonderacaoPeriodoDTO { String periodicidade; BigDecimal avaliacaoFinal; BigDecimal ponderacao; }`
  e `AvaliacaoFinalDTO.semestres` → `List<PonderacaoPeriodoDTO> periodos`.

Regras do projecto a respeitar: actualizar `docs/frontend_changes_avaliacao_desempenho.md`
com todas estas alterações; nunca devolver `Map`; arrays nos PUT seguem a
convenção de sync (sem id cria, omitido apaga, null preserva, `[]` limpa).

## 5. Faseamento

**F0 — Alinhamento** ✅ concluído a 21/09 (ver §2.1). Nada bloqueado.

**F1 — Parametrização (independente, pode arrancar já)**
1. `ParamObjetivoDetEntity` + `periodicidade`.
2. Lista de componentes: query com filtros ano/estado, paginada.
3. Ações clonar (deep copy do det + objectivos, `versao + 1`) e inativar
   (guard: `not exists` em `RH_T_AVD` para o ano).
4. DTOs `ComponenteAvaliacaoParamDTO` / `ComponenteAvaliacaoListItemDTO`.

**F2 — Modelo de dados**
5. Entidades `AvaliacaoPeriodicidadeEntity` e `AvaliacaoDetalheEntity`
   (com `@EntityListeners(AuditingEntityListener.class)`).
6. `AvaliacaoEntity`: `funcionario` nullable, `abrangencia`, `@OneToMany` para
   detalhes; campos de resultado marcados `@Deprecated` até ao backfill.
7. Repositórios + queries de leitura por (`avdId`, `periodicidade`) e por
   (`referencia`, `referenciaId`).

**F3 — Escrita**
8. `DefinicaoObjetivoCommandHandler`: suportar as três abrangências;
   INSTITUIÇÃO / DIREÇÃO criam 1 `RH_T_AVD` sem `FUN_ID`.
9. `AvaliacaoCommandHandler` / `AutoAvaliacaoCommandHandler`: escrever em
   `RH_T_AVD_PERIODICIDADE` em vez das colunas das componentes.
10. Cálculo do resultado do período → `RH_T_AVD_DETALHE` (objectivo,
    competência, atitude, final, qualitativa via `RH_T_PARAM_ESCALA`).
11. Observação geral / parecer colaborador / comissão executiva passam a
    escrever em `RH_T_AVD_DETALHE`.

**F4 — Leitura e contratos**
12. Grelha pai/filho de avaliação.
13. Avaliação final: ponderação por período (`AVD_PONDERACAO_FINAL`).
14. Trocar os 6 `Map<String, ?>` por `SuccessResponseDTO`.
15. Actualizar `docs/frontend_changes_avaliacao_desempenho.md`.

**F5 — Migração de dados** ❌ não se aplica: 0 linhas nas tabelas envolvidas.

**F6 — Testes**
17. Bateria live com verificação em BD por SQL directo depois de cada escrita
    (incluindo confirmar que os 400 não gravaram nada).

## 6. Ordem crítica
F1 é independente e pode arrancar já. F2 → F3 → F4 são sequenciais. Sem dados
legado, não há janela de migração a coordenar — o refactor pode ser limpo.

**Nota para staging/produção**: as tabelas novas e o
`RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE` foram criados pelo DBA só em DEV.
Antes de promover, confirmar que existem lá e correr
`docs/sql/avd_refactor_periodicidade_21_09.sql`.

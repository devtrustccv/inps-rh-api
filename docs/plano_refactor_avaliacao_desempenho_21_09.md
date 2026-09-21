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

- Abrangência INSTITUIÇÃO: `RH_T_AVD.FUN_ID = null`, `INSTIT_ID = null`
- Abrangência DIREÇÃO: `RH_T_AVD.FUN_ID = null`, `INSTIT_ID = <direção>`
- Abrangência INDIVIDUAL: `FUN_ID = <colaborador>`, `INSTIT_ID` preenchido

**`RH_T_AVD.FUN_ID` passa a ser nullable** — hoje é `nullable = false` na entidade.

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

## 2. Divergências spec ↔ BD (bloqueiam implementação)

| # | Questão | Detalhe |
|---|---|---|
| D1 | `RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE` **não existe** no DDL de 21/09 | A spec funcional grava lá. Falta DDL. |
| D2 | `RH_T_AVD_DETALHE.SEMESTRE` | A grelha lê `RH_T_AVD_DETALHE.SEMESTRE`, mas a tabela só tem `PERIODICIDADE`. Assumir `PERIODICIDADE`. |
| D3 | `RH_T_AVD.PERIODICIDADE` | A spec refere-a na autoavaliação; o DDL não mostra a coluna. Confirmar se o eixo vive só em `AVD_DETALHE` / `AVD_PERIODICIDADE`. |
| D4 | `RH_T_PARAM_MFUNCAO` vs `RH_T_PARAM_MANUAL_FUNC` | A spec usa os dois nomes para a mesma coisa. Código usa `RH_T_PARAM_MANUAL_FUNC`. |
| D5 | Ponderação final multi-período | `AVD_PONDERACAO_FINAL` só define `SEMESTRE1`/`SEMESTRE2`. Se a periodicidade for trimestral/mensal, falta a tabela de ponderações. |
| D6 | Colunas legado | Confirmar se `REALIZADO` / `AVALIACAO` / `AUTO_*` saem de `AVD_OBJECTIVO` / `AVD_COMPETENCIA` / `AVD_ATITUDE_PESSOAL` ou ficam duplicadas. |

Sem migrations Flyway no projecto — o DDL vai por SQL directo e fica no handoff/commit.

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
- `Abrangencia` — `INSTITUICAO | DIRECAO | INDIVIDUAL`
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

**F0 — Alinhamento (bloqueante)**
Levar D1–D6 ao analista/DBA. Obter DDL de `RH_T_AVD_PERIODICIDADE`,
`RH_T_AVD_DETALHE` e `PARAM_OBJETIVO_DET.PERIODICIDADE`, e os valores do
domínio `PERIODICIDADE`. Sem isto, F2+ fica bloqueado.

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

**F5 — Migração de dados**
16. SQL de backfill: `RH_T_AVD` → `RH_T_AVD_DETALHE` (1 linha por avaliação
    existente, `PERIODICIDADE = 'SEMESTRE' || SEMESTRE`), e
    `AVD_OBJECTIVO` / `AVD_COMPETENCIA` / `AVD_ATITUDE_PESSOAL` →
    `RH_T_AVD_PERIODICIDADE`. Guardar o script em `docs/sql/`.

**F6 — Testes**
17. Bateria live com verificação em BD por SQL directo depois de cada escrita
    (incluindo confirmar que os 400 não gravaram nada).

## 6. Ordem crítica
F1 é independente e pode arrancar já. F2 → F3 → F4 são sequenciais. F5 tem de
correr **antes** de F4 ir para staging, senão as grelhas aparecem vazias para
avaliações antigas.

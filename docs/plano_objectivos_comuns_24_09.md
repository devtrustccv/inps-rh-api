# Plano feat — Lista "Objectivos / Avaliação Comuns" (spec 24/09)

Base: o diff entre a spec de 21/09 e a de 24/09. Imagens em
`docs/imagens de avaliacao de desempenho 24_09/`.

## O que entrou na spec 24/09

| # | Mudança | Impacto no backend |
|---|---|---|
| 1 | Menu de 3 separadores (image13) | Nenhum: é navegação do front |
| 2 | **Lista "Objectivos Comuns / avaliação"** (image15–18) | **Feat**: este plano |
| 3 | PERIODICIDADE grava em `RH_T_AVD_PERIODICIDADE.PERIODICIDADE` (antes `.REALIZADO`, que era erro) | Nenhum: o código já grava assim na avaliação |

## A funcionalidade (texto da spec)

| | Campo | Fonte dados |
|---|---|---|
| Filtro | Ano | `RH_T_AVD.ANO` |
| Lista | Ano | `RH_T_AVD.ANO` |
| Lista | Periodicidade | `RH_T_AVD_PERIODICIDADE.PERIODICIDADE` |

| Ação | Spec | O que o front precisa |
|---|---|---|
| + Novo Objectivo | "definir Objectivo a cada ano" | Abrir o formulário de definição que já existe (`POST .../objectivos` com INPS/DIRECAO) |
| Avaliação (linha do ano) | "efetuar a avaliação em cada período no objectivo definido anualmente" | Carregar os objectivos comuns do ano e gravar a avaliação de um período |
| Ver Objectivo / Avaliação (linha do período) | "visualizar a avaliação efectuada em cada período" | Carregar os objectivos comuns do ano com as medições desse período |

Os objectivos comuns de um ano são **várias** `RH_T_AVD`: uma do INPS e uma por direção,
todas com `FUN_ID = null`. A lista é por **ano**. Por isso o front não tem, na linha, o
`uuid` que os endpoints atuais pedem. É esse o buraco que a feat fecha.

## Endpoints novos (só leitura)

Base: `avaliacao-desempenho/avaliacoes/objectivos-comuns`

### 1. `GET /objectivos-comuns?ano=&pageNumber=&pageSize=` — a lista

```json
{
  "content": [
    {
      "ano": 2025,
      "periodos": [
        { "periodicidade": "SEMESTRE1", "descricao": "Semestre 1" },
        { "periodicidade": "SEMESTRE2", "descricao": "Semestre 2" }
      ]
    }
  ],
  "pageNumber": 0, "pageSize": 20, "totalElements": 1, "totalPages": 1
}
```

- **Linha pai.** Uma por ano com `RH_T_AVD` comum (`FUN_ID IS NULL`, abrangência INPS ou
  DIRECAO, estado diferente de `E`). Ordem: ano descendente.
- **Filhos.** Os valores distintos de `RH_T_AVD_PERIODICIDADE.PERIODICIDADE` dessas
  `RH_T_AVD`, pela ordem do ciclo, com a descrição do domínio. Só há linhas nessa tabela
  quando se avalia, por isso aparecem apenas os períodos avaliados, que são os que o "Ver"
  mostra.
- **Performance.** Paginação na BD, por ano, e um único select para os períodos da página.

### 2. `GET /objectivos-comuns/{ano}?periodicidade=` — o ano, para Avaliação e Ver

```json
{
  "ano": 2025,
  "periodicidade": "SEMESTRE1",
  "instituicao": {
    "uuid": "uuid da RH_T_AVD do INPS",
    "objectivos": [ { "...": "mesma linha de GET .../avaliacoes/{uuid}" } ]
  },
  "direcoes": [
    {
      "uuid": "uuid da RH_T_AVD da direção",
      "institId": 12,
      "institNome": "Direção de Recursos Humanos",
      "objectivos": [ { "...": "idem" } ]
    }
  ]
}
```

- Cada linha de objectivo reutiliza a forma que o `GET .../avaliacoes/{uuid}` já devolve:
  numero, abrangencia, objectivo, kpi, meta, ponderacao, realizado, avaliacao e resultado.
- **Sem `?periodicidade`**: as linhas vêm sem medições. Serve para abrir a "Avaliação"
  antes de escolher o período.
- **Com `?periodicidade`**: as linhas trazem as medições desse período. Serve para o "Ver"
  e para reabrir uma avaliação.
- Erros: 400 se o período não pertencer ao ciclo do ano; 404 se o ano não tiver objectivos
  comuns.

## Notas da spec sobre o formulário (já estavam em 21/09)

> Nota: embora foi Feito 1 formulario. Mas o mesmo formulario pode server para definição de
> Ojectivo e avaliação.
> - Campo **Ponderação** e **Realizadas** so devem aparecer em avaliação
>
> PERIODIODICIDADE — "ESSE CAMPO DEVE APARECER SOMENTE NO MOMENTO DE AVALIAÇÃO" /
> "ESSE REGISTO É SOMENTE EM AVALIAÇÃO. RH_T_AVD_PERIODICIDADE.PERIODICIDADE"

| Nota | Consequência |
|---|---|
| Um formulário, dois modos | Definição → `POST .../objectivos`. Avaliação e Ver → endpoint 2 + `PUT .../processos-avaliacao/{uuid}` |
| Ponderação e Realizadas só na avaliação | É visibilidade no front. O endpoint 2 (modo avaliação) traz ambos; o POST de definição não os recebe |
| Periodicidade só na avaliação; o registo é só na avaliação | **Fix 0** (abaixo): hoje o POST exige-a na definição dos comuns e cria `RH_T_AVD_DETALHE` por período |

## Fix 0 — definição dos comuns sem periodicidade (pré-requisito do "+ Novo Objectivo")

No `POST .../objectivos` com `abrangencia = INPS | DIRECAO`:
- a `periodicidade`/`periodicidades[]` deixa de ser obrigatória e passa a ser **ignorada**;
- deixa de se criar `RH_T_AVD_DETALHE` na definição.

O registo do período nasce na avaliação: `PUT .../processos-avaliacao/{uuid}` já cria as
medições em `RH_T_AVD_PERIODICIDADE` com o período. O ramo `INDIVIDUAL` fica fora deste
plano.

## Escrita da avaliação — decidido (25/09): endpoint próprio

`PUT /objectivos-comuns/{ano}/avaliacoes/{periodicidade}`, porque:
- A gravação da spec para os comuns é **só** `RH_T_AVD_PERIODICIDADE`. O
  `PUT .../processos-avaliacao/{uuid}` também recalcula `RH_T_AVD_DETALHE` e muda
  `RH_T_AVD.ESTADO`, que são regras da avaliação individual.
- A ação é "avaliação em cada período no objectivo definido anualmente", com um único
  Guardar: uma chamada, numa transação.
- As linhas identificam-se pelo `id` de `RH_T_AVD_OBJECTIVO`, que é o `REFERENCIA_ID`.
  O `numero` repete-se entre direções.

O resto desta secção era a proposta inicial, que foi substituída.

## Escrita — proposta inicial (substituída)

- **Novo Objectivo**: `POST .../objectivos` com `abrangencia = INPS` e com `DIRECAO`, sem
  periodicidade (Fix 0).
- **Avaliação**: `PUT .../processos-avaliacao/{uuid}` com `periodicidade`, uma chamada por
  bloco. O front usa os `uuid` da resposta do endpoint 2.

**Decisão em aberto.** O ecrã tem um só botão Guardar para vários blocos, por isso ficam N
chamadas sem atomicidade entre elas. A alternativa é um
`PUT /objectivos-comuns/{ano}/avaliacoes/{periodicidade}` que grave tudo numa transação.

## Passos

0. **Fix 0**: `AvaliacaoService.definicaoObjetivos`, no ramo dos comuns.
1. **Repositório**
   - Anos com objectivos comuns, paginados.
   - Comuns de um ano.
   - Períodos distintos de `RH_T_AVD_PERIODICIDADE` para um conjunto de `avdIds`.
2. **DTOs**: `ObjectivosComunsAnoDTO` (+ wrapper), `ObjectivosComunsDetalheDTO` e
   `BlocoObjectivosComunsDTO`. Reutilizam o DTO de linha e `PeriodoResumoDTO`. Sem `Map`.
3. **Serviço**: `ObjectivosComunsReadService`, que reutiliza `AvaliacaoPeriodoService`
   (`validarPeriodo`, `tipoDoAno`, `descricoesDosPeriodos`) e o mapeamento de linhas do
   `AvaliacaoReadService`.
4. **Queries, handlers e controller**: 2 rotas GET no padrão IGRP.
5. **Changelog**: `docs/frontend_changes_avaliacao_desempenho.md`, secção
   "2026-09-24 — Lista de objectivos comuns".
6. **Testes com SQL directo**:
   - definir os comuns do ano **sem periodicidade**;
   - confirmar que não se criou `RH_T_AVD_DETALHE` nem `RH_T_AVD_PERIODICIDADE`;
   - confirmar que a lista mostra o ano **sem filhos**;
   - avaliar o SEMESTRE1 no INPS e numa direção;
   - confirmar que a lista mostra SEMESTRE1;
   - confirmar que o endpoint 2 com `?periodicidade=SEMESTRE1` bate com a BD;
   - período fora do ciclo → 400;
   - ano sem comuns → 404.

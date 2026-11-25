# Processamento Salarial — alterações de API para o front-end

**Última actualização:** 2026-11-25 · **Base dos endpoints:** sem `api/v1` (`/processamento-salarial`, `/pesquisa`,
`/fos`)

Changelog das alterações feitas ao backend do Processamento Salarial. Cada secção diz o que mudou no
contrato e o que o ecrã tem de fazer. Spec de referência: `Especificação Tecnica Funcional - PROCESSAMENTO SALARIAL_23_10_2026.md`.

---

## 1. Bug report QA (22/10/2026) — PS-006, PS-017, PS-033, PS-018c, PS-024 (23/10/2026)

### 1.1 Lista Processamento — filtro Data Início / Data Fim (PS-006)

`GET /processamento-salarial?dataInicio=…&dataFim=…`

- O filtro passa a funcionar e compara as duas datas com `RH_T_PROC_SALARIOS.DATA_DE` (spec). `DATA_DE` é o
  **último dia do mês** processado, por isso "01/10 a 31/10" devolve os processamentos de 10-2026.
- Aceita `yyyy-MM-dd`, `dd-MM-yyyy`, `dd/MM/yyyy` e data com hora (`2026-10-01T00:00:00.000Z`, o que o ecrã
  envia hoje — usa-se só a parte da data).
- Antes, uma data com hora era ignorada sem aviso e a lista vinha toda.

| Caso | Resposta |
|---|---|
| `dataFim` < `dataInicio` | 400 *"Data Fim não pode ser anterior à Data Início."* |
| Data que não se percebe | 400 *"Data inválida em 'dataInicio': …"* |

### 1.2 Marcar N/Processar — coluna NIF (PS-017)

`GET /pesquisa/colaborador` — cada linha traz o campo novo **`nif`** (número, `RH_T_FUNCIONARIOS.NIF`).

**Ação front:** mapear a coluna NIF para `nif` (hoje está a ler um campo que não existia).

### 1.3 Processar Salário — Data Fim anterior à Data Início (PS-033)

`POST /processamento-salarial/processar` com `dataFim` < `dataInicio` → **400** *"Data Fim não pode ser anterior
à Data Início."*, sem criar nada (antes criava o processamento do mês da Data Início).

### 1.4 Validar Provisório — Quantidade e Diferença (PS-018)

`GET /processamento-salarial/processar/dados-validacao` — cada linha traz os campos novos:

| Campo | Tipo | Coluna do ecrã |
|---|---|---|
| `quantidadeFuncionarios` | número | Quantidade |
| `diferenca` | número | Diferença Salarial |

Atenção à grafia: estes dois vêm em **camelCase** (é como o procedimento os devolve); os restantes continuam
em snake_case. O **Tipo Movimento** já vinha preenchido em **`tipo_movimento`** — o ecrã tem de ler essa chave.

**Pendente da BD (DBA, PS-018d):** Valor Anterior/Atual saem trocados do procedimento e, na Somatória por Tipo
Movimento (`TIPO_MOVIMENTO`), a `diferenca` vem sempre 0. Não há nada a mudar no ecrã quando o DBA corrigir.

### 1.5 Lista FOS (PS-024)

`GET /fos`

- Novo parâmetro opcional **`mesReferencia`** (filtro Mês Referência): aceita `202604`, `2026-04` ou `2026/04`.
  Mês inválido → 400 *"Mês Referência inválido: … (formato AAAAMM)."*
- Ordem passa a ser do mês mais recente para o mais antigo (spec: `ORDER BY ano||mes DESC`).
- Os totais recalculados pelo backend (ao apagar uma linha da FOS) passam a ser gravados como inteiros, como o
  procedimento faz — antes ficava `"657680.0"` e a contribuição `161131.60`.

**Ação front:**
- Mostrar **`tipoEntregaDesc`** ("Substituição") em vez de `tipoEntrega` ("SUBST").
- Formatar Total Remuneração/Contribuição com separador de milhares.
- Acrescentar o filtro Mês Referência.

A FOS 18 (abril 2026, dados de teste) continua com `"657680.0"` gravado; só muda se for regenerada.

---

## 2. Resumo Processamento — valores formatados (25/10/2026)

`POST /processamento-salarial/resumo`

Cada linha de `remuneracao[]` e de `pagamento[]` traz agora, além dos números, o texto já formatado com
separador de milhares por espaço (`NumberUtils.spaceDecimalFormat`, sem casas decimais). Os campos numéricos
mantêm-se, sem alterações.

| Lista           | Campo numérico | Campo formatado (novo) | Exemplo                   |
|-----------------|----------------|------------------------|---------------------------|
| `remuneracao[]` | `quantidade`   | `quantidadeFormatada`  | `1234` → `"1 234"`        |
| `remuneracao[]` | `total`        | `totalFormatado`       | `1234567` → `"1 234 567"` |
| `pagamento[]`   | `quantidade`   | `quantidadeFormatada`  | `12` → `"12"`             |
| `pagamento[]`   | `valor`        | `valorFormatado`       | `98765` → `"98 765"`      |

Se o valor numérico for `null`, o campo formatado também vem `null`.

**Ação front:** mostrar os campos `*Formatad*` nas colunas Quantidade e Total/Valor do Resumo; continuar a usar
os numéricos para somas ou ordenação.

### 2.1 Detalhe Processamento — valor formatado

`GET /processamento-salarial/resumo/detalhes`

Cada linha de `content[]` traz `valorFormatado` (mesmo formato: `1234567` → `"1 234 567"`), além de `valor`.

**Ação front:** mostrar `valorFormatado` na coluna Valor do Detalhe.

---

## 3. Importação de Movimentos — validar com o id da lista e decisão única (25/11/2026)

### Porquê
A lista devolvia em `movimentoId` o id numérico (`"4"`), mas o validar procura pelo uuid: validar a partir do
ecrã dava sempre `400 "Invalid UUID string: 4"`. E o movimento ficava `A` desde a importação, por isso podia ser
validado várias vezes — cada vez criava outra remuneração/desconto e outra ligação ao vínculo.

### `GET /colaborador/movimentos`

| Campo | Antes | Depois |
|---|---|---|
| `content[].movimentoId` | id numérico (`"4"`) | **uuid** (`"01a214ac-bc0b-…"`) |

O ecrã não tem de mudar nada se já reenvia o `movimentoId` da lista no path do validar.

### `POST /colaborador/movimentos/{movimentoId}/validar`
- `{movimentoId}` = o `movimentoId` da lista (uuid).
- `validacao` só aceita **`SIM`** ou **`NAO`**; `CORRIGIR` (e qualquer outro) dá `400` — antes `CORRIGIR` era tratado
  como `SIM`.
- Estados (spec, Importação Movimentos): importado fica **`P`**; `SIM` → **`A`**; `NAO` → **`I`**.
- Decidir de novo um movimento já `A` ou `I` dá `400` "Este movimento já foi validado." / "… já foi rejeitado.",
  sem gravar nada.

### Lista — nome do ficheiro e nomes dos movimentos (spec, Lista Importados)

| Campo | Antes | Depois |
|---|---|---|
| `content[].nomeFicheiro` | sempre `""` | nome do ficheiro importado (ex.: `movimentos_2026_12.xlsx`) |
| `content[].movimentoRetencaoDesc` | ❌ não existia | nome do tipo de movimento de retenção (ou `null`) |
| `content[].movimentoRemuneracaoDesc` | ❌ não existia | nome do tipo de movimento de remuneração (ou `null`) |

`movimentoRetencao` / `movimentoRemuneracao` continuam a trazer o **id** do tipo de movimento (é o que o formulário
de validação reenvia); o ecrã mostra os `…Desc`.

### `POST /colaborador/importar-movimento` — coluna A passa a ser o Nº de Colaborador

| Coluna A do Excel | Antes | Depois |
|---|---|---|
| Identificação do colaborador | UUID do funcionário | **Número de colaborador** (`RH_T_FUNCIONARIOS.ID_COLABORADOR`), como o ficheiro de ginásio/cantina traz |

Erros novos por linha (400, nada gravado): `colaborador não encontrado (número N)`,
`o número de colaborador N corresponde a K colaboradores` (o número não é único na BD),
`número de colaborador inválido (x)`. Modelo actualizado em `docs/modelos/importacao_movimentos_modelo.xlsx`.

### Fora desta alteração (spec, por implementar — precisam de colunas na BD)
`CODIGO` por ficheiro, registo em `RH_T_VALIDACAO` na importação, `NOME_FUNCIONARIO` e `TIPREL_ID` (Vínculo).
A lista também não devolve o estado.

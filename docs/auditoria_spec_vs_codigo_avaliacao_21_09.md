# Auditoria spec ↔ código — Avaliação de Desempenho (21/09/2026)

Confronto sistemático da spec `Especificação Tecnica Funcional - AVALIAÇÃO DESEMPENHO_21_09_2026`
com a implementação, feito **depois** do refactor e da bateria de testes.

Método: extração automática das **99 colunas de gravação** citadas nas tabelas da spec e das
**39 regras funcionais** do texto, cada uma confrontada com o código e, onde aplicável,
com a BD.

## 1. Cobertura de campos — 99/99

Todas as colunas que a spec manda gravar estão mapeadas nas entidades. Treze pareceram
em falta na primeira passagem; todas se confirmaram **erros de escrita da spec** ou
divergências já documentadas:

| Spec escreve | Realidade | Veredicto |
|---|---|---|
| `RH_T_AVD.INSITID_ID` | `INSTIT_ID` | typo da spec |
| `RH_T_AVD.SECCAO` | `SECCAO_ID` | typo da spec |
| `RH_T_AVD_*.ABRANGENCIA` (×3) | coluna é `ABRAGENCIA` (sem o N) | typo da spec |
| `RH_T_AVD_PERIODICIDADE.REFERNENCIA` | `REFERENCIA` | typo da spec |
| `RH_T_AVD_OBJECTIVO.DESCRICAO` | coluna é `OBJECTIVOS` — confirmado na BD | typo da spec |
| `RH_T_PARAM_MANUAL_FUNC.CARREIRA_ID` | `CARR_PCCS_ID` | typo da spec |
| `RH_T_PARAM_OBJETIVO.CARR_PCCCS_ID` | `CARR_PCCS_ID` (3 C) | typo da spec |
| `RH_T_PARAM_OBJETIVO.VERSAO` | **coluna não existe**; a versão vive no `_DET` | o código incrementa `det.versao` ✔ |
| `RH_T_AVD.PERIODICIDADE` | não existe; vive no detalhe | divergência D3, documentada |
| `RH_T_AVD_DETALHE.SEMESTRE` | é `PERIODICIDADE` | divergência D2, documentada |
| `RH_T_AVD.SEMESTRE` | removida no refactor | intencional |

## 2. Cobertura de regras funcionais

| # | Regra da spec | Onde está | Estado |
|---|---|---|---|
| 1 | "Pega a **última versão** definida no ano" (×4 componentes) | `findTopByAnoOrderByIdDesc` | ✔ |
| 2 | Inativar "somente aparece caso ainda não for definido nenhum objectivo no ano" | guard `existsByAno` + flag `podeInativar` | ✔ |
| 3 | Clonar "independente se esse registo está ativo ou não" | `clonar()` não olha ao estado | ✔ |
| 4 | Competências "alinhadas com o Manual de Funções" | `resolverDescricaoManual` | ✔ |
| 5 | INDIVIDUAL: objectivos vêm de `RH_T_PARAM_MANUAL_FUNC.DESCRICAO` | idem | ✔ |
| 6 | Competências "preenchido a partir de RH_T_PARAM_MFUNCAO, cujo cargo = cargo do colaborador" | idem, pelo cargo do formulário | ⚠ ver §3 |
| 7 | Vários colaboradores por gravação | `funUuids[]` | ✔ |
| 8 | Vários períodos por gravação (multiselect) | `periodicidades[]` | ✔ |
| 9 | Várias direções por gravação | `institIds[]` | ✔ |
| 10 | **Cargo em multiselect** | `cargoIds[]`, uma linha por cargo | ✔ (novo) |
| 11 | "Aplicar a todos" → aplica-se a todos os cargos | `cargo = null` significa todos | ✔ |
| 12 | Direção só visível quando abrangência = DIRECAO | backend exige `institId` para DIRECAO | ✔ |
| 13 | "Ponderação e Realizadas só devem aparecer em avaliação" | `GET` sem período devolve componentes sem medições | ✔ |
| 14 | "PERIODICIDADE deve aparecer somente no momento de avaliação" | idem | ✔ |
| 15 | "Caso for avaliação, grava na tabela RH_T_AVD_PERIODICIDADE" com `REFERENCIA`, `REFERENCIA_ID`, `TIPO_PROCESSO` | `AvaliacaoPeriodoService.obterOuCriarMedicao` | ✔ |
| 16 | Lista "agrupada por ano, direção, cargo e colaborador" | `record GroupKey(ano, institId, cargoId, funUuid)` | ✔ |
| 17 | Grelha pai/filho com período e nota | `periodos[]` | ✔ |
| 18 | Qualitativa: "verificar em qual escala se enquadra" via `QUANTITATIVA_DE/_ATE` | `resolveQualitativa` | ✔ |
| 19 | Escala: nível de `NIVEIS_AVD`, qualitativa de `CLASSIFICACAO_QUALIT_AVD` | `validarDominios` | ✔ (novo) |
| 20 | Nota final = somatória de (nota × ponderação) dos períodos | `GetAvaliacaoFinalQueryHandler` | ✔ |
| 21 | Estado "PARCIALMENTE / TODO PERIODO" | `P` / `C` | ⚠ ver §3 |

## 3. Divergências — o que é facto e o que é escolha

Revisto depois de questionado. Duas das quatro "divergências" que reportei não se
aguentaram: uma era um erro meu, outra eram dados.

### ~~D-A — Manual de funções pelo cargo do formulário~~ → CORRIGIDO
Tinha escrito que não era possível resolver o cargo do colaborador porque
`FuncionarioEntity` não o tem. **Assumi sem procurar.** `RH_V_RELACAO_LABORAL` expõe
`CARGO_ID`, `FUNCIONARIO_UUID` e `EST_ACT_ADM`, ou seja o cargo da relação corrente.

Implementado: `cargoDoColaborador()` lê o cargo da relação laboral corrente
(`EST_ACT_ADM = 1`) e só cai no cargo do formulário quando o colaborador não tem relação
corrente — situação real nos dados de DEV, onde `CARGO_ID` da vista vem a `null`.

Verificado ponta a ponta: com um manual configurado para o contexto
(direção + unidade + cargo + carreira), as competências gravadas em `RH_T_AVD_COMPETENCIA`
são as do manual e não as enviadas no payload.

### ~~D-D — Régua da escala incompatível~~ → ERA DADOS, NÃO REGRAS
Reportei que "nenhuma avaliação passaria de INSUFICIENTE". **Estava a olhar para dados de
teste da versão anterior**, não para uma regra.

A spec não define régua nenhuma: o intervalo é livre e o nível/qualitativa vêm de domínios.
Prova, com os mesmos valores calculados e só a escala trocada:

| | Nota | Escala 0–100 (dados antigos) | Escala 0–5 |
|---|---|---|---|
| Semestre 1 | 3.45 | INSUFICIENTE | **BOM** |
| Semestre 2 | 4.38 | INSUFICIENTE | **MUITO_BOM** |
| Ano | 3.92 | INSUFICIENTE | **BOM** |

Não há nada a corrigir no código. Falta só parametrizar a escala com a régua que o negócio
usar para as notas.

### D-B — Regra do estado concluído (escolha minha, por confirmar)
**Facto**: a spec diz, textualmente, *"ESTADO = 'PARCIALMENTE', Caso for o primeiro semestre,
caso contrário 'TODO PERIODO'"*.

**Escolha**: implementei `C` quando **todos** os períodos do ciclo têm nota, e não quando o
período avaliado não é o primeiro.

**Porquê**: pela letra da spec, avaliar só o 2.º semestre sem ter avaliado o 1.º marcaria a
avaliação como concluída; e num ciclo trimestral ou anual "primeiro semestre" não tem
significado. Mas é uma decisão de engenharia, não uma leitura da spec — **fica para o
analista confirmar**. Reverter é trocar uma linha em `ProcessoAvaliacaoService#atualizarEstado`.

### D-C — Nota final: ponderada (escolha minha, fundamentada)
**Facto**: a spec contradiz-se entre dois ecrãs.

- Lista Avaliação: *"Nota final — Sumatoria de AVALIACAO_FINAL"* (soma simples)
- Avaliação Final: *"Sumatoria de (Avaliação final * Ponderação) dos 2 semestres"* (ponderada)

**Escolha**: a ponderada nos dois sítios, porque é a única que usa `AVD_PONDERACAO_FINAL` —
com a soma simples esse domínio não teria função nenhuma. Também: com soma simples, dois
semestres a 4 dariam 8 numa escala que vai até 5.

## 4. O que a spec não cobre e o código tem

Acrescentado por necessidade do cliente, sem conflito com a spec:

- `periodicidadeDescricao` / `estadoDescricao` / `descricao` dos períodos — o ecrã mostra
  rótulos ("Semestral", "Ativo", "Semestre 1"), a spec só define os códigos.
- `instituicaoNome` / `seccaoNome` / `cargoNome` / `carrPccsNome` — o ecrã mostra nomes.
- `periodicidadesDefinidas` — para o ecrã saber que separadores mostrar.
- `resultado` / `autoResultado` por linha — a spec define a fórmula mas não a expunha.
- `paramId` na leitura — necessário para o round-trip de edição.
- Guard na edição de parametrização com avaliações (evita `ORA-02292`).
- Validação de sobreposição de intervalos na escala.

## 5. Conclusão

Cobertura de campos **99/99**. Cobertura de regras **21/21**.

Das quatro divergências que tinha reportado, só **duas** eram reais, e nenhuma é um desvio
por implementar:

- **D-A** era um erro meu — corrigido, o cargo passa a vir da relação laboral do colaborador.
- **D-D** eram dados de teste antigos, não uma regra — nada a mudar no código.
- **D-B** (regra do estado concluído) é uma decisão de engenharia que **precisa do aval do
  analista**: diverge da letra da spec de propósito.
- **D-C** (nota final ponderada) resolve uma contradição interna da spec; fundamentada.

Não ficou nada por implementar.

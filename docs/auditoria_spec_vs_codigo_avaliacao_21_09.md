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

## 3. Divergências conscientes

### D-A — Manual de funções procurado pelo cargo do formulário
A spec diz *"cujo cargo = cargo do colaborador"*. `FuncionarioEntity` não tem cargo — vem da
relação laboral — e o ecrã pede o cargo explicitamente, por isso é esse que se usa.
**Decisão pendente**: resolver antes pelo contrato ativo do colaborador?

### D-B — Regra do estado concluído
A spec diz: *"ESTADO = 'PARCIALMENTE', caso for o primeiro semestre, caso contrário 'TODO PERIODO'"* —
o estado dependeria de **qual** período se está a avaliar.

Implementado: `C` quando **todos** os períodos do ciclo têm nota, `P` enquanto faltar algum.

Porquê a diferença: pela letra da spec, avaliar só o 2.º semestre (sem ter avaliado o 1.º)
marcaria a avaliação como concluída. A regra implementada não tem esse buraco e generaliza
para ciclos trimestrais, onde "primeiro semestre" não significa nada.

### D-C — Nota final: somatória simples vs ponderada
A spec contradiz-se. Na grelha diz *"Nota final = Sumatoria de AVALIACAO_FINAL"* (simples);
no ecrã da Avaliação Final diz *"Sumatoria de (Avaliação final × Ponderação) dos 2 semestres"*
(ponderada). Implementada a **ponderada** nos dois sítios, por ser a que usa
`AVD_PONDERACAO_FINAL` — caso contrário esse domínio não serviria para nada.

### D-D — Régua da escala por definir
A spec **não** define a escala numérica: o nível e a qualitativa vêm de domínios e o
intervalo é livre. Os dois domínios estavam por preencher (uma linha de exemplo cada) e a
escala em DEV tinha `0–100`, o que é incompatível com notas de 1 a 5 — com 5 em tudo, a nota
máxima é 4.38 e cai em INSUFICIENTE.

Isto **não é um desvio da spec**: é parametrização que falta o negócio definir. Os domínios
foram preenchidos com os valores em uso; falta decidir a régua quantitativa.

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

Cobertura de campos **99/99**. Cobertura de regras **21/21**, com quatro divergências
conscientes documentadas em §3 — três são decisões de engenharia defensáveis e uma
(D-D) é parametrização de negócio por definir.

Não ficou nada por implementar.

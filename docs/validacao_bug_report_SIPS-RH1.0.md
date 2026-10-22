# Validação do Bug Report SIPS-RH 1.0 (QA, assiduidade, 13/10/2026)

Report: `Bug_Report_SIPS-RH1.0.html` (removido a 22/10 depois de fechado; recuperar com `git show bb054dd9:docs/Bug_Report_SIPS-RH1.0.html`) · validado contra `develop` @ 389202d0 em 21/10/2026.

> **Spec:** as referências a linhas da spec são da versão 09/09 (removida; ver git). A spec 20/10 que a substitui não muda nenhum destes itens — só renomeia DEF_PAGAMENTOS → DEF_REMUNERACOES na validação da falta e acrescenta a gravação da falta na licença.

**Contexto**
- Depois do teste houve dois fixes: **a4c77a30** (14/10, já em `develop` e `main`, tags 1.7.7+), com o script `scripts/assiduidade_bug_report_AS.sql` (vistas), e **3cb00caa** (16/10, `ConstraintViolationException` no commit passa a dar 400).
- A BD de dev (62.84.179.137, Oracle **11g XE**) tem os dados dos prints. É, quase de certeza, a mesma BD de QA. O script das vistas já lá foi aplicado. Não se sabe que jar está em QA.
- O a4c77a30 **não documentou** os campos novos em `frontend_changes_assiduidade.md` (`nomeColaborador`, `motivoAusencia`, `responsavelNome`, `estadoDesc`).

Legenda: ✅ resolvido · 🔧 por resolver (backend/vista) · 🖥️ frontend · 🗄️ dados · ❓ decisão de negócio

## Quadro

| Item | Parte | Estado | Causa / evidência | Fix |
|---|---|---|---|---|
| AS-001 | Justificada/Injustificada vazio | 🖥️ + ❓ | O backend envia `totalFaltasJustificadas`. O front mostra `justificadas / totalFalta`, por isso "injustificada" repete o total. A vista conta como justificada qualquer falta em A, sem olhar a `FLG_JUSTIFICATIVO`. | Expor `totalFaltasInjustificadas` próprio, depois da decisão A. |
| AS-001 | Horas Trabalhadas 00:00 | 🗄️ | `SINTESE_DIARIA.HORAS_TRABALHADAS` é null em todas as linhas (não há picagens). | — |
| AS-001 | Motivo Ausência | ✅ + ❓ | `motivoAusencia` existe (a4c77a30). Na spec 09/09 a coluna está **riscada**. | Decisão C. |
| AS-002 | 500 com linha de documento vazia | ✅ | Linha vazia ignorada: `FaltaServiceWrite:183`, `DocumentoMapper.anexoPreenchido`. | Robustez 🔧: `tipoDocumentoId:null` dá 400 por `@NotNull` em `AnexoReqDTO`; uma linha meio preenchida é descartada sem erro. |
| AS-002 | OBS "injustificada" | 🔧 vista + ❓ | `RH_V_FALTA_MENSAL.EST_MENSAL` deriva de `FLG_FALTA_DECONTO_SAL`, não de `FLG_JUSTIFICATIVO`. | Decisão A. |
| AS-003 | Filtro data dd/MM/yyyy dá 500 | ✅ | `MovimentoResumoService.lerData` (a4c77a30). | ❓ D: a vista é mensal, por isso 13–15/10 devolve o mês inteiro. |
| AS-004 | Ilha | ✅ | Subquery `ParamLocalTrab.ilhaId` (a4c77a30). | Confirmar que o front envia o id de geografia. |
| AS-004 | Estado | 🖥️ | O front usa o enum genérico (`C` "Em correção"). A vista só tem CONFORME/JUSTIFICADA/INJUSTIFICADA/PENDENTE. | Front usar `AssiduidadeDiariaEstado`. Backend dar 400 a valores inválidos (opcional). |
| AS-005 | Tipo Falta obrigatório na API | ✅ | Justificar: `resolverTipoJustificacao(...,true)` (a4c77a30). Marcar com SIM: `FaltaServiceWrite:115`. | 🗄️ O pedido 281 (Dinora) ficou sem `PARAM_SIT_ID` e sem `VALOR_DESCONTO`. |
| AS-006 | Responsável ID 36 | ✅ backend / 🖥️ | `responsavelNome` no GET (a4c77a30). | Front mostrar `responsavelNome`. |
| AS-006 | Tipo Falta vazio | 🖥️ | O backend devolve `tipoJustificacao=17` (activo, está no combo). | Corrigir o binding do select no front. |
| AS-007 | Não foi para validação | ✅ | Comportamento correcto: 1 dia e tipo sem desconto, logo fica A (RN09). Casos positivos: validações 1242 e 1245. | — |
| AS-007 | Estado "Indefinido" | 🖥️ | O DTO envia JUSTIFICADA/INJUSTIFICADA e `estadoDesc`. O front mapeia por A/P/I. | Front usar `estadoDesc`. |
| AS-007 | Desconto "Não" com 8550.48 | ✅ parcial | A vista já soma só onde há desconto. | 🔧 a vista soma `VALOR` (bruto), devia somar `VALOR_DESCONTO` (Wilson 10/26: 51302.88 vs 42752.40). |
| AS-008 | Não aparece na lista | ✅ | Vista corrigida (script 14/10). | — |
| AS-008 | OBS "injustificada" | 🔧 vista + ❓ | Mesma causa do AS-002 OBS. | Decisão A. |
| AS-009 | Nome / Hora Início-Fim | ✅ | `nomeColaborador` e `intervalFormatToHHmm` (a4c77a30). | Front ligar `nomeColaborador`. |
| AS-009 | Hora Direito "null" | 🔧 + ❓ | `DispensaReadService:152` faz `setTotalHorasDireito(null)` fixo. | Preencher via `DispensaHorasService` (decisão F). |
| AS-009 | Total Horas 240 | 🔧 | `:153` devolve minutos. | Devolver HH:mm. |
| AS-009 | Categoria vazia | 🗄️ | O tiprel 173484 tem `CARGO_ID` null (8 de 13 activos estão assim). | — |
| AS-009 | Estado "Indefinido" | 🖥️ | O backend envia `estado=P` e `estadoDesc=Pendente`. | — |
| AS-010 | Licença não grava | 🔧 | `FaltaEntity:37` tem `@NotNull pedidoId`, mas a licença não cria pedido (a coluna aceita null). O 500 vem de datas vazias sem validação. `RH_T_ABONOS_BENEFICIOS` tem 0 linhas. | Tirar o `@NotNull`. Dar 400 para datas, tipo e períodos em falta. |
| AS-010 | Combos | ✅ Tipo / 🗄️ Motivo | O Tipo foi corrigido em a4c77a30. Os tipos 40 e 41 não têm motivos em `PARAM_SITUACAO_DET`. | — |
| AS-010 | Fluxo seguinte (bloqueado) | 🔧 | `ColaboradorController:241/256`: a rota declara `{pedidoId}` mas o parâmetro lê `@PathVariable("abonoId")`, o que dá 500 em toda a validação. A validação não grava `FUN_ID`/`TIPREL_ID`. `validar()` não muda os estados. | Corrigir os três. Actualizar `frontend_changes_baixa_medica.md`. |
| AS-011 | Hora Extra sem nome | ✅ | `FUN_ID`/`TIPREL_ID` na validação, com backfill (a4c77a30). | ❓ Pedido com vários colaboradores: hoje mostra só o 1.º. |
| AS-013 | Total Faltas não soma | 🔧 vista + ❓ | `total_faltas` conta só os dias por registar (redefinição de 11/09; a spec diz SUM(FALTA)). As faltas rejeitadas (I) desaparecem de todos os contadores. | Decisão A/B. |
| AS-014 | Linha vazia ao abrir | 🖥️ | Estado inicial do front. A API já é robusta. | — |
| AS-015 | CORRIGIR → `despacho_rh` longo | 🔧 | A coluna é `VARCHAR2(3)` e recebe `"CORRIGIR"` (`JustificarFaltaWriteService:358`, `FaltaServiceWrite:275`). Mesmo sem o erro, CORRIGIR seria uma rejeição silenciosa (o ciclo está em standby desde 10/09). `validar=null` também cai em I. | 400 para CORRIGIR e para null nos dois validar. Front esconder "Corrigir". |
| AS-016 | Estado "Injustificada" | 🔧 vista + ❓ | Mesma causa do AS-002 OBS. | Decisão A. |
| AS-016 | Motivo vazio | 🔧 | Não existe na vista nem no `FaltaListDTO`. | `LISTAGG` de `ps.NOME` na vista, mapear na entidade e no DTO. |
| AS-017 | "PEND" | 🔧 | Código cru da vista (`DEF_REM_ID IS NULL`). | Pendente/Processado e "Não aplicável" sem desconto. ❓ Estado parcial (`MAX`). |
| AS-018 | Desconto Sim, valor 0 | 🗄️ + ❓ | O saldo **não** cobriu nada. `GET_SALARIO_BASE` dá 0: faltas anteriores ao início do vínculo (Nicolau, João); tiprel 173474 sem remuneração SAL (Nilsson). Grava 0 sem avisar. | Decisão B. |

## Decisões de negócio pendentes

- **A. Justificada/Injustificada.** Hoje deriva do flag de desconto no salário. Devia derivar de `FLG_JUSTIFICATIVO`, ou a lista devia seguir a spec (estado da falta, por falta) e não o agregado mensal? Aplica-se a AS-001, 002, 008, 013 e 016.
- **B. Total Faltas.** Seguir a spec (todas as faltas) ou manter "por registar"? As rejeitadas contam como injustificadas? As pendentes entram no total?
- **C. Motivo Ausência.** A coluna está riscada na spec: manter ou remover?
- **D. Filtro de datas.** Ao dia, como diz a spec, ou ao mês, como faz a vista agora?
- **E. Salário 0 (AS-018).** Bloquear ou avisar quando a falta é anterior ao vínculo ou quando o salário base é 0? Usar `tiprel.SALARIO` como fallback?
- **F. Hora Direito (dispensa).** É o direito mensal ou a soma do que já foi usado?
- **G. Licença Paternidade/Maternidade** com `FLG_FALTA_DECONTO_SAL=1`: confirmar. Este flag decide a RN09 e o estado.

## Fixes de backend que não dependem de decisão

1. AS-015: guard 400 para CORRIGIR e para `validar` null em `validarFaltaJustificada` e `FaltaServiceWrite.validarFalta`.
2. AS-010: `@NotNull pedidoId`; validação de datas, tipo e períodos; `{abonoId}` na rota; `FUN_ID`/`TIPREL_ID` na validação. `validar()` deve mudar os estados: confirmar com a spec, l.1429-1690.
3. AS-009: `totalHorasSolicitadas` em HH:mm.
4. AS-017: descrição do estado de processamento.
5. AS-016: coluna Motivo (vista, entidade e DTO).
6. AS-007/018: a vista deve somar `VALOR_DESCONTO`, não `VALOR`.
7. AS-002: anexo meio preenchido dá 400 e `tipoDocumentoId:null` passa a ser tratado como 0.
8. Documentar em `frontend_changes_assiduidade.md` os campos do a4c77a30 e os novos.

## Estado dos fixes (branch `fix/assiduidade-bug-report-qa`, 21/10/2026)

| Fix | Estado | Smoke test (como cliente da API) |
|---|---|---|
| AS-015 — CORRIGIR dá 400 nos 5 validar (falta, justificar, dispensa, hora extra, férias); `validar` null no justificar dá 400 | ✅ feito | 5× `400` *"A opção 'Corrigir' ainda não está disponível…"*; null → `400` *"Campo validar é obrigatório"*; BD igual antes/depois |
| AS-009 — `totalHorasSolicitadas` em HH:mm a partir de `TOTAL_HORA` | ✅ feito | `GET dispensa` → `"04:00"`, `intervaloHoras` `"00:00 / 04:00"`, `nomeColaborador` preenchido |
| AS-016/017/007/018 — `motivo`, `estadoProcessamentoDesc`, Valor a Descontar = líquido | ✅ feito · vista aplicada em dev/QA 21/10 12:08 | `GET falta` 200, 10 linhas: `motivo` preenchido, Wilson 10/26 = 42752.40 (= SUM(VALOR_DESCONTO) na BD; bruto 59853.36), Amadou "Não aplicável"; filtros `estado=JUSTIFICADA` (1) e datas `dd/MM/yyyy` (5) ok. `GET movimento-resumos` com o pedido exacto do QA (AS-003) 200, `ilha=12387` 3, `estado=JUSTIFICADA` 8 |
| Documentação para o frontend | ✅ `frontend_changes_assiduidade.md` §29 | — |
| AS-010 Licença | ⏸ em espera (pedido do utilizador) — patch no scratchpad da sessão | — |
| AS-002 anexo meio preenchido → 400 (só assiduidade: `DocumentoMapper.anexoPreenchidoOuErro`/`validarAnexos`) | ✅ feito | `POST falta`: tipo sem ficheiro → 400, ficheiro sem tipo → 400, BD igual; linha vazia do QA → 200 (falta 988 / pedido 327, Rui, 03/11, 0 docs). Outros módulos: por decidir |
| Ciclo CORRIGIR como no dossiê (falta, justificar, hora extra, férias; dispensa fica 400) | ✅ feito | Bateria `scratchpad/bugreport_qa/corrigir/` (cliente da API + BD): A 39 · B 30 · C 25 · D 29 · E 17 verificações, todas verdes; `mvn test` 55/55 |
| AS-009 Hora Direito = SUM(TOTAL_HORA) P+A do mês (decisão F, opção a, 21/10) | ✅ feito | `GET dispensa` → `"04:00"` = 240 min na BD (só há 1 dispensa: exclusão de I/E sem caso para testar) |

## Decisão A — porque é que as duas listas não concordam no estado (21/10/2026)

As duas vistas calculam "Justificada/Injustificada" com regras diferentes, e **nenhuma lê o campo que
diz se a falta foi justificada**: `RH_T_FALTA.FLG_JUSTIFICATIVO` (SIM/NAO, o radio "Com Justificativo?").

| Lista | Vista | Regra hoje | O que a spec diz |
|---|---|---|---|
| Gestão Falta (`movimento-resumos`) | `RH_V_RESUMO_ASSIDUIDADE` | INJUSTIFICADA = dia com falta na síntese **sem** `RH_T_FALTA`; PENDENTE = falta em P; JUSTIFICADA = falta em **A** (qualquer). Ou seja, "justificada" = "registada e aprovada" | Estado = `SINTESE_DIARIA.ESTADO` (conforme / injustificada / justificada / pendente) — sem regra |
| Falta Justificada/Injustificada (`falta`) | `RH_V_FALTA_MENSAL` | INJUSTIFICADA se **alguma** falta do mês tem tipo com `FLG_FALTA_DECONTO_SAL = 1` (desconta no salário); só vê faltas em A | Estado = `RH_T_FALTA.ESTADO` |

Evidência (BD, faltas em A/P por mês):

| Colaborador / mês | Faltas | `falta` mostra | `movimento-resumos` mostra | Pelo `FLG_JUSTIFICATIVO` seria |
|---|---|---|---|---|
| Wilson 10/2026 | 10 A, **todas SIM** (9 com tipo que desconta) | Injustificada | Justificada | **Justificada** |
| Nilsson 10/2026 | 2 A, **ambas NAO** | Injustificada (por acaso: o tipo desconta) | Justificada | **Injustificada** |
| Nilsson 08/2026 | 7 A NAO + 4 A SIM | Injustificada | Justificada | misto |
| Amadou 09/2026 | 7 A SIM (tipo sem desconto) + 5 P NAO + 18 por registar | Justificada (não vê as P) | Injustificada | misto |

Ou seja: desconto no salário ≠ injustificada (uma licença de paternidade justificada desconta), e
aprovada ≠ justificada (marcar falta com "Com Justificativo? = NÃO" também fica em A).

**Proposta (a confirmar com o analista):** uma só regra, nas duas vistas, por falta:
- **Justificada** = `RH_T_FALTA` em A com `FLG_JUSTIFICATIVO = 'SIM'`
- **Injustificada** = dia de falta sem `RH_T_FALTA`, ou `RH_T_FALTA` em A com `FLG_JUSTIFICATIVO = 'NAO'`, ou rejeitada (I)
- **Pendente** = `RH_T_FALTA` em P
- Mensal: Injustificada se houver alguma injustificada; senão Pendente se houver pendente; senão Justificada; senão Conforme.

Os totais (`TOT_JUS`/`TOT_INJ`, `total_faltas_justificadas`, Total Faltas — AS-001/AS-013) passam a
contar pela mesma regra, e `justificadas + injustificadas + pendentes = total`. O desconto no salário
continua a ser só a coluna "Desconto na Remuneração?".

### Decisão A — implementada (21/10/2026)

Regra decidida: **Justificada** = falta aprovada com `FLG_JUSTIFICATIVO = 'SIM'`; **Injustificada** =
aprovada com `'NAO'` ou dia de falta sem registo vivo (nunca justificado, ou **rejeitado** — o registo
rejeitado não conta e o dia volta a "por registar"); **Pendente** = P ou C. **Total Faltas** = todas as
faltas do mês = J + I + P. Aplicada nas duas vistas (`assiduidade_decisao_a_*.sql`) e no rótulo por dia
do painel. Encontrado e corrigido no caminho: o painel escondia para sempre os dias rejeitados (a
escrita deixava re-justificar) e podia prender o dia re-justificado ao pedido rejeitado; a vista contava
a dobrar dias com duas faltas/ausências.

Bateria `f_decisao_a_mar.sh` (mês limpo, o dia passa por todas as classes): **49/49**; regressão
`g_regressao_listas.sh` 15/15; invariante J+I+P = total em **todas** as linhas da BD; `mvn test` 55/55.
Itens resolvidos: AS-001 (Justificada/Injustificada), AS-002 OBS, AS-008 OBS, AS-013, AS-016 (estado).

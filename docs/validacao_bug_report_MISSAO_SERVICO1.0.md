# Validação do Bug Report SIPS-RH Missão de Serviço 1.0 (QA, 14/10/2026)

Report: [Bug_Report_SIPS-RH_MISSAO_SERVICO1.0.html](Bug_Report_SIPS-RH_MISSAO_SERVICO1.0.html) · validado contra
`develop` @ 1220c39a em 22/10/2026 (código, BD de dev — que é a de QA, só leitura — e GET à API).

**Contexto**
- Depois do teste houve 2 commits no módulo: **568695b7** (16/10: período na lista, valorTotal do
  alojamento, guard de missão finalizada na submissão, linha de anexo vazia ignorada, etapaDesc) e
  **0e25f614** (17/10: parametrização da ajuda de custo). E **3cb00caa** (16/10): violação de constraint
  no commit passa a 400 em vez de 500. Nenhum está em `frontend_changes_missao_servico.md`.
- O .docx tem um bloco repetido (MS-023 … MS-021). São **25 itens distintos**; faltam MS-003/004/009/011/014.
  A introdução cita "cabimento gerado sem número" (alta) sem item próprio — tratado abaixo como **MS-014?**.

Legenda: ✅ resolvido · 🔧 por resolver (backend) · 🖥️ frontend · 🗄️ dados/DBA · ⚙️ ambiente · ❓ decisão

## Quadro

| Item | Parte | Estado | Causa / evidência | Fix |
|---|---|---|---|---|
| MS-001 | Sem paginação | 🖥️ | API pagina: `totalElements:22, totalPages:3, pageSize:10` (`MissaoServicoServiceRead:184-190`) | Ligar o paginador a `pageNumber/pageSize/totalPages` |
| MS-002 | Período | ✅ backend · 🖥️ | `dataInicio`/`dataFim` na lista (568695b7) | Coluna "Período Missão" = início – fim, sempre |
| MS-006 | Etapa em código | 🖥️ | Lista já devolve `etapaDesc`; Aprovação RH `parecerUgal.parecerDesc:"Favorável"` | Usar `etapaDesc` / `parecerDesc` |
| MS-005 | Autorizado por | 🖥️ + ❓ | `autorizadoPor` é texto livre obrigatório (`MissaoServicoServiceWrite:336`); não há GET do utilizador logado; QA corre sem JWT (`anonymousUser`) | Front pré-preenche com o nome da sessão + `*`. ❓ spec diz SELECT sem dizer de quê |
| MS-007 | Validação/mensagens | 🖥️ + 🔧 | Mensagens com nome JSON (`paisDestinoId é obrigatório` …, `:316-345`); pára no 1.º erro; "Too small…" é Zod do front | Front valida País/Âmbito/datas + minDate. Backend: mensagens legíveis |
| MS-008 | Linha de anexo vazia → 400 | ✅ (568695b7) · 🖥️ | `syncDocumentos` ignora a linha vazia; GET devolve `documentos:[]` | Front não cria a linha por defeito |
| MS-010 | 500 ao gravar 2.ª vez | ✅ provável (568695b7 + 3cb00caa) · 🖥️ | Mesma linha de anexo vazia, a rebentar no commit → 500. Hoje ignorada, e qualquer violação dá 400. Lateral: sem recarregar, o front reenvia anexos sem `id` → duplica (doc 544 E / 545 P na 14/2026) | Retestar. Front faz GET depois de gravar |
| MS-012 | Emails "Erro"/"Pendente" | ⚙️ + 🗄️ | Nunca houve `Enviado` na BD (35 Erro c/ email, 39 Pendente s/ email). `OracleEmailService` chama `SIPSV0.SEND_MAIL_V1`, que o INPSRH não vê (sem grant). Colaboradores de teste só têm telemóvel | DBA: grant/sinónimo `SEND_MAIL_V1`. Dados: emails. 🔧 opcional: gravar motivo do erro; ❓ reenvio |
| MS-013 | Valor diário da parametrização | ✅ (0e25f614) · 🖥️ | GET logística devolve `valorDiarioAjudaCusto` (15/2026 = 3500; 19/2026 internacional = 12000) | Front pré-preenche com `valorDiarioAjudaCusto` |
| MS-013 | Colaborador volta a "Seleciona…" | 🖥️ | GET devolve `colaborador{funcionarioUuid, missaoColabUuid}`; o front compara `id` de tabelas diferentes | Emparelhar por `funcionarioUuid`. 🔧 opcional: `colaboradorId` plano na resposta |
| MS-013 | Base vazia ao reabrir | 🔧 | Só se grava o valor efectivo (base × fracção), não há coluna da base | Devolver `valorDiarioBase` + `flgAlimentacao` por linha |
| MS-013 | Com alimentação (⅓) | ❓ | Alimentação vem da linha de ALOJAMENTO do colaborador; sem ela aplica ⅔ sem aviso | Mostrar a fracção usada; decidir se a ajuda aceita alimentação explícita |
| MS-013 | Cargo superior para todos | ❓ | `ParamAjudaCustoService:162-186` dá a todos o valor da carreira mais alta; spec diz "varia consoante a função" | Decisão |
| MS-013b | Valor diário 0 aceite | 🔧 novo | Ajuda de custo (`:710` só testa null) e alojamento (`:651`). 19/2026 avançou com ajudas a 0 | 400 quando ≤ 0 |
| MS-014? | Cabimento sem número | ❓ integração + 🔧 | SGAL continua stub. **Novo:** desde 14/10 todas as linhas cabimentadas têm `CAB_ID = 0` (front manda 0, `:1253` grava qualquer ≠ null) | Backend: `cabId ≤ 0` = vazio. 🗄️ limpar os 0. ❓ contrato SGAL |
| MS-015 | Tipo Serviço em código | 🔧 | `ProcessoCabimentoItemResponseDTO` só tem `referencia` (`MissaoProcessoServiceRead:652`) | `referenciaDesc` por linha |
| MS-015 | Valor "50,000" | 🖥️ | API: `50000` + `moeda:"CVE"` | Formatar pt-PT |
| MS-016 | Diálogo de cancelar | 🖥️ + 🔧 | Front: `nrMissao` vs `nrMissaoFormatado`, data ISO, título, Zod. **Backend:** detalhe devolve `etapaDesc:"SUBMISSAO"` (`MissaoServicoServiceRead:152`, enum não tem SUBMISSAO; lista usa o processo mais atrasado) | Detalhe com a mesma regra da lista |
| MS-017 | valorTotal 0 | ✅ (568695b7) · 🗄️ | Agora 0/vazio → diário × dias. Linha antiga 290 (17/2026) ficou 0 | 🗄️ corrigir linha 290 |
| MS-017 | Datas vazias | 🖥️ (parece já corrigido) | GET devolve `dataInicioMissao/dataFimMissao`; no print de 21/10 já vêm | Confirmar com o QA |
| MS-018 | Um dia a menos | 🖥️ | API envia `"2026-10-14"` (LocalDate) nos 3 sítios; o front faz `new Date()` → UTC → 13/10 em Cabo Verde | Tratar `yyyy-MM-dd` como data local |
| MS-019 | Finalizada editável | ✅ parcial · 🔧 · 🖥️ | Submissão recusa FINALIZADO (568695b7). **Mas** as escritas por processo só recusam `I` (`MissaoProcessoSupport:74,197`) → prestadores/requisição/logística/pareceres/cabimento gravam numa missão paga. **E** o PUT/POST da submissão aceita `estado` do cliente (`MissaoServicoServiceWrite:101,151`) | Recusar FINALIZADO por processo; ignorar `dto.estado`. Front: só consulta. ❓ avaliações/pagamento depois de finalizar |
| MS-020 | Sem campo Estado | 🖥️ | API aceita `estado` A/I (prestador 16 já está I) | Front mostra o select |
| MS-020 | Designação "A" | 🔧 (pequeno) | `PrestadorAvaliacaoResponseDTO` sem `designacaoDesc` | `designacaoDesc` do domínio |
| MS-020 | Data ISO | 🖥️ | `LocalDate` | Formatar |
| MS-021 | 404 parametrização | 🖥️ | Backend em `api/v1/missao-servico/configuracao-ajuda-custo` (200 local); front chama sem `/api/v1`. Tabela existe | Corrigir o URL. Documentar endpoint |
| MS-022 | Tipos de outros módulos | 🖥️ + 🗄️ | Endpoint filtra por `?referencia=`; front chama sem filtro; não há tipos com `REFERENCIA='MISSAO_SERVICO'` | DBA cria os tipos; front filtra. 🔧 opcional: validar a referência |
| MS-023 | Email adicional | ❓ | Spec não o pede. API ignora a selecção de emails do ecrã e envia a todos os activos | Front: emails só leitura. ❓ email livre |
| MS-024 | Documentos "preenchidos" | 🖥️ | São documentos reais (anexo do registo com tipo "Documento Financeiro"; PDF da requisição); ecrã mostra o tipo em vez do ficheiro | Mostrar `documento` |
| MS-025 | Voltar à Aprovação RH | 🖥️ (guard certo) | Parecer exige a etapa exacta; reemitir duplicaria. GET da etapa já permite só consulta. Doc contradiz-se (`frontend_changes:907-910`) | Front em só consulta. 🔧 opcional `editavel`; corrigir doc |
| MS-026 | Responsável em código | 🔧 (pequeno) | `ParecerResponseDTO` sem `responsavelDesc` | `responsavelDesc` |
| MS-027 | Alojamento 1 colaborador | 🖥️ | API aceita `colaboradorIds` (mesmo prestador) | Multiselect |
| MS-028 | Colaborador perde-se | 🖥️ | GET traz os dados; pedido usa `colaboradorId(s)`, resposta `colaborador{funcionarioUuid}` | Reidratar do GET. 🔧 opcional: ids planos |
| MS-029 | Nome opcional | 🔧 + 🖥️ + 🗄️ | `PrestadorServicoServiceWrite:132` grava o nome opcional (invenção nossa; spec: nome = entidade). 7/10 prestadores divergem | Gravar sempre o nome da entidade; front tira o campo; UPDATE |
| MS-030 | X desloca-se | 🖥️ | CSS | — |

## Decisões pendentes

- **D1 — MS-019:** depois de FINALIZADO continuam permitidas as avaliações de prestadores e a correcção do pagamento?
- **D2 — MS-013:** ajuda de custo com o valor da carreira mais alta para todos, ou por colaborador ("varia consoante a função")?
- **D3 — MS-013:** "com alimentação" na Ajuda de Custo sem alojamento?
- **D4 — MS-005:** "Autorizado por" texto livre ou SELECT (de quê)?
- **D5 — MS-023:** email livre no pedido de proposta?
- **D6 — MS-014?:** contrato do SGAL para o número de cabimento (endpoint, payload, onde vem o número).
- **D7 — MS-012:** reenvio de notificações em Erro/Pendente?
- **D8 — MS-029:** nome = sempre o da entidade (spec) ou manter nome comercial?

## Fixes de backend sem decisão

1. MS-019: recusar escrita por processo em missão FINALIZADO; ignorar `estado` vindo do cliente na submissão.
2. MS-013b: valor diário ≤ 0 → 400 (ajuda de custo e alojamento).
3. MS-014?: `cabId ≤ 0` tratado como vazio.
4. MS-016: `etapaDesc` do detalhe pela mesma regra da lista.
5. Descrições em falta: `referenciaDesc` (MS-015), `responsavelDesc` (MS-026), `designacaoDesc` (MS-020), `valorDiarioBase`/`flgAlimentacao` na ajuda de custo (MS-013).
6. MS-007: mensagens de validação legíveis.
7. Documentar em `frontend_changes_missao_servico.md` o que mudou desde 16/09 (568695b7, 0e25f614) e o novo.

## Do DBA / dados

- Grant/sinónimo `SIPSV0.SEND_MAIL_V1` para o INPSRH (MS-012).
- Tipos de documento com `REFERENCIA='MISSAO_SERVICO'` (MS-022).
- Limpar `CAB_ID = 0`; linha 290 do alojamento com valorTotal 0; nomes dos prestadores (MS-029, se D8 = entidade).

## Estado dos fixes (branch `fix/missao-bug-report-qa`, 22/10/2026)

Decisões D1–D8 tomadas com o utilizador a 22/10 (todas pela recomendação): D1 avaliações e pagamento
continuam depois de finalizar · D2 ajuda de custo por colaborador · D3 sem alimentação explícita, mostrar
a fracção · D4 "Autorizado por" texto livre · D5 sem email livre · D6 SGAL pendente · D7 reenvio fora ·
D8 nome = entidade.

| Fix | Estado | Smoke test (cliente da API + BD) |
|---|---|---|
| MS-019 finalizada só de consulta (8 escritas por processo + submissão; guard antes da validação) | ✅ | 7/2026: 8× 400 "finalizada", logística e pareceres intactos na BD; avaliações e pagamento continuam |
| MS-019 `estado` do cliente ignorado | ✅ | criar com `estado:"I"` → A; editar com `"FINALIZADO"` → A |
| MS-007 mensagens legíveis | ✅ | 3 casos (vazio, datas invertidas, sem âmbito) |
| D2 ajuda de custo por colaborador | ✅ | Wilson 3500 (ignora 9999 do ecrã), Amadou 2000 × ⅔ = 1333,33, Rui sem parametrização → valor do ecrã; GET por colaborador; processo `null` com carreiras diferentes |
| MS-013 `valorDiarioBase` / `fracaoAplicada` / `flgAlimentacao` | ✅ | ⅔ sem alojamento, ⅓ depois do alojamento com alimentação (666,67 → base 2000, teste unitário `BaseDaAjudaCustoTest`) |
| MS-013b valor 0 → 400 (ajuda de custo e alojamento) | ✅ | 400 sem gravar nada |
| MS-014? `cabId` 0 = sem número | ✅ + 🗄️ | cabimento NEXT com `cabId:0` → CAB_ID null; 9 linhas antigas limpas (`scripts/missao_bug_report_MS.sql` §1) |
| MS-017 linha 290 com total 0 | 🗄️ ✅ | 30000 (§2 do script) |
| MS-015 `referenciaDesc` · MS-026 `responsavelDesc` · MS-020 `designacaoDesc` · MS-016 `etapaDesc` | ✅ | lidos na missão de teste e nas existentes |
| MS-029 nome = entidade | ✅ código · ⏸ dados | editar com nome livre → grava "Agecabo". O UPDATE dos nomes existentes (§3) fica por correr: renomeia prestadores de teste (TAP → "SINTAP …") |
| MS-025 guard | inalterado (correcto) | 400 confirmado; doc corrigida |
| Documentação | ✅ | `frontend_changes_missao_servico.md` §1 "Alterações de 2026-10-22" |

Bateria `scratchpad/bugreport_qa/missao/m_missao.sh`: **65/65** (3.ª corrida; as duas anteriores
revelaram 1 defeito real — base 2000,01 — e expectativas minhas). Regressão GET de todos os ecrãs em
3 missões: 68/68 + 1 expectativa minha. `mvn test` 60/60. Missões de teste 23/2026, 24/2026 e 25/2026
criadas e **canceladas** no fim.

**Do DBA / analista:** grant `SIPSV0.SEND_MAIL_V1` (MS-012), tipos de documento `MISSAO_SERVICO`
(MS-022), contrato SGAL (D6), UPDATE dos nomes dos prestadores (§3), ajudas 291/292 a 0 da 19/2026
(regravar pelo RH).

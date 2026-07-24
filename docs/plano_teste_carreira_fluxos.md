# Bateria de Testes — Carreira (roteamento por `tipoCarreira`)

Valida a implementação alinhada com **DOSSIÊ 24_07** + **caso de uso 13_07**. Router: `tipoCarreira` (VALOR) → `REFERENCIA` no domínio `TIPO_MOV_LABORAL` → contexto.

## Mapa de valores (domínio `rh_t_domains`)
| VALOR (`tipoCarreira`) | REFERENCIA (contexto) | Rota |
|---|---|---|
| `CARGO_NOVO`, `MUDANCA_CARREIRA` | `CARREIRA_NOVO` | POST `/funcionarios/{fun}/carreiras` |
| `NOVO_CONTRATO`, `REPOSICIONAMENTO_PCCS` | `CARREIRA_EDITAR` | PUT `/funcionarios/{fun}/carreiras/{carreiraId}` |
| `PROGRESSAO`, `PROMOCAO` | `CARREIRA_PROG_PROMO` | PUT `/funcionarios/{fun}/carreiras/{carreiraId}` |

Validação: `POST /funcionarios/{fun}/carreiras/{carreiraId}/validar` `{ "validacao":"SIM|NAO" }`.
Detalhe: `GET /funcionarios/carreiras/{carreiraId}`.

**Invariante verificado em TODOS:** exatamente 1 tiprel `est_act_adm=1`; nunca 2 carreiras com `flg_processa=1`; ≥1 com `flg_processa=1`.

## Pré-flight (BD) — verificado
- Domínio `TIPO_MOV_LABORAL`: 6 valores, `estado=A`, REFERENCIA correta (ver mapa acima). ✅
- Colunas: `RH_T_CARREIRA(EST_ACT_ADM, FLG_PROCESSA)`, `RH_T_TIPOS_RELACIONAMENTO(EST_ACT_ADM, FLG_PROCESSA, ULT_PROC)`. ✅
- "Processada" (fluxos **E**) usa o **mesmo critério da vista** `RH_V_CARREIRA.PROCESSAMENTO`: existe registo em `RH_T_PROC_FUNCIONARIOS` para um tiprel desta carreira (`existsByTiprel_CarreiraId_Id`). ✅ (já não usa `ULT_PROC`).

## Simular navegação de ecrã (obrigatório)
Cada cenário reproduz o **percurso real do utilizador no ecrã**, não um request sintético. **Começa SEMPRE pela LISTA** (é assim que o utilizador chega ao formulário):

0. **Entrar na LISTA** → `GET /funcionarios/carreiras?idFuncionario=<uuid>&pageNumber=0&pageSize=20`. Confirma o ponto de partida (quantas carreiras, estados A/P/I, qual processa) e que a lista **carrega** (o ecrã abre).
1. **Abrir o formulário a partir da lista**:
   - **Novo** → botão Novo (form vazio, dropdowns do domínio/cargos/carreiras/categorias/escalões).
   - **Editar / Progressão / Promoção** → escolher **uma linha da lista** e abrir o detalhe → `GET /funcionarios/carreiras/{id}` (pré-preenche o form).
2. **Agir** → `POST`/`PUT` com o **payload REAL do frontend** (inclui `tipoCarreira`; `flgProcessa` vem como **string** `"1"`/`"0"`; subsídios com `id` quando existentes; encargos com `dataInicio`/`dataFim`).
3. **Voltar à LISTA** → `GET .../carreiras?...` e confirmar que reflete a ação (novo P aparece; estados mudaram) — **além** da verificação por BD.
4. Se for fluxo de validação → **Validar** (`POST .../validar`) e re-verificar lista + BD.

### Percursos por botão do ecrã (Relação Laboral → Carreira)
| Botão no ecrã | GET (abrir) | Ação | `tipoCarreira` enviado |
|---|---|---|---|
| **Novo** | lista `/carreiras?idFuncionario=` | `POST /carreiras` | `CARGO_NOVO` ou `MUDANCA_CARREIRA` |
| **Editar** | `GET /carreiras/{id}` | `PUT /carreiras/{id}` | `NOVO_CONTRATO` ou `REPOSICIONAMENTO_PCCS` |
| **Progressão/Promoção** | `GET /carreiras/{id}` | `PUT /carreiras/{id}` | `PROGRESSAO` ou `PROMOCAO` |
| **Validar** | detalhe do pendente | `POST /carreiras/{id}/validar` | — |
| **Eliminar** | — | `DELETE /carreiras/{id}` | — |

### Template do payload REAL (como o frontend envia)
```json
{ "tipoCarreira":"<VALOR>", "carreiraId":1, "categoriaId":2, "cargoPosicaoId":1,
  "escalaoReferenciaId":7, "salario":232352, "moeda":"CVE", "flgProcessa":"1",
  "dataInicio":"2026-07-24", "dataFim":"2027-07-23",
  "subsidios":[{"id":1264,"tipoSubsidioId":1682,"percentagem":0,"valor":232352,"observacoes":""}],
  "encargosDescontos":[{"id":1373,"tipoEncargoId":1680,"valor":0,"dataInicio":"2026-07-24","dataFim":"2027-07-23","observacoes":""}] }
```
Na validação: `{ "validacao":"SIM" }` (o `dados` é opcional/eco).

## Pré-condições
- App a correr; `dataInicio ≥ hoje` e dentro do contrato; `flgProcessa` sempre enviado (string).
- Colaborador **limpo** (1 tiprel `est_act_adm=1`, carreira A, `n_pend=0`), escolhido no momento.
- Antes de cada cenário: **GET** que o ecrã faria, para confirmar o ponto de partida (e provar que os dropdowns/dados existem na BD).

---

## A. Router / validação de rota
- **A1** POST `/carreiras` com `tipoCarreira=PROGRESSAO` → **400** ("use o PUT").
- **A2** POST `/carreiras` com `tipoCarreira` inexistente no domínio → **400** ("sem referência").
- **A3** POST com `tipoCarreira=CARGO_NOVO` → **200** (segue NOVO).
- **A4** Em qualquer criação/progressão, `tipo_situacao` (carreira + tiprel) guarda o **VALOR** enviado.

## B. NOVO (`CARGO_NOVO` / `MUDANCA_CARREIRA`) — POST
Colaborador com **1 carreira CARGO** activa.
- **B1 Acumular tipo diferente:** registar carreira **CATEGORIA** (`cargoPosicaoId=null`), `flgProcessa=0` → 200 (pendente). Validar SIM → **2 activas** (CARGO atual processa; CATEGORIA A, `est_act_adm=0`, `flg=0`, **sem data_fim**). 1 `est_act_adm=1`.
- **B2 Rejeitar mesmo tipo:** registar carreira **CARGO** (mesmo tipo da existente) → **409** ("mesmo tipo… use Progressão/Promoção").
- **B3 Máx 2:** com 2 activas (CARGO+CATEGORIA), registar 3ª de tipo novo → **409** ("mais de duas").
- **B4 flgProcessa obrigatório:** registar sem `flgProcessa` → **400**.
- **B5 flgProcessa=1 na nova:** registar CATEGORIA `flgProcessa=1`; validar SIM → a **nova passa a processar** (est=1/flg=1), a CARGO anterior **despromovida** (flg=0/est=0, sem data_fim). Nunca 2 a processar.
- **B6 Pendente por validar:** com um pendente P, registar outro → **409** ("registo por validar").

## C. PROGRESSÃO / PROMOÇÃO (`PROGRESSAO` / `PROMOCAO`) — PUT `{carreiraId}`
Colaborador com carreira CARGO activa (a fonte).
- **C1 Progressão:** PUT `{carreiraFonteId}` com `tipoCarreira=PROGRESSAO`, escalão diferente, `flgProcessa=1` → cria **pendente** (contentor) sobre a fonte, **herda os def** da fonte (sem subsídios no DTO) + salário do novo escalão.
- **C2 getById pendente:** escalão/salário novos; subsídios herdados da fonte.
- **C3 Validar SIM:** a fonte **fecha** (`est_act_adm=0`, `data_fim`, def `I`); a nova é o atual (est=1, flg=1). 1 `est_act_adm=1`.
- **C4 PROMOCAO:** idem com `tipoCarreira=PROMOCAO`; `tipo_situacao` fica `PROMOCAO`.
- **C5 Compõe com mobilidade:** registar mobilidade + progressão; validar em ambas as ordens → `carr=novo, mob=novo` (composição das partilhadas).

## D. EDITAR não processada (`NOVO_CONTRATO` / `REPOSICIONAMENTO_PCCS`) — PUT `{carreiraId}`
Carreira **validada** (A) **sem** processamento (`RH_V_CARREIRA.PROCESSAMENTO=0`).
- **D1 Só Data Fim/observações:** PUT muda `dataFim` (sem mudar cargo/carreira/escalão) → **UPDATE in place**, carreira fica **A** (não vai a validação).
- **D2 Muda escalão:** PUT muda `escalaoReferenciaId` → carreira volta a **P** (revalida) + validação UPDATE criada.
- **D3 Muda carreira (carrPccs) ou cargo:** → volta a **P** (revalida).
- **D4 Editar pendente (P):** PUT numa carreira já P → actualiza, mantém **P** (não cria nova validação).
- **D5 Subsídios por id:** PUT com subsídios com `id` → actualiza os existentes; sem `id` → cria novo P.

## E. EDITAR processada (`CARREIRA_EDITAR`) — PUT `{carreiraId}` (imediato)
Carreira **com** processamento (`PROCESSAMENTO>0`).
- **E1 Muda escalão:** → **progressão** (novo INSERT carreira+tiprel+rem_pag), vai a validação.
- **E2 flg 0→1 (marcar processar):** carreira parqueada (flg=0) → PUT `flgProcessa=1` (sem mudar escalão) → **imediato**: novo tiprel (clona o atual, `carreira_id`=esta, est=1/flg=1) + copia os def **desta** carreira + o atual anterior **despromovido** (est=0/flg=0). 1 `est_act_adm=1`.
- **E3 flg 1→0 (desmarcar):** carreira que processa → PUT `flgProcessa=0` + `dataFim` → **imediato**: `flg=0`, `data_fim` na carreira, tiprel `est_act_adm=0`+`data_fim`. **Requer** outra a processar.
- **E4 flg 1→0 sem Data Fim:** → **400** ("Data Fim obrigatória").
- **E5 flg 1→0 sendo a única a processar:** → **400** ("pelo menos uma tem de processar").
- **E6 Só Data Fim:** PUT muda só `dataFim` → UPDATE `RH_T_CARREIRA` (fecha), sem erro.

## F. Rejeição / Eliminar
- **F1 Validar NÃO** (NOVO/PROG): carreira + contentor + def (pela associação) → `I`; atual intacto.
- **F2 Eliminar** (só P): carreira → `E` + tiprel/def → `E`.

## G. Invariante `flg_processa` (transversal)
- **G1** Após qualquer fluxo, contar `flg_processa=1` nas activas → sempre **1**.
- **G2** Após qualquer fluxo, `est_act_adm=1` → sempre **1** e coincide com a que tem `flg=1`.

---

## Queries de verificação (BD)
```sql
-- atual único + estado das carreiras
SELECT id, estado, est_act_adm, carreira_id, mob_id, flg_processa, referente, tipo_situacao, data_fim
FROM RH_T_TIPOS_RELACIONAMENTO WHERE fun_id=? ORDER BY id;

SELECT id, estado, est_act_adm, cargo_id, escalao_id, flg_processa, tipo_situacao, data_fim
FROM RH_T_CARREIRA WHERE contr_vinculo_id IN (SELECT id FROM RH_T_CONTRATO_VINCULO WHERE fun_id=?);

-- invariante: deve devolver 1
SELECT COUNT(*) FROM RH_T_TIPOS_RELACIONAMENTO WHERE fun_id=? AND est_act_adm=1;
SELECT COUNT(*) FROM RH_T_CARREIRA c JOIN RH_T_CONTRATO_VINCULO ct ON ct.id=c.contr_vinculo_id
  WHERE ct.fun_id=? AND c.estado='A' AND c.flg_processa=1;
```

**Ordem sugerida:** A → B → C → D → E → F → G. Prioridade ao que mudou nesta ronda: **A (router)**, **B2/B3 (guards NOVO)**, **C (progressão via PUT)**, **E (flips na processada)**.

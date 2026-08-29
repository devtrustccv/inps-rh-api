> Updated: 2026-08-29 (sessão Opus)

## Goal

Implementar o "Processar" do JOB Alerta (doc TRANSVERSAL 3.4.2) para todos os tipos de alerta e completar a geração de alertas em falta. A renovação (grupo/individual) já está feita; falta o resto.

## Current state

**FEITO e commitado** (`5632df70`, branch `develop`): Processar renovação em lote.
- `POST /api/v1/funcionarios/renovacao-contrato/lote` — atómico, erros agregados (`List<String>` em `ProblemDetail.details`), lote de 1 = individual.
- `RenovarLoteReqDTO` { itens:[{ funcionarioId, contratoId, alertaId(opc), dadosRenovacao }] } + `RenovarLoteItemReqDTO` (+ manifestos `.igrpstudio`).
- `ProcessarRenovacaoLoteCommand`/Handler; endpoint no `ContratoController` (manifesto sincronizado).
- `RenovacaoContratoService` refatorado: `validarRenovacao`(guards)/`aplicarRenovacao`(escrita) + `processarRenovacaoLote`. Individual `renovarContrato` inalterado no comportamento.
- Alerta: maker marca `flg_tratamento='S'`; checker (`ValidacaoRenovacaoContratoService.marcarAlerta`) fecha `estado='I'` no SIM ou repõe `'N'` no NÃO, via `AlertaEntityRepository.findFirstByReferenciaIdAndTipoAlertaOrderByIdDesc`.
- Doc: `docs/frontend_changes_transversal.md`. **Compila** com JDK 23. **Não testado live.**

**Auditoria completa do doc TRANSVERSAL vs código** — o resto do doc está implementado (Documentos/Ordem Serviço, Relatórios `RelatorioController`, Pedido Declaração `DeclaracaoController`, Notificação manual `NotificacaoController`+`NotificacaoDispatchService`+`OracleEmailService`). **Só o JOB Alerta tem buracos.**

## O que falta — separado como pediste

### Parte "também tem a ver com Dossiê" (Processar abre forms do Dossiê de negócio, módulo `funcionario`)
1. **Processar CONVERSÃO** (`CONVERSAO_CONTRATO`): abre **Novo Contrato PRÉ-PREENCHIDO** (mudança 29_08) → reutilizar `POST .../{id}/contratos` + marcar alerta (mesmo padrão da renovação). ← próximo passo.
2. **Processar LICENÇA s/ vencimento** (`LICENCA_S_VENCIMENTO`): abre editar/registo licença (Proc. Salarial 3.4) + marcar alerta.
3. **Processar LICENÇA c/ vencimento** (`LICENCA_C_VENCIMENTO`): abre abonos/benefícios (Proc. Salarial 3.3.1) + marcar alerta.

### Parte "só transversal" (não toca no Dossiê — vive no JOB `AlertaWriteService`)
4. **Gerar alertas em falta** no `AlertaWriteService` (hoje gera só 3): Doença/`LICENCA_C_VENCIMENTO`, Empréstimo pagamento atrasado, Empréstimo cessado c/ dívida.
5. **Reconciliação `P→I`** do alerta de empréstimo quando a dívida é resolvida (doc: linhas ~1224/1257).
6. **JOB → notificação/email automático** (`flg_notificacao`): ligar `AlertaWriteService` ao `NotificacaoDispatchService` (destinatários COLABORADOR/RESPONSAVEL_COLABORADOR — resolvem SEM login). **NÃO está bloqueado** — só falta fazer a ligação (o JOB nunca chama o dispatch nem põe `flg_notificacao='S'`). Ver nota corrigida em Blockers.
7. Missão Serviço: doc diz *"Pendente: verificar se ainda faz sentido"* → **não fazer** sem decisão de negócio.

## Decisions made — do not re-litigate

- **`flg_tratamento` estende a spec**: o doc só define `estado` P→I. Adotámos `flg_tratamento` (S ao processar, N se rejeitado) para a janela maker-checker. Grelha "por tratar" = `estado='P' AND flg_tratamento='N'`. Ver `docs/frontend_changes_transversal.md` §2.
- **Lote atómico** (não parcial): valida todos, se ≥1 erro rollback total + todos os erros. Confirmado pelo utilizador.
- **`alertaId` é contexto, não dado**: fica no item do lote, nunca no `RenovacaoContratoDTO`/`validar`. Checker localiza alerta por `referencia_id`(=contrato.id).
- **Datas form-driven** (Opção A): backend não calcula dataFim; ver [[project_renovacao_datas_form_driven]].
- **Endpoints via skill `igrp-spring-generator`** + manter `.igrpstudio/**.json` em sincronia.

## Constraints

- JDK 23 obrigatório: `export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-23.0.2.7-hotspot"`.
- Não regenerar controllers via skill (destrói handlers custom) — acrescentar action ao manifesto + método à mão, como se fez no `ContratoController`.
- Replicar a marcação do alerta nos fluxos dos outros módulos (conversão→Novo Contrato; licenças→Processamento Salarial), ou criar endpoint fino "marcar alerta tratado" chamado após gravação.

## Blockers & risks

- **JOB→notificação (item 6) — CORRIGIDO: não está bloqueado.** A infra (dispatch+resolver+`OracleEmailService`) funciona; só falta o `AlertaWriteService` chamar o `NotificacaoDispatchService` e pôr `flg_notificacao='S'`. Destinatários COLABORADOR/RESPONSAVEL_COLABORADOR resolvem sem utilizador autenticado (só RESPONSAVEL_REGISTO precisa de login, e esse não se aplica a um JOB). **Limitação SEPARADA** (não bloqueia o envio): `AuditEntityListener` grava `createdById=1L/2L` hardcoded ([AuditEntityListener.java:31,35]) — a autoria/`USER_REGISTO_ID` não é o utilizador real; relaciona-se com o padrão IAM por integrar. Ver [[project_notificacao_destinatarios]] e [[reference_iam_user_profile_pattern]].
- **Nada testado live** — só compila. Precisa de BD Oracle + alertas gerados pelo JOB.
- `contratoId` do item é informativo: `validarRenovacao` usa o contrato ATUAL do tiprel do `funcionarioId` (igual ao endpoint individual). Se o negócio exigir validar que bate certo, acrescentar guard.

## Relevant files

- `src/main/java/cv/inps/rh/funcionario/application/service/RenovacaoContratoService.java` — padrão validar/aplicar/lote/marcarAlertaTratado a replicar para conversão/licenças.
- `src/main/java/cv/inps/rh/funcionario/application/service/ValidacaoRenovacaoContratoService.java:190` — `marcarAlerta` (fecha I / repõe N).
- `src/main/java/cv/inps/rh/shared/domain/service/AlertaWriteService.java` — JOB `@Scheduled`; adicionar geradores (itens 4–5) e ligação a notificação (item 6).
- `src/main/java/cv/inps/rh/shared/domain/service/NotificacaoDispatchService.java` — envio+registo+email já pronto para reutilizar no JOB.
- `src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/AlertaEntityRepository.java` — `findFirstByReferenciaIdAndTipoAlertaOrderByIdDesc`.
- `docs/Especificação Tecnica Funcional - TRANSVERSAL_29_08_26.md` — 3.4.2 (Processar, 1349), tipos de alerta JOB (1090–1293).

## How to verify / resume

```
cd "c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE"
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-23.0.2.7-hotspot"
mvn -q -DskipTests compile          # deve compilar sem erros
git log --oneline -1                  # 5632df70 feat(alerta): processar renovacao ... em lote
```
Run local: `mvn spring-boot:run` (perfil development, porta 8089). Swagger em `/swagger-ui.html`.

## Test / validation plan (renovação em lote — ainda por validar live)

1. **Setup**: ter ≥2 colaboradores com contrato renovável ativo (`tp_contrato.flg_renovavel=1`), próximos do fim, e correr o JOB (ou inserir alertas `RENOVACAO_CONTRATO` estado P/flg_tratamento N em `RH_T_ALERTA` com `referencia_id`=id do contrato). Guardar os `alertaId`, `funcionarioId`(uuid), `contratoId`(uuid).
2. **Ação (sucesso)**: `POST /api/v1/funcionarios/renovacao-contrato/lote` com `itens:[{funcionarioId, contratoId, alertaId, dadosRenovacao:{dataInicio, dataFim, duracaoMeses}}]` para 2 colaboradores válidos.
   - **Esperado**: 200, mensagem "…2 colaborador(es) enviados para validação"; cada funcionário com tiprel novo estado P + validação P; `RH_T_ALERTA.flg_tratamento='S'` nos 2. **Evidência**: HTTP 200 + `SELECT flg_tratamento,estado FROM rh_t_alerta WHERE id IN (...)`.
3. **Ação (erro agregado/atómico)**: lote com 1 válido + 1 inválido (ex.: contrato não renovável, ou já com renovação pendente).
   - **Esperado**: 400, `details` com o erro do inválido; **NENHUM** dos dois renovado (rollback); alertas mantêm `flg_tratamento='N'`. **Evidência**: HTTP 400 body + confirmar na BD que o válido NÃO criou tiprel novo.
4. **Checker SIM**: `POST /api/v1/funcionarios/{id}/validar-renovacao-contrato/{contratoId}` com `validacao=SIM` para um renovado.
   - **Esperado**: contrato atualizado; `RH_T_ALERTA.estado='I'`. **Evidência**: SELECT do alerta.
5. **Checker NÃO** (outro): `validacao=NAO`.
   - **Esperado**: `RH_T_ALERTA.flg_tratamento` volta a `'N'` (reaparece na grelha). **Evidência**: SELECT.
6. Registar tudo em scratchpad (ver [[feedback_scratchpad_referencia_viva]]), imprimir resposta crua ([[feedback_mostrar_resultado_sempre]]).

## Open questions

- Marcação do alerta nos outros Processar: hook dentro de cada service de destino vs endpoint fino "marcar tratado"? Decidir ao implementar conversão (item 1).
- Conversão "pré-preenchido": que campos o backend devolve para pré-popular o Novo Contrato? Confirmar contra Dossiê 3.5.2.5.1.

## Next step

Implementar **Processar CONVERSÃO** (item 1): reutilizar o Novo Contrato pré-preenchido + marcar o alerta `CONVERSAO_CONTRATO`, espelhando o padrão maker-checker da renovação.

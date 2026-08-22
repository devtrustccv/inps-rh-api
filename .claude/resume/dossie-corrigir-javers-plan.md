> Plano de execução — CORRIGIR + JaVers nos services restantes do dossiê
> Branch: `feat/dossie-corrigir-javers-restantes`  ·  Criado: 2026-08-22

## Objetivo (mandato do utilizador)

Entregar **ciclo CORRIGIR (maker-checker por estado)** E **JaVers (auto-audit)** implementados e
testados live nos 7 pontos NO-OP restantes. Fases à minha escolha desde que:
- Tudo num branch próprio; ao verde pedir autorização para merge com `develop` (após verificação do user).
- A CADA service: compilar + handoff `dossieFix`, depois passar ao próximo.
- Testar live no fim: **leitura antes de escrita**; **arrays reenviados com o seu `id`**; **colaborador
  novo tem de ser validado ANTES de ação nos filhos** (1ª vez); obs/inconveniências → escrever em md.

## Lista (7 pontos / 6 services)

| # | Service | Ref | Decisão | Âncora | Convenção |
|---|---------|-----|---------|--------|-----------|
| 1 | ValidarDadosBancariosService | DADOS_BANCARIOS | UPDATE | funcionario.uuid | enum EstadoValidacao |
| 2 | AlterarSituacaoLaboralWriteService | ESTADO_COLABORADOR | UPDATE | situacaoLaboral.uuid | enum |
| 3 | ValidacaoRenovacaoContratoService | RENOVACAO_CONTRATO | UPDATE | contrato.uuid | enum |
| 4 | SubstituicaoWriteService | SUBSTITUICAO | INSERT | substituicao.uuid | enum |
| 5 | RenumeracoesWriteService.validarNovoRemuneracao | RENDIMENTO | INSERT | remuneracao.uuid | String (ValidationUtil) |
| 6 | RenumeracoesWriteService.validarNovoPagamento | DESCONTO | INSERT | pagamento.uuid | String |
| 7 | ProcessoDisciplinarWriteService.updateProcessoDisciplinar | PROCESSO_DISCIPLINAR | INSERT | process.uuid* | String; estado=String |

\* ProcessoDisciplinar: validação NÃO grava `referencia_uuid` no registo → tratado inline (funId-based),
  e adiciono `setReferenciaUuid(process.getUuid())` no registo para consistência/JaVers.

## FASE 1 — CORRIGIR (padrão "edita-no-validar", validar=null reenvia C→P)

Padrões consolidados a replicar:
- **funcionario-centric** (ValidarContratoService): CORRIGIR → `funcionarioRules.devolverParaCorrecao(refUuid, estado, Ref)` + entidade→C + save. Reenvio (validar=null + entidade C) → aplica edições + `reabrirParaValidacao` + entidade→P. Guard: validar!=null com entidade C → 400.
- **entity-uuid** (MobilidadeWriteService): CORRIGIR no validar() põe entidade+validação em C; reenvio aplica edições + C→P.

- [x] #1 DadosBancarios — CORRIGIR feito, compile EXIT=0
- [x] #2 SituacaoLaboral — CORRIGIR feito, compile EXIT=0
- [x] #3 RenovacaoContrato — CORRIGIR feito (+2 helpers no ContratoHistoricoWriteService), compile EXIT=0
- [x] #4 Substituicao — CORRIGIR feito, compile EXIT=0
- [x] #5 Rendimento — CORRIGIR feito, compile EXIT=0
- [x] #6 Desconto — CORRIGIR feito, compile EXIT=0
- [x] #7 ProcessoDisciplinar — CORRIGIR feito (+referenciaUuid no registo), compile EXIT=0

**FASE 1 COMPLETA — CORRIGIR nos 7. Compila. Falta commit + Fase 2 (JaVers) + Fase 3 (teste live).**

## FASE 2 — JaVers auto-audit (5 entidades limpas; DadosBancarios+Renovacao DEFERIDAS)

Decisão do user: ligar as 5 sem anchor mismatch agora; DadosBancarios (colecção, refId=funcionario) e
Renovacao (histórico vs contrato.id) ficam para 3ª passagem.

Feito: `JaversAuditConfig` refs rasas (Banco/ParamSituacao/ParamSituacaoDetalhe/TiposRelacionamento/
TipoMovimento) + 5 repos anotados `@JaversSpringDataAuditable` + 5 descriptors + carimbo
`ValidacaoAuditContext` no save da correção (baseline já vem do registo via repo anotado).

- [x] SituacaoLaboral (ESTADO_COLABORADOR) — JaVers ligado, compile EXIT=0
- [x] Substituicao — JaVers ligado, compile EXIT=0
- [x] Rendimento (RH_T_DEF_REMUNERACOES) — JaVers ligado, compile EXIT=0
- [x] Desconto (RH_T_DEF_PAGAMENTOS) — JaVers ligado, compile EXIT=0
- [x] ProcessoDisciplinar — JaVers ligado, compile EXIT=0
- [ ] (DEFERIDO) DadosBancarios JaVers — anchor refId=funcionario
- [ ] (DEFERIDO) Renovacao JaVers — anchor histórico vs contrato

**FASE 2 COMPLETA (5). Compila com JDK23.** NB: forçar `JAVA_HOME=/c/Program Files/Eclipse Adoptium/jdk-23.0.2.7-hotspot` no mvn.

## FASE 3 — Teste live (porta 8089, JDK23)

- [ ] Colaborador de teste ATIVO (registar → validar SIM) — reutilizar existente se já ativo.
- [ ] Por service: GET (ler) → ação de escrita → GET (confirmar). Ciclo CORRIGIR: registar/pendente →
      CORRIGIR (P→C) → reenviar (C→P) → SIM (A). Arrays sempre com `id`.
- [ ] Confirmar grelha JaVers (detalhe de alterações) onde aplicável.
- [ ] Ao verde: pedir autorização para merge com develop.

## Notas
- Obs/inconveniências em `.claude/resume/dossie-corrigir-javers-obs.md`.
- Compile rápido: `mvn -q -DskipTests compile`.

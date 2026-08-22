> Updated: 2026-08-22 09:48

## Goal

Replicar o ciclo **CORRIGIR** (maker-checker por estado, P→C→P→A) e a grelha **JaVers**
("Detalhe de alterações") a todos os serviços do dossiê.

## Current state

- **MERGEADO em `develop`** (merge commit `fdc5c298`; branch de trabalho já apagado; push feito pelo user).
- **CORRIGIR** em 7 services: DadosBancarios, SituacaoLaboral, RenovacaoContrato, Substituicao,
  Rendimento, Desconto, ProcessoDisciplinar. Padrão "edita-no-validar" (validar=null reenvia C→P).
- **Grelha JaVers** ativa em 9 referências (as 7 + Mobilidade/Carreira anteriores). DadosBancarios e
  Renovacao ligados via `matchByTypeOnly` (anchor mismatch coleção/histórico).
- **Extras**: motivo da substituição corrigível; colaborador substituído mostrado por NOME.
- **Fixes**: `ValidationUtil.isCorrigir(null)` NPE; lista de mobilidade reflete estado workflow P/C.
- Tudo provado LIVE contra Oracle dev (colaborador 958897). Detalhes/mutações em
  `.claude/resume/dossie-corrigir-javers-obs.md` e `-plan.md`.

## Decisions made — do not re-litigate

- CORRIGIR: forma "edita-no-validar"; CORRIGIR exige payload completo (bean validation).
- `matchByTypeOnly` no ValidacaoDetalheDescriptor resolve anchor mismatch (não mexer em referenciaId).
- Vista `RH_V_MOBILIDADE` calcula estado por datas (A/I) — leitura sobrepõe com estado da tabela p/ P/C.

## Constraints

- Arranque: JDK23 + carregar `.env` SEM `source` (password tem `$$`). Segurança OFF em dev.
- SQL directo: usar `DbExec` (ver `reference_db_helpers`). NÃO limpar colaboradores de teste.

## Open questions

- Outras listas que leem de vistas (ex.: Carreira) podem ter o mesmo sintoma da mobilidade (vista
  ignora workflow) — verificar se o frontend reportar.

## Next step

Aguardar feedback do frontend; se reportar o mesmo em Carreira/outra lista, aplicar o mesmo fix de leitura.

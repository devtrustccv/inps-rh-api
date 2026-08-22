> Updated: 2026-08-22 12:20

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

## Sessão 2026-08-22 (pós-merge) — sintoma estado-C + direcaoAntesId

Varredura de TODAS as listas que leem de vistas quanto ao sintoma "estado C invisível":
- **Carreira** (RH_V_CARREIRA): vista colapsa C→'A' (CASE ELSE). Aplicado overlay P/C em
  `CarreiraReadService.list` (lê estado real da tabela por carreira_id) — igual à mobilidade.
- **Contrato** (RH_V_CONTRATO): expõe `d.estado` cru (emite C), mas o filtro era só (A,P,I) → renovação
  em correção desaparecia. Fix: **+Estado.C no filtro** de `ContratoReadService.listaContratos` (sem overlay).
- **Já corretos (sem alteração)**: Mobilidade (já tinha overlay P/C) e Dossiê/RH_V_DOSSIE (filtro já
  inclui A,P,C,I + estado cru).
- **N/A**: DadosBancarios, Substituicao, Rendimento, Desconto, ProcessoDisciplinar leem de TABELAS
  (estado real), não de vistas. HistoricoLaboral aplica direto (sem maker-checker).

Campo novo **`direcaoAntesId` (Long)** no `MobilidadeDTO` (só leitura) + mapper (2 métodos) + manifesto
`.igrpstudio/funcionario/dto/MobilidadeDTO.json`. NB: `DirecaoEntity` NÃO tem uuid → identificador é o
Long id (coerente com direcaoDepois e os selects). Pedido do user "por id, por agora".

Doc frontend nova: `docs/frontend_changes_funcionario.md`.

### Teste live (colaborador 958897, app em código novo na porta 8088)
- ✅ direcaoAntesId=100010075 no GET mobilidade 660.
- ✅ Contrato: renovação fresca (contrato 698, v4/hist 242) → CORRIGIR → lista mostra v4 estado C.
- ✅ Carreira: progressão (carreira 756) → CORRIGIR → VISTA diz 'A', TABELA diz 'C', LISTA (overlay) diz 'C'.
- Dados de teste criados via REST (deixados em C, coerentes): renovação 698 (tiprel 173330/hist 242) +
  carreira 756. NÃO limpar. SQL directo bloqueado pelo classificador → tudo testado via REST real.

## Open questions

- (resolvida a varredura) Nenhuma outra lista com o sintoma; todas as que leem de vista foram cobertas.

## Next step

MERGEADO em develop (merge `337dc3f1`, commit `26bda6c7`; branch feat/dossie-listas-estado-c apagado).
Merge é LOCAL — falta `git push origin develop` (aguardar decisão do user). Artefactos de teste
(DbExec*/DbQuery*/app*.log/scratchpad/) continuam untracked — candidatos a .gitignore.

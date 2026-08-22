# Observações / inconveniências — CORRIGIR + JaVers restantes

> Branch: `feat/dossie-corrigir-javers-restantes`. Registo corrido de decisões, armadilhas e pendências
> encontradas durante a execução. O utilizador lê depois.

## Decisões de âmbito
- JaVers pode vir DEPOIS do CORRIGIR (fases separadas), mas AMBOS têm de ser entregues e testados.
- Regime, Pedido de Declaração e Relação Laboral (HistoricoLaboral) ficam FORA: não têm caminho de
  validação maker-checker (aplicam direto). Confirmado no código.

## Registo por service

### #1 ValidarDadosBancariosService — CORRIGIR ✔ (compile OK)
- Padrão funcionario-centric. Âncora = funcionario.uuid (referencia_uuid da validação UPDATE/DADOS_BANCARIOS).
- CORRIGIR: pendentes P→C + validação P→C (devolverParaCorrecao). Reenvio (validar=null + por corrigir):
  sync aplica edições, C→P + reabrirParaValidacao. Guard: validar!=null em correção → 400.
- Nota: syncBancarios marca item ALTERADO como P e soft-delete (E) dos ausentes; itens não alterados
  ficam em C e são repostos a P explicitamente no reenvio.

### #2 AlterarSituacaoLaboralWriteService — CORRIGIR ✔ (compile OK)
- Âncora = situacaoLaboral.uuid. CORRIGIR: sit+tiprel P→C. Reenvio: update in place da situação atual
  + tiprel P, reabrir. NÃO cria novo tiprel na correção (é sobre o registo devolvido).
- Reordenado: funcionario carregado antes do bloco CORRIGIR.

### #3 ValidacaoRenovacaoContratoService — CORRIGIR ✔ (compile OK)
- Renovação é especial: contrato mantém-se A; pendente = tiprel novo (P) + histórico renovação (P) +
  validação (P). Âncora = contrato.uuid.
- Adicionados 2 helpers em ContratoHistoricoWriteService: marcarRenovacaoPendenteComoCorrecao (P→C) e
  reabrirRenovacaoCorrecao (C→P + datas), sem criar nova versão de histórico.
- Reenvio repõe estado pós-registo → SIM posterior corre como renovação normal (não toquei no fluxo SIM/NAO).

### #4 SubstituicaoWriteService — CORRIGIR ✔ (compile OK)
- Entity-uuid. Âncora=substituicao.uuid (validação vive no SUBSTITUTO, funId=substituto). CORRIGIR antes
  das edições; reenvio depois das edições (C→P). Guard C+validar→400.

### #5/#6 RenumeracoesWriteService — CORRIGIR ✔ (compile OK)
- Convenção STRING (ValidationUtil.isCorrigir/isAprovado). Âncora = remuneracao.uuid / pagamento.uuid.
- Import adicionado: IgrpResponseStatusException. Load da entidade movido para antes do bloco CORRIGIR.

### #7 ProcessoDisciplinarWriteService — CORRIGIR ✔ (compile OK)
- estado é STRING; validar é STRING ("S"/"N"/CORRIGIR). Validação NÃO gravava referencia_uuid → ADICIONADO
  `setReferenciaUuid(process.getUuid())` no registo (registos antigos não têm; testar com processo novo).
- Import adicionado: IgrpResponseStatusException. Usa helpers partilhados via referenciaUuid.

## FASE 2 (JaVers) — complicações encontradas (decisão pendente)
- O detalhe (JaversValidacaoDetalheReadService) consulta SÓ commits carimbados com validacaoUuid, e
  filtra pela instância via `isAlvo`: tipo==entityTypeSuffix E cdoId==validacao.referenciaId
  (se referenciaId==null → filtro só-por-tipo).
- **Anchor OK** (referenciaId == id da entidade auditada, ou null): SituacaoLaboral (refId null→tipo),
  Substituicao, Rendimento, Desconto, ProcessoDisciplinar. → descriptor + carimbo funcionam limpo.
- **Anchor MISMATCH**: 
  - DadosBancarios: validação.referenciaId = funcionario.id; entidade = DadosBancariosEntity.id (colecção).
    Grid ficaria vazio. Solução: referenciaId null → filtro por tipo (mostra todos os bancários da validação).
  - Renovacao: validação.referenciaId = contrato.id; alteração real = ContratoHistoricoEntity (datas), id
    diferente. Grid não casa. Precisa de anchoring próprio (auditar histórico com refId=histórico) — mexe
    no registo/contagem de renovações. RISCO.
- **Blast radius**: anotar DefinicaoRemuneracao/DefPagamento/SituacaoLaboral (repos partilhados) faz o
  auto-audit disparar em MUITOS fluxos (processamento, progressão de carreira), não só na correção.
  Refs rasas mantêm o commit rápido, mas cresce a JV_* e audita fluxos não relacionados.
- Feito até agora na Fase 2: JaversAuditConfig refs rasas (Banco/ParamSituacao/ParamSituacaoDetalhe/
  TiposRelacionamento/TipoMovimento) + 6 repos anotados @JaversSpringDataAuditable. Compila. FALTA
  descriptors + carimbos.

## FASE 3 — teste live (resultados)
- Colaborador ativo usado: 958897 (uuid 01a0256d-dbed-7f2d-bdcc-a1789b9d5203), tiprel 01a02573-...
- Arranque: forçar JAVA_HOME JDK23; carregar .env SEM `source` (password Pa$$w0rd tem `$$` → expandir
  rebenta; ler linha-a-linha e tirar BOM). Segurança OFF em dev (permitAll, sem token).
- **BUG corrigido**: `ValidationUtil.isCorrigir(null)` fazia NPE (`List.of(...).contains(null)`). O
  reenvio (validar=null) expunha-o. Fix: null-safe. Afetava ProcessoDisciplinar e Renumeracoes.
- **#7 ProcessoDisciplinar ✅ PROVADO** P→C→P→A + JaVers: grelha `[{Entidade: "Entidade A" →
  "Entidade B CORRIGIDA"}]` via GET .../validacoes/{vuuid}/detalhes-javers.
  NB: CORRIGIR exige payload COMPLETO (bean validation @NotBlank em entidade/tipoProcesso).

### FASE 3 — resultados por fluxo (colaborador 958897)
- ✅ **ProcessoDisciplinar** — CORRIGIR (P→C→P→A) + JaVers grid [Entidade A→B CORRIGIDA]. PROVADO.
- ✅ **Rendimento** — CORRIGIR + JaVers [Valor 5000→7500]. PROVADO.
- ✅ **Desconto** — CORRIGIR + JaVers [Valor 3000→4200]. PROVADO.
- ✅ **SituacaoLaboral** — CORRIGIR + JaVers [Observações OBS INICIAL→OBS CORRIGIDA]. PROVADO.
- ✅ **DadosBancarios** — CORRIGIR (P→C→P→A, nib editado). PROVADO. (JaVers deferido — sem grelha.)
- ✅ **Substituicao** — CORRIGIR (C→P→A) + JaVers [Observações subst diff→SUBST CORRIGIDA]. PROVADO.
  NB: o cálculo de diferença usa `tiprel.salario` (NÃO def_remuneracoes) — baixei tiprel.salario do
  substituto (173328) p/ 100000 (<186980) para gerar diferença → substituição nasce P. vuuid
  01a028e5-f52d-7324-9b49-17c3806155c4.
- ✅ **Renovacao** — CORRIGIR (C→P→A). PROVADO. Setup: contrato 698 convertido p/ tipo 1 (renovável)
  + data_fim. Data corrigida no reenvio (2027-06-30) aplicou-se ao contrato SÓ no SIM; contrato manteve-se
  A durante a correção; histórico pendente C→P→(consolidação). (JaVers da renovacao continua deferido.)

### Mutações de dados de teste feitas (colaborador 958897 — NÃO limpar per instrução)
- tiprel 173328 (atual do 958897): salario 186980 → 100000 (para diferença de substituição).
- contrato 698: tp_contrato_id 2→1, data_fim → 2027-06-30 (renovação consolidada).
- def_remuneracoes 1426: mexido e RESTAURADO a 'A'.

### App/infra
- App a correr: mvn spring-boot:run (JDK23), .env carregado sem `source`. Log em scratchpad/app_live.log.
- Segurança OFF (dev) → sem token. autor dos commits JaVers = system-bot@nosi.cv (auditor dev).

## PENDENTE p/ Fase 3 (teste live) — atenção
- ProcessoDisciplinar: só processos criados APÓS este fix têm referencia_uuid → CORRIGIR só funciona neles.
- Renovação CORRIGIR: contrato mantém-se A; verificar que após C→P→SIM a renovação consolida igual ao normal.

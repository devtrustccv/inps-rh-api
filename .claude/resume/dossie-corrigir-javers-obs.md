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

## PENDENTE p/ Fase 3 (teste live) — atenção
- ProcessoDisciplinar: só processos criados APÓS este fix têm referencia_uuid → CORRIGIR só funciona neles.
- Renovação CORRIGIR: contrato mantém-se A; verificar que após C→P→SIM a renovação consolida igual ao normal.

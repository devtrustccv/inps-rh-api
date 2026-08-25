> Updated: 2026-08-25 17:10

## Goal

Grelha "Detalhe de alterações" (JaVers) para o REGISTO_COLABORADOR, capturada só no PUT de reenvio de
correção (C→P). Valores sempre LEGÍVEIS (rótulos PT, FKs→nome, nunca id). Antes: expor estado/estadoDesc
uniforme (feito e commitado, `274a2799`).

## Current state

- Âmbito: **todos os filhos que o registo toca** (pessoais + contratuais). TiposRelacionamento FORA.
- TODOS os filhos LIGADOS e **testados live**: um reenvio com 6 edições devolveu 9 linhas em
  `/detalhes` cobrindo CONTACTO, ENDERECO, FAMILIARES, HABILITACOES, DADOS_BANCARIOS, CARREIRA,
  MOBILIDADE, SITUACAO_LABORAL — valores legíveis, `tabelaName` desambigua.
- Bug corrigido (c1de535e): `isAlvo` aceita `Set<String>` (o compile offline incremental mascarava;
  usar SEMPRE `mvn clean compile` antes de arrancar).
- Adicionados **Documento pessoal** (RH_T_DOCUMENTO_PESSOAL) e **Remunerações/Pagamentos**
  (RH_T_DEF_REMUNERACOES/PAGAMENTOS, por composição de Rendimento/Desconto). Clean compile OK.
- FORA (com justificação no javadoc): TiposRelacionamento/Contrato (ligação/shallow) e os campos-núcleo
  do FuncionarioEntity (nome/NIF/nomes pais) — funcionário é ShallowReference; detalhá-los exige
  mecanismo dedicado fora do auto-audit.
- OBSERVAÇÃO: a grelha ACUMULA diffs de todos os ciclos de correção da MESMA validação (mesmo
  validacaoUuid em P→C→P). Registo novo com 1 ciclo = 1 linha por campo. Afinar para "último por campo"
  se o negócio quiser (só na leitura).
- Read-model **multi-tipo** ligado: `isAlvo` usa `entityTypeSuffixes()`.
- Descritor do registo faz **composição**: injeta descritores de módulo existentes (DadosBancarios já;
  Carreira/Mobilidade/Situação a fazer) + config "dossiê" própria (mapa `DOSSIE`).
- Serviço tem helpers genéricos `baseline(...)`/`capturar(...)` — cada filho novo = anotar repo
  `@JaversSpringDataAuditable` + 1 linha em `criarBaselineFilhos`/`capturarDetalheFilhos` + entrada no
  descritor.
- TODO dos próximos filhos está **no código** (javadoc do descritor).

## Decisions made — do not re-litigate

- Reutilizar descritores existentes por COMPOSIÇÃO (não duplicar campos/rótulos).
- TiposRelacionamento e Contrato (campos próprios) FORA: tabela de ligação / shallow ref.
- Detalhe só no PUT (C→P): registo só editável via CORRIGIR; depois usam-se os módulos individuais.
- Baseline no CORRIGIR (P→C) SEM contexto (não entra na grelha); diff no reenvio DENTRO do
  `ValidacaoAuditContext` carimbado com a validação.
- `matchByTypeOnly=true` (referenciaId = funcionário, não o filho).

## Constraints

- JDK23; `TableName` não tem RH_T_DADOS_BANCARIOS → usar string literal. Get-by-id/validar por UUID.
- Filhos gravados em cascata + `FuncionarioEntity` é ShallowReference → filho só é auditado se o seu
  repo for `@JaversSpringDataAuditable` e gravado pelo próprio repo.

## Relevant files

- `RegistoColaboradorValidacaoDetalheDescriptor.java` — descritor + TODO dos próximos filhos.
- `ValidarRegistoColaboradorService.java` — `criarBaselineBancarios`, `capturarDetalheBancarios`.
- `ValidacaoDetalheDescriptor.java:31` — `entityTypeSuffixes()` default (hook multi-tipo, por ligar).

## Open questions

- `alteradoPor` em dev = `system-bot@nosi.cv` (auditor sem token real).

## Next step

Feature completa e testada. Decisão de negócio pendente: manter histórico acumulado por ciclo de
correção OU mostrar só o último valor por campo (afinação na leitura do JaversValidacaoDetalheReadService).
Opcional: ligar Documento pessoal e def rem/pag. Considerar push da branch develop.

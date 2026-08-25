> Updated: 2026-08-25 16:05

## Goal

Grelha "Detalhe de alterações" (JaVers) para o REGISTO_COLABORADOR, capturada só no PUT de reenvio de
correção (C→P). Valores sempre LEGÍVEIS (rótulos PT, FKs→nome, nunca id). Antes: expor estado/estadoDesc
uniforme (feito e commitado, `274a2799`).

## Current state

- Fatia vertical **DadosBancarios** validada end-to-end: editar Nº de conta no reenvio → `/detalhes`
  mostra `{campoAlterado:"Nº de conta", valorAnterior:"9019055", valorNovo:"9019099", tabelaName:...}`.
- App a correr na **8088** (spring-boot:run default). Log confirma descritor REGISTO_COLABORADOR ativo.
- TODO dos próximos filhos está **no código** (javadoc do descritor), não em doc.

## Decisions made — do not re-litigate

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

Ligar o read-model multi-tipo: usar `entityTypeSuffixes()` no `JaversValidacaoDetalheReadService.isAlvo`
e replicar baseline/diff para Contactos (repo `@JaversSpringDataAuditable`), o filho mais simples.

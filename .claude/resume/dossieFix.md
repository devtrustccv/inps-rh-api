> Updated: 2026-08-25 16:46

## Goal

Grelha "Detalhe de alterações" (JaVers) para o REGISTO_COLABORADOR, capturada só no PUT de reenvio de
correção (C→P). Valores sempre LEGÍVEIS (rótulos PT, FKs→nome, nunca id). Antes: expor estado/estadoDesc
uniforme (feito e commitado, `274a2799`).

## Current state

- Âmbito: **todos os filhos que o registo toca** (pessoais + contratuais). TiposRelacionamento FORA.
- Dossiê LIGADO: **DadosBancarios** ✅ (testado live), **Contactos**, **Endereço**, **Familiares**,
  **Habilitações** ✅ (compilam; falta teste live dos novos). Falta ainda **Documento pessoal** e a
  parte **contratual** (por composição). FKs shallow já tratadas.
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

Parte **contratual** por composição: injetar `CarreiraValidacaoDetalheDescriptor`,
`MobilidadeValidacaoDetalheDescriptor`, `SituacaoLaboralValidacaoDetalheDescriptor` em `reutilizados`
do descritor do registo, e no serviço gravar carreira/mobilidade/situação (via os seus repos auditados)
no baseline/captura (entidades vêm de `tiposRelacionamento`, não são coleções do funcionário).
(Opcional) Documento pessoal. Depois: teste live consolidado editando ≥1 campo por secção.

> Updated: 2026-08-20 19:45

## Goal

Grelha "Detalhe de alterações" (antes→novo) servida pelo **JaVers** (auto-audit) em vez de RH_T_VALIDACAO_DETALHE. Piloto na mobilidade, já promovido ao endpoint oficial.

## Current state

- **Endpoint oficial migrado**: `GetDetalheAlteracoesQueryHandler` passou a chamar `JaversValidacaoDetalheReadService`. Path `GET api/v1/funcionarios/validacoes/{uuid}/detalhes` e `ValidacaoDetalheDTO` mantidos — front não quebra. Controller piloto `.../detalhes-javers` ainda existe.
- **Provado end-to-end** (app na 8089, Oracle real): EDIÇÃO mostra só os campos mudados (sem ruído inter-mobilidade); REGISTO mostra todos os valores iniciais de negócio.
- Compila (JDK 23, EXIT=0). App reiniciada; falta **1 curl ao path oficial `/detalhes`** para confirmar (foi interrompido).

## Decisions made — do not re-litigate

- **Filtro por instância-alvo** (`isAlvo` via `validacao.referenciaId`), não só por tipo: mata o ruído da mobilidade anterior desativada na consolidação.
- **Allow-list** `CAMPOS_NEGOCIO` (secaoId, localTrabId, instidId, tipoSituacao, dataInicio, dataFim, obs): fora estado/funId/mobId/created*.
- **Semântica por `tipoAccao`**: INSERT mostra valores iniciais completos; UPDATE só diffs reais.
- Passo-2 (commit JaVers na validação) **revertido** — sem estado na grelha, era custo sem valor.

## Relevant files

- `shared/application/service/JaversValidacaoDetalheReadService.java` — filtros/allow-list.
- `funcionario/application/queries/GetDetalheAlteracoesQueryHandler.java:20` — fonte trocada.

## Open questions

- `obs` = literal "MOBILIDADE" (marcador) aparece no REGISTO — tirar da whitelist?
- Generalizar a outros services (carreira/contrato): o chamador só precisa de `repository.save()` com `ValidacaoAuditContext.set(...)`; TIPO_ALVO_SUFIXO/ROTULOS ainda são mobilidade-only.

## Next step

`curl .../validacoes/01a020de-b430-7f3b-9c71-7abb52743466/detalhes` e confirmar 3 linhas iguais ao `/detalhes-javers`.

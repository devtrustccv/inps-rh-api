> Updated: 2026-08-15 09:51

## Goal

Implementar a **escrita** das linhas de `RH_T_VALIDACAO_DETALHE` — capturar valor anterior/novo de cada campo alterado e ligá-los à validação pendente. A leitura já está feita e commitada.

## Current state

- Commit `80659e4c` (develop): leitura completa — entidade, repositório, read service, DTO, query+handler, endpoint `GET api/v1/funcionarios/validacoes/{idValidacao}/detalhes`, manifestos `.igrpstudio`, DDL no `V1__init_schema.sql`.
- BD Oracle (INPSRH): `RH_T_VALIDACAO_DETALHE`, `SEQ_VALIDACAO_DETALHE`, `TRG_VALIDACAO_DETALHE`, `IX_VALID_DET_VALIDACAO` **já criados e válidos**.
- Escrita: **zero**. A tentativa via `org.hibernate.Interceptor` foi revertida.

## Decisions made — do not re-litigate

- **Uma linha por campo alterado, não JSON**: spec linha 2089 ("cada campo alterado") e 2534 ("2 registos, 1 para estado e outro para OBS").
- **Envers rejeitado**: exige dezenas de tabelas `_AUD` fora da spec de BD; a unidade "revisão" não coincide com "validação"; não vê SQL nativo.
- **Interceptor Hibernate rejeitado**: rebentava com `ClassNotFoundException: AlteracoesBuffer$ChaveEntidade` no `onFlushDirty`, deitando abaixo qualquer UPDATE com 500.

## Constraints

- O padrão dos write services é mutar → consultar → decidir. A query do meio provoca **auto-flush**, que substitui o `loadedState` — o "antes" desaparece antes do fim do método. Ler o estado original no fim do service **não funciona**.
- Bibliotecas de diff (JaVers, `ReflectionDiffBuilder` do commons-lang3, já disponível) não resolvem isto: todas exigem que lhes entreguem o "antes".
- Entidades são geradas pelo iGRP Studio (`DO NOT MODIFY`) — anotações nelas não sobrevivem a regeneração.
- Escrita direta na BD: usar `DbExec`, nunca `DbUpdate` (ORA-17273).

## Relevant files

- `src/main/java/cv/inps/rh/funcionario/application/service/MobilidadeWriteService.java:280-294` — o caso canónico do auto-flush; alvo do piloto.
- `src/main/java/cv/inps/rh/shared/infrastructure/persistence/entity/ValidacaoDetalheEntity.java` — destino da escrita.
- `docs/Especificação Tecnica Funcional - DOSSIÊ DO COLABORADOR_24_07_2026.md:1559,1640,1643,1658` — grelha, "validador também regista", uma linha por tabela, só se antes≠novo.

## Open questions

- `CAMPO_ALTERADO` leva nome cru da coluna (literal à spec) ou label legível? Valores traduzidos ou ids crus?
- Inserção/remoção de linhas (contacto novo, subsídio removido) não tem forma "campo/antes/depois" — a spec não modela. Registar como?

## Next step

Escolher o mecanismo de captura entre as duas alternativas ainda abertas — **snapshot explícito no service antes de mutar** (simples, repete a lista de campos por ecrã) ou **trigger PL/SQL** (apanha até `DbExec`, mas não conhece o `VALIDACAO_ID` sem contexto de sessão) — e provar com o piloto da mobilidade.

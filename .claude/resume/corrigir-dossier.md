> Updated: 2026-08-19

## Goal

Normalizar a validação do dossiê para 3 caminhos (SIM/NAO/CORRIGIR), onde CORRIGIR é por agora NO-OP (log + 200), e fazer TODOS os endpoints de escrita do dossiê devolverem `SuccessResponseDTO` em vez de eco do DTO.

## Current state — CONCLUÍDO (BUILD SUCCESS)

`SuccessResponseDTO` (`sucesso`, `id`, `mensagem`, `alertas[]`) + `ValidationUtil.MSG_CORRIGIR_NAO_IMPLEMENTADO`/`isCorrigir(String)` já existiam.

Convertidos end-to-end (service + handler + controller + manifesto `.igrpstudio`) — **15 services / ~30 endpoints**:

- **Registo Colaborador** (create + validar) — guard ✅
- **ValidarDados\*** (funcionário/dossiê): Pessoais, Académicos, Familiares, Bancários — guard só em Bancários (validar ativo; os outros têm o bloco validar comentado → sem guard, só retorno padrão)
- **Contrato**: validar, validar-renovação — guard ✅ ambos
- **Substituição**: registar, validar — guard em validar
- **Regime**: adicionar, validar — guard em validar
- **Situação Laboral**: alterar (PATCH, FuncionarioController) — guard ✅
- **Mobilidade**: save, editar, validar — guard em validar
- **Renumerações**: novoRem, novoPag, validarRem, validarPag — guard em validarRem/validarPag (`isCorrigir` String)
- **Processo Disciplinar**: novo, update, delete — guard em update (`isCorrigir` String); delete 204→200
- **Histórico Laboral**: novo, atualizar — SEM guard (use case não passa por validação)
- **Carreira**: nova, validar, eliminar, atualizar — guard em validar (já existia; passou a devolver Success); eliminar 204→200
- **Declaração**: novoPedido, submeterAnalise, validarPedido — guard ADICIONADO em validarPedido (corrigiu bug: CORRIGIR caía no else = rejeição)

## Decisions made — do not re-litigate

- CORRIGIR = NO-OP (guard no TOPO, sem validar/gravar/mudar estado) + log + `SuccessResponseDTO(false,null,MSG,[])`. Estado `C` só existe em `RH_T_FUNCIONARIOS`; NÃO cascatear.
- **Guard CORRIGIR só onde a validação está ATIVA**. Onde o bloco `validar`/`validacao` está comentado, NÃO descomentar e NÃO pôr guard — apenas trocar o retorno para `SuccessResponseDTO`.
- Enum `EstadoValidacao.CORRIGIR.equals(...)`; String `ValidationUtil.isCorrigir(...)` (ProcessoDisciplinar, Renumerações, Declaração).
- Alcance final = TODOS os endpoints de escrita do dossiê (registar/editar/eliminar/validar), decidido com o utilizador durante a sessão (incluídos HistoricoLaboral, Carreira, Declaração além da lista original de 9).

## Constraints

- Manifesto ↔ Java sincronizados (schema resposta: `type:"SuccessResponse", objectType:"dto", module:"shared", collectionType:"none"`).
- Compilar com JDK 23; app a correr segura o `target` → usar `mvn -o compile` (sem clean). Última compilação: BUILD SUCCESS.

## Relevant files

- ValidarRegistoColaboradorService.java — padrão de referência (guard + returns).
- SuccessResponseDTO.java + .igrpstudio/shared/dto/SuccessResponseDTO.json.

## Next step

Trabalho concluído. Se surgirem novos endpoints de escrita no dossiê, aplicar o mesmo padrão (guard CORRIGIR só se validar ativo + retorno `SuccessResponseDTO` + manifesto). Falta (opcional): atualizar `docs/frontend_changes_*` com o novo formato de resposta.

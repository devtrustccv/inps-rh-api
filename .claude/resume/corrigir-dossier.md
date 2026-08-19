> Updated: 2026-08-19

## Goal

Normalizar a validação do dossiê para 3 caminhos (SIM/NAO/CORRIGIR), onde CORRIGIR é por agora NO-OP (log + 200), e fazer todos os endpoints de escrita do dossiê devolverem `SuccessResponseDTO` em vez de eco do DTO.

## Current state

- `SuccessResponseDTO` criado (Java + manifesto `.igrpstudio/shared/dto/SuccessResponseDTO.json`): `sucesso`, `id`, `mensagem`, `alertas[]`.
- Constante `ValidationUtil.MSG_CORRIGIR_NAO_IMPLEMENTADO`.
- **Registo Colaborador convertido end-to-end** (create + validar): service+handler+controller devolvem `SuccessResponseDTO`; guard CORRIGIR no topo; manifesto `FuncionarioController.json` (2 actions) atualizado. Compila (BUILD SUCCESS).
- `CarreiraWriteService.validarCarreira` já tinha guard CORRIGIR (`isCorrigir → return`).

## Decisions made — do not re-litigate

- CORRIGIR = NO-OP (guard no TOPO, sem validar/gravar/mudar estado) + log + `SuccessResponseDTO(false,null,MSG,[])`. Estado `C` só existe em `RH_T_FUNCIONARIOS`; NÃO cascatear.
- Alcance: só módulo funcionário (dossiê). Excluir assiduidade/processamento e os 4 `ValidarDados*/Agregados` (decisão comentada).

## Constraints

- Manifesto ↔ Java têm de ficar sincronizados (gerador IGRP). Cada endpoint = 3 ficheiros Java + 1 action no `*Controller.json` (schema DTO: `type:"SuccessResponse", objectType:"dto", module:"shared", collectionType:"none"`).
- Compilar com JDK 23; app a correr segura o `target` → usar `mvn -o compile` (sem clean).

## Relevant files

- ValidarRegistoColaboradorService.java:56 — padrão de referência (guard + returns).
- .igrpstudio/funcionario/controllers/FuncionarioController.json — formato do schema de resposta.

## Next step

Aplicar o mesmo padrão (guard CORRIGIR + retorno `SuccessResponseDTO` + manifesto) aos restantes: ValidarContratoService, ValidacaoRenovacaoContratoService, SubstituicaoWriteService, RegimeWriteService, AlterarSituacaoLaboralWriteService, MobilidadeWriteService, RenumeracoesWriteService (×2), ProcessoDisciplinarWriteService.

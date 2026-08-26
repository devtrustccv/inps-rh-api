# Handoff — Melhorias Dossiê do Colaborador

> Documento vivo. Atualizado durante a implementação. Worktree: `feat/dossier-melhorias`.
> Última atualização: 2026-08-26.

## Contexto

Implementação das melhorias do `docs/MELHORIAS_DOSSIER.md` (secção 2 — Dossiê do Colaborador),
cruzadas com prints/spec fornecidos. Trabalho isolado num git worktree
(`.claude/worktrees/dossier-melhorias`), para não colidir com a app do utilizador a correr no **8088**.

### ⚠️ Bloqueio de infraestrutura (ativo)
A BD Oracle (`62.84.179.137:1521`) está **inacessível** desta máquina (ping 100% loss; JDBC timeout).
Decisão do utilizador: **implementar agora, testar live depois**. Portanto:
- Código implementado e **compilado** (`mvn package`) neste worktree.
- **Teste live no 8089 + evidências HTML + aplicação do backfill** → PENDENTES até a BD voltar.
- O utilizador autorizou-me a **aplicar o backfill** (via helper `DbExec`) quando houver ligação.

## Decisões de negócio tomadas

1. **Dono do salário no "Alterar Escalão" sem carreira** → **Java** escreve o vencimento/subsídios
   (não a `PKG_AUMENTO_SALARIAL`, que filtra por `CARREIRA_ID` e rebentaria — bug ORA-01403).
2. **Modelo do `flg_salario`** → **String crua + validação no service** (enum `TipoSalarioVinculo`).
3. **Backfill ambíguo** (`flg_salario=1 & flg_carreira=0`) → default **`SIM_FORA_PCCS`** (conservador);
   promoção manual a `SIM_PCCS` dos que forem mesmo PCCS. Valores legados/ambíguos **nunca** contam
   como PCCS no código (só o literal `SIM_PCCS` ativa a lógica de escalão).

## Estado por item (do MELHORIAS_DOSSIER.md)

| Item | Descrição | Estado |
|---|---|---|
| 2.2.2 Regime | conforme doc | ✅ nada a fazer |
| 2.4 Validação (detalhe) | JaVers | ✅ já resolvido |
| 2.5 Conversão/Renovação | job do transversal | ⏭️ fora deste módulo |
| **Break change** | `FLG_SALARIO` Integer→String (`TIPO_SALARIO_VINCULO`) + enum | 🟡 em curso |
| **Break change** | refactor dos 11 consumidores "tem salário" | ✅ feito (compila) |
| **2.1** | escalão no tiprel ao registar/validar colaborador **e contrato** (sem carreira) | ✅ feito (compila) |
| **2.2.1 lista** | `categoria`→`escalao` na lista Gestão Laboral | ✅ feito (compila) |
| **2.3** | filtros `situacaoLaboral` + `contrVinculo` nas remunerações | ✅ feito (compila) |
| **2.2.1 Alterar Escalão/Cargo** | novo fluxo (escalão→validação→substitui salário) | ⬜ por fazer |
| **Manifestos IGRP** | `.igrpstudio/**.json` a refletir DTOs/endpoints alterados | ⬜ por fazer |

## Ficheiros alterados / criados

### Núcleo do break change (FLG_SALARIO → domínio)
- [x] `shared/application/constants/custom/TipoSalarioVinculo.java` — **novo** enum + helpers `isValido`/`temSalario`/`ehPccs`.
- [x] `shared/application/constants/Domains.java` — add `TIPO_SALARIO_VINCULO`.
- [x] `shared/infrastructure/persistence/entity/ParamVinculoEntity.java` — `flgSalario` Integer→String.
- [x] `configuracao/.../ParamVinculoService.java` — validar+gravar String; response usa domínio novo.
- [x] `configuracao/.../dto/VinculoLaboralResponseDTO.java` — `salario` Integer→String.
- [x] `parametrizacao/domain/models/ParamVinculo.java` — `flgSalario` Integer→String.
- [x] `parametrizacao/.../dto/VinculoDTO.java` — `flgSalario` Integer→String.
- [x] `parametrizacao/.../mappers/ParamVinculoMapper.java` — pass-through genérico (sem alteração de código).
- [x] Consumidores "tem salário" → `TipoSalarioVinculo.temSalario(...)`:
  - [x] `ColaboradorValidationRules` (+ helpers `vinculoEhPccs`, `aplicarEscalaoTiprelSemCarreira`)
  - [x] `ValidarDadosContratuaisService`
  - [x] `ValidarContratoService` (2 sítios)
  - [x] `RegistarColaboradorService`
  - [x] `NovoContratoService` (2 sítios)

### Escalão no tiprel (2.1 + 2.2.1)
- [x] `TiposRelacionamentoEntity.java` — add `escalaoId` (`@ManyToOne` → `RH_T_PARAM_ESCALAO`, `escalao_id`).
- [x] `DadosContratuaisMapper.clone()` — carrega `escalaoId`.
- [x] `RegistarColaboradorService` — `tr.escalaoId` via helper quando sem carreira + SIM_PCCS.
- [x] `NovoContratoService` (registo contrato, 2 sítios) — idem via helper.
- [x] `ValidarRegistoColaboradorService` — reafirma escalão + salário do escalão na reconciliação.
- [x] `ValidarContratoService` — idem na validação de contrato.
- [x] `RelacaoLaboralSumaryDTO` — `categoria`→`escalao`; read service lê `escalaoDesc` da vista.
- [x] Novo fluxo **Alterar Escalão/Cargo** (2.2.1) — **escrito, por compilar/testar**:
  - `dto/AlterarEscalaoCargoDTO.java` — tipoAlteracao (multiselect ESCALAO/CARGO), novoEscalaoId, novoCargoId, dataInicio, observacao, validar.
  - `commands/AlterarEscalaoCargoCommand(+Handler)` e `ValidarEscalaoCargoCommand(+Handler)`.
  - `service/historicolaboral/AlterarEscalaoCargoService.java` — CARGO só = imediato; ESCALÃO = pendente→validação; na validação SIM fecha o vencimento antigo (DATA_FIM, mantém A), abre novo DEF_REMUNERACOES (valor do escalão), reassocia TIPREL_REM_PAG (exclui salário fechado), fecha tiprel antigo, ativa novo. **Java escreve o dinheiro** (sem PKG).
  - `shared/.../constants/custom/Referencia.java` — +`GESTAO_LABORAL`.
  - `TiposRelacionamentoEntityRepository` — +`findFirstByTiprelId_IdAndEstado` (guard de pendente duplicado).
  - `HistoricoLaboralController` — endpoints `POST .../alterar-escalao-cargo` e `PUT .../alterar-escalao-cargo/{tiprelUuid}`.
  - Guard: só PCCS (`ehPccs`) **sem** carreira.
  - [x] Manifestos IGRP: `dto/AlterarEscalaoCargoDTO.json` + 2 ações no `HistoricoLaboralController.json`.
  - [x] Compila (**BUILD SUCCESS**). [ ] Testar live (bloqueado: BD).

## Estado global: CÓDIGO COMPLETO (compila) — falta só teste live + backfill (BD em baixo)
Tudo o que o utilizador pediu está implementado e a compilar no worktree `feat/dossier-melhorias`.
Pendentes, todos dependentes da BD voltar:
- Aplicar `docs/db/melhorias_dossier_tipo_salario.sql` (ALTER + backfill) via DbExec.
- Boot no 8089 + teste live end-to-end dos fluxos.
- Produzir o documento HTML de evidências do teste live.
- Pedir permissão para merge.

### 2.3 — Remunerações
- [x] `RenumeracaoController` + `GetListRenumeracoesQuery` + `RenumeracoesReadService` — filtros `situacaoLaboral` + `contrVinculo` (IDs).

### Manifestos IGRP (`.igrpstudio/**.json`)
- [x] `configuracao/dto/VinculoLaboralResponseDTO.json` — salario integer→string.
- [x] `funcionario/dto/RelacaoLaboralSumaryDTO.json` — categoria→escalao.
- [x] `parametrizacao/dto/VinculoDTO.json` — flgSalario integer→string.
- [x] `funcionario/dto/GetListRenumeracoesQuery.json` — +situacaoLaboral +contrVinculo.
- [x] `funcionario/controllers/RenumeracaoController.json` — +2 params na ação getListRenumeracoes.
- [x] `shared/models/ParamVinculoEntity.json` — flg_salario integer→string.
- [x] `shared/models/TiposRelacionamentoEntity.json` — +escalao_id (ManyToOne ParamEscalaoEntity).
- [x] `shared/enum/Domains.json` — +TIPO_SALARIO_VINCULO.
- [ ] (pendente) manifestos do novo endpoint/DTO Alterar Escalão/Cargo.
- Nota: `TipoSalarioVinculo` é enum hand-written em `constants/custom` (como `Referencia`) — sem manifesto.

### BD — script pronto
- [x] `docs/db/melhorias_dossier_tipo_salario.sql` — ALTER FLG_SALARIO + backfill (default SIM_FORA_PCCS
  para 1+sem-carreira) + guard ESCALAO_ID no tiprel. **Aplico eu via DbExec quando a BD voltar.**

## Log de progresso
- 2026-08-26: break-change + 2.1 (registo/validar colaborador+contrato) + 2.2.1-lista + 2.3 → **BUILD SUCCESS**.
  BD ainda inacessível (teste live/backfill pendentes). A seguir: Alterar Escalão/Cargo + manifestos.

### BD (aplicar quando a BD voltar)
- [ ] `docs/db/melhorias_dossier_tipo_salario.sql` — ALTER `FLG_SALARIO` NUMBER→VARCHAR2 + backfill.
- [ ] Confirmar `RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID` existe e é nullable.

## Pendências / riscos a confirmar no boot
- `RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID` — existência/nullabilidade (schema V1 está desatualizado).
- `parseFlag` já não é usado para `remuneracao`; confirmar nenhuma regressão nos outros flags.
- Backfill dos `flg_salario=1 & flg_carreira=0` → revisão manual dos PCCS.

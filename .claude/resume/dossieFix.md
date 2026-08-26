> Updated: 2026-08-26 18:30

## Goal

Nova funcionalidade **Ativar/Desativar Contrato** (RH INPS): PATCH que desativa (A→I) o contrato atual
e toda a cadeia de filhos, e reativa (I→A) o último contrato — inverso simétrico da ativação da
validação SIM, IMEDIATO (sem maker/checker) e SEM tocar em `funcionario.estado`. **Ainda em estudo** —
o utilizador vai pedir mais análise antes de fechar.

## Current state — COMMITADO em `develop`

- Endpoint `PATCH api/v1/funcionarios/{idFuncionario}/contratos/{contratoId}/estado`, body `{"estado":"A"|"I"}`.
- Padrão IGRP CommandBus: `AlterarEstadoContratoCommand` + `...CommandHandler` + `AlterarEstadoContratoService`.
- Guards **desativar (A→I)**: tiprel atual `est_act_adm=1` · contrato `estado=A` · NÃO processado em folha
  (`ProcessamentoFuncionarioRepository.existsByTiprel_Id`).
- Guards **ativar (I→A)**: contrato `estado=I` (rejeita P/C/E) · é o ÚLTIMO/atual contrato · sem outro
  contrato em vigor (`existeContratoEmVigor`, mesmo guard do registo/Novo Contrato).
- Fix relacionado: **Novo Contrato pós-desativação** — quando não há vínculo atual, cria contrato fresco
  (`primeiroContrato(..., "CONTINUIDADE")`) em vez de rebentar; `FuncionarioRules.getTipoRelacionamentoAtualOrNull`.

## Decisions made — do not re-litigate

- "Último/atual contrato" = **contrato cujo HISTÓRICO tem `est_act_adm=1`** (NÃO `max(id)` nem `versao`).
  `versao` é sempre 1 (Novo Contrato e Registo); Renovação versiona só no histórico. `max(id)` falha no
  edge do Novo Contrato REJEITADO (fica com id maior). Ver [[project_contrato_versao_model]].
- Desativação **mantém `est_act_adm=1` no histórico** (só baixa `estado`→I) = breadcrumb do "atual
  administrativo" que a reativação usa; a reativação repõe via `transicionarEstado`.
- `est_act_adm` só existe no **tiprel** e no **histórico** (a `RH_T_CONTRATO_VINCULO` não tem a coluna).
  Verificado na BD (prod): 0 contratos `ESTADO=A` sem histórico `est_act_adm=1`; func 940 → contrato 708
  (A) hist `est_act_adm=1`, contrato 707 (I) hist `0`. Multi-tiprel por contrato confirmado (708 → 2
  tiprels; usa-se o de maior id).
- Toggle é IMEDIATO, sem maker/checker; não mexe em `funcionario.estado` (isso é só a Cessação/CESSADO).

## Tabelas afetadas na DESATIVAÇÃO (todas ESTADO='I')

`RH_T_CONTRATO_VINCULO` (contrato) · `RH_T_CONTRATO_HISTORICO` (linha atual, mantém EST_ACT_ADM=1) ·
`RH_T_TIPOS_RELACIONAMENTO` (tiprel atual, **EST_ACT_ADM=0**) · `RH_T_MOBILIDADE` · `RH_T_CARREIRA` ·
`RH_T_REGIME_TRAB` · `RH_T_SITUACAO_LABORAL` · `RH_T_DEF_REMUNERACOES`/`RH_T_DEF_PAGAMENTOS` (só os
associados ao tiprel em estado A). NÃO toca: `RH_T_TIPREL_REM_PAG` (sem coluna estado), `RH_T_FUNCIONARIOS`,
`RH_T_PROC_FUNCIONARIOS` (só lida no guard).

## Constraints (preferências do utilizador — ver [[feedback-fluxo-validacao-teste]])

- Compilar SEMPRE com `JAVA_HOME=.../Eclipse Adoptium/jdk-23.0.2.7-hotspot`. Query direta à BD:
  `java -cp ".;<ojdbc11 jar do .m2>" DbQuery "<SQL>"` (helper em DbQuery.java, credenciais lá). Ver [[reference-db-helpers]].
- GET SEMPRE antes de cada escrita; pedir AUTORIZAÇÃO por cada fluxo de escrita; testar casos NEGATIVOS
  antes do happy path. contratoId no path = UUID.

## Open questions (o utilizador vai aprofundar)

- **Reativação de `def`**: o filtro é pelo estado do próprio def, logo `origem=I→A` reativaria TODOS os
  def em I associados ao tiprel — incluindo algum subsídio já `I` de propósito antes da desativação.
  Decidir: só reativar os que acompanham o contrato (ex.: por DATA_FIM), ou marcar quais foram desativados.
- Tiprels/def "estacionados" (`est_act_adm=0`) com `estado=A` que não batem certo com o estado do
  contrato — dados não normalizados; a feature não lhes toca (correto), mas convém saber a origem.
- Maker/checker no toggle? Por agora é imediato.

## Relevant files

- `service/AlterarEstadoContratoService.java` (guards + flip) + `commands/AlterarEstadoContrato*` + `dto/AlterarEstadoContratoDTO.java`.
- `interfaces/rest/ContratoController.java` (PATCH `.../estado`).
- `service/ContratoHistoricoWriteService.java` (`transicionarEstado`) · `service/NovoContratoService.java` (fix pós-desativação).
- `rules/FuncionarioRules.java` (`getTipoRelacionamentoAtualOrNull`) · repos: `TiposRelacionamentoEntityRepository.findFirstByContrVinculoId_UuidOrderByIdDesc`.

## Next step

Aguardar as perguntas/análise adicional do utilizador sobre a funcionalidade. Colaboradores de teste:
940 (`01a03aab-41c9-7412-a89f-0198e25319df`, contrato atual 708), 930, 926. Depois: testar live
(desativar → ativar, e desativar → novo contrato) e decidir as Open questions.

# Registo de Colaborador — happy path (analisado e testado a 2026-08-19)

Registo COMPLETO validado com sucesso (HTTP 200) contra a Oracle de dev. Este doc tem o
fluxo, os campos obrigatórios, as armadilhas e o payload que **funciona à primeira**.

## Endpoint e fluxo

- `POST /api/v1/funcionarios` (porta 8089, dev), body `FuncionarioRequestDTO`.
- Controller `FuncionarioController.createFuncionario` → `CreateFuncionarioCommand` →
  `CreateFuncionarioCommandHandler` → **`RegistarColaboradorService.saveDossierColaborador`**.
- Resposta: `{ "uuid": "...", "alertas": [] }`. Tudo entra em estado **P** (pendente de validação).
- Uma única transação cria: funcionário + documento pessoal + contactos + endereço + familiares +
  habilitações + bancários + contrato(v1) + carreira + regime + mobilidade + situação laboral +
  tipos_relacionamento + validação (INSERT/REGISTO_COLABORADOR). `tipo_situacao="INICIO"` em tudo.

## Ordem das validações (onde costuma rebentar)

1. `ColaboradorValidationRules.validarDadosPessoais`: documento único (tipoDoc+num), **NIF** (9 díg.,
   positivo, único, estado!=E), **NIF↔colaborador via API externa** e campos obrigatórios.
2. `ValidarDadosContratuaisService.validar`: obrigatórios + existência das FK + regras de datas +
   obrigatórios por tipo de vínculo (flgCarreira/flgSalario).
3. Familiares: duplicado por documento + responsável único (só se responsavel=SIM).
4. NIB obrigatório se o vínculo tem salário.
5. Vínculo tem de ter movimentos **REM** e **PAG** (tipo exacto) parametrizados (RH_T_PARAM_VINCULO_MOV).

## ARMADILHAS (a razão dos erros repetidos)

- **NIF↔colaborador é fail-OPEN**: `EXTERNAL_NIF_URL` default `http://localhost:8080`. Se a API não
  responder (`RestClientException`) a validação é ignorada. Se ESTIVER a responder, nome/nomeMae/
  nomePai/dataNascimento têm de bater com a API para aquele NIF, senão 409.
- **NIF**: exactamente **9 dígitos**, positivo, único.
- **NIB**: exactamente **21 dígitos**, só números (`ValidationUtil.sanitizeNib`). Obrigatório se salário.
- **dataInicio do contrato**: NÃO pode ser no passado (`isBefore(hoje)`), no registo. Usar hoje ou futuro.
- **regimeTrabalho é obrigatório na prática**: `RegimeTrabalhoMapper.toRegime` cria SEMPRE o regime e
  `RH_T_REGIME_TRAB.TIPO_REGIME` é NOT NULL → enviar `"INTEGRAL"` (único valor existente). Omitir = ORA NOT NULL.
- **cada bancário precisa de `dataInicio`**: `RH_T_DADOS_BANCARIOS.DATA_INICIO` NOT NULL.
- **flg do vínculo mandam** (`RH_T_PARAM_VINCULO`): `flgCarreira=1` → carreira+escalão obrigatórios e
  salário AUTOMÁTICO (= `RH_T_PARAM_ESCALAO.VALOR`); `flgSalario=1` → salário obrigatório + NIB + REM/PAG.
- **Familiar**: apesar do nome, `GDP_ID`/`DEPENDENCIA`/`MEMBRO_AGR` são colunas **String** (mapeadas de
  `grauParentesco`/`dependente`/`agregada`) e NOT NULL → enviar strings (ex. "FILHO"/"SIM"/"SIM").
- **Auditoria**: campos de alteração ficam null no registo (fix desta sessão). `createdBy` = nome da auth
  (em dev sem token = `anonymousUser`); `user_registo_id` = 1L (stub do AuditEntityListener).
- `ValidationUtil.ref(em, Classe, id)` devolve null para id nulo → FKs opcionais podem ser omitidas.

## Campos por secção (DadosPessoaisReqDTO / DadosContratuaisReqDTO / ...)

Obrigatórios pessoais: tipoDocumentoId, numDocumento, nome, dataNascimento, genero(M/F),
nomeMae, **nomePai**, estadoCivil(C/S), nacionalidade, naturalidadeId, nif. (endereço/contactos opcionais)
Obrigatórios contratuais: tipoContratoId, tipoVinculoLaboralId, direcaoId, localTrabalhoId, dataInicio,
situacaoLaboralId; + carreiraId & escalaoReferenciaId se flgCarreira=1. moeda default "CVE".

## Dados de referência que FUNCIONAM na Oracle de dev (62.84.179.137:xe / INPSRH)

- tipoDocumentoId: **1** (BI). naturalidadeId: **1238705002006004** (CHÃ DE CARDOSO). nacionalidade "CABOVERDIANA".
- país (endereço/habilitação) **1238** (CABO VERDE); ilha 12387; concelho 1238705.
- tipoContratoId: **2** (Contrato Indeterminado, prazo_obrigatorio=0 → dataFim opcional).
- tipoVinculoLaboralId: **1** (Efetivo, flgCarreira=1, flgSalario=1, tem REM+PAG). Alternativas com REM+PAG: 7,8,9,16.
- situacaoLaboralId: **1** (ATIVO). direcaoId: **100010075** (DRHDO). seccaoId: **2**. localTrabalhoId: **1** (Sal-espargos).
- carreiraId (carr_pccs): **6**; escalaoReferenciaId: **21** (valor 186980, param_carr_id=6); cargoPosicaoId: **2**.
- entidadeBancariaId (banco): **1** (BCA) ou 6 (TESTE). regimeTrabalho: **"INTEGRAL"**. género M/F, estadoCivil C/S.

## Payload testado (200 OK, uuid 01a01a3a-..., funcionário id 958885)

Ver `scratchpad/colaborador.json` desta sessão. Resumo: vínculo Efetivo (carreira+escalão → salário
186980 automático), 2 contactos, endereço, 1 familiar (responsavel=NAO), 1 habilitação, 1 bancário.
Trocar `numDocumento`, `nif` e o `numDocumento` do familiar por valores ÚNICOS a cada corrida.

## Como consultar/inserir SQL directo (dev)

`DbExec.java`/`.class` estão na raiz do repo (untracked). Correr:
```
java -cp ".;<...>/ojdbc11-23.7.0.25.01.jar" DbExec "SELECT ..."
```
Usar **DbExec** (não DbUpdate → ORA-17273). Ver memória `reference_db_helpers`.
```
JDK 23: "/c/Program Files/Eclipse Adoptium/jdk-23.0.2.7-hotspot/bin/java.exe"
ojdbc: C:/Users/ivanick.santos/.m2/repository/com/oracle/database/jdbc/ojdbc11/23.7.0.25.01/ojdbc11-23.7.0.25.01.jar
```
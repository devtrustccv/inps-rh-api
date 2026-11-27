# Alterações Front-End — Melhorias Dossiê do Colaborador

**Data:** 2026-08-26
**Branch:** feat/dossier-melhorias (merged → develop @ merge `de698d2a`)
**Âmbito:** Dossiê / Funcionário + Parametrização de Vínculo. Testado live (FASES 0–9 verdes; T7.8 adiado).

> ⚠️ **Contém um break change** (secção 1). Ler antes de subir o front contra develop.

---

## 1. BREAK CHANGE — `flgSalario` / salário do vínculo passa a String

O campo que antes vinha como inteiro `0/1/2` passa a ser um **literal de domínio** (`TIPO_SALARIO_VINCULO`):

| Valor (String) | Significado |
|---|---|
| `SIM_PCCS` | Tem salário, tabela PCCS (escalão manda) |
| `SIM_FORA_PCCS` | Tem salário, fora do PCCS (valor manual) |
| `NAO` | Sem salário |

Aplica-se a:

| Endpoint | Campo | Antes | Agora |
|---|---|---|---|
| `GET`/`POST` parametrização de vínculo (`VinculoDTO`) | `flgSalario` | `Integer` (0/1/2) | `String` (literal acima) |
| `GET` vínculo laboral (`VinculoLaboralResponseDTO`) | `salario` | `Integer` | `String` (literal); + `remuneracaoDesc` com a descrição do domínio |

**Ação front:** enviar/ler os literais String (não 0/1/2). Selects de vínculo devem usar o domínio
`TIPO_SALARIO_VINCULO` (exposto pelo enum-exposer em `api/v1/enums`). Enviar `flg_salario=1` (inteiro)
passa a ser rejeitado na gravação do vínculo.

---

## 2. Escalão no vínculo (tiprel) para PCCS **sem carreira**

Para vínculos `SIM_PCCS` **com `flg_carreira=0`**, o escalão passa a viver no próprio vínculo e o
**salário é derivado do escalão** (não é manual).

- **Registo de Colaborador** e **Novo Contrato** (gravar/validar): enviar `escalaoReferenciaId` (`Long`)
  no bloco de dados contratuais (`DadosContratuaisReqDTO`). O backend grava o escalão no tiprel e usa o
  **valor do escalão** como salário — não é preciso enviar salário à mão.
- **Get-by-id** (colaborador e contrato, `DadosContratuaisRespDTO`): passa a devolver
  `escalaoReferenciaId` preenchido para estes casos (antes vinha `null`), para o formulário reabrir com o
  escalão certo.

> Vínculos **com carreira** (`flg_carreira=1`) mantêm-se iguais: o escalão vem da carreira, não se envia
> `escalaoReferenciaId` no tiprel.

---

## 3. Lista "Gestão Laboral" — campo `categoria` → `escalao`

Resposta da lista de relação laboral (`GET .../funcionarios/{funcionarioId}/relacao-laboral`,
`RelacaoLaboralSumaryDTO`):

| Antes | Agora |
|---|---|
| `categoria` | **removido** |
| — | `escalao` (`String`, ex.: `"16/A"`) |

Preenchido tanto para vínculos com carreira (escalão da carreira) como sem carreira (escalão do tiprel).
**Ação front:** ler `escalao`; remover a coluna/binding de `categoria`.

---

## 4. Novo fluxo "Alterar Escalão / Cargo" (Gestão Laboral)

Só para colaboradores **PCCS sem carreira** (para carreira usa-se Progressão/Promoção).

| Ação | Método + Endpoint |
|---|---|
| Registar alteração | `POST .../funcionarios/{funcionarioId}/relacao-laboral/alterar-escalao-cargo` |
| Validar alteração | `PUT .../funcionarios/{funcionarioId}/relacao-laboral/alterar-escalao-cargo/{tiprelUuid}` |

**Body (`AlterarEscalaoCargoDTO`)** — igual no POST e no PUT:

| Campo | Tipo | Notas |
|---|---|---|
| `tipoAlteracao` | `String` | multiselect enviado como **CSV** (grava tal-e-qual); domínio `TIPO_MOV_LABORAL` / ref `GESTAO_LABORAL` |
| `novoEscalaoId` | `Long` | opcional |
| `novoCargoId` | `Long` | opcional |
| `dataInicio` | `date` | efetividade |
| `dataFim` | `date` | opcional |
| `observacao` | `String` | opcional |
| `validar` | `String` | só no PUT: `SIM` / `NAO` / `CORRIGIR` |

**Comportamento:**
- **Só cargo** (sem `novoEscalaoId`) → aplicado **imediatamente** no vínculo atual, **sem** ir a validação.
- **Escalão** (com ou sem cargo) → cria um movimento **pendente (P)** e entra na **lista de validações**
  com o rótulo **"Gestão Laboral"**.
- Validação: `SIM` consolida (fecha o vencimento antigo por data, cria o novo com o valor do escalão e as
  datas do form, ativa o novo vínculo); `NAO` rejeita (`I`); `CORRIGIR` devolve ao maker (`C`) e o re-POST
  reabre para `P` sem duplicar.
- Enviar sem `novoEscalaoId` **nem** `novoCargoId` → `400` ("Indique o novo escalão e/ou o novo cargo").
- Já existe alteração pendente sobre o mesmo vínculo → `409` ("Existe uma alteração … por validar").

**Erros de guard (HTTP 400):**
- Vínculo **com carreira**: *"Alterar Escalão/Cargo só se aplica a vínculos sem carreira. Para carreira use Progressão/Promoção."*
- Vínculo **não-PCCS**: *"Alterar Escalão/Cargo só se aplica a vínculos com salário do PCCS (SIM_PCCS)."*

---

## 5. Remunerações — dois filtros novos

`GET .../funcionarios/renumeracoes` ganha dois query params opcionais:

| Param | Tipo | Filtra por |
|---|---|---|
| `situacaoLaboral` | `Long` (id) | Situação laboral |
| `contrVinculo` | `Long` (id) | Contrato/Vínculo |

Valores não numéricos são ignorados silenciosamente. Sem `tiprelUuid`, a lista continua a mostrar **só o
vínculo ativo** (`est_act_adm=1`); os filtros combinam-se por cima disso.

---

## 6. Pendente conhecido — grelha de detalhe da "Gestão Laboral"

`GET .../funcionarios/validacoes/{tiprelUuid}/detalhes` para uma **alteração de escalão** (Gestão Laboral)
devolve por agora `[]` (HTTP 200) — o "antes → depois" ainda não é montado para este fluxo. As grelhas de
detalhe de **Mobilidade, Carreira, Registo, Renovação**, etc. **não mudam** e continuam a funcionar.

**Ação front:** não assumir conteúdo na grelha de detalhe **especificamente para Gestão Laboral**; tratar
lista vazia sem erro. (Será preenchida numa iteração seguinte.)

---
---

# Alterações Front-End — Inativar/Ativar Colaborador + Licença S/Vencimento

**Data:** 2026-08-29
**Branch:** develop (commits `b4920553`, `7586e8bf`)
**Âmbito:** endpoint de Situação Laboral (`PATCH .../funcionarios/{id}/situacao-laboral`). Testado live (fun 958915).

## 7. Novas validações (HTTP 400) no registo de situação laboral

Aplicam-se ao **registo/reenvio** (maker, `validar=null`), no `PATCH .../funcionarios/{id}/situacao-laboral`:

| Situação | Resposta 400 (`title`) |
|---|---|
| `motivoId` em falta | *"O motivo da alteração da situação laboral é obrigatório."* |
| Pedir **CESSADO** com colaborador já inativo | *"O colaborador já está inativo; não é necessária esta ação."* |
| Pedir **ATIVO** com colaborador já ativo | *"O colaborador já está ativo; não é necessária esta ação."* |
| Situação de **ausência** (ex.: Licença S/Venc.) sem `dataFim` | *"A data fim é obrigatória para situações de ausência (ex.: Licença Sem Vencimento)."* |

**Ação front:** tornar **Motivo** obrigatório sempre; **Data fim** obrigatória quando a situação é de
ausência (Licença S/Vencimento e afins); no combo **Estado**, não permitir escolher o estado atual
(ativar quem está ativo / inativar quem está inativo).

## 8. Comportamento — rejeição, datas e detalhe

- **Rejeição (`validar=NAO`)** de uma cessação já processada faz **rollback**: o colaborador volta a
  **Ativo** e o vínculo anterior é reaberto (antes ficava inativo e sem vínculo — corrigido).
- **Datas:** o fecho do vínculo anterior e o novo vínculo passam a usar a **`dataInicio` do formulário**
  (antes usavam a data do sistema). Enviar `dataInicio` coerente com o efeito pretendido.
- **Detalhe de alterações** (`GET .../validacoes/{uuid}/detalhes`) passa a vir **preenchido já no registo**
  (situação/motivo/datas/observação), não só após uma correção.

## 9. Licença S/Vencimento (e outras ausências)

Passa pelo **mesmo** endpoint, escolhendo a situação de ausência (ex.: `LIC_SV`). Efeitos:
- **Não** altera o Estado do colaborador (continua **Ativo** — é uma licença, não uma cessação).
- Regista automaticamente a **ausência** (período `dataInicio`–`dataFim`).
- Na **aprovação** gera a **Ordem de Serviço** de Licença S/Vencimento (enviar `tipoOrdemServico`).
- `dataFim` é **obrigatória** (ver secção 7).

## 10. Combo "Situação Laboral" — só associações ativas (23/10/2026)

`GET api/v1/parametrizacao/vinculos/situacoes-laborais/ativos?vinculoId=…` passa a devolver **só** as
situações cuja associação ao vínculo está **ativa** (`ESTADO = 'A'`). As **inativas** (`I`) deixam de
aparecer no combo (antes só eram excluídas as removidas, `E`). Ex.: vínculo 10 (Comissão de Serviço)
devolve só `ATIVO`; a "Situação do Teste" inativa saiu. Sem `vinculoId` nada muda.

O ecrã **Parametrização → Vínculo** (`GET configuracao/vinculo/{uuid}/situacoes-laborais`) continua a
listar as inativas, para as poder gerir.

**Ação front:** nenhuma — o contrato é o mesmo; só muda o conjunto devolvido.

## 11. Nacionalidade — id gravado em `NACION_ID` + `nacionalidadeDesc` (11/11/2026)

O id da nacionalidade passa a ser gravado em `RH_T_FUNCIONARIOS.NACION_ID` (indicação do analista); antes ia
parar à coluna de texto `NACIONALIDADE`. **Sem breaking change:** o campo continua a chamar-se `nacionalidade`.

| Onde | Antes | Agora |
|---|---|---|
| Request `dadosPessoais` (registo `POST api/v1/funcionarios`, validar `PUT api/v1/funcionarios/{id}`, editar `PUT .../{id}/dados-pessoais`) | `nacionalidade: "1238"` (String) | `nacionalidade: 1238` (`Long`, id de `SIPSGLOBAL.GLB_T_GEOGRAFIA` — país). `"1238"` em string continua a ser aceite. |
| Response `dadosPessoais` (`GET api/v1/funcionarios/{id}`) | `nacionalidade: "1238"` | `nacionalidade: 1238` (`Long`) + **novo** `nacionalidadeDesc: "CABOVERDIANO"` |

- Em falta → 400 `Nacionalidade é obrigatória.`; id inexistente → 400 `Nacionalidade inválida: o valor indicado não existe.`;
  texto não numérico (ex. `"CABOVERDIANA"`) → 400 de pedido inválido.
- O backend preenche `NACIONALIDADE` com o texto da geografia (ex. `CABOVERDIANO`); o front não o envia.
- Detalhe de alterações: a linha "Nacionalidade" passa a mostrar o nome do país.

**Ação front:** só o tipo — tratar `nacionalidade` como **número** no modelo e no combo (o GET já não
devolve string; uma comparação `=== "1238"` deixa de bater). O nome do campo e o envio não mudam.
Usar `nacionalidadeDesc` para mostrar o texto.

## 12. Substituição — salário pelo vencimento ativo (26/11/2026)

O cálculo da substituição passa a usar o **salário que o colaborador recebe hoje** — o vencimento ativo (`SAL`, tipo
`REM`, estado `A`, sem `TIPO` (`TIPO IS NULL`), já iniciado e não terminado) em `RH_T_DEF_REMUNERACOES` ligado ao contrato atual — em vez do
`RH_T_TIPOS_RELACIONAMENTO.SALARIO` gravado na contratação, que fica desatualizado depois de um aumento (indicação do
analista). Com mais do que um vencimento ativo usa o mais recente.

| Endpoint | O que muda |
|---|---|
| `GET api/v1/funcionarios/substituicoes/calcular` | `valorSubstituto` / `valorSubstituido` vêm do vencimento ativo |
| `POST api/v1/funcionarios/{idFuncionario}/substituicoes` (registar) | a decisão "vai a validação" e o detalhe mensal usam o vencimento ativo |

**Novo erro (400):** se o substituto ou o substituído não tiver vencimento ativo, tanto o `calcular` como o registar
devolvem `O colaborador substituto|substituído (<nome>) não tem vencimento ativo.` — antes o `calcular` mostrava 0 e o
registo seguia sem diferença, sem aviso.

**Ação front:** nenhuma no contrato; mostrar a mensagem do 400.

## 13. Contrato em correção — GET devolve os subsídios e encargos manuais (26/11/2026)

`GET api/v1/funcionarios/{idFuncionario}/contratos/{contratoId}` com o contrato em correção (estado `C`, depois de um
CORRIGIR) passa a devolver em `subsidios` e `encargosDescontos` as linhas manuais do contrato (as que estão pendentes),
como já fazia com o contrato pendente (`P`). Antes vinham `[]`; o ecrã reenviava sem elas e o backend eliminava-as (`E`).

**Ação front:** nenhuma — o reenvio deve mandar as linhas como vêm do GET, cada uma com o seu `id`.

## 14. Correções do QA do dossiê (27/11/2026)

### 14.1 Uma alteração da relação laboral de cada vez (novo 400)
Mobilidade, renovação, novo contrato, desativar/ativar contrato, carreira (nova, progressão, edição que vai a validação,
marcar "processa salário") e situação laboral passam a ser bloqueadas se o colaborador tiver **outra** destas alterações
pendente (`P`) ou em correção (`C`):
`Existe uma alteração de <Mobilidade|Renovação de Contrato|...> pendente de validação|em correção para este colaborador. Valide-a ou rejeite-a antes de registar outra alteração da relação laboral.`
O caso "mesmo tipo" mantém a mensagem e o status de sempre (ex.: 409 `Existe um registo de carreira por validar!`).
Rendimentos, descontos, substituições, PD, banco, agregado e académicos continuam livres.

**Situação laboral:** com uma alteração já pendente, novo envio devolve 400
`Já existe uma alteração de situação laboral pendente de validação para este colaborador.` (antes 200 "sem alterações").

### 14.2 Movimentos de dinheiro (novo 400)
`POST .../{id}/remuneracao`, `POST .../{id}/pagamento`, `POST .../{id}/substituicoes`:
- colaborador cessado → `O colaborador está inativo (cessado): não é possível registar movimentos.`
- situação laboral pendente → `Existe uma alteração da situação laboral do colaborador por validar: ...`
- rendimento/desconto com `dataInicio` depois do fim do contrato → `A Data de Início não pode ser posterior ao fim do contrato (dd-MM-yyyy).`
- movimento inativo → `O movimento indicado está inativo.`; tipo errado → `O movimento indicado não é um movimento de rendimento|desconto.`

### 14.3 Listas mostram itens em correção (`C`)
`GET api/v1/funcionarios/renumeracoes`, a lista de pagamentos/descontos, `GET api/v1/funcionarios/substituicoes` e os
dados bancários passam a incluir o estado `C`, para o maker abrir e reenviar.

**Ação front:** permitir abrir/editar a linha em `C` (reenvio com `validar=null`/`validacao=null`).

### 14.4 Processo disciplinar
- `POST/PUT .../processo-disciplinar`: passam a ser gravados `numeroBO`, `datPublicacaoBO`, `numeroOfa`, `dataEmissaoOfa`
  (o ecrã já os enviava; eram descartados).
- `GET api/v1/funcionarios/processo-disciplinar/{id}` devolve também os campos do formulário: `estado`, `vinculoReferente`
  (uuid do tiprel), `numeroProcesso`, `entidade`, `tipoProcesso`, `penaDisciplinar`, `dataInicioPd`, `dataFimPd`,
  `dataInicioPena`, `dataFimPena`, `numeroBO`, `datPublicacaoBO`, `numeroOrdemServico`, `dataOrdemServico`, `numeroOfa`,
  `dataEmissaoOfa` (datas `dd-MM-yyyy`). Os campos antigos mantêm-se.
- Validar aceita `SIM`/`NAO` (antes só `S` aprovava — `SIM` rejeitava). Valor desconhecido → 400.

**Ação front:** pré-preencher o formulário de edição/validação com estes campos.

### 14.5 Carreira — editar a chave vai como proposta
`PUT api/v1/funcionarios/{funcionarioId}/carreiras/{carreiraId}` numa carreira **aprovada** mudando cargo, carreira PCCS ou
escalão: a carreira aprovada **não é alterada**; é criada uma carreira **proposta** pendente. Resposta:
`id` = uuid da **proposta**, mensagem `Alteração da carreira enviada para validação.`. A validação (UPDATE) fica ancorada
na proposta (`referenciaUuid`), por isso o ecrã de validação abre a proposta. SIM troca a aprovada pela proposta; NAO só
inativa a proposta. Se `tipoCarreira` não vier, mantém o tipo da carreira aprovada.

### 14.6 Substituição
- Mês inteiramente no período conta **30 dias** também no `GET .../substituicoes/calcular` (regra do Processamento
  Salarial "1 mês tem 30 dias"); o ecrã passa a mostrar o mesmo que é gravado.
- Só meses com **mais de 15 dias** geram diferença (antes: total do período ≥ 15). Sem nenhum mês > 15 dias, não vai a validação.
- Reenvio de correção recalcula o detalhe mensal com o novo período.
- SIM sem o movimento `REM_SUBSTITUICAO` parametrizado no vínculo do substituto → 400 (antes aprovava sem pagamento).
- A Ordem de Serviço fica no **substituto** e usa o motivo e o período da substituição.

### 14.7 Cessação e reativação
- Cessação aprovada: a versão em vigor do histórico do contrato passa a terminar na data da cessação; só os def da relação
  atual ainda em vigor são fechados.
- Reativação aprovada: cria uma **nova relação laboral** e uma **nova versão do histórico** a partir da data de reativação
  até ao termo previsto do contrato (contrato a termo) ou em aberto; os def renascem como novas linhas para esse período.
  A cessação fica visível como intervalo no histórico (antes anulava as datas de fim de toda a cadeia).

### 14.8 Registar colaborador com registo rejeitado da mesma pessoa
`POST api/v1/funcionarios` com o mesmo **tipo + número de documento** de um registo de colaborador **rejeitado** (NAO):
em vez de 409, o registo rejeitado é **reaproveitado**. Os dados submetidos substituem os do rejeitado e ele volta a
`P` para validação.
- Resposta 200 com `id` = uuid do registo **rejeitado** (é o mesmo colaborador) e um alerta em `alertas`:
  `Já existia um registo rejeitado deste colaborador (documento X). Foi reaproveitado com os dados agora submetidos e voltou para validação.`
- A validação rejeitada fica no histórico (`I`); é aberta uma validação nova. O "Detalhe de alterações" mostra a
  diferença para o registo rejeitado.
- O documento ou NIF de um registo pendente, em correção ou aprovado (incluindo cessado) continua a dar 409.

**Ação front:** mostrar os `alertas` da resposta do registo (já são mostrados para contactos duplicados).

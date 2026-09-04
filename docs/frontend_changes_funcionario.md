# Alterações Front-End — Funcionário / Dossiê (ciclo CORRIGIR — estado "C")

**Data:** 2026-08-22
**Branch:** feat/dossie-corrigir-javers-restantes

---

## 1. Novo campo `direcaoAntesId` no `MobilidadeDTO`

Aplica-se à resposta de:
- `GET` detalhe/atual de mobilidade (usa `MobilidadeDTO`).

### 1.1 Campo `direcaoAntesId`

| | Valor |
|---|---|
| **Nome** | `direcaoAntesId` |
| **Tipo** | `Long` (id da direcção) |
| **Acesso** | Só leitura (não enviar no corpo do pedido) |

Complementa o já existente `dirrecaoAntes` (nome/descrição, `String`) com o **id** da direcção "Antes".
O identificador é o `Long id` — a `DirecaoEntity` **não tem uuid** —, o mesmo espaço de chaves de
`direcaoDepois` e dos selects do formulário, para permitir comparação directa "antes → depois".

---

## 2. Listas passam a refletir o estado de workflow "C" (em correção)

Contexto: os registos devolvidos ao maker ficam em estado **C** (em correção). Algumas listas liam de
vistas que não expunham o C, pelo que um registo devolvido aparecia como "Ativo" ou desaparecia. Corrigido:

- **Lista de Carreira** (`GET .../carreiras`): o estado passa a mostrar **P/C** corretamente (a vista
  colapsava C→A; agora sobrepõe-se com o estado real da tabela). Sem alteração de contrato de API — só o
  valor de `estado`/`estadoDesc` fica correto.
- **Lista de Contrato / Gestão Contratual** (`GET .../contratos`): uma **renovação** devolvida para
  correção (histórico em C) deixava de aparecer; agora o filtro inclui **C** e ela mantém-se visível.
  Sem alteração de contrato de API.

> Já corretos (sem alteração): lista de Mobilidade (já sobrepunha P/C) e lista de Dossiê/Colaboradores
> (já incluía A, P, C, I no filtro).

Valores possíveis de estado nas listas: `A` (Ativo), `P` (Pendente), `C` (Em correção), `I` (Inativo).

---

## 3. Data de início — deixa de ser rejeitada no passado (2026-08-24)

Regra anterior: no **Registo de Colaborador** (e na sua validação) e na **Renovação de Contrato**, uma
`dataInicio` anterior a hoje devolvia `400`. Isso impedia registar admissões/renovações retroativas.

Regra actual (alinhada com o DOSSIÊ — "data início não maior que sysdate"):

| Fluxo | Endpoint | Regra da `dataInicio` |
|---|---|---|
| Registo de Colaborador (gravar) | `POST .../funcionarios` | Passado **permitido**; futuro rejeitado |
| Registo de Colaborador (validar) | `PUT .../funcionarios/{id}/validar` | Passado **permitido**; futuro rejeitado |
| Novo Contrato (gravar/validar) | `.../contratos` | Passado **permitido**; futuro rejeitado (já era assim ao gravar; a validação estava incoerente e foi alinhada) |
| Renovação de Contrato | `.../renovacao` | Passado **permitido**; sem restrição de futuro (a renovação arranca tipicamente em data futura) |

Mantém-se em todos os fluxos:
- `dataInicio` é **obrigatória**;
- `dataInicio` **não pode ser posterior à `dataFim`** — mensagem: *"A Data de Início não pode ser posterior à Data de Fim."*;
- contrato a termo continua a exigir `dataFim`.

Mensagens de erro removidas (deixam de ocorrer nestes fluxos):
- *"A data de início não pode ser uma data no passado."*
- *"A data de início da renovação não pode ser uma data no passado."*

Mensagem que passa a poder ocorrer no Registo de Colaborador:
- *"A data de início não pode ser uma data no futuro."*

Impacto no front-end: retirar (se existir) o `min = hoje` no date-picker da data de início do registo de
colaborador e da renovação; no registo de colaborador definir antes `max = hoje`.

---

## 4. Validação de NIF contra a API — mensagens de erro específicas (2026-08-24)

Aplica-se ao registo e à validação de colaborador (`validarDadosPessoais`). Sem alteração de contrato:
continua a ser `409 Conflict`, só muda o texto da mensagem.

**Antes:** uma única mensagem genérica — *"O NIF introduzido não corresponde ao colaborador selecionado"*.

**Agora:**

| Situação | Mensagem |
|---|---|
| NIF não existe no cadastro de contribuintes | `O NIF <nif> não foi encontrado no cadastro de contribuintes. Confirme o número introduzido.` |
| NIF existe mas os dados não batem | `O NIF <nif> pertence a outra pessoa: Nome: introduziu "X", no cadastro do NIF consta "Y"; Data de nascimento: ... Confirme o NIF e os dados do colaborador.` |

A segunda mensagem lista **todos** os campos divergentes (Nome, Nome da mãe, Nome do pai, Data de
nascimento), separados por `;` — o front-end pode mostrá-la tal como vem, ou partir por `;` para
destacar cada campo no formulário.

**API em baixo continua a deixar gravar (fail-open).** Falha de rede, timeout ou erro HTTP da API de
NIF não bloqueia o registo — fica só um `warn` no log. Passou a ser igualmente fail-open a resposta
`200` sem corpo ou sem o bloco `entries` (antes dava erro ao utilizador). Só uma lista de `entries`
vazia é tratada como "NIF inexistente".

Inalterado: NIF obrigatório, 9 dígitos, único entre colaboradores; comparação tolerante a acentos,
caixa e espaços; campo em falta de um dos lados não invalida.

---

## 5. Notificação — o utilizador deixa de indicar emails (2026-08-25)

**Endpoint:** `POST api/v1/funcionarios/notificacoes/notificar`

O ecrã passa a escolher apenas **tipos de destinatário**; os endereços de email são sempre
resolvidos pelo backend. Já temos os três lados: o email do colaborador (contactos), o do
responsável da direcção/secção onde ele está colocado, e o de quem está a registar (perfil IAM
do utilizador autenticado).

### 5.1 Campo removido do pedido: `emailsAdicionais`

| | |
|---|---|
| **Campo** | `emailsAdicionais` (lista de emails) |
| **Estado** | **Removido** — deixa de ser aceite |
| **Substituto** | nenhum; usar o tipo `RESPONSAVEL_COLABORADOR` em `destinatarios` |

Impacto no front-end: **retirar o multiselect "Email do Responsável"** do ecrã de notificação.

### 5.2 Endpoint removido: `GET configuracao/responsaveis/emails`

Existia só para preencher esse multiselect. **Deixa de existir** (404). Retirar a chamada.

### 5.3 `destinatarios` — obrigatório quando `notificar = true`

Valores aceites (domínio `DESTINATARIO_NOTIFICACAO`):

| Valor | Email usado |
|---|---|
| `COLABORADOR` | contacto do colaborador do tipo `EMAIL` |
| `RESPONSAVEL_COLABORADOR` | chefia da **secção** onde está colocado; se a secção não tiver responsável activo, sobe para a **chefia da direcção** (a linha de `RH_T_RESPONSAVEL` sem secção). O email vem dos contactos do funcionário responsável |
| `RESPONSAVEL_REGISTO` | utilizador autenticado que está a fazer o registo (**passa a funcionar**; antes era ignorado com um aviso) |

Qualquer outro valor → `400`. Lista vazia/ausente com `notificar = true` → `400` com a mensagem
*"Indique pelo menos um destinatário da notificação."*
Com `notificar = false` a lista continua a poder vir vazia (pedido legítimo, sem efeito).

### 5.4 `referenciaId` — passa a ser validado como campo do pedido

Já era obrigatório na prática (`RH_T_NOTIFICACAO.REFERENCIA_ID` é NOT NULL), mas só rebentava ao
gravar, devolvendo um erro vindo da entidade. Agora é validado à entrada:

| | Antes | Agora |
|---|---|---|
| Sem `referenciaId` | `400` *"Violação de restrição nos dados"* | `400` *"Erro de validação nos campos enviados — Campo 'referenciaId': The field &lt;referenciaId&gt; is required"* |

Sem alteração de comportamento — só a mensagem fica accionável pelo formulário.

### 5.5 Resposta enriquecida

Antes: `{ "enviados": 2, "message": "..." }`.
Agora acrescenta dois campos, para o ecrã poder dizer *quem* recebeu e *o que falhou*:

```json
{
  "enviados": 2,
  "message": "Notificação enviada para 2 destinatário(s).",
  "destinatarios": [
    { "tipo": "COLABORADOR", "nome": "João Silva", "email": "joao@x.cv" },
    { "tipo": "RESPONSAVEL_COLABORADOR", "nome": "Ana Dias", "email": "ana@x.cv" }
  ],
  "semEmail": ["RESPONSAVEL_REGISTO"]
}
```

`semEmail` lista os tipos pedidos que não deram nenhum endereço — colaborador sem contacto de
email, direcção sem responsável configurado, ou sessão sem perfil IAM. Sugestão: mostrar como
aviso ("O responsável da direcção não tem email configurado"), não como erro — os restantes
foram enviados.

Nota: `{"enviados": 0, "message": "Notificação não solicitada."}` (caso `notificar = false`)
mantém-se sem os campos novos.

---

## Novos campos `estado` / `estadoDesc` no GET do dossiê

**Data:** 2026-08-27

Aplica-se à resposta de `GET /api/v1/funcionarios/{id}` (`FuncionarioResponseDTO`).

Quatro blocos passam a expor o estado de workflow (mesma convenção já usada nas outras secções —
`estado` = código `P`/`C`/`A`/`I`/`E`, `estadoDesc` = descrição legível ex. "Pendente"):

| Bloco | Caminho na resposta | Origem do estado |
|---|---|---|
| Funcionário | `dadosPessoais.estado` / `dadosPessoais.estadoDesc` | estado do próprio funcionário |
| Subsídios | `dadosContratuais.subsidios[].estado` / `.estadoDesc` | `RH_T_DEF_REMUNERACOES` |
| Encargos/Descontos | `dadosContratuais.encargosDescontos[].estado` / `.estadoDesc` | `RH_T_DEF_PAGAMENTOS` |
| Anexos | `anexos[].estado` / `.estadoDesc` | `DocumentoEntity` |

Só leitura (não enviar no corpo). Alteração **aditiva** — nenhum campo existente mudou.

Nota: `AnexoRespDTO` é partilhado, pelo que `estado`/`estadoDesc` passam a aparecer também noutras
respostas que o usem (missão/serviço, assiduidade, …); onde o respectivo mapper não os preenche vêm `null`.

---

## Renovação de contrato — request deixa de aceitar `tipoContratoId` / `tipoVinculoId`

**Data:** 2026-08-27

Endpoints afetados (corpo `RenovacaoContratoDTO.dadosRenovacao`, `RenovarContratoReqDTO`):
- `POST /api/v1/funcionarios/{idFuncionario}/renovacao-contrato/{contratoId}`
- `POST /api/v1/funcionarios/{idFuncionario}/validar-renovacao-contrato/{contratoId}`

O `RenovarContratoReqDTO` passa a ter **apenas** `dataInicio`, `dataFim`, `duracaoMeses`. Removidos
`tipoContratoId` e `tipoVinculoId` — na renovação o tipo de contrato/vínculo são **os do contrato
atual** (não se alteram), logo não precisam de vir do formulário. Enviar esses campos deixa de ter
efeito (são ignorados pela desserialização).

Leitura mantém tudo: o GET `.../renovacao-contrato/{contratoId}` (`RenovacaoDetalheDTO`, com `atual` e
`renovacao`) e o `RenovarContratoRespDTO` continuam a expor `tipoContratoId`/`tipoContratoDesc` e
`tipoVinculoId`/`tipoVinculoDesc` para a parte informativa do modal.

---

## Mobilidade — campos `Antes`/`Depois` renomeados para `Origem`/`Destino` (breaking)

**Data:** 2026-09-04

Endpoints afetados (todos os que devolvem/aceitam `MobilidadeDTO`):
- `GET /api/v1/funcionarios/mobilidades/{idFuncionario}/atual`
- `GET`/`POST`/`PUT` de detalhe, registo e validação de mobilidade.

Os sufixos `Antes`/`Depois` não descreviam o que os campos são — são a **origem** e o **destino** da
mobilidade. Renomeação directa (mesmos tipos, mesma semântica, mesmo acesso):

| Antigo | Novo | Tipo | Acesso |
|---|---|---|---|
| `dirrecaoAntes` | `direcaoOrigemDesc` | `String` | só leitura |
| `direcaoAntesId` | `direcaoOrigemId` | `Long` | só leitura |
| `direcaoDepois` | `direcaoDestino` | `Long` | leitura/escrita |
| `direcaoDepoisDesc` | `direcaoDestinoDesc` | `String` | só leitura |
| `seccaoAntes` | `seccaoOrigemDesc` | `String` | só leitura |
| `seccaoDepois` | `seccaoDestino` | `Long` | leitura/escrita |
| `seccaoDepoisDesc` | `seccaoDestinoDesc` | `String` | só leitura |
| `localTrabalhoAntes` | `localTrabalhoOrigemDesc` | `String` | só leitura |
| `localTrabalhoDepois` | `localTrabalhoDestino` | `Long` | leitura/escrita |
| `localTrabalhoDepoisDesc` | `localTrabalhoDestinoDesc` | `String` | só leitura |

De passagem corrige-se o gralho `dirrecaoAntes` (duplo "r"). Os nomes antigos **deixam de existir**: os
de escrita (`direcaoDepois`, `seccaoDepois`, `localTrabalhoDepois`) passam a ser ignorados na
desserialização, pelo que a mobilidade gravaria sem destino se o front não for actualizado.

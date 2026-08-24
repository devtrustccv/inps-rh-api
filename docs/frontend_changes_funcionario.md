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

# Missão de Serviço — guia de implementação para o front-end

**Última actualização:** 2026-09-16 · **Base de todos os endpoints:** `/api/v1/missao-servico`

> Versão em HTML para partilhar: [evidencias_missao.html](evidencias_missao.html) — gerada a partir
> deste ficheiro; em caso de divergência, vale o Markdown.

Este documento substitui o changelog anterior. Está organizado **por ecrã**, pela ordem em que o
utilizador os percorre. Cada capítulo traz os *lookups* que o ecrã precisa, o `GET` que o preenche,
o payload da gravação e os erros esperados. Tudo aqui foi verificado contra a API em execução a
2026-09-15.

O changelog cronológico e a documentação do modelo anterior estão no fim, em **[Histórico](#histórico)**.

---

## 1. O que mudou — leitura obrigatória

A missão **deixou de ter uma etapa única**. Passou a ter **quatro processos independentes**, cada um
com a sua etapa e o seu percurso:

| Processo | Percurso |
| --- | --- |
| `BILHETE_PASSAGEM` | Prestador Serviço → Emissão Requisição → Logística → Validação UGAL → Aprovação RH → Cabimento → Autorização → Pagamento |
| `ALOJAMENTO` | igual ao bilhete |
| `SEGURO_VIAGEM` | começa na **Logística** — não passa por prestadores nem requisição |
| `AJUDA_CUSTO` | começa na **Logística** |

A submissão cria os quatro de uma vez. Cada um avança ao seu ritmo: o bilhete pode estar em
Cabimento enquanto a ajuda de custo ainda está na Logística.

**Consequências directas:**

1. As rotas de etapa levam o tipo de processo no caminho: `/{missaoUuid}/processos/{tipoProcesso}/{etapa}`.
2. A lista de trabalho é de **processos**, não de missões.
3. O ecrã a mostrar decide-se pela **etapa do processo**.
4. `AUTORIZACAO` é agora uma **etapa própria**. Acabou o artifício de distinguir Cabimentação de
   Autorização pelo `estadoCabimento`.

### Alterações de 2026-09-16

| Alteração | Impacto no front-end | Detalhe |
|---|---|---|
| Os dez endpoints antigos foram removidos | Passam a responder **404** | tabela abaixo |
| As gravações devolvem um objecto em vez de um mapa | Nenhum para quem lê `id`, `etapa`, `nrMissao`, `total` ou `designacao`: os nomes mantêm-se. Há campos novos, e o `cancelar` passa a ter corpo | secção "Resposta das gravações" |
| Três notificações novas: confirmação do pedido, ajuda de custo paga e alteração da missão | Aparecem em "Ver Notificação" | capítulos 7, 14 e 16 |
| Os avisos de logística e de cancelamento ao colaborador passam a ir por email (antes ficavam só gravados) | Nenhum na API; o `estado` passa a `Enviado`/`Erro`, ou `Pendente` se o colaborador não tiver email | capítulos 10 e 17 |
| Correcção do guia: o exemplo de Alojamento tinha `numeroDias` e `valor`, que não existem | Enviar `valorTotal` (opcional) e as datas; `colaboradorIds` permite agrupar colaboradores | capítulo 10 |

### Endpoints antigos — removidos

Os dez endpoints do modelo anterior **foram removidos a 2026-09-16** e passam a responder **404**.

| Antigo (removido) | Novo |
|---|---|
| `GET`/`PUT /{uuid}/analise` | `GET`/`PUT /{uuid}/processos/{tipoProcesso}/prestadores` |
| `GET`/`PUT /{uuid}/emissao-requisicao` | `GET`/`PUT /{uuid}/processos/{tipoProcesso}/requisicoes` |
| `GET`/`PUT /{uuid}/logistica` | `GET`/`PUT /{uuid}/processos/{tipoProcesso}/logistica` |
| `GET`/`PUT /{uuid}/cabimento` | `GET`/`PUT /{uuid}/processos/{tipoProcesso}/cabimento` |
| `GET`/`PUT /{uuid}/autorizacao` | `GET`/`PUT /{uuid}/processos/{tipoProcesso}/autorizacao` |

Mantêm o caminho: `POST /submissao`, `GET`/`PUT /{uuid}/submissao`, `GET /{uuid}`,
`GET /api/v1/missao-servico`, `PATCH /{id}/cancelar` e `GET`/`PUT /{uuid}/pagamento`.


---

## 2. Regras transversais

Valem para todos os ecrãs de etapa. Lê isto uma vez e não precisas de o repetir por capítulo.

### `SAVE` vs `NEXT`

Todos os `PUT` de etapa aceitam `processoEtapaAction`:

- **`SAVE`** — grava e fica na etapa. Grava mesmo fora de ordem (fica só um aviso no log), para não
  perder o que o utilizador preencheu.
- **`NEXT`** — valida, grava e avança. Exige que o processo já tenha atingido a etapa do ecrã.

A etapa **nunca retrocede**, excepto por parecer desfavorável, que devolve o processo à etapa
anterior de propósito.

### A guarda de etapa está no `PUT`, não no `GET`

Gravar numa etapa que o processo não percorre (`prestadores` em `SEGURO_VIAGEM`, por exemplo) ou
fora de sequência dá **400**. O `GET` correspondente devolve **200** com as listas vazias.

**Não uses o `GET` para decidir se o ecrã existe** — usa o percurso do tipo de processo (tabela do
capítulo 1).

### Identificadores de colaborador

Onde há colaboradores, o `GET` devolve dois identificadores lado a lado: `funUuid` (do funcionário)
e `uuid` (do colaborador *desta* missão). **Ambos são aceites** na gravação, em todos os campos:
`colaboradorId`, `colaboradorIds` e `funcionarioUuids`.

### Sincronização de listas

Nos `PUT` que recebem arrays (emails do prestador, linhas de logística, requisições):

| No payload | Efeito |
|---|---|
| item **sem** `id` | cria |
| item **com** `id` | actualiza |
| item **omitido** do array | inactiva (`estado: "I"`) |
| campo a `null` | preserva o que está gravado |
| array `[]` | inactiva tudo |

Regravar o mesmo payload **não duplica linhas** — os ids mantêm-se.

### Anexos — fluxo de dois passos

O campo `documento` de um anexo **não é o nome do ficheiro**: é o `fileId` devolvido pelo upload.

1. `POST /api/v1/documento/private` com o ficheiro → devolve o `fileId`
2. Enviar esse `fileId` em `documento`, com o `tipoDocumentoId` do lookup
3. Para ver ou descarregar: `GET /api/v1/documento?fileId={documento}` → devolve uma **URL
   pré-assinada** do storage, com validade limitada

Enviar um nome de ficheiro inventado grava o texto mas o passo 3 responde **400**. É este `GET` que
serve o ícone "Ver documento" do ecrã de Validação UGAL.

Anexos enviados pelo ecrã ficam em `estado: "P"`; os gerados pelo sistema (PDF da requisição) ficam
em `"A"`.

### Anexos e PDFs nunca bloqueiam o fluxo

Se o storage falhar, a etapa grava na mesma e devolve **200**; fica um `ERROR` no log do servidor e
o documento não aparece na resposta. A nota de encomenda continua disponível em *Extrair Requisição*,
que a gera a partir dos dados.

### Formato dos erros

Todos os erros de validação vêm assim, com a mensagem em `title`:

```jsonc
{ "type": "about:blank", "title": "ambitoMissao é obrigatório", "status": 400,
  "instance": "/api/v1/missao-servico/submissao", "igrpType": "validation" }
```

### Encoding

Enviar sempre `Content-Type: application/json; charset=utf-8`.

### Resposta das gravações

Nenhuma gravação devolve um mapa: cada uma tem um objecto próprio, que aparece no Swagger.

| Endpoints | Objecto | Campos |
|---|---|---|
| `POST /submissao`, `PUT /{uuid}/submissao` | `MissaoSubmissaoGravadaResponseDTO` | `id` (uuid da missão), `nrMissao`, `nrMissaoFormatado` |
| `PUT` de etapa (`prestadores`, `requisicoes`, `logistica`, `validacao-ugal`, `aprovacao-rh`, `cabimento`, `autorizacao`) | `ProcessoEtapaGravadaResponseDTO` | `id` (uuid do processo), `etapa`, `parecer`, `estadoMissao` |
| `PUT .../avaliacao` | `AvaliacaoPrestadorGravadaResponseDTO` | `id` (uuid da avaliação), `total`, `designacao` |
| `PUT /{uuid}/pagamento`, `PATCH /{id}/cancelar`, `POST`/`PUT /prestadores` | `SuccessResponseDTO` | `sucesso`, `id` (uuid), `mensagem`, `alertas` |

No `ProcessoEtapaGravadaResponseDTO`:

- `parecer` só vem preenchido em `validacao-ugal` e `aprovacao-rh`;
- `estadoMissao` só vem preenchido em `autorizacao`;
- nas restantes etapas os dois vêm a `null`.

```jsonc
// PUT /{uuid}/processos/AJUDA_CUSTO/logistica
{ "id": "…", "etapa": "VALIDACAO_UGAL", "parecer": null, "estadoMissao": null }

// PATCH /{id}/cancelar — antes vinha sem corpo
{ "sucesso": true, "id": "…", "mensagem": "Missão cancelada", "alertas": [] }
```

---

## 3. *Lookups* partilhados

Cinco campos não são servidos pelo módulo de missão — vêm da parametrização partilhada. Todos
devolvem `{ "label": "...", "value": 123 }`; o `value` é o que segue no payload.

| Campo do ecrã | Endpoint | Referência |
| --- | --- | --- |
| País de Destino | `GET /api/v1/parametrizacao/geografias?nivelDetalhe=1` | 223 países; Cabo Verde = `1238` |
| Ilha | `GET /api/v1/parametrizacao/geografias?nivelDetalhe=2&geogrId=1238` | 10 ilhas; só para destino em Cabo Verde |
| Concelho | `GET /api/v1/parametrizacao/geografias?nivelDetalhe=3&geogrId={ilhaId}` | Santiago = `12387` → Praia = `1238704` |
| Tipo Documento | `GET /api/v1/parametrizacao/tipo-documento/ativos` | 28 tipos; `value` é o `tipoDocumentoId` |
| Entidade (`entId`) | `GET /api/v1/parametrizacao/entidades/ativos` | `value` é o `entId` do prestador |
| Colaboradores | `GET /api/v1/funcionarios?pageNumber=0&pageSize=10` | pesquisa de colaborador |

Os *selects* de domínio (parecer, etapa, tipo de processo, avaliação) **vêm na resposta do próprio
ecrã** — ver cada capítulo.

> `entidades/ativos` devolve **11 981 registos** de uma vez, sem filtro nem paginação. Para a caixa
> "Pesquisar prestador de serviços…" é preciso filtrar no cliente ou pedir um endpoint de pesquisa.

---

## 4. Ecrã — Gestão de Prestadores de Serviço

Menu próprio, independente do fluxo da missão. É aqui que se parametrizam os prestadores que depois
se escolhem na etapa Prestadores Serviço.

| Acção | Método | Path |
|---|---|---|
| Lista | `GET` | `/prestadores` |
| Detalhe (ecrã de edição) | `GET` | `/prestadores/{uuid}` |
| Registar | `POST` | `/prestadores` |
| Editar | `PUT` | `/prestadores/{uuid}` |
| Ver Avaliação | `GET` | `/prestadores/{uuid}/avaliacoes` |

**Lookups:** `entidades/ativos` (campo Nome → `entId`) e `geografias?nivelDetalhe=2&geogrId=1238` (Ilha).

**Registar / Editar:**
```jsonc
{
  "entId": 100000007,                      // obrigatório — do lookup de entidades
  "nome": "Atlantico Viagens",             // se omitido, usa-se o nome da entidade
  "nif": "200987654",
  "email": "geral@atlanticoviagens.cv",    // obrigatório — email principal
  "telefone": "2612345",
  "ilhaId": 12387,
  "morada": "Av. Cidade de Lisboa, Praia",
  "estado": "A",                           // no registo, por defeito "A"
  "emails": [                              // aba "Outros Email"; null = não mexer
    { "email": "reservas@atlanticoviagens.cv" },   // sem id → cria
    { "id": 19, "email": "financeiro@..." }        // com id → mantém
  ]
}
```
→ `{ "sucesso": true, "id": "<uuid do prestador>", "mensagem": "Prestador gravado", "alertas": [] }` — repara que o uuid vem em **`id`**, não em `uuid`.

O array `emails` segue a [sincronização de listas](#sincronização-de-listas): um email omitido passa
a `estado: "I"`. O `GET /prestadores/{uuid}` devolve **todos** os emails com o seu estado (é o ecrã
de edição); o ecrã da etapa só recebe os activos.

**Erros:**

| Situação | Resposta |
|---|---|
| Sem `entId` | **400** `entId é obrigatório` |
| Sem `email` | **400** `email é obrigatório` |
| `entId` que não existe nas entidades | **400** `Entidade inválida: {entId}` |
| Entidade já registada noutro prestador | **400** `Já existe um prestador registado para a entidade {entId}` |
| Email principal ou adicional mal formado | **400** `Email inválido: {valor}` |
| Prestador inexistente (`GET`/`PUT`) | **404** |

---

## 5. Ecrã — Lista Missão

`GET /api/v1/missao-servico` — paginada. Filtros: nº de missão e período.

Cada linha traz a missão **e a sub-lista dos seus processos**, que é o que a linha expande:

```jsonc
{ "content": [ {
    "uuid": "…", "nrMissaoFormatado": "3/2026", "destino": "…",
    "nacionalInternacional": "Nacional", "dataMissao": "2026-11-10",
    "estado": "A", "estadoDesc": "Activo",        // A | I (cancelada) | FINALIZADO
    "etapa": "PRESTADOR_SERVICO",                  // etapa menos avançada dos processos activos
    "situacao": "PENDENTE_REQUISICAO", "situacaoDesc": "Pendente de Requisição",
    "processos": [
      { "uuid": "…", "tipoProcesso": "BILHETE_PASSAGEM", "tipoProcessoDesc": "Bilhete Passagem",
        "etapa": "LOGISTICA", "etapaDesc": "Processamento Logístico",
        "estado": "A", "valorTotal": 223000 }
    ] } ],
  "pageNumber": 0, "pageSize": 10, "totalElements": 3, "totalPages": 1 }
```

Acções da linha: Editar Missão, Cancelar Missão, Ver Notificação, Ver Alerta. Acções da sub-linha:
Ver/Executar Processo (leva ao ecrã da etapa daquele processo) e Avaliar Prestador.

> O processo com `estado: "I"` é um processo inactivo — tipicamente o `ALOJAMENTO` quando a missão
> tem "Alojamento = Não". Mostrar a cinzento ou esconder, mas não oferecer "Executar".

---

## 6. Ecrã — Lista Missão por Etapa

A lista de trabalho. `GET /processos?etapa=&tipoProcesso=` — uma linha **por processo**, paginada.

```jsonc
{ "content": [ {
    "missaoUuid": "…", "nrMissaoFormatado": "3/2026",
    "nacionalInternacional": "Nacional", "destino": "…",
    "dataInicio": "2026-11-10", "dataFim": "2026-11-14",
    "processoUuid": "…", "tipoProcesso": "AJUDA_CUSTO", "tipoProcessoDesc": "Ajuda Custo",
    "etapa": "LOGISTICA", "etapaDesc": "Processamento Logístico" } ],
  "opcoesEtapa":        [ { "valor": "PRESTADOR_SERVICO", "descricao": "Prestador Serviço" }, … ],
  "opcoesTipoProcesso": [ { "valor": "BILHETE_PASSAGEM",  "descricao": "Bilhete Passagem"  }, … ],
  "pageNumber": 0, "pageSize": 10, "totalElements": 7, "totalPages": 1 }
```

Os dois *selects* do filtro vêm na própria resposta: `opcoesEtapa` (as 8 etapas, pela ordem do fluxo)
e `opcoesTipoProcesso` (os 4 tipos). Usar `valor` no pedido e `descricao` no ecrã.

Ambos os filtros são **opcionais na API** — sem eles devolve todos os processos. O ecrã marca a
Etapa como obrigatória; essa exigência é do lado do front-end.

---

## 7. Ecrã — Nova Missão / Registo de Missão

| Acção | Método | Path |
|---|---|---|
| Criar | `POST` | `/submissao` |
| Carregar para edição | `GET` | `/{uuid}/submissao` |
| Editar | `PUT` | `/{uuid}/submissao` |

**Lookups:** países, ilhas, concelhos, tipos de documento e a pesquisa de colaboradores.

```jsonc
{
  "paisDestinoId": 1238,                   // obrigatório
  "ilhaId": 12387,                         // só se o destino for Cabo Verde
  "concelhoId": 1238704,                   // idem
  "descricaoDestino": "Praia — formação",  // obrigatório
  "ambitoMissao": "Formação sobre RH",     // obrigatório
  "dataInicio": "2026-11-10",              // obrigatório
  "dataFim": "2026-11-14",                 // obrigatório, >= dataInicio
  "alojamento": true,                      // a instituição trata do alojamento?
  "autorizadoPor": "Director de RH",       // obrigatório
  "dataAutorizacao": "2026-09-15",         // obrigatório
  "colaboradores": [                       // obrigatório, pelo menos um
    { "colaboradorId": "<funUuid>", "numeroDocumento": "PA496450" },
    { "colaboradorId": "<funUuid>" }       // sem numeroDocumento usa o do funcionário
  ],
  "documentos": [ { "tipoDocumentoId": 20, "documento": "convite.pdf" } ],
  "processoEtapaAction": "SAVE"
}
```
→ `{ "id": "<uuid da missão>", "nrMissao": 3, "nrMissaoFormatado": "3/2026" }`

**O que acontece ao gravar pela primeira vez:** são criados os **4 processos**, cada um na etapa
inicial do seu percurso — `BILHETE_PASSAGEM` e `ALOJAMENTO` em `PRESTADOR_SERVICO`, `SEGURO_VIAGEM`
e `AJUDA_CUSTO` em `LOGISTICA`.

**O campo `alojamento` comanda o processo `ALOJAMENTO`:** a `false` inactiva-o (`estado: "I"`), a
`true` reactiva-o. Pode alternar-se a qualquer momento editando a submissão.

`nrDias` é calculado pelo servidor a partir das datas — o ecrã mostra-o, não o envia.

**Notificações:**

| Quando | Quem recebe | Tipo |
|---|---|---|
| A missão é criada (`POST`) | cada colaborador | `MISSAO_CONFIRMACAO_PEDIDO` |
| Um colaborador é acrescentado na edição (`PUT`) | só esse colaborador | `MISSAO_CONFIRMACAO_PEDIDO` |
| A edição muda país, ilha, concelho, destino ou datas de uma missão activa | os colaboradores que já estavam na missão | `MISSAO_ALTERACAO` |
| Idem, com algum processo já depois da primeira etapa | também os prestadores e os restantes destinatários já contactados, por email | `MISSAO_ALTERACAO` |

O aviso de alteração lista cada campo mudado com o valor anterior, por exemplo
`- Data de fim: 2026-11-15 (antes: 2026-11-14)`. Uma edição que não mexe nesses campos não notifica
ninguém.

O email do colaborador vem dos seus contactos. Se o colaborador não tiver email, a notificação
fica gravada com estado `Pendente` e aparece no portal.

O `GET /{uuid}/submissao` devolve `processos[]`, `colaboradores[]` (com `funUuid` e `uuid`),
`documentos[]` e a auditoria (`userRegistoName`, `dataRegisto`, …).

> `GET /{uuid}` é um resumo e **não** traz `processos[]`. Para o ecrã de edição usar
> `GET /{uuid}/submissao`; para a listagem, a lista geral.

**Erros:**

| Situação | Resposta |
|---|---|
| `dataFim` anterior a `dataInicio` | **400** `dataFim não pode ser anterior a dataInicio` |
| Sem colaboradores | **400** `colaboradores é obrigatório` |
| Sem `paisDestinoId`, `descricaoDestino`, `ambitoMissao`, `dataInicio`, `dataFim`, `autorizadoPor` ou `dataAutorizacao` | **400** `{campo} é obrigatório` |

> `alojamento` é o único campo marcado com `*` no ecrã que a API aceita em falta: quando não vem, a
> criação assume **Sim**. É um valor por defeito deliberado.

---

## 8. Ecrã — Prestadores Serviço *(ex-Análise)*

Só em `BILHETE_PASSAGEM` e `ALOJAMENTO`.

`GET`/`PUT /{uuid}/processos/{tipoProcesso}/prestadores`

O `GET` devolve os prestadores **já escolhidos** e o texto da notificação. O catálogo para escolher
vem de `GET /prestadores` (capítulo 4).

```jsonc
{
  "prestadores": ["<paramPrestUuid>", "<paramPrestUuid>"],   // 1 a 3; lista completa
  "notificacao": {
    "assunto": "Pedido de proposta — Missão 3/2026",
    "corpoEmail": "Exmos. Senhores, solicitamos proposta…"   // vazio = template parametrizado
  },
  "processoEtapaAction": "NEXT"
}
```

No `GET`, cada prestador traz `emails[]` — o principal **mais os adicionais activos**. São esses que
recebem o pedido de proposta no `NEXT`.

A coluna **Nota Avaliação** vem em `notaAvaliacao` (total 0–100), `notaAvaliacaoDesignacao`
(`A`–`D`) e `notaAvaliacaoDesc` (ex.: "Fornecedor Preferencial"). É a avaliação **mais recente**
desse prestador, de qualquer missão; vem `null` enquanto nunca tiver sido avaliado.

`NEXT` → etapa passa a `EMISSAO_REQUISICAO` e notifica todos esses emails.

**Erros:**

| Situação | Resposta |
|---|---|
| Mais de 3 prestadores **distintos** | **400** `Máximo de 3 prestadores por processo` |
| Lista vazia | **400** `Selecione pelo menos um prestador` |
| Em `SEGURO_VIAGEM` ou `AJUDA_CUSTO` | **400** `O processo {tipo} não passa pela etapa PRESTADOR_SERVICO` |
| `tipoProcesso` inexistente | **400** `Tipo de processo inválido: {valor}` |

> Prestadores repetidos no array são deduplicados antes de se validar o limite — enviar o mesmo
> prestador duas vezes não consome duas vagas.

---

## 9. Ecrã — Emissão de Requisição

Só em `BILHETE_PASSAGEM` e `ALOJAMENTO`.

`GET`/`PUT /{uuid}/processos/{tipoProcesso}/requisicoes`

Uma requisição **por prestador**, cada uma com N colaboradores. O `GET` devolve uma linha por
prestador escolhido na etapa anterior, e `colaboradoresMissao[]` para o multiselect.

```jsonc
{
  "requisicoes": [
    { "missaoPrestUuid": "…",              // do GET: requisicoes[].missaoPrestUuid
      "selecionado": true,
      "funcionarioUuids": ["<funUuid>"],   // colaboradores desta requisição
      "valorTotal": 125000,
      "proposta": { "tipoDocumentoId": 20, "documento": "proposta.pdf" } }
  ],
  "processoEtapaAction": "SAVE"
}
```

Ao gravar, cada requisição recebe um **número anual sequencial** e uma nota de encomenda:
`nrRequisicao: 3`, `anoRequisicao: 2026`, `notaEncomenda: "RMS-2026/3"`.

**Extrair Requisição (PDF):**
`GET /{uuid}/processos/{tipoProcesso}/requisicoes/{requisicaoUuid}/pdf` → `application/pdf`.
Gerado a partir dos dados, sempre disponível. No `NEXT` fica também arquivado em documentos.

**Erros:**

| Situação | Resposta |
|---|---|
| Colaborador em duas requisições do mesmo processo | **400** `O colaborador {nome} já está associado à requisição de {prestador}` |
| Colaborador que não é da missão | **400** `Colaborador não pertence à missão: {uuid}` |
| Prestador não escolhido na etapa anterior | **400** `Prestador não seleccionado neste processo: {uuid}` |
| Requisição sem colaboradores | **400** `Associe pelo menos um colaborador à requisição de {prestador}` |
| PDF de requisição inexistente | **404** |

---

## 10. Ecrã — Logística

Todos os processos passam por aqui. `GET`/`PUT /{uuid}/processos/{tipoProcesso}/logistica`

**Só a secção do tipo do processo é aceite.** As outras devem vir a `null` (não mexer). O `GET`
devolve `colaboradoresDisponiveis[]`, com `funUuid`, `uuid` e — quando aplicável — o prestador de
que esse colaborador vem.

O `GET` traz ainda `notificacao` (assunto e corpo do aviso ao colaborador, já preenchidos com o
template e editáveis no ecrã) e, nas linhas de bilhete, `missaoPrestUuid` e `nomePrestador` — a
coluna "Prestador Serviço" do ecrã. O prestador não se envia: vem da requisição.

```jsonc
// BILHETE_PASSAGEM
{ "bilhetesPassagem": [ { "colaboradorIds": ["<uuid>"], "valor": 125000,
                          "anexo": { "tipoDocumentoId": 21, "documento": "fatura.pdf" } } ],
  "processoEtapaAction": "SAVE" }

// SEGURO_VIAGEM
{ "segurosViagem": [ { "entId": 100000003,          // obrigatório — do lookup de entidades
                       "nomeSeguradora": null,       // null = nome da entidade
                       "colaboradorIds": ["<uuid>", "<uuid>"],
                       "valor": 45000 } ] }         // obrigatório

// ALOJAMENTO — uma linha por quarto/estadia; pode juntar vários colaboradores do mesmo prestador
{ "alojamentos": [ { "colaboradorIds": ["<uuid>"],  // ou "colaboradorId": "<uuid>" (um só)
                     "lugarHospedagem": "Hotel Praia Mar",   // obrigatório
                     "flgAlimentacao": "SIM",                // obrigatório — "SIM" | "NAO"
                     "valorDiario": 6000,                    // obrigatório
                     "valorTotal": 36000,                    // null = valorDiario × nº de dias
                     "moeda": "CVE",                         // null = CVE
                     "dataInicio": "2026-11-10",             // null = datas da missão
                     "dataFim": "2026-11-15" } ] }

// AJUDA_CUSTO
{ "ajudasCusto": [ { "colaboradorId": "<uuid>", "flgAlojamento": true,
                     "numeroDiasAlojamento": 5, "valorDiario": 6000 } ] }
```

No alojamento, o nº de dias é calculado a partir das datas (`dataFim − dataInicio + 1`). Não se envia
`numeroDias` nem `valor`: esses campos não existem e são ignorados sem erro.

### O cálculo da ajuda de custo

`valorDiario` é a **base** enviada pelo ecrã. O servidor aplica-lhe a fracção e devolve o
`valorDiario` efectivo e o `valorTotal` — ambos só de leitura no ecrã.

| `flgAlojamento` | Alimentação no processo ALOJAMENTO | Fracção | Exemplo com base 6000 × 5 dias |
|---|---|---|---|
| `false` | — | **100%** | 6000/dia → 30 000 |
| `true` | `NAO`, ou sem linha de alojamento | **⅔** | 4000/dia → 20 000 |
| `true` | `SIM` | **⅓** | 2000/dia → 10 000 |

> **A ordem importa.** O ⅓ só se aplica se o processo `ALOJAMENTO` já tiver, para esse colaborador,
> uma linha com `flgAlimentacao: "SIM"`. Gravar a ajuda de custo antes do alojamento dá ⅔ **sem
> erro nenhum**. Gravar o alojamento primeiro, ou regravar a ajuda de custo depois.

O texto do aviso pode ser diferente por processo: procura-se o template
`MISSAO_LOGISTICA_COLABORADOR_{TIPO}` (ex.: `..._BILHETE_PASSAGEM`, "Emissão de Bilhete de Viagem") e,
se não existir, usa-se o genérico `MISSAO_LOGISTICA_COLABORADOR`.

`NEXT` → `VALIDACAO_UGAL` e notifica **por email** os colaboradores das linhas do processo (`MISSAO_LOGISTICA_COLABORADOR`, uma notificação por processo). O campo `notificacao` do payload permite editar o assunto e o corpo; se vier a `null`, usa-se o template. Sem email nos contactos, fica `Pendente`.

**Erros:** campos obrigatórios em falta (`{secção}: {campo} é obrigatório`), colaborador que não é da
missão, e colaboradores de prestadores diferentes na mesma linha.

---

## 11. Ecrã — Validação UGAL

`GET`/`PUT /{uuid}/processos/{tipoProcesso}/validacao-ugal`

O `GET` traz os documentos a consultar, em três listas — `autorizacao` (anexos do registo da
missão), `requisicoes` (PDFs emitidos) e `faturas` (anexos da logística) — mais `parecerAtual`,
`historico` e as opções do *select*:

```jsonc
{ "processo": { … },
  "autorizacao": [], "requisicoes": [ { "id": 513, "tipoDocumentoDesc": "…", "documento": "…" } ],
  "faturas": [],
  "parecerAtual": null,
  "historico": [],
  "opcoesParecer": [ { "valor": "FAVORAVEL",    "descricao": "Favorável" },
                     { "valor": "DESFAVORAVEL", "descricao": "Desfavorável" } ] }
```

```jsonc
{ "parecer": "FAVORAVEL", "observacao": "Conforme.", "processoEtapaAction": "NEXT" }
```

- **Favorável + `NEXT`** → `APROVACAO_RH`.
- **Desfavorável + `NEXT`** → o processo **volta a `LOGISTICA`** e o parecer é arquivado no
  histórico (`estado: "I"`); abre-se um ciclo novo.

**Erros:**

| Situação | Resposta |
|---|---|
| Sem `parecer` | **400** `parecer é obrigatório (FAVORAVEL ou DESFAVORAVEL)` |
| Desfavorável sem `observacao` | **400** `A observação é obrigatória num parecer desfavorável` |
| `parecer` fora do domínio | **400** `Parecer inválido: {valor}` |

> O protótipo marca a Observação como opcional. É opcional **só** no parecer favorável.

---

## 12. Ecrã — Aprovação RH

`GET`/`PUT /{uuid}/processos/{tipoProcesso}/aprovacao-rh`

Dois pareceres sequenciais no mesmo ecrã. O `GET` devolve `parecerCoordenador`, `parecerDirector`,
`parecerUgal` (o que abriu a etapa, para consulta), `historico` e dois *selects*:
`opcoesParecer` e `opcoesResponsavel` (`COORDENADOR_RH`, `DIRECTOR_RH`).

```jsonc
{ "responsavel": "COORDENADOR_RH",   // obrigatório neste ecrã
  "parecer": "FAVORAVEL", "observacao": "Concordo.",
  "processoEtapaAction": "NEXT" }
```

**A ordem é fixa:** o Coordenador emite primeiro; só depois o Director. O parecer do Director é o
que faz avançar — e ao avançar dispara o cabimento, passando a etapa a `CABIMENTO`. O parecer do
Coordenador é obrigatório mas não vinculativo.

**Erros:**

| Situação | Resposta |
|---|---|
| Director antes do Coordenador | **400** `O parecer do Director exige o parecer emitido do Coordenador RH` |
| Sem `parecer` | **400** `parecer é obrigatório (FAVORAVEL ou DESFAVORAVEL)` |
| Desfavorável sem `observacao` | **400** `A observação é obrigatória num parecer desfavorável` |

---

## 13. Ecrãs — Cabimentação e Autorização

`GET`/`PUT /{uuid}/processos/{tipoProcesso}/cabimento` e `.../autorizacao`

São **etapas distintas** com a mesma estrutura de resposta — já não se distinguem pelo
`estadoCabimento`:

```jsonc
{ "processo": { "etapa": "CABIMENTO" }, "estadoMissao": "A", "valorTotal": 45000,
  "itens": [ { "logisticaUuid": "…", "referencia": "SEGURO_VIAGEM",
               "nome": "Halcyon Viagens",     // prestador, seguradora ou colaborador
               "valorTotal": 45000, "moeda": "CVE",
               "cabId": null, "estadoCabimento": null,
               "colaboradores": [ … ], "documento": null } ] }
```

**Cabimentação:**
```jsonc
{ "itens": [ { "logisticaUuid": "…", "selecionado": true,
               "cabId": null,     // só no cabimento manual/internacional
               "anexo": { "tipoDocumentoId": 21, "documento": "nota.pdf" } } ],
  "processoEtapaAction": "NEXT" }
```

**Autorização** — sem campos: `{ "processoEtapaAction": "NEXT" }`

| Acção | Efeito |
|---|---|
| Cabimentação `NEXT` | Linhas seleccionadas → `CABIMENTADO`. Só avança com **todas** cabimentadas |
| Autorização `NEXT` | **Todas** as linhas → `AUTORIZADO`; processo → `PAGAMENTO` |
| Último processo activo a chegar a `PAGAMENTO` | Missão passa a `estado: "FINALIZADO"` |

**Erros:** `Faltam cabimentar {n} linha(s) do processo`, linha que não pertence ao processo, e
alterar o `cabId` de uma linha já autorizada.

> **`cabId` continua `null`** — a integração com o SGAL ainda não existe. Acabou a autorização
> parcial: o `NEXT` exige todas as linhas.

---

## 14. Ecrã — Pagamento

`GET`/`PUT /{uuid}/pagamento` — continua a ser **por missão**, não por processo.

Exige a missão em `FINALIZADO`, ou seja, os quatro processos activos autorizados.

```jsonc
{ "referenciaPagamento": "TRF-2026/0917", "dataPagamento": "2026-09-16" }
```
→ `{ "sucesso": true, "id": "<uuid da missão>", "mensagem": "Pagamento registado", "alertas": [] }`

**Notificação:** no **primeiro** registo do pagamento, cada colaborador com linha de ajuda de custo
recebe `MISSAO_AJUDA_CUSTO` com os seus valores (valor diário, dias, total, referência e data do
pagamento). Voltar a gravar para corrigir a referência ou a data não notifica outra vez.

| Situação | Resposta |
|---|---|
| Missão por finalizar | **400** `O pagamento só pode ser registado com a missão finalizada (todos os processos autorizados)` |

---

## 15. Ecrã — Avaliar Prestador

`GET`/`PUT /{uuid}/processos/{tipoProcesso}/prestadores/{missaoPrestUuid}/avaliacao`

Acessível pela sub-lista de processos da Lista Missão. O `GET` traz tudo o que o ecrã precisa —
critérios, pesos e as opções dos *selects*:

```jsonc
{ "nomePrestador": "…", "nrMissaoFormatado": "3/2026", "tipoProcesso": "ALOJAMENTO",
  "podeAvaliar": true, "avaliado": false,
  "criterios": [ { "criterio": "SISTEMA_QUALIDADE",  "peso": 5,  "avaliacao": null, … },
                 { "criterio": "PRAZO_FORNECIMENTO", "peso": 15, … },
                 { "criterio": "QUALIDADE_PRODUTO",  "peso": 40, … },
                 { "criterio": "CAPACIDADE_RESPOSTA","peso": 20, … },
                 { "criterio": "PRECO",              "peso": 20, … } ],
  "opcoesAvaliacao": [ { "valor": "100", "descricao": "Muito Bom" },
                       { "valor": "75",  "descricao": "Bom" },
                       { "valor": "50",  "descricao": "Satisfaz" },
                       { "valor": "25",  "descricao": "Mau" } ],
  "total": null, "designacao": null, "designacaoDesc": null }
```

```jsonc
{ "sistemaQualidade": "100", "prazoFornecimento": "75", "qualidadeProduto": "100",
  "capacidadeResposta": "75", "preco": "50" }
```
→ `{ "total": 81.25, "designacao": "A", "id": "…" }`

O total é a soma de `peso × (avaliação/100)`. A classe vem do total: **A** > 75, **B** ]40;75],
**C** ]25;40], **D** [0;25] — `designacaoDesc` traz o texto (ex.: "Fornecedor Preferencial").

**Erros:** `Avaliação obrigatória para o critério {X}` e
`Avaliação inválida para {X}: {v} (valores: 100, 75, 50, 25)`.

> O protótipo mostra totais numa escala 1–5 com "Muito Bom / Bom / Regular". A API segue o texto da
> spec: total ponderado 0–100 e classes **A–D**. Vale a API.

---

## 16. Ecrã — Ver Notificação

`GET /api/v1/missao-servico/{uuid}/notificacoes` — o botão "Ver Notificação" da Lista Missão.

Devolve **todas** as notificações emitidas no âmbito da missão numa só chamada, da mais recente
para a mais antiga. Ficam gravadas com referências diferentes conforme a etapa que as gerou, e o
campo `origem` diz qual:

```jsonc
[ { "uuid": "…", "tipoNotificacao": "MISSAO_CANCELAMENTO",
    "assunto": "Cancelamento de Missão Nº 6/2026",
    "mensagem": "A missão Nº 6/2026 foi cancelada. Motivo: …",
    "email": "geral@atlanticoviagens.cv",     // null = colaborador sem email (fica "Pendente")
    "nomeReceptor": "Atlantico Viagens Teste",
    "dataEnvio": "2026-09-15", "estado": "Enviado",
    "origem": "RH_T_MISSAO_SERVICO" } ]
```

| `origem` | Notificações |
|---|---|
| `RH_T_MISSAO_PRESTADOR` | pedido de proposta (um registo por email do prestador) |
| `RH_T_MISSAO_REQUISICAO` | envio da requisição |
| `RH_T_MISSAO_COLABORADOR` | ao colaborador: logística, confirmação do pedido, ajuda de custo, alteração |
| `RH_T_MISSAO_SERVICO` | aos prestadores: cancelamento e alteração |

| `tipoNotificacao` | O que é |
|---|---|
| `MISSAO_PRESTADOR` | pedido de proposta |
| `MISSAO_EMISSAO_REQUISICAO` | requisição |
| `MISSAO_LOGISTICA_COLABORADOR` | logística da viagem (uma por processo) |
| `MISSAO_CONFIRMACAO_PEDIDO` | confirmação do pedido ao colaborador |
| `MISSAO_AJUDA_CUSTO` | ajuda de custo paga |
| `MISSAO_ALTERACAO` | alteração da missão |
| `MISSAO_CANCELAMENTO` | cancelamento |

O endpoint genérico `GET /api/v1/funcionarios/notificacoes` ganhou também os filtros
`referenciaName` e `referenciaUuid`, úteis para consultar as notificações de **uma** entidade
concreta. Para o ecrã da missão usar o endpoint acima: evita ter de somar uma chamada por
prestador, requisição e colaborador.

---

## 17. Cancelar Missão

`PATCH /{uuid}/cancelar` → `{ "motivoCancelamento": "…" }` *(obrigatório)*

Resposta: `{ "sucesso": true, "id": "<uuid da missão>", "mensagem": "Missão cancelada", "alertas": [] }`
(antes vinha sem corpo).

Inactiva a missão e, com ela, os processos, os pareceres, os colaboradores das requisições e as
avaliações.

**Notificação:** se algum processo já passou da primeira etapa do seu percurso, são renotificados
todos os que já tinham recebido email desta missão — prestadores, emails adicionais e destinatários
das requisições — com o nº da missão e o motivo. Os colaboradores que **estavam** na missão recebem o mesmo aviso por email; quem já tinha sido retirado não é avisado.

| Situação | Resposta |
|---|---|
| Sem `motivoCancelamento` | **400** `motivoCancelamento é obrigatório` |
| Missão já cancelada | **400** `A missão já está cancelada` |
| Missão finalizada | **400** `A missão está finalizada e não pode ser cancelada` |

---

## 18. Limitações conhecidas

| Tema | Situação |
|---|---|
| `cabId` (SGAL) | Não gerado — integração por definir (sem endpoint nem contrato). As linhas ficam `CABIMENTADO` com `cabId: null` |
| `valorDiario` | A base vem do cliente, sem validação. A tabela de preços da ajuda de custo nunca foi especificada |
| Pesquisa de entidades | `entidades/ativos` devolve 11 981 registos sem filtro nem paginação |
| Identidade do utilizador | `executadoPor` grava `anonymousUser` em desenvolvimento; sem bloqueio por perfil nesta fase |
| Encoding | Enviar `Content-Type: application/json; charset=utf-8` |
| **"Ver Alerta"** (Lista Missão) | O analista retirou os alertas da missão do âmbito actual: `GET /api/v1/funcionarios/alertas` não devolve nada da missão. O botão pode ficar escondido |
| Templates de notificação | Estão parametrizados em `RH_T_PARAM_NOTIFICACAO` com textos **provisórios**, a rever pelo negócio. O script para os outros ambientes é `docs/db/missao_servico_notificacoes_dml.sql` |

---

# Histórico

> **Tudo o que se segue está superado, e os endpoints aqui descritos foram removidos a 2026-09-16.** Descreve o modelo anterior, em que a missão tinha uma etapa
> única e os endpoints não levavam o tipo de processo no caminho. Fica como registo do que mudou,
> para quem ainda esteja a migrar. **Não implementar a partir daqui** — em particular, a afirmação
> "não existe etapa `AUTORIZACAO`" deixou de ser verdade, e as etapas `ANALISE` e `CABIMENTO`
> passaram a `PRESTADOR_SERVICO` e a `CABIMENTO` + `AUTORIZACAO` separadas.


### 1. Correção — `cabId` deixou de ser obrigatório

`PUT /{uuid}/cabimento` rejeitava com **400 `cabId é obrigatório`** qualquer item selecionado sem `cabId`.

Estava errado: segundo a Especificação Técnica Funcional, o número de cabimento é **gerado**, não introduzido pelo utilizador — na etapa de Autorização o campo é descrito como *"Mostra o numero de Cabimento Gerado na Etapa Anterior"*. O formulário da Cabimentação só tem selecionar, tipo de serviço, nome, valor e anexo.

### Antes
```jsonc
// 400 — cabId é obrigatório
{ "itens": [ { "logisticaId": 101, "selecionado": true, "anexo": { ... } } ],
  "processoEtapaAction": "SAVE" }
```

### Depois
```jsonc
// 200 — cabId é opcional
{ "itens": [ { "logisticaId": 101, "selecionado": true, "anexo": { ... } } ],
  "processoEtapaAction": "SAVE" }
```

> `cabId` continua a ser aceite no payload — serve os **cabimentos manuais/internacionais**, que a spec descreve como preenchidos à mão pelo departamento financeiro.

**Nota:** a geração automática do `cabId` via SGAL **ainda não está implementada** (sem endpoint nem contrato definidos). As linhas ficam `CABIMENTADO` com `cabId: null`.

---

### 2. `SAVE` vs `NEXT` — comportamento corrigido em todas as etapas

Todos os `PUT` de etapa aceitam `processoEtapaAction` com dois valores: `"SAVE"` (Gravar) e `"NEXT"` (Avançar/Cabimentar/Autorizar).

| Ação | O que faz |
|---|---|
| `SAVE` | Grava os dados do formulário. **Não** avança a etapa, **não** muda estados, **não** envia notificações. |
| `NEXT` | Grava, avança a etapa, muda estados e envia as notificações da etapa. |

### O que mudou

| Endpoint | Antes | Depois |
|---|---|---|
| `PUT /{uuid}/cabimento` | `SAVE` marcava `CABIMENTADO` e avançava a etapa | `SAVE` só grava anexos/seleção |
| `PUT /{uuid}/autorizacao` | `SAVE` marcava `AUTORIZADO` | `SAVE` só valida; só `NEXT` autoriza |
| Todos os `PUT` de etapa | `SAVE` reescrevia a etapa da missão | `SAVE` nunca toca na etapa |

---

### 3. A etapa nunca retrocede

Cada `salvar*` escrevia a etapa do seu ecrã de forma incondicional. Numa missão em `PAGAMENTO`, gravar no ecrã de submissão devolvia-a a `SUBMISSAO`, reabrindo etapas já concluídas.

Agora a transição é **monotónica**: a etapa só avança, nunca recua. Gravar num ecrã de uma etapa já ultrapassada continua a ser permitido (correções), mas não puxa o processo para trás.

Ordem das etapas: `SUBMISSAO` → `ANALISE` → `EMISSAO_REQUISICAO` → `LOGISTICA` → `CABIMENTO` → `PAGAMENTO`

---

### 4. Guarda de ordem — `NEXT` fora de sequência dá 400

| Ação | Etapa à frente da atual | Etapa já ultrapassada |
|---|---|---|
| `SAVE` | grava (só regista aviso no log) | grava |
| `NEXT` | **400** | avança (sem retroceder) |

```jsonc
// PUT /{uuid}/cabimento com NEXT numa missão em SUBMISSAO
{
  "status": 400,
  "title": "A missão encontra-se na etapa 'SUBMISSAO' — esta operação exige que já tenha atingido a etapa 'CABIMENTO'"
}
```

`PUT /{uuid}/pagamento` exige sempre que a etapa `PAGAMENTO` tenha sido atingida (não tem Gravar/Avançar).

---

### 5. Gravações idempotentes

Gravar duas vezes o mesmo formulário deixou de ter efeitos colaterais.

**Logística** — o maior problema: cada gravação **inativava e recriava** as linhas. Consequências, agora resolvidas:

| | Antes | Depois |
|---|---|---|
| `logisticaId` após re-gravar | mudavam (ex.: 132→137) | estáveis |
| Anexos | perdidos (ficavam na linha inativa) | preservados |
| Linhas em `RH_T_MISSAO_LOGISTICA` | +N por gravação | sem crescimento |

> **Importante:** os `logisticaId` que o ecrã de Cabimentação envia deixam de ser invalidados por uma gravação da Logística noutro separador.

**Autorização** — gravar duas vezes dava `400 Item sem cabimento`. Agora aceita itens já `AUTORIZADO` (idempotente).

**Anexos** — ao reenviar um anexo já existente, incluir sempre o `id` que veio no GET:

```jsonc
"anexo": { "id": 349, "tipoDocumentoId": 21, "documento": "bilhete.pdf" }  // atualiza
"anexo": { "tipoDocumentoId": 21, "documento": "bilhete.pdf" }             // cria novo e marca o anterior como eliminado
```

---

### 6. Novo campo — `colaboradoresMissao`

**Endpoints:** `GET /{uuid}/emissao-requisicao` e `GET /{uuid}/logistica`

Devolve o universo de colaboradores afetos à missão, para popular os multiselects **sem uma segunda chamada** ao `/submissao`.

```jsonc
"colaboradoresMissao": [
  {
    "id": 31,                                            // RH_T_MISSAO_COLABORADOR.id
    "uuid": "019fed6d-ae2a-755d-812b-4efb450802b0",      // uuid da linha missão-colaborador
    "funId": 958873,                                     // RH_T_FUNCIONARIOS.id
    "funUuid": "019fd75f-0b61-7406-a256-a709912e8b51",   // uuid do funcionário  ← usar este
    "nomeColaborador": "Wilson Cabral Tavares",
    "numDocumento": "19940819M002H",
    "estado": "A",
    "missaoPrestId": 17,                                 // só em /logistica
    "nomePrestador": "Halcyon Viagens"                   // só em /logistica
  }
]
```

> ⚠️ **Armadilha:** os campos `missaoColabIds`, `colaboradorIds` e `colaboradorId` dos payloads esperam o **`funUuid`** (uuid do funcionário), apesar do nome. Os outros três identificadores devolvem `400 Colaborador inválido`.

### `missaoPrestId` / `nomePrestador` (só na Logística)

Uma linha de **bilhete** ou **seguro** só pode agrupar colaboradores do **mesmo prestador** — a associação é feita na etapa de Emissão de Requisição. Agrupar colaboradores de agências diferentes devolve:

```jsonc
{ "status": 400, "title": "Prestador inconsistente para os colaboradores selecionados" }
```

Com estes campos, o multiselect pode agrupar por agência e evitar a seleção inválida:

```
▾ Colaboradores
  ── Halcyon Viagens ──────────
     ☐ Wilson Cabral Tavares
  ── Cabo Verde Travel ────────
     ☐ Tatiana Delgado Barbosa
```

Colaborador com `missaoPrestId: null` ficou de fora da Emissão de Requisição — qualquer linha de logística para ele dá `400 Requisição não encontrada para colaborador`. Deve ser desativado na UI.

---

### 7. Novo campo — `colaboradores` por linha

**Endpoints:** `GET /{uuid}/cabimento` e `GET /{uuid}/autorizacao`

O campo `nome` mostra o **prestador** (ou o colaborador, no caso da ajuda de custo), o que torna linhas distintas indistinguíveis:

```
│ 144 │ SEGURO_VIAGEM │ Impar Seguros │ 15.000 │   ← de quem?
│ 145 │ SEGURO_VIAGEM │ Impar Seguros │ 15.000 │   ← de quem?
```

Cada item passa a incluir os seus colaboradores:

```jsonc
{
  "logisticaId": 144,
  "referencia": "SEGURO_VIAGEM",
  "nome": "Impar Seguros",
  "valorTotal": 15000,
  "colaboradores": [
    { "id": 165,
      "missaoColabUuid": "019fed6d-ae2a-755d-812b-4efb450802b0",
      "funcionarioUuid": "019fd75f-0b61-7406-a256-a709912e8b51",
      "nomeColaborador": "Wilson Cabral Tavares",
      "estado": "A" }
  ]
}
```

---

### 8. `numDocumento` — passaportes deixaram de se perder

Números de documento **alfanuméricos** (ex.: passaporte `PA466262`) eram convertidos para `null` sem erro, por a coluna `RH_T_MISSAO_COLABORADOR.NUM_DOCUMENTO` ser `NUMBER`. Passou a `VARCHAR2`, e a leitura usa o funcionário como fonte de verdade.

Aplica-se retroativamente: missões já gravadas passam a mostrar o valor correto.

> **Ops:** o `ALTER TABLE` foi aplicado diretamente na BD (o Flyway está desligado e esta tabela não consta das migrações). **Tem de ser repetido noutros ambientes.**

---

### Fluxo completo — o que enviar em cada etapa

Percurso real de uma missão com 2 colaboradores repartidos por 2 agências.

### Etapa 1 · Submissão

**Lookups do ecrã:**
```
GET /api/v1/parametrizacao/geografias?nivelDetalhe=1   → países  [{label, value}]
GET /api/v1/funcionarios?pageNumber=0&pageSize=10      → colaboradores
```

**Criar:** `POST /submissao`
```jsonc
{
  "paisDestinoId": 1033,
  "descricaoDestino": "Paris - conferência CIPRES",
  "ambitoMissao": "INTERNACIONAL",
  "dataInicio": "2026-10-05",
  "dataFim": "2026-10-09",
  "autorizadoPor": "Wilson",
  "dataAutorizacao": "2026-08-10",
  "colaboradores": [
    { "colaboradorId": "019fd75f-0b61-7406-a256-a709912e8b51" },
    { "colaboradorId": "019fd759-2039-74b2-a587-cd2b5f131ae0" }
  ],
  "documentos": [ { "tipoDocumentoId": 20, "documento": "convite_cipres.pdf" } ],
  "processoEtapaAction": "SAVE"
}
```
→ `{ "nrMissao": 3, "id": "<uuid>" }`

**Editar:** `PUT /{uuid}/submissao` com o mesmo corpo.

- **Não enviar `estado`** — o backend põe `"A"`.
- `nrDias` é calculado a partir das datas.
- `documentos[].documento` é o **nome do ficheiro já carregado** via `POST /api/v1/documento/private`, não o binário.

### Etapa 2 · Análise

`GET /{uuid}/analise` → `{ missaoId, etapaAtual, prestadores: [], notificacao }`

`PUT /{uuid}/analise`
```jsonc
{
  "prestadores": [
    { "entId": 11, "nome": "Halcyon Viagens",   "email": "reservas@halcyon.cv" },
    { "entId": 12, "nome": "Cabo Verde Travel", "email": "geral@cvtravel.cv" }
  ],
  "processoEtapaAction": "SAVE"
}
```

- Máximo **3 prestadores**.
- A lista é sincronizada por inteiro: quem não vier é inativado.
- `NEXT` → envia o pedido de proposta às agências.

### Etapa 3 · Emissão de Requisição

`GET /{uuid}/emissao-requisicao` → `requisicoes[]` + `colaboradoresMissao[]`

`PUT /{uuid}/emissao-requisicao`
```jsonc
{
  "requisicoes": [
    { "missaoPrestId": 17, "selecionado": true,
      "missaoColabIds": ["019fd75f-0b61-7406-a256-a709912e8b51"],
      "documentoProposta": { "tipoDocumentoId": 19, "documento": "proposta_halcyon.pdf" } },
    { "missaoPrestId": 18, "selecionado": true,
      "missaoColabIds": ["019fd759-2039-74b2-a587-cd2b5f131ae0"],
      "documentoProposta": { "tipoDocumentoId": 19, "documento": "proposta_cvtravel.pdf" } }
  ],
  "processoEtapaAction": "SAVE"
}
```

- `missaoColabIds` = **`funUuid`**.
- Cria uma requisição por par prestador × colaborador.
- **Esta associação determina o que é possível agrupar na Logística.**
- `NEXT` → notifica os prestadores selecionados.

### Etapa 4 · Logística

`GET /{uuid}/logistica` → 4 secções + `colaboradoresMissao[]` (com prestador)

`PUT /{uuid}/logistica`
```jsonc
{
  "bilhetesPassagem": [
    { "colaboradorIds": ["<funUuid>"], "valor": 90000,
      "anexo": { "tipoDocumentoId": 21, "documento": "bilhete_wilson.pdf" } }
  ],
  "segurosViagem": [
    { "entId": 12, "nomeSeguradora": "Impar Seguros",
      "colaboradorIds": ["<funUuid>"], "valor": 15000 }
  ],
  "alojamentos": [
    { "colaboradorId": "<funUuid>", "flgAlimentacao": "NAO",
      "lugarHospedagem": "Ibis Paris Gare du Nord",
      "valorDiario": 12000, "valorTotal": 60000, "moeda": "CVE",
      "dataInicio": "2026-10-05", "dataFim": "2026-10-09" }
  ],
  "ajudasCusto": [
    { "colaboradorId": "<funUuid>", "flgAlojamento": true,
      "numeroDiasAlojamento": 5, "valorDiario": 12000 }
  ],
  "processoEtapaAction": "SAVE"
}
```

| Secção | Colaboradores | Campos obrigatórios |
|---|---|---|
| `bilhetesPassagem` | lista (mesmo prestador) | `colaboradorIds`, `valor` |
| `segurosViagem` | lista (mesmo prestador) | `entId`, `colaboradorIds`, `valor` |
| `alojamentos` | **um por linha** | `colaboradorId`, `flgAlimentacao`, `lugarHospedagem`, `valorDiario`, `valorTotal` |
| `ajudasCusto` | **um por linha** | `colaboradorId`, `flgAlojamento`, `numeroDiasAlojamento`, `valorDiario` |

**Cálculo da ajuda de custo** — o backend aplica a fração ao `valorDiario` enviado:

| Situação | Fração | Exemplo (base 12.000) |
|---|---|---|
| `flgAlojamento: false` (alojamento próprio) | 100% | 12.000/dia |
| `flgAlojamento: true` + alojamento com `flgAlimentacao: "NAO"` | ⅔ | 8.000/dia |
| `flgAlojamento: true` + alojamento com `flgAlimentacao: "SIM"` | ⅓ | 4.000/dia |

`valorTotal = valorDiário calculado × numeroDiasAlojamento`, feito no backend.

> ⚠️ O **`valorDiario` base vem do cliente** e não é validado contra nenhuma tabela de preços. A spec prevê que dependa da função do colaborador e de missão nacional/internacional, mas essa parametrização não existe. Só omitir uma secção do payload a deixa intacta.

`NEXT` → notifica os colaboradores com os detalhes de viagem.

### Etapa 5 · Cabimentação

`GET /{uuid}/cabimento` → uma linha por serviço, com `colaboradores` e `fatura`

`PUT /{uuid}/cabimento`
```jsonc
{
  "itens": [
    { "logisticaId": 142, "selecionado": true,
      "anexo": { "id": 360, "tipoDocumentoId": 21, "documento": "bilhete_wilson.pdf" } },
    { "logisticaId": 148, "selecionado": true }
  ],
  "processoEtapaAction": "SAVE"
}
```

- `SAVE` → grava anexos e seleção; `estadoCabimento` continua `null`.
- `NEXT` (**Cabimentar**) → `estadoCabimento: "CABIMENTADO"` nos selecionados.
- Pelo menos um item selecionado, senão `400 Selecione pelo menos um item`.

### Etapa 6 · Autorização

`GET /{uuid}/autorizacao` → linhas com `estadoCabimento` e `numeroCabimento`

`PUT /{uuid}/autorizacao`
```jsonc
{
  "itens": [
    { "logisticaId": 142, "autorizado": true },
    { "logisticaId": 143, "autorizado": true }
  ],
  "processoEtapaAction": "NEXT"
}
```

- Item não `CABIMENTADO` → `400 Item sem cabimento: {id}`.
- `SAVE` **não** autoriza; só `NEXT`.
- `NEXT` → `AUTORIZADO` + etapa `PAGAMENTO`.

### Etapa 7 · Pagamento

`PUT /{uuid}/pagamento`
```jsonc
{ "referenciaPagamento": "TRF-2026-0003", "dataPagamento": "2026-08-10" }
```
Sem `processoEtapaAction`.

---

### Como saber que ecrã mostrar

**Não existe etapa `AUTORIZACAO`.** Depois de cabimentar, `etapaAtual` continua `CABIMENTO` — conforme a spec, que manda escrever `'CABIMENTO'` no fim da Cabimentação e `'PAGAMENTO'` no fim da Autorização.

O frontend distingue os dois ecrãs pelo **`estadoCabimento` dos itens**:

| `etapaAtual` | `estadoCabimento` dos itens | Ecrã |
|---|---|---|
| `SUBMISSAO` | — | Submissão |
| `ANALISE` | — | Análise |
| `EMISSAO_REQUISICAO` | — | Emissão de Requisição |
| `LOGISTICA` | — | Logística |
| `CABIMENTO` | `null` | **Cabimentação** (por cabimentar) |
| `CABIMENTO` | `CABIMENTADO` | **Autorização** (por autorizar) |
| `PAGAMENTO` | `AUTORIZADO` | Pagamento / concluído |

Sequência de estados de cada linha: `null` → `CABIMENTADO` (Cabimentar) → `AUTORIZADO` (Autorizar).

### Autorização parcial

É possível autorizar apenas alguns itens. **Mas o `NEXT` avança para `PAGAMENTO` de qualquer forma**, deixando os restantes em `CABIMENTADO`. Esses itens podem ser autorizados mais tarde (a guarda de etapa permite gravar em etapas já ultrapassadas), mas **nada na UI os sinaliza** — a missão aparece como concluída.

Recomendação para o frontend: só permitir Autorizar quando todos os itens estiverem selecionados, ou avisar explicitamente sobre os que ficam por autorizar.

---


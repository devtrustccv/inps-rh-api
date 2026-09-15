# Alterações Front-End — Missão de Serviço

**Data:** 2026-08-10
**Branch:** develop
**Commits:** `a16c0989`, `dca10c31`

Base de todos os endpoints: `/api/v1/missao-servico`

> Versão em HTML para partilhar com a equipa de front-end: [evidencias_missao.html](evidencias_missao.html)
> (gerada a partir deste ficheiro — em caso de divergência, vale o Markdown).

---

## 2026-09-14 — LEIA PRIMEIRO: a missão deixou de ter uma etapa

Este é o resumo da alteração estrutural da spec de 14/09. As secções por fase, mais abaixo,
têm os payloads campo a campo.

### O que mudou

Antes, a missão tinha **uma** etapa e o ecrã avançava a missão inteira. Agora a missão tem
**quatro processos independentes**, cada um com a sua etapa e o seu percurso:

| Processo | Percurso |
| --- | --- |
| `BILHETE_PASSAGEM` | Prestador Serviço → Emissão Requisição → Logística → Validação UGAL → Aprovação RH → Cabimento → Autorização → Pagamento |
| `ALOJAMENTO` | igual ao bilhete |
| `SEGURO_VIAGEM` | começa na **Logística** (não passa por prestadores nem requisição) |
| `AJUDA_CUSTO` | começa na **Logística** |

A submissão cria os quatro processos de uma vez. Cada um avança ao seu ritmo: o bilhete pode
estar em Cabimento enquanto a ajuda de custo ainda está na Logística.

### O que isto obriga a mudar no front-end

1. **Todas as rotas de etapa passam a levar o tipo de processo no caminho:**
   `/{missaoUuid}/processos/{tipoProcesso}/{etapa}`.
2. **A lista de trabalho passa a ser de processos, não de missões** — `GET /processos?etapa=…`
   devolve uma linha por processo. A lista geral (`GET /missao-servico`) traz a missão com a
   sub-lista dos seus quatro processos e respectivas etapas.
3. **O ecrã a mostrar decide-se pela etapa do processo**, não pela etapa da missão.
4. **A guarda de etapa está no `PUT`, não no `GET`.** Gravar numa etapa que o processo não
   percorre (ex.: `prestadores` em `SEGURO_VIAGEM` ou `AJUDA_CUSTO`), ou fora de sequência, dá
   **400**. O `GET` correspondente devolve **200** com as listas vazias — não uses o `GET` para
   decidir se o ecrã existe; usa o percurso do tipo de processo (tabela acima).

### Endpoints antigos — para onde migrar

Os dez endpoints do modelo anterior continuam a responder, marcados `deprecated = true` no
Swagger. Vão ser removidos numa fase seguinte, assim que o front-end migrar.

`{tipoProcesso}` é um de `BILHETE_PASSAGEM` | `SEGURO_VIAGEM` | `AJUDA_CUSTO` | `ALOJAMENTO`.
Onde a chamada era **uma por missão**, passa a ser **uma por processo**.

| Antigo (deprecated) | Novo | Nota |
|---|---|---|
| `GET /{uuid}/analise` | `GET /{uuid}/processos/{tipoProcesso}/prestadores` | Só em `BILHETE_PASSAGEM` e `ALOJAMENTO` |
| `PUT /{uuid}/analise` | `PUT /{uuid}/processos/{tipoProcesso}/prestadores` | idem |
| `GET /{uuid}/emissao-requisicao` | `GET /{uuid}/processos/{tipoProcesso}/requisicoes` | idem |
| `PUT /{uuid}/emissao-requisicao` | `PUT /{uuid}/processos/{tipoProcesso}/requisicoes` | Uma requisição **por prestador**, com N colaboradores |
| `GET /{uuid}/logistica` | `GET /{uuid}/processos/{tipoProcesso}/logistica` | Devolve só a secção daquele processo |
| `PUT /{uuid}/logistica` | `PUT /{uuid}/processos/{tipoProcesso}/logistica` | idem |
| `GET /{uuid}/cabimento` | `GET /{uuid}/processos/{tipoProcesso}/cabimento` | |
| `PUT /{uuid}/cabimento` | `PUT /{uuid}/processos/{tipoProcesso}/cabimento` | |
| `GET /{uuid}/autorizacao` | `GET /{uuid}/processos/{tipoProcesso}/autorizacao` | Etapa própria — já não se distingue pelo `estadoCabimento` |
| `PUT /{uuid}/autorizacao` | `PUT /{uuid}/processos/{tipoProcesso}/autorizacao` | Acabou a autorização parcial |

> No Swagger, os dois `emissao-requisicao` antigos têm o parâmetro de caminho escrito `uui` em vez
> de `uuid` (gralha do modelo antigo). A URL é a mesma; só afecta quem gere cliente a partir do
> OpenAPI. As rotas novas não têm o problema.

### Endpoints novos, sem equivalente antigo

| Ecrã | Método | Path |
|---|---|---|
| Gestão de Prestadores — criar | `POST` | `/api/v1/missao-servico/prestadores` |
| Gestão de Prestadores — editar | `PUT` | `/api/v1/missao-servico/prestadores/{uuid}` |
| Gestão de Prestadores — lista | `GET` | `/api/v1/missao-servico/prestadores` |
| Gestão de Prestadores — detalhe | `GET` | `/api/v1/missao-servico/prestadores/{uuid}` |
| Ver Avaliação do prestador | `GET` | `/api/v1/missao-servico/prestadores/{uuid}/avaliacoes` |
| Lista Etapa Missão (lista de trabalho) | `GET` | `/api/v1/missao-servico/processos?etapa=&tipoProcesso=` |
| Validação UGAL | `GET`/`PUT` | `/{uuid}/processos/{tipoProcesso}/validacao-ugal` |
| Aprovação RH | `GET`/`PUT` | `/{uuid}/processos/{tipoProcesso}/aprovacao-rh` |
| Avaliar Prestador | `GET`/`PUT` | `/{uuid}/processos/{tipoProcesso}/prestadores/{missaoPrestUuid}/avaliacao` |
| Extrair Requisição (PDF) | `GET` | `/{uuid}/processos/{tipoProcesso}/requisicoes/{requisicaoUuid}/pdf` |

### Endpoints que se mantêm

`POST /submissao`, `GET`/`PUT /{uuid}/submissao`, `GET /{uuid}`, a lista `GET /api/v1/missao-servico`
e `PATCH /{id}/cancelar` mantêm o caminho. Mudou o **conteúdo**: a submissão aceita `ilhaId`/
`concelhoId` e o campo `alojamento`, e as respostas ganham `processos[]`.

`GET`/`PUT /{uuid}/pagamento` mantém o caminho e continua a ser **por missão** — mas o `PUT` passou
a exigir a missão em `FINALIZADO` (os quatro processos activos autorizados), senão devolve **400**.

### Comportamentos a ter em conta

- **Anexos e PDFs não bloqueiam o fluxo.** Se o storage falhar, a gravação da etapa é feita na
  mesma e devolve `200`; fica um `ERROR` no log do servidor e o documento não aparece na
  resposta. A nota de encomenda continua sempre disponível em *Extrair Requisição*, que a gera
  a partir dos dados.
- **Um parecer desfavorável devolve o processo à etapa anterior** e arquiva o parecer no
  histórico; abre-se um ciclo novo.
- **Na Aprovação RH a ordem é fixa:** o Coordenador emite primeiro, só depois o Director. O
  parecer do Director é o que faz avançar.
- **O valor da ajuda de custo é calculado pelo backend** a partir do valor diário que o ecrã
  envia: 100% sem alojamento da instituição, ⅔ com alojamento sem alimentação, ⅓ com
  alimentação.
- **O pagamento só aceita a missão finalizada** — ou seja, com os quatro processos autorizados.

### Validado em

Bateria de 129 passos a percorrer os ecrãs pela ordem de utilização (lista → formulário →
`GET by id` → editar → gravar → reler), com caminhos felizes e negativos, e com confirmação
directa na base de dados depois de cada escrita. Todos os payloads e respostas documentados
abaixo saíram dessa bateria, corrida contra o ambiente de desenvolvimento.

---

## 2026-09-14 — Listas e cancelamento (Fase 10)

### Lista Geral — sub-lista de processos

`GET /api/v1/missao-servico` — cada missão de `content[]` traz agora:
```jsonc
{
  "nrMissaoFormatado": "3/2026",
  "estado": "A", "estadoDesc": "Activo",           // A | I (Cancelado) | FINALIZADO (Finalizado)
  "etapa": "LOGISTICA", "etapaDesc": "Processamento Logístico",   // etapa do processo activo mais atrasado
  "situacao": "PENDENTE_FATURA", "situacaoDesc": "Pendente de Fatura",
  "processos": [
    { "uuid": "…", "tipoProcesso": "BILHETE_PASSAGEM", "tipoProcessoDesc": "Bilhete Passagem",
      "etapa": "CABIMENTO", "etapaDesc": "Cabimento", "estado": "A", "valorTotal": 180000 },
    { "uuid": "…", "tipoProcesso": "ALOJAMENTO", "etapa": "PRESTADOR_SERVICO", "estado": "I", "valorTotal": null }
  ]
}
```
- **`etapa` e `situacao` da missão:** passam a vir do **processo activo mais atrasado**. A etapa ao nível da missão fica sempre em `SUBMISSAO`.
- **Valores da `situacao`:** `PENDENTE_REQUISICAO`, `PENDENTE_FATURA`, `EM_VALIDACAO` (novo), `POR_PAGAR`, `PAGO`.
- **Link "Executar":** usar `processos[].tipoProcesso` + `processos[].etapa` para abrir o ecrã da etapa desse processo.

### Lista Etapa Missão (novo)

`GET /api/v1/missao-servico/processos?etapa=VALIDACAO_UGAL&tipoProcesso=BILHETE_PASSAGEM&pageNumber=0&pageSize=10`

- Filtros opcionais: `etapa` (domínio `TIPO_PROCESSO_ETAPA`) e `tipoProcesso` (domínio `TIPO_PROCESSO`). Um valor fora do domínio → **400**.
- Só mostra processos activos de missões activas (sem canceladas nem finalizadas).
```jsonc
{ "content": [
    { "missaoUuid": "…", "nrMissaoFormatado": "3/2026", "nacionalInternacional": "Internacional",
      "destino": "Paris", "dataInicio": "2026-10-05", "dataFim": "2026-10-09",
      "processoUuid": "…", "tipoProcesso": "BILHETE_PASSAGEM", "etapa": "VALIDACAO_UGAL", "etapaDesc": "Validação UGAL" } ],
  "pageNumber": 0, "pageSize": 10, "totalElements": 1, "totalPages": 1, "first": true, "last": true }
```

### Cancelar

`PATCH /api/v1/missao-servico/{uuid}/cancelar` — o corpo não muda: `{ "motivoCancelamento": "…" }`.

- **O que fica inactivo:** para além do que já ficava, também os processos, os pareceres, os colaboradores das requisições e as avaliações.
- **Missão já cancelada** → **400**. **Missão finalizada** → **400**.
- **Quando se notifica:** se algum processo já passou da primeira etapa do seu percurso.
- **Quem é notificado:**
  - todos os que já receberam emails desta missão: prestadores, emails adicionais e destinatários das requisições;
  - cada colaborador recebe um aviso no portal.

---

## 2026-09-14 — Cabimentação e Autorização, por processo (Fase 9)

| Ecrã | Método | Path |
|---|---|---|
| Cabimentação — carregar | `GET` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/cabimento` |
| Cabimentação — gravar / cabimentar | `PUT` | idem |
| Autorização — carregar | `GET` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/autorizacao` |
| Autorização — autorizar | `PUT` | idem |

Os dois `GET` devolvem a mesma estrutura:
```jsonc
{
  "processo": { "tipoProcesso": "BILHETE_PASSAGEM", "etapa": "CABIMENTO" },
  "estadoMissao": "A",
  "valorTotal": 180000,
  "itens": [
    { "logisticaUuid": "…", "referencia": "BILHETE_PASSAGEM", "nome": "Halcyon Viagens",
      "valorTotal": 90000, "moeda": "CVE", "cabId": null, "estadoCabimento": null,
      "colaboradores": [ { "nomeColaborador": "…" } ], "documento": { … } }
  ]
}
```
Na coluna `nome` aparece o **prestador** (bilhete e alojamento), a **seguradora** (seguro) ou o **colaborador** (ajuda de custo).

**PUT Cabimentação**
```jsonc
{ "itens": [ { "logisticaUuid": "…", "selecionado": true,
               "cabId": null,                                   // só em cabimento manual/internacional
               "anexo": { "tipoDocumentoId": 21, "documento": "nota_transferencia.pdf" } } ],
  "processoEtapaAction": "NEXT" }
```

**PUT Autorização** — sem campos:
```jsonc
{ "processoEtapaAction": "NEXT" }
```
→ `{ "id": "…", "etapa": "PAGAMENTO", "estadoMissao": "FINALIZADO" }`

| Acção | Efeito |
|---|---|
| Cabimentação `SAVE` | Grava anexos e `cabId` manual; não muda estados |
| Cabimentação `NEXT` | Linhas seleccionadas → `CABIMENTADO`. Só avança para `AUTORIZACAO` com **todas** as linhas cabimentadas; se faltar alguma → **400** |
| Autorização `SAVE` | Não faz nada |
| Autorização `NEXT` | **Todas** as linhas → `AUTORIZADO`; processo → `PAGAMENTO` |
| Último processo activo a chegar a `PAGAMENTO` | Missão `estado: "FINALIZADO"` |

- ✅ **Acabou a autorização parcial:** o `NEXT` exige todas as linhas.
- ⚠️ **`cabId` continua `null`:** a integração com o SGAL ainda não existe.
- **Erros:**

| Situação | Resposta |
|---|---|
| Linha que não pertence ao processo | **400** |
| Alterar o `cabId` de uma linha já autorizada | **400** |
| `NEXT` fora da etapa | **400** |

---

## 2026-09-14 — Validação UGAL, Aprovação RH e Avaliar Prestador (Fases 7–8)

### Pareceres

| Ecrã | Método | Path |
|---|---|---|
| Validação UGAL — carregar | `GET` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/validacao-ugal` |
| Validação UGAL — gravar / emitir | `PUT` | idem |
| Aprovação RH — carregar | `GET` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/aprovacao-rh` |
| Aprovação RH — gravar / emitir | `PUT` | idem |

**PUT** (os dois ecrãs usam o mesmo corpo)
```jsonc
{
  "responsavel": "COORDENADOR_RH",   // só na Aprovação RH: COORDENADOR_RH | DIRECTOR_RH (na UGAL omitir)
  "parecer": "DESFAVORAVEL",         // domínio PARECER: FAVORAVEL | DESFAVORAVEL
  "observacao": "Fatura com valor diferente da proposta",  // obrigatória com DESFAVORAVEL (máx. 500)
  "processoEtapaAction": "NEXT"      // SAVE = rascunho | NEXT = emitir
}
```
→ `{ "id": "<processoUuid>", "etapa": "APROVACAO_RH", "parecer": "<parecerUuid>" }`

**Regras**

| Situação | Efeito |
|---|---|
| `SAVE` | Guarda **rascunho** (`estado: "P"`); pode ser regravado |
| `NEXT` | **Emite** (`estado: "A"`); só com o processo **exactamente** nessa etapa |
| UGAL favorável | Processo → `APROVACAO_RH` |
| Coordenador (qualquer parecer) | Fica registado; **não** muda a etapa (não vinculativo) |
| Director sem parecer emitido do Coordenador | **400** |
| Director favorável | Processo → `CABIMENTO` |
| UGAL **ou** Director desfavorável | Processo → **`LOGISTICA`**; os pareceres do ciclo ficam `"I"` (anulados) e a ronda recomeça |
| Emitir de novo no mesmo ciclo | **400** `já foi emitido neste ciclo` |

**GET Validação UGAL**
```jsonc
{
  "autorizacao": [ { "documento": "convite.pdf", … } ],        // anexos da submissão
  "requisicoes": [ { "documento": "…_requisicao_RMS-2026-3.pdf" } ],  // só bilhete/alojamento
  "faturas": [ { "documento": "bilhete.pdf" } ],                // anexos da logística
  "parecerAtual": { "parecer": "FAVORAVEL", "estado": "P", "estadoDesc": "Rascunho", "executadoPor": "…" },
  "historico": [ … ]                                            // inclui os anulados ("I")
}
```

**GET Aprovação RH** → `parecerCoordenador`, `parecerDirector` (do ciclo actual), `parecerUgal` (o parecer emitido que abriu a etapa) e `historico`.

> ⚠️ O "cabimento automático" no fim da Aprovação RH ainda não chama o SGAL (integração sem contrato). O processo segue para `CABIMENTO` sem nº de cabimento.

### Avaliar Prestador

| Ecrã | Método | Path |
|---|---|---|
| Carregar | `GET` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/prestadores/{missaoPrestUuid}/avaliacao` |
| Gravar | `PUT` | idem |

`{missaoPrestUuid}` = `prestadores[].uuid` do GET de prestadores do processo.

**PUT** — valores do domínio `AVALIACAO_FORNECEDOR` (`opcoesAvaliacao` do GET):
```jsonc
{ "sistemaQualidade": "100", "prazoFornecimento": "100", "qualidadeProduto": "100",
  "capacidadeResposta": "100", "preco": "75" }
```
→ `{ "id": "<uuid>", "total": 95.00, "designacao": "A" }`

**GET**
```jsonc
{
  "nomePrestador": "Halcyon Viagens", "podeAvaliar": true, "avaliado": true,
  "criterios": [
    { "criterio": "SISTEMA_QUALIDADE", "peso": 5, "avaliacao": "100", "avaliacaoDesc": "Muito Bom", "pontos": 5.00 },
    …
  ],
  "total": 95.00, "designacao": "A", "designacaoDesc": "Fornecedor Preferencial",
  "opcoesAvaliacao": [ { "valor": "100", "descricao": "Muito Bom" }, { "valor": "75", "descricao": "Bom" }, … ]
}
```

- **Pesos:** vêm do domínio (referência `PESO`); os da spec são o valor por defeito: 5, 15, 40, 20, 20.
- **Classes:** A &gt; 75, B ]40;75], C ]25;40], D [0;25].
- **Quem se pode avaliar:** só prestadores com **requisição activa** no processo. Os outros vêm com `podeAvaliar: false` e o `PUT` devolve **400**.
- Regravar **actualiza** a avaliação existente.
- A avaliação passa a aparecer em `GET /prestadores/{uuid}/avaliacoes`.

**Erros**

| Situação | Resposta |
|---|---|
| Critério em falta | **400** |
| Valor fora do domínio | **400** |
| Prestador que não pertence ao processo | **404** |

---

## 2026-09-14 — Logística, por processo (Fase 6)

Um ecrã por processo. Os 4 tipos passam por esta etapa.

| Ecrã | Método | Path |
|---|---|---|
| Carregar | `GET` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/logistica` |
| Gravar / Avançar | `PUT` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/logistica` |

**GET**
```jsonc
{
  "processo": { "tipoProcesso": "ALOJAMENTO", "etapa": "LOGISTICA" },
  "dataInicioMissao": "2026-10-05", "dataFimMissao": "2026-10-09",
  "bilhetesPassagem": [], "segurosViagem": [], "ajudasCusto": [],     // só a secção do tipo vem preenchida
  "alojamentos": [
    { "uuid": "…", "lugarHospedagem": "Hotel Praia Mar", "flgAlimentacao": "NAO",
      "valorDiario": 12000, "valorTotal": 60000, "moeda": "CVE",
      "dataInicio": "2026-10-05", "dataFim": "2026-10-09", "nrDias": 5,
      "colaboradores": [ { "funcionarioUuid": "…", "nomeColaborador": "…" } ],
      "colaborador": { … },                                           // primeiro (compatibilidade)
      "documento": { "id": 40, "documento": "reserva.pdf" } }
  ],
  "colaboradoresDisponiveis": [
    { "funUuid": "…", "nomeColaborador": "…", "missaoPrestId": 17, "nomePrestador": "Halcyon Viagens" }
  ],
  "notificacao": { "assunto": "…", "corpoEmail": "…" }
}
```

**`colaboradoresDisponiveis`** — no bilhete e no alojamento só aparecem os colaboradores com **requisição** neste processo, cada um com o seu prestador (usar para agrupar o multiselect). No seguro e na ajuda de custo aparecem todos os colaboradores activos da missão.

**PUT** — enviar **só** a secção do tipo do processo:
```jsonc
// BILHETE_PASSAGEM
{ "bilhetesPassagem": [ { "colaboradorIds": ["<funUuid>"], "valor": 90000,
                          "anexo": { "tipoDocumentoId": 21, "documento": "bilhete.pdf" } } ],
  "processoEtapaAction": "SAVE" }

// SEGURO_VIAGEM
{ "segurosViagem": [ { "entId": 12, "nomeSeguradora": "Impar Seguros",     // nome opcional: por defeito, o da entidade
                       "colaboradorIds": ["<funUuid>"], "valor": 15000 } ] }

// ALOJAMENTO — agora com vários colaboradores
{ "alojamentos": [ { "colaboradorIds": ["<funUuid>", "<funUuid>"], "lugarHospedagem": "Hotel Praia Mar",
                     "flgAlimentacao": "NAO", "valorDiario": 12000,
                     "valorTotal": null,                  // null = valorDiario × nº de dias
                     "dataInicio": null, "dataFim": null, // null = datas da missão
                     "moeda": "CVE" } ] }

// AJUDA_CUSTO — uma linha por colaborador
{ "ajudasCusto": [ { "colaboradorId": "<funUuid>", "flgAlojamento": true,
                     "numeroDiasAlojamento": 5, "valorDiario": 12000 } ] }
```
→ `{ "id": "<processoUuid>", "etapa": "VALIDACAO_UGAL" }`

- **Secção:** a lista enviada é a **secção completa**. Uma linha que fica de fora é removida (inactivada) e `null` não altera nada.
- **Linhas reaproveitadas:** uma linha com o mesmo conjunto de colaboradores mantém o `uuid` e o anexo entre gravações.
- **Ajuda de custo:** o valor diário gravado é `valorDiario` × a fracção:

| Situação | Fracção |
|---|---|
| `flgAlojamento: false` (alojamento próprio ou casa de família) | 100% |
| `flgAlojamento: true` e alojamento **sem** alimentação | ⅔ |
| `flgAlojamento: true` e alojamento **com** alimentação | ⅓ |

  A alimentação é lida do alojamento do colaborador, no processo `ALOJAMENTO`. Por isso, registe primeiro o alojamento.
- **`NEXT`:** exige pelo menos uma linha, avança para `VALIDACAO_UGAL` e grava um aviso para cada colaborador envolvido.

**Erros**

| Situação | Resposta |
|---|---|
| Secção de outro tipo | **400** `O processo X só aceita a secção do seu tipo` |
| Campo obrigatório em falta | **400** |
| `flgAlimentacao` diferente de `SIM` ou `NAO` | **400** |
| Colaborador sem requisição neste processo (bilhete ou alojamento) | **400** |
| Colaboradores de prestadores diferentes na mesma linha | **400** |
| Colaborador repetido em duas linhas | **400** |
| Seguradora inexistente | **400** |
| Remover ou alterar o valor de uma linha já cabimentada | **400** |
| `NEXT` sem linhas | **400** |

---

## 2026-09-14 — Emissão de Requisição, por processo (Fase 5)

Uma **requisição por prestador**, com N colaboradores. Só existe em `BILHETE_PASSAGEM` e `ALOJAMENTO`.

| Ecrã | Método | Path |
|---|---|---|
| Carregar | `GET` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/requisicoes` |
| Gravar / Avançar | `PUT` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/requisicoes` |
| Extrair Requisição (PDF) | `GET` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/requisicoes/{requisicaoUuid}/pdf` |

**GET** — um item por prestador activo do processo:
```jsonc
{
  "processo": { "tipoProcesso": "BILHETE_PASSAGEM", "etapa": "EMISSAO_REQUISICAO" },
  "requisicoes": [
    { "missaoPrestUuid": "…", "nomePrestador": "Halcyon Viagens", "selecionado": true,
      "requisicaoUuid": "…", "nrRequisicao": 3, "anoRequisicao": 2026, "notaEncomenda": "RMS-2026/3",
      "valorTotal": 105000,
      "colaboradores": [ { "uuid": "…", "funUuid": "…", "nomeColaborador": "Wilson Cabral Tavares" } ],
      "proposta": { "id": 12, "tipoDocumentoId": 20, "documento": "proposta.pdf" },
      "documentoRequisicao": { "id": 13, "documento": "…_requisicao_RMS-2026-3.pdf" } },   // só depois do NEXT
    { "missaoPrestUuid": "…", "nomePrestador": "Cabo Verde Travel", "selecionado": false, "colaboradores": [] }
  ],
  "colaboradoresMissao": [ { "uuid": "…", "funUuid": "…", "nomeColaborador": "…" } ]
}
```

**PUT**
```jsonc
{
  "requisicoes": [
    { "missaoPrestUuid": "…", "selecionado": true,
      "funcionarioUuids": ["<funUuid>", "<funUuid>"],        // uuid do FUNCIONÁRIO (colaboradoresMissao[].funUuid)
      "valorTotal": 105000,                                  // null = não mexer
      "proposta": { "tipoDocumentoId": 20, "documento": "proposta.pdf" } }
  ],
  "processoEtapaAction": "NEXT"
}
```
→ `{ "id": "<processoUuid>", "etapa": "LOGISTICA" }`

- **Nº de requisição:** gerado na primeira gravação, sequencial dentro do ano e fixo daí em diante. Aparece no PDF como `RMS-{ano}/{nr}`.
- **Prestador sem `selecionado: true`:** a requisição dele passa a inactiva.
- **Colaboradores:** um colaborador só pode estar numa requisição de cada processo.
- **`NEXT`:**
  - gera o PDF da nota de encomenda e guarda-o (aparece em `documentoRequisicao`);
  - envia email ao prestador (principal e emails adicionais);
  - avança para `LOGISTICA`.
- ⚠️ **O email segue sem o PDF anexado.** O serviço de correio (`sipsv0.SEND_MAIL_V1`) não suporta anexos; o PDF fica disponível para descarregar.
- **Extrair:** gera o PDF com os dados actuais. Funciona antes do `NEXT`, como pré-visualização.
- **PDF:** lista "{tipo} a favor de {colaborador}" e o **total** da requisição, também por extenso. Não há valor por linha, porque o modelo de dados só guarda o total.

**Erros**

| Situação | Resposta |
|---|---|
| Prestador que não está seleccionado no processo | **400** |
| Requisição sem colaboradores | **400** |
| Colaborador que não pertence à missão | **400** |
| Colaborador já associado a outra requisição do mesmo processo | **400** |
| `NEXT` sem nenhum prestador seleccionado | **400** |
| Retirar uma requisição que já seguiu para a logística | **400** |
| PDF de uma requisição que não pertence ao processo | **404** |

---

## 2026-09-14 — Etapa Prestadores Serviço, por processo (Fase 4)

Substitui o ecrã **Análise**. É executada **por processo** e só existe em `BILHETE_PASSAGEM` e `ALOJAMENTO`.

| Ecrã | Método | Path |
|---|---|---|
| Carregar | `GET` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/prestadores` |
| Gravar / Avançar | `PUT` | `/api/v1/missao-servico/{uuid}/processos/{tipoProcesso}/prestadores` |

`{tipoProcesso}` = `BILHETE_PASSAGEM` | `ALOJAMENTO` (vem de `processos[].tipoProcesso` do GET da submissão).

**Lookup de prestadores:** `GET /api/v1/missao-servico/prestadores?estado=A`

**GET**
```jsonc
{
  "missaoUuid": "…", "nrMissaoFormatado": "3/2026",
  "processo": { "uuid": "…", "tipoProcesso": "BILHETE_PASSAGEM", "etapa": "PRESTADOR_SERVICO", "estado": "A" },
  "prestadores": [
    { "uuid": "…", "paramPrestUuid": "…", "nome": "Halcyon Viagens",
      "email": "reservas@halcyon.cv", "emails": ["reservas@halcyon.cv", "financeiro@halcyon.cv"], "estado": "A" }
  ],
  "notificacao": { "assunto": "…", "corpoEmail": "…" },   // pré-preenchida com o template (editável)
  "executadoPor": "…", "dataExecucao": "2026-09-14"
}
```

**PUT**
```jsonc
{
  "prestadores": ["<paramPrestUuid>", "<paramPrestUuid>"],   // selecção completa, 1 a 3
  "notificacao": { "assunto": "…", "corpoEmail": "…" },     // opcional — vazio usa o template
  "processoEtapaAction": "SAVE"                              // SAVE | NEXT
}
```
→ `{ "id": "<processoUuid>", "etapa": "EMISSAO_REQUISICAO" }`

- **`NEXT`:** envia o pedido de proposta a **todos os emails activos** de cada prestador (o principal e os adicionais) e avança o processo para `EMISSAO_REQUISICAO`.
- Um `NEXT` com o processo já adiante só notifica os prestadores **acrescentados** nessa gravação.
- **Erros:**

| Situação | Resposta |
|---|---|
| Nenhum prestador seleccionado, ou mais de 3 | **400** |
| Prestador inactivo ou inexistente | **400** |
| Retirar um prestador que já tem requisição activa | **400** |
| Tipo de processo que não passa por esta etapa (`SEGURO_VIAGEM`, `AJUDA_CUSTO`) | **400** |
| Missão cancelada | **400** |
| `NEXT` com o processo numa etapa anterior | **400** |

---

## 2026-09-14 — Submissão cria os 4 processos (Fase 3)

**Endpoints:** `POST /submissao`, `PUT /{uuid}/submissao` (request) e `GET /{uuid}/submissao` (response)

Cada missão passa a ter 4 processos, cada um com a sua etapa. São criados na primeira gravação.

```jsonc
// request — campo novo
{ ..., "alojamento": true }

// response — campos novos
{
  "alojamento": true,
  "processos": [
    { "uuid": "…", "tipoProcesso": "BILHETE_PASSAGEM", "tipoProcessoDesc": "Bilhete Passagem",
      "etapa": "PRESTADOR_SERVICO", "etapaDesc": "Prestador Serviço", "estado": "A" },
    { "uuid": "…", "tipoProcesso": "SEGURO_VIAGEM", "etapa": "LOGISTICA", "estado": "A" },
    { "uuid": "…", "tipoProcesso": "AJUDA_CUSTO",   "etapa": "LOGISTICA", "estado": "A" },
    { "uuid": "…", "tipoProcesso": "ALOJAMENTO",    "etapa": "PRESTADOR_SERVICO", "estado": "A" }
  ]
}
```

- **Percursos:** bilhete e alojamento começam em `PRESTADOR_SERVICO`; seguro e ajuda de custo começam em `LOGISTICA` (não têm prestador nem requisição).
- **`alojamento`:** `false` inactiva o processo `ALOJAMENTO`; `true` reactiva-o. Omitido ou `null` não altera nada; na criação, sem o campo, fica activo.
- Retirar o alojamento com o processo já fora da primeira etapa → **400** `Não é possível retirar o alojamento: o processo ALOJAMENTO já está na etapa …`.

---

## 2026-09-14 — Gestão de Prestadores de Serviço (Fase 2)

Menu próprio. Base: `/api/v1/missao-servico/prestadores`

| Ecrã | Método | Path |
|---|---|---|
| Lista | `GET` | `?nome=&ilhaId=&estado=&pageNumber=0&pageSize=10` |
| Detalhe / Editar (carregar) | `GET` | `/{uuid}` |
| Registar | `POST` | `/` |
| Editar | `PUT` | `/{uuid}` |
| Ver Avaliação | `GET` | `/{uuid}/avaliacoes` |

**Registar / Editar**
```jsonc
{
  "entId": 11,                          // lookup GET /api/v1/parametrizacao/entidades/ativos — obrigatório
  "nome": "Halcyon Viagens",            // opcional: por defeito, o nome da entidade
  "nif": "200123456",
  "email": "reservas@halcyon.cv",       // obrigatório
  "telefone": "2601234",
  "ilhaId": 2387,
  "morada": "Praia, Plateau",
  "estado": "A",                        // A | I — no registo, por defeito A
  "emails": [                           // outros emails
    { "email": "financeiro@halcyon.cv" },
    { "id": 4, "email": "geral@halcyon.cv" }
  ]
}
```
→ `{ "id": "<uuid>" }`

- **`emails`:** sem `id` cria; com `id` actualiza; um email que fica de fora do array passa a `estado: "I"`. Omitido ou `null` não altera nada; `[]` inactiva todos.
- **Erros:**

| Situação | Resposta |
|---|---|
| `entId` inexistente | **400** `Entidade inválida` |
| Entidade já registada noutro prestador | **409** |
| Email principal ou adicional mal formado | **400** `Email inválido` |
| Email repetido (entre o principal e os adicionais) | **400** `Email duplicado` |
| `estado` diferente de `A`/`I` | **400** |

**Lista** — `content[]` com `nome`, `email`, `morada`, `telefone`, `estado`/`estadoDesc` e os `emails` **activos**. O **detalhe** traz todos os emails, com o respectivo estado.

**Ver Avaliação** — `[{ nrMissaoFormatado, tipoProcesso, sistemaQualidade, prazoFornecimento, qualidadeProduto, capacidadeResposta, preco, total, designacao }]`. Fica vazio até existir o ecrã Avaliar Prestador.

---

## 2026-09-14 — Ilha/concelho na submissão e nº/valor da requisição

**Branch:** `feat/missao-servico-processos`

Primeiro passo rumo à spec 14/09 (modelo por processo). Só campos novos — nada foi removido nem mudou de nome.

### Submissão — `ilhaId` e `concelhoId`

**Endpoints:** `POST /submissao`, `PUT /{uuid}/submissao` (request) e `GET /{uuid}/submissao` (response)

```jsonc
// request — só quando o país de destino é Cabo Verde
{ "paisDestinoId": 1238, "ilhaId": 2387, "concelhoId": 238704, ... }

// response
{ "ilhaId": 2387, "ilhaNome": "Santiago", "concelhoId": 238704, "concelhoNome": "Praia", ... }
```

- Opcionais. Com destino **estrangeiro** são ignorados e gravados a `null`.
- Id inexistente em `GLB_T_GEOGRAFIA` → **404**.

### Emissão de Requisição — `valorTotal` e nº de requisição

`PUT /{uuid}/emissao-requisicao` — novo campo opcional por prestador:
```jsonc
{ "requisicoes": [
    { "missaoPrestId": 17, "selecionado": true, "valorTotal": 105000,
      "missaoColabIds": ["<funUuid>"], "documentoProposta": { ... } } ],
  "processoEtapaAction": "SAVE" }
```
- `valorTotal` omitido ou `null` **mantém** o valor gravado.

`GET /{uuid}/emissao-requisicao` — cada item de `requisicoes[]` passa a trazer:
```jsonc
{ "missaoPrestId": 17, "nrRequisicao": 3, "anoRequisicao": 2026, "valorTotal": 105000, ... }
```
- `nrRequisicao` é **gerado** ao gravar: sequencial dentro do ano, o mesmo para todos os colaboradores do mesmo prestador. Não muda em gravações seguintes.
- Prestador ainda sem requisição → `nrRequisicao`, `anoRequisicao` e `valorTotal` a `null`.

> **Corrige:** a BD passou a exigir `NR_REQUISACAO` (NOT NULL) e a gravação da Emissão de Requisição falhava com `ORA-01400`.

---

## 1. Correção — `cabId` deixou de ser obrigatório

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

## 2. `SAVE` vs `NEXT` — comportamento corrigido em todas as etapas

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

## 3. A etapa nunca retrocede

Cada `salvar*` escrevia a etapa do seu ecrã de forma incondicional. Numa missão em `PAGAMENTO`, gravar no ecrã de submissão devolvia-a a `SUBMISSAO`, reabrindo etapas já concluídas.

Agora a transição é **monotónica**: a etapa só avança, nunca recua. Gravar num ecrã de uma etapa já ultrapassada continua a ser permitido (correções), mas não puxa o processo para trás.

Ordem das etapas: `SUBMISSAO` → `ANALISE` → `EMISSAO_REQUISICAO` → `LOGISTICA` → `CABIMENTO` → `PAGAMENTO`

---

## 4. Guarda de ordem — `NEXT` fora de sequência dá 400

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

## 5. Gravações idempotentes

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

## 6. Novo campo — `colaboradoresMissao`

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

## 7. Novo campo — `colaboradores` por linha

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

## 8. `numDocumento` — passaportes deixaram de se perder

Números de documento **alfanuméricos** (ex.: passaporte `PA466262`) eram convertidos para `null` sem erro, por a coluna `RH_T_MISSAO_COLABORADOR.NUM_DOCUMENTO` ser `NUMBER`. Passou a `VARCHAR2`, e a leitura usa o funcionário como fonte de verdade.

Aplica-se retroativamente: missões já gravadas passam a mostrar o valor correto.

> **Ops:** o `ALTER TABLE` foi aplicado diretamente na BD (o Flyway está desligado e esta tabela não consta das migrações). **Tem de ser repetido noutros ambientes.**

---

## Fluxo completo — o que enviar em cada etapa

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

## Como saber que ecrã mostrar

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

## Limitações conhecidas

| Tema | Situação |
|---|---|
| `cabId` (SGAL) | Não gerado — integração por definir (sem endpoint nem contrato). Linhas ficam `CABIMENTADO` com `cabId: null`. |
| `valorDiario` | Vem do cliente, sem validação. Tabela de preços da ajuda de custo não existe. |
| `entId` | Aceite sem validação — não há lookup de entidades (agências/seguradoras). |
| Alojamento em grupo | O DTO força uma linha por colaborador; a spec admite cabimento único para o mesmo hotel. |
| Autorização parcial | Avança para `PAGAMENTO` mesmo com itens por autorizar. |
| Encoding | Enviar `Content-Type: application/json; charset=utf-8` — há nomes com mojibake vindos de `/funcionarios`. |

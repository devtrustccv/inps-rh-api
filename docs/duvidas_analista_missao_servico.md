# Missão de Serviço — dúvidas para o analista

**Data:** 2026-09-15 · **Base:** `Especificação Tecnica Funcional - MISSÃO SERVIÇO_14_09_26` e
`… TRANSVERSAL_29_08_26`

O fluxo de negócio está implementado e validado ponta a ponta (duas baterias de testes contra a API
a correr, com verificação em base de dados). As dúvidas abaixo são o que **falta decidir** para
fechar o módulo. Nenhuma impede o que já existe de funcionar.

Cada ponto diz **o que a spec diz**, **o que o sistema faz hoje** e **o que precisamos de saber**.
As mais urgentes estão assinaladas com 🔴 — são as que bloqueiam trabalho.

## Estado a 2026-09-16 — respostas do analista

| Ponto | Decisão | Estado |
|---|---|---|
| 1.1 Notificação da logística | Fica como está: uma notificação por processo | ✅ fechado |
| 1.2 UGAL ou UPAL | É UGAL. Na spec, "UGAL" aparece no fluxo e em `REPONSAVEL = 'UGAL'`; "UPAL" só no título da etapa (gralha). O domínio usa `VALIDACAO_UGAL` | ✅ fechado |
| 1.3 Limiares A–D | Estão na spec, na tabela "Intervalos (pontuação total %)", e conferem com o código | ✅ fechado |
| 2.1, 2.2 Alertas da missão | Fora do âmbito por agora | ⛔ descartado |
| 3.2 Valores dos prazos | Só serviam os alertas | ⛔ descartado |
| 3.3, 3.4 Destinatários e gatilhos | Não se parametrizam: os destinatários são o próprio prestador e o próprio funcionário | ⛔ descartado |
| 3.1 Textos dos templates | Parametrizados com textos **provisórios** (`docs/db/missao_servico_notificacoes_dml.sql`), a rever pelo negócio | ✅ feito |
| 4. Notificações internas | Implementadas as que vão ao colaborador e aos prestadores: confirmação do pedido, ajuda de custo e alteração. As que iam para RH, Financeiro ou SGAL caem, porque não há destinatários por papel | ✅ feito |
| 5.3, 5.4, 5.5 | Só serviam os alertas | ⛔ descartado |
| 5.1 SGAL, 5.2 Tabela de preços | Verificado que não há especificação nem dados; ficam para o fim, com perguntas concretas | ⏳ adiado |
| 6. Filtro "Etapa" | Continua opcional na API | ✅ fechado |
| 7.1 Email nos avisos ao colaborador | Implementado: email + texto por processo (o ecrã do bilhete esclareceu o conteúdo). Falta só o anexo, que depende de um procedimento de envio que o suporte | ⏳ parcial |
| 7.2 Colaborador retirado | Por decidir | ⏳ aberto |

---

## 1. Contradições dentro da spec da missão

### 1.1 ✅ Notificação da logística ao colaborador: uma ou quatro? — *fica uma por processo*

A spec diz as duas coisas.

Na **secção de Notificações**:
> *Detalhes da Viagem Confirmados: Enviada ao colaborador **quando todos os arranjos logísticos**
> (bilhetes, alojamento, seguro) **estão finalizados e confirmados**. Esta notificação deve incluir
> todos os detalhes relevantes para a viagem.*

Na **especificação da etapa Logística**, no botão Seguinte (aparece duas vezes, uma por bloco):
> *NOTA: o colaborador Recebe notificação sobre a logística da viagem (Deve registar na tabela
> `RH_T_NOTIFICACAO`)*

**Hoje:** seguimos a segunda — notificamos no `NEXT` de cada processo. Numa missão com os quatro
processos activos, o colaborador recebe **quatro emails**.

**Precisamos de saber:** um aviso por processo, um único de síntese no fim, ou os dois (o
operacional por processo e o de síntese para o colaborador levar na viagem)?

### 1.2 ✅ Nome da etapa: UGAL ou UPAL? — *é UGAL (16/09)*

O título da etapa diz **"ETAPA – VALIDAÇÃO UPAL"**, mas o resto do documento diz **UGAL** (3 vezes).

**Hoje:** usamos `VALIDACAO_UGAL`, alinhado com o domínio `TIPO_PROCESSO_ETAPA` que já existia na
base de dados.

**Precisamos de saber:** confirmar que UGAL está certo e que UPAL é gralha. Se for ao contrário,
muda o domínio e o valor gravado.

### 1.3 ✅ ~~Faltam os limiares que separam as classes A, B, C e D~~ — *estão na spec*

> **Resolvido a 2026-09-16.** A spec tem, ao lado da tabela Classe/Designação, a tabela
> **"Intervalos (pontuação total %)"**: A > 75, B ]40;75], C ]25;40], D [0;25]. São exactamente os
> valores do código. A dúvida surgiu porque a conversão da spec para HTML
> (`docs/spec_missao_servico_14_09.html`) **perdeu essa tabela**. Em caso de dúvida, confirmar
> sempre no `.docx`. O texto abaixo fica como registo.

A spec define a tabela **Critério / Peso (%)** (5, 15, 40, 20, 20 = 100%) e a tabela
**Classe / Designação** (A = Fornecedor Preferencial, B = Aceitável, C = Recurso, D = Rejeitado).
Ambas estão parametrizadas no domínio `AVALIACAO_FORNECEDOR` e conferem.

**O que não existe em lado nenhum é a partir de que total se atribui cada classe.** Não está na
spec (a tabela Classe/Designação tem só duas colunas), não está no domínio, e não está na spec
anterior de 19/08.

**Hoje:** o nosso código tem os limiares fixos — **A > 75, B ]40;75], C ]25;40], D [0;25]** — em
`AvaliacaoPrestadorCalculo.designacao()`. Não conseguimos rastrear a origem: presumimos que foram
assumidos numa sessão anterior.

**Precisamos de saber:** quais são os limiares certos, e se devem ficar parametrizados no domínio
(como os pesos e as designações) em vez de fixos no código.

### 1.4 Avaliação: escala do protótipo ou do texto?

O ecrã do protótipo mostra totais tipo **4,20** com designações *Muito Bom / Bom / Regular*. O texto
da spec define **pesos somados a 100** e classes **A–D**.

**Hoje:** seguimos o texto — total 0–100 e classes A–D, com a descrição a vir do domínio (ex.: "A —
Fornecedor Preferencial"). Exemplo real da API: 100/75/100/75/50 → total **81,25**, classe **A**.
O exemplo da própria spec dá 95 (o Preço pontua "Bom": 20 × 75% = 15), o que confirma a escala 0–100.

**Precisamos de saber:** confirmar que o protótipo é ilustrativo e o texto é o que vale.

---

## 2. Contradições entre a spec da missão e a TRANSVERSAL

### 2.1 ⛔ Quantos alertas tem a missão: dois ou seis? — *alertas fora do âmbito*

A **spec da missão** descreve seis: fatura próxima do vencimento, fatura em atraso, fatura em falta,
requisição pendente de resposta, missão próxima do início sem confirmação, documentos obrigatórios
em falta.

A **TRANSVERSAL**, que é onde os alertas são especificados a sério (requisitos, regra de não
duplicação, gravação campo a campo), reconhece apenas dois — e marca-os como por decidir:

> **Missão Serviço**
> - Faturas não submetidas após X dias
> - Missão solicitada sem cabimentação
>
> *Pendente: A por verificar se isso faz sentido ainda fazer*

Note-se ainda que a TRANSVERSAL é de **29/08** e a spec da missão foi reescrita a **14/09**.

**Hoje:** nenhum alerta da missão implementado. A infraestrutura do job existe e funciona — corre
diariamente às 6h e já gera três tipos de alerta noutros módulos.

**Precisamos de saber:** qual das listas vale? E os dois da TRANSVERSAL ainda fazem sentido depois
da reestruturação de 14/09?

### 2.2 O que é "Missão solicitada sem cabimentação"?

Aparece só na TRANSVERSAL e não tem equivalente na spec da missão.

**Contexto:** no modelo novo, cada um dos quatro processos cabimenta ao seu ritmo. Uma missão pode
ter o bilhete cabimentado e a ajuda de custo ainda na logística.

**Precisamos de saber:** o alerta dispara quando *algum* processo está por cabimentar, quando
*nenhum* está, ou ao fim de X dias desde a submissão?

---

## 3. Parametrizações pedidas sem especificação

A spec pede cinco mecanismos de parametrização, numa frase cada, sem tabela, colunas nem valores —
ao contrário do resto do documento, que mapeia campo a campo. Estado actual:

| Parametrização pedida | Mecanismo | Falta |
|---|---|---|
| Conteúdo (templates) | ✅ existe e é usado | os textos reais (ver 3.1) |
| Tipos de notificação/alerta | ✅ tabela e domínio existem | registar os tipos da missão |
| Prazos e limiares | ✅ domínio `CONFIGURACAO_PRAZO` + leitura no código | **os valores** (ver 3.2) |
| Destinatários | ⚠️ existe, mas só para envio manual | os papéis da missão (ver 3.3) |
| Gatilhos (triggers) | ❌ não existe | desenho (ver 3.4) |

### 3.1 Textos dos templates

`RH_T_PARAM_NOTIFICACAO` tem o template `MISSAO_PRESTADOR` registado com texto de teste
("Polhover imoant"). Os outros dois que o código procura —
`MISSAO_EMISSAO_REQUISICAO` e `MISSAO_LOGISTICA_COLABORADOR` — não existem, e caem num texto por
defeito embutido no código.

**Precisamos de:** assunto e corpo de cada um. Suportam campos dinâmicos já disponíveis:
`{nrMissao}`, `{destino}`, `{dataInicio}`, `{dataFim}`, `{nrDias}`, `{nrColaboradores}`,
`{tipoProcesso}`.

### 3.2 ⛔ Valores dos prazos — *descartado com os alertas*

O único número concreto em toda a spec de alertas é *"5 a 10 dias antes"* para a fatura a vencer — e
não diz se são 5 ou 10.

**Precisamos de saber**, em dias, para cada alerta que ficar decidido em 2.1:
- fatura próxima do vencimento;
- fatura em falta (a contar de quê — da emissão da requisição?);
- requisição pendente de resposta;
- missão próxima do início sem confirmação.

### 3.3 ⛔ Quem recebe cada notificação — *sem parametrização: prestador e funcionário*

A spec lista cinco papéis: **"Colaborador", "RH", "SGAL", "Financeiro", "Agência de Viagem"**.

**Hoje:** o sistema conhece três — `COLABORADOR`, `RESPONSAVEL_COLABORADOR`, `RESPONSAVEL_REGISTO` —
e são escolhidos **no ecrã, a cada envio manual**. Para envio automático (etapas e job) não há onde
os declarar por tipo de notificação. A missão contorna isto enviando directamente para os emails do
prestador que tem em mão.

**Precisamos de saber:**
- "a sua direção" (na Confirmação de Pedido de Missão) resolve-se por `RH_T_RESPONSAVEL` da secção
  do colaborador, ou é outra coisa?
- "RH", "Financeiro" e "SGAL" são caixas de correio fixas, grupos de utilizadores, ou um perfil?
- confirmar que "Agência de Viagem" é o prestador da missão (é o que fazemos hoje).

### 3.4 Gatilhos

Não existe nenhuma tabela que diga *"quando o processo entra na etapa X, notificar Y"*. Hoje cada
`NEXT` chama o envio directamente no código.

**Nota:** a forma de o resolver — por exemplo uma tabela de detalhe em `RH_T_PARAM_NOTIFICACAO` a
declarar, por tipo, destinatários e gatilho — é **proposta nossa**, não requisito da spec. Fica para
validação convosco.

---

## 4. Notificações do lado interno que não emitimos

As notificações que existem são as externas — prestador, agência e cancelamento. Das que a spec
lista para o lado interno, falta emitir:

| Notificação | Momento | Estado |
|---|---|---|
| Confirmação de Pedido de Missão | após submissão e autorização | não emitida |
| Informação sobre Ajuda de Custo | após pagamento efetuado | não emitida |
| Novo Pedido de Missão Autorizado (ao RH) | após autorização | não emitida |
| Confirmação de Cabimento (ao RH) | após cabimento | não emitida |
| Cabimento Pendente de Autorização (ao Financeiro) | na autorização | não emitida |
| **Alteração** da missão (a todos os envolvidos) | ao editar uma missão em curso | não emitida — só notificamos o **cancelamento** |

Todas dependem de 3.3 — só se podem enviar quando estiver decidido a quem.

**Precisamos de saber:** confirmar a lista e a prioridade. Alguma é obrigatória para o arranque?

---

## 5. Integrações e dados em falta

### 5.1 🔴 SGAL — cabimento

A spec diz que *"é gerado um cabimento para cada tipo de serviço"* e que o SGAL pode cabimentar
*"diretamente na plataforma ou por exportação para o SIPS FUN"*. Não indica endpoint, payload nem
onde vem o número devolvido.

**Hoje:** a etapa funciona — as linhas ficam `CABIMENTADO` e a missão avança — mas o `cabId` fica
**`null`**. Não há número de cabimento.

**O ecrã da Cabimentação (3.2.4.6) levanta mais duas questões:**
1. **Formato do nº de cabimento.** O ecrã mostra `CAB/2026/001` — um **texto**. A coluna
   `RH_T_MISSAO_LOGISTICA.CAB_ID` é **NUMBER**, e a API devolve `cabId` numérico. Se o nº vier no
   formato do ecrã, é preciso mudar a coluna para VARCHAR2 (alteração de base de dados). O nº é
   gerado pelo SGAL ou por nós, com o ano e um sequencial?
2. **A coluna "Nome"** mostra, em todas as linhas, *"INPS - Instituto Nacional de Previdência
   Social"*, e não o prestador. Hoje devolvemos o nome do prestador, da seguradora ou do colaborador
   da ajuda de custo, que é o que identifica a linha. Qual é o pretendido?

**Precisamos do financeiro/SGAL:**
- endpoint de cabimento aplicável a uma linha de `RH_T_MISSAO_LOGISTICA`;
- contrato do payload (1 cabimento por tipo de serviço; individual por colaborador na ajuda de custo);
- em que campo da resposta vem o `CAB_ID`;
- a direcção: somos nós a chamar o SGAL, ou é o SGAL a escrever o `CAB_ID`?

**Verificado a 2026-09-16:** a spec (`.md` completo) não diz mais do que isto. No passo da Aprovação
RH lê-se *"Essa etapa invoca um serviço para fazer cabimento automatico …."*, com as reticências no
próprio texto. Também não há nenhum objecto de cabimento para missões nos esquemas a que temos acesso
(INPSRH, SIPSV0, INPSDB, INPSSIGOF, SIPSGLOBAL).

**O que podemos adiantar enquanto não há contrato:** isolar a chamada ao cabimento numa interface
própria, com a implementação actual (sem SGAL) por trás. Quando o contrato chegar, muda só essa peça.

### 5.2 🔴 Tabela de preços da ajuda de custo

A spec diz que o Valor Diário é *"preenchido automaticamente com base no cálculo definido na
parametrização"*, variando com a função do colaborador e com missão nacional vs internacional. Essa
tabela de preços é referida mas **nunca especificada** — sem nome, colunas nem faixas.

**Hoje:** o valor diário **vem do cliente** e é aceite sem validação. O backend só lhe aplica a
fracção (100% / ⅔ / ⅓), que está correcta e testada.

**Risco:** quem chama a API decide quanto se transfere ao colaborador, sem travão.

**Precisamos de:** a tabela de preços — que valor, por que função, nacional vs internacional.

**Verificado a 2026-09-16:** a spec (`.md` completo) só lista os factores (função do colaborador,
nacional/internacional, alojamento) e as fracções. Não há valores, colunas nem lista de
funções/categorias, e não existe nenhuma tabela ou domínio com isto na BD.

**Proposta nossa, a validar** (não é requisito da spec): uma tabela de parametrização com
- função ou categoria do colaborador;
- nacional / internacional (e, se aplicável, grupo de países);
- valor diário e moeda;
- data de início e de fim de vigência.

O servidor passaria a calcular o valor diário a partir dessa tabela; enquanto estiver vazia,
continua a aceitar o valor enviado pelo ecrã.

**Perguntas concretas:**
1. Qual é a chave da tabela: função, categoria PCCS ou cargo? De onde vem essa informação do
   colaborador — da relação laboral actual?
2. Há valores diferentes por país ou região, ou só nacional vs internacional?
3. Quem mantém a tabela e com que vigência? Os valores mudam por despacho?
4. Numa missão internacional, a moeda é CVE ou a do destino?

### 5.3 Data de vencimento da fatura

Dois dos seis alertas (fatura a vencer, fatura em atraso) precisam de uma data de vencimento. A
fatura é hoje um anexo em `RH_T_DOCUMENTO`, que não tem datas.

**Precisamos de saber:** a data de vencimento passa a ser pedida no ecrã da Logística ao anexar a
fatura? (Implica alteração de base de dados.)

### 5.4 O que é "a agência confirmou a requisição"?

O alerta *"Requisição Pendente de Resposta"* pressupõe saber se a agência respondeu.

**A nossa leitura:** a confirmação é o processo avançar de `EMISSAO_REQUISICAO` para `LOGISTICA` —
esse dado já existe. **Precisamos que confirmem** que serve, ou se é preciso um estado explícito.

### 5.5 Documentos obrigatórios por etapa

O alerta *"Missão com documentos obrigatórios em falta"* precisa da lista.

**Precisamos de saber:** que documentos são obrigatórios, em que etapa, e por tipo de processo.

---

## 6. Pontos menores

- ~~**Alojamento em grupo.**~~ *Já resolvido no modelo por processo (verificado a 16/09):* uma linha
  de alojamento aceita vários colaboradores do mesmo prestador (`colaboradorIds`), o que dá um único
  cabimento para o grupo.
- ~~**`entId` do prestador.**~~ *Já resolvido (verificado a 16/09):* o registo do prestador e o seguro
  confrontam o `entId` com as entidades e respondem 400 se não existir.
- ~~**Filtro "Etapa" da Lista Missão por Etapa.**~~ *Decidido a 16/09:* continua opcional na API
  (sem ele devolve todos os processos). A obrigatoriedade fica no ecrã.
- **Campo "Alojamento"** está marcado como obrigatório no ecrã de Nova Missão; a API assume **Sim**
  quando não vem. Confirmar que o defeito é esse.
- **Botão "Ver Alerta"** da Lista Missão não terá o que mostrar, porque os alertas da missão estão
  fora do âmbito (2.1). Esconder o botão?

## 7. Notificações ao colaborador — encontrado nos testes de 16/09

### 7.1 Aviso de logística: email e anexos

A spec, na etapa Logística, diz: *"O colaborador receberá email com o seguinte: Número de Missão,
Assunto…, Corpo do Email…, **Anexos (Bilhetes e Reservas)**"*.

**Hoje:** o aviso de logística (`MISSAO_LOGISTICA_COLABORADOR`) e o aviso de cancelamento ao
colaborador ficam **só gravados** para o portal (estado `Pendente`), **sem email e sem anexos**. As
notificações novas ao colaborador (confirmação do pedido, ajuda de custo, alteração) já vão por
email, para o endereço dos contactos do funcionário.

**Decidido e implementado a 16/09:** a spec pede email (*"O colaborador receberá email"*), e o
Portal do colaborador fica para a 2.ª fase, por isso um aviso só gravado não era visto por ninguém.
Os avisos de logística e de cancelamento passam a ir por email. Sem email nos contactos, ficam
`Pendente`. No cancelamento só são avisados os colaboradores que estavam na missão.

**Conteúdo — resolvido a 16/09 pelo ecrã da spec (3.2.4.3.1).** O ecrã do Bilhete de Passagem mostra
a secção "Notificação ao Colaborador" com assunto *"Emissão de Bilhete de Viagem"* e um corpo curto
(*"Informamos que o seu bilhete de viagem foi emitido"*), e não um resumo de toda a missão. Confirma
a decisão 1.1: **um aviso por processo, com texto próprio desse processo**. Ficou implementado assim:
procura-se o template `MISSAO_LOGISTICA_COLABORADOR_{TIPO}` e, se não existir, o genérico. O do
bilhete já está registado com o texto do ecrã; falta o texto dos outros três processos.

**Continua em aberto — anexos.** A spec pede *"Anexos (Bilhetes e Reservas)"* e o ecrã diz *"Segue em
anexo o respetivo documento"*. Hoje o email vai **sem anexos**, e há uma dependência técnica: o envio
usa o procedimento `sipsv0.SEND_MAIL_V1`, que só aceita destinatário, assunto e corpo — **não tem
parâmetro para anexos**.

**Precisamos de saber:**
1. Confirmar que o anexo a enviar é o documento carregado em "Anexar Fatura" nessa linha da logística.
2. Quem disponibiliza um procedimento de envio com anexos (ou outro meio de envio)? Sem isso, o
   requisito não é implementável.
3. Os textos dos outros três processos (alojamento, seguro e ajuda de custo).

### 7.2 Colaborador retirado de uma missão em curso

Ao editar a missão e retirar um colaborador, ele deixa de estar na missão mas **não recebe aviso**. A
spec não trata este caso.

**Precisamos de saber:** avisamos o colaborador retirado (e o prestador, se já tiver requisição com
ele)?

---

## Resumo — o que falta decidir (actualizado a 2026-09-16)

1. **Tabela de preços da ajuda de custo** (5.2) — é onde há risco financeiro real hoje; quatro
   perguntas concretas e uma proposta de modelo.
2. **Contrato do SGAL** (5.1) — depende de terceiros, convém arrancar cedo.
3. **Anexos no aviso de logística** (7.1) — depende de um procedimento de envio com anexos; o texto já está resolvido.
4. **Textos definitivos dos templates** (3.1) — os actuais são provisórios; basta o negócio rever
   assunto e corpo de cada um.
5. **Colaborador retirado da missão** (7.2) e **pontos menores** (6).

---

## Nota de proveniência

Cada ponto acima foi confirmado na fonte — spec, base de dados ou código — e não de memória. Onde a
solução proposta é nossa e não requisito da spec, está assinalado no próprio ponto (ver 3.4 e 5.4).

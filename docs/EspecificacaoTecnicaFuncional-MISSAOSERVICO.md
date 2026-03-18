# Especificação Técnica e Funcional – Gestão de Missão de Serviço

**Projeto:** SIPS-RH  
**Módulo:** Gestão de Missão de Serviço

---

## 1. Enquadramento

O módulo de missão de serviço é uma solução integrada que visa digitalizar e automatizar os processos relacionados com viagens de trabalho dos colaboradores da organização. Atualmente, estes processos envolvem múltiplos intervenientes, incluindo as direções organizacionais, recursos humanos (RH), o Sistema de Gestão Administrativa e Logística (SGAL), agências de viagem e o departamento financeiro.

A necessidade de um sistema integrado surge da complexidade dos processos atuais, que envolvem múltiplas aprovações, comunicações por email, gestão manual de documentos e controlo disperso de pagamentos. O sistema proposto pretende centralizar todas estas operações numa plataforma única, proporcionando maior eficiência, transparência e controlo sobre todo o processo.

---

## 2. Âmbito

O módulo de **Missão de Serviço** abrange todas as fases do ciclo de vida de uma missão (da origem à conclusão e registo financeiro). Inclui funcionalidades complementares de apoio à gestão.

### 2.1 Intervenientes

O processo de Gestão de Missão de Serviço envolve a colaboração e interação entre os seguintes intervenientes:

*   **Direções Organizacionais:** Responsáveis pela autorização inicial dos pedidos de missão.
*   **Recursos Humanos (RH):** Gerem a logística, desde a solicitação de propostas até à emissão de requisições e acompanhamento do pagamento.
*   **Colaboradores:** Beneficiários das missões de serviço e recetores de notificações e informações sobre as suas viagens.
*   **Agências de Viagem / Prestadores de Serviço:** Fornecem propostas de serviços (passagens, alojamento, seguros) e recebem requisições formais.
*   **SGAL (Sistema de Gestão Administrativa e Logística):** Responsável pela cabimentação e gestão administrativa dos processos financeiros.
*   **Departamento Financeiro:** Executa os pagamentos e gere as transferências financeiras associadas às missões.

### 2.2 Mapa de Funcionalidades

#### 2.2.1 Descrição das Funcionalidades (Sistema RH)

| Funcionalidade | Descrição Detalhada |
| :--- | :--- |
| **Processo de Missão de Viagem** | Este processo inicia-se com o pedido autorizado pela direção, identificando o colaborador, destino e datas da viagem. O departamento de Recursos Humanos (RH) é responsável por toda a logística, que inclui o envio de pedidos de propostas a agências, seleção do fornecedor, emissão de requisição e recolha de faturas, culminando com o pagamento. As despesas são categorizadas em ajuda de custo, passagem, alojamento e seguro de viagem. O sistema deve suportar anexos, notificações automáticas, controlo de pagamentos e integração com o SGAL para cabimento das faturas. |
| **Lista de Missão de Serviço** | Esta funcionalidade oferece uma visualização centralizada de todas as missões de serviço registadas no sistema. A lista exibe informações cruciais como o número da missão, nome do colaborador, destino, datas da viagem, estado do processo (e.g., em análise, requisitada, cabimentada, paga) e a situação das faturas associadas. Permite ao RH acompanhar o progresso de cada missão, aplicar filtros por período, tipo (nacional ou internacional), colaborador ou estado, e aceder rapidamente aos detalhes e documentos anexos. |
| **Notificação / Alerta** | Para assegurar a fluidez e eficiência do processo, o sistema gera diversas notificações e alertas, direcionados aos intervenientes apropriados. Estes mecanismos de comunicação visam manter todos informados sobre o estado das missões, prazos importantes e ações necessárias. |
| **Parametrizações** | A eficácia e flexibilidade do sistema de missão de serviço dependem de mecanismos de parametrização robustos. Estas configurações permitem ajustar o comportamento das comunicações (notificações e alertas) sem a necessidade de alterações no código-fonte, adaptando-se às necessidades específicas do RH. Inclui a parametrização de tipos de notificação/alerta, destinatários, conteúdo (templates com campos dinâmicos) e gatilhos (eventos que despoletam o envio). |

#### 2.2.2 Portal (Nota: Especificação das funcionalidades do Portal serão desenvolvidas na segunda fase do projeto)

O portal permitirá aos colaboradores:

*   **Visualizar Notificações e Alertas:** O colaborador poderá aceder a todas as notificações e alertas relacionados com as suas missões de viagem.
*   **Acompanhar o Estado da Missão:** Será possível acompanhar todos os detalhes da missão, incluindo informações sobre ajuda de custo, alojamento e dados de passagem.

---

## 3. Notificações e Alertas

### 3.1 Notificações

O sistema gerará as seguintes notificações para os intervenientes:

1.  **Notificações aos Colaboradores e Direções que solicitaram a missão:**
    *   **Confirmação de Pedido de Missão:** Enviada ao colaborador e à sua direção após a submissão e autorização inicial do pedido de missão.
    *   **Detalhes da Viagem Confirmados:** Enviada ao colaborador quando todos os arranjos logísticos (bilhetes, alojamento, seguro) estão finalizados e confirmados. Esta notificação deve incluir todos os detalhes relevantes para a viagem.
    *   **Informação sobre Ajuda de Custo:** Confirmação de pagamento da ajuda de custo efetuado.

2.  **Notificações aos RH:**
    *   **Novo Pedido de Missão Autorizado:** Alerta o RH sobre um novo pedido de missão que requer a sua análise e processamento.
    *   **Confirmação de Cabimento (SGAL):** Informa o RH que o cabimento de uma fatura ou ajuda de custo foi processado pelo SGAL.

3.  **Notificações às Agências de Viagem:**
    *   **Pedido de Simulação/Proposta:** Enviada automaticamente às agências selecionadas para solicitar propostas de fatura proforma.
    *   **Requisição de Serviço:** Enviada à agência selecionada para formalizar a contratação de um serviço (ex: emissão de bilhete, reserva de alojamento).

4.  **Notificações ao Financeiro:**
    *   **Cabimento Pendente de Autorização:** Alerta o departamento financeiro sobre um cabimento que aguarda a sua autorização final para pagamento.

5.  **Notificação de RH a todos envolvidos sobre:**
    *   Alteração ou cancelamento da missão.

### 3.2 Alertas

O sistema emitirá os seguintes alertas para os intervenientes:

*   **Alertas de Pagamento e Faturas:**
    *   **Fatura Próxima do Vencimento:** Alerta o RH e o Financeiro (5 a 10 dias antes) sobre faturas que estão prestes a vencer, para que o pagamento seja processado atempadamente.
    *   **Fatura em Atraso:** Alerta o RH e o Financeiro sobre faturas cujo prazo de pagamento já expirou.
    *   **Fatura em Falta:** Alerta o RH se uma fatura final esperada de uma agência não for recebida dentro de um prazo razoável após a requisição.

*   **Alertas de Processo:**
    *   **Requisição Pendente de Resposta:** Alerta o RH se uma agência não confirmar a requisição de serviço dentro do tempo esperado.
    *   **Missão Próxima da Data de Início sem Confirmação:** Alerta o RH se uma missão estiver próxima da data de início e ainda não tiver todos os arranjos logísticos confirmados.
    *   **Missão com documentos obrigatórios em falta:** (ex: fatura, comprovativo de alojamento).

---

## 4. Especificação Técnica e Funcional

### 4.1 Requisitos Funcionais / Regras

*   É mandatório associar os endereços de email aos prestadores de serviços na gestão de entidades do sistema financeiro para o correto funcionamento das notificações automáticas.

### 4.2 Processo de Submissão de Missão de Serviço

O processo de submissão de missão de serviço é composto pelas seguintes etapas:

| Etapa | Responsável | Descrição | Observações |
| :--- | :--- | :--- | :--- |
| **1. Submissão de Pedidos de Missão** | Direções | Fase inicial onde a necessidade de uma missão é identificada e formalmente registada através de um formulário digital. O sistema valida automaticamente o preenchimento de todos os campos obrigatórios. | Após autorização, o pedido é encaminhado para o RH. |
| **2. Análise / Verificação RH** | RH | O RH analisa o pedido e contacta agências de viagem para solicitar faturas proforma. Seleciona até três fornecedores e envia e-mails automáticos com os detalhes da missão. | As agências devem ter e-mail associado na gestão de entidades do financeiro. |
| **3. Emissão de Requisição** | RH | Após receber e analisar as propostas das agências, o RH seleciona a(s) agência(s) e o sistema gera uma ou mais requisições formais com um número sequencial único. | O documento da proposta (fatura proforma) é anexado à requisição e enviado automaticamente aos fornecedores selecionados. |
| **4. Processamento Logístico** | RH | Registo de todos os aspetos logísticos da missão, incluindo Bilhete de Passagem, Seguro de Viagem, Alojamento e Ajuda de Custo. | O cálculo da ajuda de custo depende da função do colaborador, tipo de missão (nacional/internacional) e condições de alojamento. Notificação automática ao colaborador com os detalhes da viagem. |
| **5. Cabimentação SGAL** | SGAL | Integração com o SGAL para realizar o cabimento diretamente na plataforma ou exportar dados para o SIPS FUN. Gerencia o cabimento individual para ajuda de custo e para agências/hotéis para os restantes serviços. | Cabimentos manuais são necessários para alojamento no exterior devido à reconversão de divisas. |
| **6. Autorização RH** | RH | O cabimento é autorizado pelo RH para dar seguimento ao respetivo pagamento. | O estado do cabimento é atualizado para 'AUTORIZADO'. |
| **7. Pagamento Financeiro** | Financeiro | Executado automaticamente pelo sistema, encerrando o processo assim que o pagamento for efetuado no sistema financeiro. | O estado da missão é atualizado para 'PAGAMENTO'. |

---

## 5. Detalhe das Etapas (Especificação de Campos e Gravação)

### 5.1 ETAPA 1: Submissão e Autorização

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Número Missão** | NUMBER | Gerado automaticamente pelo sistema (sequencial único). Não editável. Identificador principal da missão. | `RH_T_MISSAO_SERVICO.NR_MISSAO` |
| **País Destino** | LOOKUP | Indica o país de destino da missão. Se "Cabo Verde", a missão é nacional; caso contrário, estrangeira. (Preenchimento Obrigatório) | `RH_T_MISSAO_SERVICO.PAIS_DESTINO_ID` / `RH_T_MISSAO_SERVICO.FLG_DESTINO` |
| **Descrição do Destino** | TEXT | Especifica o local preciso de realização da missão. (Preenchimento Obrigatório) | `RH_T_MISSAO_SERVICO.DESCRICAO_DESTINO` |
| **Data Início da Missão** | DATE | Indica a data de início prevista para a realização da missão. (Preenchimento Obrigatório) | `RH_T_MISSAO_SERVICO.DATA_INICIO` |
| **Data Fim da Missão** | DATE | Indica a data final prevista para a realização da missão. (Preenchimento Obrigatório) | `RH_T_MISSAO_SERVICO.DATA_FIM` |
| **Número Dias** | NUMBER | Calculado automaticamente entre a data de início e fim. | `RH_T_MISSAO_SERVICO.NR_DIAS` |
| **Nome do Colaborador** | LOOKUP | Pesquisa o colaborador no sistema. | `RH_T_MISSAO_COLABORADOR.FUN_ID` |
| **Número Documento** | TEXT | Preenchido automaticamente. | `RH_T_MISSAO_COLABORADOR.NUM_DOCUMENTO` |
| **Autorizado por** | SELECT | Campo preenchido automaticamente com o nome do utilizador que submeteu o pedido, com possibilidade de alteração. (Preenchimento Obrigatório) | `RH_T_MISSAO_SERVICO.AUTORIZADO_POR` |
| **Data Autorização** | DATE | Data de autorização. Por defeito, a data corrente, mas editável. (Preenchimento Obrigatório) | `RH_T_MISSAO_SERVICO.DATA_AUTORIZACAO` |
| **Tipo Documento** | SELECT | Opcional. Permite anexar documentos como convites, agendas, etc. (PDF). | `RH_T_DOCUMENTO.TP_DOCUMENTO_ID` |
| **Documento** | UPLOAD | Upload do documento. | `RH_T_DOCUMENTO.DOC_ID` |

### 5.2 ETAPA 2: Análise (RH)

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Nome Prestador Serviço** | LOOKUP | Permite selecionar mais de um prestador de serviço (agência de viagem) a partir da gestão de entidades do sistema financeiro. (Preenchimento Obrigatório) | `RH_T_MISSAO_PRESTADOR.ENT_ID` / `RH_T_MISSAO_PRESTADOR.NOME` |
| **Email** | TEXT | Preenchido automaticamente a partir da pesquisa de nome do prestador. (Preenchimento Obrigatório) | `RH_T_MISSAO_PRESTADOR.EMAIL` |
| **Assunto** | TEXT | Preenchido automaticamente com base na parametrização de notificações definida no sistema. | `RH_T_NOTIFICACAO.ASSUNTO` |
| **Corpo do Email** | TEXT | Preenchido automaticamente com base na parametrização de notificações definida no sistema. | `RH_T_NOTIFICACAO.MESSAGE` |

### 5.3 ETAPA 3: Emissão de Requisição

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Selecionar Prestador** | CHECK | Permite selecionar uma ou mais prestadoras para o envio da requisição. | - |
| **Prestador Serviço** | TEXT | Preenchido automaticamente com o nome de todos os prestadores aos quais foi enviada a proposta. | `RH_T_MISSAO_REQUISICAO.MISSAO_PREST_ID` / `RH_T_MISSAO_PRESTADOR.NOME` |
| **Email** | TEXT | Preenchido automaticamente com o email de todos os prestadores aos quais foi enviada a proposta. | `RH_T_MISSAO_PRESTADOR.EMAIL` |
| **Colaborador** | MULTISELECT | Permite associar um ou mais colaboradores a cada prestador de serviço para o envio da requisição. (Preenchimento Obrigatório) | `RH_T_MISSAO_REQUISICAO.MISSAO_COLAB_ID` / `RH_T_MISSAO_COLABORADOR.FUN_ID` |
| **Anexar Proposta** | UPLOAD | Permite anexar a proposta recebida por email, enviada pelos prestadores. | `RH_T_DOCUMENTO.TP_DOCUMENTO_ID` / `RH_T_DOCUMENTO.REFERENCIA_ID` |

### 5.4 ETAPA 4: Processamento Logístico

#### 5.4.1 Bilhete de Passagem

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Prestador Serviço** | TEXT | Nome do prestador de serviço (preenchido automaticamente com os dados da requisição da etapa anterior). (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA.PRESTADOR_SERV_ID` / `RH_T_MISSAO_PRESTADOR.NOME` |
| **Colaboradores** | MULTISELECT | Preenchido automaticamente a partir dos dados da requisição da etapa anterior. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA_DET.MISSAO_LOGIST_ID` / `RH_T_MISSAO_COLABORADOR.FUN_ID` |
| **Valor** | NUMBER | Introduzir o Valor de Bilhete de Passagem. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` |

#### 5.4.2 Seguro de Viagem

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Nome** | LOOKUP | Nome da seguradora (permite pesquisar a entidade no sistema financeiro). (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA.NOME_SEGURADORA` / `RH_T_MISSAO_LOGISTICA.ENT_ID` |
| **Colaborador** | MULTISELECT | Permite associar um ou mais colaboradores afetos a cada seguro de viagem. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA_DET.MISSAO_LOGIST_ID` / `RH_T_MISSAO_COLABORADOR.NOME` |
| **Valor** | NUMBER | Valor de seguro de viagem. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` |
| **Anexo** | UPLOAD | Anexar documento comprovativo de Seguro de viagem. | `RH_T_DOCUMENTO.TP_DOCUMENTO_ID` / `RH_T_DOCUMENTO.DOC_ID` |

#### 5.4.3 Alojamento

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Lugar Hospedagem** | TEXT | Nome do local onde o colaborador ficará hospedado. | `RH_T_MISSAO_LOGISTICA.LUGAR_HOSPEDAGEM` |
| **Inclui Alimentação** | CHECK | Indicar se o alojamento inclui alimentação. | `RH_T_MISSAO_LOGISTICA.FLG_ALIMENTACAO` |
| **Valor Alojamento (Diário)** | NUMBER | Valor de alojamento diário. | `RH_T_MISSAO_LOGISTICA.VALOR_DIARIO` |
| **Valor Total** | NUMBER | Valor total de alojamento. | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` |
| **Moeda** | TEXT | Tipo de moeda em que o alojamento será pago. | `RH_T_MISSAO_LOGISTICA.MOEDA` |
| **Data Início/Fim** | DATE | Datas de início e fim do alojamento (preenchidas automaticamente com as datas da missão, mas editáveis). | `RH_T_MISSAO_LOGISTICA.DATA_INICIO` / `RH_T_MISSAO_LOGISTICA.DATA_FIM` |
| **Colaborador** | MULTISELECT | Permite associar um ou mais colaboradores afetos a cada alojamento. | `RH_T_MISSAO_LOGISTICA_DET.MISSAO_LOGIST_ID` / `RH_T_MISSAO_COLABORADOR.FUN_ID` |
| **Anexar Documento** | UPLOAD | Permite anexar o documento comprovativo, caso necessário. | `RH_T_DOCUMENTO.TP_DOCUMENTO_ID` / `RH_T_DOCUMENTO.DOC_ID` |

#### 5.4.4 Ajuda de Custo

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Colaborador** | SELECT | Selecionar o colaborador afeto à missão. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA_DET.MISSAO_LOGIST_ID` / `RH_T_MISSAO_COLABORADOR.FUN_ID` |
| **Inclui Alojamento?** | CHECK | Indicar se a ajuda de custo inclui alojamento. | `RH_T_MISSAO_LOGISTICA.FLG_ALOJAMENTO` |
| **Número Dias Alojamento** | NUMBER | Indicar o número de dias em que o colaborador assumirá o alojamento. | `RH_T_MISSAO_LOGISTICA.NR_DIAS` |
| **Valor Diário** | NUMBER | Preenchido automaticamente com base no cálculo definido na parametrização. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA.VALOR_DIARIO` |
| **Valor Total** | NUMBER | Cálculo entre o valor diário e o número de dias de missão. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` |

**Regras de Cálculo da Ajuda de Custo:**

*   **Função do colaborador:** O valor varia consoante a função que o colaborador desempenha.
*   **Missão nacional ou internacional:** As missões internacionais suportam um valor superior às nacionais.
*   **Alojamento:**
    *   Caso o colaborador tenha o seu próprio alojamento, recebe 100% do valor de ajuda de custo.
    *   Caso a empresa pague o alojamento sem alimentação, transfere-se ao colaborador 2/3 do valor de ajuda de custo por dia.
    *   No caso de alojamento e alimentação serem garantidos, o trabalhador terá direito a 1/3 do montante estabelecido de ajuda de custo por dia.
    *   Se o colaborador ficar em casa de família, receberá 100% da ajuda de custo.

### 5.5 ETAPA 5: Cabimentação (SGAL)

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Selecionar** | CHECK | Permite selecionar cada item da lista (tipo de serviço) que se pretende cabimentar. | `RH_T_MISSAO_LOGISTICA.ID` |
| **Tipo Serviço** | TEXT | Apresenta, em cada linha, os tipos de serviço associados à missão: Bilhete Passagem, Seguro de Viagem, Alojamento, Ajuda de Custo. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA.REFERENCIA` |
| **Nome** | TEXT | Apresenta o nome do prestador de serviço ou, no caso da ajuda de custo, o nome do colaborador. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA.PRESTADOR_SERV_ID` / `RH_T_MISSAO_LOGISTICA_DET.MISSAO_COLABORADOR_ID` |
| **Valor** | NUMBER | Apresenta o valor total a ser cabimentado por tipo de serviço. (Preenchimento Obrigatório) | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` |
| **Anexar Documento** | UPLOAD | Permite anexar documento de Bilhete de passagem. | `RH_T_DOCUMENTO.TP_DOCUMENTO_ID` / `RH_T_DOCUMENTO.DOC_ID` |
| **Ver Detalhe** | HYPERLINK | Permite visualizar os detalhes das informações inseridas na etapa anterior para cada tipo de serviço. | - |

### 5.6 ETAPA 6: Autorização RH

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Tipo Serviço** | TEXT | Apresenta, em cada linha, os tipos de serviço associados à missão: Bilhete Passagem, Seguro de Viagem, Alojamento, Ajuda de Custo. | `RH_T_MISSAO_LOGISTICA.REFERENCIA` |
| **Nome** | TEXT | Apresenta o nome do prestador de serviço ou, no caso da ajuda de custo, o nome do colaborador. | `RH_T_MISSAO_LOGISTICA.PRESTADOR_SERV_ID` / `RH_T_MISSAO_LOGISTICA_DET.MISSAO_COLABORADOR_ID` |
| **Valor** | NUMBER | Apresenta o valor total a ser cabimentado por tipo de serviço. | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` |
| **Ver Detalhe** | HYPERLINK | Permite visualizar os detalhes das informações inseridas na etapa anterior para cada tipo de serviço. | - |
| **Número Cabimento** | CHECK | Mostra o número de Cabimento Gerado na Etapa Anterior. | `RH_T_MISSAO_LOGISTICA.CAB_ID` |

### 5.7 ETAPA 7: Pagamento Financeiro

Esta etapa é executada automaticamente pelo sistema, encerrando o processo assim que o pagamento for efetuado no sistema financeiro. O estado da missão é atualizado para 'PAGAMENTO'.

---

## 6. Gestão e Listagem

### 6.1 Lista de Missões

Esta funcionalidade permite a gestão operacional das missões, oferecendo filtros e ações para facilitar o acompanhamento.

#### 6.1.1 Filtros Disponíveis

| Filtro | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Nº Missão** | TEXT | Permite filtrar a lista com base no número de missão. | `RH_T_MISSAO_SERV.NR_MISSAO` |
| **Período De:** | DATE | Permite filtrar a lista com base na data de início da missão. | `RH_T_MISSAO_SERV.DATA_INICIO` |
| **Período Até:** | DATE | Permite filtrar a lista com base na data de fim da missão. | `RH_T_MISSAO_SERV.DATA_FIM` |

#### 6.1.2 Detalhes da Lista

| Item | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Estado** | TEXT | Estado do processo de missão. | `RH_T_MISSAO_SERVICO.ESTADO` |
| **Nº Missão** | NUMBER | Número de Missão. | `RH_T_MISSAO_SERVICO.NR_MISSAO` |
| **Nacional/Estrangeiro** | TEXT | Indica se a missão é nacional ou estrangeira. | `RH_T_MISSAO_SERVICO.FLG_DESTINO` |
| **Data Missão** | DATE | Data de início da missão. | `RH_T_MISSAO_SERVICO.DATA_INICIO` |
| **Valor AC** | NUMBER | Valor total de Ajuda de Custo. | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` (REFERENCIA = AJUDA_CUSTO) |
| **Valor BP** | NUMBER | Valor total de Bilhete de Passagem. | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` (REFERENCIA = BILHETE_PASSAGEM) |
| **Valor Alojamento** | NUMBER | Valor total de Alojamento. | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` (REFERENCIA = ALOJAMENTO) |
| **Valor Seguro** | NUMBER | Valor total de seguro de viagem. | `RH_T_MISSAO_LOGISTICA.VALOR_TOTAL` (REFERENCIA = SEGURO_VIAGEM) |
| **Ver Detalhe** | HYPERLINK | Permite visualizar os detalhes do processo da missão de viagem. | - |
| **Etapa** | TEXT | O utilizador pode ver o nome da etapa em que o processo se encontra e clicar no link para avançar para a próxima etapa. | `RH_T_MISSAO_SERVICO.ETAPA` |

#### 6.1.3 Ações Disponíveis

*   **Novo Processo de Missão:** Permite iniciar um novo processo de missão de viagem, abrindo o formulário da Etapa 1 – Submissão do Pedido de Missão.
*   **Ver Notificação:** Permite visualizar todas as notificações desse processo.
*   **Ver Alerta:** Permite visualizar todos os alertas gerados nesse processo.
*   **Cancelar:** Permite cancelar uma missão.

### 6.2 Cancelamento de Missão

Ao cancelar uma missão, as seguintes ações são executadas:

| Campo | Tipo | Descrição | Gravação (Tabela.Campo) |
| :--- | :--- | :--- | :--- |
| **Motivo Cancelamento** | TEXTAREA | Estado do processo de missão. | `RH_T_MISSAO_SERVICO.MOTIVO_CANCELAMENTO` |

**Atualizações nas Tabelas Associadas:**

*   `ESTADO = I` (Inativo)
*   `USER_ALTERACAO_ID`
*   `USER_ALTERACAO_NAME`
*   `DATA_ALTERACAO`

**Notificação de Cancelamento:**

Caso a viagem já tenha ultrapassado a etapa de análise, será enviada uma notificação a todos os que anteriormente receberam notificações relativas a essa missão, informando sobre o seu cancelamento. Esta notificação incluirá o número da missão e o motivo do cancelamento.

### 6.3 Lista de Alertas / Job

Será implementado um *job* que regista os alertas descritos na secção 3.2.

### 6.4 Lista de Notificações

O sistema enviará notificações em cada uma das etapas descritas na secção 3.1.

---

## 7. Modelo de Dados (Tabelas Principais)

As principais tabelas envolvidas na gestão de missão de serviço são:

*   `RH_T_MISSAO_SERVICO`: Armazena os dados gerais e de cabeçalho de cada missão de serviço.
*   `RH_T_MISSAO_COLABORADOR`: Contém informações sobre os colaboradores afetos a cada missão.
*   `RH_T_MISSAO_PRESTADOR`: Regista os fornecedores (agências de viagem) consultados ou selecionados para a missão.
*   `RH_T_MISSAO_LOGISTICA`: Detalha os custos logísticos da missão, como bilhetes de passagem, seguros, alojamento e ajudas de custo.
*   `RH_T_MISSAO_REQUISICAO`: Guarda os registos das requisições de serviço emitidas para os prestadores.
*   `RH_T_DOCUMENTO`: Repositório para todos os documentos anexados relacionados com as missões.
*   `RH_T_NOTIFICACAO`: Mantém um histórico de todas as comunicações (notificações e alertas) enviadas pelo sistema.

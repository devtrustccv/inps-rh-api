![SIPS-RH](media/image3.jpeg){alt="C:\\Users\\joelm\\Desktop\\Imagens\\sergey-zolkin-_UeY8aTI6d0-unsplash (2).jpg"
width="14.656944444444445in" height="9.770833333333334in"}

**DOSSIÊ DO COLABORADOR**

# Enquadramento 

Este documento descreve a **Especificação Funcional** para a
implementação da funcionalidade **Dossiê do Colaborador** no sistema de
RH. A funcionalidade visa armazenar, organizar e garantir o acesso
controlado a dados do colaborador, como informações pessoais, contratos,
avaliações de desempenho e outros documentos relacionados. A
especificação define os requisitos, fluxos de dados e integrações
necessários para garantir a correta operação do dossiê, com foco na
segurança e conformidade com as normas aplicáveis.

# Âmbito 

  -------------------- ----------------------------------------------------
   **Funcionalidade**                     **Descrição**

       Registo do        A funcionalidade de **Registo de Colaboradores**
      Colaborador       permite criar e atualizar o dossiê digital de cada
                           trabalhador no sistema de RH. Através de uma
                          interface intuitiva, o utilizador insere dados
                       pessoais, contactos, identificação (NIF/BI), vínculo
                             e informações contratuais, enquadramento
                         organizacional (direção, função, escalão), dados
                             bancários e qualificações académicas ou
                          profissionais, podendo ainda anexar documentos
                              relevantes como contratos e certidões.

        Lista de         A **Lista de Colaboradores** apresenta de forma
     Colaboradores       organizada todos os trabalhadores registados no
                          sistema de RH, com dados essenciais como nome,
                       cargo, direção, vínculo e estado. Permite pesquisar,
                          aplicar filtros e aceder rapidamente ao perfil
                          detalhado de cada colaborador para consulta ou
                                           atualização.

       Dossiê do            O **Dossiê do Colaborador** reúne todas as
      colaborador        informações individuais do trabalhador num único
                       registo digital. Contém dados pessoais, contratuais,
                             organizacionais, bancários, académicos e
                       profissionais, bem como documentos anexos, servindo
                          de base para gestão, consulta e atualização no
                                          sistema de RH.
  -------------------- ----------------------------------------------------

## Mapa Mental das Funcionalidades 

> ![Uma imagem com texto, diagrama, captura de ecrã, número Os conteúdos
> gerados por IA podem estar
> incorretos.](media/image4.png){width="7.327083333333333in"
> height="5.893055555555556in"}

# Especificação 

Nestas sessão se descreve de forma integrada o que o sistema deve fazer
(regras, fluxos e funcionalidades) e como deve ser implementado
(arquitetura, dados, integrações e requisitos).

## Registo do colaborador 

### Fluxo de registo de colaborador 

![Uma imagem com texto, captura de ecrã, ecrã, diagrama Os conteúdos
gerados por IA podem estar
incorretos.](media/image5.png){width="5.982638888888889in"
height="2.995138888888889in"}

  ------------- ----------------- -------------------------------------------
    **Etapa**    **Responsável**                 **Descrição**

    Registo /        Técnico        Os dados de registo do colaborador são
   Atualização                      inseridos no sistema pelo técnico com o
       do                         perfil adequado. Esses registos permanecem
   Colaborador                     no estado **Não Validado**, aguardando a
                                           validação do responsável.

    Validação        Técnico       O registo do colaborador é validado pelo
                 responsável com  técnico com o perfil adequado. Esse técnico
                   perfil para        pode atualizar os dados, caso seja
                    validação     necessário. Ao validar os dados, o registo
                                           é alterado para o estado
                                              **\'Validado\'**.\"
  ------------- ----------------- -------------------------------------------

### Desenho de interface e Descrição

### Dados Pessoais

![Uma imagem com texto, captura de ecrã, software, Ícone de computador
Os conteúdos gerados por IA podem estar
incorretos.](media/image6.png){width="8.4875in"
height="5.138194444444444in"}

+:-----------------:+:-----------------:+:-------------------:+:------------------------:+:---------------------------------------:+
| **Formulario**    | **Tipo**          | **Descrição**                                  | **Gravação**                            |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| **identificação do Colaborador**                                                                                                 |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \*Tipo Documento  | *SELECT*          | Tipo de documento de identificação do          | *RH_T_FUNCIONARIOS.TIPO_DOCUMENTO*      |
| Identificação     |                   | Colaborador                                    |                                         |
|                   |                   |                                                | *RH_T_DOCUMENTO_PESSOAL.TIPO_DOCUMENTO* |
|                   |                   | **Tabela**: RH_T_TIPO_DOCUMENTO onde tipo      |                                         |
|                   |                   | '**DOCUMENTO_PESSOAL'**                        |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \* N.º Documento  | *TEXT*            | Numero documento de identificação do           | *RH_T_FUNCIONARIOS.NUM_DOCUMENTO*       |
| de Identificação  |                   | colaborador                                    |                                         |
|                   |                   |                                                | *RH_T_DOCUMENTO_PESSOAL.NUM_DOCUMENTO*  |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \*Nome            | *TEXT*            | [Número do documento apresentado para fins de  | *RH_T_FUNCIONARIOS.NOME*                |
|                   |                   | identificação]{.mark}                          |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Foto              | *IMAGEM*          | PENDENTE: decidir como será registado (ver com | *RH_T_FUNCIONARIOS.FOTOGRAFIA*          |
|                   |                   | ATY)                                           |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \*Data Nascimento | *DATE*            | [Nome completo do colaborador, conforme o      | *RH_T_FUNCIONARIOS.DATA_NASCIMENTO*     |
|                   |                   | documento de identificação]{.mark}             |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \*Género          | *SELECT*          | [Data do Nascimento do colaborador (*DOMAINS = | *RH_T_FUNCIONARIOS.SEXO*                |
|                   |                   | GENERO*)]{.mark}                               |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \*Nome Mãe        | *TEXT*            | Nome completo da mãe do colaborador            | *RH_T_FUNCIONARIOS.NM_MAE*              |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \*Nome Pai        | *TEXT*            | Nome completo do pai do colaborador            | *RH_T_FUNCIONARIOS.NM_PAI*              |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \*Estado Civil    | *SELECT*          | Esrado civil atual do colaborador              | *RH_T_FUNCIONARIOS.ESTADO_CIVIL*        |
|                   |                   | (*DOMAINS=ESTADO_CIVIL*)                       |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \*Nacionalidade   | *SELECT*          | País de nacionalidade do colaborador           | *RH_T_FUNCIONARIOS.* *NACIONALIDADE*    |
|                   |                   | (***SIPSGLOBAL**.GLB_T_GEOGRAFIA.ID*)          |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| \*Naturalidade    | *LOCKUP*          | Local de nascimento do colaborador             | *RH_T_FUNCIONARIOS**.**LOC_NASC_ID*     |
|                   |                   | (*GEOGRAFIA*)                                  |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Data emissão      | *DATE*            | Preenchida Automaticamente                     | *RH_T_DOCUMENTO_PESSOAl.DATA_EMISSAO*   |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Data Validade     | *DATE*            | Preenchida Automaticamente                     | *RH_T_DOCUMENTO_PESSOAl.DATA_VALIDADE*  |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| **Documentos      |                   |                                                |                                         |
| Administrativos** |                   |                                                |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| NIF               | *NUMBER*          | Numero de identificação Fiscal                 | *RH_T_FUNCIONARIOS.NIF*                 |
|                   |                   |                                                |                                         |
|                   |                   | \*PENDENTE: API Pesquisa NIF                   |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| N.º Segurado      | *NUMBER*          | Número de Identificação Fiscal do colaborador  | *RH_FUNCIONARIOS.* *NU_SEG_INPS*        |
|                   |                   |                                                |                                         |
|                   |                   | \*PENDENTE:API pesquisa segurado               |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| **Contacto**      |                   |                                                |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Tipo Contacto     | *SELECT*          | Tipo de Contacto do colaborador (Telemóvel,    | *RH_T_CONTACTO.TIPO_CONTACTO*           |
|                   |                   | Telefone, Email)                               |                                         |
|                   |                   |                                                |                                         |
|                   |                   | *DOMAINS = TP_CONTACTO*                        |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Contacto          | *TEXT*            | Número de telefone, endereço de e-mail ou      | *RH_T_CONTACTO.CONTACTO*                |
|                   |                   | outro contacto indicado pelo colaborador.      |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| **Endereço**                                                                                                                     |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Pais              | *SELECT*          | País onde o colaborador reside atualmente.     | *RH_T_ENDERECO.PAIS_ID*                 |
|                   |                   |                                                |                                         |
|                   |                   | **Função**: GET_GEOGRAFIA (P_NIVEL = 1)        |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Ilha              | *SELECT*          | Ilha de residência do colaborador              | *RH_T_ENDERECO.ILHA_ID*                 |
|                   |                   |                                                |                                         |
|                   |                   | **Função**: GET_GEOGRAFIA (P_NIVEL = 2)        |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Concelho          | *SELECT*          | Município onde o colaborador reside.           | *RH_T_ENDERECO.CONCELHO_ID*             |
|                   |                   |                                                |                                         |
|                   |                   | **Função:** GET_GEOGRAFIA (P_NIVEL = 3)        |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Freguesia         | *SELECT*          | Divisão administrativa onde se encontra a      | *RH_T_ENDERECO.FREGUESIA_ID*            |
|                   |                   | residência do colaborador.                     |                                         |
|                   |                   |                                                |                                         |
|                   |                   | **Função:** GET_GEOGRAFIA (P_NIVEL = 4)        |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Zona              | *SELECT*          | Bairro, distrito ou localidade dentro da       | *RH_T_ENDERECO.ZONA_ID*                 |
|                   |                   | freguesia.                                     |                                         |
|                   |                   |                                                |                                         |
|                   |                   | **Função:** GET_GEOGRAFIA (P_NIVEL = 5)        |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| Morada            | *TEXT*            | Endereço completo e detalhado do colaborador,  | *RH_T_ENDERECO.MORADA*                  |
|                   |                   | incluindo rua, número de casa, referência, etc |                                         |
+-------------------+-------------------+------------------------------------------------+-----------------------------------------+
| **REGRAS**                                                                                                                       |
+----------------------------------------------------------------------------------------------------------------------------------+
| - *A validação dos campos obrigatórios deve ser efetuada tanto ao nível do formulário como ao nível da tabela*                   |
|                                                                                                                                  |
| - *O sistema não permite duplicação de colaborador. Caso seja detetado um número de documento já registado, deve ser emitida uma |
|   mensagem de ERRO ("Já existe um colaborador registado com este número de documento"). A combinação entre o tipo de documento e |
|   o número de documento deve ser única*                                                                                          |
|                                                                                                                                  |
| - *O sistema deve validar se o **NIF** indicado corresponde efetivamente ao colaborador através da **API** disponibilizado.      |
|   Mensagem de ERRO: ''O NIF introduzido não corresponde ao colaborador selecionado**".** Validar com nome, data nascimento, nome |
|   de mae e pai*                                                                                                                  |
|                                                                                                                                  |
| - *O sistema deve validar se o **N.º Segurado** indicado corresponde efetivamente ao colaborador através da **API**              |
|   disponibilizado. Mensagem de ERRO: ''O Nº segurado introduzido não corresponde ao colaborador selecionado**".** Validar com    |
|   nome, data nascimento, nome de mae e pai.*                                                                                     |
|                                                                                                                                  |
| - *Validar se o contacto já está associado a outro funcionário e emitir um ALERTA: "O contacto informado já está associado a     |
|   outro colaborador"*                                                                                                            |
+----------------------------------------------------------------------------------------------------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                                                    |
+-------------------------------------------------------------+--------------------------+-----------------------------------------+
| 1.  ***RH_T_FUNCIONARIOS***                                 | 2.  ***RH_T_CONTACTO***  | 3.  ***RH_T_ENDERECO***                 |
|                                                             |                          |                                         |
| - *ESTADO **= 'A'***                                        | - *ESTADO = '**A**'*     | - *ESTADO = 'A'*                        |
|                                                             |                          |                                         |
| - *ESTADO_VALIDACAO = "**P**"*                              | - *USER_REGISTO \_ID =   | - *USER_REGISTO_ID = id de utilizador   |
|                                                             |   id de utilizador       |   Logado*                               |
| - *DATA_REGISTO= '**SYSDATE'***                             |   Logado*                |                                         |
|                                                             |                          | - *USER_REGISTO_NAME = id de utilizador |
| - *USER_REGISTO_ID = id de utilizador Logado*               | - *USER_REGISTO_NAME =   |   Logado*                               |
|                                                             |   nome de utilizador     |                                         |
| - *USER_REGISTO_NAME = nome de utilizador logado*           |   logado*                | - *USER_ALTERACAO_ID = **NULL***        |
|                                                             |                          |                                         |
| - *USER_ALTERACAO_ID = **NULL***                            | - *USER_ALTERACAO_ID =   | - *DATA\_ REGISTO = **SYSDATE***        |
|                                                             |   **NULL***              |                                         |
| - *USER_ALTERACAO_NAME = **NULL***                          |                          | - *DATA_ALTERACAO = **NULL***           |
|                                                             | - *USER_ALTERACAO_NAME = |                                         |
| - *DATA_ALTERACAO = **NULL***                               |   **NULL***              | - *USER_ALTERACAO_NAME =**NULL***       |
|                                                             |                          |                                         |
|                                                             | - *DATA_REGISTO =        | - *FUN_ID= id de tabela                 |
|                                                             |   **SYSDATE***           |   **RH_T_FUNCIONARIOS***                |
|                                                             |                          |                                         |
|                                                             | - *DATA_ALTERACAO =      |                                         |
|                                                             |   **NULL***              |                                         |
|                                                             |                          |                                         |
|                                                             | - *FUN_ID = **ID** de    |                                         |
|                                                             |   tabela                 |                                         |
|                                                             |   **RH_T_FUNCIONARIOS*** |                                         |
+-------------------------------------------------------------+--------------------------+-----------------------------------------+
| 4.  ***RH_T_DOCUMENTO_PESSOAL***                                                                                                 |
|                                                                                                                                  |
| - *ESTADO **= 'A'***                                                                                                             |
|                                                                                                                                  |
| - *DATA_REGISTO= '**SYSDATE'***                                                                                                  |
|                                                                                                                                  |
| - *USER_REGISTO_ID = id de utilizador Logado*                                                                                    |
|                                                                                                                                  |
| - *USER_REGISTO_NAME =nome de utilizador Logado*                                                                                 |
|                                                                                                                                  |
| - *USER_ALTERACAO \_ID = **NULL***                                                                                               |
|                                                                                                                                  |
| - *DATA_ALTERACAO = **NULL***                                                                                                    |
|                                                                                                                                  |
| - *DATA_ALTERACAO_NAME = **NULL***                                                                                               |
|                                                                                                                                  |
| - *FUN_ID: ID de tabela **RH_T_FUNCIONARIOS***                                                                                   |
+----------------------------------------------------------------------------------------------------------------------------------+

### Agregado Dependente

![Uma imagem com texto, captura de ecrã, file, número Os conteúdos
gerados por IA podem estar
incorretos.](media/image7.png){width="9.693055555555556in"
height="4.367361111111111in"}

+:------------------:+:------------------:+:---------------------------:+:------------------------------:+
| **Formulario**     | **Tipo**           | **Descrição**               | **Gravação**                   |
+--------------------+--------------------+-----------------------------+--------------------------------+
| \*Tipo Documento   | *TEXT*             | Tipo de documento de        | *RH_T_FAMILIARES.TP_DOCUMENTO* |
| Identificação      |                    | identificação do            |                                |
|                    |                    | Colaborador                 |                                |
|                    |                    |                             |                                |
|                    |                    | *RH_TP_DOCUMENTO.REFERENCIA |                                |
|                    |                    | =* **DOCUMENTO_PESSOAL'**   |                                |
+--------------------+--------------------+-----------------------------+--------------------------------+
| \* N.º Documento   | *TEXT*             |                             | *RH_T_FAMILIARES.*             |
| de Identificação   |                    |                             | *NUM_DOCUMENTO*                |
+--------------------+--------------------+-----------------------------+--------------------------------+
| \*Nome             | *TEXT*             |                             | *RH_T_FAMILIARES.NOME*         |
+--------------------+--------------------+-----------------------------+--------------------------------+
| \*Data Nascimento  | *DATE*             |                             | *RH_T_FAMILIARES.*             |
|                    |                    |                             | *DATA_NASCIMENTO*              |
+--------------------+--------------------+-----------------------------+--------------------------------+
| \*Género           | *SELECT*           | ***DOMAINS** = GENERO*      | *RH_T_FAMILIARES.SEXO*         |
+--------------------+--------------------+-----------------------------+--------------------------------+
| \*Grau Parentesco  | *SELECT*           | ***DOMAINS** =              | *RH_T_FAMILIARES.GDP_ID*       |
|                    |                    | GRAUS_DE_PARENTESCO*        |                                |
+--------------------+--------------------+-----------------------------+--------------------------------+
| \*Dependente       | *SELECT*           | ***DOMAINS** = DEPENDENCIA* | *RH_T_FAMILIARES.*             |
|                    |                    |                             | *DEPENDENCIA*                  |
+--------------------+--------------------+-----------------------------+--------------------------------+
| \*Agregado         | *SELECT*           | ***DOMAINS=** MEMBRO_AGR*   | *RH_T_FAMILIARES.MEMBRO_AGR*   |
+--------------------+--------------------+-----------------------------+--------------------------------+
| **REGRAS**                                                                                             |
+--------------------------------------------------------------------------------------------------------+
| - *A validação dos campos obrigatórios deve ser efetuada tanto ao nível do formulário como ao nível da |
|   tabela*                                                                                              |
|                                                                                                        |
| - *O sistema deve garantir que não existam duplicados de familiares para o mesmo colaborador. A        |
|   verificação deve ser feita com base nos campos FUN_ID, NUM_DOCUMENTO e NOME. Mensagem de ERRO : "Já  |
|   existe um familiar com este nome e número de documento associado a este colaborador"*                |
+--------------------------------------------------------------------------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                          |
+--------------------------------------------------------------------------------------------------------+
| 1.  ***RH_T_FAMILIARES***                                                                              |
|                                                                                                        |
| - *ESTADO **= 'A'***                                                                                   |
|                                                                                                        |
| - *DATA_REGISTO= '**SYSDATE'***                                                                        |
|                                                                                                        |
| - *USER_REGISTO_ID = id de utilizador Logado*                                                          |
|                                                                                                        |
| - *USER_REGISTO_NAME = id de utilizador Logado*                                                        |
|                                                                                                        |
| - *USER_ALTERACAO \_ID = **NULL***                                                                     |
|                                                                                                        |
| - *USER\_ ALTERACAO \_NAME = Nome de utilizador Logado*                                                |
|                                                                                                        |
| - *DATA_ALTERACAO = **NULL***                                                                          |
|                                                                                                        |
| - *FUN_ID: ID de tabela **RH_T_FUNCIONARIOS***                                                         |
+--------------------------------------------------------------------------------------------------------+

### Dados Acedémicos e Profissional

![Uma imagem com texto, captura de ecrã, file, software Os conteúdos
gerados por IA podem estar
incorretos.](media/image8.png){width="9.693055555555556in"
height="2.767361111111111in"}

+:------------------:+:------------------:+:------------------------:+:---------------------------------------------:+
| **Formulario**     | **Tipo**           | **Descrição**            | **Gravação**                                  |
+--------------------+--------------------+--------------------------+-----------------------------------------------+
| PAIS               | *SELECT*           | País onde o colaborador  | *RH_T_HABILITACOES_LITERARIAS.PAIS_ID*        |
|                    |                    | obteve a habilitação     |                                               |
|                    |                    | literária.\              |                                               |
|                    |                    | (Ex.: Cabo Verde,        |                                               |
|                    |                    | Portugal, Brasil)        |                                               |
|                    |                    |                          |                                               |
|                    |                    | ***Função** :*           |                                               |
|                    |                    | GET_GEOGRAFIA (P_NIVEL   |                                               |
|                    |                    | =1 )                     |                                               |
+--------------------+--------------------+--------------------------+-----------------------------------------------+
| ESTABELECIMENTO    | *TEXT*             | Nome da instituição de   | *RH_T_HABILITACOES_LITERARIAS.ESTABLECIMENTO* |
|                    |                    | ensino onde foi          |                                               |
|                    |                    | realizada a formação.\   |                                               |
|                    |                    | *(Ex.: Universidade de   |                                               |
|                    |                    | Lisboa, Instituto        |                                               |
|                    |                    | Politécnico de Cabo      |                                               |
|                    |                    | Verde)*                  |                                               |
+--------------------+--------------------+--------------------------+-----------------------------------------------+
| AREA               | *SELECT*           | Área de estudo ou        | *RH_T_HABILITACOES_LITERARIAS.AREA*           |
|                    |                    | domínio científico a que |                                               |
|                    |                    | pertence a formação.\    |                                               |
|                    |                    | (Ex.: Ciências Sociais,  |                                               |
|                    |                    | Engenharia, Saúde)       |                                               |
|                    |                    |                          |                                               |
|                    |                    | ***DOMAINS** =           |                                               |
|                    |                    | AREA_FORMACAO*           |                                               |
+--------------------+--------------------+--------------------------+-----------------------------------------------+
| CURSO              |                    | Designação específica do | *RH_T_HABILITACOES_LITERARIAS.NOME_CURSO*     |
|                    |                    | curso concluído ou       |                                               |
|                    |                    | frequentado pelo         |                                               |
|                    |                    | colaborador.             |                                               |
+--------------------+--------------------+--------------------------+-----------------------------------------------+
| GRAU ACÁDEMICO     |                    | Grau ou nível académico  | *RH_T_HABILITACOES_LITERARIAS.NIVEL*          |
| /NIVEL             |                    | correspondente à         |                                               |
|                    |                    | habilitação obtida.\     |                                               |
|                    |                    | (Ex.: Licenciatura,      |                                               |
|                    |                    | Mestrado, Doutoramento,  |                                               |
|                    |                    | Ensino Secundário,       |                                               |
|                    |                    | Técnico Profissional)    |                                               |
|                    |                    |                          |                                               |
|                    |                    | ***DOMAINS** =           |                                               |
|                    |                    | NIVEL_HABILITACOES*      |                                               |
+--------------------+--------------------+--------------------------+-----------------------------------------------+
| DATA INICIO        |                    | Data em que o            | RH_T_HABILITACOES_LITERARIAS. DATA_INICIO     |
|                    |                    | colaborador iniciou o    |                                               |
|                    |                    | curso ou formação.       |                                               |
+--------------------+--------------------+--------------------------+-----------------------------------------------+
| DATA TERMINO       |                    | Data em que o            | RH_T_HABILITACOES_LITERARIAS. DATA_FIM        |
|                    |                    | colaborador concluiu (ou |                                               |
|                    |                    | abandonou) a formação.   |                                               |
+--------------------+--------------------+--------------------------+-----------------------------------------------+
| CONCLUIDO          |                    | Indicador (Sim/Não) que  | RH_T_HABILITACOES_LITERARIAS.CONCLUIDO        |
|                    |                    | informa se a formação    |                                               |
|                    |                    | foi concluída com        |                                               |
|                    |                    | aproveitamento.          |                                               |
|                    |                    |                          |                                               |
|                    |                    | ***DOMAINS               |                                               |
|                    |                    | =**SIM_NAO_NUMBER*       |                                               |
+--------------------+--------------------+--------------------------+-----------------------------------------------+
| **REGRAS**                                                                                                         |
+--------------------------------------------------------------------------------------------------------------------+
| - *A validação dos campos obrigatórios deve ser efetuada tanto ao nível do formulário como ao nível da tabela*     |
+--------------------------------------------------------------------------------------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                                      |
+--------------------------------------------------------------------------------------------------------------------+
| **1. RH_T_HABILITACOES_LITERARIAS**                                                                                |
|                                                                                                                    |
| - *ESTADO **= 'A'***                                                                                               |
|                                                                                                                    |
| - *DATA_REGISTO= '**SYSDATE'***                                                                                    |
|                                                                                                                    |
| - *USER_REGISTO_ID = id de utilizador Logado*                                                                      |
|                                                                                                                    |
| - *USER_REGISTO_NAME = nome de utilizador Logado*                                                                  |
|                                                                                                                    |
| - *USER_ALTERACAO \_ID = NULL*                                                                                     |
|                                                                                                                    |
| - *USER_ALTERACAO_NAME = **NULL***                                                                                 |
|                                                                                                                    |
| - *DATA_ALTERACAO = **NULL***                                                                                      |
|                                                                                                                    |
| - *FUN_ID: ID de tabela **RH_T_FUNCIONARIOS***                                                                     |
+--------------------------------------------------------------------------------------------------------------------+

d)  Dados Contratuais

> ![Uma imagem com texto, captura de ecrã, número, software Os conteúdos
> gerados por IA podem estar
> incorretos.](media/image9.png){width="8.104166666666666in"
> height="4.988194444444445in"}

+:---------------:+:--------------:+:-------------------:+:-------------------:+:------------------------------------------------------:+:------------------------------------------------------:+
| **Formulario**  | **Tipo**       | **Descrição**                             | **Gravação**                                                                                                    |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| **Dados         |                |                                           |                                                                                                                 |
| Contratuais**   |                |                                           |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Tipo contrato   | *Select*       |                                           | *[~~RH_T_TIPOS_RELACIONAMENTO.TIPO_CONTRATO_ID~~]{.mark}*                                                       |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *RH_T_CONTRATO_VINCULO.TIPO_CONTRATO_ID*                                                                        |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Cargo/Posição | *SELECT*       | Designação do cargo ou função que o       | *RH_T_TIPOS_RELACIONAMENTO.CARG_ID*                                                                             |
|                 |                | colaborador irá desempenhar na            |                                                                                                                 |
|                 |                | instituição.                              | *RH_T_CARREIRA.CARGO_ID*                                                                                        |
|                 |                |                                           |                                                                                                                 |
|                 |                | ***TABELA** : RH_CARGOS ; **CAMPOS**      |                                                                                                                 |
|                 |                | :cod_cargo e descricao*                   |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Direção       | *SELECT*       | Unidade orgânica ou direção em que o      | *RH_T_TIPOS_RELACIONAMENTO.* *INSTIT_ID*                                                                        |
|                 |                | colaborador está afeto.\                  |                                                                                                                 |
|                 |                | *(Ex.: Direção Financeira, Direção de     | *RH_T_MOBILIDADE.INSTIT_ID*                                                                                     |
|                 |                | Recursos Humanos)*                        |                                                                                                                 |
|                 |                |                                           |                                                                                                                 |
|                 |                | ***FUNÇÃO:** GET_DIRECAO_SERVICO*         |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*secção        | *SELECT*       | Subunidade ou divisão dentro da direção   | *RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID*                                                                           |
|                 |                | onde o colaborador desempenhará funções.\ |                                                                                                                 |
|                 |                | *(Ex.: Secção de Contabilidade)*          | *RH_T_MOBILIDADE.SECCAO_ID*                                                                                     |
|                 |                | (pendente)                                |                                                                                                                 |
|                 |                |                                           |                                                                                                                 |
|                 |                | **FUNÇÃO:** GET_SECCAO (*P\_ INSTIT_ID)*  |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Centro custo  | *TEXT*         | centro de custo responsável pelas         | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--*                           |
|                 |                | despesas com este colaborador (pendente)  |                                                                                                                 |
|                 |                | **FUNÇÃO:** *GET_NOME_CENTRO_CUSTO(*P\_   |                                                                                                                 |
|                 |                | INSTIT_ID*)*                              |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Carreira      | *SELECT*       | Estrutura profissional a que o            | *RH_T_TIPOS_RELACIONAMENTO.CARR_PCCS_ID*                                                                        |
|                 |                | colaborador pertence (pendente)           |                                                                                                                 |
|                 |                | **FUNÇÃO:** *GET_CARREIRA (P_CARGO)*      | *RH_T_CARREIRA.CARREIRA_ID*                                                                                     |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Categoria       | *SELECT*       | Nível ou grupo profissional do            | *RH_T_TIPOS_RELACIONAMENTO.CATEGORIA_ID*                                                                        |
|                 |                | colaborador dentro da carreira (pendente) |                                                                                                                 |
|                 |                |                                           | *RH_T_CARREIRA.CATEGORIA_ID*                                                                                    |
|                 |                | **FUNÇÃO:** *GET_CATEGORIA(P_CARREIRA)*   |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Escalão /     | *SELECT*       | Escalão salarial ou referência            | *RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID*                                                                          |
| referência      |                | remuneratória correspondente à posição do |                                                                                                                 |
|                 |                | colaborador                               | *RH_T_CARREIRA. ESCALAO_ID*                                                                                     |
|                 |                |                                           |                                                                                                                 |
|                 |                | ***FUNÇÃO:** GET_ESCALAO                  |                                                                                                                 |
|                 |                | (P_CARREIRA,P_CATEGORIA)*                 |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Tipo de       | *SELECT*       | Natureza do vínculo contratual entre      | *~~RH_T_TIPOS_RELACIONAMENTO.VINCULO_ID~~*                                                                      |
| vinculo Laboral |                | colaborador e entidade empregadora        |                                                                                                                 |
|                 |                |                                           | *RH_T_CONTRATO_VINCULO. VINCULO_ID*                                                                             |
|                 |                | (Ex.: Efetivo, Contrato a Termo,          |                                                                                                                 |
|                 |                | Requisição, Estágio)                      |                                                                                                                 |
|                 |                |                                           |                                                                                                                 |
|                 |                | ***FUNÇÃO:** GET_TIPO_VINCULO*            |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Regime        | *SELECT*       | Condições de prestação de trabalho        | *RH_T_REGIME_TRABALHO.REGIME*                                                                                   |
| Trabalho        |                | definidas no contrato.\                   |                                                                                                                 |
|                 |                | *(Ex.: Tempo Integral, Tempo Parcial,     | *RH_T_TIPOS_RELACIONAMENTO.REGIME*                                                                              |
|                 |                | Teletrabalho, Horário Flexível)*          |                                                                                                                 |
|                 |                |                                           |                                                                                                                 |
|                 |                | ***DOMAINS** = REGIME_TRABALHO*           |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Salário       | *NUMBER*       | Valor contratual da remuneração base do   | *RH_T_DEF_REMUNERACOES.VALOR*                                                                                   |
|                 |                | colaborador.                              |                                                                                                                 |
|                 |                |                                           | *RH_T_TIPOS_RELACIONAMENTO.SALARIO*                                                                             |
|                 |                | **FUNÇÃO** : GET_SALARIO (P_ESCALAO)      |                                                                                                                 |
|                 |                |                                           | *RH_T_CARREIRA.SALARIO*                                                                                         |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Moeda         | *SELECT*       | Moeda em que o salário e demais           | *RH_T_DEF_REMUNERACOES.MOEDA*                                                                                   |
|                 |                | remunerações são processados.\            |                                                                                                                 |
|                 |                | *(Ex.: CVE, EUR, USD)*                    | *RH_T_TIPOS_RELACIONAMENTO.MOEDA*                                                                               |
|                 |                |                                           |                                                                                                                 |
|                 |                | ***DOMAINS** = MOEDA*                     |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Data Inicio   | *DATE*         | Data em que o colaborador inicia          | *RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO*                                                                         |
|                 |                | efetivamente o exercício das funções.     |                                                                                                                 |
|                 |                |                                           | *RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO_CONTRATO*                                                                |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *RH_T_MOBILIDADE. DATA_INICIO*                                                                                  |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *RH_T_CARREIRA.DATA_INICIO*                                                                                     |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *RH_T_REGIME_TRABALHO. DATA_INICIO*                                                                             |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *RH_T_CONTRATO_VINCULO. DATA_INICIO*                                                                            |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Data Fim      | *DATE*         | Data prevista para o termo do vínculo     | *RH_T_TIPOS_RELACIONAMENTO.DATA_FIM*                                                                            |
|                 |                | laboral (quando aplicável).               |                                                                                                                 |
|                 |                |                                           | *RH_T_TIPOS_RELACIONAMENTO.DATA_FIM_CONTRATO*                                                                   |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *RH_T_MOBILIDADE. DATA_FIM*                                                                                     |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *RH_T_CARREIRA. DATA_FIM*                                                                                       |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *RH_T_CONTRATO_VINCULO. DATA_FIM*                                                                               |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *RH_T_REGIME_TRABALHO. DATA_FIM*                                                                                |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Duração (MESES) | *NUMBER*       | Período total de vigência do contrato,    | *RH_T_CONTRATO_VINCULO.DURACAO*                                                                                 |
|                 |                | expresso em meses ou anos (aplicável a    |                                                                                                                 |
|                 |                | contratos temporários). (Diferença entre  |                                                                                                                 |
|                 |                | data inicio funcão e data fim)            |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Local de      | *SELECT*       | Lugar fisico onde o Colaborador exerce o  | *RH_T_TIPOS_RELACIONAMENTO.LOC_TRAB_ID*                                                                         |
| Trabalho        |                | seu trabalho                              |                                                                                                                 |
|                 |                |                                           | *RH_T_MOBILIDADE. LOC_TRAB_ID*                                                                                  |
|                 |                | **FUNÇÃO** : GET_LOCAL_TRABALHO           |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Pais            | *TEXT*         | País onde o contrato terá execução        | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--*       |
|                 |                |                                           |                                                                                                                 |
|                 |                | **FUNÇÃO:**                               |                                                                                                                 |
|                 |                | GET_PAIS_LOCAL_TRAB(P_LOCAL_TRABALHO)     |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Ilha            | *TEXT*         | Localidade específica (quando aplicável   | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--*     |
|                 |                | em contexto nacional, ex.: Cabo Verde).   |                                                                                                                 |
|                 |                | Apresenta as ilhas correspondentes quando |                                                                                                                 |
|                 |                | o país escolhido for Cabo Verde           |                                                                                                                 |
|                 |                |                                           |                                                                                                                 |
|                 |                | **FUNÇÃO:**                               |                                                                                                                 |
|                 |                | GET_PAIS_ILHA_TRAB(P_LOCAL_TRABALHO)      |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| **Subsidios :** Indicação geral sobre a existência de subsídios atribuídos ao colaborador.                                                                                                     |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Tipo de       | *SELECT*       | Natureza do subsídio atribuído.\          | *RH_T_DEF_REMUNERACOES.TM_ID*                                                                                   |
| Subsídio        |                | *(Ex.: Subsídio de Alimentação, Subsídio  |                                                                                                                 |
|                 |                | de Transporte, Subsídio de Férias, 13.º   |                                                                                                                 |
|                 |                | mês)*                                     |                                                                                                                 |
|                 |                |                                           |                                                                                                                 |
|                 |                | ***FUNÇÃO**: GET_MOVIMENTO_REMUNERACAO    |                                                                                                                 |
|                 |                | (P_TIPO)*                                 |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Percentagem     | *TEXT*         | Percentagem do salário base usada para    | *RH_T_DEF_REMUNERACOES.PERCENTAGEM*                                                                             |
|                 |                | calcular o valor do subsídio (quando      |                                                                                                                 |
|                 |                | aplicável).                               |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Valor           | *NUMBER*       | Montante atribuído ao colaborador a       | *RH_T_DEF_REMUNERACOES.VALOR*                                                                                   |
|                 |                | título de subsídio                        |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| **Encargos / Descontos :** Indicação geral sobre os encargos (patronais) ou descontos (do colaborador) aplicáveis.                                                                             |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Tipo de       | *SELECT*       | Identificação do tipo de encargo ou       | *RH_T_DEF_PAGAMENTOS.TM_ID*                                                                                     |
| Encargos /      |                | desconto.\                                |                                                                                                                 |
| Descontao       |                | *(Ex.: INPS, Imposto IRPS, Fundo Social,  |                                                                                                                 |
|                 |                | Sindicato)*                               |                                                                                                                 |
|                 |                |                                           |                                                                                                                 |
|                 |                | ***FUNÇÃO**: GET_MOVIMENTO_DESCONTO       |                                                                                                                 |
|                 |                | (P_TIPO)*                                 |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Valor           | *NUMBER*       | Montante a deduzir ou a assumir pela      | *RH_T_DEF_PAGAMENTOS.VALOR*                                                                                     |
|                 |                | entidade empregadora, podendo ser fixo ou |                                                                                                                 |
|                 |                | percentual.                               |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| \*Data Inicio   | *DATE*         | Data a partir da qual o encargo/desconto  | *RH_T_DEF_PAGAMENTOS.DATA_INICIO*                                                                               |
|                 |                | entra em vigor.                           |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Data Fim        | *DATE*         | Data de cessação do encargo/desconto      | *RH_T_DEF_PAGAMENTOS.DATA_FIM*                                                                                  |
|                 |                | (quando aplicável).                       |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| **Remuneração** |                |                                           |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Remuneração     | *NUMBER*       | Montante total antes da aplicação de      | *Deve somar (salario + subsidio)*                                                                               |
| Bruta           |                | impostos e descontos obrigatórios.        |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| Total Desconto  | *NUMBER*       | Valor total dos descontos aplicados à     | *processamento_salarial_db.CalcularDesAtual*                                                                    |
|                 |                | remuneração do colaborador.               |                                                                                                                 |
|                 |                |                                           | *( \-\-\--SUBSISDIO\-\-\-\-\-\--*                                                                               |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *p_tm_id_subsidio =\> tm_id de subsisio,*                                                                       |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *p_valor_subsidio =\> valor de subsudio,*                                                                       |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *\-\-\--DESCONTO\-\-\-\-\--*                                                                                    |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *p_tm_id_desconto =\> tm_id de desconto,*                                                                       |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *p_valor_desconto =\> valor de desconto,*                                                                       |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *p_tipo_remuneracao =\> 'SAL',*                                                                                 |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *p_valor_base =\> salario,*                                                                                     |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *P_moeda =\> moeda,*                                                                                            |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *p_data_de =\> Data inicio,*                                                                                    |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *p_total_remun =\> devolde* Remuneração *Liquido,*                                                              |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *P_total_pagamentos =\> devolde total desconto*                                                                 |
|                 |                |                                           |                                                                                                                 |
|                 |                |                                           | *)*                                                                                                             |
+-----------------+----------------+-------------------------------------------+                                                                                                                 |
| Remuneração     | *NUMBER*       | Montante final recebido pelo colaborador  |                                                                                                                 |
| Líquida         |                | após a dedução de impostos e descontos.   |                                                                                                                 |
+-----------------+----------------+-------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
| **REGRAS**                                                                                                                                                                                     |
+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| - *Data inicio não pode se superior a data fim de função*                                                                                                                                      |
|                                                                                                                                                                                                |
| - *Data inicio não ser maior que sysdate*                                                                                                                                                      |
|                                                                                                                                                                                                |
| - *Validar campos obrigatorios*                                                                                                                                                                |
|                                                                                                                                                                                                |
| - *Os campo **Carreira**, **escalão** **categroria** ficam visivel caso **RH_T_PARAM_VINCULO**.FLG_CARREIRA = 1*                                                                               |
|                                                                                                                                                                                                |
| - *O campo salario fica visivel caso **RH_T_PARAM_VINCULO**.FLG_SALARIO = 1*                                                                                                                   |
|                                                                                                                                                                                                |
| - *O campo salario é preenchido automaticamente caso **RH_T_PARAM_VINCULO**.FLG_CARREIRA = 1*                                                                                                  |
+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                                                                                                                  |
+--------------------------------------------------------+------------------------------------------------------------------------------+--------------------------------------------------------+
| ***1** Registo em **RH_T_CONTRATO_VINCULO***           | ***3.** O sistema deve gravar na tabela **RH_T_DEF_REMUNERACAO** as          | 2.  *IUR*                                              |
|                                                        | informações do separador de **subsídio** e 1 registo do **salário***         |                                                        |
| - *DATA_REGISTO= '**SYSDATE'***                        |                                                                              | - *VALOR = 0*                                          |
|                                                        | ***3.1 separador Subsidio (1 ou varios registos)***                          |                                                        |
| - *USER_REGISTO_ID = id de utilizador Logado*          |                                                                              | - *DATA_INICIO = Data inicio de Função do formulario*  |
|                                                        | - *ESTADO = '**P**'*                                                         |                                                        |
| - *USER_REGISTO_NAME = nome de utilizador Logado*      |                                                                              | - *DATA_FIM = Data Fim de Funão*                       |
|                                                        | - *USER_REGISTO_ID = id de utilizador Logado*                                |                                                        |
| - *USER_ALTERACAO \_ID = **NULL***                     |                                                                              | - *TM_ID = **FUNÇÃO**:GET_MOVIMENTO_IUR*               |
|                                                        | - *USER_REGISTO_NAME = nome de utilizador Logado*                            |                                                        |
| - *USER_ALTERACAO_NAME = **NULL***                     |                                                                              | - *ESTADO = '**P**'*                                   |
|                                                        | - *USER_ALTERACAO \_ID = **NULL***                                           |                                                        |
| - *DATA_ALTERACAO = **NULL***                          |                                                                              | - *USER_REGISTO_ID = id de utilizador Logado*          |
|                                                        | - *USER_ALTERACAO_NAME = **NULL***                                           |                                                        |
| - *ESTADO = "**P**"*                                   |                                                                              | - *USER_REGISTO_NAME = nome de utilizador Logado*      |
|                                                        | - *DATA_ALTERACAO = **NULL***                                                |                                                        |
| - *FUN_ID = id de RH_T_FUNCIONARIOS*                   |                                                                              | - *USER_ALTERACAO \_ID = **NULL***                     |
|                                                        | - *FUN_ID = id de RH_T_FUNCIONARIOS*                                         |                                                        |
| - *ESTADO_CONTRATO = 'ATIVO'*                          |                                                                              | - *USER_ALTERACAO_NAME = **NULL***                     |
|                                                        | - *CONTRATO_ID = CONTRATO_ID de RH_T_CONTRATO_VINCULO*                       |                                                        |
| - *REFERENCIA = ´ NOVO_CONTRATO ´*                     |                                                                              | - *DATA_ALTERACAO = **NULL***                          |
|                                                        | - *OBS = 'NOVO_CONTRATO'*                                                    |                                                        |
| - *OBS = = ´ NOVO_CONTRATO*                            |                                                                              | - *OBS = 'NOVO_CONTRATO'*                              |
|                                                        | -                                                                            |                                                        |
| - *VERSAO = 1*                                         |                                                                              | - *FUN_ID = id de RH_T_FUNCIONARIOS*                   |
|                                                        | ***3.2 Salario (1 registo)***                                                |                                                        |
| - *CONTRATO_ID = ID DE RH_T_CONTRATO_VINCULO*          |                                                                              | - *CONTRATO_ID = CONTRATO_ID de RH_T_CONTRATO_VINCULO* |
|                                                        | - *VALOR = Valor do campo Salario do formulario*                             |                                                        |
| *2.1 **RH_T_MOBILIDADE***                              |                                                                              |   2.  *INPS*                                           |
|                                                        | - *DATA_INICIO = Data inicio de Função do formulario*                        |                                                        |
| - *DATA_REGISTO= '**SYSDATE'***                        |                                                                              | - *VALOR = 0*                                          |
|                                                        | - *DATA_FIM = Data Fim de Função*                                            |                                                        |
| - *USER_REGISTO_ID = id de utilizador Logado*          |                                                                              | - *DATA_INICIO = Data inicio de Função do formulario*  |
|                                                        | - *TM_ID = **FUNÇÃO**:GET_MOVIMENTO_SALL*                                    |                                                        |
| - *USER_REGISTO_NAME = nome de utilizador Logado*      |                                                                              | - *DATA_FIM = Data Fim de Funão*                       |
|                                                        | - *ESTADO = '**P**'*                                                         |                                                        |
| - *USER_ALTERACAO \_ID = **NULL***                     |                                                                              | - *TM_ID = **FUNÇÃO**:GET_MOVIMENTO_INPS*              |
|                                                        | - *USER_REGISTO_ID = id de utilizador Logado*                                |                                                        |
| - *USER_ALTERACAO_NAME = **NULL***                     |                                                                              | - *ESTADO = '**P**'*                                   |
|                                                        | - *USER_REGISTO_NAME = nome de utilizador Logado*                            |                                                        |
| - *TIPO_SITUACAO = "INICIO"*                           |                                                                              | - *USER_REGISTO_ID = id de utilizador Logado*          |
|                                                        | - *USER_ALTERACAO \_ID = **NULL***                                           |                                                        |
| - *OBS = "NOVO_CONTRATO"*                              |                                                                              | - *USER_REGISTO_NAME = nome de utilizador Logado*      |
|                                                        | - *USER_ALTERACAO_NAME = **NULL***                                           |                                                        |
| - *CONTRATO_ID = id de RH_T_CONTRATO_VINCULO*          |                                                                              | - *USER_ALTERACAO \_ID = **NULL***                     |
|                                                        | - *DATA_ALTERACAO = **NULL***                                                |                                                        |
| *2.2-registo em RH_T_CARREIRA*                         |                                                                              | - *USER_ALTERACAO_NAME = **NULL***                     |
|                                                        | - *OBS = 'NOVO_CONTRATO'*                                                    |                                                        |
| - *DATA_REGISTO= '**SYSDATE'***                        |                                                                              | - *DATA_ALTERACAO = **NULL***                          |
|                                                        | - *FUN_ID = id de RH_T_FUNCIONARIOS*                                         |                                                        |
| - *USER_REGISTO_ID = id de utilizador Logado*          |                                                                              | - *OBS = 'NOVO_CONTRATO'*                              |
|                                                        | - *CONTRATO_ID = CONTRATO_ID de RH_T_CONTRATO_VINCULO*                       |                                                        |
| - *USER_REGISTO_NAME = nome de utilizador Logado*      |                                                                              | - *FUN_ID = id de RH_T_FUNCIONARIOS*                   |
|                                                        | *3.3- deve ser feito nova associação da tabela **RH_T_TIPOS_RELACIONAMENTO** |                                                        |
| - *USER_ALTERACAO \_ID = **NULL***                     | e **RH_T_DEF_REMUNECACAO** na TABELA **RH_T_REMUN_TIPREL***                  | - *CONTRATO_ID = CONTRATO_ID de RH_T_CONTRATO_VINCULO* |
|                                                        |                                                                              |                                                        |
| - *USER_ALTERACAO_NAME = **NULL***                     | - *REM_ID = ide de RH_T_DEF_REMUNERACAO*                                     | *4. deve ser feito uma nova associação da tabela       |
|                                                        |                                                                              | **RH_T_TIPOS_RELACIONAMENTO** e **RH_T_DEF_PAGAMENTO** |
| - *TIPO_SITUACAO = "INICIO"*                           | - *TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO*                              | na TABELA **RH_T_REMUN_TIPREL***                       |
|                                                        |                                                                              |                                                        |
| - *OBS = "NOVO_CONTRATO"*                              | - *ESTADO = P*                                                               | - *PAG_ID = ide de RH_T_DEF_PAGAMENTO*                 |
|                                                        |                                                                              |                                                        |
| - *CONTRATO_ID = id de RH_T_CONTRATO_VINCULO*          | - *USER_REGISTO_ID = id de utilizador Logado*                                | - *TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO*        |
|                                                        |                                                                              |                                                        |
| - *CONTRATO_VINCULO_ID = ID DE RH_T_CONTRATO_VINCULO*  | - *USER_REGISTO_NAME = nome de utilizador Logado*                            | - *ESTADO = P*                                         |
|                                                        |                                                                              |                                                        |
| *2.3- registo em RH_T_REGIME*                          | - *USER_ALTERACAO \_ID = **NULL***                                           | - *USER_REGISTO_ID = id de utilizador Logado*          |
|                                                        |                                                                              |                                                        |
| - *DATA_REGISTO= '**SYSDATE'***                        | - *USER_ALTERACAO_NAME = **NULL***                                           | - *USER_REGISTO_NAME = nome de utilizador Logado*      |
|                                                        |                                                                              |                                                        |
| - *USER_REGISTO_ID = id de utilizador Logado*          | - *DATA_ALTERACAO = **NULL***                                                | - *USER_ALTERACAO \_ID = **NULL***                     |
|                                                        |                                                                              |                                                        |
| - *USER_REGISTO_NAME = nome de utilizador Logado*      | ***4**.O sistema deve gravar na tabela **RH_DEF_PAGAMENTOS** as informações  | - *USER_ALTERACAO_NAME = **NULL***                     |
|                                                        | do separador de **Encargos / Descontos*** *e 2 registo de **IUR** e          |                                                        |
| - *USER_ALTERACAO \_ID = **NULL***                     | **INPS***                                                                    | > *DATA_ALTERACAO = **NULL***                          |
|                                                        |                                                                              |                                                        |
| - *USER_ALTERACAO_NAME = **NULL***                     | ***4.1 Separador Encargos / Descontos***                                     |                                                        |
|                                                        |                                                                              |                                                        |
| - *TIPO_SITUACAO = "INICIO"*                           | - *ESTADO = '**P**'*                                                         |                                                        |
|                                                        |                                                                              |                                                        |
| - *OBS = "NOVO_CONTRATO"*                              | - *USER_REGISTO_ID = id de utilizador Logado*                                |                                                        |
|                                                        |                                                                              |                                                        |
| *2.4- registo em RH_T_SITUCAO_LABORAL*                 | - *USER_REGISTO_NAME = nome de utilizador Logado*                            |                                                        |
|                                                        |                                                                              |                                                        |
| - *[SITUACAO_LABORAL_ID]{.mark} = P ID DE              | - *USER_ALTERACAO \_ID = **NULL***                                           |                                                        |
|   RH_T_PARAM_SITUACAO .NOME = ATIVO*                   |                                                                              |                                                        |
|                                                        | - *USER_ALTERACAO_NAME = **NULL***                                           |                                                        |
| - *MOTIVO_SIT_LAB = 'NOVO_CONTRATO'*                   |                                                                              |                                                        |
|                                                        | - *DATA_ALTERACAO = **NULL***                                                |                                                        |
| - *DATA_INICIO = DATA INICIO CONTRATO*                 |                                                                              |                                                        |
|                                                        | - *FUN_ID = ID de RH_T_FUNCIONARIOS*                                         |                                                        |
| - *DATA_FIM = DATA FIM CONTRATO*                       |                                                                              |                                                        |
|                                                        | - *OBS = 'NOVO_CONTRATO'*                                                    |                                                        |
| - *FUN_ID = ID DE RH_T_FUNCIONARIO*                    |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *CONTRATO_ID = ID DE RH_T_CONTRATO_VINCULO*          |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - ESTADO = 'P'                                         |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| <!-- -->                                               |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *DATA_REGISTO = '**SYSDATE'***                       |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| <!-- -->                                               |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *USER_REGISTO_ID = = id de utilizador Logado*        |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| <!-- -->                                               |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *USER_REGISTO_NAME = nome de utilizador Logado*      |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| *2.5.Registo em **RH_T_TIPOS_RELACIONAMENTO***         |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *DATA_REGISTO= 'SYSDATE'*                            |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *USER_REGISTO_ID = id de utilizador Logado*          |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *USER_REGISTO_NAME = nome de utilizador Logado*      |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *USER_ALTERACAO \_ID = NULL*                         |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *DATA_INICIO*                                        |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *USER_ALTERACAO_NAME = NULL*                         |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *DATA_ALTERACAO = NULL*                              |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *FUN_ID = id de RH_T_FUNCIONARIOS*                   |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *CONTRATO_ID = ID de tabela Contrato*                |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *CARREIRA_ID = id de tabela RH_T_CARREIRA*           |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *MOB_ID = id de MOBILIDADE*                          |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *REGIME_ID = ID de tabela RH_T_REGIME*               |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *ESTADO = 'P'*                                       |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *EST_ACT_ADM = 1*                                    |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *TIPREL_ID = NULL*                                   |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *OBS = "NOVO_CONTRATO"*                              |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *TIPO_SITUACAO = "INICIO"*                           |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *SITUAÇAO_LABORAL_ID = **ID DE                       |                                                                              |                                                        |
|   RH_T_SITUACAO_LABORAL***                             |                                                                              |                                                        |
|                                                        |                                                                              |                                                        |
| - *REFERENCIA = 'NOVO_CONTRATO*                        |                                                                              |                                                        |
+--------------------------------------------------------+------------------------------------------------------------------------------+--------------------------------------------------------+

e)  Dados Bancarios

![Uma imagem com texto, captura de ecrã, file, Tipo de letra Os
conteúdos gerados por IA podem estar
incorretos.](media/image10.png){width="9.693055555555556in"
height="2.404861111111111in"}

+:------------------:+:------------------:+:------------------------:+:----------------------------------:+
| **Formulario**     | **Tipo**           | **Descrição**            | **Gravação**                       |
+--------------------+--------------------+--------------------------+------------------------------------+
| Entidade Bancária  | *SELECT*           | ***Tabela:               | *RH_T_DADOS_BANCARIOS.RHB_ID*      |
|                    |                    | RH_BANCO**.NM_BANCO*     |                                    |
+--------------------+--------------------+--------------------------+------------------------------------+
| Nº Conta           | *TEXT*             | Numero de conta do banco | *RH_T_DADOS_BANCARIOS.NUM_CONTA*   |
+--------------------+--------------------+--------------------------+------------------------------------+
| NIB/IBAN           | *TEXT*             | NIB fo colaborador       | *RH_T_DADOS_BANCARIOS.NIB*         |
+--------------------+--------------------+--------------------------+------------------------------------+
| Data inicio        | *DATE*             |                          | *RH_T_DADOS_BANCARIOS.DATA_INICIO* |
+--------------------+--------------------+--------------------------+------------------------------------+
| Data Fim           | *DATE*             |                          | *RH_T_DADOS_BANCARIOS. DATA_FIM*   |
+--------------------+--------------------+--------------------------+------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                           |
+---------------------------------------------------------------------------------------------------------+
| ***1.registo na tabela** RH_T_DADOS_BANCARIOS*                                                          |
|                                                                                                         |
| - *FUN_ID = id de RH_T_FUNCIONARIOS*                                                                    |
|                                                                                                         |
| - *ESTADO **= 'A'***                                                                                    |
|                                                                                                         |
| - *USER_REGISTO_ID = id de utilizador Logado*                                                           |
|                                                                                                         |
| - *USER_REGISTO_NAME = nome de utilizador Logado*                                                       |
|                                                                                                         |
| - *USER_ALTERACAO \_ID = **NULL***                                                                      |
|                                                                                                         |
| - *USER_ALTERACAO_NAME = **NULL***                                                                      |
|                                                                                                         |
| - *DATA_ALTERACAO = **NULL***                                                                           |
+---------------------------------------------------------------------------------------------------------+

f)  Anexar Documento

![Uma imagem com texto, número, software, Tipo de letra Os conteúdos
gerados por IA podem estar
incorretos.](media/image11.png){width="9.693055555555556in"
height="3.145138888888889in"}

+:-----------------:+:-----------------:+:-----------------------:+:--------------------------------:+
| **Formulario**    | **Tipo**          | **Descrição**           | **Gravação**                     |
+-------------------+-------------------+-------------------------+----------------------------------+
| Tipo Documento    |                   |                         | *RH_T_DOCUMENTO.TP_DOCUMENTO_ID* |
+-------------------+-------------------+-------------------------+----------------------------------+
| Documento         |                   |                         | *RH_T_DOCUMENTO.DOC_ID*          |
+-------------------+-------------------+-------------------------+----------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                      |
+----------------------------------------------------------------------------------------------------+
| ***1.Outros registos na tabela RH_T_DOCUMENTO***                                                   |
|                                                                                                    |
| - *FUN_ID = id de RH_T_FUNCIONARIOS*                                                               |
|                                                                                                    |
| - *ESTADO **= 'A'***                                                                               |
|                                                                                                    |
| - *USER_REGISTO_ID = id de utilizador Logado*                                                      |
|                                                                                                    |
| - *USER_REGISTO_NAME = nome de utilizador Logado*                                                  |
|                                                                                                    |
| - *USER_ALTERACAO \_ID = **NULL***                                                                 |
|                                                                                                    |
| - *USER_ALTERACAO_NAME = **NULL***                                                                 |
|                                                                                                    |
| - *DATA_ALTERACAO = **NULL***                                                                      |
|                                                                                                    |
| - *REFERENCIA_NAME = "**COLABORADOR**"*                                                            |
|                                                                                                    |
| - *REFERENCIA_ID **= ID** de tabela **RH_T_FUNCIONARIOS***                                         |
+----------------------------------------------------------------------------------------------------+

g)  Outras Gravações

+-------------------------------------+-------------------------------------+
| **Gravações (Registo na tabela de Validação e Auditoria)**                |
+-------------------------------------+-------------------------------------+
| *1 Registo na tabela de validação   | 2.  ***O registo de log é guardado  |
| **RH_T_VALIDACAO***                 |     automaticamente no IGRP***      |
|                                     |                                     |
| - *TIPO_ACCAO**= 'INSERT '          |                                     |
|   (**DOMAINS = TIPO_ACAO**)***      |                                     |
|                                     |                                     |
| - *REFERENCIA_NAME **=              |                                     |
|   'REGISTO_COLABORADOR' (**DOMAINS  |                                     |
|   = ACCAO_REFERENTE**)***           |                                     |
|                                     |                                     |
| - *REFERENCIA_ID **= ID** de tabela |                                     |
|   **RH_T_FUNCIONARIOS***            |                                     |
|                                     |                                     |
| - *FUN_ID **= ID** de tabela        |                                     |
|   **RH_T_FUNCIONARIOS***            |                                     |
|                                     |                                     |
| - *TIPREL_ID **= NULL***            |                                     |
|                                     |                                     |
| - *DATA_REGISTO **= SYSDATE***      |                                     |
|                                     |                                     |
| - *USER_REGISTO_NAME = nome de      |                                     |
|   utilizador Logado*                |                                     |
|                                     |                                     |
| - *USER_REGISTO_ID = id de          |                                     |
|   utilizador Logado*                |                                     |
|                                     |                                     |
| - *ESTADO **= 'P'***                |                                     |
+-------------------------------------+-------------------------------------+

## Validar 

Após o registo, os dados dos colaboradores são encaminhados para a etapa
de validação. A lista abaixo exibe todos os registos e atualizações
realizados, os quais necessitam de validação antes de serem ativados no
sistema

![Uma imagem com texto, software, número, Ícone de computador Os
conteúdos gerados por IA podem estar
incorretos.](media/image12.png){width="9.693055555555556in"
height="4.336805555555555in"}

+:------------------:+:--------------------:+:-----------------------------:+:--------------------------------------:+
| **Filtro**         | **Tipo**             | **Descrição**                 | **Fonte dados**                        |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Nome Colaborador   | *TEXT*               | Nome do Colaborador           | *RH_T_FUNCIONARIOS.NOME*               |
|                    |                      |                               |                                        |
|                    |                      |                               | *(RH_T_VALIDACAO. FUN_ID =             |
|                    |                      |                               | RH_T_FUNCIONARIOS.ID)*                 |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Tipo Operação      | *SELECT*             | *DOMAINS = TIPO_ACAO*         | *RH_T_VALIDACAO.TIPO_ACCAO*            |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Referente a:       | *SELECT*             | Permite Pesquisar por area na | *RH_T_VALIDACAO. REFERENCIA*           |
|                    |                      | qual se pretende efectuar a   |                                        |
|                    |                      | validação                     |                                        |
|                    |                      |                               |                                        |
|                    |                      | *DOMAINS =                    |                                        |
|                    |                      | **ACCAO_REFERENTE***          |                                        |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Data Inicio        | *DATE*               | Período de Registo da         | *RH_T_VALIDACAO.DATA_REGISTO*          |
|                    |                      | Operação                      |                                        |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Data Fim           | *DATE*               | Período de Registo da         | *RH_T_VALIDACAO.DATA_REGISTO*          |
|                    |                      | Operação                      |                                        |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| **Lista**          | **Tipo**             | **Descrição**                 | **Fonte dados**                        |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Nome Colaborador   | *TEXT*               | Nome do colaborador           | *RH_T_FUNCIONARIOS.NOME*               |
|                    |                      |                               |                                        |
|                    |                      |                               | ***(RH_T_VALIDACAO.** FUN_ID **=       |
|                    |                      |                               | RH_T_FUNCIONARIOS**.ID)*               |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Tipo Operação      | *TEXT*               | Tipo de operação (deve        | *RH_T_VALIDACAO**.**TIPO_ACCAO*        |
|                    |                      | apresentar a descricão de     |                                        |
|                    |                      | ***RH_T_DOMAINS.**DESCRICAO*) |                                        |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Referente a        | *TEXT*               | Mostra o registo é referencia | *RH_T_VALIDACAO**.** REFERENCIA_NAME*  |
|                    |                      | a colaborador (deve           |                                        |
|                    |                      | apresentar a descricão de     |                                        |
|                    |                      | ***RH_T_DOMAINS.**DESCRICAO*) |                                        |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Data Operação      | *TEXT*               | Data n qual foi feito o       | *RH_T_VALIDACAO**.DATA_REGISTO***      |
|                    |                      | registo                       |                                        |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| Utilizador         | *TEXT*               | Utilizador que efectou o      | *RH_T_VALIDACAO**.**USER_REGISTO_NAME* |
|                    |                      | registo                       |                                        |
+--------------------+----------------------+-------------------------------+----------------------------------------+
| **ACÇÕES**         |                                                                                               |
+--------------------+-----------------------------------------------------------------------------------------------+
| Validar Registo    | Abre-se o mesmo formulário de registo , permitindo ao validador alterar os dados e validar os |
| Colaborador        | registos.                                                                                     |
+--------------------+-----------------------------------------------------------------------------------------------+
| Validar Mobilidade | Abre o formulario de Edição de uma mobilidade                                                 |
+--------------------+-----------------------------------------------------------------------------------------------+
| Validar Carreira   | Abre o formulario de Edição de uma Carreira                                                   |
+--------------------+-----------------------------------------------------------------------------------------------+
| Validar Contrato   | Abre o formulario de registo de Desconto                                                      |
+--------------------+-----------------------------------------------------------------------------------------------+
| Validar Dados      | Abre formulario de registo de dados Bancarios                                                 |
| bancarios          |                                                                                               |
+--------------------+-----------------------------------------------------------------------------------------------+
| Validar            | Abre o o formulario registo de Rend / Enc                                                     |
| Remuneracao /      |                                                                                               |
| Desconto           |                                                                                               |
+--------------------+-----------------------------------------------------------------------------------------------+
| Validar            | Abre o formulario de registo de Substituição                                                  |
| substituição       |                                                                                               |
+--------------------+-----------------------------------------------------------------------------------------------+
| **REGRAS**         |                                                                                               |
+--------------------+-----------------------------------------------------------------------------------------------+
| - O Campo "**Detalhe de alterações**" fica visivel somente se o ***RH_T_VALIDACAO.**TIPO_ACCAO for diferente de    |
|   INSERT*                                                                                                          |
|                                                                                                                    |
| [Regras nos Botões]{.underline}                                                                                    |
|                                                                                                                    |
| - **Detalhe de alterações:** somente fica visivel caso ***RH_T_VALIDACAO.**REFERENCIA_NAME **= 'UPDATE'.           |
|   (***Permite ver detalhe de alteraçoes feitas na tabela RH_T_VALIDACAO_DETALHE***)***                             |
|                                                                                                                    |
| - **Validar Registo Colaborador**: somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME =              |
|   "**REGISTO_COLABORADOR**" ([VER ESPECIFICAÇÃO](#ver-registo-colaborador))*                                       |
|                                                                                                                    |
| - **Validar Mobilidade** : somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME = "**MOBILIDADE**"     |
|   ([VER ESPECIFICAÇÃO](#novo-editar-mobilidade))*                                                                  |
|                                                                                                                    |
| - **Validar Carreira:** somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME = "**CARREIRA**" ([VER    |
|   ESPECIFICAÇÃO](#novo-editar))*                                                                                   |
|                                                                                                                    |
| - **Validar Contrato**: somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME = "**CONTRATO**" ([VER    |
|   ESPECIFICAÇÃO](#novo-contrato))*                                                                                 |
|                                                                                                                    |
| - **Validar Renovação:** somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME =                        |
|   "**RENOVACAO_CONTRATO**" ([VER ESPECIFICAÇÃO](#renovação))*                                                      |
|                                                                                                                    |
| - **Validar Dados academicos:** somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME =                 |
|   "DADOS_ACADEMICOS" ([VER ESPECIFICAÇÃO](#dados-acedemicos-e-pessoais))*                                          |
|                                                                                                                    |
| <!-- -->                                                                                                           |
|                                                                                                                    |
| - **Validar estado Colaborador:** somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME =               |
|   "*ESTADO_COLABORADOR*" ([VER ESPECIFICAÇÃO](#inativar-ativar))*                                                  |
|                                                                                                                    |
| <!-- -->                                                                                                           |
|                                                                                                                    |
| - **Validar Dados bancarios**: somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME =                  |
|   "**DADOS_BANCARIOS**" ([VER ESPECIFICAÇÃO](#novo-contrato))*                                                     |
|                                                                                                                    |
| - **Validar Remuneracao / Desconto**: somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME =           |
|   "**RENDIMENTO**" ou "**DESCONTO**" ([VER ESPECIFICAÇÃO](#novo))*                                                 |
|                                                                                                                    |
| - **Validar substituição :** somente fica visivel caso o ***RH_T_VALIDACAO.** REFERENCIA_NAME =                    |
|   "**SUBSTITUICAO_MOBILIDADE**" ([VER ESPECIFICAÇÃO](#substituição))*                                              |
+--------------------------------------------------------------------------------------------------------------------+

### Detalhe de alterações

+:------------------:+:------------------:+:------------------------:+:-------------------------------------:+
| **Lista**          | **Tipo**           | **Descrição**            | **Fonte dados**                       |
+--------------------+--------------------+--------------------------+---------------------------------------+
| Campo alterado     | TEXT               | Indicar qual campo foi   | *RH_T_VALIDACAO_DETALHE.              |
|                    |                    | alterado                 | CAMPO_ALTERADO*                       |
+--------------------+--------------------+--------------------------+---------------------------------------+
| Valor anterior     | TEXT               | Qual o valor que o campo | *RH_T_VALIDACAO_DETALHE.              |
|                    |                    | Tinha Antes              | VALOR_ANTERIOR*                       |
+--------------------+--------------------+--------------------------+---------------------------------------+
| Novo Valor         | TEXT               | Qual o campo tem agora   | *RH_T_VALIDACAO_DETALHE. VALOR_NOVO*  |
+--------------------+--------------------+--------------------------+---------------------------------------+
| Alterado Por       | TEXT               | O utilizador responsavel | *RH_T_VALIDACAO_DETALHE .             |
|                    |                    | pela alteração           | USER_REGISTO_NAME*                    |
+--------------------+--------------------+--------------------------+---------------------------------------+
| Data da Alteração  | DATE               | Data de alteração        | *RH_T_VALIDACAO_DETALHE.DATA_REGISTO* |
+--------------------+--------------------+--------------------------+---------------------------------------+
| **REGRAS**                                                                                                 |
+------------------------------------------------------------------------------------------------------------+
| - *Para trazer informações de detalhe na tabela **RH_T_VALIDACAO_DETALHE,** onde VALIDACAO_ID **=** ID de  |
|   **RH_T_VALIDACAO***                                                                                      |
+------------------------------------------------------------------------------------------------------------+

### Ver Registo Colaborador

+-------------------------------------+-------------------------------------+
| **REGRAS**                                                                |
+---------------------------------------------------------------------------+
| - Ao abrir o formulário de registo do colaborador, o campo ***Validar***  |
|   deve ficar visível.                                                     |
|                                                                           |
| - O formulário deve ser automaticamente preenchido com todas as           |
|   informações já registadas no registo de colaborador ([ver               |
|   formulário](#registo-do-colaborador)).                                  |
|                                                                           |
| - *Caso o validador altere algum campo, o sistema deve atualizar de       |
|   imediato as tabelas de negócio especificadas acima e, adicionalmente,   |
|   registar o log das alterações nas tabelas.*                             |
|                                                                           |
| - *O log é registado para cada **tabela** que sofrer alterações. Ou seja  |
|   se um campo de uma tabela associado na edição de um colaborador for     |
|   alterado , logo faz um rh_registo na tabela **RH_T_VALIDACAO_DETALHE*** |
|                                                                           |
| - *O **log** é registado sempre que uma tabela sofrer alterações. Ou      |
|   seja, se algum **campo de uma tabela associada à edição de um           |
|   colaborador** for alterado, o sistema deve:*                            |
|                                                                           |
|   - *Criar um registo na tabela **RH_T_LOG**;*                            |
|                                                                           |
|   - *Guardar o detalhe do **campo alterado** na tabela                    |
|     **RH_T_VALIDACAO_DETALHE** (incluindo valor anterior e novo valor*    |
|                                                                           |
| - *Regista na tabela **RH_T_VALIDACAO_DETALHE** apenas quando o valor     |
|   anterior de cada campo for diferente do valor novo.*                    |
+---------------------------------------------------------------------------+
| **ATUALIZAÇÃO / GRAVAÇÃO**                                                |
+-------------------------------------+-------------------------------------+
| 1-Caso o utilizador                 | 1.4-Caso for alterado o valor do    |
| **[altere]{.underline}** alguma     | **salário** ou **data inicio de     |
| informação em qualquer formulário e | função** ou **data fim de função**, |
| **[não efetue a                     | deve fazer atualização na tabela    |
| validação]{.underline}**            | **RH_T_DEF_REMUNERACAO** onde o     |
|                                     | **TM_ID=**GET_MOVIMENTO_SALL e      |
| -Deve atualizar os campos           | **TIPREL_ID**=ID de                 |
| atualizados das tabelas             | RH_T_TIPOS_RELACIONAMENTO           |
| correspondentes ao formulário em    |                                     |
| causa e outros seguintes            | - *VALOR* = novo valor de salario   |
| campos**:**                         |   do formulario                     |
|                                     |                                     |
| - *USER_ALTERACAO \_ID = id de      | - *DATA_INICIO* **=** Data inicio   |
|   utilizador Logado*                |   de função                         |
|                                     |                                     |
| - *USER_ALTERACAO_NAME = nome de    | - *DATA_FIM* = Data fim de função   |
|   utilizador Logado*                |                                     |
|                                     | - *USER_ALTERACAO \_ID = id de      |
| - *DATA_ALTERACAO = **SYSDATE***    |   utilizador Logado*                |
|                                     |                                     |
| 1.2-Caso o utilizador               | - *USER_ALTERACAO_NAME = nome de    |
| **[altere]{.underline}** alguma     |   utilizador Logado*                |
| informação em qualquer formulário e |                                     |
| **[efetue a                         | - *DATA_ALTERACAO = **SYSDATE***    |
| validação]{.underline}**            |                                     |
|                                     | ***2.2- O r**egisto de log é        |
| Além das atualizações indicadas no  | guardao automaticamente no IGRP*    |
| ponto **1.1**, também deve          |                                     |
| atualizar o estado de validação nas |                                     |
| seguintes tabelas:                  |                                     |
|                                     |                                     |
| - **RH_T_VALIDACAO**.*ESTADO = "A"* |                                     |
|                                     |                                     |
| - **RH_T_FUNCIONARIOS.**            |                                     |
|   *ESTADO_VALIDACAO = "A"*          |                                     |
|                                     |                                     |
| - **RH_T_CONTRATO_VINCULO.**ESTADO  |                                     |
|   **= 'A'**                         |                                     |
|                                     |                                     |
| - **RH_T_SITUACAO_LABORAL = 'A'**   |                                     |
|                                     |                                     |
| - **RH_T_TIPOS_RELACIONAMENTO.**    |                                     |
|   *ESTADO = "A"*                    |                                     |
|                                     |                                     |
| - **RH_T_DEF_REMUNERACAO.** *ESTADO |                                     |
|   = "A"*                            |                                     |
|                                     |                                     |
| - **RH_T_DEF_PAGAMENTO.** *ESTADO = |                                     |
|   "A"*                              |                                     |
|                                     |                                     |
| - **RH_T_REMUN_TIPREL**             |                                     |
|                                     |                                     |
| 1.2.1-Ao Validar gera um ordem de   |                                     |
| Serviço na tabela                   |                                     |
| **RH_T_ORDEM_SERVICO**              |                                     |
|                                     |                                     |
| - DESCRICAO = 'Registo de           |                                     |
|   colaborador - ' \|\|              |                                     |
|   RH_T_FUNCIONARIOS.NOME            |                                     |
|                                     |                                     |
| - REFERENTE = 'REGISTO_COLABORADOR' |                                     |
|                                     |                                     |
| - FUN_ID = RH_T_FUNCIONARIOS.ID     |                                     |
|                                     |                                     |
| - CONTRATO_ID =                     |                                     |
|   RH_T_CONTRATO_VINCULO.ID          |                                     |
|                                     |                                     |
| - TIPREL_ID =                       |                                     |
|   RH_T_TIPOS_RELACIONAMENTO.ID      |                                     |
|                                     |                                     |
| - VALIDACAO_ID = RH_T_VALIDACAO.ID  |                                     |
|                                     |                                     |
| 1.3-Caso o utilizador não valide    |                                     |
|                                     |                                     |
| - **RH_T_VALIDACAO**.*ESTADO = "I"* |                                     |
|                                     |                                     |
| - **RH_T_FUNCIONARIOS.**            |                                     |
|   *ESTADO_VALIDACAO = "I"*          |                                     |
|                                     |                                     |
| - **RH_T_CONTRATO_VINCULO.**ESTADO  |                                     |
|   **= 'I'**                         |                                     |
|                                     |                                     |
| - **RH_T_SITUACAO_LABORAL = 'I'**   |                                     |
|                                     |                                     |
| - **RH_T_TIPOS_RELACIONAMENTO.**    |                                     |
|   *ESTADO = "I"*                    |                                     |
|                                     |                                     |
| - **RH_T_DEF_REMUNERACAO.** *ESTADO |                                     |
|   = "I"*                            |                                     |
|                                     |                                     |
| - **RH_T_DEF_PAGAMENTO.** *ESTADO = |                                     |
|   "I"*                              |                                     |
+-------------------------------------+-------------------------------------+

## Lista de Colaboradores 

![Uma imagem com texto, captura de ecrã, software, número Os conteúdos
gerados por IA podem estar
incorretos.](media/image13.png){width="9.693055555555556in"
height="4.979166666666667in"}

+:------------------:+:-------------------:+:------------------------:+:-------------------------------------:+
| **Filtro**         | **Tipo**            | **Descrição**            | **Fonte dados**                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Nª Colaborador     | *NUMBER*            | Numero de colaborador    | *RH_V_DOSSIE.ID_COLABORADOR*          |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Nome               | *TEXT*              | Nome de Colaborador      | *RH_V_DOSSIE.NOME*                    |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Direcção           | *SELECT*            | Buscar dados no:         | *RH_V_DOSSIE. DIRECAO_ID*             |
|                    |                     |                          |                                       |
|                    |                     | ***FUNÇÃO**:             |                                       |
|                    |                     | GET_DIRECAO_SERVICO*     |                                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Seccão             | *SELECT*            | *Buscar dados no:*       | *RH_T_TIPOS_RELACIONAMENTO.SESSAO_ID* |
|                    |                     |                          |                                       |
|                    |                     | ***FUNÇÃO**: GET_SECCAO  |                                       |
|                    |                     | (P\_ SECCAO_ID)*         |                                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Tipo Vinculo       | *SELECT*            | Buscar dados no:         | *RH_T_TIPOS_RELACIONAMENTO.           |
| Laboral            |                     |                          | VINCULO_ID*                           |
|                    |                     | ***FUNÇÃO**:             |                                       |
|                    |                     | GET_TIPO_VINCULO*        |                                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Data Inicio        | *DATE*              |                          | *RH_T_TIPOS_RELACIONAMENTO.           |
|                    |                     |                          | DATA_INICIO_CONTRATO*                 |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Data Fim           | *DATE*              |                          | *RH_T_TIPOS_RELACIONAMENTO.           |
|                    |                     |                          | DATA_FIM_CONTRATO*                    |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Estado             | *SELECT*            |                          | *RH_T_FUNCIONARIOS.                   |
|                    |                     |                          | ESTADO_COLABORADOR*                   |
+--------------------+---------------------+--------------------------+---------------------------------------+
| **Lista**          | **Tipo**            | **Descrição**            | **Fonte dados**                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Estado do Registo  | *TEXT*              | Indica se o registo de   | *RH_V_DOSSIE.ESTADO_VALIDACAO*        |
|                    |                     | colaborador esté         |                                       |
|                    |                     | validado ou não          |                                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Estado do          | *TEXT*              | Estado do colaborador    | *RH_V_DOSSIE. ESTADO_COLABORADOR*     |
| Colaborador        |                     | (ativo ou inativo)       |                                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Nº Colaborador     | *TEXT*              | Numero do colaborador    | *RH_V_DOSSIE.ID_COLABORADOR*          |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Nome               | *TEXT*              | Nome do colaborador      | *RH_V_DOSSIE.NOME*                    |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Cargo              | *TEXT*              | Cargo que desempenha o   | *RH_V_DOSSIE.CARGO_DESC*              |
|                    |                     | colaborador              |                                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Data inicio        | *TEXT*              | Data inicio de função do | *RH_V_DOSSIE. DATA_INICIO_CONTRATO*   |
|                    |                     | colaborador              |                                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Direcção           | *TEXT*              | Direcção de trabalho do  | *RH_V_DOSSIE.DIRECAO_DESC*            |
|                    |                     | colaborador              |                                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Secção             | *TEXT*              | Secção de trabalho do    | *RH_V_DOSSIE.SECCAO_DESC*             |
|                    |                     | colaborador              |                                       |
+--------------------+---------------------+--------------------------+---------------------------------------+
| Carreira /         |                     | Carreira/ categoria do   | *RH_V_DOSSIE.CARREIRA_DESC /          |
| Categoria          |                     | Colaborador              | RH_V_DOSSIE.CATEGORIA_DESC*           |
+--------------------+---------------------+--------------------------+---------------------------------------+
| **Acções**         |                                                                                        |
+--------------------+----------------------------------------------------------------------------------------+
| Ver Dossiê do      | Abrir um formulario para ver Dossie do Colaborador                                     |
| colaborador        |                                                                                        |
+--------------------+----------------------------------------------------------------------------------------+
| Inativar / Ativar  | Permite ativar ou inativar um colaborador, deve ter uma transação associada            |
| Colaborador        |                                                                                        |
+--------------------+----------------------------------------------------------------------------------------+
| **REGRA**                                                                                                   |
+-------------------------------------------------------------------------------------------------------------+
| - *Se o **estado de validação** do colaborador estiver em **"Pendente"**, os botões **"Ver Dossiê do        |
|   Colaborador"** e **"Inativar/Ativar Colaborador"** devem ficar visíveis.*                                 |
|                                                                                                             |
| > *Ou seja, para colaboradores cujo campo **RH_T_COLABORADORES.ESTADO_VALIDACAO = \'P\'**.)*                |
|                                                                                                             |
| - *A **lista** deve ser preenchida apenas após a aplicação do **filtro**.*                                  |
|                                                                                                             |
| - *Por **defeito**, a lista deve apresentar apenas os colaboradores com **estado = \'A\'**.*                |
+-------------------------------------------------------------------------------------------------------------+

## Inativar / Ativar 

![](media/image14.png){width="9.693055555555556in"
height="2.6194444444444445in"}

+:----------------:+:----------------:+:--------------------:+:--------------------:+:--------------------------------------------:+
| **Formulario**   | **Tipo**         | **Descrição**                               | **Gravação**                                 |
+------------------+------------------+---------------------------------------------+----------------------------------------------+
| Validar          | *RADIOLIST*      | Fica visivel somente no modo validar        | *RH_T_FUNCIONARIOS.ESTADO*                   |
|                  |                  |                                             |                                              |
|                  |                  | **DOMAINS** = STATUS                        | *RH_T_SITUACAO_LABORAL.ESTADO*               |
+------------------+------------------+---------------------------------------------+----------------------------------------------+
| Situação Laboral |                  | A funcionalidade permite **alterar o estado | *RH_T_SITUACAO_LABORAL. SITUACAO_LABORAL_ID* |
|                  |                  | de um colaborador** entre **Ativo** e       |                                              |
|                  |                  | **Inativo**, de acordo com o seu estado     |                                              |
|                  |                  | atual.                                      |                                              |
|                  |                  |                                             |                                              |
|                  |                  | **RH_T_PARAM_SIT_LABORAL.ID**, Deve trazer  |                                              |
|                  |                  | somente valor ´ATIVO´ e CESSADO             |                                              |
+------------------+------------------+---------------------------------------------+----------------------------------------------+
| Motivo           |                  | Ao alterar o estado, o utilizador deve      | *RH_T_SITUACAO_LABORAL. MOTIVO_SIT_LAB_ID*   |
|                  |                  | **indicar obrigatoriamente o motivo da      |                                              |
|                  |                  | alteração**                                 | *RH_T_TIPOS_RELACIONAMENTO.                  |
|                  |                  |                                             | MOTIVO_SIT_LAB_ID*                           |
|                  |                  | **[RH_T_PARAM_SITUACAO_DET.NOME]{.mark},    |                                              |
|                  |                  | [RH_T_PARAM_SITUACAO_DET.]{.mark}ID**       |                                              |
+------------------+------------------+---------------------------------------------+----------------------------------------------+
| [Data            | [DATE]{.mark}    |                                             | *[RH_T_SITUACAO_LABORAL.DATA_INICIO]{.mark}* |
| inicio]{.mark}   |                  |                                             |                                              |
+------------------+------------------+---------------------------------------------+----------------------------------------------+
| [DATA            | [DATE]{.mark}    |                                             | *[RH_T_SITUACAO_LABORAL.DATA_FIM]{.mark}*    |
| FIM]{.mark}      |                  |                                             |                                              |
+------------------+------------------+---------------------------------------------+----------------------------------------------+
| Observação       |                  |                                             | *RH_T_SITUACAO_LABORAL. OBS*                 |
|                  |                  |                                             |                                              |
|                  |                  |                                             | *RH_T_TIPOS_RELACIONAMENTO. OBS*             |
+------------------+------------------+---------------------------------------------+----------------------------------------------+
| **REGRAS**                                                                                                                       |
+----------------------------------------------------------------------------------------------------------------------------------+
| - *O sistema deve permitir visualizar dados de um colaborador inativo, mas não deve ser possivel realizar nenhuma ação em cima   |
|   del , ou seja deve ser retirado accão a qualquer ou outro botão que não seja ativar / Inativar colaborador*                    |
|                                                                                                                                  |
| - *Este botão deve ter uma transação, somente utilizador atribuido acesso, deve conseguir executalo*                             |
|                                                                                                                                  |
| - *O sistema deve registar Log de alteração*                                                                                     |
+----------------------------------------------------------------------------------------------------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                                                    |
+------------------------------------------------------------+---------------------------------------------------------------------+
| *1.Faz update nos registos anteriores , ou seja inativa os | *2-insert em **RH_T_SITUACAO_LABORAL***                             |
| registos ativos*                                           |                                                                     |
|                                                            | - *DATA_REGISTO= '**SYSDATE'***                                     |
| *1.1Inativa a mobilidade em estado ativo                   |                                                                     |
| **RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e data fim is | - *USER_REGISTO_ID = id de utilizador Logado*                       |
| not null)***                                               |                                                                     |
|                                                            | - *USER_REGISTO_NAME = nome de utilizador Logado*                   |
| - *DATA_FIM = data Inicio*                                 |                                                                     |
|                                                            | - *USER_ALTERACAO \_ID = **NULL***                                  |
| - *USER_ALTERACAO \_ID = utilizador logado*                |                                                                     |
|                                                            | - *USER_ALTERACAO_NAME = **NULL***                                  |
| - *USER_ALTERACAO_NAME = **nome de utilizador***           |                                                                     |
|                                                            | - *DATA_ALTERACAO = **NULL***                                       |
| - *DATA_ALTERACAO = sysdate*                               |                                                                     |
|                                                            | - *ESTADO = "**P**"*                                                |
| - *EST_ACT_ADM = 0*                                        |                                                                     |
|                                                            | - *CONTR_VINCULO_ID = id de RH_T_CONTRATO_VINCULO*                  |
| *1.2 Fazer uma nova gravação na tabela de                  |                                                                     |
| **RH_T_TIPOS_RELACIONAMENTO**, pegas todas informações do  | - *CONTRATO_ID = CONTRATO_ID DE RH_T_CONTRATO_VINCULO*              |
| registo anterior , e regista com novas alteraçoes nos      |                                                                     |
| campos do formulario e outros seguinte campos*             | 1.  *Atualiza na tabela **RH_T_FUNCIONARIOS** dados do formulario e |
|                                                            |     outros seguintes dados*                                         |
| - *DATA_REGISTO= 'SYSDATE'*                                |                                                                     |
|                                                            | - *USER_ALTERACAO_ID = id de utilizador logado*                     |
| - *USER_REGISTO_ID = id de utilizador Logado*              |                                                                     |
|                                                            | - *USER_ALTERACAO_NAME = nome de utilizador logado*                 |
| - *USER_REGISTO_NAME = nome de utilizador Logado*          |                                                                     |
|                                                            | - *DATA_ALTERACAO = SYSDATE*                                        |
| - *USER_ALTERACAO \_ID = NULL*                             |                                                                     |
|                                                            | - *Estado = **ATIVO** OU **INATIVO, dependendo da situação cessado  |
| - *DATA_INICIO*                                            |   = I***                                                            |
|                                                            |                                                                     |
| - *USER_ALTERACAO_NAME = NULL*                             | *1.1 **RH_T_VALIDACAO***                                            |
|                                                            |                                                                     |
| - *DATA_ALTERACAO = NULL*                                  | - *TIPO_ACCAO**= 'UPDATE' (**DOMAINS = TIPO_ACAO**)***              |
|                                                            |                                                                     |
| - *TIPREL_ID = ID DO REGISTO FECHADO (id de                | - *REFERENCIA_NAME **= 'ESTADO_COLABORADOR' (**DOMAINS =            |
|   RH_TIPOS_RELAIONAMENTO)*                                 |   ACCAO_REFERENTE**)***                                             |
|                                                            |                                                                     |
| - *SITUACAO_LAB_ID = ID DE SITUACAO LABORAL*               | - *REFERENCIA_ID **= ID** de tabela **RH_T_FUNCIONARIOS***          |
|                                                            |                                                                     |
| - *EST_ACT_ADM = 1*                                        | - *FUN_ID **= ID** de tabela **RH_T_FUNCIONARIOS** TIPREL_ID **=    |
|                                                            |   NULL***                                                           |
| - *REFERENTE = 'SITUACAO_LABORAL'*                         |                                                                     |
|                                                            | - *DATA_REGISTO **= SYSDATE***                                      |
| - *TIPO_SITUACAO = ´* MUDANCA_SITUACAO_LAB *´*             |                                                                     |
|                                                            | - *USER_REGISTO_NAME = nome de utilizador Logado*                   |
| - *ESTADO = 'P'*                                           |                                                                     |
|                                                            | - *USER_REGISTO_ID = id de utilizador Logado*                       |
|                                                            |                                                                     |
|                                                            | - *ESTADO **= 'P'***                                                |
+------------------------------------------------------------+---------------------------------------------------------------------+
|                                                            | ***2.1.** Registo Detalhe de LOG na tabela **RH_T_VALIDACAO_DETALHE |
|                                                            | (faz registo de cada campo alterado)***                             |
|                                                            |                                                                     |
|                                                            | - *VALIDACAO_ID **= id de tabela RH_T_VALIDACAO***                  |
|                                                            |                                                                     |
|                                                            | - *CAMPO_ALTERADO **= ESTADO ou OBS***                              |
|                                                            |                                                                     |
|                                                            | - *VALOR_ANTERIOR = valor queo campo estado tinha antes*            |
|                                                            |                                                                     |
|                                                            | - *VALOR_NOVO = Valor do campo do estado atual*                     |
|                                                            |                                                                     |
|                                                            | - *TABELA_NAME = " nome de tabela a ser registado"*                 |
|                                                            |                                                                     |
|                                                            | - *TABELA \_ID = "id de tabela a ser registado*                     |
+------------------------------------------------------------+---------------------------------------------------------------------+

### Validar 

+----------------------------------------------------------------------+
| - A validação invoca a mesma página de Registo                       |
|                                                                      |
| - O campo validar deve ficar visivel                                 |
|                                                                      |
| - Ao **validar**, devem ser atualizadas todas as tabelas associadas, |
|   definindo o campo **estado = \'A\'**.                              |
|                                                                      |
| - Ao **desvalidar**, devem ser atualizadas todas as tabelas          |
|   associadas, definindo o campo **estado = \'I\'**.                  |
|                                                                      |
| - Caso o utilizador **atualize algum campo no formulário**, a        |
|   alteração deve ser **refletida na tabela correspondente**.         |
|                                                                      |
| *3-Caso o Caso o tipo de situação for 'Cessar', logo deve fazer      |
| update nas seguintes tabelas :*                                      |
|                                                                      |
| - *RH_T_CONTRATO_VINCULO.DATA_FIM*                                   |
|                                                                      |
| - *RH_T_TIPOS_RELACIONAMENTO.DATA_FIM*                               |
|                                                                      |
| - *RH_T_DEF_REMUNERACAO.DATA_FIM*                                    |
|                                                                      |
| - *RH_T_CARREIRA.DATA_FIM*                                           |
|                                                                      |
| - *RH_T_MOBILIDADE.DATA_FIM*                                         |
|                                                                      |
| - *RH_T_DEF_PAGAMENTO.DATA_FIM*                                      |
+----------------------------------------------------------------------+

## Dossiê do Colaborador 

### Perfil do colaborador

#### Dados Pessoais

![Uma imagem com texto, captura de ecrã, número, Tipo de letra Os
conteúdos gerados por IA podem estar
incorretos.](media/image15.png){width="7.1409722222222225in"
height="3.8020833333333335in"}

+:--------------------:+:------------------------:+:----------------:+:---------------------------------:+:---------------------------------------:+
| **Formulario**       | **Tipo**                 | **Descrição**                                        | **UPDATE**                              |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| **identificação do Colaborador (**pega dados na vista ***RH_V_DOSSIE** onde **ULTIMO_VINCULO = 1*****)**                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| [~~Validar~~]{.mark} | *[~~RADIOLIST~~]{.mark}* | [~~Fica visivel somente no modo validar~~]{.mark}    | *[~~RH_T_FUNCIONARIOS.ESTADO~~]{.mark}* |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \*Tipo Documento     | *SELECT*                 | Tipo de documento de identificação do Colaborador    | *RH_T_FUNCIONARIOS.TIPO_DOCUMENTO*      |
| Identificação        |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: RH_V_DOSSIE. *TIPO_DOCUMENTO*       | *RH_T_DOCUMENTO_PESSOAL.TIPO_DOCUMENTO* |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \* N.º Documento de  | *TEXT*                   | Numero documento de identificação do colaborador     | *RH_T_FUNCIONARIOS.NUM_DOCUMENTO*       |
| Identificação        |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE. NUM_DOCUMENTO*        | *RH_T_DOCUMENTO_PESSOAL.NUM_DOCUMENTO*  |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \*Nome               | *TEXT*                   | Número do documento apresentado para fins de         | *RH_T_FUNCIONARIOS.NOME*                |
|                      |                          | identificação                                        |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE. NOME*                 |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| Foto                 | *IMAGEM*                 | PENDENTE: decidir como será registado (ver com ATY)  | *RH_FUNCIONARIOS.FOTOGRAFIA*            |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \*Data Nascimento    | *DATE*                   | Nome completo do colaborador, conforme o documento   | *RH_T_FUNCIONARIOS.DATA_NASCIMENTO*     |
|                      |                          | de identificação                                     |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE.DATA_NASCIMENTO*       |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \*Género             | *SELECT*                 | Data do Nascimento do colaborador (***DOMAINS** =    | *RH_T_FUNCIONARIOS.SEXO*                |
|                      |                          | GENERO*)                                             |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE.SEXO*                  |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \*Nome Mãe           | *TEXT*                   | Nome completo da mãe do colaborador                  | *RH_T_FUNCIONARIOS.NM_MAE*              |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE.NM_MAE*                |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \*Nome Pai           | *TEXT*                   | Nome completo do pai do colaborador                  | *RH_T_FUNCIONARIOS.NM_PAI*              |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE.NM_PAI*                |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \*Estado Civil       | *SELECT*                 | Esrado civil atual do colaborador                    | *RH_T_FUNCIONARIOS.ESTADO_CIVIL*        |
|                      |                          | (***DOMAINS**=ESTADO_CIVIL*)                         |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE.ESTADO_CIVIL*          |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \*Nacionalidade      | *SELECT*                 | País de nacionalidade do colaborador                 | *RH_T_FUNCIONARIOS.NACIONALIDADE*       |
|                      |                          |                                                      |                                         |
|                      |                          | **Função**: GET_GEOGRAFIA                            |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE.NACIONALIDADE*         |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| \*Naturalidade       | *LOCKUP*                 | Local de nascimento do colaborador (*GEOGRAFIA*)     | *RH_T_FUNCIONARIOS.LOC_NASC_ID*         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE.LOC_NASC_ID e          |                                         |
|                      |                          | RH_V_DOSSIE. LOC_NASC_NOME*                          |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| DATA_EMISSAO         |                          |                                                      |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| **Documentos         |                          |                                                      |                                         |
| Administrativos**    |                          |                                                      |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| NIF                  | *NUMBER*                 | Numero de identificação Fiscal                       | *RH_T_FUNCIONARIOS.NIF*                 |
|                      |                          |                                                      |                                         |
|                      |                          | \*PENDENTE: API Pesquisa NIF                         |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE.NIF*                   |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| N.º Segurado         | *NUMBER*                 | Número de Identificação Fiscal do colaborador        | *RH_FUNCIONARIOS.NU_SEG_INPS*           |
|                      |                          |                                                      |                                         |
|                      |                          | \*PENDENTE:API pesquisa segurado                     |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V_DOSSIE. NU_SEG_INPS*          |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| **Contacto (**busca dados na vista **RH_V_CONTATO,** passando como paramentro **FUN_ID** = ID de **RH_T_FUNCIONARIO )**                          |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| Tipo Contacto        | *SELECT*                 | Tipo de Contacto do colaborador (Telemóvel,          | *RH_T_CONTACTO.TIPO_CONTACTO*           |
|                      |                          | Telefone, Email)                                     |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | ***DOMAINS** = TP_CONTACTO*                          |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: RH_V_CONTATO.*TIPO_CONTACTO*        |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| Contacto             | *TEXT*                   | Número de telefone, endereço de e-mail ou outro      | *RH_T_CONTACTO.CONTACTO*                |
|                      |                          | contacto indicado pelo colaborador.                  |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: RH_V_CONTATO.*CONTACTO*             |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| **Endereço (**busca dados de Geografia no schema **Global na** tabela **GLB_T_GEOGRAFIA)**                                                       |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| Pais                 | *SELECT*                 | País onde o colaborador reside atualmente.           | *RH_T_ENDERECO.PAIS_ID*                 |
|                      |                          |                                                      |                                         |
|                      |                          | **Função**: GET_GEOGRAFIA (P_NIVEL = 1)              |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V\_ ENDERECO \_FUNC. PAIS_ID*   |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| Ilha                 | *SELECT*                 | Ilha de residência do colaborador                    | *RH_T_ENDERECO.ILHA_ID*                 |
|                      |                          |                                                      |                                         |
|                      |                          | **Função**: GET_GEOGRAFIA (P_NIVEL = 2)              |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V\_ ENDERECO \_FUNC. ILHA_ID*   |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| Concelho             | *SELECT*                 | Município onde o colaborador reside.                 | *RH_T_ENDERECO.CONCELHO_ID*             |
|                      |                          |                                                      |                                         |
|                      |                          | **Função:** GET_GEOGRAFIA (P_NIVEL = 3)              |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V\_ ENDERECO \_FUNC. ILHA_ID*   |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| Freguesia            | *SELECT*                 | Divisão administrativa onde se encontra a residência | *RH_T_ENDERECO.FREGUESIA_ID*            |
|                      |                          | do colaborador.                                      |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Função:** GET_GEOGRAFIA (P_NIVEL = 4)              |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V\_ ENDERECO . FREGUESIA_ID*    |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| Zona                 | *SELECT*                 | Bairro, distrito ou localidade dentro da freguesia.  | *RH_T_ENDERECO.ZONA_ID*                 |
|                      |                          |                                                      |                                         |
|                      |                          | **Função:** GET_GEOGRAFIA (P_NIVEL = 5)              |                                         |
|                      |                          |                                                      |                                         |
|                      |                          | **Fonte dados**: *RH_V\_ ENDERECO.ZONA_ID*           |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| Morada               | *TEXT*                   | Endereço completo e detalhado do colaborador,        | *RH_T_ENDERECO.MORADA*                  |
|                      |                          | incluindo rua, número de casa, referência, etc       |                                         |
+----------------------+--------------------------+------------------------------------------------------+-----------------------------------------+
| **ACÇÕES**                                                                                                                                       |
+----------------------+---------------------------------------------------------------------------------------------------------------------------+
| Eliminar             | *Ao eliminar um registo existente na tabela, o sistema deve atualizar de imediato o respetivo estado. (estado = 'I')*     |
+----------------------+---------------------------------------------------------------------------------------------------------------------------+
| Editar               | *Ao atualizar, devem ser guardadas as informações do formulário, bem como os outros campos indicados abaixo*              |
+----------------------+---------------------------------------------------------------------------------------------------------------------------+
| **REGRAS**                                                                                                                                       |
+--------------------------------------------------------------------------------------------------------------------------------------------------+
| - *A validação dos campos obrigatórios deve ser efetuada tanto ao nível do formulário como ao nível da tabela*                                   |
|                                                                                                                                                  |
| - *O sistema não permite duplicação de colaborador. Caso seja detetado um número de documento já registado, deve ser emitida uma mensagem de     |
|   ERRO ("Já existe um colaborador registado com este número de documento"). A combinação entre o tipo de documento e o número de documento deve  |
|   ser única*                                                                                                                                     |
|                                                                                                                                                  |
| - *O sistema deve validar se o **NIF** indicado corresponde efetivamente ao colaborador através da **API** disponibilizado. Mensagem de ERRO:    |
|   ''O NIF introduzido não corresponde ao colaborador selecionado**".** Validar com nome, data nascimento, nome de mae e pai*                     |
|                                                                                                                                                  |
| - *O sistema deve validar se o **N.º Segurado** indicado corresponde efetivamente ao colaborador através da **API** disponibilizado. Mensagem de |
|   ERRO: ''O Nº segurado introduzido não corresponde ao colaborador selecionado**".** Validar com nome, data nascimento, nome de mae e pai.*      |
|                                                                                                                                                  |
| - *Validar se o contacto já está associado a outro funcionário e emitir um ALERTA: "O contacto informado já está associado a outro colaborador"* |
|                                                                                                                                                  |
| - *Ao Alterar documento de um funcionario deve garantir que seja o mesmo nome de funcionario antes atraves do **nome**, **data nascimento**,     |
|   **nome de mae e pai***                                                                                                                         |
+--------------------------------------------------------------------------------------------------------------------------------------------------+
| **ATUALIZAÇÃO DE OUTROS CAMPOS**                                                                                                                 |
+--------------------------------------------------------------------+-----------------------------------+-----------------------------------------+
| 1.  ***RH_T_FUNCIONARIOS***                                        | 2.  ***RH_T_CONTACTO** (caso for  | 3.  ***RH_T_ENDERECO** (caso for        |
|                                                                    |     alterado contato**)***        |     alterado endere**)***               |
| - *ESTADO = 'A'*                                                   |                                   |                                         |
|                                                                    | - *ESTADO = 'A'*                  | - *ESTADO = 'A'*                        |
| - *ESTADO_VALIDACAO = "**P**"*                                     |                                   |                                         |
|                                                                    | - *USER_REGISTO \_ID = id de      | - *USER_REGISTO_ID = id de utilizador   |
| - *USER_ALTERACAO_ID = id de utilizador Logado*                    |   utilizador Logado*              |   Logado*                               |
|                                                                    |                                   |                                         |
| - *USER_ALTERACAO_NAME = nome de utilizador Logado*                | - *USER_ALTERACAO \_ID =          | - *USER_ALTERACAO_ID = **NULL***        |
|                                                                    |   **NULL***                       |                                         |
| - *DATA_ALTERACAO = SYSDATE'*                                      |                                   | - *DATA\_ REGISTO = **SYSDATE***        |
|                                                                    | - *DATA_REGISTO = **SYSDATE***    |                                         |
|                                                                    |                                   | - *DATA_ALTERACAO = **NULL***           |
|                                                                    | - *DATA_ALTERACAO = **NULL***     |                                         |
|                                                                    |                                   | - *FUN_ID= id de tabela                 |
|                                                                    | - *FUN_ID = **ID** de tabela      |   **RH_T_FUNCIONARIOS***                |
|                                                                    |   **RH_T_FUNCIONARIOS***          |                                         |
+--------------------------------------------------------------------+-----------------------------------+-----------------------------------------+
| 4.  ***RH_T_DOCUMENTO_PESSOAL***                                   | 5.  *[~~Deve registar na tabela   | 6.  *Deve registar log de alteração em  |
|                                                                    |     de validação                  |     cada tabela que sofrer alteração*   |
|     - *ESTADO **= 'A'***                                           |     (**RH_T_VALIDACAO**) caso for |                                         |
|                                                                    |     alterado um dos seguintes     | *6.1Registo de log **RH_T_LOG***        |
|     - *DATA_REGISTO= '**SYSDATE'***                                |     campos:~~]{.mark}*            |                                         |
|                                                                    |                                   | - *TIPO_ACCAO **= UPDATE (**DOMAINS =   |
|     - *USER_REGISTO_ID = id de utilizador Logado*                  | - *[~~Tipo de                     |   TIPO_ACAO**)***                       |
|                                                                    |   documento~~]{.mark}*            |                                         |
|     - *USER_ALTERACAO \_ID = NULL*                                 |                                   | - *TABELA_NAME **= 'nome tabela'***     |
|                                                                    | - *[~~Numero documento~~]{.mark}* |                                         |
|     - *DATA_ALTERACAO = **NULL***                                  |                                   | - *TABELA \_ID **= ID** de tabela*      |
|                                                                    | - *[~~NIF~~]{.mark}*              |                                         |
|     - *FUN_ID= ID de tabela **RH_T_FUNCIONARIOS***                 |                                   | - *FUN_ID = ID de colaborador           |
|                                                                    | - *[~~N.º Segurado~~]{.mark}*     |   (**RH_T_FUNCIONARIOS.ID**)*           |
|                                                                    |                                   |                                         |
|                                                                    | ***[~~RH_T_VALIDACAO~~]{.mark}*** | - *TIPREL_ID **= NULL***                |
|                                                                    |                                   |                                         |
|                                                                    | - *[~~TIPO_ACCAO**= 'UPDATE'      | - *USER_REGISTO_ID **= ID utilizador    |
|                                                                    |   (**DOMAINS =                    |   que fez a ação***                     |
|                                                                    |   TIPO_ACAO**)**~~]{.mark}*       |                                         |
|                                                                    |                                   | - *USER_REGISTO_NAME **= Nome do        |
|                                                                    | - *[~~REFERENCIA_NAME **=         |   utilizador que fez a ação***          |
|                                                                    |   'DADOS_PESSOAIS' (**DOMAINS =   |                                         |
|                                                                    |   ACCAO_REFERENTE**)**~~]{.mark}* | - *DATA_REGISTO = SYSDATE*              |
|                                                                    |                                   |                                         |
|                                                                    | - *[~~REFERENCIA_ID **= ID** de   | - *VALIDACAO_ID = id de tabela de       |
|                                                                    |   tabela **RH_T_FUNCIONARIOS**~~  |   RH_T_VALIDACAO*                       |
|                                                                    |   ]{.mark}*                       |                                         |
|                                                                    |                                   | *6.2Registo Detalhe de LOG na tabela    |
|                                                                    | - *[~~FUN_ID **= ID** de tabela   | **RH_T_VALIDACAO_DETALHE (Faz 2 registo |
|                                                                    |   **RH_T_FUNCIONARIOS** TIPREL_ID | 1 para estado e outro para OBS)***      |
|                                                                    |   **= NULL**~~]{.mark}*           |                                         |
|                                                                    |                                   | - *VALIDACAO_ID **= id de tabela**      |
|                                                                    | - *[~~DATA_REGISTO **=            |   RH_T_VALIDACAO*                       |
|                                                                    |   SYSDATE**~~ ]{.mark}*           |                                         |
|                                                                    |                                   | - *CAMPO_ALTERADO **= ESTADO ou OBS***  |
|                                                                    | - *USER_REGISTO_NAME = nome de    |                                         |
|                                                                    |   utilizador Logado*              | - *VALOR_ANTERIOR = valor queo campo    |
|                                                                    |                                   |   estado tinha antes*                   |
|                                                                    | - *USER_REGISTO_ID = id de        |                                         |
|                                                                    |   utilizador Logado*              | - *VALOR_NOVO = Valor do campo do       |
|                                                                    |                                   |   estado atual*                         |
|                                                                    | - *ESTADO **= 'P'***              |                                         |
|                                                                    |                                   | - *DADOS_REGISTO = NULL*                |
+--------------------------------------------------------------------+-----------------------------------+-----------------------------------------+

#### Dados Acedemicos e Pessoais

![Uma imagem com texto, captura de ecrã, número, software Os conteúdos
gerados por IA podem estar
incorretos.](media/image16.png){width="9.693055555555556in"
height="4.301388888888889in"}

+----------------------+--------------------------+------------------+---------------------+-----------------------------------------------+
| **FORMULÁRIO**       | **TIPO**                 | **Descrição**                          | **UPDATE / INSERT**                           |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Habilitação          |                          |                                        |                                               |
| Literaria            |                          |                                        |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| [~~Validar~~]{.mark} | *[~~RADIOLIST~~]{.mark}* | [~~Fica visivel somente no modo        | *[~~RH_T_HABILITACOES_LITERARIAS.ESTADO       |
|                      |                          | validar~~]{.mark}                      | ou~~]{.mark}*                                 |
|                      |                          |                                        |                                               |
|                      |                          |                                        | *[~~RH_T_FORMACAO_FEITOS.ESTADO ou            |
|                      |                          |                                        | RH_T_EXPERIENCIA_PROF.ESTADO~~]{.mark}*       |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| PAIS                 | *SELECT*                 | País onde o colaborador obteve a       | *RH_T_HABILITACOES_LITERARIAS.PAIS_ID*        |
|                      |                          | habilitação literária.\                |                                               |
|                      |                          | (Ex.: Cabo Verde, Portugal, Brasil)    |                                               |
|                      |                          |                                        |                                               |
|                      |                          | ***FUNÇÃO** :* GET_GEOGRAFIA (P_NIVEL  |                                               |
|                      |                          | =1 )                                   |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| ESTABELECIMENTO      | *TEXT*                   | Nome da instituição de ensino onde foi | *RH_T_HABILITACOES_LITERARIAS.ESTABLECIMENTO* |
|                      |                          | realizada a formação.\                 |                                               |
|                      |                          | *(Ex.: Universidade de Lisboa,         |                                               |
|                      |                          | Instituto Politécnico de Cabo Verde)*  |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| AREA                 | *SELECT*                 | Área de estudo ou domínio científico a | *RH_T_HABILITACOES_LITERARIAS.AREA*           |
|                      |                          | que pertence a formação.\              |                                               |
|                      |                          | (Ex.: Ciências Sociais, Engenharia,    |                                               |
|                      |                          | Saúde)                                 |                                               |
|                      |                          |                                        |                                               |
|                      |                          | ***DOMAINS** = AREA_FORMACAO*          |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| CURSO                | *TEXT*                   | Designação específica do curso         | *RH_T_HABILITACOES_LITERARIAS.NOME_CURSO*     |
|                      |                          | concluído ou frequentado pelo          |                                               |
|                      |                          | colaborador.                           |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| GRAU ACÁDEMICO       | *SELECT*                 | Grau ou nível académico correspondente | *RH_T_HABILITACOES_LITERARIAS.NIVEL*          |
| /NIVEL               |                          | à habilitação obtida.\                 |                                               |
|                      |                          | (Ex.: Licenciatura, Mestrado,          |                                               |
|                      |                          | Doutoramento, Ensino Secundário,       |                                               |
|                      |                          | Técnico Profissional)                  |                                               |
|                      |                          |                                        |                                               |
|                      |                          | ***DOMAINS** = NIVEL_HABILITACOES*     |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| DATA INICIO          | *DATE*                   | Data em que o colaborador iniciou o    | *RH_T_HABILITACOES_LITERARIAS.DATA_INICIO*    |
|                      |                          | curso ou formação.                     |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| DATA TERMINO         | *DATE*                   | Data em que o colaborador concluiu (ou | *RH_T_HABILITACOES_LITERARIAS*.DATA_FIM       |
|                      |                          | abandonou) a formação.                 |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Formação             |                          |                                        |                                               |
| Profissional         |                          |                                        |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| País                 | *SELECT*                 | Campo destinado a identificar o país   | *RH_T_FORMACAO_FEITOS.PAIS_ID*                |
|                      |                          | onde foi realizada a formação          |                                               |
|                      |                          | profissional.                          |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Establecimento       |                          | Indica a instituição, centro ou        | *RH_T_FORMACAO_FEITOS. ESTABELECIMENTO*       |
|                      |                          | entidade responsável pela realização   |                                               |
|                      |                          | da formação.                           |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Tipo de Formação     | *SELECT*                 | Define a natureza ou modalidade da     | *RH_T_FORMACAO_FEITOS.RHTPFOR*                |
|                      |                          | formação frequentada.\                 |                                               |
|                      |                          | *Exemplo: Curso Técnico, Formação      |                                               |
|                      |                          | Modular, Workshop*                     |                                               |
|                      |                          |                                        |                                               |
|                      |                          | ***DOMAINS =** TP_FORMACAO*            |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Designação           |                          | Refere-se ao nome ou título oficial da | *RH_T_FORMACAO_FEITOS.CURSO*                  |
|                      |                          | formação.                              |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Nível                | *SELECT*                 | Indica o grau ou enquadramento da      | *RH_T_FORMACAO_FEITOS.NIVEL*                  |
|                      |                          | formação no sistema                    |                                               |
|                      |                          | educativo/profissional.\               |                                               |
|                      |                          | *Exemplo: Nível II (Qualificação de    |                                               |
|                      |                          | Base), Nível IV (Curso Técnico), Nível |                                               |
|                      |                          | VI (Licenciatura).*                    |                                               |
|                      |                          |                                        |                                               |
|                      |                          | ***DOMAINS** = NIVEL_HABILITACOES*     |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Experiência          |                          |                                        |                                               |
| Profissional         |                          |                                        |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Pais                 | *SELECT*                 | Identifica o país onde a experiência   | *RH_T_EXPERIENCIA_PROF.PAIS_ID*               |
|                      |                          | profissional foi exercida              |                                               |
|                      |                          |                                        |                                               |
|                      |                          | ***FUNÇÃO** :* GET_GEOGRAFIA (P_NIVEL  |                                               |
|                      |                          | =1 )                                   |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Empresa              | *TEXT*                   | Nome da entidade/organização onde o    | *RH_T_EXPERIENCIA_PROF.EMPRESA*               |
|                      |                          | colaborador exerceu funções.           |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Cargo                | *TEXT*                   | Função ou posição ocupada pelo         | *RH_T_EXPERIENCIA_PROF.CARGO*                 |
|                      |                          | colaborador durante o período da       |                                               |
|                      |                          | experiência                            |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Data Entrada         | *DATE*                   | Data em que o colaborador iniciou      | *RH_T_EXPERIENCIA_PROF.DATA_INICIO*           |
|                      |                          | funções na empresa referida.           |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Data Saída           | *DATE*                   | Data em que o colaborador cessou       | *RH_T_EXPERIENCIA_PROF.DATA_FIM*              |
|                      |                          | funções na empresa.                    |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| Observações          | *TEXTAREA*               | Campo livre para registo de            | *RH_T_EXPERIENCIA_PROF.OBSERVACAO*            |
|                      |                          | informações adicionais relevantes.     |                                               |
+----------------------+--------------------------+----------------------------------------+-----------------------------------------------+
| **ACÇÕES**                                                                                                                               |
+----------------------+-------------------------------------------------------------------------------------------------------------------+
| Eliminar             | *Ao eliminar um registo existente na tabela, o sistema deve atualizar de imediato o respetivo estado. (estado =   |
|                      | 'I')*                                                                                                             |
+----------------------+-------------------------------------------------------------------------------------------------------------------+
| Editar               | *Ao atualizar, devem ser guardadas as informações do formulário, bem como os outros campos indicados abaixo*      |
+----------------------+-------------------------------------------------------------------------------------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                                                            |
+--------------------------------------------------------------------+---------------------------------------------------------------------+
| *1.Altera dados nas tabelas **RH_T_HABILITACOES_LITERARIAS,        | *[~~3-.Registo log nas tabelas de IGRP~~]{.mark}*                   |
| RH_T_FORMACAO_FEITOS, RH_T_EXPERIENCIA_PROF** dos campos de        |                                                                     |
| formulario e outros seguintes campos*                              | *[~~**3.1 -**Registo Detalhe de LOG na tabela                       |
|                                                                    | **RH_T_VALIDACAO_DETALHE**~~]{.mark}*                               |
| - *ESTADO **= 'A'***                                               |                                                                     |
|                                                                    | - *[~~VALIDACAO_ID **= id de tabela RH_T_VALIDACAO**~~]{.mark}*     |
| - *USER_ALTERACAO \_ID = **id de utilizador logado***              |                                                                     |
|                                                                    | - *[~~CAMPO_ALTERADO **=** nome de campo alterado~~]{.mark}*        |
| - *USER\_ ALTERACAO \_NAME = **Nome de utilizador Logado***        |                                                                     |
|                                                                    | - *[~~VALOR_ANTERIOR = valor antes~~]{.mark}*                       |
| - *DATA_ALTERACAO = **SYSDATE***                                   |                                                                     |
|                                                                    | - *[~~VALOR_NOVO = valor depois~~]{.mark}*                          |
| 2.  *[~~Registr na Tabela de validacao                             |                                                                     |
|     **(RH_T_VALIDACAO)**~~]{.mark}*                                | - *[~~TABELA_NAME = " nome de tabela a ser registado"~~]{.mark}*    |
|                                                                    |                                                                     |
| - *[~~TIPO_ACCAO= 'INSERT ' (DOMAINS = TIPO_ACAO)~~]{.mark}*       | - *[~~TABELA \_ID = "id de tabela a ser registado~~]{.mark}*        |
|                                                                    |                                                                     |
| - *[~~REFERENCIA_NAME = '**DADOS_ACADEMICOS**"' (DOMAINS =         |                                                                     |
|   **ACCAO_REFERENTE**)~~]{.mark}*                                  |                                                                     |
|                                                                    |                                                                     |
| - *[~~REFERENCIA_ID = ID de tabela RH_T_FAMILIARES~~]{.mark}*      |                                                                     |
|                                                                    |                                                                     |
| - *[~~FUN_ID = ID de tabela RH_T_FUNCIONARIOS~~]{.mark}*           |                                                                     |
|                                                                    |                                                                     |
| - *[~~TIPREL_ID = NULL~~]{.mark}*                                  |                                                                     |
|                                                                    |                                                                     |
| - *[~~DATA_REGISTO = SYSDATE~~]{.mark}*                            |                                                                     |
|                                                                    |                                                                     |
| - *[~~USER_REGISTO_NAME = nome de utilizador Logado~~]{.mark}*     |                                                                     |
|                                                                    |                                                                     |
| - *[~~USER_REGISTO_ID = id de utilizador Logado~~]{.mark}*         |                                                                     |
|                                                                    |                                                                     |
| - *[~~ESTADO = 'P'~~]{.mark}*                                      |                                                                     |
+--------------------------------------------------------------------+---------------------------------------------------------------------+

#### Agregado / dependente

![Uma imagem com texto, captura de ecrã, file, Tipo de letra Os
conteúdos gerados por IA podem estar
incorretos.](media/image17.png){width="9.693055555555556in"
height="2.363888888888889in"}

+:---------------------+--------------------------+-------------------+---------------------+---------------------------------------+
| **FORMULÁRIO**       | **TIPO**                 | **Descrição**                           | **UPDATE / INSERT**                   |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| [~~Validar~~]{.mark} | *[~~RADIOLIST~~]{.mark}* | [~~Fica visivel somente no modo         | *[~~RH_T_FAMILIARES.ESTADO~~]{.mark}* |
|                      |                          | validar~~]{.mark}                       |                                       |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| \*Tipo Documento     | *TEXT*                   | Tipo de documento de identificação do   | *RH_T_FAMILIARES.TP_DOCUMENTO*        |
| Identificação        |                          | Colaborador                             |                                       |
|                      |                          |                                         |                                       |
|                      |                          | *RH_TP_DOCUMENTO.REFERENCIA =*          |                                       |
|                      |                          | **DOCUMENTO_PESSOAL'**                  |                                       |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| \* N.º Documento de  | *TEXT*                   |                                         | *RH_T_FAMILIARES.* *NUM_DOCUMENTO*    |
| Identificação        |                          |                                         |                                       |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| \*Nome               | *TEXT*                   |                                         | *RH_T_FAMILIARES.NOME*                |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| \*Data Nascimento    | *DATE*                   |                                         | *RH_T_FAMILIARES.* *DATA_NASCIMENTO*  |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| \*Género             | *SELECT*                 | ***DOMAINS** = GENERO*                  | *RH_T_FAMILIARES.SEXO*                |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| \*Grau Parentesco    | *SELECT*                 | ***DOMAINS** = GRAUS_DE_PARENTESCO*     | *RH_T_FAMILIARES.GDP_ID*              |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| \*Dependente         | *SELECT*                 | ***DOMAINS** = DEPENDENCIA*             | *RH_T_FAMILIARES.* *DEPENDENCIA*      |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| \*Agregado           | *SELECT*                 | ***DOMAINS**= MEMBRO_AGR*               | *RH_T_FAMILIARES.MEMBRO_AGR*          |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| Mãe                  | *TEXT*                   |                                         | *RH_T_FAMILIARES .NM_PAI*             |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| Pai                  | *TEXT*                   |                                         | *RH_T_FAMILIARES .NM_MAE*             |
+----------------------+--------------------------+-----------------------------------------+---------------------------------------+
| **ACÇÕES**                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------+
| Eliminar             | *Ao eliminar um registo existente na tabela, o sistema deve atualizar de imediato o respetivo estado.      |
|                      | (**RH_T_FAMILIARES**.estado = 'I')*                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------+
| Editar               | *Ao atualizar, devem ser guardadas as informações do formulário, bem como os outros campos indicados       |
|                      | abaixo*                                                                                                    |
+----------------------+------------------------------------------------------------------------------------------------------------+
| **REGRAS**                                                                                                                        |
+-----------------------------------------------------------------------------------------------------------------------------------+
| - *A validação dos campos obrigatórios deve ser efetuada tanto ao nível do formulário como ao nível da tabela*                    |
|                                                                                                                                   |
| - *O sistema deve garantir que não existam duplicados de familiares para o mesmo colaborador. A verificação deve ser feita com    |
|   base nos campos **FUN_ID**, **NUM_DOCUMENTO** e **NOME**. Mensagem de ERRO : "Já existe um familiar com este nome e número de   |
|   documento associado a este colaborador"*                                                                                        |
+-----------------------------------------------------------------------------------------------------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                                                     |
+---------------------------------------------------------------------+-------------------------------------------------------------+
| *1.Altera dados na tabela **RH_T_FAMILIARES** dos campos de         | ***3.**Guarda na tabela de log **na tabela de IGRP***       |
| formulario e outros seguintes campos*                               |                                                             |
|                                                                     | *[**3.~~2~~** ~~Registo Detalhe de LOG na tabela            |
| - *ESTADO **= 'A'***                                                | **RH_T_VALIDACAO_DETALHE**~~]{.mark}*                       |
|                                                                     |                                                             |
| - *USER_ALTERACAO \_ID = **id de utilizador logado***               | - *[~~VALIDACAO_ID **= id de tabela                         |
|                                                                     |   RH_T_VALIDACAO**~~]{.mark}*                               |
| - *USER\_ ALTERACAO \_NAME = **Nome de utilizador Logado***         |                                                             |
|                                                                     | - *[~~CAMPO_ALTERADO **= nome de campo                      |
| - *DATA_ALTERACAO = **SYSDATE***                                    |   Alterado**~~]{.mark}*                                     |
|                                                                     |                                                             |
| 3.  *[~~Registr na Tabela de validacao                              | - *[~~VALOR_ANTERIOR = valor antes~~]{.mark}*               |
|     **(RH_T_VALIDACAO)**~~]{.mark}*                                 |                                                             |
|                                                                     | - *[~~VALOR_NOVO = valor depois~~]{.mark}*                  |
| - *[~~TIPO_ACCAO= 'INSERT ' (DOMAINS = TIPO_ACAO)~~]{.mark}*        |                                                             |
|                                                                     | - *[~~TABELA_NAME = " nome de tabela a ser                  |
| - *[~~REFERENCIA_NAME = 'FAMILIA"' (DOMAINS =                       |   registado"~~]{.mark}*                                     |
|   ACCAO_REFERENTE)~~]{.mark}*                                       |                                                             |
|                                                                     | - *[~~TABELA \_ID = "id de tabela a ser                     |
| - *[~~REFERENCIA_ID = ID de tabela RH_T_FAMILIARES~~]{.mark}*       |   registado~~]{.mark}*                                      |
|                                                                     |                                                             |
| - *[~~FUN_ID = ID de tabela RH_T_FUNCIONARIOS~~]{.mark}*            |                                                             |
|                                                                     |                                                             |
| - *[~~TIPREL_ID = NULL~~]{.mark}*                                   |                                                             |
|                                                                     |                                                             |
| - *[~~DATA_REGISTO = SYSDATE~~]{.mark}*                             |                                                             |
|                                                                     |                                                             |
| - *[~~USER_REGISTO_NAME = nome de utilizador Logado~~]{.mark}*      |                                                             |
|                                                                     |                                                             |
| - *[~~USER_REGISTO_ID = id de utilizador Logado~~]{.mark}*          |                                                             |
|                                                                     |                                                             |
| - *[~~ESTADO = 'P'~~]{.mark}*                                       |                                                             |
+---------------------------------------------------------------------+-------------------------------------------------------------+

#### Dados Bancários

- ![](media/image18.png){width="2.847222222222222e-2in"
  height="7.430555555555556e-2in"}![Uma imagem com texto, software,
  Ícone de computador, Página web Os conteúdos gerados por IA podem
  estar incorretos.](media/image19.png){width="9.368055555555555in"
  height="3.4722222222222223in"}

+:-----------------:+:-----------------:+:-----------------:+:-----------------:+:----------------------------------:+
| **Formulario**    | **Tipo**          | **Descrição**                         | **Gravação**                       |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| Validar           | *RADIOLIST*       | Fica visivel somente no modo validar  | *RH_T_DADOS_BANCARIOS.ESTADO*      |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| Entidade Bancária | *SELECT*          | *RH_BANCO.NM_BANCO*                   | *RH_T_DADOS_BANCARIOS.ENT_ID*      |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| Nº Conta          | *TEXT*            | Numero de conta do banco              | *RH_T_DADOS_BANCARIOS.NUM_CONTA*   |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| NIB/IBAN          | *TEXT*            | NIB do colaborador                    | *RH_T_DADOS_BANCARIOS.NIB*         |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| Data inicio       | *DATE*            |                                       | *RH_T_DADOS_BANCARIOS.DATA_INICIO* |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| Data Fim          | *DATE*            |                                       | *RH_T_DADOS_BANCARIOS. DATA_FIM*   |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| **REGRAS**        |                   |                                       |                                    |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| - *Qualquer altetação / registo, efectuado deve passar para validação (**RH_T_VALIDACAO**)*                        |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| ACÕES             |                   |                                       |                                    |
+-------------------+-------------------+---------------------------------------+------------------------------------+
| Editar            | *Caso já esteja validado , so deve deixar altera data_fim*                                     |
+-------------------+------------------------------------------------------------------------------------------------+
| Eliminar          | *Botão eliminar fica visivel caso o o registo não for validado (**RH_T_DADOS_BANCARIOS.ESTADO  |
|                   | = 'E', DATA_FIM = SYSDATE**)*                                                                  |
+-------------------+------------------------------------------------------------------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                                      |
+-----------------------------------------------------------+--------------------------------------------------------+
| *1.Altera dados na tabela **RH_T_DADOS_BANCARIOS** dos    | ***3.**Guarda LOG no IGRP*                             |
| campos de formulario e outros seguintes campos*           |                                                        |
|                                                           | ***3.2** Registo Detalhe de alteracaona tabela         |
| - *ESTADO **= 'P'***                                      | **RH_T_VALIDACAO_DETALHE***                            |
|                                                           |                                                        |
| - *USER_ALTERACAO \_ID = **id de utilizador logado***     | - *VALIDACAO_ID **= id de tabela RH_T_VALIDACAO***     |
|                                                           |                                                        |
| - *USER\_ ALTERACAO \_NAME = **Nome de utilizador         | - *CAMPO_ALTERADO **= nome de campo Alterado***        |
|   Logado***                                               |                                                        |
|                                                           | - *VALOR_ANTERIOR = valor antes*                       |
| - *DATA_ALTERACAO = **SYSDATE***                          |                                                        |
|                                                           | - *VALOR_NOVO = valor depois*                          |
| 4.  Caso for alterado ou registado o nib ou banco deve    |                                                        |
|     ser registado na tabela de **RH_T_VALIDACAO**         | - *TABELA_NAME = " nome de tabela a ser registado"*    |
|                                                           |                                                        |
| - *TIPO_ACCAO**= 'UPDATE ou NSERT' (**DOMAINS =           | - *TABELA \_ID = "id de tabela a ser registado*        |
|   TIPO_ACAO**)***                                         |                                                        |
|                                                           |                                                        |
| - *REFERENCIA_NAME **= 'DADOS_BANCARIOS' (**DOMAINS =     |                                                        |
|   ACCAO_REFERENTE**)***                                   |                                                        |
|                                                           |                                                        |
| - *REFERENCIA_ID **= ID** de tabela RH_T_DADOS_BANCARIOS, |                                                        |
|   Separados por virgula*                                  |                                                        |
|                                                           |                                                        |
| - *FUN_ID **= ID** de tabela **RH_T_FUNCIONARIOS***       |                                                        |
|                                                           |                                                        |
| - *TIPREL_ID **= NULL***                                  |                                                        |
|                                                           |                                                        |
| - *DATA_REGISTO **= SYSDATE***                            |                                                        |
|                                                           |                                                        |
| - *USER_REGISTO_NAME = nome de utilizador Logado*         |                                                        |
|                                                           |                                                        |
| - *USER_REGISTO_ID = id de utilizador Logado*             |                                                        |
|                                                           |                                                        |
| - *ESTADO **= 'P'***                                      |                                                        |
+-----------------------------------------------------------+--------------------------------------------------------+

- 

### Relação Laboral

#### Gestão Contratual

![Uma imagem com texto, software, número, Página web Os conteúdos
gerados por IA podem estar
incorretos.](media/image20.png){width="9.693055555555556in"
height="4.283333333333333in"}

+:-------------:+:------------------:+:--------------------:+:-----------------------------------:+
| **filtro**    | **Tipo**           | **Descrição**        | **Fonte Dados**                     |
+---------------+--------------------+----------------------+-------------------------------------+
| Vinculo       | SELECT             |                      | *RH_T_CONTRATO_VINCULO. VINCULO_ID* |
+---------------+--------------------+----------------------+-------------------------------------+
| **Lista**     | **Tipo**           | **Descrição**        | **Fonte Dados**                     |
+---------------+--------------------+----------------------+-------------------------------------+
| Tipo Contrato | *TEXT*             |                      | *RH_T_CONTRATO_VINCULO.             |
|               |                    |                      | TP_CONTRADO_ID*                     |
+---------------+--------------------+----------------------+-------------------------------------+
| Tipo Vinculo  | *TEXT*             |                      | *RH_T_CONTRATO_VINCULO. VINCULO_ID* |
+---------------+--------------------+----------------------+-------------------------------------+
| Data inicio   | *TEXT*             |                      | *RH_T_CONTRATO_VINCULO.DATA_INICIO* |
+---------------+--------------------+----------------------+-------------------------------------+
| Duração       | *TEXT*             |                      | *RH_T_CONTRATO_VINCULO. DURACAO*    |
+---------------+--------------------+----------------------+-------------------------------------+
| Data Fim      | *TEXT*             |                      | *RH_T_CONTRATO_VINCULO. DATA_FIM*   |
+---------------+--------------------+----------------------+-------------------------------------+
| **AÇÕES**     |                    |                      |                                     |
+---------------+--------------------+----------------------+-------------------------------------+
| Ver           | *Esse botão só deve ficar visivel na ultima versão do contrato*                 |
| Informação    |                                                                                 |
| Atual         |                                                                                 |
+---------------+---------------------------------------------------------------------------------+
| Ver           | *Esse botão só deve ficar visivel na primeira versão do contrato*               |
| informação    |                                                                                 |
| Inicial       |                                                                                 |
+---------------+---------------------------------------------------------------------------------+
| Renovar       | *Esse botão só deve ficar visivel na ultima versão do contrato*                 |
| Contrato      |                                                                                 |
+---------------+---------------------------------------------------------------------------------+

##### Contrato de Trabalho (Ver Informação Atual, Ver informação Inicial )

![Uma imagem com texto, captura de ecrã, software, número Os conteúdos
gerados por IA podem estar
incorretos.](media/image21.png){width="9.3125in"
height="4.770833333333333in"}

![Uma imagem com texto, captura de ecrã, software, número Os conteúdos
gerados por IA podem estar
incorretos.](media/image22.png){width="9.25in"
height="2.3541666666666665in"}

+:------------------:+:------------------:+:-----------------------:+:--------------------------------------------:+
| **Formulario**     | **Tipo**           | **Descrição**           | **FONTE DADOS**                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| **Dados Contratuais**                                                                                            |
|                                                                                                                  |
| **Ver informação Atual: (**busca dados em ***RH_V_MOBILIDADE*** TIPO_CONTRATO = '**RENOVACAO'** ), pego o ultimo |
| contrato ativo do colaborador**),**                                                                              |
|                                                                                                                  |
| **Ver informação Inicial (**busca dados em ***RH_V_MOBILIDADE*** TIPO_CONTRATO = '**NOVO_CONTRATO'** ), pega o   |
| contrato inicial do colabora**), não deve permitir editar**                                                      |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Validar            | *RADIOLIST*        | Fica visivel somente no | *RH_T_CONTRATO_VINCULO.ESTADO*               |
|                    |                    | modo validar            |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Situação Laboral   | *TEXT*             | **DOMAINS** =           | RH_V_DOSSIE.SITUACAO_LABORAL                 |
|                    |                    | SITUACAO_LABORAL        |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Cargo/Posição      | *TEXT*             | Designação do cargo ou  | RH_V_DOSSIE.CARGO_DESC                       |
|                    |                    | função que o            |                                              |
|                    |                    | colaborador irá         |                                              |
|                    |                    | desempenhar na          |                                              |
|                    |                    | instituição.            |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Direção            | *TEXT*             | Unidade orgânica ou     | RH_V_DOSSIE.DIRECAO_DESC                     |
|                    |                    | direção em que o        |                                              |
|                    |                    | colaborador está        |                                              |
|                    |                    | afeto.\                 |                                              |
|                    |                    | *(Ex.: Direção          |                                              |
|                    |                    | Financeira, Direção de  |                                              |
|                    |                    | Recursos Humanos)*      |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| secção             | *TEXT*             | Subunidade ou divisão   | RH_V_DOSSIE.SECAO_DESC                       |
|                    |                    | dentro da direção onde  |                                              |
|                    |                    | o colaborador           |                                              |
|                    |                    | desempenhará funções.\  |                                              |
|                    |                    | *(Ex.: Secção de        |                                              |
|                    |                    | Contabilidade)*         |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Centro custo       | *TEXT*             | centro de custo         | RH_V_DOSSIE..CENTRO_CUSTO_DESC               |
|                    |                    | responsável pelas       |                                              |
|                    |                    | despesas com este       |                                              |
|                    |                    | colaborador             |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Carreira           | *TEXT*             | Estrutura profissional  | RH_V_DOSSIE.CARREIRA_DES                     |
|                    |                    | a que o colaborador     |                                              |
|                    |                    | pertence                |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Categoria          | *TEXT*             | Nível ou grupo          | RH_V_DOSSIE.CATEGORIA_ESC                    |
|                    |                    | profissional do         |                                              |
|                    |                    | colaborador dentro da   |                                              |
|                    |                    | carreira (              |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Escalão /          | *TEXT*             | Escalão salarial ou     | RH_V_DOSSIE*.ESCALAO_DESC*                   |
| referência         |                    | referência              |                                              |
|                    |                    | remuneratória           |                                              |
|                    |                    | correspondente à        |                                              |
|                    |                    | posição do colaborador  |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Tipo de vinculo    | *TEXT*             | Natureza do vínculo     | RH_V_DOSSIE*. VINCULO_DESC*                  |
| Laboral            |                    | contratual entre        |                                              |
|                    |                    | colaborador e entidade  |                                              |
|                    |                    | empregadora             |                                              |
|                    |                    |                         |                                              |
|                    |                    | (Ex.: Efetivo, Contrato |                                              |
|                    |                    | a Termo, Requisição,    |                                              |
|                    |                    | Estágio)                |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Regime Trabalho    | *TWXT*             | Condições de prestação  | RH_V_DOSSIE REGIME_TRABALHO_DESC             |
|                    |                    | de trabalho definidas   |                                              |
|                    |                    | no contrato.\           |                                              |
|                    |                    | *(Ex.: Tempo Integral,  |                                              |
|                    |                    | Tempo Parcial,          |                                              |
|                    |                    | Teletrabalho, Horário   |                                              |
|                    |                    | Flexível)*              |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Salário            | *NUMBER*           | Valor contratual da     | RH_V_DOSSIE.*VALOR*                          |
|                    |                    | remuneração base do     |                                              |
|                    |                    | colaborador.            |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Moeda              | *TEXT*             | Moeda em que o salário  | RH_V_DOSSIE*.MOEDA*                          |
|                    |                    | e demais remunerações   |                                              |
|                    |                    | são processados.\       |                                              |
|                    |                    | *(Ex.: CVE, EUR, USD)*  |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Data Inicio de     | *DATE*             | Data em que o           | RH_V_DOSSIE. *DATA_INICIO_CONTRATO*          |
| Função             |                    | colaborador inicia      |                                              |
|                    |                    | efetivamente o          |                                              |
|                    |                    | exercício das funções.  |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Data Fim de Função | *DATE*             | Data prevista para o    | RH_V_DOSSIE*. DATA_FIM_CONTRATO*             |
|                    |                    | termo do vínculo        |                                              |
|                    |                    | laboral (quando         |                                              |
|                    |                    | aplicável).             |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Duração (MESES)    | *NUMBER*           | Período total de        | RH_V_DOSSIE*. DURACAO_CONTRATO*              |
|                    |                    | vigência do contrato,   |                                              |
|                    |                    | expresso em meses ou    |                                              |
|                    |                    | anos (aplicável a       |                                              |
|                    |                    | contratos temporários). |                                              |
|                    |                    | (Diferença entre data   |                                              |
|                    |                    | inicio funcão e data    |                                              |
|                    |                    | fim)                    |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Local de Trabalho  |                    | Lugar fisico onde o     | RH_V_DOSSIE*.LOCAL_TRABALHO_DESC*            |
|                    |                    | Colaborador exerce o    |                                              |
|                    |                    | seu trabalho            |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Pais               | *TEXT*             | País onde o contrato    | RH_V_DOSSIE*. PAIS_TRAB_DESC*                |
|                    |                    | terá execução           |                                              |
|                    |                    |                         |                                              |
|                    |                    | Buscar dados na TABELA  |                                              |
|                    |                    | GEOGRAFIA no nivel de   |                                              |
|                    |                    | pais                    |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Ilha               | *TEXT*             | Localidade específica   | RH_V_DOSSIE*. ILHA_TRAB_DESC*                |
|                    |                    | (quando aplicável em    |                                              |
|                    |                    | contexto nacional, ex.: |                                              |
|                    |                    | Cabo Verde). Apresenta  |                                              |
|                    |                    | as ilhas                |                                              |
|                    |                    | correspondentes quando  |                                              |
|                    |                    | o país escolhido for    |                                              |
|                    |                    | Cabo Verde              |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| **Subsidios :** Indicação geral sobre a existência de subsídios atribuídos ao colaborador. (busca dados em       |
| RH_V_REND_ENC ONDE TIPO = '**REM'** e TIPO_MOBILIDADE = '**NOVO_CONTRATO'** ), pego o ultimo contrato do         |
| colaborador                                                                                                      |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Tipo de Subsídio   | *SELECT*           | Natureza do subsídio    | *RH_V_REND_ENC. MOVIMENTO*                   |
|                    |                    | atribuído.\             |                                              |
|                    |                    | *(Ex.: Subsídio de      |                                              |
|                    |                    | Alimentação, Subsídio   |                                              |
|                    |                    | de Transporte, Subsídio |                                              |
|                    |                    | de Férias, 13.º mês)*   |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Percentagem        | *TEXT*             | Percentagem do salário  | *RH_V_REND_ENC.PERCENTAGEM*                  |
|                    |                    | base usada para         |                                              |
|                    |                    | calcular o valor do     |                                              |
|                    |                    | subsídio (quando        |                                              |
|                    |                    | aplicável).             |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Valor              | *NUMBER*           | Montante atribuído ao   | *RH_V_REND_ENC.VALOR*                        |
|                    |                    | colaborador a título de |                                              |
|                    |                    | subsídio                |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| **Encargos / Descontos :** Indicação geral sobre os encargos (patronais) ou descontos (do colaborador)           |
| aplicáveis. . (busca dados em RH_V_REND_ENC ONDE TIPO = '**PAG'** e TIPO_MOBILIDADE = '**NOVO_CONTRATO'** ),     |
| pego o ultimo contrato do colaborador                                                                            |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Tipo de Encargos / | *SELECT*           | Identificação do tipo   | *RH_V_REND_ENC.MOVIMENTO_DESC*               |
| Descontao          |                    | de encargo ou           |                                              |
|                    |                    | desconto.\              |                                              |
|                    |                    | *(Ex.: INPS, Imposto    |                                              |
|                    |                    | IRPS, Fundo Social,     |                                              |
|                    |                    | Sindicato)*             |                                              |
|                    |                    |                         |                                              |
|                    |                    | ***FUNÇÃO**:            |                                              |
|                    |                    | GET_MOVIMENTO_DESCONTO  |                                              |
|                    |                    | (P_TIPO)*               |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Valor              | *NUMBER*           | Montante a deduzir ou a | *RH_V_REND_ENC..VALOR*                       |
|                    |                    | assumir pela entidade   |                                              |
|                    |                    | empregadora, podendo    |                                              |
|                    |                    | ser fixo ou percentual. |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Data Inicio        | *DATE*             | Data a partir da qual o | *RH_V_REND_ENC..DATA_INICIO*                 |
|                    |                    | encargo/desconto entra  |                                              |
|                    |                    | em vigor.               |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Data Fim           | *DATE*             | Data de cessação do     | *RH_V_REND_ENC..DATA_FIM*                    |
|                    |                    | encargo/desconto        |                                              |
|                    |                    | (quando aplicável).     |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| **Remuneração**    |                    |                         |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Remuneração Bruta  | *NUMBER*           | Montante total antes da | *Deve somar (salario + subsidio)*            |
|                    |                    | aplicação de impostos e |                                              |
|                    |                    | descontos obrigatórios. |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+
| Total Desconto     | *NUMBER*           | Valor total dos         | *processamento_salarial_db.CalcularDesAtual* |
|                    |                    | descontos aplicados à   |                                              |
|                    |                    | remuneração do          | *( \-\-\--SUBSISDIO\-\-\-\-\-\--*            |
|                    |                    | colaborador.            |                                              |
|                    |                    |                         | *p_tm_id_subsidio =\> tm_id de subsisio,*    |
|                    |                    |                         |                                              |
|                    |                    |                         | *p_valor_subsidio =\> valor de subsudio,*    |
|                    |                    |                         |                                              |
|                    |                    |                         | *\-\-\--DESCONTO\-\-\-\-\--*                 |
|                    |                    |                         |                                              |
|                    |                    |                         | *p_tm_id_desconto =\> tm_id de desconto,*    |
|                    |                    |                         |                                              |
|                    |                    |                         | *p_valor_desconto =\> valor de desconto,*    |
|                    |                    |                         |                                              |
|                    |                    |                         | *p_tipo_remuneracao =\> 'SAL',*              |
|                    |                    |                         |                                              |
|                    |                    |                         | *p_valor_base =\> salario,*                  |
|                    |                    |                         |                                              |
|                    |                    |                         | *P_moeda =\> moeda,*                         |
|                    |                    |                         |                                              |
|                    |                    |                         | *p_data_de =\> Data inicio,*                 |
|                    |                    |                         |                                              |
|                    |                    |                         | *p_total_remun =\> devolde* Remuneração      |
|                    |                    |                         | *Liquido,*                                   |
|                    |                    |                         |                                              |
|                    |                    |                         | *P_total_pagamentos =\> devolde total        |
|                    |                    |                         | desconto*                                    |
|                    |                    |                         |                                              |
|                    |                    |                         | *)*                                          |
+--------------------+--------------------+-------------------------+                                              |
| Remuneração        | *NUMBER*           | Montante final recebido |                                              |
| Líquida            |                    | pelo colaborador após a |                                              |
|                    |                    | dedução de impostos e   |                                              |
|                    |                    | descontos.              |                                              |
+--------------------+--------------------+-------------------------+----------------------------------------------+

#####  Novo Contrato

Mesmo formulario descrito no registo de colaborador (Dados Contratuais)

+---------------------------------------+----------------------------------+----------------------------------+
| **REGRAS**                                                                                                  |
+-------------------------------------------------------------------------------------------------------------+
| - *Data inicio de não pode se superior a data fim de função*                                                |
|                                                                                                             |
| - *Data inicio não ser maior que **sysdate***                                                               |
|                                                                                                             |
| - *Validar os campos obrigatorios*                                                                          |
|                                                                                                             |
| - ***O Botão Novo contrato , so deve ficar visivel caso não existe um contrato ativo***                     |
+-------------------------------------------------------------------------------------------------------------+
| **GRAVAÇÃO DE OUTROS CAMPOS**                                                                               |
+-------------------------------------------------------------------------------------------------------------+
| - ***Se for o primeiro contrato do colaborador:** no campo TIPO_SITUACAO, registar **INICIO** (em vez de    |
|   CONTINUIDADE).*                                                                                           |
|                                                                                                             |
| - ***Se não for o primeiro contrato:** verificar se existem registos **ativos** (DATA_FIM IS NULL) nas      |
|   tabelas RH_T_CARREIRA, RH_T_MOBILIDADE e RH_T_REGIME; **encerrá-los** definindo DATA_FIM (ex.: com a data |
|   de término do contrato anterior) e **depois** executar as demais ações descritas abaixo.*                 |
+---------------------------------------+----------------------------------+----------------------------------+
| ***1** Registo em                     | ***3.** O sistema deve gravar na | 2.  *IUR*                        |
| **RH_T_CONTRATO_VINCULO***            | tabela **RH_T_DEF_REMUNERACAO**  |                                  |
|                                       | as informações do separador de   | - *VALOR = 0*                    |
| - *DATA_REGISTO= '**SYSDATE'***       | **subsídio** e 1 registo do      |                                  |
|                                       | **salário***                     | - *DATA_INICIO = Data inicio de  |
| - *USER_REGISTO_ID = id de utilizador |                                  |   Função do formulario*          |
|   Logado*                             | ***3.1 separador Subsidio (1 ou  |                                  |
|                                       | varios registos)***              | - *DATA_FIM = Data Fim de Funão* |
| - *USER_REGISTO_NAME = nome de        |                                  |                                  |
|   utilizador Logado*                  | - *OBS = 'NOVO_CONTRATO'*        | - *TM_ID =                       |
|                                       |                                  |   **FUNÇÃO**:GET_MOVIMENTO_IUR*  |
| - *USER_ALTERACAO \_ID = **NULL***    | - *ESTADO = '**P**'*             |                                  |
|                                       |                                  | - *ESTADO = '**P**'*             |
| - *USER_ALTERACAO_NAME = **NULL***    | - *USER_REGISTO_ID = id de       |                                  |
|                                       |   utilizador Logado*             | - *USER_REGISTO_ID = id de       |
| - *DATA_ALTERACAO = **NULL***         |                                  |   utilizador Logado*             |
|                                       | - *USER_REGISTO_NAME = nome de   |                                  |
| - *ESTADO = "**P**"*                  |   utilizador Logado*             | - *USER_REGISTO_NAME = nome de   |
|                                       |                                  |   utilizador Logado*             |
| - *FUN_ID = id de RH_T_FUNCIONARIOS*  | - *USER_ALTERACAO \_ID =         |                                  |
|                                       |   **NULL***                      | - *USER_ALTERACAO \_ID =         |
| - *TIPO_CONTRATO =* Tipo de vinculo   |                                  |   **NULL***                      |
|   Laboral                             | - *USER_ALTERACAO_NAME =         |                                  |
|                                       |   **NULL***                      | - *USER_ALTERACAO_NAME =         |
| - *TIPO_SITUACAO = INICIO ou          |                                  |   **NULL***                      |
|   CONTINUIDADE*                       | - *DATA_ALTERACAO = **NULL***    |                                  |
|                                       |                                  | - *DATA_ALTERACAO = **NULL***    |
| - *ESTADO_CONTRATO = 'ATIVO'*         | - *FUN_ID = id de                |                                  |
|                                       |   RH_T_FUNCIONARIOS*             | - *OBS = 'NOVO_CONTRATO''*       |
| - *REFERENCIA = ´ NOVO_CONTRATO ´*    |                                  |                                  |
|                                       | - *CARREIRA_ID = ID de           | - *FUN_ID = id de                |
| - *OBS = ´ NOVO_CONTRATO*             |   RH_T_CARREIRA*                 |   RH_T_FUNCIONARIOS*             |
|                                       |                                  |                                  |
| - *VERSAO = 1*                        | ***3.2 Salario (1 registo)***    | - *CARREIRA_ID = ID de           |
|                                       |                                  |   RH_T_CARREIRA*                 |
| - *CONTRATO_ID = ID DE                | - *VALOR = Valor do campo        |                                  |
|   RH_T_CONTRATO_VINCULO*              |   Salario do formulario*         |   2.  *INPS*                     |
|                                       |                                  |                                  |
| *1.1 registo em RH_T_MOBILIDADE*      | - *DATA_INICIO = Data inicio de  | - *VALOR = 0*                    |
|                                       |   Função do formulario*          |                                  |
| - *DATA_REGISTO= '**SYSDATE'***       |                                  | - *DATA_INICIO = Data inicio de  |
|                                       | - *DATA_FIM = Data Fim de        |   Função do formulario*          |
| - *USER_REGISTO_ID = id de utilizador |   Função*                        |                                  |
|   Logado*                             |                                  | - *DATA_FIM = Data Fim de Funão* |
|                                       | - *TM_ID =                       |                                  |
| - *USER_REGISTO_NAME = nome de        |   **FUNÇÃO**:GET_MOVIMENTO_SALL* | - *TM_ID =                       |
|   utilizador Logado*                  |                                  |   **FUNÇÃO**:GET_MOVIMENTO_INPS* |
|                                       | - *ESTADO = '**P**'*             |                                  |
| - *USER_ALTERACAO \_ID = **NULL***    |                                  | - *ESTADO = '**P**'*             |
|                                       | - *USER_REGISTO_ID = id de       |                                  |
| - *USER_ALTERACAO_NAME = **NULL***    |   utilizador Logado*             | - *USER_REGISTO_ID = id de       |
|                                       |                                  |   utilizador Logado*             |
| - *TIPO_SITUACAO = INICIO ou          | - *USER_REGISTO_NAME = nome de   |                                  |
|   CONTINUIDADE*                       |   utilizador Logado*             | - *USER_REGISTO_NAME = nome de   |
|                                       |                                  |   utilizador Logado*             |
| - *OBS = "NOVO_CONTRATO"*             | - *USER_ALTERACAO \_ID =         |                                  |
|                                       |   **NULL***                      | - *USER_ALTERACAO \_ID =         |
| - *CONTRATO_ID = id de                |                                  |   **NULL***                      |
|   RH_T_CONTRATO_VINCULO*              | - *USER_ALTERACAO_NAME =         |                                  |
|                                       |   **NULL***                      | - *USER_ALTERACAO_NAME =         |
| - *FUN_ID = ID DE RH_T_FUNCIONARIOS*  |                                  |   **NULL***                      |
|                                       | - *DATA_ALTERACAO = **NULL***    |                                  |
| *1.2 RH_T_CARREIRA*                   |                                  | - *DATA_ALTERACAO = **NULL***    |
|                                       | - *OBS = 'NOVO_CONTRATO'*        |                                  |
| - *DATA_REGISTO= '**SYSDATE'***       |                                  | - *OBS = 'NOVO_CONTRATO'*        |
|                                       | - *FUN_ID = id de                |                                  |
| - *USER_REGISTO_ID = id de utilizador |   RH_T_FUNCIONARIOS*             | - *FUN_ID = id de                |
|   Logado*                             |                                  |   RH_T_FUNCIONARIOS*             |
|                                       | - *CARREIRA_ID = ID de           |                                  |
| - *USER_REGISTO_NAME = nome de        |   RH_T_CARREIRA*                 | - *CARREIRA_ID = ID de           |
|   utilizador Logado*                  |                                  |   RH_T_CARREIRA*                 |
|                                       | *3.3 deve ser feito nova         |                                  |
| - *USER_ALTERACAO \_ID = **NULL***    | associação da tabela             | *5- Registo na tabela de         |
|                                       | **RH_T_TIPOS_RELACIONAMENTO** e  | validação **RH_T_VALIDACAO***    |
| - *USER_ALTERACAO_NAME = **NULL***    | **RH_T_DEF_REMUNECACAO** na      |                                  |
|                                       | TABELA **RH_T_REMUN_TIPREL***    | - *TIPO_ACCAO**= 'INSERT '       |
| - *TIPO_SITUACAO = INICIO ou          |                                  |   (**DOMAINS = TIPO_ACAO**)***   |
|   CONTINUIDADE*                       | - *REM_ID = ide de               |                                  |
|                                       |   RH_T_DEF_REMUNERACAO*          | - *REFERENCIA_NAME **=           |
| - *OBS = "NOVO_CONTRATO"*             |                                  |   'CONTRATO' (**DOMAINS =        |
|                                       | - *TIPREL_ID = id de             |   ACCAO_REFERENTE**)***          |
| - *CONTRATO_ID = id de                |   RH_T_TIPOS_RELACIONAMENTO*     |                                  |
|   RH_T_CONTRATO_VINCULO*              |                                  | - *REFERENCIA_ID **= ID** de     |
|                                       | - *ESTADO = P*                   |   tabela                         |
| - *CONTR_VINCULO_ID = ID DE           |                                  |   **RH_T_CONTRATO_VINCULO***     |
|   RH_T_CONTRATO_VINCULO*              | - *USER_REGISTO_ID = id de       |                                  |
|                                       |   utilizador Logado*             | - *FUN_ID **= ID** de tabela     |
| *2.3 RH_T_REGIME*                     |                                  |   **RH_T_FUNCIONARIOS***         |
|                                       | - *USER_REGISTO_NAME = nome de   |                                  |
| - *DATA_REGISTO= '**SYSDATE'***       |   utilizador Logado*             | - *TIPREL_ID **= ID DE           |
|                                       |                                  |   RH_TIPOS_RELACIONAMENTO***     |
| - *USER_REGISTO_ID = id de utilizador | - *USER_ALTERACAO \_ID =         |                                  |
|   Logado*                             |   **NULL***                      | - *DATA_REGISTO **= SYSDATE***   |
|                                       |                                  |                                  |
| - *USER_REGISTO_NAME = nome de        | - *USER_ALTERACAO_NAME =         | - *USER_REGISTO_NAME = nome de   |
|   utilizador Logado*                  |   **NULL***                      |   utilizador Logado*             |
|                                       |                                  |                                  |
| - *USER_ALTERACAO \_ID = **NULL***    | - *DATA_ALTERACAO = **NULL***    | - *USER_REGISTO_ID = id de       |
|                                       |                                  |   utilizador Logado*             |
| - *USER_ALTERACAO_NAME = **NULL***    | ***4**.O sistema deve gravar na  |                                  |
|                                       | tabela **RH_DEF_PAGAMENTOS** as  | - *ESTADO **= 'P'***             |
| - *TIPO_SITUACAO = INICIO ou          | informações do separador de      |                                  |
|   CONTINUIDADE*                       | **Encargos / Descontos*** *e 2   | ***5.1** Registo Detalhe na      |
|                                       | registo de **IUR** e **INPS***   | tabela                           |
| - *OBS = "NOVO_CONTRATO"*             |                                  | **RH_T_VALIDACAO_DETALHE***      |
|                                       | ***4.1 Separador Encargos /      |                                  |
| - *FUN_ID = ID DE RH_T_FUNCIONARIOS*  | Descontos***                     | - *VALIDACAO_ID **= id de tabela |
|                                       |                                  |   RH_T_VALIDACAO***              |
| - *CONTRATO_ID = id de                | - *OBS = 'NOVO_CONTRATO'*        |                                  |
|   RH_T_CONTRATO_VINCULO*              |                                  | - *CAMPO_ALTERADO **= NULL***    |
|                                       | - *ESTADO = '**P**'*             |                                  |
| *2.4- RH_T_SITUCAO_LABORAL*           |                                  | - *VALOR_ANTERIOR = NULL*        |
|                                       | - *USER_REGISTO_ID = id de       |                                  |
| - *[SITUACAO_LABORAL_ID = PEGA ID DE  |   utilizador Logado*             | - *VALOR_NOVO = NULL*            |
|   RH_T_PARAM_SIT_LABORAL.PARAM_SIT_ID |                                  |                                  |
|   = RH_T_PARAM_SITUACAO.ID ONDE       | - *USER_REGISTO_NAME = nome de   | - *TABELA_NAME = " nome de       |
|   ESTADO = ATIVO]{.mark}*             |   utilizador Logado*             |   tabela a ser registado"*       |
|                                       |                                  |                                  |
| - *MOTIVO_SIT_LAB = 'NOVO_CONTRATO'*  | - *USER_ALTERACAO \_ID =         | - *TABELA \_ID = "id de tabela a |
|                                       |   **NULL***                      |   ser registado*                 |
| - *DATA_INICIO = DATA INICIO          |                                  |                                  |
|   CONTRATO*                           | - *USER_ALTERACAO_NAME =         |                                  |
|                                       |   **NULL***                      |                                  |
| - *DATA_FIM = DATA FIM CONTRATO*      |                                  |                                  |
|                                       | - *DATA_ALTERACAO = **NULL***    |                                  |
| - *FUN_ID = ID DE RH_T_FUNCIONARIO*   |                                  |                                  |
|                                       | - *CARREIRA_ID = ID de           |                                  |
| - *CONTRATO_ID = ID DE                |   RH_T_CARREIRA*                 |                                  |
|   RH_T_CONTRATO_VINCULO*              |                                  |                                  |
|                                       | - *FUN_ID = ID de                |                                  |
| - ESTADO = 'P'                        |   RH_T_FUNCIONARIOS*             |                                  |
|                                       |                                  |                                  |
| - *DATA_REGISTO = '**SYSDATE'***      |                                  |                                  |
|                                       |                                  |                                  |
| - *USER_REGISTO_ID = = id de          |                                  |                                  |
|   utilizador Logado*                  |                                  |                                  |
|                                       |                                  |                                  |
| - *USER_REGISTO_NAME = nome de        |                                  |                                  |
|   utilizador Logado*                  |                                  |                                  |
|                                       |                                  |                                  |
| *2.5\--update e novo Registo - Fazer  |                                  |                                  |
| uma nova gravação na tabela de        |                                  |                                  |
| RH_T_TIPOS_RELACIONAMENTO, pegas      |                                  |                                  |
| todas informações onde campo          |                                  |                                  |
| est_ult_adm = 1 (faz update           |                                  |                                  |
| est_ult_adm = 0 e data \_fim = data   |                                  |                                  |
| inicio do novo registo ) , e regista  |                                  |                                  |
| com novas alteraçoes nos campos do    |                                  |                                  |
| formulario e outros seguinte campos*  |                                  |                                  |
|                                       |                                  |                                  |
| - *DATA_REGISTO= 'SYSDATE'*           |                                  |                                  |
|                                       |                                  |                                  |
| - *USER_REGISTO_ID = id de utilizador |                                  |                                  |
|   Logado*                             |                                  |                                  |
|                                       |                                  |                                  |
| - *USER_REGISTO_NAME = nome de        |                                  |                                  |
|   utilizador Logado*                  |                                  |                                  |
|                                       |                                  |                                  |
| - *USER_ALTERACAO \_ID = NULL*        |                                  |                                  |
|                                       |                                  |                                  |
| - *DATA_INICIO*                       |                                  |                                  |
|                                       |                                  |                                  |
| - *USER_ALTERACAO_NAME = NULL*        |                                  |                                  |
|                                       |                                  |                                  |
| - *DATA_ALTERACAO = NULL*             |                                  |                                  |
|                                       |                                  |                                  |
| - *FUN_ID = id de RH_T_FUNCIONARIOS*  |                                  |                                  |
|                                       |                                  |                                  |
| - *[CONTR_VINCULO_ID]{.mark} = ID de  |                                  |                                  |
|   tabela Contrato*                    |                                  |                                  |
|                                       |                                  |                                  |
| - *CARREIRA_ID = id de tabela         |                                  |                                  |
|   RH_T_CARREIRA*                      |                                  |                                  |
|                                       |                                  |                                  |
| - *MOB_ID = id de MOBILIDADE*         |                                  |                                  |
|                                       |                                  |                                  |
| - *REGIME_ID = ID de tabela           |                                  |                                  |
|   RH_T_REGIME*                        |                                  |                                  |
|                                       |                                  |                                  |
| - *ESTADO = 'P'*                      |                                  |                                  |
|                                       |                                  |                                  |
| - *EST_ACT_ADM = 1*                   |                                  |                                  |
|                                       |                                  |                                  |
| - *TIPREL_ID = id no relacionamento   |                                  |                                  |
|   fechado*                            |                                  |                                  |
|                                       |                                  |                                  |
| - *OBS = "NOVO_CONTRATO"*             |                                  |                                  |
|                                       |                                  |                                  |
| - *TIPO_SITUACAO = INICIO ou          |                                  |                                  |
|   CONTINUIDADE*                       |                                  |                                  |
|                                       |                                  |                                  |
| <!-- -->                              |                                  |                                  |
|                                       |                                  |                                  |
| - *SITUAÇAO_LABORAL_ID = ID DE        |                                  |                                  |
|   RH_T_SITUACAO_LABORAL*              |                                  |                                  |
|                                       |                                  |                                  |
|   - *SITUACAO_LABORAL = 'ATIVO'*      |                                  |                                  |
|                                       |                                  |                                  |
|   - *REFERENTE = 'NOVO_CONTRATO'*     |                                  |                                  |
+---------------------------------------+----------------------------------+----------------------------------+
| 5.  *Registo de log **no IGRP***                                                                            |
+-------------------------------------------------------------------------------------------------------------+

###### Validar Contrato

+----------------------------------------------------------------------+
| - A validação invoca a mesma página de Registo                       |
|                                                                      |
| - O campo validar deve ficar visivel                                 |
|                                                                      |
| - Ao **validar**, devem ser atualizadas todas as tabelas associadas, |
|   definindo o campo **estado = \'A\'**.                              |
|                                                                      |
| - Ao **desvalidar**, devem ser atualizadas todas as tabelas          |
|   associadas, definindo o campo **estado = \'I\'**.                  |
|                                                                      |
| - Caso o utilizador **atualize algum campo no formulário**, a        |
|   alteração deve ser **refletida na tabela correspondente**.         |
+----------------------------------------------------------------------+

##### Conversão de Contrato 

##### Renovação

![Uma imagem com captura de ecrã, texto, file Os conteúdos gerados por
IA podem estar
incorretos.](media/image23.png){width="9.693055555555556in"
height="2.5006944444444446in"}

+:-----------------:+:-----------------:+:-----------------:+:-----------------:+:-----------------------------------:+
| **Formulario**    | **Tipo**          | **Descrição**                         | **Gravação**                        |
+-------------------+-------------------+---------------------------------------+-------------------------------------+
| Validar           |                   |                                       |                                     |
+-------------------+-------------------+---------------------------------------+-------------------------------------+
| Vinculo           | *SELECT*          |                                       | *RH_T_CONTRATO_VINCULO.VINCULO_ID*  |
+-------------------+-------------------+---------------------------------------+-------------------------------------+
| Data inicio       | *DATE*            |                                       | *RH_T_CONTRATO_VINCULO.DATA_INICIO* |
+-------------------+-------------------+---------------------------------------+-------------------------------------+
| Duracão (meses)   | *NUMBER*          |                                       | *RH_T_CONTRATO_VINCULO.DURACAO*     |
+-------------------+-------------------+---------------------------------------+-------------------------------------+
| Data Fim          | *DATE*            |                                       | *RH_T_CONTRATO_VINCULO.DATA_FIM*    |
+-------------------+-------------------+---------------------------------------+-------------------------------------+
| **REGRAS**        |                   |                                       |                                     |
+-------------------+-------------------+---------------------------------------+-------------------------------------+
| - *O sistema deve aletar quando o prazo de contrato esta quase a atingir o prazo. Deve ser criado um job que        |
|   notifica o utilizador sempre que o contrato esta proximo a atingir o Prozo. (VER ESPECIFICAÇÃO )*                 |
+-------------------+-------------------+---------------------------------------+-------------------------------------+
| **ACOES**         |                   |                                       |                                     |
+-------------------+-------------------+-------------------+-------------------+-------------------------------------+
| *1.novo registo em **RH_T_CONTRATO_VINCULO***             | *2. registar Validacao*                                 |
|                                                           |                                                         |
| - *VERSAO = ULTIMA VERSAO + 1*                            | - *TIPO_ACCAO**= 'UPDATE ' (**DOMAINS = TIPO_ACAO**)*** |
|                                                           |                                                         |
| - *CONTRATO_ID = CONTRATO_ID DE RH_T_CONTRATO_VINCULO     | - *REFERENCIA_NAME **= 'RENOVACAO_CONTRATO' (**DOMAINS  |
|   ORIGEM*                                                 |   = ACCAO_REFERENTE**)***                               |
|                                                           |                                                         |
| *2-update e novo Registo - Fazer uma nova gravação na     | - *REFERENCIA_ID **= ID** de tabela                     |
| tabela de RH_T_TIPOS_RELACIONAMENTO, pegas todas          |   **RH_T_FUNCIONARIOS***                                |
| informações onde campo est_ult_adm = 1 (faz update        |                                                         |
| est_ult_adm = 0 e data \_fim = data inicio do novo        | - *FUN_ID **= ID** de tabela **RH_T_FUNCIONARIOS***     |
| registo) , e regista com novas alteraçoes nos campos do   |                                                         |
| formulario e outros seguinte campos*                      | - *TIPREL_ID **= NULL***                                |
|                                                           |                                                         |
| - *DATA_REGISTO= 'SYSDATE'*                               | - *DATA_REGISTO **= SYSDATE***                          |
|                                                           |                                                         |
| - *USER_REGISTO_ID = id de utilizador Logado*             | - *USER_REGISTO_NAME = nome de utilizador Logado*       |
|                                                           |                                                         |
| - *USER_REGISTO_NAME = nome de utilizador Logado*         | - *USER_REGISTO_ID = id de utilizador Logado*           |
|                                                           |                                                         |
| - *USER_ALTERACAO \_ID = NULL*                            | - *ESTADO **= 'P'***                                    |
|                                                           |                                                         |
| - *DATA_INICIO*                                           | ***2.1** Registo Detalhe de na tabela                   |
|                                                           | **RH_T_VALIDACAO_DETALHE***                             |
| - *USER_ALTERACAO_NAME = NULL*                            |                                                         |
|                                                           | - ***VALIDACAO_ID = id de tabela RH_T_VALIDACAO***      |
| - *DATA_ALTERACAO = NULL*                                 |                                                         |
|                                                           | - *CAMPO_ALTERADO **= NULL***                           |
| - *ESTADO = 'P'*                                          |                                                         |
|                                                           | - *VALOR_ANTERIOR = NULL*                               |
| - *EST_ACT_ADM = 1*                                       |                                                         |
|                                                           | - *VALOR_NOVO = NULL*                                   |
| - *TIPREL_ID = id do relacionamento Fechado*              |                                                         |
|                                                           | - *TABELA_NAME = " nome de tabela a ser registado"*     |
| - *OBS = "RENOVACAO \_CONTRATO"*                          |                                                         |
|                                                           | - *TABELA \_ID = "id de tabela a ser registado*         |
| - *TIPO_SITUACAO = "RENOVACAO"*                           |                                                         |
|                                                           | *3- Registo de log **no IGRP***                         |
| - *REFERENCIA = 'CONTRATO*                                |                                                         |
+-----------------------------------------------------------+---------------------------------------------------------+

###### Validar Renovacao

+----------------------------------------------------------------------+
| - A validação invoca a mesma página de Registo                       |
|                                                                      |
| - O campo validar deve ficar visivel                                 |
|                                                                      |
| - Ao **validar**, devem ser atualizadas todas as tabelas associadas, |
|   definindo o campo **estado = \'A\'**.                              |
|                                                                      |
| - Ao **desvalidar**, devem ser atualizadas todas as tabelas          |
|   associadas, definindo o campo **estado = \'I\'**.                  |
|                                                                      |
| - Caso o utilizador **atualize algum campo no formulário**, a        |
|   alteração deve ser **refletida na tabela correspondente**.         |
+----------------------------------------------------------------------+

#### Mobilidade

###### Lista Mobilidade 

+:------------------:+:------------------:+:------------------:+:------------------------------------:+
| **filtro**         | **Tipo**           | **Descrição**      | **Fonte Dados**                      |
+--------------------+--------------------+--------------------+--------------------------------------+
| Data Inicio        | DATE               |                    | *RH_V_MOBILIDADE.DATA_INICIO*        |
+--------------------+--------------------+--------------------+--------------------------------------+
| Data Inicio        | DATE               |                    | *RH_V_MOBILIDADE.DATA_FIM*           |
+--------------------+--------------------+--------------------+--------------------------------------+
| Tipo Mobilidade    | SELECT             | **DOMAINS** =      | *RH_V_MOBILIDADE. TIPO_MOBILIDADE*   |
|                    |                    | TIPO_MOV_LABORAL,  |                                      |
|                    |                    | REFERENTE =        |                                      |
|                    |                    | 'MOBILIDADE'       |                                      |
+--------------------+--------------------+--------------------+--------------------------------------+
| **Lista**          | **Tipo**           | **Descrição**      | **Fonte Dados**                      |
+--------------------+--------------------+--------------------+--------------------------------------+
| Tipo Mobilidade    | *TEXT*             | **DOMAINS** =      | *RH_V_MOBILIDADE.TIPO_SITUACAO_DESC* |
|                    |                    | TIPO_MOV_LABORAL   |                                      |
+--------------------+--------------------+--------------------+--------------------------------------+
| Direção            | *TEXT*             |                    | *RH_V_MOBILIDADE.DIRECAO_DESC*       |
+--------------------+--------------------+--------------------+--------------------------------------+
| Secão              | *TEXT*             |                    | *RH_V_MOBILIDADE. SECAO_DESC*        |
+--------------------+--------------------+--------------------+--------------------------------------+
| Cargo              |                    |                    | *RH_V_MOBILIDADE. CARGO_DESC*        |
+--------------------+--------------------+--------------------+--------------------------------------+
| Local Trabalho     |                    |                    | *RH_V_MOBILIDADE. LOCAL_TRAB_DESC*   |
+--------------------+--------------------+--------------------+--------------------------------------+
| Data inicio        | *TEXT*             |                    | *RH_V_MOBILIDADE.DATA_INICIO*        |
+--------------------+--------------------+--------------------+--------------------------------------+
| Data Fim           | *TEXT*             |                    | *RH_V_MOBILIDADE.DATA_FIM*           |
+--------------------+--------------------+--------------------+--------------------------------------+
| **REGRAS**         |                    |                    |                                      |
+--------------------+--------------------+--------------------+--------------------------------------+
| - *O sistema não deve deixar efectuar qq alteração quando a mobilidade em questão já tenho um       |
|   processamento.*                                                                                   |
|                                                                                                     |
| - *Deve aparecer somente mobilidade Ativo*                                                          |
+-----------------------------------------------------------------------------------------------------+
| **ACCOES**                                                                                          |
+--------------------+--------------------------------------------------------------------------------+
| Editar             | *Somente deiza editar caso o registo ainda não for processado                  |
|                    | (RH_V_MOBILIDADE.PROCESSAMENTO = NÃO)*                                         |
+--------------------+--------------------------------------------------------------------------------+
| Eliminar           | *Somente deixa cancelar caso o registo ainda não for validade.                 |
|                    | (RH_V_MOBILIDADE.ESTADO = 'P') .*                                              |
|                    |                                                                                |
|                    | *Ao eliminar o RH_V_MOBILIDADE.ESTADO = 'E'*                                   |
+--------------------+--------------------------------------------------------------------------------+

###### Novo/ Editar Mobilidade

![Uma imagem com texto, captura de ecrã, número, file Os conteúdos
gerados por IA podem estar
incorretos.](media/image24.png){width="9.693055555555556in"
height="3.25625in"}

+:----------------:+:----------------:+:-----------------:+:-----------------:+:-------------------------------------------------------------------------------------------------:+
| **Formulario**   | **Tipo**         | **Descrição**                         | **GRAVAÇÃO**                                                                                      |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| Validar          | *RADIOLIST*      | Esse deve ficar oculto, so fica       | *RH_T_MOBILIDADE. ESTADO*                                                                         |
|                  |                  | visivel em caso de validação desse    |                                                                                                   |
|                  |                  | Registo. No modo validação esse campo |                                                                                                   |
|                  |                  | é obrigatorio                         |                                                                                                   |
|                  |                  |                                       |                                                                                                   |
|                  |                  | DOMAINS= SIM_NAO                      |                                                                                                   |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| \*Tipo           | *MULTISELCT*     | **DOMAINS** = TIPO_MOV_LABORAL,       | *RH_T_MOBILIDADE. TIPO_SITUACAO*                                                                  |
| Mobilidade       |                  | REFERENTE = '**MOBILIDADE'**,         |                                                                                                   |
|                  |                  |                                       | *RH_T_TIPOS_RELACIONAMENTO.TIPO_SITUACAO*                                                         |
|                  |                  | Permite selecionar mais de uma        |                                                                                                   |
|                  |                  | mobilidade                            |                                                                                                   |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| \*Data Inicio    | *DATE*           |                                       | *RH_T_MOBILIDADE.DATA_INICIO*                                                                     |
|                  |                  |                                       |                                                                                                   |
|                  |                  |                                       | *RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO*                                                           |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| Data Fim         | *DATE*           |                                       | *RH_T_MOBILIDADE.DATA_FIM*                                                                        |
|                  |                  |                                       |                                                                                                   |
|                  |                  |                                       | *RH_T_TIPOS_RELACIONAMENTO.DATA_FIM*                                                              |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| Mobilidade       | *Esses campos ficam visivel somente apos selecionar o tipo Mobilidade*                                                                                       |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| Direção (Antes ) | *ENABLE*         | Visivel caso o tipo de Mobilidade =   | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--*                                   |
|                  |                  | DIRECAO                               |                                                                                                   |
|                  |                  |                                       |                                                                                                   |
|                  |                  | (vem preenchido com                   |                                                                                                   |
|                  |                  | *RH_V_MOBILIDADE.DIRECAO_DESC*)       |                                                                                                   |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| Direção (Depois  | *SELECT*         | Visivel caso o tipo de Mobilidade =   | *RH_T_MOBILIDADE.INSTIT_ID*                                                                       |
| )                |                  | DIRECAO                               |                                                                                                   |
|                  |                  |                                       | *RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID*                                                             |
|                  |                  | ***FUNÇÃO:** GET_DIRECAO_SERVICO*     |                                                                                                   |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| Secão (Antes )   | *ENABLE*         | Visivel caso o tipo de Mobilidade =   | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--* |
|                  |                  | SECAO                                 |                                                                                                   |
|                  |                  |                                       |                                                                                                   |
|                  |                  | vem preenchido com                    |                                                                                                   |
|                  |                  | *RH_V_MOBILIDADE.SECAO_DESC*)         |                                                                                                   |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| Secão (Depois )  | *SELECT*         | Visivel caso o tipo de Mobilidade =   | *RH_T_MOBILIDADE.SECAO_ID RH_T_TIPOS_RELACIONAMENTO.SECAO_ID*                                     |
|                  |                  | SECAO                                 |                                                                                                   |
|                  |                  |                                       |                                                                                                   |
|                  |                  | **FUNÇÃO:** GET_SECCAO (*P\_          |                                                                                                   |
|                  |                  | INSTIT_ID)*                           |                                                                                                   |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| Local Trabalho   | *ENABLE*         | Visivel caso o tipo de Mobilidade =   | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--*         |
| (Antes )         |                  | Local                                 |                                                                                                   |
|                  |                  |                                       |                                                                                                   |
|                  |                  | vem preenchido com                    |                                                                                                   |
|                  |                  | *RH_V_MOBILIDADE.LOCAL_TRAB_DESC)*    |                                                                                                   |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| Local            | *SELECT*         | Visivel caso o tipo de Mobilidade =   | *RH_T_MOBILIDADE. LOCAL_TRAB_ID*                                                                  |
| Trabalho(Depois  |                  | Local                                 |                                                                                                   |
| )                |                  |                                       | *RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID*                                                         |
|                  |                  | **FUNÇÃO** : GET_LOCAL_TRABALHO       |                                                                                                   |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| **REGRAS**       |                  |                                       |                                                                                                   |
+------------------+------------------+---------------------------------------+---------------------------------------------------------------------------------------------------+
| ***Ao Clicar no botão gravar, deve ser feito as seguintes ações***                                                                                                              |
|                                                                                                                                                                                 |
| - *Deve quardar na tabela Log*                                                                                                                                                  |
|                                                                                                                                                                                 |
| - *Deve quardar na tabela de Validação*                                                                                                                                         |
|                                                                                                                                                                                 |
| - *Criar um historico de trababo*                                                                                                                                               |
+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **OUTRAS GRAVAÇOES**                                                                                                                                                            |
+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| *UPDATE*                                                                                                                                                                        |
+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| *Faz update nos registos anteriores , ou seja inativa os registos ativos*                                                                                                       |
|                                                                                                                                                                                 |
| *1.2Inativa a mobilidade em estado ativo **RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e data fim is not null)***                                                                |
|                                                                                                                                                                                 |
| - *DATA_FIM = data Inicio Carreira -1*                                                                                                                                          |
|                                                                                                                                                                                 |
| - *USER_ALTERACAO \_ID = utilizador logado*                                                                                                                                     |
|                                                                                                                                                                                 |
| - *USER_ALTERACAO_NAME = **nome de utilizador***                                                                                                                                |
|                                                                                                                                                                                 |
| - *DATA_ALTERACAO = sysdate*                                                                                                                                                    |
|                                                                                                                                                                                 |
| - *EST_ACT_ADM = 0*                                                                                                                                                             |
+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| *INSERT*                                                                                                                                                                        |
+---------------------------------------------------------+-----------------------------------------------------------------------------------------------------------------------+
| *1-**Fazer uma nova gravação na tabela de               | *3-Grava na tabela de validação -- **RH_T_VALIDACAO***                                                                |
| RH_T_MOBILIDADE, pegas todas informações do registo     |                                                                                                                       |
| anterior , e regista com novas alteraçoes nos campos do | - *TIPO_ACCAO**= 'INSERT ' (**DOMAINS = TIPO_ACAO**)***                                                               |
| formulario e outros seguinte campos*                    |                                                                                                                       |
|                                                         | - *REFERENCIA_NAME **= 'MOBILIDADE' (**DOMAINS = ACCAO_REFERENTE**)***                                                |
| - *DATA_REGISTO= 'SYSDATE'*                             |                                                                                                                       |
|                                                         | - *REFERENCIA_ID **= ID** de tabela **RH_T_MOBILIDADE***                                                              |
| - *USER_REGISTO_ID = id de utilizador Logado*           |                                                                                                                       |
|                                                         | - *FUN_ID **= ID** de tabela **RH_T_FUNCIONARIOS***                                                                   |
| - *USER_REGISTO_NAME = nome de utilizador Logado*       |                                                                                                                       |
|                                                         | - *TIPREL_ID **= ID de RH_T_TIPOS_RELACIONAMENTO***                                                                   |
| - *USER_ALTERACAO \_ID = NULL*                          |                                                                                                                       |
|                                                         | - *DATA_REGISTO **= SYSDATE***                                                                                        |
| - *DATA_INICIO*                                         |                                                                                                                       |
|                                                         | - *USER_REGISTO_NAME = nome de utilizador Logado*                                                                     |
| - *USER_ALTERACAO_NAME = NULL*                          |                                                                                                                       |
|                                                         | - *USER_REGISTO_ID = id de utilizador Logado*                                                                         |
| - *DATA_ALTERACAO = NULL*                               |                                                                                                                       |
|                                                         | - *ESTADO **= 'P'***                                                                                                  |
| - *TIPREL_ID = ID DO REGISTO FECHADO*                   |                                                                                                                       |
|                                                         | *3.1- Registo Detalhe de LOG na tabela **RH_T_VALIDACAO_DETALHE***                                                    |
| - *CONTRATO_ID = ID DE TABELA RH_T_CONTRATO_VINCULO*    |                                                                                                                       |
|                                                         | - *VALIDACAO_ID **= id de tabela RH_T_VALIDACAO***                                                                    |
| - *FUN_ID = ID DE TABELA RH_T_FUNCIONARIOS*             |                                                                                                                       |
|                                                         | - *CAMPO_ALTERADO **= nome de campos***                                                                               |
| - *OBS = 'MOBILIDADE'*                                  |                                                                                                                       |
|                                                         | - *VALOR_ANTERIOR = valor antes*                                                                                      |
| *2-Fazer uma nova gravação na tabela de                 |                                                                                                                       |
| RH_T_TIPOS_RELACIONAMENTO, pegas todas informações onde | - *VALOR_NOVO = valor depois*                                                                                         |
| campo est_ult_adm = 1 (faz update est_ult_adm = 0 e     |                                                                                                                       |
| data \_fim = data inicio do novo registo) , e regista   | *• TABELA_NAME = " nome de tabela a ser registado"*                                                                   |
| com novas alteraçoes nos campos do formulario e outros  |                                                                                                                       |
| seguinte campos*                                        | *• TABELA \_ID = "id de tabela a ser registado*                                                                       |
|                                                         |                                                                                                                       |
| - *DATA_REGISTO= 'SYSDATE'*                             | *4-Grava LOG no IGRP*                                                                                                 |
|                                                         |                                                                                                                       |
| - *USER_REGISTO_ID = id de utilizador Logado*           | -                                                                                                                     |
|                                                         |                                                                                                                       |
| - *USER_REGISTO_NAME = nome de utilizador Logado*       |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *USER_ALTERACAO \_ID = NULL*                          |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *DATA_INICIO*                                         |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *USER_ALTERACAO_NAME = NULL*                          |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *DATA_ALTERACAO = NULL*                               |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *MOB_ID = ID DE RH_T_MOBILIDADE*                      |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *TIPREL_ID = ID da mobilidade anterior*               |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *ESTADO = 'P'*                                        |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *OBS = 'MOBILIDADE- \|\| TIPO_MOBILIDADE*             |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *EST_ACT_ADM = 1*                                     |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *OBS = 'MOBILIDADE'*                                  |                                                                                                                       |
|                                                         |                                                                                                                       |
| - *REFERENTE = 'MOBILIDADE´'*                           |                                                                                                                       |
+---------------------------------------------------------+-----------------------------------------------------------------------------------------------------------------------+

###### Validar mobilidade 

+-----------------------------------------------------------------------+
| **REGRAS**                                                            |
+-----------------------------------------------------------------------+
| - A validação invoca a mesma página de Registo                        |
|                                                                       |
| - O campo validar deve ficar visivel                                  |
|                                                                       |
| - Ao **validar**, devem ser atualizadas todas as tabelas associadas,  |
|   definindo o campo **estado = \'A\'**.                               |
|                                                                       |
| - Ao **desvalidar**, devem ser atualizadas todas as tabelas           |
|   associadas, definindo o campo **estado = \'I\'**.                   |
|                                                                       |
| - Caso o utilizador **atualize algum campo no formulário**, a         |
|   alteração deve ser **refletida na tabela correspondente**.          |
|                                                                       |
| 1.2.-Ao Validar gera um ordem de Serviço na tabela                    |
| **RH_T_ORDEM_SERVICO (**caso for validado**)**                        |
|                                                                       |
| - DESCRICAO = 'Mobilidade do colaborador - ' \|\|                     |
|   RH_T_FUNCIONARIOS.NOME                                              |
|                                                                       |
| - REFERENTE = 'MOBILIDADE'                                            |
|                                                                       |
| - FUN_ID = RH_T_FUNCIONARIOS.ID                                       |
|                                                                       |
| - TIPREL_ID = RH_T_TIPOS_RELACIONAMENTO.ID                            |
|                                                                       |
| - VALIDACAO_ID = RH_T_VALIDACAO.ID                                    |
|                                                                       |
| *1.Caso o utilizador não valide a mobilidade, o sistema deve inativar |
| a própria mobilidade, bem como as remunerações e os descontos a ela   |
| associados.*                                                          |
|                                                                       |
| - *RH_T_TIPOS_RELACIONAMENTO.ESTADO = 'I'*                            |
|                                                                       |
| - *RH_T_MOBILIDADE.ESTADO = 'I'*                                      |
|                                                                       |
| *1.Caso o utilizador valida a mobilidade deve seguir o especificado   |
| em Editar Mobilidade.*                                                |
|                                                                       |
| - *RH_T_TIPOS_RELACIONAMENTO.ESTADO = 'A'*                            |
|                                                                       |
| - *RH_T_MOBILIDADE.ESTADO = 'A'*                                      |
+-----------------------------------------------------------------------+

####### Modelo de mobilidade 

  -----------------------------------------------------
  ![](media/image25.png){width="6.052083333333333in"
  height="5.124305555555556in"}

  -----------------------------------------------------

####  Gestão Carreira

##### Lista Carreira

![Uma imagem com texto, captura de ecrã, número, software Os conteúdos
gerados por IA podem estar
incorretos.](media/image26.png){width="9.693055555555556in"
height="4.470833333333333in"}

+:-------------:+:------------------:+:--------------------:+:-------------------------------------:+
| **filtro**    | **Tipo**           | **Descrição**        | **Fonte Dados**                       |
+---------------+--------------------+----------------------+---------------------------------------+
| Data Inicio   | Date               |                      | *RH_V_CARREIRA.DATA_INICIO*           |
+---------------+--------------------+----------------------+---------------------------------------+
| Data Fim      | Date               |                      | *RH_V_CARREIRA.DATA_FIM*              |
+---------------+--------------------+----------------------+---------------------------------------+
| Tipo Carreira | Select             | **DOMAINS** =        | *RH_T_CARREIRA. TIPO_SITUACAO*        |
|               |                    | TIPO_MOV_LABORAL,    |                                       |
|               |                    | REFERENTE =          |                                       |
|               |                    | 'CARREIRA'           |                                       |
+---------------+--------------------+----------------------+---------------------------------------+
| **Lista**     | **Tipo**           | **Descrição**        | **Fonte Dados**                       |
+---------------+--------------------+----------------------+---------------------------------------+
| Tipo Carreira | *TEXT*             | **DOMAINS** =        | *RH_V_CARREIRA. TIPO_SITUACAO_DESC*   |
|               |                    | TIPO_MOV_LABORAL,    |                                       |
|               |                    | REFERENTE =          |                                       |
|               |                    | 'CARREIRA'           |                                       |
+---------------+--------------------+----------------------+---------------------------------------+
| Vinculo       | *TEXT*             |                      | *RH_V_CARREIRA.VINCULO_DESC*          |
+---------------+--------------------+----------------------+---------------------------------------+
| Carreira      | *TEXT*             |                      | *RH_V_CARREIRA.CARREIRA_DESC*         |
+---------------+--------------------+----------------------+---------------------------------------+
| cargo         | *TEXT*             |                      | *RH_V_CARREIRA.CARGO_DESC*            |
+---------------+--------------------+----------------------+---------------------------------------+
| Escalão       | *TEXT*             |                      | *RH_V_CARREIRA.ESCALAO_DESC*          |
+---------------+--------------------+----------------------+---------------------------------------+
| Salario       | *TEXT*             |                      | *RH_V_CARREIRA.SALARIO*               |
+---------------+--------------------+----------------------+---------------------------------------+
| Situação      | *TEXT*             |                      | *RH_V_CARREIRA.SITUACAO_LABORAL_DESC* |
| Laboral       |                    |                      |                                       |
+---------------+--------------------+----------------------+---------------------------------------+
| Data inicio   | *TEXT*             |                      | *RH_V_CARREIRA.DATA_INICIO*           |
+---------------+--------------------+----------------------+---------------------------------------+
| Data Fim      | *TEXT*             |                      | *RH_V_CARREIRA.DATA_FIM*              |
+---------------+--------------------+----------------------+---------------------------------------+
| **AÇÕES**     |                    |                      |                                       |
+---------------+--------------------+----------------------+---------------------------------------+
| Editar        | *Abre o mesmo formulario de eliminar, so pemite editar caso ainda não tenha tenho |
|               | processamento associado **(RH_V_CARREIRA. PROCESSAMENTO = NÃO), ...** Caso for    |
|               | editado, deve passar novamente para validação.*                                   |
+---------------+-----------------------------------------------------------------------------------+
| Eliminar      | *Só pemite eliminar caso ainda não for validdo ou seja caso                       |
|               | **RH_T_CARREIRA.**ESTADO **= 'P'***                                               |
|               |                                                                                   |
|               | *Ao eliminar o RH_V_MOBILIDADE.ESTADO = 'E'*                                      |
+---------------+-----------------------------------------------------------------------------------+

##### Novo / Editar 

![Uma imagem com texto, captura de ecrã, número, software Os conteúdos
gerados por IA podem estar
incorretos.](media/image27.png){width="9.693055555555556in"
height="5.201388888888889in"}

+:---------------:+:-------------:+:-------------:+:-------------:+:-------------:+:-----------------------------:+:-----------------------------:+:------------------------------:+
| **Formulario**  | **Tipo**      | **Descrição**                                 | **FONTE DADOS**                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Validar         | *RADIOLIST*   | Esse deve ficar oculto, so fica visivel em    | *RH_T_CARREIRA.ESTADO*                                                                         |
|                 |               | caso de validação desse Registo. No modo      |                                                                                                |
|                 |               | validação esse campo é obrigatorio            |                                                                                                |
|                 |               |                                               |                                                                                                |
|                 |               | DOMAINS= SIM_NAO                              |                                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Vinculo         | *ENABLE*      |                                               | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--*        |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| \*Tipo Carreira |               | *DOMAINS=* TIPO_MOV_LABORAL referente a       | *RH_T_CARREIRA. TIPO_SITUACAO*                                                                 |
|                 |               | 'CARREIRA'                                    |                                                                                                |
|                 |               |                                               | *RH_T_TIPOS_RELACIONAMENTO. TIPO_SITUACAO*                                                     |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| \*cargo         | *SELECT*      | ***TABELA** : RH_CARGOS ; **CAMPOS**          | *RH_T_CARREIRA.CARGO_ID*                                                                       |
|                 |               | :cod_cargo e descricao*                       |                                                                                                |
|                 |               |                                               | *RH_T_TIPOS_RELACIONAMENTO. CARGO_ID*                                                          |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| \*Carreira      | *SELECT*      | **FUNÇÃO:** *GET_CARREIRA (P_CARGO)*          | *RH_T_CARREIRA. CARR_PCCS_ID*                                                                  |
|                 |               |                                               |                                                                                                |
|                 |               |                                               | *RH_T_TIPOS_RELACIONAMENTO. CARR_PCCS_ID*                                                      |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Categoria       | *SELECT*      | **FUNÇÃO:** *GET_CATEGORIA(P_CARREIRA)*       | *RH_T_CARREIRA.CATEGORIA_ID*                                                                   |
|                 |               |                                               |                                                                                                |
|                 |               |                                               | *RH_T_TIPOS_RELACIONAMENTO. CATEGORIA_ID*                                                      |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| \*Escalão       | *SELECT*      | ***FUNÇÃO:** GET_ESCALAO                      | *RH_T_CARREIRA.ESCALAO_ID*                                                                     |
|                 |               | (P_CARREIRA,P_CATEGORIA)*                     |                                                                                                |
|                 |               |                                               | *RH_T_TIPOS_RELACIONAMENTO. ESCALAO_ID*                                                        |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| \*Salário       | *NUMBER*      | **FUNÇÃO** : GET_SALARIO (P_ESCALAO)          | *RH_T_CARREIRA.SALARIO*                                                                        |
|                 |               |                                               |                                                                                                |
|                 |               |                                               | *RH_T_TIPOS_RELACIONAMENTO. SALARIO*                                                           |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Moeda           | *Select*      | ***DOMAINS** = MOEDA*                         | *RH_T_CARREIRA.MOEDA*                                                                          |
|                 |               |                                               |                                                                                                |
|                 |               |                                               | *RH_T_TIPOS_RELACIONAMENTO. MOEDA*                                                             |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| \*Processamento | *radio*       | **DOMAINS**: SIM_NAO_NUMBER                   | *RH_T_CARREIRA.FLG_PROCESSA*                                                                   |
| Salarial        |               |                                               |                                                                                                |
|                 |               |                                               | *RH_T_TIPOS_RELACIONAMENTO. FLG_PROCESSA*                                                      |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| **Subsido : deve trazer por defeito os subsisdios registados ativos**                                                                                                            |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Tipo de         | *SELECT*      | Natureza do subsídio atribuído.\              | *RH_T_DEF_REMUNERACOES.TM_ID*                                                                  |
| Subsídio        |               | *(Ex.: Subsídio de Alimentação, Subsídio de   |                                                                                                |
|                 |               | Transporte, Subsídio de Férias, 13.º mês)*    |                                                                                                |
|                 |               |                                               |                                                                                                |
|                 |               | ***FUNÇÃO**: GET_MOVIMENTO_REMUNERACAO        |                                                                                                |
|                 |               | (P_TIPO)*                                     |                                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Percentagem     | *TEXT*        | Percentagem do salário base usada para        | *RH_T_DEF_REMUNERACOES.PERCENTAGEM*                                                            |
|                 |               | calcular o valor do subsídio (quando          |                                                                                                |
|                 |               | aplicável).                                   |                                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Valor           | *NUMBER*      | Montante atribuído ao colaborador a título de | *RH_T_DEF_REMUNERACOES.VALOR*                                                                  |
|                 |               | subsídio                                      |                                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| **Encargos / Descontos : deve trazer por defeito os descontos registados ativos**                                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Tipo de         | *SELECT*      | Identificação do tipo de encargo ou           | *RH_T_DEF_PAGAMENTOS.TM_ID*                                                                    |
| Encargos /      |               | desconto.\                                    |                                                                                                |
| Descontao       |               | *(Ex.: INPS, Imposto IRPS, Fundo Social,      |                                                                                                |
|                 |               | Sindicato)*                                   |                                                                                                |
|                 |               |                                               |                                                                                                |
|                 |               | ***FUNÇÃO**: GET_MOVIMENTO_DESCONTO (P_TIPO)* |                                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Valor           | *NUMBER*      | Montante a deduzir ou a assumir pela entidade | *RH_T_DEF_PAGAMENTOS.VALOR*                                                                    |
|                 |               | empregadora, podendo ser fixo ou percentual.  |                                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Data Inicio     | *DATE*        | Data a partir da qual o encargo/desconto      | *RH_T_DEF_PAGAMENTOS.DATA_INICIO*                                                              |
|                 |               | entra em vigor.                               |                                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| Data Fim        | *DATE*        | Data de cessação do encargo/desconto (quando  | *RH_T_DEF_PAGAMENTOS.DATA_FIM*                                                                 |
|                 |               | aplicável).                                   |                                                                                                |
+-----------------+---------------+-----------------------------------------------+------------------------------------------------------------------------------------------------+
| **REGRAS**                                                                                                                                                                       |
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| ***Ao Clicar no botão gravar, deve ser feito as seguintes ações***                                                                                                               |
|                                                                                                                                                                                  |
| - *Deve quardar na tabela Log*                                                                                                                                                   |
|                                                                                                                                                                                  |
| - *O colaborador pode ter mais de um vinculo ativo, mais somente recebe salarios em um deles (sempre rece salarios no vinculo cujo salario é mais alto), logo o sistema deve     |
|   validar se já existe uma marcado como sim, caso sim não deixar registar outra como sim*                                                                                        |
|                                                                                                                                                                                  |
| - *Deve quardar na tabela de Validação*                                                                                                                                          |
|                                                                                                                                                                                  |
| - *Criar um historico de trababo*                                                                                                                                                |
|                                                                                                                                                                                  |
| - *O sistema **não deve permitir o registo de uma CARREIRA** caso **não exista um contrato ativo** associado ao colaborador. Nessa situação, o botão **"Registar CARREIRA"**     |
|   deve permanecer **oculto**.*                                                                                                                                                   |
|                                                                                                                                                                                  |
| <!-- -->                                                                                                                                                                         |
|                                                                                                                                                                                  |
| - *Sempre que for registada uma **nova carreira**, esta deve ser automaticamente **submetida a validação**. O respetivo registo deve ser inserido na tabela de **Validação       |
|   (RH_T_VALIDACAO)**.*                                                                                                                                                           |
|                                                                                                                                                                                  |
| - *Ao Criar uma nova carreira, o sistema deve fechar o registo de salario anteiror (RH_T_DEF_REMUNERAÇÃO anteriror ativos )*                                                     |
|                                                                                                                                                                                  |
| ***Subsidio***                                                                                                                                                                   |
|                                                                                                                                                                                  |
| - *Deve trazer, preenchidos automaticamente, os subsídios ativos (**RH_T_DEF_REMUNERACAO.ESTADO = \'A\' e data fim não nulo**) e cujo movimento não corresponda a salário        |
|   (RH_T_DEF_REMUNERACOES.TM_ID = referente a SAL).*                                                                                                                              |
|                                                                                                                                                                                  |
| - *Ao gravar a mobilidade:*                                                                                                                                                      |
|                                                                                                                                                                                  |
|   - *O sistema deve recuperar todos os subsídios do separador **Subsídio** e registá-los na tabela de remuneração (**RH_T_DEF_REMUNERACAO**), cujo Data inicio é igual **Data    |
|     Inicio carreira***                                                                                                                                                           |
|                                                                                                                                                                                  |
|   - *O sistema deve igualmente registar um lançamento de salário, utilizando o valor indicado no campo **Salário da carreira**, na tabela RH_T_DEF_REMUNERACOES.*                |
|                                                                                                                                                                                  |
|   - *O sistema deve finalizar todas as remunerações da mobilidade anterior cujo campo **Data de Fim (**RH_T_DEF_REMUNERACAO.data_fim**)** não seja nulo e cujo **Estado** esteja |
|     ativo **(**RH_T_DEF_REMUNERACAO.estado = 'A'**)***                                                                                                                           |
|                                                                                                                                                                                  |
| ***Encargos / Descontos***                                                                                                                                                       |
|                                                                                                                                                                                  |
| - *Deve trazer, preenchidos automaticamente, os descontos ativos (**RH_T_DEF_PAGAMENTOS.ESTADO = \'A\' e data fim não nulo**) e cujo movimento não corresponda a salário         |
|   (**RH_T_DEF_PAGAMENTOS**.TM_ID != de IUR e INPS ).*                                                                                                                            |
|                                                                                                                                                                                  |
| - *Ao gravar a mobilidade:*                                                                                                                                                      |
|                                                                                                                                                                                  |
|   - *O sistema deve recuperar todos os subsídios do separador de **DESCONTOS** e registá-los na tabela de remuneração (**RH_T_DEF_PAGAMENTOS**), cujo Data inicio é igual **Data |
|     Inicio CARREIRA***                                                                                                                                                           |
|                                                                                                                                                                                  |
|   - *O sistema deve igualmente registar um lançamento de **INPS** e **IUR***                                                                                                     |
|                                                                                                                                                                                  |
|   - *O sistema deve finalizar todas os descontos da CARREIRA anterior cujo campo **Data de Fim (**RH_T_DEF_PAGAMENTOS.data_fim**)** não seja nulo e cujo **Estado** esteja ativo |
|     **(**RH_T_DEF_PAGAMENTOS.estado = 'A'**)***                                                                                                                                  |
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **OUTRAS GRAVAÇOES**                                                                                                                                                             |
+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 1.  **Update**                                                                                                                                                                   |
+-------------------------------------------------+---------------------------------------------------------------+----------------------------------------------------------------+
| *Faz update nos registos anteriores , ou seja   | *1.2.1 inativa as remuneraçoes dessa mobilidade               | *1.2.2 inativa os descontos dessa mobilidade                   |
| inativa os registos ativos*                     | **RH_T_DEF_REMUNERACAO***                                     | **(RH_T_DEF_PAGAMENTO)***                                      |
|                                                 |                                                               |                                                                |
| *1.2Inativa a mobilidade em estado ativo        | - *DATA_FIM = data Inicio Carreira -1*                        | - *DATA_FIM = data Inicio Carreira -1*                         |
| **RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e  |                                                               |                                                                |
| data fim is not null)***                        | - *USER_ALTERACAO \_ID = utilizador logado*                   | - *USER_ALTERACAO \_ID = utilizador logado*                    |
|                                                 |                                                               |                                                                |
| - *DATA_FIM = data Inicio Carreira -1*          | - *USER_ALTERACAO_NAME = **nome de utilizador***              | - *USER_ALTERACAO_NAME = **nome de utilizador***               |
|                                                 |                                                               |                                                                |
| - *USER_ALTERACAO \_ID = utilizador logado*     | - *DATA_ALTERACAO = **sysdate***                              | - *DATA_ALTERACAO = sysdate*                                   |
|                                                 |                                                               |                                                                |
| - *USER_ALTERACAO_NAME = **nome de              | - *ESTADO = I*                                                | - *ESTADO = I*                                                 |
|   utilizador***                                 |                                                               |                                                                |
|                                                 |                                                               |                                                                |
| - *DATA_ALTERACAO = sysdate*                    |                                                               |                                                                |
|                                                 |                                                               |                                                                |
| - *EST_ACT_ADM = 0*                             |                                                               |                                                                |
+-------------------------------------------------+---------------------------------------------------------------+----------------------------------------------------------------+
| 2.  **Insert**                                                                                                                                                                   |
+-----------------------------------------------------------------+-------------------------------------------------------------------------------+--------------------------------+
| *1-Fazer uma nova gravação na tabela de RH_T_CARREIRA, pegas    | ***3-.** O sistema deve gravar na tabela **RH_T_DEF_REMUNERACAO** as          | *4.3 deve ser feito uma nova   |
| todas informações do registo anterior , e regista com novas     | informações do separador de **subsídio** e 1 registo do **salário***          | associação da tabela           |
| alteraçoes nos campos do formulario e outros seguinte campos*   |                                                                               | **RH_T_TIPOS_RELACIONAMENTO**  |
|                                                                 | ***Nota**: Só deve ser efetuado um novo registo de subsídio em caso de        | e **RH_T_DEF_PAGAMENTO** na    |
| - *DATA_REGISTO= 'SYSDATE'*                                     | alteração do registo anterior.\                                               | TABELA **RH_T_REMUN_TIPREL***  |
|                                                                 | Nessa situação, deve encerrar o registo anterior e criar um novo registo.\    |                                |
| - *USER_REGISTO_ID = id de utilizador Logado*                   | Se se tratar de um subsídio novo, então deve proceder ao registo              | - *PAG_ID = ide de             |
|                                                                 | normalmente..*                                                                |   RH_T_DEF_PAGAMENTO*          |
| - *USER_REGISTO_NAME = nome de utilizador Logado*               |                                                                               |                                |
|                                                                 | ***3.1 separador Subsidio (1 ou varios registos)***                           | - *TIPREL_ID = id de           |
| - *USER_ALTERACAO \_ID = NULL*                                  |                                                                               |   RH_T_TIPOS_RELACIONAMENTO*   |
|                                                                 | - *OBS = 'MOBILIDADE- \|\| TIPO_CARREIRA*                                     |                                |
| - *DATA_INICIO*                                                 |                                                                               | - *ESTADO = P*                 |
|                                                                 | - *ESTADO = '**P**'*                                                          |                                |
| - *USER_ALTERACAO_NAME = NULL*                                  |                                                                               | - *USER_REGISTO_ID = id de     |
|                                                                 | - *USER_REGISTO_ID = id de utilizador Logado*                                 |   utilizador Logado*           |
| - *DATA_ALTERACAO = NULL*                                       |                                                                               |                                |
|                                                                 | - *USER_REGISTO_NAME = nome de utilizador Logado*                             | - *USER_REGISTO_NAME = nome de |
| - *CONTRATO_ID = ID DE TABELA RH_T_CONTRATO_VINCULO*            |                                                                               |   utilizador Logado*           |
|                                                                 | - *USER_ALTERACAO \_ID = **NULL***                                            |                                |
| - *CONTR_VINCULO_ID = ID de tabela RH_T_CONTRATO_VINCULO*       |                                                                               | - *USER_ALTERACAO \_ID =       |
|                                                                 | - *USER_ALTERACAO_NAME = **NULL***                                            |   **NULL***                    |
| - *OBS = 'CARREIRA"*                                            |                                                                               |                                |
|                                                                 | - *DATA_ALTERACAO = **NULL***                                                 | - *USER_ALTERACAO_NAME =       |
| *2-Fazer uma nova gravação na tabela de                         |                                                                               |   **NULL***                    |
| RH_T_TIPOS_RELACIONAMENTO, pegas todas informações onde campo   | - *FUN_ID = id de RH_T_FUNCIONARIOS*                                          |                                |
| est_ult_adm = 1 (faz update est_ult_adm = 0 e data \_fim = data |                                                                               | - *DATA_ALTERACAO = **NULL***  |
| inicio do novo registo) , e regista com novas alteraçoes nos    | - *CARREIRA_ID = ID de RH_T_CARREIRA*                                         |                                |
| campos do formulario e outros seguinte campos*                  |                                                                               | *5-Grava na tabela de          |
|                                                                 | ***3.2 Salario (1 registo)***                                                 | validação --                   |
| - *DATA_REGISTO= 'SYSDATE'*                                     |                                                                               | **RH_T_VALIDACAO***            |
|                                                                 | - *VALOR = Valor do campo Salario do formulario*                              |                                |
| - *USER_REGISTO_ID = id de utilizador Logado*                   |                                                                               | - *TIPO_ACCAO**= 'INSERT '     |
|                                                                 | - *DATA_INICIO = Data inicio de Função do formulario*                         |   (**DOMAINS = TIPO_ACAO**)*** |
| - *USER_REGISTO_NAME = nome de utilizador Logado*               |                                                                               |                                |
|                                                                 | - *DATA_FIM = Data Fim de Função*                                             | - *REFERENCIA_NAME **=         |
| - *USER_ALTERACAO \_ID = NULL*                                  |                                                                               |   'CARREIRA' (**DOMAINS =      |
|                                                                 | - *TM_ID = **FUNÇÃO**:GET_MOVIMENTO_SALL*                                     |   ACCAO_REFERENTE**)***        |
| - *DATA_INICIO*                                                 |                                                                               |                                |
|                                                                 | - *ESTADO = '**P**'*                                                          | - *REFERENCIA_ID **= ID** de   |
| - *USER_ALTERACAO_NAME = NULL*                                  |                                                                               |   tabela **RH_T_MOBILIDADE***  |
|                                                                 | - *USER_REGISTO_ID = id de utilizador Logado*                                 |                                |
| - *DATA_ALTERACAO = NULL*                                       |                                                                               | - *FUN_ID **= ID** de tabela   |
|                                                                 | - *USER_REGISTO_NAME = nome de utilizador Logado*                             |   **RH_T_FUNCIONARIOS***       |
| - *MOB_ID = ID DE RH_T_MOBILIDADE*                              |                                                                               |                                |
|                                                                 | - *USER_ALTERACAO \_ID = **NULL***                                            | - *TIPREL_ID **= ID de         |
| - *EST_ULT_ADM = 1*                                             |                                                                               |   RH_T_TIPOS_RELACIONAMENTO*** |
|                                                                 | - *USER_ALTERACAO_NAME = **NULL***                                            |                                |
| - *ESTADO = 'P'*                                                |                                                                               | - *DATA_REGISTO **= SYSDATE*** |
|                                                                 | - *DATA_ALTERACAO = **NULL***                                                 |                                |
| - *OBS = 'MOBILIDADE- \|\| TIPO_CARREIRA*                       |                                                                               | - *USER_REGISTO_NAME = nome de |
|                                                                 | - *OBS = ''MOBILIDADE- \|\| TIPO_CARREIRA*                                    |   utilizador Logado*           |
| - *REFERENTE = 'CARREIRA*                                       |                                                                               |                                |
|                                                                 | - *FUN_ID = id de RH_T_FUNCIONARIOS*                                          | - *USER_REGISTO_ID = id de     |
|                                                                 |                                                                               |   utilizador Logado*           |
|                                                                 | - *CARREIRA_ID = ID de RH_T_CARREIRA*                                         |                                |
|                                                                 |                                                                               | - *ESTADO **= 'P'***           |
|                                                                 | *3.3deve ser feito nova associação da tabela **RH_T_TIPOS_RELACIONAMENTO** e  |                                |
|                                                                 | **RH_T_DEF_REMUNECACAO** na TABELA **RH_T_REMUN_TIPREL***                     | *5.1- Registo Detalhe de LOG   |
|                                                                 |                                                                               | na tabela                      |
|                                                                 | - *REM_ID = ide de RH_T_DEF_REMUNERACAO*                                      | **RH_T_VALIDACAO_DETALHE***    |
|                                                                 |                                                                               |                                |
|                                                                 | - *TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO*                               | - *VALIDACAO_ID **= id de      |
|                                                                 |                                                                               |   tabela RH_T_VALIDACAO***     |
|                                                                 | - *ESTADO = P*                                                                |                                |
|                                                                 |                                                                               | - *CAMPO_ALTERADO **= nome de  |
|                                                                 | - *USER_REGISTO_ID = id de utilizador Logado*                                 |   campos***                    |
|                                                                 |                                                                               |                                |
|                                                                 | - *USER_REGISTO_NAME = nome de utilizador Logado*                             | - *VALOR_ANTERIOR = valor      |
|                                                                 |                                                                               |   antes*                       |
|                                                                 | - *USER_ALTERACAO \_ID = **NULL***                                            |                                |
|                                                                 |                                                                               | - *VALOR_NOVO = valor depois*  |
|                                                                 | - *USER_ALTERACAO_NAME = **NULL***                                            |                                |
|                                                                 |                                                                               | - *TABELA_NAME = " nome de     |
|                                                                 | - *DATA_ALTERACAO = **NULL***                                                 |   tabela a ser registado"*     |
|                                                                 |                                                                               |                                |
|                                                                 | ***4**.O sistema deve gravar na tabela **RH_DEF_PAGAMENTOS** as informações   | - *TABELA \_ID = "id de tabela |
|                                                                 | do separador de **Encargos / Descontos*** *e 2 registo de **IUR** e **INPS*** |   a ser registado*             |
|                                                                 |                                                                               |                                |
|                                                                 | ***Nota**: Só deve ser efetuado um novo registo de desconto em caso de        | *6-Grava LOG no IGRP*          |
|                                                                 | alteração do registo anterior.\                                               |                                |
|                                                                 | Nessa situação, deve encerrar o registo anterior e criar um novo registo.\    |                                |
|                                                                 | Se se tratar de um subsídio novo, então deve proceder ao registo              |                                |
|                                                                 | normalmente..*                                                                |                                |
|                                                                 |                                                                               |                                |
|                                                                 | ***4.1 Separador Encargos / Descontos***                                      |                                |
|                                                                 |                                                                               |                                |
|                                                                 | - *OBS = 'MOBILIDADE- \|\| TIPO_CARREIRA*                                     |                                |
|                                                                 |                                                                               |                                |
|                                                                 | - *ESTADO = '**P**'*                                                          |                                |
|                                                                 |                                                                               |                                |
|                                                                 | - *USER_REGISTO_ID = id de utilizador Logado*                                 |                                |
|                                                                 |                                                                               |                                |
|                                                                 | - *USER_REGISTO_NAME = nome de utilizador Logado*                             |                                |
|                                                                 |                                                                               |                                |
|                                                                 | - *USER_ALTERACAO \_ID = **NULL***                                            |                                |
|                                                                 |                                                                               |                                |
|                                                                 | - *USER_ALTERACAO_NAME = **NULL***                                            |                                |
|                                                                 |                                                                               |                                |
|                                                                 | - *DATA_ALTERACAO = **NULL***                                                 |                                |
|                                                                 |                                                                               |                                |
|                                                                 | - *CARREIRA_ID = ID de RH_T_CARREIRA*                                         |                                |
|                                                                 |                                                                               |                                |
|                                                                 | - *FUN_ID = ID de RH_T_FUNCIONARIOS*                                          |                                |
+-----------------------------------------------------------------+-------------------------------------------------------------------------------+--------------------------------+

##### Validar Carreira

+-----------------------------------------------------------------------+
| **REGRAS**                                                            |
+-----------------------------------------------------------------------+
| *Ao validar uma carreira deve fazer as seguintes ações:*              |
|                                                                       |
| - *O Campo **Validar** deve ficar visivel*                            |
|                                                                       |
| - *Caso o utilizador valida o registo, logo deve atualizar todos as   |
|   tabelas associdas ao registo de carreira para **estado = 'A'***     |
|                                                                       |
| - *Caso o utilizado não Valida , logo deve atualizar todos as tabelas |
|   associdas ao registo de carreira para **estado = 'I', e atulizar    |
|   OBS = 'Não Validado''***                                            |
+-----------------------------------------------------------------------+

#### Substituição

##### Novo / Editar 

![](media/image28.png){width="9.693055555555556in"
height="5.475694444444445in"}

+:--------------:+:--------------:+:--------------:+:--------------------------------------------:+:------------------------:+:------------------------:+
| **Formulario** | **Tipo**                        | **Descrição**                                | **Gravação**                                        |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| **Dados de     |                                                                                                                                      |
| Substituição** |                                                                                                                                      |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| Validar        | *RADIOLIST*                     | Esse deve ficar oculto, so fica visivel em   | *RH_T_SUBSTITUICAO.ESTADO*                          |
|                |                                 | caso de validação desse Registo. No modo     |                                                     |
|                |                                 | validação esse campo é obrigatorio           |                                                     |
|                |                                 |                                              |                                                     |
|                |                                 | DOMAINS= SIM_NAO                             |                                                     |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| \*Colaborador  | *LOOKUP*                        | Pesquisar o colaborador que o irá substituir | *RH_T_SUBSTITUICAO.SUSBSTITUIDO_TIPREL_ID*          |
| Susbstituido   |                                 | temporariamente, deve pegar o vinculo do     |                                                     |
|                |                                 | colaborador Ativo                            |                                                     |
|                |                                 | (**RH_T_TIPOS_RELACIONAMENTO.EST_ACT_ADM=1** |                                                     |
|                |                                 | ) e que processa salario                     |                                                     |
|                |                                 | (**RH_T_TIPOS_RELACIONAMENTO.FLG_PROCESSSA=  |                                                     |
|                |                                 | 1**)                                         |                                                     |
|                |                                 |                                              |                                                     |
|                |                                 | ***FUNÇÃO**: GET_COLABORADOR_MOBILIDADE*     |                                                     |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| \*Data Inicio  | *DATE*                          | Data inicio de usbstituição                  | *RH_T_SUBSTITUICAO.DATA_INICIO*                     |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| \*Data Fim     |                                 | Data fim de susbtituicao                     | *RH_T_SUBSTITUICAO.DATA_FIM*                        |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| \*Motivo de    | *SELECT*                        | Motivo de susbstituição                      | *RH_T_SUBSTITUICAO.MOTIVO*                          |
| Substituição   |                                 |                                              |                                                     |
|                |                                 | **DOMAIN** = MOTIVO_SUBSTITUICAO_MOB         |                                                     |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| Observação     | *TEXTAREA*                      | Descritivo do motivo de sustituição          | *RH_T_SUBSTITUICAO.OBSERVACAO*                      |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| **Calculo      | *SEPARADOR*                     | caso o colaborador vai susbsituir o outro em mais de um , logo o sistema deve mostrar detalhe de   |
| Remuneração**  |                                 | cada mês o que ele vai recevber                                                                    |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| Mês            | *Varchar*                       | INVOCA A FUNÇAO PARA FAZER O CALCULO DE      | *RH_T_SUBSTITUICAO_DETALHE.MES_ANO*                 |
|                |                                 | SUBSTITUICAO                                 |                                                     |
|                |                                 |                                              |                                                     |
|                |                                 | **PROCESSAMENTO_SALARIAL_DB                  |                                                     |
|                |                                 | .CALCULAR_SUBSTITUICAO**                     |                                                     |
|                |                                 |                                              |                                                     |
|                |                                 | > (P_DATA_DE =\> Data inicio                 |                                                     |
|                |                                 | >                                            |                                                     |
|                |                                 | > P_DATA_ATE =\> Data Fim ,                  |                                                     |
|                |                                 | >                                            |                                                     |
|                |                                 | > P_TIPREL_DE =\> colaborador sustituto ,    |                                                     |
|                |                                 | >                                            |                                                     |
|                |                                 | > P_TIPREL_PARA =\> Colaborador              |                                                     |
|                |                                 | > Susbstituido,                              |                                                     |
|                |                                 | >                                            |                                                     |
|                |                                 | > P_MES_ANO =\> devolve o Mês ,              |                                                     |
|                |                                 | >                                            |                                                     |
|                |                                 | > P_NR_DIAS =\> devolve o Numero de dias ,   |                                                     |
|                |                                 | >                                            |                                                     |
|                |                                 | > P_VALOR_TIPREL_DE =\> devolve o Valor do   |                                                     |
|                |                                 | > sustituto,                                 |                                                     |
|                |                                 | >                                            |                                                     |
|                |                                 | > P_VALOR_TIPREL_PARA =\> devolde o Valor do |                                                     |
|                |                                 | > substituido)                               |                                                     |
+----------------+---------------------------------+                                              +-----------------------------------------------------+
| Numero de dias | *Number*                        |                                              | *RH_T_SUBSTITUICAO_DETALHE.NR_DIAS*                 |
+----------------+---------------------------------+                                              +-----------------------------------------------------+
| Valor do       | *NUMBER*                        |                                              | *RH_T_SUBSTITUICAO_DETALHE.VALOR_DO\_*SUSTITUTO     |
| sustituto      |                                 |                                              |                                                     |
+----------------+---------------------------------+                                              +-----------------------------------------------------+
| Valor do       | *NUMBER*                        |                                              | *RH_T_SUBSTITUICAO_DETALHE.VALOR_DO_SUBSTITUIDO*    |
| subsituido     |                                 |                                              |                                                     |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| **Anexar       |                                 |                                              |                                                     |
| Documento**    |                                 |                                              |                                                     |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| Tipo Documento |                                 | *RH_T_TIPOS_DOCUMENTO.NOME Onde REFERENCIA = | *RH_T_DOCUMENTO.* *TP_DOCUMENTO_ID*                 |
|                |                                 | '**SUBSTITUICAO'***                          |                                                     |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| Documento      |                                 |                                              | *RH_T_DOCUMENTO.DOC_ID*                             |
+----------------+---------------------------------+----------------------------------------------+-----------------------------------------------------+
| **OUTRAS GRAVAÇÕES**                                                                                                                                  |
+---------------------------------+------------------------------------------------------------------------------------------+--------------------------+
| ***1, gravação na tabela de     | *Registo na tabela de validação **RH_T_VALIDACAO***                                      | *3. Registo de Log no    |
| RH_T_SUBSTITUICAO***            |                                                                                          | IGRP*                    |
|                                 | - *TIPO_ACCAO**= 'INSERT' (**DOMAINS = VALIDACAO_TIPO_ACAO**)***                         |                          |
| - *DATA_REGISTO= '**SYSDATE'*** |                                                                                          |                          |
|                                 | - *REFERENCIA_NAME **= 'SUBSTITUICAO' (**DOMAINS = ACCAO_REFERENTE**)***                 |                          |
| - *USER_REGISTO_ID = id de      |                                                                                          |                          |
|   utilizador Logado*            | - *REFERENCIA_ID **= ID** de tabela **RH_T_SUBSTITUICAO***                               |                          |
|                                 |                                                                                          |                          |
| - *USER_REGISTO_NAME = nome de  | - *FUN_ID **= ID** de tabela **RH_T_FUNCIONARIOS***                                      |                          |
|   utilizador Logado*            |                                                                                          |                          |
|                                 | <!-- -->                                                                                 |                          |
| - *USER_ALTERACAO \_ID =        |                                                                                          |                          |
|   **NULL***                     | - *TIPREL_ID = ID DE RH_T_TIPOS RELACIONAMENTO de colaborador que será substutido*       |                          |
|                                 |                                                                                          |                          |
| - *USER_ALTERACAO_NAME =        | <!-- -->                                                                                 |                          |
|   **NULL***                     |                                                                                          |                          |
|                                 | - *DATA_REGISTO **= SYSDATE***                                                           |                          |
| - *DATA_ALTERACAO = **NULL***   |                                                                                          |                          |
|                                 | - *USER_REGISTO_NAME = nome de utilizador Logado*                                        |                          |
| - *ESTADO = "**P**"*            |                                                                                          |                          |
|                                 | - *USER_REGISTO_ID = id de utilizador Logado*                                            |                          |
| - *SUBSTITUTO_TIREPL_ID = ID DE |                                                                                          |                          |
|   RH_T_TIPOS_RELACIONAMENTO     | - *ESTADO **= 'P'***                                                                     |                          |
|   (pega o do colaborador        |                                                                                          |                          |
|   substituto, onde data_fim não |                                                                                          |                          |
|   é nulo E EST_ULT_ADM = 1 e    |                                                                                          |                          |
|   FLG_PROCESSA = 1)*            |                                                                                          |                          |
+---------------------------------+------------------------------------------------------------------------------------------+--------------------------+

###### Validar Substituição

+----------------------------------------------------------------------+
| - A validação invoca a mesma página de Registo                       |
|                                                                      |
| - O campo validar deve ficar visivel                                 |
|                                                                      |
| - Ao **validar**, devem ser atualizadas todas as tabelas associadas, |
|   definindo o campo **estado = \'A\'**.                              |
|                                                                      |
| - Ao **desvalidar**, devem ser atualizadas todas as tabelas          |
|   associadas, definindo o campo **estado = \'I\'**.                  |
|                                                                      |
| - Caso o utilizador **atualize algum campo no formulário**, a        |
|   alteração deve ser **refletida na tabela correspondente**.         |
|                                                                      |
| - Apôs validação é gerado uma ordem de serviço ([ver template a      |
|   seguir](#modelo-de-ordem-serviço-de-subsituicao))                  |
|                                                                      |
| 1.2.-Ao Validar gera um ordem de Serviço na tabela                   |
| **RH_T_ORDEM_SERVICO**                                               |
|                                                                      |
| - DESCRICAO = 'Registo de colaborador - ' \|\|                       |
|   RH_T_FUNCIONARIOS.NOME                                             |
|                                                                      |
| - REFERENTE = 'SUBSTITUICAO'                                         |
|                                                                      |
| - FUN_ID = RH_T_FUNCIONARIOS.ID                                      |
|                                                                      |
| - TIPREL_ID = RH_T_TIPOS_RELACIONAMENTO.ID                           |
|                                                                      |
| - VALIDACAO_ID = RH_T_VALIDACAO.ID                                   |
+----------------------------------------------------------------------+

#######  Modelo de ordem serviço de subsituicao 

+----------------------------------------------------------------------+
| Pegar o template de informacao em **RH_T_TEMPLATE_REPORT** onde      |
| Referencia = **SUBSTITUICAO**                                        |
+----------------------------------------------------------------------+
| > ![](media/image29.png){width="4.229166666666667in"                 |
| > height="4.676388888888889in"}                                      |
+----------------------------------------------------------------------+

##### Lista de substituição

![Uma imagem com texto, captura de ecrã, Tipo de letra, número Os
conteúdos gerados por IA podem estar
incorretos.](media/image30.png){width="9.693055555555556in"
height="2.9618055555555554in"}

+:------------------:+:------------------:+:------------------------------:+:-------------------------------:+
| **Lista**          | **Tipo**           | **Descrição**                  | **Fonte Dados**                 |
+--------------------+--------------------+--------------------------------+---------------------------------+
| Estado             | *TEXT*             |                                | *RH_T_SUBSTITUICAO.ESTADO*      |
+--------------------+--------------------+--------------------------------+---------------------------------+
| Colaborador        | *TEXT*             | **QUERY**:                     | *RH_T_SUBSTITUICAO.*            |
| Substituido        |                    | GET_NOME_COLABORADOR           | *SUBSTITUIDO TIPREL_ID*         |
|                    |                    | (*FUN_ID*)                     |                                 |
+--------------------+--------------------+--------------------------------+---------------------------------+
| Cargo              | *TEXT*             | **QUERY**: *GET_NOME_CARGO*    | *RH_T_TIPOS.RELACIONAMENTO.     |
|                    |                    |                                | CARG_COD_CARGO*                 |
+--------------------+--------------------+--------------------------------+---------------------------------+
| Colabordor         | *TEXT*             | **QUERY**:                     | *RH_T_SUBSTITUICAO.*            |
| Sustituto          |                    | GET_NOME_COLABORADOR(*FUN_ID*) | SUSTITUTO\_*TIPREL_ID*          |
+--------------------+--------------------+--------------------------------+---------------------------------+
| Data Inicio        | *TEXT*             |                                | *RH_T_SUBSTITUICAO.DATA_INICIO* |
+--------------------+--------------------+--------------------------------+---------------------------------+
| Data Fim           | *TEXT*             |                                | *RH_T_SUBSTITUICAO.DATA_FIM*    |
+--------------------+--------------------+--------------------------------+---------------------------------+
| Motivo             | *TEXT*             |                                | *RH_T_SUBSTITUICAO.MOTIVO*      |
+--------------------+--------------------+--------------------------------+---------------------------------+
| Observação         | *TEXT*             |                                | *RH_T_SUBSTITUICAO.OBSERVACAO*  |
+--------------------+--------------------+--------------------------------+---------------------------------+
| **AÇÕES**                                                                                                  |
+--------------------+---------------------------------------------------------------------------------------+
| Ver Detalhe        | *Abre o mesmo Formulario de Registo para Ver Detalhe , para este caso deverá esconder |
|                    | o botão gravar*                                                                       |
+--------------------+---------------------------------------------------------------------------------------+

#### Gestão / Histórico Laboral 

##### Editar / Novo relacao Laboral

![Uma imagem com texto, captura de ecrã, número, Paralelo Os conteúdos
gerados por IA podem estar
incorretos.](media/image31.png){width="7.668055555555555in"
height="4.396527777777778in"}

+:--------------:+:--------------:+:----------------------:+:----------------------:+:-------------------------------------------------------:+:-------------------------------------------------------:+
| **Formulario** | **Tipo**       | **Descrição**                                   | **Gravação**                                                                                                      |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| ***CONTRATO VINCULO***                                                                                                                                                                                |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Validar        | RADIOLIST      | Só aparece em momento de validar                | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--*     |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Ordem serviço  | Select         | **DOMAINS** = ORDEM_SERVICO                     | *RH_T_ORDEM_SERVICO .NOME*                                                                                        |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Contrato       | TEXT           | RH_T_PARAM_CONTRATO.NOME                        | *RH_T_CONTRATO_VINCULO.TP_CONTRATO_ID*                                                                            |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Vinculo        | TEXT           | RH_T_PARAM_VINCULO.NOME                         | *RH_T_CONTRATO_VINCULO.VINCULO_ID*                                                                                |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| **MOBILIDADE** |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Tipo           | SELECT         | ***DOMAINS**=* TIPO_MOV_LABORAL, pega valor de  | *RH_T_MOBILIDADE.TIPO_SITUACAO*                                                                                   |
| Mobilidade     |                | cada campo Referencia = MOBILIDADE              |                                                                                                                   |
|                |                |                                                 | *RH_T_TIPOS_RELACIONAMENTO.TIPO_SITUACAO*                                                                         |
|                |                | ***Nota**: Guarda várias valores em mesmo       |                                                                                                                   |
|                |                | campo*                                          |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Direcção       | SELECT         | INPSSIGOF.INSTITUICOES.nome                     | *RH_T_MOBILIDADE.INSTIT_ID*                                                                                       |
|                |                |                                                 |                                                                                                                   |
|                |                |                                                 | *RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID*                                                                             |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Secção         | SELECT         |                                                 | *RH_T_MOBILIDADE.SECAO_ID*                                                                                        |
|                |                |                                                 |                                                                                                                   |
|                |                |                                                 | *RH_T_TIPOS_RELACIONAMENTO.SECAO_ID*                                                                              |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Data inicio    | DATE           |                                                 | *RH_T_MOBILIDADE.DATA_INICIO*                                                                                     |
| Mobilidade     |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Data Fim       | DATE           |                                                 | *RH_T_MOBILIDADE.DATA_FIM*                                                                                        |
| Mobilidade     |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Local de       | SELECT         | *RH_T_PARAM_LOCAL_TRAB.*.NOME                   | *RH_T_MOBILIDADE.LOCAL_TRAB_ID*                                                                                   |
| Trabalho       |                |                                                 |                                                                                                                   |
|                |                |                                                 | *RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID*                                                                         |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Pais           | TEXT           |                                                 | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--*                                                       |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Ilha           | TEXT           | *RH_T_PARAM_LOCAL_TRAB.ILHA_ID,*                | *\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--*                                                       |
|                |                |                                                 |                                                                                                                   |
|                |                | *SIPSGLOBAL.GLB_GEOGRAFIA*                      |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Cargo          | SELECT         |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| **CARREIRA**   |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Tipo Alteração | SELECT         | ***DOMAINS**=* TIPO_MOV_LABORAL, pega valor de  | *RH_T_CARREIRAE.TIPO_SITUACAO*                                                                                    |
| Carreira       |                | cada campo Referencia = **CARREIRA**            |                                                                                                                   |
|                |                |                                                 | *RH_T_TIPOS_RELACIONAMENTO.TIPO_SITUACAO*                                                                         |
|                |                | ***Nota**: Guarda várias valores em mesmo       |                                                                                                                   |
|                |                | campo*                                          |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Carreira       | SELECT         | **RH_T_PARAM_CARREIRA.**NOME                    | *RH_T_CARREIRA.CARR_PCCS_ID*                                                                                      |
|                |                |                                                 |                                                                                                                   |
|                |                |                                                 | *RH_T_TIPOS_RELACIONAMENTO. CARR_PCCS_ID*                                                                         |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Categoria      | SELECT         |                                                 | *RH_T_CARREIRA.* *CATEGORIA_ID*                                                                                   |
|                |                |                                                 |                                                                                                                   |
|                |                |                                                 | *RH_T_TIPOS_RELACIONAMENTO.CATEGORIA_ID*                                                                          |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Escalão        | SELECT         | **RH_T_PARAM_ESCALAO**.NIVEL_REFERENCIA\|\|     | *RH_T_CARREIRA.* *ESCALAO_ID*                                                                                     |
|                |                | **RH_T_PARAM_ESCALAO.** ESCALAO                 |                                                                                                                   |
|                |                |                                                 | *RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID*                                                                            |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Data Inicio    | DATE           |                                                 | *RH_T_CARREIRA.DATA_INICIO*                                                                                       |
| Carreira       |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Data Fim       | DATE           |                                                 | *RH_T_CARREIRA.DATA_FIM*                                                                                          |
| Carreira       |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| **SITUACAO     |                |                                                 |                                                                                                                   |
| LABORAL**      |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Situação       | SELECT         | RH_T_PARAM_SITUACAO.NOME                        | *RH_T_SITUACAO_LABORAL.SITUACAO_LABORAL_ID*                                                                       |
| Laboral        |                |                                                 |                                                                                                                   |
|                |                | Nota: o combo deve vir preenchido somente com   |                                                                                                                   |
|                |                | informacao de situação laboral, ou seja         |                                                                                                                   |
|                |                | ***RH_T_PARAM_SITUACAO**. FLG_SITUACAO_LABORAL  |                                                                                                                   |
|                |                | = 1*                                            |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Motivo         | SELECT         | RH_T_PARAM_SITUACAO_DET. *MOTIVO*               | *RH_T_SITUACAO_LABORAL.* *MOTIVO_SIT_LAB_ID*                                                                      |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Data Inicio    | DATE           |                                                 | *RH_T_SITUACAO_LABORAL.DATA_INICIO*                                                                               |
| Situação       |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Data Fim       | DATE           |                                                 | *RH_T_SITUACAO_LABORAL.DATA_FIM*                                                                                  |
| Situação       |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Observação     | TEXTAREA       |                                                 | *RH_T_SITUACAO_LABORAL.OBS*                                                                                       |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| ***ACÕES***                                                                                                                                                                                           |
+----------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Editar         |                                                                                                                                                                                      |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| Regime         |                |                                                 |                                                                                                                   |
| Trabalho       |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| **REGRAS**     |                |                                                 |                                                                                                                   |
+----------------+----------------+-------------------------------------------------+-------------------------------------------------------------------------------------------------------------------+
| 1\. Botão Editar:                                                                                                                                                                                     |
|                                                                                                                                                                                                       |
| ***1. Caso o Registo já tenha um processamento (RH_T_TIPOS_RELACIONAMENTO.FLG_PROCESSAMENTO não é nulo)***                                                                                            |
|                                                                                                                                                                                                       |
| - *Para verificar se o RH_T_TIPOS_RELACIONAMENTO já tem um registo, deve-se ver se o flg_processamento não é nulo. Nesse caso ao actualizar um campo de carreira ou mobilidade e situação Laboral,    |
|   deve-se fazer um novo registo. Para este caso não faz um novo registo, mas sim atualiza os dados nas tabelas (**RH_T_MOBILIADE**, **RH_T_CARREIRA**, **RH_T_SITUACAO_LABORAL**,                     |
|   **RH_T_TIPOS_RELACIONAMENTO**)*                                                                                                                                                                     |
|                                                                                                                                                                                                       |
| ***1.2- Caso o Registo ainda não tenha nenhum Processamento***                                                                                                                                        |
|                                                                                                                                                                                                       |
| - ***Ao alterar uma mobilidade***                                                                                                                                                                     |
|                                                                                                                                                                                                       |
|   - *Deve fazer um novo registo na tabela **RH_T_MOBILIDADE***                                                                                                                                        |
|                                                                                                                                                                                                       |
| - ***Ao Alterar uma carreira***                                                                                                                                                                       |
|                                                                                                                                                                                                       |
|   - *Deve fazer um novo registo na tabela **RH_T_CARREIRA***                                                                                                                                          |
|                                                                                                                                                                                                       |
| - ***Ao alterar uma situacao Laboral***                                                                                                                                                               |
|                                                                                                                                                                                                       |
|   - *Deve-se fazer um novo registo na tabela **RH_T_SITUACAO_LABORAL***                                                                                                                               |
|                                                                                                                                                                                                       |
| - *Faz um novo registo na tabela **RH_T_TIPOS_RELACIONAMENTO***                                                                                                                                       |
|                                                                                                                                                                                                       |
| - *FAZ NOVO REGISTO NA TABELA **RH_T_VALIDACAO***                                                                                                                                                     |
+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| OUTRAS GRAVAÇÕES                                                                                                                                                                                      |
+----------------------------------------------------------+----------------------------------------------------------------------------------+---------------------------------------------------------+
| *1---Caso For Alterado Mobilidade ,*                     | *3\-- Caso a situacao Laboral For alterado Guarda na tabela                      | *3---Regista na tabela de validacao **RH_T_VALIDACAO*** |
|                                                          | **RH_T_SITUACAO_LABORAL***                                                       |                                                         |
| *2---caso for alterado Carreira guarda na                |                                                                                  |                                                         |
| **RH_T_CARREIRA***                                       |                                                                                  |                                                         |
|                                                          |                                                                                  |                                                         |
| *2.2 Caso for alterado salario, logo fecha o registo     |                                                                                  |                                                         |
| anterior e faz novo registo em **RH_T_DEF_REMUNERACAO*** |                                                                                  |                                                         |
+----------------------------------------------------------+----------------------------------------------------------------------------------+---------------------------------------------------------+

##### Lista Relação Laboral

![Uma imagem com texto, número, Tipo de letra, captura de ecrã Os
conteúdos gerados por IA podem estar
incorretos.](media/image32.png){width="9.693055555555556in"
height="3.4618055555555554in"}

+:-------------:+:--------:+:------------------------:+:-----------------------------------------------:+
| **Lista**     | **Tipo** | **Descrição**            | **Fonte de dados**                              |
+---------------+----------+--------------------------+-------------------------------------------------+
| Situação      |          |                          | RH_T_TIPOS_RELACIONAMENTO.EST\_                 |
| Atual         |          |                          |                                                 |
+---------------+----------+--------------------------+-------------------------------------------------+
| vinculo       |          | RH_T_PARAM_VINCULO.NOME  | *RH_T_CONTRATO_VINCULO.VINCULO_ID*              |
+---------------+----------+--------------------------+-------------------------------------------------+
| Direção       |          |                          | ***RH_T_MOBILIDE.**INSTIT_ID*                   |
+---------------+----------+--------------------------+-------------------------------------------------+
| Seccão        |          |                          | ***RH_T_MOBILIDE.**SECAO_ID*                    |
+---------------+----------+--------------------------+-------------------------------------------------+
| Carreira      |          |                          | ***RH_T_CARREIRA.**CARR_PCCS_ID*                |
+---------------+----------+--------------------------+-------------------------------------------------+
| Categoria     |          |                          | ***RH_T_CARREIRA.**CATEGORIA_ID*                |
+---------------+----------+--------------------------+-------------------------------------------------+
| Data Inicio   |          |                          | ***RH_T_CONTRATO_VINCULO.**DATA_INICIO **\|\|   |
| Contrato /    |          |                          | RH_T_CARREIRA.**DATA_INICIO*                    |
| Carreira      |          |                          |                                                 |
+---------------+----------+--------------------------+-------------------------------------------------+
| Data Fim      |          |                          | ***RH_T_CONTRATO_VINCULO.**DATA_FIM **\|\|      |
| Contrato /    |          |                          | RH_T_CARREIRA.**DATA_FIM*                       |
| Carreira      |          |                          |                                                 |
+---------------+----------+--------------------------+-------------------------------------------------+
| Situação      |          | RH_T_PARAM_SITUACAO.NOME | ***RH_T_SITUACAO_LABORAL.SITUACAO_LABORAL_ID*** |
| Laboral       |          |                          |                                                 |
+---------------+----------+--------------------------+-------------------------------------------------+
| **Ações**     |          |                                                                            |
+---------------+----------+----------------------------------------------------------------------------+
| Novo / Editar |          |                                                                            |
+---------------+----------+----------------------------------------------------------------------------+
| Regime        |          |                                                                            |
| Emprego       |          |                                                                            |
+---------------+----------+----------------------------------------------------------------------------+

##### Historico Laboral

A lista deve apresentar somente dados ativos.

![Uma imagem com texto, captura de ecrã, número, software Os conteúdos
gerados por IA podem estar
incorretos.](media/image33.png){width="9.693055555555556in"
height="5.229166666666667in"}

+:------------------:+:------------------:+:------------------------:+:------------------------:+
| **FIltro**         | **Tipo**           | **Descrição**            | **Fonte de dados**       |
+--------------------+--------------------+--------------------------+--------------------------+
| Referencia         | *SELECT*           | **DOMAINS** =            | *RH_V_HIST_LABORAL.      |
|                    |                    | TIPO_MOV_LABORAL*, pega  | REFERENCIA*              |
|                    |                    | somente Referencia*      |                          |
+--------------------+--------------------+--------------------------+--------------------------+
| Tipo Situação      | *SELECT*           | ***DOMAINS**=*           | *RH_V_HIST_LABORAL       |
|                    |                    | TIPO_MOV_LABORAL, pega   | .TIPO_SITUACAO*          |
|                    |                    | valor de cada campo      |                          |
|                    |                    | Referencia               |                          |
+--------------------+--------------------+--------------------------+--------------------------+
| Data Inicio        | *DATE*             |                          | *RH_V_HIST_LABORAL       |
|                    |                    |                          | .DATA_INICIO*            |
+--------------------+--------------------+--------------------------+--------------------------+
| Data Fim           | *DATE*             |                          | *RH_V_HIST_LABORAL       |
|                    |                    |                          | .DATA_FIM*               |
+--------------------+--------------------+--------------------------+--------------------------+
| **Lista**          | **Tipo**           | **Fonte de dados**                                  |
+--------------------+--------------------+-----------------------------------------------------+
| Ultimo Movimento   | *TEXT*             | RH_V_HIST_LABORAL.ULTIMO_VINCULO                    |
+--------------------+--------------------+-----------------------------------------------------+
| Tipo Situação      | *TEXT*             | RH_V_HIST_LABORAL. TIPO_SITUACAO_DESC               |
+--------------------+--------------------+-----------------------------------------------------+
| Tipo contrato      | *TEXT*             | RH_V_HIST_LABORAL.*TIPO_CONTRATO_DESC*              |
+--------------------+--------------------+-----------------------------------------------------+
| Vinculo            | *TEXT*             | RH_V_HIST_LABORAL.*VINCULO_DESC*                    |
+--------------------+--------------------+-----------------------------------------------------+
| Direção            | *TEXT*             | RH_V_HIST_LABORAL . *DIRECAO_DESC*                  |
+--------------------+--------------------+-----------------------------------------------------+
| Seção              | *TEXT*             | RH_V_HIST_LABORAL . *SECCAO_DESC*                   |
+--------------------+--------------------+-----------------------------------------------------+
| Referencia /       | *TEXT*             | RH_V_HIST_LABORAL . *REFERENCIA_ESCALAO_DESC*       |
| Escalão            |                    |                                                     |
+--------------------+--------------------+-----------------------------------------------------+
| Cargo              | *TEXT*             | RH_V_HIST_LABORAL. *CARGO_DESC*                     |
+--------------------+--------------------+-----------------------------------------------------+
| Carreira           | *TEXT*             | RH_V_HIST_LABORAL.*CARREIRA_DESC*                   |
+--------------------+--------------------+-----------------------------------------------------+
| Situação Laboral   | *TEXT*             | RH_V_HIST_LABORAL . SITUACAO_LABORAL                |
+--------------------+--------------------+-----------------------------------------------------+
| Data Inicio / Data | *TEXT*             | RH_V_HIST_LABORAL . *DATA_INICIO /*                 |
| Fim                |                    | RH_V_HIST_LABORAL . *DATA_FIM*                      |
+--------------------+--------------------+-----------------------------------------------------+
| **Ações**                                                                                     |
+--------------------+--------------------------------------------------------------------------+
| Registar Vinculo   | Abre formulário para o registo de um novo vinculo Laboral                |
+--------------------+--------------------------------------------------------------------------+
| Editar Vinculo     | Abre o mesmo formulario de registo                                       |
+--------------------+--------------------------------------------------------------------------+
| Eliminar           | Permite eliminar os dados registados                                     |
+--------------------+--------------------------------------------------------------------------+
| Alterar Situaçao   | Abre o formulario para editar situação Laboral de um colaborador         |
| Laboral            |                                                                          |
+--------------------+--------------------------------------------------------------------------+
| Alterar Regime     | Abre o formulario para alterar regime de um colaborador                  |
+--------------------+--------------------------------------------------------------------------+

##### ~~Alterar Situação Laboral -~~ descontinuado

![Uma imagem com texto, captura de ecrã, número, software Os conteúdos
gerados por IA podem estar
incorretos.](media/image34.png){width="9.693055555555556in"
height="4.314583333333333in"}

+:------------------:+:----------------:+:----------------:+:-----------------------:+:-----------------------------------------------------------------------------------------------------------------------------:+
| **~~Formulario~~** | **~~Tipo~~**     | **~~Descrição~~**                          | **~~Gravação~~**                                                                                                              |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~Validar~~        | *~~RADIOLIST~~*  | ~~Esse deve ficar oculto, so fica visivel  | *~~RH_T_SITUACAO_LABORAL.ESTADO~~*                                                                                            |
|                    |                  | em caso de validação desse Registo. No     |                                                                                                                               |
|                    |                  | modo validação esse campo é obrigatorio~~  |                                                                                                                               |
|                    |                  |                                            |                                                                                                                               |
|                    |                  | ~~DOMAINS= SIM_NAO~~                       |                                                                                                                               |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~Gerar Ordem      |                  | ~~Esse deve ficar oculto, so fica visivel  | *~~\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\--~~* |
| Serviço~~          |                  | em caso de validação desse Registo. No     |                                                                                                                               |
|                    |                  | modo validação esse campo é obrigatorio~~  |                                                                                                                               |
|                    |                  |                                            |                                                                                                                               |
|                    |                  | ~~DOMAINS= SIM_NAO~~                       |                                                                                                                               |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~Tipo ordem       | *~~Select~~*     | ~~**DOMAINS** = ORDEM_SERVICO~~            | ~~*RH_T_ORDEM_SERVICO.* motivo- ' \|\| RH_T_FUNCIONARIOS.NOME~~                                                               |
| serviço~~          |                  |                                            |                                                                                                                               |
|                    |                  |                                            | ~~*RH_T_ORDEM_SERVICO.* REFERENTE = ´STUACAO_LABORAL~~                                                                        |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~Vinculo~~        |                  | ~~Traz por defeiro o vinculo de            |                                                                                                                               |
|                    |                  | RH_T_TIPOS_RELACIONAMENTO de est_act_adm   |                                                                                                                               |
|                    |                  | «1~~                                       |                                                                                                                               |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~\*situação       | *~~SELECT~~*     | *~~Tabela : RH_T_PARAM_SIT_LABORAL ,       | *~~RH_T_SITUACAO_LABORAL. SITUACAO_LABORAL_ID~~*                                                                              |
| Laboral~~          |                  | RH_T_PARAM_SITUACAO_DET~~*                 |                                                                                                                               |
|                    |                  |                                            |                                                                                                                               |
|                    |                  | ~~(traz os tipo de situacao associado ao   |                                                                                                                               |
|                    |                  | vinculo)~~                                 |                                                                                                                               |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~\*Motivo~~       | *~~SELECT~~*     | ~~**DOMAINS** = MOTIVO_SIT_LABORAL e~~     | *~~RH_T_SITUACAO_LABORAL. MOTIVO_SIT_LAB~~*                                                                                   |
|                    |                  |                                            |                                                                                                                               |
|                    |                  | ~~(traz somente os vinculo associados ao   | *~~RH_T_TIPOS_RELACIONAMENTO. MOTIVO_SIT_LAB~~*                                                                               |
|                    |                  | motivo)~~                                  |                                                                                                                               |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~\*Data Inicio~~  |                  |                                            | *~~RH_T_SITUACAO_LABORAL.DATA_INICIO~~*                                                                                       |
|                    |                  |                                            |                                                                                                                               |
|                    |                  |                                            | *~~RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO~~*                                                                                   |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~Data Fim~~       | *~~DATE~~*       | ~~Data fim de contrato~~                   | *~~RH_T_SITUACAO_LABORAL.DATA_FIM~~*                                                                                          |
|                    |                  |                                            |                                                                                                                               |
|                    |                  |                                            | *~~RH_T_TIPOS_RELACIONAMENTO.DATA_FIM~~*                                                                                      |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~Observação~~     |                  |                                            | *~~RH_T_SITUACAO_LABORAL. OBS~~*                                                                                              |
|                    |                  |                                            |                                                                                                                               |
|                    |                  |                                            | *~~RH_T_TIPOS_RELACIONAMENTO. OBS~~*                                                                                          |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| ~~Hitorico situçao |                  | *~~Traz todos os registos                  |                                                                                                                               |
| Laboral~~          |                  | RH_T_SITUACAO_LABORAL que não estejam      |                                                                                                                               |
|                    |                  | eliminados~~*                              |                                                                                                                               |
+--------------------+------------------+--------------------------------------------+-------------------------------------------------------------------------------------------------------------------------------+
| **~~OUTRAS GRAVAÇOES~~**                                                                                                                                                                                           |
+----------------------------------------------------------+---------------------------------------------------------------------------------------------------------------------------------------------------------+
| *~~1.Faz update nos registos anteriores , ou seja        | *~~2-insert em **RH_T_SITUACAO_LABORAL**~~*                                                                                                             |
| inativa os registos ativos~~*                            |                                                                                                                                                         |
|                                                          | - *~~DATA_REGISTO= '**SYSDATE'**~~*                                                                                                                     |
| *~~1.1Inativa a mobilidade em estado ativo               |                                                                                                                                                         |
| **RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e data fim  | - *~~USER_REGISTO_ID = id de utilizador Logado~~*                                                                                                       |
| is not null)**~~*                                        |                                                                                                                                                         |
|                                                          | - *~~USER_REGISTO_NAME = nome de utilizador Logado~~*                                                                                                   |
| - *~~DATA_FIM = data Inicio~~*                           |                                                                                                                                                         |
|                                                          | - *~~USER_ALTERACAO \_ID = **NULL**~~*                                                                                                                  |
| - *~~USER_ALTERACAO \_ID = utilizador logado~~*          |                                                                                                                                                         |
|                                                          | - *~~USER_ALTERACAO_NAME = **NULL**~~*                                                                                                                  |
| - *~~USER_ALTERACAO_NAME = **nome de utilizador**~~*     |                                                                                                                                                         |
|                                                          | - *~~DATA_ALTERACAO = **NULL**~~*                                                                                                                       |
| - *~~DATA_ALTERACAO = sysdate~~*                         |                                                                                                                                                         |
|                                                          | - *~~ESTADO = "**P**"~~*                                                                                                                                |
| - *~~EST_ACT_ADM = 0~~*                                  |                                                                                                                                                         |
|                                                          | - *~~FUN_ID = id de RH_T_FUNCIONARIOS~~*                                                                                                                |
| *~~1.2 Fazer uma nova gravação na tabela de              |                                                                                                                                                         |
| **RH_T_TIPOS_RELACIONAMENTO**, pegas todas informações   | - *~~CONTR_VINCULO_ID = ID DE RH_T_CONTRATO_VINCULO~~*                                                                                                  |
| do registo anterior , e regista com novas alteraçoes nos |                                                                                                                                                         |
| campos do formulario e outros seguinte campos~~*         | *~~2.2-update **RH_T_CONTRATO_VINCULO.SITUACAO_LABORAL**~~*                                                                                             |
|                                                          |                                                                                                                                                         |
| - *~~DATA_REGISTO= 'SYSDATE'~~*                          | *~~3-Caso o Caso o tipo de situação for 'Cessar', logo deve faz update nas seguintes tabelas :~~*                                                       |
|                                                          |                                                                                                                                                         |
| - *~~USER_REGISTO_ID = id de utilizador Logado~~*        | - *~~RH_T_CONTRATO_VINCULO.DATA_FIM~~*                                                                                                                  |
|                                                          |                                                                                                                                                         |
| - *~~USER_REGISTO_NAME = nome de utilizador Logado~~*    | - *~~RH_T_TIPOS_RELACIONAMENTO.DATA_FIM~~*                                                                                                              |
|                                                          |                                                                                                                                                         |
| - *~~USER_ALTERACAO \_ID = NULL~~*                       | - *~~RH_T_DEF_REMUNERACAO.DATA_FIM~~*                                                                                                                   |
|                                                          |                                                                                                                                                         |
| - *~~DATA_INICIO~~*                                      | - *~~RH_T_CARREIRA.DATA_FIM~~*                                                                                                                          |
|                                                          |                                                                                                                                                         |
| - *~~USER_ALTERACAO_NAME = NULL~~*                       | - *~~RH_T_MOBILIDADE.DATA_FIM~~*                                                                                                                        |
|                                                          |                                                                                                                                                         |
| - *~~DATA_ALTERACAO = NULL~~*                            | - *~~RH_T_DEF_PAGAMENTO.DATA_FIM~~*                                                                                                                     |
|                                                          |                                                                                                                                                         |
| - *~~TIPREL_ID = ID DO REGISTO FECHADO (id de            | *~~4-Registo em **RH_T_AVALIACAO**~~*                                                                                                                   |
|   RH_TIPOS_RELAIONAMENTO)~~*                             |                                                                                                                                                         |
|                                                          | *~~5.Registo em **RH_T_LOG**, **RH_T_VALIDACAO_DETALHE**~~*                                                                                             |
| - *~~SITUACAO_LAB_ID = ID DE SITUACAO LABORAL~~*         |                                                                                                                                                         |
|                                                          |                                                                                                                                                         |
| - *~~EST_ACT_ADM = 1~~*                                  |                                                                                                                                                         |
|                                                          |                                                                                                                                                         |
| - *~~REFERENTE = 'SITUACAO_LABORAL'~~*                   |                                                                                                                                                         |
|                                                          |                                                                                                                                                         |
| - ~~*TIPO_SITUACAO = ´* MUDANCA_SITUACAO_LAB *´*~~       |                                                                                                                                                         |
|                                                          |                                                                                                                                                         |
| - *~~ESTADO = 'P'~~*                                     |                                                                                                                                                         |
+----------------------------------------------------------+---------------------------------------------------------------------------------------------------------------------------------------------------------+

###### Validacao

+----------------------------------------------------------------------+
| - A validação invoca a mesma página de Registo                       |
|                                                                      |
| - O campo validar deve ficar visivel                                 |
|                                                                      |
| - Ao **validar**, devem ser atualizadas todas as tabelas associadas, |
|   definindo o campo **estado = \'A\'**.                              |
|                                                                      |
| - Ao **desvalidar**, devem ser atualizadas todas as tabelas          |
|   associadas, definindo o campo **estado = \'I\'**.                  |
|                                                                      |
| - Caso o utilizador **atualize algum campo no formulário**, a        |
|   alteração deve ser **refletida na tabela correspondente**.         |
|                                                                      |
| - Apôs validação é gerado uma ordem de serviço                       |
|                                                                      |
| 1.2.-Ao Validar gera um ordem de Serviço na tabela                   |
| **RH_T_ORDEM_SERVICO**                                               |
|                                                                      |
| - DESCRICAO = motivo- ' \|\| RH_T_FUNCIONARIOS.NOME                  |
|                                                                      |
| - REFERENTE = ´STUACAO_LABORAL´                                      |
|                                                                      |
| - FUN_ID = RH_T_FUNCIONARIOS.ID                                      |
|                                                                      |
| - TIPREL_ID = RH_T_TIPOS_RELACIONAMENTO.ID                           |
|                                                                      |
| - VALIDACAO_ID = RH_T_VALIDACAO.ID                                   |
+----------------------------------------------------------------------+

##### Regime Trabalho 

######  Alterar regime trabalho

![Uma imagem com texto, captura de ecrã, número, Tipo de letra Os
conteúdos gerados por IA podem estar
incorretos.](media/image35.png){width="9.693055555555556in"
height="3.338888888888889in"}

+:---------------------------------------------------------------------------:+:------------------:+:--------------------------------:+:----------------------------------:+
| **form**                                                                    | **Tipo**           | **Descrição**                    | **GRAVAÇÃO**                       |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| Validar                                                                     | *RADIOLIST*        | Esse deve ficar oculto, so fica  | *RH_T_REGIME_TRAB.ESTADO*          |
|                                                                             |                    | visivel em caso de validação     |                                    |
|                                                                             |                    | desse Registo. No modo validação |                                    |
|                                                                             |                    | esse campo é obrigatorio         |                                    |
|                                                                             |                    |                                  |                                    |
|                                                                             |                    | DOMAINS= SIM_NAO                 |                                    |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| \-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-\-- | *SELECT*           | Pega o ultimo Vinculo do         | *RH_T_REGIME_TRAB.FUN_ID*          |
|                                                                             |                    | colaborador , e o contrato atual |                                    |
|                                                                             |                    |                                  | *RH_T_REGIME_TRAB.CONTRATO_ID*     |
|                                                                             |                    | ***Nota**: O **regime de         |                                    |
|                                                                             |                    | trabalho** está associado ao     |                                    |
|                                                                             |                    | **contrato do colaborador**.\    |                                    |
|                                                                             |                    | No entanto, é possível guardar   |                                    |
|                                                                             |                    | informações adicionais de        |                                    |
|                                                                             |                    | contexto --- por exemplo, **em   |                                    |
|                                                                             |                    | que unidade, secção ou local o   |                                    |
|                                                                             |                    | colaborador estava a exercer     |                                    |
|                                                                             |                    | funções** no momento em que o    |                                    |
|                                                                             |                    | regime foi aplicado. È possivel  |                                    |
|                                                                             |                    | em casos ectermos que um         |                                    |
|                                                                             |                    | colaborador tenha dois           |                                    |
|                                                                             |                    | regime(covid)*                   |                                    |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| \`\*Tipo Regime                                                             | *MULTISELECT*      | **DOMAIN** = REGIME_TRABALHO     | *RH_T_REGIME_TRAB.TIPO_REGIME*     |
|                                                                             |                    |                                  |                                    |
|                                                                             |                    |                                  | *RH_T_TIPOS_RELACIONAMENTO.REGIME* |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| \*Data Inicio                                                               | *DATE*             |                                  | *RH_T_REGIME_TRAB.DATA_INICIO*     |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| \*Data Fim                                                                  | *DATE*             |                                  | *RH_T_REGIME_TRAB.DATA_FIM*        |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| Estado                                                                      | *SELECT*           | Deve ficar Visivel somente na    | RH_T_REGIME_TRAB*.ESTADO*          |
|                                                                             |                    | Edição                           |                                    |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
|                                                                             |                    |                                  |                                    |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| \*Modalidade                                                                | *SELECT*           | Por defeito vem preencho.        | RH_T_REGIME_MODAL*.MODALIDADE*     |
|                                                                             |                    |                                  |                                    |
|                                                                             |                    | **DOMAINS** = MODALIDADE_REGIME  |                                    |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| Dias Semana                                                                 | *Select*           | **DOMAINS** = DIAS_SEMANA        | RH_T_REGIME_MODAL.DIAS_SEMANA      |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| Numero Horas                                                                | *Number*           |                                  | RH_T_REGIME_MODAL.NUM_HORAS        |
+-----------------------------------------------------------------------------+--------------------+----------------------------------+------------------------------------+
| **REGRAS**                                                                                                                                                               |
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| - *Validar Campos Obrigatorios*                                                                                                                                          |
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **OUTRA GRAVAÇOES**                                                                                                                                                      |
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **UPDATE**                                                                                                                                                               |
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| *Faz update nos registos anteriores , ou seja inativa os registos ativos*                                                                                                |
|                                                                                                                                                                          |
| *1.2Inativa a mobilidade em estado ativo **RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e data fim is not null)***                                                         |
|                                                                                                                                                                          |
| - *DATA_FIM = data Inicio Carreira -1*                                                                                                                                   |
|                                                                                                                                                                          |
| - *USER_ALTERACAO \_ID = utilizador logado*                                                                                                                              |
|                                                                                                                                                                          |
| - *USER_ALTERACAO_NAME = **nome de utilizador***                                                                                                                         |
|                                                                                                                                                                          |
| - *DATA_ALTERACAO = sysdate*                                                                                                                                             |
|                                                                                                                                                                          |
| - *EST_ACT_ADM = 0*                                                                                                                                                      |
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| **INSERT**                                                                                                                                                               |
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 1.  **Registar**                                                                                                                                                         |
|                                                                                                                                                                          |
|     1.  O sistema deve registar os dados introduzidos no formulário para a tabelas *RH_T_REGIME_TRAB*, bem como os campos adicionais especificados a seguir              |
|                                                                                                                                                                          |
| - *ESTADO **= 'A'***                                                                                                                                                     |
|                                                                                                                                                                          |
| - *DATA_REGISTO= '**SYSDATE'***                                                                                                                                          |
|                                                                                                                                                                          |
| - *USER_REGISTO_ID = id de utilizador Logado*                                                                                                                            |
|                                                                                                                                                                          |
| - *USER_REGISTO_NAME =nome de utilizador Logado*                                                                                                                         |
|                                                                                                                                                                          |
| - *USER_ALTERACAO \_ID = **NULL***                                                                                                                                       |
|                                                                                                                                                                          |
| - *DATA_ALTERACAO = **NULL***                                                                                                                                            |
|                                                                                                                                                                          |
| - *DATA_ALTERACAO_NAME = **NULL***                                                                                                                                       |
|                                                                                                                                                                          |
|   1.  O sistema deve registar os dados introduzidos no formulário para a tabelas *RH_T_REGIME_MODAL*, bem como os campos adicionais especificados a seguir               |
|                                                                                                                                                                          |
| - *ESTADO **= 'A'***                                                                                                                                                     |
|                                                                                                                                                                          |
| - *DATA_REGISTO= '**SYSDATE'***                                                                                                                                          |
|                                                                                                                                                                          |
| - *USER_REGISTO_ID = id de utilizador Logado*                                                                                                                            |
|                                                                                                                                                                          |
| - *USER_REGISTO_NAME =nome de utilizador Logado*                                                                                                                         |
|                                                                                                                                                                          |
| - *USER_ALTERACAO \_ID = **NULL***                                                                                                                                       |
|                                                                                                                                                                          |
| - *DATA_ALTERACAO = **NULL***                                                                                                                                            |
|                                                                                                                                                                          |
| - *DATA_ALTERACAO_NAME = **NULL***                                                                                                                                       |
|                                                                                                                                                                          |
| - *REGIME_ID = ID DE RH_T_REGIME_TRAB*                                                                                                                                   |
|                                                                                                                                                                          |
|   1.  *~~Fazer uma nova gravação na tabela de RH_T_TIPOS_RELACIONAMENTO, pegas todas informações onde campo est_ult_adm = 1 (faz update est_ult_adm = 0 ) , e regista    |
|       com novas alteraçoes nos campos do formulario e outros seguinte campos~~*                                                                                          |
|                                                                                                                                                                          |
| <!-- -->                                                                                                                                                                 |
|                                                                                                                                                                          |
| - *[~~DATA_REGISTO= 'SYSDATE'~~]{.mark}*                                                                                                                                 |
|                                                                                                                                                                          |
| - *[~~USER_REGISTO_ID = id de utilizador Logado~~]{.mark}*                                                                                                               |
|                                                                                                                                                                          |
| - *[~~USER_REGISTO_NAME = nome de utilizador Logado~~]{.mark}*                                                                                                           |
|                                                                                                                                                                          |
| - *[~~USER_ALTERACAO \_ID = NULL~~]{.mark}*                                                                                                                              |
|                                                                                                                                                                          |
| - *[~~DATA_INICIO~~]{.mark}*                                                                                                                                             |
|                                                                                                                                                                          |
| - *[~~USER_ALTERACAO_NAME = NULL~~]{.mark}*                                                                                                                              |
|                                                                                                                                                                          |
| - *[~~DATA_ALTERACAO = NULL~~]{.mark}*                                                                                                                                   |
|                                                                                                                                                                          |
| - *[~~REGIME_ID = ID DE RH_T_REGIME~~]{.mark}*                                                                                                                           |
|                                                                                                                                                                          |
| - *[~~EST_ULT_ADM = 1~~]{.mark}*                                                                                                                                         |
|                                                                                                                                                                          |
| - *[~~ESTADO = 'P'~~]{.mark}*                                                                                                                                            |
|                                                                                                                                                                          |
| - [~~*TIPO_SITUACAO = '*MUDANCA_REGIME*'*~~]{.mark}                                                                                                                      |
|                                                                                                                                                                          |
| - *[~~TIPO_MOV_LABORAL = 'REGIME"~~]{.mark}*                                                                                                                             |
|                                                                                                                                                                          |
| - *[~~OBS = 'REGIME- \|\| TIPO_REGIME´'~~]{.mark}*                                                                                                                       |
|                                                                                                                                                                          |
| - *[~~REFERENTE = 'REGIME'~~]{.mark}*                                                                                                                                    |
|                                                                                                                                                                          |
|   1.  *Faz update na tabela RH_TIPOS_RELACIONAMENTO*                                                                                                                     |
|                                                                                                                                                                          |
| <!-- -->                                                                                                                                                                 |
|                                                                                                                                                                          |
| - *REGIME_ID = NOVO ID DE RH_T_REGIME*                                                                                                                                   |
|                                                                                                                                                                          |
| 2.  **Editar**                                                                                                                                                           |
|                                                                                                                                                                          |
| - *USER_ALTERACAO \_ID = id de utilizador Logado*                                                                                                                        |
|                                                                                                                                                                          |
| - *DATA_ALTERACAO = **SYSDATE'***                                                                                                                                        |
|                                                                                                                                                                          |
| - *DATA_ALTERACAO_NAME = nome de utilizador Logado*                                                                                                                      |
|                                                                                                                                                                          |
|   1.  *~~Eliminar linha Modalidade **RH_T_REGIME_MODAL**~~*                                                                                                              |
|                                                                                                                                                                          |
| <!-- -->                                                                                                                                                                 |
|                                                                                                                                                                          |
| - *~~USER_ALTERACAO \_ID = id de utilizador Logado~~*                                                                                                                    |
|                                                                                                                                                                          |
| - *~~DATA_ALTERACAO = **SYSDATE'**~~*                                                                                                                                    |
|                                                                                                                                                                          |
| - *~~DATA_ALTERACAO_NAME = nome de utilizador Logado~~*                                                                                                                  |
|                                                                                                                                                                          |
| - *~~ESTADO = I~~*                                                                                                                                                       |
+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

###### Lista Regime 

+:------------------:+:------------------:+:------------------------:+:-------------------------:+
| **FIltro**         | **Tipo**           | **Descrição**            | **Fonte de dados**        |
+--------------------+--------------------+--------------------------+---------------------------+
| Estado             | *SELECT*           | **DOMAINS** = STATUS     | *RH_T_REGIME_TRAB*.ESTADO |
+--------------------+--------------------+--------------------------+---------------------------+
| Tipo Regime        | *SELECT*           | **DOMAIN** =             | *RH_T_REGIME_TRAB*.       |
|                    |                    | REGIME_TRABALHO          | *TIPO_REGIME*             |
+--------------------+--------------------+--------------------------+---------------------------+
| **Lista**          | **Tipo**           | **Fonte de dados**                                   |
+--------------------+--------------------+------------------------------------------------------+
| Tipo Regime        | *TEXT*             | *RH_T_REGIME_TRAB*. *TIPO_REGIME*                    |
+--------------------+--------------------+------------------------------------------------------+
| Data inicio        | *TEXT*             | *RH_T_REGIME_TRAB.DATA_INICIO*                       |
+--------------------+--------------------+------------------------------------------------------+
| Data Fim           | *TEXT*             | *RH_T_REGIME_TRAB.DATA_FIM*                          |
+--------------------+--------------------+------------------------------------------------------+
| Modalidade         | *TEXT*             | *Agrupar (RH_T_REGIME_MODAL.DIAS_SEMANA)*            |
+--------------------+--------------------+------------------------------------------------------+
| Numero Horas       | *TEXT*             | *Sum (RH_T_REGIME_MODAL.*NUM_HORAS                   |
+--------------------+--------------------+------------------------------------------------------+
| Estado             | *TEXT*             | *RH_T_REGIME_TRAB.ESTADO*                            |
+--------------------+--------------------+------------------------------------------------------+
| **ACÇÕES**                                                                                     |
+--------------------+---------------------------------------------------------------------------+
| Editar             | *Abre a mesma pagina de Registar*                                         |
+--------------------+---------------------------------------------------------------------------+

#### Rendimentos / Encargo

##### Lista 

a)  Rendimentos / Abonos / Subsidio

![Uma imagem com texto, número, software, Tipo de letra Os conteúdos
gerados por IA podem estar
incorretos.](media/image36.png){width="9.693055555555556in"
height="3.716666666666667in"}

+:------------------:+:------------------:+:------------------------:+:---------------------------:+
| **Filtro**         | **Tipo**           | **Descrição**            | **Fonte dados**             |
+--------------------+--------------------+--------------------------+-----------------------------+
| Data Inicio        | *DATE*             |                          | *RH_V_REND_ENC.             |
|                    |                    |                          | DATA_INICIO*                |
+--------------------+--------------------+--------------------------+-----------------------------+
| Data Fim           | *DATE*             |                          | *RH_V_REND_ENC. DATA_FIM*   |
+--------------------+--------------------+--------------------------+-----------------------------+
| Estado             | *select*           | ***DOMAINS** = STATUS*   | *RH_V_REND_ENC.ESTADO*      |
+--------------------+--------------------+--------------------------+-----------------------------+
| **Lista**          | **Tipo**           | **Descrição**            | **Fonte dados**             |
+--------------------+--------------------+--------------------------+-----------------------------+
| Estado             | *TEXT*             |                          | *RH_V_REND_ENC.ESTADO_DESC* |
+--------------------+--------------------+--------------------------+-----------------------------+
| Movimento          | *TEXT*             |                          | *RH_V_REND_ENC.* MOVIMENTO  |
+--------------------+--------------------+--------------------------+-----------------------------+
| Valor              | *TEXT*             |                          | *RH_V_REND_ENC.VALOR*       |
+--------------------+--------------------+--------------------------+-----------------------------+
| Data Inicio        | *TEXT*             |                          | *RH_V_REND_ENC.DATA_INICIO* |
+--------------------+--------------------+--------------------------+-----------------------------+
| Data Fim           | *TEXT*             |                          | *RH_V_REND_ENC.DATA_FIM*    |
+--------------------+--------------------+--------------------------+-----------------------------+
| Ultimo Proc        | *TEXT*             |                          | *RH_V_REND_ENC.*ULTIMO_PROC |
+--------------------+--------------------+--------------------------+-----------------------------+
| REGRAS                                                                                           |
+--------------------------------------------------------------------------------------------------+
| - ***Por defeito**, devem ser apresentadas apenas as remunerações ativas. (**RH_V_REND_ENC.**)*  |
|                                                                                                  |
| - *Os dados são obtidos a partir da vista **RH_V_REND_ENC(:P_TIPREL_ID, :P_TIPO)**, passando     |
|   como parâmetros:*                                                                              |
|                                                                                                  |
|   - ***TIPREL_ID**, que corresponde ao campo ID da tabela **RH_T_TIPOS_RELACIONAMENTO**.*        |
|                                                                                                  |
|   - ***TIPO = \'REM\'**.*                                                                        |
+--------------------------------------------------------------------------------------------------+

####### 

b)  Pagamentos / Desconto

![Uma imagem com texto, captura de ecrã, file, Tipo de letra Os
conteúdos gerados por IA podem estar
incorretos.](media/image37.png){width="9.693055555555556in"
height="2.49375in"}

+:------------------:+:------------------:+:------------------------:+:---------------------------:+
| **Filtro**         | **Tipo**           | **Descrição**            | **Fonte dados**             |
+--------------------+--------------------+--------------------------+-----------------------------+
| Data Inicio        | *DATE*             |                          | *RH_V_REND_ENC.             |
|                    |                    |                          | DATA_INICIO*                |
+--------------------+--------------------+--------------------------+-----------------------------+
| Data Fim           | *DATE*             |                          | *RH_V_REND_ENC. DATA_FIM*   |
+--------------------+--------------------+--------------------------+-----------------------------+
| Estado             | *select*           | ***DOMAINS** = STATUS*   | *RH_V_REND_ENC.ESTADO*      |
+--------------------+--------------------+--------------------------+-----------------------------+
| **Lista**          | **Tipo**           | **Descrição**            | **Fonte dados**             |
+--------------------+--------------------+--------------------------+-----------------------------+
| Estado             | *TEXT*             |                          | *RH_V_REND_ENC.ESTADO_DESC* |
+--------------------+--------------------+--------------------------+-----------------------------+
| Movimento          | *TEXT*             |                          | *RH_V_REND_ENC.* MOVIMENTO  |
+--------------------+--------------------+--------------------------+-----------------------------+
| Valor              | *TEXT*             |                          | *RH_V_REND_ENC.VALOR*       |
+--------------------+--------------------+--------------------------+-----------------------------+
| Data Inicio        | *TEXT*             |                          | *RH_V_REND_ENC.DATA_INICIO* |
+--------------------+--------------------+--------------------------+-----------------------------+
| Data Fim           | *TEXT*             |                          | *RH_V_REND_ENC.DATA_FIM*    |
+--------------------+--------------------+--------------------------+-----------------------------+
| Ultimo Proc        | *TEXT*             |                          | *RH_V_REND_ENC.*ULTIMO_PROC |
+--------------------+--------------------+--------------------------+-----------------------------+
| REGRAS                                                                                           |
+--------------------------------------------------------------------------------------------------+
| - ***Por defeito**, devem ser apresentadas apenas as remunerações ativas. (**RH_V_REND_ENC.**)*  |
|                                                                                                  |
| - *Os dados são obtidos a partir da vista **RH_V_REND_ENC(:P_TIPREL_ID, :P_TIPO)**, passando     |
|   como parâmetros:*                                                                              |
|                                                                                                  |
|   - ***TIPREL_ID**, que corresponde ao campo ID da tabela **RH_T_TIPOS_RELACIONAMENTO**.*        |
|                                                                                                  |
|   - ***TIPO = 'PAG'**.*                                                                          |
+--------------------------------------------------------------------------------------------------+

#####  Novo

a)  Remuneração

+:--------------:+:-------------:+:-------------:+:----------------------------:+:--------------:+:-----------------------------------:+
| **Formulario** | **Tipo**      | **Descrição**                                                 | **Gravação**                        |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| **Remuneracção |                                                                                                                     |
| /Abonos        |                                                                                                                     |
| /subsidio**    |                                                                                                                     |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| Validar        | *RADIOLIST*   | Esse deve ficar oculto, so fica visivel em caso de validação  | *RH_T_DEF_REMUNERACOES.ESTADO*      |
|                |               | desse Registo. No modo validação esse campo é obrigatorio     |                                     |
|                |               |                                                               |                                     |
|                |               | DOMAINS= SIM_NAO                                              |                                     |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| Movimento      | *SELECT*      |                                                               | *RH_T_DEF_REMUNERACOES.* *TM_ID*    |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| Valor          | *NUMBER*      |                                                               | *RH_T_DEF_REMUNERACOES.* *VALOR*    |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| Percentagem    | *NUMBER*      |                                                               | *RH_T_DEF_REMUNERACOES.PERCENTAGEM* |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| Moeda          | *SELECT*      |                                                               | *RH_T_DEF_REMUNERACOES.* *MOEDA*    |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| Data Inicio    | *DATE*        |                                                               | *RH_T_DEF_REMUNERACOES.*            |
|                |               |                                                               | *DATA_INICIO*                       |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| Data Fim       | *DATE*        |                                                               | *RH_T_DEF_REMUNERACOES. DATA_FIM*   |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| Observação     |               |                                                               | *RH_T_DEF_REMUNERACOES.OBS*         |
+----------------+---------------+---------------------------------------------------------------+-------------------------------------+
| **REGRAS**                                                                                                                           |
+--------------------------------------------------------------------------------------------------------------------------------------+
| - *Caso a remuneração já tenha um processamento , logo somente se pode altrar Data Fim e Observaçáo*                                 |
+--------------------------------------------------------------------------------------------------------------------------------------+
| **OUTRAS GRAVAÇOES**                                                                                                                 |
+------------------------------------------------+------------------------------+------------------------------------------------------+
| ***1,** gravação na tabela de                  | *Registo ou update na tabela | *3. Registo de log **RH_T_LOG***                     |
| **RH_T_DEF_REMUNERACOES***                     | de validação                 |                                                      |
|                                                | **RH_T_VALIDACAO***          | - *TIPO_ACCAO **= INSERT (**DOMAINS =                |
| - *DATA_REGISTO= '**SYSDATE'***                |                              |   TIPO_ACAO**)***                                    |
|                                                | - *TIPO_ACCAO**= 'INSERT'    |                                                      |
| - *USER_REGISTO_ID = id de utilizador Logado*  |   (**DOMAINS =               | - *TABELA_NAME **= 'RH_T_DEF_REMUNERACOES**"*        |
|                                                |   VALIDACAO_TIPO_ACAO**)***  |                                                      |
| - *USER_REGISTO_NAME = nome de utilizador      |                              | - *TABELA_ID **= ID** de tabela                      |
|   Logado*                                      | - *REFERENCIA_NAME **=       |   **RH_T_DEF_REMUNERACOES***                         |
|                                                |   'RENDIMENTO' (**DOMAINS =  |                                                      |
| - *USER_ALTERACAO \_ID = **NULL***             |   ACCAO_REFERENTE**)***      | - *FUN_ID = ID de colaborador                        |
|                                                |                              |   (**RH_T_FUNCIONARIOS.ID**) , id de colaborador que |
| - *USER_ALTERACAO_NAME = **NULL***             | - *REFERENCIA_ID **= ID** de |   será substutido*                                   |
|                                                |   tabela                     |                                                      |
| - *DATA_ALTERACAO = **NULL***                  |   RH_T_DEF_REMUNERACOES*     | - *TIPREL_ID = ID de tabela                          |
|                                                |                              |   RH_T_TIPOS_RELACIONAMENTO*                         |
| - *CARREIRA_ID*                                | - *FUN_ID **= ID** de tabela |                                                      |
|                                                |   **RH_T_FUNCIONARIOS***     | - *USER_REGISTO_ID **= ID utilizador que fez a       |
| - *FUN_ID*                                     |                              |   ação***                                            |
|                                                | - *TIPREL_ID = ID de tabela  |                                                      |
| - *ESTADO = "**P**"*                           |   RH_T_TIPOS_RELACIONAMENTO* | - *USER_REGISTO_NAME **= Nome do utilizador que fez  |
|                                                |                              |   a ação***                                          |
| - *OBS = 'NOVO_RENDIMENTO''*                   | - *DATA_REGISTO **=          |                                                      |
|                                                |   SYSDATE***                 | - *DATA_REGISTO = SYSDATE*                           |
|                                                |                              |                                                      |
|                                                | - *USER_REGISTO_NAME = nome  | ***3.1** Registo Detalhe de LOG na tabela            |
|                                                |   de utilizador Logado*      | **RH_T_VALIDACAO_DETALHE***                          |
|                                                |                              |                                                      |
|                                                | - *USER_REGISTO_ID = id de   | - *LOG_ID = id de tabela **RH_T_LOG***               |
|                                                |   utilizador Logado*         |                                                      |
|                                                |                              | - *CAMPO_ALTERADO **=** null*                        |
|                                                | - *ESTADO **= 'P'***         |                                                      |
|                                                |                              | - *VALOR_ANTERIOR = null*                            |
|                                                |                              |                                                      |
|                                                |                              | - *VALOR_NOVO = null*                                |
|                                                |                              |                                                      |
|                                                |                              | - *DADOS_REGISTO = CLOB (dados do formulário)*       |
+------------------------------------------------+------------------------------+------------------------------------------------------+

b)  Pagamento

+------------------+:-------------:+:--------------+:----------------------------:+:--------------:+:---------------------------------:+
| > **Formulario** | **Tipo**      | **Descrição**                                                 | **Gravação**                      |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| **Pagamento /    |                                                                                                                   |
| Desconto**       |                                                                                                                   |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| Validar          | *RADIOLIST*   | Esse deve ficar oculto, so fica visivel em caso de validação  | *RH_T_DEF_PAGAMENTOS.ESTADO*      |
|                  |               | desse Registo. No modo validação esse campo é obrigatorio     |                                   |
|                  |               |                                                               |                                   |
|                  |               | DOMAINS= SIM_NAO                                              |                                   |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| Movimento        | *SELECT*      |                                                               | *RH_T_DEF_PAGAMENTOS.* *TM_ID*    |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| Valor            | *NUMBER*      |                                                               | *RH_T_DEF_PAGAMENTOS.VALOR*       |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| Percentagem      | *NUMBER*      |                                                               | *RH_T_DEF_PAGAMENTOS.PERCENTAGEM* |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| Moeda            | *SELECT*      |                                                               | *RH_T_DEF_PAGAMENTOS.* MOEDA      |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| Data Inicio      | *DATE*        |                                                               | *RH_T_DEF_PAGAMENTOS.             |
|                  |               |                                                               | DATA_INICIO*                      |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| Data Fim         | *DATE*        |                                                               | *RH_T_DEF_PAGAMENTOS. DATA_FIM*   |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| Observação       |               |                                                               | *RH_T_DEF_PAGAMENTOS.*OBS         |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| **Dados de       |               |                                                               |                                   |
| Entidade**       |               |                                                               |                                   |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| Entidade         | *lookup*      |                                                               | *RH_T_DEF_PAGAMENTOS.*            |
|                  |               |                                                               | *NM_ENTIDADE*                     |
|                  |               |                                                               |                                   |
|                  |               |                                                               | *RH_T_DEF_PAGAMENTOS.* *ENT_ID*   |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| NIF              | *NUMBER*      | Preeenchido automatico aparitr de pesquisa entidade           | *RH_T_DEF_PAGAMENTOS.NIF*         |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| BANCO            | *SELECT*      | RH_T_BANCO                                                    | *RH_T_DEF_PAGAMENTOS.* *RHB_ID*   |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| NIB              | *TEXT*        |                                                               | *RH_T_DEF_PAGAMENTOS.* *NIB*      |
+------------------+---------------+---------------------------------------------------------------+-----------------------------------+
| **REGRAS**                                                                                                                           |
+--------------------------------------------------------------------------------------------------------------------------------------+
| - *Caso a remuneração já tenha um processamento , logo somente se pode altrar Data Fim e Observaçáo*                                 |
+--------------------------------------------------------------------------------------------------------------------------------------+
| **OUTRAS GRAVAÇOES**                                                                                                                 |
+--------------------------------------------------+------------------------------+----------------------------------------------------+
| ***1,** gravação na tabela de                    | *Registo ou update na tabela | *3. Registo de log **RH_T_LOG***                   |
| RH_T_DEF_PAGAMENTOS*                             | de validação                 |                                                    |
|                                                  | **RH_T_VALIDACAO***          | - *TIPO_ACCAO **= INSERT (**DOMAINS =              |
| - *DATA_REGISTO= '**SYSDATE'***                  |                              |   TIPO_ACAO**)***                                  |
|                                                  | - *TIPO_ACCAO**= 'INSERT'    |                                                    |
| - *USER_REGISTO_ID = id de utilizador Logado*    |   (**DOMAINS =               | - *TABELA_NAME **= 'RH_T_DEF_PAGAMENTOS**"*        |
|                                                  |   VALIDACAO_TIPO_ACAO**)***  |                                                    |
| - *USER_REGISTO_NAME = nome de utilizador        |                              | - *TABELA \_ID **= ID** de tabela                  |
|   Logado*                                        | - *REFERENCIA_NAME **=       |   **RH_T_DEF_PAGAMENTOS***                         |
|                                                  |   'DESCONTO" (**DOMAINS =    |                                                    |
| - *USER_ALTERACAO \_ID = **NULL***               |   ACCAO_REFERENTE**)***      | - *FUN_ID = ID de colaborador                      |
|                                                  |                              |   (**RH_T_FUNCIONARIOS.ID**) , id de colaborador   |
| - *USER_ALTERACAO_NAME = **NULL***               | - *REFERENCIA_ID **= ID** de |   que será substutido*                             |
|                                                  |   tabela                     |                                                    |
| - *DATA_ALTERACAO = **NULL***                    |   **RH_T_DEF_PAGAMENTOS***   | - *TIPREL_ID = id de tabela                        |
|                                                  |                              |   RH_T_TIPOS_RELACIONAMENTO*                       |
| - *ESTADO = "**P**"*                             | - *FUN_ID **= ID** de tabela |                                                    |
|                                                  |   **RH_T_FUNCIONARIOS***     | - *USER_REGISTO_ID **= ID utilizador que fez a     |
| - *OBS = 'NOVO_DESCONTO''*                       |                              |   ação***                                          |
|                                                  | - *TIPREL_ID = id de tabela  |                                                    |
| - *CARREIRA_ID*                                  |   RH_T_TIPOS_RELACIONAMENTO* | - *USER_REGISTO_NAME **= Nome do utilizador que    |
|                                                  |                              |   fez a ação***                                    |
| - *FUN_ID*                                       | - *DATA_REGISTO **=          |                                                    |
|                                                  |   SYSDATE***                 | - *DATA_REGISTO = SYSDATE*                         |
|                                                  |                              |                                                    |
|                                                  | - *USER_REGISTO_NAME = nome  | ***3.1** Registo Detalhe de LOG na tabela          |
|                                                  |   de utilizador Logado*      | **RH_T_VALIDACAO_DETALHE***                        |
|                                                  |                              |                                                    |
|                                                  | - *USER_REGISTO_ID = id de   | - *LOG_ID = id de tabela **RH_T_LOG***             |
|                                                  |   utilizador Logado*         |                                                    |
|                                                  |                              | - *CAMPO_ALTERADO **=** null*                      |
|                                                  | - *ESTADO **= 'P'***         |                                                    |
|                                                  |                              | - *VALOR_ANTERIOR = null*                          |
|                                                  |                              |                                                    |
|                                                  |                              | - *VALOR_NOVO = null*                              |
|                                                  |                              |                                                    |
|                                                  |                              | - *DADOS_REGISTO = CLOB (dados do formulário)*     |
+--------------------------------------------------+------------------------------+----------------------------------------------------+

###### Validar

+----------------------------------------------------------------------+
| - A validação invoca a mesma página de Registo                       |
|                                                                      |
| - O campo validar deve ficar visivel                                 |
|                                                                      |
| - Ao **validar**, devem ser atualizadas todas as tabelas associadas, |
|   definindo o campo **estado = \'A\'**.                              |
|                                                                      |
| - Ao **desvalidar**, devem ser atualizadas todas as tabelas          |
|   associadas, definindo o campo **estado = \'I\'**.                  |
|                                                                      |
| - Caso o utilizador **atualize algum campo no formulário**, a        |
|   alteração deve ser **refletida na tabela correspondente**.         |
+----------------------------------------------------------------------+

#### Processo Disciplinar

##### Lista Processo Disciplinar

![Uma imagem com texto, número, Tipo de letra, software Os conteúdos
gerados por IA podem estar
incorretos.](media/image38.png){width="9.693055555555556in"
height="3.9694444444444446in"}

+:------------------:+:------------------:+:------------------------:+:----------------------------------------:+
| **Lista**          | **Tipo**           | **Descrição**            | **Fonte dados**                          |
+--------------------+--------------------+--------------------------+------------------------------------------+
| Direção            | *TEXT*             |                          | *RH_V_MOBILIDADE.DIRECAO, passando como  |
|                    |                    |                          | parametro*                               |
|                    |                    |                          |                                          |
|                    |                    |                          | *RH_T_PROCESSO_DISCIPLINAR.TIPREL_ID*    |
+--------------------+--------------------+--------------------------+------------------------------------------+
| Sessao             | *TEXT*             |                          | *RH_V_MOBILIDADE.SESSAO, passando como   |
|                    |                    |                          | parametro*                               |
|                    |                    |                          |                                          |
|                    |                    |                          | *RH_T_PROCESSO_DISCIPLINAR.TIPREL_ID*    |
+--------------------+--------------------+--------------------------+------------------------------------------+
| Vinculo            | *TEXT*             |                          | *RH_V_MOBILIDADE.VINCULO, passando como  |
|                    |                    |                          | parametro*                               |
|                    |                    |                          |                                          |
|                    |                    |                          | *RH_T_PROCESSO_DISCIPLINAR.TIPREL_ID*    |
+--------------------+--------------------+--------------------------+------------------------------------------+
| Processo           | *TEXT*             |                          | *RH_T_PROCESSO_DISCIPLINAR.TP_PROCESSO*  |
| Disciplinar        |                    |                          |                                          |
+--------------------+--------------------+--------------------------+------------------------------------------+
| Data Inicio        | *TEXT*             |                          | *RH_T_PROCESSO_DISCIPLINAR.DATE_INIC_PD* |
+--------------------+--------------------+--------------------------+------------------------------------------+
| Data Fim           | *TEXT*             |                          | *RH_T_PROCESSO_DISCIPLINAR.DATE_FIM_PD*  |
+--------------------+--------------------+--------------------------+------------------------------------------+
|                                                                                                               |
+---------------------------------------------------------------------------------------------------------------+
| ***Editar**: Abre o mesmo formulario que Registo*                                                             |
+---------------------------------------------------------------------------------------------------------------+

##### Novo Processo Disciplinar 

![Uma imagem com texto, captura de ecrã, número, software Os conteúdos
gerados por IA podem estar
incorretos.](media/image39.png){width="9.693055555555556in"
height="4.65in"}

+--------------------+:------------------:+:-----------------------:+:-------------------------------------------:+
| > **Formulario**   | **Tipo**           | **Descrição**           | **Gravação**                                |
+--------------------+--------------------+-------------------------+---------------------------------------------+
|                    |                                                                                            |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Validar            | *RADIOLIST*        | Esse deve ficar oculto, | *RH_T_PROCESSO_DISCIPLINAR.ESTADO*          |
|                    |                    | so fica visivel em caso |                                             |
|                    |                    | de validação desse      |                                             |
|                    |                    | Registo. No modo        |                                             |
|                    |                    | validação esse campo é  |                                             |
|                    |                    | obrigatorio             |                                             |
|                    |                    |                         |                                             |
|                    |                    | DOMAINS= SIM_NAO        |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Vinculo Referente  | *SELCECT*          | Identifica o tipo de    | *RH_T_PROCESSO_DISCIPLINAR.TIPREL_ID*       |
|                    |                    | vínculo laboral do      |                                             |
|                    |                    | colaborador             |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Numero Processo    | *SELECT*           | Código ou número        | *RH_T_PROCESSO_DISCIPLINAR.NUM_PROCESSO*    |
|                    |                    | sequencial único        |                                             |
|                    |                    | atribuído ao processo   |                                             |
|                    |                    | disciplinar para        |                                             |
|                    |                    | efeitos de registo e    |                                             |
|                    |                    | controlo                |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Entidade           | *TEXT*             | Instituição,            | *RH_T_PROCESSO_DISCIPLINAR.ENTIDADE*        |
|                    |                    | departamento ou unidade |                                             |
|                    |                    | orgânica responsável    |                                             |
|                    |                    | pela abertura e         |                                             |
|                    |                    | tramitação do processo  |                                             |
|                    |                    | disciplinar.            |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Tipo Processo      | *SELECT*           | Classificação do        | *RH_T_PROCESSO_DISCIPLINAR.TP_PROCESSO*     |
|                    |                    | processo                |                                             |
|                    |                    |                         |                                             |
|                    |                    | **DOMAINS** =           |                                             |
|                    |                    | TP_PROCESSO_DISCP       |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Estado Do Processo | *SEELCT*           | Situação atual do       | *RH_T_PROCESSO_DISCIPLINAR.ESTADO*          |
|                    |                    | processo disciplinar    |                                             |
|                    |                    |                         |                                             |
|                    |                    | **DOMAINS** =           |                                             |
|                    |                    | STATUS_PROCESSO_DISCP   |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Pena Disciplinar   | *SELECT*           | Tipo de sanção aplicada | *RH_T_PROCESSO_DISCIPLINAR.PENA_DISCP*      |
|                    |                    | ao colaborador, se      |                                             |
|                    |                    | aplicável               |                                             |
|                    |                    |                         |                                             |
|                    |                    | **DOMAINS** =           |                                             |
|                    |                    | PENA_DISCIP             |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| **Data PD / Pena** |                    |                         |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Data Inicio PD     | *DATE*             | Data oficial de         | *RH_T_PROCESSO_DISCIPLINAR.DATE_INIC_PD*    |
|                    |                    | abertura ou instauração |                                             |
|                    |                    | do processo             |                                             |
|                    |                    | disciplinar.            |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Data Fim PD        | *DATE*             | Data de encerramento    | *RH_T_PROCESSO_DISCIPLINAR.DATE_FIM_PD*     |
|                    |                    | formal do processo      |                                             |
|                    |                    | disciplinar.            |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Data Inicio Pena   | *DATE*             | Data em que a pena      | *RH_T_PROCESSO_DISCIPLINAR.DATE_INIC_PENA*  |
|                    |                    | disciplinar começa a    |                                             |
|                    |                    | ter efeito.             |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Data Fim Pena      | *DATE*             | ata em que a pena       | *RH_T_PROCESSO_DISCIPLINAR.DATE_FIM_PENA*   |
|                    |                    | disciplinar termina ou  |                                             |
|                    |                    | deixa de produzir       |                                             |
|                    |                    | efeito.                 |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| **BO / ordem       |                    |                         |                                             |
| Serviços**         |                    |                         |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Nº BO              | *TEXT*             | Número do Boletim       | *RH_T_PROCESSO_DISCIPLINAR.NUM_BO*          |
|                    |                    | Oficial onde foi        |                                             |
|                    |                    | publicada a decisão.    |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Data Publicação BO | *DATE*             | Data de publicação da   | *RH_T_PROCESSO_DISCIPLINAR.DATA_PUBL_BO*    |
|                    |                    | decisão no Boletim      |                                             |
|                    |                    | Oficial.                |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Nº Ordem Serviço   | *TEXT*             | Número da Ordem de      | *RH_T_PROCESSO_DISCIPLINAR.NUM_ORDEM_SERV*  |
|                    |                    | Serviço associada ao    |                                             |
|                    |                    | processo disciplinar.   |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Data Ordem Serviço | *DATE*             | Data de emissão da      | *RH_T_PROCESSO_DISCIPLINAR.DATA_ORDEM_SERV* |
|                    |                    | Ordem de Serviço.       |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| **OFA :** Documento ou Ordem Formal Administrativa emitida pela entidade competente no âmbito do processo**.**  |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Nº Ofa             | *TEXT*             | Número identificador da | *RH_T_PROCESSO_DISCIPLINAR.NUM_OFA*         |
|                    |                    | Ordem Formal            |                                             |
|                    |                    | Administrativa.         |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| Data Emissão Ofa   | *DATE*             | Data em que a Ordem     | *RH_T_PROCESSO_DISCIPLINAR.DATA_EMISS_OFA*  |
|                    |                    | Formal Administrativa   |                                             |
|                    |                    | foi emitida.            |                                             |
+--------------------+--------------------+-------------------------+---------------------------------------------+
| **REGRAS**                                                                                                      |
+-----------------------------------------------------------------------------------------------------------------+
|                                                                                                                 |
+-----------------------------------------------------------------------------------------------------------------+
| **OUTRAS GRAVAÇOES**                                                                                            |
+-----------------------------------------------------------------------------------------------------------------+
| *Gravações de outros dados na tabela RH_T_PROCESSO_DISCIPLINAR*                                                 |
|                                                                                                                 |
| - *DATA_REGISTO= '**SYSDATE'***                                                                                 |
|                                                                                                                 |
| <!-- -->                                                                                                        |
|                                                                                                                 |
| - *USER_REGISTO_ID = id de utilizador Logado*                                                                   |
|                                                                                                                 |
| - *USER_REGISTO_NAME = nome de utilizador Logado*                                                               |
|                                                                                                                 |
| - *USER_ALTERACAO \_ID = **NULL***                                                                              |
|                                                                                                                 |
| - *USER_ALTERACAO_NAME = **NULL***                                                                              |
|                                                                                                                 |
| - *DATA_ALTERACAO = **NULL***                                                                                   |
|                                                                                                                 |
| <!-- -->                                                                                                        |
|                                                                                                                 |
| - *ESTADO = "**P**"*                                                                                            |
+-----------------------------------------------------------------------------------------------------------------+

###### Validar 

+----------------------------------------------------------------------+
| - A validação invoca a mesma página de Registo                       |
|                                                                      |
| - O campo validar deve ficar visível                                 |
|                                                                      |
| - Ao **validar**, devem ser atualizadas todas as tabelas associadas, |
|   definindo o campo **estado = \'A\'**.                              |
|                                                                      |
| - Ao **Desvalidar,** devem ser atualizadas todas as tabelas          |
|   associadas, definindo o campo **estado = \'I\'**.                  |
|                                                                      |
| - Caso o utilizador **atualize algum campo no formulário**, a        |
|   alteração deve ser **refletida na tabela correspondente**.         |
+----------------------------------------------------------------------+

#### Documento

##### Recibo (pendente)

Permite Extrair Recibo de salario de um colaborador

##### Declarações 

Modulo que Permite o Colaborador solicitar uma declaração Mediante um
pedido.

Qualquer declaração deve ser feito mediante um pedido, após a declaração
, o sistema deve permitir que o imprima a declaração, a declaração
emitida deve ser permitir assinatura digital..

###### Pedido

Formulario de pedido de declaracao

+--------------------+:-------------------:+:-----------------------:+:------------------------------------:+
| > **Formulario**   | **Tipo**            | **Descrição**           | **Gravação**                         |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Nome Requerente    | *SELECT*            | PESQUISA DO COLABORADOR | *RH_T_PEDIDO.FUN_ID*                 |
|                    |                     |                         |                                      |
|                    |                     |                         | *RH_T_DECLARACAO.FUN_ID*             |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Tipo Declaracao    |                     | DOMINIO = TIPO_PEDIDO   | *RH_T_PEDIDO.TIPO_PEDIDO=            |
|                    |                     |                         | '**DECLARACAO**'*                    |
|                    |                     |                         |                                      |
|                    |                     |                         | *RH_T_DECLARACAO.TIPO_DECLARACAO*    |
+--------------------+---------------------+-------------------------+--------------------------------------+
| **Informações do   | *Separador Lista*   |                         |                                      |
| Pedido**           |                     |                         |                                      |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Finalidade         |                     |                         | *RH_T_DECLARACAO.FINALIDADE*         |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Entidade           |                     |                         | *RH_T_DECLARACAO.ENTIDADE_DESTINADO* |
| destinatária       |                     |                         |                                      |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Data referente     |                     |                         | *RH_T_DECLARACAO.DATA_REFERENTE*     |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Analise Pedido     |                     |                         |                                      |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Em conformidade    |                     | DOMAIN = SIM_NAO        | *RH_T_PEDIDO.ESTADO = '**P**'*       |
|                    |                     |                         |                                      |
|                    |                     |                         | *RH_T_PEDIDO.ETAPA = 'Analise'*      |
|                    |                     |                         |                                      |
|                    |                     |                         | *RH_T_DECLARACAO.DECISAO_ANALISE*    |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Observação         |                     |                         | *RH_T_DECLARACAO.OBS_ANALISE*        |
+--------------------+---------------------+-------------------------+--------------------------------------+
| **Validação        | *Aparece somente na Etapa Validação*                                                 |
| Pedido**           |                                                                                      |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Validar            | *Radio List*        | Isso aparece somente na | *RH_T_PEDIDO.ESTADO = '**P**'*       |
|                    |                     | etapa Validacao         |                                      |
|                    |                     |                         | *RH_T_PEDIDO.ETAPA = '**Validação    |
|                    |                     | DOMAIN = SIM_NAO        | RH**'*                               |
|                    |                     |                         |                                      |
|                    |                     |                         | *RH_T_DECLARACAO.*DECISAO_RH         |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Entrega            | *RADIOLIST*         | DOMAIN = SIM_NAO        | *RH_T\_.ETAPA ='ENTREGA'*            |
+--------------------+---------------------+-------------------------+--------------------------------------+
|                    | *RADIOLIST*         | DOMAIN = SIM_NAO        | *Ccas*                               |
+--------------------+---------------------+-------------------------+--------------------------------------+
| ANEXO              | se necessário comprovativos                                                          |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Tipo Documento     |                     |                         |                                      |
+--------------------+---------------------+-------------------------+--------------------------------------+
| Documento          |                     |                         |                                      |
+--------------------+---------------------+-------------------------+--------------------------------------+
| **ACÇOES**                                                                                                |
+-----------------------------------------------------------------------------------------------------------+
| *GRAVACAO:*                                                                                               |
|                                                                                                           |
| *TABELA :*                                                                                                |
|                                                                                                           |
| - *RH_T_PEDIDO*                                                                                           |
|                                                                                                           |
|   - *DATA_PEDIDO*                                                                                         |
|                                                                                                           |
|   - *DATA_REGISTO*                                                                                        |
|                                                                                                           |
|   - *USER_REGISTO*                                                                                        |
|                                                                                                           |
| - *RH_T_DECLARACAO*                                                                                       |
|                                                                                                           |
|   - *DATA_REGISTO*                                                                                        |
|                                                                                                           |
|   - *USER_REGISTO*                                                                                        |
|                                                                                                           |
| - *RH_T_NOTIFICACAO\*                                                                                     |
|                                                                                                           |
| Parte inferior do formulário                                                                              |
+-----------------------------------------------------------------------------------------------------------+

###### 

Lista de pedido de declaracao

+:-----------:+:--------------------:+:------------------------:+:-----------------------:+
| **Lista**   | **Tipo**             | **Descrição**            | **Fonte dados**         |
+-------------+----------------------+--------------------------+-------------------------+
| Nome        |                      | RH_T_FUNCIONARIOS.NOME   | RH_T_DECLARACAO.FUN_ID  |
| Requerente  |                      |                          |                         |
+-------------+----------------------+--------------------------+-------------------------+
| Data Pedido |                      |                          | RH_T_DECLARACAO.DATA    |
| DE          |                      |                          |                         |
+-------------+----------------------+--------------------------+-------------------------+
| Data Pedo   |                      |                          |                         |
| Até         |                      |                          |                         |
+-------------+----------------------+--------------------------+-------------------------+
| **Lista**   | **Tipo**             | **Descrição**            | **Fonte dados**         |
+-------------+----------------------+--------------------------+-------------------------+
| Nome        | *TEXT*               |                          |                         |
| Requerente  |                      |                          |                         |
+-------------+----------------------+--------------------------+-------------------------+
| Tipo        | *TEXT*               |                          |                         |
| Declaracao  |                      |                          |                         |
+-------------+----------------------+--------------------------+-------------------------+
| Finalidade  | *TEXT*               |                          |                         |
+-------------+----------------------+--------------------------+-------------------------+
| Data        | *TEXT*               |                          |                         |
| necessária  |                      |                          |                         |
| (prazo )    |                      |                          |                         |
+-------------+----------------------+--------------------------+-------------------------+
| Estado      | *TEXT*               | **DOMAIN** =             |                         |
|             |                      | ESTADO_PEDIDO            |                         |
+-------------+----------------------+--------------------------+-------------------------+
| **Etapa**   |                      | **DOMAIN** =             |                         |
|             |                      | ETAPA_PEDIDO, onde       |                         |
|             |                      | referencia = DECLARACAO  |                         |
+-------------+----------------------+--------------------------+-------------------------+
| **ACCÕES**  |                      |                          |                         |
+-------------+----------------------+--------------------------+-------------------------+
| Ver Detalhe | *PERMITE VALIDAR E ENTREGAR O DOCUMENTO*                                  |
+-------------+---------------------------------------------------------------------------+
| Ver         | *VER TODAS NOTIFICAÇÕES ASSOCIADA AO PEDIDO*                              |
| Notificação |                                                                           |
+-------------+---------------------------------------------------------------------------+

##### Ordem de Serviço

![Uma imagem com texto, número, file, software Os conteúdos gerados por
IA podem estar
incorretos.](media/image40.png){width="9.693055555555556in"
height="3.3430555555555554in"}

+:------------------:+:------------------:+:------------------------:+:------------------------------:+
| **Lista**          | **Tipo**           | **Descrição**            | **Fonte dados**                |
+--------------------+--------------------+--------------------------+--------------------------------+
| Descricao          |                    |                          | *RH_T_ORDEM_SERVICO.DESCRICAO* |
+--------------------+--------------------+--------------------------+--------------------------------+
| Referente          |                    | ***DOMAINS:**            | *RH_T_ORDEM_SERVICO.REFERENTE* |
|                    |                    | ACCAO_REFERENTE*         |                                |
+--------------------+--------------------+--------------------------+--------------------------------+
| Numero             |                    |                          | *RH_T_ORDEM_SERVICO.NUMERO*    |
+--------------------+--------------------+--------------------------+--------------------------------+
| Anexar Ordem       |                    |                          | *RH_T_DOCUMENTO*               |
| Serviço            |                    |                          |                                |
+--------------------+--------------------+--------------------------+--------------------------------+
| Ver ordem Serviço  |                    |                          | *RH_T_ORDEM_SERVICO.DOC_ID*    |
+--------------------+--------------------+--------------------------+--------------------------------+
| OUTRAS GRAVAÇÕES   |                    |                          |                                |
+--------------------+--------------------+--------------------------+--------------------------------+
| 1.  *Grava na tabela RH_T_DOCUMENTO*                                                                |
|                                                                                                     |
| 2.  *Grava na tabela RH_T_ORDEM_SERVICO*                                                            |
+-----------------------------------------------------------------------------------------------------+

### Alerta e Notificação

Deve ser criado job para notificaçao e alerta e notificação.

Para ver o prazo de notificacao de cada tipo .

#### JOB

+:--------------:+:--------------------------:+:--------------------------:+
| **JOB**        | **Descrição**                                           |
+----------------+---------------------------------------------------------+
| 1.  Alerta     | O sistema deve alertar RH 45 dias antes de data de      |
|     prazo      | renovacao do contrato                                   |
|     Renovação  |                                                         |
|                | **DOMAINS** =**'PRAZO' , DESCRICAO = 'Renovacao         |
|                | Contrato'**                                             |
|                |                                                         |
|                | Este *job* deve automaticamente:                        |
|                |                                                         |
|                | 1.  Criar um registo na tabela **RH_T_ALERTA**          |
|                |                                                         |
|                | 2.  Criar um registo correspondente na tabela           |
|                |     **RH_T_NOTIFICACAO**                                |
|                |                                                         |
|                | 3.  Associar o alerta ao tipo RENOVAÇÃO DE CONTRATO     |
|                |                                                         |
|                | O RH terá acesso a uma lista de alertas especificamente |
|                | relacionada à renovação de contratos.\                  |
|                | A partir desta lista, o RH poderá:                      |
|                |                                                         |
|                | - Selecionar um grupo de colaboradores com contratos    |
|                |   prestes a expirar                                     |
|                |                                                         |
|                | - Abrir um formulário que permite:                      |
|                |                                                         |
|                |   - Rever e confirmar a renovação automática, ou        |
|                |                                                         |
|                |   - Editar/ajustar manualmente o novo prazo do contrato |
|                |     (ex.: mensal → anual)                               |
|                |                                                         |
|                | Após confirmação pelo RH, os registos selecionados      |
|                | devem ser enviados para validação (**RH_T_VALIDACAO**). |
|                |                                                         |
|                | Quando o contrato é renovado, não altera a carreira     |
|                +----------------------------+----------------------------+
|                | 1.1-Registo na tabela      | 1.2 Registo na tabela      |
|                | RH_T_ALERTA                | RH_T_NOTIFICACAO           |
|                |                            |                            |
|                | - *REFERENCIA =            | - *REFERENCIA =            |
|                |   'RENOVACAO´*             |   'RENOVACAO'*             |
|                |                            |                            |
|                | - *DESCRICAO = ´Renovacao  | - *ASSUNTO =*              |
|                |   contrato´*               |                            |
|                |                            | - *MESSAGE =*              |
|                | - ESTADO = ´P´             |                            |
|                |                            | - *ASSUNTO*                |
|                | - *DATA_REGISTO =          |                            |
|                |   **SYSDATE***             | - *EMAIL =*                |
|                |                            |                            |
|                | - *USER_REGISTO_NAME =     | *1.4 RH_T_NOTIF_ALERTA*    |
|                |   **utilizador logado***   |                            |
|                |                            | - *ALERTA_ID*              |
|                | - *USER_ALTERACAO_ID =*    |                            |
|                |                            | - *NOTIFI_ID*              |
|                | 1.2 *RH_T_ALERTA_DETALHE.* |                            |
|                |                            |                            |
|                | - *TIREPL_ID*              |                            |
|                |                            |                            |
|                | - *ALERTA_ID*              |                            |
|                |                            |                            |
|                | - *DATA_INICIO =           |                            |
|                |   DATA_INICIO DE           |                            |
|                |   RH_T_CONTRATO_VINCULO*   |                            |
|                |                            |                            |
|                | - *DATA FIIM = DATA_FIM de |                            |
|                |   RH_T_CONTRATO_VINCULO*   |                            |
+----------------+----------------------------+----------------------------+
| 2.  Alerta     | (ESPECIFICAÇÃO em outro documento)                      |
|     data Fim   |                                                         |
|     de licença | **DOMAINS** =**'PRAZO' , DESCRICAO = 'LICENÇA /S        |
|     sem        | VENCIMENTO'**                                           |
|     vencimento |                                                         |
+----------------+---------------------------------------------------------+
| 3.  Alerta de  | ESPECIFICAÇÃO em outro documento)                       |
|     progressão |                                                         |
|     e Promoção | **DOMAINS** =**'PRAZO' , DESCRICAO = 'PROGRESSÃO'**     |
+----------------+---------------------------------------------------------+

#### Lista alerta notificação 

+:-----------:+:-------------------:+:------------------------:+:--------------------------------:+
| **Lista**   | **Tipo**            | **Descrição**            | **Fonte dados**                  |
+-------------+---------------------+--------------------------+----------------------------------+
| Referente   | *TEXT*              | ***DOMAINS:***           | *RH_T_ALERTA.REFERENTE*          |
|             |                     | TIPO_MOV_LABORAL         |                                  |
+-------------+---------------------+--------------------------+----------------------------------+
| descricao   |                     |                          | *RH_T_ALERTA.DESCRICAO*          |
+-------------+---------------------+--------------------------+----------------------------------+
| Nome de     |                     | *RH_T_FUNCIONARIO.NOME*  | *RH_T_ALERTA_DETALHE.TIPREL_ID*  |
| colaborador |                     |                          |                                  |
| /           |                     |                          |                                  |
| Quantidade  |                     |                          |                                  |
+-------------+---------------------+--------------------------+----------------------------------+
| Periodo     |                     |                          | *RH_T_ALERTA_DETALHE.DATA_INICIO |
|             |                     |                          | \|\|                             |
|             |                     |                          | RH_T_ALERTA_DETALHE.DATA_FIM*    |
+-------------+---------------------+--------------------------+----------------------------------+
| Estado      |                     | **DOMINIO**=STATUS       |                                  |
+-------------+---------------------+--------------------------+----------------------------------+
| Selecionar  | *check*             |                          |                                  |
| todos       |                     |                          |                                  |
+-------------+---------------------+--------------------------+----------------------------------+
| **REGRAS**  |                     |                          |                                  |
+-------------+---------------------+--------------------------+----------------------------------+
|             |                     |                          |                                  |
+-------------+---------------------+--------------------------+----------------------------------+
| **AÇOES**   |                     |                          |                                  |
+-------------+---------------------+--------------------------+----------------------------------+
| Renovar     |                                                                                   |
| Contrato    |                                                                                   |
+-------------+-----------------------------------------------------------------------------------+
| Ver         |                                                                                   |
| Notificação |                                                                                   |
+-------------+-----------------------------------------------------------------------------------+

##### Validação

  ----------------------------------------------------------------------

  ----------------------------------------------------------------------

### Missões de Serviço

(Pendente -- Especificação em outro Documento )

### Emprestimo 

(Pendente -- Especificação em outro Documento )

### Controlo Assiduidade

#### Ferias 

#### Falta

### Avaliação Desempenho 

(Pendente -- Especificação em outro Documento )

# Tabelas associdas 

![Uma imagem com texto, captura de ecrã, diagrama, file Os conteúdos
gerados por IA podem estar
incorretos.](media/image41.png){width="9.693055555555556in"
height="4.879861111111111in"}

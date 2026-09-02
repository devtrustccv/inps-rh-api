<figure>
<img src="media/image2.jpeg" style="width:14.65694in;height:9.77083in"
alt="C:\Users\joelm\Desktop\Imagens\sergey-zolkin-_UeY8aTI6d0-unsplash (2).jpg" />
<figcaption><p>SIPS-RH</p></figcaption>
</figure>

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

| **Funcionalidade** | **Descrição** |
|:--:|----|
| Registo do Colaborador | A funcionalidade de **Registo de Colaboradores** permite criar e atualizar o dossiê digital de cada trabalhador no sistema de RH. Através de uma interface intuitiva, o utilizador insere dados pessoais, contactos, identificação (NIF/BI), vínculo e informações contratuais, enquadramento organizacional (direção, função, escalão), dados bancários e qualificações académicas ou profissionais, podendo ainda anexar documentos relevantes como contratos e certidões. |
| Lista de Colaboradores | A **Lista de Colaboradores** apresenta de forma organizada todos os trabalhadores registados no sistema de RH, com dados essenciais como nome, cargo, direção, vínculo e estado. Permite pesquisar, aplicar filtros e aceder rapidamente ao perfil detalhado de cada colaborador para consulta ou atualização. |
| Dossiê do colaborador | O **Dossiê do Colaborador** reúne todas as informações individuais do trabalhador num único registo digital. Contém dados pessoais, contratuais, organizacionais, bancários, académicos e profissionais, bem como documentos anexos, servindo de base para gestão, consulta e atualização no sistema de RH. |

## Mapa Mental das Funcionalidades 

> <img src="media/image5.png" style="width:7.32708in;height:5.89306in"
> alt="Uma imagem com texto, diagrama, captura de ecrã, número Os conteúdos gerados por IA podem estar incorretos." />

# Especificação 

Nestas sessão se descreve de forma integrada o que o sistema deve fazer
(regras, fluxos e funcionalidades) e como deve ser implementado
(arquitetura, dados, integrações e requisitos).

## Registo do colaborador 

### Fluxo de registo de colaborador 

<img src="media/image6.png" style="width:5.98264in;height:2.99514in"
alt="Uma imagem com texto, captura de ecrã, ecrã, diagrama Os conteúdos gerados por IA podem estar incorretos." />

| **Etapa** | **Responsável** | **Descrição** |
|:--:|----|----|
| Registo / Atualização do Colaborador | Técnico | Os dados de registo do colaborador são inseridos no sistema pelo técnico com o perfil adequado. Esses registos permanecem no estado **Não Validado**, aguardando a validação do responsável. |
| Validação | Técnico responsável com perfil para validação | O registo do colaborador é validado pelo técnico com o perfil adequado. Esse técnico pode atualizar os dados, caso seja necessário. Ao validar os dados, o registo é alterado para o estado **'Validado'**." |

### Desenho de interface e Descrição

### Dados Pessoais

<img src="media/image7.png" style="width:8.4875in;height:5.13819in"
alt="Uma imagem com texto, captura de ecrã, software, Ícone de computador Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 7%" />
<col style="width: 2%" />
<col style="width: 37%" />
<col style="width: 31%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="5" style="text-align: center;"><strong>identificação do
Colaborador</strong></td>
</tr>
<tr>
<td style="text-align: left;">*Tipo Documento Identificação</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Tipo de documento de identificação do Colaborador</p>
<p><strong>Tabela</strong>: RH_T_TIPO_DOCUMENTO onde tipo
‘<strong>DOCUMENTO_PESSOAL’</strong></p></td>
<td><p><em>RH_T_FUNCIONARIOS.TIPO_DOCUMENTO</em></p>
<p><em>RH_T_DOCUMENTO_PESSOAL.TIPO_DOCUMENTO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">* N.º Documento de Identificação</td>
<td><em>TEXT</em></td>
<td colspan="2">Numero documento de identificação do colaborador</td>
<td><p><em>RH_T_FUNCIONARIOS.NUM_DOCUMENTO</em></p>
<p><em>RH_T_DOCUMENTO_PESSOAL.NUM_DOCUMENTO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Nome</td>
<td><em>TEXT</em></td>
<td colspan="2">Número do documento apresentado para fins de
identificação</td>
<td><em>RH_T_FUNCIONARIOS.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">Foto</td>
<td><em>IMAGEM</em></td>
<td colspan="2">PENDENTE: decidir como será registado (ver com ATY)</td>
<td><em>RH_T_FUNCIONARIOS.FOTOGRAFIA</em></td>
</tr>
<tr>
<td style="text-align: left;">*Data Nascimento</td>
<td><em>DATE</em></td>
<td colspan="2">Nome completo do colaborador, conforme o documento de
identificação</td>
<td><em>RH_T_FUNCIONARIOS.DATA_NASCIMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Género</td>
<td><em>SELECT</em></td>
<td colspan="2">Data do Nascimento do colaborador (<em>DOMAINS =
GENERO</em>)</td>
<td><em>RH_T_FUNCIONARIOS.SEXO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Nome Mãe</td>
<td><em>TEXT</em></td>
<td colspan="2">Nome completo da mãe do colaborador</td>
<td><em>RH_T_FUNCIONARIOS.NM_MAE</em></td>
</tr>
<tr>
<td style="text-align: left;">*Nome Pai</td>
<td><em>TEXT</em></td>
<td colspan="2">Nome completo do pai do colaborador</td>
<td><em>RH_T_FUNCIONARIOS.NM_PAI</em></td>
</tr>
<tr>
<td style="text-align: left;">*Estado Civil</td>
<td><em>SELECT</em></td>
<td colspan="2">Esrado civil atual do colaborador
(<em>DOMAINS=ESTADO_CIVIL</em>)</td>
<td><em>RH_T_FUNCIONARIOS.ESTADO_CIVIL</em></td>
</tr>
<tr>
<td style="text-align: left;">*Nacionalidade</td>
<td><em>SELECT</em></td>
<td colspan="2">País de nacionalidade do colaborador
(<em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em>)</td>
<td><em>RH_T_FUNCIONARIOS.</em> <em>NACIONALIDADE</em></td>
</tr>
<tr>
<td style="text-align: left;">*Naturalidade</td>
<td><em>LOCKUP</em></td>
<td colspan="2">Local de nascimento do colaborador
(<em>GEOGRAFIA</em>)</td>
<td><em>RH_T_FUNCIONARIOS<strong>.</strong>LOC_NASC_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Data emissão</td>
<td><em>DATE</em></td>
<td colspan="2">Preenchida Automaticamente</td>
<td><em>RH_T_DOCUMENTO_PESSOAl.DATA_EMISSAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Validade</td>
<td><em>DATE</em></td>
<td colspan="2">Preenchida Automaticamente</td>
<td><em>RH_T_DOCUMENTO_PESSOAl.DATA_VALIDADE</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Documentos
Administrativos</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">NIF</td>
<td><em>NUMBER</em></td>
<td colspan="2"><p>Numero de identificação Fiscal</p>
<p>*PENDENTE: API Pesquisa NIF</p></td>
<td><em>RH_T_FUNCIONARIOS.NIF</em></td>
</tr>
<tr>
<td style="text-align: left;">N.º Segurado</td>
<td><em>NUMBER</em></td>
<td colspan="2"><p>Número de Identificação Fiscal do colaborador</p>
<p>*PENDENTE:API pesquisa segurado</p></td>
<td><em>RH_FUNCIONARIOS.</em> <em>NU_SEG_INPS</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Contacto</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Contacto</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Tipo de Contacto do colaborador (Telemóvel, Telefone,
Email)</p>
<p><em>DOMAINS = TP_CONTACTO</em></p></td>
<td><em>RH_T_CONTACTO.TIPO_CONTACTO</em></td>
</tr>
<tr>
<td style="text-align: left;">Contacto</td>
<td><em>TEXT</em></td>
<td colspan="2">Número de telefone, endereço de e-mail ou outro contacto
indicado pelo colaborador.</td>
<td><em>RH_T_CONTACTO.CONTACTO</em></td>
</tr>
<tr>
<td colspan="5"
style="text-align: center;"><strong>Endereço</strong></td>
</tr>
<tr>
<td style="text-align: left;">Pais</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>País onde o colaborador reside atualmente.</p>
<p><strong>Função</strong>: GET_GEOGRAFIA (P_NIVEL = 1)</p></td>
<td><em>RH_T_ENDERECO.PAIS_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Ilha</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Ilha de residência do colaborador</p>
<p><strong>Função</strong>: GET_GEOGRAFIA (P_NIVEL = 2)</p></td>
<td><em>RH_T_ENDERECO.ILHA_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Concelho</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Município onde o colaborador reside.</p>
<p><strong>Função:</strong> GET_GEOGRAFIA (P_NIVEL = 3)</p></td>
<td><em>RH_T_ENDERECO.CONCELHO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Freguesia</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Divisão administrativa onde se encontra a residência
do colaborador.</p>
<p><strong>Função:</strong> GET_GEOGRAFIA (P_NIVEL = 4)</p></td>
<td><em>RH_T_ENDERECO.FREGUESIA_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Zona</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Bairro, distrito ou localidade dentro da
freguesia.</p>
<p><strong>Função:</strong> GET_GEOGRAFIA (P_NIVEL = 5)</p></td>
<td><em>RH_T_ENDERECO.ZONA_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Morada</td>
<td><em>TEXT</em></td>
<td colspan="2">Endereço completo e detalhado do colaborador, incluindo
rua, número de casa, referência, etc</td>
<td><em>RH_T_ENDERECO.MORADA</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ul>
<li><p><em>A validação dos campos obrigatórios deve ser efetuada tanto
ao nível do formulário como ao nível da tabela</em></p></li>
<li><p><em>O sistema não permite duplicação de colaborador. Caso seja
detetado um número de documento já registado, deve ser emitida uma
mensagem de ERRO (“Já existe um colaborador registado com este número de
documento”). A combinação entre o tipo de documento e o número de
documento deve ser única</em></p></li>
<li><p><em>O sistema deve validar se o <strong>NIF</strong> indicado
corresponde efetivamente ao colaborador através da <strong>API</strong>
disponibilizado. Mensagem de ERRO: ‘’O NIF introduzido não corresponde
ao colaborador selecionado<strong>”.</strong> Validar com nome, data
nascimento, nome de mae e pai</em></p></li>
<li><p><em>O sistema deve validar se o <strong>N.º Segurado</strong>
indicado corresponde efetivamente ao colaborador através da
<strong>API</strong> disponibilizado. Mensagem de ERRO: ‘’O Nº segurado
introduzido não corresponde ao colaborador
selecionado<strong>”.</strong> Validar com nome, data nascimento, nome
de mae e pai.</em></p></li>
<li><p><em>Validar se o contacto já está associado a outro funcionário e
emitir um ALERTA: “O contacto informado já está associado a outro
colaborador”</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>GRAVAÇÃO DE OUTROS
CAMPOS</strong></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><ol type="1">
<li><p><em><strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>ESTADO_VALIDACAO = “<strong>P</strong>”</em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador logado</em></p></li>
<li><p><em>USER_ALTERACAO_ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
</ul></td>
<td><ol start="2" type="1">
<li><p><em><strong>RH_T_CONTACTO</strong></em></p></li>
</ol>
<ul>
<li><p><em>ESTADO = ‘<strong>A</strong>’</em></p></li>
<li><p><em>USER_REGISTO _ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador logado</em></p></li>
<li><p><em>USER_ALTERACAO_ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO = <strong>SYSDATE</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID = <strong>ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ul></td>
<td><ol start="3" type="1">
<li><p><em><strong>RH_T_ENDERECO</strong></em></p></li>
</ol>
<ul>
<li><p><em>ESTADO = ‘A’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = id de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO_ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ REGISTO = <strong>SYSDATE</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME =<strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID= id de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ol start="4" type="1">
<li><p><em><strong>RH_T_DOCUMENTO_PESSOAL</strong></em></p></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID: ID de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Agregado Dependente

<img src="media/image8.png" style="width:9.69306in;height:4.36736in"
alt="Uma imagem com texto, captura de ecrã, file, número Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 6%" />
<col style="width: 35%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">*Tipo Documento Identificação</td>
<td><em>TEXT</em></td>
<td><p>Tipo de documento de identificação do Colaborador</p>
<p><em>RH_TP_DOCUMENTO.REFERENCIA =</em>
<strong>DOCUMENTO_PESSOAL’</strong></p></td>
<td><em>RH_T_FAMILIARES.TP_DOCUMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">* N.º Documento de Identificação</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_FAMILIARES.</em> <em>NUM_DOCUMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Nome</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_FAMILIARES.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">*Data Nascimento</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_FAMILIARES.</em> <em>DATA_NASCIMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Género</td>
<td><em>SELECT</em></td>
<td><em><strong>DOMAINS</strong> = GENERO</em></td>
<td><em>RH_T_FAMILIARES.SEXO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Grau Parentesco</td>
<td><em>SELECT</em></td>
<td><em><strong>DOMAINS</strong> = GRAUS_DE_PARENTESCO</em></td>
<td><em>RH_T_FAMILIARES.GDP_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">*Dependente</td>
<td><em>SELECT</em></td>
<td><em><strong>DOMAINS</strong> = DEPENDENCIA</em></td>
<td><em>RH_T_FAMILIARES.</em> <em>DEPENDENCIA</em></td>
</tr>
<tr>
<td style="text-align: left;">*Agregado</td>
<td><em>SELECT</em></td>
<td><em><strong>DOMAINS=</strong> MEMBRO_AGR</em></td>
<td><em>RH_T_FAMILIARES.MEMBRO_AGR</em></td>
</tr>
<tr>
<td style="text-align: left;">*Responsavel</td>
<td><em>SELECT</em></td>
<td><em><strong>DOMAINS</strong>= SIM_NAO</em></td>
<td><em>RH_T_FAMILIARES.RESPONSAVEL</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em>A validação dos campos obrigatórios deve ser efetuada tanto
ao nível do formulário como ao nível da tabela</em></p></li>
<li><p><em>O sistema deve garantir que não existam duplicados de
familiares para o mesmo colaborador. A verificação deve ser feita com
base nos campos FUN_ID, NUM_DOCUMENTO e NOME. Mensagem de ERRO : “Já
existe um familiar com este nome e número de documento associado a este
colaborador”</em></p></li>
<li><p><em><mark>Um familiar só pode ter um único responsável (verificar
se o número do documento já está associado a outro colaborador como seu
responsável). Emitir a seguinte mensagem de erro: “<strong>O referido
familiar já possui outro colaborador associado como seu
responsável</strong>.”</mark></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>GRAVAÇÃO DE OUTROS
CAMPOS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ol type="1">
<li><p><em><strong>RH_T_FAMILIARES</strong></em></p></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = id de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ ALTERACAO _NAME = Nome de utilizador
Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID= ID de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Dados Acedémicos e Profissional

<img src="media/image9.png" style="width:9.69306in;height:2.76736in"
alt="Uma imagem com texto, captura de ecrã, file, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">PAIS</td>
<td><em>SELECT</em></td>
<td><p>País onde o colaborador obteve a habilitação literária.<br />
(Ex.: Cabo Verde, Portugal, Brasil)</p>
<p><em><strong>Função</strong> :</em> GET_GEOGRAFIA (P_NIVEL =1
)</p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.PAIS_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><mark>ESTABELECIMENTO</mark></td>
<td><em><mark>SELECT</mark></em></td>
<td><p>Nome da instituição de ensino onde foi realizada a
formação.<br />
<em>(Ex.: Universidade de Lisboa, Instituto Politécnico de Cabo Verde)
.. <mark>ao Selecionar um pais deve trazer o
establecimento.</mark></em></p>
<p><em><mark><strong>TABELA</strong>: RH_T_ESTABELECIMENTO onde o
PAIS_ID é o ID DO PAIS SELECIONADO</mark></em></p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.ESTABLECIMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">AREA</td>
<td><em>SELECT</em></td>
<td><p>Área de estudo ou domínio científico a que pertence a
formação.<br />
(Ex.: Ciências Sociais, Engenharia, Saúde)</p>
<p><em><strong>DOMAINS</strong> = AREA_FORMACAO</em></p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.AREA</em></td>
</tr>
<tr>
<td style="text-align: left;"><mark>CURSO</mark></td>
<td><em><mark>SELECT</mark></em></td>
<td><p>Designação específica do curso concluído ou frequentado pelo
colaborador.</p>
<p><mark><strong>DOMAIN</strong> = CURSO</mark></p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.NOME_CURSO</em></td>
</tr>
<tr>
<td style="text-align: left;">GRAU ACÁDEMICO /NIVEL</td>
<td></td>
<td><p>Grau ou nível académico correspondente à habilitação
obtida.<br />
(Ex.: Licenciatura, Mestrado, Doutoramento, Ensino Secundário, Técnico
Profissional)</p>
<p><em><mark><strong>DOMAINS</strong> = NIVEL_HABILITACOES REFERENCIA =
HABILITACOES</mark></em></p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.NIVEL</em></td>
</tr>
<tr>
<td style="text-align: left;">DATA INICIO</td>
<td></td>
<td>Data em que o colaborador iniciou o curso ou formação.</td>
<td>RH_T_HABILITACOES_LITERARIAS. DATA_INICIO</td>
</tr>
<tr>
<td style="text-align: left;">DATA TERMINO</td>
<td></td>
<td>Data em que o colaborador concluiu (ou abandonou) a formação.</td>
<td>RH_T_HABILITACOES_LITERARIAS. DATA_FIM</td>
</tr>
<tr>
<td style="text-align: left;">CONCLUIDO</td>
<td></td>
<td><p>Indicador (Sim/Não) que informa se a formação foi concluída com
aproveitamento.</p>
<p><em><strong>DOMAINS =</strong>SIM_NAO_NUMBER</em></p></td>
<td>RH_T_HABILITACOES_LITERARIAS.CONCLUIDO</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em>A validação dos campos obrigatórios deve ser efetuada tanto
ao nível do formulário como ao nível da tabela</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>GRAVAÇÃO DE OUTROS
CAMPOS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><strong>1.
RH_T_HABILITACOES_LITERARIAS</strong></p>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID: ID de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

4)  Dados Contratuais

> <img src="media/image10.png" style="width:8.10417in;height:4.98819in"
> alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 6%" />
<col style="width: 5%" />
<col style="width: 31%" />
<col style="width: 0%" />
<col style="width: 31%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><strong>Dados Contratuais</strong></td>
<td></td>
<td colspan="2"></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Tipo contrato</td>
<td><em>Select</em></td>
<td colspan="2"></td>
<td colspan="2"><em>RH_T_CONTRATO_VINCULO.TIPO_CONTRATO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Cargo/Posição</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Designação do cargo ou função que o colaborador irá
desempenhar na instituição.</p>
<p><em><strong>TABELA</strong> : RH_CARGOS ; <strong>CAMPOS</strong>
:cod_cargo e descricao</em></p></td>
<td colspan="2"><p><em>RH_T_TIPOS_RELACIONAMENTO.CARG_ID</em></p>
<p><em>RH_T_CARREIRA.CARGO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Direção</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Unidade orgânica ou direção em que o colaborador está
afeto.<br />
<em>(Ex.: Direção Financeira, Direção de Recursos Humanos)</em></p>
<p><em><strong>FUNÇÃO:</strong> GET_DIRECAO_SERVICO</em></p></td>
<td colspan="2"><p><em>RH_T_TIPOS_RELACIONAMENTO.</em>
<em>INSTIT_ID</em></p>
<p><em>RH_T_MOBILIDADE.INSTIT_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*secção</td>
<td><em>SELECT</em></td>
<td colspan="2" style="text-align: left;"><p>Subunidade ou divisão
dentro da direção onde o colaborador desempenhará funções.<br />
<em>(Ex.: Secção de Contabilidade)</em> (pendente)</p>
<p><strong>FUNÇÃO:</strong> GET_SECCAO (<em>P_ INSTIT_ID)</em></p></td>
<td colspan="2"><p><em>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</em></p>
<p><em>RH_T_MOBILIDADE.SECCAO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Centro custo</td>
<td><em>TEXT</em></td>
<td colspan="2">centro de custo responsável pelas despesas com este
colaborador (pendente) <strong>FUNÇÃO:</strong>
<em>GET_NOME_CENTRO_CUSTO(</em>P_ INSTIT_ID<em>)</em></td>
<td colspan="2"><em>------------------------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td><em>SELECT</em></td>
<td colspan="2">Estrutura profissional a que o colaborador pertence
(pendente) <strong>FUNÇÃO:</strong> <em>GET_CARREIRA (P_CARGO)</em></td>
<td colspan="2"><p><em>RH_T_TIPOS_RELACIONAMENTO.CARR_PCCS_ID</em></p>
<p><em>RH_T_CARREIRA.CARREIRA_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Categoria</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Nível ou grupo profissional do colaborador dentro da
carreira (pendente)</p>
<p><strong>FUNÇÃO:</strong> <em>GET_CATEGORIA(P_CARREIRA)</em></p></td>
<td colspan="2"><p><em>RH_T_TIPOS_RELACIONAMENTO.CATEGORIA_ID</em></p>
<p><em>RH_T_CARREIRA.CATEGORIA_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Escalão / referência</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Escalão salarial ou referência remuneratória
correspondente à posição do colaborador</p>
<p><em><strong>FUNÇÃO:</strong> GET_ESCALAO
(P_CARREIRA,P_CATEGORIA)</em></p></td>
<td colspan="2"><p><em>RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID</em></p>
<p><em>RH_T_CARREIRA. ESCALAO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Tipo de vinculo Laboral</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Natureza do vínculo contratual entre colaborador e
entidade empregadora</p>
<p>(Ex.: Efetivo, Contrato a Termo, Requisição, Estágio)</p>
<p><em><strong>FUNÇÃO:</strong> GET_TIPO_VINCULO</em></p></td>
<td
colspan="2"><p><em><del>RH_T_TIPOS_RELACIONAMENTO.VINCULO_ID</del></em></p>
<p><em>RH_T_CONTRATO_VINCULO. VINCULO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Regime Trabalho</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Condições de prestação de trabalho definidas no
contrato.<br />
<em>(Ex.: Tempo Integral, Tempo Parcial, Teletrabalho, Horário
Flexível)</em></p>
<p><em><strong>DOMAINS</strong> = REGIME_TRABALHO</em></p></td>
<td colspan="2"><p><em>RH_T_REGIME_TRABALHO.REGIME</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.REGIME</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Salário</td>
<td><em>NUMBER</em></td>
<td colspan="2"><p>Valor contratual da remuneração base do
colaborador.</p>
<p><strong>FUNÇÃO</strong> : GET_SALARIO (P_ESCALAO)</p></td>
<td colspan="2"><p><em>RH_T_DEF_REMUNERACOES.VALOR</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.SALARIO</em></p>
<p><em>RH_T_CARREIRA.SALARIO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Moeda</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Moeda em que o salário e demais remunerações são
processados.<br />
<em>(Ex.: CVE, EUR, USD)</em></p>
<p><em><strong>DOMAINS</strong> = MOEDA</em></p>
<ul>
<li><p><em><mark>Por Defeito deve trazer moeda
<strong>CVE</strong></mark></em></p></li>
</ul></td>
<td colspan="2"><p><em>RH_T_DEF_REMUNERACOES.MOEDA</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.MOEDA</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><mark>Valor Convertido</mark></td>
<td></td>
<td colspan="2"><em><strong><mark>Campo que permite convertir moeda em
valor de acordo a moeda selecionado</mark></strong></em></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">*Data Inicio</td>
<td><em>DATE</em></td>
<td colspan="2">Data em que o colaborador inicia efetivamente o
exercício das funções.</td>
<td colspan="2"><p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO_CONTRATO</em></p>
<p><em>RH_T_MOBILIDADE. DATA_INICIO</em></p>
<p><em>RH_T_CARREIRA.DATA_INICIO</em></p>
<p><em>RH_T_REGIME_TRABALHO. DATA_INICIO</em></p>
<p><em>RH_T_CONTRATO_VINCULO. DATA_INICIO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Data Fim</td>
<td><em>DATE</em></td>
<td colspan="2">Data prevista para o termo do vínculo laboral (quando
aplicável).</td>
<td colspan="2"><p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_FIM</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_FIM_CONTRATO</em></p>
<p><em>RH_T_MOBILIDADE. DATA_FIM</em></p>
<p><em>RH_T_CARREIRA. DATA_FIM</em></p>
<p><em>RH_T_CONTRATO_VINCULO. DATA_FIM</em></p>
<p><em>RH_T_REGIME_TRABALHO. DATA_FIM</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Duração (MESES)</td>
<td><em>NUMBER</em></td>
<td colspan="2">Período total de vigência do contrato, expresso em meses
ou anos (aplicável a contratos temporários). (Diferença entre data
inicio funcão e data fim)</td>
<td colspan="2"><em>RH_T_CONTRATO_VINCULO.DURACAO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Local de Trabalho</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Lugar fisico onde o Colaborador exerce o seu
trabalho</p>
<p><strong>FUNÇÃO</strong> : GET_LOCAL_TRABALHO</p></td>
<td colspan="2"><p><em>RH_T_TIPOS_RELACIONAMENTO.LOC_TRAB_ID</em></p>
<p><em>RH_T_MOBILIDADE. LOC_TRAB_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Pais</td>
<td><em>TEXT</em></td>
<td colspan="2"><p>País onde o contrato terá execução</p>
<p><strong>FUNÇÃO:</strong>
GET_PAIS_LOCAL_TRAB(P_LOCAL_TRABALHO)</p></td>
<td
colspan="2"><em>----------------------------------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">Ilha</td>
<td><em>TEXT</em></td>
<td colspan="2"><p>Localidade específica (quando aplicável em contexto
nacional, ex.: Cabo Verde). Apresenta as ilhas correspondentes quando o
país escolhido for Cabo Verde</p>
<p><strong>FUNÇÃO:</strong>
GET_PAIS_ILHA_TRAB(P_LOCAL_TRABALHO)</p></td>
<td
colspan="2"><em>-----------------------------------------------------</em></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><strong>Subsidios :</strong>
Indicação geral sobre a existência de subsídios atribuídos ao
colaborador.</td>
</tr>
<tr>
<td style="text-align: left;">*Tipo de Subsídio</td>
<td><em>SELECT</em></td>
<td colspan="2" style="text-align: left;"><p>Natureza do subsídio
atribuído.<br />
<em>(Ex.: Subsídio de Alimentação, Subsídio de Transporte, Subsídio de
Férias, 13.º mês)</em></p>
<p><em><strong>FUNÇÃO</strong>: GET_MOVIMENTO_REMUNERACAO
(P_TIPO)</em></p></td>
<td colspan="2"><em>RH_T_DEF_REMUNERACOES.TM_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Percentagem</td>
<td><em>TEXT</em></td>
<td colspan="2">Percentagem do salário base usada para calcular o valor
do subsídio (quando aplicável).</td>
<td colspan="2"><em>RH_T_DEF_REMUNERACOES.PERCENTAGEM</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor</td>
<td><em>NUMBER</em></td>
<td colspan="2">Montante atribuído ao colaborador a título de
subsídio</td>
<td colspan="2"><em>RH_T_DEF_REMUNERACOES.VALOR</em></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><strong>Encargos / Descontos
:</strong> Indicação geral sobre os encargos (patronais) ou descontos
(do colaborador) aplicáveis.</td>
</tr>
<tr>
<td style="text-align: left;">*Tipo de Encargos / Descontao</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Identificação do tipo de encargo ou desconto.<br />
<em>(Ex.: INPS, Imposto IRPS, Fundo Social, Sindicato)</em></p>
<p><em><strong>FUNÇÃO</strong>: GET_MOVIMENTO_DESCONTO
(P_TIPO)</em></p></td>
<td colspan="2"><em>RH_T_DEF_PAGAMENTOS.TM_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor</td>
<td><em>NUMBER</em></td>
<td colspan="2">Montante a deduzir ou a assumir pela entidade
empregadora, podendo ser fixo ou percentual.</td>
<td colspan="2"><em>RH_T_DEF_PAGAMENTOS.VALOR</em></td>
</tr>
<tr>
<td style="text-align: left;">*Data Inicio</td>
<td><em>DATE</em></td>
<td colspan="2">Data a partir da qual o encargo/desconto entra em
vigor.</td>
<td colspan="2"><em>RH_T_DEF_PAGAMENTOS.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td colspan="2">Data de cessação do encargo/desconto (quando
aplicável).</td>
<td colspan="2"><em>RH_T_DEF_PAGAMENTOS.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Remuneração</strong></td>
<td></td>
<td colspan="2"></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Remuneração Bruta</td>
<td><em>NUMBER</em></td>
<td colspan="2">Montante total antes da aplicação de impostos e
descontos obrigatórios.</td>
<td colspan="2"><em>Deve somar (salario + subsidio)</em></td>
</tr>
<tr>
<td style="text-align: left;">Total Desconto</td>
<td><em>NUMBER</em></td>
<td colspan="2">Valor total dos descontos aplicados à remuneração do
colaborador.</td>
<td colspan="2"
rowspan="2"><p><em>processamento_salarial_db.CalcularDesAtual</em></p>
<p><em>( ----SUBSISDIO-------</em></p>
<p><em>p_tm_id_subsidio =&gt; tm_id de subsisio,</em></p>
<p><em>p_valor_subsidio =&gt; valor de subsudio,</em></p>
<p><em>----DESCONTO------</em></p>
<p><em>p_tm_id_desconto =&gt; tm_id de desconto,</em></p>
<p><em>p_valor_desconto =&gt; valor de desconto,</em></p>
<p><em>p_tipo_remuneracao =&gt; ’SAL’,</em></p>
<p><em>p_valor_base =&gt; salario,</em></p>
<p><em>P_moeda =&gt; moeda,</em></p>
<p><em>p_data_de =&gt; Data inicio,</em></p>
<p><em>p_total_remun =&gt; devolde</em> Remuneração
<em>Liquido,</em></p>
<p><em>P_total_pagamentos =&gt; devolde total desconto</em></p>
<p><em>)</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Remuneração Líquida</td>
<td><em>NUMBER</em></td>
<td colspan="2">Montante final recebido pelo colaborador após a dedução
de impostos e descontos.</td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><ul>
<li><p><em>Data inicio não pode se superior a data fim de
função</em></p></li>
<li><p><em>Data inicio não ser maior que sysdate</em></p></li>
<li><p><em>Validar campos obrigatorios</em></p></li>
<li><p><em>Os campos <strong>Carreira</strong>, <strong>escalão</strong>
<strong>categroria</strong> ficam visivel caso
<strong>RH_T_PARAM_VINCULO</strong>.FLG_CARREIRA = 1</em></p></li>
<li><p><em>O campo salário fica visivel caso
<strong>RH_T_PARAM_VINCULO</strong>.FLG_SALARIO = 1</em></p></li>
<li><p><em>O campo salário é preenchido automaticamente caso
<strong>RH_T_PARAM_VINCULO</strong>.FLG_CARREIRA = 1</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><strong>GRAVAÇÃO DE OUTROS
CAMPOS</strong></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em><strong>1</strong>
Registo em <strong>RH_T_CONTRATO_VINCULO</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>ESTADO = “<strong>P</strong>”</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>ESTADO_CONTRATO = ‘ATIVO’</em></p></li>
<li><p><em>REFERENCIA = ´ NOVO_CONTRATO ´</em></p></li>
<li><p><em>OBS = = ´ NOVO_CONTRATO</em></p></li>
<li><p><em>VERSAO = 1</em></p></li>
<li><p><em>CONTRATO_ID = ID DE RH_T_CONTRATO_VINCULO</em></p></li>
</ul>
<p><em><mark>1.1 Registo Em
<strong>RH_T_CONTRATO_HISTORICO</strong></mark></em></p>
<p><em>2.1 <strong>RH_T_MOBILIDADE</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>TIPO_SITUACAO = “INICIO”</em></p></li>
<li><p><em>OBS = “NOVO_CONTRATO”</em></p></li>
<li><p><em>CONTRATO_ID = id de RH_T_CONTRATO_VINCULO</em></p></li>
</ul>
<p><em>2.2-registo em <strong>RH_T_CARREIRA</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>TIPO_SITUACAO = “INICIO”</em></p></li>
<li><p><em>OBS = “NOVO_CONTRATO”</em></p></li>
<li><p><em>CONTRATO_ID = id de RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>CONTRATO_VINCULO_ID = ID DE
RH_T_CONTRATO_VINCULO</em></p></li>
</ul>
<p><em>2.3- registo em <strong>RH_T_REGIME </strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>TIPO_SITUACAO = “INICIO”</em></p></li>
<li><p><em>OBS = “NOVO_CONTRATO”</em></p></li>
</ul>
<p><em>2.4- registo em <strong>RH_T_SITUACAO_LABORAL</strong></em></p>
<ul>
<li><p><em>SITUACAO_LABORAL_ID = P ID DE RH_T_PARAM_SITUACAO .NOME =
ATIVO</em></p></li>
<li><p><em>MOTIVO_SIT_LAB = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>DATA_INICIO = DATA INICIO CONTRATO</em></p></li>
<li><p><em>DATA_FIM = DATA FIM CONTRATO</em></p></li>
<li><p><em>FUN_ID = ID DE RH_T_FUNCIONARIO</em></p></li>
<li><p><em><mark>CONTR_VINCULO_ID = ID
RH_T_CONTRATO_VINCULO</mark></em></p></li>
<li><p>ESTADO = ‘P’</p></li>
</ul>
<ul>
<li><p><em>DATA_REGISTO = ‘<strong>SYSDATE’</strong></em></p></li>
</ul>
<ul>
<li><p><em>USER_REGISTO_ID = = id de utilizador Logado</em></p></li>
</ul>
<ul>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
</ul>
<p><em>2.5.Registo em
<strong>RH_T_TIPOS_RELACIONAMENTO</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em><mark>CONTR_VINCULO_ID = ID de tabela
Contrato</mark></em></p></li>
<li><p><em>CARREIRA_ID = id de tabela RH_T_CARREIRA</em></p></li>
<li><p><em>MOB_ID = id de MOBILIDADE</em></p></li>
<li><p><em>REGIME_ID = ID de tabela RH_T_REGIME</em></p></li>
<li><p><em>ESTADO = ‘P’</em></p></li>
<li><p><em>EST_ACT_ADM = 1</em></p></li>
<li><p><em>TIPREL_ID = NULL</em></p></li>
<li><p><em>OBS = “NOVO_CONTRATO”</em></p></li>
<li><p><em>TIPO_SITUACAO = “INICIO”</em></p></li>
<li><p><em>SITUAÇAO_LABORAL_ID = <strong>ID DE
RH_T_SITUACAO_LABORAL</strong></em></p></li>
<li><p><em>REFERENCIA = ‘NOVO_CONTRATO</em></p></li>
</ul></td>
<td colspan="2"><p><em><strong>3.</strong> O sistema deve gravar na
tabela <strong>RH_T_DEF_REMUNERACAO</strong> as informações do separador
de <strong>subsídio</strong> e 1 registo do
<strong>salário</strong></em></p>
<p><em><strong>3.1 separador Subsidio (1 ou varios
registos)</strong></em></p>
<ul>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CONTRATO_ID = CONTRATO_ID de
RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
<li></li>
</ul>
<p><em><strong>3.2 Salario (1 registo) <mark>(pegar tm_id em
rh_t_param_vinculo_mov </mark></strong><mark>onde <strong>tipo = REM e
deve passar como parametro o id de Vinculo)</strong></mark></em></p>
<ul>
<li><p><em>VALOR = Valor do campo Salario do formulario</em></p></li>
<li><p><em>DATA_INICIO = Data inicio de Função do
formulario</em></p></li>
<li><p><em>DATA_FIM = Data Fim de Função</em></p></li>
<li><p><em>TM_ID = <mark>tm_id de
<strong>rh_t_param_vinculo_mov</strong></mark></em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CONTRATO_ID = CONTRATO_ID de
RH_T_CONTRATO_VINCULO</em></p></li>
</ul>
<p><em>3.3- deve ser feito nova associação da tabela
<strong>RH_T_TIPOS_RELACIONAMENTO</strong> e
<strong>RH_T_DEF_REMUNECACAO</strong> na TABELA
<strong><mark>RH_T_TIPREL_REM_PAG</mark></strong></em></p>
<ul>
<li><p><em>REM_ID = ide de RH_T_DEF_REMUNERACAO</em></p></li>
<li><p><em>TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO</em></p></li>
<li><p><em>ESTADO = P</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
</ul>
<p><em><strong>4</strong>.O sistema deve gravar na tabela
<strong>RH_DEF_PAGAMENTOS</strong> as informações do separador de
<strong>Encargos / Descontos</strong></em> <em>e 3 registo de
<strong>IUR</strong> e <strong>INPS</strong> e <strong>SALL <mark>(pegar
tm_id em rh_t_param_vinculo_mov </mark></strong><mark>onde <strong>tipo
= PAG e deve passar como parametro o id de
Vinculo)</strong></mark></em></p>
<p><em><strong>4.1 Separador Encargos / Descontos</strong></em></p>
<ul>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID = ID de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
</ul></td>
<td><ol start="2" type="1">
<li><p><em>IUR</em></p></li>
</ol>
<ul>
<li><p><em>VALOR = 0</em></p></li>
<li><p><em>DATA_INICIO = Data inicio de Função do
formulario</em></p></li>
<li><p><em>DATA_FIM = Data Fim de Funão</em></p></li>
<li><p><em>TM_ID = <mark>tm_id de
<strong>rh_t_param_vinculo_mov</strong></mark></em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CONTRATO_ID = CONTRATO_ID de RH_T_CONTRATO_VINCULO</em></p>
<ol start="2" type="1">
<li><p><em>INPS</em></p></li>
</ol></li>
<li><p><em>VALOR = 0</em></p></li>
<li><p><em>DATA_INICIO = Data inicio de Função do
formulario</em></p></li>
<li><p><em>DATA_FIM = Data Fim de Funão</em></p></li>
<li><p><em>TM_ID =<mark>tm_id de</mark>
<strong><mark>rh_t_param_vinculo_mov</mark></strong></em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CONTRATO_ID = CONTRATO_ID de RH_T_CONTRATO_VINCULO</em></p>
<ol start="2" type="1">
<li><p><em>SALL</em></p></li>
</ol></li>
<li><p><em>VALOR = 0</em></p></li>
<li><p><em>DATA_INICIO = Data inicio de Função do
formulario</em></p></li>
<li><p><em>DATA_FIM = Data Fim de Funão</em></p></li>
<li><p><em>TM_ID =<mark>tm_id de
<strong>rh_t_param_vinculo_mov</strong></mark></em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CONTRATO_ID = CONTRATO_ID de
RH_T_CONTRATO_VINCULO</em></p></li>
</ul>
<p><em>4. deve ser feito uma nova associação da tabela
<strong>RH_T_TIPOS_RELACIONAMENTO</strong> e
<strong>RH_T_DEF_PAGAMENTO</strong> na TABELA
<strong><mark>RH_T_TIPREL_REM_PAG</mark></strong></em></p>
<ul>
<li><p><em>PAG_ID = ide de RH_T_DEF_PAGAMENTO</em></p></li>
<li><p><em>TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO</em></p></li>
<li><p><em>ESTADO = P</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
</ul>
<blockquote>
<p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p>
</blockquote></td>
</tr>
</tbody>
</table>

5)  Dados Bancários

<img src="media/image11.png" style="width:9.69306in;height:2.40486in"
alt="Uma imagem com texto, captura de ecrã, file, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 6%" />
<col style="width: 38%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Entidade Bancária</td>
<td><em>SELECT</em></td>
<td><em><strong>Tabela: RH_BANCO</strong>.NM_BANCO</em></td>
<td><em>RH_T_DADOS_BANCARIOS.RHB_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><mark><del>Nº Conta</del></mark></td>
<td><em><mark><del>TEXT</del></mark></em></td>
<td><mark><del>Numero de conta do banco</del></mark></td>
<td><em><mark><del>RH_T_DADOS_BANCARIOS.NUM_CONTA</del></mark></em></td>
</tr>
<tr>
<td style="text-align: left;">NIB/IBAN</td>
<td><em>TEXT</em></td>
<td>NIB fo colaborador</td>
<td><em>RH_T_DADOS_BANCARIOS.NIB</em></td>
</tr>
<tr>
<td style="text-align: left;">Data inicio</td>
<td><em>DATE</em></td>
<td><mark>Data Inicio deve vir preenchido Automaticamente</mark></td>
<td><em>RH_T_DADOS_BANCARIOS.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_DADOS_BANCARIOS. DATA_FIM</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><mark>Caso o colaborador tenha salario , logo o nib deve ser
obrigatorio. Para este caso o sistema deve emitir uma mensagem de Erro
‘<strong>Erro: O Nib é Obrigatório</strong>’</mark></p></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>GRAVAÇÃO DE OUTROS
CAMPOS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><em><strong>1.registo na
tabela RH_T_DADOS_BANCARIOS</strong></em></p>
<ul>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

6)  Anexar Documento

<img src="media/image12.png" style="width:9.69306in;height:3.14514in"
alt="Uma imagem com texto, número, software, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 25%" />
<col style="width: 5%" />
<col style="width: 36%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Tipo Documento</td>
<td></td>
<td></td>
<td><em>RH_T_DOCUMENTO.TP_DOCUMENTO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Documento</td>
<td></td>
<td></td>
<td><em>RH_T_DOCUMENTO.DOC_ID</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>GRAVAÇÃO DE OUTROS
CAMPOS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><em><strong>1.Outros
registos na tabela RH_T_DOCUMENTO</strong></em></p>
<ul>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>REFERENCIA_NAME =
“<strong>COLABORADOR</strong>”</em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

7)  Outras Gravações

<table>
<colgroup>
<col style="width: 47%" />
<col style="width: 52%" />
</colgroup>
<thead>
<tr>
<th colspan="2"><strong>Gravações (Registo na tabela de Validação e
Auditoria)</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><p><em>1 Registo na tabela de validação
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘REGISTO_COLABORADOR’
(</strong>DOMAINS = ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
<td><ol start="2" type="1">
<li><p><em><strong>O registo de log é guardado automaticamente no
IGRP</strong></em></p></li>
</ol></td>
</tr>
</tbody>
</table>

## Validar 

Após o registo, os dados dos colaboradores são encaminhados para a etapa
de validação. A lista abaixo exibe todos os registos e atualizações
realizados, os quais necessitam de validação antes de serem ativados no
sistema

<img src="media/image13.png" style="width:9.69306in;height:4.33681in"
alt="Uma imagem com texto, software, número, Ícone de computador Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Nome Colaborador</td>
<td><em>TEXT</em></td>
<td>Nome do Colaborador</td>
<td><p><em>RH_T_FUNCIONARIOS.NOME</em></p>
<p><em>(RH_T_VALIDACAO. FUN_ID = RH_T_FUNCIONARIOS.ID)</em></p></td>
</tr>
<tr>
<td style="text-align: center;">Tipo Operação</td>
<td><em>SELECT</em></td>
<td><em>DOMAINS = TIPO_ACAO</em></td>
<td><em>RH_T_VALIDACAO.TIPO_ACCAO</em></td>
</tr>
<tr>
<td style="text-align: center;">Referente a:</td>
<td><em>SELECT</em></td>
<td><p>Permite Pesquisar por area na qual se pretende efectuar a
validação</p>
<p><em>DOMAINS = <strong>ACCAO_REFERENTE</strong></em></p></td>
<td><em>RH_T_VALIDACAO. REFERENCIA</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td><em>DATE</em></td>
<td>Período de Registo da Operação</td>
<td><em>RH_T_VALIDACAO.DATA_REGISTO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td><em>DATE</em></td>
<td>Período de Registo da Operação</td>
<td><em>RH_T_VALIDACAO.DATA_REGISTO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Nome Colaborador</td>
<td><em>TEXT</em></td>
<td>Nome do colaborador</td>
<td><p><em>RH_T_FUNCIONARIOS.NOME</em></p>
<p><em><strong>(RH_T_VALIDACAO.</strong> FUN_ID <strong>=
RH_T_FUNCIONARIOS</strong>.ID)</em></p></td>
</tr>
<tr>
<td style="text-align: center;">Tipo Operação</td>
<td><em>TEXT</em></td>
<td>Tipo de operação (deve apresentar a descricão de
<em><strong>RH_T_DOMAINS.</strong>DESCRICAO</em>)</td>
<td><em>RH_T_VALIDACAO<strong>.</strong>TIPO_ACCAO</em></td>
</tr>
<tr>
<td style="text-align: center;">Referente a</td>
<td><em>TEXT</em></td>
<td>Mostra o registo é referencia a colaborador (deve apresentar a
descricão de <em><strong>RH_T_DOMAINS.</strong>DESCRICAO</em>)</td>
<td><em>RH_T_VALIDACAO<strong>.</strong> REFERENCIA_NAME</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Operação</td>
<td><em>TEXT</em></td>
<td>Data n qual foi feito o registo</td>
<td><em>RH_T_VALIDACAO<strong>.DATA_REGISTO</strong></em></td>
</tr>
<tr>
<td style="text-align: center;">Utilizador</td>
<td><em>TEXT</em></td>
<td>Utilizador que efectou o registo</td>
<td><em>RH_T_VALIDACAO<strong>.</strong>USER_REGISTO_NAME</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>ACÇÕES</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Validar Registo Colaborador</td>
<td colspan="3">Abre-se o mesmo formulário de registo, permitindo ao
validador alterar os dados e validar os registos.</td>
</tr>
<tr>
<td style="text-align: center;">Validar Mobilidade</td>
<td colspan="3">Abre o formulario de Edição de uma mobilidade</td>
</tr>
<tr>
<td style="text-align: center;">Validar Carreira</td>
<td colspan="3">Abre o formulario de Edição de uma Carreira</td>
</tr>
<tr>
<td style="text-align: center;">Validar Contrato</td>
<td colspan="3">Abre o formulario de registo de Desconto</td>
</tr>
<tr>
<td style="text-align: center;">Validar Dados bancarios</td>
<td colspan="3">Abre formulario de registo de dados Bancarios</td>
</tr>
<tr>
<td style="text-align: center;">Validar Remuneracao / Desconto</td>
<td colspan="3">Abre o o formulario registo de Rend / Enc</td>
</tr>
<tr>
<td style="text-align: center;">Validar substituição</td>
<td colspan="3">Abre o formulario de registo de Substituição</td>
</tr>
<tr>
<td style="text-align: center;"><strong>REGRAS</strong></td>
<td colspan="3"></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p>O Campo “<strong>Detalhe de alterações</strong>” fica visivel
somente se o <em><strong>RH_T_VALIDACAO.</strong>TIPO_ACCAO for
diferente de INSERT</em></p></li>
</ul>
<p><u>Regras nos Botões</u></p>
<ul>
<li><p><strong>Detalhe de alterações:</strong> somente fica visivel caso
<em><strong>RH_T_VALIDACAO.</strong>REFERENCIA_NAME <strong>= ‘UPDATE’.
(</strong></em>Permite ver detalhe de alteraçoes feitas na tabela
RH_T_VALIDACAO_DETALHE<em><strong>)</strong></em></p></li>
<li><p><strong>Validar Registo Colaborador</strong>: somente fica
visivel caso o <em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“<strong>REGISTO_COLABORADOR</strong>” (<a
href="#ver-registo-colaborador">VER ESPECIFICAÇÃO</a>)</em></p></li>
<li><p><strong>Validar Mobilidade</strong> : somente fica visivel caso o
<em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“<strong>MOBILIDADE</strong>” (<a href="#novo-editar-mobilidade">VER
ESPECIFICAÇÃO</a>)</em></p></li>
<li><p><strong>Validar Carreira:</strong> somente fica visivel caso o
<em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“<strong>CARREIRA</strong>” (<a href="#novo-editar">VER
ESPECIFICAÇÃO</a>)</em></p></li>
<li><p><strong>Validar Contrato</strong>: somente fica visivel caso o
<em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“<strong>CONTRATO</strong>” (<a href="#novo-contrato">VER
ESPECIFICAÇÃO</a>)</em></p></li>
<li><p><strong>Validar Renovação:</strong> somente fica visivel caso o
<em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“<strong>RENOVACAO_CONTRATO</strong>” (<a href="#renovação">VER
ESPECIFICAÇÃO</a>)</em></p></li>
<li><p><strong>Validar Dados academicos:</strong> somente fica visivel
caso o <em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“DADOS_ACADEMICOS” (<a href="#dados-acedemicos-e-pessoais">VER
ESPECIFICAÇÃO</a>)</em></p></li>
</ul>
<ul>
<li><p><strong>Validar estado Colaborador:</strong> somente fica visivel
caso o <em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“</em>ESTADO_COLABORADOR<em>” (<a href="#inativar-ativar">VER
ESPECIFICAÇÃO</a>)</em></p></li>
</ul>
<ul>
<li><p><strong>Validar Dados bancarios</strong>: somente fica visivel
caso o <em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“<strong>DADOS_BANCARIOS</strong>” (<a href="#novo-contrato">VER
ESPECIFICAÇÃO</a>)</em></p></li>
<li><p><strong>Validar Remuneracao / Desconto</strong>: somente fica
visivel caso o <em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“<strong>RENDIMENTO</strong>” ou “<strong>DESCONTO</strong>” (<a
href="#novo">VER ESPECIFICAÇÃO</a>)</em></p></li>
<li><p><strong>Validar substituição :</strong> somente fica visivel caso
o <em><strong>RH_T_VALIDACAO.</strong> REFERENCIA_NAME =
“<strong>SUBSTITUICAO_MOBILIDADE</strong>” (<a href="#substituição">VER
ESPECIFICAÇÃO</a>)</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Detalhe de alterações

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Campo alterado</td>
<td>TEXT</td>
<td>Indicar qual campo foi alterado</td>
<td><em>RH_T_VALIDACAO_DETALHE. CAMPO_ALTERADO</em></td>
</tr>
<tr>
<td style="text-align: center;">Valor anterior</td>
<td>TEXT</td>
<td>Qual o valor que o campo Tinha Antes</td>
<td><em>RH_T_VALIDACAO_DETALHE. VALOR_ANTERIOR</em></td>
</tr>
<tr>
<td style="text-align: center;">Novo Valor</td>
<td>TEXT</td>
<td>Qual o campo tem agora</td>
<td><em>RH_T_VALIDACAO_DETALHE. VALOR_NOVO</em></td>
</tr>
<tr>
<td style="text-align: center;">Alterado Por</td>
<td>TEXT</td>
<td>O utilizador responsavel pela alteração</td>
<td><em>RH_T_VALIDACAO_DETALHE . USER_REGISTO_NAME</em></td>
</tr>
<tr>
<td style="text-align: center;">Data da Alteração</td>
<td>DATE</td>
<td>Data de alteração</td>
<td><em>RH_T_VALIDACAO_DETALHE.DATA_REGISTO</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em>Para trazer informações de detalhe na tabela
<strong>RH_T_VALIDACAO_DETALHE,</strong> onde VALIDACAO_ID
<strong>=</strong> ID de <strong>RH_T_VALIDACAO</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Ver Registo Colaborador

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th colspan="2"><strong>REGRAS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><ul>
<li><p>Ao abrir o formulário de registo do colaborador, o campo
<em><strong>Validar</strong></em> deve ficar visível.</p></li>
<li><p>O formulário deve ser automaticamente preenchido com todas as
informações já registadas no registo de colaborador (<a
href="#registo-do-colaborador">ver formulário</a>).</p></li>
<li><p><em>Caso o validador altere algum campo, o sistema deve atualizar
de imediato as tabelas de negócio especificadas acima e, adicionalmente,
registar o log das alterações nas tabelas.</em></p></li>
<li><p><em>O log é registado para cada <strong>tabela</strong> que
sofrer alterações. Ou seja se um campo de uma tabela associado na edição
de um colaborador for alterado , logo faz um rh_registo na tabela
<strong>RH_T_VALIDACAO_DETALHE</strong></em></p></li>
<li><p><em>O <strong>log</strong> é registado sempre que uma tabela
sofrer alterações. Ou seja, se algum <strong>campo de uma tabela
associada à edição de um colaborador</strong> for alterado, o sistema
deve:</em></p>
<ul>
<li><p><em>Criar um registo na tabela
<strong>RH_T_LOG</strong>;</em></p></li>
<li><p><em>Guardar o detalhe do <strong>campo alterado</strong> na
tabela <strong>RH_T_VALIDACAO_DETALHE</strong> (incluindo valor anterior
e novo valor</em></p></li>
</ul></li>
<li><p><em>Regista na tabela <strong>RH_T_VALIDACAO_DETALHE</strong>
apenas quando o valor anterior de cada campo for diferente do valor
novo.</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><strong>ATUALIZAÇÃO / GRAVAÇÃO</strong></td>
</tr>
<tr>
<td><p>1-Caso o utilizador <strong><u>altere</u></strong> alguma
informação em qualquer formulário e <strong><u>não efetue a
validação</u></strong></p>
<p>-Deve atualizar os campos atualizados das tabelas correspondentes ao
formulário em causa e outros seguintes campos<strong>:</strong></p>
<ul>
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE</strong></em></p></li>
</ul>
<p>1.2-Caso o utilizador <strong><u>altere</u></strong> alguma
informação em qualquer formulário e <strong><u>efetue a
validação</u></strong></p>
<p>Além das atualizações indicadas no ponto <strong>1.1</strong>, também
deve atualizar o estado de validação nas seguintes tabelas:</p>
<ul>
<li><p><strong>RH_T_VALIDACAO</strong>.<em>ESTADO = “A”</em></p></li>
<li><p><strong>RH_T_FUNCIONARIOS.</strong> <em>ESTADO_VALIDACAO =
“A”</em></p></li>
<li><p><strong>RH_T_CONTRATO_VINCULO.</strong>ESTADO <strong>=
‘A’</strong></p></li>
<li><p><strong>RH_T_SITUACAO_LABORAL = ‘A’</strong></p></li>
<li><p><strong>RH_T_TIPOS_RELACIONAMENTO.</strong> <em>ESTADO =
“A”</em></p></li>
<li><p><strong>RH_T_DEF_REMUNERACAO.</strong> <em>ESTADO =
“A”</em></p></li>
<li><p><strong>RH_T_DEF_PAGAMENTO.</strong> <em>ESTADO =
“A”</em></p></li>
<li><p><strong>RH_T_REMUN_TIPREL</strong></p></li>
</ul>
<p>1.2.1-Ao Validar gera um ordem de Serviço na tabela
<strong>RH_T_ORDEM_SERVICO</strong></p>
<ul>
<li><p>DESCRICAO = ‘Registo de colaborador - ’ ||
RH_T_FUNCIONARIOS.NOME</p></li>
<li><p>REFERENTE = ‘REGISTO_COLABORADOR’</p></li>
<li><p>FUN_ID = RH_T_FUNCIONARIOS.ID</p></li>
<li><p>CONTRATO_ID = RH_T_CONTRATO_VINCULO.ID</p></li>
<li><p>TIPREL_ID = RH_T_TIPOS_RELACIONAMENTO.ID</p></li>
<li><p>VALIDACAO_ID = RH_T_VALIDACAO.ID</p></li>
</ul>
<p>1.3-Caso o utilizador não valide</p>
<ul>
<li><p><strong>RH_T_VALIDACAO</strong>.<em>ESTADO = “I”</em></p></li>
<li><p><strong>RH_T_FUNCIONARIOS.</strong> <em>ESTADO_VALIDACAO =
“I”</em></p></li>
<li><p><strong>RH_T_CONTRATO_VINCULO.</strong>ESTADO <strong>=
‘I’</strong></p></li>
<li><p><strong>RH_T_SITUACAO_LABORAL = ‘I’</strong></p></li>
<li><p><strong>RH_T_TIPOS_RELACIONAMENTO.</strong> <em>ESTADO =
“I”</em></p></li>
<li><p><strong>RH_T_DEF_REMUNERACAO.</strong> <em>ESTADO =
“I”</em></p></li>
<li><p><strong>RH_T_DEF_PAGAMENTO.</strong> <em>ESTADO =
“I”</em></p></li>
</ul></td>
<td style="text-align: left;"><p>1.4-Caso for alterado o valor do
<strong>salário</strong> ou <strong>data inicio de função</strong> ou
<strong>data fim de função</strong>, deve fazer atualização na tabela
<strong>RH_T_DEF_REMUNERACAO</strong> onde o
<strong>TM_ID=</strong>GET_MOVIMENTO_SALL e
<strong>TIPREL_ID</strong>=ID de RH_T_TIPOS_RELACIONAMENTO</p>
<ul>
<li><p><em>VALOR</em> = novo valor de salario do formulario</p></li>
<li><p><em>DATA_INICIO</em> <strong>=</strong> Data inicio de
função</p></li>
<li><p><em>DATA_FIM</em> = Data fim de função</p></li>
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE</strong></em></p></li>
</ul>
<p><em><strong>2.2- O r</strong>egisto de log é guardao automaticamente
no IGRP</em></p></td>
</tr>
</tbody>
</table>

## Lista de Colaboradores 

<img src="media/image14.png" style="width:9.69306in;height:4.97917in"
alt="Uma imagem com texto, captura de ecrã, software, número Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 6%" />
<col style="width: 38%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Nª Colaborador</td>
<td><em>NUMBER</em></td>
<td>Numero de colaborador</td>
<td><em>RH_V_DOSSIE.ID_COLABORADOR</em></td>
</tr>
<tr>
<td style="text-align: center;">Nome</td>
<td><em>TEXT</em></td>
<td>Nome de Colaborador</td>
<td><em>RH_V_DOSSIE.NOME</em></td>
</tr>
<tr>
<td style="text-align: center;">Direcção</td>
<td><em>SELECT</em></td>
<td><p>Buscar dados no:</p>
<p><em><strong>FUNÇÃO</strong>: GET_DIRECAO_SERVICO</em></p></td>
<td><em>RH_V_DOSSIE. DIRECAO_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Seccão</td>
<td><em>SELECT</em></td>
<td><p><em>Buscar dados no:</em></p>
<p><em><strong>FUNÇÃO</strong>: GET_SECCAO (P_ SECCAO_ID)</em></p></td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.SESSAO_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Tipo Vinculo Laboral</td>
<td><em>SELECT</em></td>
<td><p>Buscar dados no:</p>
<p><em><strong>FUNÇÃO</strong>: GET_TIPO_VINCULO</em></p></td>
<td><em>RH_T_TIPOS_RELACIONAMENTO. VINCULO_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_TIPOS_RELACIONAMENTO. DATA_INICIO_CONTRATO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_TIPOS_RELACIONAMENTO. DATA_FIM_CONTRATO</em></td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>RH_T_FUNCIONARIOS. ESTADO_COLABORADOR</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Estado do Registo</td>
<td><em>TEXT</em></td>
<td>Indica se o registo de colaborador esté validado ou não</td>
<td><em>RH_V_DOSSIE.ESTADO_VALIDACAO</em></td>
</tr>
<tr>
<td style="text-align: center;">Estado do Colaborador</td>
<td><em>TEXT</em></td>
<td>Estado do colaborador (ativo ou inativo)</td>
<td><em>RH_V_DOSSIE. ESTADO_COLABORADOR</em></td>
</tr>
<tr>
<td style="text-align: center;">Nº Colaborador</td>
<td><em>TEXT</em></td>
<td>Número do colaborador</td>
<td><em>RH_V_DOSSIE.ID_COLABORADOR</em></td>
</tr>
<tr>
<td style="text-align: center;">Nome</td>
<td><em>TEXT</em></td>
<td>Nome do colaborador</td>
<td><em>RH_V_DOSSIE.NOME</em></td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td><em>TEXT</em></td>
<td>Cargo que desempenha o colaborador</td>
<td><em>RH_V_DOSSIE.CARGO_DESC</em></td>
</tr>
<tr>
<td style="text-align: center;">Data inicio</td>
<td><em>TEXT</em></td>
<td>Data inicio de função do colaborador</td>
<td><em>RH_V_DOSSIE. DATA_INICIO_CONTRATO</em></td>
</tr>
<tr>
<td style="text-align: center;">Direcção</td>
<td><em>TEXT</em></td>
<td>Direcção de trabalho do colaborador</td>
<td><em>RH_V_DOSSIE.DIRECAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: center;">Secção</td>
<td><em>TEXT</em></td>
<td>Secção de trabalho do colaborador</td>
<td><em>RH_V_DOSSIE.SECCAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: center;">Carreira / Categoria</td>
<td></td>
<td>Carreira/ categoria do Colaborador</td>
<td><em>RH_V_DOSSIE.CARREIRA_DESC / RH_V_DOSSIE.CATEGORIA_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Ver Dossiê do colaborador</td>
<td colspan="3">Abrir um formulario para ver Dossie do Colaborador</td>
</tr>
<tr>
<td style="text-align: center;">Inativar / Ativar Colaborador</td>
<td colspan="3">Permite ativar ou inativar um colaborador, deve ter uma
transação associada</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>REGRA</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em>Se o <strong>estado de validação</strong> do colaborador
estiver em <strong>“Pendente”</strong>, os botões <strong>“Ver Dossiê do
Colaborador”</strong> e <strong>“Inativar/Ativar Colaborador”</strong>
devem ficar visíveis.</em></p></li>
</ul>
<blockquote>
<p><em>Ou seja, para colaboradores cujo campo
<strong>RH_T_COLABORADORES.ESTADO_VALIDACAO = 'P'</strong>.)</em></p>
</blockquote>
<ul>
<li><p><em>A <strong>lista</strong> deve ser preenchida apenas após a
aplicação do <strong>filtro</strong>.</em></p></li>
<li><p><em>Por <strong>defeito</strong>, a lista deve apresentar apenas
os colaboradores com <strong>estado = 'A'</strong>.</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

## Inativar / Ativar 

<img src="media/image15.png" style="width:9.69306in;height:3.68194in" />

<table>
<colgroup>
<col style="width: 24%" />
<col style="width: 6%" />
<col style="width: 19%" />
<col style="width: 16%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td colspan="2"><p>Fica visivel somente no modo validar</p>
<p><strong>DOMAINS</strong> = STATUS</p></td>
<td><p><em>RH_T_FUNCIONARIOS.ESTADO</em></p>
<p><em>RH_T_SITUACAO_LABORAL.ESTADO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td></td>
<td colspan="2"><strong><mark>DOMAIN =
ESTADO_CONTRATO</mark></strong></td>
<td><em>---------------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">Situação Laboral</td>
<td></td>
<td colspan="2"><p>A funcionalidade permite <strong>alterar o estado de
um colaborador</strong> entre <strong>Ativo</strong> e
<strong>Inativo</strong>, de acordo com o seu estado atual.</p>
<p>, Deve trazer somente valor ´ATIVO´ e CESSADO de
<mark>(<em><strong>RH_T_PARAM_SITUACAO</strong>.FLG_ESTADO_CONTRATO</em>)</mark></p>
<p>Nota: para isso buscar as situcoes cuho o estado do contrao no
vinculo associado ao colaborador
(<strong>RH_T_CONTRATO_VINCULO</strong>.VINCULO_ID,
<strong>RH_T_PARAM_VINCULO</strong>.ID ,
<strong>RH_T_PARAM_SIT_LABORAL.</strong> VINCULO_ID)</p></td>
<td><em>RH_T_SITUACAO_LABORAL. SITUACAO_LABORAL_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Motivo</td>
<td></td>
<td colspan="2"><p>Ao alterar o estado, o utilizador deve
<strong>indicar obrigatoriamente o motivo da alteração</strong></p>
<p><strong>RH_T_PARAM_SITUACAO_DET.NOME,
RH_T_PARAM_SITUACAO_DET.ID</strong></p></td>
<td><p><em>RH_T_SITUACAO_LABORAL. MOTIVO_SIT_LAB_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. MOTIVO_SIT_LAB_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Data início</td>
<td>DATE</td>
<td colspan="2"></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">DATA FIM</td>
<td>DATE</td>
<td colspan="2"></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td></td>
<td colspan="2"></td>
<td><p><em>RH_T_SITUACAO_LABORAL. OBS</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. OBS</em></p></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ul>
<li><p><em>O sistema deve permitir visualizar dados de um colaborador
inativo, mas não deve ser possivel realizar nenhuma ação em cima del ,
ou seja deve ser retirado accão a qualquer ou outro botão que não seja
ativar / Inativar colaborador</em></p></li>
<li><p><em>Este botão deve ter uma transação, somente utilizador
atribuido acesso, deve conseguir executalo</em></p></li>
<li><p><em>O sistema deve registar Log de alteração</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>GRAVAÇÃO DE OUTROS
CAMPOS</strong></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em>1.Faz update nos
registos anteriores , ou seja inativa os registos ativos</em></p>
<p><em>1.1Inativa a mobilidade em estado ativo
<strong>RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e data fim is not
null)</strong></em></p>
<ul>
<li><p><em>DATA_FIM = data Inicio</em></p></li>
<li><p><em>USER_ALTERACAO _ID = utilizador logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>nome de
utilizador</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = sysdate</em></p></li>
<li><p><em>EST_ACT_ADM = 0</em></p></li>
</ul>
<p><em>1.2 Fazer uma nova gravação na tabela de
<strong>RH_T_TIPOS_RELACIONAMENTO</strong>, pegas todas informações do
registo anterior, e regista com novas alteraçoes nos campos do
formulario e outros seguinte campos</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>TIPREL_ID = ID DO REGISTO FECHADO (id de
RH_TIPOS_RELAIONAMENTO)</em></p></li>
<li><p><em>SITUACAO_LAB_ID = ID DE SITUACAO LABORAL</em></p></li>
<li><p><em>EST_ACT_ADM = 1</em></p></li>
<li><p><em>REFERENTE = ‘SITUACAO_LABORAL’</em></p></li>
<li><p><em>TIPO_SITUACAO = ´</em> MUDANCA_SITUACAO_LAB
<em>´</em></p></li>
<li><p><em>ESTADO = ‘P’</em></p></li>
</ul>
<p><em>1.3 <mark>gravação na tabela
<strong>RH_T_TIPREL_REM_PAG</strong></mark></em></p>
<p><em><strong><mark>Pega os dados do tiprel_id fechado e cria um novo
registo com novo Tiprel_id</mark></strong></em></p>
<ul>
<li><p><em><mark>TIPREL_ID = novo Tiprel_id</mark></em></p></li>
<li><p><em><mark>REM_ID = pega o mesmo anteriro</mark></em></p></li>
<li><p><em><mark>PAG_ID = pega o mesmo anterior</mark></em></p></li>
</ul></td>
<td colspan="2" style="text-align: left;"><p><em>2-insert em
<strong>RH_T_SITUACAO_LABORAL</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>ESTADO = “<strong>P</strong>”</em></p></li>
<li><p><em>CONTR_VINCULO_ID = id de RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>CONTRATO_ID = CONTRATO_ID DE
RH_T_CONTRATO_VINCULO</em></p></li>
</ul>
<ol type="1">
<li><p><em>Atualiza na tabela <strong>RH_T_FUNCIONARIOS</strong> dados
do formulario e outros seguintes dados</em></p></li>
</ol>
<ul>
<li><p><em>USER_ALTERACAO_ID = id de utilizador logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = nome de utilizador logado</em></p></li>
<li><p><em>DATA_ALTERACAO = SYSDATE</em></p></li>
<li><p><em>Estado = <strong>ATIVO</strong> OU <strong>INATIVO,
dependendo da situação cessado = I</strong></em></p></li>
</ul>
<p><em>1.1 <strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘UPDATE’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘ESTADO_COLABORADOR’
(</strong>DOMAINS = ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS</strong> TIPREL_ID <strong>=
NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"></td>
<td colspan="2"><p><em><strong>2.1.</strong> Registo Detalhe de LOG na
tabela <strong>RH_T_VALIDACAO_DETALHE (faz registo de cada campo
alterado)</strong></em></p>
<ul>
<li><p><em>VALIDACAO_ID <strong>= id de tabela
RH_T_VALIDACAO</strong></em></p></li>
<li><p><em>CAMPO_ALTERADO <strong>= ESTADO ou OBS</strong></em></p></li>
<li><p><em>VALOR_ANTERIOR = valor queo campo estado tinha
antes</em></p></li>
<li><p><em>VALOR_NOVO = Valor do campo do estado atual</em></p></li>
<li><p><em>TABELA_NAME = “ nome de tabela a ser registado”</em></p></li>
<li><p><em>TABELA _ID = “id de tabela a ser registado</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Validar 

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><ul>
<li><p>A validação invoca a mesma página de Registo</p></li>
<li><p>O campo validar deve ficar visivel</p></li>
<li><p>Ao <strong>validar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'A'</strong>.</p></li>
<li><p>Ao <strong>desvalidar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
</ul>
<p><em>3-Caso o Caso o tipo de situação for ‘Cessar’, logo deve fazer
update nas seguintes tabelas:</em></p>
<ul>
<li><p><em>RH_T_CONTRATO_VINCULO.DATA_FIM</em></p></li>
<li><p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_FIM</em></p></li>
<li><p><em>RH_T_DEF_REMUNERACAO.DATA_FIM</em></p></li>
<li><p><em>RH_T_CARREIRA.DATA_FIM</em></p></li>
<li><p><em>RH_T_MOBILIDADE.DATA_FIM</em></p></li>
<li><p><em>RH_T_DEF_PAGAMENTO.DATA_FIM</em></p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

## Dossier do Colaborador 

### Perfil do colaborador

#### Dados Pessoais

<img src="media/image16.png" style="width:7.14097in;height:3.80208in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 7%" />
<col style="width: 2%" />
<col style="width: 37%" />
<col style="width: 31%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>UPDATE</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="5" style="text-align: center;"><strong>identificação do
Colaborador (</strong>pega dados na vista
<em><strong>RH_V_DOSSIE</strong> onde <strong>ULTIMO_VINCULO =
1</strong></em><strong>)</strong></td>
</tr>
<tr>
<td style="text-align: left;"><del>Validar</del></td>
<td><em><del>RADIOLIST</del></em></td>
<td colspan="2"><del>Fica visivel somente no modo validar</del></td>
<td><em><del>RH_T_FUNCIONARIOS.ESTADO</del></em></td>
</tr>
<tr>
<td style="text-align: left;">*Tipo Documento Identificação</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Tipo de documento de identificação do Colaborador</p>
<p><strong>Fonte dados</strong>: RH_V_DOSSIE.
<em>TIPO_DOCUMENTO</em></p></td>
<td><p><em>RH_T_FUNCIONARIOS.TIPO_DOCUMENTO</em></p>
<p><em>RH_T_DOCUMENTO_PESSOAL.TIPO_DOCUMENTO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">* N.º Documento de Identificação</td>
<td><em>TEXT</em></td>
<td colspan="2"><p>Numero documento de identificação do colaborador</p>
<p><strong>Fonte dados</strong>: <em>RH_V_DOSSIE.
NUM_DOCUMENTO</em></p></td>
<td><p><em>RH_T_FUNCIONARIOS.NUM_DOCUMENTO</em></p>
<p><em>RH_T_DOCUMENTO_PESSOAL.NUM_DOCUMENTO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Nome</td>
<td><em>TEXT</em></td>
<td colspan="2"><p>Número do documento apresentado para fins de
identificação</p>
<p><strong>Fonte dados</strong>: <em>RH_V_DOSSIE. NOME</em></p></td>
<td><em>RH_T_FUNCIONARIOS.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">Foto</td>
<td><em>IMAGEM</em></td>
<td colspan="2">PENDENTE: decidir como será registado (ver com ATY)</td>
<td><em>RH_FUNCIONARIOS.FOTOGRAFIA</em></td>
</tr>
<tr>
<td style="text-align: left;">*Data Nascimento</td>
<td><em>DATE</em></td>
<td colspan="2"><p>Nome completo do colaborador, conforme o documento de
identificação</p>
<p><strong>Fonte dados</strong>:
<em>RH_V_DOSSIE.DATA_NASCIMENTO</em></p></td>
<td><em>RH_T_FUNCIONARIOS.DATA_NASCIMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Género</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Data do Nascimento do colaborador
(<em><strong>DOMAINS</strong> = GENERO</em>)</p>
<p><strong>Fonte dados</strong>: <em>RH_V_DOSSIE.SEXO</em></p></td>
<td><em>RH_T_FUNCIONARIOS.SEXO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Nome Mãe</td>
<td><em>TEXT</em></td>
<td colspan="2"><p>Nome completo da mãe do colaborador</p>
<p><strong>Fonte dados</strong>: <em>RH_V_DOSSIE.NM_MAE</em></p></td>
<td><em>RH_T_FUNCIONARIOS.NM_MAE</em></td>
</tr>
<tr>
<td style="text-align: left;">*Nome Pai</td>
<td><em>TEXT</em></td>
<td colspan="2"><p>Nome completo do pai do colaborador</p>
<p><strong>Fonte dados</strong>: <em>RH_V_DOSSIE.NM_PAI</em></p></td>
<td><em>RH_T_FUNCIONARIOS.NM_PAI</em></td>
</tr>
<tr>
<td style="text-align: left;">*Estado Civil</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Esrado civil atual do colaborador
(<em><strong>DOMAINS</strong>=ESTADO_CIVIL</em>)</p>
<p><strong>Fonte dados</strong>:
<em>RH_V_DOSSIE.ESTADO_CIVIL</em></p></td>
<td><em>RH_T_FUNCIONARIOS.ESTADO_CIVIL</em></td>
</tr>
<tr>
<td style="text-align: left;">*Nacionalidade</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>País de nacionalidade do colaborador</p>
<p><strong>Função</strong>: GET_GEOGRAFIA</p>
<p><strong>Fonte dados</strong>:
<em>RH_V_DOSSIE.NACIONALIDADE</em></p></td>
<td><em>RH_T_FUNCIONARIOS.NACIONALIDADE</em></td>
</tr>
<tr>
<td style="text-align: left;">*Naturalidade</td>
<td><em>LOCKUP</em></td>
<td colspan="2"><p>Local de nascimento do colaborador
(<em>GEOGRAFIA</em>)</p>
<p><strong>Fonte dados</strong>: <em>RH_V_DOSSIE.LOC_NASC_ID e
RH_V_DOSSIE. LOC_NASC_NOME</em></p></td>
<td><em>RH_T_FUNCIONARIOS.LOC_NASC_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">DATA_EMISSAO</td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Documentos
Administrativos</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">NIF</td>
<td><em>NUMBER</em></td>
<td colspan="2"><p>Numero de identificação Fiscal</p>
<p>*PENDENTE: API Pesquisa NIF</p>
<p><strong>Fonte dados</strong>: <em>RH_V_DOSSIE.NIF</em></p></td>
<td><em>RH_T_FUNCIONARIOS.NIF</em></td>
</tr>
<tr>
<td style="text-align: left;">N.º Segurado</td>
<td><em>NUMBER</em></td>
<td colspan="2"><p>Número de Identificação Fiscal do colaborador</p>
<p>*PENDENTE:API pesquisa segurado</p>
<p><strong>Fonte dados</strong>: <em>RH_V_DOSSIE.
NU_SEG_INPS</em></p></td>
<td><em>RH_FUNCIONARIOS.NU_SEG_INPS</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>Contacto
(</strong>busca dados na vista <strong>RH_V_CONTATO,</strong> passando
como paramentro <strong>FUN_ID</strong> = ID de <strong>RH_T_FUNCIONARIO
)</strong></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Contacto</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Tipo de Contacto do colaborador (Telemóvel, Telefone,
Email)</p>
<p><em><strong>DOMAINS</strong> = TP_CONTACTO</em></p>
<p><strong>Fonte dados</strong>:
RH_V_CONTATO.<em>TIPO_CONTACTO</em></p></td>
<td><em>RH_T_CONTACTO.TIPO_CONTACTO</em></td>
</tr>
<tr>
<td style="text-align: left;">Contacto</td>
<td><em>TEXT</em></td>
<td colspan="2"><p>Número de telefone, endereço de e-mail ou outro
contacto indicado pelo colaborador.</p>
<p><strong>Fonte dados</strong>: RH_V_CONTATO.<em>CONTACTO</em></p></td>
<td><em>RH_T_CONTACTO.CONTACTO</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>Endereço
(</strong>busca dados de Geografia no schema <strong>Global na</strong>
tabela <strong>GLB_T_GEOGRAFIA)</strong></td>
</tr>
<tr>
<td style="text-align: left;">Pais</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>País onde o colaborador reside atualmente.</p>
<p><strong>Função</strong>: GET_GEOGRAFIA (P_NIVEL = 1)</p>
<p><strong>Fonte dados</strong>: <em>RH_V_ ENDERECO _FUNC.
PAIS_ID</em></p></td>
<td><em>RH_T_ENDERECO.PAIS_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Ilha</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Ilha de residência do colaborador</p>
<p><strong>Função</strong>: GET_GEOGRAFIA (P_NIVEL = 2)</p>
<p><strong>Fonte dados</strong>: <em>RH_V_ ENDERECO _FUNC.
ILHA_ID</em></p></td>
<td><em>RH_T_ENDERECO.ILHA_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Concelho</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Município onde o colaborador reside.</p>
<p><strong>Função:</strong> GET_GEOGRAFIA (P_NIVEL = 3)</p>
<p><strong>Fonte dados</strong>: <em>RH_V_ ENDERECO _FUNC.
ILHA_ID</em></p></td>
<td><em>RH_T_ENDERECO.CONCELHO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Freguesia</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Divisão administrativa onde se encontra a residência
do colaborador.</p>
<p><strong>Função:</strong> GET_GEOGRAFIA (P_NIVEL = 4)</p>
<p><strong>Fonte dados</strong>: <em>RH_V_ ENDERECO .
FREGUESIA_ID</em></p></td>
<td><em>RH_T_ENDERECO.FREGUESIA_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Zona</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Bairro, distrito ou localidade dentro da
freguesia.</p>
<p><strong>Função:</strong> GET_GEOGRAFIA (P_NIVEL = 5)</p>
<p><strong>Fonte dados</strong>: <em>RH_V_
ENDERECO.ZONA_ID</em></p></td>
<td><em>RH_T_ENDERECO.ZONA_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Morada</td>
<td><em>TEXT</em></td>
<td colspan="2">Endereço completo e detalhado do colaborador, incluindo
rua, número de casa, referência, etc</td>
<td><em>RH_T_ENDERECO.MORADA</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td style="text-align: left;">Eliminar</td>
<td colspan="4"><em>Ao eliminar um registo existente na tabela, o
sistema deve atualizar de imediato o respetivo estado. (estado =
‘I’)</em></td>
</tr>
<tr>
<td style="text-align: left;">Editar</td>
<td colspan="4"><em>Ao atualizar, devem ser guardadas as informações do
formulário, bem como os outros campos indicados abaixo</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ul>
<li><p><em>A validação dos campos obrigatórios deve ser efetuada tanto
ao nível do formulário como ao nível da tabela</em></p></li>
<li><p><em>O sistema não permite duplicação de colaborador. Caso seja
detetado um número de documento já registado, deve ser emitida uma
mensagem de ERRO (“Já existe um colaborador registado com este número de
documento”). A combinação entre o tipo de documento e o número de
documento deve ser única</em></p></li>
<li><p><em>O sistema deve validar se o <strong>NIF</strong> indicado
corresponde efetivamente ao colaborador através da <strong>API</strong>
disponibilizado. Mensagem de ERRO: ‘’O NIF introduzido não corresponde
ao colaborador selecionado<strong>”.</strong> Validar com nome, data
nascimento, nome de mae e pai</em></p></li>
<li><p><em>O sistema deve validar se o <strong>N.º Segurado</strong>
indicado corresponde efetivamente ao colaborador através da
<strong>API</strong> disponibilizado. Mensagem de ERRO: ‘’O Nº segurado
introduzido não corresponde ao colaborador
selecionado<strong>”.</strong> Validar com nome, data nascimento, nome
de mae e pai.</em></p></li>
<li><p><em>Validar se o contacto já está associado a outro funcionário e
emitir um ALERTA: “O contacto informado já está associado a outro
colaborador”</em></p></li>
<li><p><em>Ao Alterar documento de um funcionario deve garantir que seja
o mesmo nome de funcionario antes atraves do <strong>nome</strong>,
<strong>data nascimento</strong>, <strong>nome de mae e
pai</strong></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>ATUALIZAÇÃO DE
OUTROS CAMPOS</strong></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><ol type="1">
<li><p><em><strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ol>
<ul>
<li><p><em>ESTADO = ‘A’</em></p></li>
<li><p><em>ESTADO_VALIDACAO = “<strong>P</strong>”</em></p></li>
<li><p><em>USER_ALTERACAO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = SYSDATE’</em></p></li>
</ul></td>
<td><ol start="2" type="1">
<li><p><em><strong>RH_T_CONTACTO</strong> (caso for alterado
contato<strong>)</strong></em></p></li>
</ol>
<ul>
<li><p><em>ESTADO = ‘A’</em></p></li>
<li><p><em>USER_REGISTO _ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO = <strong>SYSDATE</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID = <strong>ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ul></td>
<td><ol start="3" type="1">
<li><p><em><strong>RH_T_ENDERECO</strong> (caso for alterado
endere<strong>)</strong></em></p></li>
</ol>
<ul>
<li><p><em>ESTADO = ‘A’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO_ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ REGISTO = <strong>SYSDATE</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID= id de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><ol start="4" type="1">
<li><p><em><strong>RH_T_DOCUMENTO_PESSOAL</strong></em></p>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID= ID de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
</ul></li>
</ol></td>
<td><ol start="5" type="1">
<li><p><em><del>Deve registar na tabela de validação
(<strong>RH_T_VALIDACAO</strong>) caso for alterado um dos seguintes
campos:</del></em></p></li>
</ol>
<ul>
<li><p><em><del>Tipo de documento</del></em></p></li>
<li><p><em><del>Numero documento</del></em></p></li>
<li><p><em><del>NIF</del></em></p></li>
<li><p><em><del>N.º Segurado</del></em></p></li>
</ul>
<p><em><strong><del>RH_T_VALIDACAO</del></strong></em></p>
<ul>
<li><p><em><del>TIPO_ACCAO<strong>= ‘UPDATE’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></del></em></p></li>
<li><p><em><del>REFERENCIA_NAME <strong>= ‘DADOS_PESSOAIS’
(</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></del></em></p></li>
<li><p><em><del>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></del></em></p></li>
<li><p><em><del>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS</strong> TIPREL_ID <strong>=
NULL</strong></del></em></p></li>
<li><p><em><del>DATA_REGISTO <strong>= SYSDATE
</strong></del></em></p></li>
<li><p><em><del>USER_REGISTO_NAME = nome de utilizador
Logado</del></em></p></li>
<li><p><em><del>USER_REGISTO_ID = id de utilizador
Logado</del></em></p></li>
<li><p><em><del>ESTADO <strong>= ‘P’</strong></del></em></p></li>
</ul></td>
<td><ol start="6" type="1">
<li><p><em>Deve registar log de alteração em cada tabela que sofrer
alteração</em></p></li>
</ol>
<p><em>6.1Registo de log <strong>RH_T_LOG</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO <strong>= UPDATE (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>TABELA_NAME <strong>= ‘nome tabela’</strong></em></p></li>
<li><p><em>TABELA _ID <strong>= ID</strong> de tabela</em></p></li>
<li><p><em>FUN_ID = ID de colaborador
(<strong>RH_T_FUNCIONARIOS.ID</strong>)</em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>USER_REGISTO_ID <strong>= ID utilizador que fez a
ação</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME <strong>= Nome do utilizador que fez a
ação</strong></em></p></li>
<li><p><em>DATA_REGISTO = SYSDATE</em></p></li>
<li><p><em>VALIDACAO_ID = id de tabela de RH_T_VALIDACAO</em></p></li>
</ul>
<p><em>6.2Registo Detalhe de LOG na tabela
<strong>RH_T_VALIDACAO_DETALHE (Faz 2 registo 1 para estado e outro para
OBS)</strong></em></p>
<ul>
<li><p><em>VALIDACAO_ID <strong>= id de tabela</strong>
RH_T_VALIDACAO</em></p></li>
<li><p><em>CAMPO_ALTERADO <strong>= ESTADO ou OBS</strong></em></p></li>
<li><p><em>VALOR_ANTERIOR = valor queo campo estado tinha
antes</em></p></li>
<li><p><em>VALOR_NOVO = Valor do campo do estado atual</em></p></li>
<li><p><em>DADOS_REGISTO = NULL</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

#### Dados Acedemicos e Pessoais

<img src="media/image17.png" style="width:9.69306in;height:4.30139in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 16%" />
<col style="width: 8%" />
<col style="width: 22%" />
<col style="width: 24%" />
<col style="width: 28%" />
</colgroup>
<thead>
<tr>
<th><strong>FORMULÁRIO</strong></th>
<th><strong>TIPO</strong></th>
<th colspan="2"><strong>Descrição</strong></th>
<th><strong>UPDATE / INSERT</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Habilitação Literaria</td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td><del>Validar</del></td>
<td><em><del>RADIOLIST</del></em></td>
<td colspan="2"><del>Fica visivel somente no modo validar</del></td>
<td><p><em><del>RH_T_HABILITACOES_LITERARIAS.ESTADO ou</del></em></p>
<p><em><del>RH_T_FORMACAO_FEITOS.ESTADO ou
RH_T_EXPERIENCIA_PROF.ESTADO</del></em></p></td>
</tr>
<tr>
<td>PAIS</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>País onde o colaborador obteve a habilitação
literária.<br />
(Ex.: Cabo Verde, Portugal, Brasil)</p>
<p><em><strong>FUNÇÃO</strong> :</em> GET_GEOGRAFIA (P_NIVEL =1
)</p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.PAIS_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><mark>ESTABELECIMENTO</mark></td>
<td><em><mark>SELECT</mark></em></td>
<td colspan="2"><p>Nome da instituição de ensino onde foi realizada a
formação.<br />
<em>(Ex.: Universidade de Lisboa, Instituto Politécnico de Cabo Verde)
.. <mark>ao Selecionar um pais deve trazer o
establecimento.</mark></em></p>
<p><em><mark><strong>TABELA</strong>: RH_T_ESTABELECIMENTO onde o
PAIS_ID é o ID DO PAIS SELECIONADO</mark></em></p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.ESTABLECIMENTO</em></td>
</tr>
<tr>
<td>AREA</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Área de estudo ou domínio científico a que pertence a
formação.<br />
(Ex.: Ciências Sociais, Engenharia, Saúde)</p>
<p><em><strong>DOMAINS</strong> = AREA_FORMACAO</em></p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.AREA</em></td>
</tr>
<tr>
<td><mark>CURSO</mark></td>
<td><em><mark>TEXT</mark></em></td>
<td colspan="2"><p>Designação específica do curso concluído ou
frequentado pelo colaborador.</p>
<p><mark><em><strong>DOMAINS</strong></em> = CURSO</mark></p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.NOME_CURSO</em></td>
</tr>
<tr>
<td>GRAU ACÁDEMICO /NIVEL</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Grau ou nível académico correspondente à habilitação
obtida.<br />
(Ex.: Licenciatura, Mestrado, Doutoramento, Ensino Secundário, Técnico
Profissional)</p>
<p><em><strong>DOMAINS</strong> = NIVEL_HABILITACOES</em></p></td>
<td><em>RH_T_HABILITACOES_LITERARIAS.NIVEL</em></td>
</tr>
<tr>
<td>DATA INICIO</td>
<td><em>DATE</em></td>
<td colspan="2">Data em que o colaborador iniciou o curso ou
formação.</td>
<td><em>RH_T_HABILITACOES_LITERARIAS.DATA_INICIO</em></td>
</tr>
<tr>
<td>DATA TERMINO</td>
<td><em>DATE</em></td>
<td colspan="2">Data em que o colaborador concluiu (ou abandonou) a
formação.</td>
<td><em>RH_T_HABILITACOES_LITERARIAS</em>.DATA_FIM</td>
</tr>
<tr>
<td>Formação Profissional</td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td>País</td>
<td><em>SELECT</em></td>
<td colspan="2">Campo destinado a identificar o país onde foi realizada
a formação profissional.</td>
<td><em>RH_T_FORMACAO_FEITOS.PAIS_ID</em></td>
</tr>
<tr>
<td>Establecimento</td>
<td></td>
<td colspan="2">Indica a instituição, centro ou entidade responsável
pela realização da formação.</td>
<td><em>RH_T_FORMACAO_FEITOS. ESTABELECIMENTO</em></td>
</tr>
<tr>
<td>Tipo de Formação</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Define a natureza ou modalidade da formação
frequentada.<br />
<em>Exemplo: Curso Técnico, Formação Modular, Workshop</em></p>
<p><em><strong>DOMAINS =</strong> TP_FORMACAO</em></p></td>
<td><em>RH_T_FORMACAO_FEITOS.RHTPFOR</em></td>
</tr>
<tr>
<td>Designação</td>
<td></td>
<td colspan="2">Refere-se ao nome ou título oficial da formação.</td>
<td><em>RH_T_FORMACAO_FEITOS.CURSO</em></td>
</tr>
<tr>
<td>Nível</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Indica o grau ou enquadramento da formação no sistema
educativo/profissional.<br />
<em>Exemplo: Nível II (Qualificação de Base), Nível IV (Curso Técnico),
Nível VI (Licenciatura).</em></p>
<p><em><mark><strong>DOMAINS</strong> = NIVEL_HABILITACOES onde
REFERENCIA = <strong>FORMACAO</strong></mark></em></p></td>
<td><em>RH_T_FORMACAO_FEITOS.NIVEL</em></td>
</tr>
<tr>
<td>Experiência Profissional</td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td>País</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Identifica o país onde a experiência profissional foi
exercida</p>
<p><em><strong>FUNÇÃO</strong> :</em> GET_GEOGRAFIA (P_NIVEL =1
)</p></td>
<td><em>RH_T_EXPERIENCIA_PROF.PAIS_ID</em></td>
</tr>
<tr>
<td>Empresa</td>
<td><em>TEXT</em></td>
<td colspan="2">Nome da entidade/organização onde o colaborador exerceu
funções.</td>
<td><em>RH_T_EXPERIENCIA_PROF.EMPRESA</em></td>
</tr>
<tr>
<td>Cargo</td>
<td><em>TEXT</em></td>
<td colspan="2">Função ou posição ocupada pelo colaborador durante o
período da experiência</td>
<td><em>RH_T_EXPERIENCIA_PROF.CARGO</em></td>
</tr>
<tr>
<td>Data Entrada</td>
<td><em>DATE</em></td>
<td colspan="2">Data em que o colaborador iniciou funções na empresa
referida.</td>
<td><em>RH_T_EXPERIENCIA_PROF.DATA_INICIO</em></td>
</tr>
<tr>
<td>Data Saída</td>
<td><em>DATE</em></td>
<td colspan="2">Data em que o colaborador cessou funções na
empresa.</td>
<td><em>RH_T_EXPERIENCIA_PROF.DATA_FIM</em></td>
</tr>
<tr>
<td>Observações</td>
<td><em>TEXTAREA</em></td>
<td colspan="2">Campo livre para registo de informações adicionais
relevantes.</td>
<td><em>RH_T_EXPERIENCIA_PROF.OBSERVACAO</em></td>
</tr>
<tr>
<td colspan="5"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="4"><em>Ao eliminar um registo existente na tabela, o
sistema deve atualizar de imediato o respetivo estado. (estado =
‘I’)</em></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="4"><em>Ao atualizar, devem ser guardadas as informações do
formulário, bem como os outros campos indicados abaixo</em></td>
</tr>
<tr>
<td colspan="5"><strong>GRAVAÇÃO DE OUTROS CAMPOS</strong></td>
</tr>
<tr>
<td colspan="3"><p><em>1.Altera dados nas tabelas
<strong>RH_T_HABILITACOES_LITERARIAS, RH_T_FORMACAO_FEITOS,
RH_T_EXPERIENCIA_PROF</strong> dos campos de formulario e outros
seguintes campos</em></p>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>id de utilizador
logado</strong></em></p></li>
<li><p><em>USER_ ALTERACAO _NAME = <strong>Nome de utilizador
Logado</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE</strong></em></p></li>
</ul>
<ol start="2" type="1">
<li><p><em><del>Registr na Tabela de validacao
<strong>(RH_T_VALIDACAO)</strong></del></em></p></li>
</ol>
<ul>
<li><p><em><del>TIPO_ACCAO= ‘INSERT ’ (DOMAINS =
TIPO_ACAO)</del></em></p></li>
<li><p><em><del>REFERENCIA_NAME = ‘<strong>DADOS_ACADEMICOS</strong>”’
(DOMAINS = <strong>ACCAO_REFERENTE</strong>)</del></em></p></li>
<li><p><em><del>REFERENCIA_ID = ID de tabela
RH_T_FAMILIARES</del></em></p></li>
<li><p><em><del>FUN_ID = ID de tabela
RH_T_FUNCIONARIOS</del></em></p></li>
<li><p><em><del>TIPREL_ID = NULL</del></em></p></li>
<li><p><em><del>DATA_REGISTO = SYSDATE</del></em></p></li>
<li><p><em><del>USER_REGISTO_NAME = nome de utilizador
Logado</del></em></p></li>
<li><p><em><del>USER_REGISTO_ID = id de utilizador
Logado</del></em></p></li>
<li><p><em><del>ESTADO = ‘P’</del></em></p></li>
</ul></td>
<td colspan="2"><p><em><del>3-.Registo log nas tabelas de
IGRP</del></em></p>
<p><em><del><strong>3.1 -</strong>Registo Detalhe de LOG na tabela
<strong>RH_T_VALIDACAO_DETALHE</strong></del></em></p>
<ul>
<li><p><em><del>VALIDACAO_ID <strong>= id de tabela
RH_T_VALIDACAO</strong></del></em></p></li>
<li><p><em><del>CAMPO_ALTERADO <strong>=</strong> nome de campo
alterado</del></em></p></li>
<li><p><em><del>VALOR_ANTERIOR = valor antes</del></em></p></li>
<li><p><em><del>VALOR_NOVO = valor depois</del></em></p></li>
<li><p><em><del>TABELA_NAME = “ nome de tabela a ser
registado”</del></em></p></li>
<li><p><em><del>TABELA _ID = “id de tabela a ser
registado</del></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

#### Agregado / dependente

<img src="media/image18.png" style="width:9.69306in;height:2.36389in"
alt="Uma imagem com texto, captura de ecrã, file, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 24%" />
<col style="width: 7%" />
<col style="width: 18%" />
<col style="width: 16%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: left;"><strong>FORMULÁRIO</strong></th>
<th><strong>TIPO</strong></th>
<th colspan="2"><strong>Descrição</strong></th>
<th><strong>UPDATE / INSERT</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><del>Validar</del></td>
<td><em><del>RADIOLIST</del></em></td>
<td colspan="2"><del>Fica visivel somente no modo validar</del></td>
<td><em><del>RH_T_FAMILIARES.ESTADO</del></em></td>
</tr>
<tr>
<td style="text-align: left;">*Tipo Documento Identificação</td>
<td><em>TEXT</em></td>
<td colspan="2"><p>Tipo de documento de identificação do Colaborador</p>
<p><em>RH_TP_DOCUMENTO.REFERENCIA =</em>
<strong>DOCUMENTO_PESSOAL’</strong></p></td>
<td><em>RH_T_FAMILIARES.TP_DOCUMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">* N.º Documento de Identificação</td>
<td><em>TEXT</em></td>
<td colspan="2"></td>
<td><em>RH_T_FAMILIARES.</em> <em>NUM_DOCUMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Nome</td>
<td><em>TEXT</em></td>
<td colspan="2"></td>
<td><em>RH_T_FAMILIARES.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">*Data Nascimento</td>
<td><em>DATE</em></td>
<td colspan="2"></td>
<td><em>RH_T_FAMILIARES.</em> <em>DATA_NASCIMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Género</td>
<td><em>SELECT</em></td>
<td colspan="2"><em><strong>DOMAINS</strong> = GENERO</em></td>
<td><em>RH_T_FAMILIARES.SEXO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Grau Parentesco</td>
<td><em>SELECT</em></td>
<td colspan="2"><em><strong>DOMAINS</strong> =
GRAUS_DE_PARENTESCO</em></td>
<td><em>RH_T_FAMILIARES.GDP_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">*Dependente</td>
<td><em>SELECT</em></td>
<td colspan="2"><em><strong>DOMAINS</strong> = DEPENDENCIA</em></td>
<td><em>RH_T_FAMILIARES.</em> <em>DEPENDENCIA</em></td>
</tr>
<tr>
<td style="text-align: left;">*Agregado</td>
<td><em>SELECT</em></td>
<td colspan="2"><em><strong>DOMAINS</strong>= MEMBRO_AGR</em></td>
<td><em>RH_T_FAMILIARES.MEMBRO_AGR</em></td>
</tr>
<tr>
<td style="text-align: left;"><mark>*<strong>Responsavel
</strong></mark></td>
<td><em><mark>SELECT</mark></em></td>
<td colspan="2"><em><mark><strong>DOMAINS</strong>=
SIM_NAO</mark></em></td>
<td><em><mark>RH_T_FAMILIARES.RESPONSAVEL</mark></em></td>
</tr>
<tr>
<td colspan="5" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td style="text-align: left;">Eliminar</td>
<td colspan="4"><em>Ao eliminar um registo existente na tabela, o
sistema deve atualizar de imediato o respetivo estado.
(<strong>RH_T_FAMILIARES</strong>.estado = ‘I’)</em></td>
</tr>
<tr>
<td style="text-align: left;">Editar</td>
<td colspan="4"><em>Ao atualizar, devem ser guardadas as informações do
formulário, bem como os outros campos indicados abaixo</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="5" style="text-align: left;"><ul>
<li><p><em>A validação dos campos obrigatórios deve ser efetuada tanto
ao nível do formulário como ao nível da tabela</em></p></li>
<li><p><em>O sistema deve garantir que não existam duplicados de
familiares para o mesmo colaborador. A verificação deve ser feita com
base nos campos <strong>FUN_ID</strong>, <strong>NUM_DOCUMENTO</strong>
e <strong>NOME</strong>. Mensagem de ERRO : “Já existe um familiar com
este nome e número de documento associado a este
colaborador”</em></p></li>
<li><p><em><mark>Um familiar só pode ter um único responsável (verificar
se o número do documento já está associado a outro colaborador como seu
responsável). Emitir a seguinte mensagem de erro: “<strong>O referido
familiar já possui outro colaborador associado como seu
responsável</strong>.”</mark></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: left;"><strong>GRAVAÇÃO DE OUTROS
CAMPOS</strong></td>
</tr>
<tr>
<td colspan="3" style="text-align: left;"><p><em>1.Altera dados na
tabela <strong>RH_T_FAMILIARES</strong> dos campos de formulario e
outros seguintes campos</em></p>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>id de utilizador
logado</strong></em></p></li>
<li><p><em>USER_ ALTERACAO _NAME = <strong>Nome de utilizador
Logado</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE</strong></em></p></li>
</ul>
<ol start="3" type="1">
<li><p><em><del>Registr na Tabela de validacao
<strong>(RH_T_VALIDACAO)</strong></del></em></p></li>
</ol>
<ul>
<li><p><em><del>TIPO_ACCAO= ‘INSERT ’ (DOMAINS =
TIPO_ACAO)</del></em></p></li>
<li><p><em><del>REFERENCIA_NAME = ‘FAMILIA”’ (DOMAINS =
ACCAO_REFERENTE)</del></em></p></li>
<li><p><em><del>REFERENCIA_ID = ID de tabela
RH_T_FAMILIARES</del></em></p></li>
<li><p><em><del>FUN_ID = ID de tabela
RH_T_FUNCIONARIOS</del></em></p></li>
<li><p><em><del>TIPREL_ID = NULL</del></em></p></li>
<li><p><em><del>DATA_REGISTO = SYSDATE</del></em></p></li>
<li><p><em><del>USER_REGISTO_NAME = nome de utilizador
Logado</del></em></p></li>
<li><p><em><del>USER_REGISTO_ID = id de utilizador
Logado</del></em></p></li>
<li><p><em><del>ESTADO = ‘P’</del></em></p></li>
</ul></td>
<td colspan="2"><p><em><strong>3.</strong>Guarda na tabela de log
<strong>na tabela de IGRP</strong></em></p>
<p><em><strong>3.<del>2 </del></strong><del>Registo Detalhe de LOG na
tabela <strong>RH_T_VALIDACAO_DETALHE</strong></del></em></p>
<ul>
<li><p><em><del>VALIDACAO_ID <strong>= id de tabela
RH_T_VALIDACAO</strong></del></em></p></li>
<li><p><em><del>CAMPO_ALTERADO <strong>= nome de campo
Alterado</strong></del></em></p></li>
<li><p><em><del>VALOR_ANTERIOR = valor antes</del></em></p></li>
<li><p><em><del>VALOR_NOVO = valor depois</del></em></p></li>
<li><p><em><del>TABELA_NAME = “ nome de tabela a ser
registado”</del></em></p></li>
<li><p><em><del>TABELA _ID = “id de tabela a ser
registado</del></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

#### Dados Bancários

- <img src="media/image19.png" /><img src="media/image20.png" style="width:9.36806in;height:3.47222in"
  alt="Uma imagem com texto, software, Ícone de computador, Página web Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 7%" />
<col style="width: 21%" />
<col style="width: 16%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td colspan="2">Fica visivel somente no modo validar</td>
<td><em>RH_T_DADOS_BANCARIOS.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Entidade Bancária</td>
<td><em>SELECT</em></td>
<td colspan="2"><em>RH_BANCO.NM_BANCO</em></td>
<td><em>RH_T_DADOS_BANCARIOS.ENT_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><mark><del>Nº Conta</del></mark></td>
<td><em><mark><del>TEXT</del></mark></em></td>
<td colspan="2"><mark><del>Numero de conta do banco</del></mark></td>
<td><em><mark><del>RH_T_DADOS_BANCARIOS.NUM_CONTA</del></mark></em></td>
</tr>
<tr>
<td style="text-align: left;">NIB/IBAN</td>
<td><em>TEXT</em></td>
<td colspan="2">NIB do colaborador</td>
<td><em>RH_T_DADOS_BANCARIOS.NIB</em></td>
</tr>
<tr>
<td style="text-align: left;">Data inicio</td>
<td><em>DATE</em></td>
<td colspan="2"></td>
<td><em>RH_T_DADOS_BANCARIOS.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td colspan="2"></td>
<td><em>RH_T_DADOS_BANCARIOS. DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>REGRAS</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ul>
<li><p><em>Qualquer altetação / registo, efectuado deve passar para
validação (<strong>RH_T_VALIDACAO</strong>)</em></p></li>
<li><p><mark>Caso o colaborador tenha salario , logo o nib deve ser
obrigatorio. Para este caso o sistema deve emitir uma mensagem de Erro
‘<strong>Erro: O Nib é Obrigatório</strong>’</mark></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: left;">ACÕES</td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Editar</td>
<td colspan="4"><em>Caso já esteja validado , so deve deixar altera
data_fim</em></td>
</tr>
<tr>
<td style="text-align: left;">Eliminar</td>
<td colspan="4"><em>Botão eliminar fica visivel caso o o registo não for
validado (<strong>RH_T_DADOS_BANCARIOS.ESTADO = ‘E’, DATA_FIM =
SYSDATE</strong>)</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>GRAVAÇÃO DE OUTROS
CAMPOS</strong></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em>1.Altera dados na
tabela <strong>RH_T_DADOS_BANCARIOS</strong> dos campos de formulario e
outros seguintes campos</em></p>
<ul>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>id de utilizador
logado</strong></em></p></li>
<li><p><em>USER_ ALTERACAO _NAME = Nome <strong>de utilizador
Logado</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE</strong></em></p></li>
</ul>
<ol start="4" type="1">
<li><p>Caso for alterado ou registado o nib ou banco deve ser registado
na tabela de <strong>RH_T_VALIDACAO</strong></p></li>
</ol>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘UPDATE ou NSERT’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘DADOS_BANCARIOS’ (</strong>DOMAINS
= ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
RH_T_DADOS_BANCARIOS, Separados por virgula</em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
<td colspan="2"><p><em><strong>3.</strong>Guarda LOG no IGRP</em></p>
<p><em><strong>3.2</strong> Registo Detalhe de alteracaona tabela
<strong>RH_T_VALIDACAO_DETALHE</strong></em></p>
<ul>
<li><p><em>VALIDACAO_ID <strong>= id de tabela
RH_T_VALIDACAO</strong></em></p></li>
<li><p><em>CAMPO_ALTERADO <strong>= nome de campo
Alterado</strong></em></p></li>
<li><p><em>VALOR_ANTERIOR = valor antes</em></p></li>
<li><p><em>VALOR_NOVO = valor depois</em></p></li>
<li><p><em>TABELA_NAME = “ nome de tabela a ser registado”</em></p></li>
<li><p><em>TABELA _ID = “id de tabela a ser registado</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

- 

### Relação Laboral

#### Gestão Contratual

<img src="media/image21.png" style="width:9.69306in;height:4.28333in"
alt="Uma imagem com texto, software, número, Página web Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 6%" />
<col style="width: 32%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte Dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Vinculo</td>
<td style="text-align: center;">SELECT</td>
<td style="text-align: center;"></td>
<td style="text-align: left;"><em>RH_T_CONTRATO_VINCULO.
VINCULO_ID</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte Dados</strong></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Contrato</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_CONTRATO_VINCULO. TP_CONTRADO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Vinculo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_CONTRATO_VINCULO. VINCULO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Data inicio</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_CONTRATO_VINCULO.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Duração</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_CONTRATO_VINCULO. DURACAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_CONTRATO_VINCULO. DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>AÇÕES</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Ver Informação Atual</td>
<td colspan="3"><em>Esse botão só deve ficar visivel na ultima versão do
contrato</em></td>
</tr>
<tr>
<td style="text-align: left;">Ver informação Inicial</td>
<td colspan="3"><em>Esse botão só deve ficar visivel na primeira versão
do contrato</em></td>
</tr>
<tr>
<td style="text-align: left;">Renovar Contrato</td>
<td colspan="3"><em>Esse botão só deve ficar visivel na ultima versão do
contrato</em></td>
</tr>
</tbody>
</table>

##### Contrato de Trabalho (Ver Informação Atual, Ver informação Inicial )

<img src="media/image22.png" style="width:9.3125in;height:4.77083in"
alt="Uma imagem com texto, captura de ecrã, software, número Os conteúdos gerados por IA podem estar incorretos." />

<img src="media/image23.png" style="width:9.25in;height:2.35417in"
alt="Uma imagem com texto, captura de ecrã, software, número Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 7%" />
<col style="width: 36%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>FONTE DADOS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="4" style="text-align: center;"><p><strong>Dados
Contratuais</strong></p>
<p><strong>Ver informação Atual: (</strong>busca dados em
<em><strong>RH_V_MOBILIDADE</strong></em> TIPO_CONTRATO =
‘<strong>RENOVACAO’</strong> ), pego o ultimo contrato ativo do
colaborador<strong>),</strong></p>
<p><strong>Ver informação Inicial (</strong>busca dados em
<em><strong>RH_V_MOBILIDADE</strong></em> TIPO_CONTRATO =
‘<strong>NOVO_CONTRATO’</strong> ), pega o contrato inicial do
colabora<strong>), não deve permitir editar</strong></p></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td>Fica visivel somente no modo validar</td>
<td><em>RH_T_CONTRATO_VINCULO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Situação Laboral</td>
<td><em>TEXT</em></td>
<td><strong>DOMAINS</strong> = SITUACAO_LABORAL</td>
<td>RH_V_DOSSIE.SITUACAO_LABORAL</td>
</tr>
<tr>
<td style="text-align: left;">Cargo/Posição</td>
<td><em>TEXT</em></td>
<td>Designação do cargo ou função que o colaborador irá desempenhar na
instituição.</td>
<td>RH_V_DOSSIE.CARGO_DESC</td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td><em>TEXT</em></td>
<td>Unidade orgânica ou direção em que o colaborador está afeto.<br />
<em>(Ex.: Direção Financeira, Direção de Recursos Humanos)</em></td>
<td>RH_V_DOSSIE.DIRECAO_DESC</td>
</tr>
<tr>
<td style="text-align: left;">secção</td>
<td><em>TEXT</em></td>
<td style="text-align: left;">Subunidade ou divisão dentro da direção
onde o colaborador desempenhará funções.<br />
<em>(Ex.: Secção de Contabilidade)</em></td>
<td>RH_V_DOSSIE.SECAO_DESC</td>
</tr>
<tr>
<td style="text-align: left;">Centro custo</td>
<td><em>TEXT</em></td>
<td>centro de custo responsável pelas despesas com este colaborador</td>
<td>RH_V_DOSSIE.CENTRO_CUSTO_DESC</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td><em>TEXT</em></td>
<td>Estrutura profissional a que o colaborador pertence</td>
<td>RH_V_DOSSIE.CARREIRA_DES</td>
</tr>
<tr>
<td style="text-align: left;">Categoria</td>
<td><em>TEXT</em></td>
<td>Nível ou grupo profissional do colaborador dentro da carreira (</td>
<td>RH_V_DOSSIE.CATEGORIA_ESC</td>
</tr>
<tr>
<td style="text-align: left;">Escalão / referência</td>
<td><em>TEXT</em></td>
<td>Escalão salarial ou referência remuneratória correspondente à
posição do colaborador</td>
<td>RH_V_DOSSIE<em>.ESCALAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Tipo de vinculo Laboral</td>
<td><em>TEXT</em></td>
<td><p>Natureza do vínculo contratual entre colaborador e entidade
empregadora</p>
<p>(Ex.: Efetivo, Contrato a Termo, Requisição, Estágio)</p></td>
<td>RH_V_DOSSIE<em>. VINCULO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Regime Trabalho</td>
<td><em>TWXT</em></td>
<td>Condições de prestação de trabalho definidas no contrato.<br />
<em>(Ex.: Tempo Integral, Tempo Parcial, Teletrabalho, Horário
Flexível)</em></td>
<td>RH_V_DOSSIE REGIME_TRABALHO_DESC</td>
</tr>
<tr>
<td style="text-align: left;">Salário</td>
<td><em>NUMBER</em></td>
<td>Valor contratual da remuneração base do colaborador.</td>
<td>RH_V_DOSSIE.<em>VALOR</em></td>
</tr>
<tr>
<td style="text-align: left;">Moeda</td>
<td><em>TEXT</em></td>
<td>Moeda em que o salário e demais remunerações são processados.<br />
<em>(Ex.: CVE, EUR, USD)</em></td>
<td>RH_V_DOSSIE<em>.MOEDA</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio de Função</td>
<td><em>DATE</em></td>
<td>Data em que o colaborador inicia efetivamente o exercício das
funções.</td>
<td>RH_V_DOSSIE. <em>DATA_INICIO_CONTRATO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim de Função</td>
<td><em>DATE</em></td>
<td>Data prevista para o termo do vínculo laboral (quando
aplicável).</td>
<td>RH_V_DOSSIE<em>. DATA_FIM_CONTRATO</em></td>
</tr>
<tr>
<td style="text-align: left;">Duração (MESES)</td>
<td><em>NUMBER</em></td>
<td>Período total de vigência do contrato, expresso em meses ou anos
(aplicável a contratos temporários). (Diferença entre data inicio funcão
e data fim)</td>
<td>RH_V_DOSSIE<em>. DURACAO_CONTRATO</em></td>
</tr>
<tr>
<td style="text-align: left;">Local de Trabalho</td>
<td></td>
<td>Lugar fisico onde o Colaborador exerce o seu trabalho</td>
<td>RH_V_DOSSIE<em>.LOCAL_TRABALHO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Pais</td>
<td><em>TEXT</em></td>
<td><p>País onde o contrato terá execução</p>
<p>Buscar dados na TABELA GEOGRAFIA no nivel de pais</p></td>
<td>RH_V_DOSSIE<em>. PAIS_TRAB_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Ilha</td>
<td><em>TEXT</em></td>
<td>Localidade específica (quando aplicável em contexto nacional, ex.:
Cabo Verde). Apresenta as ilhas correspondentes quando o país escolhido
for Cabo Verde</td>
<td>RH_V_DOSSIE<em>. ILHA_TRAB_DESC</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Subsidios :</strong>
Indicação geral sobre a existência de subsídios atribuídos ao
colaborador. (busca dados em RH_V_REND_ENC ONDE TIPO =
‘<strong>REM’</strong> e TIPO_MOBILIDADE =
‘<strong>NOVO_CONTRATO’</strong> ), pego o ultimo contrato do
colaborador</td>
</tr>
<tr>
<td style="text-align: left;">Tipo de Subsídio</td>
<td><em>SELECT</em></td>
<td style="text-align: left;">Natureza do subsídio atribuído.<br />
<em>(Ex.: Subsídio de Alimentação, Subsídio de Transporte, Subsídio de
Férias, 13.º mês)</em></td>
<td><em>RH_V_REND_ENC. MOVIMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">Percentagem</td>
<td><em>TEXT</em></td>
<td>Percentagem do salário base usada para calcular o valor do subsídio
(quando aplicável).</td>
<td><em>RH_V_REND_ENC.PERCENTAGEM</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor</td>
<td><em>NUMBER</em></td>
<td>Montante atribuído ao colaborador a título de subsídio</td>
<td><em>RH_V_REND_ENC.VALOR</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Encargos / Descontos
:</strong> Indicação geral sobre os encargos (patronais) ou descontos
(do colaborador) aplicáveis. . (busca dados em RH_V_REND_ENC ONDE TIPO =
‘<strong>PAG’</strong> e TIPO_MOBILIDADE =
‘<strong>NOVO_CONTRATO’</strong> ), pego o ultimo contrato do
colaborador</td>
</tr>
<tr>
<td style="text-align: left;">Tipo de Encargos / Descontao</td>
<td><em>SELECT</em></td>
<td><p>Identificação do tipo de encargo ou desconto.<br />
<em>(Ex.: INPS, Imposto IRPS, Fundo Social, Sindicato)</em></p>
<p><em><strong>FUNÇÃO</strong>: GET_MOVIMENTO_DESCONTO
(P_TIPO)</em></p></td>
<td><em>RH_V_REND_ENC.MOVIMENTO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor</td>
<td><em>NUMBER</em></td>
<td>Montante a deduzir ou a assumir pela entidade empregadora, podendo
ser fixo ou percentual.</td>
<td><em>RH_V_REND_ENC..VALOR</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio</td>
<td><em>DATE</em></td>
<td>Data a partir da qual o encargo/desconto entra em vigor.</td>
<td><em>RH_V_REND_ENC..DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td>Data de cessação do encargo/desconto (quando aplicável).</td>
<td><em>RH_V_REND_ENC..DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Remuneração</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Remuneração Bruta</td>
<td><em>NUMBER</em></td>
<td>Montante total antes da aplicação de impostos e descontos
obrigatórios.</td>
<td><em>Deve somar (salario + subsidio)</em></td>
</tr>
<tr>
<td style="text-align: left;">Total Desconto</td>
<td><em>NUMBER</em></td>
<td>Valor total dos descontos aplicados à remuneração do
colaborador.</td>
<td
rowspan="2"><p><em>processamento_salarial_db.CalcularDesAtual</em></p>
<p><em>( ----SUBSISDIO-------</em></p>
<p><em>p_tm_id_subsidio =&gt; tm_id de subsisio,</em></p>
<p><em>p_valor_subsidio =&gt; valor de subsudio,</em></p>
<p><em>----DESCONTO------</em></p>
<p><em>p_tm_id_desconto =&gt; tm_id de desconto,</em></p>
<p><em>p_valor_desconto =&gt; valor de desconto,</em></p>
<p><em>p_tipo_remuneracao =&gt; ’SAL’,</em></p>
<p><em>p_valor_base =&gt; salario,</em></p>
<p><em>P_moeda =&gt; moeda,</em></p>
<p><em>p_data_de =&gt; Data inicio,</em></p>
<p><em>p_total_remun =&gt; devolde</em> Remuneração
<em>Liquido,</em></p>
<p><em>P_total_pagamentos =&gt; devolde total desconto</em></p>
<p><em>)</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Remuneração Líquida</td>
<td><em>NUMBER</em></td>
<td>Montante final recebido pelo colaborador após a dedução de impostos
e descontos.</td>
</tr>
</tbody>
</table>

#####  Novo Contrato

Mesmo formulario descrito no registo de colaborador (Dados Contratuais)

<table>
<colgroup>
<col style="width: 35%" />
<col style="width: 32%" />
<col style="width: 31%" />
</colgroup>
<thead>
<tr>
<th colspan="3"><strong>REGRAS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="3"><ul>
<li><p><em>Data início de não pode se superior a data fim de
função</em></p></li>
<li><p><em>Data início não ser maior que
<strong>sysdate</strong></em></p></li>
<li><p><em>Validar os campos obrigatorios</em></p></li>
<li><p><em><strong>O Botão Novo contrato, so deve ficar visivel caso não
existe um contrato ativo</strong></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="3"><strong>GRAVAÇÃO DE OUTROS CAMPOS</strong></td>
</tr>
<tr>
<td colspan="3"><ul>
<li><p><em><strong>Se for o primeiro contrato do colaborador:</strong>
no campo TIPO_SITUACAO, registar <strong>INICIO</strong> (em vez de
CONTINUIDADE).</em></p></li>
<li><p><em><strong>Se não for o primeiro contrato:</strong> verificar se
existem registos <strong>ativos</strong> (DATA_FIM IS NULL) nas tabelas
RH_T_CARREIRA, RH_T_MOBILIDADE e RH_T_REGIME;
<strong>encerrá-los</strong> definindo DATA_FIM (ex.: com a data de
término do contrato anterior) e <strong>depois</strong> executar as
demais ações descritas abaixo.</em></p></li>
</ul></td>
</tr>
<tr>
<td><p><em><strong>1 Registo</strong> em
<strong>RH_T_CONTRATO_VINCULO</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>ESTADO = “<strong>P</strong>”</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>TIPO_CONTRATO =</em> Tipo de vinculo Laboral</p></li>
<li><p><em>TIPO_SITUACAO = INICIO ou CONTINUIDADE</em></p></li>
<li><p><em>ESTADO_CONTRATO = ‘ATIVO’</em></p></li>
<li><p><em>REFERENCIA = ´ NOVO_CONTRATO ´</em></p></li>
<li><p><em>OBS = ´ NOVO_CONTRATO</em></p></li>
<li><p><em>VERSAO = 1</em></p></li>
<li><p><em>CONTRATO_ID = ID DE RH_T_CONTRATO_VINCULO</em></p></li>
</ul>
<p><em><mark>1.1 registo na tabela
<strong>RH_T_CONTRATO_HISTORICO</strong></mark></em></p>
<p><em>1.1 registo em RH_T_MOBILIDADE</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>TIPO_SITUACAO = INICIO ou CONTINUIDADE</em></p></li>
<li><p><em>OBS = “NOVO_CONTRATO”</em></p></li>
<li><p><em>CONTRATO_ID = id de RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>FUN_ID = ID DE RH_T_FUNCIONARIOS</em></p></li>
</ul>
<p><em>1.2 RH_T_CARREIRA</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>TIPO_SITUACAO = CONTINUIDADE</em></p></li>
<li><p><em>OBS = “NOVO_CONTRATO”</em></p></li>
<li><p><em>CONTRATO_ID = id de RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>CONTR_VINCULO_ID = ID DE RH_T_CONTRATO_VINCULO</em></p></li>
</ul>
<p><em>2.3 RH_T_REGIME_TRAB</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>TIPO_SITUACAO = CONTINUIDADE</em></p></li>
<li><p><em>OBS = “NOVO_CONTRATO”</em></p></li>
<li><p><em>FUN_ID = ID DE RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CONTRATO_ID = id de RH_T_CONTRATO_VINCULO</em></p></li>
</ul>
<p><em>2.4- RH_T_SITUCAO_LABORAL</em></p>
<ul>
<li><p><em>SITUACAO_LABORAL_ID = PEGA ID DE
RH_T_PARAM_SIT_LABORAL.PARAM_SIT_ID = RH_T_PARAM_SITUACAO.ID ONDE ESTADO
= ATIVO</em></p></li>
<li><p><em>MOTIVO_SIT_LAB = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>DATA_INICIO = DATA INICIO CONTRATO</em></p></li>
<li><p><em>DATA_FIM = DATA FIM CONTRATO</em></p></li>
<li><p><em>FUN_ID = ID DE RH_T_FUNCIONARIO</em></p></li>
<li><p><em><mark>CONTR_VINCULO_ID = ID DE
RH_T_CONTRATO_VINCULO</mark></em></p></li>
<li><p>ESTADO = ‘P’</p></li>
<li><p><em>DATA_REGISTO = ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
</ul>
<p><em>2.5--update e novo Registo - Fazer uma nova gravação na tabela de
RH_T_TIPOS_RELACIONAMENTO, pegas todas informações onde campo
est_ult_adm = 1 (faz update est_ult_adm = 0 e data _fim = data inicio do
novo registo ) , e regista com novas alteraçoes nos campos do formulario
e outros seguinte campos</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CONTR_VINCULO_ID = ID de tabela Contrato</em></p></li>
<li><p><em>CARREIRA_ID = id de tabela RH_T_CARREIRA</em></p></li>
<li><p><em>MOB_ID = id de MOBILIDADE</em></p></li>
<li><p><em>REGIME_ID = ID de tabela RH_T_REGIME</em></p></li>
<li><p><em>ESTADO = ‘P’</em></p></li>
<li><p><em>EST_ACT_ADM = 1</em></p></li>
<li><p><em>TIPREL_ID = id no relacionamento fechado</em></p></li>
<li><p><em>OBS = “NOVO_CONTRATO”</em></p></li>
<li><p><em>TIPO_SITUACAO = INICIO ou CONTINUIDADE</em></p></li>
</ul>
<ul>
<li><p><em>SITUAÇAO_LABORAL_ID = ID DE RH_T_SITUACAO_LABORAL</em></p>
<ul>
<li><p><em>SITUACAO_LABORAL = ‘ATIVO’</em></p></li>
<li><p><em>REFERENTE = ‘NOVO_CONTRATO’</em></p></li>
</ul></li>
</ul></td>
<td><p><em><strong>3.</strong> O sistema deve gravar na tabela
<strong>RH_T_DEF_REMUNERACAO</strong> as informações do separador de
<strong>subsídio</strong> e 1 registo do
<strong>salário</strong></em></p>
<p><em><strong>3.1 separador Subsidio (1 ou varios
registos)</strong></em></p>
<ul>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CARREIRA_ID = ID de RH_T_CARREIRA</em></p></li>
</ul>
<p><em><strong>3.2 Salario (1 registo)</strong></em></p>
<ul>
<li><p><em>VALOR = Valor do campo Salario do formulario</em></p></li>
<li><p><em>DATA_INICIO = Data inicio de Função do
formulario</em></p></li>
<li><p><em>DATA_FIM = Data Fim de Função</em></p></li>
<li><p><em>TM_ID = <mark>tm_id de
<strong>rh_t_param_vinculo_mov</strong></mark></em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CARREIRA_ID = ID de RH_T_CARREIRA</em></p></li>
</ul>
<p><em>3.3 deve ser feito nova associação da tabela
<strong>RH_T_TIPOS_RELACIONAMENTO</strong> e
<strong>RH_T_DEF_REMUNECACAO</strong> na TABELA
<strong><mark>RH_T_TIPREL_REM_PAG</mark></strong></em></p>
<ul>
<li><p><em>REM_ID = ide de RH_T_DEF_REMUNERACAO</em></p></li>
<li><p><em>TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO</em></p></li>
<li><p><em><mark><del>ESTADO = P</del></mark></em></p></li>
<li><p><em><mark><del>USER_REGISTO_ID = id de utilizador
Logado</del></mark></em></p></li>
<li><p><em><mark><del>USER_REGISTO_NAME = nome de utilizador
Logado</del></mark></em></p></li>
<li><p><em><mark><del>USER_ALTERACAO _ID =
<strong>NULL</strong></del></mark></em></p></li>
<li><p><em><mark><del>USER_ALTERACAO_NAME =
<strong>NULL</strong></del></mark></em></p></li>
<li><p><em><mark><del>DATA_ALTERACAO =
<strong>NULL</strong></del></mark></em></p></li>
</ul>
<p><em><strong>4</strong>.O sistema deve gravar na tabela
<strong>RH_DEF_PAGAMENTOS</strong> as informações do separador de
<strong>Encargos / Descontos</strong></em> <em>e 2 registo de
<strong>IUR</strong> e <strong>INPS</strong> e <strong>SALL <mark>(pegar
tm_id em rh_t_param_vinculo_mov </mark></strong><mark>onde <strong>tipo
= PAG e deve passar como parametro o id de
Vinculo)</strong></mark></em></p>
<p><em><strong>4.1 Separador Encargos / Descontos</strong></em></p>
<ul>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>CARREIRA_ID = ID de RH_T_CARREIRA</em></p></li>
<li><p><em>FUN_ID = ID de RH_T_FUNCIONARIOS</em></p></li>
</ul></td>
<td><ol start="2" type="1">
<li><p><em>IUR</em></p></li>
</ol>
<ul>
<li><p><em>VALOR = 0</em></p></li>
<li><p><em>DATA_INICIO = Data inicio de Função do
formulario</em></p></li>
<li><p><em>DATA_FIM = Data Fim de Funão</em></p></li>
<li><p><em>TM_ID = <mark>tm_id de
<strong>rh_t_param_vinculo_mov</strong></mark></em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>OBS = ‘NOVO_CONTRATO’’</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CARREIRA_ID = ID de RH_T_CARREIRA</em></p>
<ol start="2" type="1">
<li><p><em>INPS</em></p></li>
</ol></li>
<li><p><em>VALOR = 0</em></p></li>
<li><p><em>DATA_INICIO = Data inicio de Função do
formulario</em></p></li>
<li><p><em>DATA_FIM = Data Fim de Funão</em></p></li>
<li><p><em>TM_ID = <mark>tm_id de
<strong>rh_t_param_vinculo_mov</strong></mark></em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>OBS = ‘NOVO_CONTRATO’</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CARREIRA_ID = ID de RH_T_CARREIRA</em></p>
<ol start="2" type="1">
<li><p><em>S<mark>ALL</mark></em></p></li>
</ol></li>
<li><p><em><mark>VALOR = 0</mark></em></p></li>
<li><p><em><mark>DATA_INICIO = Data inicio de Função do
formulario</mark></em></p></li>
<li><p><em><mark>DATA_FIM = Data Fim de Funão</mark></em></p></li>
<li><p><em><mark>TM_ID = tm_id de
<strong>rh_t_param_vinculo_mov</strong></mark></em></p></li>
<li><p><em><mark>ESTADO = ‘<strong>P</strong>’</mark></em></p></li>
<li><p><em><mark>USER_REGISTO_ID = id de utilizador
Logado</mark></em></p></li>
<li><p><em><mark>USER_REGISTO_NAME = nome de utilizador
Logado</mark></em></p></li>
<li><p><em><mark>USER_ALTERACAO _ID =
<strong>NULL</strong></mark></em></p></li>
<li><p><em><mark>USER_ALTERACAO_NAME =
<strong>NULL</strong></mark></em></p></li>
<li><p><em><mark>DATA_ALTERACAO =
<strong>NULL</strong></mark></em></p></li>
<li><p><em><mark>OBS = ‘NOVO_CONTRATO’</mark></em></p></li>
<li><p><em><mark>FUN_ID = id de RH_T_FUNCIONARIOS</mark></em></p></li>
</ul>
<p><em>4.deve ser feito uma nova associação da tabela
<strong>RH_T_TIPOS_RELACIONAMENTO</strong> e
<strong>RH_T_DEF_PAGAMENTO</strong> na TABELA
<strong><mark>RH_T_TIPREL_REM_PAG</mark></strong></em></p>
<ul>
<li><p><em>PAG_ID = ide de RH_T_DEF_PAGAMENTO</em></p></li>
<li><p><em>TIPREL_ID = id de NONORH_T_TIPOS_RELACIONAMENTO</em></p></li>
</ul>
<p><em>5- Registo na tabela de validação
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘CONTRATO’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_CONTRATO_VINCULO </strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= ID DE
RH_TIPOS_RELACIONAMENTO</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul>
<p><em><strong>5.1</strong> Registo Detalhe na tabela
<strong>RH_T_VALIDACAO_DETALHE</strong></em></p>
<ul>
<li><p><em>VALIDACAO_ID <strong>= id de tabela
RH_T_VALIDACAO</strong></em></p></li>
<li><p><em>CAMPO_ALTERADO <strong>= NULL</strong></em></p></li>
<li><p><em>VALOR_ANTERIOR = NULL</em></p></li>
<li><p><em>VALOR_NOVO = NULL</em></p></li>
<li><p><em>TABELA_NAME = “ nome de tabela a ser registado”</em></p></li>
<li><p><em>TABELA _ID = “id de tabela a ser registado</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="3"><ol start="5" type="1">
<li><p><em>Registo de log <strong>no IGRP</strong></em></p></li>
</ol></td>
</tr>
</tbody>
</table>

###### Validar Contrato

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><ul>
<li><p>A validação invoca a mesma página de Registo</p></li>
<li><p>O campo validar deve ficar visivel</p></li>
<li><p>Ao <strong>validar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'A'</strong>.</p></li>
<li><p>Ao <strong>desvalidar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

##### **Conversão de Contrato** 

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><ul>
<li><p><mark>O sistema deve criar um job para verificar todos os
contratos renováveis. Caso os contratos atinjam o período definido ou o
número máximo de renovações permitido, o sistema deverá gerar um alerta
45 dias antes da data de expiração do contrato, com base no campo
<strong>rh_t_contrato_vinculo.data_fim</strong></mark></p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

##### Renovação

<img src="media/image24.png" style="width:9.69306in;height:2.50069in"
alt="Uma imagem com captura de ecrã, texto, file Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 7%" />
<col style="width: 19%" />
<col style="width: 9%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Validar</td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Vinculo</td>
<td><em>SELECT</em></td>
<td colspan="2"></td>
<td><em>RH_T_CONTRATO_VINCULO.VINCULO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Data inicio</td>
<td><em>DATE</em></td>
<td colspan="2"></td>
<td><em>RH_T_CONTRATO_VINCULO.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Duracão (meses)</td>
<td><em>NUMBER</em></td>
<td colspan="2"></td>
<td><em>RH_T_CONTRATO_VINCULO.DURACAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td colspan="2"></td>
<td><em>RH_T_CONTRATO_VINCULO.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>REGRAS</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ul>
<li><p><em>O sistema deve aletar quando o prazo de contrato está quase a
atingir o prazo. Deve ser criado um job que notifica o utilizador sempre
que o contrato esta proximo a atingir o Prozo. (VER ESPECIFICAÇÃO
)</em></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: left;"><strong>ACOES</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em><mark>1.novo registo
em <strong>RH_T_CONTRATO_HISTORICO</strong></mark></em></p>
<ul>
<li><p><em>DATA_REGISTO</em></p></li>
<li><p><em>USER_REGISTO_ID</em></p></li>
<li><p><em>USER_REGISTO_NAME</em></p></li>
<li><p><em>DURACAO</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>DATA_FIM</em></p></li>
<li><p><em>VERSAO = ultima versão desse contrato + 1</em></p></li>
<li><p><em>CONTRATO_ID = ID RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>OBS = <strong>RENOVACAO</strong></em></p>
<ol type="1">
<li><p><em><mark>update na tabela
<strong>RH_T_CONTRATO_VINCULO</strong></mark></em></p></li>
</ol></li>
</ul>
<ul>
<li><p><em>DURACAO</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>DATA_FIM</em></p></li>
<li><p><em>USER_ALTERACAO_ID</em></p></li>
<li><p><em>DATA_ALTERACAO</em></p></li>
<li><p><em>USER_ALTERACAO_NAME</em></p></li>
</ul>
<p><em>2-update e novo Registo - Fazer uma nova gravação na tabela de
RH_T_TIPOS_RELACIONAMENTO, pegas todas informações onde campo
est_ult_adm = 1 (faz update est_ult_adm = 0 e data _fim = data inicio do
novo registo) , e regista com novas alteraçoes nos campos do formulario
e outros seguinte campos</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>ESTADO = ‘P’</em></p></li>
<li><p><em>EST_ACT_ADM = 1</em></p></li>
<li><p><em>TIPREL_ID = id do relacionamento Fechado</em></p></li>
<li><p><em>OBS = “RENOVACAO _CONTRATO”</em></p></li>
<li><p><em>TIPO_SITUACAO = “RENOVACAO”</em></p></li>
<li><p><em>REFERENCIA = ‘CONTRATO</em></p></li>
</ul>
<p><em>3.3 deve ser feito nova associação da tabela
<strong>RH_T_TIPOS_RELACIONAMENTO,</strong> <strong>RH_T_DEF_REMUNECACAO
e RH_T_DEF_PAGAMENTO</strong> na TABELA
<strong><mark>RH_T_TIPREL_REM_PAG</mark></strong></em></p>
<ul>
<li><p><em><mark>REM_ID = ide de
RH_T_DEF_REMUNERACAO</mark></em></p></li>
<li><p><em><mark>TIPREL_ID = id de
RH_T_TIPOS_RELACIONAMENTO</mark></em></p></li>
</ul></td>
<td colspan="2"><p><em>2. registar Validacao</em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘UPDATE ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘RENOVACAO_CONTRATO’
(</strong>DOMAINS = ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul>
<p><em><strong>2.1</strong> Registo Detalhe de na tabela
<strong>RH_T_VALIDACAO_DETALHE</strong></em></p>
<ul>
<li><p><em><strong>VALIDACAO_ID = id de tabela
RH_T_VALIDACAO</strong></em></p></li>
<li><p><em>CAMPO_ALTERADO <strong>= NULL</strong></em></p></li>
<li><p><em>VALOR_ANTERIOR = NULL</em></p></li>
<li><p><em>VALOR_NOVO = NULL</em></p></li>
<li><p><em>TABELA_NAME = “ nome de tabela a ser registado”</em></p></li>
<li><p><em>TABELA _ID = “id de tabela a ser registado</em></p></li>
</ul>
<p><em>3- Registo de log <strong>no IGRP</strong></em></p></td>
</tr>
</tbody>
</table>

###### Validar Renovacao

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><ul>
<li><p>A validação invoca a mesma página de Registo</p></li>
<li><p>O campo validar deve ficar visivel</p></li>
<li><p>Ao <strong>validar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'A'</strong>.</p></li>
<li><p>Ao <strong>desvalidar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

#### Mobilidade

###### Lista Mobilidade 

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 6%" />
<col style="width: 24%" />
<col style="width: 45%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte Dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td style="text-align: center;">DATE</td>
<td></td>
<td><em>RH_V_MOBILIDADE.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td style="text-align: center;">DATE</td>
<td></td>
<td><em>RH_V_MOBILIDADE.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: center;">Tipo Mobilidade</td>
<td style="text-align: center;">SELECT</td>
<td><strong>DOMAINS</strong> = TIPO_MOV_LABORAL, REFERENTE =
‘MOBILIDADE’</td>
<td><em>RH_V_MOBILIDADE. TIPO_MOBILIDADE</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte Dados</strong></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Mobilidade</td>
<td><em>TEXT</em></td>
<td><strong>DOMAINS</strong> = TIPO_MOV_LABORAL</td>
<td><em>RH_V_MOBILIDADE.TIPO_SITUACAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_MOBILIDADE.DIRECAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Secão</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_MOBILIDADE. SECAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;"><del>Cargo</del></td>
<td></td>
<td></td>
<td><em><del>RH_V_MOBILIDADE. CARGO_DESC</del></em></td>
</tr>
<tr>
<td style="text-align: left;">Local Trabalho</td>
<td></td>
<td></td>
<td><em>RH_V_MOBILIDADE. LOCAL_TRAB_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Data inicio</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_MOBILIDADE.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_MOBILIDADE.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>REGRAS</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em>O sistema não deve deixar efectuar qq alteração quando a
mobilidade em questão já tenho um processamento.</em></p></li>
<li><p><em>Deve aparecer somente mobilidade Ativo</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>ACCOES</strong></td>
</tr>
<tr>
<td style="text-align: left;">Editar</td>
<td colspan="3"><p><em>Somente deixa editar caso o registo ainda não for
processado (RH_V_MOBILIDADE.PROCESSAMENTO = 0)</em></p>
<p><em><strong>Nota: Ao editar não faz um novo registo, mas sim
atualiza</strong></em></p></td>
</tr>
<tr>
<td style="text-align: left;">Mobilidade</td>
<td colspan="3"><p><em>Aparece somente a mobilidade tenha um
processamento (RH_V_MOBILIDADE.PROCESSAMENTO &gt; 0)</em></p>
<p><em><strong>Nota: Ao editar faz um novo registo, mas sim
atualiza</strong></em></p></td>
</tr>
<tr>
<td style="text-align: left;">Eliminar</td>
<td colspan="3"><p><em>Somente deixa cancelar caso o registo ainda não
for validade. (RH_V_MOBILIDADE.ESTADO = ‘P’).</em></p>
<p><em>Ao eliminar o <strong>RH_</strong>V_MOBILIDADE.ESTADO =
‘E’</em></p></td>
</tr>
</tbody>
</table>

###### Novo/ Editar Mobilidade

<img src="media/image25.png" style="width:9.69306in;height:3.25625in"
alt="Uma imagem com texto, captura de ecrã, número, file Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 8%" />
<col style="width: 19%" />
<col style="width: 17%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>GRAVAÇÃO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td colspan="2"><p>Esse deve ficar oculto, so fica visivel em caso de
validação desse Registo. No modo validação esse campo é obrigatorio</p>
<p>DOMAINS= SIM_NAO</p></td>
<td><em>RH_T_MOBILIDADE. ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Tipo Mobilidade</td>
<td><em>MULTISELCT</em></td>
<td colspan="2"><p><strong>DOMAINS</strong> = TIPO_MOV_LABORAL,
REFERENTE = ‘<strong>MOBILIDADE’</strong>,</p>
<p>Permite selecionar mais de uma mobilidade</p></td>
<td><p><em>RH_T_MOBILIDADE. TIPO_SITUACAO</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.TIPO_SITUACAO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Data Inicio</td>
<td><em>DATE</em></td>
<td colspan="2"></td>
<td><p><em>RH_T_MOBILIDADE.DATA_INICIO</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td colspan="2" style="text-align: left;"></td>
<td><p><em>RH_T_MOBILIDADE.DATA_FIM</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_FIM</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Mobilidade</td>
<td colspan="4"><em>Esses campos ficam visivel somente apos selecionar o
tipo Mobilidade</em></td>
</tr>
<tr>
<td style="text-align: left;">Direção (Antes )</td>
<td><em>ENABLE</em></td>
<td colspan="2"><p>Visivel caso o tipo de Mobilidade = DIRECAO</p>
<p>(vem preenchido com <em>RH_V_MOBILIDADE.DIRECAO_DESC</em>)</p></td>
<td><em>-------------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">Direção (Depois )</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Visivel caso o tipo de Mobilidade = DIRECAO</p>
<p><em><strong>FUNÇÃO:</strong> GET_DIRECAO_SERVICO</em></p></td>
<td><p><em>RH_T_MOBILIDADE.INSTIT_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Secão (Antes )</td>
<td><em>ENABLE</em></td>
<td colspan="2"><p>Visivel caso o tipo de Mobilidade = SECAO</p>
<p>vem preenchido com <em>RH_V_MOBILIDADE.SECAO_DESC</em>)</p></td>
<td><em>------------------------------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">Secão (Depois )</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Visivel caso o tipo de Mobilidade = SECAO</p>
<p><strong>FUNÇÃO:</strong> GET_SECCAO (<em>P_ INSTIT_ID)</em></p></td>
<td><em>RH_T_MOBILIDADE.SECAO_ID
RH_T_TIPOS_RELACIONAMENTO.SECAO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Local Trabalho (Antes )</td>
<td><em>ENABLE</em></td>
<td colspan="2"><p>Visivel caso o tipo de Mobilidade = Local</p>
<p>vem preenchido com <em>RH_V_MOBILIDADE.LOCAL_TRAB_DESC)</em></p></td>
<td><em>--------------------------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">Local Trabalho(Depois )</td>
<td><em>SELECT</em></td>
<td colspan="2"><p>Visivel caso o tipo de Mobilidade = Local</p>
<p><strong>FUNÇÃO</strong> : GET_LOCAL_TRABALHO</p></td>
<td><p><em>RH_T_MOBILIDADE. LOCAL_TRAB_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>REGRAS</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><p><em><strong>Ao Clicar no
botão gravar, deve ser feito as seguintes ações</strong></em></p>
<ul>
<li><p><em>Deve quardar na tabela Log</em></p></li>
<li><p><em>Deve quardar na tabela de Validação</em></p></li>
<li><p><em>Criar um historico de trababo</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>OUTRAS
GRAVAÇOES</strong></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><em>UPDATE</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><p><em>Faz update nos
registos anteriores , ou seja inativa os registos ativos</em></p>
<p><em>1.2Inativa a mobilidade em estado ativo
<strong>RH_T_TIPOS_RELACIONAMENTO</strong> (est_act_adm = 1 e data fim
is not null<strong>)</strong></em></p>
<ul>
<li><p><em>DATA_FIM = data Inicio Carreira -1</em></p></li>
<li><p><em>USER_ALTERACAO _ID = utilizador logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>nome de
utilizador</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = sysdate</em></p></li>
<li><p><em>EST_ACT_ADM = 0</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><em>INSERT</em></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em>1-</em><em>Fazer uma
nova gravação na tabela de <strong>RH_T_MOBILIDADE</strong>, pegas todas
informações do registo anterior , e regista com novas alteraçoes nos
campos do formulario e outros seguinte campos</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>TIPREL_ID = ID DO REGISTO FECHADO</em></p></li>
<li><p><em>CONTRATO_ID = ID DE TABELA
RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>FUN_ID = ID DE TABELA RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>OBS = ‘MOBILIDADE’</em></p></li>
</ul>
<p><em>2-Fazer uma nova gravação na tabela de RH_T_TIPOS_RELACIONAMENTO,
pegas todas informações onde campo est_ult_adm = 1 (faz update
est_ult_adm = 0 e data _fim = data inicio do novo registo) , e regista
com novas alteraçoes nos campos do formulario e outros seguinte
campos</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>MOB_ID = ID DE RH_T_MOBILIDADE</em></p></li>
<li><p><em>TIPREL_ID = ID da mobilidade anterior</em></p></li>
<li><p><em>ESTADO = ‘P’</em></p></li>
<li><p><em>OBS = ‘MOBILIDADE- || TIPO_MOBILIDADE</em></p></li>
<li><p><em>EST_ACT_ADM = 1</em></p></li>
<li><p><em>OBS = ‘MOBILIDADE’</em></p></li>
<li><p><em>REFERENTE = ‘MOBILIDADE´’</em></p></li>
</ul></td>
<td colspan="2"><p><em>3-Grava na tabela de validação –
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘MOBILIDADE’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_MOBILIDADE</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= ID de
RH_T_TIPOS_RELACIONAMENTO</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul>
<p><em>2.Cria uma nova associacao na tabela de Assossiacao gravação na
tabela <strong>RH_T_TIPREL_REM_PAGPega os dados do tiprel_id fechado e
cria um novo registo com novo Tiprel_id </strong></em></p>
<ul>
<li><p><em>TIPREL_ID = novo Tiprel_id</em></p></li>
<li><p><em>REM_ID = pega o mesmo anteriro</em></p></li>
<li><p><em>PAG_ID = pega o mesmo anterior</em></p></li>
</ul>
<p><em>3.1- Registo Detalhe de LOG na tabela
<strong>RH_T_VALIDACAO_DETALHE</strong></em></p>
<ul>
<li><p><em>VALIDACAO_ID <strong>= id de tabela
RH_T_VALIDACAO</strong></em></p></li>
<li><p><em>CAMPO_ALTERADO <strong>= nome de campos
</strong></em></p></li>
<li><p><em>VALOR_ANTERIOR = valor antes</em></p></li>
<li><p><em>VALOR_NOVO = valor depois</em></p></li>
</ul>
<p><em>• TABELA_NAME = “ nome de tabela a ser registado”</em></p>
<p><em>• TABELA _ID = “id de tabela a ser registado</em></p>
<p><em>4-Grava LOG no IGRP</em></p>
<ul>
<li></li>
</ul></td>
</tr>
</tbody>
</table>

###### Validar mobilidade 

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><strong>REGRAS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><ul>
<li><p>A validação invoca a mesma página de Registo</p></li>
<li><p>O campo validar deve ficar visivel</p></li>
<li><p>Ao <strong>validar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'A'</strong>.</p></li>
<li><p>Ao <strong>desvalidar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
</ul>
<p>1.2.-Ao Validar gera um ordem de Serviço na tabela
<strong>RH_T_ORDEM_SERVICO (</strong>caso for
validado<strong>)</strong></p>
<ul>
<li><p>DESCRICAO = ‘Mobilidade do colaborador - ’ ||
RH_T_FUNCIONARIOS.NOME</p></li>
<li><p>REFERENTE = ‘MOBILIDADE’</p></li>
<li><p>FUN_ID = RH_T_FUNCIONARIOS.ID</p></li>
<li><p>TIPREL_ID = RH_T_TIPOS_RELACIONAMENTO.ID</p></li>
<li><p>VALIDACAO_ID = RH_T_VALIDACAO.ID</p></li>
</ul>
<p><em>1.Caso o utilizador não valide a mobilidade, o sistema deve
inativar a própria mobilidade, bem como as remunerações e os descontos a
ela associados.</em></p>
<ul>
<li><p><em>RH_T_TIPOS_RELACIONAMENTO.ESTADO = ‘I’</em></p></li>
<li><p><em>RH_T_MOBILIDADE.ESTADO = ‘I’</em></p></li>
</ul>
<p><em>1.Caso o utilizador valida a mobilidade deve seguir o
especificado em Editar Mobilidade.</em></p>
<ul>
<li><p><em>RH_T_TIPOS_RELACIONAMENTO.ESTADO = ‘A’</em></p></li>
<li><p><em>RH_T_MOBILIDADE.ESTADO = ‘A’</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

####### Modelo de mobilidade 

| <img src="media/image26.png" style="width:6.05208in;height:5.12431in" /> |
|--------------------------------------------------------------------------|

####  Gestão Carreira

##### Lista Carreira

<img src="media/image27.png" style="width:9.69306in;height:3.95069in" />

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 6%" />
<col style="width: 32%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte Dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td style="text-align: center;">Date</td>
<td style="text-align: center;"></td>
<td style="text-align: left;"><em>RH_V_CARREIRA.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td style="text-align: center;">Date</td>
<td style="text-align: center;"></td>
<td style="text-align: left;"><em>RH_V_CARREIRA.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: center;">Tipo Carreira</td>
<td style="text-align: center;">Select</td>
<td style="text-align: left;"><strong>DOMAINS</strong> =
TIPO_MOV_LABORAL, REFERENTE = ‘CARREIRA’</td>
<td><em>RH_T_CARREIRA. TIPO_SITUACAO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte Dados</strong></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Carreira</td>
<td><em>TEXT</em></td>
<td><strong>DOMAINS</strong> = TIPO_MOV_LABORAL, REFERENTE =
‘CARREIRA’</td>
<td><em>RH_V_CARREIRA.TIPO_SITUACAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td></td>
<td></td>
<td><em>RH_V_CARREIRA.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Vinculo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_CARREIRA.VINCULO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_CARREIRA.CARREIRA_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">cargo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_CARREIRA.CARGO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Escalão</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_CARREIRA.ESCALAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Salário</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_CARREIRA.SALARIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Situação Laboral</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_CARREIRA.SITUACAO_LABORAL_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Data inicio</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_CARREIRA.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_CARREIRA.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Processa Salário?</td>
<td></td>
<td></td>
<td><em>RH_V_CARREIRA.FLG_PROCESSA</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em>A lista deve estar organizadas por data início de
carreira</em></p></li>
<li><p><em>Caso a carreira esteja validada ou processada o botão
Eliminar deve ficar invisivel</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>AÇÕES</strong></td>
</tr>
<tr>
<td style="text-align: left;">Novo</td>
<td colspan="3"><em>Campo Tipo Carreira de preenche com o seguinte:
DOMINIO =<strong>TIPO_MOV_LABORAL</strong>, VALOR =
<strong>CARREIRA_NOVO</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">Editar</td>
<td colspan="3"><p><em>so pemite editar todos campos caso ainda não
tenha processamento associado (<strong>RH_V_CARREIRA. PROCESSAMENTO =
0), …</strong> Caso for editado, deve passar novamente para
validação.</em></p>
<p><em>Campo Tipo Carreira de preenche com o seguinte: DOMINIO
=<strong>TIPO_MOV_LABORAL</strong>, VALOR =
<strong>CARREIRA_EDITAR</strong></em></p></td>
</tr>
<tr>
<td style="text-align: left;">Progressão / Promoção</td>
<td colspan="3"><p><em>Este botão invoca a mesma página de editar, mas
no campo tipo carreira deve aparecer o seguinte:</em></p>
<ul>
<li><p><em>Campo Tipo Carreira de preenche com o seguinte: DOMINIO
=<strong>TIPO_MOV_LABORAL</strong>, VALOR =
<strong>CARREIRA_PROG_PROMO</strong></em></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: left;">Eliminar</td>
<td colspan="3"><p><em>Só pemite eliminar caso ainda não for validdo, ou
seja, caso RH<strong>_T_CARREIRA.</strong>ESTADO <strong>=
‘P’</strong></em></p>
<p><em>Ao eliminar o RH_V_MOBILIDADE.ESTADO = ‘E’</em></p></td>
</tr>
</tbody>
</table>

##### Novo / Editar 

<img src="media/image28.png" style="width:9.69306in;height:5.20139in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 20%" />
<col style="width: 8%" />
<col style="width: 4%" />
<col style="width: 1%" />
<col style="width: 28%" />
<col style="width: 3%" />
<col style="width: 4%" />
<col style="width: 28%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="3"
style="text-align: center;"><strong>Descrição</strong></th>
<th colspan="3" style="text-align: center;"><strong>FONTE
DADOS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td colspan="3"><p>Esse deve ficar oculto, so fica visivel em caso de
validação desse Registo. No modo validação esse campo é obrigatorio</p>
<p>DOMAINS= SIM_NAO</p></td>
<td colspan="3"><em>RH_T_CARREIRA.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Vínculo</td>
<td><em>ENABLE</em></td>
<td colspan="3"></td>
<td
colspan="3"><em>-------------------------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">*Tipo Carreira</td>
<td></td>
<td colspan="3"><em>DOMAINS=</em> TIPO_MOV_LABORAL referente a
‘CARREIRA’</td>
<td colspan="3"><p><em>RH_T_CARREIRA. TIPO_SITUACAO</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. TIPO_SITUACAO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*cargo</td>
<td><em>SELECT</em></td>
<td colspan="3"><em><strong>TABELA</strong> : RH_CARGOS ;
<strong>CAMPOS</strong> :cod_cargo e descricao</em></td>
<td colspan="3"><p><em>RH_T_CARREIRA.CARGO_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. CARGO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Carreira</td>
<td><em>SELECT</em></td>
<td colspan="3"><strong>FUNÇÃO:</strong> <em>GET_CARREIRA
(P_CARGO)</em></td>
<td colspan="3"><p><em>RH_T_CARREIRA. CARR_PCCS_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. CARR_PCCS_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Categoria</td>
<td><em>SELECT</em></td>
<td colspan="3" style="text-align: left;"><strong>FUNÇÃO:</strong>
<em>GET_CATEGORIA(P_CARREIRA)</em></td>
<td colspan="3"><p><em>RH_T_CARREIRA.CATEGORIA_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. CATEGORIA_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Escalão</td>
<td><em>SELECT</em></td>
<td colspan="3"><em><strong>FUNÇÃO:</strong> GET_ESCALAO (P_CARREIRA,
P_CATEGORIA)</em></td>
<td colspan="3"><p><em>RH_T_CARREIRA.ESCALAO_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. ESCALAO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Salário</td>
<td><em>NUMBER</em></td>
<td colspan="3"><strong>FUNÇÃO</strong> : GET_SALARIO (P_ESCALAO)</td>
<td colspan="3"><p><em>RH_T_CARREIRA.SALÁRIO</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. SALÁRIO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Moeda</td>
<td><em>Select</em></td>
<td colspan="3"><em><strong>DOMAINS</strong> = MOEDA</em></td>
<td colspan="3"><p><em>RH_T_CARREIRA.MOEDA</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. MOEDA</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Processamento Salarial</td>
<td><em>radio</em></td>
<td colspan="3"><strong>DOMAINS</strong>: SIM_NAO_NUMBER</td>
<td colspan="3"><p><em>RH_T_CARREIRA.FLG_PROCESSA</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. FLG_PROCESSA</em></p></td>
</tr>
<tr>
<td colspan="8" style="text-align: center;"><strong>Subsido : deve
trazer por defeito os subsisdios registados ativos</strong></td>
</tr>
<tr>
<td style="text-align: left;">Tipo de Subsídio</td>
<td><em>SELECT</em></td>
<td colspan="3" style="text-align: left;"><p>Natureza do subsídio
atribuído.<br />
<em>(Ex.: Subsídio de Alimentação, Subsídio de Transporte, Subsídio de
Férias, 13.º mês)</em></p>
<p><em><strong>FUNÇÃO</strong>: GET_MOVIMENTO_REMUNERACAO
(P_TIPO)</em></p></td>
<td colspan="3"><em>RH_T_DEF_REMUNERACOES.TM_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Percentagem</td>
<td><em>TEXT</em></td>
<td colspan="3">Percentagem do salário base usada para calcular o valor
do subsídio (quando aplicável).</td>
<td colspan="3"><em>RH_T_DEF_REMUNERACOES.PERCENTAGEM</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor</td>
<td><em>NUMBER</em></td>
<td colspan="3">Montante atribuído ao colaborador a título de
subsídio</td>
<td colspan="3"><em>RH_T_DEF_REMUNERACOES.VALOR</em></td>
</tr>
<tr>
<td colspan="8" style="text-align: center;"><strong>Encargos / Descontos
: deve trazer por defeito os descontos registados ativos</strong></td>
</tr>
<tr>
<td style="text-align: left;">Tipo de Encargos / Descontao</td>
<td><em>SELECT</em></td>
<td colspan="3"><p>Identificação do tipo de encargo ou desconto.<br />
<em>(Ex.: INPS, Imposto IRPS, Fundo Social, Sindicato)</em></p>
<p><em><strong>FUNÇÃO</strong>: GET_MOVIMENTO_DESCONTO
(P_TIPO)</em></p></td>
<td colspan="3"><em>RH_T_DEF_PAGAMENTOS.TM_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor</td>
<td><em>NUMBER</em></td>
<td colspan="3">Montante a deduzir ou a assumir pela entidade
empregadora, podendo ser fixo ou percentual.</td>
<td colspan="3"><em>RH_T_DEF_PAGAMENTOS.VALOR</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio</td>
<td><em>DATE</em></td>
<td colspan="3">Data a partir da qual o encargo/desconto entra em
vigor.</td>
<td colspan="3"><em>RH_T_DEF_PAGAMENTOS.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td colspan="3">Data de cessação do encargo/desconto (quando
aplicável).</td>
<td colspan="3"><em>RH_T_DEF_PAGAMENTOS.DATA_FIM</em></td>
</tr>
<tr>
<td colspan="8" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="8" style="text-align: center;"><p><em><strong>Regra
Geral:</strong></em></p>
<ul>
<li><p><em>O campo processar salário deve ser obrigatório. Deve ser
validado que nunca um utilizador tenha duas carreiras marcadas para
processar ao mesmo tempo</em></p></li>
<li><p><em>O colaborador não pode ter mais de duas carreiras
ativas.</em></p></li>
<li><p><em>o colaborador não pode ter duas carreiras ativas do mesmo
tipo (duas carreiras ativas cujo cargo é nulo ou duas carreiras ativas
cujo cargo não é nulo)</em></p></li>
</ul>
<p><em><strong>1.Caso a carreira ainda não tenha um
processamento</strong> (RH_V_CARREIRA.</em> <em>PROCESSAMENTO
<strong>=</strong> 0).</em></p>
<ul>
<li><p><em>A alteração de dados não implica um novo registo nas tabelas
<strong>RH_T_CARREIRA</strong> e
<strong>RH_T_TIPOS_RELACIONAMENTO,</strong> mas sim deve fazer
atualização</em></p></li>
<li><p><em>A atualizacao de dados de <strong>escalão / salário</strong>
deve passar para validação novamente</em></p></li>
</ul>
<p><em><strong>2.Caso a carreira tenha um processamento
associado</strong> (RH_V_CARREIRA.</em> <em>PROCESSAMENTO
<strong>&gt;</strong> 0).</em></p>
<ul>
<li><p><em>Ao editar, deve ficar fechado os campos de
<strong>carreira</strong>, <strong>Cargo, Data
início</strong></em></p></li>
</ul>
<ul>
<li><p><em>A alteração dos campos de <strong>Escalão,</strong> implica
um novo (<strong>INSERT</strong>) registo em <strong>RH_T_CARREIRA,
RH_T_TIPOS_RELACIONAMENTO</strong> e
<strong>RH_T_TIPLRE_REM_PAG</strong></em></p></li>
<li><p><em>Ao fechar (Data Fim) uma carreiso somente faz
<strong>actualização</strong> na tabela
<strong>RH_T_CARREIRA,</strong></em></p></li>
<li><p><em>Quando uma carreira esta fechada não se pode fazer alteração
nela, ou seja o botão gravar deve estar desativo</em></p></li>
<li><p><em>Duas carreras não podem estar marcados para processar ao
mesmo tempo (<strong>RH_T_CARREIRA.FLG_CARREIRA = 1</strong>), ou seja
somente um qui pode ser processado.</em></p></li>
<li><p><em>Sempre que uma carreira for alterada para processar
(<strong>RH_T_CARREIRA.FLG_CARREIRA = 1</strong>), ou seja, era 0 e
passa para 1</em></p>
<ul>
<li><p><em>logo faz o novo registo na tabela,
<strong>RH_T_TIPOS_RELACIONAMENTO.</strong> Pega todos os registos do
último vínculo, excepto a CARREIRA_ID <strong>, pq este fica com
</strong></em></p></li>
</ul></li>
</ul>
<blockquote>
<p><em>Id de carreira que será último vinculo
atual<strong>).</strong></em></p>
</blockquote>
<ul>
<li><p><em>Faz um novo registo na tabela
<strong>RH_T_TIPREL_REM_PAG</strong> (pega todos os registos ativos
dessa mesma carreira que se pretende processar, e faz um novo registo
associando o novo TIPREL_ID (id de RH_T_TIPOS_RELACIONAMENTO
registado<strong>) Nota: ter atenção aqui, o registo que vai pegar em
rh_t_def_remuneraqcoes e rh_t_def_pagamentos deve ser dessa carreira
atual que se vair mudar para processar e não de outroa
carrerao</strong></em></p></li>
</ul>
<ul>
<li><p><em>Sempre que uma carreira for marcada para não processar
(<strong>RH_T_CARREIRA.FLG_CARREIRA = 0</strong>), ou seja, era 1 e
passa para 0, somente faz atualização nas tabelas:</em></p>
<ul>
<li><p><em>RH_T_CARREIRA.FLG_PROCESSAR</em></p></li>
<li><p><em>O sistema deve obrigar, colocar data fim nessa carreira
RH_T_CARREIRA.DATA_FIM</em></p></li>
<li><p><em>RH_T_TIPOS_RELACIONAMENTO.EST_ACT_AM = 0</em></p></li>
<li><p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_FIM = data fim de
carreira</em></p></li>
</ul></li>
</ul>
<p><em><strong>3.Ao clicar no botão gravar, deve ser feito as seguintes
ações</strong></em></p>
<ul>
<li><p><em>Deve quardar na tabela Log</em></p></li>
<li><p><em>O colaborador pode ter mais de um vínculo ativo, mais somente
recebe salarios em um deles (sempre rece salarios no vinculo cujo
salario é mais alto), logo o sistema deve validar se já existe um
marcado como sim, caso sim não deixar registar outra como
sim</em></p></li>
<li><p><em>Deve quardar na tabela de Validação</em></p></li>
<li><p><em>Criar um historico de trababo</em></p></li>
<li><p><em>O sistema <strong>não deve permitir o registo de uma
CARREIRA</strong> caso <strong>não exista um contrato ativo</strong>
associado ao colaborador. Nessa situação, o botão <strong>“Registar
CARREIRA”</strong> deve permanecer
<strong>oculto</strong>.</em></p></li>
</ul>
<ul>
<li><p><em>Sempre que for registada uma <strong>nova carreira</strong>,
esta deve ser automaticamente <strong>submetida a validação</strong>. O
respetivo registo deve ser inserido na tabela de <strong>Validação
(RH_T_VALIDACAO)</strong>.</em></p></li>
<li><p><em>Ao Criar uma nova carreira, o sistema deve fechar o registo
de salário anteiror (RH_T_DEF_REMUNERAÇÃO anteriror ativos
)</em></p></li>
</ul>
<p><em><strong>Subsidio</strong></em></p>
<ul>
<li><p><em>Deve trazer, preenchidos automaticamente, os subsídios ativos
(<strong>RH_T_DEF_REMUNERACAO.ESTADO = 'A' e data fim não nulo</strong>)
e cujo movimento não corresponda a salário (RH_T_DEF_REMUNERACOES.TM_ID
= referente a SAL).</em></p></li>
<li><p><em>Ao gravar a mobilidade:</em></p>
<ul>
<li><p><em>O sistema deve recuperar todos os subsídios do separador
<strong>Subsídio</strong> e registá-los na tabela de remuneração
(<strong>RH_T_DEF_REMUNERACAO</strong>), cujo Data inicio é igual
<strong>Data Inicio carreira</strong></em></p></li>
<li><p><em>O sistema deve igualmente registar um lançamento de salário,
utilizando o valor indicado no campo <strong>Salário da
carreira</strong>, na tabela RH_T_DEF_REMUNERACOES.</em></p></li>
<li><p><em>O sistema deve finalizar todas as remunerações da mobilidade
anterior cujo campo <strong>Data de Fim
(</strong>RH_T_DEF_REMUNERACAO.data_fim<strong>)</strong> não seja nulo
e cujo <strong>Estado</strong> esteja ativo
<strong>(</strong>RH_T_DEF_REMUNERACAO.estado =
‘A’<strong>)</strong></em></p></li>
</ul></li>
</ul>
<p><em><strong>Encargos / Descontos</strong></em></p>
<ul>
<li><p><em>Deve trazer, preenchidos automaticamente, os descontos ativos
(<strong>RH_T_DEF_PAGAMENTOS.ESTADO = 'A' e data fim não nulo</strong>)
e cujo movimento não corresponda a salário
(<strong>RH_T_DEF_PAGAMENTOS</strong>.TM_ID != de IUR e INPS
).</em></p></li>
<li><p><em>Ao gravar a mobilidade:</em></p>
<ul>
<li><p><em>O sistema deve recuperar todos os subsídios do separador de
<strong>DESCONTOS</strong> e registá-los na tabela de remuneração
(<strong>RH_T_DEF_PAGAMENTOS</strong>), cujo Data inicio é igual
<strong>Data Inicio CARREIRA</strong></em></p></li>
<li><p><em>O sistema deve igualmente registar um lançamento de
<strong>INPS</strong> e <strong>IUR</strong></em></p></li>
<li><p><em>O sistema deve finalizar todas os descontos da CARREIRA
anterior cujo campo <strong>Data de Fim
(</strong>RH_T_DEF_PAGAMENTOS.data_fim<strong>)</strong> não seja nulo e
cujo <strong>Estado</strong> esteja ativo
<strong>(</strong>RH_T_DEF_PAGAMENTOS.estado =
‘A’<strong>)</strong></em></p></li>
</ul></li>
</ul></td>
</tr>
<tr>
<td colspan="8" style="text-align: center;"><strong>OUTRAS
GRAVAÇOES</strong></td>
</tr>
<tr>
<td colspan="8" style="text-align: center;"><ol type="1">
<li><p><strong>Update</strong></p></li>
</ol></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em>Faz update nos
registos anteriores , ou seja inativa os registos ativos</em></p>
<p><em>1.2Inativa a mobilidade em estado ativo
<strong>RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e data fim is not
null)</strong></em></p>
<ul>
<li><p><em>DATA_FIM = data Inicio Carreira -1</em></p></li>
<li><p><em>USER_ALTERACAO _ID = utilizador logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>nome de
utilizador</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = sysdate</em></p></li>
<li><p><em>EST_ACT_ADM = 0</em></p></li>
</ul></td>
<td colspan="3"><p><em>1.2.1 inativa as remuneraçoes dessa mobilidade
<strong>RH_T_DEF_REMUNERACAO</strong></em></p>
<ul>
<li><p><em>DATA_FIM = data Inicio Carreira -1</em></p></li>
<li><p><em>USER_ALTERACAO _ID = utilizador logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>nome de
utilizador</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>sysdate</strong></em></p></li>
<li><p><em>ESTADO = I</em></p></li>
</ul></td>
<td colspan="2"><p><em>1.2.2 inativa os descontos dessa mobilidade
<strong>(RH_T_DEF_PAGAMENTO)</strong></em></p>
<ul>
<li><p><em>DATA_FIM = data Inicio Carreira -1</em></p></li>
<li><p><em>USER_ALTERACAO _ID = utilizador logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>nome de
utilizador</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = sysdate</em></p></li>
<li><p><em>ESTADO = I</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="8" style="text-align: center;"><ol start="2" type="1">
<li><p><strong>Insert</strong></p></li>
</ol></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><em>1-Fazer uma nova
gravação na tabela de RH_T_CARREIRA, pegas todas informações do registo
anterior , e regista com novas alteraçoes nos campos do formulario e
outros seguinte campos</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>CONTRATO_ID = ID DE TABELA
RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>CONTR_VINCULO_ID = ID de tabela
RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>OBS = ‘CARREIRA”</em></p></li>
</ul>
<p><em>2-Fazer uma nova gravação na tabela de RH_T_TIPOS_RELACIONAMENTO,
pegas todas informações onde campo est_ult_adm = 1 (faz update
est_ult_adm = 0 e data _fim = data inicio do novo registo) , e regista
com novas alteraçoes nos campos do formulario e outros seguinte
campos</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>MOB_ID = ID DE RH_T_MOBILIDADE</em></p></li>
<li><p><em>EST_ULT_ADM = 1</em></p></li>
<li><p><em>ESTADO = ‘P’</em></p></li>
<li><p><em>OBS = ‘MOBILIDADE- || TIPO_CARREIRA</em></p></li>
<li><p><em>REFERENTE = ‘CARREIRA</em></p></li>
</ul></td>
<td colspan="3"><p><em><strong>3-.</strong> O sistema deve gravar na
tabela <strong>RH_T_DEF_REMUNERACAO</strong> as informações do separador
de <strong>subsídio</strong> e 1 registo do
<strong>salário</strong></em></p>
<p><em><strong>Nota</strong>: Só deve ser efetuado um novo registo de
subsídio em caso de alteração do registo anterior.<br />
Nessa situação, deve encerrar o registo anterior e criar um novo
registo.<br />
Se se tratar de um subsídio novo, então deve proceder ao registo
normalmente..</em></p>
<p><em><strong>3.1 separador Subsidio (1 ou varios
registos)</strong></em></p>
<ul>
<li><p><em>OBS = ‘MOBILIDADE- || TIPO_CARREIRA</em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
</ul>
<p><em><strong>3.2 Salario (1 registo)<mark>(pegar tm_id em
rh_t_param_vinculo_mov </mark></strong><mark>onde <strong>tipo = REM e
deve passar como parametro o id de Vinculo</strong></mark></em></p>
<ul>
<li><p><em>VALOR = Valor do campo Salario do formulario</em></p></li>
<li><p><em>DATA_INICIO = Data inicio de Função do
formulario</em></p></li>
<li><p><em>DATA_FIM = Data Fim de Função</em></p></li>
<li><p><em>TM_ID = <mark>tm_id de
<strong>rh_t_param_vinculo_mov</strong></mark></em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>OBS = ‘‘MOBILIDADE- || TIPO_CARREIRA</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
</ul>
<p><em>3.3deve ser feito nova associação da tabela
<strong>RH_T_TIPOS_RELACIONAMENTO</strong> e
<strong>RH_T_DEF_REMUNECACAO</strong> na TABELA
<strong><mark>RH_T_TIPREL_REM_PAG</mark></strong></em></p>
<ul>
<li><p><em>REM_ID = ide de RH_T_DEF_REMUNERACAO</em></p></li>
<li><p><em>TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO</em></p></li>
</ul>
<p><em><strong>4</strong>.O sistema deve gravar na tabela
<strong>RH_DEF_PAGAMENTOS</strong> as informações do separador de
<strong>Encargos / Descontos</strong></em> <em>e 2 registo de
<strong>IUR</strong> e <strong>INPS e SALL</strong></em></p>
<p><em><del><strong>Nota</strong>: Só deve ser efetuado um novo registo
de desconto em caso de alteração do registo anterior.<br />
Nessa situação, deve encerrar o registo anterior e criar um novo
registo.<br />
Se se tratar de um subsídio novo, então deve proceder ao registo
normalmente</del>..</em></p>
<p><em><strong>4.1 Separador Encargos / Descontos</strong></em></p>
<ul>
<li><p><em>OBS = ‘MOBILIDADE- || TIPO_CARREIRA</em></p></li>
<li><p><em>ESTADO = ‘<strong>P</strong>’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>FUN_ID = ID de RH_T_FUNCIONARIOS</em></p></li>
</ul></td>
<td><p><em>4.3 deve ser feito uma nova associação da tabela
<strong>RH_T_TIPOS_RELACIONAMENTO</strong> e
<strong>RH_T_DEF_PAGAMENTO</strong> na TABELA
<strong><mark>RH_T_TIPREL_REM_PAG</mark></strong></em></p>
<ul>
<li><p><em>PAG_ID = ide de RH_T_DEF_PAGAMENTO</em></p></li>
<li><p><em>TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO</em></p></li>
</ul>
<p><em>5-Grava na tabela de validação –
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘CARREIRA’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_MOBILIDADE</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= ID de
RH_T_TIPOS_RELACIONAMENTO</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul>
<p><em>5.1- Registo Detalhe de LOG na tabela
<strong>RH_T_VALIDACAO_DETALHE</strong></em></p>
<ul>
<li><p><em>VALIDACAO_ID <strong>= id de tabela
RH_T_VALIDACAO</strong></em></p></li>
<li><p><em>CAMPO_ALTERADO <strong>= nome de campos
</strong></em></p></li>
<li><p><em>VALOR_ANTERIOR = valor antes</em></p></li>
<li><p><em>VALOR_NOVO = valor depois</em></p></li>
<li><p><em>TABELA_NAME = “ nome de tabela a ser registado”</em></p></li>
<li><p><em>TABELA _ID = “id de tabela a ser registado</em></p></li>
</ul>
<p><em>6-Grava LOG no IGRP</em></p></td>
</tr>
</tbody>
</table>

##### Validar Carreira

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><strong>REGRAS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><p><em>Ao validar uma carreira deve fazer as seguintes
ações:</em></p>
<ul>
<li><p><em>O Campo <strong>Validar</strong> deve ficar
visivel</em></p></li>
<li><p><em>Caso o utilizador valida o registo, logo deve atualizar todos
as tabelas associdas ao registo de carreira para <strong>estado =
‘A’</strong></em></p></li>
<li><p><em>Caso o utilizado não Valida , logo deve atualizar todos as
tabelas associdas ao registo de carreira para <strong>estado = ‘I’, e
atulizar OBS = ‘Não Validado’’</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

#### Substituição

##### Novo / Editar 

<img src="media/image29.png" style="width:9.69306in;height:5.47569in" />

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 11%" />
<col style="width: 31%" />
<col style="width: 1%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><strong>Dados de
Substituição</strong></td>
<td colspan="4"></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td><p>Esse deve ficar oculto, so fica visivel em caso de validação
desse Registo. No modo validação esse campo é obrigatorio</p>
<p>DOMAINS= SIM_NAO</p></td>
<td colspan="2"><em>RH_T_SUBSTITUICAO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Colaborador Susbstituido</td>
<td><em>LOOKUP</em></td>
<td><p>Pesquisar o colaborador que o irá substituir temporariamente,
deve pegar o vinculo do colaborador Ativo
(<strong>RH_T_TIPOS_RELACIONAMENTO.EST_ACT_ADM=1</strong> ) e que
processa salario (<strong>RH_T_TIPOS_RELACIONAMENTO.FLG_PROCESSSA=
1</strong>)</p>
<p><em><strong>FUNÇÃO</strong>: GET_COLABORADOR_MOBILIDADE</em></p></td>
<td colspan="2"><em>RH_T_SUBSTITUICAO.SUSBSTITUIDO_TIPREL_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">*Data Inicio</td>
<td><em>DATE</em></td>
<td>Data inicio de usbstituição</td>
<td colspan="2"><em>RH_T_SUBSTITUICAO.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Data Fim</td>
<td></td>
<td>Data fim de susbtituicao</td>
<td colspan="2"><em>RH_T_SUBSTITUICAO.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">*Motivo de Substituição</td>
<td><em>SELECT</em></td>
<td><p>Motivo de susbstituição</p>
<p><strong>DOMAIN</strong> = MOTIVO_SUBSTITUICAO_MOB</p></td>
<td colspan="2"><em>RH_T_SUBSTITUICAO.MOTIVO</em></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td><em>TEXTAREA</em></td>
<td>Descritivo do motivo de sustituição</td>
<td colspan="2"><em>RH_T_SUBSTITUICAO.OBSERVACAO</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Calculo Remuneração</strong></td>
<td><em>SEPARADOR</em></td>
<td colspan="3">caso o colaborador vai susbsituir o outro em mais de um
, logo o sistema deve mostrar detalhe de cada mês o que ele vai
recevber</td>
</tr>
<tr>
<td style="text-align: left;">Mês</td>
<td><em>Varchar</em></td>
<td rowspan="4"><p>INVOCA A FUNÇAO PARA FAZER O CALCULO DE
SUBSTITUICAO</p>
<p><strong>PROCESSAMENTO_SALARIAL_DB .CALCULAR_SUBSTITUICAO</strong></p>
<blockquote>
<p>(P_DATA_DE =&gt; Data inicio</p>
<p>P_DATA_ATE =&gt; Data Fim ,</p>
<p>P_TIPREL_DE =&gt; colaborador sustituto ,</p>
<p>P_TIPREL_PARA =&gt; Colaborador Susbstituido,</p>
<p>P_MES_ANO =&gt; devolve o Mês ,</p>
<p>P_NR_DIAS =&gt; devolve o Numero de dias ,</p>
<p>P_VALOR_TIPREL_DE =&gt; devolve o Valor do sustituto,</p>
<p>P_VALOR_TIPREL_PARA =&gt; devolde o Valor do substituido)</p>
</blockquote></td>
<td colspan="2"><em>RH_T_SUBSTITUICAO_DETALHE.MES_ANO</em></td>
</tr>
<tr>
<td style="text-align: left;">Numero de dias</td>
<td><em>Number</em></td>
<td colspan="2"><em>RH_T_SUBSTITUICAO_DETALHE.NR_DIAS</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor do sustituto</td>
<td><em>NUMBER</em></td>
<td
colspan="2"><em>RH_T_SUBSTITUICAO_DETALHE.VALOR_DO_</em>SUSTITUTO</td>
</tr>
<tr>
<td style="text-align: left;">Valor do subsituido</td>
<td><em>NUMBER</em></td>
<td
colspan="2"><em>RH_T_SUBSTITUICAO_DETALHE.VALOR_DO_SUBSTITUIDO</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Anexar Documento</strong></td>
<td></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Documento</td>
<td></td>
<td><em>RH_T_TIPOS_DOCUMENTO.NOME Onde REFERENCIA =
‘<strong>SUBSTITUICAO’</strong></em></td>
<td colspan="2"><em>RH_T_DOCUMENTO.</em> <em>TP_DOCUMENTO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Documento</td>
<td></td>
<td></td>
<td colspan="2"><em>RH_T_DOCUMENTO.DOC_ID</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>OUTRAS
GRAVAÇÕES</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: center;"><p><em><strong>1, gravação
na tabela de RH_T_SUBSTITUICAO</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>ESTADO = “<strong>P</strong>”</em></p></li>
<li><p><em>SUBSTITUTO_TIREPL_ID = ID DE RH_T_TIPOS_RELACIONAMENTO (pega
o do colaborador substituto, onde data_fim não é nulo E
<strong>EST_ULT_ADM</strong> = 1 e <strong>FLG_PROCESSA</strong> =
1)</em></p>
<ol type="1">
<li><p><em><strong><mark>Ao Validar o Registo logo deve fazer um registo
em na tabela</mark></strong></em></p></li>
</ol></li>
</ul>
<ul>
<li><p><em><strong><mark>RH_T_DEF _REMUNERACAO</mark></strong></em></p>
<ul>
<li><p><em><mark><strong>VALOR= diferença salarial (</strong>diferença
entre o salario do colocaborador que vai substituir e o seu salario
<strong>)</strong></mark></em></p></li>
<li><p><em><mark><strong>TM_ID =</strong> tm_id de
<strong>rh_t_param_vinculo_mov (REM_OUTRO)</strong></mark></em></p></li>
<li><p><em><mark><strong>DATA_INICIO</strong> <strong>=</strong> data
inicio de substituição</mark></em></p></li>
<li><p><em><mark><strong>DATA_FIM =</strong> Data fim de
substituição</mark></em></p></li>
</ul></li>
<li><p><em><strong><mark>RH_T_TIPREL_REM_PAG</mark></strong></em></p>
<ul>
<li><p><em><strong><mark>TIPREL_ID = ID de
RH_T_TIPOS_RELACIONAMENTO</mark></strong></em></p></li>
<li><p><em><strong><mark>REM_ID = ID de
RH_T_DEF_REMUNERACOES</mark></strong></em></p></li>
</ul></li>
</ul></td>
<td colspan="2"><p><em>Registo na tabela de validação
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT’ (</strong>DOMAINS =
VALIDACAO_TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘SUBSTITUICAO’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_SUBSTITUICAO</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
</ul>
<ul>
<li><p><em>TIPREL_ID = ID DE RH_T_TIPOS RELACIONAMENTO de colaborador
que será substutido</em></p></li>
</ul>
<ul>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
<td><em>3. Registo de Log no IGRP</em></td>
</tr>
</tbody>
</table>

###### Validar Substituição

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><ul>
<li><p>A validação invoca a mesma página de Registo</p></li>
<li><p>O campo validar deve ficar visivel</p></li>
<li><p>Ao <strong>validar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'A'</strong>.</p></li>
<li><p>Ao <strong>desvalidar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
<li><p>Apôs validação é gerado uma ordem de serviço (<a
href="#modelo-de-ordem-serviço-de-subsituicao">ver template a
seguir</a>)</p></li>
</ul>
<p>1.2.-Ao Validar gera um ordem de Serviço na tabela
<strong>RH_T_ORDEM_SERVICO</strong></p>
<ul>
<li><p>DESCRICAO = ‘Registo de colaborador - ’ ||
RH_T_FUNCIONARIOS.NOME</p></li>
<li><p>REFERENTE = ‘SUBSTITUICAO’</p></li>
<li><p>FUN_ID = RH_T_FUNCIONARIOS.ID</p></li>
<li><p>TIPREL_ID = RH_T_TIPOS_RELACIONAMENTO.ID</p></li>
<li><p>VALIDACAO_ID = RH_T_VALIDACAO.ID</p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

#######  Modelo de ordem serviço de subsituicao 

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th>Pegar o template de informacao em
<strong>RH_T_TEMPLATE_REPORT</strong> onde Referencia =
<strong>SUBSTITUICAO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p><img src="media/image30.png"
style="width:4.22917in;height:4.67639in" /></p>
</blockquote></td>
</tr>
</tbody>
</table>

##### Lista de substituição

<img src="media/image31.png" style="width:9.69306in;height:2.96181in"
alt="Uma imagem com texto, captura de ecrã, Tipo de letra, número Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 6%" />
<col style="width: 32%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte Dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_SUBSTITUICAO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Colaborador Substituido</td>
<td><em>TEXT</em></td>
<td><strong>QUERY</strong>: GET_NOME_COLABORADOR (<em>FUN_ID</em>)</td>
<td><em>RH_T_SUBSTITUICAO.</em> <em>SUBSTITUIDO TIPREL_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td><em>TEXT</em></td>
<td><strong>QUERY</strong>: <em>GET_NOME_CARGO</em></td>
<td><em>RH_T_TIPOS.RELACIONAMENTO. CARG_COD_CARGO</em></td>
</tr>
<tr>
<td style="text-align: left;">Colabordor Sustituto</td>
<td><em>TEXT</em></td>
<td><strong>QUERY</strong>: GET_NOME_COLABORADOR(<em>FUN_ID</em>)</td>
<td><em>RH_T_SUBSTITUICAO.</em> SUSTITUTO_<em>TIPREL_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_SUBSTITUICAO.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_SUBSTITUICAO.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Motivo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_SUBSTITUICAO.MOTIVO</em></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_SUBSTITUICAO.OBSERVACAO</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>AÇÕES</strong></td>
</tr>
<tr>
<td style="text-align: left;">Ver Detalhe</td>
<td colspan="3"><em>Abre o mesmo Formulario de Registo para Ver Detalhe
, para este caso deverá esconder o botão gravar</em></td>
</tr>
</tbody>
</table>

#### Gestão / Histórico Laboral 

##### Editar / Novo relacao Laboral

<img src="media/image32.png" style="width:7.66806in;height:4.39653in"
alt="Uma imagem com texto, captura de ecrã, número, Paralelo Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 8%" />
<col style="width: 0%" />
<col style="width: 34%" />
<col style="width: 0%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="6" style="text-align: center;"><em><strong>CONTRATO
VINCULO</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td>RADIOLIST</td>
<td colspan="2">Só aparece em momento de validar</td>
<td
colspan="2"><em>------------------------------------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">Ordem serviço</td>
<td>Select</td>
<td colspan="2"><strong>DOMAINS</strong> = ORDEM_SERVICO</td>
<td colspan="2"><em>RH_T_ORDEM_SERVICO .NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">Contrato</td>
<td>TEXT</td>
<td colspan="2">RH_T_PARAM_CONTRATO.NOME</td>
<td colspan="2"><em>RH_T_CONTRATO_VINCULO.TP_CONTRATO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Vinculo</td>
<td>TEXT</td>
<td colspan="2">RH_T_PARAM_VINCULO.NOME</td>
<td colspan="2"><em>RH_T_CONTRATO_VINCULO.VINCULO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>MOBILIDADE</strong></td>
<td></td>
<td colspan="2"></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Mobilidade</td>
<td>SELECT</td>
<td colspan="2"><p><em><strong>DOMAINS</strong>=</em> TIPO_MOV_LABORAL,
pega valor de cada campo Referencia = MOBILIDADE</p>
<p><em><strong>Nota</strong>: Guarda várias valores em mesmo
campo</em></p></td>
<td colspan="2"><p><em>RH_T_MOBILIDADE.TIPO_SITUACAO</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.TIPO_SITUACAO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Direcção</td>
<td>SELECT</td>
<td colspan="2">INPSSIGOF.INSTITUICOES.nome</td>
<td colspan="2"><p><em>RH_T_MOBILIDADE.INSTIT_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Secção</td>
<td>SELECT</td>
<td colspan="2"></td>
<td colspan="2"><p><em>RH_T_MOBILIDADE.SECAO_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.SECAO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Data inicio Mobilidade</td>
<td>DATE</td>
<td colspan="2"></td>
<td colspan="2"><em>RH_T_MOBILIDADE.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim Mobilidade</td>
<td>DATE</td>
<td colspan="2"></td>
<td colspan="2"><em>RH_T_MOBILIDADE.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Local de Trabalho</td>
<td>SELECT</td>
<td colspan="2"><em>RH_T_PARAM_LOCAL_TRAB.</em>.NOME</td>
<td colspan="2"><p><em>RH_T_MOBILIDADE.LOCAL_TRAB_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Pais</td>
<td>TEXT</td>
<td colspan="2"></td>
<td colspan="2"><em>-----------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">Ilha</td>
<td>TEXT</td>
<td colspan="2"><p><em>RH_T_PARAM_LOCAL_TRAB.ILHA_ID,</em></p>
<p><em>SIPSGLOBAL.GLB_GEOGRAFIA</em></p></td>
<td colspan="2"><em>-----------------------------</em></td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td>SELECT</td>
<td colspan="2"></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>CARREIRA</strong></td>
<td></td>
<td colspan="2"></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Alteração Carreira</td>
<td>SELECT</td>
<td colspan="2"><p><em><strong>DOMAINS</strong>=</em> TIPO_MOV_LABORAL,
pega valor de cada campo Referencia = <strong>CARREIRA</strong></p>
<p><em><strong>Nota</strong>: Guarda várias valores em mesmo
campo</em></p></td>
<td colspan="2"><p><em>RH_T_CARREIRAE.TIPO_SITUACAO</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.TIPO_SITUACAO</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td>SELECT</td>
<td colspan="2"><strong>RH_T_PARAM_CARREIRA.</strong>NOME</td>
<td colspan="2"><p><em>RH_T_CARREIRA.CARR_PCCS_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. CARR_PCCS_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Categoria</td>
<td>SELECT</td>
<td colspan="2"></td>
<td colspan="2"><p><em>RH_T_CARREIRA.</em> <em>CATEGORIA_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.CATEGORIA_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Escalão</td>
<td>SELECT</td>
<td colspan="2"><strong>RH_T_PARAM_ESCALAO</strong>.NIVEL_REFERENCIA||
<strong>RH_T_PARAM_ESCALAO.</strong> ESCALAO</td>
<td colspan="2"><p><em>RH_T_CARREIRA.</em> <em>ESCALAO_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio Carreira</td>
<td>DATE</td>
<td colspan="2"></td>
<td colspan="2"><em>RH_T_CARREIRA.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim Carreira</td>
<td>DATE</td>
<td colspan="2"></td>
<td colspan="2"><em>RH_T_CARREIRA.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>SITUACAO LABORAL</strong></td>
<td></td>
<td colspan="2"></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Situação Laboral</td>
<td>SELECT</td>
<td colspan="2"><p>RH_T_PARAM_SITUACAO.NOME</p>
<p>Nota: o combo deve vir preenchido somente com informacao de situação
laboral, ou seja <em><strong>RH_T_PARAM_SITUACAO</strong>.
FLG_SITUACAO_LABORAL = 1</em></p></td>
<td colspan="2"><em>RH_T_SITUACAO_LABORAL.SITUACAO_LABORAL_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Motivo</td>
<td>SELECT</td>
<td colspan="2">RH_T_PARAM_SITUACAO_DET. <em>MOTIVO</em></td>
<td colspan="2"><em>RH_T_SITUACAO_LABORAL.</em>
<em>MOTIVO_SIT_LAB_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio Situação</td>
<td>DATE</td>
<td colspan="2"></td>
<td colspan="2"><em>RH_T_SITUACAO_LABORAL.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim Situação</td>
<td>DATE</td>
<td colspan="2"></td>
<td colspan="2"><em>RH_T_SITUACAO_LABORAL.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td>TEXTAREA</td>
<td colspan="2"></td>
<td colspan="2"><em>RH_T_SITUACAO_LABORAL.OBS</em></td>
</tr>
<tr>
<td colspan="6"
style="text-align: center;"><em><strong>ACÕES</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">Editar</td>
<td colspan="5"></td>
</tr>
<tr>
<td style="text-align: left;">Regime Trabalho</td>
<td></td>
<td colspan="2"></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>REGRAS</strong></td>
<td></td>
<td colspan="2"></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><p>1. Botão Editar:</p>
<p><em><strong>1. Caso o Registo já tenha um processamento
(RH_T_TIPOS_RELACIONAMENTO.FLG_PROCESSAMENTO não é
nulo)</strong></em></p>
<ul>
<li><p><em>Para verificar se o RH_T_TIPOS_RELACIONAMENTO já tem um
registo, deve-se ver se o flg_processamento não é nulo. Nesse caso ao
actualizar um campo de carreira ou mobilidade e situação Laboral,
deve-se fazer um novo registo. Para este caso não faz um novo registo,
mas sim atualiza os dados nas tabelas (<strong>RH_T_MOBILIADE</strong>,
<strong>RH_T_CARREIRA</strong>, <strong>RH_T_SITUACAO_LABORAL</strong>,
<strong>RH_T_TIPOS_RELACIONAMENTO</strong>)</em></p></li>
</ul>
<p><em><strong>1.2- Caso o Registo ainda não tenha nenhum
Processamento</strong></em></p>
<ul>
<li><p><em><strong>Ao alterar uma mobilidade</strong></em></p>
<ul>
<li><p><em>Deve fazer um novo registo na tabela
<strong>RH_T_MOBILIDADE</strong></em></p></li>
</ul></li>
<li><p><em><strong>Ao Alterar uma carreira</strong></em></p>
<ul>
<li><p><em>Deve fazer um novo registo na tabela
<strong>RH_T_CARREIRA</strong></em></p></li>
</ul></li>
<li><p><em><strong>Ao alterar uma situacao Laboral</strong></em></p>
<ul>
<li><p><em>Deve-se fazer um novo registo na tabela
<strong>RH_T_SITUACAO_LABORAL</strong></em></p></li>
</ul></li>
<li><p><em>Faz um novo registo na tabela
<strong>RH_T_TIPOS_RELACIONAMENTO</strong></em></p></li>
<li><p><em>FAZ NOVO REGISTO NA TABELA
<strong>RH_T_VALIDACAO</strong></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">OUTRAS GRAVAÇÕES</td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em>1—Caso For Alterado
Dados de <strong>Mobilidade</strong> ,</em></p>
<ul>
<li><p><em>Logo deve Registar na TABELA
<strong>RH_T_MOBILIDADE</strong></em></p></li>
</ul>
<p><em>2—caso for alterado Carreira</em></p>
<ul>
<li><p><em>ogo deve Registar na TABELA
<strong>RH_T_CARREIRA</strong></em></p></li>
</ul>
<p><em>2.2 Caso for alterado salario,</em></p>
<ul>
<li><p><em>logo fecha o registo anterior e faz novo registo em
<strong>RH_T_DEF_REMUNERACAO</strong></em></p></li>
</ul></td>
<td colspan="2"><ol start="3" type="1">
<li><p><em>- Caso a situacao Laboral For</em></p></li>
</ol>
<ul>
<li><p><em>logo deve Registar na
<strong>RH_T_SITUACAO_LABORAL</strong></em></p></li>
</ul>
<p><em>4--a alteracao em qualquer uma das tabelas deve ter um novo
registo em <strong>RH_T_TIPOS_RELACIONAMENTO</strong> e nova associação
em <strong>RH_T_TIPREL_REM_PAG </strong></em></p></td>
<td><em>5—Regista na tabela de validacao
<strong>RH_T_VALIDACAO</strong></em></td>
</tr>
</tbody>
</table>

##### Lista Relação Laboral

<img src="media/image33.png" style="width:9.69306in;height:3.46181in"
alt="Uma imagem com texto, número, Tipo de letra, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 6%" />
<col style="width: 32%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte de dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Situação Atual</td>
<td style="text-align: center;"></td>
<td></td>
<td><p>RH_T_CARREIRA.EST_ACT_ADM</p>
<p><em>RH_V_RELACAO_LABORAL.ULTIMO_VINCULO</em></p></td>
</tr>
<tr>
<td style="text-align: center;">vinculo</td>
<td style="text-align: center;"></td>
<td><strong>RH_T_PARAM_VINCULO</strong>.NOME</td>
<td><em>RH_T_CONTRATO_VINCULO.VINCULO_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td style="text-align: center;"></td>
<td><strong>INPSIGOF.INSITUTICOES</strong>.NOME</td>
<td
style="text-align: left;"><em><strong>RH_T_MOBILIDE.</strong>INSTIT_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Seccão</td>
<td style="text-align: center;"></td>
<td></td>
<td
style="text-align: left;"><em><strong>RH_T_MOBILIDE.</strong>SECAO_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td style="text-align: center;"></td>
<td></td>
<td
style="text-align: left;"><em><strong>RH_T_CARREIRA.</strong>CARR_PCCS_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Escalão</td>
<td style="text-align: center;"></td>
<td><strong>RH_T_PARAM_ESCALAO.</strong> REFERENCIA / NIVEL</td>
<td
style="text-align: left;"><em><strong>RH_T_CARREIRA.ESCALAO_ID</strong></em></td>
</tr>
<tr>
<td style="text-align: center;">Data Inicio Contrato / Data fim
contrato</td>
<td style="text-align: center;"></td>
<td></td>
<td
style="text-align: left;"><em><strong>RH_T_CONTRATO_VINCULO.</strong>DATA_INICIO
<strong>|| RH_T_CARREIRA.</strong>DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data inicio Carreira / Data Fim
Carreira</td>
<td style="text-align: center;"></td>
<td></td>
<td
style="text-align: left;"><em><strong>RH_T_CONTRATO_VINCULO.</strong>DATA_FIM
<strong>|| RH_T_CARREIRA.</strong>DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: center;">Situação Laboral</td>
<td style="text-align: center;"></td>
<td><strong>RH_T_PARAM_SITUACAO.</strong>NOME</td>
<td
style="text-align: left;"><em><strong>RH_T_SITUACAO_LABORAL.SITUACAO_LABORAL_ID</strong></em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Ações</strong></td>
<td style="text-align: center;"></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Novo / Editar</td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Regime Emprego</td>
<td></td>
<td colspan="2"></td>
</tr>
</tbody>
</table>

##### Historico Laboral

A lista deve apresentar somente dados ativos.

<img src="media/image34.png" style="width:9.69306in;height:5.22917in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 6%" />
<col style="width: 32%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>FIltro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte de dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Referencia</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td><strong>DOMAINS</strong> = TIPO_MOV_LABORAL<em>, pega somente
Referencia</em></td>
<td><em>RH_V_HIST_LABORAL. REFERENCIA</em></td>
</tr>
<tr>
<td style="text-align: center;">Tipo Situação</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td><em><strong>DOMAINS</strong>=</em> TIPO_MOV_LABORAL, pega valor de
cada campo Referencia</td>
<td><em>RH_V_HIST_LABORAL .TIPO_SITUACAO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td style="text-align: center;"><em>DATE</em></td>
<td></td>
<td style="text-align: left;"><em>RH_V_HIST_LABORAL
.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td style="text-align: center;"><em>DATE</em></td>
<td></td>
<td style="text-align: left;"><em>RH_V_HIST_LABORAL .DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td colspan="2"><strong>Fonte de dados</strong></td>
</tr>
<tr>
<td style="text-align: left;">Ultimo Movimento</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL.ULTIMO_VINCULO</td>
</tr>
<tr>
<td style="text-align: left;">Tipo Situação</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL. TIPO_SITUACAO_DESC</td>
</tr>
<tr>
<td style="text-align: left;">Tipo contrato</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL.<em>TIPO_CONTRATO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Vinculo</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL.<em>VINCULO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL . <em>DIRECAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Seção</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL . <em>SECCAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Referencia / Escalão</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL .
<em>REFERENCIA_ESCALAO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL. <em>CARGO_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL.<em>CARREIRA_DESC</em></td>
</tr>
<tr>
<td style="text-align: left;">Situação Laboral</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL . SITUACAO_LABORAL</td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio / Data Fim</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_V_HIST_LABORAL . <em>DATA_INICIO /</em>
RH_V_HIST_LABORAL . <em>DATA_FIM</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: left;">Registar Vinculo</td>
<td colspan="3">Abre formulário para o registo de um novo vinculo
Laboral</td>
</tr>
<tr>
<td style="text-align: left;">Editar Vinculo</td>
<td colspan="3">Abre o mesmo formulario de registo</td>
</tr>
<tr>
<td style="text-align: left;">Eliminar</td>
<td colspan="3">Permite eliminar os dados registados</td>
</tr>
<tr>
<td style="text-align: left;">Alterar Situaçao Laboral</td>
<td colspan="3">Abre o formulário para editar situação Laboral de um
colaborador</td>
</tr>
<tr>
<td style="text-align: left;">Alterar Regime</td>
<td colspan="3">Abre o formulário para alterar regime de um
colaborador</td>
</tr>
</tbody>
</table>

##### ~~Alterar Situação Laboral -~~ descontinuado

<img src="media/image35.png" style="width:9.69306in;height:4.31458in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 7%" />
<col style="width: 19%" />
<col style="width: 8%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th
style="text-align: center;"><strong><del>Formulario</del></strong></th>
<th style="text-align: center;"><strong><del>Tipo</del></strong></th>
<th colspan="2"
style="text-align: center;"><strong><del>Descrição</del></strong></th>
<th
style="text-align: center;"><strong><del>Gravação</del></strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><del>Validar</del></td>
<td><em><del>RADIOLIST</del></em></td>
<td colspan="2"><p><del>Esse deve ficar oculto, so fica visivel em caso
de validação desse Registo. No modo validação esse campo é
obrigatorio</del></p>
<p><del>DOMAINS= SIM_NAO</del></p></td>
<td><em><del>RH_T_SITUACAO_LABORAL.ESTADO</del></em></td>
</tr>
<tr>
<td style="text-align: left;"><del>Gerar Ordem Serviço</del></td>
<td></td>
<td colspan="2"><p><del>Esse deve ficar oculto, so fica visivel em caso
de validação desse Registo. No modo validação esse campo é
obrigatorio</del></p>
<p><del>DOMAINS= SIM_NAO</del></p></td>
<td><em><del>------------------------------------------------------------</del></em></td>
</tr>
<tr>
<td style="text-align: left;"><del>Tipo ordem serviço</del></td>
<td><em><del>Select</del></em></td>
<td colspan="2"><del><strong>DOMAINS</strong> = ORDEM_SERVICO</del></td>
<td><p><del><em>RH_T_ORDEM_SERVICO.</em> motivo- ’ ||
RH_T_FUNCIONARIOS.NOME</del></p>
<p><del><em>RH_T_ORDEM_SERVICO.</em> REFERENTE =
´STUACAO_LABORAL</del></p></td>
</tr>
<tr>
<td style="text-align: left;"><del>Vinculo</del></td>
<td></td>
<td colspan="2"><del>Traz por defeiro o vinculo de
RH_T_TIPOS_RELACIONAMENTO de est_act_adm «1</del></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><del>*situação Laboral</del></td>
<td><em><del>SELECT</del></em></td>
<td colspan="2"><p><em><del>Tabela : RH_T_PARAM_SIT_LABORAL ,
RH_T_PARAM_SITUACAO_DET</del></em></p>
<p><del>(traz os tipo de situacao associado ao vinculo)</del></p></td>
<td><em><del>RH_T_SITUACAO_LABORAL. SITUACAO_LABORAL_ID</del></em></td>
</tr>
<tr>
<td style="text-align: left;"><del>*Motivo</del></td>
<td><em><del>SELECT</del></em></td>
<td colspan="2"><p><del><strong>DOMAINS</strong> = MOTIVO_SIT_LABORAL
e</del></p>
<p><del>(traz somente os vinculo associados ao motivo)</del></p></td>
<td><p><em><del>RH_T_SITUACAO_LABORAL. MOTIVO_SIT_LAB</del></em></p>
<p><em><del>RH_T_TIPOS_RELACIONAMENTO.
MOTIVO_SIT_LAB</del></em></p></td>
</tr>
<tr>
<td style="text-align: left;"><del>*Data Inicio</del></td>
<td></td>
<td colspan="2"></td>
<td><p><em><del>RH_T_SITUACAO_LABORAL.DATA_INICIO</del></em></p>
<p><em><del>RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO</del></em></p></td>
</tr>
<tr>
<td style="text-align: left;"><del>Data Fim</del></td>
<td><em><del>DATE</del></em></td>
<td colspan="2"><del>Data fim de contrato</del></td>
<td><p><em><del>RH_T_SITUACAO_LABORAL.DATA_FIM</del></em></p>
<p><em><del>RH_T_TIPOS_RELACIONAMENTO.DATA_FIM</del></em></p></td>
</tr>
<tr>
<td style="text-align: left;"><del>Observação</del></td>
<td></td>
<td colspan="2"></td>
<td><p><em><del>RH_T_SITUACAO_LABORAL. OBS</del></em></p>
<p><em><del>RH_T_TIPOS_RELACIONAMENTO. OBS</del></em></p></td>
</tr>
<tr>
<td style="text-align: left;"><del>Hitorico situçao Laboral</del></td>
<td></td>
<td colspan="2"><em><del>Traz todos os registos RH_T_SITUACAO_LABORAL
que não estejam eliminados</del></em></td>
<td></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong><del>OUTRAS
GRAVAÇOES</del></strong></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em><del>1.Faz update nos
registos anteriores , ou seja inativa os registos ativos</del></em></p>
<p><em><del>1.1Inativa a mobilidade em estado ativo
<strong>RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e data fim is not
null)</strong></del></em></p>
<ul>
<li><p><em><del>DATA_FIM = data Inicio</del></em></p></li>
<li><p><em><del>USER_ALTERACAO _ID = utilizador
logado</del></em></p></li>
<li><p><em><del>USER_ALTERACAO_NAME = <strong>nome de
utilizador</strong></del></em></p></li>
<li><p><em><del>DATA_ALTERACAO = sysdate</del></em></p></li>
<li><p><em><del>EST_ACT_ADM = 0</del></em></p></li>
</ul>
<p><em><del>1.2 Fazer uma nova gravação na tabela de
<strong>RH_T_TIPOS_RELACIONAMENTO</strong>, pegas todas informações do
registo anterior , e regista com novas alteraçoes nos campos do
formulario e outros seguinte campos</del></em></p>
<ul>
<li><p><em><del>DATA_REGISTO= ‘SYSDATE’</del></em></p></li>
<li><p><em><del>USER_REGISTO_ID = id de utilizador
Logado</del></em></p></li>
<li><p><em><del>USER_REGISTO_NAME = nome de utilizador
Logado</del></em></p></li>
<li><p><em><del>USER_ALTERACAO _ID = NULL</del></em></p></li>
<li><p><em><del>DATA_INICIO</del></em></p></li>
<li><p><em><del>USER_ALTERACAO_NAME = NULL</del></em></p></li>
<li><p><em><del>DATA_ALTERACAO = NULL</del></em></p></li>
<li><p><em><del>TIPREL_ID = ID DO REGISTO FECHADO (id de
RH_TIPOS_RELAIONAMENTO)</del></em></p></li>
<li><p><em><del>SITUACAO_LAB_ID = ID DE SITUACAO
LABORAL</del></em></p></li>
<li><p><em><del>EST_ACT_ADM = 1</del></em></p></li>
<li><p><em><del>REFERENTE = ‘SITUACAO_LABORAL’</del></em></p></li>
<li><p><del><em>TIPO_SITUACAO = ´</em> MUDANCA_SITUACAO_LAB
<em>´</em></del></p></li>
<li><p><em><del>ESTADO = ‘P’</del></em></p></li>
</ul></td>
<td colspan="2" style="text-align: left;"><p><em><del>2-insert em
<strong>RH_T_SITUACAO_LABORAL</strong></del></em></p>
<ul>
<li><p><em><del>DATA_REGISTO=
‘<strong>SYSDATE’</strong></del></em></p></li>
<li><p><em><del>USER_REGISTO_ID = id de utilizador
Logado</del></em></p></li>
<li><p><em><del>USER_REGISTO_NAME = nome de utilizador
Logado</del></em></p></li>
<li><p><em><del>USER_ALTERACAO _ID =
<strong>NULL</strong></del></em></p></li>
<li><p><em><del>USER_ALTERACAO_NAME =
<strong>NULL</strong></del></em></p></li>
<li><p><em><del>DATA_ALTERACAO =
<strong>NULL</strong></del></em></p></li>
<li><p><em><del>ESTADO = “<strong>P</strong>”</del></em></p></li>
<li><p><em><del>FUN_ID = id de RH_T_FUNCIONARIOS</del></em></p></li>
<li><p><em><del>CONTR_VINCULO_ID = ID DE
RH_T_CONTRATO_VINCULO</del></em></p></li>
</ul>
<p><em><del>2.2-update
<strong>RH_T_CONTRATO_VINCULO.SITUACAO_LABORAL</strong></del></em></p>
<p><em><del>3-Caso o Caso o tipo de situação for ‘Cessar’, logo deve faz
update nas seguintes tabelas :</del></em></p>
<ul>
<li><p><em><del>RH_T_CONTRATO_VINCULO.DATA_FIM</del></em></p></li>
<li><p><em><del>RH_T_TIPOS_RELACIONAMENTO.DATA_FIM</del></em></p></li>
<li><p><em><del>RH_T_DEF_REMUNERACAO.DATA_FIM</del></em></p></li>
<li><p><em><del>RH_T_CARREIRA.DATA_FIM</del></em></p></li>
<li><p><em><del>RH_T_MOBILIDADE.DATA_FIM</del></em></p></li>
<li><p><em><del>RH_T_DEF_PAGAMENTO.DATA_FIM</del></em></p></li>
</ul>
<p><em><del>4-Registo em <strong>RH_T_AVALIACAO</strong></del></em></p>
<p><em><del>5.Registo em <strong>RH_T_LOG</strong>,
<strong>RH_T_VALIDACAO_DETALHE</strong></del></em></p></td>
</tr>
</tbody>
</table>

###### Validação

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><ul>
<li><p>A validação invoca a mesma página de Registo</p></li>
<li><p>O campo validar deve ficar visivel</p></li>
<li><p>Ao <strong>validar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'A'</strong>.</p></li>
<li><p>Ao <strong>desvalidar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
<li><p>Apôs validação é gerado uma ordem de serviço</p></li>
</ul>
<p>1.2.-Ao Validar gera um ordem de Serviço na tabela
<strong>RH_T_ORDEM_SERVICO</strong></p>
<ul>
<li><p>DESCRICAO = motivo- ’ || RH_T_FUNCIONARIOS.NOME</p></li>
<li><p>REFERENTE = ´STUACAO_LABORAL´</p></li>
<li><p>FUN_ID = RH_T_FUNCIONARIOS.ID</p></li>
<li><p>TIPREL_ID = RH_T_TIPOS_RELACIONAMENTO.ID</p></li>
<li><p>VALIDACAO_ID = RH_T_VALIDACAO.ID</p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

##### Regime Trabalho 

######  Alterar regime trabalho

<img src="media/image36.png" style="width:9.69306in;height:3.33889in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 7%" />
<col style="width: 48%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>form</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>GRAVAÇÃO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td><p>Esse deve ficar oculto, so fica visivel em caso de validação
desse Registo. No modo validação esse campo é obrigatorio</p>
<p>DOMAINS= SIM_NAO</p></td>
<td><em>RH_T_REGIME_TRAB.ESTADO</em></td>
</tr>
<tr>
<td
style="text-align: left;">--------------------------------------</td>
<td><em>SELECT</em></td>
<td><p>Pega o ultimo Vinculo do colaborador , e o contrato atual</p>
<p><em><strong>Nota</strong>: O <strong>regime de trabalho</strong> está
associado ao <strong>contrato do colaborador</strong>.<br />
No entanto, é possível guardar informações adicionais de contexto — por
exemplo, <strong>em que unidade, secção ou local o colaborador estava a
exercer funções</strong> no momento em que o regime foi aplicado. È
possivel em casos ectermos que um colaborador tenha dois
regime(covid)</em></p></td>
<td><p><em>RH_T_REGIME_TRAB.FUN_ID</em></p>
<p><em>RH_T_REGIME_TRAB.CONTRATO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">`*Tipo Regime</td>
<td><em>MULTISELECT</em></td>
<td><strong>DOMAIN</strong> = REGIME_TRABALHO</td>
<td><p><em>RH_T_REGIME_TRAB.TIPO_REGIME</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.REGIME</em></p></td>
</tr>
<tr>
<td style="text-align: left;">*Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_REGIME_TRAB.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">*Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_REGIME_TRAB.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td><em>SELECT</em></td>
<td>Deve ficar Visivel somente na Edição</td>
<td>RH_T_REGIME_TRAB<em>.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">*Modalidade</td>
<td><em>SELECT</em></td>
<td><p>Por defeito vem preencho.</p>
<p><strong>DOMAINS</strong> = MODALIDADE_REGIME</p></td>
<td>RH_T_REGIME_MODAL<em>.MODALIDADE</em></td>
</tr>
<tr>
<td style="text-align: left;">Dias Semana</td>
<td><em>Select</em></td>
<td><strong>DOMAINS</strong> = DIAS_SEMANA</td>
<td>RH_T_REGIME_MODAL.DIAS_SEMANA</td>
</tr>
<tr>
<td style="text-align: left;">Numero Horas</td>
<td><em>Number</em></td>
<td></td>
<td>RH_T_REGIME_MODAL.NUM_HORAS</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em>Validar Campos Obrigatorios</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>OUTRA
GRAVAÇOES</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>UPDATE</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><em>Faz update nos
registos anteriores , ou seja inativa os registos ativos</em></p>
<p><em><del>1.2Inativa a mobilidade em estado ativo
<strong>RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e data fim is not
null)</strong></del></em></p>
<ul>
<li><p><em><del>DATA_FIM = data Inicio Carreira -1</del></em></p></li>
<li><p><em><del>USER_ALTERACAO _ID = utilizador
logado</del></em></p></li>
<li><p><em><del>USER_ALTERACAO_NAME = <strong>nome de
utilizador</strong></del></em></p></li>
<li><p><em><del>DATA_ALTERACAO = sysdate</del></em></p></li>
<li><p><em><del>EST_ACT_ADM = 0</del></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>INSERT</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ol type="1">
<li><p><strong>Registar</strong></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <em>RH_T_REGIME_TRAB</em>, bem como os campos adicionais
especificados a seguir</p></li>
</ol></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <em>RH_T_REGIME_MODAL</em>, bem como os campos adicionais
especificados a seguir</p></li>
</ol></li>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>REGIME_ID = ID DE RH_T_REGIME_TRAB</em></p>
<ol type="1">
<li><p><em><del>Fazer uma nova gravação na tabela de
RH_T_TIPOS_RELACIONAMENTO, pegas todas informações onde campo
est_ult_adm = 1 (faz update est_ult_adm = 0 ) , e regista com novas
alteraçoes nos campos do formulario e outros seguinte
campos</del></em></p></li>
</ol></li>
</ul>
<ul>
<li><p><em><mark><del>DATA_REGISTO= ‘SYSDATE’</del></mark></em></p></li>
<li><p><em><mark><del>USER_REGISTO_ID = id de utilizador
Logado</del></mark></em></p></li>
<li><p><em><mark><del>USER_REGISTO_NAME = nome de utilizador
Logado</del></mark></em></p></li>
<li><p><em><mark><del>USER_ALTERACAO _ID =
NULL</del></mark></em></p></li>
<li><p><em><mark><del>DATA_INICIO</del></mark></em></p></li>
<li><p><em><mark><del>USER_ALTERACAO_NAME =
NULL</del></mark></em></p></li>
<li><p><em><mark><del>DATA_ALTERACAO = NULL</del></mark></em></p></li>
<li><p><em><mark><del>REGIME_ID = ID DE
RH_T_REGIME</del></mark></em></p></li>
<li><p><em><mark><del>EST_ULT_ADM = 1</del></mark></em></p></li>
<li><p><em><mark><del>ESTADO = ‘P’</del></mark></em></p></li>
<li><p><mark><del><em>TIPO_SITUACAO =
‘</em>MUDANCA_REGIME<em>’</em></del></mark></p></li>
<li><p><em><mark><del>TIPO_MOV_LABORAL =
‘REGIME”</del></mark></em></p></li>
<li><p><em><mark><del>OBS = ‘REGIME- ||
TIPO_REGIME´’</del></mark></em></p></li>
<li><p><em><mark><del>REFERENTE = ‘REGIME’</del></mark></em></p>
<ol type="1">
<li><p><em>Faz update na tabela RH_TIPOS_RELACIONAMENTO</em></p></li>
</ol></li>
</ul>
<ul>
<li><p><em>REGIME_ID = NOVO ID DE RH_T_REGIME</em></p></li>
</ul>
<ol start="2" type="1">
<li><p><strong>Editar</strong></p></li>
</ol>
<ul>
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE’</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p>
<ol type="1">
<li><p><em><del>Eliminar linha Modalidade
<strong>RH_T_REGIME_MODAL</strong></del></em></p></li>
</ol></li>
</ul>
<ul>
<li><p><em><del>USER_ALTERACAO _ID = id de utilizador
Logado</del></em></p></li>
<li><p><em><del>DATA_ALTERACAO =
<strong>SYSDATE’</strong></del></em></p></li>
<li><p><em><del>DATA_ALTERACAO_NAME = nome de utilizador
Logado</del></em></p></li>
<li><p><em><del>ESTADO = I</del></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

###### Lista Regime 

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 6%" />
<col style="width: 32%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>FIltro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte de dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Estado</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td><strong>DOMAINS</strong> = STATUS</td>
<td style="text-align: left;"><em>RH_T_REGIME_TRAB</em>.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;">Tipo Regime</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td><strong>DOMAIN</strong> = REGIME_TRABALHO</td>
<td style="text-align: left;"><em>RH_T_REGIME_TRAB</em>.
<em>TIPO_REGIME</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td colspan="2"><strong>Fonte de dados</strong></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Regime</td>
<td><em>TEXT</em></td>
<td colspan="2"><em>RH_T_REGIME_TRAB</em>. <em>TIPO_REGIME</em></td>
</tr>
<tr>
<td style="text-align: left;">Data inicio</td>
<td><em>TEXT</em></td>
<td colspan="2"><em>RH_T_REGIME_TRAB.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>TEXT</em></td>
<td colspan="2"><em>RH_T_REGIME_TRAB.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Modalidade</td>
<td><em>TEXT</em></td>
<td colspan="2"><em>Agrupar (RH_T_REGIME_MODAL.DIAS_SEMANA)</em></td>
</tr>
<tr>
<td style="text-align: left;">Numero Horas</td>
<td><em>TEXT</em></td>
<td colspan="2"><em>Sum (RH_T_REGIME_MODAL.</em>NUM_HORAS</td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td><em>TEXT</em></td>
<td colspan="2"><em>RH_T_REGIME_TRAB.ESTADO</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td style="text-align: left;">Editar</td>
<td colspan="3"><em>Abre a mesma pagina de Registar</em></td>
</tr>
</tbody>
</table>

#### Rendimentos / Encargo

##### Lista 

1)  Rendimentos / Abonos / Subsidio

<img src="media/image37.png" style="width:9.69306in;height:3.71667in"
alt="Uma imagem com texto, número, software, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 6%" />
<col style="width: 38%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_V_DEF_REMUNERACAO. DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_V_DEF_REMUNERACAO. DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td><em>select</em></td>
<td><em><strong>DOMAINS</strong> = STATUS</em></td>
<td><em>RH_V_DEF_REMUNERACAO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_DEF_REMUNERACAO.ESTADO_DESC</em></td>
</tr>
<tr>
<td style="text-align: center;">Movimento</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_DEF_REMUNERACAO.</em> descricao</td>
</tr>
<tr>
<td style="text-align: center;">Valor</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_V_DEF_REMUNERACAO.VALOR</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td><em>TEXT</em></td>
<td>A data deve sempre dia / mm / yyyy</td>
<td><em>RH_V_DEF_REMUNERACAO.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td><em>TEXT</em></td>
<td>A data deve sempre dia / mm / yyyy</td>
<td><em>RH_V_DEF_REMUNERACAO.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: center;">Ultimo Proc</td>
<td><em>TEXT</em></td>
<td>A data deve sempre dia / mm / yyyy</td>
<td><em>RH_V_DEF_REMUNERACAO.</em>DATA_ULTIMO_PROC</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">REGRAS</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em><strong>Por defeito</strong>, devem ser apresentadas apenas
as remunerações ativas.
<strong>RH_V_DEF_REMUNERACAO</strong>)</em></p></li>
<li><p><em>Deve apresentar informacoes cujo
<strong>RH_V_DEF_REMUNERACAO.</strong>estado_por_defeito =
‘A’</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

####### 

2)  Pagamentos / Desconto

<img src="media/image38.png" style="width:9.69306in;height:2.49375in"
alt="Uma imagem com texto, captura de ecrã, file, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 6%" />
<col style="width: 38%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td><em><mark>RH_V_DEF_PAGAMENTO. DATA_INICIO</mark></em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td><em><mark>RH_V_DEF_PAGAMENTO. DATA_FIM</mark></em></td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td><em>select</em></td>
<td><em><strong>DOMAINS</strong> = STATUS</em></td>
<td><em><mark>RH_V_DEF_PAGAMENTO.ESTADO</mark></em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td><em><mark>RH_V_DEF_PAGAMENTO.ESTADO_DESC</mark></em></td>
</tr>
<tr>
<td style="text-align: center;">Movimento</td>
<td><em>TEXT</em></td>
<td></td>
<td><em><mark>RH_V_DEF_PAGAMENTO. DESCRICAO</mark></em></td>
</tr>
<tr>
<td style="text-align: center;">Valor</td>
<td><em>TEXT</em></td>
<td></td>
<td><em><mark>RH_V_DEF_PAGAMENTO.VALOR</mark></em></td>
</tr>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td><em>TEXT</em></td>
<td><mark>A data deve sempre dia / mm / yyyy</mark></td>
<td><em><mark>RH_V_DEF_PAGAMENTO.DATA_INICIO</mark></em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td><em>TEXT</em></td>
<td><mark>A data deve sempre dia / mm / yyyy</mark></td>
<td><em><mark>RH_V_DEF_PAGAMENTO.DATA_FIM</mark></em></td>
</tr>
<tr>
<td style="text-align: center;">Ultimo Proc</td>
<td><em>TEXT</em></td>
<td><mark>A data deve sempre dia / mm / yyyy</mark></td>
<td><mark><em>RH_V_DEF_PAGAMENTO..</em>DATA_ULTIMO_PROC</mark></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">REGRAS</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em><strong>Por defeito</strong>, devem ser apresentadas apenas
as remunerações ativas.
<strong>RH_V_DEF_REMUNERACAO</strong>)</em></p></li>
</ul>
<blockquote>
<p><em><mark>Deve apresentar informacoes cujo
<strong>RH_V_DEF_REMUNERACAO.</strong>estado_por_defeito =
‘A’</mark></em></p>
</blockquote></td>
</tr>
</tbody>
</table>

#####  Novo

1)  Remuneração

<table style="width:100%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 7%" />
<col style="width: 2%" />
<col style="width: 33%" />
<col style="width: 0%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="3"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><strong>Remuneracção /Abonos
/subsidio</strong></td>
<td colspan="5"></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td colspan="3"><p>Esse deve ficar oculto, so fica visivel em caso de
validação desse Registo. No modo validação esse campo é obrigatorio</p>
<p>DOMAINS= SIM_NAO</p></td>
<td><em>RH_T_DEF_REMUNERACOES.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Movimento</td>
<td><em>SELECT</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_REMUNERACOES.</em> <em>TM_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor</td>
<td><em>NUMBER</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_REMUNERACOES.</em> <em>VALOR</em></td>
</tr>
<tr>
<td style="text-align: left;">Percentagem</td>
<td><em>NUMBER</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_REMUNERACOES.PERCENTAGEM</em></td>
</tr>
<tr>
<td style="text-align: left;">Moeda</td>
<td><em>SELECT</em></td>
<td colspan="3"><mark>Por defeito de trazer preenchido
<strong>CVE</strong></mark></td>
<td><em>RH_T_DEF_REMUNERACOES.</em> <em>MOEDA</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio</td>
<td><em>DATE</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_REMUNERACOES.</em> <em>DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_REMUNERACOES. DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_REMUNERACOES.OBS</em></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><ul>
<li><p><em>Caso a remuneração já tenha um processamento , logo somente
se pode alterar Data Fim e Observaçáo</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><strong>OUTRAS
GRAVAÇOES</strong></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em><strong>1,</strong>
gravação na tabela de <strong>RH_T_DEF_REMUNERACOES</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>CARREIRA_ID</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>ESTADO = “<strong>P</strong>”</em></p></li>
<li><p><em>OBS = ‘NOVO_RENDIMENTO’’</em></p></li>
</ul>
<ol start="3" type="1">
<li><p><em><mark>Grava na tabela
<strong>RH_T_TIPREL_REM_PAG</strong></mark></em></p></li>
</ol>
<ul>
<li><p><em><mark>TIPREL_ID = ID DE ULTIMO VINCULO
RH_T_TIPOS_RELACIONAMENTO</mark></em></p></li>
<li><p><em><mark>REM_ID = ID DE
RH_T_TIPOS_RELACIONAMENTO</mark></em></p></li>
</ul></td>
<td><p><em>Registo ou update na tabela de validação
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT’ (</strong>DOMAINS =
VALIDACAO_TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘RENDIMENTO’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
RH_T_DEF_REMUNERACOES</em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID = ID de tabela
RH_T_TIPOS_RELACIONAMENTO</em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
<td colspan="2"><p><em>3. Registo de log
<strong>RH_T_LOG</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO <strong>= INSERT (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>TABELA_NAME <strong>=
‘RH_T_DEF_REMUNERACOES</strong>”</em></p></li>
<li><p><em>TABELA_ID <strong>= ID</strong> de tabela
<strong>RH_T_DEF_REMUNERACOES</strong></em></p></li>
<li><p><em>FUN_ID = ID de colaborador
(<strong>RH_T_FUNCIONARIOS.ID</strong>) , id de colaborador que será
substutido</em></p></li>
<li><p><em>TIPREL_ID = ID de tabela
RH_T_TIPOS_RELACIONAMENTO</em></p></li>
<li><p><em>USER_REGISTO_ID <strong>= ID utilizador que fez a
ação</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME <strong>= Nome do utilizador que fez a
ação</strong></em></p></li>
<li><p><em>DATA_REGISTO = SYSDATE</em></p></li>
</ul>
<p><em><strong>3.1</strong> Registo Detalhe de LOG na tabela
<strong>RH_T_VALIDACAO_DETALHE </strong></em></p>
<ul>
<li><p><em>LOG_ID = id de tabela <strong>RH_T_LOG</strong></em></p></li>
<li><p><em>CAMPO_ALTERADO <strong>=</strong> null</em></p></li>
<li><p><em>VALOR_ANTERIOR = null</em></p></li>
<li><p><em>VALOR_NOVO = null</em></p></li>
<li><p><em>DADOS_REGISTO = CLOB (dados do formulário)</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

2)  Pagamento

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 7%" />
<col style="width: 2%" />
<col style="width: 33%" />
<col style="width: 0%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Formulario</strong></p>
</blockquote></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="3"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><strong>Pagamento / Desconto</strong></td>
<td colspan="5"></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td colspan="3"><p>Esse deve ficar oculto, so fica visivel em caso de
validação desse Registo. No modo validação esse campo é obrigatorio</p>
<p>DOMAINS= SIM_NAO</p></td>
<td><em>RH_T_DEF_PAGAMENTOS.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Movimento</td>
<td><em>SELECT</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_PAGAMENTOS.</em> <em>TM_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor</td>
<td><em>NUMBER</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_PAGAMENTOS.VALOR</em></td>
</tr>
<tr>
<td style="text-align: left;">Percentagem</td>
<td><em>NUMBER</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_PAGAMENTOS.PERCENTAGEM</em></td>
</tr>
<tr>
<td style="text-align: left;">Moeda</td>
<td><em>SELECT</em></td>
<td colspan="3"><mark>Por defeito de trazer preenchido
<strong>CVE</strong></mark></td>
<td><em>RH_T_DEF_PAGAMENTOS.</em> MOEDA</td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio</td>
<td><em>DATE</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_PAGAMENTOS. DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_PAGAMENTOS. DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_PAGAMENTOS.</em>OBS</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Dados de Entidade</strong></td>
<td></td>
<td colspan="3"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Entidade</td>
<td><em>lookup</em></td>
<td colspan="3"></td>
<td><p><em>RH_T_DEF_PAGAMENTOS.</em> <em>NM_ENTIDADE</em></p>
<p><em>RH_T_DEF_PAGAMENTOS.</em> <em>ENT_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">NIF</td>
<td><em>NUMBER</em></td>
<td colspan="3">Preeenchido automatico aparitr de pesquisa entidade</td>
<td><em>RH_T_DEF_PAGAMENTOS.NIF</em></td>
</tr>
<tr>
<td style="text-align: left;">BANCO</td>
<td><em>SELECT</em></td>
<td colspan="3"><strong>RH_T_BANCO</strong></td>
<td><em>RH_T_DEF_PAGAMENTOS.</em> <em>RHB_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">NIB</td>
<td><em>TEXT</em></td>
<td colspan="3"></td>
<td><em>RH_T_DEF_PAGAMENTOS.</em> <em>NIB</em></td>
</tr>
<tr>
<td colspan="6"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="6"><ul>
<li><p><em>Caso a remuneração já tenha um processamento , logo somente
se pode altrar Data Fim e Observaçáo</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="6"><strong>OUTRAS GRAVAÇOES</strong></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>1,</strong> gravação na tabela de
<strong>RH_T_DEF_PAGAMENTOS</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>ESTADO = “<strong>P</strong>”</em></p></li>
<li><p><em>OBS = ‘NOVO_DESCONTO’’</em></p></li>
<li><p><em>FUN_ID = ID de RH_T_FUNCIONARIOS</em></p></li>
</ul>
<ol start="4" type="1">
<li><p><em><mark>Grava na tabela
<strong>RH_T_TIPREL_REM_PAG</strong></mark></em></p></li>
</ol>
<ul>
<li><p><em><mark>TIPREL_ID = ID DE ULTIMO VINCULO
RH_T_TIPOS_RELACIONAMENTO</mark></em></p></li>
<li><p><em><mark>PAG_ID = ID DE
RH_T_TIPOS_RELACIONAMENTO</mark></em></p></li>
</ul></td>
<td><p><em>Registo ou update na tabela de validação
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT’ (</strong>DOMAINS =
VALIDACAO_TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘DESCONTO” (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_DEF_PAGAMENTOS</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID = id de tabela
RH_T_TIPOS_RELACIONAMENTO</em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
<td colspan="2"><p><em>3. Registo de log
<strong>RH_T_LOG</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO <strong>= INSERT (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>TABELA_NAME <strong>=
‘RH_T_DEF_PAGAMENTOS</strong>”</em></p></li>
<li><p><em>TABELA _ID <strong>= ID</strong> de tabela
<strong>RH_T_DEF_PAGAMENTOS</strong></em></p></li>
<li><p><em>FUN_ID = ID de colaborador
(<strong>RH_T_FUNCIONARIOS.ID</strong>) , id de colaborador que será
substutido</em></p></li>
<li><p><em>TIPREL_ID = id de tabela
RH_T_TIPOS_RELACIONAMENTO</em></p></li>
<li><p><em>USER_REGISTO_ID <strong>= ID utilizador que fez a
ação</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME <strong>= Nome do utilizador que fez a
ação</strong></em></p></li>
<li><p><em>DATA_REGISTO = SYSDATE</em></p></li>
</ul>
<p><em><strong>3.1</strong> Registo Detalhe de LOG na tabela
<strong>RH_T_VALIDACAO_DETALHE </strong></em></p>
<ul>
<li><p><em>LOG_ID = id de tabela <strong>RH_T_LOG</strong></em></p></li>
<li><p><em>CAMPO_ALTERADO <strong>=</strong> null</em></p></li>
<li><p><em>VALOR_ANTERIOR = null</em></p></li>
<li><p><em>VALOR_NOVO = null</em></p></li>
<li><p><em>DADOS_REGISTO = CLOB (dados do formulário)</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

###### Validar

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><ul>
<li><p>A validação invoca a mesma página de Registo</p></li>
<li><p>O campo validar deve ficar visivel</p></li>
<li><p>Ao <strong>validar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'A'</strong>.</p></li>
<li><p>Ao <strong>desvalidar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

#### Processo Disciplinar

##### Lista Processo Disciplinar

<img src="media/image39.png" style="width:9.69306in;height:3.96944in"
alt="Uma imagem com texto, número, Tipo de letra, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 6%" />
<col style="width: 38%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Direção</td>
<td><em>TEXT</em></td>
<td></td>
<td><p><em>RH_V_MOBILIDADE.DIRECAO, passando como parametro</em></p>
<p><em>RH_T_PROCESSO_DISCIPLINAR.TIPREL_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;">Sessao</td>
<td><em>TEXT</em></td>
<td></td>
<td><p><em>RH_V_MOBILIDADE.SESSAO, passando como parametro</em></p>
<p><em>RH_T_PROCESSO_DISCIPLINAR.TIPREL_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;">Vinculo</td>
<td><em>TEXT</em></td>
<td></td>
<td><p><em>RH_V_MOBILIDADE.VINCULO, passando como parametro</em></p>
<p><em>RH_T_PROCESSO_DISCIPLINAR.TIPREL_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;">Processo Disciplinar</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.TP_PROCESSO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.DATE_INIC_PD</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.DATE_FIM_PD</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><em><strong>Editar</strong>:
Abre o mesmo formulario que Registo</em></td>
</tr>
</tbody>
</table>

##### Novo Processo Disciplinar 

<img src="media/image40.png" style="width:9.69306in;height:4.65in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Formulario</strong></p>
</blockquote></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"></td>
<td colspan="3"></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>RADIOLIST</em></td>
<td><p>Esse deve ficar oculto, so fica visivel em caso de validação
desse Registo. No modo validação esse campo é obrigatorio</p>
<p>DOMAINS= SIM_NAO</p></td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Vinculo Referente</td>
<td><em>SELCECT</em></td>
<td>Identifica o tipo de vínculo laboral do colaborador</td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.TIPREL_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Numero Processo</td>
<td><em>SELECT</em></td>
<td>Código ou número sequencial único atribuído ao processo disciplinar
para efeitos de registo e controlo</td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.NUM_PROCESSO</em></td>
</tr>
<tr>
<td style="text-align: left;">Entidade</td>
<td><em>TEXT</em></td>
<td>Instituição, departamento ou unidade orgânica responsável pela
abertura e tramitação do processo disciplinar.</td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.ENTIDADE</em></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Processo</td>
<td><em>SELECT</em></td>
<td><p>Classificação do processo</p>
<p><strong>DOMAINS</strong> = TP_PROCESSO_DISCP</p></td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.TP_PROCESSO</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado Do Processo</td>
<td><em>SEELCT</em></td>
<td><p>Situação atual do processo disciplinar</p>
<p><strong>DOMAINS</strong> = STATUS_PROCESSO_DISCP</p></td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Pena Disciplinar</td>
<td><em>SELECT</em></td>
<td><p>Tipo de sanção aplicada ao colaborador, se aplicável</p>
<p><strong>DOMAINS</strong> = PENA_DISCIP</p></td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.PENA_DISCP</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Data PD / Pena</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio PD</td>
<td><em>DATE</em></td>
<td>Data oficial de abertura ou instauração do processo
disciplinar.</td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.DATE_INIC_PD</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim PD</td>
<td><em>DATE</em></td>
<td>Data de encerramento formal do processo disciplinar.</td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.DATE_FIM_PD</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio Pena</td>
<td><em>DATE</em></td>
<td>Data em que a pena disciplinar começa a ter efeito.</td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.DATE_INIC_PENA</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim Pena</td>
<td><em>DATE</em></td>
<td>ata em que a pena disciplinar termina ou deixa de produzir
efeito.</td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.DATE_FIM_PENA</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>BO / ordem Serviços</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><del>Nº BO</del></td>
<td><em><del>TEXT</del></em></td>
<td><del>Número do Boletim Oficial onde foi publicada a
decisão.</del></td>
<td><em><del>RH_T_PROCESSO_DISCIPLINAR.NUM_BO</del></em></td>
</tr>
<tr>
<td style="text-align: left;"><del>Data Publicação BO</del></td>
<td><em><del>DATE</del></em></td>
<td><del>Data de publicação da decisão no Boletim Oficial.</del></td>
<td><em><del>RH_T_PROCESSO_DISCIPLINAR.DATA_PUBL_BO</del></em></td>
</tr>
<tr>
<td style="text-align: left;"><del>Nº Ordem Serviço</del></td>
<td><em><del>TEXT</del></em></td>
<td><del>Número da Ordem de Serviço associada ao processo
disciplinar.</del></td>
<td><em><del>RH_T_PROCESSO_DISCIPLINAR.NUM_ORDEM_SERV</del></em></td>
</tr>
<tr>
<td style="text-align: left;">Data Ordem Serviço</td>
<td><em>DATE</em></td>
<td>Data de emissão da Ordem de Serviço.</td>
<td><em>RH_T_PROCESSO_DISCIPLINAR.DATA_ORDEM_SERV</em></td>
</tr>
<tr>
<td colspan="4"><strong>OFA :</strong> Documento ou Ordem Formal
Administrativa emitida pela entidade competente no âmbito do
processo<strong>.</strong></td>
</tr>
<tr>
<td style="text-align: left;"><del>Nº Ofa</del></td>
<td><em><del>TEXT</del></em></td>
<td><del>Número identificador da Ordem Formal Administrativa.</del></td>
<td><em><del>RH_T_PROCESSO_DISCIPLINAR.NUM_OFA</del></em></td>
</tr>
<tr>
<td style="text-align: left;"><del>Data Emissão Ofa</del></td>
<td><em><del>DATE</del></em></td>
<td><del>Data em que a Ordem Formal Administrativa foi
emitida.</del></td>
<td><em><del>RH_T_PROCESSO_DISCIPLINAR.DATA_EMISS_OFA</del></em></td>
</tr>
<tr>
<td colspan="4"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4"></td>
</tr>
<tr>
<td colspan="4"><strong>OUTRAS GRAVAÇOES</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><p><em>Gravações de outros
dados na tabela RH_T_PROCESSO_DISCIPLINAR</em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
</ul>
<ul>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
</ul>
<ul>
<li><p><em>ESTADO = “<strong>P</strong>”</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

###### Validar 

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><ul>
<li><p>A validação invoca a mesma página de Registo</p></li>
<li><p>O campo validar deve ficar visível</p></li>
<li><p>Ao <strong>validar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'A'</strong>.</p></li>
<li><p>Ao <strong>Desvalidar,</strong> devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

#### Documento

##### Recibo 

| **Filtro** | **Tipo** | **Descrição** | **Fonte dados** |
|:--:|----|----|----|
| Data Inicio | *DATE* |  | *<span class="mark">RH_T_PROC_FUNCIONARIOS.DATA_REFERENCIA_DE</span>* |
| Data Fim | *DATE* |  | *<span class="mark">RH_T_PROC_FUNCIONARIOS.DATA_REFERENCIA_DE</span>* |
| **Lista** | **Tipo** | **Descrição** |  |
| Recibo | *HYPERLINK* | Abre o recibo de salario relatorio “**Recibo de Pagamento de Salário para o Funcionario**”, descrito no documento de processamento salarial |  |

##### Declarações (**NOTA**: ESPECIFICADO NO DOCUMENTO TRANSVERSAL)

Modulo que Permite o Colaborador solicitar uma declaração Mediante um
pedido.

Qualquer declaração deve ser feito mediante um pedido, após a declaração
, o sistema deve permitir que o imprima a declaração, a declaração
emitida deve ser permitir assinatura digital..

###### Pedido

Formulario de pedido de declaracao

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 11%" />
<col style="width: 37%" />
<col style="width: 32%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Formulario</strong></p>
</blockquote></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Nome Requerente</td>
<td><em>SELECT</em></td>
<td>PESQUISA DO COLABORADOR</td>
<td><p><em>RH_T_PEDIDO.FUN_ID</em></p>
<p><em>RH_T_DECLARACAO.FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Declaracao</td>
<td></td>
<td>DOMINIO = TIPO_PEDIDO</td>
<td><p><em>RH_T_PEDIDO.TIPO_PEDIDO=
‘<strong>DECLARACAO</strong>’</em></p>
<p><em>RH_T_DECLARACAO.TIPO_DECLARACAO</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Informações do
Pedido</strong></td>
<td><em>Separador Lista</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Finalidade</td>
<td></td>
<td></td>
<td><em>RH_T_DECLARACAO.FINALIDADE</em></td>
</tr>
<tr>
<td style="text-align: left;">Entidade destinatária</td>
<td></td>
<td></td>
<td><em>RH_T_DECLARACAO.ENTIDADE_DESTINADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data referente</td>
<td></td>
<td></td>
<td><em>RH_T_DECLARACAO.DATA_REFERENTE</em></td>
</tr>
<tr>
<td style="text-align: left;">Analise Pedido</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Em conformidade</td>
<td></td>
<td>DOMAIN = SIM_NAO</td>
<td><p><em>RH_T_PEDIDO.ESTADO = ‘<strong>P</strong>’</em></p>
<p><em>RH_T_PEDIDO.ETAPA = ‘Analise’</em></p>
<p><em>RH_T_DECLARACAO.DECISAO_ANALISE</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td></td>
<td></td>
<td><em>RH_T_DECLARACAO.OBS_ANALISE</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Validação Pedido</strong></td>
<td colspan="3"><em>Aparece somente na Etapa Validação</em></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td><em>Radio List</em></td>
<td><p>Isso aparece somente na etapa Validacao</p>
<p>DOMAIN = SIM_NAO</p></td>
<td><p><em>RH_T_PEDIDO.ESTADO = ‘<strong>P</strong>’</em></p>
<p><em>RH_T_PEDIDO.ETAPA = ‘<strong>Validação RH</strong>’</em></p>
<p><em>RH_T_DECLARACAO.</em>DECISAO_RH</p></td>
</tr>
<tr>
<td style="text-align: left;">Entrega</td>
<td><em>RADIOLIST</em></td>
<td>DOMAIN = SIM_NAO</td>
<td><em>RH_T_.ETAPA =’ENTREGA’</em></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td><em>RADIOLIST</em></td>
<td>DOMAIN = SIM_NAO</td>
<td><em>Ccas</em></td>
</tr>
<tr>
<td style="text-align: left;">ANEXO</td>
<td colspan="3">se necessário comprovativos</td>
</tr>
<tr>
<td style="text-align: left;">Tipo Documento</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Documento</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="4"><strong>ACÇOES</strong></td>
</tr>
<tr>
<td colspan="4"><p><em>GRAVACAO nas Seguintes TABELAS :</em></p>
<ul>
<li><p><em><strong>RH_T_PEDIDO</strong></em></p>
<ul>
<li><p><em>DATA_PEDIDO</em></p></li>
<li><p><em>DATA_REGISTO</em></p></li>
<li><p><em>USER_REGISTO</em></p></li>
</ul></li>
<li><p><em><strong>RH_T_DECLARACAO</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO</em></p></li>
<li><p><em>USER_REGISTO</em></p></li>
</ul></li>
<li><p><em><strong>RH_T_NOTIFICACAO</strong><br />
</em></p></li>
</ul>
<p>Parte inferior do formulário</p></td>
</tr>
</tbody>
</table>

###### Lista (**NOTA**: ESPECIFICADO NO DOCUMENTO TRANSVERSAL)

Lista de pedido de declaracao

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 36%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Nome Requerente</td>
<td style="text-align: center;"></td>
<td>RH_T_FUNCIONARIOS.NOME</td>
<td>RH_T_DECLARACAO.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;">Data Pedido DE</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td>RH_T_DECLARACAO.DATA</td>
</tr>
<tr>
<td style="text-align: center;">Data Pedo Até</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: left;">Nome Requerente</td>
<td><em>TEXT</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Tipo Declaracao</td>
<td><em>TEXT</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Finalidade</td>
<td><em>TEXT</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Data necessária (prazo )</td>
<td><em>TEXT</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td><em>TEXT</em></td>
<td><strong>DOMAIN</strong> = ESTADO_PEDIDO</td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Etapa</strong></td>
<td></td>
<td><strong>DOMAIN</strong> = ETAPA_PEDIDO, onde referencia =
DECLARACAO</td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><strong>ACCÕES</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Ver Detalhe</td>
<td colspan="3"><em>PERMITE VALIDAR E ENTREGAR O DOCUMENTO</em></td>
</tr>
<tr>
<td style="text-align: center;">Ver Notificação</td>
<td colspan="3"><em>VER TODAS NOTIFICAÇÕES ASSOCIADA AO PEDIDO</em></td>
</tr>
</tbody>
</table>

##### Ordem de Serviço (**NOTA**: ESPECIFICADO NO DOCUMENTO TRANSVERSAL)

<img src="media/image41.png" style="width:9.69306in;height:3.34306in"
alt="Uma imagem com texto, número, file, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 6%" />
<col style="width: 38%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Descricao</td>
<td></td>
<td></td>
<td><em>RH_T_ORDEM_SERVICO.DESCRICAO</em></td>
</tr>
<tr>
<td style="text-align: center;">Referente</td>
<td></td>
<td><em><strong>DOMAINS:</strong> ACCAO_REFERENTE</em></td>
<td><em>RH_T_ORDEM_SERVICO.REFERENTE</em></td>
</tr>
<tr>
<td style="text-align: center;">Numero</td>
<td></td>
<td></td>
<td><em>RH_T_ORDEM_SERVICO.NUMERO</em></td>
</tr>
<tr>
<td style="text-align: center;">Anexar Ordem Serviço</td>
<td></td>
<td></td>
<td><p><em>RH_T_DOCUMENTO.DOC_ID</em></p>
<p><em>RH_T_DOCUMENTO.</em></p></td>
</tr>
<tr>
<td style="text-align: center;">Ver ordem Serviço</td>
<td></td>
<td></td>
<td><em>RH_T_ORDEM_SERVICO.DOC_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">OUTRAS GRAVAÇÕES</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ol type="1">
<li><p><em>Grava na tabela RH_T_DOCUMENTO</em></p></li>
<li><p><em>Grava na tabela RH_T_ORDEM_SERVICO</em></p></li>
</ol></td>
</tr>
</tbody>
</table>

### Alerta e Notificação (NOTA: ESPECIFICADO NO DOCUMENTO TRANSVERSAL)

Deve ser criado job para notificaçao e alerta e notificação.

Para ver o prazo de notificacao de cada tipo .

#### JOB (NOTA: ESPECIFICADO NO DOCUMENTO TRANSVERSAL)

<table>
<colgroup>
<col style="width: 23%" />
<col style="width: 38%" />
<col style="width: 38%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>JOB</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td rowspan="2" style="text-align: center;"><ol type="1">
<li><p>Alerta prazo Renovação</p></li>
</ol></td>
<td colspan="2"><p>O sistema deve alertar RH 45 dias antes de data de
renovacao do contrato</p>
<p><strong>DOMAINS</strong> =<strong>’PRAZO’ , DESCRICAO = ‘Renovacao
Contrato’</strong></p>
<p>Este <em>job</em> deve automaticamente:</p>
<ol type="1">
<li><p>Criar um registo na tabela <strong>RH_T_ALERTA</strong></p></li>
<li><p>Criar um registo correspondente na tabela
<strong>RH_T_NOTIFICACAO</strong></p></li>
<li><p>Associar o alerta ao tipo RENOVAÇÃO DE CONTRATO</p></li>
</ol>
<p>O RH terá acesso a uma lista de alertas especificamente relacionada à
renovação de contratos.<br />
A partir desta lista, o RH poderá:</p>
<ul>
<li><p>Selecionar um grupo de colaboradores com contratos prestes a
expirar</p></li>
<li><p>Abrir um formulário que permite:</p>
<ul>
<li><p>Rever e confirmar a renovação automática, ou</p></li>
<li><p>Editar/ajustar manualmente o novo prazo do contrato (ex.: mensal
→ anual)</p></li>
</ul></li>
</ul>
<p>Após confirmação pelo RH, os registos selecionados devem ser enviados
para validação (<strong>RH_T_VALIDACAO</strong>).</p>
<p>Quando o contrato é renovado, não altera a carreira</p></td>
</tr>
<tr>
<td><p>1.1-Registo na tabela RH_T_ALERTA</p>
<ul>
<li><p><em>REFERENCIA = ‘RENOVACAO´</em></p></li>
<li><p><em>DESCRICAO = ´Renovacao contrato´</em></p></li>
<li><p>ESTADO = ´P´</p></li>
<li><p><em>DATA_REGISTO = <strong>SYSDATE</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = <strong>utilizador
logado</strong></em></p></li>
<li><p><em>USER_ALTERACAO_ID =</em></p></li>
</ul>
<p>1.2 <em>RH_T_ALERTA_DETALHE.</em></p>
<ul>
<li><p><em>TIREPL_ID</em></p></li>
<li><p><em>ALERTA_ID</em></p></li>
<li><p><em>DATA_INICIO = DATA_INICIO DE
RH_T_CONTRATO_VINCULO</em></p></li>
<li><p><em>DATA FIIM = DATA_FIM de RH_T_CONTRATO_VINCULO</em></p></li>
</ul></td>
<td><p>1.2 Registo na tabela RH_T_NOTIFICACAO</p>
<ul>
<li><p><em>REFERENCIA = ‘RENOVACAO’</em></p></li>
<li><p><em>ASSUNTO =</em></p></li>
<li><p><em>MESSAGE =</em></p></li>
<li><p><em>ASSUNTO</em></p></li>
<li><p><em>EMAIL =</em></p></li>
</ul>
<p><em>1.4 RH_T_NOTIF_ALERTA</em></p>
<ul>
<li><p><em>ALERTA_ID</em></p></li>
<li><p><em>NOTIFI_ID</em></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><ol start="2" type="1">
<li><p>Alerta data Fim de licença sem vencimento</p></li>
</ol></td>
<td colspan="2"><p>(ESPECIFICAÇÃO em outro documento)</p>
<p><strong>DOMAINS</strong> =<strong>’PRAZO’ , DESCRICAO = ‘LICENÇA /S
VENCIMENTO’</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><ol start="3" type="1">
<li><p>Alerta de progressão e Promoção</p></li>
</ol></td>
<td colspan="2"><p>ESPECIFICAÇÃO em outro documento)</p>
<p><strong>DOMAINS</strong> =<strong>’PRAZO’ , DESCRICAO =
‘PROGRESSÃO’</strong></p></td>
</tr>
</tbody>
</table>

#### Lista alerta notificação (NOTA: ESPECIFICADO NO DOCUMENTO TRANSVERSAL)

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 6%" />
<col style="width: 38%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Referente</td>
<td><em>TEXT</em></td>
<td><em><strong>DOMAINS:</strong></em> TIPO_MOV_LABORAL</td>
<td><em>RH_T_ALERTA.REFERENTE</em></td>
</tr>
<tr>
<td style="text-align: left;">descricao</td>
<td></td>
<td></td>
<td><em>RH_T_ALERTA.DESCRICAO</em></td>
</tr>
<tr>
<td style="text-align: center;">Nome de colaborador / Quantidade</td>
<td></td>
<td><em>RH_T_FUNCIONARIO.NOME</em></td>
<td><em>RH_T_ALERTA_DETALHE.TIPREL_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Periodo</td>
<td></td>
<td></td>
<td><em>RH_T_ALERTA_DETALHE.DATA_INICIO ||
RH_T_ALERTA_DETALHE.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td></td>
<td><strong>DOMINIO</strong>=STATUS</td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Selecionar todos</td>
<td><em>check</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><strong>REGRAS</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><strong>AÇOES</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Renovar Contrato</td>
<td colspan="3"></td>
</tr>
<tr>
<td style="text-align: center;">Ver Notificação</td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

##### Validação

|     |
|-----|

### Missões de Serviço

(Pendente – Especificação em outro Documento )

### Emprestimo 

(Pendente – Especificação em outro Documento )

### Controlo Assiduidade

#### Ferias 

#### Falta

### Avaliação Desempenho 

(Pendente – Especificação em outro Documento )

# Tabelas associdas 

<img src="media/image42.png" style="width:9.66806in;height:5.02431in" />

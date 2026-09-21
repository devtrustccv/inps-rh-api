<figure>
<img src="media/image1.jpeg" style="width:14.65694in;height:9.77083in"
alt="C:\Users\joelm\Desktop\Imagens\sergey-zolkin-_UeY8aTI6d0-unsplash (2).jpg" />
<figcaption><p>SIPS-RH</p></figcaption>
</figure>

**AVALAIAÇÃO DESEMPENHO**

# Enquadramento 

Este documento tem como finalidade definir, de forma clara e
operacional, os requisitos e regras do sistema de Avaliação de
Desempenho. Descreve os tipos de objetivos, as etapas do processo
(autoavaliação, avaliação, entrevista e parecer), as funcionalidades
necessárias e os formulários que suportam todo o ciclo avaliativo. O
objetivo é garantir um modelo simples, padronizado e alinhado com as
práticas da instituição.

# Âmbito 

<img src="media/image4.png" style="width:6.36806in;height:3.45972in"
alt="Uma imagem com texto, captura de ecrã, Tipo de letra, file Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 22%" />
<col style="width: 28%" />
<col style="width: 49%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Funcionalidade</strong></th>
<th style="text-align: center;"><strong>Sub -
Funcionalidade</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td rowspan="2" style="text-align: center;">Parametrização</td>
<td><p>Identificação dos componentes de avaliação</p>
<ul>
<li><p>Aqui se define os obectivos e as ponderações</p></li>
</ul></td>
<td><p>Cada ano, cada direcçao deve identificar os objectivos do seu
departamento.</p>
<p><strong>Objectivos:</strong> Existem objectivos comum ao INPS,
objectivos que são por direção e objectivos que são por invidual (sao
definidos no manual de função)</p></td>
</tr>
<tr>
<td>Registo de escala de avaliação</td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Definição Dos Objectivos</td>
<td><ul>
<li><p><strong>OJECTIVOS</strong></p></li>
</ul>
<ul>
<li><p><strong>(</strong>comuns ao inps, Comum à Unidade Orgânica,
individual)</p></li>
</ul>
<ul>
<li><p><strong>COMPETÊNCIAS - COMPORTAMENTAIS</strong></p></li>
</ul>
<ul>
<li><p>individual</p></li>
</ul>
<ul>
<li><p><strong>COMPETÊNCIAS - TÉCNICAS</strong></p></li>
</ul>
<ul>
<li><p>individual</p></li>
</ul>
<ul>
<li><p><strong>ATITUDE PESSOAL</strong></p></li>
</ul>
<ul>
<li><p>individual</p></li>
</ul></td>
<td style="text-align: left;"><p>Caso os objetivos sejam aplicados a
vários colaboradores, o sistema deve permitir selecionar múltiplos
objetivos que poderão ser associados a vários colaboradores.</p>
<p><strong>Objetivos Comuns ao INPS:</strong> aplicam-se a todos os
colaboradores do INPS.</p>
<p><strong>Competências Comportamentais e Técnicas:</strong> são
definidas por direção, podendo ser atribuídas a todos os colaboradores
dessa direção com a mesma ponderação. Devem estar alinhadas com o Manual
de Funções. O sistema deve permitir aplicar as competências a todos os
colaboradores ou selecionar apenas alguns, garantindo flexibilidade.
Cada direção define o seu conjunto genérico de competências, em
conformidade com o descrito no Manual de Funções.</p>
<p><strong>Atitude Pessoal:</strong> normalmente é comum a todos os
colaboradores, ou seja, todos possuem a mesma descrição e a mesma
ponderação.</p></td>
</tr>
<tr>
<td style="text-align: center;">Lista avaliação</td>
<td></td>
<td>Permite ver e filtrar todos colaboradores avaliados</td>
</tr>
<tr>
<td style="text-align: center;">Autoavalição</td>
<td></td>
<td>Permite cada colaborador efetua a sua propia avaliação</td>
</tr>
<tr>
<td style="text-align: center;">Proceso de Avaliação</td>
<td></td>
<td>Permite cada responsavel directo, realiza a avaliação ao seu
colaborador</td>
</tr>
</tbody>
</table>

# Especificação 

## Parametrização

### Registar Componentes de Avaliação

<img src="media/image5.png" style="width:9.69306in;height:4.39861in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 5%" />
<col style="width: 36%" />
<col style="width: 2%" />
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
<td style="text-align: left;">Ano</td>
<td>MULTISELECT</td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO_DET.ANO</td>
</tr>
<tr>
<td style="text-align: left;"><mark>Periodicidade</mark></td>
<td></td>
<td colspan="2"><p><mark>Para indicar a periodicidade é feito a
definição de Objectivo</mark></p>
<p><mark><strong>DOMAIN</strong> = PERIODICIDADE onde a REFERENCIA =
PERIODICIDADE</mark></p></td>
<td><mark>RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE</mark></td>
</tr>
<tr>
<td style="text-align: left;">Peso Competência Comportamentais</td>
<td>NUMBER</td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO_DET.PESO_COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: left;">Peso Competência Técnica</td>
<td>NUMBER</td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO_DET.PESO_TECNICA</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">AVALIAÇÃO GLOBAL DE
DESEMPENHO</td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO_DET.PONDERACAO_OBJETIVO</td>
</tr>
<tr>
<td style="text-align: left;">Competências</td>
<td></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO_DET.PONDERACAO_COMPETENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Atitude Pessoal</td>
<td></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO_DET.PONDERACAO_ATITUDE_PESS</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">OBJECTIVOS</td>
</tr>
<tr>
<td style="text-align: left;">Aplicar a Todos</td>
<td><em>RADIO</em></td>
<td colspan="2">Caso for selecionado deve ser registado cada uma das
funções (ou seja será aplicado a todos os cargos)</td>
<td>RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td><em>MULTISELECT</em></td>
<td colspan="2"><p>Fica visivel, caso não for selecionado aplicar a
todos</p>
<p>RH_T_PARAM_CARGO.ID</p></td>
<td>RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td></td>
<td colspan="2">RH_T_PARAM_CARREIRA.ID</td>
<td>RH_T_PARAM_OBJETIVO.CARR_PCCCS_ID</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td><em>NUMBER</em></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td><em>SELECT</em></td>
<td colspan="2">DOMAINS =<strong>ABRANGENCIA_AVD</strong></td>
<td>RH_T_PARAM_OBJETIVO.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td><em>SELECT</em></td>
<td colspan="2">Somente fica visivel caso o tipo de abragencia
selecionado for DIRECAO</td>
<td>RH_T_PARAM_OBJETIVO.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: left;">Objectivo</td>
<td><em>TEXT</em></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO.KPI</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO.PONDERACAO</td>
</tr>
<tr>
<td style="text-align: left;">----------------------------</td>
<td><em>HIDDEN</em></td>
<td
colspan="2">-------------------------------------------------------------</td>
<td>RH_T_PARAM_OBJETIVO.COMPONENTE = ‘<strong>OBJECTIVO’</strong></td>
</tr>
<tr>
<td style="text-align: center;">----------------------</td>
<td>HIDDEN</td>
<td
colspan="2">------------------------------------------------------------------------</td>
<td>RH_T_PARAM_OBJETIVO.VERSAO = VERSAO+1</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">COMPETÊNCIAS -
COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: center;">Aplicar a Todos</td>
<td>CHECK</td>
<td colspan="2">Caso for selecionado deve ser registado cada uma das
funções</td>
<td>RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td></td>
<td colspan="2"><p>Fica caso não for selecionado aplicar a todos</p>
<p>RH_T_PARAM_CARGO.ID</p></td>
<td>RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td></td>
<td colspan="2">RH_T_PARAM_CARREIRA.ID</td>
<td>RH_T_PARAM_OBJETIVO.CARR_PCCCS_ID</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td><em>NUMBER</em></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO. PONDERACAO</td>
</tr>
<tr>
<td style="text-align: center;">-----------------------------</td>
<td>HIDDEN</td>
<td
colspan="2">------------------------------------------------------------</td>
<td>RH_T_PARAM_OBJETIVO.COMPONENTE =
‘<strong>COMPETENCIAS_COMPORTAMENTAIS’</strong></td>
</tr>
<tr>
<td style="text-align: center;">-----------------------</td>
<td>HIDDEN</td>
<td
colspan="2">----------------------------------------------------------------</td>
<td>RH_T_PARAM_OBJETIVO.ABRAGENCIA = INDIVIDUAL</td>
</tr>
<tr>
<td style="text-align: center;">----------------------</td>
<td>HIDDEN</td>
<td
colspan="2">------------------------------------------------------------------------</td>
<td>RH_T_PARAM_OBJETIVO.VERSAO = VERSAO+1</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">COMPETÊNCIAS - TÉCNICAS</td>
</tr>
<tr>
<td style="text-align: center;">Aplicar a Todos</td>
<td>CHECK</td>
<td colspan="2">Caso for selecionado deve ser registado cada uma das
funções (ou seja será aplicado a todos os cargos)</td>
<td>RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td>SELECT</td>
<td colspan="2"><p>Fica visivel, caso não for selecionado aplicar a
todos</p>
<p>RH_T_PARAM_CARGO.ID</p></td>
<td>RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td>SELECT</td>
<td colspan="2">RH_T_PARAM_CARREIRA.ID</td>
<td>RH_T_PARAM_OBJETIVO.CARR_PCCCS_ID</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td><em>NUMBER</em></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO. PONDERACAO</td>
</tr>
<tr>
<td style="text-align: center;">-----------------------------</td>
<td>HIDDEN</td>
<td
colspan="2">------------------------------------------------------------</td>
<td>RH_T_PARAM_OBJETIVO.COMPONENTE =
‘<strong>COMPETENCIAS_TECNICA’</strong></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO.ABRAGENCIA = INDIVIDUAL</td>
</tr>
<tr>
<td style="text-align: center;">----------------------</td>
<td>HIDDEN</td>
<td
colspan="2">------------------------------------------------------------------------</td>
<td>RH_T_PARAM_OBJETIVO.VERSAO = VERSAO+1</td>
</tr>
<tr>
<td style="text-align: center;">ATITUDE PESSOAL</td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Aplicar a Todos</td>
<td>CHECK</td>
<td colspan="2">Caso for selecionado deve ser registado cada uma das
funções (ou seja será aplicado a todos os cargos)</td>
<td>RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">cargo</td>
<td>SELECT</td>
<td colspan="2"><p>Fica visivel, caso não for selecionado aplicar a
todos</p>
<p>RH_T_PARAM_CARGO.ID</p></td>
<td>RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td>SELECT</td>
<td colspan="2">RH_T_PARAM_CARREIRA.ID</td>
<td>RH_T_PARAM_OBJETIVO.CARR_PCCCS_ID</td>
</tr>
<tr>
<td style="text-align: center;"><del>Abrangência</del></td>
<td></td>
<td colspan="2"></td>
<td>RH_T_PARAM_OBJETIVO.ABRAGENCIA = INDIVIDUAL</td>
</tr>
<tr>
<td style="text-align: center;">-----------------------</td>
<td>HIDDEN</td>
<td
colspan="2">-------------------------------------------------------</td>
<td>RH_T_PARAM_OBJETIVO.COMPONENTE =
‘<strong>ATITUDE_PESSOAL’</strong></td>
</tr>
<tr>
<td style="text-align: center;">----------------------</td>
<td>HIDDEN</td>
<td
colspan="2">------------------------------------------------------------------------</td>
<td>RH_T_PARAM_OBJETIVO.VERSAO = VERSAO+1</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="2"><ol type="1">
<li><p>Regista na <strong>RH_T_PARAM_OBJETIVO_DET</strong></p></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
</ul></td>
<td colspan="2"><ol start="2" type="1">
<li><p>Regista na tabela <strong>RH_T_PARAM_OBJETIVO</strong></p></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>PARAM_OBJ_DET_ID = id de</em>
<strong>RH_T_PARAM_OBJETIVO_DET</strong></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Lista components de Avaliação

<img src="media/image7.png" style="width:9.58in;height:2.33769in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 36%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte Dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Ano</td>
<td></td>
<td></td>
<td><strong>RH_T_PARAM_OBJETIVO_DET</strong>.ANO</td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td></td>
<td></td>
<td><strong>RH_T_PARAM_OBJETIVO_DET.</strong>ESTADO</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">Lista</td>
</tr>
<tr>
<td style="text-align: left;">Ano</td>
<td></td>
<td></td>
<td><strong>RH_T_PARAM_OBJETIVO_DET</strong>.ANO</td>
</tr>
<tr>
<td style="text-align: left;">Periodicidade</td>
<td></td>
<td></td>
<td><strong>RH_T_PARAM_OBJETIVO_DET.</strong> PERIODICIDADE</td>
</tr>
<tr>
<td style="text-align: left;">Peso Comportamentais</td>
<td></td>
<td></td>
<td><strong>RH_T_PARAM_OBJETIVO_DET.</strong>.PESO_COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: left;">Peso Técnico</td>
<td></td>
<td></td>
<td><strong>RH_T_PARAM_OBJETIVO_DET.</strong> PESO_TECNICA</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação Ojectivo</td>
<td></td>
<td></td>
<td><strong>RH_T_PARAM_OBJETIVO_DET.</strong> PONDERACAO_OBJETIVO</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação competência</td>
<td></td>
<td></td>
<td><strong>RH_T_PARAM_OBJETIVO_DET.</strong>
PONDERACAO_COMPETENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Ponderacão Atitude Pessoal</td>
<td></td>
<td></td>
<td><strong>RH_T_PARAM_OBJETIVO_DET.</strong>PONDERACAO_ATITUDE_PESS</td>
</tr>
<tr>
<td style="text-align: left;">Acoes</td>
<td colspan="3"></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image8.png"
style="width:0.48in;height:0.22481in" /></td>
<td colspan="3">Permite criar novos components de avaliação</td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image9.png"
style="width:0.18667in;height:0.20364in" /></td>
<td colspan="3">Permite editar components de Avaliação</td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image10.png"
style="width:0.21181in;height:0.20333in" /></td>
<td colspan="3">Permite clonar um registo, ou seja faz um novo registo
com dados do atual. Esse botão deve ficar independente se esse registo
esta ativo ou não</td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image11.png"
style="width:0.14758in;height:0.16042in" /></td>
<td colspan="3">Permite inativar o registo do botão. Somente aparece
caso ainda nao for defenido nenhum objectivo no ano na tabela
<strong>RH_T_AVD</strong></td>
</tr>
</tbody>
</table>

### Registar Escala

<img src="media/image12.png" style="width:9.68403in;height:3.39306in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 36%" />
<col style="width: 40%" />
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
<td colspan="4" style="text-align: center;">ESCALA DE AVALIACAO DE
OBJECTIVOS</td>
</tr>
<tr>
<td style="text-align: left;">Nivies de Avaliação</td>
<td>NUMBER</td>
<td>DOMAINS = <strong>NIVEIS_AVD</strong></td>
<td>RH_T_PARAM_ESCALA.NIVEL</td>
</tr>
<tr>
<td style="text-align: left;">Avaliacao Qualitativa</td>
<td>TEXTAREA</td>
<td>DOMAINS = <strong>CLASSIFICACAO_QUALIT_AVD</strong></td>
<td>RH_T_PARAM_ESCALA.QUALITATIVA</td>
</tr>
<tr>
<td style="text-align: left;">Descricao</td>
<td>TEXT</td>
<td></td>
<td>RH_T_PARAM_ESCALA.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Avaliação quantitativa de</td>
<td>NUMBER</td>
<td></td>
<td>RH_T_PARAM_ESCALA.QUANTITATIVA_DE</td>
</tr>
<tr>
<td style="text-align: left;">Avaliação quantitativa Até</td>
<td>NUMBER</td>
<td>-------------------------------------------------------------</td>
<td>RH_T_PARAM_ESCALA.QUANTITATIVA_ATE</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p>Gravação na tabela RH_T_PARAM_ESCALA, campos de
formulario e outros campos :</p>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Manual das Funções

#### Registo

Importar o manual de funções

| **Formulario** | **Tipo** | **Descrição** | **Gravação** |
|:--:|----|----|----|
| Direção |  | INS | RH_T_PARAM_MANUAL_FUNC.INSTIT_ID |
| Unidade Organica |  | RH_T_SECCAO | RH_T_PARAM_MANUAL_FUNC.SECCAO_ID |
| Cargo | SELECT | RH_T_PARAM_CARGO | RH_T_PARAM_MANUAL_FUNC.CARGO_ID |
| Carreira |  | RH_T_PARAM_CARREIRA | RH_T_PARAM_MANUAL_FUNC.CARREIRA_ID |
| Conteúdo |  |  | RH_T_PARAM_MANUAL_FUNC.DESCRICAO |
| Estado |  |  | RH_T_PARAM_MANUAL_FUNC.estado |

#### Lista

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 36%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Direção</td>
<td></td>
<td>INS</td>
<td>RH_T_PARAM_MANUAL_FUNC.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: left;">Unidade Organica</td>
<td></td>
<td>RH_T_SECCAO</td>
<td>RH_T_PARAM_MANUAL_FUNC.SECCAO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td>SELECT</td>
<td>RH_T_PARAM_CARGO</td>
<td>RH_T_PARAM_MANUAL_FUNC.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td></td>
<td>RH_T_PARAM_CARREIRA</td>
<td>RH_T_PARAM_MANUAL_FUNC.CARREIRA_ID</td>
</tr>
<tr>
<td style="text-align: left;">Conteúdo</td>
<td></td>
<td></td>
<td>RH_T_PARAM_MANUAL_FUNC.DESCRICAO</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Lista</strong></td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td>text</td>
<td></td>
<td>RH_T_PARAM_MANUAL_FUNC.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: left;">Unidade Organica</td>
<td>text</td>
<td></td>
<td>RH_T_PARAM_MANUAL_FUNC.SECCAO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td>text</td>
<td></td>
<td>RH_T_PARAM_MANUAL_FUNC.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td>text</td>
<td></td>
<td>RH_T_PARAM_MANUAL_FUNC.CARREIRA_ID</td>
</tr>
<tr>
<td style="text-align: left;">Conteúdo</td>
<td>text</td>
<td></td>
<td>RH_T_PARAM_MANUAL_FUNC.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td>text</td>
<td></td>
<td>RH_T_PARAM_MANUAL_FUNC.estado</td>
</tr>
</tbody>
</table>

## Definição Dos Objectivos

### Lista Definicao Ojectivos 

<img src="media/image13.png" style="width:9.69306in;height:3.24167in" />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
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
<td style="text-align: center;">Ano</td>
<td></td>
<td></td>
<td>RH_T_AVD.ANO</td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td></td>
<td></td>
<td>RH_T_AVD.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td></td>
<td></td>
<td>RH_T_AVD.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Unidade</td>
<td></td>
<td></td>
<td>RH_T_AVD.SECCAO</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td></td>
<td></td>
<td>RH_T_AVD.CARR_PCCS_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td></td>
<td></td>
<td>RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;"><del>Semestre</del></td>
<td></td>
<td></td>
<td><del>RH_T_AVD.SEMESTRE</del></td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td></td>
<td></td>
<td>RH_T_AVD.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Ano</td>
<td></td>
<td></td>
<td>RH_T_AVD.ANO</td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td></td>
<td></td>
<td>RH_T_AVD.INSITID_ID</td>
</tr>
<tr>
<td style="text-align: center;">Unidade</td>
<td></td>
<td></td>
<td>RH_T_AVD.SECCAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td></td>
<td></td>
<td>RH_T_AVD. CARR_PCCS_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td></td>
<td></td>
<td>RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td></td>
<td></td>
<td>RH_T_AVD.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td></td>
<td></td>
<td>RH_T_AVD.Carr_pccs_id</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acoes</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Definir Objectivo</td>
<td colspan="3">Abre o formulario Para Definir Os Objectivos</td>
</tr>
<tr>
<td style="text-align: center;">Editar</td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

### Objectivos Comuns / avaliação 

Nota: embora foi Feito 1 formulario. Mas o mesmo formulario pode server
para definição de Ojectivo e avaliação.

- Campo **Ponderação** e **Realizadas** so devem aparecer em avaliação

<img src="media/image14.png" style="width:9.69306in;height:3.20764in" />

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 5%" />
<col style="width: 30%" />
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
<td style="text-align: left;">ANO</td>
<td></td>
<td colspan="2"></td>
<td style="text-align: left;">RH_T_AVD.ANO</td>
</tr>
<tr>
<td style="text-align: left;">PERIODIODICIDADE</td>
<td></td>
<td colspan="2"><strong>NOTA:</strong> ESSE CAMPO DEVE APARECER SOMENTE
NO MOMENTO DE AVALIAÇÃO</td>
<td style="text-align: left;">ESSE REGISTO É SOMENTE EM AVALIAÇÃO .
<mark>RH_T_AVD_PERIODICIDADE.</mark>REALIZADO</td>
</tr>
<tr>
<td colspan="5" style="text-align: left;"><strong>OBJECTIVOS
INSTITUIÇÃO</strong></td>
</tr>
<tr>
<td style="text-align: left;">Numero</td>
<td>NUMBER</td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.NUMERO_ORDEM</td>
<td style="text-align: left;">RH_T_AVD_OBJECTIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abrangência</td>
<td>TEXT</td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.ABRAGENCIA</td>
<td style="text-align: left;"><p>RH_T_AVD_OBJECTIVO.ABRAGENCIA</p>
<p>RH_T_AVD .ABRAGENCIA</p></td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td>SELECT</td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.DESCRICAO</td>
<td>RH_T_AVD_OBJECTIVO.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td>SELECT</td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.KPI</td>
<td>RH_T_AVD_OBJECTIVO.KPI</td>
</tr>
<tr>
<td style="text-align: left;">Meta</td>
<td>TEXT</td>
<td colspan="2"></td>
<td>RH_T_AVD_OBJECTIVO.META</td>
</tr>
<tr>
<td style="text-align: left;">Realizadas</td>
<td></td>
<td colspan="2"></td>
<td><p>ESSE REGISTO É SOMENTE EM AVALIAÇÃO .</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>.REALIZADO</p></td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td></td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.PONDERACAO</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Avaliação</td>
<td></td>
<td colspan="2"></td>
<td><p>ESSE REGISTO É SOMENTE EM AVALIAÇÃO .</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>.AVALIACAO</p></td>
</tr>
<tr>
<td style="text-align: left;">Resultado da avaliação</td>
<td></td>
<td colspan="2">RH_T_AVD_OBJECTIVO.PONDERACAO*
<mark>RH_T_AVD_PERIODICIDADE</mark>.AVALIACAO</td>
<td></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p>Deve pegar todos
objectivos cujo componente = ‘OBJETIVO’ da tabela RH_T_PARAM_OBJECTIVO,
Pega a última versão definida no ano (RH_T_PARAM_OBJECTIVO_DET.ANO)</p>
<p><strong>Gravação</strong></p>
<p><strong>1-TABELA RH_T_AVD</strong></p>
<ul>
<li><p>ABRAGENCIA</p></li>
<li><p>ANO</p></li>
<li><p>FUN_ID = null</p></li>
<li><p>INSTIT_ID = null</p></li>
</ul></td>
<td colspan="2"><p><strong>2-TABELA</strong> RH_T_AVD_OBJECTIVO</p>
<ul>
<li><p>RH_T_AVD_OBJECTIVO.PARAM_OBJECTIVO_ID =
RH_T_PARAM_OBJECTIVO.ID</p></li>
</ul>
<ul>
<li><p>RH_T_AVD_OBJECTIVO.AVD_ID = ID de RH_T_AVD</p></li>
</ul>
<p><strong>3- Caso for avaliação, logo grava na tabela
<mark>RH_T_AVD_PERIODICIDADE</mark></strong></p>
<ul>
<li><p><mark>RH_T_AVD_PERIODICIDADE.</mark>REFERNENCIA =
<strong>OBJECTIVO</strong></p></li>
<li><p><mark>RH_T_AVD_PERIODICIDADE</mark> .REFERENCIA_ID</p></li>
<li><p>= ID DE RH_T_AVD_OBJECTIVO</p></li>
<li><p><mark>RH_T_AVD_PERIODICIDADE.</mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p></li>
<li><p><mark>RH_T_AVD_PERIODICIDADE.</mark>PERIODICIDADE</p></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>OBJECTIVOS
DIREÇÃO</strong></td>
</tr>
<tr>
<td style="text-align: left;">Numero</td>
<td>NUMBER</td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.NUMERO_ORDEM</td>
<td>RH_T_AVD_OBJECTIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">DIreção</td>
<td></td>
<td colspan="2"><strong>RH_T_DIRECAO</strong></td>
<td>RH_T_AVD.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: left;">Abrangência</td>
<td>TEXT</td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.ABRAGENCIA</td>
<td>RH_T_AVD_OBJECTIVO.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td>SELECT</td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.DESCRICAO</td>
<td>RH_T_AVD_OBJECTIVO.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td>SELECT</td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.KPI</td>
<td>RH_T_AVD_OBJECTIVO.KPI</td>
</tr>
<tr>
<td style="text-align: left;">Meta</td>
<td>TEXT</td>
<td colspan="2"></td>
<td>RH_T_AVD_OBJECTIVO.META</td>
</tr>
<tr>
<td style="text-align: left;">Realizadas</td>
<td></td>
<td colspan="2"></td>
<td><p>ESSE REGISTO É SOMENTE EM AVALIAÇÃO .</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>.REALIZADO</p></td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td></td>
<td colspan="2"><strong>RH_T_PARAM_OBJETIVO</strong>.PONDERACAO</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Avaliação</td>
<td></td>
<td colspan="2"></td>
<td><p>ESSE REGISTO É SOMENTE EM AVALIAÇÃO .</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>.AVALIACAO</p></td>
</tr>
<tr>
<td style="text-align: left;">Resultado da avaliação</td>
<td></td>
<td colspan="2">RH_T_AVD_OBJECTIVO.PONDERACAO*
<mark>RH_T_AVD_PERIODICIDADE</mark>.AVALIACAO</td>
<td></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p>Deve pegar todos
objectivos cujo componente = ‘OBJETIVO’ da tabela RH_T_PARAM_OBJECTIVO,
Pega a última versão definida no ano (RH_T_PARAM_OBJECTIVO_DET.ANO)</p>
<p><strong>Gravação</strong></p>
<p><strong>1-TABELA RH_T_AVD</strong></p>
<ul>
<li><p>ABRAGENCIA</p></li>
<li><p>ANO</p></li>
<li><p>FUN_ID = null</p></li>
</ul>
<blockquote>
<p>INSTIT_ID = direção</p>
</blockquote></td>
<td colspan="2"><p><strong>2-TABELA</strong> RH_T_AVD_OBJECTIVO</p>
<ul>
<li><p>RH_T_AVD_OBJECTIVO.PARAM_OBJECTIVO_ID =
RH_T_PARAM_OBJECTIVO.ID</p></li>
</ul>
<ul>
<li><p>RH_T_AVD_OBJECTIVO.AVD_ID = ID de RH_T_AVD</p></li>
</ul>
<p><strong>3- Caso for avaliação, logo grava na tabela
<mark>RH_T_AVD_PERIODICIDADE</mark></strong></p>
<ul>
<li><p><mark>RH_T_AVD_PERIODICIDADE.</mark>REFERNENCIA =
<strong>OBJECTIVO</strong></p></li>
<li><p><mark>RH_T_AVD_PERIODICIDADE</mark> .REFERENCIA_ID</p></li>
<li><p>= ID DE RH_T_AVD_OBJECTIVO</p></li>
<li><p><mark>RH_T_AVD_PERIODICIDADE.</mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p></li>
<li><p><mark>RH_T_AVD_PERIODICIDADE.</mark>PERIODICIDADE</p></li>
</ul></td>
</tr>
</tbody>
</table>

### Registo dos Objectivo indidiviaul por colaborador

<img src="media/image15.png" style="width:9.69306in;height:4.41319in" />

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 5%" />
<col style="width: 30%" />
<col style="width: 6%" />
<col style="width: 2%" />
<col style="width: 40%" />
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
<td style="text-align: center;">Ano</td>
<td>select</td>
<td colspan="3"></td>
<td>RH_T_AVD.ANO</td>
</tr>
<tr>
<td style="text-align: center;"><del>Semestre</del></td>
<td><del>select</del></td>
<td
colspan="3"><mark><del>RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE</del></mark></td>
<td><del>RH_T_AVD.SEMESTRE</del></td>
</tr>
<tr>
<td style="text-align: center;">Direçcão</td>
<td>Select</td>
<td colspan="3">RH_T_AVD.INSTIT_ID</td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Seccão</td>
<td>Select</td>
<td colspan="3"></td>
<td><del>RH_T_AVD.SECCAO_ID</del></td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td>Select</td>
<td colspan="3">-----------------------------------</td>
<td><del>RH_T_AVD.CARGO_ID</del></td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td></td>
<td colspan="3"></td>
<td><del>RH_T_AVD.CARR_PCCS_ID</del></td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td>multiselect</td>
<td colspan="3">Pode selecionar mais de um colaborador para definir
objectivo</td>
<td><p>RH_T_AVD.FUN_ID</p>
<p>RH_T_AVD.ABRAGENCIA= INDIVIDUAL</p></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">OJECTIVO</td>
</tr>
<tr>
<td style="text-align: left;">Numero</td>
<td>NUMBER</td>
<td colspan="3"><strong>RH_T_PARAM_OBJETIVO</strong>.NUMERO_ORDEM</td>
<td style="text-align: left;">RH_T_AVD_OBJECTIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abrangência</td>
<td>TEXT</td>
<td colspan="3"><strong>RH_T_PARAM_OBJETIVO</strong>.ABRAGENCIA</td>
<td style="text-align: left;">RH_T_AVD_OBJECTIVO.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td>SELECT</td>
<td colspan="3"><p><strong>RH_T_PARAM_OBJETIVO</strong>.DESCRICAO</p>
<p>Caso de abrangecia seja individual, logo deve pegar os objectivos
devem vir preenchidos apartir de
<strong>RH_T_PARAM_MANUAL_FUNC</strong>.DESCRICAO, caso contrario são
preenchidos apartir de
<strong>RH_T_PARAM_OBJETIVO</strong>.DESCRICAO</p></td>
<td>RH_T_AVD_OBJECTIVO.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td>SELECT</td>
<td colspan="3"><strong>RH_T_PARAM_OBJETIVO</strong>.KPI</td>
<td>RH_T_AVD_OBJECTIVO.KPI</td>
</tr>
<tr>
<td style="text-align: left;">Meta</td>
<td>TEXT</td>
<td colspan="3"></td>
<td>RH_T_AVD_OBJECTIVO.META</td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p>Deve pegar todos
objectivos cujo componente = ‘OBJETIVO’ da tabela RH_T_PARAM_OBJECTIVO,
Pega a ultima versão definida no ano (RH_T_PARAM_OBJECTIVO_DET.ANO)</p>
<p><strong>Gravação</strong></p>
<p><strong>Gravação</strong></p>
<p><strong>1-TABELA RH_T_AVD</strong></p>
<ul>
<li><p>ABRAGENCIA</p></li>
<li><p>ANO</p></li>
<li><p>FUN_ID = ID DE FUNCIONARIO</p></li>
<li><p>INSTIT_ID = INSTIT_ID</p></li>
</ul></td>
<td colspan="3"><p><strong>2-TABELA</strong> RH_T_AVD_OBJECTIVO</p>
<ul>
<li><p>RH_T_AVD_OBJECTIVO.PARAM_OBJECTIVO_ID =
RH_T_PARAM_OBJECTIVO.ID</p></li>
<li><p>RH_T_AVD_OBJECTIVO.AVD_ID = ID de RH_T_AVD</p></li>
</ul></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">COMPETÊNCIAS -
COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td>TEXT</td>
<td colspan="3"><strong>RH_T_PARAM_OBJETIVO</strong>.NUMERO_ORDEM</td>
<td>RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td>TEXT</td>
<td colspan="3"><strong>RH_T_PARAM_OBJETIVO</strong>.ABRAGENCIA</td>
<td>RH_T_AVD_COMPETENCIA.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Competencia Comportamental</td>
<td>Select</td>
<td colspan="3">Preenchido apartir de Tabela RH_T_PARAM_MFUNCAO, CUJO
cargo = cargo do colaborador</td>
<td>RH_T_AVD_COMPETENCIA.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Peso</td>
<td>number</td>
<td
colspan="3"><strong>RH_T_PARAM_OBJETIVO_DET</strong>.PESO_COMPORTAMENTAIS</td>
<td>RH_T_AVD.PESO_COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td>number</td>
<td colspan="3"><strong>RH_T_PARAM_OBJETIVO</strong>.PONDERACAO</td>
<td>RH_T_AVD_COMPETENCIA.PONDERACAO</td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><p>Deve pegar todos
objectivos cujo componente = ‘COMPETENCIA_COMPORTAMENTAL’ da tabela
RH_T_PARAM_OBJECTIVO, Pega a última versão definida no ano
(RH_T_PARAM_OBJECTIVO_DET.ANO)</p>
<p><strong>Gravação:</strong></p>
<ul>
<li><p>RH_T_AVD_COMPETENCIA.COMPONENTE =
‘COMPETENCIA_COMPORTAMENTAL’</p></li>
<li><p>RH_T_AVD_COMPETENCIA.PARAM_OBJECTIVO_ID =
RH_T_PARAM_OBJECTIVO.ID</p></li>
<li><p>RH_T_AVD_COMPETENCIA.AVD_ID = ID de RH_T_AVD</p></li>
</ul></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">COMPETÊNCIA -TÉCNICA</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td>TEXT</td>
<td colspan="3"><strong>RH_T_PARAM_OBJETIVO</strong>.NUMERO_ORDEM</td>
<td>RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td>TEXT</td>
<td colspan="3"><strong>RH_T_PARAM_OBJETIVO</strong>.ABRAGENCIA</td>
<td>RH_T_AVD_COMPETENCIA.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Competencia Comportamental</td>
<td>Select</td>
<td colspan="3">Preenchido apartir de Tabela RH_T_PARAM_MFUNCAO, CUJO
cargo = cargo do colaborador</td>
<td>RH_T_AVD_COMPETENCIA.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Peso</td>
<td></td>
<td
colspan="3"><strong>RH_T_PARAM_OBJETIVO_DET</strong>.PESO_TECNICA</td>
<td>RH_T_AVD.PESO_TECNICA</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td></td>
<td colspan="3"><strong>RH_T_PARAM_OBJETIVO</strong>.PONDERACAO</td>
<td>RH_T_AVD_COMPETENCIA.PONDERACAO</td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><p>Deve pegar todos
objectivos cujo componente = ‘COMPETENCIA_TECNICA’ da tabela
RH_T_PARAM_OBJECTIVO, Pega a última versão definida no ano
(RH_T_PARAM_OBJECTIVO_DET.ANO)</p>
<p><strong>Gravação:</strong></p>
<ul>
<li><p>RH_T_AVD_COMPETENCIA.COMPONENTE = ‘COMPETENCIA_TECNICA’</p></li>
<li><p>RH_T_AVD_COMPETENCIA.PARAM_OBJECTIVO_ID =
RH_T_PARAM_OBJECTIVO.ID</p></li>
<li><p>RH_T_AVD_COMPETENCIA.AVD_ID = ID de RH_T_AVD</p></li>
</ul></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">ATITUDE PESSOAL</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td>TEXT</td>
<td colspan="3"></td>
<td>RH_T_AVD_ATITUDE_PESSOAL.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td>TEXT</td>
<td colspan="3"></td>
<td>RH_T_AVD_ATITUDE_PESSOAL.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Atitude Pessoal</td>
<td>TEXT</td>
<td colspan="3"></td>
<td>RH_T_AVD_ATITUDE_PESSOAL.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td>TEXT</td>
<td colspan="3"></td>
<td>RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO</td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><p>Deve pegar todos
objectivos cujo componente = ‘<strong>ATITUDE_PESSOAL</strong>’ da
tabela RH_T_PARAM_OBJECTIVO, Pega a ultima versão definida no ano
(RH_T_PARAM_OBJECTIVO_DET.ANO)</p>
<p><strong>Gravação:</strong></p>
<ul>
<li><p>RH_T_AVD_ATITUDE_PESSOAL.PARAM_OBJECTIVO_ID =
RH_T_PARAM_OBJECTIVO.ID</p></li>
<li><p>RH_T_AVD_ATITUDE_PESSOAL.AVD_ID = ID de RH_T_AVD</p></li>
</ul></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p>Deve fazer gravação nas seguintes Tabelas:</p>
<ol type="1">
<li><p><strong>RH_T_AVD_OBJECTIVO</strong></p></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
</ul>
<ol start="2" type="1">
<li><p><strong>RH_T_AVD_COMPETENCIA</strong> (<em>Aqui se faz duas
gravações, uma referente a componente técnico e outro referente a
componente comportamental</em>)</p></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
</ul></td>
<td colspan="2"><ol start="3" type="1">
<li><p><strong>RH_T_AVD_ATUTUDE_PESSOAL</strong></p></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

## Avaliação

### Lista Avaliação

<img src="media/image16.png" style="width:9.69306in;height:3.24167in" />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
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
<td style="text-align: center;">Ano</td>
<td><em>SELECT</em></td>
<td></td>
<td>RH_T_AVD.ANO</td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td><em>SEELCT</em></td>
<td></td>
<td>RH_T_AVD.INSITID_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td><em>SELECT</em></td>
<td></td>
<td>RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td></td>
<td></td>
<td>RH_T_AVD.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td></td>
<td></td>
<td>RH_T_AVD.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td></td>
<td>INSPSIGOF.INSTITUICOES.NOME</td>
<td>RH_T_AVD.INSITID_ID</td>
</tr>
<tr>
<td style="text-align: center;">UNIDADE</td>
<td></td>
<td>RH_T_SECCAO</td>
<td>RH_T_AVD.SECCAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td></td>
<td>RH_T_param_CARGO.NOME</td>
<td>RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td></td>
<td>Rh_t_param_carreira</td>
<td>RH_T_AVD. CARR_PCCS_ID</td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td></td>
<td>RH_T_FUNCIONARIO.NOME</td>
<td>RH_T_AVD.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;"><del>Semestre / Nota</del></td>
<td></td>
<td><del>Agrupar semestre e nota por semestre</del></td>
<td><del>RH_T_AVD_DETALHE.SEMESTRE/
RH_T_AVD_DETALHE.AVALIACAO_FINAL</del></td>
</tr>
<tr>
<td style="text-align: center;">Nota final</td>
<td></td>
<td>Sumatoria de AVALIACAO_FINAL</td>
<td>RH_T_AVD.AVALIACAO_FINAL</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Regra</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p>A Lista deve estar agrupado por ano, direcao, cargo e
colaborador</p></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Acoes
Pai</strong></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image17.png"
style="width:1.12847in;height:0.21597in" /></td>
<td colspan="3">Permite cada colaboradro efectuar a sua propia
avaliação</td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image18.png"
style="width:1.09514in;height:0.22014in" /></td>
<td colspan="3">Permite que cada responsavel direto efectua a avaliacão
ao seu colaborador</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação Final</td>
<td colspan="3">Permite Ver Nota final atribuido ao semestre</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Acoes
Filho</strong></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image19.png"
style="width:0.76667in;height:0.20909in" /></td>
<td colspan="3"></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image20.png"
style="width:1.01583in;height:0.22361in" /></td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

### Autoavaliação 

<img src="media/image21.png" style="width:9.69306in;height:4.41319in" />

<table>
<colgroup>
<col style="width: 10%" />
<col style="width: 6%" />
<col style="width: 48%" />
<col style="width: 35%" />
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
<td colspan="4" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Ano</td>
<td></td>
<td>RH_T_AVD.ANO</td>
<td>---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td></td>
<td>RH_T_AVD.instit_id</td>
<td>---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Seccão</td>
<td></td>
<td>RH_T_AVD.seccao_id</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td></td>
<td>RH_T_AVD.CARGO_ID</td>
<td>---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td></td>
<td>RH_T_AVD.CARR_PCCS_ID</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><mark>Periodicidade</mark></td>
<td></td>
<td><mark>RH_T_AVD.PERIODICIDADE =
<strong>RH_T_DOMINIO</strong>.REFERENCIA onde <strong>DOMINIO</strong> =
PERIODICIDADE</mark></td>
<td><mark>RH_T_AVD_PERIODICIDADE..PERIODICIDADE</mark></td>
</tr>
<tr>
<td style="text-align: left;">OBJECTIVOS</td>
<td><em>FORMLIST</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td></td>
<td>RH_T_AVD_OBJECTIVO.NUMERO_ORDEM</td>
<td>------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td></td>
<td>RH_T_AVD_OBJECTIVO.ABRANGENCIA</td>
<td>----------------------</td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td></td>
<td>RH_T_AVD_OBJECTIVO.DESCRICAO</td>
<td>-------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td></td>
<td>RH_T_AVD_OBJECTIVO.KPI</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Meta</td>
<td></td>
<td>RH_T_AVD_OBJECTIVO.META</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Realizado</td>
<td></td>
<td></td>
<td><mark>RH_T_AVD_PERIODICIDADE.</mark>AUTO_REALIZADO</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td></td>
<td>RH_T_AVD_OBJECTIVO.PONDERACAO</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Avaliação</td>
<td></td>
<td></td>
<td><mark>RH_T_AVD_PERIODICIDADE</mark> .AUTO_AVALIACAO</td>
</tr>
<tr>
<td style="text-align: left;">Resultado Avaliação</td>
<td></td>
<td><p>Multiplicação entre avaliação e ponderação</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>.AUTO_AVALIACAO<strong>*</strong>RH_T_AVD_OBJECTIVO.PONDERACAO</p></td>
<td>-------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td><p><mark>RH_T_AVD_PERIODICIDADE.</mark>REFERENCIA =
<strong>OBJECTIVO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark> .REFERENCIA_ID</p>
<p>= ID DE RH_T_AVD_OBJECTIVO</p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>PERIODICIDADE</p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">COMPETÊNCIAS -
COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.ABRANGENCIA</td>
<td>-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Competência comportamentais</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.DESCRICAO</td>
<td>-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Peso</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.PESO</td>
<td>-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td>-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td></td>
<td></td>
<td><mark>RH_T_AVD_PERIODICIDADE.</mark>AUTO_AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td></td>
<td><mark>RH_T_AVD_PERIODICIDADE</mark>. AUTO_AVALIACAO
*RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;"><del>------------------------</del></td>
<td></td>
<td><del>---------------------------------------------------------------------------------------------------------------------------------</del></td>
<td><p><mark>RH_T_AVD_PERIODICIDADE.</mark>REFERNENCIA =
COMPETENCIA_COMPORTAMENTAIS</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark> .REFERENCIA_ID</p>
<p>= ID DE RH_T_AVD_COMPETENCIA</p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>PERIODICIDADE</p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">COMPETÊNCIAS - TÉCNICAS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.ABRANGENCIA</td>
<td>-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Competência Técnica</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.DESCRICAO</td>
<td>-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Peso</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.PESO</td>
<td>-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td></td>
<td>RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td>-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td></td>
<td></td>
<td><mark>RH_T_AVD_PERIODICIDADE.</mark>AUTO_AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td></td>
<td><mark>RH_T_AVD_PERIODICIDADE</mark>. AUTO_AVALIACAO
*RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td></td>
<td></td>
<td><p><mark>RH_T_AVD_PERIODICIDADE.</mark>REFERNENCIA =
COMPETENCIA_TECNICA</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark> .REFERENCIA_ID</p>
<p>= ID DE RH_T_AVD_COMPETENCIA</p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>PERIODICIDADE</p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">ATITUDE PESSOAL</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td></td>
<td>RH_T_AVD_ATITUDE_PESSOAL.NUMERO_ORDEM</td>
<td>---------------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td></td>
<td>RH_T_AVD_ATITUDE_PESSOAL.ABRANGENCIA</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Atitude Pessoal</td>
<td></td>
<td>RH_T_AVD_ATITUDE_PESSOAL.DESCRICAO</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td></td>
<td>RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td></td>
<td></td>
<td><mark>RH_T_AVD_PERIODICIDADE. AUTO_AVALIACAO</mark></td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td></td>
<td><mark>RH_T_AVD_PERIODICIDADE</mark>.AUTO_AVALIACAO*RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;"><del>------------------------</del></td>
<td></td>
<td><del>---------------------------------------------------------------------------------------------------------------------------------</del></td>
<td><p><mark>RH_T_AVD_PERIODICIDADE</mark>.REFERNENCIA =
<strong>ATITUDE_PESSOAL</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>.REFERENCIA_ID = ID DE
RH_T_AVD_ATITUDE_PESSOAL</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>. <mark></mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>. PERIODICIDADE</p></td>
</tr>
<tr>
<td style="text-align: center;"></td>
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
<td colspan="4" style="text-align: center;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3">Faz update nas tabelas acima indicada</td>
</tr>
</tbody>
</table>

### Processo de Avaliação

<img src="media/image22.png" style="width:9.69306in;height:5.52639in" />

<img src="media/image23.png" style="width:9.69167in;height:5.36528in" />

<table style="width:100%;">
<colgroup>
<col style="width: 8%" />
<col style="width: 3%" />
<col style="width: 39%" />
<col style="width: 17%" />
<col style="width: 15%" />
<col style="width: 15%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th colspan="2" style="text-align: center;"><strong>Update</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">Colaborador</td>
<td></td>
<td colspan="2">RH_T_FUNCIONARIOS.NOME, RH_T_AVD.FUN_ID</td>
<td colspan="2">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Ano</td>
<td></td>
<td colspan="2">RH_T_AVD.ANO</td>
<td colspan="2">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td></td>
<td colspan="2">RH_T_AVD.INSTIT_ID</td>
<td colspan="2">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Seccao</td>
<td></td>
<td colspan="2">RH_T_AVD.SECCAO_ID</td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td></td>
<td colspan="2">RH_T_AVD.CARGO_ID</td>
<td colspan="2">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td></td>
<td colspan="2">RH_T_AVD.CARR_PCCS_ID</td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;"><del>Semestre</del></td>
<td></td>
<td colspan="2"><del>RH_T_AVD.SEMESTRE</del></td>
<td colspan="2">--------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;"><mark>Periodicidade</mark></td>
<td></td>
<td colspan="2"><mark>RH_T_AVD.PERIODICIDADE =
<strong>RH_T_DOMINIO</strong>.REFERENCIA onde <strong>DOMINIO</strong> =
PERIODICIDADE</mark></td>
<td colspan="2"><mark>RH_T_AVD_PERIODICIDADE</mark>.PERIODICIDADE</td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">OBJECTIVOS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td></td>
<td colspan="2">RH_T_AVD_OBJECTIVO.NUMERO_ORDEM</td>
<td colspan="2">------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td></td>
<td colspan="2">RH_T_AVD_OBJECTIVO.ABRANGENCIA</td>
<td colspan="2">----------------------</td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td></td>
<td colspan="2">RH_T_AVD_OBJECTIVO.DESCRICAO</td>
<td colspan="2">-------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td></td>
<td colspan="2">RH_T_AVD_OBJECTIVO.KPI</td>
<td colspan="2">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Meta</td>
<td></td>
<td colspan="2">RH_T_AVD_OBJECTIVO.META</td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: left;">Realizado</td>
<td></td>
<td colspan="2"></td>
<td colspan="2"><mark>RH_T_AVD_PERIODICIDADE</mark>.REALIZADO</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td></td>
<td colspan="2">RH_T_AVD_OBJECTIVO.PONDERACAO</td>
<td colspan="2">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Avaliação</td>
<td></td>
<td colspan="2"></td>
<td colspan="2"><mark>RH_T_AVD_PERIODICIDADE.</mark>.AVALIACAO</td>
</tr>
<tr>
<td style="text-align: left;">Resultado Avaliação</td>
<td></td>
<td colspan="2">RH_T_AVD_OBJECTIVO.PONDERACAO*
<mark>RH_T_AVD_PERIODICIDADE</mark>.AVALIACAO</td>
<td colspan="2">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td colspan="2"></td>
<td colspan="2"><p><mark>RH_T_AVD_PERIODICIDADE.</mark>REFERNENCIA =
<strong>OBJECTIVO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark> .REFERENCIA_ID</p>
<p>= ID DE RH_T_AVD_COMPETENCIA</p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>PERIODICIDADE</p></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">COMPETÊNCIAS -
COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
<td colspan="2">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.ABRANGENCIA</td>
<td colspan="2">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Competência comportamentais</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.DESCRICAO</td>
<td colspan="2">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Peso</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.PESO</td>
<td colspan="2">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td colspan="2">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td></td>
<td colspan="2"></td>
<td colspan="2"><mark>RH_T_AVD_PERIODICIDADE</mark>.AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td></td>
<td
colspan="2"><mark>RH_T_AVD_PERIODICIDADE</mark>.AVALIACAO*RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td colspan="2">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td></td>
<td colspan="2"></td>
<td colspan="2"><p><mark>RH_T_AVD_PERIODICIDADE.</mark>REFERNENCIA =
COMPETENCIA_COMPORTAMENTAIS</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark> .REFERENCIA_ID</p>
<p>= ID DE RH_T_AVD_COMPETENCIA</p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>PERIODICIDADE</p></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">COMPETÊNCIAS - TÉCNICAS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.ABRANGENCIA</td>
<td colspan="2">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Competência Técnica</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.DESCRICAO</td>
<td colspan="2">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Peso</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.PESO</td>
<td colspan="2">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td colspan="2">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td></td>
<td colspan="2"></td>
<td colspan="2"><mark>RH_T_AVD_PERIODICIDADE</mark>.AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td></td>
<td colspan="2">RH_T_AVD_COMPETENCIA.PONDERACAO*
<mark>RH_T_AVD_PERIODICIDADE</mark>.AVALIACAO</td>
<td colspan="2">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">-----------</td>
<td></td>
<td
colspan="2">---------------------------------------------------------------------------------</td>
<td colspan="2"><p><mark>RH_T_AVD_PERIODICIDADE.</mark>REFERNENCIA =
COMPETENCIA_TECNICA</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark> .REFERENCIA_ID</p>
<p>= ID DE RH_T_AVD_COMPETENCIA</p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE.</mark>PERIODICIDADE</p></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">ATITUDE PESSOAL</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td></td>
<td colspan="2">RH_T_AVD_ATITUDE_PESSOAL.NUMERO_ORDEM</td>
<td colspan="2">---------------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td></td>
<td colspan="2">RH_T_AVD_ATITUDE_PESSOAL.ABRANGENCIA</td>
<td colspan="2">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Atitude Pessoal</td>
<td></td>
<td colspan="2">RH_T_AVD_ATITUDE_PESSOAL.DESCRICAO</td>
<td colspan="2">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td></td>
<td colspan="2">RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO</td>
<td colspan="2">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td></td>
<td colspan="2"></td>
<td colspan="2"><mark>RH_T_AVD_PERIODICIDAD</mark>E. AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td></td>
<td colspan="2">RH_T_AVD_ATITUDE_PESSOAL. AVALIACAO*
<mark>RH_T_AVD_PERIODICIDAD</mark>E.PONDERACAO</td>
<td colspan="2">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">----</td>
<td></td>
<td
colspan="2">---------------------------------------------------------------------------------------------------------</td>
<td colspan="2"><p><mark>RH_T_AVD_PERIODICIDADE</mark>.REFERNENCIA =
<strong>ATITUDE_PESSOAL</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>.REFERENCIA_ID = ID DE
RH_T_AVD_ATITUDE_PESSOAL</p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>. <mark></mark>TIPO_PROCESSO =
<strong>AVALIACAO</strong></p>
<p><mark>RH_T_AVD_PERIODICIDADE</mark>. PERIODICIDADE</p></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">AVALIAÇÃO GLOBAL DE
DESEMPENHO</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;"><strong>Comonente de
Avaliação</strong></td>
<td style="text-align: center;"><strong>Resultado</strong></td>
<td style="text-align: center;"><strong>Ponderação</strong></td>
<td><strong>Resultado Final</strong></td>
<td><strong>Gravação</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Objectivos</td>
<td><strong>SUM</strong>(RH_T_AVD_OBJECTIVO.PONDERACAO*RH_T_AVD_OBJECTIVO.AVALIACAO)</td>
<td>RH_T_PARAM_OBJETIVO_DET.PONDERACAO_OBJETIVO</td>
<td>Resultado * Ponderação</td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>.AVALIACAO_OBJECTIVO</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Competências</td>
<td><p><strong>Resultado</strong> COMPETENCIAS COMPORTAMENTAIS
<strong>+</strong> <strong>Resultado</strong> COMPETENCIAS TÉCNICAS</p>
<p><strong>SUM</strong>(RH_T_AVD_COMPETENCIA.PONDERACAO*RH_T_AVD_COMPETENCIA.AVALIACAO)</p></td>
<td>RH_T_PARAM_OBJETIVO_DET.PONDERACAO_COMPETENCIA</td>
<td>Resultado * Ponderação</td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>
AVALIACAO_COMPETENCIA</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Atitude Pessoal</td>
<td><strong>SUM</strong>(RH_T_AVD_ATITUDE_PESSOAL.
AVALIACAO*RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO)</td>
<td>RH_T_PARAM_OBJETIVO_DET.PONDERACAO_ATITUDE_PESS</td>
<td>Resultado * Ponderação</td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>
AVALIACAO_ATITUDE_PESS</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Avaliação Expressiva -
Expressão quantitativa</td>
<td colspan="2"></td>
<td>Somatoria de (<strong>Resultado * Ponderação</strong>) de Objectivo,
Compênticias, Atitude Pessoal</td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong> AVALIACAO_FINAL</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Avaliação Expressiva -
Expressão qualitativa</td>
<td colspan="2">Deve verificar em qual escala se enquadra apartir de
parametrização, <strong>RH_T_PARAM_ESCALA.</strong>QUANTITATIVA_DE,
<strong>RH_T_PARAM_ESCALA</strong>.QUANTITATIVA_ATE</td>
<td>RH_T_PARAM_ESCALA.QUALITATIVA</td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>.AVALIACAO_QUALITATIVA</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">-----------------------</td>
<td
colspan="2">---------------------------------------------------------------------------------------------------------------------------------------</td>
<td>--------------------------------------</td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong> PERIODICIDADE</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">-----------------------</td>
<td
colspan="2">---------------------------------------------------------------------------------------------------------------------------------------</td>
<td>--------------------------------------</td>
<td><p><strong><mark>RH_T_AVD_DETALHE.</mark>PERIODICIDADE</strong></p>
<p><strong><mark>RH_T_AVD_DETALHE. AVD_ID =</mark> ID DE
RH_T_AVD</strong></p></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr>
<th colspan="4"><img src="media/image24.png"
style="width:9.67569in;height:2.25in" /></th>
</tr>
</thead>
<tbody>
<tr>
<td>OBSERVAÇÃO GERAL</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Observação Geral de Avaliação</td>
<td></td>
<td></td>
<td><mark><strong>RH_T_AVD_DETALHE</strong>.</mark>OBSERVACAO_GERAL</td>
</tr>
<tr>
<td>Descrição Do Plano Desemvolvimento</td>
<td></td>
<td></td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>.DESCRICAO_PLANO</td>
</tr>
<tr>
<td>Data início</td>
<td></td>
<td></td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>.DATA_INICIO_ENTREVISTA</td>
</tr>
<tr>
<td>Hora Inicio</td>
<td></td>
<td></td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>.HORA_INICIO_ENTREVISTA</td>
</tr>
<tr>
<td>Hora Fim</td>
<td></td>
<td></td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>.HORA_FIM_ENTREVISTA</td>
</tr>
<tr>
<td colspan="4"><img src="media/image25.png"
style="width:9.69167in;height:1.70833in" /></td>
</tr>
<tr>
<td colspan="4">PARECER COLABORADOR</td>
</tr>
<tr>
<td>Parecer</td>
<td>Select</td>
<td></td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>
.PARECER_COLABORADOR</td>
</tr>
<tr>
<td>Justificar</td>
<td>Text Area</td>
<td></td>
<td><mark><strong>RH_T_AVD_DETALHE</strong>.</mark>JUSTIFICACAO_MOTIVO</td>
</tr>
<tr>
<td colspan="4"><img src="media/image26.png"
style="width:9.675in;height:1.62361in" /></td>
</tr>
<tr>
<td>Comissão Executiva</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Observação</td>
<td>TEXTAREA</td>
<td></td>
<td><strong><mark>RH_T_AVD_DETALHE</mark></strong>.OBS_COMISSAO_EXEC</td>
</tr>
<tr>
<td colspan="4"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image6.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p>Faz update nas tabelas acima indicada</p>
<ul>
<li><p><strong>RH_T_AVD.</strong>ESTADO =
“<strong>PARCIALMENTE</strong>” , Caso for o primeiro semestre, caso
contrario “<strong>TODO PERIODO</strong>”</p></li>
</ul></td>
</tr>
</tbody>
</table>

### Avaliação Final

<table style="width:100%;">
<colgroup>
<col style="width: 11%" />
<col style="width: 7%" />
<col style="width: 30%" />
<col style="width: 34%" />
<col style="width: 16%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Comonente de
Avaliação</strong></th>
<th style="text-align: center;"><strong>TIPO</strong></th>
<th style="text-align: center;"><strong>Avaliação Final</strong></th>
<th style="text-align: center;"><strong>Ponderação</strong></th>
<th style="text-align: center;"><strong>Classificação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">I Semetre</td>
<td><em>TEXT</em></td>
<td><strong>RH_T_AVD_DET</strong>.AVALIACAO_FINAL, do primer
Semestre</td>
<td><strong>DOMIAN</strong> = AVD_PONDERACAO_FINAL REFERENCIA =
SEMESTRE1</td>
<td>Avaliação final * Ponderação</td>
</tr>
<tr>
<td style="text-align: center;">II Semestre</td>
<td><em>TEXT</em></td>
<td><strong>RH_T_AVD</strong>.AVALIACAO_FINAL, do segundo Semestre</td>
<td><strong>DOMIAN</strong> = AVD_PONDERACAO_FINAL REFERENCIA =
SEMESTRE2</td>
<td>Avaliação final * Ponderação</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação Expressiva - Expressão
quantitativa</td>
<td><em>TEXT</em></td>
<td colspan="2"></td>
<td>Sumatoria de (Avaliação final * Ponderação) dos 2 semestres</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação Expressiva - Expressão
qualitativa</td>
<td><em>TEXT</em></td>
<td colspan="2">Deve verificar em qual escala se enquadra apartir de
parametrização, <strong>RH_T_PARAM_ESCALA.</strong>QUANTITATIVA_DE,
<strong>RH_T_PARAM_ESCALA</strong>.QUANTITATIVA_ATE</td>
<td>RH_T_PARAM_ESCALA.QUALITATIVA</td>
</tr>
</tbody>
</table>

## Modelo Dados

<img src="media/image27.png" style="width:8.03125in;height:5.80208in" />

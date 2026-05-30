<figure>
<img src="media/image3.jpeg" style="width:14.65694in;height:9.77083in"
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

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 28%" />
<col style="width: 49%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Funcionalidade</strong></td>
<td style="text-align: center;"><strong>Sub -
Funcionalidade</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
</tr>
<tr>
<td rowspan="2" style="text-align: center;">Parametrização</td>
<td style="text-align: center;"><p>Identificação dos componentes de
avaliação</p>
<ul>
<li><p>Aqui se define os obectivos e as ponderações</p></li>
</ul></td>
<td style="text-align: center;"><p>Cada ano, cada direcçao deve
identificar os objectivos do seu departamento.</p>
<p><strong>Objectivos:</strong> Existem objectivos comum ao INPS,
objectivos que são por direção e objectivos que são por invidual (sao
definidos no manual de função )</p></td>
</tr>
<tr>
<td style="text-align: center;">Registo de escala de avaliação</td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Definição Dos Objectivos</td>
<td style="text-align: center;"><ul>
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
<td style="text-align: center;"></td>
<td style="text-align: center;">Permite ver e filtrar todos
colaboradores avaliados</td>
</tr>
<tr>
<td style="text-align: center;">Autoavalição</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">Permite cada colaborador efetua a sua
propia avaliação</td>
</tr>
<tr>
<td style="text-align: center;">Proceso de Avaliação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">Permite cada responsavel directo,
realiza a avaliação ao seu colaborador</td>
</tr>
</tbody>
</table>

# Especificação 

## Parametrização

### Componentes de Avaliação

<img src="media/image5.png" style="width:9.68264in;height:4.39167in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 5%" />
<col style="width: 36%" />
<col style="width: 2%" />
<col style="width: 40%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Formulario</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td colspan="2"
style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Gravação</strong></td>
</tr>
<tr>
<td style="text-align: left;">Ano</td>
<td style="text-align: center;">MULTISELECT</td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO_DET.ANO</td>
</tr>
<tr>
<td style="text-align: left;">Peso Competência Comportamentais</td>
<td style="text-align: center;">NUMBER</td>
<td colspan="2" style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PARAM_OBJETIVO_DET.PESO_COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: left;">Peso Competência Técnica</td>
<td style="text-align: center;">NUMBER</td>
<td colspan="2" style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PARAM_OBJETIVO_DET.PESO_TECNICA</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">AVALIAÇÃO GLOBAL DE
DESEMPENHO</td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PARAM_OBJETIVO_DET.PONDERACAO_OBJETIVO</td>
</tr>
<tr>
<td style="text-align: left;">Competências</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PARAM_OBJETIVO_DET.PONDERACAO_COMPETENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Atitude Pessoal</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PARAM_OBJETIVO_DET.PONDERACAO_ATITUDE_PESS</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">OBJECTIVOS</td>
</tr>
<tr>
<td style="text-align: left;">Aplicar a Todos</td>
<td style="text-align: center;"><em>RADIO</em></td>
<td colspan="2" style="text-align: center;">Caso for selecionado deve
ser registado cada uma das funções (ou seja será aplicado a todos os
cargos)</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td style="text-align: center;"><em>MULTISELECT</em></td>
<td colspan="2" style="text-align: center;"><p>Fica visivel, caso não
for selecionado aplicar a todos</p>
<p>RH_T_PARAM_CARGO.ID</p></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_PARAM_CARREIRA.ID</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARR_PCCCS_ID</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td style="text-align: center;"><em>NUMBER</em></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td colspan="2" style="text-align: center;">DOMAINS
=<strong>ABRANGENCIA_AVD</strong></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td colspan="2" style="text-align: center;">Somente fica visivel caso o
tipo de abragencia selecionado for DIRECAO</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: left;">Objectivo</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.KPI</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.PONDERACAO</td>
</tr>
<tr>
<td style="text-align: left;">----------------------------</td>
<td style="text-align: center;"><em>HIDDEN</em></td>
<td colspan="2"
style="text-align: center;">-------------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.COMPONENTE =
‘<strong>OBJECTIVO’</strong></td>
</tr>
<tr>
<td style="text-align: center;">----------------------</td>
<td style="text-align: center;">HIDDEN</td>
<td colspan="2"
style="text-align: center;">------------------------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.VERSAO =
VERSAO+1</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">COMPETÊNCIAS -
COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: center;">Aplicar a Todos</td>
<td style="text-align: center;">CHECK</td>
<td colspan="2" style="text-align: center;">Caso for selecionado deve
ser registado cada uma das funções</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"><p>Fica caso não for
selecionado aplicar a todos</p>
<p>RH_T_PARAM_CARGO.ID</p></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_PARAM_CARREIRA.ID</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARR_PCCCS_ID</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td style="text-align: center;"><em>NUMBER</em></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO. PONDERACAO</td>
</tr>
<tr>
<td style="text-align: center;">-----------------------------</td>
<td style="text-align: center;">HIDDEN</td>
<td colspan="2"
style="text-align: center;">------------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.COMPONENTE =
‘<strong>COMPETENCIAS_COMPORTAMENTAIS’</strong></td>
</tr>
<tr>
<td style="text-align: center;">-----------------------</td>
<td style="text-align: center;">HIDDEN</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.ABRAGENCIA =
INDIVIDUAL</td>
</tr>
<tr>
<td style="text-align: center;">----------------------</td>
<td style="text-align: center;">HIDDEN</td>
<td colspan="2"
style="text-align: center;">------------------------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.VERSAO =
VERSAO+1</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">COMPETÊNCIAS - TÉCNICAS</td>
</tr>
<tr>
<td style="text-align: center;">Aplicar a Todos</td>
<td style="text-align: center;">CHECK</td>
<td colspan="2" style="text-align: center;">Caso for selecionado deve
ser registado cada uma das funções (ou seja será aplicado a todos os
cargos)</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td style="text-align: center;">SELECT</td>
<td colspan="2" style="text-align: center;"><p>Fica visivel, caso não
for selecionado aplicar a todos</p>
<p>RH_T_PARAM_CARGO.ID</p></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td style="text-align: center;">SELECT</td>
<td colspan="2" style="text-align: center;">RH_T_PARAM_CARREIRA.ID</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARR_PCCCS_ID</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td style="text-align: center;"><em>NUMBER</em></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO. PONDERACAO</td>
</tr>
<tr>
<td style="text-align: center;">-----------------------------</td>
<td style="text-align: center;">HIDDEN</td>
<td colspan="2"
style="text-align: center;">------------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.COMPONENTE =
‘<strong>COMPETENCIAS_TECNICA’</strong></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.ABRAGENCIA =
INDIVIDUAL</td>
</tr>
<tr>
<td style="text-align: center;">----------------------</td>
<td style="text-align: center;">HIDDEN</td>
<td colspan="2"
style="text-align: center;">------------------------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.VERSAO =
VERSAO+1</td>
</tr>
<tr>
<td style="text-align: center;">ATITUDE PESSOAL</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Aplicar a Todos</td>
<td style="text-align: center;">CHECK</td>
<td colspan="2" style="text-align: center;">Caso for selecionado deve
ser registado cada uma das funções (ou seja será aplicado a todos os
cargos)</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">cargo</td>
<td style="text-align: center;">SELECT</td>
<td colspan="2" style="text-align: center;"><p>Fica visivel, caso não
for selecionado aplicar a todos</p>
<p>RH_T_PARAM_CARGO.ID</p></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td style="text-align: center;">SELECT</td>
<td colspan="2" style="text-align: center;">RH_T_PARAM_CARREIRA.ID</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.CARR_PCCCS_ID</td>
</tr>
<tr>
<td style="text-align: center;"><del>Abrangência</del></td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.ABRAGENCIA =
INDIVIDUAL</td>
</tr>
<tr>
<td style="text-align: center;">-----------------------</td>
<td style="text-align: center;">HIDDEN</td>
<td colspan="2"
style="text-align: center;">-------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.COMPONENTE =
‘<strong>ATITUDE_PESSOAL’</strong></td>
</tr>
<tr>
<td style="text-align: center;">----------------------</td>
<td style="text-align: center;">HIDDEN</td>
<td colspan="2"
style="text-align: center;">------------------------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_OBJETIVO.VERSAO =
VERSAO+1</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="2" style="text-align: center;"><ol type="1">
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
<td colspan="2" style="text-align: center;"><ol start="2" type="1">
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

### Registar Escala

<img src="media/image7.png" style="width:9.68403in;height:3.39306in" />

<table style="width:100%;">
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 36%" />
<col style="width: 40%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Formulario</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Gravação</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">ESCALA DE AVALIACAO DE
OBJECTIVOS</td>
</tr>
<tr>
<td style="text-align: left;">Nivies de Avaliação</td>
<td style="text-align: center;">NUMBER</td>
<td style="text-align: center;">DOMAINS =
<strong>NIVEIS_AVD</strong></td>
<td style="text-align: center;">RH_T_PARAM_ESCALA.NIVEL</td>
</tr>
<tr>
<td style="text-align: left;">Avaliacao Qualitativa</td>
<td style="text-align: center;">TEXTAREA</td>
<td style="text-align: center;">DOMAINS =
<strong>CLASSIFICACAO_QUALIT_AVD</strong></td>
<td style="text-align: center;">RH_T_PARAM_ESCALA.QUALITATIVA</td>
</tr>
<tr>
<td style="text-align: left;">Descricao</td>
<td style="text-align: center;">TEXT</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_ESCALA.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Avaliação quantitativa de</td>
<td style="text-align: center;">NUMBER</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_ESCALA.QUANTITATIVA_DE</td>
</tr>
<tr>
<td style="text-align: left;">Avaliação quantitativa Até</td>
<td style="text-align: center;">NUMBER</td>
<td
style="text-align: center;">-------------------------------------------------------------</td>
<td style="text-align: center;">RH_T_PARAM_ESCALA.QUANTITATIVA_ATE</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3" style="text-align: center;"><p>Gravação na tabela
RH_T_PARAM_ESCALA, campos de formulario e outros campos :</p>
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

|  |  |  |  |
|:--:|:--:|:--:|:--:|
| **Formulario** | **Tipo** | **Descrição** | **Gravação** |
| Direção |  | INS | RH_T_PARAM_MANUAL_FUNC.INSTIT_ID |
| Unidade Organica |  | RH_T_SECCAO | RH_T_PARAM_MANUAL_FUNC.SECCAO_ID |
| Cargo | SELECT | RH_T_PARAM_CARGO | RH_T_PARAM_MANUAL_FUNC.CARGO_ID |
| Carreira |  | RH_T_PARAM_CARREIRA | RH_T_PARAM_MANUAL_FUNC.CARREIRA_ID |
| Conteúdo |  |  | RH_T_PARAM_MANUAL_FUNC.DESCRICAO |
| Estado |  |  | RH_T_PARAM_MANUAL_FUNC.estado |

#### Lista

|  |  |  |  |
|:--:|:--:|:--:|:--:|
| **Filtro** | **Tipo** | **Descrição** | **Gravação** |
| Direção |  | INS | RH_T_PARAM_MANUAL_FUNC.INSTIT_ID |
| Unidade Organica |  | RH_T_SECCAO | RH_T_PARAM_MANUAL_FUNC.SECCAO_ID |
| Cargo | SELECT | RH_T_PARAM_CARGO | RH_T_PARAM_MANUAL_FUNC.CARGO_ID |
| Carreira |  | RH_T_PARAM_CARREIRA | RH_T_PARAM_MANUAL_FUNC.CARREIRA_ID |
| Conteúdo |  |  | RH_T_PARAM_MANUAL_FUNC.DESCRICAO |
| Lista |  |  |  |
| Direção | text |  | RH_T_PARAM_MANUAL_FUNC.INSTIT_ID |
| Unidade Organica | text |  | RH_T_PARAM_MANUAL_FUNC.SECCAO_ID |
| Cargo | text |  | RH_T_PARAM_MANUAL_FUNC.CARGO_ID |
| Carreira | text |  | RH_T_PARAM_MANUAL_FUNC.CARREIRA_ID |
| Conteúdo | text |  | RH_T_PARAM_MANUAL_FUNC.DESCRICAO |
| Estado | text |  | RH_T_PARAM_MANUAL_FUNC.estado |

## Definição Dos Objectivos

### Lista Definicao Ojectivos 

<img src="media/image8.png" style="width:9.69306in;height:4.53958in"
alt="Uma imagem com texto, captura de ecrã, software, número Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Filtro</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Ano</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.ANO</td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.Carr_pccs_id</td>
</tr>
<tr>
<td style="text-align: center;">Semestre</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.SEMESTRE</td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;">Ano</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.ANO</td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.INSITID_ID</td>
</tr>
<tr>
<td style="text-align: center;">Semestre</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.Carr_pccs_id</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acoes</strong></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Definir Objectivo</td>
<td colspan="3" style="text-align: center;">Abre o formulario Para
Definir Os Objectivos</td>
</tr>
<tr>
<td style="text-align: center;">Autoavaliacao</td>
<td colspan="3" style="text-align: center;">Abre o formulario Para
Registar Auto Autoavaliação</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td colspan="3" style="text-align: center;">Abre o formulario para o
Registo de Avaliaçao</td>
</tr>
<tr>
<td style="text-align: center;">Ver Avaliação Final</td>
<td colspan="3" style="text-align: center;">Abre o formulario para Ver
avaliação Final</td>
</tr>
</tbody>
</table>

### Registo dos Obejcto por colaborador

<img src="media/image9.png" style="width:8.9125in;height:3.25486in" />

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 5%" />
<col style="width: 37%" />
<col style="width: 2%" />
<col style="width: 40%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Formulario</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td colspan="2"
style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Gravação</strong></td>
</tr>
<tr>
<td style="text-align: center;">Ano</td>
<td style="text-align: center;">select</td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.ANO</td>
</tr>
<tr>
<td style="text-align: center;">Semestre</td>
<td style="text-align: center;">select</td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.SEMESTRE</td>
</tr>
<tr>
<td style="text-align: center;">Direçcão</td>
<td style="text-align: center;">Select</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
<td style="text-align: center;">RH_T_AVD.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Seccão</td>
<td style="text-align: center;">Select</td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.SECCAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td style="text-align: center;">Select</td>
<td colspan="2"
style="text-align: center;">-----------------------------------</td>
<td style="text-align: center;">RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.Carr_pccs_id</td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td style="text-align: center;">multiselect</td>
<td colspan="2" style="text-align: center;">Pode selecionar mais de um
colaborador para definir objectivo</td>
<td style="text-align: center;">RH_T_AVD.FUN_ID</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">OJECTIVO</td>
</tr>
<tr>
<td style="text-align: left;">Numero</td>
<td style="text-align: center;">NUMBER</td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO</strong>.NUMERO_ORDEM</td>
<td style="text-align: left;">RH_T_AVD_OBJECTIVO.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abrangência</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO</strong>.ABRAGENCIA</td>
<td style="text-align: left;">RH_T_AVD_OBJECTIVO.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td style="text-align: center;">SELECT</td>
<td colspan="2"
style="text-align: center;"><p><strong>RH_T_PARAM_OBJETIVO</strong>.DESCRICAO</p>
<p>Caso de abrangecia seja individual, logo deve pegar os objectivos
devem vir preenchidos apartir de
<strong>RH_T_PARAM_MANUAL_FUNC</strong>.DESCRICAO, caso contrario são
preenchidos apartir de
<strong>RH_T_PARAM_OBJETIVO</strong>.DESCRICAO</p></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td style="text-align: center;">SELECT</td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO</strong>.KPI</td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.KPI</td>
</tr>
<tr>
<td style="text-align: left;">Meta</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.META</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><p>Deve pegar todos
objectivos cujo componente = ‘OBJETIVO’ da tabela RH_T_PARAM_OBJECTIVO,
Pega a ultima versão definida no ano (RH_T_PARAM_OBJECTIVO_DET.ANO)</p>
<p><strong>Gravação</strong></p>
<ul>
<li><p>RH_T_AVD_OBJECTIVO.PARAM_OBJECTIVO_ID =
RH_T_PARAM_OBJECTIVO.ID</p></li>
<li><p>RH_T_AVD_OBJECTIVO.AVD_ID = ID de RH_T_AVD</p></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;">COMPETÊNCIAS -
COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO</strong>.NUMERO_ORDEM</td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO</strong>.ABRAGENCIA</td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Competencia Comportamental</td>
<td style="text-align: center;">Select</td>
<td colspan="2" style="text-align: center;">Preenchido apartir de Tabela
RH_T_PARAM_MFUNCAO, CUJO cargo = cargo do colaborador</td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Peso</td>
<td style="text-align: center;">number</td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO_DET</strong>.PESO_COMPORTAMENTAIS</td>
<td style="text-align: center;">RH_T_AVD.PESO_COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td style="text-align: center;">number</td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO</strong>.PONDERACAO</td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.PONDERACAO</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><p>Deve pegar todos
objectivos cujo componente = ‘COMPETENCIA_COMPORTAMENTAL’ da tabela
RH_T_PARAM_OBJECTIVO, Pega a ultima versão definida no ano
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
<td colspan="5" style="text-align: center;">COMPETÊNCIA -TÉCNICA</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO</strong>.NUMERO_ORDEM</td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO</strong>.ABRAGENCIA</td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Competencia Comportamental</td>
<td style="text-align: center;">Select</td>
<td colspan="2" style="text-align: center;">Preenchido apartir de Tabela
RH_T_PARAM_MFUNCAO, CUJO cargo = cargo do colaborador</td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Peso</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO_DET</strong>.PESO_TECNICA</td>
<td style="text-align: center;">RH_T_AVD.PESO_TECNICA</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;"><strong>RH_T_PARAM_OBJETIVO</strong>.PONDERACAO</td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.PONDERACAO</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><p>Deve pegar todos
objectivos cujo componente = ‘COMPETENCIA_TECNICA’ da tabela
RH_T_PARAM_OBJECTIVO, Pega a ultima versão definida no ano
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
<td colspan="5" style="text-align: center;">ATITUDE PESSOAL</td>
</tr>
<tr>
<td style="text-align: left;">Numero Ordem</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2" style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.NUMERO_ORDEM</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.ABRAGENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Atitude Pessoal</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td style="text-align: center;">TEXT</td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO</td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><p>Deve pegar todos
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
<td colspan="5" style="text-align: center;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="2" style="text-align: center;"><p>Deve fazer gravação nas
seguintes Tabelas:</p>
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
<td colspan="2" style="text-align: center;"><ol start="3" type="1">
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

<img src="media/image10.png" style="width:9.68681in;height:3.2in" />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Filtro</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Ano</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.ANO</td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td style="text-align: center;"><em>SEELCT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.INSITID_ID</td>
</tr>
<tr>
<td style="text-align: center;">Seçção</td>
<td style="text-align: center;"><em>select</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.SECCAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.CARR_PCCS_IS</td>
</tr>
<tr>
<td style="text-align: center;">Semestre</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.SEMESTRE</td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">INSPSIGOF.INSTITUICOES.NOME</td>
<td style="text-align: center;">RH_T_AVD.INSITID_ID</td>
</tr>
<tr>
<td style="text-align: center;">sECCAO</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_SECCAO</td>
<td style="text-align: center;">RH_T_AVD.SECCAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_param_CARGO.NOME</td>
<td style="text-align: center;">RH_T_AVD.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">Rh_t_param_carreira</td>
<td style="text-align: center;">RH_T_AVD.CARGO_pccs_is</td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_FUNCIONARIO.NOME</td>
<td style="text-align: center;">RH_T_AVD.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;">Semestre / Nota</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">Agrupar semestre e nota por
semestre</td>
<td style="text-align: center;">RH_T_AVD.SEMESTRE/
RH_T_AVD.AVALIACAO_FINAL</td>
</tr>
<tr>
<td style="text-align: center;">Nota final</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">Sumatoria de AVALIACAO_FINAL</td>
<td style="text-align: center;">RH_T_AVD.AVALIACAO_FINAL</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Regra</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p>A Lista deve estar agrupado por ano , direcao, cargo e
colaborador</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acoes</strong></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image11.png"
style="width:1.12847in;height:0.21597in" /></td>
<td colspan="3" style="text-align: center;">Permite cada colaboradro
efectuar a sua propia avaliação</td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image12.png"
style="width:1.09514in;height:0.22014in" /></td>
<td colspan="3" style="text-align: center;">Permite que cada responsavel
direto efectua a avaliacão ao seu colaborador</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação Final</td>
<td colspan="3" style="text-align: center;">Permite Ver Nota final
atribuido ao semestre</td>
</tr>
</tbody>
</table>

### Autoavaliação 

<img src="media/image13.png" style="width:9.69306in;height:4.26528in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 11%" />
<col style="width: 6%" />
<col style="width: 57%" />
<col style="width: 24%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Formulario</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Gravação</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Ano</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.ANO</td>
<td
style="text-align: center;">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.instit_id</td>
<td
style="text-align: center;">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Seccão</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.seccao_id</td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.CARGO_ID</td>
<td
style="text-align: center;">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.CARR_PCCS_ID</td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Semestre</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD.SEMESTRE</td>
<td
style="text-align: center;">--------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">OBJECTIVOS</td>
<td style="text-align: center;"><em>FORMLIST</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.NUMERO_ORDEM</td>
<td style="text-align: center;">------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.ABRANGENCIA</td>
<td style="text-align: center;">----------------------</td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.DESCRICAO</td>
<td style="text-align: center;">-------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.KPI</td>
<td
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Meta</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.META</td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Realizado</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.AUTO_REALIZADO</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.PONDERACAO</td>
<td
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Avaliação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_OBJECTIVO.AUTO_AVALIACAO</td>
</tr>
<tr>
<td style="text-align: left;">Resultado Avaliação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"><p>Multiplicação entre avaliação e
ponderação</p>
<p>RH_T_AVD_OBJECTIVO.AUTO_AVALIACAO<strong>*</strong>RH_T_AVD_OBJECTIVO.PONDERACAO</p></td>
<td
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">COMPETÊNCIAS -
COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
<td
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.ABRANGENCIA</td>
<td
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Competência comportamentais</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.DESCRICAO</td>
<td
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Peso</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.PESO</td>
<td
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_AVD_COMPETENCIA..AUTO_AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_AVD_COMPETENCIA.AVALIACAO*RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">COMPETÊNCIAS - TÉCNICAS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.ABRANGENCIA</td>
<td
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Competência Técnica</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.DESCRICAO</td>
<td
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Peso</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.PESO</td>
<td
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_COMPETENCIA.AUTO_AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_AVD_COMPETENCIA.AVALIACAO*RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">ATITUDE PESSOAL</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.NUMERO_ORDEM</td>
<td
style="text-align: center;">---------------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.ABRANGENCIA</td>
<td
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Atitude Pessoal</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.DESCRICAO</td>
<td
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO</td>
<td
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.
AUTO_AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.AUTO_AVALIACAO*RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO</td>
<td
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3" style="text-align: center;">Faz update nas tabelas acima
indicada</td>
</tr>
</tbody>
</table>

### Processo de Avaliação

<img src="media/image14.png" style="width:9.68125in;height:5.51181in" />

<img src="media/image15.png" style="width:9.69167in;height:5.36528in" />

<table>
<colgroup>
<col style="width: 8%" />
<col style="width: 3%" />
<col style="width: 39%" />
<col style="width: 17%" />
<col style="width: 15%" />
<col style="width: 15%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Formulario</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td colspan="2"
style="text-align: center;"><strong>Descrição</strong></td>
<td colspan="2" style="text-align: center;"><strong>Update</strong></td>
</tr>
<tr>
<td style="text-align: left;">Colaborador</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_FUNCIONARIOS.NOME,
RH_T_AVD.FUN_ID</td>
<td colspan="2"
style="text-align: center;">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Ano</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD.ANO</td>
<td colspan="2"
style="text-align: center;">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD.INSTIT_ID</td>
<td colspan="2"
style="text-align: center;">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Seccao</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD.SECCAO_ID</td>
<td colspan="2" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Cargo</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD.CARGO_ID</td>
<td colspan="2"
style="text-align: center;">---------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD.CARR_PCCS_ID</td>
<td colspan="2" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Semestre</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD.SEMESTRE</td>
<td colspan="2"
style="text-align: center;">--------------------------------------</td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">OBJECTIVOS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_OBJECTIVO.NUMERO_ORDEM</td>
<td colspan="2"
style="text-align: center;">------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Abragência</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_OBJECTIVO.ABRANGENCIA</td>
<td colspan="2" style="text-align: center;">----------------------</td>
</tr>
<tr>
<td style="text-align: left;">Objectivos</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_OBJECTIVO.DESCRICAO</td>
<td colspan="2"
style="text-align: center;">-------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD_OBJECTIVO.KPI</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Meta</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD_OBJECTIVO.META</td>
<td colspan="2" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Realizado</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_OBJECTIVO.REALIZADO</td>
</tr>
<tr>
<td style="text-align: left;">Ponderação</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_OBJECTIVO.PONDERACAO</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Avaliação</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_OBJECTIVO.AVALIACAO</td>
</tr>
<tr>
<td style="text-align: left;">Resultado Avaliação</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_OBJECTIVO.PONDERACAO*RH_T_AVD_OBJECTIVO.AVALIACAO</td>
<td colspan="2"
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">COMPETÊNCIAS -
COMPORTAMENTAIS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.ABRANGENCIA</td>
<td colspan="2"
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Competência comportamentais</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.DESCRICAO</td>
<td colspan="2"
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Peso</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.PESO</td>
<td colspan="2"
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td colspan="2"
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.AVALIACAO*RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">COMPETÊNCIAS - TÉCNICAS</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.NUMERO_ORDEM</td>
<td colspan="2" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.ABRANGENCIA</td>
<td colspan="2"
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Competência Técnica</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.DESCRICAO</td>
<td colspan="2"
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Peso</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.PESO</td>
<td colspan="2"
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.PONDERACAO</td>
<td colspan="2"
style="text-align: center;">-------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_COMPETENCIA.PONDERACAO*RH_T_AVD_COMPETENCIA.AVALIACAO</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td colspan="6" style="text-align: center;">ATITUDE PESSOAL</td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.NUMERO_ORDEM</td>
<td colspan="2"
style="text-align: center;">---------------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Abragência</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.ABRANGENCIA</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Atitude Pessoal</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.DESCRICAO</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Ponderação</td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.
AVALIACAO</td>
</tr>
<tr>
<td style="text-align: center;">Resultado Avaliação</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_AVD_ATITUDE_PESSOAL.
AVALIACAO*RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO</td>
<td colspan="2"
style="text-align: center;">----------------------------------------------</td>
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
<td style="text-align: left;"><strong>Resultado Final</strong></td>
<td style="text-align: left;"><strong>Gravação</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Objectivos</td>
<td
style="text-align: center;"><strong>SUM</strong>(RH_T_AVD_OBJECTIVO.PONDERACAO*RH_T_AVD_OBJECTIVO.AVALIACAO)</td>
<td
style="text-align: center;">RH_T_PARAM_OBJETIVO_DET.PONDERACAO_OBJETIVO</td>
<td style="text-align: center;">Resultado * Ponderação</td>
<td
style="text-align: center;"><strong>RH_T_AVD</strong>.AVALIACAO_OBJECTIVO</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Competências</td>
<td style="text-align: center;"><p><strong>Resultado</strong>
COMPETENCIAS COMPORTAMENTAIS <strong>+</strong>
<strong>Resultado</strong> COMPETENCIAS TÉCNICAS</p>
<p><strong>SUM</strong>(RH_T_AVD_COMPETENCIA.PONDERACAO*RH_T_AVD_COMPETENCIA.AVALIACAO)</p></td>
<td
style="text-align: center;">RH_T_PARAM_OBJETIVO_DET.PONDERACAO_COMPETENCIA</td>
<td style="text-align: center;">Resultado * Ponderação</td>
<td
style="text-align: center;"><strong>RH_T_AVD</strong>.AVALIACAO_COMPETENCIA</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Atitude Pessoal</td>
<td
style="text-align: center;"><strong>SUM</strong>(RH_T_AVD_ATITUDE_PESSOAL.
AVALIACAO*RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO)</td>
<td
style="text-align: center;">RH_T_PARAM_OBJETIVO_DET.PONDERACAO_ATITUDE_PESS</td>
<td style="text-align: center;">Resultado * Ponderação</td>
<td
style="text-align: center;"><strong>RH_T_AVD</strong>.AVALIACAO_ATITUDE_PESS</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Avaliação Expressiva -
Expressão quantitativa</td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">Somatoria de (<strong>Resultado *
Ponderação</strong>) de Objectivo, Compênticias, Atitude Pessoal</td>
<td
style="text-align: center;"><strong>RH_T_AVD</strong>.AVALIACAO_FINAL</td>
</tr>
<tr>
<td colspan="2" style="text-align: center;">Avaliação Expressiva -
Expressão qualitativa</td>
<td colspan="2" style="text-align: center;">Deve verificar em qual
escala se enquadra apartir de parametrização,
<strong>RH_T_PARAM_ESCALA.</strong>QUANTITATIVA_DE,
<strong>RH_T_PARAM_ESCALA</strong>.QUANTITATIVA_ATE</td>
<td style="text-align: center;">RH_T_PARAM_ESCALA.QUALITATIVA</td>
<td
style="text-align: center;"><strong>RH_T_AVD</strong>.AVALIACAO_QUALITATIVA</td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 40%" />
</colgroup>
<tbody>
<tr>
<td colspan="4"><img src="media/image16.png"
style="width:9.67569in;height:2.25in" /></td>
</tr>
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
<td>RH_T_AVD.OBSERVACAO_GERAL</td>
</tr>
<tr>
<td>Descrição Do Plano Desemvolvimento</td>
<td></td>
<td></td>
<td>RH_T_AVD.DESCRICAO_PLANO</td>
</tr>
<tr>
<td>Data inicio</td>
<td></td>
<td></td>
<td>RH_T_AVD.DATA_INICIO_ENTREVISTA</td>
</tr>
<tr>
<td>Hora Inicio</td>
<td></td>
<td></td>
<td>RH_T_AVD.HORA_INICIO_ENTREVISTA</td>
</tr>
<tr>
<td>Hora Fim</td>
<td></td>
<td></td>
<td>RH_T_AVD_DETALHE.HORA_FIM_ENTREVISTA</td>
</tr>
<tr>
<td colspan="4"><img src="media/image17.png"
style="width:9.69167in;height:1.70833in" /></td>
</tr>
<tr>
<td colspan="4">PARECER COLABORADOR</td>
</tr>
<tr>
<td>Parecer</td>
<td>Select</td>
<td></td>
<td>RH_T_AVD.PARECER_COLABORADOR</td>
</tr>
<tr>
<td>Justificar</td>
<td>Text Area</td>
<td></td>
<td>RH_T_AVD.JUSTIFICACAO_MOTIVO</td>
</tr>
<tr>
<td colspan="4"><img src="media/image18.png"
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
<td>RH_T_AVD_DETALHE.OBS_COMISSAO_EXEC</td>
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
contrario “<strong>TODO SEMESTRE</strong>”</p></li>
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
<tbody>
<tr>
<td style="text-align: center;"><strong>Comonente de
Avaliação</strong></td>
<td style="text-align: center;"><strong>TIPO</strong></td>
<td style="text-align: center;"><strong>Avaliação Final</strong></td>
<td style="text-align: center;"><strong>Ponderação</strong></td>
<td style="text-align: center;"><strong>Classificação</strong></td>
</tr>
<tr>
<td style="text-align: center;">I Semetre</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td
style="text-align: center;"><strong>RH_T_AVD</strong>.AVALIACAO_FINAL,
do primer Semestre</td>
<td style="text-align: center;"><strong>DOMIAN</strong> =
AVD_PONDERACAO_FINAL REFERENCIA = SEMESTRE1</td>
<td style="text-align: center;">Avaliação final * Ponderação</td>
</tr>
<tr>
<td style="text-align: center;">II Semestre</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td
style="text-align: center;"><strong>RH_T_AVD</strong>.AVALIACAO_FINAL,
do segundo Semestre</td>
<td style="text-align: center;"><strong>DOMIAN</strong> =
AVD_PONDERACAO_FINAL REFERENCIA = SEMESTRE2</td>
<td style="text-align: center;">Avaliação final * Ponderação</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação Expressiva - Expressão
quantitativa</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;">Sumatoria de (Avaliação final *
Ponderação) dos 2 semestres</td>
</tr>
<tr>
<td style="text-align: center;">Avaliação Expressiva - Expressão
qualitativa</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="2" style="text-align: center;">Deve verificar em qual
escala se enquadra apartir de parametrização,
<strong>RH_T_PARAM_ESCALA.</strong>QUANTITATIVA_DE,
<strong>RH_T_PARAM_ESCALA</strong>.QUANTITATIVA_ATE</td>
<td style="text-align: center;">RH_T_PARAM_ESCALA.QUALITATIVA</td>
</tr>
</tbody>
</table>

## Modelo Dados

<img src="media/image19.png" style="width:8.03125in;height:3.40625in"
alt="Uma imagem com texto, diagrama, file, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

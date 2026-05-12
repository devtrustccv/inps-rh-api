<figure>
<img src="media/image3.jpeg" style="width:14.65694in;height:9.77083in"
alt="C:\Users\joelm\Desktop\Imagens\sergey-zolkin-_UeY8aTI6d0-unsplash (2).jpg" />
<figcaption><p>SIPS-RH</p></figcaption>
</figure>

**TRANSVERSAL**

# Enquadramento 

# Âmbito 

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 16%" />
<col style="width: 35%" />
<col style="width: 35%" />
</colgroup>
<tbody>
<tr>
<td rowspan="3"><strong>Documento e Recibos</strong></td>
<td>ORDEM SERVICO</td>
<td colspan="2">Lista de documentos de ordem de serviço</td>
</tr>
<tr>
<td>Pedido de Declaracoes</td>
<td colspan="2">Funcionalidade que permite o colaborador solicitar uma
declaração</td>
</tr>
<tr>
<td>RECIBOS DE PROCESSAMENTO SALARIAL</td>
<td colspan="2">Permite exportar declaracoes e enviar ao
colaborador</td>
</tr>
<tr>
<td rowspan="7"><strong>Relatorio</strong></td>
<td>Dossier dos colaboradores</td>
<td><ul>
<li><p>Número de Colaborador;</p></li>
<li><p>Por Direção;</p></li>
<li><p>Por Seção</p></li>
<li><p>Por Função;</p></li>
<li><p>Por idade;</p></li>
<li><p>Por género;</p></li>
<li><p>Por ilhas/Lisboa;</p></li>
<li><p>Por faixa etária;</p></li>
<li><p>Por carreira (grupo profissional);</p></li>
<li><p>Por categoria;</p></li>
<li><p>Por escalão;</p></li>
</ul></td>
<td><ul>
<li><p>Por antiguidade;</p></li>
<li><p>Grau de escolaridade;</p></li>
<li><p>Mobilidade/Saídas;</p></li>
<li><p>Estrutura Remuneratória;</p></li>
<li><p>Tipo de Vínculo</p></li>
<li><p>Licença sem vencimento</p></li>
<li><p>Requisições</p></li>
<li><p>Reforma</p></li>
</ul></td>
</tr>
<tr>
<td>Processamento Salarial</td>
<td colspan="2"><ul>
<li><p>Lista de salário por período, direção, por colaborador;</p></li>
<li><p>SOAT</p></li>
<li><p>FOS</p></li>
<li><p>Retenções (IUR, INPS, Sindicato)</p></li>
<li><p>Validações dos salários</p></li>
</ul></td>
</tr>
<tr>
<td>Assiduidade</td>
<td colspan="2"><ul>
<li><p>Nº de dias de férias</p></li>
<li><p>Período de férias</p></li>
<li><p>Quem está de férias</p></li>
<li><p>Nº de faltas</p></li>
<li><p>Horas de dispensas gozadas e por gozar</p></li>
<li><p>Nº de horas extras</p></li>
<li><p>Faltas dadas</p></li>
</ul></td>
</tr>
<tr>
<td>Gestão de Empréstimo</td>
<td colspan="2"><ul>
<li><p>Dados do empréstimo por colaborador;</p></li>
<li><p>Lista de Empréstimos por tipo, por colaborador</p></li>
</ul></td>
</tr>
<tr>
<td>Missão de Serviço</td>
<td colspan="2"><ul>
<li><p>Listagem de missões</p></li>
<li><p>Missão por Colaboraor</p></li>
<li><p>Faturas pendentes/vencimento de faturas</p></li>
<li><p>Lista de ajuda de custos, alojamento, transporte e seguro de
viagem por colaborador, por direção</p></li>
</ul></td>
</tr>
<tr>
<td>Progressão/promoção</td>
<td colspan="2"><ul>
<li><p>Lista das próximas progressões/promoções</p></li>
<li><p>Lista de progressões/promoções sem tratamento</p></li>
</ul></td>
</tr>
<tr>
<td>Declarações</td>
<td colspan="2"><ul>
<li><p>Número de declarações por período; Tempo de entrega das
declarações; Tipo de declarações</p></li>
</ul></td>
</tr>
<tr>
<td><strong>Alerta</strong></td>
<td colspan="3">Permite listar varias alertas guardado na tabela
RH_T_ALERTA</td>
</tr>
<tr>
<td><strong>Notificação</strong></td>
<td colspan="3">Permite listar todas notificacaçoes guardadas na tabela
RH_T_NOTIFICACAO</td>
</tr>
<tr>
<td><strong>Pedido Declaração</strong></td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

# Especificação 

## Documentos 

### Registar Documento Exportados

<img src="media/image4.png" style="width:9.69097in;height:2.41111in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
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
<td style="text-align: left;">Documento Referente</td>
<td style="text-align: center;"><em>Select</em></td>
<td style="text-align: center;"><strong>RH_T_DOMINIO</strong>.REFERENCIA
onde <strong>DOMAINS</strong> = ORDEM_SERVICO_DECLARARACAO</td>
<td
style="text-align: center;">--------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Tipo documento</td>
<td style="text-align: center;">Select</td>
<td style="text-align: center;"><strong>DOMAINS</strong>=
ORDEM_SERVICO_DECLARARACAO</td>
<td
style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.TIPO_DOCUMENTO</td>
</tr>
<tr>
<td style="text-align: left;">Titulo / Assunto</td>
<td style="text-align: center;">TEXT</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.TITULO</td>
</tr>
<tr>
<td style="text-align: left;">Corpo</td>
<td style="text-align: center;">TEXTAREA</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.CORPO</td>
</tr>
<tr>
<td style="text-align: left;">Assinado Por</td>
<td style="text-align: center;">text</td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.RESPONSAVEL_ASSINATURA</td>
</tr>
<tr>
<td style="text-align: left;">Responsavel Assinatura</td>
<td style="text-align: center;">Select</td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.RESPONSAVEL_ID</td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td style="text-align: center;">Selet</td>
<td style="text-align: center;">DOMAINS = STATUS</td>
<td style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.ESTADO</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>ACÕES</strong></td>
</tr>
<tr>
<td style="text-align: center;">Gravar</td>
<td colspan="3" style="text-align: center;"><ol type="1">
<li><p><strong>Registar</strong></p>
<p>O sistema deve registar os dados introduzidos no formulário para a
tabelas <em><strong>RH_T_PARAM_DOC_OUTPUT</strong></em>, bem como os
campos adicionais especificados a seguir</p></li>
</ol>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em><strong>RESPONSAVEL_ID = ID de</strong></em>
RH_T_RESPONSAVEL</p></li>
</ul>
<ol start="2" type="1">
<li><p><strong>Editar</strong></p></li>
</ol>
<ul>
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE’</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Lista Documentos Output 

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
<td style="text-align: center;">Tipo documento</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"><strong>DOMAINS</strong>=
ORDEM_SERVICO_DECLARACAO</td>
<td
style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.TIPO_DOCUMENTO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Tipo Documento</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.TIPO_DOCUMENTO</td>
</tr>
<tr>
<td style="text-align: center;">Titulo / Assunto</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.TITULO</td>
</tr>
<tr>
<td style="text-align: center;">Assinado Por</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.ASSINATUDO_POR</td>
</tr>
<tr>
<td style="text-align: center;">Responsavel Assinatura</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_DOC_OUTPUT.RESPONVEL_ID</td>
</tr>
<tr>
<td style="text-align: center;">Acões</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Editar</td>
<td colspan="3" style="text-align: center;">Caso for editado , deve
inactivar e fazer novo registo</td>
</tr>
</tbody>
</table>

### Ordem Serviço

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 52%" />
<col style="width: 30%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Ordem Serviço</strong></td>
<td colspan="2" style="text-align: left;"><strong>Fonte de
dados</strong></td>
</tr>
<tr>
<td rowspan="5" style="text-align: center;">TRANVERSAL</td>
<td style="text-align: left;"><strong>ORDEM DE SERVIÇO N.º______/ CE
<mark>/2024</mark></strong></td>
<td style="text-align: left;">TO_CHAR(SYSDATE, ‘YYYY’)</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Assunto: <mark>Nomeação
</mark></strong></td>
<td style="text-align: left;">RH_T_PARAM_DOC_OUTPUT.TITULO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Instituto Nacional de Previdência
Social, aos <mark>27 de junho de 2024</mark></strong></td>
<td style="text-align: left;">SYSDATE</td>
</tr>
<tr>
<td style="text-align: center;"><mark>O Presidente da Comissão
Executiva</mark></td>
<td style="text-align: left;">RH_T_PARAM_DOC_OUTPUT.ASSINATUDO_POR</td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>/Mario Rui Lopes Fernandes
/</mark></strong></td>
<td style="text-align: left;">RH_T_PARAM_DOC_OUTPUT.RESPONVEL_ID</td>
</tr>
<tr>
<td style="text-align: center;">Conversão de Contratos -2025_
Modelo</td>
<td style="text-align: left;">A Comissão Executiva do INPS, na sua
reunião <mark>de 18 de fevereiro <sup>1</sup></mark> do ano corrente,
após análise da estrutura de contratos de trabalho dos colaboradores em
regime de contratos a prazo na data referenciada e a luz do código
laboral, vigente dos resultados das avaliações, dos perfis técnicos e
associados aos conhecimento adquiridos em sistema de proteção social, e
visando a consolidação e o reforço da estabilidade das equipas,
deliberou no sentido de converter o contrato de trabalho em termo
indeterminado, o colaborador <strong><mark>XXXXXXXXXX
<sup>2</sup></mark></strong> , com efeito a data de <mark>19 de março de
2025<sup>3</sup></mark>.</td>
<td style="text-align: left;"><p><mark><sup>1.</sup></mark></p>
<p><strong><mark><sup>2</sup></mark></strong></p>
<p><mark><sup>3</sup></mark></p></td>
</tr>
<tr>
<td style="text-align: center;">Ordem de Serviço - Licença sem
Vencimento</td>
<td style="text-align: left;"><p>A pedido da interessada e considerando
os fundamentos apresentados é concedido o pedido de licença sem
Retribuição, por um período de <mark>2 <strong>(dois</strong>)
<sup>1</sup></mark> meses, a <mark>Coordenadora<sup>2</sup></mark>
<strong><mark>XXXXXXXXX XX <sup>3</sup></mark></strong>, categoria
<mark>14 E <sup>4.</sup></mark></p>
<p>Esta Ordem de Serviço, produz efeito a partir do dia <mark>20 de maio
de 2025<sup>5</sup></mark>.</p></td>
<td style="text-align: left;"><p>1º
<strong>RH_V_RELACAO_LABORAL</strong>.DATA_INICIO_SITUACAO ,
<strong>RH_V_RELACAO_LABORAL.</strong>DATA_FIM_SITUACAO (<em>caso o
situação for licença sem vencimento</em>)</p>
<p>2º <strong>RH_V_RELACAO_LABORAL</strong>.CARGO_DESC</p>
<p>3º <strong>RH_V_RELACAO_LABORAL.</strong>NOME_COLABORADOR</p>
<p>4º <strong>RH_V_RELACAO_LABORAL.</strong>ESCALAO_DESC</p></td>
</tr>
<tr>
<td style="text-align: center;">Ordem de serviço de
Progressão_Cargo_Modelo</td>
<td style="text-align: left;"><p>Ao abrigo do art.º 23º do Plano de
Cargos, Carreiras e Salários (PCCS), é progredido no exercício do cargo,
os seguintes colaboradores:</p>
<ol type="1">
<li><p><strong><mark>XXXXXXXXX <sup>1</sup></mark></strong>,
<mark>Coordenador<sup>2</sup></mark> da Secção de <mark>Inscrição,
Cadastro e Direito</mark> – <mark>DCC<sup>3,</sup></mark> Referência e
Escalão <mark>14E<sup>4</sup></mark>, para <mark>14D<sup>5</sup></mark>,
com efeitos a partir de <mark>01 de outubro de
2025<sup>6</sup></mark>.</p></li>
</ol></td>
<td style="text-align: left;"><p>1º
<strong>RH_V_RELACAO_LABORAL.</strong>NOME_COLABORADOR</p>
<p>2º <strong>RH_V_RELACAO_LABORAL.</strong>CARGO_DESC</p>
<p>3ª</p></td>
</tr>
<tr>
<td style="text-align: center;">Ordem de serviço de
Progressão_Categoria</td>
<td style="text-align: left;"><p>Ao abrigo do art.º 23º do Plano de
Cargos, Carreiras e Salários (PCCS), são progredidos na categoria, os
seguintes colaboradores:</p>
<ol type="1">
<li><p><strong><mark>XXXXXXXX <sup>1</sup></mark></strong>,
<mark>Técnico Geral<sup>2</sup></mark> <mark>4C <sup>3</sup></mark>para
<mark>Técnico Geral<sup>4</sup></mark> <mark>4B<sup>5</sup></mark>, com
efeitos a partir <mark>de 1 de outubro de
2025<sup>5</sup></mark>;</p></li>
</ol></td>
<td style="text-align: left;"><p>1º
<strong>RH_V_RELACAO_LABORAL.</strong>NOME_COLABORADOR</p>
<p>2ª <strong>RH_V_RELACAO_LABORAL.</strong>CARGO_DESC</p>
<p>3º <strong>RH_V_RELACAO_LABORAL.</strong>ESCALAO_DESC</p></td>
</tr>
<tr>
<td style="text-align: center;">Ordem de serviço de substituição</td>
<td style="text-align: left;">É indicada a colaboradora
<strong><mark>XXXXXXXXX<sup>1</sup></mark>,</strong> <mark>Técnica
superior<sup>2</sup></mark> <mark>10C<sup>3 </sup></mark>afeto a Direção
das <mark>Prestações Definidas<sup>4</sup></mark>, para substituir a
<mark>Coordenadora<sup>5</sup></mark> da Secção de <mark>Pensões</mark>
<strong><mark>XXXXXXXXXXXXXXX<sup>6</sup></mark></strong>, durante a
ausência da mesma, por motivos de <mark>férias<sup>7</sup></mark>, com
início na data de <mark>21 de julho a 08 de agosto<sup>8</sup></mark> do
ano corrente.</td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: center;">Ordem de serviço de transferência</td>
<td style="text-align: left;"><p>A pedido do interessado e por
conveniência de serviço é transferido o colaborador
<strong><mark>XXXXXXXXX<sup>1</sup></mark></strong>, <mark>Técnico
Superio<sup>2</sup></mark> <mark>10D<sup>3</sup></mark>, da Direção de
<mark>Sistemas de Informação<sup>4</sup></mark>- Seção de
<mark>Desenvolvimento, para a mesma Direção<sup>5</sup></mark>, <mark>na
Ilha de São Vicente<sup>6.</sup></mark></p>
<p>Esta Ordem de Serviço, produz efeitos a partir de <mark>01 de
setembro de 2025<sup>7</sup></mark>, data que a colaboradora deve
comparecer no seu novo posto de trabalho.</p></td>
<td style="text-align: left;"><p>Esse ordem de serviço ºe dado quando ha
uma mobilidade regional (RH_T_MOBILIDADE.TIPO_SITUACAO =
MOBILIDADE_REGIONAL)</p>
<p>1º<strong>RH_V_RELACAO_LABORAL</strong>.NOME</p>
<p><strong>2º RH_V_RELACAO_LABORAL.</strong>CARGO_DESC</p>
<p>3º <strong>RH_V_RELACAO_LABORAL.</strong>ESCALAO_DESC</p>
<p>4º <strong>RH_V_RELACAO_LABORAL.</strong>DIRECAO_DESC</p>
<p>5º<strong>RH_V_RELACAO_LABORAL.</strong>SECCAO_DESC</p>
<p>6º <strong>RH_V_RELACAO_LABORAL</strong>.LOCAL_TRAB_ILHA</p>
<p>7º
<strong>RH_V_RELACAO_LABORAL</strong>.DATA_INICIO_MOBILIDADE</p></td>
</tr>
<tr>
<td style="text-align: center;">ORDEM DE SERVIÇO_ MOBILIDADE
INTERNA</td>
<td style="text-align: left;"><blockquote>
<p>Por conveniência de serviço e no âmbito das mobilidades interna, a
Comissão Executiva decidiu-se pela mobilidade do seguinte
colaborador:</p>
</blockquote>
<ol type="1">
<li><p><strong><mark>XXXXXXX<sup>1</sup></mark></strong>, <mark>Técnico
Geral<sup>2</sup></mark> <mark>6B<sup>3</sup></mark>, da Direção de
<mark>Prestações Definidadas<sup>4</sup></mark>, Seção de
<mark>Prestações Pecuniárias<sup>5</sup></mark>, para a Seção
<mark>Pensões<sup>6</sup></mark> <mark>da mesma
Direção<sup>7</sup></mark>.</p></li>
<li><p><mark><strong>XXXXXXXXXX<sup>1</sup></strong>,</mark>
<mark>Técnico Superior<sup>2</sup></mark> <mark>10F<sup>3</sup></mark>,
da Direção das <mark>Unidades de Previdência Social<sup>4</sup></mark>
da <mark>Ilha do Sal<sup>5</sup></mark>, para a Direção de
<mark>Assistência na Doença<sup>6</sup></mark>, Secção <mark>de
Liquidação de Prestações<sup>7</sup></mark>,
<mark>Norte<sup>8</sup></mark>;</p></li>
</ol></td>
<td style="text-align: left;"><p>Esse ordem de serviço ºe dado quando ha
uma mobilidade regional (RH_T_MOBILIDADE.TIPO_SITUACAO =
MOBILIDADE_INTERNA-SECAO, MOBILIDADE_INTERNA-DIRECAO )</p>
<p>1º<strong>RH_V_RELACAO_LABORAL</strong>.NOME</p>
<p><strong>2º RH_V_RELACAO_LABORAL.</strong>CARGO_DESC</p>
<p>3º <strong>RH_V_RELACAO_LABORAL.</strong>ESCALAO_DESC</p>
<p>4º <strong>RH_V_RELACAO_LABORAL.</strong>DIRECAO_DESC</p>
<p>5º<strong>RH_V_RELACAO_LABORAL.</strong>SECCAO_DESC</p></td>
</tr>
<tr>
<td style="text-align: center;">requalificacao</td>
<td style="text-align: left;"><p><em>Nota : o outro texto é
fixo</em></p>
<ol type="1">
<li><p><strong><mark>XXXXXXXXXXXXXXXXXXXX <sup>1</sup></mark>-</strong>
<mark>Técnico Geral Assistente<sup>2</sup></mark>
<mark>5D<sup>3</sup></mark> para <mark>Técnico
Superior<sup>4</sup></mark> <mark>10F<sup>5</sup></mark>;</p></li>
</ol></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: center;">Nomeação Coordenador</td>
<td style="text-align: left;"><p>É nomeado em regime de comissão de
serviço, a colaboradora
<mark><strong>XXXXXXXXXXXX<sup>1</sup></strong></mark>, <mark>Técnico
Superior Assistente<sup>2</sup></mark> <mark>10E<sup>3</sup></mark>,
para desempenhar a função de <mark>Coordenadora<sup>4</sup></mark> das
<mark>Unidades de Previdência Social<sup>5</sup></mark> das <mark>ilhas
do Fogo<sup>5</sup></mark> e <mark>Brava<sup>6</sup></mark>, afetas a
Direção das <mark>Unidades de Previdência Social
Sul<sup>7</sup></mark>.</p>
<p>Esta Ordem de Serviço, produz efeitos a partir do dia <mark>01 de
julho de 2024<sup>8</sup></mark>.</p></td>
<td
style="text-align: left;"><p>1º<strong>RH_V_RELACAO_LABORAL</strong>.NOME</p>
<p><strong>2º RH_V_RELACAO_LABORAL.</strong>CARGO_DESC</p></td>
</tr>
<tr>
<td style="text-align: center;">Fim de Comissão de Serviço</td>
<td style="text-align: left;"><p>A pedido do interessado, é dado por fim
de comissão de serviço, na Função de
<mark>Coordenador<sup>1</sup></mark> das <mark>Unidades de Previdência
Social<sup>2</sup></mark> das i<mark>lhas do Fogo<sup>3</sup></mark> e
<mark>Brava<sup>4</sup></mark>, afetas a Direção das <mark>Unidades de
Previdência Social Sul<sup>5,</sup></mark> o colaborador
<mark><strong>XXXXXXXXXX<sup>6</sup></strong></mark>, <mark>Técnico
Superior Assistente<sup>7</sup></mark> <mark>10E<sup>8</sup></mark>.</p>
<p>Esta Ordem de Serviço, produz efeitos a partir do dia <mark>30 de
junho de 2024<sup>9</sup></mark>.</p></td>
<td style="text-align: left;"></td>
</tr>
</tbody>
</table>

## Relatórios 

### Dossier do colaborador

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 4%" />
<col style="width: 45%" />
<col style="width: 31%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Filtro</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Filtros Genéricos Chave</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td style="text-align: center;"><p>Dossier Colaboradores</p>
<ul>
<li><p>Direção, Secção,Cargo,idade,Género, Faixa Etária, Local trabalho,
carreira, escalão, categoria, Antiguidade, Grau de escolaridade,
Mobilidade, Estrutura Remuneratório, vinculo, Situação Laboral</p></li>
</ul></td>
<td style="text-align: center;">Permitir filtrar por vários ou por
um</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Numero colaborador</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">COUNT(RH_T_FUNCIONRIOS)</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>Numero de
Colaborador por:</strong></td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">INPSSIGOF.INSTITUICOES.NOME</td>
<td style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Seção</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_SECAO.NOME</td>
<td style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.SECAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_PARAM_CARGO.NOME</td>
<td style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</td>
</tr>
<tr>
<td style="text-align: center;">IDADE</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_FUNCIONARIOS.DATA_NASCIMENTO</td>
</tr>
<tr>
<td style="text-align: center;">Género</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"><p>DEVE DEVOLVER</p>
<ul>
<li><p>Femenino e Maculino</p></li>
</ul></td>
<td style="text-align: center;">RH_T_FUNCIONARIOS.SEXO</td>
</tr>
<tr>
<td style="text-align: center;">Faixa Etária</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"><p>EX:</p>
<p>SELECT TRUNC(IDADE/10)*10 || ' - ' || (TRUNC(IDADE/10)*10 + 9) AS
FAIXA_ETARIA, COUNT(*) AS TOTAL FROM (SELECT GREATEST(0,
FLOOR(MONTHS_BETWEEN(SYSDATE, DATA_NASCIMENTO)/12)) AS IDADE FROM
RH_FUNCIONARIOS WHERE DATA_NASCIMENTO IS NOT NULL)</p>
<p>GROUP BY TRUNC(IDADE/10)</p>
<p>ORDER BY TRUNC(IDADE/10);</p></td>
<td style="text-align: center;">RH_T_FUNCIONARIOS.DATA_NASCIMENTO</td>
</tr>
<tr>
<td style="text-align: center;">Local trabalho</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_PARAM_LOCAL_TRAB.NOME</td>
<td
style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</td>
</tr>
<tr>
<td style="text-align: center;">Carreira</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_PARAM_CARREIRA.NOME</td>
<td
style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.CARREIRA_ID</td>
</tr>
<tr>
<td style="text-align: center;">Escalão</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">SELECT NIVEL_REFERENCIA||ESCALAO FROM
RH_T_PARAM_ESCALAO</td>
<td
style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Categoria</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_PARAM_CATEGORIA.NOME</td>
<td
style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.CATEGORIA_ID</td>
</tr>
<tr>
<td style="text-align: center;">Grau de Escolaridade</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_HABILITACOES_LITERARIAS.NIVEL</td>
<td style="text-align: center;">RH_T_HABILITACOES_LITERARIAS.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;">Mobilidade</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_MOBILIDADE.TIPO_SITUACAO</td>
<td style="text-align: center;">RH_T_MOBILIDADE.TIPO_SITUACAO</td>
</tr>
<tr>
<td style="text-align: center;">Estrutura Remuneratório</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_PARAM_CARREIRA.NOME
||RH_T_PARAM_ESCALAO.NIVEL_REFERENCIA||ESCALAO ||VALOR</td>
<td
style="text-align: center;"><p>RH_T_TIPOS_RELACIONAMENTO.CARREIRA_ID</p>
<p>RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID</p>
<p>RH_T_TIPOS_RELACIONAMENTO.SALARIO</p></td>
</tr>
<tr>
<td style="text-align: center;">Vinculo</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_PARAM_VINCULO.NOME</td>
<td style="text-align: center;">RH_T_CONTRATO_VINCULO.VINCULO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Antiguidade</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"><p>ORDENAR PELO MAIS ANTIGO.. EX:</p>
<p>SELECT FUN_ID, DATA_INICIO, FLOOR(MONTHS_BETWEEN(SYSDATE,
DATA_INICIO)/12) AS ANOS_ANTIGUIDADE</p>
<p>FROM RH_T_CONTRATO_VINCULO</p>
<p>ORDER BY DATA_INICIO ASC;</p></td>
<td style="text-align: center;">RH_T_CONTRATO_VINCULO.DATA_INICIO</td>
</tr>
<tr>
<td style="text-align: center;">Situação Laboral</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_SITUACAO.NOME</td>
<td
style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.SITUACAO_LABORAL_ID</td>
</tr>
</tbody>
</table>

### Assiduidade

<img src="media/image5.png" style="width:9.67708in;height:2.61875in" />

<table style="width:100%;">
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
<td style="text-align: center;">Direção</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td style="text-align: center;">INPSSIGOF.INSTITUICOES.NOME</td>
<td style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Seção</td>
<td style="text-align: center;"><em>SEELCT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.SECAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_FUNCIONARIOS.NOME</td>
</tr>
<tr>
<td style="text-align: center;">Tipo Assiduidade</td>
<td style="text-align: center;"><em>SEELCT</em></td>
<td style="text-align: center;"><p>Falta, Hora Extra, Dispensa,
Ferias</p>
<p><strong>DOMAINS=</strong> TIPO_ASSIDUIDADE</p></td>
<td style="text-align: center;">A tabela que deve listar dados depende
do tipo de assiduidade selecionado</td>
</tr>
<tr>
<td style="text-align: center;">Data Inicio</td>
<td style="text-align: center;"><em>DATE</em></td>
<td style="text-align: center;">Data inicio dependente tipo assiduidade
selecionado</td>
<td style="text-align: center;"><p>RH_T_DISPENSA.DATA OU</p>
<p>RH_T_FALTA.DATA_INICIO OU</p>
<p>RH_T_FERIAS_GOZADAS.DATA_INICIO OU</p>
<p>RH_T_HORA_EXTRA.DATA_INICIO</p></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td style="text-align: center;"><em>DATE</em></td>
<td style="text-align: center;">Data fim dependente tipo assiduidade
selecionado</td>
<td style="text-align: center;"><p>RH_T_DISPENSA.DATA OU</p>
<p>RH_T_FALTA.DATA_FIM OU</p>
<p>RH_T_FERIAS_GOZADAS.DATA_FIM OU</p>
<p>RH_T_HORA_EXTRA..DATA_FIM</p></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong>FERIAS</strong></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Nº de dias de férias</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_FERIAS_GOZADAS.NUM_DIA</td>
</tr>
<tr>
<td style="text-align: center;">Período Ferias (mês / ano)</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"><p>RH_T_FERIAS_GOZADAS.DATA_INICIO</p>
<p>RH_T_FERIAS_GOZADAS.DATA_FIM</p></td>
</tr>
<tr>
<td style="text-align: center;">Quem está de férias (num período)</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"><p>RH_T_FERIAS_GOZADAS.DATA_INICIO</p>
<p>RH_T_FERIAS_GOZADAS.DATA_FIM</p>
<p>RH_T_FERIAS_GOZADAS.FUN_ID</p></td>
</tr>
<tr>
<td style="text-align: center;">Férias suspensas / alteradas</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_FERIAS_GOZADAS.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;">Férias acumuladas de anos
anteriores</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">(RH_T_FERIAS.NUM_DIA ||
RH_T_FERIAS.ANO_REFERENTE )</td>
</tr>
<tr>
<td style="text-align: center;"><strong>FALTA</strong></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Nº de faltas</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"><strong>SUM</strong>
(RH_T_FALTA.NUM_FALTA)</td>
</tr>
<tr>
<td style="text-align: center;">Falta</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_FALTA.NUM_FALTA</td>
</tr>
<tr>
<td style="text-align: center;"><strong>HORA EXTRA</strong></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Nº de horas extras</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">SIM(RH_T_HORA_EXTRA.DURACAO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>DISPENSA</strong></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Horas de dispensas gozadas e por
gozar</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">Verifica horas de dispensa por mês
corrente , ou pelo período filtrado</td>
<td style="text-align: center;">RH_T_DISPENSA.TOTAL_HORA /
RH_T_DISPENSA.TOTAL_HORA -RH_ASSIDUIDADE_PARAMETRO. T_DISPENSA</td>
</tr>
</tbody>
</table>

####  Extrair ficheiro Efectividade 

<img src="media/image6.png" style="width:9.69306in;height:5.01319in" />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>Nome
Direc</strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">INPSSIGOF.INSTITUICOES.NOME</td>
<td style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Seção</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_SECAO.NOME</td>
<td style="text-align: center;">RH_T_TIPOS_RELACIONAMENTO.SECAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_T_FUNCIONARIOS.NOME</td>
<td style="text-align: center;">RH_T_FUNCIONARIOS.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;">Falta Justificadas</td>
<td style="text-align: center;"><em>NUMBER</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_FALTA.PARAM_SIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Faltas Injustificadas</td>
<td style="text-align: center;"><em>NUMBER</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_FALTA.PARAM_SIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Por Justificar</td>
<td style="text-align: center;"><em>NUMBER</em></td>
<td style="text-align: left;">Correspondem às faltas sobre as quais
ainda não foi realizada qualquer ação, quer para justificação, quer para
classificação como injustificadas.</td>
<td style="text-align: center;">Todas as faltas cometidas
(<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>) no mês que nao estao na
tabela <strong>RH_T_FALTA</strong></td>
</tr>
<tr>
<td style="text-align: center;">Licencas</td>
<td style="text-align: center;"><em>NUMBER</em></td>
<td style="text-align: center;">RH_T_PARAM_SITUACAO.NOME</td>
<td style="text-align: center;">RH_T_AUSENCIA.PARAM_SIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Total Ausencia</td>
<td style="text-align: center;"><em>NUMBER</em></td>
<td style="text-align: center;">SUM (<strong>Falta
justificacao</strong>, <strong>Injustificaca</strong>, <strong>Por
justificar</strong>)</td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Dispensa no mes (hora)</td>
<td style="text-align: center;"><em>NUMBER</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_DISPENSA.HORA_INICIO,
RH_T_DISPENSA.HORA_FIM,</td>
</tr>
</tbody>
</table>

##  Pedido de Declaração

## JOB Alerta 

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 25%" />
<col style="width: 58%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Refencia</strong></td>
<td><strong>DOMAIN: TIPO_ALERTA_NOTIFICACAO</strong></td>
<td><strong>JOB</strong></td>
</tr>
<tr>
<td rowspan="2" style="text-align: center;">Contrato</td>
<td><ul>
<li><p>Alerta sobre Renovação de contrato</p></li>
</ul></td>
<td><p><strong>Requisitos:</strong></p>
<p>O JOB dev verficiar todos contratos proximos a serem renovados, para
isso, deve ter em conta o seguinte:</p>
<ul>
<li><p>Tipo contato é renovável =
<strong>(RH_T_PARAM_CONTRATO.</strong>FLG_RENOVAVEL)</p></li>
<li><p>Estado contrato = <strong>Ativo
(RH_T_CONTRATO_VINCULO.</strong>ESTADO<strong>)</strong></p></li>
<li><p>Data Proxima =
<strong>RH_T_CONTRATO_VINCULO.</strong>DATA_INICIO</p></li>
<li><p>Prazo de alerta <strong>= RH_T_DOMAIN</strong>.CONFIGURACAO_PRAZO
<strong>(REFERENCIA=RENOVACAO)</strong></p>
<p><strong>Regra:</strong></p></li>
</ul>
<ul>
<li><p>Não Permitir duplicacao de alerta, ou seja o job deve verificar
se existe uma alerta registada</p></li>
<li><p>Deve ter em conta o numero de vezes que o contrato deve ser
renovado <strong>(RH_T_PARAM_CONTRATO.</strong>MAX_RENOVACAO)</p></li>
<li><p>Notificar ao RH</p>
<p><strong>Gravação</strong></p>
<p>Registo na tabela <strong>RH_T_ALERTA</strong></p>
<ul>
<li><p><strong>FUN_ID =</strong> RH_T_FUNCIONARIO.ID</p></li>
<li><p><strong>REFERENCIA</strong> = CONTRATO</p></li>
<li><p>REFERENCIA_NAME = ‘RH_T_CONTRATO_VINCULO’</p></li>
<li><p><strong>REFERENCIA_ID =</strong>
RH_T_CONTRATO_VINCULO.ID</p></li>
<li><p><strong>TIPO</strong> = RENOVACAO_CONTRATO</p></li>
<li><p><strong>ESTADO</strong> = P</p></li>
<li><p><strong>DESCRICAO</strong> = O Colaborador
(RH_T_FUNCIONARIO.NOME), tem um contrato (RH_T_PARAM_CONTRATO.NOME),
cuja duração é (RH_T_CONTRATO_VINCULO.DURACAO) com data inicio em
(RH_T_CONTRATO_VINCULO.DATA_INICIO), próximo a ser renovado.</p></li>
<li><p>Prioridade = <strong>ALTA</strong></p></li>
</ul></li>
</ul></td>
</tr>
<tr>
<td><ul>
<li><p>Conversao de contrato</p></li>
</ul></td>
<td><p>Apos 3 anos o contrato de tipo deteminado , deve ser convertido
para Contrato Indeterminado.</p>
<ul>
<li><p>Prazo de alerta <strong>= RH_T_DOMAIN</strong>.CONFIGURACAO_PRAZO
<strong>(REFERENCIA=CONVERSAO)</strong></p>
<p><strong>Gravação</strong></p>
<p>Registo na tabela <strong>RH_T_ALERTA</strong></p></li>
</ul>
<ul>
<li><p><strong>FUN_ID =</strong> RH_T_FUNCIONARIO.ID</p></li>
<li><p><strong>REFERENCIA</strong> = CONTRATO</p></li>
<li><p>REFERENCIA_NAME = ‘RH_T_CONTRATO_VINCULO’</p></li>
<li><p><strong>REFERENCIA_ID =</strong>
RH_T_CONTRATO_VINCULO.ID</p></li>
<li><p><strong>TIPO</strong> = CONVERSAO_CONTRATO</p></li>
<li><p><strong>ESTADO</strong> = P</p></li>
<li><p><strong>DESCRICAO</strong> = O Colaborador
(RH_T_FUNCIONARIO.NOME), tem um contrato (RH_T_PARAM_CONTRATO.NOME),
cuja data inicio do primeiro contrato é
(RH_T_CONTRATO_VINCULO.DATA_INICIO), Já completou (calcular o numero de
anos no vinculo ), o seu contrato pode ser convertido</p></li>
<li><p>Prioridade = <strong>ALTA</strong></p></li>
</ul></td>
</tr>
<tr>
<td rowspan="2" style="text-align: center;">Licença sem Vencimento</td>
<td>Licença a terminar nos próximos 15 / 30 dias</td>
<td><p>Verifica todo pessoal que seu ultimo é licença sem vencimento,
cuja data ainda não expirou
(RH_T_TIPOS_RELACIONAMENTO.SITUACAO_LABORAL_ID e EST_ACT_ADM = 1)</p>
<p>Quanto este proximo do prazo estipulado:</p>
<ul>
<li><p>Prazo de alerta <strong>= RH_T_DOMAIN</strong>.CONFIGURACAO_PRAZO
<strong>(REFERENCIA=</strong>LICENCA_S_VENCIMENTO<strong>)</strong></p>
<p><strong>Gravação</strong></p>
<p>Registo na tabela <strong>RH_T_ALERTA</strong></p></li>
</ul>
<ul>
<li><p><strong>FUN_ID =</strong> RH_T_FUNCIONARIO.ID</p></li>
<li><p>REFERENCIA = LICENCA</p></li>
<li><p>REFERENCIA_NAME = ‘RH_T_SITUACAO_LABORA’</p></li>
<li><p><strong>REFERENCIA_ID =</strong> RH_T_SITUACAO_LABORA.ID</p></li>
<li><p><strong>TIPO</strong> = LICENCA_S_VENCIMENTO</p></li>
<li><p><strong>ESTADO</strong> = P</p></li>
<li><p><strong>DESCRICAO</strong> = O Colaborador
(RH_T_FUNCIONARIO.NOME), tem uma licença sem vencimento proxima de
expirar (Data inicio: RH_T_SITUACAO_LABORA.DATA_INICIO e Data FIM:
RH_T_SITUACAO_LABORA.DATA_FIM)</p></li>
<li><p>Prioridade = MEDIA</p></li>
</ul></td>
</tr>
<tr>
<td>Licença ultrapassou o prazo estipulado</td>
<td><p>Verifica todo pessoal que seu ultimo é licença sem vencimento,
cuja a data ja expirou (RH_T_TIPOS_RELACIONAMENTO.SITUACAO_LABORAL_ID e
EST_ACT_ADM = 1)</p>
<p><strong>Gravação</strong></p>
<p>Registo na tabela <strong>RH_T_ALERTA</strong></p>
<ul>
<li><p><strong>FUN_ID =</strong> RH_T_FUNCIONARIO.ID</p></li>
<li><p>REFERENCIA = LICENCA</p></li>
<li><p>REFERENCIA_NAME = ‘RH_T_SITUACAO_LABORA’</p></li>
<li><p><strong>REFERENCIA_ID =</strong> RH_T_SITUACAO_LABORA.ID</p></li>
<li><p><strong>TIPO_ALERTA</strong> = LICENCA_S_VENCIMENTO</p></li>
<li><p><strong>ESTADO</strong> = P</p></li>
<li><p><strong>DESCRICAO</strong> = O Colaborador
(RH_T_FUNCIONARIO.NOME), tem uma Licença sem vencimento expirado(Data
inicio: RH_T_SITUACAO_LABORA.DATA_INICIO e Data FIM:
RH_T_SITUACAO_LABORA.DATA_FIM)</p></li>
<li><p>Prioridade = MEDIA</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;">Doença</td>
<td>Indicar quando uma data esta proximo</td>
<td><p>Verifica todos os colaboradores com RH_T_ABONOS_BENEFICIOS, cuja
data fim esteja proxima a atingir o prazo</p>
<p>Quanto este proximo do prazo estipulado:</p>
<ul>
<li><p>Prazo de alerta <strong>= RH_T_DOMAIN</strong>.CONFIGURACAO_PRAZO
<strong>(REFERENCIA=</strong>LICENCA_COM_VENCIMENTO<strong>)</strong></p>
<p><strong>Gravação</strong></p>
<p>Registo na tabela <strong>RH_T_ALERTA</strong></p></li>
</ul>
<ul>
<li><p><strong>FUN_ID =</strong> RH_T_FUNCIONARIO.ID</p></li>
<li><p>REFERENCIA = LICENCA</p></li>
<li><p>REFERENCIA_NAME = ‘RH_T_SITUACAO_LABORA’</p></li>
<li><p><strong>REFERENCIA_ID =</strong> RH_T_SITUACAO_LABORA.ID</p></li>
<li><p><strong>TIPO</strong> = LICENCA_C_VENCIMENTO</p></li>
<li><p><strong>ESTADO</strong> = P</p></li>
<li><p><strong>DESCRICAO</strong> = O Colaborador
(RH_T_FUNCIONARIO.NOME), tem uma licença (RH_T_PARAM_SITUACAO.NOME)
proxima de expirar (Data inicio: RH_T_SITUACAO_LABORA.DATA_INICIO e Data
FIM: RH_T_SITUACAO_LABORA.DATA_FIM)</p></li>
</ul>
<p>Prioridade = MEDIA</p></td>
</tr>
<tr>
<td rowspan="4" style="text-align: center;">Empréstimo</td>
<td>Emprestimo com Pagamento Atrazado</td>
<td>O sistema deve alerta</td>
</tr>
<tr>
<td>Prestação não descontada no salário</td>
<td></td>
</tr>
<tr>
<td>Empréstimo liquidado mas desconto continua ativo</td>
<td></td>
</tr>
<tr>
<td>Funcionário cessado com empréstimo em aberto</td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Missão Serviço</td>
<td><ul>
<li><p>Faturas não submetidas após X dias</p></li>
<li><p>Missão solicitada sem cabimentação</p></li>
<li><p>Missão em período de férias/licença</p></li>
</ul></td>
<td></td>
</tr>
</tbody>
</table>

#### Envio de email 

set SERVEROUTPUT ON;

declare

v_ok varchar2(100);

begin

sipsv0.SEND_MAIL_V1(

p_from_name =\>'inps.notificacao@govcv.gov.cv',

p_to_name =\>'carlos.j.monteiro@inps.cv',--Emanuel.M.Furtado@inps.cv',

p_subject =\>'New teste send email STG',

p_message_body =\>'DEV',

p_cc_name =\> '',

--p_attachments GLB_TIPOS.array_attachments DEFAULT NULL,

p_message_type =\> 'text/plain'

) ;

end;

|  |  |  |  |
|:--:|:--:|:--:|:--:|
| **Filtro** | **Tipo** | **Descrição** | **Fonte dados** |
| Referencia | *SELECT* | DOMAIN.REFERENCIA de DOMINIO TIPO_ALERTA | RH_T_ALERTA.REFERENCIA |
| Tipo alerta | *SELECT* | TIPO = **TIPO_ALERTA_NOTIFICACAO** | RH_T_ALERTA.TIPO_ALERTA |
| Nome colaborador | *TEXT* |  | RH_T_ALERTA.FUN_ID |
| Direção | *SELECT* |  | RH_T_MOBILIDADE.INSTIT_ID |
| Seccão | *SELECT* |  | RH_T_MOBILIDADE.SECAO_ID |
| Estado | *SELECT* |  | RH_T_ALERTA.ESTADO |
| Data Registo De: | *DATE* |  | RH_T_ALERTA.DATA.REGISTO |
| Data Registo Até | *DATE* |  | RH_T_ALERTA.DATA.REGISTO |
| **Lista** | **Tipo** | **Descrição** | **Fonte dados** |
| Estado |  |  | RH_T_ALERTA.ESTADO |
| Referencia |  |  | RH_T_ALERTA.REFERENCIA |
| Tipo Alerta |  |  | RH_T_ALERTA.TIPO_ALERTA |
| Colaborador |  |  | RH_T_ALERTA.FUN_ID |
| Secção |  |  | RH_T_MOBILIDADE.INSTIT_ID |
| Descrição |  |  | RH_T_ALERTA.DESCRICAO |
| Data Registo |  |  | RH_T_ALERTA.DATA_REGISTO |
| Notificado? |  |  | RH_T_ALERTA.FLG_NOTIFICACAO |
| Selecionar | *Check List* |  | RH_T_ALERTA |
| AÇÕES |  |  |  |
| Ver Notificação |  | Caso a alerta gerou uma notificação, logo deve abrir a lista de notificacao |  |
| Processar |  | Abre o formulario da funcionalidade que gerou a alerta |  |

### Processar 

1)  **<u>Tipo_alerta Renovavao</u>:** Caso o tipo de alerta for
    renovação do contrato : o sistema deve permitir renovar um grupo de
    colabodores ou individualmente. Deve abrir um formulario onde
    permite mudar duração do contrato

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
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
<td style="text-align: left;">Data Inicio</td>
<td style="text-align: center;"><em>DATE</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_CONTRATO_VINCULO.DATA_INICIO</td>
</tr>
<tr>
<td style="text-align: left;">Duração</td>
<td style="text-align: center;">NUMBER</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_CONTRATO_VINCULO.DURACAO</td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td style="text-align: center;">DATE</td>
<td style="text-align: center;">Ao colocar Data inicio e duacao o
sistema calcula automaticamente Data Fim</td>
<td style="text-align: center;">RH_T_CONTRATO_VINCULO.DATA_FIM</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>ACÕES</strong></td>
</tr>
<tr>
<td style="text-align: center;">GRAVAR</td>
<td colspan="3" style="text-align: center;">Nota: Especificação descrito
no documeto Dossier do colaborador na linea 3.5.2.1.4Renovacao</td>
</tr>
</tbody>
</table>

2)  **<u>Tipo_alerta- conversao contrato</u>**

    Abre o mesmo formulario de registo de relaçao laboral , descritos em
    **DOSSIER DO COLABORADOR** 3.5.2.5.1Editar / Novo Relação Laboral

3)  **<u>Tipo alerta Licença sem vencimento</u>**

    Abre o formulario de registo Editar / Licença Sem Vencimento
    descritos no Doc. **PROCESSAMENTO SALARIAL** 3.4 Licença sem
    Vencimento

4)  **<u>Tipo_alerta Licença com vencimento</u>**

    Abre o formulario de editar / registo de abonos e beneficios
    descritos no Doc. **PROCESSAMENTO SALARIAL** 3.3.1 Lista Abonos e
    Beneficios

## Notificação 

### Registar “Tipo Notificação“

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
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
<td style="text-align: left;">Tipo Notificacao</td>
<td style="text-align: center;">SELECT</td>
<td style="text-align: center;">DOMAINS =
<strong>TIPO_ALERTA_NOTIFICACAO</strong></td>
<td
style="text-align: center;">RH_T_PARAM_NOTIFICACAO.TIPO_NOTIFICACAO</td>
</tr>
<tr>
<td style="text-align: left;">Assunto</td>
<td style="text-align: center;">TEXT</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_NOTIFICACAO.ASUNTO</td>
</tr>
<tr>
<td style="text-align: left;">Corpo</td>
<td style="text-align: center;">TEXTAREA</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_NOTIFICACAO.CORPO</td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td style="text-align: center;">Selet</td>
<td style="text-align: center;"><strong>DOMAINS</strong> = STATUS</td>
<td style="text-align: center;">RH_T_PARAM_NOTIFICACAO.ESTADO</td>
</tr>
<tr>
<td style="text-align: left;">Ações</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Gravar</td>
<td colspan="3" style="text-align: center;"><ol start="3" type="1">
<li><p><strong>Registar</strong></p>
<p>O sistema deve registar os dados introduzidos no formulário para a
tabelas <em><strong>RH_T_PARAM_NOTIFICACAO</strong></em>, bem como os
campos adicionais especificados a seguir</p></li>
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
<ol start="4" type="1">
<li><p><strong>Editar</strong></p></li>
</ol>
<ul>
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE’</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
</ul>
<p><em><strong>Nota</strong>: cada vez que for editado deve fazer umnovo
registo</em></p></td>
</tr>
</tbody>
</table>

### Lista “Tipo Notificação”

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
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
<td style="text-align: left;">Tipo Notificacao</td>
<td style="text-align: center;">SELECT</td>
<td style="text-align: center;">DOMAINS =
<strong>TIPO_ALERTA_NOTIFICACAO</strong></td>
<td
style="text-align: center;">RH_T_PARAM_NOTIFICACAO.TIPO_NOTIFICACAO</td>
</tr>
<tr>
<td style="text-align: left;">Assunto</td>
<td style="text-align: center;">TEXT</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_NOTIFICACAO.ASUNTO</td>
</tr>
<tr>
<td style="text-align: left;">Corpo</td>
<td style="text-align: center;">TEXTAREA</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_NOTIFICACAO.CORPO</td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td style="text-align: center;">Selet</td>
<td style="text-align: center;"><strong>DOMAINS</strong> = STATUS</td>
<td style="text-align: center;">RH_T_PARAM_NOTIFICACAO.ESTADO</td>
</tr>
<tr>
<td style="text-align: left;">Ações</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Gravar</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"><ol start="5" type="1">
<li><p><strong>Registar</strong></p>
<p>O sistema deve registar os dados introduzidos no formulário para a
tabelas <em><strong>RH_T_PARAM_NOTIFICACAO</strong></em>, bem como os
campos adicionais especificados a seguir</p></li>
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
<ol start="6" type="1">
<li><p><strong>Editar</strong></p></li>
</ol>
<ul>
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE’</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p>
<p><em><strong>Nota</strong>: cada vez que for editado deve fazer umnovo
registo</em></p></li>
</ul></td>
<td style="text-align: center;"></td>
</tr>
</tbody>
</table>

### Lista de Notificacao

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 7%" />
<col style="width: 37%" />
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
<td style="text-align: center;">Tipo Notificação</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td style="text-align: center;">TIPO =
<strong>TIPO_ALERTA_NOTIFICACAO</strong></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.TIPO_NOTIFICACAO</td>
</tr>
<tr>
<td style="text-align: center;">Data Envio De:</td>
<td style="text-align: center;"><em>DATE</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.DATA_REGISTO</td>
</tr>
<tr>
<td style="text-align: center;">Data Envio Até:</td>
<td style="text-align: center;"><em>DATE</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.DATA_REGISTO</td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td style="text-align: center;"><p><strong>DOMIAN</strong> =
ESTADO_NOTIFICACAO</p>
<ul>
<li><p>Enviado</p></li>
<li><p>Por enviar</p></li>
</ul></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Assunto</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.ASSUNTO</td>
</tr>
<tr>
<td style="text-align: center;">Nome Receptor</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">O receptor pode ser um nome de
funcionario, deirecao ou secao</td>
<td
style="text-align: center;">RH_T_NOTIFICACAO.<em>NOME_RECEPTOR</em></td>
</tr>
<tr>
<td style="text-align: center;"><em>Email</em></td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.EMAIL</td>
</tr>
<tr>
<td style="text-align: center;"><em>Data Envio</em></td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.DATA_REGISTO</td>
</tr>
<tr>
<td style="text-align: center;"><em>Estado</em></td>
<td style="text-align: center;"><em>SELECT</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;"><em>Seleccionar</em></td>
<td style="text-align: center;"><em>CHECKLIST</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Enviar / Reenviar</td>
<td colspan="3" style="text-align: center;">Permite Reenviar Email</td>
</tr>
<tr>
<td style="text-align: center;">Ver Detalhe</td>
<td colspan="3" style="text-align: center;"></td>
</tr>
</tbody>
</table>

####  Ver Detalhe 

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
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
<td style="text-align: left;">Tipo Notificacao</td>
<td style="text-align: center;">SELECT</td>
<td style="text-align: center;">DOMAINS =
<strong>TIPO_ALERTA_NOTIFICACAO</strong></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.TIPO_NOTIFICACAO</td>
</tr>
<tr>
<td style="text-align: left;">Assunto</td>
<td style="text-align: center;">TEXT</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.ASUNTO</td>
</tr>
<tr>
<td style="text-align: left;">Corpo</td>
<td style="text-align: center;">TEXTAREA</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.CORPO</td>
</tr>
<tr>
<td style="text-align: left;">Receptor</td>
<td style="text-align: center;">Nome</td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_NOTIFICACAO.<em>NOME_RECEPTOR</em></td>
</tr>
<tr>
<td style="text-align: left;">Email</td>
<td style="text-align: center;">EMIAL</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_NOTIFICACAO.EMAIL</td>
</tr>
<tr>
<td style="text-align: left;">Ações</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Enviar / Reenviar</td>
<td colspan="3" style="text-align: center;">Atualiza os dados; caso haja
alguma alteração, e invoca o serviço de envio de e-mail.</td>
</tr>
</tbody>
</table>

## Pedido de Declaracao

### Registar Pedido

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 11%" />
<col style="width: 19%" />
<col style="width: 17%" />
<col style="width: 32%" />
</colgroup>
<tbody>
<tr>
<td><blockquote>
<p><strong>Formulario</strong></p>
</blockquote></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td colspan="2"
style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Gravação</strong></td>
</tr>
<tr>
<td style="text-align: left;">Nome Requerente</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td colspan="2" style="text-align: center;">PESQUISA DO COLABORADOR</td>
<td style="text-align: center;"><p><em>RH_T_PEDIDO.FUN_ID</em></p>
<p><em>RH_T_DECLARACAO.TIPREL_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Declaracao</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"><strong>DOMAINS</strong>=
ORDEM_SERVICO_DECLARACAO onde referencia = DECLARACAO</td>
<td style="text-align: center;"><p><em>RH_T_PEDIDO.TIPO_PEDIDO=
‘<strong>DECLARACAO</strong>’</em></p>
<p><em>RH_T_DECLARACAO.TIPO_DECLARACAO</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Informações do
Pedido</strong></td>
<td style="text-align: center;"><em>Separador Lista</em></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Finalidade</td>
<td style="text-align: center;"><em>SELECTY</em></td>
<td colspan="2" style="text-align: center;">DOMAINS =
FINALIDADE_DECLARACAO</td>
<td style="text-align: center;"><em>RH_T_DECLARACAO.FINALIDADE</em></td>
</tr>
<tr>
<td style="text-align: left;">Entidade destinatária</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="2" style="text-align: center;"></td>
<td
style="text-align: center;"><em>RH_T_DECLARACAO.ENTIDADE_DESTINADO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data pedido</td>
<td style="text-align: center;"><em>DATE</em></td>
<td colspan="2" style="text-align: center;"></td>
<td
style="text-align: center;"><em>RH_T_DECLARACAO.DATA_PEDIDO</em></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td style="text-align: center;"><em>TEXTAREA</em></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;"><em>RH_T_DECLARACAO.OBS</em></td>
</tr>
<tr>
<td style="text-align: left;">Analise Pedido</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;">Em conformidade</td>
<td style="text-align: center;"><em>SELECT</em></td>
<td colspan="2" style="text-align: center;">DOMAIN = SIM_NAO</td>
<td style="text-align: center;"><p><em>RH_T_PEDIDO.ETAPA =
‘Analise’</em></p>
<p><em>RH_T_DECLARACAO.DECISAO_ANALISE</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td style="text-align: center;"><em>TEXTAREA</em></td>
<td colspan="2" style="text-align: center;"></td>
<td
style="text-align: center;"><em>RH_T_DECLARACAO.OBS_ANALISE</em></td>
</tr>
<tr>
<td style="text-align: left;">Visualizar Declaracao</td>
<td style="text-align: center;"><em>hyperlink</em></td>
<td colspan="2" style="text-align: center;">Estando na etapa de pedido,
o utilizador pode pré-visualizar a declaração com a marca de água “Não
Válido”. Caso esteja na etapa de validação, pode visualizá-la sem marca
de água.</td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Validação Pedido</strong></td>
<td colspan="4" style="text-align: center;"><em>Aparece somente na Etapa
Validação</em></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td style="text-align: center;"><em>Radio List</em></td>
<td colspan="2" style="text-align: center;"><p>Isso aparece somente na
etapa Validacao</p>
<p>DOMAIN = SIM_NAO</p></td>
<td style="text-align: center;"><p><em>RH_T_PEDIDO.ESTADO =
‘<strong>P</strong>’</em></p>
<p><em>RH_T_PEDIDO.ETAPA = ‘<strong>Validação RH</strong>’</em></p>
<p><em>RH_T_DECLARACAO.</em>DECISAO_RH</p></td>
</tr>
<tr>
<td style="text-align: left;">Entrega por email</td>
<td style="text-align: center;"><em>RADIOLIST</em></td>
<td colspan="2" style="text-align: center;"><p>DOMAIN = SIM_NAO</p>
<p>Caso o entrega for por email, for sim, o colaborador recebe uma
notificação com a declaracao, caso contrario recebe notificação sem
declaracao</p></td>
<td style="text-align: center;"><p><em>RH_T_DECLARACAO.ENTREGA</em></p>
<p><em>RH_T_NOTIFICACAO.FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">ANEXO</td>
<td colspan="4" style="text-align: center;">se necessário
comprovativos</td>
</tr>
<tr>
<td style="text-align: left;">Tipo Documento</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;">RH_T_TIPOS_DOCUMENTO , ONDE
REFERENCIA = PEDIDO_DECLARACAO</td>
<td rowspan="2"
style="text-align: left;"><p><em>RH_T_DOCUMENTO.TP_DOCUMENTO_ID</em></p>
<p><em>RH_T_DOCUMENTO.DOC_ID</em></p>
<p><em>RH_T_DOCUMENTO.FUN_ID</em></p>
<p><em>RH_T_DOCUMENTO.REFERENCIA_NAME= ´RH_T_DECLARACAO´</em></p>
<p><em>RH_T_DOCUMENTO.REFERENCIA_ID = ID de
RH_T_DECLARACAO.ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Documento</td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
</tr>
<tr>
<td colspan="5"><strong>ACÇOES</strong></td>
</tr>
<tr>
<td colspan="3"><p><em>GRAVACAO:</em></p>
<p><em>Registo nas seguintes tabelas:</em></p>
<ul>
<li><p><em><strong>RH_T_PEDIDO</strong></em></p>
<ul>
<li><p><em>DATA_PEDIDO</em></p></li>
<li><p><em>DATA_REGISTO</em></p></li>
<li><p><em>USER_REGISTO</em></p></li>
<li><p><em>ORIGEM = ‘<strong>RH’</strong></em></p></li>
</ul></li>
<li><p><em><strong>RH_T_DECLARACAO</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO</em></p></li>
<li><p><em>USER_REGISTO</em></p></li>
<li><p><em>PEDIDO_ID</em></p></li>
</ul></li>
</ul>
<p>Parte inferior do formulário</p></td>
<td colspan="2"
style="text-align: center;"><p><strong>RH_T_NOTIFICACAO</strong></p>
<ul>
<li><p><em>REFERENCIA</em></p></li>
<li><p><em>MESSAGE</em></p></li>
<li><p><em>ASSUNTO</em></p></li>
<li><p><em>EMAIL =</em></p></li>
<li><p><em>NOME_RECEPTOR</em></p></li>
<li><p><em>DATA_ENVIO</em></p></li>
<li><p><em>URL</em></p></li>
<li><p><em>DATA_REGISTO</em></p></li>
<li><p><em>USER_REGISTO_ID</em></p></li>
<li><p><em>USER_REGISTO_NAME</em></p></li>
<li><p><em>REFERENCIA_NOME: ‘RH_T_PEDIDO’</em></p></li>
<li><p><em>REFERENCIA_ID = ID de RH_T_PEDIDO</em></p></li>
<li><p><em>FUN_ID</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Lista Pedido declaracao

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 7%" />
<col style="width: 37%" />
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
<td style="text-align: center;">Tipo Declaracao</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td
style="text-align: center;">RH_T_PEDIDO_DECLARACAO.TIPO_DECLARACAO</td>
</tr>
<tr>
<td style="text-align: center;">Data Pedido De:</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PEDIDO.DATA_INICIO</td>
</tr>
<tr>
<td style="text-align: center;">Data Pedido Até:</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PEDIDO.DATA_INICIO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Tipo Declaracao</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"><strong>DOMAINS</strong>=
ORDEM_SERVICO_DECLARACAO</td>
<td
style="text-align: center;">RH_T_PEDIDO_DECLARACAO.TIPO_DECLARACAO</td>
</tr>
<tr>
<td style="text-align: center;">Efeito</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PEDIDO_DECLARACAO.EFEITO</td>
</tr>
<tr>
<td style="text-align: center;">Data Pedido</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PEDIDO.DATA_INICIO</td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PEDID.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;">Direccão</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">INSPSIGOF.INSTITUICOES.NOME</td>
<td style="text-align: center;">RH_T_PEDIDO_DECLARACA.TIPREL_ID,
RH_T_TIPOS_RELACIONAMENTO.MOB_ID, RH_T_MOBILIDADE.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: center;">Seccão</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_SECAO.NOME</td>
<td style="text-align: center;">RH_T_PEDIDO_DECLARACA.TIPREL_ID,
RH_T_TIPOS_RELACIONAMENTO.MOB_ID, RH_T_MOBILIDADE.SECAO_ID</td>
</tr>
<tr>
<td style="text-align: center;">vinculo</td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PARAM_VINCULO.VINCULO_ID</td>
<td style="text-align: center;">RH_T_PEDIDO_DECLARACA.TIPREL_ID,
RH_T_TIPOS_RELACIONAMENTO.CONTRATO_VINCULO_ID,
RH_T_CONTRATO_VINCULO.VINCULO_ID</td>
</tr>
<tr>
<td style="text-align: center;"><em>Estado Pedido</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PEDIDO_DECLARACAO.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;"><em>Etapa</em></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;">RH_T_PEDIDO.ETAPA</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;">Ve Pedido</td>
<td colspan="3" style="text-align: center;">Abre o formulario de pedido
de declaracao</td>
</tr>
<tr>
<td style="text-align: center;">Ver Declaracao</td>
<td colspan="3" style="text-align: center;">Caso o pedido ainda não
esteja na etapa de validação, o utilizador apenas poderá visualizar o
documento, não sendo permitida a sua impressão. Nessa situação, a
declaração deverá conter a marca de água “Não Válido”.</td>
</tr>
<tr>
<td style="text-align: center;">Ver notificacao</td>
<td colspan="3" style="text-align: center;">Permite visualizar
notificação associado a esse pedido</td>
</tr>
</tbody>
</table>

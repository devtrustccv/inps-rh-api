<figure>
<img src="media/image1.jpeg" style="width:14.65694in;height:9.77083in"
alt="C:\Users\joelm\Desktop\Imagens\sergey-zolkin-_UeY8aTI6d0-unsplash (2).jpg" />
<figcaption><p>SIPS-RH</p></figcaption>
</figure>

**PROCESSAMENTO SALARIAL**

# Enquadramento 

Este documento apresenta todas as funcionalidades abrangidas pelo módulo
de processamento salarial, descrevendo de forma objetiva as ações,
fluxos e operações necessárias para o cálculo, validação, controlo
financeiro e emissão de relatórios relacionados à folha de pagamento.

# Âmbito 

<img src="media/image4.png" style="width:9.25in;height:4.12569in"
alt="Uma imagem com texto, diagrama, captura de ecrã, file Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Funcionalidade</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Lista Processamento</td>
<td>Lista dados os processamentos efetuados</td>
</tr>
<tr>
<td><ul>
<li><p>Processar</p></li>
</ul></td>
<td><p>Execução do cálculo mensal da remuneração dos colaboradores,
contemplando salários base, subsídios, benefícios, descontos
obrigatórios e facultativos, bem como a aplicação de impostos conforme a
legislação vigente.</p>
<p>O sistema permite processar os seguintes tipos:</p>
<ul>
<li><p><strong>Salário Mensal:</strong> Não engloba pessoal em
projeto</p></li>
<li><p><strong>Projetos</strong>: projetos é processado separado dos
outros tipos de contratos</p></li>
<li><p><strong>Subsidio</strong> <strong>Natal</strong>: não engloba
falta</p></li>
<li><p><strong>Subsidio Ferias</strong>: não Engloba falta</p></li>
</ul></td>
</tr>
<tr>
<td><ul>
<li><p>Eliminar Proc</p></li>
</ul></td>
<td>Permite remover um processamento salarial que ainda não tenha sido
previamente validado, garantindo que apenas processamentos confirmados
permaneçam registados no sistema.</td>
</tr>
<tr>
<td><ul>
<li><p>Validar</p></li>
</ul></td>
<td>Confirma e consolida o processamento salarial realizado, bloqueando
alterações posteriores e atualizando os respetivos registos para efeitos
de pagamento, contabilização e histórico.</td>
</tr>
<tr>
<td><ul>
<li><p>Cabimentar</p></li>
</ul></td>
<td>Permite realizar automaticamente o cabimento orçamental através da
invocação de um serviço do sistema financeiro, assegurando a reserva dos
recursos necessários para o pagamento da folha salarial.</td>
</tr>
<tr>
<td><ul>
<li><p>Eliminar Cab.</p></li>
</ul></td>
<td>Permite remover um cabimento que ainda não tenha sido autorizado,
mediante a invocação do serviço correspondente no sistema
financeiro.</td>
</tr>
<tr>
<td><ul>
<li><p>Autorizar</p></li>
</ul></td>
<td>Permite autorizar o cabimento previamente efetuado, através da
invocação do serviço correspondente no sistema financeiro</td>
</tr>
<tr>
<td><ul>
<li><p>Relatorio</p></li>
</ul></td>
<td><p>Permite extrair um conjunto de relatórios relacionados ao
processamento salarial, incluindo informações detalhadas sobre
remunerações, descontos, benefícios e demais componentes da folha.</p>
<ul>
<li><p>Lista de Recibos para Tesouraria</p></li>
<li><p>Recibo do Pagamento de Salário para o colaborador</p></li>
<li><p>Listagem Pagamentos por Banco</p></li>
<li><p>Resumo para Contabilidade</p></li>
<li><p>Listagem Pagamentos de Beneficiários por Banco</p></li>
</ul></td>
</tr>
<tr>
<td><p>Marcar N/ Processar</p>
<p>Desmarcar Para Processar</p></td>
<td>Esta funcionalidade é útil quando há necessidade de excluir
determinados funcionários ou períodos da folha de pagamento, seja por
licenças não remuneradas, afastamentos temporários ou outros
motivos.</td>
</tr>
<tr>
<td>LICENÇA SEM VENCIMENTO</td>
<td>Permite listar todos os colaboradores que se encontram em licença
sem vencimento, bem como registar novas licenças, atualizar o respetivo
período e ajustar automaticamente a situação laboral do colaborador no
sistema.</td>
</tr>
<tr>
<td>IMPORTAR DESCONTO</td>
<td>Permite importar dados de cantina e outros para</td>
</tr>
<tr>
<td>FOS</td>
<td>Envio de folha salarial para sistema core de INPS</td>
</tr>
<tr>
<td>SOAT</td>
<td>Permite listar e exportar dados em formato Excel a segurador</td>
</tr>
</tbody>
</table>

# Especificação 

## Desmarcar Para Processar

<img src="media/image5.png" style="width:8.65625in;height:3.84792in"
alt="Uma imagem com texto, software, número, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 6%" />
<col style="width: 37%" />
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
<td style="text-align: left;">NIF</td>
<td>NUMBER</td>
<td></td>
<td>RH_T_RUNCIONARIOS.NIF</td>
</tr>
<tr>
<td style="text-align: left;">Nome Colaborador</td>
<td>TEXT</td>
<td></td>
<td>RH_T_RUNCIONARIOS.NOME</td>
</tr>
<tr>
<td style="text-align: left;">TIPREL_ID</td>
<td>HIDDEN</td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;">REGRAS</td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><ul>
<li><p><em>A Lista deve ilustrar todos os colaboradores de uma
determinada direção aptos para processar, ou seja nestas seguintes
condições</em></p>
<ul>
<li><p><em>O colaborador deve estar ativo</em></p></li>
<li><p><em>O colaborador deve ter o vinculo ativo
(<strong>RH_TIPOS_RELACIONAMENTO.EST_ACT_ADM = 1</strong>)</em></p></li>
<li><p><em>o salario deve estar ativo <strong>RH_T_DEF_REMUNERACAO =
A</strong></em></p></li>
</ul></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;">Ações</td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p>Ao clicar no botão gravar o sistema deve fazer a
seguinte ação:</p>
<ul>
<li><p><strong>RH_T_TIPOS_RELACIONAMENTO.FLG_PROCESSA =
0</strong></p></li>
</ul></td>
</tr>
</tbody>
</table>

## Lista Processamento

<img src="media/image7.png" style="width:7.43542in;height:4.39653in"
alt="Uma imagem com texto, captura de ecrã, software, número Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
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
<td>Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td><em>INPSRH.RH_T_PROC_SALARIOS.DATA_DE</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td><em>INPSRH. RH_T_PROC_SALARIOS. DATA_DE</em></td>
</tr>
<tr>
<td>Direção</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>INPSRH. RH_T_PROC_SALARIOS.CC_ID</em></td>
</tr>
<tr>
<td>Tipo</td>
<td><em>SELECT</em></td>
<td><strong>DOMAIN</strong> = TIPO_PROCESSAMENTO</td>
<td><p>Antes rh_tipos_relacionamento. qr_cod_relacao</p>
<p><em>RH_T_PROC_SALARIOS. TIPO_PROCESSAMENTO</em></p></td>
</tr>
<tr>
<td>Estado</td>
<td><em>SELECT</em></td>
<td><em><strong>DOMAIN</strong> = ESTADO_PROCESSAMENTO</em></td>
<td><em>INPSRH. RH_T_PROC_SALARIOS.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p>
<p><del><strong>(<em>RH_LISTA_PROCESSAMENTO_DB.</em></strong>
<strong><em>load_list</em>) </strong></del></p></td>
</tr>
<tr>
<td>Estado</td>
<td><em>TEXT</em></td>
<td>Situação atual do processamento salarial.</td>
<td><em><mark>RH_V_LISTA_PROCESSAMENTO</mark>.ESTADO</em></td>
</tr>
<tr>
<td>Mês Referencia</td>
<td><em>TEXT</em></td>
<td>Mês e ano a que se refere o processamento salarial</td>
<td><em><mark>RH_V_LISTA_PROCESSAMENTO</mark>.</em>
<em>mes_referencia</em></td>
</tr>
<tr>
<td>Código CC</td>
<td><em>TEXT</em></td>
<td>Código do <strong>Centro de Custo</strong> a que está associado o
processamento.</td>
<td><em><mark>RH_V_LISTA_PROCESSAMENTO</mark>.</em> codigo_cc</td>
</tr>
<tr>
<td>Direção</td>
<td><em>TEXT</em></td>
<td>Unidade orgânica ou direção responsável pelos colaboradores
incluídos no processamento.</td>
<td><em><mark>RH_V_LISTA_PROCESSAMENTO</mark>.</em>
<em>direcao</em></td>
</tr>
<tr>
<td>OBS</td>
<td><em>TEXT</em></td>
<td>Campo de <strong>Observações</strong> para registar informações
complementares ou exceções relevantes.</td>
<td><em><mark>RH_V_LISTA_PROCESSAMENTO</mark>.OBS</em></td>
</tr>
<tr>
<td>Quantidade Processada</td>
<td><em>TEXT</em></td>
<td>Número total de colaboradores ou registos incluídos no processamento
salarial.</td>
<td><em><mark>RH_V_LISTA_PROCESSAMENTO</mark>.</em>
<em>quantidade</em></td>
</tr>
<tr>
<td>Cabimento</td>
<td><em>TEXT</em></td>
<td>Indicação do montante ou autorização orçamental afeta ao
processamento salarial.</td>
<td><em><mark>RH_V_LISTA_PROCESSAMENTO</mark>.</em>
<em>cabimento</em></td>
</tr>
<tr>
<td>Total</td>
<td><em>TEXT</em></td>
<td>Valor monetário total resultante do processamento salarial
(somatório das remunerações e descontos).</td>
<td><em><mark>RH_V_LISTA_PROCESSAMENTO</mark>.</em> <em>total</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4"><ul>
<li><p>Os processamentos eliminados não devem aparecer na lista</p></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td>Processar</td>
<td colspan="3"><p>Executa o cálculo do processamento salarial para o
período e colaboradores selecionados, gerando os valores de
remunerações, subsídios e descontos</p>
<p>Só se pode processar uma direção caso existe pelo menos um
colaborador que ainda não foi processado</p></td>
</tr>
<tr>
<td>Eliminar Proc</td>
<td colspan="3"><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado :
(</em><strong>RH_T_PROC_SALARIOS.estado =
‘PROCESSADO’</strong><em>)</em></td>
</tr>
<tr>
<td>Validar</td>
<td colspan="3"><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘PROCESSADO’</strong><em>)</em></td>
</tr>
<tr>
<td>Cabimentar</td>
<td colspan="3"><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘VALIDADO’</strong><em>)</em></td>
</tr>
<tr>
<td>Eliminar Cab.</td>
<td colspan="3"><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘CABIMENTADO’</strong><em>)</em></td>
</tr>
<tr>
<td>Autorizar</td>
<td colspan="3"><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘CABIMENTADO’</strong><em>)</em></td>
</tr>
<tr>
<td>Relatorio</td>
<td colspan="3">Este botão fica visível em qualquer estado</td>
</tr>
<tr>
<td>Desmarcar para / Processar</td>
<td colspan="3">Só se pode desmarcar para processar somente direções que
ainda não foram processados</td>
</tr>
</tbody>
</table>

### Processar Salario 

<img src="media/image8.png" style="width:9.69306in;height:3.22917in"
alt="Uma imagem com texto, file, número, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 7%" />
<col style="width: 39%" />
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
<td style="text-align: left;">Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_PROC_SALARIOS.DATA_DE</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_PROC_SALARIOS.DATA_ATE</em></td>
</tr>
<tr>
<td style="text-align: left;">Tipo</td>
<td><em>SELECT</em></td>
<td><strong>DOMAIN</strong> = TIPO_PROCESSAMENTO</td>
<td><em>RH_T_PROC_SALARIOS.TIPO_PROCESSAMENTO</em></td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td><em>lOOKUP</em></td>
<td>iNPSSIGOF.INSTITUICOES, INPSSIGOF.CENTROS_CUSTO</td>
<td><em>RH_T_PROC_SALARIOS.</em> <em>CC_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td><em>TEXTAREA</em></td>
<td></td>
<td><em>RH_T_PROC_SALARIOS.OBS</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><ul>
<li><p><em><strong>O sistema deve permitir o processamento de múltiplos
meses de referência em simultâneo.</strong> Ou seja, o utilizador pode
selecionar um intervalo de datas que abranja mais de um mês; contudo, o
processamento propriamente dito deve ser efetuado de forma
<strong>mensal e independente por cada mês incluído</strong> no
intervalo.</em></p></li>
<li><p><em>Para Processar deve verificar as seguintes condições</em></p>
<ul>
<li><p><em>Verificar se o colaborador está marcado para não ser
processado<strong>.</strong> Ou seja, caso o campo
<strong>RH_T_TIPOS_RELACIONAMENTO.FLG_PROCESSA = 0</strong>, o
colaborador não deve</em> <em>ser incluído no
processamento<strong>.</strong></em></p></li>
<li><p><em>Deve validar se existe dotação orçamental</em></p></li>
</ul></li>
<li><p><em>Um colaborador processado numa determinada direção, num mesmo
mês de referência, não deve ser processado mais de uma vez.<br />
No entanto, caso o colaborador possua mais de um vínculo ativo sujeito a
processamento, poderá ser processado mais de uma vez, correspondendo a
cada vínculo distinto. Contudo, o mesmo vínculo não pode, em hipótese
alguma, ter mais de um processamento no mesmo mês de
referência.</em></p></li>
<li><p><em>Verifica se o colaborador tem falta</em></p></li>
</ul>
<ol type="1">
<li><p><em>Só se pode processar falta, para o tipo de processamento
“<strong>SAL</strong>” (salário)</em></p></li>
<li><p><em>Caso o utilizador não selecionar centro direção, logo o
sistema processa todos os vínculos
(<strong>'Efetivo</strong>,<strong>'Contratado'</strong>)</em></p></li>
<li><p>O pode processar 1 ou vários direções ao mesmo tempo, e também
pode processar por colaborador.</p></li>
</ol></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;">AÇÕES</td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p>Ao clicar no botão Gravar, deve invocar a seguinte
funcionalidade:</p>
<p><em><strong>RH_PROCESSAMENTO_SALARIAL_DB.</strong>PROCESSAR</em>
<em>(p_dt_inicio = Data Inicio,</em></p>
<p><em>p_dt_fim = Data fim,</em></p>
<p><em>p_cc_id = Direção (Centro de custo de uma direção),</em></p>
<p><em>p_tiprel_id =Colaborador ( id de tipos de relacionamento do
Colaborador,</em></p>
<p><em>p_tipo = Tipo</em></p>
<p><em>P_user_name = nome de utilizador logado,</em></p>
<p><em>p_user_id NUMBER = id de utilizador Logado)</em></p></td>
</tr>
</tbody>
</table>

#### Pesquiza colaborador 

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 7%" />
<col style="width: 38%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte Dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Nome</td>
<td>TEXT</td>
<td>Nome de colaborador (RH_T_FUNCIONARIO.NOME)</td>
<td>RH_T_FUNCIONARIO.NOME</td>
</tr>
<tr>
<td>Direcao</td>
<td>LOOCKUP</td>
<td>Nome de instituições (INPSSIGOF.INSTIDUICOES.NOME)</td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID= INPSSIGOF.INSTIDUICOES.ID</td>
</tr>
<tr>
<td>Centro Custo</td>
<td>LOOCKUP</td>
<td>Nome de Centro de custo</td>
<td>INPSSIGOF.CENTROS_CUSTO.ID</td>
</tr>
<tr>
<td>TPREL_ID</td>
<td>HIDDEN</td>
<td>Deve devolver o id de tipo relacionamento</td>
<td>RH_T_TIPOS_RELACIONAMENTO.ID</td>
</tr>
<tr>
<td colspan="4"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4"><ul>
<li><p>Pesquiza colaborador cujo vínculo é último
(<em><strong>EST_ACT_ADM = 1</strong></em>)</p></li>
</ul></td>
</tr>
<tr>
<td colspan="4"><strong>AÇÕES</strong></td>
</tr>
<tr>
<td><img src="media/image6.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><em><strong>RH_PROCESSAMENTO_SALARIAL_DB.</strong>
ELIMINAR_PROCESSAMENTO</em></td>
</tr>
</tbody>
</table>

#### Pesquiza Centro Custo

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 7%" />
<col style="width: 38%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulario</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte Dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Nome</td>
<td>TEXT</td>
<td>Nome de instituições (INPSSIGOF.INSTIDUICOES. NOME)</td>
<td>INPSSIGOF.INSTITUICOES.ID</td>
</tr>
<tr>
<td>CC_ID</td>
<td>HIDDEN</td>
<td>Devolve o id de Centro de custo</td>
<td>INPSSIGOF.CENTROS_CUSTO.ID</td>
</tr>
<tr>
<td colspan="4"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4">Na lista deve devolver somente as direções que existe em
<em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.INSTIT_ID</em></td>
</tr>
<tr>
<td colspan="4"><strong>AÇÕES</strong></td>
</tr>
<tr>
<td><img src="media/image6.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td
colspan="3"><em><strong>RH_PROCESSAMENTO_SALARIAL_DB.</strong>ELIMINAR_PROCESSAMENTO</em></td>
</tr>
</tbody>
</table>

### Eliminar Proc

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 86%" />
</colgroup>
<thead>
<tr>
<th colspan="2"><strong>REGRAS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><ul>
<li><p>Só é possível eliminar processamentos que se encontrem apenas no
estado “<strong>PROV</strong>”, ou seja, que ainda não tenham sido
validados</p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><strong>AÇÕES</strong></td>
</tr>
<tr>
<td><img src="media/image6.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td><em><strong>RH_PROCESSAMENTO_SALARIAL_DB.</strong>ELIMINAR_PROCESSAMENTO</em></td>
</tr>
</tbody>
</table>

### Validar

Objectivo desse formulário é verificar todas as lacunas no processamento

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 7%" />
<col style="width: 41%" />
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
<td>Tipo Validação</td>
<td><em>Select</em></td>
<td><ul>
<li><p>Sem Nif</p></li>
<li><p>Nib Incorreto</p></li>
<li><p>Diferente liquida entre meses</p></li>
<li><p>Tipo Movimento entre meses</p></li>
<li><p>Remuneração / subsidio entre meses</p></li>
<li><p>Descontos entre meses</p></li>
<li><p>Diferença colaborador entre meses</p></li>
<li><p>Diferença Salárial base processado e Escalao</p></li>
<li><p>Colaborador com Falta</p></li>
<li><p>Colaborador com Hora Extra</p></li>
<li><p>Colaborador suspenso Processado</p></li>
<li><p>Duplicados</p></li>
<li><p>Sem Desconto IUR</p></li>
<li><p>Sem Desconto INPS</p></li>
</ul></td>
<td><em><strong>DOMAIN</strong> = TIPO_VALIDACAO</em></td>
</tr>
<tr>
<td>Mês Atual</td>
<td><em>SELECT</em></td>
<td>Deve trazer preenchido o mês do Processamento</td>
<td><em>TO_CHAR(rh_t_proc_salarios.DATA_DE, ‘MM’)</em></td>
</tr>
<tr>
<td>Mês Anterior</td>
<td><em>SELECT</em></td>
<td>Deve devolver o ames anterior do processamento</td>
<td><em>TO_CHAR(rh_t_proc_salarios.DATA_DE, ‘MM’)</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td colspan="4"><strong>Sem Nif -» (</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO</strong>.NOME_COLABORADOR</td>
</tr>
<tr>
<td colspan="4"><strong>Nib Incorreto -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NOME_COLABORADOR</td>
</tr>
<tr>
<td>Nib</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO</strong>.NIB</td>
</tr>
<tr>
<td colspan="4"><strong>Diferenca Liquida entre meses -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO</strong>.NOME_COLABORADOR</td>
</tr>
<tr>
<td>Valor Anterior</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO</strong>.VALOR_ANTERIOR</td>
</tr>
<tr>
<td>Valor Atual</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>VALOR_ATUAL</td>
</tr>
<tr>
<td colspan="4"><strong>Tipo Movimento entre meses -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Tipos Movimento</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO</strong></td>
</tr>
<tr>
<td>Valor Anterior</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>VALOR_ANTERIOR</td>
</tr>
<tr>
<td>Valor Atual</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>VALOR_ATUAL</td>
</tr>
<tr>
<td colspan="4"><strong>Remuneração / subsidio entre meses -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Tipos Movimento</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>TIPO_MOVIMENTO</td>
</tr>
<tr>
<td>Valor Anterior</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>MES_ANTERIOR</td>
</tr>
<tr>
<td>Valor Atual</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>MES_ATUAL</td>
</tr>
<tr>
<td colspan="4"><strong>Desconto entre meses -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Tipo Movimentos</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>TIPO_MOVIMENTO</td>
</tr>
<tr>
<td>Valor Anterior</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>MES_ANTERIOR</td>
</tr>
<tr>
<td>Valor Atual</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>MES_ATUAL</td>
</tr>
<tr>
<td colspan="4"><strong>Diferença colaborador entre meses -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NOME_COLABORADOR</td>
</tr>
<tr>
<td>Mês Processamento</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>MES_ATUAL OU
<strong>RH_V_VALIDACAO.</strong> MES_ANTERIOR</td>
</tr>
<tr>
<td colspan="4"><strong>Diferente Salário base processado e Escalao -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NOME_COLABORADOR</td>
</tr>
<tr>
<td>Valor Processado</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO</strong>.VALOR_ATUAL</td>
</tr>
<tr>
<td>Valor Escalão</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>VALOR_ESCALAO</td>
</tr>
<tr>
<td colspan="4"><strong>Colaborador com Falta -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NOME_COLABORADOR</td>
</tr>
<tr>
<td>Numero Falta</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NUMERO</td>
</tr>
<tr>
<td>Valor</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>VALOR_ATUAL</td>
</tr>
<tr>
<td colspan="4"><strong>Colaborador com Hora Extra -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NOME_COLABORADOR</td>
</tr>
<tr>
<td>Numero Hora</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NUMERO</td>
</tr>
<tr>
<td>Valor</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>VALOR_ATUAL</td>
</tr>
<tr>
<td colspan="4"><strong>Colaborador suspenso Processado -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NOME_COLABORADOR</td>
</tr>
<tr>
<td>Situacao Laboral</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>SITUACAO_LABORAL</td>
</tr>
<tr>
<td colspan="4"><strong>Duplicados -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NOME_COLABORADOR</td>
</tr>
<tr>
<td colspan="4"><strong>Sem Desconto IUR -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NOME_COLABORADOR</td>
</tr>
<tr>
<td colspan="4"><strong>Sem Desconto INPS -»
(</strong>RH_V_VALIDACAO.TIPO)</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td></td>
<td><strong>RH_V_VALIDACAO.</strong>NOME_COLABORADOR</td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 85%" />
</colgroup>
<thead>
<tr>
<th colspan="2"><strong>REGRAS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><em>Só é possível Validar processamentos que se
encontrem apenas no estado “</em><strong>PROCESSADO</strong><em>”, ou
seja, que ainda não tenham sido validados</em></td>
</tr>
<tr>
<td colspan="2"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image6.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td>Faz update, na tabela <strong>RH_T_PROC_SALARIO.ESTADO =
‘</strong>VALIDADO’</td>
</tr>
</tbody>
</table>

### Cabimentar

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 83%" />
</colgroup>
<thead>
<tr>
<th colspan="2">REGRAS</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><ul>
<li><p><em>Só se pode Cabimentar informações cujo processamento esteja
validado “<strong>VALIDADO</strong>”</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2">Ações</td>
</tr>
<tr>
<td><img src="media/image6.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td>Invoca a funcionalidade,
<strong>RH_PROCESSAMENTO_SALARIAL_DB</strong>. CABIMENTAR</td>
</tr>
</tbody>
</table>

### Eliminar Cabimento

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 83%" />
</colgroup>
<thead>
<tr>
<th colspan="2"><strong>REGRAS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><ul>
<li><p><em>Só é possível eliminar cabimentos que ainda não tenham sido
autorizados, ou seja, cujo estado se encontre apenas como
“<strong>DEV</strong>”.</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><strong>ACÇÃO</strong></td>
</tr>
<tr>
<td><img src="media/image6.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td>Pendente envio de Procedimento</td>
</tr>
</tbody>
</table>

### Autorizar

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 83%" />
</colgroup>
<thead>
<tr>
<th colspan="2">REGRAS</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><ul>
<li><p><em>Só pode autorizar cabimento esm estado =
<strong>CABIMENTADO</strong></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2">Ações</td>
</tr>
<tr>
<td><img src="media/image6.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td>Invoca a funcionalidade,
<strong>RH_PROCESSAMENTO_SALARIAL_DB</strong>. AUTORIZAR</td>
</tr>
</tbody>
</table>

## 

### Relatórios 

<table>
<colgroup>
<col style="width: 11%" />
<col style="width: 29%" />
<col style="width: 58%" />
</colgroup>
<thead>
<tr>
<th>Nome Relatório</th>
<th colspan="2">imagem</th>
</tr>
</thead>
<tbody>
<tr>
<td>Detalhe de Processamento Para o Funcionário</td>
<td colspan="2"><img src="media/image9.jpeg"
style="width:6.55694in;height:6.26806in" /></td>
</tr>
<tr>
<td colspan="2" style="text-align: center;"><strong>Campo</strong></td>
<td style="text-align: center;"><strong>Fonte de
Informação</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Processamento de Salários</strong></td>
<td>hardcode</td>
</tr>
<tr>
<td colspan="2">Elaborado em:</td>
<td><strong>SYSDATE</strong></td>
</tr>
<tr>
<td colspan="2">Data Proc:</td>
<td>proc_sal_cc.<strong>DATA_PROCESSAMENTO</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Processar Remunerações Gabinete Sistemas de
Informação</strong></td>
<td>proc_sal_cc<strong>.CENTRO_DE_CUSTO</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Vencimento Pessoal contratado</strong></td>
<td>Agrupar, proc_sal_cc<strong>.DESCRICAO,</strong> onde
proc_sal_cc<strong>..SHORT_DESC</strong> = ‘SAL’</td>
</tr>
<tr>
<td colspan="2">10116231</td>
<td>proc_sal_cc.<strong>NIF</strong></td>
</tr>
<tr>
<td colspan="2">Anilton Pina Brandão</td>
<td>proc_sal_cc.<strong>NOME</strong></td>
</tr>
<tr>
<td colspan="2">Contratado</td>
<td>proc_sal_cc<strong>.RELACAO</strong></td>
</tr>
<tr>
<td colspan="2">Director Gabinete / 15F</td>
<td>proc_sal_cc.<strong>ESCALAO</strong></td>
</tr>
<tr>
<td colspan="2"><p>Subsidio Desgaste Viatura</p>
<p>Vencimento Pessoal contratado</p></td>
<td>proc_sal_id.<strong>DESCRICAO e</strong> proc_sal_id.<strong>VALOR
onde o</strong> proc_sal_id.<strong>TIPO =AREM</strong></td>
</tr>
<tr>
<td colspan="2"><p>Retenção Previdência social</p>
<p>Retenção Iur</p></td>
<td>proc_sal_id.<strong>DESCRICAO e</strong> proc_sal_id.<strong>VALOR
onde o</strong> proc_sal_id.<strong>TIPO =PAG</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Total Remuneração</strong></td>
<td>proc_sal_id<strong>.TOTAL_REMUNERACOES</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Total Desconto</strong></td>
<td style="text-align: left;">proc_sal_id.<strong>DESCONTO</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Total Liquido</strong></td>
<td
style="text-align: left;">proc_sal_id.<strong>TOTAL_LIQUIDO</strong></td>
</tr>
<tr>
<td colspan="2">Recibo de Pagamento de Salário para o Funcionario</td>
<td><img src="media/image10.jpeg"
style="width:4.88542in;height:6.26806in" /></td>
</tr>
<tr>
<td colspan="2" rowspan="3">Recibo individual do funcionario</td>
<td><img src="media/image11.jpeg"
style="width:8.49722in;height:1.79028in" /></td>
</tr>
<tr>
<td><strong><em>N</em>ota</strong>: Este relatório é idêntico ao
Relatório 2; a única diferença é que apresenta a informação do recibo de
salário de apenas um colaborador.</td>
</tr>
<tr>
<td><img src="media/image12.jpeg"
style="width:5.92361in;height:6.26806in" /></td>
</tr>
<tr>
<td colspan="2" style="text-align: center;"><strong>Campo</strong></td>
<td style="text-align: center;"><strong>Fonte De Dados</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><strong>Recibo de Pagamento de
Salário</strong></td>
<td>hardcode</td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><strong>Processar Remunerações
Gabinete Sistemas De Informação</strong></td>
<td>proc_sal_cc<strong>.CENTRO_DE_CUSTO</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">10458041</td>
<td>proc_sal_cc.<strong>NIF</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">Contratado</td>
<td>proc_sal_cc<strong>.RELACAO</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">Contratado - Contratado</td>
<td>proc_sal_cc.<strong>ESCALAO</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">Nivaldo Cardoso Tavares</td>
<td>proc_sal_cc.<strong>NOME</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">Total Remunerações</td>
<td>proc_sal_id<strong>.TOTAL_REMUNERACOES</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">Total Descontos</td>
<td style="text-align: left;">proc_sal_id.<strong>DESCONTO</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">Total Liquido</td>
<td
style="text-align: left;">proc_sal_id.<strong>TOTAL_LIQUIDO</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><em>REMUNERAÇÕES</em></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">Vencimento Pessoal
contratado</td>
<td>proc_sal_id.<strong>DESCRICAO e</strong> proc_sal_id.<strong>VALOR
onde o</strong> proc_sal_id.<strong>TIPO =AREM</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><em>DESCONTOS</em></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">Retenção Previdência
Social</td>
<td rowspan="2">proc_sal_id.<strong>DESCRICAO e</strong>
proc_sal_id.<strong>VALOR onde o</strong> proc_sal_id.<strong>TIPO
=PAG</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;">Retenção IUR</td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><em>Desconto emitido
Em:</em></td>
<td>sysdate</td>
</tr>
</tbody>
</table>

### Resumo Processamento

<img src="media/image13.png" style="width:9.68819in;height:4.91319in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 30%" />
<col style="width: 3%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="5"><strong>Total Remunerações</strong></td>
</tr>
<tr>
<td>Tipo Movimento</td>
<td><em>TEXT</em></td>
<td colspan="2"></td>
<td><em><strong>PROC_SAL_CC_REMUN.</strong>descricao || ‘(‘
||PROC_SAL_CC_REMUN.short_desc||’)’</em></td>
</tr>
<tr>
<td>Total a Pagar</td>
<td><em>TEXT</em></td>
<td colspan="2"></td>
<td><em><strong>PROC_SAL_CC_REMUN</strong>.total</em></td>
</tr>
<tr>
<td>Detalhe Remuneração</td>
<td><em>TEXT</em></td>
<td colspan="2">Abre o Detalhe de Processamento e passa como parâmetro
<em><strong>PROC_SAL_CC_REMUN.</strong></em>PROC_SAL_ID <strong>e
<em>PROC_SAL_CC_REMUN.</em></strong>descricao</td>
<td><em>-----------------------------------------------------------------</em></td>
</tr>
<tr>
<td colspan="5"><strong>Total Pagamento</strong></td>
</tr>
<tr>
<td>Tipo Movimento</td>
<td><em>TEXT</em></td>
<td colspan="2"></td>
<td><em><strong>PROC_SAL_CC_PAG.</strong>descricao</em></td>
</tr>
<tr>
<td>Total</td>
<td><em>TEXT</em></td>
<td colspan="2"></td>
<td><em><strong>TO_CHAR (PROC_SAL_CC_PAG.</strong>total,
'999,999,999,999' <strong>)</strong></em></td>
</tr>
<tr>
<td>Detalhe Pagamento</td>
<td><em>TEXT</em></td>
<td colspan="2">Abre o Detalhe de Processamento e passa como parâmetro
<em><strong>PROC_SAL_CC_PAG.</strong></em>PROC_SAL_ID <strong>e
<em>PROC_SAL_CC_PAG.</em></strong>descricao</td>
<td><em>-----------------------------------------------------------------</em></td>
</tr>
<tr>
<td colspan="5" style="text-align: left;"><strong>SCRIPT</strong></td>
</tr>
<tr>
<td colspan="3"><p><strong>Script de Remunerações</strong></p>
<p><img src="media/image14.png"
style="width:5.19931in;height:1.88681in" /></p></td>
<td colspan="2"><p><strong>Script de Pagamentos</strong></p>
<p><img src="media/image15.png"
style="width:4.47847in;height:1.525in" /></p></td>
</tr>
</tbody>
</table>

####  Detalhe Processamento

<img src="media/image16.png" style="width:9.68819in;height:2.65972in" />

| **FILTRO** |  |  |  |
|----|:--:|:--:|:---|
| Tipo Movimento | *DESABLED* |  | ***PROC_SAL_CC_REMUN.**DESCRICAO ou **PROC_SAL_CC_PAG.**DESCRICAO* |
| Valor Total | *NUMBER* |  | ***SUM (PROC_SAL_CC_REMUN.**VALOR **)** OU **SUM** ( **PROC_SAL_CC_PAG**.VALOR )* |
| **Lista** | **Tipo** | **Descrição** | **Fonte dados** |
| Nome | *TEXT* |  | ***PROC_SAL_CC_REMUN.**NOME OU **PROC_SAL_CC_PAG**.NOME* |
| Cargo | *TEXT* |  | ***PROC_SAL_CC_REMUN.**CARGO OU **PROC_SAL_CC_PAG**.CARGO* |
| Vinculo | *TEXT* |  | ***PROC_SAL_CC_REMUN.**RELACAO OU **PROC_SAL_CC_PAG**.RELACAO* |
| Valor | *TEXT* |  | ***PROC_SAL_CC_REMUN.**VALOR OU **PROC_SAL_CC_PAG**.VALOR* |

### Lista Susbsisdio Férias 

Permitir de uma forma mais rápida e eficaz o processamento do subsidio
de natal e de ferias levado em consideração o seguinte:

- Meses trabalhados no ano ate 15/12;

- Numero de faltas (injustificadas e justificadas) de acordo com o
  controlo de assiduidade;

- Processos disciplinares;

<img src="media/image17.png" style="width:9.68958in;height:3.68611in" />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 7%" />
<col style="width: 47%" />
<col style="width: 31%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>FILTRO</strong></th>
<th style="text-align: center;"></th>
<th style="text-align: center;"></th>
<th style="text-align: center;"></th>
</tr>
</thead>
<tbody>
<tr>
<td><del>Tipo de Subsisio</del></td>
<td><del>SELECT</del></td>
<td style="text-align: center;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td>Direção</td>
<td><em>SELECT</em></td>
<td style="text-align: center;"></td>
<td
style="text-align: left;"><em><strong>RH_T_SUBSIDIO_FERIAS</strong>.FUN_ID</em></td>
</tr>
<tr>
<td>Colaborador</td>
<td><em>DATE</em></td>
<td style="text-align: center;"><em>RH_T_FUNCIONARIO.NOME</em></td>
<td
style="text-align: left;"><em><strong>RH_T_SUBSIDIO_FERIAS.</strong>FUN_ID</em></td>
</tr>
<tr>
<td>Data a Processar</td>
<td><em>DATE</em></td>
<td style="text-align: center;"><em>Deve trazer por defeito o ano
Atual</em></td>
<td style="text-align: left;"><em><strong>RH_T_SUBSIDIO_FERIAS.</strong>
ANO_REFERENTE</em></td>
</tr>
<tr>
<td>Estado Subsídio</td>
<td><em>SELECT</em></td>
<td style="text-align: center;"></td>
<td
style="text-align: left;"><em><strong>RH_T_SUBSIDIO_FERIAS.</strong>ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td colspan="4"
style="text-align: left;"><p><em><strong>RH_PK_SUBSISIO_NATAL_F_DB</strong>.
SAL_BASE_DET, passando como parâmetro</em></p>
<ul>
<li><p><em><strong>P_DATA_PROC</strong> = ano (data a
Processar)</em></p></li>
<li><p><em><strong>P_FUN_ID</strong> = id de colaborador</em></p></li>
<li><p><em><strong>P_DIRECAO =</strong> Direção</em></p></li>
</ul></td>
</tr>
<tr>
<td>Nome</td>
<td><em>TEXT</em></td>
<td style="text-align: center;">Nome do funcionário afeta a direção
escolhida</td>
<td style="text-align: left;"><em>P_FUN_NOME</em></td>
</tr>
<tr>
<td>Salario Base</td>
<td><em>TEXT</em></td>
<td style="text-align: center;"><p>Media de Salário do ano referente as
ferias.</p>
<p>Caso o salário mudar no meio do mês, o salário do mês deve ser feito
através da regra 3 simples contando que 1 mês tem 30 dias.</p></td>
<td
style="text-align: left;"><em><strong>sipsv0.sips_prest_api.format_dinheiro(p_num
=&gt;</strong></em> <em>P_VALOR_SAL_BASE<strong>, p_separator =&gt;
',');</strong></em></td>
</tr>
<tr>
<td>Mes. Trab.</td>
<td><em>TEXT</em></td>
<td style="text-align: center;"><p>O intervalo dos meses cujo vínculo
com o INPS esteve ativo.</p>
<p>Exemplo:</p>
<ul>
<li><p>01/01/2021 à 31/05/2021</p></li>
<li><p>01/07/2021 à null</p></li>
</ul>
<p>Este exemplo deve contabilizar 11 meses.</p>
<p>Só deve presentar os dias caso o número de dias for maior que 0
(zero), caso contrário deve apresentar somente o número de
meses.</p></td>
<td style="text-align: left;"><em>P_MES_TOTAL <strong>|| ‘-’ ||</strong>
P_DIAS_TOTAL</em></td>
</tr>
<tr>
<td>Dias de Ferias</td>
<td><em>TEXT</em></td>
<td style="text-align: center;"><p>Visto que o 12 mês de trabalho dão
direito a 22 dias de ferias, deve ser calculado através de</p>
<p>DiasFeriasMes = (mêsTrab *22) / 12</p>
<p>Cálculo dos dias</p>
<p>DiasFeriasDias = (dias *(22/12)) /30</p>
<p>Dias de ferias será o somatório de:</p>
<p>DiasFerias = DiasFeriasMes + DiasFeriasDias</p></td>
<td style="text-align: left;"><em>P_DIAS_TOTAL</em></td>
</tr>
<tr>
<td>Valor Subsidio</td>
<td><em>TEXT</em></td>
<td style="text-align: center;">ValorSub = diasFerias * SalarioBase /
22</td>
<td
style="text-align: left;"><em><strong>sipsv0.sips_prest_api.format_dinheiro(p_num
=&gt;</strong></em> <em>P_VALOR_SUBSIDIO<strong>, p_separator =&gt;
',');</strong></em></td>
</tr>
<tr>
<td>Subsidio Ativo</td>
<td><em>TEXT</em></td>
<td style="text-align: center;">Estado em que se encontra o
subsídio</td>
<td style="text-align: left;"><em>P_ESTADO</em></td>
</tr>
<tr>
<td colspan="4"><em><strong>Regras</strong></em></td>
</tr>
<tr>
<td colspan="4"><ul>
<li><p>Beneficiam deste subsidio todos os trabalhadores com direito a
feria;</p></li>
<li><p>O montante base do subsidio é calculado tendo em conta o salario
base mensal ate 31 de dezembro do ano que as ferias dizem respeito (ano
anterior ao processamento);</p></li>
<li><p>É considerado 1 mês de trabalho se o funcionário completar 30
dias no mês ou ultimo dia do mês de fevereiro (28 ou 29);</p></li>
<li><p>Caso o funcionário tiver alguma alteração no salario ao longo do
ano em que as ferias dizem respeito, o calculo é feito do seguinte
modo:</p>
<ul>
<li><p>Média do salario recebido no ano, caso a alteração do salario
ocorrer no final de cada mês;</p></li>
<li><p>Caso a alteração de salario ocorrer no meio de um mês, o calculo
no mês deve ser baseado no numero de dias em que o salario esteve em
vigor tendo em conta que um mês de trabalho tem 30 dias,
exemplo:</p></li>
</ul></li>
</ul>
<blockquote>
<p><img src="media/image18.png"
style="width:6.3125in;height:1.625in" /></p>
</blockquote>
<p>No máximo o trabalhador goza 22 dias uteis de ferias por 12 meses de
trabalho, através da regra “3 simples” pode-se chegar ao numero exato de
ferias, exemplo:</p>
<ul>
<li><p>1 de janeiro à 31 de dezembro – 22 dias uteis;</p></li>
<li><p>10 de janeiro à 31 de dezembro – 21 dias uteis;</p></li>
<li><p>10 de abril à 31 de dezembro – 17 dias uteis;</p></li>
<li><p>20 de julho à 31 de dezembro – 10 dias uteis;</p></li>
<li><p>Etc…</p></li>
</ul>
<p>O subsidio deve ser processado entre janeiro e fevereiro.</p></td>
</tr>
<tr>
<td colspan="4"><strong>Acoes</strong></td>
</tr>
<tr>
<td>Ver detalhe</td>
<td colspan="3"><em>Permite visualizar detalhe de Processamento do
Calculo de Ferias</em></td>
</tr>
<tr>
<td><img src="media/image19.png"
style="width:1.15625in;height:0.28333in" /></td>
<td colspan="3"><em>Permite inactivar um colaborador de forma qe el não
possa receber o seu subsidio</em></td>
</tr>
</tbody>
</table>

#### Ativar / inativar 

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th colspan="2" style="text-align: left;"><strong>Ativar</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><p><em>Ao ativar, deve efectuar gravação
nas seguintes tabelas</em></p>
<p><em><strong>1.RH_T_SUBSIDIO_FERIAS</strong></em></p>
<ul>
<li><p><em>FUN_ID</em></p></li>
<li><p><em>ANO_REFERENTE</em></p></li>
<li><p><em>VALOR_SALARIO_BASE</em></p></li>
<li><p><em>MES_TRAB</em></p></li>
<li><p><em>DIAS_TRAB</em></p></li>
<li><p><em>DIAS_ANUAL</em></p></li>
<li><p><em>DIAS_FERIAS</em></p></li>
<li><p><em>VALOR_SUBSIDIO</em></p></li>
<li><p><em>ESTADO = <strong>P,</strong> ANTES VALIDACAO,
<strong>A,</strong> APÔS VALIDACAO</em></p></li>
<li><p><em>REFERENCIA_ID = UM CODIGO ÚNICO 1 + MAX (VERIFICA O MAXIMA
REFEERENCIA ), TODOS OS REGISTOS DO MESMO BLOCO TEM A MESMA
REFERENCIA</em></p></li>
</ul></td>
<td
style="text-align: left;"><p><em><strong>1.RH_T_SUBSIDIO_FERIAS_DET</strong></em></p>
<ul>
<li><p><em>NUMERO</em></p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>DATA_FIM</em></p></li>
<li><p><em>ESCALAO_ID</em></p></li>
<li><p><em>MES_TRAB</em></p></li>
<li><p><em>VALOR_MES</em></p></li>
<li><p><em>DIA_TRAB</em></p></li>
<li><p><em>VALOR_DIA</em></p></li>
</ul>
<p><em>2. Registo na tabela RH_T_VALIDACAO</em></p>
<ul>
<li><p><em>REFERENCIA_NAME = ‘RH_T_SUBSIDIO_FERIAS’</em></p></li>
<li><p><em>REFERENCIA_ID = REFERENCIA_ID DE
RH_T_SUBSIDIO_FERIAS’</em></p></li>
<li><p><em>TIPO_ACCAO = ‘SUBSIDIO_FERIAS’</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><em><strong>Inativar</strong></em></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><em>UPDATE
<strong>RH_T_SUBSIDIO_FERIAS.</strong>ESTADO <strong>=
I</strong></em></td>
</tr>
</tbody>
</table>

#### Ver Detalhe

<img src="media/image20.png" style="width:9.67361in;height:3.38333in" />

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 6%" />
<col style="width: 44%" />
<col style="width: 31%" />
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
<td colspan="4" style="text-align: left;"><p>Para preencher o formulário
se deve invocar o seguinte procedimento:</p>
<p><em><strong>RH_PK_SUBSISIO_NATAL_F_DB</strong>. SAL_BASE_DET,
passando como parâmetro</em></p>
<ul>
<li><p><em><strong>P_DATA_PROC</strong> = ano (data a
Processar)</em></p></li>
<li><p><em><strong>P_FUN_ID</strong> = id de colaborador</em></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: left;">Nome</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;">Nome do colaborador</td>
<td style="text-align: left;"><em>P_FUN_NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">Nº</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>P_NUM</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;">Data inicio no escalao</td>
<td style="text-align: left;"><em>P_DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;">Data fim no escalao ou data final de
contagem de escalao</td>
<td style="text-align: left;"><em>P_DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: left;">Escalão</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;">Referencia / Escalao</td>
<td style="text-align: left;"><em>P_ESCALAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor Escalão</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;">Valor Salarial do Escalão</td>
<td style="text-align: left;"><em>P_VALOR_ESCALAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Meses / Dias</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;">Valor recebido nos Meses trabalhado no
Escalão / Valor recebido nos dias trabalhado no Escalão</td>
<td style="text-align: left;"><p><em>P_MESES || P_MESES_VALOR /</em></p>
<p><em>P_DIAS || P_DIAS_VALOR</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Valor Escalão / Tempo</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;">Total de Remunerações Recebido po
Escalão</td>
<td style="text-align: left;"><em>P_SUM_DIAS_MESES</em></td>
</tr>
<tr>
<td style="text-align: left;">Total Remuneração</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;">Total Recebido no ano</td>
<td style="text-align: left;"><em>P_TOTAL_REMUN</em></td>
</tr>
<tr>
<td style="text-align: left;">Salário Base do Calculo
(<strong>XXXXX</strong>)</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;"><p><strong>XXXX</strong> = dias
trabalhado</p>
<p><strong>XXXX</strong> = <em>P_DESC_SAL_BASE</em></p>
<p>Média Recebido no periodo trabalhado no ano que serve como base para
o cálculo do subsidio</p></td>
<td style="text-align: left;"><em>P_VALOR_SAL_BASE</em></td>
</tr>
<tr>
<td style="text-align: left;">Valor do Subsidio Referente aos
<strong>XXXX</strong> dias de Ferias</td>
<td style="text-align: left;"><em>TEXT</em></td>
<td style="text-align: left;"><p><strong>XXXX</strong> = Dias de
Ferias</p>
<p><strong>XXXX</strong> = <em>P_DESC_SUBSIDIO</em></p>
<p>Valor do Subsidio com base nos dias de Ferias</p></td>
<td style="text-align: left;"><em>P_VALOR_SUBSIDIO</em></td>
</tr>
</tbody>
</table>

- Exemplo de Calculo

<img src="media/image21.png" style="width:9.68681in;height:5.05347in" />

### Lista Subsidio Natal 

<img src="media/image22.jpeg"
style="width:8.38542in;height:4.71944in" />

<table style="width:100%;">
<colgroup>
<col style="width: 13%" />
<col style="width: 4%" />
<col style="width: 28%" />
<col style="width: 3%" />
<col style="width: 49%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>FILTRO</strong></th>
<th style="text-align: center;"></th>
<th style="text-align: center;"></th>
<th colspan="2" style="text-align: center;"></th>
</tr>
</thead>
<tbody>
<tr>
<td><del>Tipo Subsidio</del></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td>Direção</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_SUBSIDIO_NATAL.</strong>FUN_ID</em></td>
</tr>
<tr>
<td>Funcionário</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_SUBSIDIO_NATAL.</strong>FUN_ID</em></td>
</tr>
<tr>
<td>Data a Processar</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_SUBSIDIO_NATAL.</strong>
ANO_REFERENTE</em></td>
</tr>
<tr>
<td>Estado</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_SUBSIDIO_NATAL.</strong>ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td colspan="2" style="text-align: center;"><strong>Fonte
dados</strong></td>
</tr>
<tr>
<td colspan="5"
style="text-align: left;"><p><strong>RH_PK_SUBSISIO_NATAL_F_DB.</strong>
<strong>load_list (P_DIRECAO_ID NUMBER DEFAULT NULL ,</strong></p>
<p><strong>P_FUN_ID NUMBER DEFAULT NULL,</strong></p>
<p><strong>p_VALOR_C_BRINDE NUMBER,</strong></p>
<p><strong><mark>p_ano_processamento number</mark>,</strong></p></td>
</tr>
<tr>
<td>Nome</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">Nome do funcionário afeta a direção
escolhida</td>
<td colspan="2"
style="text-align: left;"><em><strong>P_LS_NOME</strong></em></td>
</tr>
<tr>
<td>Salario</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">Salario do funcionário de acordo com o
montante estabelecido no contrato (<em>Salario base do trabalhador até
15 de dezembro</em>)</td>
<td colspan="2"
style="text-align: left;"><em><strong>P_LS_SALARIO</strong></em></td>
</tr>
<tr>
<td>Meses %</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">O intervalo dos meses cujo o vinculo com
o INPS esteve ativo.Exemplo:<strong>01/01/2021 à 31/05/202101/07/2021 à
nullEste exemplo deve contabilizar 11 meses</strong>.</td>
<td colspan="2"
style="text-align: left;"><strong>P_LS_MESES_TRABALHO||'</strong> /
<strong>'||P_LS_PERC_SALARIO</strong></td>
</tr>
<tr>
<td>Faltas %</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">RH_MESES_TRAB.PERCENTAGEM - de acordo
com o numero de meses trabalhados no ano / Numero de Faltas
injustificadas no ano</td>
<td colspan="2" style="text-align: left;"><em><strong>P_LS_FALTAS||' /
'||P_LS_PERC_FALTA</strong></em></td>
</tr>
<tr>
<td>Valor Subsidio</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"><p>Montante bruto a receber do subsidio
de natal;</p>
<p><strong>Valor Subsídio = (Salário * (% Salario/100)) * (%
Falta/100)</strong></p></td>
<td colspan="2"
style="text-align: left;"><em><strong>P_LS_VALOR_SUBSIDIO</strong></em></td>
</tr>
<tr>
<td><ol start="3" type="A">
<li><p>Brinde</p></li>
</ol></td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: left;"><em><strong><mark>p_ls_valor_cheque_brid</mark></strong></em></td>
</tr>
<tr>
<td>Prenda Nat.</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: left;"><em><strong>p_ls_valor_prenda_natal</strong></em></td>
</tr>
<tr>
<td>Subsidio Ativo</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">Estado em que se encontra o
subsídio</td>
<td colspan="2"
style="text-align: left;"><em><strong>P_ESTADO</strong></em></td>
</tr>
<tr>
<td colspan="5"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4"><p><strong>De acordo com a O.S. Nº18/2000 o funcionário
deve ter um contrato a prazo ou contrato indeterminado</strong> até 15
de Dezembro e que estejam em efetividade de funções;</p>
<p>Funcionários ate 15/12 irão receber o subsidio de acordo com o tempo
de serviço nos seguintes termos:</p>
<ul>
<li><p>100% - 1 ano de serviço;</p></li>
<li><p>75% - Menos de 1 ano, mas igual ou superior a 6 meses;</p></li>
<li><p>50% - Igual o superior a 3 meses e inferior a 6 meses.</p></li>
</ul>
<ul>
<li><p>É considerado 1 mês de trabalho se o funcionário completar 30
dias no mês;</p></li>
<li><p>Montante é calculado, tendo como base o salário correspondente a
data 15/12;</p></li>
<li><p>Contrato a prazo e contrato indeterminado</p></li>
<li><p>Trabalhadores com processo disciplinar não são contemplados com o
subsidio</p></li>
</ul>
<p><strong>Faltas justificadas serão descontados no salário, exceto
(código laboral, alíneas a), b) e c) Nº2 do artigo 186º):</strong>- 2
faltas por mês atividade sindical;- 6 faltas consecutivas por motivo de
casamento;- Até 8 meses por motivo de falecimento;</p>
<p><strong>As faltas injustificadas também serão descontadas no subsidio
e cada falta é contada em dobro (código laboral, Nº2 do artigo
186º);</strong></p>
<ul>
<li><p>O trabalhador receberá o subsidio nos seguintes moldes (código
laboral, Artigo 206, nº 5):</p></li>
<li><p>Até 3 – 100%;</p></li>
<li><p>Entre 4 a 6 – 75%;</p></li>
<li><p>Entre 7 a 10 – 50%;</p></li>
<li><p>Mais que 10 – 0%.</p></li>
</ul>
<p><strong>São deduzidos o período de ausência:</strong></p>
<ul>
<li><p>Licença sem vencimento;</p></li>
<li><p>Faltas injustificadas;</p></li>
</ul>
<p>Dispensa de serviço no total de 30 dias, mais que 30 serão
consideradas injustificadas.</p></td>
<td style="text-align: left;"><p><strong><u>Regras Subsídio de
Férias</u></strong></p>
<p><em>Subsídio de ferias é subsídio pago uma vez ao ano, no mês de
fevereiro, cujas regras são as que se seguem:</em></p>
<ol type="1">
<li><p><em>Beneficiam deste subsídio todos os trabalhadores com direito
a ferias;</em></p></li>
<li><p><em>O montante base do subsídio é calculado tendo em conta o
salário base mensal até 31 de dezembro do ano que as ferias dizem
respeito;</em></p></li>
<li><p><em>É considerado 1 mês de trabalho se o funcionário completar 30
dias no mês;</em></p></li>
<li><p><em>Caso o funcionário tiver alguma alteração no salário ao longo
do ano em que as ferias dizem respeito, o cálculo é feito do seguinte
modo:</em></p>
<ol type="a">
<li><p><em>Média dos salários recebidos no ano;</em></p></li>
<li><p><em>Caso a alteração de salário ocorrer no meio de um mês, o
cálculo no mês deve ser baseado no número de dias em que o salário
esteve em vigor tendo em conta que um mês de trabalho tem 30 dias,
exemplo:</em></p></li>
</ol></li>
</ol>
<table style="width:33%;">
<colgroup>
<col style="width: 7%" />
<col style="width: 8%" />
<col style="width: 8%" />
<col style="width: 8%" />
</colgroup>
<thead>
<tr>
<th><em>Salário</em></th>
<th><em>DT Inicio</em></th>
<th><em>DT Fim</em></th>
<th><em>Montante</em></th>
</tr>
</thead>
<tbody>
<tr>
<td><em>98.776</em></td>
<td><em>01/01/2021</em></td>
<td><em>10/05/2021</em></td>
<td><em>32.925</em></td>
</tr>
<tr>
<td><em>110.000</em></td>
<td><em>11/05/2021</em></td>
<td></td>
<td><em>73.333</em></td>
</tr>
<tr>
<td colspan="3"><em>Total a ser considerado no mês de maio
(05)</em></td>
<td><em>106.258</em></td>
</tr>
</tbody>
</table>
<ol start="5" type="1">
<li><p><em>No máximo o trabalhador goza 22 dias uteis de ferias por 12
meses de trabalho, através da regra “3 simples” pode-se chegar ao número
exato de ferias, exemplo:</em></p>
<ol type="a">
<li><p><em>1 de janeiro à 31 de dezembro – 22 dias uteis;</em></p></li>
<li><p><em>10 de janeiro à 31 de dezembro – 21 dias uteis;</em></p></li>
<li><p><em>10 de abril à 31 de dezembro – 17 dias uteis;</em></p></li>
<li><p><em>20 de julho à 31 de dezembro – 10 dias uteis;</em></p></li>
<li><p><em>Etc…</em></p></li>
</ol></li>
<li><p><em>O subsídio deve ser processado entre janeiro e
fevereiro.</em></p></li>
</ol></td>
</tr>
<tr>
<td><strong>Acoes</strong></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td><img src="media/image19.png"
style="width:1.15625in;height:0.28333in" /></td>
<td colspan="4" style="text-align: center;"></td>
</tr>
</tbody>
</table>

#### Ativar / inativar 

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th colspan="2" style="text-align: left;"><strong>Ativar</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><p><em>Ao ativar, deve efectuar gravação
nas seguintes tabelas</em></p>
<p><em><strong>1.RH_T_SUBSIDIO_NATAL</strong></em></p>
<ul>
<li><p><em>FUN_ID</em></p></li>
<li><p><em>ANO_REFERENTE</em></p></li>
<li><p><em>VALOR_SALARIO_BASE</em></p></li>
<li><p><em>MES_TRAB</em></p></li>
<li><p><em>PERC_SALARIO</em></p></li>
<li><p><em>FALTAS</em></p></li>
<li><p><em>PERC_FALTA</em></p></li>
<li><p><em>VALOR_SUBSIDIO</em></p></li>
<li><p><em>CHEQUE_BRINDE</em></p></li>
<li><p><em>PRENDA_NATAL</em></p></li>
<li><p><em>ESTADO = P (ANTES DE VALIDACAO), A APÔS</em></p></li>
<li><p><em>REFERENCIA_ID = UM CODIGO ÚNICO 1 + MAX (VERIFICA O MAXIMA
REFEERENCIA ), TODOS OS REGISTOS DO MESMO BLOCO TEM A MESMA
REFERENCIA</em></p></li>
</ul></td>
<td style="text-align: left;"><p><em>2. Registo na tabela
RH_T_VALIDACAO</em></p>
<ul>
<li><p><em>REFERENCIA_NAME = ‘RH_T_SUBSIDIO_NATAL’</em></p></li>
<li><p><em>REFERENCIA_ID = REFERENCIA_ID DE
RH_T_SUBSIDIO_NATAL’</em></p></li>
<li><p><em>TIPO_ACCAO = ‘SUBSIDIO_NATAL’</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><em><strong>Inativar</strong></em></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><em>UPDATE
<strong>RH_T_SUBSIDIO_NATAL.</strong>ESTADO <strong>=
I</strong></em></td>
</tr>
</tbody>
</table>

## Baixa Medica ~~– Abonos e Beneficios~~ Licença

- PARA BAIXA , DEVE ABRIR UMA TEBELA COM OS MESES PROCESSADOS, O RH
  INTRODUZ OS VALORES DE SDO, E O SISEMA FAZ OS CALUCLOS, O RH VALIDA E
  O VALOR é introduzido SALÁRIO (SABER QUAL SERºA O TIPO DE MOVIMENTO
  PARA RETROATIVO DE BAIXA)

### Lista baixa Médica – ~~Abonos e beneficios~~ Licença

<img src="media/image23.png" style="width:9.69306in;height:4.26806in"
alt="Uma imagem com texto, software, número, Ícone de computador Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 7%" />
<col style="width: 41%" />
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
<td>Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_ABONOS_BENEFICIOS.DATA_INICIO</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_ABONOS_BENEFICIOS.DATA_FIM</em></td>
</tr>
<tr>
<td>Colaborador</td>
<td></td>
<td>RH_T_FUNCIONARIO.NOME</td>
<td><em>RH_T_ABONOS_BENEFICIOS.FUN_ID</em></td>
</tr>
<tr>
<td>Direcção</td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENT.INSTIT_ID, do ultimo Vinculo</td>
<td><em>-----------------------------------------------------------</em></td>
</tr>
<tr>
<td><p><del>Tipo Abono e Beneficio</del></p>
<p><mark>Licença</mark></p></td>
<td><em>SELECT</em></td>
<td>RH_T_PARAM_SITUACAO.NOME</td>
<td><em>RH_T_ABONOS_BENEFICIOS.PARAM_SIT_ID</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Estado</td>
<td></td>
<td></td>
<td><em>RH_T_ABONOS_BENEFICIOS.ESTADO</em></td>
</tr>
<tr>
<td>Direção</td>
<td></td>
<td></td>
<td><p><em>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</em></p>
<p><em>INPSSIGOF.INSTITUICOES. NOME</em></p></td>
</tr>
<tr>
<td>Seccão</td>
<td></td>
<td></td>
<td><p><em>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</em></p>
<p><em>RH_T_SECCAO.NOME</em></p></td>
</tr>
<tr>
<td>Nome</td>
<td></td>
<td></td>
<td><em>RH_T_FUNCIONARIOS.NOME</em></td>
</tr>
<tr>
<td>Vínculo</td>
<td></td>
<td></td>
<td><p><em>RH_T_TIPOS_RELACIONAMENTO.CONTRATO_VíNCULO_ID</em></p>
<p><em>RH_T_PARAM_VíNCULO.NOME</em></p></td>
</tr>
<tr>
<td>Categoria</td>
<td></td>
<td></td>
<td><p><em>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</em></p>
<p><em>RH_T_PARAM_CARGO.NOME</em></p></td>
</tr>
<tr>
<td><p><del>Tipo de Abono e Beneficio</del></p>
<p><mark>Tipo Licença</mark></p></td>
<td></td>
<td>RH_T_PARAM_SITUACAO.NOME</td>
<td><em>RH_T_ABONOS_BENEFICIOS.PARAM_SIT_ID</em></td>
</tr>
<tr>
<td>Motivo</td>
<td></td>
<td></td>
<td><em>RH_T_ABONOS_BENEFICIOS. PARAM_SIT_DET_ID</em></td>
</tr>
<tr>
<td>Data início</td>
<td></td>
<td></td>
<td><em>RH_T_ABONOS_BENEFICIOS.DATA_INICIO</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td></td>
<td></td>
<td><em>RH_T_ABONOS_BENEFICIOS.DATA_FIM</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4"></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Abre o mesmo formulário de registo</td>
</tr>
<tr>
<td>Regularização de contas</td>
<td colspan="3">Abre um formulario que pemite ajustar os valos pagos
pelo RH e SDO referente baixa medica de um colaborador</td>
</tr>
</tbody>
</table>

### Novo / Editar

RH_T_PARAM_SITUACAO

RH_T_ABONOS_BENEFICIOS

RH_T_FALTA

RH_T_AUSENCIA

<img src="media/image24.png" style="width:9.69306in;height:4.9875in" />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
<col style="width: 23%" />
<col style="width: 12%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulário</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Validar</td>
<td></td>
<td colspan="2"><strong>Fica visivel somente na validacao</strong></td>
<td></td>
</tr>
<tr>
<td><p><del>*Tipo Abono Benefício</del></p>
<p><mark>*Tipo Licença</mark></p></td>
<td><em>SELECT</em></td>
<td colspan="2"><strong>RH_T_PARAM_SITUACAO</strong>.NOME</td>
<td><p><em>RH_T_ABONOS_BENEFICIOS.PARAM_SIT_ID</em></p>
<p><em>RH_T_FALTA. PARAM_SIT_ID</em></p>
<p><em>RH_T_AUSENCIA. PARAM_SIT_ID</em></p></td>
</tr>
<tr>
<td>Motivo</td>
<td><em>SELECT</em></td>
<td colspan="2"><p><strong>RH_T_PARAM_SITUACAO_DET.NOME</strong></p>
<p>Motivo não é obrigatário</p></td>
<td><em>RH_T_ABONOS_BENEFICIOS. PARAM_SIT_DET_ID</em></td>
</tr>
<tr>
<td>*Data Inicio Licença</td>
<td><em>DATE</em></td>
<td colspan="2"></td>
<td><p><em>RH_T_ABONOS_BENEFICIOS.DATA_INICIO</em></p>
<p><em>RH_T_AUSENCIA.DATA_INICIO</em></p></td>
</tr>
<tr>
<td>Data Fim Licença</td>
<td><em>DATE</em></td>
<td colspan="2"></td>
<td><p><em>RH_T_ABONOS_BENEFICIOS.DATA_FIM</em></p>
<p><em>RH_T_AUSENCIA. DATA_FIM</em></p></td>
</tr>
<tr>
<td>Data Inicio Falta</td>
<td><em>DATE</em></td>
<td colspan="2">Caso essa informação não for nula, logo deva enviar essa
informação como parâmetro no Procedimento</td>
<td><em><del>-------------------------------</del></em></td>
</tr>
<tr>
<td>Dias Total Licença</td>
<td><em>NUMBER</em></td>
<td colspan="2">Contar número de dias entre <strong>Data Inicio de
Licença</strong> e <strong>Data Fim Licença</strong></td>
<td></td>
</tr>
<tr>
<td>Observação</td>
<td><em>TEXTAREA</em></td>
<td colspan="2"></td>
<td><p><em>RH_T_ABONOS_BENEFICIOS OBS</em></p>
<p><em>RH_T_AUSENCIA.OBS</em></p></td>
</tr>
<tr>
<td>----------------------------</td>
<td><em>HIDDEN</em></td>
<td
colspan="2">-------------------------------------------------------</td>
<td><p><em>RH_T_ABONOS_BENEFICIOS.FUN_ID</em></p>
<p><em>RH_T_FALTA.TIPREL_ID</em></p>
<p><em>RH_T_AUSENCIA.FUN_ID</em></p></td>
</tr>
<tr>
<td colspan="5"><strong>Informações Definidas No
Regulamento</strong></td>
</tr>
<tr>
<td
colspan="5"><p><strong>RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_FALTA_LICENCA(P_TIPREL_ID</strong>
=: ID de RH_T_TIPOS_RELACIONAMENTO onde EST_ACT_ADM = 1,</p>
<p><strong>P_DATA_INICIO</strong> =: Data Inicio Licença,</p>
<p><strong>P_DATA_FIM</strong> := Data Fim Licença,</p>
<p><strong>P_TIPO_LICENCA</strong> := Tipo Licença ,</p></td>
</tr>
<tr>
<td>Desconto Sobre</td>
<td></td>
<td colspan="2"><em>p_desc_sobre</em></td>
<td><em><del>-----------------------------------------------</del></em></td>
</tr>
<tr>
<td>Dias Direito Licença</td>
<td></td>
<td colspan="2"><em>p_dias_Direito</em></td>
<td><em><del>------------------------------------------------</del></em></td>
</tr>
<tr>
<td>Dias Descontado por RH</td>
<td></td>
<td colspan="2"><em>p_dias_desc_rh</em></td>
<td><em><del>-----------------------------------------------</del></em></td>
</tr>
<tr>
<td>Dias não descontado por RH</td>
<td></td>
<td colspan="2"><em>p_dias_ndesc_rh</em></td>
<td><em><del>----------------------------------------------</del></em></td>
</tr>
<tr>
<td><strong>Falta Mensal</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td>Mês</td>
<td></td>
<td colspan="2"><em>p_meses</em></td>
<td><em>-----------------------------------------------------</em></td>
</tr>
<tr>
<td>Data Inicio</td>
<td></td>
<td colspan="2"><em>p_data_ini_falta</em></td>
<td><em>RH_T_FALTA.DATA_INICIO</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td></td>
<td colspan="2"><em>p_data_fim_falta</em></td>
<td><em>RH_T_FALTA.DATA_FIM</em></td>
</tr>
<tr>
<td>Dias Falta Mensal</td>
<td></td>
<td colspan="2"><em>p_dias_falta</em></td>
<td><em>RH_T_FALTA.AUSENCIA</em></td>
</tr>
<tr>
<td>Salário Base</td>
<td></td>
<td colspan="2"><em>p_valor_salario</em></td>
<td><em>-----------------------------</em></td>
</tr>
<tr>
<td>Valor Descontado</td>
<td></td>
<td colspan="2"><em>p_valor_desc</em></td>
<td><em>RH_T_FALTA.VALOR</em></td>
</tr>
<tr>
<td colspan="5"><strong>Anexar Documento Comprovativo</strong></td>
</tr>
<tr>
<td>Tipo Documento</td>
<td></td>
<td colspan="2"></td>
<td rowspan="2"><p>RH_T_DOCUMENTO.<em>DOC_ID</em></p>
<p>RH_T_DOCUMENTO.TIPO_DOCUMETO = ID DE TABELA RH_T_TIPO_DOCUMENTO ONDE
REFERENCIA = ‘JUSTIFICACAO_FALTA’</p>
<p>REFERENCIA_NAME = ‘<em>RH_T_ABONOS_BENEFICIOS</em></p>
<p>REFERENCIA_ID = id de <em>RH_T_ABONOS_BENEFICIOS</em></p></td>
</tr>
<tr>
<td>Documento</td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="5"><em><strong>OUTRAS GRAVAÇÕES</strong></em></td>
</tr>
<tr>
<td colspan="3"><p><em>2-insert em
<strong>RH_T_ABONOS_BENEFICIOS</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>ESTADO = “<strong>P</strong>”</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
</ul>
<p><em>4-Registo em <strong>RH_T_AVALIACAO,
RH_T_VALIDACAO_DETALHE</strong></em></p></td>
<td colspan="2" style="text-align: left;"><p><em>2.1 Caso o tipo de
ausência é considerado uma ausência. (ou seja verifica na tabela
RH_T_PARAM_SITUACAO.FLG_AUSENCIA = 1). Logo regista na tabela
<strong>RH_T_AUSENCIA</strong></em></p>
<ul>
<li><p><em>PARAM_SIT_ID = ID de RH_T_PARAM_SITUACAO</em></p></li>
<li><p><em>DATA_INICIO = Data inicio</em></p></li>
<li><p><em>DATA_FIM = Data Fim</em></p></li>
<li><p><em>REFERENCIA_ID = id de
<strong>RH_T_ABONOS_BENEFICIOS</strong></em></p></li>
<li><p><em>REFERENCIA_NAME =
‘<strong>RH_T_ABONOS_BENEFICIOS’</strong></em></p></li>
<li><p><em>FUN_ID</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

####  Validação 

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

### Regularização de Contas 

<img src="media/image25.png" style="width:9.69306in;height:3.62778in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
<col style="width: 23%" />
<col style="width: 8%" />
<col style="width: 41%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Meses</td>
<td><em>TEXT</em></td>
<td colspan="2"></td>
<td>RH_T_REGULARIZACAO_SDO.MES_REFERENTE</td>
</tr>
<tr>
<td>Valor Liquido Recevido</td>
<td><em>TEXT</em></td>
<td colspan="2">RH_T_PROC_FUNCIONARIOS. TOT_LIQUIDO</td>
<td>-------------------------------</td>
</tr>
<tr>
<td>SDO Recebido</td>
<td><em>NUMBER</em></td>
<td colspan="2"></td>
<td>RH_T_REGULARIZACAO_SDO.SDO_RECEBIDO</td>
</tr>
<tr>
<td>Retroativo Salário</td>
<td></td>
<td colspan="2"></td>
<td>RH_T_REGULARIZACAO_SDO.VALOR_RETROATIVO_SALARIO</td>
</tr>
<tr>
<td>Retroativo SDO</td>
<td></td>
<td colspan="2"></td>
<td>RH_T_REGULARIZACAO_SDO.VALOR_RETROATIVO_SDO</td>
</tr>
<tr>
<td>Subsidio fiscal Recebido</td>
<td></td>
<td colspan="2">RH_T_PROC_FUNCIONARIOS. .TOT_REMUN_COLLECT</td>
<td>-------------------------------------</td>
</tr>
<tr>
<td>Salario Liquido + Subsidio Fiscal</td>
<td></td>
<td colspan="2">RH_T_PROC_FUNCIONARIOS.TOT_LIQUIDO +
RH_T_PROC_FUNCIONARIOS. .TOT_REMUN_COLLECT</td>
<td>----------------------------------------------</td>
</tr>
<tr>
<td></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td>TOTAL RECEBIDO</td>
<td><em>TEXT</em></td>
<td colspan="2">Somatoria entre o valor liquido recebido +SDO
recebido</td>
<td></td>
</tr>
<tr>
<td>TOTAL DIREITO</td>
<td><em>TEXT</em></td>
<td colspan="2">Somatoria entre o valor liquido recebido + retroativo
salario – retroativo SDO</td>
<td></td>
</tr>
<tr>
<td>DIFERENÇA POR EXPEDIENTE</td>
<td><em>TEXT</em></td>
<td colspan="2">retroativo salario + retroativo salario</td>
<td>??</td>
</tr>
<tr>
<td>Validar</td>
<td><em>RADIOLIST</em></td>
<td colspan="2">Só fica visivel ao validar salário</td>
<td>RH_T_REGULARIZACAO_SDO.ESTADO</td>
</tr>
<tr>
<td colspan="5"><em>OUTRAS GRAVAÇÕES</em></td>
</tr>
<tr>
<td colspan="3"><p><em>1.2 Fazer uma nova gravação na tabela de</em>
<strong>RH_T_REGULARIZACAO_SDO</strong></p>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL</em></p></li>
</ul>
<blockquote>
<p><em>USER_ALTERACAO_NAME = NULL</em></p>
</blockquote>
<ul>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>ESTADO = ‘P’</em></p></li>
<li><p><em>PROC_FUN_ID = ID DE
<strong>RH_T_PROC_FUNCIONARIOS</strong></em></p></li>
<li><p><em>SITUACAO_LABORAL_ID = ID DE
<strong>RH_T_SITUACAO_LABORAL</strong></em></p></li>
</ul></td>
<td colspan="2"><p><em>1.Registo na tabela de validação
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘REGULARIZAÇÃO’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_REGULARIZACAO </strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= ID de
RH_T_TIPOS_RELACIONAMENTO</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

#### Validação 

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
<li><p>Ao <strong>desvalidar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
<li><p>Ao clicar no botão validar deve ser efetuado as seguintes
ações,</p>
<ul>
<li><p>caso o colaborador tenha valor a receber por parte do RH</p>
<ul>
<li><p>Registo na tabela <strong>RH_T_DEF_REMUNERACOES</strong> (o valor
do retroativo), para isso é necessário definir qual o TM_ID</p></li>
<li><p>Registo na tabela de associação RH_T_REMUN_TIREPL</p></li>
</ul></li>
<li><p>Caso o colaborador tem a pagar</p>
<ul>
<li><p>Registo na tabela <strong>RH_T_DEF_REMUNERACOES</strong> (o valor
do retroativo negativo), para isso é necessário definir qual o
TM_ID</p></li>
<li><p>Registo na tabela de associação RH_T_REMUN_TIREPL</p></li>
</ul></li>
</ul></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

## Licença Sem Vencimento

A **licença sem vencimento** é uma **situação temporária de suspensão do
vínculo laboral**, em que o colaborador **mantém o posto e o vínculo à
função pública**, mas **não exerce funções nem aufere remuneração**
durante o período da licença.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th><strong>elemento</strong></th>
<th><strong>Descrição</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><strong>Natureza</strong></td>
<td>Suspensão temporária do exercício de funções.</td>
</tr>
<tr>
<td><strong>Direitos</strong></td>
<td>Mantém o vínculo e o posto, mas <strong>sem vencimento nem contagem
de tempo de serviço</strong> (exceto se a lei permitir).</td>
</tr>
<tr>
<td><strong>Duração</strong></td>
<td>Pode variar (ex.: 6 meses a 2 anos), renovável mediante
autorização.</td>
</tr>
<tr>
<td><strong>Motivos comuns</strong></td>
<td>Motivos pessoais, formação, acompanhamento familiar, missão externa,
estudos, etc.</td>
</tr>
<tr>
<td colspan="2"><p>Campo TIPO_SITUACAO_LABORAL = 'Licença sem
vencimento'</p>
<p>ESTADO_CONTRATO = 'S' (suspenso)</p>
<p>Mantém o mesmo ID_CONTRATO, mas com registo na tabela
RH_T_SITUACAO_LABORAL ou RH_T_MOVIMENTO</p></td>
</tr>
</tbody>
</table>

*RH_T_PARAM_SITUACAO*

*RH_T_PARAM_SITUACAO_DET*

Novo / Editar

RH_T_AUSENCIA

RH_T_CONTRATO_VINCULO

RH_T_SITUACAO_LABORAL

<img src="media/image26.png" style="width:9.69306in;height:2.67153in"
alt="Uma imagem com texto, captura de ecrã, Tipo de letra, file Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
<col style="width: 23%" />
<col style="width: 12%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulário</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Validar</td>
<td><em>Select</em></td>
<td colspan="2">Só parecer ao validar</td>
<td><em>---------------------------------------------------</em></td>
</tr>
<tr>
<td>Situação Laboral</td>
<td><em>SELECT</em></td>
<td colspan="2">Pegar id de <strong>RH_T_PARAM_SITUACAO
(</strong><em>Pega id onde código = LIC_SV referente a licença sem
Vencimento</em><strong>) ,</strong></td>
<td><em>RH_T_SITUACAO_LABORAL.</em> <em>SITUACAO_LABORAL_ID</em></td>
</tr>
<tr>
<td>Motivo</td>
<td><em>SELECT</em></td>
<td colspan="2">O motivo não é obrigatário (somente traz informação caso
o tipo de situação tem motivo)
<strong>RH_T_PARAM_SITUACAO_DET</strong></td>
<td><p><em>RH_T_SITUACAO_LABORAL. MOTIVO_SIT_LAB_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.MOTIVO_SIT_LAB_ID</em></p></td>
</tr>
<tr>
<td>*Data Inicio</td>
<td></td>
<td colspan="2"></td>
<td><p><em>RH_T_SITUACAO_LABORAL.DATA_INICIO</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO</em></p></td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>DATE</em></td>
<td colspan="2"></td>
<td><p><em>RH_T_SITUACAO_LABORAL.DATA_FIM</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_FIM</em></p></td>
</tr>
<tr>
<td>Observação</td>
<td></td>
<td colspan="2"></td>
<td><p><em>RH_T_SITUACAO_LABORAL. OBS</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. OBS</em></p></td>
</tr>
<tr>
<td>-----------------------</td>
<td><em>HIDDEN</em></td>
<td
colspan="2">-----------------------------------------------------</td>
<td><em>RH_T_SITUACAO_LABORAL.CONTRATO_VINCULO_ID</em></td>
</tr>
<tr>
<td colspan="5"><em><strong>OUTRAS GRAVAÇOES</strong></em></td>
</tr>
<tr>
<td colspan="3"><p><em>1.Faz update nos registos anteriores , ou seja
inativa os registos ativos</em></p>
<p><em>1.1Inativa a mobilidade em estado ativo
<strong>RH_T_TIPOS_RELACIONAMENTO (est_act_adm = 1 e data fim is not
null)</strong></em></p>
<ul>
<li><p><em>DATA_FIM = data inicio</em></p></li>
<li><p><em>USER_ALTERACAO _ID = utilizador logado</em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>nome de
utilizador</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = sysdate</em></p></li>
<li><p><em>EST_ACT_ADM = 0</em></p></li>
</ul>
<p><em>1.2 Fazer uma nova gravação na tabela de
<strong>RH_T_TIPOS_RELACIONAMENTO</strong>, pegas todas informações do
registo anterior, e regista com novas alterações nos campos do
formulário e outros seguinte campos</em></p>
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
<p><em>2. visto que licença sem vencimento é considerado uma ausência do
trabalho logo deve ser registado tb na <strong>tabela
RH_T_AUSENCIA</strong></em></p>
<ul>
<li><p><em>PARAM_SIT_ID = ID de RH_T_PARAM_SITUACAO</em></p></li>
<li><p><em>DATA_INICIO = Data inicio</em></p></li>
<li><p><em>DATA_FIM = Data Fim</em></p></li>
<li><p><em>REFERENCIA_ID = id de RH_T_SITUACAO_LABORAL</em></p></li>
<li><p><em>REFERENCIA_NAME =
‘<strong>SITUACAO_LABORAL’</strong></em></p></li>
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
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
<li><p><em>CONTRATO_ID = CONTRATO_ID DE RH_T_CONTRATO</em></p></li>
</ul>
<p><em>4-Registo em <strong>RH_T_AVALIACAO,</strong>
<strong>RH_T_VALIDACAO_DETALHE</strong></em></p></td>
</tr>
</tbody>
</table>

####  Validação 

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
<li><p>Ao <strong>desvalidar</strong>, devem ser atualizadas todas as
tabelas associadas, definindo o campo <strong>estado =
'I'</strong>.</p></li>
<li><p>Caso o utilizador <strong>atualize algum campo no
formulário</strong>, a alteração deve ser <strong>refletida na tabela
correspondente</strong>.</p></li>
<li><p>Apôs validação é gerado uma ordem de serviço (<a
href="#_Template_de_ordem">ver template a seguir</a>)</p></li>
</ul>
<p>1.2.-Ao Validar gera uma ordem de Serviço na tabela
<strong>RH_T_ORDEM_SERVICO</strong></p>
<ul>
<li><p>DESCRICAO = ‘Licença Sem Vencimento-’ ||
RH_T_FUNCIONARIOS.NOME</p></li>
<li><p>REFERENTE = ‘SITUACAO_LABORAL</p></li>
<li><p>FUN_ID = RH_T_FUNCIONARIOS.ID</p></li>
<li><p>TIPREL_ID = RH_T_TIPOS_RELACIONAMENTO.ID</p></li>
<li><p>VALIDACAO_ID = RH_T_VALIDACAO.ID</p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

### Lista de colaboradores

Lista todos colaboradores que estão de licença

<img src="media/image27.png" style="width:9.69306in;height:4.6375in"
alt="Uma imagem com texto, software, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 7%" />
<col style="width: 41%" />
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
<td>Data Inicio</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_INICIO</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_FIM</em></td>
</tr>
<tr>
<td>Colaborador</td>
<td></td>
<td>RH_T_FUNCIONARIO.NOME</td>
<td><em>RH_T_SITUACAO_LABORAL.CONTRA_VINCULO.ID</em></td>
</tr>
<tr>
<td>Direcção</td>
<td></td>
<td>INPSSIGOF.INTITUICOES.NOME</td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Estado</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.ESTADO</em></td>
</tr>
<tr>
<td>Direção</td>
<td></td>
<td>INPSSIGOF.INTITUICOES.NOME</td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</em></td>
</tr>
<tr>
<td>Seccão</td>
<td></td>
<td>RH_T_SECAO.NOME</td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</em></td>
</tr>
<tr>
<td>Nome</td>
<td></td>
<td></td>
<td><em>RH_T_FUNCIONARIOS.NOME</em></td>
</tr>
<tr>
<td>Vínculo</td>
<td></td>
<td>RH_T_PARAM_VINCULO.NOME</td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.CONTRATO_VINCULO_ID</em></td>
</tr>
<tr>
<td>Cargo</td>
<td></td>
<td><em>RH_T_PARAM_CARGO.NOME</em></td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</em></td>
</tr>
<tr>
<td>Motivo</td>
<td></td>
<td>RH_T_PARAM_SITUACAO_DET.NOME</td>
<td><em>RH_T_SITUACAO_LABORAL. MOTIVO_SIT_LAB_ID</em></td>
</tr>
<tr>
<td>Data inicio</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_INICIO</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_FIM</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4"></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Abre o mesmo formulário de registo</td>
</tr>
</tbody>
</table>

####  Modelo Ordem Serviço

| <img src="media/image28.png" style="width:4.01667in;height:4.01181in"
alt="Uma imagem com texto, captura de ecrã, Tipo de letra, documento Os conteúdos gerados por IA podem estar incorretos." /> |
|----|

## IMPORTAÇÃO MOVIMENTOS

### UPLOAD FICHEIRO

<img src="media/image29.png" style="width:9.69306in;height:1.62292in"
alt="Uma imagem com texto, file, Tipo de letra, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

<img src="media/image30.png" style="width:9.69306in;height:2.2875in"
alt="Uma imagem com texto, file, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Campo Ficheiro</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Num_fun</td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.FUN_ID</em></td>
</tr>
<tr>
<td><em>NOME_FUNCIONARIO</em></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.NOME_FUNCIONARIO</em></td>
</tr>
<tr>
<td><em>TP_MOV_RETENCAO</em></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.TP_MOV_RETENCAO</em></td>
</tr>
<tr>
<td><em>TP_MOV_REM</em></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.TP_MOV_REM</em></td>
</tr>
<tr>
<td><em>PERCENTAGEM</em></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.PERCENTAGEM</em></td>
</tr>
<tr>
<td><em>VALOR</em></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.VALOR</em></td>
</tr>
<tr>
<td><em>DATA_INICIO</em></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_INICIO</em></td>
</tr>
<tr>
<td><em>DATA_FIM</em></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_FIM</em></td>
</tr>
<tr>
<td><em>SITUACAO</em></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.SITUACAO</em></td>
</tr>
<tr>
<td colspan="2"><em>OUTRAS GRAVAÇÕES</em></td>
</tr>
<tr>
<td><p><em>1.outras gravações na tabela
<strong>RH_T_IMPORTACAO_MOVIMENTO</strong></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>ESTADO = ‘P’</em></p></li>
<li><p><em>CODIGO <strong>= cada ficheiro tem um identificador
sequencial igual a todos campos</strong></em></p></li>
</ul></td>
<td><p><em>2. gravações na tabela
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘IMPORTACAO_MOVIMENTO’
(</strong>DOMAINS = ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= CODIGO de
RH_T_IMPORTACAO_MOVIMENTO</strong></em></p></li>
<li><p><em>FUN_ID <strong>= NULL </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Lista Importados

<img src="media/image31.png" style="width:9.69306in;height:4.36597in"
alt="Uma imagem com texto, número, Tipo de letra, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 6%" />
<col style="width: 41%" />
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
<td>Data Impotação</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_REGISTO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Nome Ficheiro</td>
<td></td>
<td></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.NOME_FICHEIRO</em></td>
</tr>
<tr>
<td>FUN_ID</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.FUN_ID</em></td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td><em>TEXT</em></td>
<td>-----------------------------------------------------------</td>
<td><em>----------------------------------------------------</em></td>
</tr>
<tr>
<td>Movimento retenção (TP_MOV_RETENCAO)</td>
<td><em>TEXT</em></td>
<td>INPSSIGOF.RH_TIPOS_MOVIMENTO.NOME</td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.TP_MOV_RETENCAO</em></td>
</tr>
<tr>
<td>Movimento Remuneração (TP_MOV_REM)</td>
<td><em>TEXT</em></td>
<td>INPSSIGOF.RH_TIPOS_MOVIMENTO.NOME</td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.</em> TP_MOV_REM</td>
</tr>
<tr>
<td>Percentagem</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.PERCENTAGEM</em></td>
</tr>
<tr>
<td>Valor</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.VALOR</em></td>
</tr>
<tr>
<td>Data Inicio</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_INICIO</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_FIM</em></td>
</tr>
<tr>
<td>Vinculo (Situação)</td>
<td><em>TEXT</em></td>
<td></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.SITUACAO</em></p>
<p><em>RH_T_IMPORTACAO_MOVIMENTO.TIPREL_ID</em></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4"></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td>Importar</td>
<td colspan="3">Permite fazer upload do ficheiro de Ganasio e
Cantina</td>
</tr>
</tbody>
</table>

### Validação

<table style="width:95%;">
<colgroup>
<col style="width: 47%" />
<col style="width: 47%" />
</colgroup>
<thead>
<tr>
<th colspan="2"><ul>
<li><p>A validação invoca a mesma página de Registo</p></li>
<li><p>O campo validar deve ficar visível</p></li>
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
<p>Ao validar deve ser feito gravação nas seguintes tabelas de
Negócio:</p></th>
</tr>
</thead>
<tbody>
<tr>
<td><ul>
<li><p>Caso <strong>TP_MOV_REM</strong> não for nulo, grava na tabela
<strong>RH_T_DEF_REMUNERACAO</strong> e
<strong>RH_T_REMUN_TIPREL:</strong></p></li>
</ul>
<p><strong><u>RH_T_DEF_REMUNERACAO</u></strong></p>
<ul>
<li><p>TM_ID =
<em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong>.</em><strong>TP_MOV_REM</strong></p></li>
<li><p>PERCENTAGEM= <em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong></em>.
PERCENTAGEM</p></li>
<li><p>VALOR = <em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong></em>.
VALOR</p></li>
<li><p>ESTADO = ´A´</p></li>
<li><p>DATA_REGISTO = SYSDATE</p></li>
<li><p>USER_REGISTO_ID = ID UTILIZADOR LOGADO</p></li>
<li><p>USER_REGISTO_NAME = nome do utilizador logado</p></li>
<li><p>USER_ALTERACAO_ID = NULL</p></li>
<li><p>USER_ALTERACAO_NAME = NULL</p></li>
<li><p>DATA_ALTERACAO = NULL</p></li>
<li><p>FUN_ID =
<em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong>.FUN_ID</em></p></li>
<li><p>DATA_INICIO=<em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong>.</em>
DATA_INICIO</p></li>
<li><p>DATA_FIM =
<em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong>.DATA_FIM</em></p></li>
<li><p>OBS= ‘Registo referente ao ficheiro:’ ||
<em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong>.NM_FICHEIRO</em></p></li>
<li><p>MOEDA = ‘<strong>CVE’</strong></p></li>
<li><p>DATA_ULTIMO_PROC = ‘NULL’</p></li>
</ul>
<p><strong><u>RH_T_REMUN_TIPREL</u></strong></p>
<ul>
<li><p>REM_ID = ID de <strong>RH_T_DEF_REMUNERACOES</strong></p></li>
<li><p>TIPREL_ID = ID de RH_T_TIPOS_RELACIONAMENTO onde
est_act_adm=1</p></li>
<li><p>ESTADO = ‘A’</p></li>
<li><p>DATA_REGISTO = <strong>SYSDATE</strong></p></li>
<li><p>USER_REGISTO_ID = ID DE UTILIZADOR LOGADO</p></li>
<li><p>USER_REGISTO_NAME = nome de utilizador logado</p></li>
<li><p>OBS = ‘Registo referente ao ficheiro:’ ||
<em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong>.NM_FICHEIRO</em></p></li>
</ul></td>
<td><ul>
<li><p>Caso <strong>TP_MOV_RETENCAO</strong> não for nulo,
RH<strong>_T_DEF_PAGAMENTO</strong> e
<strong>RH_T_PAG_TIPREL</strong></p></li>
</ul>
<p><strong><u>RH_T_DEF_PAGAMENTOS</u></strong></p>
<ul>
<li><p>TM_ID =
<em><strong>RH_T_IMPORTACAO_MOVIMENTO.TP_MOV_RETENCAO</strong></em></p></li>
<li><p>VALOR = <em><strong>RH_T_IMPORTACAO_MOVIMENTO.</strong></em>
VALOR</p></li>
<li><p>DATA_INICIO =
<em><strong>RH_T_IMPORTACAO_MOVIMENTO.</strong></em>
DATA_INICIO</p></li>
<li><p>DATA_FIM =
<em><strong>RH_T_IMPORTACAO_MOVIMENTO.</strong>DATA_FIM</em></p></li>
<li><p>ESTADO = ‘A’</p></li>
<li><p>DATA_REGISTO = SYSDATE</p></li>
<li><p>USER_REGISTO_ID = ID UTILIZADOR LOGADO</p></li>
<li><p>USER_REGISTO_NAME = nome do utilizador logado</p></li>
<li><p>USER_ALTERACAO_ID = NULL</p></li>
<li><p>USER_ALTERACAO_NAME = NULL</p></li>
<li><p>DATA_ALTERACAO = NULL</p></li>
<li><p>FUN_ID =
<em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong>.FUN_ID</em></p></li>
<li><p>OBS = Registo referente ao ficheiro:’||
<em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong>.NM_FICHEIRO</em></p></li>
<li><p>RHB_ID = ??</p></li>
<li><p>REFERENCIA = ??</p></li>
<li><p>NM_ENTIDADE = ??</p></li>
<li><p>NU_CONTA =??</p></li>
<li><p>NIB =??</p></li>
<li><p>ENT_ID =??</p></li>
<li><p>DATA_ULTIMO_PROC = NULL</p></li>
</ul>
<p><strong><u>RH_T_PAG_TIPREL</u></strong></p>
<ul>
<li><p>REM_ID = ID de <strong>RH_T_DEF_PAGAMENTOS</strong></p></li>
<li><p>TIPREL_ID = ID de RH_T_TIPOS_RELACIONAMENTO onde
est_act_adm=1</p></li>
<li><p>ESTADO = ‘A’</p></li>
<li><p>DATA_REGISTO = <strong>SYSDATE</strong></p></li>
<li><p>USER_REGISTO_ID = ID DE UTILIZADOR LOGADO</p></li>
<li><p>USER_REGISTO_NAME = nome de utilizador logado</p></li>
<li><p>OBS = ‘Registo referente ao ficheiro:’ ||
<em><strong>RH_T_IMPORTACAO_MOVIMENTO</strong>.NM_FICHEIRO</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

## FOS

O módulo FOS (Folha de Ordenados para Segurança Social) tem como
principal objetivo **gerar, gerir e submeter ficheiros XML de
remunerações** dos funcionários para o sistema externo (INPS), com base
nos dados internos de:

- Processamento salarial

- Vínculo do funcionário

- Assiduidade

Este módulo está estruturado em **duas páginas principais**:

1.  **Lista principal de XML (FOS gerados)**

2.  **Página de detalhe do XML (linhas por funcionário)**

### Lista FOS

Ao aceder ao módulo, o sistema apresenta uma lista de FOS já gerados.\
Esta lista é construída a partir da tabela (**RH_XML_FOS**)

· Cada linha representa um XML (um FOS)

· O sistema trabalha ao nível do **header do XML**

O DADOS não SO VEM DO PROCESAMENTO SALARIAL , PQ TEM TEM CASOS DE
NECESSIDADE DE INGRODUCAO DE OUTROS TRABALHADORE QUE NAO FORAM
PROCESSADO (COLABORADORES QUE TRABALHAM NO EXTERIOR). OU SEJA PERMITIR A
INTRODUCAO DE OUTROS COLABORADORES , MEDIANTE JUSTIFICAÇÃO

<img src="media/image32.jpeg" style="width:7.7125in;height:3.95903in"
alt="Uma imagem com texto, captura de ecrã, número, diagrama Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 8%" />
<col style="width: 16%" />
<col style="width: 22%" />
<col style="width: 35%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Filtro</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="2"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Mês Referencia</td>
<td></td>
<td colspan="2"></td>
<td><em>RH_XML_FOS ano||</em> <em>RH_XML_FOS.mes</em></td>
</tr>
<tr>
<td>Data Inicio</td>
<td></td>
<td colspan="2"></td>
<td><em>RH_XML_FOS.</em> <em>DT_REGISTO</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td></td>
<td colspan="2"></td>
<td><em>RH_XML_FOS.</em> <em>DT_REGISTO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td colspan="2"
style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Mês Referencia</td>
<td><em>TEXT</em></td>
<td colspan="2">Período do processamento</td>
<td><strong>RH_XML_FOS.</strong> ANO||MES</td>
</tr>
<tr>
<td>Data Entega</td>
<td><em>TEXT</em></td>
<td colspan="2">Data de submissão</td>
<td><strong>RH_XML_FOS.</strong> DT_ENTREGA</td>
</tr>
<tr>
<td>Tipo Entrega</td>
<td><em>TEXT</em></td>
<td colspan="2"><p>PRIME / SUBST / CORRE</p>
<p>case rec.tp_entrega when <strong>'PRIME'</strong> then 'Primeira'
when <strong>'SUBST'</strong> then 'Substitui o' when 'CORRE' then
'Corre o' end;</p></td>
<td><strong>RH_XML_FOS</strong>.tp_entrega</td>
</tr>
<tr>
<td>Total Remuneração</td>
<td><em>TEXT</em></td>
<td colspan="2">Soma salários</td>
<td><strong>RH_XML_FOS.</strong> TT_REMUNERACAO</td>
</tr>
<tr>
<td>Total Contribuição</td>
<td><em>TEXT</em></td>
<td colspan="2">Valor INPS</td>
<td><strong>RH_XML_FOS.</strong> TT_CONTRIBUICAO</td>
</tr>
<tr>
<td>Obs</td>
<td><em>TEXT</em></td>
<td colspan="2">Texto Livre</td>
<td><strong>RH_XML_FOS.</strong> OBS</td>
</tr>
<tr>
<td>id</td>
<td><em>HIDDEN</em></td>
<td colspan="2"></td>
<td><strong>RH_XML_FOS.</strong>ID</td>
</tr>
<tr>
<td>id_dec_e</td>
<td><em>HIDDEN</em></td>
<td colspan="2"><strong>FUNCTION</strong>: getIDDC (RH_XML_FOS.
num_dc)</td>
<td><strong>RH_XML_FOS.</strong> NUM_DC</td>
</tr>
<tr>
<td colspan="5" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="5"><ul>
<li><p>A lista se constroi da seguinte forma:</p></li>
</ul>
<blockquote>
<p>SELECT x.* from RH_XML_FOS x</p>
<p>where (tp_field.flt_p_mes_referencia is null or
tp_field.flt_p_mes_referencia = x.ano||x.mes )</p>
<p>AND (tp_field.flt_p_data_inicio is null or
to_date(tp_field.flt_p_data_inicio,'dd-mm-yyyy') &lt;=
trunc(x.DT_REGISTO) )</p>
<p>AND (tp_field.flt_p_data_fim is null or
to_date(tp_field.flt_p_data_fim,'dd-mm-yyyy') &gt;= trunc(x.DT_REGISTO)
)</p>
<p>ORDER BY x.ano||x.mes des</p>
</blockquote>
<ul>
<li><p>Apenas vínculos válidos (contratados e efetivos)</p></li>
<li><p>Funcionário ativo no mês</p></li>
<li><p>Tipos de remuneração</p>
<ul>
<li><p>SL Salário</p></li>
<li><p>OT Outras</p></li>
<li><p>SN Subsídio</p></li>
<li><p>SF Férias</p></li>
</ul></li>
</ul>
<ul>
<li><p>Dias trabalhados (30 - dias não trabalhado)</p></li>
<li><p>Contribuição (24.5% do total)</p></li>
<li><p>Não gera XML sem dados</p></li>
<li><p>Não pode remover XML entregue (if dt_entrega is not null →
erro)</p></li>
<li><p>Atualização manual da contribuição (Só mostra botão se valor
alterado)</p></li>
<li><p>Novo</p>
<ul>
<li><p>Deve pegar todos os colaboradores afetos às direções ativas, e
verificar no view PROC_SAL_CC (RH_PROC_FUNCIONARIOS) se têm pagamentos
do tipo Salario, Subsidio Natal, Subsidio Ferias e Outras Remunerações
no mês de referencia escolhido:</p></li>
<li><p>Caso tiver deve pegar o valor para cada linha do tipo acima
referido.</p></li>
<li><p>Se não tiver e o tipo for diferente de Salário, não deve
acrescentar a linha;</p></li>
</ul></li>
<li><p>Se não tiver e o tipo for salario, deve acrescentar a linha e o
valor é igual a 0.</p></li>
<li><p>DT Entrega – caso for NULL</p>
<ul>
<li><p>Utilizador pode editar as informações e gerar um novo
XML;</p></li>
<li><p>Não mostrar o botão de DC;</p></li>
<li><p>Caso Clicar em NOVO num Mês de Referencia existente, não deve
permitir aceder a pagina, exibindo uma mensagem de erro;</p></li>
</ul></li>
<li><p>DT Entrega – caso for NOT NULL</p>
<ul>
<li><p>Retirar o botão de Eliminar;</p></li>
<li><p>Retirar o botão de Ver Detalhes;</p></li>
<li><p>Caso Clicar em NOVO num Mês de Referencia existente, deve gerar
uma SUBSTITUIÇÃO com consentimento do utilizador (Perguntar se pretende
realizar a substituição) ;</p></li>
</ul></li>
<li><p>Só deve existir uma FOS do Tipo PRIMEIRA;</p></li>
<li><p>DT Entrega – gravar a data quando a FOS for enviada;</p></li>
<li><p>Não deve permitir apagar a FOS se a DT Entrega for not
null;</p></li>
<li><p>Não deve permitir editar os registos se a DT Entrega for not
null;</p></li>
<li><p>Não deve permitir enviar a FOS se a DT Entrega for not
null;</p></li>
<li><p>Caso houver uma Substituição não deve mostra a primeira entrega
na lista;</p></li>
<li></li>
</ul></td>
</tr>
<tr>
<td colspan="5" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td colspan="5"><ul>
<li><p><strong>Ações fora da lista (toolbar)</strong></p></li>
</ul></td>
</tr>
<tr>
<td><img src="media/image33.png"
style="width:1.14583in;height:0.23958in" /></td>
<td colspan="4">É a ação que inicia a geração de um novo FOS. Gera XML
inicial.</td>
</tr>
<tr>
<td colspan="5"><ul>
<li><p><strong>Ações dentro da lista</strong></p></li>
</ul></td>
</tr>
<tr>
<td>Atualizar XML</td>
<td colspan="4"><p>Regra: Atualiza contribuição, C<strong>aso for
alterado alguma informação</strong></p>
<p><strong>Execução:
RH_PK_GERA_XML_DB.updateTotalContribuicoes</strong></p></td>
</tr>
<tr>
<td><img src="media/image34.png"
style="width:0.36458in;height:0.26042in" />: Baixar XML</td>
<td colspan="4"><p>Exporta XML :</p>
<p><strong>Execução: RH_PK_GERA_XML_DB.</strong>get_xml_by_ajax</p></td>
</tr>
<tr>
<td><img src="media/image35.png"
style="width:0.35417in;height:0.28125in" />: Ver Detalhe</td>
<td colspan="4">Abre detalhe (abaixo descrito)</td>
</tr>
<tr>
<td><img src="media/image36.png"
style="width:0.34375in;height:0.21875in" />: Eliminar</td>
<td colspan="2"><img src="media/image37.png"
style="width:2.17014in;height:1.38819in" /></td>
<td colspan="2"><p><strong>Regra: Deixar eliminar somente caso a
declaração ainda não for entregue</strong></p>
<p><strong>Execucao: RH_PK_GERA_XML_DB.removerXML()</strong></p></td>
</tr>
<tr>
<td><img src="media/image38.png"
style="width:0.38542in;height:0.3125in" />: Recibo Fos</td>
<td colspan="4"><p>Visualiza recibo</p>
<p><strong>Nota</strong>: inps deve disponibilizar um endpoint</p>
<p><em>Execucao:</em><strong>RH_PK_GERA_XML_DB</strong><em>.recibo_fos</em></p></td>
</tr>
</tbody>
</table>

#### NOVO SEGURADO

<img src="media/image39.jpeg" style="width:6.53542in;height:3.66667in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th colspan="2" style="text-align: left;"><strong>REGRAS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><ul>
<li><p>Não pode ser gerado! Mês de Referência não pode ser superior a
mes sysdate</p></li>
</ul>
<p>Execução: Invoca o seguinte Procedimento:</p>
<ul>
<li><p><em><strong>RH_PK_GERA_XML_DB.configXML</strong></em>(p_mes_referencia
varchar2, p_tipo varchar2)</p>
<ul>
<li><p>p_mes_referencia : = mês do formulário</p></li>
<li><p>P_tipo := <strong>PRIME</strong></p></li>
</ul></li>
</ul>
<p><strong>Resultado:</strong></p>
<p>• Header criado em RH_XML_FOS</p>
<p>• Linhas criadas em RH_DET_XML_FOS</p>
<p>• XML disponível para edição e submissão</p></td>
</tr>
<tr>
<td><p>O procedimento Grava nas seguintes Tabelas:</p>
<ol type="1">
<li><p><strong>RH_XML_FOS</strong></p></li>
</ol>
<ul>
<li><p><strong>ID</strong> = Auto incremente</p></li>
<li><p><strong>NU_CONTRIBUINTE</strong> = Nº Contribuinte
(INPS)</p></li>
<li><p><strong>ANO</strong>= Ano do processamento</p></li>
<li><p><strong>MES =</strong> Mês do processamento</p></li>
<li><p><strong>DT_REGISTO</strong> = Sysdate</p></li>
<li><p><strong>DT_ENTREGA</strong> = Data de entrega da FOS</p></li>
<li><p><strong>TT_REMUNERACAO =</strong> Total remuneração</p></li>
<li><p><strong>TT_CONTRIBUICAO</strong> = (Total remuneração
*24,5/100)</p></li>
<li><p><strong>TP_ENTREGA</strong> = Data da ultima gravação
(update)</p></li>
<li><p><strong>OBS =</strong> Texto</p></li>
<li><p><strong>XML_ENTRAGA =</strong> XML gerado</p></li>
<li><p><strong>ID_USER_REG</strong> = ID do utilizador que
gerou</p></li>
<li><p><strong>NUM_DC =</strong> Numero Documento Cobrança</p></li>
<li><p><strong>TT_CONTRIB_CALC</strong> = (Total remuneração
*24,5/100)</p></li>
<li><p><strong>USER_UPDATE</strong> = Null</p></li>
<li><p><strong>DT_UPDATE =</strong> Null</p></li>
</ul></td>
<td><ol start="2" type="1">
<li><p><strong>RH_DET_XML_FOS</strong></p></li>
</ol>
<ul>
<li><p><strong>ID</strong> = Auto incremente</p></li>
<li><p><strong>NU_SEGURADO</strong> = Nº Segurado</p></li>
<li><p><strong>ID_FUNC</strong> = RH_FUNCIONARIOS.id</p></li>
<li><p><strong>NU_TRAB_AUTO</strong> = Dias Trabalhados (30 -
Faltas)</p></li>
<li><p><strong>NU_TRAB_MAN</strong> = Igual a NU_TRAB_AUTO</p></li>
<li><p><strong>DT_REGISTO</strong> = sysdate</p></li>
<li><p><strong>VL_REMUN_AUTO</strong> = Valor Processado</p></li>
<li><p><strong>VL_REMUN_MAN</strong> = Igual a VL_REMUN_AUTO</p></li>
<li><p><strong>TIPO</strong> = ‘SL’ ou ‘OT’ ou ‘SN’ ou ‘SF’ ou
‘HE’</p></li>
<li><p><strong>ID_XML_FOS</strong> = RH_XML_FOS.id</p></li>
<li><p><strong>ID_USER_UPDATE</strong> = Null</p></li>
<li><p><strong>DATA_UPDATE</strong> = Null</p></li>
<li><p><strong>DIR_SERV_ID</strong> = Direção do colaborador</p></li>
</ul></td>
</tr>
</tbody>
</table>

#### Ver Detalhe

<img src="media/image40.png" style="width:8.76667in;height:5.00972in" />

<table style="width:100%;">
<colgroup>
<col style="width: 14%" />
<col style="width: 15%" />
<col style="width: 5%" />
<col style="width: 2%" />
<col style="width: 12%" />
<col style="width: 15%" />
<col style="width: 34%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="4"
style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Mês Referência</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="4" style="text-align: center;"></td>
<td><strong>RH_XML_FOS.</strong>ANO<strong>||'-'||
RH_XML_FOS.</strong>MES</td>
</tr>
<tr>
<td style="text-align: center;">Tipo Entrega</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="4" style="text-align: center;"></td>
<td><strong>RH_XML_FOS.</strong> TP_ENTREGA</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td colspan="4"
style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Nº</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="4" style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;">Nº Segurado</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="4" style="text-align: center;"></td>
<td><strong>RH_DET_XML_FOS.</strong> NU_SEGURADO</td>
</tr>
<tr>
<td style="text-align: center;">Nome</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="4"
style="text-align: center;"><strong>RH_FUNCIONARIOS.NOME</strong></td>
<td><strong>RH_DET_XML_FOS.</strong> ID_FUNC</td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="4" style="text-align: center;">FUNCTION:
GET_NOME_DIRECAO(RH_DET_XML_FOS.dir_serv_id)</td>
<td><strong>RH_DET_XML_FOS</strong>.DIR_SERV_ID</td>
</tr>
<tr>
<td style="text-align: center;">Dias de Trabalho</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="4" style="text-align: center;">(30 – faltas no mês anterior
ao processamento)</td>
<td><strong>RH_DET_XML_FOS.</strong> NU_TRAB_MAN</td>
</tr>
<tr>
<td style="text-align: center;">Remuneração</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="4" style="text-align: center;"></td>
<td><strong>RH_DET_XML_FOS.</strong> VL_REMUN_MAN</td>
</tr>
<tr>
<td style="text-align: center;">Tipo Remuneração</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td colspan="4" style="text-align: center;"></td>
<td><strong>RH_DET_XML_FOS.</strong>TIPO</td>
</tr>
<tr>
<td style="text-align: center;">ID</td>
<td style="text-align: center;"><em>HIDDEN</em></td>
<td colspan="4" style="text-align: center;"></td>
<td><strong>RH_DET_XML_FOS.</strong>ID</td>
</tr>
<tr>
<td style="text-align: center;">id_func</td>
<td style="text-align: center;"><em>HIDDEN</em></td>
<td colspan="4" style="text-align: center;"></td>
<td><strong>RH_DET_XML_FOS.</strong> ID_FUNC</td>
</tr>
<tr>
<td style="text-align: center;">id_xml</td>
<td style="text-align: center;"><em>HIDDEN</em></td>
<td colspan="4" style="text-align: center;"></td>
<td><strong>RH_XML_FOS.</strong>ID</td>
</tr>
<tr>
<td colspan="7" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ul>
<li><p>Lista é preenchida pela seguinte query:</p></li>
</ul>
<p>SELECT xf.*, x.ano||'-'||x.mes as mes, x.tp_entrega, x.id as id_xml,
f.nome</p>
<p>from RH_DET_XML_FOS xf, RH_XML_FOS x, rh_funcionarios f</p>
<p>where x.id = xf.ID_XML_FOS</p>
<p>and xf.ID_FUNC = f.id</p>
<p>AND (tp_field.frm_p_id_xml is null or tp_field.frm_p_id_xml = x.id
)</p>
<p>AND (v_mes_ref is null or v_mes_ref = x.ano||x.mes )</p>
<p>AND (tp_field.frm_p_direcao is null or tp_field.frm_p_direcao =
xf.dir_serv_id)</p>
<p>ORDER BY f.nome asc, xf.TIPO desc</p></td>
<td colspan="2" style="text-align: center;"><ul>
<li><p>Não pode remover linha após envio</p></li>
<li><p>Recalcular totais ao remover linha</p></li>
<li><p>Envio fecha XML</p>
<ul>
<li><p>Não permite remover XML</p></li>
<li><p>Não permite editar</p></li>
</ul></li>
</ul>
<ul>
<li><p>Não deve ser permitido a um colaborador ter mais do que uma
Remuneração para cada tipo de pagamento;</p></li>
<li><p>Outras Remunerações, Subsidio Natal e Subsidio Férias o numero de
dia de trabalho = 0;</p></li>
<li><p>Assinalar com um cor vermelha quando o Nº Segurado for
null;</p></li>
<li><p>Não deve gerar XML caso um colaborador não tiver o Nº
Segurado</p></li>
<li><p>Assinalar com uma cor amarela quando o numero de dias trabalhados
for inferior a 30 e o tipo de remuneração for Salário;</p></li>
<li><p>Todos os campos na lista são obrigatórios para gravar.</p></li>
</ul></td>
</tr>
<tr>
<td colspan="7" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td colspan="7" style="text-align: center;"><ul>
<li><p>AÇÕES DENTRO DA LISTA</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image41.png"
style="width:0.32292in;height:0.26042in" /></td>
<td colspan="2" style="text-align: center;"><img src="media/image42.png"
style="width:2.10069in;height:1.32292in" /></td>
<td colspan="4" style="text-align: center;"><p>Elimina o registo data
tabela RH_DET_XML_FOS e update na tabela RH_XML_FOS (TT_REMUNERACAO e
TT_CONTRIBUICAO)</p>
<p><strong>Ojectivo</strong>:</p>
<p>• Remove uma linha da tabela RH_DET_XML_FOS</p>
<p>• Recalcula o total de remuneração</p>
<p>• Recalcula a contribuição a 24,5%</p>
<p>• Atualiza o header RH_XML_FOS</p>
<p>Sempre que uma linha é removida, o sistema recalcula remuneração
total e contribuição.</p>
<p><strong>Execução:RH_PK_GERA_XML_DB.</strong>removerBodyXML(p_id_body_xml
number)</p></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image43.png"
style="width:0.34375in;height:0.3125in" /></td>
<td colspan="6" style="text-align: center;"><p>Adicionar: adiciona mais
um elemento a lista</p>
<p><img src="media/image44.png"
style="width:8.29792in;height:1.13681in" /></p></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image45.png"
style="width:0.33333in;height:0.28125in" /></td>
<td colspan="2" style="text-align: center;"><img src="media/image46.png"
style="width:2.10278in;height:1.35347in" /></td>
<td colspan="4" style="text-align: center;"><p>Atualizar linha</p>
<p><strong>Execução:</strong>inpsrh.<strong>RH_PK_GERA_XML_DB</strong>.gravarNovaLinhaXML</p></td>
</tr>
<tr>
<td colspan="7" style="text-align: center;"><ul>
<li><p><strong>AÇÕES FORA DA LISTA</strong></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image47.png"
style="width:1.17708in;height:0.17639in" /></td>
<td colspan="3" style="text-align: center;"><img src="media/image48.png"
style="width:2.06944in;height:1.36111in" /></td>
<td colspan="3" style="text-align: center;"><p>(<em>Reset das
informações na tabela <strong>RH_XML_FOS</strong> e
<strong>RH_DET_XML_FOS</strong></em>)</p>
<p><em><strong>Ojectivo:</strong></em> Esta ação serve para
<strong>reconstruir o FOS</strong> com base nos dados originais do
processamento salarial e assiduidade, descartando alterações manuais
feitas nas linhas. Restaurar apaga e recria (Ao restaurar, o sistema
elimina o XML atual e gera um novo XML para o mesmo mês)</p>
<ul>
<li><p><em>Apaga o header e as linhas do XML atual</em></p></li>
<li><p><em>Volta a gerar o XML do zero para o mesmo mês</em></p></li>
<li><p><em>Mantém o tipo de entrega anterior</em></p></li>
</ul>
<p><em><strong>Regra</strong>: Esse botão só aparece quando ainda não
existir uma declaração entregue. O campo RH_XML_FOS.DT_ENTREGA deve
estar nulo.</em></p>
<p><em><strong>Execução</strong>: para ver como funciona , ver o
seguinte procedimento -</em>
<strong>RH_PK_GERA_XML_DB.</strong><em>restaurarXML(p_mes_referencia
varchar2, p_id number)</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image49.png"
style="width:1.32708in;height:0.22708in" /></td>
<td colspan="3" style="text-align: center;"><img src="media/image50.png"
style="width:2.16319in;height:1.68125in" /></td>
<td colspan="3" style="text-align: center;"><p>A ação “Gravar Nova Linha
XML” permite ao utilizador inserir manualmente uma nova linha no
ficheiro FOS, especificando os dados do segurado, remuneração e tipo.
Esta funcionalidade é utilizada em situações onde o processamento
automático não contempla determinados casos ou quando são necessários
ajustes manuais.</p>
<p><em><strong>Ojectivo:</strong> Adicionar outros tipos de
processamento na folha a ser enviado a INPS:</em> Esta ação permite
<strong>incluir manualmente um segurado</strong> que não tenha entrado
automaticamente na geração inicial do FOS.</p>
<ul>
<li><p><em>Solicita o número de segurado</em></p></li>
<li><p><em>Procura o funcionário elegível</em></p></li>
<li><p><em>Recalcula as remunerações desse segurado para o
mês</em></p></li>
<li><p><em>Adiciona as linhas ao XML existente</em></p></li>
</ul>
<p><em><strong>Regra</strong>. Esse botão só aparece quando ainda não
existir uma declaração entregue. O campo RH_XML_FOS.DT_ENTREGA deve
estar nulo (ver função ex: <strong>RH_GERA_XML_DB.</strong></em>
<em><strong>isXMLEntregue</strong></em></p>
<p><em><strong>Execução</strong>: para ver como funciona , ver o
seguinte procedimento :</em>
<strong>RH_PK_GERA_XML_DB<em>.configSeguradoXML(p_mes_referencia,
p_id_xml_fos, p_nr_segurado)</em></strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image51.png"
style="width:1.25417in;height:0.20486in" /></td>
<td colspan="3" style="text-align: center;"><img src="media/image52.png"
style="width:2.14375in;height:1.39653in" /></td>
<td colspan="3"
style="text-align: center;"><p><em><strong>Objectivo:</strong> envia os
direitos de segurança social ao INPS</em></p>
<p><em><strong>Regra</strong>. Esse botão só aparece quando ainda não
existir uma declaração entregue. O campo
<strong>RH_XML_FOS.DT_ENTREGA</strong> deve estar nulo (ver função ex:
<strong>RH_GERA_XML_DB.</strong></em> <em><strong>isXMLEntregue</strong>
)</em></p>
<p><em><strong>Execução</strong>: para ver como funciona , ver o
seguinte procedimento -</em>
<strong>RH_PK_GERA_XML_DB<em>.</em></strong>
<em><strong>entregar_fos_upxml_validate</strong></em></p>
<p><em><strong>Nota: inps deve disponibilizar um serviço para essa
funcionalidade</strong></em></p>
<ul>
<li><p><em><strong>INVOCA O API INPS</strong></em></p></li>
<li><p><em><strong>update dt_entrega</strong></em></p></li>
</ul>
<p>O envio não é apenas “download”; ele chama uma rotina de
validação/entrega e devolve mensagens de erro ou sucesso.</p></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image53.png"
style="width:1.30417in;height:0.21736in" /></td>
<td colspan="3" style="text-align: center;"><img src="media/image54.png"
style="width:2.12014in;height:1.35347in" /></td>
<td colspan="3" style="text-align: center;"><p>A ação <strong>Substituir
Folha</strong> permite gerar um novo XML do tipo
<strong>SUBSTITUIÇÃO</strong>, com base num XML já existente, para o
mesmo mês de referência.</p>
<p><em><strong>Objectivo:</strong> funcionalidade destinada a substituir
o ficheiro XML que já foi entregue<strong>.</strong></em></p>
<p><em><strong>Regra</strong>. Esse botão só aparece quando ainda não
existir uma declaração entregue. O campo
<strong>RH_XML_FOS.DT_ENTREGA</strong> deve estar nulo.</em></p>
<ul>
<li><p>Pode coexistir com PRIME (Não elimina o anterior
(histórico))</p></li>
<li><p>Só faz sentido se já existir XML (Não se substitui algo que não
existe)</p></li>
<li><p>Mantém mês de referência (Não altera período)</p></li>
<li><p>Tipo de entrega = SUBST (tp_entrega =
<strong>'SUBST'</strong>)</p></li>
</ul>
<p><em><strong>Execução</strong>: para ver como funciona , ver o
seguinte procedimento -</em> <strong>RH_PK_GERA_XML_DB<em>.substituirXML
</em></strong>configXML(p_mes_referencia, 'SUBST')</p>
<p>Ele <strong>não cria um XML normal (PRIME),</strong> Ele chama a
mesma lógica de geração:</p></td>
</tr>
<tr>
<td style="text-align: center;"><img src="media/image55.png"
style="width:1.70486in;height:0.29375in" /></td>
<td colspan="6" style="text-align: center;"><em>Volta para pagina
inicial</em></td>
</tr>
</tbody>
</table>

## SOAT 

No soat o rh alguns momentos tem necessidade de fazer algum ajusto nos
valores , principalmente em caso de baixa.. que o valor que recebeu no
mês não ºe igual ao valor processado

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
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
<td>Data Referente</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Direção</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Seccão</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Direção</td>
<td></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Seccão</td>
<td></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</td>
</tr>
<tr>
<td>Nome</td>
<td></td>
<td></td>
<td>RH_T_FUNCIONARIO.NOME</td>
</tr>
<tr>
<td>Data Nascimento</td>
<td></td>
<td></td>
<td>RH_T_FUNCIONARIO.DATA_NASCIMENTO</td>
</tr>
<tr>
<td>Tipo documento</td>
<td></td>
<td></td>
<td>RH_T_FUNCIONARIO.TP_DOCUMENTO</td>
</tr>
<tr>
<td>Tipo Documento</td>
<td></td>
<td></td>
<td>RH_T_FUNCIONARIO.NUM_DOCUMENTO</td>
</tr>
<tr>
<td>NIF</td>
<td></td>
<td></td>
<td>RH_T_FUNCIONARIO.NIF</td>
</tr>
<tr>
<td>Profissão</td>
<td></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4"></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td>EXPORTAR EXCEL</td>
<td colspan="3">Permite Exportat um ficheiro excel.</td>
</tr>
<tr>
<td>AJUSTAR VALOR</td>
<td colspan="3">DEIXAR ALGUNS CAMPOS QUE PODEM SER ALTERADOS MEDIANTE
JUSTIFICAÇÃO. POPDE SER EDITADO MEDIANTE JUSTIFICACAO</td>
</tr>
</tbody>
</table>

### Ficheiro Excel

<table style="width:91%;">
<colgroup>
<col style="width: 23%" />
<col style="width: 33%" />
<col style="width: 34%" />
</colgroup>
<thead>
<tr>
<th colspan="3" style="text-align: center;"><img src="media/image56.png"
style="width:7.82917in;height:4.56389in" /></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><strong>Campo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte Dados</strong></td>
</tr>
<tr>
<td colspan="3" style="text-align: left;"><strong>DADOS DO TOMADOR DE
SEGURO</strong></td>
</tr>
<tr>
<td style="text-align: left;"><strong>NOME:</strong></td>
<td style="text-align: left;">Nome do INPS CENTRAL (PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO.</strong>NOME</td>
</tr>
<tr>
<td style="text-align: left;"><strong>NIF:</strong></td>
<td style="text-align: left;">NIF do INPS CENTRAL (PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO</strong>.NIF</td>
</tr>
<tr>
<td style="text-align: left;"><strong>COD CAE:</strong></td>
<td style="text-align: left;">CAE do INPS CENTRAL (PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO.</strong>COD_CAE</td>
</tr>
<tr>
<td style="text-align: left;"><strong>ATIVIDADE ECONÓMICA:</strong></td>
<td style="text-align: left;"><strong>ATIVIDADE ECONÓMICA</strong> do
INPS CENTRAL (PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO.</strong>ATIVIDADE_ECONOMICA</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Nº CERTIDÃO COMERCIAL
(NC):</strong></td>
<td style="text-align: left;"><strong>Nº CERTIDÃO COMERCIAL
(NC):</strong> do INPS CENTRAL (PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO.</strong>NUM_CERTIDAO_COMERCIAL</td>
</tr>
<tr>
<td style="text-align: left;"><strong>DATA VALIDADE NC:</strong></td>
<td style="text-align: left;"><strong>DATA VALIDADE NC:</strong> do INPS
CENTRAL (PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO.</strong>DATA_VALIDADE</td>
</tr>
<tr>
<td style="text-align: left;"><strong>TELEFONE:</strong></td>
<td style="text-align: left;"><strong>TELEFONE:</strong> do INPS CENTRAL
(PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO.</strong>TELEFONE</td>
</tr>
<tr>
<td style="text-align: left;"><strong>TELEMÓVEL:</strong></td>
<td style="text-align: left;"><strong>TELEMÓVEL:</strong> do INPS
CENTRAL (PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO.</strong>TELEMOVEL</td>
</tr>
<tr>
<td style="text-align: left;"><strong>LOCALIDADE:</strong></td>
<td style="text-align: left;"><strong>LOCALIDADE:</strong> do INPS
CENTRAL (PRAIA)</td>
<td style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO.</strong>
LOCALIDADE:</td>
</tr>
<tr>
<td style="text-align: left;"><strong>EMAIL:</strong></td>
<td style="text-align: left;"><strong>EMAIL:</strong> do INPS CENTRAL
(PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO</strong>.EMAIL</td>
</tr>
<tr>
<td style="text-align: left;"><strong>MORADA:</strong></td>
<td style="text-align: left;"><strong>MORADA:</strong> do INPS CENTRAL
(PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO</strong>.MORADA</td>
</tr>
<tr>
<td style="text-align: left;"><strong>LOCALIDADE:</strong></td>
<td style="text-align: left;"><strong>LOCALIDADE:</strong> do INPS
CENTRAL (PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO.</strong>LOCALIDADE</td>
</tr>
<tr>
<td style="text-align: left;"><strong>CONCELHO:</strong></td>
<td style="text-align: left;"><strong>CONCELHO:</strong> do INPS CENTRAL
(PRAIA)</td>
<td
style="text-align: left;"><strong>RH_T_DADOS_INSTITUICAO</strong>.CONCELHO</td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>APÓLICE Nº:</strong></td>
<td style="text-align: center;"><p><strong>NUMERO REFERENTE
ZONA:</strong></p>
<ul>
<li><p><strong>SAL</strong></p></li>
<li><p><strong>SAO VICENTE (SV, SA, SN, BV) – ZONA
NORTE</strong></p></li>
<li><p><strong>SANTIAGO (SA, FG, MA, BR)</strong></p></li>
</ul></td>
<td
style="text-align: left;"><strong>RH_T_DADOS_APOLICE.</strong>NUM_APOLICE</td>
</tr>
<tr>
<td style="text-align: left;"><strong>DATA INÍCIO DA
APÓLICE:</strong></td>
<td style="text-align: center;"><strong>ESSA INFORMAÇÃO TEM TEM SIDO
PREENCHIDO</strong></td>
<td
style="text-align: left;"><strong>--------------------------------------</strong></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>DATA DE REFERÊNCIA
(*)</strong></td>
<td style="text-align: center;"><strong>Mês e ano referente ,</strong>
que coincide com mês de processamento</td>
<td style="text-align: center;">RH_T_PROC_SALARIOS.
<em>DATA_PROC_PROVISORIO</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Nº TOTAL DE PESSOAS
SEGURAS</strong></td>
<td style="text-align: center;"><strong>NUMERO TOTAL DE
COLABORADORES</strong></td>
<td
style="text-align: center;"><strong>COUNT</strong>(<strong>RH_T_PROC_FUNCIONARIOS</strong>.TIPREL_ID)</td>
</tr>
<tr>
<td style="text-align: left;"><strong>MASSA SALARIAL ANUAL
SEGURA</strong></td>
<td style="text-align: center;"><strong>VALOR TOTAL ANNUAL (X
12)</strong></td>
<td style="text-align: center;"><strong>RH_T_REMUNERACOES.</strong>
VALOR_REAL</td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>SEGURO NOVO /
EXISTENTE:</strong></td>
<td style="text-align: center;"><strong>NULL</strong></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>MODALIDADE:</strong></td>
<td style="text-align: center;"><strong>NULL</strong></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>NOME COMPLETO (*)</strong></td>
<td style="text-align: left;"><strong>Indicar o nome completo de cada
colaborador</strong></td>
<td
style="text-align: center;"><strong>RH_T_FUNCIONARIO.</strong>NOME</td>
</tr>
<tr>
<td style="text-align: left;"><strong>TIPO DOC. IDENT. (*)</strong></td>
<td style="text-align: left;"><p><strong>Deve-se indicar um dos três (3)
tipos documentos de identificação:</strong></p>
<ul>
<li><p><strong>NIC (número de identificação civil) ou BI (bilhete de
identidade para os colaboradores nacionais;</strong></p></li>
<li><p><strong>Passaporte para colaboradores
estrangeiros.</strong></p></li>
</ul></td>
<td
style="text-align: left;"><strong>RH_T_FUNCIONARIO.</strong>TP_DOCUMENTO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Nº DOC. IDENT. (*)</strong></td>
<td style="text-align: left;"><strong>Inserir o número de identificação
(se TIPO DOC. IDENT. = NIC, deve-se introduzir 13
carateres).</strong></td>
<td style="text-align: left;"><strong>RH_T_FUNCIONARIO.</strong>
NUM_DOCUMENTO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>DATA VALIDADE</strong></td>
<td style="text-align: left;"><strong>Indicar a data de validade do
documento de identificação com o seguinte formato
dd/mm/aaaa.</strong></td>
<td
style="text-align: center;"><strong>RH_T_DOCUMENTO_PESSOAL.</strong>DATA_VALIDADE</td>
</tr>
<tr>
<td style="text-align: left;"><strong>NIF (*)</strong></td>
<td style="text-align: left;"><strong>Indicar o NIF (número de
identificação fiscal) de cada colaborador (9 dígitos)</strong></td>
<td style="text-align: left;"><strong>RH_T_FUNCIONARIO.</strong>NIF</td>
</tr>
<tr>
<td style="text-align: left;"><strong>DATA NASCIMENTO</strong></td>
<td style="text-align: left;"><strong>Indicar a data de nascimento de
cada colaborador com o seguinte formato dd/mm/aaaa.</strong></td>
<td style="text-align: left;"><strong>RH_T_FUNCIONARIO.</strong>
DATA_NASCIMENTO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>SEXO (*)</strong></td>
<td style="text-align: left;"><strong>Indicar o sexo de cada colaborador
(Masculino ou Feminino)</strong></td>
<td
style="text-align: left;"><strong>RH_T_FUNCIONARIO.</strong>SEXO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>SITUAÇÃO (*)</strong></td>
<td style="text-align: left;"><p><strong>Indicar:</strong></p>
<p><strong>I - Incluir: para incluir colaboradores novos</strong></p>
<p><strong>E - Excluir: para excluir colaboradores existentes na
apólice</strong></p>
<p><strong>A - Alterar: sempre que haja qualquer alteração nos dados dos
colaboradores existentes na apólice</strong></p>
<p><strong>M - Manter: para os colaboradores existentes na apólice, sem
qualquer alteração</strong></p></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>PROFISSÃO (*)</strong></td>
<td style="text-align: center;"><strong>Indicar a profissão/função que
cada colaborador exerce na empresa/instituição</strong></td>
<td
style="text-align: left;"><strong>RH_T_TIPOS_RELACIONAMENTO.CARGO</strong></td>
</tr>
<tr>
<td style="text-align: left;"><strong>APRENDIZ OU ESTAGIÁRIO
(*)</strong></td>
<td style="text-align: left;"><p><strong>Indicar:</strong></p>
<p><strong>Sim: se o colaborador é aprendiz, eventual, temporário,
estagiário ou praticante</strong></p></td>
<td style="text-align: left;"><strong>Depende do tipo de contrato
RH_T_CONTRATO_VINCULO</strong>.TIPO_CONTRATO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Nº HORAS SEMANA (*)</strong></td>
<td style="text-align: left;"><p><strong>Indicar o número de horas
semanais de cada colaborador (o período normal de trabalho previsto no
artigo 149º do Código Laboral não pode ultrapassar 44 horas por
semana).</strong></p>
<p><strong>Nas situações em que o colaborador não trabalha todos os dias
da semana, o nº de horas por semana deve ser calculado da seguinte
formula: 44 *( o número de dias de trabalho efetivo do mês)/(30
dias).</strong></p></td>
<td style="text-align: center;"><strong>Calcular 8 horas por semana –
horas que faltou</strong></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RETRIBUIÇÃO BASE (*)</strong></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>HORA /DIA/ MÊS (*)</strong></td>
<td style="text-align: left;"><p><strong>Indicar:</strong></p>
<p><strong>H - HORA: se a retribuição é paga ao colaborador à
hora;</strong></p>
<p><strong>D - DIA: se a retribuição é paga ao colaborador ao
dia;</strong></p>
<p><strong>M - MÊS: se a retribuição é paga ao colaborador ao
mês.</strong></p></td>
<td style="text-align: center;"><strong>D</strong></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RETRIBUIÇÃO HORA /DIÁRIO/MENSAL
(*)</strong></td>
<td style="text-align: left;"><p><strong>Inserir a retribuição de acordo
com a periodicidade do pagamento (Hora/Diário/Mensal).</strong></p>
<ul>
<li><p><strong>Se HORA/DIA/MÊS = H - HORA, deve-se introduzir a
retribuição paga a hora;</strong></p></li>
<li><p><strong>Se HORA/DIA/MÊS = D - DIA, deve-se introduzir a
retribuição paga por dia;</strong></p></li>
<li><p><strong>Se HORA/DIA/MÊS = M - MÊS, deve-se introduzir a
retribuição paga por mês.</strong></p></li>
</ul></td>
<td
style="text-align: center;"><strong>RH_T_PROC_FUNCIONARIOS.</strong>TOT_LIQUIDO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>RETRIBUIÇÃO ANUAL
(*)</strong></td>
<td style="text-align: left;"><p><strong>Inserir o salário anual
auferido pelo colaborador.</strong></p>
<p><strong>O valor a indicar não pode ser inferior a 12 vezes do salário
mínimo nacional em vigor.</strong></p></td>
<td
style="text-align: center;"><strong>(RH_T_PROC_FUNCIONARIOS.</strong>TOT_LIQUIDO)
X 12</td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>TRABALHADOR TEMPORARIAMENTE NO
ESTRANGEIRO (*)</strong></td>
<td style="text-align: left;"><p><strong>Indicar:</strong></p>
<p><strong>Sim: se o colaborador está temporariamente no estrangeiro ao
serviço</strong></p>
<p><strong>Não: caso contrário</strong></p></td>
<td style="text-align: center;"><strong>Ver a partir de
RH_T_TIPOS_RELACIONAMENTO.</strong>LOCAL_TRAB_ID</td>
</tr>
<tr>
<td style="text-align: left;"><strong>OBSERVAÇÕES</strong></td>
<td style="text-align: left;"><strong>Indicar as observações relevantes
sobre cada colaborador</strong></td>
<td style="text-align: center;"></td>
</tr>
</tbody>
</table>

## Aumento Salarial 

### Registo Criterios aumento

<img src="media/image57.png" style="width:9.68194in;height:2.40764in" />

<img src="media/image58.png" style="width:9.68264in;height:4.05694in" />

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 5%" />
<col style="width: 42%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Formulário</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Gravação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Designação Aumento</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL.DESCRICAO</em></td>
</tr>
<tr>
<td>Motivo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL.MOTIVO</em></td>
</tr>
<tr>
<td>Data Referencia do aumento</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL.DATA_REFERENTE</em></td>
</tr>
<tr>
<td>Percentagem</td>
<td></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL.PERCENTAGEM</em></td>
</tr>
<tr>
<td colspan="4"><em><strong>Lista colaborador</strong></em></td>
</tr>
<tr>
<td>Direção</td>
<td><em>SELECT</em></td>
<td>INPSSIGOF.INSTITUICOES</td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</em></td>
</tr>
<tr>
<td>Unidade</td>
<td><em>SELECT</em></td>
<td>RH_T_SECAO</td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</em></td>
</tr>
<tr>
<td>Lista</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Colaborador</td>
<td></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL_DET.FUN_ID</em></td>
</tr>
<tr>
<td>Carreira</td>
<td></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL_DET.CARREIRA_ID</em></td>
</tr>
<tr>
<td>Nivel / Escalão</td>
<td><em>SELETCT</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL_DET.NIVEL_ESCALAO</em></td>
</tr>
<tr>
<td>Salário Antes:</td>
<td><em>NUMBER</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL_DET.SALARIO_ANTES</em></td>
</tr>
<tr>
<td>Salário Depois:</td>
<td><em>NUMBER</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL_DET.SALARIO_DEPOIS</em></td>
</tr>
<tr>
<td colspan="4">Acoes</td>
</tr>
<tr>
<td>Gravar</td>
<td colspan="3"><p><em>Ao confirma deve gravar nas seguintes
Tabelas</em></p>
<ul>
<li><p><em>RH_T_AUMENTO_SALARIAL</em></p></li>
<li><p><em>Pccs_id = id RH_T_PARAM_PCCS (Pega o id de pccs cujo data fim
é nulo e estado ativo)</em></p></li>
<li><p><em>RH_T_AUMENTO_SALARIAL_DET (essa tabela regista todos
colaboradores sujeitos a aumento)</em></p></li>
<li><p><em>RH_T_VALIDACAO</em></p></li>
</ul>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘AUMENTO_SALARIAL’
(</strong>DOMAINS = ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
RH_T_AUMENTO_SALARIAL</em></p></li>
<li><p><em>FUN_ID <strong>= NUL</strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Validação

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><p><em>Ao validar deve invocar o serviço de aumento
Salarial</em></p>
<ul>
<li><p><em>Esse serviço deve invocar o Procedimento de aumento Salarial
(<strong>PKG_AUMENTO_SALARIAL.</strong>SIMULAR_AUMENTO)</em></p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

### Lista Aumento

<table style="width:92%;">
<colgroup>
<col style="width: 14%" />
<col style="width: 5%" />
<col style="width: 34%" />
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
<td>Ano Referente</td>
<td><em>Select</em></td>
<td>Deve somente extrair o ano</td>
<td><em>RH_T_AUMENTO_SALARIAL.DATA_REFERENTE</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Designação</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL.DESCRICAO</em></td>
</tr>
<tr>
<td>Motivo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL.MOTIVO</em></td>
</tr>
<tr>
<td>Data Referente</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL.DATA_REFERENTE</em></td>
</tr>
<tr>
<td>Percentagem</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL.PERCENTAGEM</em></td>
</tr>
<tr>
<td>Data Registo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_AUMENTO_SALARIAL.DATA_REGISTO</em></td>
</tr>
<tr>
<td><strong>Acoes</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3"><p><em>Abre o mesmo formulário de Registo</em></p>
<p><em><strong>Nota:</strong> Não deve ser permitido editar um registo
que ainda não tenha sido validado; nesses casos, o registo deve estar
apenas disponível para visualização pelo utilizador.</em></p></td>
</tr>
</tbody>
</table>

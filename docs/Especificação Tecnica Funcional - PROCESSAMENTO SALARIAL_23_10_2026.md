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

<table style="width:99%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<thead>
<tr>
<th
style="text-align: center;"><p><strong>Funcionalidade</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Lista Processamento</p></td>
<td><p>Lista dados os processamentos efetuados</p></td>
</tr>
<tr>
<td style="text-align: center;"><ul>
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
<li><p><strong>Retroativo</strong>: Deferença salarial</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><ul>
<li><p>Eliminar Proc</p></li>
</ul></td>
<td><p>Permite remover um processamento salarial que ainda não tenha
sido previamente validado, garantindo que apenas processamentos
confirmados permaneçam registados no sistema.</p></td>
</tr>
<tr>
<td style="text-align: center;"><ul>
<li><p>Validar</p></li>
</ul></td>
<td><p>Confirma e consolida o processamento salarial realizado,
bloqueando alterações posteriores e atualizando os respetivos registos
para efeitos de pagamento, contabilização e histórico.</p></td>
</tr>
<tr>
<td style="text-align: center;"><ul>
<li><p>Validar Provisorio</p></li>
</ul></td>
<td><p>Permitir o processamento salaria de forma Provisorio</p></td>
</tr>
<tr>
<td style="text-align: center;"><ul>
<li><p>Validar Definitivo</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><ul>
<li><p>Cabimentar</p></li>
</ul></td>
<td><p>Permite realizar automaticamente o cabimento orçamental através
da invocação de um serviço do sistema financeiro, assegurando a reserva
dos recursos necessários para o pagamento da folha salarial.</p></td>
</tr>
<tr>
<td style="text-align: center;"><ul>
<li><p>Eliminar Cab.</p></li>
</ul></td>
<td><p>Permite remover um cabimento que ainda não tenha sido autorizado,
mediante a invocação do serviço correspondente no sistema
financeiro.</p></td>
</tr>
<tr>
<td style="text-align: center;"><ul>
<li><p>Autorizar</p></li>
</ul></td>
<td><p>Permite autorizar o cabimento previamente efetuado, através da
invocação do serviço correspondente no sistema financeiro</p></td>
</tr>
<tr>
<td style="text-align: center;"><ul>
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
<td style="text-align: center;"><p>Marcar N/ Processar</p>
<p>Desmarcar Para Processar</p></td>
<td><p>Esta funcionalidade é útil quando há necessidade de excluir
determinados funcionários ou períodos da folha de pagamento, seja por
licenças não remuneradas, afastamentos temporários ou outros
motivos.</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>LICENÇA SEM VENCIMENTO</p></td>
<td><p>Permite listar todos os colaboradores que se encontram em licença
sem vencimento, bem como registar novas licenças, atualizar o respetivo
período e ajustar automaticamente a situação laboral do colaborador no
sistema.</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>IMPORTAR DESCONTO</p></td>
<td><p>Permite importar dados de cantina e outros para</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>FOS</p></td>
<td><p>Envio de folha salarial para sistema core de INPS</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>SOAT</p></td>
<td><p>Permite listar e exportar dados em formato Excel a
segurador</p></td>
</tr>
</tbody>
</table>

# Especificação 

## Desmarcar Para Processar

<img src="media/image5.png" style="width:9.69306in;height:4.44097in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 6%" />
<col style="width: 37%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte Dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><p>Nome colaorador</p></td>
<td><p>loockup</p></td>
<td></td>
<td><p>RH_T_RUNCIONARIOS.NOME</p></td>
</tr>
<tr>
<td style="text-align: left;"><p>Direção</p></td>
<td><p>select</p></td>
<td></td>
<td style="text-align: left;"><p>RH_T_DIRECAO.NOME;
RH_T_MOBILIDADE.INSTIT_ID, RH_T_TIPOS_RELACIONAMENTO.MOB_ID</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>Lista</strong></p></td>
<td></td>
<td></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><p>Nome Colaborador</p></td>
<td><p>TEXT</p></td>
<td></td>
<td><p>RH_T_RUNCIONARIOS.NOME</p></td>
</tr>
<tr>
<td style="text-align: left;"><p>NIF</p></td>
<td><p>TEXT</p></td>
<td></td>
<td><p>RH_T_RUNCIONARIOS.NIF</p></td>
</tr>
<tr>
<td style="text-align: left;"><p>Direção</p></td>
<td><p>TEXT</p></td>
<td></td>
<td><p>RH_T_DIRECAO.NOME; RH_T_MOBILIDADE.INSTIT_ID,
RH_T_TIPOS_RELACIONAMENTO.MOB_ID</p></td>
</tr>
<tr>
<td style="text-align: left;"><p>selecionar</p></td>
<td><p>check</p></td>
<td><p>Deve razer por defeito os colaboradores selecionados.</p></td>
<td><p>RH_T_TIPOS_RELACIONAMENTO.ID</p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p>REGRAS</p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p><em>A Lista deve ilustrar todos os colaboradores de uma
determinada direção aptos para processar, ou seja, nestas seguintes
condições</em></p>
<ul>
<li><p><em><mark>Por defeito todos os colaboradores devem estar
marcados. Ao Desmarcar um colaborador ele nao Devera receber
Salario.</mark></em></p></li>
<li><p><em>O colaborador deve estar ativo</em></p></li>
<li><p><em>O colaborador deve ter o vínculo ativo
(<strong>RH_TIPOS_RELACIONAMENTO.EST_ACT_ADM = 1</strong>)</em></p></li>
<li><p><em>o salário deve estar ativo <strong>RH_T_DEF_REMUNERACAO =
A</strong></em></p></li>
<li><p><em><mark>não deve trazer colaboradores em situação laboral que
não receberá salário</mark> <strong><mark>-
RH_T_PARAM_SITUACAO.FLG_REMUNERACAO (ex: Licença sem
Retribuição)</mark></strong></em></p></li>
</ul></li>
</ul></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p>Ações</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p></td>
<td colspan="3"><p>Ao clicar no botão gravar o sistema deve fazer a
seguinte ação:</p>
<ul>
<li><p><strong>RH_T_TIPOS_RELACIONAMENTO.FLG_PROCESSA =
0</strong></p></li>
</ul>
<p><mark><strong>Nota:</strong> atualiza o estado aos colaboradores
Desmarcados</mark></p></td>
</tr>
</tbody>
</table>

## Lista Processamento

<img src="media/image7.png" style="width:9.69306in;height:3.08125in" />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Data Inicio</p></td>
<td><p><em>DATE</em></p></td>
<td></td>
<td><p><em>INPSRH.RH_T_PROC_SALARIOS.DATA_DE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Fim</p></td>
<td><p><em>DATE</em></p></td>
<td></td>
<td><p><em>INPSRH. RH_T_PROC_SALARIOS.DATA_DE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Direção</p></td>
<td><p><em>SELECT</em></p></td>
<td></td>
<td><p><em>INPSRH. RH_T_PROC_SALARIOS.CC_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Tipo</p></td>
<td><p><em>SELECT</em></p></td>
<td><p><strong>DOMAIN</strong> = TIPO_PROCESSAMENTO</p></td>
<td><p><em>RH_T_PROC_SALARIOS.TIPO_PROCESSAMENTO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Estado</p></td>
<td><p><em>SELECT</em></p></td>
<td><p><em><strong>DOMAIN</strong> = ESTADO_PROCESSAMENTO</em></p></td>
<td><p><em>INPSRH. RH_T_PROC_SALARIOS.ESTADO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Estado</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Situação atual do processamento salarial.</p></td>
<td><p><em>RH_V_LISTA_PROCESSAMENTO.ESTADO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Mês Referencia</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Mês e ano a que se refere o processamento salarial</p></td>
<td><p><em>RH_V_LISTA_PROCESSAMENTO.MES_REFERENCIA</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Código CC</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Código do <strong>Centro de Custo</strong> a que está associado o
processamento.</p></td>
<td><p><em>RH_V_LISTA_PROCESSAMENTO.</em>CODIGO_CC</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Direção</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Unidade orgânica ou direção responsável pelos colaboradores
incluídos no processamento.</p></td>
<td><p><em>RH_V_LISTA_PROCESSAMENTO.</em> <em>DIRECAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>OBS</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Campo de <strong>Observações</strong> para registar informações
complementares ou exceções relevantes.</p></td>
<td><p><em>RH_V_LISTA_PROCESSAMENTO.OBS</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Quantidade Processada</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Número total de colaboradores ou registos incluídos no
processamento salarial.</p></td>
<td><p><em>RH_V_LISTA_PROCESSAMENTO.QUANTIDADE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Cabimento</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Indicação do montante ou autorização orçamental afeta ao
processamento salarial.</p></td>
<td><p><em>RH_V_LISTA_PROCESSAMENTO.CABIMENTO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Total</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Valor monetário total resultante do processamento salarial
(somatório das remunerações e descontos).</p></td>
<td><p><em>RH_V_LISTA_PROCESSAMENTO.TOTAL</em></p></td>
</tr>
<tr>
<td colspan="4"
style="text-align: left;"><p><strong>REGRAS</strong></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p>Os processamentos eliminados não devem aparecer na lista</p></li>
</ul></td>
</tr>
<tr>
<td colspan="4"
style="text-align: left;"><p><strong>ACÇÕES</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Processar</p></td>
<td colspan="3"><p>Executa o cálculo do processamento salarial para o
período e colaboradores selecionados, gerando os valores de
remunerações, subsídios e descontos. Só se pode processar uma direção
caso existe pelo menos um colaborador que ainda não foi processado</p>
<p><strong>Regras:</strong><br />
• Um colaborador não pode ser processado mais do que uma vez no mesmo
<strong>Centro de Custo</strong> e no mesmo <strong>mês de
processamento</strong>.<br />
• O sistema deve validar que o mesmo
<strong>RH_T_TIPOS_RELACIONAMENTO.ID</strong> não possui outro
processamento registado para o mesmo Centro de Custo e para o mesmo
mês.<br />
• Caso já exista um processamento para esse colaborador, o sistema deve
impedir um novo processamento e apresentar uma mensagem informativa ao
utilizador.</p>
<p>O sistema Deve permitir processar um colaborador, uma Direção ou
Selecionar varias Direções e Processar</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Eliminar Proc</p></td>
<td colspan="3"><p><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘PROCESSADO’</strong><em>)</em></p>
<p><em><strong>Regras:</strong><br />
• Caso o processamento se encontre num estado diferente dos indicados, a
eliminação não deve ser permitida.</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Validar Provisorio</p></td>
<td colspan="3"><p><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘PROCESSADO’</strong><em>)</em></p>
<p><strong>Regras:</strong><br />
• O botão <strong>Validar</strong> apenas deve ficar ativo
(<em>enabled</em>) quando o processamento estiver no estado
<strong>PROCESSADO</strong>.<br />
• Caso seja necessário eliminar um processamento que já tenha sido
validado, o sistema deve permitir reverter o seu estado para
<strong>PROCESSADO</strong>, desde que ainda não exista Cabimento
associado.</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Validar Definitivo</p></td>
<td colspan="3"><p><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘VALIDADO_PROVISORIO</strong><em>)</em></p>
<p><em><strong>Regras:</strong><br />
• O botão <strong>Validar</strong> apenas deve ficar ativo (enabled)
quando o processamento estiver no estado
<strong>VALIDADO_PROVISORIO</strong>.<br />
• Caso seja necessário eliminar um processamento que já tenha sido
validado, o sistema deve permitir reverter o seu estado para
<strong>PROCESSADO</strong>, desde que ainda não exista Cabimento
associado.</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Cabimentar</p></td>
<td colspan="3"><p><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘VALIDADO_PROVISORIO’</strong><em>)</em></p>
<p><em><strong>Regras:</strong><br />
• Não deve ser possível cabimentar um processamento em qualquer outro
estado.<br />
• Depois de cabimentado, o processamento deixa de poder ser validado
novamente ou eliminado.<br />
• O sistema não deve permitir qualquer alteração de estado enquanto
existir um Cabimento ativo.<br />
• Para alterar o estado do processamento, deverá primeiro ser eliminado
o respetivo Cabimento.</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Eliminar Cab.</p></td>
<td colspan="3"><p><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘CABIMENTADO’</strong><em>)</em></p>
<p><strong>Regras:</strong><br />
• Caso o processamento já tenha sido <strong>AUTORIZADO</strong>, a
eliminação do Cabimento não deve ser permitida.<br />
• Caso o processamento ainda não esteja <strong>CABIMENTADO</strong>, o
botão <strong>Eliminar Cabimento</strong> deve permanecer desativado
(<em>disabled</em>).</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Autorizar</p></td>
<td colspan="3"><p><em>Esse botão só deve ficar visível caso o
Processamento estiver no estado: (</em><strong>RH_T_PROC_SALARIOS.estado
= ‘CABIMENTADO’</strong><em>)</em></p>
<p><strong>Regras:</strong><br />
• O sistema não deve permitir autorizar um processamento que não tenha
sido previamente cabimentado.<br />
• Após a autorização, o processamento passa a ser considerado
definitivo.<br />
• Depois de autorizado, deixa de ser possível executar qualquer uma das
etapas anteriores (<strong>Eliminar</strong>, <strong>Validar</strong>,
<strong>Cabimentar</strong> ou <strong>Eliminar Cabimento</strong>),
salvo se existir um mecanismo de anulação especificamente previsto para
esse efeito.</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Relatorio</p></td>
<td colspan="3"><p>Este botão fica visível em qualquer estado</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Desmarcar para / Processar</p></td>
<td colspan="3"><p>Só se pode desmarcar para processar somente direções
que ainda não foram processados</p></td>
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
<th style="text-align: center;"><p><strong>Formulario</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Gravação</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><p>Data Inicio</p></td>
<td><p><em>DATE</em></p></td>
<td></td>
<td><p><em>RH_T_PROC_SALARIOS.DATA_DE</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><p>Data Fim</p></td>
<td><p><em>DATE</em></p></td>
<td></td>
<td><p><em>RH_T_PROC_SALARIOS.DATA_ATE</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><p>Tipo</p></td>
<td><p><em>SELECT</em></p></td>
<td><p><strong>DOMAIN</strong> = TIPO_PROCESSAMENTO</p></td>
<td><p><em>RH_T_PROC_SALARIOS.TIPO_PROCESSAMENTO</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><p>Direção</p></td>
<td><p><em>lOOKUP</em></p></td>
<td><p>iNPSSIGOF.INSTITUICOES, INPSSIGOF.CENTROS_CUSTO</p></td>
<td><p><em>RH_T_PROC_SALARIOS.</em> <em>CC_ID</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><p>Observação</p></td>
<td><p><em>TEXTAREA</em></p></td>
<td></td>
<td><p><em>RH_T_PROC_SALARIOS.OBS</em></p></td>
</tr>
<tr>
<td colspan="4"
style="text-align: center;"><p><strong>REGRAS</strong></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
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
<li><p>O pode processar 1 ou várias direções ao mesmo tempo, e também
pode processar por colaborador.</p></li>
</ol></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p>AÇÕES</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p></td>
<td colspan="3"><p>Ao clicar no botão Gravar, deve invocar a seguinte
funcionalidade:</p>
<p><em><strong>RH_PROCESSAMENTO_SALARIAL_DB.</strong> PROCESSAR</em>
<em>(p_dt_inicio = Data Inicio,</em></p>
<p><em>p_dt_fim = Data fim,</em></p>
<p><em>p_cc_id = Direção (Centro de custo de uma direção),</em></p>
<p><em>p_tiprel_id =Colaborador (id de tipos de relacionamento do
Colaborador,</em></p>
<p><em>p_tipo = Tipo</em></p>
<p><em>P_user_name = nome de utilizador logado,</em></p>
<p><em>p_user_id NUMBER = id de utilizador Logado</em></p>
<p><em>p_msg = retorna mensagem de erro)</em></p></td>
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
<th style="text-align: center;"><p><strong>Formulario</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte Dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Nome</p></td>
<td><p>TEXT</p></td>
<td><p>Nome de colaborador (RH_T_FUNCIONARIO.NOME)</p></td>
<td><p>RH_T_FUNCIONARIO.NOME</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Direcao</p></td>
<td><p>LOOCKUP</p></td>
<td><p>Nome de instituições (INPSSIGOF.INSTIDUICOES.NOME)</p></td>
<td><p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID=
INPSSIGOF.INSTIDUICOES.ID</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Centro Custo</p></td>
<td><p>LOOCKUP</p></td>
<td><p>Nome de Centro de custo</p></td>
<td><p>INPSSIGOF.CENTROS_CUSTO.ID</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>TPREL_ID</p></td>
<td><p>HIDDEN</p></td>
<td><p>Deve devolver o id de tipo relacionamento</p></td>
<td><p>RH_T_TIPOS_RELACIONAMENTO.ID</p></td>
</tr>
<tr>
<td colspan="4"
style="text-align: center;"><p><strong>REGRAS</strong></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
<li><p>Pesquiza colaborador cujo vínculo é último
(<em><strong>EST_ACT_ADM = 1</strong></em>)</p></li>
</ul></td>
</tr>
<tr>
<td colspan="4"
style="text-align: center;"><p><strong>AÇÕES</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p></td>
<td colspan="3"><p><em><strong>RH_PROCESSAMENTO_SALARIAL_DB.</strong>
ELIMINAR_PROCESSAMENTO</em></p></td>
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
<td style="text-align: center;">Nome</td>
<td>TEXT</td>
<td><mark>Nome de instituições (RH_T_DIRECAO.NOME)</mark></td>
<td>RH_T_DIRECAO.ID</td>
</tr>
<tr>
<td style="text-align: center;">CC_ID</td>
<td>HIDDEN</td>
<td>Devolve o id de Centro de custo</td>
<td>RH_T_DIRECAO.ID</td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;">Na lista deve devolver
somente as direções que existe em
<em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.INSTIT_ID cujo
<mark>EST_ACT_ADM = 1</mark></em></td>
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
<th colspan="2"><p><strong>REGRAS</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><ul>
<li><p>Só é possível eliminar processamentos que se encontrem apenas no
estado “<strong>PROCESSADO’</strong>”, ou seja, que ainda não tenham
sido validados</p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><p><strong>AÇÕES</strong></p></td>
</tr>
<tr>
<td><p><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p></td>
<td><p><em><strong>RH_PROCESSAMENTO_SALARIAL_DB.</strong>ELIMINAR_PROCESSAMENTO</em></p></td>
</tr>
</tbody>
</table>

### <span class="mark">Validar Provisorio</span>

Objectivo desse formulário é verificar todas as lacunas no
processamento.

<img src="media/image9.png" style="width:7.81618in;height:4.29615in" />

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 7%" />
<col style="width: 41%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Tipo Validação</p></td>
<td><p><em>SELECT</em></p></td>
<td><ul>
<li><p>Diferencia Salarial entre Meses</p></li>
<li><p>Somatória por Tipo Movimento</p></li>
<li><p>Outros Motivos</p></li>
<li><p>Ativos não processados</p></li>
<li><p>Subsídio Atendedor</p></li>
</ul></td>
<td><p><em><strong>DOMAIN</strong> = TIPO_VALIDACAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><del>Mês Atual</del></p></td>
<td><p><em><del>SELECT</del></em></p></td>
<td><p><del>Deve trazer preenchido o mês do Processamento</del></p></td>
<td><p><em><del>TO_CHAR(rh_t_proc_salarios.DATA_DE,
‘MM’)</del></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><del>Mês Anterior</del></p></td>
<td><p><em><del>SELECT</del></em></p></td>
<td><p><del>Deve devolver o ames anterior do
processamento</del></p></td>
<td><p><em><del>TO_CHAR(rh_t_proc_salarios.DATA_DE,
‘MM’)</del></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Validar</p></td>
<td></td>
<td><p>O mesmo dominio usado em Validação dominio=
<strong>VALIDAR_REGISTO</strong></p></td>
<td><p><em><strong>RH_T_NOTIFICACAO.</strong>REFERENCIA_NAME =
RH_T_DOMINIO. .VALUE || PROCESSAMENTO_SALARIAL</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Observacao</p></td>
<td></td>
<td></td>
<td><p><em><strong>RH_T_NOTIFICACAO.</strong> MESSAGE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><strong>Diferencia
Salarial entre Meses -»
(<em>RH_PK_VALIDACAO_SALARIAL_DB</em></strong><em>. RH_F_VALIDACAO2
(p_proc_sal_id OWA_UTIL.VC_ARR, P_TIPO=
‘<strong>TIPO_MOVIMENTO</strong>’</em>))</p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><table style="width:48%;">
<colgroup>
<col style="width: 24%" />
<col style="width: 24%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">FORMULARIO</th>
<th style="text-align: center;">JSON</th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Tipo Movimento</td>
<td>Tipo_movimento</td>
</tr>
<tr>
<td style="text-align: center;">Colaboradores</td>
<td>nome_colaborador</td>
</tr>
<tr>
<td style="text-align: center;">Quantidade</td>
<td>quantidadeFuncionarios</td>
</tr>
<tr>
<td style="text-align: center;">Valor Anterior</td>
<td>valor_anterior</td>
</tr>
<tr>
<td style="text-align: center;">Valor Atual</td>
<td>valor_atual</td>
</tr>
<tr>
<td style="text-align: center;">Diferença Salarial</td>
<td>diferenca</td>
</tr>
</tbody>
</table></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><strong>Somatória por
Tipo Movimento -» <em>(RH_PK_VALIDACAO_SALARIAL_DB</em></strong><em>.
RH_F_VALIDACAO2 (p_proc_sal_id OWA_UTIL.VC_ARR, P_TIPO=
‘<strong>TIPO_MOVIMENTO</strong>’))</em></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><table style="width:48%;">
<colgroup>
<col style="width: 24%" />
<col style="width: 24%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">FORMULARIO</th>
<th style="text-align: center;">JSON</th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Tipo Movimento</td>
<td>Tipo_movimento</td>
</tr>
<tr>
<td style="text-align: center;">Valor Anterior</td>
<td>valor_anterior</td>
</tr>
<tr>
<td style="text-align: center;">Valor Atual</td>
<td>valor_atual</td>
</tr>
</tbody>
</table></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><strong>Outros Motivos -»
<em>(RH_PK_VALIDACAO_SALARIAL_DB</em></strong><em>. RH_F_VALIDACAO2
(p_proc_sal_id OWA_UTIL.VC_ARR, P_TIPO=
‘<strong>OUTROS’)</strong>)</em></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><table style="width:48%;">
<colgroup>
<col style="width: 24%" />
<col style="width: 24%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">FORMULARIO</th>
<th style="text-align: center;">JSON</th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Colaboradores</td>
<td>nome_colaborador</td>
</tr>
<tr>
<td style="text-align: center;">Motivo</td>
<td>situacao</td>
</tr>
</tbody>
</table></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><strong>Ativos não
processados -» (<em>RH_PK_VALIDACAO_SALARIAL_DB</em></strong><em>.
RH_F_VALIDACAO2 (p_proc_sal_id OWA_UTIL.VC_ARR, P_TIPO=
‘<strong>ATIVOS_N_PROCESSADOS</strong>’</em>))</p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><table style="width:48%;">
<colgroup>
<col style="width: 24%" />
<col style="width: 24%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">FORMULARIO</th>
<th style="text-align: center;">JSON</th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Colaboradores</td>
<td>nome_colaborador</td>
</tr>
<tr>
<td style="text-align: center;">Motivo</td>
<td>situacao</td>
</tr>
</tbody>
</table></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><strong>Subsídio
Atendedor -» <em>(RH_PK_VALIDACAO_SALARIAL_DB.
</em></strong><em>RH_F_VALIDACAO2 (p_proc_sal_id OWA_UTIL.VC_ARR,
P_TIPO<strong>= ‘SUBSIDIO_ATENDENDOR’)</strong>)</em></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><table style="width:48%;">
<colgroup>
<col style="width: 24%" />
<col style="width: 24%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">FORMULARIO</th>
<th style="text-align: center;">JSON</th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;">Colaboradores</td>
<td>nome_colaborador</td>
</tr>
<tr>
<td style="text-align: center;">Motivo</td>
<td>situacao</td>
</tr>
</tbody>
</table></td>
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
<th colspan="2"><p><strong>REGRAS</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><p><strong>Regra:</strong></p>
<ul>
<li><p><mark>Caso o utilizador decida <strong>enviar o processo para
retificação</strong> ou <strong>não aprovar o processamento</strong>, o
processamento deverá <strong>regressar automaticamente à etapa anterior
(PROCESSAMENTO)</strong>. Neste caso, o sistema deverá <strong>enviar
uma notificação aos intervenientes responsáveis pela etapa
anterior</strong>, informando sobre a devolução do processo e respetivo
motivo. Para este caso, o campo <strong>Observação</strong> deverá ser
de <strong>preenchimento obrigatório</strong>, devendo o utilizador
indicar a justificação da retificação ou da não
aprovação.</mark></p></li>
<li><p><mark><em>Só é possível Validar processamentos que se encontrem
apenas no estado “</em><strong>PROCESSADO</strong><em>”, ou seja, que
ainda não tenham sido validados</em></mark></p></li>
</ul>
<p><em>Regista na <strong>RH_T_NOTIFICACAO</strong></em></p>
<ul>
<li><p><em>USER_REGISTO_NAME = USER LOGADO QUE FEZ A ACAO</em></p></li>
<li><p><em>USER_REGISTO_ID = USER LOGADO QUE FEZ A ACAO</em></p></li>
<li><p><em>DATA_REGISTO = SYSDATE</em></p></li>
<li><p><em>REFERENCIA_NAME = RH_T_DOMINIO. VALUE ||’_’||
VALIDACAO_PROVISORIO</em></p></li>
<li><p><em>MESSAGE = Campo Observação do formulario’</em></p></li>
<li><p><em>ASSUNTO = Validação provisoria</em></p></li>
<li><p><em>EMAIL = email do utilizador que tinha feito o processamento
<strong>RH_T_PROC_SALARIOS</strong>.USER_REGISTO_NAME</em></p></li>
<li><p><em>NOME_RECEPTOR =
<strong>RH_T_PROC_SALARIOS</strong>.USER_REGISTO_NAME</em></p></li>
<li><p><em>DATA_ENVIO = SYSDATE</em></p></li>
<li><p><em>URL = NULL</em></p></li>
<li><p><em>ESTADO = ‘A’</em></p></li>
<li><p><em>UUID</em></p></li>
<li><p><em>ALERTA_ID = NULL</em></p></li>
<li><p><em>REFERENCIA_ID = RH_T_PROC_SALARIOS.ID</em></p></li>
<li><p><em>REFERENCIA_UUID = NULL</em></p></li>
<li><p><em>FUN_ID = NULL</em></p></li>
<li><p><em>TIPO_NOTIFICACAO = ‘VALIDACAO_PROCESSAMENTO’</em></p></li>
<li><p><em>DESTINATARIO = RESPONSAVEL_REGISTO’</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><p><strong>Ações</strong></p></td>
</tr>
<tr>
<td><p><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p></td>
<td><p>Faz update, na tabela <strong>RH_T_PROC_SALARIO.ESTADO =
‘</strong>VALIDADO_PROVISORIO’,
<strong>RH_T_PROC_SALARIO.</strong>USER_VALID_PROV = nome de utilizador
que validou</p></td>
</tr>
</tbody>
</table>

### Validar Definitivo

Invoca o mesmo formulario de Validaçao provisoria

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 85%" />
</colgroup>
<thead>
<tr>
<th colspan="2"><p><strong>REGRAS</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><p><strong>Regra:</strong></p>
<ul>
<li><p><mark>Caso o utilizador decida <strong>enviar o processo para
retificação</strong> ou <strong>não aprovar o processamento</strong>, o
processamento deverá <strong>regressar automaticamente à etapa anterior
(VALIDACAO_PROVISORIA)</strong>. Neste caso, o sistema deverá
<strong>enviar uma notificação aos intervenientes responsáveis pela
etapa anterior</strong>, informando sobre a devolução do processo e
respetivo motivo. Para este caso, o campo <strong>Observação</strong>
deverá ser de <strong>preenchimento obrigatório</strong>, devendo o
utilizador indicar a justificação da retificação ou da não
aprovação.</mark></p></li>
<li><p><em>Só é possível Validar processamentos que se encontrem apenas
no estado “</em><strong>VALIDADO_PROVISORIO</strong><em>”, ou seja, que
ainda não tenham sido validados</em></p></li>
</ul>
<p><em>Regista na <strong>RH_T_NOTIFICACAO</strong></em></p>
<ul>
<li><p><em>USER_REGISTO_NAME = USER LOGADO QUE FEZ A ACAO</em></p></li>
<li><p><em>USER_REGISTO_ID = USER LOGADO QUE FEZ A ACAO</em></p></li>
<li><p><em>DATA_REGISTO = SYSDATE</em></p></li>
<li><p><em>REFERENCIA_NAME = RH_T_DOMINIO. VALUE ||’_’||
VALIDACAO_DEFINITIVO</em></p></li>
<li><p><em>MESSAGE = Campo Observação do formulario’</em></p></li>
<li><p><em>ASSUNTO = Validação Definitivo</em></p></li>
<li><p><em>EMAIL = email do utilizador que tinha feito o processamento
<strong>RH_T_PROC_SALARIOS</strong>.USER_REGISTO_NAME</em></p></li>
<li><p><em>NOME_RECEPTOR =
<strong>RH_T_PROC_SALARIOS</strong>.USER_REGISTO_NAME</em></p></li>
<li><p><em>DATA_ENVIO = SYSDATE</em></p></li>
<li><p><em>URL = NULL</em></p></li>
<li><p><em>ESTADO = ‘A’</em></p></li>
<li><p><em>UUID</em></p></li>
<li><p><em>ALERTA_ID = NULL</em></p></li>
<li><p><em>REFERENCIA_ID = RH_T_PROC_SALARIOS.ID</em></p></li>
<li><p><em>REFERENCIA_UUID = NULL</em></p></li>
<li><p><em>FUN_ID = NULL</em></p></li>
<li><p><em>TIPO_NOTIFICACAO = ‘VALIDACAO_PROCESSAMENTO’</em></p></li>
</ul>
<p><em>DESTINATARIO = RESPONSAVEL_REGISTO’</em></p></td>
</tr>
<tr>
<td colspan="2"><p><strong>Ações</strong></p></td>
</tr>
<tr>
<td><p><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p></td>
<td><p>Faz update, na tabela <strong>RH_T_PROC_SALARIO.</strong>ESTADO
<strong>= ‘VALIDADO_DEFINITIVO</strong>’,
<strong>RH_T_PROC_SALARIO.</strong> USER_VALID_DEF = EMAIL DE USER QUE
VALIDOU</p></td>
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
<th colspan="2"><p>REGRAS</p></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><ul>
<li><p><em>Só se pode Cabimentar informações cujo processamento esteja
validado “<strong>VALIDADO_DEFINITIVO</strong>”</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><p>Ações</p></td>
</tr>
<tr>
<td><p><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p></td>
<td><ul>
<li><p>Invoca um serviço financeiro para cabimentar</p></li>
<li><p><mark>Muda o estado: <strong>RH_T_PROC_SALARIOS</strong>.ESTADO
=” <strong>CABIMENTADO</strong>”; <strong>RH_T_PROC_SALARIOS</strong>.
USER_CABIMENTO = utilizador que fez o cabimento</mark></p></li>
<li><p>UPDATE <strong>RH_T_PROC_SALARIOS</strong>. <strong>CAB_1_ID =
número de cabimento devolvido no serviço</strong></p></li>
</ul></td>
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
<th colspan="2"><p><strong>REGRAS</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><ul>
<li><p><em>Só é possível eliminar cabimentos que ainda não tenham sido
autorizados, ou seja, cujo estado se encontre apenas como
“<strong>CABIMENTADO</strong>”.</em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><p><strong>ACÇÃO</strong></p></td>
</tr>
<tr>
<td><p><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p></td>
<td><ul>
<li><p>Invoca um serviço financeiro para eliminar cabimento</p></li>
<li><p>Muda o estado: <strong>RH_T_PROC_SALARIOS.ESTADO</strong> =”
<strong>VALIDADO</strong>”</p></li>
</ul></td>
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
<th colspan="2"><p>REGRAS</p></th>
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
<td colspan="2"><p>Ações</p></td>
</tr>
<tr>
<td><p><img src="media/image6.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p></td>
<td><ul>
<li><p>Invoca um serviço financeiro para autorizar</p></li>
</ul>
<ul>
<li><p><mark>Muda o estado: <strong>RH_T_PROC_SALARIOS</strong>.ESTADO
=” <strong>AUTORIZADO</strong>”,
<strong>RH_T_PROC_SALARIOS</strong>.USER_AUTORIZA= utilizador que fez a
autorização</mark></p></li>
</ul></td>
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
<th><p>Nome Relatório</p></th>
<th colspan="2"><p>imagem</p></th>
</tr>
</thead>
<tbody>
<tr>
<td><p>Detalhe de Processamento Para o Funcionário</p></td>
<td colspan="2"><p><img src="media/image10.jpeg"
style="width:6.55694in;height:6.26806in" /></p></td>
</tr>
<tr>
<td colspan="2"
style="text-align: center;"><p><strong>Campo</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte de
Informação</strong></p></td>
</tr>
<tr>
<td colspan="2"><p><strong>Processamento de Salários</strong></p></td>
<td><p>hardcode</p></td>
</tr>
<tr>
<td colspan="2"><p>Elaborado em:</p></td>
<td><p><strong>SYSDATE</strong></p></td>
</tr>
<tr>
<td colspan="2"><p>Data Proc:</p></td>
<td><p>proc_sal_cc.<strong>DATA_PROCESSAMENTO</strong></p></td>
</tr>
<tr>
<td colspan="2"><p><strong>Processar Remunerações Gabinete Sistemas de
Informação</strong></p></td>
<td><p>proc_sal_cc<strong>.CENTRO_DE_CUSTO</strong></p></td>
</tr>
<tr>
<td colspan="2"><p><strong>Vencimento Pessoal
contratado</strong></p></td>
<td><p>Agrupar, proc_sal_cc<strong>.DESCRICAO,</strong> onde
proc_sal_cc<strong>..SHORT_DESC</strong> = ‘SAL’</p></td>
</tr>
<tr>
<td colspan="2"><p>10116231</p></td>
<td><p>proc_sal_cc.<strong>NIF</strong></p></td>
</tr>
<tr>
<td colspan="2"><p>Anilton Pina Brandão</p></td>
<td><p>proc_sal_cc.<strong>NOME</strong></p></td>
</tr>
<tr>
<td colspan="2"><p>Contratado</p></td>
<td><p>proc_sal_cc<strong>.RELACAO</strong></p></td>
</tr>
<tr>
<td colspan="2"><p>Director Gabinete / 15F</p></td>
<td><p>proc_sal_cc.<strong>ESCALAO</strong></p></td>
</tr>
<tr>
<td colspan="2"><p>Subsidio Desgaste Viatura</p>
<p>Vencimento Pessoal contratado</p></td>
<td><p>proc_sal_id.<strong>DESCRICAO e</strong>
proc_sal_id.<strong>VALOR onde o</strong> proc_sal_id.<strong>TIPO
=AREM</strong></p></td>
</tr>
<tr>
<td colspan="2"><p>Retenção Previdência social</p>
<p>Retenção Iur</p></td>
<td><p>proc_sal_id.<strong>DESCRICAO e</strong>
proc_sal_id.<strong>VALOR onde o</strong> proc_sal_id.<strong>TIPO
=PAG</strong></p></td>
</tr>
<tr>
<td colspan="2"><p><strong>Total Remuneração</strong></p></td>
<td><p>proc_sal_id<strong>.TOTAL_REMUNERACOES</strong></p></td>
</tr>
<tr>
<td colspan="2"><p><strong>Total Desconto</strong></p></td>
<td
style="text-align: left;"><p>proc_sal_id.<strong>DESCONTO</strong></p></td>
</tr>
<tr>
<td colspan="2"><p><strong>Total Liquido</strong></p></td>
<td
style="text-align: left;"><p>proc_sal_id.<strong>TOTAL_LIQUIDO</strong></p></td>
</tr>
<tr>
<td colspan="2"><p>Recibo de Pagamento de Salário para o
Funcionario</p></td>
<td><p><img src="media/image11.jpeg"
style="width:4.88542in;height:6.26806in" /></p></td>
</tr>
<tr>
<td colspan="2" rowspan="3"><p>Recibo individual do funcionario</p></td>
<td><p><img src="media/image12.jpeg"
style="width:8.49722in;height:1.79028in" /></p></td>
</tr>
<tr>
<td><p><strong><em>N</em>ota</strong>: Este relatório é idêntico ao
Relatório 2; a única diferença é que apresenta a informação do recibo de
salário de apenas um colaborador.</p></td>
</tr>
<tr>
<td><p><img src="media/image13.jpeg"
style="width:5.92361in;height:6.26806in" /></p></td>
</tr>
<tr>
<td colspan="2"
style="text-align: center;"><p><strong>Campo</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte De
Dados</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>Recibo de Pagamento
de Salário</strong></p></td>
<td><p>hardcode</p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>Processar
Remunerações Gabinete Sistemas De Informação</strong></p></td>
<td><p>proc_sal_cc<strong>.CENTRO_DE_CUSTO</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>10458041</p></td>
<td><p>proc_sal_cc.<strong>NIF</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>Contratado</p></td>
<td><p>proc_sal_cc<strong>.RELACAO</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>Contratado -
Contratado</p></td>
<td><p>proc_sal_cc.<strong>ESCALAO</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>Nivaldo Cardoso
Tavares</p></td>
<td><p>proc_sal_cc.<strong>NOME</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>Total Remunerações</p></td>
<td><p>proc_sal_id<strong>.TOTAL_REMUNERACOES</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>Total Descontos</p></td>
<td
style="text-align: left;"><p>proc_sal_id.<strong>DESCONTO</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>Total Liquido</p></td>
<td
style="text-align: left;"><p>proc_sal_id.<strong>TOTAL_LIQUIDO</strong></p></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><em>REMUNERAÇÕES</em></p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>Vencimento Pessoal
contratado</p></td>
<td><p>proc_sal_id.<strong>DESCRICAO e</strong>
proc_sal_id.<strong>VALOR onde o</strong> proc_sal_id.<strong>TIPO
=AREM</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><em>DESCONTOS</em></p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>Retenção Previdência
Social</p></td>
<td rowspan="2"><p>proc_sal_id.<strong>DESCRICAO e</strong>
proc_sal_id.<strong>VALOR onde o</strong> proc_sal_id.<strong>TIPO
=PAG</strong></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p>Retenção IUR</p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><em>Desconto emitido
Em:</em></p></td>
<td><p>sysdate</p></td>
</tr>
</tbody>
</table>

### Resumo Processamento

<img src="media/image14.png" style="width:4.06025in;height:3.43209in" />

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
<th style="text-align: center;"><p><strong>Lista</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th colspan="2"
style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="5" style="text-align: center;"><p><strong>Total
Remunerações</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Tipo Movimento</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"></td>
<td><p><em><strong>PROC_SAL_CC_REMUN.</strong>descricao || ‘(‘
||PROC_SAL_CC_REMUN.short_desc||’)’</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><mark>Quantidade</mark></p></td>
<td><p><em><mark>Number</mark></em></p></td>
<td colspan="2"><p><mark>Número total de colcaboradores processados em
cada tipo de movimento</mark></p></td>
<td><p><em><mark><strong>PROC_SAL_CC_REMUN.</strong>TIPREL</mark></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Total a Pagar</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"></td>
<td><p><em><strong>PROC_SAL_CC_REMUN</strong>.total</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Detalhe Remuneração</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"><p>Abre o Detalhe de Processamento e passa como
parâmetro <em><strong>PROC_SAL_CC_REMUN.</strong></em>PROC_SAL_ID
<strong>e <em>PROC_SAL_CC_REMUN.</em></strong>descricao</p></td>
<td><p><em>-----------------------------------------------------------------</em></p></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><p><strong>Total
Pagamento</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Tipo Movimento</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"></td>
<td><p><em><strong>PROC_SAL_CC_PAG.</strong>descricao</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><mark>Quantidade</mark></p></td>
<td><p><em><mark>Number</mark></em></p></td>
<td colspan="2"><p><mark>Número total de colcaboradores processados em
cada tipo de movimento</mark></p></td>
<td><p><em><mark><strong>PROC_SAL_CC_REMUN.</strong>TIPREL</mark></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Total</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"></td>
<td><p><em><strong>TO_CHAR (PROC_SAL_CC_PAG.</strong>total,
'999,999,999,999' <strong>)</strong></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Detalhe Pagamento</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"><p>Abre o Detalhe de Processamento e passa como
parâmetro <em><strong>PROC_SAL_CC_PAG.</strong></em>PROC_SAL_ID
<strong>e <em>PROC_SAL_CC_PAG.</em></strong>descricao</p></td>
<td><p><em>-----------------------------------------------------------------</em></p></td>
</tr>
<tr>
<td colspan="5"
style="text-align: center;"><p><strong>Ações</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Detalhes Processamento</p></td>
<td colspan="4"><p><em>Permite visualizar todos os colaboradores do tipo
movimento selecionado</em></p></td>
</tr>
<tr>
<td colspan="5"
style="text-align: left;"><p><strong>SCRIPT</strong></p></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><strong>Script de
Remunerações</strong></p>
<p><img src="media/image15.png"
style="width:5.19931in;height:1.88681in" /></p></td>
<td colspan="2"><p><strong>Script de Pagamentos</strong></p>
<p><img src="media/image16.png"
style="width:4.47847in;height:1.525in" /></p></td>
</tr>
</tbody>
</table>

####  Detalhe Processamento

<img src="media/image17.png" style="width:9.68819in;height:2.65972in" />

| **FILTRO** |  |  |  |
|:--:|----|----|----|
| Tipo Movimento | *DESABLED* |  | ***PROC_SAL_CC_REMUN.**DESCRICAO ou **PROC_SAL_CC_PAG.**DESCRICAO* |
| Valor Total | *NUMBER* |  | ***SUM (PROC_SAL_CC_REMUN.**VALOR **)** OU **SUM** ( **PROC_SAL_CC_PAG**.VALOR )* |
| **Lista** | **Tipo** | **Descrição** | **Fonte dados** |
| Nome | *TEXT* |  | ***PROC_SAL_CC_REMUN.**NOME OU **PROC_SAL_CC_PAG**.NOME* |
| Cargo | *TEXT* |  | ***PROC_SAL_CC_REMUN.**CARGO OU **PROC_SAL_CC_PAG**.CARGO* |
| Vinculo | *TEXT* |  | ***PROC_SAL_CC_REMUN.**RELACAO OU **PROC_SAL_CC_PAG**.RELACAO* |
| Valor | *TEXT* |  | ***PROC_SAL_CC_REMUN.**VALOR OU **PROC_SAL_CC_PAG**.VALOR* |

###  Lista Susbsisdio Férias 

Permitir de uma forma mais rápida e eficaz o processamento do subsidio
de natal e de ferias levado em consideração o seguinte:

- Meses trabalhados no ano ate 15/12;

- Numero de faltas (injustificadas e justificadas) de acordo com o
  controlo de assiduidade;

- Processos disciplinares;

<img src="media/image18.png" style="width:9.4186in;height:5.58854in" />

<table style="width:98%;">
<colgroup>
<col style="width: 16%" />
<col style="width: 4%" />
<col style="width: 37%" />
<col style="width: 15%" />
<col style="width: 24%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>FILTRO</strong></p></th>
<th style="text-align: center;"></th>
<th style="text-align: center;"></th>
<th colspan="2" style="text-align: center;"><p><strong>Fonte de
dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p><del>Tipo de Subsisio</del></p></td>
<td><p><del>SELECT</del></p></td>
<td style="text-align: center;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: center;"><p>Direção</p></td>
<td><p><em>SELECT</em></p></td>
<td style="text-align: center;"></td>
<td colspan="2"
style="text-align: left;"><p><em>RH_V_SUBSIDIO_FERIAS<strong>.</strong>INSTIT_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Colaborador</p></td>
<td><p><em>DATE</em></p></td>
<td><p><em>RH_T_FUNCIONARIO.NOME</em></p></td>
<td colspan="2"
style="text-align: left;"><p><em>RH_V_SUBSIDIO_FERIAS<strong>.</strong>FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Ano referente</p></td>
<td><p><em>DATE</em></p></td>
<td><p><em>Deve trazer por defeito o ano Atual</em></p></td>
<td colspan="2"
style="text-align: left;"><p><em>RH_V_SUBSIDIO_FERIAS<strong>.</strong>
ANO_REFERENTE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td colspan="4"
style="text-align: left;"><p><em><del><strong>RH_PK_SUBSISIO_NATAL_F_DB</strong>.
SAL_BASE_DET, passando como parâmetro</del></em></p>
<ul>
<li><p><em><del><strong>P_DATA_PROC</strong> = ano (data a
Processar)</del></em></p></li>
<li><p><em><del><strong>P_FUN_ID</strong> = id de
colaborador</del></em></p></li>
<li><p><em><del><strong>P_DIRECAO =</strong> Direção</del></em></p></li>
</ul></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nome</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Nome do funcionário afeta a direção escolhida</p></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS.FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Salario Base</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Media de Salário do ano referente as ferias.</p>
<p>Caso o salário mudar no meio do mês, o salário do mês deve ser feito
através da regra 3 simples contando que 1 mês tem 30 dias.</p></td>
<td colspan="2"><p><em><strong>sipsv0.sips_prest_api.format_dinheiro
(p_num =&gt;</strong></em> <em>VALOR_MES_TOTAL<strong>, p_separator
=&gt; ',');</strong></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Mes. Trab.</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>O intervalo dos meses cujo vínculo com o INPS esteve ativo.</p>
<p>Exemplo:</p>
<ul>
<li><p>01/01/2021 à 31/05/2021</p></li>
<li><p>01/07/2021 à null</p></li>
</ul>
<p>Este exemplo deve contabilizar 11 meses.</p>
<p>Só deve presentar os dias caso o número de dias for maior que 0
(zero), caso contrário deve apresentar somente o número de
meses.</p></td>
<td colspan="2"
style="text-align: left;"><p><em>RH_V_SUBSIDIO_FERIAS.MESES_TOTAL
<strong>|| ‘-’ ||</strong> RH_V_SUBSIDIO_FERIAS.</em>
<em>DIAS_TOTAL</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Dias de Ferias</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Visto que o 12 mês de trabalho dão direito a 22 dias de ferias,
deve ser calculado através de</p>
<p>DiasFeriasMes = (mêsTrab *22) / 12</p>
<p>Cálculo dos dias</p>
<p>DiasFeriasDias = (dias *(22/12)) /30</p>
<p>Dias de ferias será o somatório de:</p>
<p>DiasFerias = DiasFeriasMes + DiasFeriasDias</p></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS.</em>
<em>DIAS_SUBSIDIO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Valor Subsidio</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>ValorSub = diasFerias * SalarioBase / 22</p></td>
<td
colspan="2"><p><em><strong>sipsv0.sips_prest_api.format_dinheiro(p_num
=&gt;</strong></em> <em>VALOR_SUBSIDIO<strong>, p_separator =&gt;
',');</strong></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Ativar / inativar</p></td>
<td></td>
<td></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS.
FLG_ATIVO_INACTIVO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Estado Validação</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Estado em que se encontra o subsídio</p></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS.ESTADO</em></p></td>
</tr>
<tr>
<td colspan="4"
style="text-align: center;"><p><em><strong>Regras</strong></em></p></td>
<td></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><ul>
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
<p><img src="media/image19.png"
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
<td><ul>
<li></li>
</ul></td>
</tr>
<tr>
<td colspan="4"
style="text-align: center;"><p><strong>Acoes</strong></p></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p>Ver detalhe</p></td>
<td colspan="3"><p><em>Permite visualizar detalhe de Processamento do
Calculo de Ferias</em></p></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image20.png"
style="width:0.95349in;height:0.34116in" /></p></td>
<td colspan="3"><p><em>Invoca o procedimento
<strong>RH_PK_SUBSISIO_NATAL_F_DB</strong>.CALCULO_DIAS_FERIA
(P_ANO_REFERENTE VARCHAR2,P_USER_REGISTO_ID NUMBER,P_USER_REGISTO_NAME
VARCHAR2 )</em></p></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image21.png"
style="width:1.70339in;height:0.30233in" /></p></td>
<td colspan="3"><p><em>Permite inactivar um colaborador de forma qe el
não possa receber o seu subsidio</em></p>
<p><em><strong>RH_T_SUBSIDIO_FERIA</strong>. FLG_ATIVO_INACTIVO = I ou
A</em></p>
<p><em><strong>Nota</strong>: por defeito devem trazer tuvo
ativo</em></p></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image22.png"
style="width:1.0939in;height:0.32296in" /></p></td>
<td colspan="3"><p><em>Faz update na tabela
<strong>RH_T_SUBSIDIO_FERIA</strong>.ESTADO = ‘A’no ano referente
selecionado</em></p></td>
<td></td>
</tr>
</tbody>
</table>

#### ~~Ativar / inativar~~ 

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th colspan="2"><p><strong><del>Ativar</del></strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><p><em><del>Ao ativar, deve efectuar
gravação nas seguintes tabelas</del></em></p>
<p><em><del><strong>1.RH_T_SUBSIDIO_FERIAS</strong></del></em></p>
<ul>
<li><p><em><del>FUN_ID</del></em></p></li>
<li><p><em><del>ANO_REFERENTE</del></em></p></li>
<li><p><em><del>VALOR_SALARIO_BASE</del></em></p></li>
<li><p><em><del>MES_TRAB</del></em></p></li>
<li><p><em><del>DIAS_TRAB</del></em></p></li>
<li><p><em><del>DIAS_ANUAL</del></em></p></li>
<li><p><em><del>DIAS_FERIAS</del></em></p></li>
<li><p><em><del>VALOR_SUBSIDIO</del></em></p></li>
<li><p><em><del>ESTADO = <strong>P,</strong> ANTES VALIDACAO,
<strong>A,</strong> APÔS VALIDACAO</del></em></p></li>
<li><p><em><del>REFERENCIA_ID = UM CODIGO ÚNICO 1 + MAX (VERIFICA O
MAXIMA REFEERENCIA ), TODOS OS REGISTOS DO MESMO BLOCO TEM A MESMA
REFERENCIA</del></em></p></li>
</ul></td>
<td
style="text-align: left;"><p><em><del><strong>1.RH_T_SUBSIDIO_FERIAS_DET</strong></del></em></p>
<ul>
<li><p><em><del>NUMERO</del></em></p></li>
<li><p><em><del>DATA_INICIO</del></em></p></li>
<li><p><em><del>DATA_FIM</del></em></p></li>
<li><p><em><del>ESCALAO_ID</del></em></p></li>
<li><p><em><del>MES_TRAB</del></em></p></li>
<li><p><em><del>VALOR_MES</del></em></p></li>
<li><p><em><del>DIA_TRAB</del></em></p></li>
<li><p><em><del>VALOR_DIA</del></em></p></li>
</ul>
<p><em><del>2. Registo na tabela RH_T_VALIDACAO</del></em></p>
<ul>
<li><p><em><del>REFERENCIA_NAME =
‘RH_T_SUBSIDIO_FERIAS’</del></em></p></li>
<li><p><em><del>REFERENCIA_ID = REFERENCIA_ID DE
RH_T_SUBSIDIO_FERIAS’</del></em></p></li>
<li><p><em><del>TIPO_ACCAO = ‘SUBSIDIO_FERIAS’</del></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><em><strong><del>Inativar</del></strong></em></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><em><del>UPDATE
<strong>RH_T_SUBSIDIO_FERIAS.</strong>ESTADO <strong>=
I</strong></del></em></p></td>
</tr>
</tbody>
</table>

#### Ver Detalhe

<img src="media/image23.png" style="width:8.39535in;height:5.26469in" />

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 6%" />
<col style="width: 28%" />
<col style="width: 21%" />
<col style="width: 26%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Lista</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th colspan="2" style="text-align: center;"><p><strong>Fonte
dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="4" style="text-align: left;"><p>Para preencher o formulário
se deve invocar o seguinte procedimento:</p>
<p><em><del><strong>RH_PK_SUBSISIO_NATAL_F_DB</strong>. SAL_BASE_DET,
passando como parâmetro</del></em></p>
<ul>
<li><p><em><del><strong>P_DATA_PROC</strong> = ano (data a
Processar)</del></em></p></li>
<li><p><em><del><strong>P_FUN_ID</strong> = id de
colaborador</del></em></p></li>
</ul></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nome</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Nome do colaborador</p></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS..NOME</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nº</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS.FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Inicio</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Data inicio no escalao</p></td>
<td
colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS_DET.DATA_INICIO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Fim</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Data fim no escalao ou data final de contagem de escalao</p></td>
<td
colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS_DET<strong>.</strong>DATA_FIM</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Escalão</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Referencia / Escalao</p></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS_DET
.ESCALAO_DESC</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Valor Escalão</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Valor Salarial do Escalão</p></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS_DET.</em>
<em>VALOR_ESCALAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Meses / Dias</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Valor recebido nos Meses trabalhado no Escalão / Valor recebido
nos dias trabalhado no Escalão</p></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS_DET .</em> <em>MES_TRAB ,
RH_V_SUBSIDIO_FERIAS_DET.</em> <em>VALOR_MES</em></p>
<p><em>RH_V_SUBSIDIO_FERIAS_DET .</em> DIAS_TRAB,
<em>RH_V_SUBSIDIO_FERIAS_DET.</em> <em>VALOR_DIA</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Valor Escalão / Tempo</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Total de Remunerações Recebido po Escalão</p></td>
<td colspan="2"><p><em>SUM (, RH_V_SUBSIDIO_FERIAS_DET.</em>
<em>VALOR_MES +</em>, <em>RH_V_SUBSIDIO_FERIAS_DET.</em>
<em>VALOR_DIA)</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Situação</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS_DET.SITUACAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Total Remuneração</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Total Recebido no ano</p></td>
<td colspan="2"><p><em>SUM (SUM (RH_V_SUBSIDIO_FERIAS_DET.</em>
<em>VALOR_MES +</em>, <em>RH_V_SUBSIDIO_FERIAS_DET.</em>
<em>VALOR_DIA))</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Salário Base do Calculo
(<strong>XXXXX</strong>)</p></td>
<td><p><em>TEXT</em></p></td>
<td><p><strong>XXXX</strong> = <em>RH_V_SUBSIDIO_FERIAS</em>
.SUMATORIA_DIAS_TOTAL</p>
<p>Média Recebido no periodo trabalhado no ano que serve como base para
o cálculo do subsidio</p></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS_DET
.VALOR_MES_TOTAL</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Valor do Subsidio Referente aos
<strong>XXXX</strong> dias de Ferias</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Valor do Subsidio com base nos dias de Ferias</p>
<p><strong>XXXX</strong> = <em>RH_V_SUBSIDIO_FERIAS.</em>
DIAS_SUBSIDIO</p></td>
<td colspan="2"><p><em>RH_V_SUBSIDIO_FERIAS.</em>
<em>VALOR_SUBSIDIO</em></p></td>
</tr>
</tbody>
</table>

- Exemplo de Calculo

<img src="media/image24.png" style="width:9.68681in;height:5.05347in" />

###  Lista Subsidio Natal 

<img src="media/image25.png" style="width:9.69306in;height:4.71736in" />

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 4%" />
<col style="width: 18%" />
<col style="width: 32%" />
<col style="width: 30%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>FILTRO</strong></p></th>
<th style="text-align: center;"></th>
<th style="text-align: center;"></th>
<th style="text-align: center;"></th>
<th style="text-align: center;"></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Direção</p></td>
<td></td>
<td style="text-align: center;"></td>
<td
style="text-align: left;"><p><em><strong>RH_V_SUBSIDIO_NATAL.INSTIT_ID</strong></em></p></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: center;"><p>Funcionário</p></td>
<td></td>
<td style="text-align: center;"></td>
<td
style="text-align: left;"><p><em><strong>RH_V_SUBSIDIO_NATAL.</strong>FUN_ID</em></p></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: center;"><p>Ano Referente</p></td>
<td></td>
<td style="text-align: center;"></td>
<td
style="text-align: left;"><p><em><strong>RH_V_SUBSIDIO_NATAL.</strong>ANO_REFERENTE</em></p></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
<td style="text-align: center;"><p><strong>Gravação</strong></p></td>
</tr>
<tr>
<td colspan="5"
style="text-align: left;"><p><del><strong>RH_PK_SUBSISIO_NATAL_F_DB.</strong>
<strong>load_list (P_DIRECAO_ID NUMBER DEFAULT NULL ,
</strong></del></p>
<p><strong><del>P_FUN_ID NUMBER DEFAULT NULL,</del></strong></p>
<p><strong><del>p_VALOR_C_BRINDE NUMBER,</del></strong></p>
<p><strong><del><mark>p_ano_processamento
number</mark>,</del></strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nome</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Nome do funcionário afeta a direção escolhida</p></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL..</strong>NOME</em></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.</strong>FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Salario</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Salario do funcionário de acordo com o montante estabelecido no
contrato (<em>Salario base do trabalhador até 15 de
dezembro</em>)</p></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL..</strong>SALARIO</em></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.VALOR_SALARIO_BASE</strong></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Meses %</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>O intervalo dos meses cujo o vinculo com o INPS esteve
ativo.Exemplo:<strong>01/01/2021 à 31/05/202101/07/2021 à nullEste
exemplo deve contabilizar 11 meses</strong>.</p></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL..</strong></em>MES_TRAB<strong>||'</strong>
/ <strong>'|| PERC_SALARIO</strong></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.MES_TRAB</strong></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Faltas %</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>RH_MESES_TRAB.PERCENTAGEM - de acordo com o numero de meses
trabalhados no ano / Numero de Faltas injustificadas no ano</p></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL.FALTAS||' / '||
PERC_FALTA</strong></em></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.FALTAS,</strong></em></p>
<p><em><strong>RH_T_SUBSIDIO_NATAL.PERC_FALTA</strong></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Valor Subsidio</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Montante bruto a receber do subsidio de natal;</p>
<p><strong>Valor Subsídio = (Salário * (% Salario/100)) * (%
Falta/100)</strong></p></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL</strong>
.VALOR_SUBSIDIO</em></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.VALOR_SUBSIDIO</strong></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><ol start="3" type="A">
<li><p>Brinde</p></li>
</ol></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL.</strong>VALOR_CHEQUE_BRID</em></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.</strong>VALOR_CHEQUE_BRINDE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Prenda Nat.</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL.</strong>VALOR_PRENDA_NATAL</em></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.</strong>
VALOR_PRENDA_NATAL</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Observação</p></td>
<td></td>
<td><p>Obrigatório caso for alterado valor Subsidio ou Prenda
Na.</p></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL.</strong>OBSERVACAO</em></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.</strong>
OBSERVACAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Ativar / inativar</p></td>
<td></td>
<td></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL</strong>.
FLG_ATIVO_INACTIVO</em></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.</strong>
FLG_ATIVO_INACTIVO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Estado Validação</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>Estado em que se encontra o subsídio</p></td>
<td><p><em><strong>RH_V_SUBSIDIO_NATAL</strong>.ESTADO</em></p></td>
<td><p><em><strong>RH_T_SUBSIDIO_NATAL.</strong> ESTADO</em></p></td>
</tr>
<tr>
<td colspan="4"
style="text-align: center;"><p><strong>REGRAS</strong></p></td>
<td></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><strong>De acordo com a
O.S. Nº18/2000 o funcionário deve ter um contrato a prazo ou contrato
indeterminado</strong> até 15 de Dezembro e que estejam em efetividade
de funções;</p>
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
<td colspan="2"><p><strong><u>Regras Subsídio de Férias</u></strong></p>
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
<table style="width:45%;">
<colgroup>
<col style="width: 10%" />
<col style="width: 11%" />
<col style="width: 11%" />
<col style="width: 11%" />
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
<ol type="1">
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
<td colspan="5"
style="text-align: center;"><p><strong>Acoes</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image20.png"
style="width:0.95349in;height:0.34116in" /></p></td>
<td colspan="4"><p><em>Invoca o procedimento
<strong>RH_PK_SUBSISIO_NATAL_F_DB</strong>.CALCULO_DIAS_NATAL
(P_ANO_REFERENTE VARCHAR2,P_USER_REGISTO_ID NUMBER,P_USER_REGISTO_NAME
VARCHAR2 )</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image21.png"
style="width:1.70339in;height:0.30233in" /></p></td>
<td colspan="4"><p><em>Permite inactivar um colaborador de forma qe el
não possa receber o seu subsidio</em></p>
<p><em><strong>RH_T_SUBSIDIO_NATAL</strong>..FLG_ATIVO_INACTIVO = I ou
A</em></p>
<p><em><strong>Nota</strong>: por defeito devem trazer tuvo
ativo</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image22.png"
style="width:1.0939in;height:0.32296in" /></p></td>
<td colspan="4"><p><em>Faz update na tabela
<strong>RH_T_SUBSIDIO_NATAL.</strong>ESTADO = ‘A’no ano referente
selecionado</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image26.png"
style="width:0.40631in;height:0.31254in" /></p></td>
<td colspan="4"><p><em><strong>Regra</strong>: A observacao deve ser
obrigatorio, quando se pretende mudar <strong>Valores subsídio</strong>
de Natal ou <strong>Prenda de Natal</strong></em></p>
<p><em>Faz update nos seguintes na tabela
<strong>RH_T_SUBSIDIO_NATAL</strong></em></p>
<ul>
<li><p><em>VALOR_SUBSIDIO <strong>: caso for
alterado</strong></em></p></li>
<li><p><em>VALOR_PRENDA_NATAL : caso for alterado</em></p></li>
<li><p><em>OBSERVACAO: é obrigatorio caso for alterado um dos
campo</em></p></li>
<li><p><em>ESTADO: <strong>‘P</strong>’ (caso for alterado um dos
campo)</em></p></li>
</ul></td>
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
<th colspan="2"><p><strong>Ativar</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><p><em><del>Ao ativar, deve efectuar
gravação nas seguintes tabelas</del></em></p>
<p><em><strong><del>1.RH_T_SUBSIDIO_NATAL</del></strong></em></p>
<ul>
<li><p><em><del>FUN_ID</del></em></p></li>
<li><p><em><del>ANO_REFERENTE</del></em></p></li>
<li><p><em><del>VALOR_SALARIO_BASE</del></em></p></li>
<li><p><em><del>MES_TRAB</del></em></p></li>
<li><p><em><del>PERC_SALARIO</del></em></p></li>
<li><p><em><del>FALTAS</del></em></p></li>
<li><p><em><del>PERC_FALTA</del></em></p></li>
<li><p><em><del>VALOR_SUBSIDIO</del></em></p></li>
<li><p><em><del>CHEQUE_BRINDE</del></em></p></li>
<li><p><em><del>PRENDA_NATAL</del></em></p></li>
<li><p><em><del>ESTADO = P (ANTES DE VALIDACAO), A
APÔS</del></em></p></li>
<li><p><em><del>REFERENCIA_ID = UM CODIGO ÚNICO 1 + MAX (VERIFICA O
MAXIMA REFEERENCIA ), TODOS OS REGISTOS DO MESMO BLOCO TEM A MESMA
REFERENCIA</del></em></p></li>
</ul></td>
<td style="text-align: left;"><p><em><del>2. Registo na tabela
RH_T_VALIDACAO</del></em></p>
<ul>
<li><p><em><del>REFERENCIA_NAME =
‘RH_T_SUBSIDIO_NATAL’</del></em></p></li>
<li><p><em><del>REFERENCIA_ID = REFERENCIA_ID DE
RH_T_SUBSIDIO_NATAL’</del></em></p></li>
<li><p><em><del>TIPO_ACCAO = ‘SUBSIDIO_NATAL’</del></em></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><em><strong>Inativar</strong></em></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><em>UPDATE
<strong>RH_T_SUBSIDIO_NATAL.</strong>ESTADO <strong>=
I</strong></em></p></td>
</tr>
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
<th><p><strong>elemento</strong></p></th>
<th><p><strong>Descrição</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td><p><strong>Natureza</strong></p></td>
<td><p>Suspensão temporária do exercício de funções.</p></td>
</tr>
<tr>
<td><p><strong>Direitos</strong></p></td>
<td><p>Mantém o vínculo e o posto, mas <strong>sem vencimento nem
contagem de tempo de serviço</strong> (exceto se a lei
permitir).</p></td>
</tr>
<tr>
<td><p><strong>Duração</strong></p></td>
<td><p>Pode variar (ex.: 6 meses a 2 anos), renovável mediante
autorização.</p></td>
</tr>
<tr>
<td><p><strong>Motivos comuns</strong></p></td>
<td><p>Motivos pessoais, formação, acompanhamento familiar, missão
externa, estudos, etc.</p></td>
</tr>
<tr>
<td colspan="2"><p>Campo <strong>TIPO_SITUACAO_LABORAL</strong> =
'Licença sem vencimento'</p>
<p><strong>ESTADO_CONTRATO</strong> = 'S' (suspenso)</p>
<p>Mantém o mesmo <strong>ID_CONTRATO</strong>, mas com registo na
tabela RH_T_SITUACAO_LABORAL ou RH_T_MOVIMENTO</p></td>
</tr>
</tbody>
</table>

### Novo / Editar 

RH_T_SITUACAO_LABORAL

<img src="media/image27.png" style="width:9.69306in;height:2.67153in"
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
<th style="text-align: center;"><p><strong>Formulário</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th colspan="2"
style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Gravação</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Validar</p></td>
<td><p><em>Select</em></p></td>
<td colspan="2"><p>Só parecer ao validar</p></td>
<td><p><em>---------------------------------------------------</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Situação Laboral</p></td>
<td><p><em>SELECT</em></p></td>
<td colspan="2"><p>Pegar id de <strong>RH_T_PARAM_SITUACAO
(</strong><em>Pega id onde código = LIC_SV referente a licença sem
Vencimento</em><strong>) ,</strong></p></td>
<td><p><em>RH_T_SITUACAO_LABORAL.</em>
<em>SITUACAO_LABORAL_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Motivo</p></td>
<td><p><em>SELECT</em></p></td>
<td colspan="2"><p>O motivo não é obrigatário (somente traz informação
caso o tipo de situação tem motivo)
<strong>RH_T_PARAM_SITUACAO_DET</strong></p></td>
<td><p><em>RH_T_SITUACAO_LABORAL. MOTIVO_SIT_LAB_ID</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.MOTIVO_SIT_LAB_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>*Data Inicio</p></td>
<td></td>
<td colspan="2"></td>
<td><p><em>RH_T_SITUACAO_LABORAL.DATA_INICIO</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Fim</p></td>
<td><p><em>DATE</em></p></td>
<td colspan="2"></td>
<td><p><em>RH_T_SITUACAO_LABORAL.DATA_FIM</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO.DATA_FIM</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Observação</p></td>
<td></td>
<td colspan="2"></td>
<td><p><em>RH_T_SITUACAO_LABORAL. OBS</em></p>
<p><em>RH_T_TIPOS_RELACIONAMENTO. OBS</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>-----------------------</p></td>
<td><p><em>HIDDEN</em></p></td>
<td
colspan="2"><p>-----------------------------------------------------</p></td>
<td><p><em>RH_T_SITUACAO_LABORAL.CONTRATO_VINCULO_ID</em></p></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><p><em><strong>OUTRAS
GRAVAÇOES</strong></em></p></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p><em>1.Faz update nos
registos anteriores , ou seja inativa os registos ativos</em></p>
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

<table style="width:99%;">
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

<img src="media/image28.png" style="width:9.69306in;height:4.6375in"
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
<td style="text-align: center;">Data Inicio</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_FIM</em></td>
</tr>
<tr>
<td style="text-align: center;">Colaborador</td>
<td></td>
<td>RH_T_FUNCIONARIO.NOME</td>
<td><em>RH_T_SITUACAO_LABORAL.CONTRA_VINCULO.ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Direcção</td>
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
<td style="text-align: center;">Estado</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;">Direção</td>
<td></td>
<td>INPSSIGOF.INTITUICOES.NOME</td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Seccão</td>
<td></td>
<td>RH_T_SECAO.NOME</td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Nome</td>
<td></td>
<td></td>
<td><em>RH_T_FUNCIONARIOS.NOME</em></td>
</tr>
<tr>
<td style="text-align: center;">Vínculo</td>
<td></td>
<td>RH_T_PARAM_VINCULO.NOME</td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.CONTRATO_VINCULO_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Cargo</td>
<td></td>
<td><em>RH_T_PARAM_CARGO.NOME</em></td>
<td><em>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Motivo</td>
<td></td>
<td>RH_T_PARAM_SITUACAO_DET.NOME</td>
<td><em>RH_T_SITUACAO_LABORAL. MOTIVO_SIT_LAB_ID</em></td>
</tr>
<tr>
<td style="text-align: center;">Data inicio</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Fim</td>
<td></td>
<td></td>
<td><em>RH_T_SITUACAO_LABORAL.DATA_FIM</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td style="text-align: center;">Editar</td>
<td colspan="3">Abre o mesmo formulário de registo</td>
</tr>
</tbody>
</table>

####  Modelo Ordem Serviço

| <img src="media/image29.png" style="width:4.01667in;height:4.01181in"
alt="Uma imagem com texto, captura de ecrã, Tipo de letra, documento Os conteúdos gerados por IA podem estar incorretos." /> |
|----|

## IMPORTAÇÃO MOVIMENTOS

### UPLOAD FICHEIRO

<img src="media/image30.png" style="width:9.69306in;height:1.62292in"
alt="Uma imagem com texto, file, Tipo de letra, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

<img src="media/image31.png" style="width:9.69306in;height:2.2875in"
alt="Uma imagem com texto, file, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Campo
Ficheiro</strong></p></th>
<th style="text-align: center;"><p><strong>Gravação</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Num_fun</p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><em>NOME_FUNCIONARIO</em></p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.NOME_FUNCIONARIO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><em>TP_MOV_RETENCAO</em></p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.TP_MOV_RETENCAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><em>TP_MOV_REM</em></p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.TP_MOV_REM</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><em>PERCENTAGEM</em></p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.PERCENTAGEM</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><em>VALOR</em></p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.VALOR</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><em>DATA_INICIO</em></p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_INICIO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><em>DATA_FIM</em></p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_FIM</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><em>SITUACAO</em></p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.SITUACAO</em></p></td>
</tr>
<tr>
<td colspan="2" style="text-align: center;"><p><em>OUTRAS
GRAVAÇÕES</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><em>1.outras gravações na tabela
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

<img src="media/image32.png" style="width:9.69306in;height:4.36597in"
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
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Data Impotação</p></td>
<td><p><em>DATE</em></p></td>
<td></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_REGISTO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nome Ficheiro</p></td>
<td></td>
<td></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.NOME_FICHEIRO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>FUN_ID</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nome Colaborador</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>-----------------------------------------------------------</p></td>
<td><p><em>----------------------------------------------------</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Movimento retenção
(TP_MOV_RETENCAO)</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>INPSSIGOF.RH_TIPOS_MOVIMENTO.NOME</p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.TP_MOV_RETENCAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Movimento Remuneração
(TP_MOV_REM)</p></td>
<td><p><em>TEXT</em></p></td>
<td><p>INPSSIGOF.RH_TIPOS_MOVIMENTO.NOME</p></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.</em> TP_MOV_REM</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Percentagem</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.PERCENTAGEM</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Valor</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.VALOR</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Inicio</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_INICIO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Fim</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.DATA_FIM</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Vinculo (Situação)</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_IMPORTACAO_MOVIMENTO.SITUACAO</em></p>
<p><em>RH_T_IMPORTACAO_MOVIMENTO.TIPREL_ID</em></p></td>
</tr>
<tr>
<td colspan="4"
style="text-align: left;"><p><strong>REGRAS</strong></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"></td>
</tr>
<tr>
<td colspan="4"
style="text-align: left;"><p><strong>ACÇÕES</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Importar</p></td>
<td colspan="3"><p>Permite fazer upload do ficheiro de Ganasio e
Cantina</p></td>
</tr>
</tbody>
</table>

### Validação

<table style="width:94%;">
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
<p><strong><u>RH_T_ TIPREL _REMUN_PAG</u></strong></p>
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
<p><strong><u>RH_T_ TIPREL _REMUN_PAG</u></strong></p>
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

<img src="media/image33.jpeg" style="width:7.7125in;height:3.95903in"
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
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th colspan="2"
style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Mês Referencia</p></td>
<td></td>
<td colspan="2"></td>
<td><p><em>RH_XML_FOS ano||</em> <em>RH_XML_FOS.mes</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Inicio</p></td>
<td></td>
<td colspan="2"></td>
<td><p><em>RH_XML_FOS.</em> <em>DT_REGISTO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Fim</p></td>
<td></td>
<td colspan="2"></td>
<td><p><em>RH_XML_FOS.</em> <em>DT_REGISTO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td colspan="2"
style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Mês Referencia</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"><p>Período do processamento</p></td>
<td><p><strong>RH_XML_FOS.</strong> ANO||MES</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Entega</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"><p>Data de submissão</p></td>
<td><p><strong>RH_XML_FOS.</strong> DT_ENTREGA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Tipo Entrega</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"><p>PRIME / SUBST / CORRE</p>
<p>case rec.tp_entrega when <strong>'PRIME'</strong> then 'Primeira'
when <strong>'SUBST'</strong> then 'Substitui o' when 'CORRE' then
'Corre o' end;</p></td>
<td><p><strong>RH_XML_FOS</strong>.tp_entrega</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Total Remuneração</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"><p>Soma salários</p></td>
<td><p><strong>RH_XML_FOS.</strong> TT_REMUNERACAO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Total Contribuição</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"><p>Valor INPS</p></td>
<td><p><strong>RH_XML_FOS.</strong> TT_CONTRIBUICAO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Obs</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="2"><p>Texto Livre</p></td>
<td><p><strong>RH_XML_FOS.</strong> OBS</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>id</p></td>
<td><p><em>HIDDEN</em></p></td>
<td colspan="2"></td>
<td><p><strong>RH_XML_FOS.</strong>ID</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>id_dec_e</p></td>
<td><p><em>HIDDEN</em></p></td>
<td colspan="2"><p><strong>FUNCTION</strong>: getIDDC (RH_XML_FOS.
num_dc)</p></td>
<td><p><strong>RH_XML_FOS.</strong> NUM_DC</p></td>
</tr>
<tr>
<td colspan="5"
style="text-align: left;"><p><strong>REGRAS</strong></p></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ul>
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
</ul></td>
</tr>
<tr>
<td colspan="5"
style="text-align: left;"><p><strong>ACÇÕES</strong></p></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ul>
<li><p><strong>Ações fora da lista (toolbar)</strong></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image34.png"
style="width:1.14583in;height:0.23958in" /></p></td>
<td colspan="4"><p>É a ação que inicia a geração de um novo FOS. Gera
XML inicial.</p></td>
</tr>
<tr>
<td colspan="5" style="text-align: center;"><ul>
<li><p><strong>Ações dentro da lista</strong></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><p>Atualizar XML</p></td>
<td colspan="4"><p>regra: atualiza contribuição, c<strong>aso for
alterado alguma informação</strong></p>
<p><strong>execução:
rh_pk_gera_xml_db.updatetotalcontribuicoes</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image35.png"
style="width:0.36458in;height:0.26042in" />: Baixar XML</p></td>
<td colspan="4"><p>Exporta XML :</p>
<p><strong>Execução: RH_PK_GERA_XML_DB.</strong>get_xml_by_ajax</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image36.png"
style="width:0.35417in;height:0.28125in" />: Ver Detalhe</p></td>
<td colspan="4"><p>Abre detalhe (abaixo descrito)</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image37.png"
style="width:0.34375in;height:0.21875in" />: Eliminar</p></td>
<td colspan="2"><p><img src="media/image38.png"
style="width:2.17014in;height:1.38819in" /></p></td>
<td colspan="2"><p><strong>Regra: Deixar eliminar somente caso a
declaração ainda não for entregue</strong></p>
<p><strong>Execucao: RH_PK_GERA_XML_DB.removerXML()</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image39.png"
style="width:0.38542in;height:0.3125in" />: Recibo Fos</p></td>
<td colspan="4"><p>Visualiza recibo</p>
<p><strong>Nota</strong>: inps deve disponibilizar um endpoint</p>
<p><em>Execucao:</em><strong>RH_PK_GERA_XML_DB</strong><em>.recibo_fos</em></p></td>
</tr>
</tbody>
</table>

#### NOVO SEGURADO

<img src="media/image40.jpeg" style="width:6.17117in;height:3.46227in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th colspan="2"
style="text-align: left;"><p><strong>REGRAS</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" style="text-align: left;"><ul>
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
<td style="text-align: left;"><p>O procedimento Grava nas seguintes
Tabelas:</p>
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
<td style="text-align: left;"><ol start="2" type="1">
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

<img src="media/image41.png" style="width:7.41258in;height:4.23593in" />

<table style="width:99%;">
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
<th style="text-align: center;"><p><strong>Lista</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th colspan="4"
style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Mês Referência</p></td>
<td style="text-align: center;"><p><em>TEXT</em></p></td>
<td colspan="4" style="text-align: center;"></td>
<td><p><strong>RH_XML_FOS.</strong>ANO<strong>||'-'||
RH_XML_FOS.</strong>MES</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Tipo Entrega</p></td>
<td style="text-align: center;"><p><em>TEXT</em></p></td>
<td colspan="4" style="text-align: center;"></td>
<td><p><strong>RH_XML_FOS.</strong> TP_ENTREGA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td colspan="4"
style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nº</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="4"></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nº Segurado</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="4"></td>
<td><p><strong>RH_DET_XML_FOS.</strong> NU_SEGURADO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nome</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="4"><p><strong>RH_FUNCIONARIOS.NOME</strong></p></td>
<td><p><strong>RH_DET_XML_FOS.</strong> ID_FUNC</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Direção</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="4"><p>FUNCTION:
GET_NOME_DIRECAO(RH_DET_XML_FOS.dir_serv_id)</p></td>
<td><p><strong>RH_DET_XML_FOS</strong>.DIR_SERV_ID</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Dias de Trabalho</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="4"><p>(30 – faltas no mês anterior ao
processamento)</p></td>
<td><p><strong>RH_DET_XML_FOS.</strong> NU_TRAB_MAN</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Remuneração</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="4"></td>
<td><p><strong>RH_DET_XML_FOS.</strong> VL_REMUN_MAN</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Tipo Remuneração</p></td>
<td><p><em>TEXT</em></p></td>
<td colspan="4"></td>
<td><p><strong>RH_DET_XML_FOS.</strong>TIPO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>ID</p></td>
<td><p><em>HIDDEN</em></p></td>
<td colspan="4"></td>
<td><p><strong>RH_DET_XML_FOS.</strong>ID</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>id_func</p></td>
<td><p><em>HIDDEN</em></p></td>
<td colspan="4"></td>
<td><p><strong>RH_DET_XML_FOS.</strong> ID_FUNC</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>id_xml</p></td>
<td><p><em>HIDDEN</em></p></td>
<td colspan="4"></td>
<td><p><strong>RH_XML_FOS.</strong>ID</p></td>
</tr>
<tr>
<td colspan="7"
style="text-align: left;"><p><strong>REGRAS</strong></p></td>
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
<td colspan="2"><ul>
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
<td colspan="7"
style="text-align: left;"><p><strong>ACÇÕES</strong></p></td>
</tr>
<tr>
<td colspan="7" style="text-align: center;"><ul>
<li><p>AÇÕES DENTRO DA LISTA</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image42.png"
style="width:0.32292in;height:0.26042in" /></p></td>
<td colspan="2"><p><img src="media/image43.png"
style="width:1.8555in;height:1.1685in" /></p></td>
<td colspan="4"><p>Elimina o registo data tabela RH_DET_XML_FOS e update
na tabela RH_XML_FOS (TT_REMUNERACAO e TT_CONTRIBUICAO)</p>
<p><strong>Ojectivo</strong>:</p>
<p>· Remove uma linha da tabela RH_DET_XML_FOS</p>
<p>· Recalcula o total de remuneração</p>
<p>· Recalcula a contribuição a 24,5%</p>
<p>· Atualiza o header RH_XML_FOS</p>
<p>Sempre que uma linha é removida, o sistema recalcula remuneração
total e contribuição.</p>
<p><strong>Execução:RH_PK_GERA_XML_DB.</strong>removerBodyXML(p_id_body_xml
number)</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image44.png"
style="width:0.34375in;height:0.3125in" /></p></td>
<td colspan="6"><p>Adicionar: adiciona mais um elemento a lista</p>
<p><img src="media/image45.png"
style="width:8.29792in;height:1.13681in" /></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image46.png"
style="width:0.33333in;height:0.28125in" /></p></td>
<td colspan="2"><p><img src="media/image47.png"
style="width:2.10278in;height:1.35347in" /></p></td>
<td colspan="4"><p>Atualizar linha</p>
<p><strong>Execução:</strong>inpsrh.<strong>RH_PK_GERA_XML_DB</strong>.gravarNovaLinhaXML</p>
<p><mark>NOTA: Caso for alguma informação FOR ALTERADO logo deve ir para
Validacao</mark></p>
<ul>
<li><p><mark>Grava na tabela <strong>RH_T_VALIDACAO,</strong> quando
esses valores forem alterados:</mark></p>
<ul>
<li><p><em><mark>TIPO_ACCAO<strong>= ‘UPDATE’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></mark></em></p></li>
<li><p><em><mark>REFERENCIA_NAME <strong>= ‘FOS’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></mark></em></p></li>
<li><p><mark><em>REFERENCIA_ID <strong>= ID</strong> de tabela</em>
<strong>RH_DET_XML_FOS</strong></mark></p></li>
<li><p><em><mark>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></mark></em></p></li>
<li><p><em><mark>TIPREL_ID <strong>= NULL</strong></mark></em></p></li>
<li><p><em><mark>DATA_REGISTO <strong>= SYSDATE
</strong></mark></em></p></li>
<li><p><em><mark>USER_REGISTO_NAME = nome de utilizador
Logado</mark></em></p></li>
<li><p><em><mark>USER_REGISTO_ID = id de utilizador
Logado</mark></em></p></li>
<li><p><em><mark>ESTADO <strong>= ‘P’</strong></mark></em></p></li>
</ul></li>
</ul></td>
</tr>
<tr>
<td colspan="7" style="text-align: center;"><ul>
<li><p><strong>AÇÕES FORA DA LISTA</strong></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image48.png"
style="width:1.17708in;height:0.17639in" /></p></td>
<td colspan="3"><p><img src="media/image49.png"
style="width:2.06944in;height:1.36111in" /></p></td>
<td colspan="3"><p>(<em>Reset das informações na tabela
<strong>RH_XML_FOS</strong> e <strong>RH_DET_XML_FOS</strong></em>)</p>
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
<td style="text-align: center;"><p><img src="media/image50.png"
style="width:1.32708in;height:0.22708in" /></p></td>
<td colspan="3"><p><img src="media/image51.png"
style="width:2.09992in;height:1.63208in" /></p></td>
<td colspan="3"><p>A ação “Gravar Nova Linha XML” permite ao utilizador
inserir manualmente uma nova linha no ficheiro FOS, especificando os
dados do segurado, remuneração e tipo. Esta funcionalidade é utilizada
em situações onde o processamento automático não contempla determinados
casos ou quando são necessários ajustes manuais.</p>
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
<td style="text-align: center;"><p><img src="media/image52.png"
style="width:1.25417in;height:0.20486in" /></p></td>
<td colspan="3"><p><img src="media/image53.png"
style="width:2.03088in;height:1.323in" /></p></td>
<td colspan="3"><p><em><strong>Objectivo:</strong> envia os direitos de
segurança social ao INPS</em></p>
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
<td style="text-align: center;"><p><img src="media/image54.png"
style="width:1.30417in;height:0.21736in" /></p></td>
<td colspan="3"><p><img src="media/image55.png"
style="width:2.06127in;height:1.31589in" /></p></td>
<td colspan="3"><p>A ação <strong>Substituir Folha</strong> permite
gerar um novo XML do tipo <strong>SUBSTITUIÇÃO</strong>, com base num
XML já existente, para o mesmo mês de referência.</p>
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
<td style="text-align: center;"><p><img src="media/image56.png"
style="width:1.70486in;height:0.29375in" /></p></td>
<td colspan="6"><p><em>Volta para pagina inicial</em></p></td>
</tr>
</tbody>
</table>

## <span class="mark">SOAT</span> 

### Lista SOAT 

<img src="media/image57.png" style="width:7.17647in;height:3.42217in" />

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
<td style="text-align: center;">Ano Refencia</td>
<td></td>
<td></td>
<td><em>RH_T_SOAT.ANO_REFERENTE</em></td>
</tr>
<tr>
<td style="text-align: center;">Mês Referencia</td>
<td></td>
<td></td>
<td><em>RH_T_SOAT.MES_ REFERENTE</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td style="text-align: center;">Mês Referencia</td>
<td></td>
<td></td>
<td><em>RH_T_SOAT.MES_ REFERENTE</em></td>
</tr>
<tr>
<td style="text-align: center;">Data Criação</td>
<td></td>
<td></td>
<td><em>RH_T_SOAT.DATA_REGISTO</em></td>
</tr>
<tr>
<td style="text-align: center;">Total Remunerações</td>
<td></td>
<td></td>
<td><em>SUM (RH_T_SOAT_DETALHE.SALARIO_BASE_ATUAL)</em></td>
</tr>
<tr>
<td style="text-align: center;">Total Colaboradores</td>
<td></td>
<td></td>
<td>Total (<em>(RH_T_SOAT_DETALHE.</em>FUN_ID)</td>
</tr>
<tr>
<td style="text-align: center;">Estado</td>
<td></td>
<td></td>
<td><em>RH_T_SOAT.ESTADO</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>REGRAS</strong></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>ACÇÕES</strong></td>
</tr>
<tr>
<td style="text-align: center;">Novo</td>
<td colspan="3"></td>
</tr>
<tr>
<td style="text-align: center;">Dados Seguro</td>
<td colspan="3"></td>
</tr>
<tr>
<td style="text-align: center;">Baixar Ficheiro SOAT</td>
<td colspan="3">Permite Exportat um ficheiro excel.</td>
</tr>
<tr>
<td style="text-align: center;">Ver Detalhes</td>
<td colspan="3">Abre a mesma página do Registo</td>
</tr>
<tr>
<td style="text-align: center;">Finalizar</td>
<td colspan="3">Actuliza o Estado <em><strong>RH_T_SOAT</strong>.ESTADO
= F</em></td>
</tr>
</tbody>
</table>

#### Novo 

No soat o rh alguns momentos necessidade de fazer algum ajusto nos
valores, principalmente em caso de baixa. que o valor que recebeu no mês
não ºe igual ao valor processado

<img src="media/image58.png" style="width:3.91983in;height:1.79282in" />
<img src="media/image59.png" style="width:5.43518in;height:2.67515in" />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Formulario1</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição / Fonte
dados</strong></p></th>
<th style="text-align: center;"><p><strong>Gravação</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Ano Refencia</p></td>
<td></td>
<td></td>
<td><p><em>RH_T_SOAT.ANO_REFERENTE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Mês Referencia</p></td>
<td></td>
<td></td>
<td><p><em>RH_T_SOAT.MES_REFERENTE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>ACÇÕES</strong></p></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p>Criar</p></td>
<td colspan="3"><p><em>Invoca o procedimento</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Formulario2</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Direcao</p></td>
<td></td>
<td><p><strong>RH_T_DIRECAO</strong>.ID,
<strong>RH_T_TIPOS_RELACIONAMENTO</strong>.MOB_ID,
RH_T_MOBILIDADE.<strong>INSTIT_ID</strong></p></td>
<td><p><em>RH_T_SOAT_DETALHE. FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Tipo Identificação</p></td>
<td></td>
<td></td>
<td><p><em>RH_T_SOAT_DETALHE.FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Número Identificação</p></td>
<td></td>
<td></td>
<td><p><em>RH_T_SOAT_DETALHE.FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Dias Trabalho</p></td>
<td></td>
<td><p><em>Por defeito traz os dados de
<strong>RH_T_SOAT_DETALHE</strong>.</em> <em>NU_TRAB_AUTO</em></p></td>
<td><p><em>RH_T_SOAT_DETALHE.</em> <em>NU_TRAB_MAN</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Remuneracao</p></td>
<td></td>
<td><p><em>Por defeito traz os dados de
<strong>RH_T_SOAT_DETALHE</strong>.</em> <em>VL_REMUN_AUTO</em></p></td>
<td><p><em>RH_T_SOAT_DETALHE.</em> <em>VL_REMUN_ MAN</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Observação</p></td>
<td></td>
<td></td>
<td><p><em>RH_T_SOAT_DETALHE.OBS</em></p></td>
</tr>
<tr>
<td colspan="4"
style="text-align: left;"><p><strong>REGRAS</strong></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"></td>
</tr>
<tr>
<td colspan="4"
style="text-align: left;"><p><strong>ACÇÕES</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image60.png"
style="width:0.53346in;height:0.29282in" /></p></td>
<td colspan="3"><p>Invoca o seguinte Procedimento
<strong>RH_PK_GERA_SOAT_DB</strong>. CRIAR (P_ANO NUMBER, P_MES
NUMBER)</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image61.png"
style="width:0.86111in;height:0.2725in" /></p></td>
<td colspan="3"><p><strong>Restaurar</strong>: Invova o Procedimento
para restarar SOAT.</p>
<p><strong>Nota</strong>: so deve sonseguir baixar SOAT que não esteja
fechado (caso o SOAT estiver fechado, este botão não deve aparecer)</p>
<p>Invoca o Procedimento <strong>RH_PK_GERA_SOAT_DB</strong>. RESTAURAR
(P_ANO NUMBER, P_MES NUMBER)</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image62.png"
style="width:0.26196in;height:0.22454in" /></p></td>
<td colspan="3"><p>1.Permite alterar dados registados de <strong>Dias de
trabalho</strong> e <strong>Remuneração</strong>.</p>
<ul>
<li><p>Caso esses valores forem alterados faz update na tabela
<strong>RH_T_SOAT_DETALHE, nos seguintes campos</strong></p>
<ul>
<li><p>NU_TRAB_MAN</p></li>
<li><p>VL_REMUN_MAN</p></li>
<li><p>ESTADO = ‘<strong>P</strong>’</p></li>
<li><p>USER_ALTERACAO_ID</p></li>
<li><p>USER_ALTERACAO_NAME</p></li>
<li><p>DATA_ALTERACAO</p></li>
</ul></li>
<li><p>Grava na tabela <strong>RH_T_VALIDACAO,</strong> quando esses
valores forem alterados:</p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘UPDATE’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘SOAT’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela</em>
<strong>RH_T_SOAT_DETALHE</strong></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></li>
</ul>
<p><em>2.Caso for introduzido Observação somente faz update em</em>
<strong>RH_T_SOAT_DETALHE.</strong>OBS</p></td>
</tr>
</tbody>
</table>

### Dados Seguro

<img src="media/image63.png" style="width:4.45341in;height:5.20208in" />

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 7%" />
<col style="width: 35%" />
<col style="width: 36%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Campo</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Gravação</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="4" style="text-align: left;"><p><strong>DADOS DO TOMADOR DE
SEGURO</strong></p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>NOME:</strong></p></td>
<td style="text-align: left;"><p>texto</p></td>
<td style="text-align: left;"><p>Nome do INPS CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>NOME</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>NIF:</strong></p></td>
<td style="text-align: left;"><p>number</p></td>
<td style="text-align: left;"><p>NIF do INPS CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO</strong>.NIF</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>COD CAE:</strong></p></td>
<td style="text-align: left;"><p>texto</p></td>
<td style="text-align: left;"><p>CAE do INPS CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>COD_CAE</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>ATIVIDADE
ECONÓMICA:</strong></p></td>
<td style="text-align: left;"><p>texto</p></td>
<td style="text-align: left;"><p><strong>ATIVIDADE ECONÓMICA</strong> do
INPS CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>ATIVIDADE_ECONOMICA</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>Nº CERTIDÃO COMERCIAL
(NC):</strong></p></td>
<td style="text-align: left;"><p>texto</p></td>
<td style="text-align: left;"><p><strong>Nº CERTIDÃO COMERCIAL
(NC):</strong> do INPS CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>NUM_CERTIDAO_COMERCIAL</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>DATA VALIDADE
NC:</strong></p></td>
<td style="text-align: left;"><p><strong>date</strong></p></td>
<td style="text-align: left;"><p><strong>DATA VALIDADE NC:</strong> do
INPS CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>DATA_VALIDADE</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>TELEFONE:</strong></p></td>
<td style="text-align: left;"><p><strong>number</strong></p></td>
<td style="text-align: left;"><p><strong>TELEFONE:</strong> do INPS
CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>TELEFONE</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>TELEMÓVEL:</strong></p></td>
<td style="text-align: left;"><p><strong>number</strong></p></td>
<td style="text-align: left;"><p><strong>TELEMÓVEL:</strong> do INPS
CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>TELEMOVEL</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>LOCALIDADE:</strong></p></td>
<td style="text-align: left;"><p><strong>text</strong></p></td>
<td style="text-align: left;"><p><strong>LOCALIDADE:</strong> do INPS
CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>
LOCALIDADE:</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>EMAIL:</strong></p></td>
<td style="text-align: left;"><p><strong>text</strong></p></td>
<td style="text-align: left;"><p><strong>EMAIL:</strong> do INPS CENTRAL
(PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO</strong>.EMAIL</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>MORADA:</strong></p></td>
<td style="text-align: left;"><p><strong>text</strong></p></td>
<td style="text-align: left;"><p><strong>MORADA:</strong> do INPS
CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO</strong>.MORADA</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>LOCALIDADE:</strong></p></td>
<td style="text-align: left;"><p><strong>text</strong></p></td>
<td style="text-align: left;"><p><strong>LOCALIDADE:</strong> do INPS
CENTRAL (PRAIA)</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>LOCALIDADE</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>CONCELHO:</strong></p></td>
<td style="text-align: left;"><p><strong>select</strong></p></td>
<td style="text-align: left;"><p><strong>CONCELHO:</strong> do INPS
CENTRAL (PRAIA), Por defeito traz Praia</p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO</strong>.CONCELHO</p></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><p><strong>Lista
Apolice</strong></p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>APÓLICE Nº:</strong></p></td>
<td><p><strong>Select</strong></p></td>
<td><p><strong>NUMERO REFERENTE ZONA:</strong></p>
<ul>
<li><p><strong>SAL</strong></p></li>
<li><p><strong>SAO VICENTE (SV, SA, SN, BV) – ZONA
NORTE</strong></p></li>
<li><p><strong>SANTIAGO (SA, FG, MA, BR)</strong></p></li>
</ul></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_APOLICE.</strong>NUM_APOLICE</p>
<p><strong>RH_T_DADOS_APOLICE.</strong>ILHA_ID</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>DATA INÍCIO DA
APÓLICE:</strong></p></td>
<td><p><strong>Date</strong></p></td>
<td><p><strong>ESSA INFORMAÇÃO TEM TEM SIDO PREENCHIDO</strong></p></td>
<td
style="text-align: left;"><p><strong>RH_T_DADOS_APOLICE.</strong>DATA_APOLICE</p></td>
</tr>
</tbody>
</table>

### Baixar ficheiro SOAT

<img src="media/image64.png" style="width:7.55147in;height:3.8531in" />

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 2%" />
<col style="width: 4%" />
<col style="width: 27%" />
<col style="width: 7%" />
<col style="width: 25%" />
<col style="width: 10%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Campo</strong></p></th>
<th colspan="2"
style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th colspan="2"
style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th colspan="2" style="text-align: center;"><p><strong>Fonte
Dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><p><strong>Nº apolice</strong></p></td>
<td colspan="2" style="text-align: left;"><p>texto</p></td>
<td colspan="2" style="text-align: left;"></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_T_DADOS_APOLICE.</strong>NUM_APOLICE</p></td>
</tr>
<tr>
<td style="text-align: left;"><p><strong>Baixar ficheiro
SOAT</strong></p></td>
<td colspan="2" style="text-align: left;"><p>Hiperlink</p></td>
<td colspan="2" style="text-align: left;"><p>Abre o ficheiro abaixo
Indicado</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>-----------------------------------------------</strong></p></td>
</tr>
<tr>
<td colspan="6" style="text-align: center;"><p><img
src="media/image65.png"
style="width:7.82917in;height:4.56389in" /></p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: center;"><p><strong>Campo</strong></p></td>
<td colspan="2"
style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td colspan="2" style="text-align: center;"><p><strong>Fonte
Dados</strong></p></td>
<td></td>
</tr>
<tr>
<td colspan="6" style="text-align: left;"><p><strong>DADOS DO TOMADOR DE
SEGURO</strong></p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>NOME:</strong></p></td>
<td colspan="2" style="text-align: left;"><p>Nome do INPS CENTRAL
(PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_T_DADOS_INSTITUICAO.</strong>NOME</p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>NIF:</strong></p></td>
<td colspan="2" style="text-align: left;"><p>NIF do INPS CENTRAL
(PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO</strong>.NIF</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>COD
CAE:</strong></p></td>
<td colspan="2" style="text-align: left;"><p>CAE do INPS CENTRAL
(PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>COD_CAE</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>ATIVIDADE
ECONÓMICA:</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>ATIVIDADE
ECONÓMICA</strong> do INPS CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>ATIVIDADE_ECONOMICA</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>Nº CERTIDÃO
COMERCIAL (NC):</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Nº CERTIDÃO
COMERCIAL (NC):</strong> do INPS CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>NUM_CERTIDAO_COMERCIAL</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>DATA VALIDADE
NC:</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>DATA VALIDADE
NC:</strong> do INPS CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>DATA_VALIDADE</p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>TELEFONE:</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>TELEFONE:</strong>
do INPS CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>TELEFONE</p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>TELEMÓVEL:</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>TELEMÓVEL:</strong>
do INPS CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>TELEMOVEL</p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>LOCALIDADE:</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>LOCALIDADE:</strong> do INPS
CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>
LOCALIDADE:</p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>EMAIL:</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>EMAIL:</strong> do
INPS CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO</strong>.EMAIL</p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>MORADA:</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>MORADA:</strong> do
INPS CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO</strong>.MORADA</p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>LOCALIDADE:</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>LOCALIDADE:</strong> do INPS
CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>LOCALIDADE</p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>CONCELHO:</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>CONCELHO:</strong>
do INPS CENTRAL (PRAIA)</p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO</strong>.CONCELHO</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>APÓLICE
Nº:</strong></p></td>
<td colspan="2"><p><strong>NUMERO REFERENTE ZONA:</strong></p>
<ul>
<li><p><strong>SAL</strong></p></li>
<li><p><strong>SAO VICENTE (SV, SA, SN, BV) – ZONA
NORTE</strong></p></li>
<li><p><strong>SANTIAGO (SA, FG, MA, BR)</strong></p></li>
</ul></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>NUM_APOLICE</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>DATA INÍCIO DA
APÓLICE:</strong></p></td>
<td colspan="2"><p><strong>ESSA INFORMAÇÃO TEM TEM SIDO
PREENCHIDO</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_DADOS_INSTITUICAO.</strong>DATA_APOLICE</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>DATA DE REFERÊNCIA
(*)</strong></p></td>
<td colspan="2"><p><strong>Mês e ano referente ,</strong> que coincide
com mês de processamento</p></td>
<td colspan="2"><p>RH_T_PROC_SALARIOS.
<em>DATA_PROC_PROVISORIO</em></p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>Nº TOTAL DE PESSOAS
SEGURAS</strong></p></td>
<td colspan="2"><p><strong>NUMERO TOTAL DE
COLABORADORES</strong></p></td>
<td
colspan="2"><p><strong>COUNT</strong>(<strong>RH_T_PROC_FUNCIONARIOS</strong>.TIPREL_ID)</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>MASSA SALARIAL
ANUAL SEGURA</strong></p></td>
<td colspan="2"><p><strong>VALOR TOTAL ANNUAL (X 12)</strong></p></td>
<td colspan="2"><p><strong>RH_T_REMUNERACOES.</strong>
VALOR_REAL</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>SEGURO NOVO /
EXISTENTE:</strong></p></td>
<td colspan="2"><p><strong>NULL</strong></p></td>
<td colspan="2" style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>MODALIDADE:</strong></p></td>
<td colspan="2"><p><strong>NULL</strong></p></td>
<td colspan="2" style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: center;"><p><strong>Lista</strong></p></td>
<td colspan="2"
style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td colspan="2" style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>NOME COMPLETO
(*)</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Indicar o nome
completo de cada colaborador</strong></p></td>
<td colspan="2"><p><strong>RH_V_SOAT.</strong>NOME</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>TIPO DOC. IDENT.
(*)</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Deve-se indicar um
dos três (3) tipos documentos de identificação:</strong></p>
<ul>
<li><p><strong>NIC (número de identificação civil) ou BI (bilhete de
identidade para os colaboradores nacionais;</strong></p></li>
<li><p><strong>Passaporte para colaboradores
estrangeiros.</strong></p></li>
</ul></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_SOAT.</strong>TP_DOCUMENTO</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>Nº DOC. IDENT.
(*)</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Inserir o número de
identificação (se TIPO DOC. IDENT. = NIC, deve-se introduzir 13
carateres).</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>RH_V_SOAT.</strong>
NUM_DOCUMENTO</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>DATA
VALIDADE</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Indicar a data de
validade do documento de identificação com o seguinte formato
dd/mm/aaaa.</strong></p></td>
<td colspan="2"><p><strong>RH_V_SOAT.</strong>DATA_VALIDADE</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>NIF
(*)</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Indicar o NIF
(número de identificação fiscal) de cada colaborador (9
dígitos)</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_SOAT.</strong>NIF</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>DATA
NASCIMENTO</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Indicar a data de
nascimento de cada colaborador com o seguinte formato
dd/mm/aaaa.</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>RH_V_SOAT.</strong>
DATA_NASCIMENTO</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>SEXO
(*)</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Indicar o sexo de
cada colaborador (Masculino ou Feminino)</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_SOAT.</strong>SEXO</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>SITUAÇÃO
(*)</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>Indicar:</strong></p>
<p><strong>I - Incluir: para incluir colaboradores novos</strong></p>
<p><strong>E - Excluir: para excluir colaboradores existentes na
apólice</strong></p>
<p><strong>A - Alterar: sempre que haja qualquer alteração nos dados dos
colaboradores existentes na apólice</strong></p>
<p><strong>M - Manter: para os colaboradores existentes na apólice, sem
qualquer alteração</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_SOAT.SITUACAO</strong></p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>PROFISSÃO
(*)</strong></p></td>
<td colspan="2"><p><strong>Indicar a profissão/função que cada
colaborador exerce na empresa/instituição</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_SOAT.CARGO_CARREIRA</strong></p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>APRENDIZ OU
ESTAGIÁRIO (*)</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>Indicar:</strong></p>
<p><strong>Sim: se o colaborador é aprendiz, eventual, temporário,
estagiário ou praticante</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Depende do tipo de
contrato</strong></p>
<p><strong>RH_V_SOAT.</strong>TIPO_CONTRATO</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>Nº HORAS SEMANA
(*)</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Indicar o número de
horas semanais de cada colaborador (o período normal de trabalho
previsto no artigo 149º do Código Laboral não pode ultrapassar 44 horas
por semana).</strong></p>
<p><strong>Nas situações em que o colaborador não trabalha todos os dias
da semana, o nº de horas por semana deve ser calculado da seguinte
formula: 44 *( o número de dias de trabalho efetivo do mês)/(30
dias).</strong></p></td>
<td colspan="2"><p><strong>Calcular 8 horas por semana – horas que
faltou</strong></p>
<p><strong>RH_V_SOAT.</strong>DIAS_TRAB_SEMANA</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>RETRIBUIÇÃO BASE
(*)</strong></p></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>HORA /DIA/ MÊS
(*)</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>Indicar:</strong></p>
<p><strong>H - HORA: se a retribuição é paga ao colaborador à
hora;</strong></p>
<p><strong>D - DIA: se a retribuição é paga ao colaborador ao
dia;</strong></p>
<p><strong>M - MÊS: se a retribuição é paga ao colaborador ao
mês.</strong></p></td>
<td colspan="2"><p><strong>M</strong></p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>RETRIBUIÇÃO HORA
/DIÁRIO/MENSAL (*)</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Inserir a
retribuição de acordo com a periodicidade do pagamento
(Hora/Diário/Mensal).</strong></p>
<ul>
<li><p><strong>Se HORA/DIA/MÊS = H - HORA, deve-se introduzir a
retribuição paga a hora;</strong></p></li>
<li><p><strong>Se HORA/DIA/MÊS = D - DIA, deve-se introduzir a
retribuição paga por dia;</strong></p></li>
<li><p><strong>Se HORA/DIA/MÊS = M - MÊS, deve-se introduzir a
retribuição paga por mês.</strong></p></li>
</ul></td>
<td colspan="2"><p><strong>RH_V_SOAT.</strong> SALARIO_BASE</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>RETRIBUIÇÃO ANUAL
(*)</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Inserir o salário
anual auferido pelo colaborador.</strong></p>
<p><strong>O valor a indicar não pode ser inferior a 12 vezes do salário
mínimo nacional em vigor.</strong></p></td>
<td
colspan="2"><p><strong>RH_V_SOAT.</strong>SALARIO_BASE_ANUAL</p></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"></td>
<td colspan="2" style="text-align: center;"></td>
<td colspan="2" style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><p><strong>TRABALHADOR
TEMPORARIAMENTE NO ESTRANGEIRO (*)</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>Indicar:</strong></p>
<p><strong>Sim: se o colaborador está temporariamente no estrangeiro ao
serviço</strong></p>
<p><strong>Não: caso contrário</strong></p></td>
<td
colspan="2"><p><strong>RH_V_SOAT.</strong>COLAB_NO_ESTRANGEIRO</p></td>
<td></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>OBSERVAÇÕES</strong></p></td>
<td colspan="2" style="text-align: left;"><p><strong>Indicar as
observações relevantes sobre cada colaborador</strong></p></td>
<td colspan="2"
style="text-align: left;"><p><strong>RH_V_SOAT.</strong>OBS</p></td>
<td></td>
</tr>
</tbody>
</table>

## Aumento Salarial 

### Registo Criterios aumento

<img src="media/image66.png" style="width:9.68194in;height:2.40764in" />

<img src="media/image67.png" style="width:9.68264in;height:4.05694in" />

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 5%" />
<col style="width: 42%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Formulário</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Gravação</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Designação Aumento</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.DESCRICAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Motivo</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.MOTIVO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Referencia do aumento</p></td>
<td><p><em>DATE</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.DATA_REFERENTE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Percentagem</p></td>
<td></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.PERCENTAGEM</em></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p><em><strong>Lista
colaborador</strong></em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Direção</p></td>
<td><p><em>SELECT</em></p></td>
<td><p>INPSSIGOF.INSTITUICOES</p></td>
<td><p><em>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Unidade</p></td>
<td><p><em>SELECT</em></p></td>
<td><p>RH_T_SECAO</p></td>
<td><p><em>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Lista</p></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p>Colaborador</p></td>
<td></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL_DET.FUN_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Carreira</p></td>
<td></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL_DET.CARREIRA_ID</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nivel / Escalão</p></td>
<td><p><em>SELETCT</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL_DET.NIVEL_ESCALAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Salário Antes:</p></td>
<td><p><em>NUMBER</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL_DET.SALARIO_ANTES</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Salário Depois:</p></td>
<td><p><em>NUMBER</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL_DET.SALARIO_DEPOIS</em></p></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><p>Acoes</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Gravar</p></td>
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

<table style="width:99%;">
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

<table style="width:91%;">
<colgroup>
<col style="width: 14%" />
<col style="width: 5%" />
<col style="width: 34%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Ano Referente</p></td>
<td><p><em>Select</em></p></td>
<td><p>Deve somente extrair o ano</p></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.DATA_REFERENTE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Designação</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.DESCRICAO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Motivo</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.MOTIVO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Referente</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.DATA_REFERENTE</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Percentagem</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.PERCENTAGEM</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Registo</p></td>
<td><p><em>TEXT</em></p></td>
<td></td>
<td><p><em>RH_T_AUMENTO_SALARIAL.DATA_REGISTO</em></p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Acoes</strong></p></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p>Editar</p></td>
<td colspan="3"><p><em>Abre o mesmo formulário de Registo</em></p>
<p><em><strong>Nota:</strong> Não deve ser permitido editar um registo
que ainda não tenha sido validado; nesses casos, o registo deve estar
apenas disponível para visualização pelo utilizador.</em></p></td>
</tr>
</tbody>
</table>

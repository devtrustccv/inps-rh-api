<figure>
<img src="media/image1.jpeg" style="width:14.65694in;height:9.77083in"
alt="C:\Users\joelm\Desktop\Imagens\sergey-zolkin-_UeY8aTI6d0-unsplash (2).jpg" />
<figcaption><p>SIPS-RH</p></figcaption>
</figure>

**GESTÃO ASSIDUIDADE**

# Enquadramento 

# Âmbito 

<img src="media/image4.png" style="width:9.69306in;height:3.84444in"
alt="Uma imagem com texto, captura de ecrã, Tipo de letra, número Os conteúdos gerados por IA podem estar incorretos." />

# Especificação 

<img src="media/image5.png" style="width:8.10347in;height:0.85764in" />

## Movimento de Picagem (Entrada / saída)

Mostra a Lista Geral de todas as entradas e saídas do relógio

<img src="media/image6.png" style="width:9.69306in;height:4.35833in"
alt="Uma imagem com texto, software, número, Ícone de computador Os conteúdos gerados por IA podem estar incorretos." />

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
<td>Nome Colaborador</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_MOVIMENTOS.ID_COLABORADOR</td>
</tr>
<tr>
<td>Direção</td>
<td><em>SELECT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Secção</td>
<td><em>SELECT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>UPS</td>
<td><em>UPS</em></td>
<td>SIPSGLOBAL.GLB_T_UPS.NOME</td>
<td><p>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</p>
<p>RH_T_LOCAL_TRABALHO. ID_UPS</p></td>
</tr>
<tr>
<td>Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td>RH_MOVIMENTOS. DT_MOVIMENTO</td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td>RH_MOVIMENTOS. DT_MOVIMENTO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td><em>TEXT</em></td>
<td>RH_T_FUNCIONARIOS.NOME</td>
<td>RH_MOVIMENTOS.ID_COLABORADOR</td>
</tr>
<tr>
<td>Data</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_MOVIMENTOS.DT_MOVIMENTO</td>
</tr>
<tr>
<td>Hora Entrada</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_MOVIMENTOS. getLocalUserMovimento</td>
</tr>
<tr>
<td>Hora Saida</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_MOVIMENTOS.hora_movimento Dir||' - '||.getLocalUserMovimento</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td><img src="media/image7.png"
style="width:1.18264in;height:0.30347in" /></td>
<td colspan="3"><p>Caso algum dados não foi importado de forma
automático, o RH Tem a possibilidade de o fazer manualmente com esse
botão-</p>
<p>Esse botão invoca um serviço que importa dados do relógio.</p></td>
</tr>
</tbody>
</table>

### <span class="mark">Importar Dados</span> 

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><p>BEGIN</p>
<p>select count(id) into v_count_processado from rh_movimentos where
trunc(dt_movimento) between
trunc(to_date(<strong>´P_data_inicio</strong>´,'dd-mm-yyyy')) and
trunc(to_date(<strong>´P_data_fim</strong>´, 'dd-mm-yyyy')) and
processado = 1;</p>
<p>IF v_count_processado = 0 THEN</p>
<p>DELETE FROM RH_MOVIMENTOS WHERE trunc(dt_movimento) between
trunc(to_date(<strong>´P_data_inicio</strong>´,'dd-mm-yyyy')) and
trunc(to_date(<strong>´P_data_fim</strong>´, 'dd-mm-yyyy'));</p>
<p>INPSRH.IMPORT_DADOS_CONTR_ACESSO(p_data_inicio
=&gt;to_char(to_date(<strong>´P_data_inicio</strong>´,'dd-mm-yyyy'),
'yyyy-mm-dd'), p_data_fim
=&gt;to_char(to_date(<strong>´P_data_fim</strong>´, 'dd-mm-yyyy') + 1,
'yyyy-mm-dd'));</p>
<p>END IF;</p>
<p>EXCEPTION WHEN OTHERS THEN</p>
<p>NULL;</p>
<p>END;</p></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

## Movimento Resumido

### Lista

<img src="media/image8.png" style="width:9.69306in;height:4.21042in"
alt="Uma imagem com texto, número, software, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 4%" />
<col style="width: 26%" />
<col style="width: 50%" />
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
<td>Direção</td>
<td></td>
<td>INPSSIGOF.INSTITUICOES.NOME</td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Colaborador</td>
<td></td>
<td>RH_T_FUNCIONARIOS.NOME</td>
<td>RH_ASSIDUIDADE_SINTESE_DIARIA. FUNCIONARIO_ID</td>
</tr>
<tr>
<td>UPS</td>
<td></td>
<td>SIPSGLOBAL.GLB_T_UPS. NOME</td>
<td><p>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</p>
<p>RH_T_PARAM_LOCAL_TRAB.UPS_ID</p></td>
</tr>
<tr>
<td>Data Inicio</td>
<td></td>
<td></td>
<td>RH_ASSIDUIDADE_SINTESE_DIARIA .DATA</td>
</tr>
<tr>
<td>Data Fim</td>
<td></td>
<td></td>
<td>RH_ASSIDUIDADE_SINTESE_DIARIA .DATA</td>
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
<td><p>Estado da falta,conforme,inj<strong>ustiticada</strong>,
<strong>justificada ,Pendente,</strong></p>
<p><strong>DOMAIN =</strong></p></td>
<td>RH_ASSIDUIDADE_SINTESE_DIARIA.ESTADO</td>
</tr>
<tr>
<td>Mês Referencia</td>
<td></td>
<td>AGRUPAR</td>
<td>RH_ASSIDUIDADE_SINTESE_DIARIA.DATA</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td></td>
<td><p>Nome do colaborador</p>
<p>RH_T_FUNCIONARIOS.NOME</p></td>
<td>RH_ASSIDUIDADE_SINTESE_DIARIA. FUNCIONARIO_ID</td>
</tr>
<tr>
<td>Total de Falta</td>
<td></td>
<td>Contam todas faltas cujo estado =’SIM’</td>
<td><strong>SUM</strong> (RH_ASSIDUIDADE_SINTESE_DIARIA.FALTA)</td>
</tr>
<tr>
<td>Total de Dias</td>
<td></td>
<td>Agrupar as datas que tem falta</td>
<td><strong>COUNT</strong> (RH_ASSIDUIDADE_SINTESE_DIARIA.DATA)</td>
</tr>
<tr>
<td>Horas Trabalhadas</td>
<td></td>
<td>Somar as horas trabalhadas</td>
<td><strong>COUNT</strong>(SUBSTR(TO_CHAR(<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.HORAS_TRABALHADAS
), 4, 8);)</td>
</tr>
<tr>
<td>Horas Extras</td>
<td></td>
<td>Somas as horas extras</td>
<td><strong>COUNT</strong>(SUBSTR(TO_CHAR(<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.HORAS_EXTRAS),
4, 8);</td>
</tr>
<tr>
<td>Hora Ausência</td>
<td></td>
<td>Somar as horas Ausência</td>
<td><strong>COUNT</strong>(SUBSTR(TO_CHAR(<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.HORAS_AUSENCIA),
4, 8);</td>
</tr>
<tr>
<td><del>Motivo Ausencia</del></td>
<td></td>
<td><del>Verifica se o colaborador esta ausente de trabalhor neste
periodo (caso sim vem preenchido )</del></td>
<td><del><strong>RH_T_AUSENCIA.</strong>PARAM_SIT_ID <strong>,
RH_T_PARAM_SITUACAO.</strong>NOME</del></td>
</tr>
<tr>
<td colspan="4"><strong>REGRA</strong></td>
</tr>
<tr>
<td colspan="4"><ul>
<li><p>Os colaboradores com ausência justificada não devem aparecer na
lista. Ou seja no período que o colaborador esta ausente de trabalho ,
já seja por motivo de ferias, missão de serviço. Licença sem vencimento
…etc , a sua falta fica justificada automaticamente e não deve aparecer
na lista. Para ver se um colaboradors está ausente de trabalho – tabela
(<strong>RH_T_AUSENCIA</strong>)</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td><img src="media/image9.png"
style="width:1.03681in;height:0.29514in" /></td>
<td colspan="3">Permite marcar um colaborador falta manualmente</td>
</tr>
<tr>
<td><img src="media/image10.png"
style="width:1.52847in;height:0.22014in" /></td>
<td colspan="3">Abre formulario para justificacao de Falta</td>
</tr>
<tr>
<td><img src="media/image11.png"
style="width:1.59097in;height:0.24931in" /></td>
<td colspan="3">Abre a mesma <strong>Lista de Movimento Picagem (Entrada
/ Saída), <mark>mas filtrado com dados de um colaborador
especifico</mark></strong></td>
</tr>
</tbody>
</table>

### Marcar Falta / Ausência

<img src="media/image12.png" style="width:9.69306in;height:4.97639in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
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
<td style="text-align: left;">Colaborador</td>
<td>SELECT</td>
<td>RH_T_COLABORADORES.NOME</td>
<td><p><strong>RH_ASSIDUIDADE_SINTESE_DIARIA.</strong>FUNCIONARIO_ID</p>
<p>RH_T_PEDIDO.FUN_ID</p></td>
</tr>
<tr>
<td style="text-align: left;">Data inicio</td>
<td>DATE</td>
<td></td>
<td><p><strong>RH_ASSIDUIDADE_SINTESE_DIARIA.DATA</strong></p>
<p>RH_T_FALTA.DATA_INICIO</p></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td>Date</td>
<td></td>
<td>RH_T_FALTA.DATA_FIM</td>
</tr>
<tr>
<td style="text-align: left;">Horas <strong>Ausência</strong></td>
<td></td>
<td></td>
<td><p><strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.
HORAS_AUSENCIA</p>
<p><strong>RH_T_T_FALTA.</strong>HORAS_AUSENCIA /TOTAL DIAS</p></td>
</tr>
<tr>
<td style="text-align: left;">Total Dias</td>
<td></td>
<td>Diferença entre Data Inicio e Data Fim.</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Justificar</td>
<td>RADIOLIST</td>
<td><strong>DOMAINS = SIM_NAO</strong></td>
<td><strong>---------------------</strong></td>
</tr>
<tr>
<td style="text-align: left;"><del>Tipo Falta</del></td>
<td></td>
<td><del><strong>DOMAIN</strong> = JUSTIFICADA_INJUSTIFICADA</del></td>
<td><del><strong>RH_T_FALTA</strong>.TIPO_FALTA</del></td>
</tr>
<tr>
<td style="text-align: left;">Motivo Ausência</td>
<td>TEXAREA</td>
<td>Preenchido a partir de tabela <strong>RH_T_FALTA</strong></td>
<td><strong>RH_T_FALTA</strong>.DESCRICAO<strong>_</strong>MOTIVO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Parecer Responsável</strong></td>
<td colspan="3"><strong>Nota:</strong> este separador só aparece caso
justificar = SIM</td>
</tr>
<tr>
<td style="text-align: left;">Parecer Responsável</td>
<td>SELECT</td>
<td><strong>DOMAIN</strong> = PARECER_DECISAO, REFERENCIA =
PARECER_RESPONSAVEL</td>
<td>RH_T_FALTA.DECISAO_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;">Responsavel</td>
<td>SELECT</td>
<td>Pegar da Tabela <strong>RH_T_RESPONSAVEL.FUN_ID</strong> ,
<strong>RH_T_FUNCIONARIO</strong>.ID</td>
<td>RH_T_FALTA RESPONSAVEL_ID</td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td>SELECT</td>
<td></td>
<td>RH_T_FALTA.OBS_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Despacho RH</strong></td>
<td colspan="3"><strong>Nota:</strong> Fica Visivel somente na
Validação</td>
</tr>
<tr>
<td style="text-align: left;">Despacho RH</td>
<td>SELECT</td>
<td>DOMAIN = SIM_NAO</td>
<td>RH_T_FALTA.DESPACHO_RH</td>
</tr>
<tr>
<td style="text-align: left;">Tipo Justificação</td>
<td>SELECT</td>
<td>Deve buscar a descrição do tipo de falta escolhido … para buscar a
justificativo, deve buscar da tabela de parametrização
RH_T_PARAM_SITUACAO onde <em>FLG_ausencia = FALTA</em></td>
<td>RH_T_FALTA<strong>.</strong>PARAM_SIT_ID</td>
</tr>
<tr>
<td style="text-align: left;"><strong>REGRA</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><ul>
<li><p>Caso campo justificar for <strong>SIM</strong>:</p>
<ul>
<li><p>Deve aparecer o separador de Parecer Respnsável e Despacho
RH</p></li>
<li><p>Regista na tabela RH_T_PEDIDO, e RH_T_FALTA</p></li>
<li><p>Despacho RH , fica visível somente em Validação</p></li>
</ul></li>
<li><p>O numero de registo na tabela RH_T_FALTA dependerá do numero de
dias de falta (por cada dia de falta é um registo na tabela)</p></li>
</ul>
<p><strong>Pendente</strong>: calculo de valor na justificacao de
Falta</p></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image13.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p>1.Grava na tabela
<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong></p>
<ul>
<li><p>MES = extrair mês de cada intervalo de data</p></li>
<li><p>ANO = extrair mês de cada intervalo de data</p></li>
<li><p>HORA_PRIMEIRA_ENTRADA = NULL</p></li>
<li><p>HORA_ULTIMA_SAIDA = NULL</p></li>
<li><p>HORAS_TRABALHADAS = 8 – horas Ausência</p></li>
<li><p>HORA_PRIMEIRA_SAIDA_ALMOCO = NULL</p></li>
<li><p>HORA_ULTIMA_ENTRADA_ALMOCO = NULL</p></li>
<li><p>HORAS_ALMOCO = NULL</p></li>
<li><p>HORAS_EXTRAS = NULL</p></li>
<li><p>FALTA = 1 (caso oras trabalhadas = 0)</p></li>
<li><p>ESTADO = ‘A’</p></li>
<li><p>DT_REGISTO = SYSDATE</p></li>
<li><p>USR_REGISTO = User logado</p></li>
<li><p>USR_REGISTO_NAME = nome de user logado</p></li>
<li><p>DT_UPDATE = NULL</p></li>
<li><p>USR_UPDATE = NULL</p></li>
<li><p>FLAG_RECECAO = ??</p></li>
<li><p>FORMA = ‘MANUAL’</p></li>
</ul>
<p>2. Caso se pretende além de registar Falta Também Justificar a Falta.
Logo deve registar o Pedido de justificação</p>
<p>2.1 Grava na tabela <strong>RH_T_PEDIDO</strong></p>
<ul>
<li><p>TIPO_PEDIDO = ‘JUSTIFICACAO_FALTA’,</p></li>
<li><p>ETAPA = DESPACHO_RH</p></li>
</ul>
<p><strong>2.2 R</strong>egisto na tabela de
<strong>RH_T_FALTA</strong>, campo do formulário e outros seguintes
campos</p>
<ul>
<li><p>DATA_REGISTO</p></li>
<li><p>USER_REGISTO_ID</p></li>
<li><p>USER_REGISTO_NAME</p></li>
<li><p>PEDIDO_ID = id de RH_T_PEDIDO</p></li>
<li><p>SINTESE_DIARIO_ID = id de
<strong>RH_ASSIDUIDADE_SINTESE_DIARIO</strong></p></li>
<li><p>TIPO = ‘FALTA’</p></li>
</ul>
<p><strong>2.3</strong> Regista na tabela
<strong>RH_T_VALIDACAO</strong></p></td>
</tr>
</tbody>
</table>

#### Validar Falta 

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><p>Ao Validar</p>
<ul>
<li><p>Atualiza o seguinte:</p>
<ul>
<li><p>RH_T_VALIDACAO.ESTADO = ‘A’</p></li>
<li><p>RH_T_PEDIDO.ETAPA = ‘FINALIZADO’</p></li>
<li><p>RH_T_FALTA.ESTADO = ‘A’</p></li>
<li><p>RH_T_FALTA.TIPREL_ID = ID de
<strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID onde
<strong>EST_ACT_ADM</strong> = 1</p></li>
</ul></li>
<li><p><strong>caso o tipo de falta desconta salário , logo deve
registar na Tabela</strong></p>
<ul>
<li><p><strong>RH_T_DEF_REMUNERACAO</strong></p></li>
</ul></li>
<li><p><strong>Caso o tipo de falta for desconto nas ferias</strong> ,
logo deve registar na Tabela</p>
<ul>
<li><p>Regista na tabela <strong>RH_T_FERIAS_GOZADAS</strong></p></li>
</ul></li>
<li><p><strong>Caso o tipo de justificação for desconto nas horas de
Dispensa</strong></p>
<ul>
<li><p>Regista na <strong>RH_T_DISPENSA</strong></p></li>
</ul></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

### Justificar Falta 

<img src="media/image14.png" style="width:9.42431in;height:5.28611in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

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
<td colspan="4"><strong>Informações colaborador</strong></td>
</tr>
<tr>
<td style="text-align: left;">Data</td>
<td></td>
<td></td>
<td><p>RH_T_FALTA.DATA_INICIO</p>
<p>RH_T_FALTA.DATA_FIM</p></td>
</tr>
<tr>
<td style="text-align: left;">Tipo (Falta ou Ausência)</td>
<td></td>
<td></td>
<td>RH_T_FALTA.TIPO</td>
</tr>
<tr>
<td style="text-align: left;">Horas de Ausência</td>
<td></td>
<td></td>
<td>RH_T_FALTA.HORAS_AUSENCIA</td>
</tr>
<tr>
<td style="text-align: left;">Valor de Ausência</td>
<td></td>
<td><p>Calcula o valor de falta por cada dia ..ou por cada horas de
ausência</p>
<p><mark>RH_PROCESSAMENTO_SALARIAL_DB. CALCULO_FALTA_DIARIO (P_TIPREL_ID
NUMBER, p_data_inicio DATE)</mark></p></td>
<td>RH_T_FALTA.VALOR</td>
</tr>
<tr>
<td style="text-align: left;">Motivo</td>
<td></td>
<td></td>
<td>RH_T_FALTA.DESCRICAO<strong>_</strong>MOTIVO</td>
</tr>
<tr>
<td style="text-align: left;"><del>Parecer (Justificar / não
Justificar)</del></td>
<td></td>
<td><del>DOMAIN = JUSTIFICADA_INJUSTIFICADA</del></td>
<td><del>RH_T_FALTA.TIPO_FALTA</del></td>
</tr>
<tr>
<td style="text-align: left;">-----------------------------</td>
<td>hidden</td>
<td><p>Depende do motivo selecionado</p>
<p>RH_T_TIPO_FALTA.DESCONTO_REMUNERACAO</p></td>
<td>RH_T_FALTA.FLG_DESCONTO_SAL</td>
</tr>
<tr>
<td style="text-align: left;">Com Justificativo</td>
<td>Seelct</td>
<td><strong>DOMAIN</strong> = SIM_NAO</td>
<td>RH_T_FALTA.FLG_JUSTIFICATIVO</td>
</tr>
<tr>
<td style="text-align: left;">Anexar Documento</td>
<td></td>
<td></td>
<td><p>RH_T_DOCUMENTO.<em>DOC_ID</em></p>
<p>RH_T_DOCUMENTO.TIPO_DOCUMETO = ID DE TABELA RH_T_TIPO_DOCUMENTO ONDE
REFERENCIA = ‘JUSTIFICACAO_FALTA’</p>
<p><mark>REFERENCIA_NAME = ‘RH_T_FALTA’</mark></p>
<p><mark>REFERENCIA_ID = id de RH_T_FALTA</mark></p></td>
</tr>
<tr>
<td style="text-align: left;">Selecionar</td>
<td></td>
<td></td>
<td>RH_T_FALTA.<em>SINTESE_DIARIO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Parecer Responsável</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Parecer Responsável</td>
<td>SELECT</td>
<td><strong>DOMAIN</strong> = PARECER_DECISAO, REFERENCIA =
PARECER_RESPONSAVEL</td>
<td>RH_T_FALTA.DECISAO_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;">Responsavel</td>
<td>SELECT</td>
<td>Pegar da Tabela <strong>RH_T_RESPONSAVEL.FUN_ID</strong> ,
<strong>RH_T_FUNCIONARIO</strong>.ID</td>
<td>RH_T_FALTA RESPONSAVEL_ID</td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td>SELECT</td>
<td></td>
<td>RH_T_FALTA.OBS_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Despacho RH</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Despacho RH</td>
<td>SELECT</td>
<td>DOMAIN = SIM_NAO</td>
<td>RH_T_FALTA.DESPACHO_RH</td>
</tr>
<tr>
<td style="text-align: left;">Tipo Justificação</td>
<td>SELECT</td>
<td>Desve buscar a descrição do tipo de falta escolhido … para buscar a
justificativo, deve buscar da tabela de parametrização
RH_T_PARAM_SITUACAO onde <em>FLG_AUSENCIA = FALTA</em></td>
<td>RH_T_FALTA<strong>.PARAM_SIT_ID</strong></td>
</tr>
<tr>
<td colspan="4"><strong>REGRA</strong></td>
</tr>
<tr>
<td colspan="4"><ul>
<li><p><em>Se o Tipo <strong>de Justificação</strong> é sujeito a
desconto de salário, logo ao validar um o pedido, deve-se registar na
RH_T_REMUNERACAO.</em></p></li>
<li><p><em>SE o ‘<strong>Tipo de justificação</strong> ’ é sujeito a
desconto nas ferias, logo ao validar deve-se registas nas ferias gozadas
(o sistema deve validar se realmente ainda o colaborador tem ferias por
Gozar)</em></p></li>
<li><p><em>Ao validar é enviado uma notificação ao Colaborador. pegar o
assunto e corpo na tabela <strong>RH_T_PARAM_NOTIFICACAO</strong> onde
REFRENCIA = <strong>‘JUSTIFICACAO_FALTA’’</strong></em></p></li>
</ul>
<p><strong>Pendente</strong>: calculo de valor na justificacao de
Falta</p></td>
</tr>
<tr>
<td colspan="4"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image13.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p>1-Registo na tabela <strong>RH_T_PEDIDO</strong></p>
<ul>
<li><p>TIPO_PEDIDO = <strong>DOMAIN</strong> = TIPO_PEDIDO ,
<strong>VALOR</strong> = JUSTIFICACAO_FALTA</p></li>
<li><p>ORIGEM = RH</p></li>
<li><p>ESTADO = <strong>‘P’</strong></p></li>
<li><p>FUN_ID</p></li>
<li><p>DATA_PEDIDO = <strong>SYSDATE</strong></p></li>
<li><p>DATA_REGISTO = <strong>SYSDATE</strong></p></li>
<li><p>USER_REGISTO_ID = user_logado</p></li>
<li><p>USER_ REGISTO_NAME = nome utilizador</p></li>
</ul>
<p>2- Registo na Tabela <strong>RH_T_VALIDACAO</strong></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘</strong></em>JUSTIFICAR
_FALTA<em><strong>’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= RH_T_PEDIDO.ID
</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
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
<th><p>Apôs Validação,</p>
<ul>
<li><p>Atualiza os estado pendentes para ativo</p></li>
<li><p>Caso a justificação é sujeita a desconto no salário, logo deve
registar no <strong>RH_T_DEF_REMUNERACOES</strong> o registo de
falta</p></li>
<li><p>Atualiza o id de remuneração na tabela
<strong>RH_T_FALTA.</strong>DEF<strong>_</strong>REM_ID</p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

### 

## Falta Justificada / injustificada

<img src="media/image15.png" style="width:9.69306in;height:4.62569in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

| **Filtro** | **Tipo** | **Descrição** | **Fonte dados** |
|----|----|----|----|
| Ilha |  |  | RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID |
| Direção | *SELECT* |  | RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID |
| Secção | *SELECT* |  | RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID |
| Data Inicio | *DATE* |  | RH_T_FALTA.DATA_INICIO |
| Data Fim | *DATE* |  | RH_T_FALTA.DATA_FIM |
| Estado | *SELECT* | DOMAIN= ESTADO_FALTA | RH_T_FALTA.ESTADO |
| **Lista** | **Tipo** | **Descrição** | **Fonte dados** |
| Estado | *TEXT* |  | RH_T_FALTA.ESTADO |
| Direção | *TEXT* |  | RH_T_FALTA.TIPREL_ID, RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID |
| Nome Colaborador | *TEXT* |  | RH_T_TIPOS_RELACIONAMENTO.FUN_ID |
| Categoria | *TEXT* |  | RH_T_TIPOS_RELACIONAMENTO.CARGO_ID |
| Data Inicio / Data Fim | *TEXT* |  | RH_T_FALTA.DATA_INICIO / RH_T_FALTA.DATA_FIM |
| Motivo | *TEXT* |  | RH_T_FALTA.TF_ID |
| Total Horas Ausente | *TEXT* |  | RH_T_FALTA.HORAS_AUSENTE |
| Numero De Falta | *TEXT* |  | RH_T_FALTA.NUM_FALTA |
| Valor a Descontar | *TEXT* |  | RH_T_FALTA.VALOR_DESCONTO |
| Desconto na Remuneração? | *TEXT* |  | RH_T_FALTA. TF_ID, RH_TIPO_FALTAS. FLG_DESCONTO_SALARIO |
| Estado Processamento | *TEXT* | Verifica se existe um registo em RH_REMUNERACOES | ----------------------------------------------------- |
| Acoes |  |  |  |
|  |  |  |  |

## Dispensa

### Lista 

<img src="media/image16.png" style="width:9.69306in;height:4.21806in"
alt="Uma imagem com texto, número, Tipo de letra, software Os conteúdos gerados por IA podem estar incorretos." />

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
<td>Colaborador</td>
<td><em>TEXT</em></td>
<td>RH_T_FUNCIONARIOS.NOME</td>
<td>RH_T_DISPENSA.TIPREL_ID</td>
</tr>
<tr>
<td>Ilha</td>
<td></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</td>
</tr>
<tr>
<td>Direção</td>
<td><em>SELECT</em></td>
<td>INPSSIGOF.INSTITUICOES.NOME</td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Seccão</td>
<td><em>SELECT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</td>
</tr>
<tr>
<td>Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td>RH_T_DISPENSA.DATA</td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td>RH_T_DISPENSA.DATA</td>
</tr>
<tr>
<td>ESTADO</td>
<td></td>
<td>DOMAIN = ESTADO_DISPENSA</td>
<td>RH_T_DISPENSA.ESTADO</td>
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
<td>INPSSIGOF.INSTITUICOES.NOME</td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Colaborador</td>
<td></td>
<td>RH_T_FUNCIONARIOS.NOME</td>
<td>RH_T_DISPENSA.TIPREL_ID</td>
</tr>
<tr>
<td>Vinculo</td>
<td></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.CONTRATO_VINCULO_ID</td>
</tr>
<tr>
<td>Categoria</td>
<td></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</td>
</tr>
<tr>
<td>Data pedido</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA. <em>DATA_REGISTO</em></td>
</tr>
<tr>
<td>Data Dispensa</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.DATA</td>
</tr>
<tr>
<td>Hora inicio / Fim</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.HORA_INICIO/RH_T_DISPENSA.HORA_FIM</td>
</tr>
<tr>
<td>Hora Direito</td>
<td></td>
<td><p>FAZ SUMATORIA DE TOTAL HORAS</p>
<p>Lembrate , pode usar o total de horas em justificação de
falta</p></td>
<td>SUM(RH_T_DISPENSA.TOTAL_HORA)</td>
</tr>
<tr>
<td>Total Horas solicitada</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.TOTAL_HORA</td>
</tr>
<tr>
<td>Motivo dispensa</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.MOTIVO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Novo</td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

### Novo / Editar 

<img src="media/image17.png" style="width:7.60833in;height:4.50417in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 39%" />
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
<td colspan="4"></td>
</tr>
<tr>
<td style="text-align: left;">Colaborador</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.TIPREL_ID</td>
</tr>
<tr>
<td style="text-align: left;">Horas usada no mês</td>
<td></td>
<td>Total horas de dispensa no mês . somar Hora inicio e Hora fim
(HORA_INICIO, HORA_FIM)</td>
<td>-------------------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Horas disponiveis</td>
<td></td>
<td style="text-align: left;">RH_ASSIDUIDADE_PARAMETRO.T_DISPENSA ,
somatoria de (HORA_INICIO, HORA_FIM)</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Data</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.DATA</td>
</tr>
<tr>
<td style="text-align: left;">Hora Inicio</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.HORA_INICIO</td>
</tr>
<tr>
<td style="text-align: left;">Hora Fim</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.HORA_FIM</td>
</tr>
<tr>
<td style="text-align: left;">Tipo Motivo</td>
<td></td>
<td>DOMAIN =MOTIVO_DISPENSA</td>
<td>RH_T_DISPENSA.TIPO_MOTIVO</td>
</tr>
<tr>
<td style="text-align: left;">Motivo</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.DESCRICAO_MOTIVO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Parecer Responsavel</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Parecer Responsável</td>
<td>SELECT</td>
<td><strong>DOMAIN</strong> = PARECER_DECISAO, REFERENCIA =
PARECER_RESPONSAVEL</td>
<td>RH_T_DISPENSA.DECISAO_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;">Responsavel</td>
<td>SELECT</td>
<td>Pegar da Tabela <strong>RH_T_RESPONSAVEL.FUN_ID</strong> ,
<strong>RH_T_FUNCIONARIO</strong>.ID</td>
<td>RH_T_DISPENSA.RESPONSAVEL_ID</td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td>SELECT</td>
<td></td>
<td>RH_T_DISPENSA.OBS_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Parecer RH</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.DECISAO_RH</td>
</tr>
<tr>
<td style="text-align: left;">Observação RH</td>
<td></td>
<td></td>
<td>RH_T_DISPENSA.OBS_RH</td>
</tr>
<tr>
<td style="text-align: left;">Anexar Documento</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Tipo documento</td>
<td></td>
<td></td>
<td>RH_T_DOCUMENTO.TIPO_DOMENTO_ID , onde tipo é referecia É igual a
<strong>DISPENSA</strong></td>
</tr>
<tr>
<td>Documento</td>
<td></td>
<td></td>
<td>RH_T_DOCUMENTO.DOC_ID</td>
</tr>
<tr>
<td colspan="4"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image13.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol type="1">
<li><p>Grava na tabela <strong>RH_T_PEDIDO</strong></p>
<ul>
<li><p>TIPO_PEDIDO = <strong>DOMAIN</strong> = TIPO_PEDIDO ,
<strong>VALOR</strong> = DISPENSA</p></li>
<li><p>ORIGEM = RH</p></li>
<li><p>ESTADO = <strong>‘P’</strong></p></li>
<li><p>FUN_ID</p></li>
<li><p>DATA_PEDIDO = <strong>SYSDATE</strong></p></li>
<li><p>DATA_REGISTO = <strong>SYSDATE</strong></p></li>
<li><p>USER_REGISTO_ID = user_logado</p></li>
<li><p>USER_ REGISTO_NAME = nome utilizador</p></li>
</ul></li>
</ol>
<blockquote>
<p>2.Grava na tabela <strong>RH_T_DISPENSA</strong></p>
</blockquote>
<ol type="a">
<li><p><em>TIPREL_ID = ID de RH_T_TIPOS_RELACIONAMENTO</em></p></li>
<li><p><em>DATA_REGISTO</em></p></li>
<li><p><em>USER_REGISTO_ID</em></p></li>
<li><p><em>USER_REGISTO_NAME</em></p></li>
<li><p><em>PEDIDO_ID = ID DE RH_T_PEDIDO</em></p></li>
</ol>
<blockquote>
<p>3.Registo na Tabela <strong>RH_T_VALIDACAO</strong></p>
</blockquote>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>=
‘</strong></em>DISPENSA<em><strong>’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= RH_T_PEDIDO.ID
</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul>
<p><em><strong><mark>Caso a dispensa for valida, log deve registar na
tabela Ausencia</mark></strong></em></p>
<p><strong><mark>O descrição do registo na
RH_T_AUSENCIA</mark></strong></p>
<ul>
<li><p>PARAM_SIT_ID = ID de tabela <strong>RH_T_PARAM_SITUACAO</strong>
onde nome = DISPENSA</p></li>
<li><p>REFERENCIA_NAME = nome de tabela
<strong>RH_T_DISPENSA</strong></p></li>
<li><p>REFERENCIA_ID = ID de tabela
<strong>RH_T_DISPENSA</strong></p></li>
<li><p>OBS =</p></li>
<li><p>DATA_INICIO = data inicio de DISPENSA</p></li>
<li><p>DATA_FIM = data fim de DISPENSA</p></li>
<li><p>HORA = hora de dispensa</p></li>
<li><p>ESTADO = ‘A’</p></li>
<li><p>DATA_REGISTO = SYSDATE</p></li>
<li><p>USER_REGISTO_ID = user logado</p></li>
<li><p>USER_REGISTO_NAME = nome de utilizador logado</p></li>
</ul></td>
</tr>
</tbody>
</table>

## Hora Extra

### Lista Hora Extra 

<img src="media/image18.png" style="width:9.69306in;height:4.45347in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 6%" />
<col style="width: 36%" />
<col style="width: 39%" />
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
<td>Colaborador</td>
<td><em>TEXT</em></td>
<td>RH_T_FUNCIONARIOS.NOME</td>
<td>RH_T_TIPOS_RELACIONAMENTO.FUN_ID</td>
</tr>
<tr>
<td>Ilha</td>
<td></td>
<td>SILGGLOBAL.GLB_GEOGRAFIA,RH_T_PARAM_LOCAL_TRAB.ILHA_ID</td>
<td>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</td>
</tr>
<tr>
<td>Direção</td>
<td><em>SELECT</em></td>
<td>INPSSIGOF.INSTITUICOES.NOME</td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Seccão</td>
<td><em>SELECT</em></td>
<td><em>RH_T_PARAM_SECAO.NOME</em></td>
<td>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</td>
</tr>
<tr>
<td>Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td>RH_T_HORA_EXTRA.DATA</td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td>RH_T_HORA_EXTRA.DATA</td>
</tr>
<tr>
<td>Estado</td>
<td></td>
<td></td>
<td>RH_T_HORA_EXTRA.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_T_HORA_EXTRA.ESTADO</td>
</tr>
<tr>
<td>Direção</td>
<td><em>TEXT</em></td>
<td>INPSSIGOF.INSTITUICOES.NOME</td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Secção</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td><em>TEXT</em></td>
<td>RH_T_FUNCIONARIOS.NOME</td>
<td>RH_T_HORA_EXTRA.TIPREL_ID</td>
</tr>
<tr>
<td>Data Inicio / Data Fim</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_T_HORA_EXTRA.DATA</td>
</tr>
<tr>
<td>Hora Contratada (Diário / Mensal)</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_T_HORA_EXTRA.HORAS_DIARIAS/ RH_T_HORA_EXTRA.HORAS_DIARIASX
12</td>
</tr>
<tr>
<td>Hora de Trabalho</td>
<td><em>TEXT</em></td>
<td></td>
<td>SUBSTR(TO_CHAR(<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.HORAS_EXTRAS),
4, 8);</td>
</tr>
<tr>
<td>Salario Mensal</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.SALARIO</td>
</tr>
<tr>
<td>Valor Horas Mensal</td>
<td></td>
<td></td>
<td>RH_T_HORA_EXTRA.VALOR_DIARIO X 12</td>
</tr>
<tr>
<td>Valor Horas Diário</td>
<td></td>
<td></td>
<td>RH_T_HORA_EXTRA.VALOR_DIARIO</td>
</tr>
<tr>
<td>Percentagem</td>
<td></td>
<td></td>
<td>RH_T_HORA_EXTRA.PERCENTAGEM</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Nova</td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

### Novo Hora Extra

<img src="media/image19.png" style="width:9.16944in;height:2.91389in" />

<table style="width:100%;">
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 39%" />
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
<td colspan="4"></td>
</tr>
<tr>
<td style="text-align: left;">Validar</td>
<td>RADIOLIST</td>
<td>Campo só aparece no momento de validação</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Direção</td>
<td>SELECT</td>
<td></td>
<td>----------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Colaborador</td>
<td>SELECT</td>
<td></td>
<td>RH_T_HORA_EXTRA.TIPREL_ID</td>
</tr>
<tr>
<td style="text-align: left;">Data inicio</td>
<td>DATE</td>
<td></td>
<td>RH_T_HORA_EXTRA.DATA_INICIO</td>
</tr>
<tr>
<td style="text-align: left;">Hora Fim</td>
<td>DATE</td>
<td></td>
<td>RH_T_HORA_EXTRA.DATA_FIM</td>
</tr>
<tr>
<td style="text-align: left;">Horas diárias</td>
<td>DATE</td>
<td></td>
<td>RH_T_HORA_EXTRA.HORAS_DIARIAS</td>
</tr>
<tr>
<td style="text-align: left;">Percentagem Referente</td>
<td>Select</td>
<td><p>DOMAINS = <strong>DIAS_PERCENTAGEM_HORA</strong></p>
<ul>
<li><p>Dias Úteis</p></li>
<li><p>Dias Uteis e Não Úteis</p></li>
<li><p>Dias Não Úteis</p></li>
</ul></td>
<td>RH_T_HORA_EXTRA.PERCENTAGEM</td>
</tr>
<tr>
<td style="text-align: left;">Valor Diário</td>
<td>DISABLED</td>
<td><p>FAZ O CALCULO DO VALOR DE HORA EXTRA</p>
<p><strong>RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_HORA_EXTRA</strong>
(P_TIPREL_ID NUMBER,P_DATA_INICIO DATE,P_DATA_FIM DATE,P_DIAS_APLICADA
VARCHAR2, P_HORAS_DIARIA NUMBER)</p></td>
<td>RH_T_HORA_EXTRA.VALOR_DIARIO</td>
</tr>
<tr>
<td style="text-align: left;">Anexar Documento</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Tipo documento</td>
<td>SELECT</td>
<td>RH_T_TIPOS_DOCUMENTO.NOME onde a Referência =
‘<strong>HORA_EXTRA</strong>’</td>
<td>RH_T_DOCUMENTO.TP_DOCUMENTO_ID</td>
</tr>
<tr>
<td>Documento</td>
<td>UPLOAD</td>
<td></td>
<td>RH_T_DOCUMENTO.DOC_ID</td>
</tr>
<tr>
<td colspan="4"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image13.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol type="1">
<li><p>Gravação na tabela RH_T_HORA_EXTRA</p></li>
</ol>
<ul>
<li><p>PEDIDO_ID = id de RH_T_PEDIDO</p></li>
<li><p>TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO</p></li>
<li><p>SINTESE_DIARIO_ID = pega o ID de registo em síntese referente a
DATA_INICIO e DATA_FIM</p></li>
<li><p>VALOR_DIARIO (CALCULA O VALOR DIARIA DE HORA EXTRA)</p>
<ol type="1">
<li><p>Gravação na Tabela RH_T_VALIDACAO</p></li>
</ol></li>
</ul>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘</strong>HORA_EXTRA<strong>’
(</strong>DOMAINS = ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= RH_T_PEDIDO.ID
</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS </strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE </strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

**Exemplo**:

| **Data início** | **Data fim** | **Mês** | **Dias úteis** | **Dias não úteis** | **% aplicado** | **Valor diário (hora extra)** | **Valor acumulado no mês** |
|---:|---:|---:|:--:|:--:|:---|:---|:---|
| **20/01/2026** | 31/01/2026 | 202601 | 9 | 3 | U=50% / N=100% | U=1.111,11 N=2.222,22 | 14.999,98 |
| 01/02/2026 | 28/02/2026 | 202602 | 20 | 8 | U=50% / N=100% | U=1.111,11 N=2.222,22 | 31.111,08 |
| 01/03/2026 | **10/03/2026** | 202603 | 8 | 2 | U=50% / N=100% | U=1.111,11 N=2.222,22 | 11.111,10 |

#### Validação

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><p>Apôs Validação,</p>
<ul>
<li><p>Atualiza os estado pendentes para ativo</p></li>
<li><p>Deve registar no <strong>RH_T_DEF_REMUNERACOES</strong> o registo
DE HORA EXTRA</p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

## Ferias 

<img src="media/image20.png" style="width:4.73333in;height:1.05625in"
alt="Uma imagem com texto, Tipo de letra, branco, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

### Lista Ferias 

<img src="media/image21.png" style="width:9.69306in;height:4.79375in"
alt="Uma imagem com texto, captura de ecrã, software, Ícone de computador Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 5%" />
<col style="width: 36%" />
<col style="width: 41%" />
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
<td><em>S</em></td>
<td>RH_T_ANO.ANO</td>
<td></td>
</tr>
<tr>
<td>Ilha</td>
<td><em>SELECT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</td>
</tr>
<tr>
<td>Direção</td>
<td><em>SELECT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Secção</td>
<td><em>SELECT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</td>
</tr>
<tr>
<td>Colaborador</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.FUN_ID</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Direcção</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Secção</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.SECCAO</td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td><em>TEXT</em></td>
<td>RH_T_FUNCIONARIOS.NOME</td>
<td>RH_T_FERIAS.FUN_ID</td>
</tr>
<tr>
<td>Vinculo</td>
<td><em>TEXT</em></td>
<td>RH_T_CONTRATO_VINCULO.TP_VINCULO_ID, RH_T_PARAM_VINCULO.NOME</td>
<td>RH_T_TIPOS_RELACIONAMENTO.CONTR_VINCULO_ID</td>
</tr>
<tr>
<td>Categoria</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.CARGO</td>
</tr>
<tr>
<td>Total Direito</td>
<td><em>TEXT</em></td>
<td></td>
<td><strong>SUM</strong>(RH_T_FERIAS.NUM_DIA)</td>
</tr>
<tr>
<td>Total direito (dias / ano)</td>
<td></td>
<td>Caso não tenha dados logo apresenta 0</td>
<td>(RH_T_FERIAS.NUM_DIA || RH_T_FERIAS.ANO_REFERENTE )</td>
</tr>
<tr>
<td>Total Planeado (Data Inicio / data fim)</td>
<td></td>
<td>Caso não tenha dados logo apresenta 0</td>
<td><p>RH_T_FERIAS_MAPA NUM_DIA (RH_T_FERIAS_MAPA.DATA_INICIO</p>
<p>|| RH_T_FERIAS_MAPA.DATA_FIM)</p></td>
</tr>
<tr>
<td>Total Gozado (Data Inicio / Data Fim )</td>
<td></td>
<td>Caso não tenha dados logo apresenta 0</td>
<td>RH_T_FERIAS_GOZADA.NUM_DIA
(RH_T_FERIAS_GOZADA.DATA_INICIO||RH_T_FERIAS_GOZADA.DATA_FIM)</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td><img src="media/image22.png"
style="width:1.17431in;height:0.28264in" /></td>
<td colspan="3">Permite exportar dados de direitos a cada direção</td>
</tr>
<tr>
<td><p><img src="media/image23.png"
style="width:0.93681in;height:0.32014in" /></p>
<p><img src="media/image24.png"
style="width:1.14931in;height:0.23264in" /></p></td>
<td colspan="3">Pemite Abrir formulario para solicitar Ferias</td>
</tr>
<tr>
<td><img src="media/image25.png"
style="width:1.11181in;height:0.26181in" /></td>
<td colspan="3">Permite ver ferias agendadas e por gozar</td>
</tr>
<tr>
<td><img src="media/image26.png"
style="width:1.17014in;height:0.23264in" /></td>
<td colspan="3">Permite enviar direito de ferias por email</td>
</tr>
</tbody>
</table>

#### Exportar direito

| **CODIGO_DIRECAO** | **NOME_DIRECAO** | **ID_COLABORADOR** | **NOME_COLABORADOR** | **TOTAL_DIREITO** | **TOTAL_DIREITO_ANO** |
|:---|:---|---:|:---|---:|:---|
| 00.01.01 | Direção dos Recursos Humanos | 1223 | Gertrudes Helena | 44 | 22 (2023); 22 (2024) |
| 00.00.01 | Direção dos Recursos Humanos | 1222 | Manuela gomes | 27 | 5 (2023); 22 (2024) |

| **Coluna Excel** | **Fonte dados** |
|----|----|
| CODIGO_DIRECAO | INPSSIGOF.INSTITUICAO.CODIGO |
| NOME_DIRECAO | INPSSIGOF.INSTITUICAO.NOME |
| ID_COLABORADOR | RH_T_FUNCIONARIOS.ID_COLABORADOR |
| NOME_COLABORADOR | RH_T_FUNCIONARIOS.NOME |
| TOTAL_DIREITO | SUM(RH_T_FERIAS.NUM_DIA) |
| TOTAL_DIREITO_ANO | AGRUPAR (RH_T_FERIAS. NUM_DIA)\|\| RH_T_FERIAS.ANO_REFERENTE) |

### Pedido Ferias / Alteração de ferias 

<img src="media/image27.png" style="width:9.69306in;height:5.44861in"
alt="Uma imagem com texto, captura de ecrã, software, número Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 34%" />
<col style="width: 2%" />
<col style="width: 39%" />
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
<td colspan="5"><strong>Dados Solicitação</strong></td>
</tr>
<tr>
<td style="text-align: left;">Nome colaborador</td>
<td>SELECT</td>
<td colspan="2">Nome do colaborador que entrara de ferias</td>
<td><p>RH_T_FERIA_GOZADAS.FUN_ID</p>
<p>RH_T_PEDIDO.FUN_ID</p></td>
</tr>
<tr>
<td style="text-align: left;">Ferias Por Gozar</td>
<td>deabled</td>
<td colspan="2">Verifica se o numero de ferias que ainda falta o
colaborador Gozar , referente ao mesmo ano corrente</td>
<td>RH_T_FERIAS.NUM_DIA – RH_T_FERIAS_GOZADAS.NUM_DIA</td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio</td>
<td>DATE</td>
<td colspan="2">Data inicio do colaborador</td>
<td><p>RH_T_FERIA_GOZADAS.DATA_INICIO</p>
<p>RH_T_SUBSTITUICAO.DATA_INICIO</p></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td>DATE</td>
<td colspan="2">Data fim do colaborador</td>
<td><p>RH_T_FERIA_GOZADAS.DATA_FIM</p>
<p>RH_T_SUBSTITUICAO.DATA_FIM</p></td>
</tr>
<tr>
<td style="text-align: left;">Numero dias</td>
<td>NUMBER</td>
<td colspan="2">Numero de dias de ferias do colaborador</td>
<td>RH_T_FERIA_GOZADAS.NUM_DIA</td>
</tr>
<tr>
<td style="text-align: left;">Substituído Por:</td>
<td>SELECT</td>
<td colspan="2">Identificação do colaborador que substituirá o
colaborador de férias</td>
<td><p><mark>RH_T_SUBSTITUICAO.SUSBSTITUTO_ID</mark></p>
<p><mark>RH_T_SUBSTITUICAO.SUBSTITUTO_ID</mark></p></td>
</tr>
<tr>
<td colspan="5"><strong>Alteração De Data Ferias (este separador só
aparece em caso de edição)</strong></td>
</tr>
<tr>
<td style="text-align: left;">Alteração Ferias</td>
<td></td>
<td colspan="2">faz novo registo com novo registo o id de
rh_t_ferias_gozada que deu origem a essa alteração</td>
<td><p>RH_T_FERIAS_GOZADA.TIPO_ALTERACAO</p>
<p>RH_T_FERIA_GOZADAS.FERIAS_GOZADA_ID</p></td>
</tr>
<tr>
<td style="text-align: left;">Nova Data Fim</td>
<td>DATE</td>
<td colspan="2">Nova data de ferias</td>
<td>RH_T_FERIAS_GOZADAS.DATA_FIM</td>
</tr>
<tr>
<td style="text-align: left;">Motivo</td>
<td>TEXTAREA</td>
<td colspan="2"></td>
<td>RH_T_FERIAS_GOZADAS.MOTIVO_ALTERACAO</td>
</tr>
<tr>
<td colspan="5"><strong>Informaçoes Sobre conveniêncoa</strong></td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td>TEXTAREA</td>
<td colspan="2"></td>
<td>RH_T_FERIA_GOZADAS.OBS_INFO_CONVENIENCIA</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Analise Responsável</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Parecer Responsável</td>
<td>SELECT</td>
<td colspan="2"><strong>DOMAIN</strong> = PARECER_DECISAO, REFERENCIA =
PARECER_RESPONSAVEL</td>
<td>RH_T_FERIA_GOZADAS.DECISAO_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;">Responsavel</td>
<td>SELECT</td>
<td colspan="2">Pegar da Tabela <strong>RH_T_RESPONSAVEL.FUN_ID</strong>
, <strong>RH_T_FUNCIONARIO</strong>.ID</td>
<td>RH_T_FERIA_GOZADAS.RESPONSAVEL_ID</td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td>SELECT</td>
<td colspan="2"></td>
<td>RH_T_FERIA_GOZADAS.OBS_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;">-------------------------</td>
<td>HIDDEN</td>
<td
colspan="2">-----------------------------------------------------------------------</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Validação RH</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Valida</td>
<td>RADIOLIST</td>
<td colspan="2"><p>Regista o parecer do RH</p>
<p>DOMAIN = SIM_NAO</p></td>
<td>RH_T_FERIA_GOZADAS.DECISAO_RH</td>
</tr>
<tr>
<td style="text-align: left;">Observação</td>
<td>TEXTAREA</td>
<td colspan="2"></td>
<td>RH_T_FERIA_GOZADAS.OBS_RH</td>
</tr>
<tr>
<td style="text-align: left;">-----------------------------</td>
<td>HIDDEN</td>
<td
colspan="2">---------------------------------------------------------------------------</td>
<td>RH_T_PEDIDO.TIPO = ‘FERIAS’</td>
</tr>
<tr>
<td style="text-align: left;">----------------------------------</td>
<td>HIDDEN</td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Anexar Documento</strong></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td>Tipo documento</td>
<td>SELECT</td>
<td colspan="2">Traz somente tipo documento cujo referencia =
´FERIAS´</td>
<td>RH_T_DOCUMENTO.TP_DOCUMENTO_ID</td>
</tr>
<tr>
<td>Documento</td>
<td>UPLOAD</td>
<td colspan="2"></td>
<td>RH_T_DOCUMENTO.DOC_ID</td>
</tr>
<tr>
<td>REGRA</td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td colspan="5"><ol type="1">
<li><p>Regista na tabela <strong>RH_T_SUBSTITUICAO</strong>, Caso for
indicado o substituto</p></li>
<li><p>Regista na tabela <strong>RH_T_DOCUMENTO</strong>, caso for
anexado algum documento</p></li>
<li><p>O separador <strong>Alteração data Ferias,</strong> só deve ficar
visível em caso de edição.</p>
<ol type="a">
<li><p><mark>Caso for feito um pedido ou alteração da data fim de
ferias</mark>, logo deva inativa o registo na tabela
<strong>RH_T_FERIAS_GOZADAS</strong>, e criar um novo registo nas
tabelas de <strong>RH_T_PEDIDO</strong>, <strong>RH_T_FERIAS_GOZADAS e
RH_T_AUSENCIA</strong></p></li>
<li><p><strong><mark>Só deve registar na tabela ausência, caso a feria
for validado ou seja decisão RH for SIM</mark></strong></p></li>
</ol></li>
</ol></td>
</tr>
<tr>
<td colspan="5"><p><strong><mark>O descrição do registo na
RH_T_AUSENCIA</mark></strong></p>
<ul>
<li><p>PARAM_SIT_ID = ID de tabela <strong>RH_T_PARAM_SITUACAO</strong>
onde nome = Ferias</p></li>
<li><p>REFERENCIA_NAME = nome de tabela
<strong>RH_T_FERIAS_GOZADAS</strong></p></li>
<li><p>REFERENCIA_ID = ID de tabela
<strong>RH_T_FERIAS_GOZADAS</strong></p></li>
<li><p>OBS = caso for alterado ferias , ‘ALTERACAO DE FERIAS’</p></li>
<li><p>DATA_INICIO = data inicio de ferias</p></li>
<li><p>DATA_FIM = data fim de ferias</p></li>
<li><p>ESTADO = ‘A’</p></li>
<li><p>DATA_REGISTO = SYSDATE</p></li>
<li><p>USER_REGISTO_ID = user logado</p></li>
<li><p>USER_REGISTO_NAME = nome de utilizador logado</p></li>
</ul></td>
</tr>
<tr>
<td colspan="5"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image13.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="2"><p>Novo</p>
<ol type="1">
<li><p>Gravação na tabela <strong>RH_T_PEDIDO</strong></p></li>
</ol>
<ol type="1">
<li><p>Gravação na tabela <strong>RH_T_FERIA_GOZADAS</strong></p></li>
<li><p>Gravação na tabela <strong>RH_T_SUBSTITUICAO</strong></p></li>
<li><p>Gravação na <strong>RH_T_AUSENCIA</strong></p></li>
</ol>
<p>5 regista na tabela <strong>RH_T_DCUMENTO</strong></p></td>
<td colspan="2"><p>Alteração</p>
<ol type="1">
<li><p>Inativa RH_T_PEDIDO ANTERIOR</p></li>
<li><p>Gravação na tabela <strong>RH_T_PEDIDO</strong></p></li>
<li><p>Gravação na tabela <strong>RH_T_FERIA_GOZADAS</strong></p></li>
<li><p><mark>Gravação na tabela <strong>RH_T_SUBSTITUICAO</strong> (caso
for substituído)</mark></p></li>
</ol>
<ol start="4" type="1">
<li><p><mark>Gravação na <strong>RH_T_AUSENCIA (</strong>caso alterar a
data<strong>)</strong></mark></p></li>
</ol>
<ol start="5" type="1">
<li><p>regista na tabela <strong>RH_T_DCUMENTO</strong></p></li>
</ol></td>
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
<th><p>Apôs validação o colaborador e recebe uma notificação ,
confirmando os dias que tem de ferias e quanto dias mais falta por
gozar.</p>
<ul>
<li><p>Envia Email e Regista na tabela
<strong>RH_T_NOTIFICACAO</strong></p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

### Ver Mapa

<img src="media/image28.png" style="width:9.69306in;height:4.56181in"
alt="Uma imagem com texto, software, Ícone de computador, Página web Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 33%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Nome colaborador</strong></th>
<th style="text-align: center;"><strong>Ferias Agendadas</strong></th>
<th style="text-align: center;"><strong>Ferias Agendadas</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>RH_T_FUNCIONARIO.NOME</td>
<td><p>RH_T_FERIAS_MAPADATA_INICIO</p>
<p>RH_T_FERIAS_MAPA.DATA_FIM</p></td>
<td><p>RH_T_FERIAS_GOZADA.DATA_INICIO</p>
<p>RH_T_MAPA_FERIAS.DATA_FIM</p></td>
</tr>
</tbody>
</table>

### Enviar Direito

<table style="width:100%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><p>Permite Enviar email dos direitos de ferias para o colaborador e
Responsável.</p>
<ul>
<li><p>Pega os dados (Assunto , corpo) de Notificação a partir da tabela
<strong>RH_T_PARAM_NOTIFICACAO</strong></p></li>
<li><p>Guarda dados de notificação na tabela
<strong>RH_T_NOTIFICACAO</strong></p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

### Mapa de Ferias 

<img src="media/image29.png" style="width:9.69306in;height:5.04514in"
alt="Uma imagem com texto, captura de ecrã, software, número Os conteúdos gerados por IA podem estar incorretos." />

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
<th style="text-align: center;"></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Ano Referente</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td>RH_T_FERIAS_MAPA.ANO_ID</td>
</tr>
<tr>
<td>Ilha</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td
style="text-align: left;">RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</td>
</tr>
<tr>
<td>Direção</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</td>
</tr>
<tr>
<td>Secção</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td>RH_T_SECAO.ID</td>
</tr>
<tr>
<td>Estado</td>
<td style="text-align: center;"></td>
<td style="text-align: center;"><strong>DOMAIN =</strong>
ESTADO_VALIDACAO</td>
<td>RH_T_FERIAS_MAPA.ESTADO_VALIDACAO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Estado</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">Pendente, validado</td>
<td>RH_T_FERIAS_MAPA.ESTADO</td>
</tr>
<tr>
<td>Direcção</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"></td>
<td>INPSSIGOF.INSTITTUICOES.NOME||</td>
</tr>
<tr>
<td>Total Colaborador</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;"></td>
<td><p>COUNT(<strong>RH_T_TIPOS_RELACIONAMENTO</strong>.EST_ACT_ADM = 1
AND</p>
<p><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.INSTIT_ID =
<strong>RH_T_FERIAS_MAPA</strong>.INSTIT_ID)</p></td>
</tr>
<tr>
<td>Total Ferias Agendadas</td>
<td style="text-align: center;"><em>TEXT</em></td>
<td style="text-align: center;">Total Colaborador - Total Ferias
Agendadas;</td>
<td>RH_T_FERIAS_MAPA.FUN_ID</td>
</tr>
<tr>
<td><strong>Acções</strong></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td></td>
</tr>
<tr>
<td><img src="media/image30.png"
style="width:1.14514in;height:0.25347in" /></td>
<td colspan="3" style="text-align: center;">Botão que permite importar
mapa, caso se importar uma mapa que já existe, log se deve
<strong>inactivar</strong> o outra
(<strong>RH_T_FERIAS_MAPA</strong>.ESTADO = “A”)</td>
</tr>
<tr>
<td><img src="media/image31.png"
style="width:1.17014in;height:0.24514in" /></td>
<td colspan="3" style="text-align: center;">Permite exportar mapa por
direção com os direitos de cada colaborador</td>
</tr>
<tr>
<td><img src="media/image32.png"
style="width:1.15556in;height:0.25417in" /></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Ver Mapa</td>
<td colspan="3" style="text-align: center;">Invoca a mesma mapa descrita
acima (<a href="#_Ver Mapa"><u>VER MAPA</u></a>)</td>
</tr>
</tbody>
</table>

#### Importar Mapa

(pendente RH envia Modelo)

####  Ver detalhe 

<img src="media/image33.png" style="width:9.69306in;height:4.39722in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
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
<td>Ferias Agendadas</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Nome Colaborador</td>
<td><em>TEXT</em></td>
<td>RH_T_FUNCIONARIO.NOME</td>
<td>RH_T_FERIAS_MAPA.FUN_ID</td>
</tr>
<tr>
<td>Total Direito</td>
<td><em>TEXT</em></td>
<td></td>
<td>COUNT(RH_T_FERIAS.NUM_DIA)</td>
</tr>
<tr>
<td>Total direito por ano</td>
<td><em>TEXT</em></td>
<td></td>
<td>COUNT(RH_T_FERIAS.NUM_DIA) agrupando por RH_T_FERIAS.NUM_DIA</td>
</tr>
<tr>
<td>Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td>RH_T_FERIAS_MAPA.DATA_INICIO</td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td>RH_T_FERIAS_MAPA.DATA_FIM</td>
</tr>
<tr>
<td>Ferias por Agendar</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Nome colaborador</td>
<td></td>
<td><p>RH_T_FUNCIONARIO.NOME</p>
<p>Verifica todos colaboradores de mesma Direção (RH_T_FERIAS.INSTIT_ID)
mas que não estão nessa tabela (RH_T_FERIAS_MAPA.INSTIT_ID)</p></td>
<td><p>RH_T_FERIAS.INSTIT_ID</p>
<p>RH_T_FERIAS_MAPA.FUN_ID</p></td>
</tr>
<tr>
<td>Total direito</td>
<td></td>
<td></td>
<td>RH_T_FERIAS.NUM_ID</td>
</tr>
<tr>
<td>Total direito por ano</td>
<td></td>
<td></td>
<td>RH_T_FERIAS. NUM_ID, RH_T_FERIAS.ANO_ID</td>
</tr>
</tbody>
</table>

# Modelo Dados

## FALTA

<img src="media/image34.png" style="width:5.55208in;height:4.59375in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

## HORA EXTRA

<img src="media/image35.png" style="width:7.61458in;height:3.11458in"
alt="Uma imagem com texto, captura de ecrã, diagrama, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

## DISPENSA

<img src="media/image36.png" style="width:8.36458in;height:2.59375in" />

## FERIAS

<img src="media/image37.png" style="width:9.69306in;height:4.47014in" />

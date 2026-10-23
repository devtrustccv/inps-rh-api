<figure>
<img src="media/image3.jpeg" style="width:14.65694in;height:9.77083in"
alt="C:\Users\joelm\Desktop\Imagens\sergey-zolkin-_UeY8aTI6d0-unsplash (2).jpg" />
<figcaption><p>SIPS-RH</p></figcaption>
</figure>

**SIMULAÇÃO PROGRESSÃO E PROMOÇÃO**

# Enquadramento 

A funcionalidade de simulação de progressão e promoção constitui o
núcleo operacional do sistema de gestão de carreiras, permitindo ao
departamento de Recursos Humanos avaliar previamente o impacto das
movimentações de carreira antes da sua efetivação. Esta funcionalidade
visa garantir a transparência, equidade e conformidade com os
regulamentos internos da organização

# Âmbito 

Para a simulação de Progressão e Promoção, se pretende implementar o
seguinte:

<img src="media/image4.png" style="width:9.69306in;height:2.58194in"
alt="Uma imagem com texto, captura de ecrã, Tipo de letra, logótipo Os conteúdos gerados por IA podem estar incorretos." />

- **Sistema RH**

<table style="width:99%;">
<colgroup>
<col style="width: 33%" />
<col style="width: 66%" />
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
<td style="text-align: center;"><p>Parametrização</p></td>
<td><p>Permite parametrizar regras para a simulação de progressão e
promoção</p>
<ul>
<li><p>Situação que congelam a progressão / Promoção</p></li>
<li><p>Vínculo que o colaborador progride</p></li>
<li><p>Tempo de progressão e promoção por tipo de cargo</p></li>
<li><p>Nota de avaliação de desempenho de progressão e promoção</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><p>Processo Simular Progressão /
Promoção</p></td>
<td><p>funcionalidade de <strong>simulação de progressão ou
promoção</strong> tem como objetivo permitir que o sistema avalie
automaticamente se um colaborador reúne as condições necessárias para
evoluir na carreira, com base nos critérios definidos pelo regulamento
interno. A simulação é realizada a partir da análise do tempo de
permanência no escalão atual, da avaliação de desempenho, do tipo de
vínculo e de eventuais situações que interrompam a contagem de tempo,
como licenças ou afastamentos.</p>
<p>O resultado da simulação é apresentado ao setor de Recursos Humanos,
que pode analisar os dados, confirmar a elegibilidade, e seguir com a
<strong>validação formal</strong> da progressão ou promoção. Após a
validação, o sistema procede à criação automática de uma
<strong>mobilidade interna</strong>, atualizando o escalão e o salário
do colaborador, e gera uma <strong>ordem de serviço</strong> com os
dados da progressão, pronta para ser assinada pelo presidente ou
autoridade competente.</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Lista de simulação</p></td>
<td><p>Esta lista permite ao setor de Recursos Humanos visualizar e
acompanhar o histórico de progressões e promoções dos colaboradores, bem
como executar ações como validação e emissão de ordens de serviço. Para
facilitar a análise, o sistema disponibiliza filtros que tornam a
pesquisa mais eficiente e ajustada às necessidades operacionais do
RH.</p></td>
</tr>
</tbody>
</table>

**Portal - (***Nota: Especificação das funcionalidade do Portal Serão
feitas na segunda Fase do projecto)*

| **Funcionalidade** | **Descrição** |
|:--:|----|
| Historico de progressão e Promoção | Permite ao colaborador visualizar todo o seu historico de promoção e progressão no portal |

# Especificação 

## Simulação Promoção / Progressão

### Fluxo

<img src="media/image5.png" style="width:8.02917in;height:4.12917in" />

### Descrição Processo 

<table style="width:97%;">
<colgroup>
<col style="width: 22%" />
<col style="width: 24%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Etapa</strong></p></th>
<th style="text-align: center;"><p><strong>Responsável</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Simulação</p></td>
<td><p>RH / Sistema</p></td>
<td><p>Simulação do cálculo de progressão /promoção consiste na
verificação automática dos critérios estabelecidos, como tempo mínimo no
escalão atual e tipo de vínculo, avaliação de desempenho (todos
funcionais definidos neste documento), para determinar se o colaborador
está elegível a avançar um escalão dentro da sua carreira.</p>
<p>O sistema deverá alertar ao RH sobre possíveis progressões ou
promoções previstas para o mês seguinte, com base na análise automática
dos critérios de elegibilidade dos colaboradores.</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Validação</p></td>
<td><p>RH</p></td>
<td><p>Após a simulação, o RH pode visualizar a lista de colaboradores
progredidos e promovidos. Ao confirmar a validação, o sistema regista na
mobilidade o novo escalão e o novo salário, encerrando a mobilidade
anterior com a seguinte observação: “<strong>Fecho de mobilidade ou
salário referente à progressão/promoção do ano XPTO</strong>.”</p>
<p>Ou seja, ao Confirmar o sistema faz as seguintes Ações</p>
<ul>
<li><p>É criada uma <strong>mobilidade</strong>;</p></li>
<li><p>Novo <strong>salário</strong> é associado ao novo
escalão;</p></li>
<li><p>O sistema gera uma <strong>Ordem de Serviço</strong> (OS)
com:</p>
<ul>
<li><p><strong>Nome do Colaborador</strong></p></li>
<li><p><strong>Categoria</strong></p></li>
<li><p><strong>Escalão</strong></p>
<ul>
<li><p>De</p></li>
<li><p>Para</p></li>
</ul></li>
</ul></li>
</ul>
<p>A lista de progressão fica, publica no portal do colaborador</p>
<p><strong>Ouput Ordem Serviço</strong>: O sistema disponibilida um
documento de <strong>Ordem de Serviço com</strong> lista de
progredido.</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Confirmar Progressao</p></td>
<td></td>
<td><p>A ação <strong>Confirmar Progressão</strong> tem como objetivo
registar oficialmente a progressão do colaborador no sistema,
reconhecendo que este avançou para um novo nível, escalão ou posição na
sua carreira. Ao executar esta ação, o sistema atualiza automaticamente
a escala do colaborador de acordo com as regras definidas, refletindo a
sua nova situação funcional. Como consequência direta, são também
recalculados e atualizados os dados remuneratórios, incluindo o novo
salário correspondente ao escalão atribuído.</p></td>
</tr>
</tbody>
</table>

### SIMULACAO

A figura abaixo apresenta a lista de colaboradores identificados como
elegíveis para progressão ou promoção, conforme resultado da
funcionalidade de simulação. Esta simulação obedece aos critérios
definidos nos requisitos funcionais detalhados na alínea seguinte.

<img src="media/image6.png" style="width:9.69306in;height:3.22639in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 5%" />
<col style="width: 45%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th><p><strong>Descrição</strong></p></th>
<th><p><strong>Fonte de Dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Progressão / Promoção</p></td>
<td><p>SELECT</p></td>
<td><p>Permite efetuar a pesquisa por progrssão ou promoção
(<strong>DOMAINS =</strong> TIPO_MOV_LABORAL E REFERENCIA =
CARREIRA)</p>
<ul>
<li><p>Progressão</p></li>
<li><p>Promocão</p></li>
</ul></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>.TIPO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><p>Progressão / Promoção</p></td>
<td><p>TEXT</p></td>
<td><p>Indica se o item na lista corresponde a uma progressão ou a uma
promoção</p></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>.TIPO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Referente</p></td>
<td><p>TEXT</p></td>
<td><p>Data referente à progressão ou promoção</p></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>.DATA_REFERENTE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Nome Colaborador</p></td>
<td><p>Text</p></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p>Carreira</p></td>
<td><p>TEXT</p></td>
<td><p>Categoria do colaborador progredido ou promovido.</p>
<p><em><strong>RH_T_PARAM_CARREIRA</strong></em>.NOME</p></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>.CARREIRA_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Cargo</p></td>
<td></td>
<td><p><em><strong>RH_T_PARAM_CARGO</strong></em>.NOME</p></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>.CARREIRA_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Escalão De:</p></td>
<td><p>TEXT</p></td>
<td><p>Escalão antes da progressão ou promoção</p></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>.ESCALAO_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Escalão Para:</p></td>
<td><p>TEXT</p></td>
<td><p>Escalão para o qual foi progredido ou promovido</p>
<p><strong>RH_T_PARAM_ESCALAO</strong>.<em>NIVEL_REFERENCIA</em>!!
<strong>RH_T_PARAM_ESCALAO.</strong><em>ESCALAO</em></p></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>.ESCALAO_ID_PARA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Avaliação Média</p></td>
<td><p>TEXT</p></td>
<td></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA.</strong>AVALIACAO_MEDIA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Obervação</p></td>
<td><p>TEXT</p></td>
<td><p>Descreve as razões pelas quais o colaborador progrediu ou foi
promovido, detalhando todas as regras aplicadas</p>
<p><strong>RH_T_PARAM_ESCALAO</strong>.<em>NIVEL_REFERENCIA</em>!!
<strong>RH_T_PARAM_ESCALAO</strong>.<em>ESCALAO</em></p></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>.OBS</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Histórica</p></td>
<td><p>TEXT</p></td>
<td></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>.FLG_HISTORICO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Acções</strong></p></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image7.png"
style="width:0.95347in;height:0.22014in" /></p></td>
<td colspan="3"><p>Exportar para pdf ou Excel</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image8.png"
style="width:0.71429in;height:0.26316in" /></p></td>
<td colspan="3"><p>Faz invoca o seguinte procedimento…. Esse
procedimento Também deve deve ser via Job.</p>
<p><strong>RH_PK_PROMO_PROGRESSAO.</strong>PR_SIMULAR (P_DATA_REFERENCIA
IN DATE DEFAULT TRUNC(SYSDATE),P_USER_ID IN NUMBER, P_USER_NAME IN
VARCHAR2, P_ORIGEM VARCHAR DEFAULT NULL)</p>
<p><strong>Nota</strong>: Caso o Procedimento for invocado via aplicação
, logo P_ORIGEM = <strong>MANUAL</strong>, caso contrario é
nulo</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image9.png"
style="width:0.78333in;height:0.28958in" /></p></td>
<td colspan="3"><p>Para validar, o utilizador deve selecionar o(s)
colaborador(es) e, em seguida, clicar no botão "Validar". Esta ação cria
automaticamente um registo de mobilidade, atribuindo o novo escalão e o
respetivo salário correspondente à progressão ou promoção efetuada. Após
a validação, os colaboradores deixam de aparecer na lista.</p>
<p>1-Pega cada registo validado da tabela
<strong>RH_T_SIM_EVOLUCAO_CARREIRA e regista na tabela
RH_T_VAL_EVOLUCAO_CARREIRA</strong> com excepção dos seguintes
campos</p>
<ul>
<li><p><em>USER_REGISTO_ID</em></p></li>
<li><p><em>USER_REGISTO_NAME</em></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image10.png"
style="width:0.62185in;height:0.29173in" /></p></td>
<td colspan="3"><p>O editar permite tornar um registo legível para
ilegível e vice-versa e se deve justificar o motivo.</p></td>
</tr>
</tbody>
</table>

#### Simular 

##### Requisitos Funcionais 

Neste quadro são descritos os requisitos funcionais para progressão e
promoção, que visam automatizar a análise dos colaboradores com base em
critérios como tempo de permanência no escalão, avaliação de desempenho,
tipo de vínculo e regras específicas da carreira. O objetivo é garantir
que a simulação esteja alinhada ao regulamento interno, permitindo assim
a validação com eficiência e controle por parte do setor de Recursos
Humanos.

<table style="width:99%;">
<colgroup>
<col style="width: 48%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Progressão</strong></p></th>
<th style="text-align: center;"><p><strong>Promoção</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><ul>
<li><p>A progressão ocorre na horizontal, dentro da mesma
carreira.</p></li>
<li><p>O colaborador sobe um escalão.</p></li>
<li><p>Um colaborador pode progredir a cada três anos.</p></li>
</ul>
<p><strong>Tempo de Permanência:</strong></p>
<ul>
<li><p>O tempo de permanência em cada nível e escalão remuneratório é de
3 (três) anos para efeitos de progressão.</p></li>
<li><p>Após três anos como contratado, o colaborador é progredido e
passa a integrar o quadro como efetivo.</p></li>
</ul>
<p><strong>Avaliação de Desempenho:</strong></p>
<ul>
<li><p>A progressão ocorre quando, ao fim de 3 (três) anos, o
colaborador tiver obtido um resultado médio das avaliações de desempenho
realizadas durante esse período igual ou superior a 60% (média igual ou
superior a 3, na escala considerada). Em nenhuma das 3 avaliações o
resultado pode ser inferior a 50% (avaliação inferior a 2,5 na escala
considerada).</p></li>
</ul>
<p>O sistema deverá calcular a média aritmética das avaliações de
desempenho dos três últimos anos consecutivos. Caso o colaborador não
atinja a média exigida, deverá aguardar mais um ano e será reavaliado
até alcançar a média necessária para se tornar elegível. O cálculo será
sempre atualizado com base nas três avaliações anuais mais recentes.</p>
<ul>
<li><p><strong>Colaboradores em Comissão de Serviço:</strong></p></li>
</ul>
<p>A progressão para os colaboradores que desempenham cargos em regime
de comissão de serviço (cargos de diretores, coordenadores, secretariado
do CA) ocorre ao fim de 4 (quatro) anos.</p>
<ul>
<li><p><strong>Comissão de Serviço:</strong></p>
<ul>
<li><p>em 4 anos no cargo de chefia.</p></li>
<li><p>2 em 2 anos na categoria base (carreira original).</p></li>
</ul></li>
</ul></td>
<td><ul>
<li><p>A promoção ocorre na horizontal, dentro da mesma
carreira.</p></li>
<li><p>O colaborador sobe um escalão em menos tempo, ou seja, facilita a
ascensão do colaborador para uma nova escalão de forma mais
rápida.</p></li>
<li><p>O colaborador não pode promover em dois anos
consecutivos.</p></li>
</ul>
<p><u><strong>Tempo de Permanência</strong></u></p>
<ul>
<li><p><strong>Diretores:</strong> O colaborador que ocupa o cargo de
diretor não é elegível para promoção, tanto no cargo de direção quanto
na sua categoria de origem (BASE).</p></li>
<li><p><strong>Outros Cargos:</strong></p>
<ul>
<li><p>Para ser promovido, o colaborador deve ter, no mínimo, três anos
como efetivo. Ou seja, um contratado torna-se efetivo após três anos e,
nessa fase, poderá progredir. Para alcançar a promoção, deverá então
aguardar mais três anos, ser progredido e, no ano seguinte, poderá
concorrer à promoção. <em>(<strong>Portanto, após seis anos de ingresso
no INPS, o colaborador poderá candidatar-se à promoção, desde que tenha
sido progredido e cumpra os demais requisitos</strong></em>.)</p></li>
<li><p>Estando como efetivo e tendo sido progredido, o sistema deverá
calcular a média aritmética das avaliações de desempenho dos 2 últimos
anos consecutivos. Caso o colaborador não atinja a média exigida para
promoção, ficará elegível para progressão no ano seguinte, uma vez que a
promoção permite a subida de escalões de forma mais rápida, exigindo,
por isso, um desempenho superior</p></li>
</ul></li>
</ul>
<p><strong>Criterios de Promoção</strong></p>
<blockquote>
<p>a) Estar há mais de 3 anos no quadro efetivo de pessoal</p>
<p>b) O resultado médio das Avaliações de Desempenho referente aos
últimos 2 anos deve ser igual ou superior a 90% (média igual ou superior
a 4.5, na escala considerada.</p>
<p>c)Assiduidade nos anos em referência, não poderá ter mais de 6 faltas
em cada ano de referência, excluindo faltas por nojo, prova/exame dos
colaboradores que gozam do estatuto de estudante, cumprimento obrigações
legais/imposição de autoridade, exercício sindical, casamento. (mesmo
que as faltas sejam justificadas, não contam para promoção)</p>
</blockquote>
<ol start="4" type="a">
<li><p>Nenhum registo de procedimento disciplinar com condenação no
correlativo período em que se assenta a avaliação.</p></li>
</ol>
<p><strong><u>Redução do Período de Permanência</u></strong></p>
<ul>
<li><p>Cumpridos todos os critérios discriminados nas alíneas do número
anterior, o período de permanência no escalão diminui de 3 (três) para 2
(dois) anos.</p></li>
</ul>
<p><strong>Beneficiário de Promoção Concedida</strong></p>
<ul>
<li><p>O colaborador que beneficiar de uma promoção concedida à luz do
presente artigo, estará elegível para outra após decorrido o período de
3 (três) anos, contados a partir da data da última promoção.</p></li>
</ul>
<p><strong>Condições para Alteração da Posição:</strong> A promoção que
envolva a alteração da posição para mais do que um nível ou escalão
remuneratório, deverão ser verificadas as seguintes condições:</p>
<ul>
<li><p>Existência de posto de trabalho vago</p></li>
<li><p>Avaliação das competências comportamentais e técnicas alinhadas
ou superiores ao requerido para o novo nível/escalão;</p></li>
<li><p>Validação da CE</p></li>
</ul>
<p><strong>Colaboradores em Regime de Comissão de Serviço</strong></p>
<ul>
<li><p>Os Colaboradores em regime de Comissão de Serviço, aplica-se o
mecanismo de promoção de forma automática, salvaguardando os normativos
estabelecidos nas alíneas c) e d) do presente artigo.</p></li>
</ul></td>
</tr>
<tr>
<td colspan="2" style="text-align: center;"><p><strong>EX: Simulação de
uma progressão</strong></p>
<table style="width:94%;">
<colgroup>
<col style="width: 8%" />
<col style="width: 23%" />
<col style="width: 20%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Ano</strong></th>
<th style="text-align: center;"><strong>Situação prevista</strong></th>
<th style="text-align: center;"><strong>Anos de avaliação
usados</strong></th>
<th style="text-align: center;"><strong>Justificação</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: right;">2000</td>
<td style="text-align: left;">Entrada como contratado</td>
<td style="text-align: left;">-</td>
<td style="text-align: left;">Início no INPS</td>
</tr>
<tr>
<td style="text-align: right;">2003</td>
<td style="text-align: left;">Efetivo + 1ª Progressão</td>
<td style="text-align: left;">2000–2002</td>
<td style="text-align: left;">3 anos como contratado. Média ≥ 3</td>
</tr>
<tr>
<td style="text-align: right;">2006</td>
<td style="text-align: left;">2ª Progressão</td>
<td style="text-align: left;">2003–2005</td>
<td style="text-align: left;">Média 2003–2005 ≥ 3</td>
</tr>
<tr>
<td style="text-align: right;">2007</td>
<td style="text-align: left;">Início do ciclo para promoção</td>
<td style="text-align: left;">-</td>
<td style="text-align: left;">Promoção só possível 1 ano após
progressão</td>
</tr>
<tr>
<td style="text-align: right;">2009</td>
<td style="text-align: left;">Não elegível à promoção</td>
<td style="text-align: left;">2007–2008</td>
<td style="text-align: left;">Média &lt; 4.5. Não promove e outras
regras</td>
</tr>
<tr>
<td style="text-align: right;">2010</td>
<td style="text-align: left;">3.ª Progressão</td>
<td style="text-align: left;">2007–2009</td>
<td style="text-align: left;">3 avaliações válidas. Média ≥ 3</td>
</tr>
<tr>
<td style="text-align: right;">2011</td>
<td style="text-align: left;">Início do ciclo para promoção</td>
<td style="text-align: left;">-</td>
<td style="text-align: left;">Início de novo ciclo de promoção após
progressão</td>
</tr>
<tr>
<td style="text-align: right;">2013</td>
<td style="text-align: left;">Promoção</td>
<td style="text-align: left;">2011–2012</td>
<td style="text-align: left;">Média ≥ 4.5 + sem faltas/processos</td>
</tr>
<tr>
<td style="text-align: right;">2016</td>
<td style="text-align: left;">4.ª Progressão</td>
<td style="text-align: left;">2013–2015</td>
<td style="text-align: left;">3 anos após promoção. Média ≥ 3</td>
</tr>
<tr>
<td style="text-align: right;">2017</td>
<td style="text-align: left;">Início ciclo de promoção</td>
<td style="text-align: left;">-</td>
<td style="text-align: left;">Início de novo ciclo de promoção</td>
</tr>
<tr>
<td style="text-align: right;">2019</td>
<td style="text-align: left;">Promoção</td>
<td style="text-align: left;">2017–2018</td>
<td style="text-align: left;">2 anos completos após progressão. Média ≥
4.5</td>
</tr>
<tr>
<td style="text-align: right;">2022</td>
<td style="text-align: left;">5.ª Progressão</td>
<td style="text-align: left;">2019–2021</td>
<td style="text-align: left;">3 anos após promoção. Média ≥ 3</td>
</tr>
<tr>
<td style="text-align: right;">2023</td>
<td style="text-align: left;">Início novo ciclo promoção</td>
<td style="text-align: left;">-</td>
<td style="text-align: left;">Novo ciclo de promoção após
progressão</td>
</tr>
<tr>
<td style="text-align: right;">2025</td>
<td style="text-align: left;">Promoção (se elegível)</td>
<td style="text-align: left;">2023–2024</td>
<td style="text-align: left;">2 anos completos após progressão. Promoção
possível</td>
</tr>
</tbody>
</table></td>
</tr>
</tbody>
</table>

##### Especificação Tecnica e Funcional 

<table style="width:99%;">
<colgroup>
<col style="width: 99%" />
</colgroup>
<thead>
<tr>
<th><p>REGRAS</p></th>
</tr>
</thead>
<tbody>
<tr>
<td><p>Para a implementação da simulação deve ter em conta o
seguinte</p>
<ul>
<li><p>Para promover ou progredir o colaborador deve ter um vinculo como
efetivo (<strong>RH_T_CONTRATO_VINCULO</strong>.VINCULO_ID)</p></li>
</ul>
<ul>
<li><p>A progressao ou promoção, deve ser em carreira ativas
<strong>(RH_T_CARREIRA.</strong>ESTADO <strong>= A</strong>)</p></li>
<li><p>O colaborador deve ter o minino de avaliação media para
progredir</p></li>
</ul>
<ul>
<li><p>Avaliaçao media defino na configuracao (<strong>DOMAIN</strong>=
<strong>CALCULO_PROG_PROMO, REFERENCIA =</strong> AVAL_MIN_PROMO,
AVAL_MIN_PROG)</p></li>
<li><p>Avaliação atribuido ao colaborador
(<strong>RH_T_AVD</strong>.AVALIACAO_FINAL)</p></li>
</ul>
<ul>
<li><p>Deve ter em conta o numero de carreiras ativa do
colaborador</p></li>
</ul>
<ul>
<li><p>Carreira de origem (<strong>RH_T_CARREIRA.CARGO_ID IS NULL OR
RH_T_CARREIRA.CARGO_ID</strong>)</p></li>
</ul>
<ul>
<li><p>Carreira dirigente (<strong>RH_T_CARREIRA.CARGO_ID IS NOT NULL OR
RH_T_CARREIRA.CARGO_ID , RH_T_PARAM_CARGO.</strong>DIRIGENTE)</p></li>
</ul>
<ul>
<li><p>O colaborador pode progredir em duas carreiras (a de origem e
como digirinte)</p></li>
<li><p>Caso o caloborador tenha uma camara como dirigente, logo ele nao
se pode promover em nenhuma das carreiras</p></li>
</ul>
<ul>
<li><p>O colabordor não deve uma situação laboral na qual não evolui na
carreira no intervalo de data que supostamente deverá evoluir.</p></li>
</ul>
<ul>
<li><p>Ver situacao laboral que nao evolui na carreira
(<em><strong>RH_T_PARAM_SITUACAO</strong>.FLG_CESSA_PROGRESSAO,
<strong>RH_T_SISTUACAO_LABORAL</strong>, RH_T_PARAM_SIT_LABORAL</em>
)</p></li>
</ul>
<ul>
<li><p>O colaborador nao deve promover duas veses
consecutivamente</p></li>
</ul>
<ul>
<li><p>Ver qual a sua ultima evolução na carreira
(<strong>RH_T_CARREIRA.</strong>TIPO_SITUACAO)</p></li>
</ul>
<ul>
<li><p>O Colaborador quando entre pela primeira vez como efetivo, deve
aguardar pelo menos 6 anos , para concorrer a promoção</p></li>
<li><p>Para evuluir deve ter no minino de anos ja seja em categoria de
origem ou como dirigente</p></li>
</ul>
<ul>
<li><p>Progresso: tempo evoluçao (<strong>DOMAIN</strong> =
<strong>CALCULO_PROG_PROMO, REFERENCIA =</strong> TEMP_PROG_DIRIGENTE ou
TEMP_PROG_DIRIGENTE_ORIG ou TEMPO_PROGRESSAO_ORIG )</p></li>
<li><p><strong>Promoçao</strong> (<strong>DOMAIN</strong> =
<strong>CALCULO_PROG_PROMO, REFERENCIA =</strong>
TEMP_PROMOCAO)</p></li>
</ul>
<ul>
<li><p>Para iniciar os registos de novos possives evoluidos na carreira
deve primero zerar a tabela
(<strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong>)</p></li>
<li><p>Uma de de carreira que ja tenha registo na tabela de validacao
que nao seja historico nao pode ter novo registo.</p></li>
</ul>
<ul>
<li><p>É historico quando :
(<strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>FLG_HISTORICO =
SIM)</p></li>
<li><p>Não é historico quando
(<strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>FLG_HISTORICO É
NULO)</p></li>
</ul>
<ul>
<li><p>Caso a linha de uma carreira ja tenha registo na tabela
(<strong>RH_T_EVOLUCAO_CARREIRA</strong>.CARREIRA_ID_DE), não deve
evoluir novamente</p></li>
<li><p>Ter em conta numero de faltas por ano para ser promovido
(RH_T_FALTA), ver em parametrização do numero limite
(<strong>DOMAIN</strong> = <strong>CALCULO_PROG_PROMO, REFERENCIA
=</strong>NUMERO_LIMITE_FALTA)</p></li>
<li><p>Registos disciplinares com condenação
(<strong>RH_T_PROCESSO_DISCIPLINAR</strong>.FLG_CONDENACAO,
DATA_INICIO_CONDENACAO, DATA_FIM_CONDENACAO)</p></li>
</ul></td>
</tr>
<tr>
<td><p><strong>GRAVACAO</strong></p></td>
</tr>
<tr>
<td><p><strong>1 Antes de regito nessa tabela, deve eliminar todos
registos antes na tabela RH_T_SIM_EVOLUCAO_CARREIRA</strong></p>
<ol type="1">
<li><p><strong>Faz um novo registo na tabela
RH_T_SIM_EVOLUCAO_CARREIRA</strong></p></li>
</ol>
<ul>
<li><p>CARREIR_ID_DE = id de carreira a progredir</p></li>
<li><p>ESCALAO_ID_DE = ID DE ESCALAO A PROGREDIR</p></li>
<li><p>ESCALAO_ID_PARA = ID DE ESCALAO QUE O COLABORADOR DEVERA
PROGREDIR</p></li>
<li><p>AVALIACAO_MEDIA = avalição media das avaliacoes</p></li>
<li><p>DATA_REFERENTE = Data de Progressão</p></li>
<li><p>TIPREL_ID = id de RH_T_TIPOS-RELACIONAMENTO</p></li>
<li><p>OBSERVACAO = Descreve as condições na qual levou a progessão do
colaborador</p></li>
<li><p>TIPO = indicar si ºe uma progressão ou (Permite efetuar a
pesquisa por progrssão ou promoção (<strong>DOMAINS =</strong>
TIPO_MOV_LABORAL E REFERENCIA = CARREIRA )</p></li>
<li><p>DATA_REGISTO = sysdate</p></li>
<li><p>USER_REGISTO_ID</p></li>
</ul></td>
</tr>
</tbody>
</table>

#### Editar 

<img src="media/image11.png" style="width:6.85607in;height:3.20209in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 44%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><p><strong>Fonte de
Dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Elegível</p></td>
<td><p>RADIOLIST</p></td>
<td></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA.</strong>FLG_ELEGIVEL</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Validar</p></td>
<td><p>RADIOLIST</p></td>
<td><p>Caso for selecionado sim os registados serão retirador de
simulação e serão registados em
<strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong></p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong></p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Observação</p></td>
<td><p>TEXTAREA</p></td>
<td></td>
<td><p><strong>RH_T_SIM_EVOLUCAO_CARREIRA.</strong>
OBS_MOTIVO_ALTERACAO</p>
<p><strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>
OBS_MOTIVO_ALTERACAO</p></td>
</tr>
</tbody>
</table>

### LISTA VALIDAÇÃO

<img src="media/image12.png" style="width:9.68889in;height:4.89722in" />

### Especificação Tecnica e Funcional 

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 5%" />
<col style="width: 45%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th><p><strong>Descrição</strong></p></th>
<th><p><strong>Fonte de Dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Progressão / Promoção</p></td>
<td><p>SELECT</p></td>
<td><p>Permite efetuar a pesquisa por progrssão ou promoção
(<strong>DOMAINS =</strong> TIPO_MOV_LABORAL E REFERENCIA = CARREIRA
)</p>
<ul>
<li><p>Progressão</p></li>
<li><p>Promocão</p></li>
</ul></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.TIPO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Colaborador</p></td>
<td><p>TEXT</p></td>
<td><p>Permite efecturar pesquisa por nome do colaborador</p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.TIPREL_ID</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Carreira</p></td>
<td><p>SELECT</p></td>
<td><p>Permite efecturar pesquisa por Categorio do colaborador</p>
<p><strong>RH_T_PARAM_CARREIRA</strong>.NOME</p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>SCARREIRA_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data referente de:</p></td>
<td><p>DATE</p></td>
<td><p>Permite efetuar a pesquisa por intervalo de datas de progressão
ou promoção</p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.DATA_REFERENTE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Referente Até:</p></td>
<td><p>DETE</p></td>
<td><p>Permite efetuar a pesquisa por intervalo de datas de progressão
ou promoção</p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.DATA_REFERENTE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><p>Progressão / Promoção</p></td>
<td><p>TEXT</p></td>
<td><p>Indica se o item na lista corresponde a uma progressão ou a uma
promoção</p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.TIPO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Referente</p></td>
<td><p>TEXT</p></td>
<td><p>Data referente à progressão ou promoção</p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.DATA_REFERENTE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Carreira</p></td>
<td><p>TEXT</p></td>
<td><p>Categoria do colaborador progredido ou promovido.</p>
<p><strong>RH_T_PARAM_CARREIRA</strong>.NOME</p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.CARREIRA_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Cargo</p></td>
<td></td>
<td><p><strong>RH_T_PARAM_CARGO</strong>.NOME</p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.CARREIRA_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Escalão De:</p></td>
<td><p>TEXT</p></td>
<td><p>Escalão antes da progressão ou promoção</p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.ESCALAO_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Escalão Para:</p></td>
<td><p>TEXT</p></td>
<td><p>Escalão para o qual foi progredido ou promovido</p>
<p>RH_T_PARAM_ESCALAO.<em>NIVEL_REFERENCIA</em>!!
RH_T_PARAM_ESCALAO.<em>ESCALAO</em></p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.ESCALAO_ID_PARA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Avaliação Média</p></td>
<td><p>TEXT</p></td>
<td></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>AVALIACAO_MEDIA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Obervação</p></td>
<td><p>TEXT</p></td>
<td><p>Descreve as razões pelas quais o colaborador progrediu ou foi
promovido, detalhando todas as regras aplicadas</p>
<p>RH_T_PARAM_ESCALAO.<em>NIVEL_REFERENCIA</em>!!
RH_T_PARAM_ESCALAO.<em>ESCALAO</em></p></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.OBS</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Histórica</p></td>
<td><p>TEXT</p></td>
<td></td>
<td><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.FLG_HISTORICO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Acções</strong></p></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image7.png"
style="width:0.95347in;height:0.22014in" /></p></td>
<td colspan="3"><p>Exportar para pdf ou Excel</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image13.png"
style="width:1.34583in;height:0.23681in" /></p></td>
<td colspan="3"><p>Permite gerar oderm de serviço de todos os dados
selecionados</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image14.png"
style="width:1.27083in;height:0.24167in" /></p></td>
<td colspan="3"><p>Permite confirmar colaborador validado</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image15.png"
style="width:1.25417in;height:0.2875in" /></p></td>
<td colspan="3"><p>Permite enviar para historico, os dados validados mas
que não foram confirmados para progressão</p>
<p><strong>Accão</strong>: Em cada linha selecionado faz update em:</p>
<ul>
<li><p><strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>FLG_HISTORICO
<strong>= ‘SIM’</strong></p></li>
</ul></td>
</tr>
</tbody>
</table>

#### CONFIRMAR

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 82%" />
</colgroup>
<thead>
<tr>
<th rowspan="2"><p><img src="media/image16.png"
style="width:1.39931in;height:0.22847in" /></p></th>
<th><p>Ao confirmat progressao , o colaborador fica definitivamente
progredido, logo pega os dados de progressao provissorio com estado
=’ordem Servico’ e regista em outra tabela</p></th>
</tr>
<tr>
<th><p><strong>A linhas marcada serão registados nas seguintes
tabelas</strong></p>
<p><strong>1 RH_T_CARREIRA</strong></p>
<p>1.1 pega todos dados de carrera progredido (pega id de carreira em
<strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>CARREIR_ID_DE), e faz um
novo registo, com os campos igual com excepcao dos seguintes campos</p>
<ul>
<li><p><strong>RH_T_PR_DEFINITIVA</strong></p>
<ul>
<li><p>ESCALAO_ID =
<strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>ESCALAO_ID_PARA</p></li>
<li><p>SALARIO = Pega o salario do escalao ,
<strong>RH_T_PARAM_ESCALAO</strong>.VALOR</p></li>
<li><p>ESTADO = ‘PROGRESSAO’</p></li>
<li><p>TIPO_SITUACAO =
<strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>TIPO</p></li>
<li><p>DATA_REGISTO = SYSDATE</p></li>
<li><p>USER_REGISTO_ID</p></li>
<li><p>USER_REGISTO_NAME</p></li>
<li><p>DATA_INICIO =
<strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.DATA_REFERENTE</p></li>
</ul></li>
</ul>
<p>1.2 Inativa a carreira progredida (pega id aqui pega id de carreira
em <strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>CARREIR_ID_DE)</p>
<ul>
<li><p>ESTADO= I</p></li>
</ul>
<p><strong>2 RH_T_EVOLUCAO_CARREIRA</strong></p>
<p>Cada linha selecionada , deve ser retirada na tabela
<strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong> e registada na tabela
RH_T_<strong>EVOLUCAO_CARREIRA</strong> Com excepção de:</p>
<ul>
<li><p>CARREIRA_ID_PARA</p></li>
<li><p>USER_REGISTO_ID</p></li>
<li><p>USER_REGISTO_NAME</p></li>
</ul>
<p><strong>3.RH_T_TIPOS_RELACIONAMENTO</strong></p>
<p>3.1Faz um novo registo nessa tabela como nova
<strong>RH_T_TIPOS_RELACIONAMENTO</strong>.CARREIRA_ID, pega todos os
dados do vinculo onde escalao é igual a escalao progregrido
RH_T_EVOLUCAO_CARREIRA.ESCALAO_ID_DE (pega o ultimo registo desse
escalao), com excepcao dos seguintes campos:</p>
<ul>
<li><p>TIPO_SITUACAO =
<strong>RH_T_VAL_EVOLUCAO_CARREIRA.</strong>TIPO</p></li>
<li><p>REFERENTE</p></li>
<li><p>CARREIRA_ID = Id de nova carreira</p></li>
<li><p>DATA_REGISTO = SYSDATE</p></li>
<li><p>DATA_INICIO =
<strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.DATA_REFERENTE</p></li>
<li><p>USER_REGISTO_ID</p></li>
<li><p>USER_REGISTO_NAME</p></li>
<li><p>SALARIO = Novo salario do escalao</p></li>
</ul>
<p>3.2actualiza o registo progregredido</p>
<ul>
<li><p>USER_ALTERACAO_NAME</p></li>
<li><p>DATA_ALTERACAO</p></li>
<li><p>EST_ACT_ADM = 0</p></li>
<li><p>DATA_FIM =
<strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.DATA_REFERENTE -1</p></li>
</ul>
<ol start="2" type="1">
<li><p><strong>RH_T_DEF_REMUNERACOES</strong></p></li>
</ol>
<p><strong>4.1</strong>Cria um novo registo nessa tabela. Pega os
registos do id de tipo_delacionamento inativa <strong>(pega o id
de</strong>
<strong>RH_T_TIPREL_REM_PAG.</strong>REM_ID<strong>,</strong> na tabela
de associacao <strong>e</strong> onde <strong>o
RH_T_TIPREL_REM_PAG.</strong>TIPREL_ID ,estado = ‘A’ e que seja id
referente ao salario<strong>),</strong> com excepção dosseguintes
campos</p>
<ul>
<li><p>DATA_REGISTO = SYSDATE</p></li>
<li><p>USER_REGISTO_ID =</p></li>
<li><p>USER_REGISTO_NAME</p></li>
<li><p>DATA_INICIO =
<strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.DATA_REFERENTE</p></li>
<li><p>OBS</p></li>
<li><p>DATA_ULTIMO_PROC</p></li>
<li><p>VALOR = Novo salario do escalao</p></li>
</ul>
<p>4.2Nota caso o colaborador progrediu no meio de mes , logo deve fazer
um registo com dferenca salarial</p>
<p>4.3. inativa o registo anterios</p>
<ul>
<li><p>DATA_FIM =
<strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong>.DATA_REFERENTE -1</p></li>
</ul>
<ol start="3" type="1">
<li><p><strong>RH_T_TIPREL_REM_PAG</strong></p>
<ol type="1">
<li><p>Cria uma nova associação do (pega os REM_ID do salario anterios
com exceção do registo do salario e faz nova associaçao com o novo
tiprel_id)</p></li>
</ol></li>
</ol></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

## Lista Historico de Progressão / Promoção

<img src="media/image17.png" style="width:9.40347in;height:4.23264in" />

### Especificação Tecnica e Funcional 

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 5%" />
<col style="width: 49%" />
<col style="width: 29%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Filtro</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th><p><strong>Descrição</strong></p></th>
<th><p><strong>Fonte de Dados</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Progressão / Promoção</p></td>
<td><p>SELECT</p></td>
<td><p>Permite efetuar a pesquisa por progrssão ou promoção (Permite
efetuar a pesquisa por progrssão ou promoção (<strong>DOMAINS =</strong>
TIPO_MOV_LABORAL E REFERENCIA = CARREIRA )</p>
<ul>
<li><p>Progressão</p></li>
<li><p>Promocão</p></li>
</ul></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.TIPO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Colaborador</p></td>
<td><p>TEXT</p></td>
<td><p>Permite efecturar pesquisa por nome do colaborador</p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.TIPREL_ID</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Carreira</p></td>
<td><p>SELECT</p></td>
<td><p>Permite efecturar pesquisa por Categorio do colaborador</p>
<p><strong>RH_T_PARAM_CARREIRA</strong>.NOME</p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.CARREIRA_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data referente de:</p></td>
<td><p>DATE</p></td>
<td><p>Permite efetuar a pesquisa por intervalo de datas de progressão
ou promoção</p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.DATA_REFERENTE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Referente Até:</p></td>
<td><p>DETE</p></td>
<td><p>Permite efetuar a pesquisa por intervalo de datas de progressão
ou promoção</p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.DATA_REFERENTE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Lista</strong></p></td>
<td style="text-align: center;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><p>Progressão / Promoção</p></td>
<td><p>TEXT</p></td>
<td><p>Indica se o item na lista corresponde a uma progressão ou a uma
promoção</p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.TIPO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Data Referente</p></td>
<td><p>TEXT</p></td>
<td><p>Data referente à progressão ou promoção</p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.DATA_REFERENTE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Carreira</p></td>
<td><p>TEXT</p></td>
<td><p>Categoria do colaborador progredido ou promovido.</p>
<p><strong>RH_T_PARAM_CARREIRA</strong>.NOME</p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.CARREIRA_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Cargo</p></td>
<td></td>
<td><p><strong>RH_T_PARAM_CARGO</strong>.NOME</p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.CARREIRA_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Escalão De:</p></td>
<td><p>TEXT</p></td>
<td><p>Escalão antes da progressão ou promoção</p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA..ESCALAO_ID_DE</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Escalão Para:</p></td>
<td><p>TEXT</p></td>
<td><p>Escalão para o qual foi progredido ou promovido</p>
<p>RH_T_PARAM_ESCALAO.<em>NIVEL_REFERENCIA</em>!!
RH_T_PARAM_ESCALAO.<em>ESCALAO</em></p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.ESCALAO_ID_PARA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Avaliação Média</p></td>
<td></td>
<td></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.AVALIACAO_MEDIA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Obervação</p></td>
<td><p>TEXT</p></td>
<td><p>Descreve as razões pelas quais o colaborador progrediu ou foi
promovido, detalhando todas as regras aplicadas</p>
<p>RH_T_PARAM_ESCALAO.<em>NIVEL_REFERENCIA</em>!!
RH_T_PARAM_ESCALAO.<em>ESCALAO</em></p></td>
<td><p>RH_T_EVOLUCAO_CARREIRA.ESCALAO_ID_PARA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p><strong>Acções</strong></p></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image7.png"
style="width:0.95347in;height:0.22014in" /></p></td>
<td></td>
<td><p>Exportar para pdf ou Excel</p></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><p><img src="media/image18.png"
style="width:1.28681in;height:0.29097in" /></p></td>
<td></td>
<td><p>Seleciona todas as linhas na qual pretende anexar a ordem de
serviço e faz um registo na documento , deve permitir selecionar somente
os cujo RH_T_EVOLUCAO_CARREIRA.<em>ORDEM_SERVICO_ID= NULL</em></p></td>
<td></td>
</tr>
</tbody>
</table>

### Anexar ordem de Serviço

<img src="media/image19.png" style="width:9.68264in;height:1.80903in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 5%" />
<col style="width: 29%" />
<col style="width: 16%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><p><strong>Lista</strong></p></th>
<th style="text-align: center;"><p><strong>Tipo</strong></p></th>
<th colspan="2"
style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><p>Numero de ordem</p></td>
<td><p>TEXT</p></td>
<td colspan="2"></td>
<td><p>RH_T_ORDEM_SERVICO.NU_ORDEM</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Descrição</p></td>
<td><p>TEXT</p></td>
<td colspan="2"></td>
<td><p>RH_T_ORDEM_SERVICO. DESCRICAO</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Referencia</p></td>
<td></td>
<td colspan="2"></td>
<td><p>RH_T_ORDEM_SERVICO. REFERENCIA</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>Anexar Ordem Serviço</p></td>
<td><p>TEXT</p></td>
<td colspan="2"><p>RH_T_TIPOS_DOCUMENTO ONDE REFERENCIA =
EVALUCAO_CARREIRA</p></td>
<td><p>RH_T_DOCUMENTO.TP_DOCUMENTO_ID</p>
<p>RH_T_DOCUMENTO.DOC_ID</p>
<p>RH_T_DOCUMENTO.FUN_ID</p>
<p>RH_T_DOCUMENTO.REFERENCIA_NAME = ‘RH_T_EVOLUCAO_CARREIRA’</p>
<p>RH_T_DOCUMENTO.REFERENCIA_ID= RH_T_EVOLUCAO_CARREIRA.ID</p></td>
</tr>
<tr>
<td style="text-align: center;"><p>OUTRAS GRAVACOES</p></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td colspan="3" style="text-align: center;"><p>1.Registo em
RH_T_ORDEM_SERVICO</p>
<ul>
<li><p>FUN_ID</p></li>
</ul>
<ul>
<li><p>DATA_REGISTO</p></li>
<li><p>USER_REGISTO_ID</p></li>
<li><p>USER_REGISTO_NAME</p></li>
</ul></td>
<td colspan="2"><p>Update em
RH_T_EVOLUCAO_CARREIRA.ORDEM_SERVICO_ID</p></td>
</tr>
</tbody>
</table>

## Parametrizar (<span class="mark">FEITO</span>)

O sistema deverá permitir a **parametrização das regras** aplicáveis à
simulação de progressão e promoção, de forma flexível e ajustável
conforme as políticas internas da instituição. Entre os parâmetros
configuráveis estão:

- **Situações que interrompem ou congelam** a contagem para
  progressão/promoção (ex: licenças, ausências prolongadas, requisição,
  etc.);

- **Tipos de vínculo elegíveis** para progressão (ex: efetivo,
  contratado) e exceções (ex: requisição ou comissão de serviço);

- **Tempo mínimo de permanência** no escalão atual, diferenciado por
  tipo de cargo (ex: técnico, coordenador, diretor);

- **Nota mínima de avaliação de desempenho** exigida para progressão ou
  promoção, podendo ser ajustada conforme categoria profissional ou ano
  de referência.

## MODELO DE DADOS

> <img src="media/image20.png" style="width:9.69028in;height:4.54792in"
> alt="PROGRESSAO_PROMOSSAO.drawio" />

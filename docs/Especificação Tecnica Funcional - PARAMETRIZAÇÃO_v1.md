<figure>
<img src="media/image1.jpeg" style="width:14.65694in;height:9.77083in"
alt="C:\Users\joelm\Desktop\Imagens\sergey-zolkin-_UeY8aTI6d0-unsplash (2).jpg" />
<figcaption><p>SIPS-RH</p></figcaption>
</figure>

**PARAMETRIZAÇÃO**

# Enquadramento 

# Âmbito 

<table style="width:100%;">
<colgroup>
<col style="width: 26%" />
<col style="width: 73%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Funcionalidade</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>GESTAO LABORAL</td>
<td><ul>
<li><p>Contrato</p></li>
<li><p> <strong>Vínculo</strong> <em>(obrig.)</em> — ID_VINCULO
<em>(dropdown; só vínculos com FLG_GERA_CONTRATO=Sim)</em></p></li>
<li><p> <strong>Código</strong> <em>(obrig.)</em> — COD_TIPO_CONTRATO
(ex.: INDET, TERMO_CERTO, TERMO_INC, COMISSAO_36M, AVENCA_12M,
ESTAGIO_TC)</p></li>
<li><p>Único por sistema.</p></li>
<li><p> <strong>Descrição</strong> <em>(obrig.)</em></p></li>
<li><p> <strong>Duração padrão (meses)</strong> <em>(num,
opcional)</em> — DURACAO_PADRAO_MES</p></li>
<li><p>Regra: <strong>obrigatório</strong> se o contrato for a termo;
<strong>nulo</strong> se indeterminado.</p></li>
<li><p> <strong>Renovável?</strong> <em>(switch)</em> —
RENOVAVEL</p></li>
<li><p> <strong>Conta tempo de serviço?</strong> <em>(switch)</em> —
FLG_CONTA_TEMPO (ex.: <em>Não</em> para Avença/Estágio)</p></li>
<li><p> <strong>Remunerado?</strong> <em>(switch)</em> — FLG_REMUNERADO
(ex.: <em>Não</em> para estágio curricular)</p></li>
<li><p> <strong>Exige validação formal?</strong> <em>(switch)</em> —
EXIGE_VALIDACAO (default: Sim)</p></li>
<li><p> <strong>Exige Ordem de Serviço?</strong> <em>(switch)</em> —
EXIGE_ORDEM_SERV (default: Sim)</p></li>
<li><p> <strong>Ativo?</strong> <em>(switch)</em> — ATIVO</p></li>
<li></li>
<li><p>Vinculo Laboral</p></li>
</ul>
<blockquote>
<p> <strong>Enquadramento</strong> <em>(obrig.)</em> — ID_SIT_JUR
<em>(dropdown, carrega da aba 1)</em></p>
<p> <strong>Código</strong> <em>(obrig.)</em> — COD_VINCULO (ex.:
EFETIVO, CONTRATADO, COMISSIONADO, REQUISITADO, AVENCADO, ESTAGIARIO,
VOLUNTARIO)</p>
</blockquote>
<ul>
<li><p>Único por sistema.</p></li>
</ul>
<blockquote>
<p> <strong>Descrição</strong> <em>(obrig.)</em> — DESCRICAO</p>
<p> <strong>Gera contrato?</strong> <em>(switch)</em> —
FLG_GERA_CONTRATO (ex.: <strong>Não</strong> para REQUISITADO)</p>
<p> <strong>Remunerado?</strong> <em>(switch)</em> — FLG_REMUNERADO</p>
<p> <strong>Conta tempo de serviço?</strong> <em>(switch)</em> —
FLG_CONTA_TEMPO</p>
<p> <strong>Ordem (listagem)</strong> <em>(num, opcional)</em> —
ORDEM</p>
<p> <strong>Ativo?</strong> <em>(switch)</em> — ATIVO</p>
</blockquote>
<ul>
<li><p>Situacao Laboral</p></li>
</ul>
<ul>
<li><p><strong>Código</strong> <em>(obrig.)</em> — COD_SIT_LAB (ex.:
ATIVO, LIC_SV, COMISSAO, REQUISICAO, SUSP_DISC, REFORMADO,
CESSACAO)</p></li>
<li><p><strong>Descrição</strong> <em>(obrig.)</em></p></li>
<li><p><strong>Tipo de Situação</strong> <em>(obrig.)</em> —
TIPO_SITUACAO <em>(dropdown)</em></p>
<ul>
<li><p>Valores: NORMAL, SUSPENSIVA, ESPECIAL, TRANSITORIA,
CESSACAO.</p></li>
</ul></li>
<li><p><strong>Vínculo padrão</strong> <em>(opcional)</em> —
ID_VINCULO_PADRAO <em>(ex.: COMISSIONADO para “Em
comissão”)</em></p></li>
<li><p><strong>Remunerado?</strong> <em>(switch)</em> —
FLG_REMUNERADO</p></li>
<li><p><strong>Conta tempo de serviço?</strong> <em>(switch)</em> —
FLG_CONTA_TEMPO</p></li>
<li><p><strong>Afeta carreira?</strong> <em>(switch)</em> —
FLG_AFETA_CARREIRA</p>
<ul>
<li><p>Ex.: <em>Sim</em> para REFORMADO/CESSACAO; <em>Parcial</em>
modelado por flags abaixo.</p></li>
</ul></li>
<li><p><strong>Suspende progressão?</strong> <em>(switch)</em> —
FLG_SUSPENDE_PROG</p>
<ul>
<li><p>Ex.: <em>Sim</em> para LIC_SV, COMISSAO (carreira técnica),
SUSP_DISC.</p></li>
</ul></li>
<li><p><strong>Cessa vínculo?</strong> <em>(switch)</em> —
FLG_CESSA_VINCULO</p>
<ul>
<li><p>Ex.: <em>Sim</em> para REFORMADO, CESSACAO, FALECIMENTO.</p></li>
</ul></li>
<li><p><strong>Ordem (listagem)</strong> <em>(num, opcional)</em> —
ORDEM</p></li>
<li><p><strong>Ativo?</strong> <em>(switch)</em> — ATIVO</p></li>
</ul>
<blockquote>
<p><strong>(Opcional) Sub-seção: Regras de Transição</strong></p>
</blockquote>
<ul>
<li><p><strong>De</strong> <em>(dropdown Situação)</em> →
<strong>Para</strong> <em>(dropdown Situação)</em></p></li>
<li><p><strong>Exige Ordem de Serviço?</strong>
<em>(switch)</em></p></li>
<li><p><strong>Exige Validação?</strong> <em>(switch)</em></p></li>
</ul>
<blockquote>
<p>Isto alimenta uma tabela de transições permitidas (workflow
simples).</p>
</blockquote></td>
</tr>
<tr>
<td>PCCS</td>
<td><ul>
<li><p>Carreira</p></li>
<li><p>Cargo</p></li>
<li><p>Escalao</p></li>
</ul></td>
</tr>
<tr>
<td>LOCAL DE TRABALHO</td>
<td>(pais, ilha, ups)</td>
</tr>
<tr>
<td>SECCAO</td>
<td><ul>
<li><p>Sessao</p></li>
</ul></td>
</tr>
<tr>
<td>TIPO DOCUMENTO</td>
<td></td>
</tr>
<tr>
<td>TEXTO DE NOTIFICAÇÃO</td>
<td></td>
</tr>
</tbody>
</table>

# Especificação 

## Tipo Situação / Ocorrência

### Registo 

<img src="media/image4.png" style="width:9.69236in;height:4.96111in" />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 6%" />
<col style="width: 35%" />
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
<td style="text-align: left;">Codigo</td>
<td>TEXT</td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.CODIGO</em></td>
</tr>
<tr>
<td style="text-align: left;">Descrição</td>
<td>TEXT</td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">Classificação/ Área</td>
<td></td>
<td>DOMAIN = CLASSIFICACAO_SITUACAO</td>
<td><em>RH_T_PARAM_SITUACAO.CLASSIFICACAO_AREA</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado do contrato</td>
<td>SELECT</td>
<td><strong>DOMAIN = ESTADO_CONTRATO</strong></td>
<td><em>RH_T_PARAM_SITUACAO.FLG_ESTADO_CONTRATO</em></td>
</tr>
<tr>
<td style="text-align: left;">Afeta Situação Laboral?</td>
<td>radioList</td>
<td>DOMAIN = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_SITUACAO.FLG_SITUACAO_LABORAL</em></td>
</tr>
<tr>
<td style="text-align: left;">Abono e Beneficio?</td>
<td>radioList</td>
<td>DOMAIN = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_SITUACAO.FLG_ABONO_BENEFICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Ausenta do local de Trabalho?</td>
<td>RadioList</td>
<td>DOMAIN = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_SITUACAO.FLG_AUSENCIA</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td>Select</td>
<td>DOMAIN = STATUS</td>
<td><em>RH_T_PARAM_SITUACAO.ESTADO</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;">Detalhe Ausência (<em>Este
separador só aparece caso</em> <strong>Ausenta do local =
‘SIM’</strong>)</td>
</tr>
<tr>
<td style="text-align: left;">Tipo Ausencia</td>
<td>RadioList</td>
<td>DOMAIN = TIPO_AUSENCIA</td>
<td><em>RH_T_PARAM_SITUACAO.FLG_FALTA</em></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Falta / Ausência</td>
<td></td>
<td><p>Este campo aparece caso o Ausenta do Local = ‘SIM’</p>
<p>DOMAIN = JUSTIFICADA_INJUSTIFICADA</p></td>
<td><em>RH_T_PARAM_SITUACAO.TIPO_FALTA</em></td>
</tr>
<tr>
<td style="text-align: left;">Desconto Salário</td>
<td></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>Detalhe Abono e
Beneficio</strong> (<em>Este separador só aparece caso</em>
<strong>Abono e Benefício = ‘SIM’</strong>)</td>
</tr>
<tr>
<td style="text-align: left;">Tempo Contagem dias</td>
<td></td>
<td><ul>
<li><p>Dias úteis</p></li>
<li><p>Dias Corrido</p></li>
</ul>
<p>DOMAIN =TIPO_CONTAGEM_DIAS</p></td>
<td><em>RH_T_PARAM_SITUACAO.TIPO_CONTAGEM_DIAS</em></td>
</tr>
<tr>
<td style="text-align: left;">Nº Días</td>
<td>NUMBER</td>
<td>Número de dias do abono</td>
<td><em>RH_T_PARAM_SITUACAO.NUM_DIAS_ABONOS</em></td>
</tr>
<tr>
<td style="text-align: left;">Nº dias Descontado</td>
<td>NUMBER</td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.NUM_DIAS_ABONOS_DESCONTO</em></td>
</tr>
<tr>
<td style="text-align: left;">Nº dias não descontado</td>
<td>NUMBER</td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.NUM_DIAS_ABONOS_NDESCONTO</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><em><strong>Detalhe Situação
Laboral (</strong>Este separador só aparece caso</em> <strong>Situação
Laboral = ‘SIM’<em>)</em></strong></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Situação Laboral</td>
<td>Select</td>
<td>DOMAIN = SITUACAO_LABORAL</td>
<td><em>RH_T_PARAM_SITUACAO.TIPO_SITUACAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Remuneração</td>
<td>radioList</td>
<td>DOMAIN = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_SITUACAO.FLG_REMUNERACAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Tem Carreira?</td>
<td>radioList</td>
<td>DOMAIN = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_SITUACAO.FLG_AFETA_CARREIRA</em></td>
</tr>
<tr>
<td style="text-align: left;">Regressa a Carreira Origem?</td>
<td>radioList</td>
<td>DOMAIN = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_SITUACAO.REGRESSA_CARREIRA</em></td>
</tr>
<tr>
<td style="text-align: left;">Conta Tempo serviço?</td>
<td>radioList</td>
<td>DOMAIN = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_SITUACAO.FLG_CONTA_TEMP_SERVICO</em></td>
</tr>
<tr>
<td style="text-align: left;">Cessa vínculo?</td>
<td>radioList</td>
<td>DOMAIN = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_SITUACAO.FLG_CESSA_VINCULO</em></td>
</tr>
<tr>
<td style="text-align: left;">Suspende Progressao / Promocao?</td>
<td>radioList</td>
<td>DOMAIN = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_SITUACAO.FLG_CESSA_PROGRESSAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Motivo Situação</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Motivo</td>
<td>TEXT</td>
<td><strong>TABELA</strong>: RH_T_PARAM_VINCULO</td>
<td><em>RH_T_PARAM_SITUACAO.MOTIVO</em></td>
</tr>
<tr>
<td style="text-align: left;">---------------------------------</td>
<td>HIDDEN</td>
<td><strong>--------------------------------------------------------</strong></td>
<td><em>RH_T_PARAM_SITUACAO.PARAM_SIT_ID</em></td>
</tr>
<tr>
<td colspan="4"
style="text-align: left;"><em><strong>REGRAS</strong></em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><ul>
<li><p><em>Qualquer alterção nessa tabela deve estar registado no
Log</em></p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Acções</strong></td>
<td colspan="3"></td>
</tr>
<tr>
<td style="text-align: left;">Gravar</td>
<td colspan="3"><ol type="1">
<li><p><strong>Registar</strong></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <em><strong>RH_T_PARAM_SITUACAO</strong></em>, bem como os
campos adicionais especificados a seguir</p></li>
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
a tabelas <em><strong>RH_T_PARAM_SITUACAO_DET</strong></em></p></li>
</ol></li>
</ul>
<ul>
<li><p><em>ESTADO = ‘A’</em></p></li>
<li><p><em><strong>PARAM_SITUACAO_ID = id de
RH_T_PARAM_SIT_LABORAL</strong></em></p></li>
<li><p><em><strong>DATA_REGISTO= ‘SYSDATE’</strong></em></p></li>
<li><p><em><strong>USER_REGISTO_ID = id de utilizador
Logado</strong></em></p></li>
<li><p><em><strong>USER_REGISTO_NAME =nome de utilizador
Logado</strong></em></p></li>
<li><p><em><strong>USER_ALTERACAO _ID = NULL</strong></em></p></li>
<li><p><em><strong>DATA_ALTERACAO = NULL</strong></em></p></li>
<li><p><em><strong>DATA_ALTERACAO_NAME = NULL</strong></em></p></li>
</ul>
<ol start="2" type="1">
<li><p><strong>Editar ou eliminar</strong></p></li>
</ol>
<ul>
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE’</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
</ul>
<p><em>3.Eliminar</em></p>
<ul>
<li><p><em>ESTADO = ‘I’</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Lista Situação

<img src="media/image5.png" style="width:9.69306in;height:4.15417in"
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
<td>Classificação / Área</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.CLASSIFICACAO_AREA</em></td>
</tr>
<tr>
<td>Afeta Situação Laboral?</td>
<td><em>CHECK</em></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.FLG_SITUACAO_LABORAL</em></td>
</tr>
<tr>
<td>Abono e Beneficio</td>
<td><em>CHECK</em></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.FLG_ABONO_BENEFICIO</em></td>
</tr>
<tr>
<td>Ausência do Local Trabalho</td>
<td><em>CHECK</em></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.FLG_AUSENCIA</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Nome</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.NOME</em></td>
</tr>
<tr>
<td>Classificação / Área</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.CLASSIFICACAO_AREA</em></td>
</tr>
<tr>
<td>Afeta Situação Laboral?</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.FLG_SITUACAO_LABORAL</em></td>
</tr>
<tr>
<td>Abono e Benefício</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.FLG_ABONO_BENEFICIO</em></td>
</tr>
<tr>
<td>Ausência do Local Trabalho</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SITUACAO.FLG_AUSENCIA</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Ações</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Novo</td>
<td colspan="3"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

## Vinculo Laboral

### Registo Vinculo Laboral

<img src="media/image6.png" style="width:9.69306in;height:2.7625in"
alt="Uma imagem com captura de ecrã, texto Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 6%" />
<col style="width: 35%" />
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
<td style="text-align: left;">Codigo</td>
<td></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO.CODIGO</em></td>
</tr>
<tr>
<td style="text-align: left;">Descricao</td>
<td></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">Tem Carreira?</td>
<td></td>
<td>DOMINIO = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_VINCULO.FLG_CARREIRA</em></td>
</tr>
<tr>
<td style="text-align: left;">Tem contrato?</td>
<td></td>
<td>DOMINIO = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_VINCULO.FLG_CONTRATO</em></td>
</tr>
<tr>
<td style="text-align: left;">Conta Tempo Serviço?</td>
<td></td>
<td>DOMINIO = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_VINCULO.FLG_TEMPO_SERVICO</em></td>
</tr>
<tr>
<td style="text-align: left;">Remuneracao?</td>
<td></td>
<td>DOMINIO = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_VINCULO.FLG_SALARIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td></td>
<td><p>Visivel so no modo editar</p>
<p>DOMINIO = STATUS</p></td>
<td><em>RH_T_PARAM_VINCULO.ESTADO</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image7.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol start="3" type="1">
<li><p><strong>Registar</strong></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <em><strong>RH_T_PARAM_VINCULO</strong></em>, bem como os
campos adicionais especificados a seguir</p></li>
</ol></li>
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
</ul></td>
</tr>
</tbody>
</table>

### Lista vinculo Laboral 

<img src="media/image8.png" style="width:9.69306in;height:4.26667in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

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
<td>Nome</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO. NOME</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Codigo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO. CODIGO</em></td>
</tr>
<tr>
<td>Descrição</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO. NOME</em></td>
</tr>
<tr>
<td>contrato</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO. FLG_CONTRATO</em></td>
</tr>
<tr>
<td>Carreira</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO. FLG_CARREIRA</em></td>
</tr>
<tr>
<td>Remuneração</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO. FLG_SALARIO</em></td>
</tr>
<tr>
<td>Conta Tempo Serviço</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO. FLG_TEMPO_SERVICO</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_VINCULO.ESTADO</em></td>
</tr>
<tr>
<td><strong>Exige Ordem de Serviço?</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Invoca o mesmo formulario de registo</td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="3"><p>Somente deve eliminar caso não existe registo em
<strong>RH_T_TIPOS_RELACIONAMENTO.</strong>VINCULO_ID</p>
<p>Ao Eliminar atualiza o estado
<strong><em>RH_T_PARAM_VINCULO</em>.estado = ‘E’</strong></p></td>
</tr>
<tr>
<td><del>Associar Situação Laboral</del></td>
<td colspan="3"><del>Permite associar situação laboral a um
Vínculo</del></td>
</tr>
</tbody>
</table>

#### Associar Vinculo a situação Laboral

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 8%" />
<col style="width: 34%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr>
<th
style="text-align: center;"><strong><del>Formulario</del></strong></th>
<th style="text-align: center;"><strong><del>Tipo</del></strong></th>
<th
style="text-align: center;"><strong><del>Descrição</del></strong></th>
<th
style="text-align: center;"><strong><del>Gravação</del></strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;"><del>Vinculo</del></td>
<td></td>
<td><em><del>RH_T_PARAM_VINCULO.ID</del></em></td>
<td><em><del>RH_T_PARAM_SIT_LABORAL.VINCULO_ID</del></em></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><del>Situação Laboral</del></td>
<td><del>Select</del></td>
<td><p><del>Pela ID e nome na tabela RH_T_PARAM_SITUACAO ,
onde</del></p>
<p><del>FLG_SITUACAO_LABORAL = 1 (sim)</del></p></td>
<td><em><del>RH_T_PARAM_SIT_LABORAL.PARAM_SIT_ID</del></em></td>
</tr>
</tbody>
</table>

###  

## Tipo de Contrato laboral

### Registar tipo contrato

<img src="media/image9.png" style="width:9.69306in;height:3.71389in"
alt="Uma imagem com texto, file, número, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 8%" />
<col style="width: 34%" />
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
<td style="text-align: left;">Codigo</td>
<td>TEXT</td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO.CODIGO</em></td>
</tr>
<tr>
<td style="text-align: left;">Nome</td>
<td>TEXT</td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">Natureza</td>
<td></td>
<td><p>Indicar se o vínculo é de tempo determinado ou indeterminado</p>
<p>DOMINIO = NATUREZA_VINCULO</p></td>
<td><em>RH_T_PARAM_CONTRATO.NATUREZA</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Renovação / Limites</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Renovavel?</td>
<td>RADIOLIST</td>
<td>DOMINIO = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_CONTRATO.FLG_RENOVAVEL</em></td>
</tr>
<tr>
<td style="text-align: left;">Duração por renovação
<strong>(meses)</strong></td>
<td>NUMBER</td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO.DURACAO_RENOVAVEL</em></td>
</tr>
<tr>
<td style="text-align: left;">Máx. nº de renovações</td>
<td>NUMER</td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO.MAX_RENOVACAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Prazo Obrigatório</td>
<td>RADIOLIST</td>
<td>DOMINIO = SIM_NAO_NUMBER</td>
<td><em>RH_T_PARAM_CONTRATO.PRAZO_OBRIGATORIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td>SELECT</td>
<td>O campo deve estar oculto no registo inicial e apenas se tornar
visível quando o formulário estiver em modo de edição. DOMINIO =
STATUS</td>
<td><em>RH_T_PARAM_CONTRATO. ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">------------------------</td>
<td>HIDDEN</td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Associado ao Vinculo</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Tipo Vinculo</td>
<td>Multiselect</td>
<td>Buscar dados na tabela <strong>RH_T_PARAM_VINCULO</strong>.NOME</td>
<td><em>RH_T_PARAM_CONTRATO.PARAM_VINCULO_ID</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image7.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol start="5" type="1">
<li><p><strong>Registar</strong></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <em><strong>RH_T_PARAM_CONTRATO</strong></em>, bem como os
campos adicionais especificados a seguir</p></li>
</ol></li>
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
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Lista Tipo contrato

<img src="media/image10.png" style="width:9.69306in;height:4.38958in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

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
<td>Nome</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO. NOME</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Codigo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO. CODIGO</em></td>
</tr>
<tr>
<td>Nome</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO. NOME</em></td>
</tr>
<tr>
<td>Natureza</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO.NATUREZA</em></td>
</tr>
<tr>
<td>Renovavel</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO. FLG_RENOVAVEL</em></td>
</tr>
<tr>
<td>Associação Vinculo</td>
<td><em>SSELECT</em></td>
<td>Agrupar os tipos de vinculos associados ao Contrato</td>
<td><em>RH_T_PARAM_CONTRATO.PARAM_VINCULO_ID</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CONTRATO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Invoca o mesmo formulario de registo</td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="3"><p>Somente deve eliminar caso não existe registo em
<strong>RH_T_CONTRATO.</strong>TP_CONTRATO_ID</p>
<p>Ao Eliminar atualiza o estado
<strong><em>RH_T_PARAM_CONTRATO</em>.estado = ‘E’</strong></p></td>
</tr>
</tbody>
</table>

*~~RH_T_PARAM_SIT_LABORAL~~ --- RH_T_PARAM_SITUACAO*

*~~RH_T_PARAM_SIT_LABORAL_DET~~ -- RH_T_PARAM_SIT_LABORAL*

## PCCS 

### Registar PCCS 

<img src="media/image11.png" style="width:9.69306in;height:2.07153in" />

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 4%" />
<col style="width: 38%" />
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
<td colspan="4"></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio</td>
<td><em>Date</em></td>
<td></td>
<td><em>RH_T_PARAM_PCCS.</em> <em>DATA_INICIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>Date</em></td>
<td></td>
<td><p><em>RH_T_PARAM_PCCS.</em> <em>DATA_FIM</em></p>
<p><em>RH_T_PARAM_ESCALAO.DATA_FIM</em></p></td>
</tr>
<tr>
<td style="text-align: left;">Descrição</td>
<td><em>text</em></td>
<td></td>
<td><em>RH_T_PARAM_PCCS.DESCRICAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Fazer copia do Anterior?</td>
<td></td>
<td><p>Ao selecionar sim, o sistema deve pegar o registo ativo e criar o
registo (fazer uma copia) nas seguintes tabelas:</p>
<ul>
<li><p><strong>RH_T_PARAM_CARREIRA</strong></p>
<ul>
<li><p>DATA REGISTO = sysdate</p></li>
<li><p>USER_REGISTO = user logado</p></li>
<li><p>USER_REGISTO_NAME</p></li>
<li><p>PCCS_ID = novo id</p></li>
<li><p>ID = Novo ID</p></li>
</ul></li>
<li><p><strong>RH_T_PARAM_CARGO</strong></p>
<ul>
<li><p>ID = novo id</p></li>
<li><p>PARAM_CARR_ID = ID DE NOVO REGISTO</p></li>
<li><p>DATA REGISTO = sysdate</p></li>
<li><p>USER_REGISTO = user logado</p></li>
<li><p>USER_REGISTO_NAME</p></li>
</ul></li>
<li><p><strong>RH_T_PARAM_ESCALAO</strong></p>
<ul>
<li><p>ID = novo id</p></li>
<li><p>PARAM_CARR_ID = ID DE NOVO REGISTO</p></li>
<li><p>DATA REGISTO = sysdate</p></li>
<li><p>USER_REGISTO = user logado</p></li>
<li><p>USER_REGISTO_NAME</p></li>
</ul></li>
</ul></td>
<td><em>RH_T_PARAM_PCCS.FLG_COPIA_ANTERIOR</em></td>
</tr>
<tr>
<td style="text-align: left;">Fechar o Anterior?</td>
<td></td>
<td><p>Ao selcionar Sim, o sistema deve, fazer o seguinte</p>
<ul>
<li><p><strong>RH_T_PARAM_CARREIRA</strong>.ESTADO = “I”</p></li>
<li><p><strong>RH_T_PARAM_CARGO</strong>.ESTADO = “I”</p></li>
<li><p><strong>RH_T_PARAM_ESCALAO</strong>.DATA_FIM = Data Inicio e
<strong>RH_T_PARAM_ESCALAO</strong>.ESTADO = “I”</p></li>
</ul></td>
<td><em>RH_T_PARAM_PCCS.FLG_FECHAR_ANTERIOR</em></td>
</tr>
<tr>
<td colspan="4"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image7.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p><strong>1.Registar</strong></p>
<p>1.1- O sistema deve registar os dados introduzidos no formulário para
a tabelas <em><strong>RH_T_PARAM_PCCS</strong></em>, bem como os campos
adicionais especificados a seguir</p>
<ul>
<li><p>DATA REGISTO = sysdate</p></li>
<li><p>USER_REGISTO = user logado</p></li>
<li><p>USER_REGISTO_NAME</p></li>
</ul></td>
</tr>
<tr>
<td><img src="media/image12.png"
style="width:1.208in;height:0.23996in" /></td>
<td colspan="3"><strong>Este menu apenas fica visível após a gravação e
deverá ser automaticamente preenchido com uma cópia dos dados
anteriores, caso o utilizador selecione a opção de copiar os dados
anteriores (Sim).</strong></td>
</tr>
</tbody>
</table>

### Lista PCCS 

<img src="media/image13.png" style="width:9.69306in;height:2.49653in" />

<table style="width:100%;">
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 12%" />
<col style="width: 20%" />
<col style="width: 3%" />
<col style="width: 16%" />
<col style="width: 20%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>Lista</strong></th>
<th style="text-align: center;"><strong>Tipo</strong></th>
<th colspan="3"
style="text-align: center;"><strong>Descrição</strong></th>
<th colspan="2" style="text-align: center;"><strong>Fonte
dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Data Inicio</td>
<td><em>TEXT</em></td>
<td colspan="3"></td>
<td colspan="2"><em>RH_T_PARAM_CARREIRA.DATA_INICIO</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>TEXT</em></td>
<td colspan="3"></td>
<td colspan="2"><em>RH_T_PARAM_CARREIRA.DATA_FIM</em></td>
</tr>
<tr>
<td>Descricao</td>
<td><em>TEXT</em></td>
<td colspan="3"></td>
<td colspan="2"><em>RH_T_PARAM_CARREIRA.DESCRICAO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="6" style="text-align: center;"></td>
</tr>
<tr>
<td>Novo / NOVO</td>
<td colspan="6">Permite abrir formulário para registar novo PCCS</td>
</tr>
<tr>
<td rowspan="2">Inativar</td>
<td colspan="6">Permite inativar um PCCS</td>
</tr>
<tr>
<td colspan="2"><p><strong>RH_T_PARAM_PCCS</strong></p>
<ul>
<li><p>USER_ALTERACAO_ID</p></li>
<li><p>USER_ALTERACAO_NAME</p></li>
<li><p>DATA_ALTERACAO</p></li>
<li><p>ESTADO = ‘I’</p></li>
</ul></td>
<td><p><strong>RH_T_PARAM_CARREIRA</strong></p>
<ul>
<li><p>USER_ALTERACAO_ID</p></li>
<li><p>USER_ALTERACAO_NAME</p></li>
<li><p>DATA_ALTERACAO</p></li>
<li><p>ESTADO = ‘I’</p></li>
</ul></td>
<td colspan="2"><p><strong>RH_T_PARAM_CARGO</strong></p>
<ul>
<li><p>USER_ALTERACAO_ID</p></li>
<li><p>USER_ALTERACAO_NAME</p></li>
<li><p>DATA_ALTERACAO</p></li>
<li><p>ESTADO = ‘I’</p></li>
</ul></td>
<td><p><strong>RH_T_PARAM_ESCALAO</strong></p>
<ul>
<li><p>USER_ALTERACAO_ID</p></li>
<li><p>USER_ALTERACAO_NAME</p></li>
<li><p>DATA_ALTERACAO</p></li>
<li><p>ESTADO = ‘I’</p></li>
</ul></td>
</tr>
<tr>
<td><img src="media/image14.png"
style="width:1.36119in;height:0.34742in" /></td>
<td colspan="6"><p><mark>Abre o formulário Para Aumento
Salarial</mark></p>
<p><em><mark><strong>Nota:</strong> especifica no documento
Processamento Salarial ( 3.8.1 regista critérios
aumento)</mark></em></p></td>
</tr>
<tr>
<td><img src="media/image15.png"
style="width:0.272in;height:0.255in" /></td>
<td colspan="6"><p><mark>Lista histórico aumento</mark></p>
<p><em><mark><strong>Nota:</strong> especifica no documento
Processamento Salarial (( 3.8.3 lista aumento)</mark></em></p></td>
</tr>
</tbody>
</table>

### Registo Carreira

<img src="media/image16.png" style="width:9.69306in;height:3.77708in"
alt="Uma imagem com texto, software, número, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 4%" />
<col style="width: 38%" />
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
<td colspan="4"></td>
</tr>
<tr>
<td style="text-align: left;">DESCRICAO</td>
<td></td>
<td></td>
<td><em>RH_T_PARAM_CARREIRA.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">-------------------------</td>
<td></td>
<td>Pega os inicias de descrição</td>
<td><em>RH_T_PARAM_CARREIRA.CODIGO</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td></td>
<td>O campo deve estar oculto no registo inicial e apenas se tornar
visível quando o formulário estiver em modo de edição . DOMINIO =
STATUS</td>
<td><em>RH_T_PARAM_CARREIRA.ESTADO</em></td>
</tr>
<tr>
<td colspan="4"><em><strong>Associar Categoria</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">Categoria</td>
<td></td>
<td><p>Na edição deve apresentar comente categorias com estado Ativo</p>
<p>DOMAIN= CATEGORIA_PCCS</p></td>
<td><em>RH_T_PARAM_CARREIRA.CATEGORIA</em></td>
</tr>
<tr>
<td colspan="4"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image7.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p><strong>1.Registar</strong></p>
<p>1.1- O sistema deve registar os dados introduzidos no formulário para
a tabelas <em><strong>RH_T_PARAM_CARREIRA</strong></em>, bem como os
campos adicionais especificados a seguir</p>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em><mark>PCCS_ID = ID DE RH_T_PARAM_PCCS</mark></em></p>
<ol start="2" type="1">
<li><p><em>Registo na Tabela de
<strong>RH_T_PARAM_CATEGORIA</strong></em></p></li>
</ol></li>
</ul>
<ul>
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>PARAM_CARR_ID = ID DE RH_T_CARREIRA_PCCS</em></p></li>
</ul>
<p><strong>2.Editar</strong></p>
<ul>
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE’</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
</ul>
<p>3- Eliminar uma categoria</p>
<ul>
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE’</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>Estado = ‘I’</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

### Lista Carreira

<img src="media/image17.png" style="width:9.69306in;height:4.65625in"
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
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Descrição</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CARREIRA.DESCRICAO</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>SELECT</em></td>
<td>DOMINIO = STATUS</td>
<td><em>RH_T_PARAM_CARREIRA.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Descrição</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CARREIRA.DESCRICAO</em></td>
</tr>
<tr>
<td>Estado</td>
<td></td>
<td></td>
<td><em>RH_T_PARAM_CARREIRA.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Invoca o mesmo formulario de Registo</td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="3"><p>Somente deve eliminar caso não existe registo em
<strong>RH_T_TIPOS_RELACIONAMENTO.</strong>PARAM_CARR_ID</p>
<p>Ao Eliminar atualiza o estado <em>RH_T_CARREIRAS</em><strong>.estado
= ‘E’</strong></p></td>
</tr>
</tbody>
</table>

### Registar Cargo

<img src="media/image18.png" style="width:9.69306in;height:1.91181in"
alt="Uma imagem com texto, file, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

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
<td style="text-align: left;">Descriçao</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CARGO.DESCRICAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Associar Carreira</td>
<td></td>
<td><strong>Tabela</strong>: RH_T_CARREIRA (DESCRICAO,ID)</td>
<td><em>RH_T_PARAM_CARGO.PARAM_CARR_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">DIRIGENTE</td>
<td><em>RADIOLIST</em></td>
<td><strong>DOMINIO</strong> = SIM_NAO</td>
<td><em>RH_T_PARAM_CARGO.DIRIGENTE</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td><em>SELECT</em></td>
<td>O campo deve estar oculto no registo inicial e apenas se tornar
visível quando o formulário estiver em modo de edição DOMINIO =
STATUS</td>
<td><em>RH_T_PARAM_CARGO.ESTADO</em></td>
</tr>
<tr>
<td colspan="4"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image7.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol start="7" type="1">
<li><p><strong>Registar</strong></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <em><strong>RH_T_PARAM_CARGO</strong></em>, bem como os campos
adicionais especificados a seguir</p></li>
</ol></li>
</ol>
<ol type="1">
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
</ol>
<ol start="8" type="1">
<li><p><strong>Editar</strong></p></li>
</ol>
<ol start="8" type="1">
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE’</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
</ol></td>
</tr>
</tbody>
</table>

### Lista Cargo 

<img src="media/image19.png" style="width:9.69306in;height:4.81667in"
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
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Descrição</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CARGO.DESCRICAO</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>SELECT</em></td>
<td>DOMINIO = STATUS</td>
<td><em>RH_T_PARAM_CARGO. ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Descrição</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CARGO. DIRIGENTE</em></td>
</tr>
<tr>
<td>Carreira</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CARGO. CARR_ID</em></td>
</tr>
<tr>
<td>Dirigente</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CARGO.DESCRICAO</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_CARGO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Invoca o mesmo formulario de Registo</td>
</tr>
</tbody>
</table>

### Escalao

<img src="media/image20.png" style="width:9.69306in;height:2.375in"
alt="Uma imagem com texto, captura de ecrã, file, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

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
<td style="text-align: left;">----------------</td>
<td></td>
<td>O codigo é a concatenação de ESCALAO||NIVEL_REFERENCIA</td>
<td><em>RH_T_PARAM_ESCALAO.CODIGO</em></td>
</tr>
<tr>
<td style="text-align: left;">Carreira</td>
<td><em>SELECT</em></td>
<td><strong>TABELA</strong>: RH_T_PARAM_CARREIRA</td>
<td><em>RH_T_PARAM_ESCALAO.CARR_PCCS_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Categoria</td>
<td><em>SELECT</em></td>
<td><strong>TABELA</strong>: RH_T_CATEGORIA</td>
<td><em>RH_T_PARAM_ESCALAO.CATEGORIA_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Nivel / Referencia</td>
<td><em>SELECT</em></td>
<td><strong>DOMAINS</strong> : REFERENCIA_NIVEL_PCCS</td>
<td><em>RH_T_PARAM_ESCALAO.NIVEL_REFERENCIA</em></td>
</tr>
<tr>
<td style="text-align: left;">Escalão</td>
<td><em>SELECT</em></td>
<td><strong>DOMAINS:</strong> REFERENCIA_LETRA</td>
<td><em>RH_T_PARAM_ESCALAO.ESCALAO</em></td>
</tr>
<tr>
<td style="text-align: left;">salário</td>
<td><em>NUMBER</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO.SALARIO</em></td>
</tr>
<tr>
<td style="text-align: left;">Data Inicio</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO.</em>DATA_INICIO</td>
</tr>
<tr>
<td style="text-align: left;">Data Fim</td>
<td><em>DATE</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO.</em>DATA_FIM</td>
</tr>
<tr>
<td colspan="4"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image7.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol start="9" type="1">
<li><p><strong>Registar</strong></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <strong>RH_T_PARAM_ESCALAO</strong>, bem como os campos
adicionais especificados a seguir</p></li>
</ol></li>
</ol>
<ol start="11" type="1">
<li><p><em>ESTADO <strong>= ‘A’</strong></em></p></li>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
</ol>
<ol start="10" type="1">
<li><p><strong>Editar</strong></p></li>
</ol>
<ol start="18" type="1">
<li><p><em>USER_ALTERACAO _ID = id de utilizador Logado</em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>SYSDATE’</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>Caso data_fim não for Nulo , estado = ‘I’</em></p></li>
</ol></td>
</tr>
</tbody>
</table>

### Lista Escalao 

<img src="media/image21.png" style="width:9.69306in;height:4.84792in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 11%" />
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
<td>Escalão</td>
<td><em>Select</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO.ESCALAO</em></td>
</tr>
<tr>
<td>Carreira</td>
<td><em>SELECT</em></td>
<td><strong>TABELA</strong>: RH_T_CATEGORIA</td>
<td><em>RH_T_PARAM_ESCALAO.CARR_PCCS_ID</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>Select</em></td>
<td>DOMINIO = STATUS</td>
<td><em>RH_T_PARAM_ESCALAO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Carreira</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO. CARR_PCCS_ID</em></td>
</tr>
<tr>
<td>Categoria</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO. CATEGORIA_ID</em></td>
</tr>
<tr>
<td>Escalão</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO.ESCALAO</em></td>
</tr>
<tr>
<td>Nivel / Referencia</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO. NIVEL_REFERENCIA</em></td>
</tr>
<tr>
<td>Salário</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO. SALARIO</em></td>
</tr>
<tr>
<td>Data Inicio</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO.DATA_INICIO</em></td>
</tr>
<tr>
<td>Data Fim</td>
<td><em>text</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO.DATA_FIM</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>text</em></td>
<td></td>
<td><em>RH_T_PARAM_ESCALAO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Invoca o mesmo formulario de Registo</td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="3"><p>Este botão so aparece caso não existe nehuma
dependencia em
<strong>RH_T_TIPOS_RELACIONAMENTO.PARAM_</strong>ESCALAO.ID caso
contrario faz update em</p>
<p>RH_T_ESCALAO.ESTADO = ‘E’</p></td>
</tr>
</tbody>
</table>

### 

## Local de Trabalho

### Registar Local

*~~RH_T_PARAM_LOCAL_BALCAO =~~ RH_T_PARAM_LOCAL_TRAB*

<img src="media/image22.png" style="width:9.69306in;height:2.00069in"
alt="Uma imagem com texto, file, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

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
<td colspan="4"></td>
</tr>
<tr>
<td style="text-align: left;">Local / balcão</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB.DESCRICAO</em></td>
</tr>
<tr>
<td style="text-align: left;">Pais</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB.PAIS_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Ilha</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB.ILHA_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">UPS</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB.UPS</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td><em>SELECT</em></td>
<td>O campo deve estar oculto no registo inicial e apenas se tornar
visível quando o formulário estiver em modo de edição DOMINIO =
STATUS</td>
<td><em>RH_T_PARAM_LOCAL_TRAB.ESTADO</em></td>
</tr>
<tr>
<td colspan="4"><strong>Ações</strong></td>
</tr>
<tr>
<td><img src="media/image7.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3">Invoca o mesmo formulario de Registo</td>
</tr>
</tbody>
</table>

### Lista Local Trabalho

<img src="media/image23.png" style="width:9.69306in;height:4.475in"
alt="Uma imagem com texto, captura de ecrã, número, software Os conteúdos gerados por IA podem estar incorretos." />

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
<td>Local Trabalho</td>
<td></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB</em>.DESCRICAO</td>
</tr>
<tr>
<td>PAIS</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB</em>.PAIS_ID</td>
</tr>
<tr>
<td>UPS</td>
<td><em>NUMBER</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB</em>.UPS</td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>SELECT</em></td>
<td>DOMINIO = STATUS</td>
<td><em>RH_T_PARAM_LOCAL_TRAB</em>.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Local / Balcão</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB</em>.DESCRICAO</td>
</tr>
<tr>
<td>País</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB</em>.PAIS_ID</td>
</tr>
<tr>
<td>Ilha</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB</em>.ILHA_ID</td>
</tr>
<tr>
<td>UPS</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB</em>.UPS</td>
</tr>
<tr>
<td>Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_LOCAL_TRAB</em>.ESTADO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Somente deve deixar editar <strong>Estado</strong></td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="3"><p>Somente deve eliminar caso não existe registo em
<strong>RH_T_TIPOS_RELACIONAMENTO.</strong>PARAM_LOCAL_TRAB_ID</p>
<p>Ao Eliminar atualiza o estado
<strong><em>RH_T_PARAM_LOCAL_TRAB</em>.</strong>estado <strong>=
‘E’</strong></p></td>
</tr>
<tr>
<td><mark>Equipamento</mark></td>
<td colspan="3">Definir os equipamentos associado a cada local de
trabalho</td>
</tr>
</tbody>
</table>

#### Equipamentos 

Este formulário é aberto partir de local trabalho, especificado no
documento de parametrização

<img src="media/image24.png" style="width:9.69306in;height:3.66597in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table style="width:100%;">
<colgroup>
<col style="width: 17%" />
<col style="width: 9%" />
<col style="width: 33%" />
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
<td style="text-align: left;">UPS</td>
<td></td>
<td></td>
<td>RH_EQUIP_CONTR_ACESSO. ID_UPS</td>
</tr>
<tr>
<td style="text-align: left;">Local Trabalho</td>
<td></td>
<td></td>
<td>---------------------------------</td>
</tr>
<tr>
<td style="text-align: left;">Ups_id</td>
<td>hidden</td>
<td></td>
<td>RH_EQUIP_CONTR_ACESSO. ID_UPS</td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Id Equipamento</td>
<td></td>
<td></td>
<td>RH_EQUIP_CONTR_ACESSO. ID_EQUIPAMENTO</td>
</tr>
<tr>
<td style="text-align: left;">Descriçao Local</td>
<td></td>
<td></td>
<td>RH_EQUIP_CONTR_ACESSO. LOCAL</td>
</tr>
<tr>
<td style="text-align: left;">Descrição Tipo Movimento</td>
<td></td>
<td></td>
<td>RH_EQUIP_CONTR_ACESSO. TP_MOVIMENTO_DESC</td>
</tr>
<tr>
<td style="text-align: left;">Tipo Movimento</td>
<td></td>
<td></td>
<td>RH_EQUIP_CONTR_ACESSO. TP_MOVIMENTO</td>
</tr>
<tr>
<td style="text-align: left;">IP Address</td>
<td></td>
<td></td>
<td>RH_EQUIP_CONTR_ACESSO.IP_ADDRESS</td>
</tr>
<tr>
<td style="text-align: left;">Picagem</td>
<td></td>
<td></td>
<td>RH_EQUIP_CONTR_ACESSO. PICAGEM</td>
</tr>
<tr>
<td style="text-align: left;">Tipo</td>
<td></td>
<td></td>
<td>RH_EQUIP_CONTR_ACESSO. TIPO</td>
</tr>
<tr>
<td style="text-align: left;">Acões</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image7.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol type="1">
<li><p>Outras GRavaçoes na tabela
<strong>RH_EQUIP_CONTR_ACESSO</strong></p>
<ul>
<li><p>USER_REGISTO</p></li>
<li><p>DT_REGISTO</p></li>
</ul></li>
</ol></td>
</tr>
</tbody>
</table>

## SECCAO

### Registar Seccão

<img src="media/image25.png" style="width:9.69306in;height:1.86042in"
alt="Uma imagem com texto, captura de ecrã, file Os conteúdos gerados por IA podem estar incorretos." />

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
<td style="text-align: left;">Nome</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SESSAO.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">Associar a Direção</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>RH_T_PARAM_SESSAO.INSTIT_ID</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td><em>SELECT</em></td>
<td>O campo deve estar oculto no registo inicial e apenas se tornar
visível quando o formulário estiver em modo de edição DOMINIO =
STATUS</td>
<td><em>RH_T_PARAM_SESSAO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td><em>hidden</em></td>
<td></td>
<td><em>RH_T_PARAM_SESSAO.ID</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image7.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol start="11" type="1">
<li><p><strong>Registar</strong></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <strong>RH_T_PARAM_SESSAO</strong>, bem como os campos
adicionais especificados a seguir</p></li>
</ol></li>
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
<ol start="12" type="1">
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

### Lista Sessão

<img src="media/image26.png" style="width:7.77778in;height:3.57778in" />

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
<td>Nome</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SESSAO.NOME</em></td>
</tr>
<tr>
<td>Direcção</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>RH_T_PARAM_SESSAO.INSTIT_ID</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>SEELCT</em></td>
<td>DOMINIO = STATUS</td>
<td><em>RH_T_PARAM_SESSAO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Nome</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SESSAO.NOME</em></td>
</tr>
<tr>
<td>Direção</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SESSAO.INSTIT_ID</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_SESSAO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Somente deve deixar editar <strong>Estado</strong></td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="3"><p>Somente deve eliminar caso não existe registo em
<strong>RH_T_TIPOS_RELACIONAMENTO.</strong>PARAM_SECAO_ID</p>
<p>Ao Eliminar atualiza o estado <em>RH_T_SESSAO</em><strong>.estado =
‘E’</strong></p></td>
</tr>
<tr>
<td><mark>Responsaveis</mark></td>
<td colspan="3">Indicar os responsaveis de cada insituicao e Seccao</td>
</tr>
</tbody>
</table>

#### Responsaveis

<img src="media/image27.png" style="width:9.69306in;height:5.03958in"
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
<td style="text-align: left;">Direção</td>
<td>CHANGE</td>
<td>INPSSIGOF.INSTITUICOES.NOME, RH_T_SESSAO.INSITIT_ID</td>
<td>RH_T_RESPONSAVEL<strong>.</strong>INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: left;">Responsavel</td>
<td>SELECT</td>
<td>RH_T_FUNCIONARIO.NOME</td>
<td>RH_T_RESPONSAVEL.FUN_ID</td>
</tr>
<tr>
<td style="text-align: left;">Email</td>
<td>TEXT</td>
<td></td>
<td>RH_T_RESPONSAVEL.EMAIL</td>
</tr>
<tr>
<td style="text-align: left;">--------------------------</td>
<td></td>
<td>---------------------------------------------------------------</td>
<td>RH_T_RESPONSAVEL.SECCAO_ID = NULL</td>
</tr>
<tr>
<td style="text-align: left;">Responsaveis Por Seção</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">Secçao</td>
<td>TEXT</td>
<td>RH_T_SESSAO.ID, RH_T_SESSAO.NOME</td>
<td>RH_T_RESPONSAVEL.SECCAO_ID</td>
</tr>
<tr>
<td style="text-align: left;">Responsavel</td>
<td></td>
<td>RH_T_FUNCIONARIO.NOME</td>
<td>RH_T_RESPONSAVEL.FUN_ID</td>
</tr>
<tr>
<td style="text-align: left;">Email</td>
<td></td>
<td></td>
<td>RH_T_RESPONSAVEL.EMAIL</td>
</tr>
<tr>
<td style="text-align: left;">------------------……</td>
<td></td>
<td>----------------------------------------------------</td>
<td>RH_T_RESPONSAVEL.INSTIT_ID</td>
</tr>
<tr>
<td style="text-align: left;">OUTRAS GRAVAÇOOES</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image7.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><p>Registo outros dados na tabela
<strong>RH_T_RESPONSAVEL</strong></p>
<ul>
<li><p><em>DATA_REGISTO = <strong>sysdate</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = <strong>id utilizador logado
</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME =<strong>nome te utilizador
logado</strong></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

## Tipo documento

### Registo tipo documento

<img src="media/image28.png" style="width:9.69306in;height:2.00764in" />

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
<td style="text-align: left;">codigo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_TIPO_DOCUMENTO.CODIGO</em></td>
</tr>
<tr>
<td style="text-align: left;">Descricao</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_TIPO_DOCUMENTO.NOME</em></td>
</tr>
<tr>
<td style="text-align: left;">Referencia</td>
<td><em>SELECT</em></td>
<td><strong>DOMAINS</strong> = <em>ACCAO_REFERENTE</em></td>
<td><em>RH_T_TIPO_DOCUMENTO.REFERENCIA</em></td>
</tr>
<tr>
<td style="text-align: left;">Estado</td>
<td><em>SELECT</em></td>
<td></td>
<td><em>RH_T_TIPO_DOCUMENTO.ESTADO</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image7.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol start="13" type="1">
<li><p><strong>Registar</strong></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <em><strong>RH_T_TIPO_DOCUMENTO</strong></em>, bem como os
campos adicionais especificados a seguir</p></li>
</ol></li>
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
<ol start="14" type="1">
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

### Listas Tipo documento

<img src="media/image29.png" style="width:9.69306in;height:4.29167in"
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
<th style="text-align: center;"><strong>Tipo</strong></th>
<th style="text-align: center;"><strong>Descrição</strong></th>
<th style="text-align: center;"><strong>Fonte dados</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td>Descrição</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_TIPO_DOCUMENTO.NOME</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Codigo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_TIPO_DOCUMENTO.CODIGO</em></td>
</tr>
<tr>
<td>Descrição</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_TIPO_DOCUMENTO.NOME</em></td>
</tr>
<tr>
<td>Referência</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_TIPO_DOCUMENTO.REFERENCIA</em></td>
</tr>
<tr>
<td>Estado</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_TIPO_DOCUMENTO.ESTADO</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Somente deve deixar editar <strong>Estado</strong></td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="3"><p>Somente deve eliminar caso não existe registo em
<strong>RH_T_DOCUMENTO.</strong>TP_DOCUMENTO_ID</p>
<p>Ao Eliminar atualiza o estado
<em>RH_T_TIPO_DOCUMENTO</em><strong>.estado = ‘E’</strong></p></td>
</tr>
</tbody>
</table>

## Notificação

### Registo de notificação

<img src="media/image30.png" style="width:8.05833in;height:3.59028in"
alt="Uma imagem com texto, número, Tipo de letra, file Os conteúdos gerados por IA podem estar incorretos." />

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
<td style="text-align: left;">Assunto</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_NOTIFICACAO.ASSUNTO</em></td>
</tr>
<tr>
<td style="text-align: left;">Corpo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_NOTIFICACAO.CORPO</em></td>
</tr>
<tr>
<td style="text-align: left;">Referencia</td>
<td><em>SELECT</em></td>
<td>DOMAINS = TIPO_NOTIFICACO</td>
<td><em>RH_T_PARAM_NOTIFICACAO.REFERENCIA</em></td>
</tr>
<tr>
<td colspan="4" style="text-align: left;"><strong>Ações</strong></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image7.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol start="15" type="1">
<li><p><strong>Registar</strong></p>
<ol type="1">
<li><p>O sistema deve registar os dados introduzidos no formulário para
a tabelas <em><strong>RH_T_PARAM_NOTIFICACAO</strong></em>, bem como os
campos adicionais especificados a seguir</p></li>
</ol></li>
</ol>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME =nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>ESTADO = A</em></p></li>
</ul>
<ol start="16" type="1">
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

### Lista Notificação

<img src="media/image31.png" style="width:9.69306in;height:4.28125in"
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
<td>Referência</td>
<td><em>TEXT</em></td>
<td>DOMAINS = TIPO_NOTIFICACO</td>
<td><em>RH_T_PARAM_NOTIFICACAO. REFERENCIA</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Assunto</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_NOTIFICACAO.ASSUNTO</em></td>
</tr>
<tr>
<td>Corpo</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_NOTIFICACAO.CORPO</em></td>
</tr>
<tr>
<td>Referência</td>
<td><em>TEXT</em></td>
<td></td>
<td><em>RH_T_PARAM_NOTIFICACAO.REFERENCIA</em></td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Somente deve deixar editar <strong>Estado</strong></td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="3">Ao Eliminar atualiza o estado
<em>RH_T_PARAM_NOTIFICACAO</em><strong>.estado = ‘E’</strong></td>
</tr>
</tbody>
</table>

# Assiduidade

<img src="media/image32.png" style="width:5.64097in;height:1.02014in" />

## Configuração de Geral

### Novo

Cada vez que se faz uma gravação na parametrizaçã se inactiva o registo
anterir e faz nova gravação

<img src="media/image33.png" style="width:8.40556in;height:5.12292in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 7%" />
<col style="width: 35%" />
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
<td colspan="4"><strong>JORNADA DIARIA -</strong> Definição de horários
de trabalho</td>
</tr>
<tr>
<td style="text-align: left;">Jornada Diária</td>
<td>HORA</td>
<td>Numero que que trabalha</td>
<td>RH_ASSIDUIDADE_PARAMETRO. DIARIA</td>
</tr>
<tr>
<td style="text-align: left;">Hora Inicio</td>
<td>HORA</td>
<td>Hara de inicio de trabalho</td>
<td>RH_ASSIDUIDADE_PARAMETRO. H_INICIO</td>
</tr>
<tr>
<td style="text-align: left;">Hara Fim</td>
<td>HORA</td>
<td>Hora fim de expediente</td>
<td>RH_ASSIDUIDADE_PARAMETRO. H_FIM</td>
</tr>
<tr>
<td style="text-align: left;"><strong>ALMOÇO</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Hora inicio</td>
<td>HORA</td>
<td>Horario inicio de almoço</td>
<td>RH_ASSIDUIDADE_PARAMETRO. AL_HORA_INICIO</td>
</tr>
<tr>
<td>Hora Fim</td>
<td>HORA</td>
<td>Horario Final do almoço</td>
<td>RH_ASSIDUIDADE_PARAMETRO. AL_HORA_FIM</td>
</tr>
<tr>
<td>Duração</td>
<td></td>
<td>Horario limite de almoço</td>
<td>RH_ASSIDUIDADE_PARAMETRO. AL_DURACAO</td>
</tr>
<tr>
<td colspan="4"><strong>TOLERÂNCA DO ATRAZO- AUSÊNCIA</strong></td>
</tr>
<tr>
<td>1º Atrazo</td>
<td></td>
<td>Primeiro minuto de tolença de atrazo</td>
<td>RH_ASSIDUIDADE_PARAMETRO. T_ATRASO</td>
</tr>
<tr>
<td>Falta Aplicada a 1º Atrazo</td>
<td></td>
<td>horas consideras como falta caso chegar ao trabalho apôs o minuto
limite do 1º atrazo</td>
<td>RH_ASSIDUIDADE_PARAMETRO.T_ATRASO_APLI_1</td>
</tr>
<tr>
<td>2º Atrazo</td>
<td></td>
<td>Parametriza apartir que qui é horario é considerado o segundo
atrado</td>
<td>RH_ASSIDUIDADE_PARAMETRO. T_ATRASO_2</td>
</tr>
<tr>
<td>Falta aplicada a 2ª atrazo</td>
<td></td>
<td>horas consideras como falta caso chegar ao trabalho apôs o minuto
limite do 2º atrazo</td>
<td>RH_ASSIDUIDADE_PARAMETRO. T_ATRASO_APLI_2</td>
</tr>
<tr>
<td>Movimento Irregular</td>
<td></td>
<td>Para que o sistema ignorar duplicações de picagem que acontence em
fraçoes de segundos definos no movimento irregular</td>
<td>RH_ASSIDUIDADE_PARAMETRO. T_MOV_IRREGULAR</td>
</tr>
<tr>
<td><strong>PRAZO LIMITE</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>prazo Limite De justificação de Falta</td>
<td>NUMBER</td>
<td></td>
<td>RH_ASSIDUIDADE_PARAMETRO.PRAZO_JUSTIF_FALTA</td>
</tr>
<tr>
<td>Prazo limite De justificação Ausência</td>
<td>NUMBER</td>
<td></td>
<td>RH_ASSIDUIDADE_PARAMETRO.PRAZO_JUSTIF_AUSENCIA</td>
</tr>
<tr>
<td><strong>DISPENSA</strong></td>
<td></td>
<td>p_frm_atraso_comp??</td>
<td></td>
</tr>
<tr>
<td>Dispensa (Em horas)</td>
<td>TIME</td>
<td>Horario limite de dispensa em um mes</td>
<td>RH_ASSIDUIDADE_PARAMETRO. T_DISPENSA</td>
</tr>
<tr>
<td><strong>HORA EXTRA</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Apartir De</td>
<td>TIME</td>
<td>Apartir de qui hora é considerado Hora Extra</td>
<td>RH_ASSIDUIDADE_PARAMETRO. HE_PARTIR_DE</td>
</tr>
<tr>
<td>Limite Diário</td>
<td>TIME</td>
<td>Limite de hora por dia</td>
<td>RH_ASSIDUIDADE_PARAMETRO. HE_DIARIA</td>
</tr>
<tr>
<td>% Dias Úteis</td>
<td>NUMBER</td>
<td>Percentagem a mais do salario receber nos dias úteis(50 , 100 ou
150??)</td>
<td>RH_ASSIDUIDADE_PARAMETRO. HE_VALOR_DUTIL</td>
</tr>
<tr>
<td>% Dias Não Úteis</td>
<td>NUMBER</td>
<td>Percentagem a mais nos dias não úteis</td>
<td>RH_ASSIDUIDADE_PARAMETRO. HE_VALOR_DNUTIL</td>
</tr>
<tr>
<td><strong>Ferias</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Numero Maximo de marcação por ano</td>
<td></td>
<td></td>
<td>RH_ASSIDUIDADE_PARAMETRO.FALTA_MAX_MARCACAO</td>
</tr>
<tr>
<td>Direito Anual</td>
<td></td>
<td></td>
<td>RH_ASSIDUIDADE_PARAMETRO.FALTA_DIREITO_ANULA</td>
</tr>
<tr>
<td>Maxímo Acomulação</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>Data Vencimento Ferias (Geral)</td>
<td></td>
<td></td>
<td>RH_ASSIDUIDADE_PARAMETRO.<em>FALTA_DATA_VENCIMENTO</em></td>
</tr>
<tr>
<td>Numero Meses limite Trabalho(1ª ano contrato)</td>
<td></td>
<td></td>
<td>RH_ASSIDUIDADE_PARAMETRO.<em>FALTA_MES_MAXIMO_ANO_1</em></td>
</tr>
<tr>
<td><strong>Fuso de Horário do UPS</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>UPS</td>
<td>SELECT</td>
<td></td>
<td>RH_FUSO_HORARIO_UPS. <em>ID_UPS</em></td>
</tr>
<tr>
<td>Fuso Horário</td>
<td>SELECT</td>
<td></td>
<td>RH_FUSO_HORARIO_UPS<em>. FUSO</em></td>
</tr>
<tr>
<td>REGRA</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="4"><ol type="1">
<li><p><em>Cada vez que se fizer uma atualizacão no formulário, logo
deve ser feito um novo insert nessatabela, se inativa o registo
anterior</em></p></li>
</ol></td>
</tr>
<tr>
<td colspan="4"><strong>AÇÕES</strong></td>
</tr>
<tr>
<td><img src="media/image7.png" style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol type="1">
<li><p>NOVO- Outras Gravaçoes na tabela
<strong>RH_ASSIDUIDADE_PARAMETRO</strong></p></li>
</ol>
<ul>
<li><p><em>DATA_REGISTO</em></p></li>
<li><p><em>USER_REGISTO_ID</em></p></li>
<li><p><em>USER_REGISTO_NAME</em></p></li>
<li><p>ESTADO</p>
<ol type="1">
<li><p>EDITAR-Gravação na tabela</p></li>
</ol></li>
</ul>
<ul>
<li><p>Inativa o registo Anterior</p>
<ul>
<li><p><em>USER_ALTERACAO_ID</em></p></li>
<li><p><em>USER_ALTERACAO_NAME</em></p></li>
<li><p><em>DATA_ALTERACAO</em></p></li>
<li><p>ESTADO = ‘I’</p></li>
</ul></li>
<li><p>Cria novo Registo</p></li>
</ul></td>
</tr>
</tbody>
</table>

### Lista 

| **Lista** | **Tipo** | **Descrição** | **Fonte dados** |
|----|----|----|----|
| Data Registo |  |  | **RH_ASSIDUIDADE_PARAMETRO.** *DATA_REGISTO* |
| Utilizador Registo |  |  | **RH_ASSIDUIDADE_PARAMETRO.** *USER_REGISTO_NAME* |
| Estado |  |  | **RH_ASSIDUIDADE_PARAMETRO**.ESTADO |

## Tipo Falta / Ausência (Justificada e injustificada)

### Lista

<img src="media/image34.png" style="width:8.02292in;height:4.54028in"
alt="Uma imagem com texto, captura de ecrã, software, número Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 6%" />
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
<td>Falta</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_TIPO_FALTAS. FALTA</td>
</tr>
<tr>
<td>Situação</td>
<td><em>SELECT</em></td>
<td></td>
<td>RH_TIPO_FALTAS.SITUACAO</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Lista</strong></td>
<td style="text-align: center;"><strong>Tipo</strong></td>
<td style="text-align: center;"><strong>Descrição</strong></td>
<td style="text-align: center;"><strong>Fonte dados</strong></td>
</tr>
<tr>
<td>Situação</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_TIPO_FALTAS.SITUACAO</td>
</tr>
<tr>
<td>Código</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_TIPO_FALTAS. TIPO</td>
</tr>
<tr>
<td>Descrição</td>
<td><em>TEXT</em></td>
<td></td>
<td>RH_TIPO_FALTAS. FALTA</td>
</tr>
<tr>
<td>------------------</td>
<td></td>
<td>Agrupar por RH_TIPO_FALTAS. TF_ID</td>
<td>RH_TIPO_FALTAS. TF_ID</td>
</tr>
<tr>
<td style="text-align: center;"><strong>Acções</strong></td>
<td colspan="3" style="text-align: center;"></td>
</tr>
<tr>
<td>Editar</td>
<td colspan="3">Invoca a mesma pagina de Registo</td>
</tr>
<tr>
<td>Eliminar</td>
<td colspan="3"><p>Faz update na tabela
<strong>RH_TIPO_FALTAS</strong></p>
<ul>
<li><p>ESTADO = I</p></li>
<li><p><em>USER_ALTERACAO_ID</em></p></li>
<li><p><em>USER_ALTERACAO_NAME</em></p></li>
<li><p><em>DATA_ALTERACAO</em></p></li>
</ul></td>
</tr>
</tbody>
</table>

###  Novo 

<img src="media/image35.png" style="width:9.69306in;height:2.70486in"
alt="Uma imagem com captura de ecrã, texto, file, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

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
<td style="text-align: left;">Situação</td>
<td>RADIOLIST</td>
<td>DOMINIO = JUSTIFICADA_INJUSTIFICADA</td>
<td><p>RH_TIPO_FALTAS.SITUACAO</p>
<p>RH_TIPO_FALTAS.REFERENCIA = RH_T_DOMINIO.REFERENCIA</p></td>
</tr>
<tr>
<td style="text-align: left;">Código</td>
<td>TEXT</td>
<td>o codigo deve ser único não pode repetir.</td>
<td>RH_TIPO_FALTAS. TIPO</td>
</tr>
<tr>
<td style="text-align: left;">Descrição</td>
<td>TEXT</td>
<td></td>
<td>RH_TIPO_FALTAS. FALTA</td>
</tr>
<tr>
<td style="text-align: left;">Associar a:</td>
<td>Select</td>
<td>Preenche o combo com dados de tabela RH_T_FALTA onde TF_ID é
nulo</td>
<td>RH_TIPO_FALTAS. TF_ID</td>
</tr>
<tr>
<td style="text-align: left;">Desconto Remuneração</td>
<td>RADIOLIST</td>
<td>DOMAIN = SIM_NAO</td>
<td>RH_TIPO_FALTAS. FLG_DESCONTO_SALARIO</td>
</tr>
<tr>
<td style="text-align: left;">Ações</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image7.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol type="1">
<li><p><strong>RH_TIPO_FALTAS</strong></p></li>
</ol>
<ul>
<li><p><em>USER_REGISTO_ID = <strong>id de utilizador
logado</strong></em></p></li>
<li><p><em>DATA_REGISTO = <strong>SYSDATE</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = <strong>Nome de utilizador
logado</strong></em></p></li>
<li><p>ESTADO = ‘<strong>A</strong>’</p></li>
</ul></td>
</tr>
</tbody>
</table>

## Feriado 

### Novo

Parametrizar os dias feriados que não serão contabelizados nas ferias

<img src="media/image36.png" style="width:9.69306in;height:2.86528in"
alt="Uma imagem com texto, file, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 10%" />
<col style="width: 36%" />
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
<td style="text-align: left;">Ano Referencia</td>
<td>COMBOCHANGE</td>
<td>Por defeito traz o ano <strong>SYSDATE</strong></td>
<td>RH_T_PARAM_FERIADO.ANO_REFERENTE</td>
</tr>
<tr>
<td style="text-align: left;">Descrição</td>
<td>TEXT</td>
<td></td>
<td>RH_T_PARAM_FERIADO.DESCRICAO</td>
</tr>
<tr>
<td style="text-align: left;">Data Feriado</td>
<td>DATE</td>
<td></td>
<td>RH_T_PARAM_FERIADO.DATA</td>
</tr>
<tr>
<td style="text-align: left;"><strong>Acões</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><img src="media/image7.png"
style="width:0.6375in;height:0.26944in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></td>
<td colspan="3"><ol type="1">
<li><p>Registo na tabela <strong>RH_T_PARAM_FERIADO</strong></p></li>
</ol>
<ul>
<li><p><em>USER_REGISTO_ID = <strong>id de utilizador
logado</strong></em></p></li>
<li><p><em>DATA_REGISTO = <strong>SYSDATE</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = <strong>Nome de utilizador
logado</strong></em></p></li>
<li><p>ESTADO = ‘<strong>A</strong>’</p></li>
</ul></td>
</tr>
</tbody>
</table>

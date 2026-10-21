Especificação Tecnica e Funcional – Dossiê do Colaborador

SIPS-RH

> GEST♙O ♙SSIDUID♙DE

<img src="media/image2.png" style="width:2.75418in;height:0.52in" /><img src="media/image3.png" style="width:0.93709in;height:0.58917in" />

> Especificação Tecnica e Funcional – Dossiê do Colaborador

# Enquadramento

2.  **Âmbito**

<img src="media/image4.jpeg" style="width:9.7764in;height:3.8775in"
alt="Uma imagem com texto, captura de ecrã, Tipo de letra, número Os conteúdos gerados por IA podem estar incorretos." />

<img src="media/image2.png" style="width:2.75418in;height:0.52in" /><img src="media/image3.png" style="width:0.93489in;height:0.58917in" />

# Especificação

<img src="media/image5.png" style="width:9.71244in;height:1.46125in" />

### Movimento de Picagem (Entrada / saída)

> Mostra a Lista Geral de todas as entradas e saídas do relógio
>
> ![](media/image6.jpeg)

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Filtro</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><blockquote>
<p>Nome Colaborador</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_MOVIMENTOS.ID_COLABORADOR</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Direção</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Secção</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>UPS</p>
</blockquote></th>
<th><blockquote>
<p><em>UPS</em></p>
</blockquote></th>
<th><blockquote>
<p>SIPSGLOBAL.GLB_T_UPS.NOME</p>
</blockquote></th>
<th><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</p>
<p>RH_T_LOCAL_TRABALHO. ID_UPS</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Data Inicio</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_MOVIMENTOS. DT_MOVIMENTO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_MOVIMENTOS. DT_MOVIMENTO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Lista</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Nome Colaborador</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FUNCIONARIOS.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_MOVIMENTOS.ID_COLABORADOR</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_MOVIMENTOS.DT_MOVIMENTO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Hora Entrada</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_MOVIMENTOS. getLocalUserMovimento</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Hora Saida</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_MOVIMENTOS.hora_movimento Dir||' - '||.getLocalUserMovimento</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Acções</strong></p>
</blockquote></td>
<td colspan="3"></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image7.png"
style="width:1.19536in;height:0.30417in" /></p>
</blockquote></td>
<td colspan="3"><blockquote>
<p>Caso alguns dados não foi importado de forma automático, o RH Tem a
possibilidade de o fazer manualmente com esse botão-</p>
<p>Esse botão invoca um serviço que importa dados do relógio.</p>
</blockquote></td>
</tr>
</tbody>
</table>

#### <span class="mark">Importar Dados</span>

> BEGIN
>
> select count(id) into v_count_processado from rh_movimentos where
> trunc(dt_movimento) between
> trunc(to_date(**´P_data_inicio**´,'dd-mm-yyyy')) and
> trunc(to_date(**´P_data_fim**´, 'dd-mm-yyyy')) and processado = 1;
>
> IF v_count_processado = 0 THEN
>
> DELETE FROM RH_MOVIMENTOS WHERE trunc(dt_movimento) between
> trunc(to_date(**´P_data_inicio**´,'dd-mm-yyyy')) and
> trunc(to_date(**´P_data_fim**´, 'dd-mm-yyyy'));
>
> INPSRH.IMPORT_DADOS_CONTR_ACESSO(p_data_inicio
> =\>to_char(to_date(**´P_data_inicio**´,'dd-mm-yyyy'), 'yyyy-mm-dd'),
> p_data_fim =\>to_char(to_date(**´P_data_fim**´, 'dd-mm-yyyy') + 1,
> 'yyyy-mm-dd'));
>
> END IF;
>
> EXCEPTION WHEN OTHERS THEN NULL;
>
> END;

### <span class="mark">Gestão Falta</span> ~~Movimento Resumido~~

#### Lista

![](media/image8.jpeg)

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 4%" />
<col style="width: 26%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Filtro</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><blockquote>
<p>Direção</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>INPSSIGOF.INSTITUICOES.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 4%" />
<col style="width: 26%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Colaborador</p>
</blockquote></th>
<th></th>
<th><blockquote>
<p>RH_T_FUNCIONARIOS.NOME</p>
</blockquote></th>
<th><blockquote>
<p>RH_ASSIDUIDADE_SINTESE_DIARIA. FUNCIONARIO_ID</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>UPS</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>SIPSGLOBAL.GLB_T_UPS. NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</p>
<p>RH_T_PARAM_LOCAL_TRAB.UPS_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Inicio</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_ASSIDUIDADE_SINTESE_DIARIA .DATA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_ASSIDUIDADE_SINTESE_DIARIA .DATA</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Lista</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Estado</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Estado da falta,conforme,inj<strong>ustiticada</strong>,</p>
<p><strong>justificada ,Pendente,</strong></p>
<p><strong>DOMAIN =</strong></p>
</blockquote></td>
<td><blockquote>
<p>RH_ASSIDUIDADE_SINTESE_DIARIA.ESTADO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Mês Referencia</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>AGRUPAR</p>
</blockquote></td>
<td><blockquote>
<p>RH_ASSIDUIDADE_SINTESE_DIARIA.DATA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Nome Colaborador</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Nome do colaborador</p>
<p>RH_T_FUNCIONARIOS.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_ASSIDUIDADE_SINTESE_DIARIA. FUNCIONARIO_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total de Falta</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Contam todas faltas cujo estado =’SIM’</p>
</blockquote></td>
<td><blockquote>
<p><strong>SUM</strong> (RH_ASSIDUIDADE_SINTESE_DIARIA.FALTA)</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total de Dias</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Agrupar as datas que tem falta</p>
</blockquote></td>
<td><blockquote>
<p><strong>COUNT</strong> (RH_ASSIDUIDADE_SINTESE_DIARIA.DATA)</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Horas Trabalhadas</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Somar as horas trabalhadas</p>
</blockquote></td>
<td><blockquote>
<p><strong>COUNT</strong>(SUBSTR(TO_CHAR(<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.HORAS_TRABALHADAS
), 4,</p>
<p>8);)</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Horas Extras</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Somas as horas extras</p>
</blockquote></td>
<td><blockquote>
<p><strong>COUNT</strong>(SUBSTR(TO_CHAR(<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.HORAS_EXTRAS),
4, 8);</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Hora Ausência</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Somar as horas Ausência</p>
</blockquote></td>
<td><blockquote>
<p><strong>COUNT</strong>(SUBSTR(TO_CHAR(<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.HORAS_AUSENCIA),
4, 8);</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table style="width:99%;">
<colgroup>
<col style="width: 3%" />
<col style="width: 3%" />
<col style="width: 3%" />
<col style="width: 7%" />
<col style="width: 4%" />
<col style="width: 26%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th colspan="4"><blockquote>
<p><del>Motivo Ausencia</del></p>
</blockquote></th>
<th></th>
<th style="text-align: left;"><blockquote>
<p><del>Verifica se o colaborador esta ausente de</del> <del>trabalhor
neste periodo (caso sim vem</del> <del>preenchido )</del></p>
</blockquote></th>
<th><blockquote>
<p><del><strong>RH_T_AUSENCIA.</strong>PARAM_SIT_ID <strong>,
RH_T_PARAM_SITUACAO.</strong>NOME</del></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="7"><blockquote>
<p><strong>REGRA</strong></p>
</blockquote></td>
</tr>
<tr>
<td colspan="7"><ul>
<li><p>Os colaboradores com ausência justificada não devem aparecer na
lista. Ou seja, no período que o colaborador esta ausente de trabalho,
já seja por motivo de ferias, missão de serviço. Licença sem vencimento
…etc , a sua falta fica justificada automaticamente e não deve aparecer
na lista. Para ver se um colaboradors está ausente de trabalho –
tabela</p></li>
</ul>
<blockquote>
<p>(<strong>RH_T_AUSENCIA</strong>)</p>
</blockquote></td>
</tr>
<tr>
<td colspan="4" style="text-align: center;"><blockquote>
<p><strong>Acções</strong></p>
</blockquote></td>
<td colspan="3"></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><img src="media/image9.png"
style="width:1.04786in;height:0.29583in" /></p>
</blockquote></td>
<td colspan="3"><blockquote>
<p>Permite marcar um colaborador falta manualmente</p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><img src="media/image10.png"
style="width:1.53105in;height:0.22083in" /></p>
</blockquote></td>
<td colspan="3"><blockquote>
<p>Abre formulario para justificacao de Falta</p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><img src="media/image11.png"
style="width:1.59609in;height:0.25in" /></p>
</blockquote></td>
<td colspan="3"><blockquote>
<p>Abre a mesma <strong>Lista de Movimento Picagem (Entrada / Saída),
<mark>mas filtrado com dados de um colaborador
especifico</mark></strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image12.png" /></p>
</blockquote></td>
<td><blockquote>
<p><img src="media/image12.png" /></p>
</blockquote></td>
<td><blockquote>
<p><img src="media/image12.png" /></p>
</blockquote></td>
<td colspan="4"></td>
</tr>
</tbody>
</table>

#### Marcar Falta / Ausência

<img src="media/image16.jpeg"
style="width:4.73717in;height:5.64375in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Formulario</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Gravação</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p><mark>Validar</mark></p>
</blockquote></td>
<td><blockquote>
<p><mark>Select</mark></p>
</blockquote></td>
<td><blockquote>
<p><mark>Deve aparecer somente em Validacao</mark></p>
<p><strong>DOMAIN</strong> (VALIDAR_REGISTO)</p>
</blockquote></td>
<td><blockquote>
<p><mark><strong>RH_ASSIDUIDADE_SINTESE_DIARIA.</strong>ESTADO</mark></p>
<p>RH_T_FALTA.estado</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Colaborador</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_COLABORADORES.NOME</p>
</blockquote></td>
<td><blockquote>
<p><strong>RH_ASSIDUIDADE_SINTESE_DIARIA.</strong>FUNCIONARIO_ID</p>
<p>RH_T_PEDIDO.FUN_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data inicio</p>
</blockquote></td>
<td><blockquote>
<p>DATE</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><strong>RH_ASSIDUIDADE_SINTESE_DIARIA.DATA</strong></p>
<p>RH_T_FALTA.DATA_INICIO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td><blockquote>
<p>Date</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.DATA_FIM</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Horas <strong>Ausência</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>. HORAS_AUSENCIA</p>
<p><strong>RH_T_T_FALTA.</strong>HORAS_AUSENCIA /TOTAL DIAS</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total Dias</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Diferença entre Data Inicio e Data Fim.</p>
</blockquote></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Com Justificativo?</p>
</blockquote></td>
<td><blockquote>
<p>RADIOLIST</p>
</blockquote></td>
<td><blockquote>
<p><strong>DOMAINS = SIM_NAO</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>---------------------</strong></p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>Nota:</strong> os campos abaixo so aparecem caso Com
Justificativo = SIM</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Motivo Ausência</p>
</blockquote></td>
<td><blockquote>
<p>TEXAREA</p>
</blockquote></td>
<td><blockquote>
<p>Preenchido a partir de tabela <strong>RH_T_FALTA</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>RH_T_FALTA</strong>.DESCRICAO<strong>_</strong>MOTIVO</p>
</blockquote></td>
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
<thead>
<tr>
<th><blockquote>
<p>Tipo Falta</p>
</blockquote></th>
<th><blockquote>
<p>SELECT</p>
</blockquote></th>
<th style="text-align: left;"><blockquote>
<p>Deve buscar a descrição do tipo de falta escolhido … para buscar a
justificativo, deve buscar da tabela de parametrização
RH_T_PARAM_SITUACAO onde <em>FLG_ausencia</em></p>
<p><em>= FALTA</em></p>
</blockquote></th>
<th><blockquote>
<p>RH_T_FALTA<strong>.</strong>PARAM_SIT_ID</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Desduzir Falta Em</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Domains = TP_DESCONTO_FALTA</p>
</blockquote>
<ul>
<li><p>Ferias</p></li>
<li><p>Dispensa</p></li>
</ul></td>
<td><blockquote>
<p>RH_T_FALTA.FLG_DESCONTO_FALTA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Valor diario Falta</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Calcula o valor de falta por cada dia por cada horas de ausência</p>
<p>RH_PROCESSAMENTO_SALARIAL_DB. CALCULO_FALTA_DIARIO (P_TIPREL_ID
NUMBER,</p>
<p>p_data_inicio DATE)</p>
<p>Nota :essa funcao somente develvo o valor diario, que deve ser
calculado por total de dias</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FALTA.VALOR</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Valor Total</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Valor diário * totais dias</p>
</blockquote></td>
<td><blockquote>
<p>-----------------------</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><strong>Parecer Responsável</strong></p>
</blockquote></td>
<td colspan="3"><blockquote>
<p><strong>Nota:</strong> este separador só aparece caso Com
Justificativo = SIM</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Parece Responsável</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p><strong>DOMAIN</strong> = PARECER_DECISAO, REFERENCIA =
PARECER_RESPONSAVEL</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FALTA.DECISAO_RESPONSAVEL</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Responsavel</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p>Pegar da Tabela <strong>RH_T_RESPONSAVEL.FUN_ID</strong>,
<strong>RH_T_FUNCIONARIO</strong>.ID</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FALTA RESPONSAVEL_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Observação</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.OBS_RESPONSAVEL</p>
</blockquote></td>
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
<thead>
<tr>
<th colspan="4"><blockquote>
<p><strong>Anexar Documentos - Nota:</strong> este separador só aparece
caso Com Justificativo = SIM</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Tipo Documento</p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Anexar Documento</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DOCUMENTO.<em>DOC_ID</em></p>
<p>RH_T_DOCUMENTO.TIPO_DOCUMETO = ID DE TABELA</p>
<p>RH_T_TIPO_DOCUMENTO ONDE REFERENCIA = ‘JUSTIFICACAO_FALTA’</p>
<p>REFERENCIA_NAME = ‘RH_T_FALTA’</p>
<p>REFERENCIA_ID = id de RH_T_FALTA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><strong>REGRA</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="4"><ul>
<li><p>Caso campo justificar for <strong>SIM</strong>:</p>
<ul>
<li><p>Deve aparecer o separador de <strong>Parecer Respnsável</strong>
e <strong>Despacho RH</strong></p></li>
<li><p>Regista na tabela RH_T_PEDIDO, e RH_T_FALTA</p></li>
<li><p>Campo Validar, fica visível somente em Validação</p></li>
</ul></li>
<li><p>O número de registo na tabela RH_T_FALTA dependerá do número de
dias de falta (por cada dia de falta é um registo na tabela)</p></li>
<li><p>Somente deve ir para validação caso o número de falta for maior
que 3 dias, e caso o tipo de justificação esta sujeito a desconto no
salário
(<em><strong>RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL</strong></em>)
caso contrário o registo fica registado com estado ATIVO</p></li>
</ul>
<blockquote>
<p><strong>Pendente</strong>: cálculo de valor na justificacao de
Falta</p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>Ações</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image17.png" style="width:0.63617in;height:0.265in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p>
</blockquote></td>
<td colspan="3"><blockquote>
<p>1.Grava na tabela <strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong></p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 84%" />
</colgroup>
<thead>
<tr>
<th></th>
<th><ul>
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
<ol start="2" type="1">
<li><p>Caso se pretende além de registar Falta Também Justificar a
Falta. Logo deve registar o Pedido de justificação</p>
<ol type="1">
<li><p>Grava na tabela <strong>RH_T_PEDIDO (somente caso a falta for
justificada “com justificação = Sim”)</strong></p>
<ul>
<li><p>TIPO_PEDIDO = ‘JUSTIFICACAO_FALTA’,</p></li>
<li><p>ETAPA = DESPACHO_RH</p></li>
</ul></li>
</ol></li>
</ol></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 84%" />
</colgroup>
<thead>
<tr>
<th></th>
<th><blockquote>
<p><strong>2.2 R</strong>egisto na tabela de
<strong>RH_T_FALTA</strong>, campo do formulário e outros seguintes
campos (regista aqui somente apos justificação de Falta)</p>
</blockquote>
<ul>
<li><p>DATA_REGISTO</p></li>
<li><p>USER_REGISTO_ID</p></li>
<li><p>USER_REGISTO_NAME</p></li>
<li><p>PEDIDO_ID = id de RH_T_PEDIDO</p></li>
<li><p>SINTESE_DIARIO_ID = id de
<strong>RH_ASSIDUIDADE_SINTESE_DIARIO</strong></p></li>
<li><p>TIPO = ‘FALTA’</p></li>
<li><p>Estado = A</p></li>
</ul>
<blockquote>
<p><strong>2.3</strong> Regista na tabela <strong>RH_T_VALIDACAO
(Somento o Tipo de Falta tem Salarios Associado)</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

##### Validar Falta

> Ao Validar

- Atualiza o seguinte:

  - RH_T_VALIDACAO.ESTADO = ‘A’

  - RH_T_PEDIDO.ETAPA = ‘FINALIZADO’

  - RH_T_FALTA.ESTADO = ‘A’

  - RH_T_FALTA.TIPREL_ID = ID de **RH_T_TIPOS_RELACIONAMENTO**.ID onde
    **EST_ACT_ADM** = 1

- **caso o tipo de falta desconta salário, logo deve registar na
  Tabela**

  - ~~​ **RH_T_DEF_REMUNERACOES**~~
    **<span class="mark">RH_T_DEF_REMUNERACOES</span>**

  - atualiza id **<span class="mark">RH_T_DEF_REMUNERACOES</span>** em
    **<span class="mark">RH_T_FALTA. DEF_REM_ID</span>**

  - Regista a associacao na tabela
    **<span class="mark">RH_T_TIPREL_REM_PAG .REM_ID</span>**

<!-- -->

- **Caso o tipo de falta for desconto nas ferias (campo DEDUZIR EM =
  FERIAS ),** logo deve registar na Tabela

  - Regista na tabela **RH_T_FERIAS_GOZADAS**

- **Caso o tipo de justificação for desconto nas horas de Dispensa
  (campo DEDUZIR EM =** DISPENSA **),**

  - Regista na **RH_T_DISPENSA**

#### Justificar Falta

![](media/image18.jpeg)

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 6%" />
<col style="width: 37%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Formulario</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Gravação</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="4"><blockquote>
<p><strong>RESUMO FALTAS</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Selecionar</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.<em>SINTESE_DIARIO_ID</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.DATA_INICIO</p>
<p>RH_T_FALTA.DATA_FIM</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Estado</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.ESTADO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total de Horas</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.
HORAS_AUSENCIA<strong>RH_T_T_FALTA.</strong>HORAS_AUSENCIA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image19.jpeg"
style="width:0.21701in;height:0.20344in" /></p>
</blockquote></td>
<td><blockquote>
<p>Editar</p>
</blockquote></td>
<td><blockquote>
<p>Permite editar a justificação de falta o grupo selecionado (agrupados
por <strong>RH_T_FALTA</strong>.PEDIDO_ID)</p>
</blockquote></td>
<td><blockquote>
<p><strong>-----------------------------------</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image20.jpeg" /></p>
</blockquote></td>
<td><blockquote>
<p>Eliminar</p>
</blockquote></td>
<td><blockquote>
<p>Permite editar a justificação de falta o grupo selecionado (agrupados
por <strong>RH_T_FALTA</strong>.PEDIDO_ID)</p>
</blockquote></td>
<td><blockquote>
<p><strong>RH_T_FALTA.</strong>ESTADO <strong>= ‘E’</strong></p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>JUSTIFICAR FALTAS SELECIONADAS</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Validar</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><strong>DOMAIN</strong> (VALIDAR_REGISTO)</p>
</blockquote></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Com Justificativo</p>
</blockquote></td>
<td><blockquote>
<p>Seelct</p>
</blockquote></td>
<td><blockquote>
<p><strong>DOMAIN</strong> = SIM_NAO</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FALTA.FLG_JUSTIFICATIVO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Motivo</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.DESCRICAO<strong>_</strong>MOTIVO</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 6%" />
<col style="width: 37%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Tipo Falta</p>
</blockquote></th>
<th><blockquote>
<p>SELECT</p>
</blockquote></th>
<th style="text-align: left;"><blockquote>
<p>Desve buscar a descrição do tipo de falta escolhido … para buscar a
justificativo, deve buscar da tabela de parametrização
RH_T_PARAM_SITUACAO onde <em>FLG_AUSENCIA = FALTA</em></p>
</blockquote></th>
<th><blockquote>
<p>RH_T_FALTA<strong>.PARAM_SIT_ID</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Deduzir Falta Em</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Domains = TP_DESCONTO_FALTA</p>
</blockquote>
<ul>
<li><p>Ferias</p></li>
<li><p>Dispensa</p></li>
</ul></td>
<td><blockquote>
<p>RH_T_FALTA.FLG_DESCONTO_FALTA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Valor Diário</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Calcula o valor de falta por cada dia ..ou por cada horas de
ausência</p>
<p>RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_FALTA_DIARIO</p>
<p>(P_TIPREL_ID NUMBER, p_data_inicio DATE)</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FALTA.VALOR</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Valor Total</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Valor diário * totais dias</p>
</blockquote></td>
<td><blockquote>
<p>-----------------------</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>-----------------------------</p>
</blockquote></td>
<td><blockquote>
<p>hidden</p>
</blockquote></td>
<td><blockquote>
<p>Depende do motivo selecionado</p>
<p>RH_T_TIPO_FALTA.DESCONTO_REMUNERACAO</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FALTA.FLG_DESCONTO_SAL</p>
</blockquote></td>
</tr>
<tr>
<td></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Tipo Documento</p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Anexar Documento</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DOCUMENTO.<em>DOC_ID</em></p>
<p>RH_T_DOCUMENTO.TIPO_DOCUMETO = ID DE TABELA</p>
<p>RH_T_TIPO_DOCUMENTO ONDE REFERENCIA = ‘JUSTIFICACAO_FALTA’</p>
<p>REFERENCIA_NAME = ‘RH_T_FALTA’</p>
<p>REFERENCIA_ID = id de RH_T_FALTA</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 6%" />
<col style="width: 37%" />
<col style="width: 40%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Parecer Responsável</strong></p>
</blockquote></th>
<th></th>
<th></th>
<th></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Parecer Responsável</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p><strong>DOMAIN</strong> = PARECER_DECISAO, REFERENCIA =
PARECER_RESPONSAVEL</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FALTA.DECISAO_RESPONSAVEL</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Responsavel</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p>Pegar da Tabela <strong>RH_T_RESPONSAVEL.FUN_ID</strong> ,
<strong>RH_T_FUNCIONARIO</strong>.ID</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FALTA RESPONSAVEL_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Observação</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.OBS_RESPONSAVEL</p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>REGRA</strong></p>
</blockquote></td>
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
<li><p>Somente deve ir para validação caso o número de falta for maior
que 3 dias, e caso o tipo de justificação esta sujeito a desconto no
salário
(<em><strong>RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL</strong></em>)
caso contrário o registo fica registado com estado ATIVO</p></li>
<li><p>Campo Validar, fica visível somente em Validação</p></li>
</ul>
<blockquote>
<p><strong>Pendente</strong>: cálculo de valor na justificacao de
Falta</p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>Ações</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image17.png" style="width:0.63029in;height:0.265in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p>
</blockquote></td>
<td colspan="3"><ol type="1">
<li><p>Registo na tabela <strong>RH_T_PEDIDO</strong></p>
<ul>
<li><p>TIPO_PEDIDO = <strong>DOMAIN</strong> = TIPO_PEDIDO,
<strong>VALOR</strong> = JUSTIFICACAO_FALTA</p></li>
<li><p>ORIGEM = RH</p></li>
<li><p>ESTADO = <strong>‘P’</strong></p></li>
</ul></li>
</ol></td>
</tr>
</tbody>
</table>

<table style="width:99%;">
<colgroup>
<col style="width: 15%" />
<col style="width: 84%" />
</colgroup>
<thead>
<tr>
<th></th>
<th><ul>
<li><p>FUN_ID</p></li>
<li><p>DATA_PEDIDO = <strong>SYSDATE</strong></p></li>
<li><p>DATA_REGISTO = <strong>SYSDATE</strong></p></li>
<li><p>USER_REGISTO_ID = user_logado</p></li>
<li><p>USER_ REGISTO_NAME = nome utilizador</p></li>
</ul>
<blockquote>
<p>2- Registo na Tabela <strong>RH_T_VALIDACAO (caso o tipo fr falta tem
salario )</strong></p>
</blockquote>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘</strong></em>JUSTIFICAR
_FALTA<em><strong>’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= RH_T_PEDIDO.ID</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

##### Validação

> Apôs Validação,

- Atualiza os estado pendentes para ativo

- Caso a justificação é sujeita a desconto no salário, logo deve
  registar no **RH_T_DEF_REMUNERACOES** o registo de falta

- Regista a associacao na tabela **RH_T_TIPREL_REM_PAG .**REM_ID

- Atualiza o id de PAGAMENTO na tabela **RH_T_FALTA.** DEF_REM_ID

- **Caso o tipo de falta for desconto nas ferias (campo DEDUZIR EM =
  FERIAS),** logo deve registar na Tabela

- Regista na tabela **RH_T_FERIAS_GOZADAS**

<!-- -->

- **Caso o tipo de justificação for desconto nas horas de Dispensa
  (campo DEDUZIR EM = DISPENSA),**

- Regista na **RH_T_DISPENSA**

### Falta Justificada / injustificada

![](media/image21.jpeg)

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Filtro</strong></p>
</blockquote></th>
<th style="text-align: right;"><p><strong>Tipo</strong></p></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><blockquote>
<p>Ilha</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Direção</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Secção</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Data Inicio</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.DATA_INICIO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.DATA_FIM</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Estado</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td><blockquote>
<p>DOMAIN= ESTADO_FALTA</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FALTA.ESTADO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Lista</strong></p>
</blockquote></td>
<td style="text-align: right;"><p><strong>Tipo</strong></p></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Estado</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.ESTADO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Direção</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.TIPREL_ID, RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Nome Colaborador</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.FUN_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Categoria</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Data Inicio / Data Fim</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.DATA_INICIO / RH_T_FALTA.DATA_FIM</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Motivo</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.TF_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Total Horas Ausente</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.HORAS_AUSENTE</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Numero De Falta</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.NUM_FALTA</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Valor a Descontar</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA.VALOR_DESCONTO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Desconto na Remuneração?</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FALTA. TF_ID, RH_TIPO_FALTAS. FLG_DESCONTO_SALARIO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Estado Processamento</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>Verifica se existe um registo em RH_REMUNERACOES</p>
</blockquote></td>
<td><blockquote>
<p>-----------------------------------------------------</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Acoes</strong></p>
</blockquote></th>
<th></th>
<th></th>
<th></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p><mark>Ver detalhe Falta</mark></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><mark>Abre o mesmo formulário de justificação de Falta</mark></p>
</blockquote></td>
<td></td>
</tr>
</tbody>
</table>

### Dispensa

#### Lista

![](media/image22.jpeg)

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: right;"><p><strong>Filtro</strong></p></th>
<th style="text-align: right;"><p><strong>Tipo</strong></p></th>
<th style="text-align: right;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: right;"><blockquote>
<p>Colaborador</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FUNCIONARIOS.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_DISPENSA.TIPREL_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Ilha</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Direção</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td><blockquote>
<p>INPSSIGOF.INSTITUICOES.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Seccão</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Data Inicio</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.DATA</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.DATA</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>ESTADO</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>DOMAIN = ESTADO_DISPENSA</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_DISPENSA.ESTADO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><p><strong>Lista</strong></p></td>
<td style="text-align: right;"><p><strong>Tipo</strong></p></td>
<td style="text-align: right;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Direção</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>INPSSIGOF.INSTITUICOES.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Colaborador</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FUNCIONARIOS.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_DISPENSA.TIPREL_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Vínculo</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.CONTRATO_VINCULO_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Categoria</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Data pedido</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA. <em>DATA_REGISTO</em></p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Data Dispensa</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.DATA</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Hora início / Fim</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.HORA_INICIO/RH_T_DISPENSA.HORA_FIM</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;"><blockquote>
<p>Hora Direito</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>FAZ SUMATORIA DE TOTAL HORAS</p>
<p>Lembrate, pode usar o total de horas em justificação de falta</p>
</blockquote></td>
<td><blockquote>
<p>SUM(RH_T_DISPENSA.TOTAL_HORA)</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Total Horas solicitada</p>
</blockquote></th>
<th></th>
<th></th>
<th><blockquote>
<p>RH_T_DISPENSA.TOTAL_HORA</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Motivo dispensa</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.MOTIVO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Acções</strong></p>
</blockquote></td>
<td colspan="3"></td>
</tr>
<tr>
<td><blockquote>
<p>Novo</p>
</blockquote></td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

#### Novo / Editar

![](media/image23.jpeg)

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Formulario</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><blockquote>
<p><strong>Gravação</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="4"></td>
</tr>
<tr>
<td><blockquote>
<p>Colaborador</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.TIPREL_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Horas usada no mês</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Total horas de dispensa no mês. somar Hora inicio e Hora fim
(HORA_INICIO, HORA_FIM)</p>
</blockquote></td>
<td><blockquote>
<p>-------------------------------------------</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Horas disponiveis</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_ASSIDUIDADE_PARAMETRO.T_DISPENSA , somatoria de (HORA_INICIO,
HORA_FIM)</p>
</blockquote></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Data início dispensa</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.DATA_INICIO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><mark>Data Fim dispensa</mark></p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><mark>RH_T_DISPENSA.DATA_FIM</mark></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Hora Inicio</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.HORA_INICIO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Hora Fim</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.HORA_FIM</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Tipo Motivo</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>DOMAIN =MOTIVO_DISPENSA</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_DISPENSA.TIPO_MOTIVO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Motivo</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.DESCRICAO_MOTIVO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><strong>Parecer Responsavel</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Parecer Responsável</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p><strong>DOMAIN</strong> = PARECER_DECISAO, REFERENCIA =
PARECER_RESPONSAVEL</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_DISPENSA.DECISAO_RESPONSAVEL</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Responsavel</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p>Pegar da Tabela <strong>RH_T_RESPONSAVEL.FUN_ID</strong>,
<strong>RH_T_FUNCIONARIO</strong>.ID</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_DISPENSA.RESPONSAVEL_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Observação</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.OBS_RESPONSAVEL</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><strong>Parecer RH</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
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
<th><blockquote>
<p>Validar</p>
</blockquote></th>
<th></th>
<th></th>
<th><blockquote>
<p>RH_T_DISPENSA.DECISAO_RH</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Observação RH</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DISPENSA.OBS_RH</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Anexar Documento</p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Tipo documento</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DOCUMENTO.TIPO_DOMENTO_ID, onde tipo é referecia É igual a
<strong>DISPENSA</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Documento</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_DOCUMENTO.DOC_ID</p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>Ações</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image17.png" style="width:0.63292in;height:0.265in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p>
</blockquote></td>
<td colspan="3"><ol type="1">
<li><p>Grava na tabela <strong>RH_T_PEDIDO</strong></p>
<ul>
<li><p>TIPO_PEDIDO = <strong>DOMAIN</strong> = TIPO_PEDIDO ,
<strong>VALOR</strong> = <strong>DISPENSA</strong></p></li>
<li><p>ORIGEM = RH</p></li>
<li><p>ESTADO = <strong>‘P’</strong></p></li>
<li><p>FUN_ID</p></li>
<li><p>DATA_PEDIDO = <strong>SYSDATE</strong></p></li>
<li><p>DATA_REGISTO = <strong>SYSDATE</strong></p></li>
<li><p>USER_REGISTO_ID = user_logado</p></li>
<li><p>USER_ REGISTO_NAME = nome utilizador</p></li>
</ul></li>
<li><p>Grava na tabela <strong>RH_T_DISPENSA</strong></p></li>
</ol>
<ol type="a">
<li><p><em>TIPREL_ID = ID de RH_T_TIPOS_RELACIONAMENTO</em></p></li>
<li><p><em>DATA_REGISTO</em></p></li>
<li><p><em>USER_REGISTO_ID</em></p></li>
<li><p><em>USER_REGISTO_NAME</em></p></li>
<li><p><em>PEDIDO_ID = ID DE RH_T_PEDIDO</em></p></li>
</ol>
<blockquote>
<p><mark>3<del>.Registo na Tabela
<strong>RH_T_VALIDACAO</strong></del></mark></p>
</blockquote>
<ul>
<li><p><em><mark><del>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></del></mark></em></p></li>
<li><p><mark><em>REFERENCIA_NAME <strong>=
‘</strong></em>DISPENSA<em><del><strong>’ (</strong>DOMAINS =</del>
ACCAO_REFERENTE<strong>)</strong></em></mark></p></li>
<li><p><em><mark>REFERENCIA_ID <strong>=
RH_T_PEDIDO.ID</strong></mark></em></p></li>
<li><p><em><mark>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS</strong></mark></em></p></li>
</ul></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 84%" />
</colgroup>
<thead>
<tr>
<th></th>
<th><ul>
<li><p><em><mark>TIPREL_ID <strong>= NULL</strong></mark></em></p></li>
<li><p><em><mark>DATA_REGISTO <strong>=
SYSDATE</strong></mark></em></p></li>
<li><p><em><mark><del>USER_REGISTO_NAME = nome de utilizador
Logado</del></mark></em></p></li>
<li><p><em><mark><del>USER_REGISTO_ID = id de utilizador
Logado</del></mark></em></p></li>
<li><p><em><mark>ESTADO <strong>= ‘P’</strong></mark></em></p></li>
</ul>
<blockquote>
<p><em><strong><mark>Caso a dispensa for valida, log deve registar na
tabela Ausencia</mark></strong></em></p>
<p><strong><mark>O descrição do registo na
RH_T_AUSENCIA</mark></strong></p>
</blockquote>
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
</ul></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

### <span class="mark">Licença / Baixa Medica</span> 

- PARA BAIXA, DEVE ABRIR UMA TEBELA COM OS MESES PROCESSADOS, O RH
  INTRODUZ OS VALORES DE SDO, E O SISEMA FAZ OS CALUCLOS, O RH VALIDA E
  O VALOR é introduzido SALÁRIO (SABER QUAL SERºA O TIPO DE MOVIMENTO
  PARA RETROATIVO DE BAIXA)

> 3.5.1 Licença / Lista baixa Médica
>
> <img src="media/image24.jpeg" style="width:9.68612in;height:4.265in"
> alt="Uma imagem com texto, software, número, Ícone de computador Os conteúdos gerados por IA podem estar incorretos." />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 7%" />
<col style="width: 41%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Filtro</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><blockquote>
<p>Data Inicio</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.DATA_INICIO</em></p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.DATA_FIM</em></p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Colaborador</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FUNCIONARIO.NOME</p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.FUN_ID</em></p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 7%" />
<col style="width: 41%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Direcção</p>
</blockquote></th>
<th></th>
<th><blockquote>
<p>RH_T_TIPOS_RELACIONAMENT.INSTIT_ID, do último Vínculo</p>
</blockquote></th>
<th><blockquote>
<p><em>-----------------------------------------------------------</em></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p><del>Tipo Abono e Beneficio</del></p>
<p><mark>Licença</mark></p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_PARAM_SITUACAO.NOME</p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.PARAM_SIT_ID</em></p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Lista</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Estado</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.ESTADO</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Direção</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><em>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</em></p>
<p><em>INPSSIGOF.INSTITUICOES. NOME</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Seccão</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><em>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</em></p>
<p><em>RH_T_SECCAO.NOME</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Nome</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><em>RH_T_FUNCIONARIOS.NOME</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Vínculo</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><em>RH_T_TIPOS_RELACIONAMENTO.CONTRATO_VíNCULO_ID</em></p>
<p><em>RH_T_PARAM_VíNCULO.NOME</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Categoria</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><em>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</em></p>
<p><em>RH_T_PARAM_CARGO.NOME</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><del>Tipo de Abono e Beneficio</del></p>
<p><mark>Tipo Licença</mark></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_PARAM_SITUACAO.NOME</p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.PARAM_SIT_ID</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Motivo</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS. PARAM_SIT_DET_ID</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data início</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.DATA_INICIO</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.DATA_FIM</em></p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>REGRAS</strong></p>
</blockquote></td>
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
<th colspan="2"></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><blockquote>
<p><strong>ACÇÕES</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Editar</p>
</blockquote></td>
<td><blockquote>
<p>Abre o mesmo formulário de registo</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Continuidade</p>
</blockquote></td>
<td><blockquote>
<p>Permite Registar Conituidade de Licença. Ou seja, em cima de mesma
licen</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Regularização de contas</p>
</blockquote></td>
<td><blockquote>
<p>Abre um formulario que pemite ajustar os valos pagos pelo RH e SDO
referente baixa medica de um colaborador</p>
</blockquote></td>
</tr>
</tbody>
</table>

#### <span class="mark">Novo / Editar</span>

![](media/image25.png)

> <img src="media/image28.jpeg"
> style="width:9.60801in;height:5.37396in" />

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Formulário</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Gravação</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Validar</p>
</blockquote></th>
<th></th>
<th><blockquote>
<p><strong>Fica visivel somente na validacao</strong></p>
</blockquote></th>
<th></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p><del>*Tipo Abono Benefício</del></p>
<p><mark>*Tipo Licença</mark></p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td><blockquote>
<p><strong>RH_T_PARAM_SITUACAO</strong>.NOME</p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.PARAM_SIT_ID RH_T_FALTA.
PARAM_SIT_ID</em></p>
<p><em>RH_T_AUSENCIA. PARAM_SIT_ID</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Motivo</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td><blockquote>
<p><strong>RH_T_PARAM_SITUACAO_DET.NOME</strong></p>
<p>Motivo não é obrigatário</p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS. PARAM_SIT_DET_ID</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Observação</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXTAREA</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS OBS</em></p>
<p><em>RH_T_AUSENCIA.OBS</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Dias Total Licença</p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>Periodo Licença</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><mark>Continuidade</mark></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><strong><mark>DOMINIO = SIM_NAO</mark></strong></p>
</blockquote></td>
<td><blockquote>
<p><em><mark>RH_T_ABONOS_BENEFICIOS_DET.FLG_CONTINUIDADE</mark></em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>*Data Inicio Licença</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS_DET.DATA_INICIO</em></p>
<p><em>RH_T_AUSENCIA.DATA_INICIO</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Fim Licença</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS_DET.DATA_FIM</em></p>
<p><em>RH_T_AUSENCIA. DATA_FIM</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><del>Data Inicio Falta</del></p>
</blockquote></td>
<td><blockquote>
<p><em><del>DATE</del></em></p>
</blockquote></td>
<td><blockquote>
<p><del>Caso essa informação não for nula, logo deva enviar essa</del>
<del>informação como parâmetro no Procedimento</del></p>
</blockquote></td>
<td><blockquote>
<p><em><del>-------------------------------</del></em></p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Dias Total Licença</p>
</blockquote></th>
<th><blockquote>
<p><em>NUMBER</em></p>
</blockquote></th>
<th><blockquote>
<p>Contar número de dias entre <strong>Data Inicio de Licença</strong> e
<strong>Data Fim Licença</strong></p>
</blockquote></th>
<th></th>
</tr>
</thead>
<tbody>
<tr>
<td></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>----------------------------</p>
</blockquote></td>
<td><blockquote>
<p><em>HIDDEN</em></p>
</blockquote></td>
<td><blockquote>
<p>-------------------------------------------------------</p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_ABONOS_BENEFICIOS.FUN_ID</em></p>
<p><em>RH_T_FALTA.TIPREL_ID RH_T_AUSENCIA.FUN_ID</em></p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>Informações Definidas No Regulamento</strong></p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>RH_PROCESSAMENTO_SALARIAL_DB.</strong>CALCULO_FALTA_LICENCA<strong>(P_TIPREL_ID</strong>
=: ID de RH_T_TIPOS_RELACIONAMENTO onde EST_ACT_ADM = 1,</p>
<p><strong>P_DATA_INICIO</strong> =: Data Inicio Licença,
<strong>P_DATA_FIM</strong>:= Data Fim Licença,
<strong>P_TIPO_LICENCA</strong>:= Tipo Licença,</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Desconto Sobre</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>p_desc_sobre</em></p>
</blockquote></td>
<td><blockquote>
<p><em><del>-----------------------------------------------</del></em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Dias Direito Licença</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>p_dias_Direito</em></p>
</blockquote></td>
<td><blockquote>
<p><em><del>------------------------------------------------</del></em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Dias Descontado por RH</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>p_dias_desc_rh</em></p>
</blockquote></td>
<td><blockquote>
<p><em><del>-----------------------------------------------</del></em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Dias não descontado por RH</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>p_dias_ndesc_rh</em></p>
</blockquote></td>
<td><blockquote>
<p><em><del>----------------------------------------------</del></em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><strong>Falta Mensal</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Mês</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><em>p_meses</em></p>
</blockquote></td>
<td><blockquote>
<p><em>-----------------------------------------------------</em></p>
</blockquote></td>
</tr>
</tbody>
</table>

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
<th><blockquote>
<p>Data Inicio</p>
</blockquote></th>
<th></th>
<th colspan="2"><blockquote>
<p><em>p_data_ini_falta</em></p>
</blockquote></th>
<th><blockquote>
<p><em>RH_T_FALTA.DATA_INICIO</em></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td></td>
<td colspan="2"><blockquote>
<p><em>p_data_fim_falta</em></p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_FALTA.DATA_FIM</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Dias Falta Mensal</p>
</blockquote></td>
<td></td>
<td colspan="2"><blockquote>
<p><em>p_dias_falta</em></p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_FALTA.AUSENCIA</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Salário Base</p>
</blockquote></td>
<td></td>
<td colspan="2"><blockquote>
<p><em>p_valor_salario</em></p>
</blockquote></td>
<td><blockquote>
<p><em>-----------------------------</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Valor Descontado</p>
</blockquote></td>
<td></td>
<td colspan="2"><blockquote>
<p><em>p_valor_desc</em></p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_FALTA.VALOR</em></p>
</blockquote></td>
</tr>
<tr>
<td></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td colspan="5"><blockquote>
<p><strong>Anexar Documento Comprovativo</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Tipo Documento</p>
</blockquote></td>
<td></td>
<td colspan="2"></td>
<td rowspan="2"><blockquote>
<p>RH_T_DOCUMENTO.<em>DOC_ID</em></p>
<p>RH_T_DOCUMENTO.TIPO_DOCUMETO = ID DE TABELA RH_T_TIPO_DOCUMENTO ONDE
REFERENCIA = ‘JUSTIFICACAO_FALTA’</p>
<p>REFERENCIA_NAME = ‘<em>RH_T_ABONOS_BENEFICIOS</em></p>
<p>REFERENCIA_ID = id de <em>RH_T_ABONOS_BENEFICIOS</em></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Documento</p>
</blockquote></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="5"><blockquote>
<p><em><strong>OUTRAS GRAVAÇÕES</strong></em></p>
</blockquote></td>
</tr>
<tr>
<td colspan="3"><blockquote>
<p><em>2-insert em <strong>RH_T_ABONOS_BENEFICIOS</strong></em></p>
</blockquote>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em>ESTADO = “<strong>P</strong>”</em></p></li>
<li><p><em>FUN_ID = id de RH_T_FUNCIONARIOS</em></p></li>
</ul></td>
<td colspan="2"><ol start="3" type="1">
<li><p><em><mark>Registar na tabela
<strong>RH_T_FALTA</strong></mark></em></p>
<ul>
<li><p><em><mark>DATA_INICIO:</mark></em></p></li>
<li><p><em><mark>DATA_FIM:</mark></em></p></li>
<li><p><em><mark>ABONO_BENEF_ID = ID DE
<strong>RH_T_ABONOS_BENEFICIOS_DET’</strong></mark></em></p></li>
<li><p><em>PARAM_SIT_ID</em></p></li>
<li><p><em>FLG_DESCONTO_SAL</em></p></li>
<li><p><em>FLG_JUSTIFICATIVO: deve se</em></p></li>
<li><p><em><mark>ESTADO</mark></em></p></li>
<li><p><em>TIPREL_ID</em></p></li>
</ul></li>
</ol></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr>
<th><ol type="1">
<li><p><em><mark>insert em
<strong>RH_T_ABONOS_BENEFICIOS_DET</strong></mark></em></p>
<ul>
<li><p><em>DATA_REGISTO= ‘<strong>SYSDATE’</strong></em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = <strong>NULL</strong></em></p></li>
<li><p><em>USER_ALTERACAO_NAME = <strong>NULL</strong></em></p></li>
<li><p><em>DATA_ALTERACAO = <strong>NULL</strong></em></p></li>
<li><p><em><strong>ESTADO</strong> = “<strong>P</strong>”</em></p></li>
<li><p><em><strong>ABONO_BENEF_ID</strong> = id de tabela
<strong>RH_T_ABONOS_BENEFICIOS</strong></em></p></li>
</ul></li>
</ol>
<blockquote>
<p><em>4-Registo em <strong>RH_T_AVALIACAO,
RH_T_VALIDACAO_DETALHE</strong></em></p>
</blockquote></th>
<th><ol type="1">
<li><p><em>Caso o tipo de ausência é considerado uma ausência. (ou seja
verifica na tabela RH_T_PARAM_SITUACAO.FLG_AUSENCIA = 1). Logo regista
na tabela <strong>RH_T_AUSENCIA</strong></em></p>
<ul>
<li><p><em>PARAM_SIT_ID = ID de RH_T_PARAM_SITUACAO</em></p></li>
<li><p><em>DATA_INICIO = Data inicio</em></p></li>
<li><p><em>DATA_FIM = Data Fim</em></p></li>
<li><p><em>REFERENCIA_ID = id de
<strong>RH_T_ABONOS_BENEFICIOS</strong></em></p></li>
<li><p><em><mark>REFERENCIA_NAME =
‘<strong>RH_T_ABONOS_BENEFICIOS_DET’</strong></mark></em></p></li>
<li><p><em>FUN_ID</em></p></li>
</ul></li>
</ol></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

##### Validação

- A validação invoca a mesma página de Registo

- O campo validar deve ficar visível

- Ao **validar**, devem ser atualizadas todas as tabelas associadas,
  definindo o campo **estado = 'A'**.

- Ao **desvalidar**, devem ser atualizadas todas as tabelas associadas,
  definindo o campo **estado = 'I'**.

- Caso o utilizador **atualize algum campo no formulário**, a alteração
  deve ser **refletida na tabela correspondente**.

#### Regularização de Contas

![](media/image29.jpeg)

<table>
<colgroup>
<col style="width: 14%" />
<col style="width: 12%" />
<col style="width: 32%" />
<col style="width: 41%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Lista</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><p><strong>Gravação</strong></p></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><blockquote>
<p>Meses</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_REGULARIZACAO_SDO.MES_REFERENTE</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Valor Liquido Recevido</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_PROC_FUNCIONARIOS. TOT_LIQUIDO</p>
</blockquote></td>
<td><blockquote>
<p>-------------------------------</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>SDO Recebido</p>
</blockquote></td>
<td><blockquote>
<p><em>NUMBER</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_REGULARIZACAO_SDO.SDO_RECEBIDO</p>
</blockquote></td>
</tr>
</tbody>
</table>

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
<th><blockquote>
<p>Retroativo Salário</p>
</blockquote></th>
<th></th>
<th colspan="2"></th>
<th><blockquote>
<p>RH_T_REGULARIZACAO_SDO.VALOR_RETROATIVO_SALARIO</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Retroativo SDO</p>
</blockquote></td>
<td></td>
<td colspan="2"></td>
<td><blockquote>
<p>RH_T_REGULARIZACAO_SDO.VALOR_RETROATIVO_SDO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Subsidio fiscal Recebido</p>
</blockquote></td>
<td></td>
<td colspan="2"><blockquote>
<p>RH_T_PROC_FUNCIONARIOS.</p>
<p>.TOT_REMUN_COLLECT</p>
</blockquote></td>
<td><blockquote>
<p>-------------------------------------</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Salario Liquido + Subsidio Fiscal</p>
</blockquote></td>
<td></td>
<td colspan="2"><blockquote>
<p>RH_T_PROC_FUNCIONARIOS.TOT_LIQUIDO + RH_T_PROC_FUNCIONARIOS.
.TOT_REMUN_COLLECT</p>
</blockquote></td>
<td><blockquote>
<p>----------------------------------------------</p>
</blockquote></td>
</tr>
<tr>
<td></td>
<td></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>TOTAL RECEBIDO</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td colspan="2"><blockquote>
<p>Somatoria entre o valor líquido recebido +SDO recebido</p>
</blockquote></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>TOTAL DIREITO</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td colspan="2"><blockquote>
<p>Somatoria entre o valor líquido recebido + retroativo salário –
retroativo SDO</p>
</blockquote></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>DIFERENÇA POR EXPEDIENTE</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td colspan="2"><blockquote>
<p>retroativo salário + retroativo salário</p>
</blockquote></td>
<td><blockquote>
<p>??</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Validar</p>
</blockquote></td>
<td><blockquote>
<p><em>RADIOLIST</em></p>
</blockquote></td>
<td colspan="2"><blockquote>
<p>Só fica visivel ao validar salário</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_REGULARIZACAO_SDO.ESTADO</p>
</blockquote></td>
</tr>
<tr>
<td colspan="5"><blockquote>
<p><em>OUTRAS GRAVAÇÕES</em></p>
</blockquote></td>
</tr>
<tr>
<td colspan="3"><blockquote>
<p><em>1.2 Fazer uma nova gravação na tabela de</em>
<strong>RH_T_REGULARIZACAO_SDO</strong></p>
</blockquote>
<ul>
<li><p><em>DATA_REGISTO= ‘SYSDATE’</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_ALTERACAO _ID = NULL USER_ALTERACAO_NAME =
NULL</em></p></li>
<li><p><em>DATA_ALTERACAO = NULL</em></p></li>
<li><p><em>ESTADO = ‘P’</em></p></li>
<li><p><em>PROC_FUN_ID = ID DE
<strong>RH_T_PROC_FUNCIONARIOS</strong></em></p></li>
<li><p><em>SITUACAO_LABORAL_ID = ID DE
<strong>RH_T_SITUACAO_LABORAL</strong></em></p></li>
</ul></td>
<td colspan="2"><ol type="1">
<li><p><em>Registo na tabela de validação
<strong>RH_T_VALIDACAO</strong></em></p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘REGULARIZAÇÃO’ (</strong>DOMAINS =
ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= ID</strong> de tabela
<strong>RH_T_REGULARIZACAO</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= ID de
RH_T_TIPOS_RELACIONAMENTO</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></li>
</ol></td>
</tr>
</tbody>
</table>

##### Validação

- A validação invoca a mesma página de Registo

- O campo validar deve ficar visível

- Ao **validar**, devem ser atualizadas todas as tabelas associadas,
  definindo o campo **estado = 'A'**.

- Ao **desvalidar**, devem ser atualizadas todas as tabelas associadas,
  definindo o campo **estado = 'I'**.

- Caso o utilizador **atualize algum campo no formulário**, a alteração
  deve ser **refletida na tabela correspondente**.

- Ao clicar no botão validar deve ser efetuado as seguintes ações,

  - caso o colaborador tenha valor a receber por parte do RH

    - Registo na tabela **RH_T_DEF_REMUNERACOES** (o valor do
      retroativo), para isso é necessário definir qual o TM_ID

    - Registo na tabela de associação **RH_T_TIPREL_REM_PAG**

  - Caso o colaborador tem a pagar

    - Registo na tabela **RH_T_DEF_REMUNERACOES** (o valor do retroativo
      negativo), para isso é necessário definir qual o TM_ID

    - Registo na tabela de associação **RH_T_TIPREL_REM_PAG**

### Hora Extra

#### Lista Hora Extra

![](media/image30.jpeg)

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 6%" />
<col style="width: 36%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Filtro</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><p><strong>Fonte dados</strong></p></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 6%" />
<col style="width: 36%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Colaborador</p>
</blockquote></th>
<th><blockquote>
<p><em>TEXT</em></p>
</blockquote></th>
<th><blockquote>
<p>RH_T_FUNCIONARIOS.NOME</p>
</blockquote></th>
<th><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.FUN_ID</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Ilha</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>SILGGLOBAL.GLB_GEOGRAFIA,RH_T_PARAM_LOCAL_TRAB.ILHA_ID</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Direção</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td><blockquote>
<p>INPSSIGOF.INSTITUICOES.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Seccão</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td><blockquote>
<p><em>RH_T_PARAM_SECAO.NOME</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Inicio</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.DATA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.DATA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Estado</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.ESTADO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Lista</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></td>
<td style="text-align: center;"><p><strong>Fonte dados</strong></p></td>
</tr>
<tr>
<td><blockquote>
<p>Estado</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.ESTADO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Direção</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>INPSSIGOF.INSTITUICOES.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Secção</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Nome Colaborador</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FUNCIONARIOS.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.TIPREL_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Inicio / Data Fim</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.DATA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Hora Contratada (Diário / Mensal)</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.HORAS_DIARIAS/ RH_T_HORA_EXTRA.HORAS_DIARIASX 12</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Hora de Trabalho</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>SUBSTR(TO_CHAR(<strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong>.HORAS_EXTRAS),
4, 8);</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Salario Mensal</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.SALARIO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Valor Horas Mensal</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.VALOR_DIARIO X 12</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Valor Horas Diário</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.VALOR_DIARIO</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 16%" />
<col style="width: 6%" />
<col style="width: 36%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Percentagem</p>
</blockquote></th>
<th></th>
<th></th>
<th><blockquote>
<p>RH_T_HORA_EXTRA.PERCENTAGEM</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Acções</strong></p>
</blockquote></td>
<td colspan="3"></td>
</tr>
<tr>
<td><blockquote>
<p>Nova</p>
</blockquote></td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

#### Novo Hora Extra

<img src="media/image31.jpeg" style="width:9.15939in;height:2.91in" />

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Formulario</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Gravação</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="4"></td>
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
<th><blockquote>
<p>Validar</p>
</blockquote></th>
<th><blockquote>
<p>RADIOLIST</p>
</blockquote></th>
<th><blockquote>
<p>Campo só aparece no momento de validação</p>
</blockquote></th>
<th></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Direção</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>----------------------------------</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Colaborador</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.TIPREL_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data inicio</p>
</blockquote></td>
<td><blockquote>
<p>DATE</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.DATA_INICIO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Hora Fim</p>
</blockquote></td>
<td><blockquote>
<p>DATE</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.DATA_FIM</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Horas diárias</p>
</blockquote></td>
<td><blockquote>
<p>DATE</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.HORAS_DIARIAS</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Percentagem Referente</p>
</blockquote></td>
<td><blockquote>
<p>Select</p>
</blockquote></td>
<td><blockquote>
<p>DOMAINS = <strong>DIAS_PERCENTAGEM_HORA</strong></p>
</blockquote>
<ul>
<li><p>Dias Úteis</p></li>
<li><p>Dias Uteis e Não Úteis</p></li>
<li><p>Dias Não Úteis</p></li>
</ul></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.PERCENTAGEM</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Valor Diário</p>
</blockquote></td>
<td><blockquote>
<p>DISABLED</p>
</blockquote></td>
<td><blockquote>
<p>FAZ O CALCULO DO VALOR DE HORA EXTRA</p>
<p><strong>RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_HORA_EXTRA</strong>
(P_TIPREL_ID NUMBER,P_DATA_INICIO DATE,P_DATA_FIM DATE,P_DIAS_APLICADA
VARCHAR2, P_HORAS_DIARIA NUMBER)</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_HORA_EXTRA.VALOR_DIARIO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Anexar Documento</p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Tipo documento</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_DOCUMENTO.NOME onde a Referência =
‘<strong>HORA_EXTRA</strong>’</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_DOCUMENTO.TP_DOCUMENTO_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Documento</p>
</blockquote></td>
<td><blockquote>
<p>UPLOAD</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_DOCUMENTO.DOC_ID</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 84%" />
</colgroup>
<thead>
<tr>
<th colspan="2"><blockquote>
<p><strong>Ações</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p><img src="media/image17.png" style="width:0.63911in;height:0.27052in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p>
</blockquote></td>
<td><ol type="1">
<li><p>Gravação na tabela RH_T_HORA_EXTRA</p>
<ul>
<li><p>PEDIDO_ID = id de RH_T_PEDIDO</p></li>
<li><p>TIPREL_ID = id de RH_T_TIPOS_RELACIONAMENTO</p></li>
<li><p>SINTESE_DIARIO_ID = pega o ID de registo em síntese referente a
DATA_INICIO e DATA_FIM</p></li>
<li><p>VALOR_DIARIO (CALCULA O VALOR DIARIA DE HORA EXTRA)</p></li>
</ul></li>
<li><p>Gravação na Tabela RH_T_VALIDACAO</p>
<ul>
<li><p><em>TIPO_ACCAO<strong>= ‘INSERT ’ (</strong>DOMAINS =
TIPO_ACAO<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_NAME <strong>= ‘</strong>HORA_EXTRA<strong>’
(</strong>DOMAINS = ACCAO_REFERENTE<strong>)</strong></em></p></li>
<li><p><em>REFERENCIA_ID <strong>= RH_T_PEDIDO.ID</strong></em></p></li>
<li><p><em>FUN_ID <strong>= ID</strong> de tabela
<strong>RH_T_FUNCIONARIOS</strong></em></p></li>
<li><p><em>TIPREL_ID <strong>= NULL</strong></em></p></li>
<li><p><em>DATA_REGISTO <strong>= SYSDATE</strong></em></p></li>
<li><p><em>USER_REGISTO_NAME = nome de utilizador Logado</em></p></li>
<li><p><em>USER_REGISTO_ID = id de utilizador Logado</em></p></li>
<li><p><em>ESTADO <strong>= ‘P’</strong></em></p></li>
</ul></li>
</ol></td>
</tr>
</tbody>
</table>

###### Exemplo:

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 13%" />
<col style="width: 8%" />
<col style="width: 11%" />
<col style="width: 10%" />
<col style="width: 13%" />
<col style="width: 14%" />
<col style="width: 14%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Data início</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Data fim</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Mês</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Dias úteis</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Dias não úteis</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>% aplicado</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Valor diário (hora extra)</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Valor acumulado no mês</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 13%" />
<col style="width: 13%" />
<col style="width: 11%" />
<col style="width: 8%" />
<col style="width: 8%" />
<col style="width: 13%" />
<col style="width: 15%" />
<col style="width: 15%" />
</colgroup>
<thead>
<tr>
<th><p><strong>20/01/2026</strong></p></th>
<th><blockquote>
<p>31/01/2026</p>
</blockquote></th>
<th><p>202601</p></th>
<th><p>9</p></th>
<th><p>3</p></th>
<th><blockquote>
<p>U=50% / N=100%</p>
</blockquote></th>
<th><blockquote>
<p>U=1.111,11 N=2.222,22</p>
</blockquote></th>
<th><blockquote>
<p>14.999,98</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><p>01/02/2026</p></td>
<td><blockquote>
<p>28/02/2026</p>
</blockquote></td>
<td><p>202602</p></td>
<td><p>20</p></td>
<td><p>8</p></td>
<td><blockquote>
<p>U=50% / N=100%</p>
</blockquote></td>
<td><blockquote>
<p>U=1.111,11 N=2.222,22</p>
</blockquote></td>
<td><blockquote>
<p>31.111,08</p>
</blockquote></td>
</tr>
<tr>
<td><p>01/03/2026</p></td>
<td><blockquote>
<p><strong>10/03/2026</strong></p>
</blockquote></td>
<td><p>202603</p></td>
<td><p>8</p></td>
<td><p>2</p></td>
<td><blockquote>
<p>U=50% / N=100%</p>
</blockquote></td>
<td><blockquote>
<p>U=1.111,11 N=2.222,22</p>
</blockquote></td>
<td><blockquote>
<p>11.111,10</p>
</blockquote></td>
</tr>
</tbody>
</table>

##### Validação

> Apôs Validação,

- Atualiza os estado pendentes para ativo

- Deve registar no **RH_T_DEF_REMUNERACOES** o registo DE HORA EXTRA

### Ferias

<img src="media/image32.png" style="width:4.68974in;height:1.045in"
alt="Uma imagem com texto, Tipo de letra, branco, captura de ecrã Os conteúdos gerados por IA podem estar incorretos." />

#### Lista Ferias

![](media/image33.jpeg)

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 5%" />
<col style="width: 36%" />
<col style="width: 41%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Filtro</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><blockquote>
<p>Ano Referente</p>
</blockquote></td>
<td><blockquote>
<p><em>S</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_ANO.ANO</p>
</blockquote></td>
<td></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 17%" />
<col style="width: 5%" />
<col style="width: 36%" />
<col style="width: 41%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Ilha</p>
</blockquote></th>
<th><blockquote>
<p><em>SELECT</em></p>
</blockquote></th>
<th></th>
<th><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Direção</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Secção</p>
</blockquote></td>
<td><blockquote>
<p><em>SELECT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.SECCAO_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Colaborador</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.FUN_ID</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Lista</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></td>
<td style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Direcção</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Secção</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.SECCAO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Nome Colaborador</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FUNCIONARIOS.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS.FUN_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Vinculo</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_CONTRATO_VINCULO.TP_VINCULO_ID, RH_T_PARAM_VINCULO.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.CONTR_VINCULO_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Categoria</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.CARGO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total Direito</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><strong>SUM</strong>(RH_T_FERIAS.NUM_DIA)</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total direito (dias / ano)</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Caso não tenha dados logo apresenta 0</p>
</blockquote></td>
<td><blockquote>
<p>(RH_T_FERIAS.NUM_DIA || RH_T_FERIAS.ANO_REFERENTE )</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total Planeado (Data Inicio / data fim)</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Caso não tenha dados logo apresenta 0</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_MAPA NUM_DIA (RH_T_FERIAS_MAPA.DATA_INICIO</p>
<p>|| RH_T_FERIAS_MAPA.DATA_FIM)</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total Gozado (Data Inicio / Data Fim )</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>Caso não tenha dados logo apresenta 0</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_GOZADA.NUM_DIA
(RH_T_FERIAS_GOZADA.DATA_INICIO||RH_T_FERIAS_GOZADA.DATA_FIM)</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Acções</strong></p>
</blockquote></td>
<td colspan="3"></td>
</tr>
</tbody>
</table>

<table style="width:99%;">
<colgroup>
<col style="width: 17%" />
<col style="width: 82%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><img src="media/image34.png"
style="width:1.17674in;height:0.28333in" /></p>
</blockquote></th>
<th><blockquote>
<p>Permite exportar dados de direitos a cada direção</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p><img src="media/image35.png"
style="width:0.94118in;height:0.32083in" /></p>
<p><img src="media/image36.png"
style="width:1.15001in;height:0.23333in" /></p>
</blockquote></td>
<td><blockquote>
<p>Pemite Abrir formulario para solicitar Ferias</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image37.png"
style="width:1.12082in;height:0.2625in" /></p>
</blockquote></td>
<td><blockquote>
<p>Permite ver ferias agendadas e por gozar</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image38.png"
style="width:0.95331in;height:0.2275in" /></p>
</blockquote></td>
<td><blockquote>
<p>Permite enviar direito de ferias por email</p>
</blockquote></td>
</tr>
</tbody>
</table>

##### Exportar direito

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 22%" />
<col style="width: 17%" />
<col style="width: 16%" />
<col style="width: 11%" />
<col style="width: 16%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>CODIGO_DIRECAO</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>NOME_DIRECAO</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>ID_COLABORADOR</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>NOME_COLABORADOR</strong></p>
</blockquote></th>
<th
style="text-align: right;"><p><strong>TOTAL_DIREITO</strong></p></th>
<th><blockquote>
<p><strong>TOTAL_DIREITO_ANO</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>00.01.01</p>
</blockquote></td>
<td><blockquote>
<p>Direção dos Recursos Humanos</p>
</blockquote></td>
<td style="text-align: right;"><p>1223</p></td>
<td><blockquote>
<p>Gertrudes Helena</p>
</blockquote></td>
<td style="text-align: right;"><p>44</p></td>
<td><blockquote>
<p>22 (2023); 22 (2024)</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>00.00.01</p>
</blockquote></td>
<td><blockquote>
<p>Direção dos Recursos Humanos</p>
</blockquote></td>
<td style="text-align: right;"><p>1222</p></td>
<td><blockquote>
<p>Manuela gomes</p>
</blockquote></td>
<td style="text-align: right;"><p>27</p></td>
<td><blockquote>
<p>5 (2023); 22 (2024)</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 45%" />
<col style="width: 54%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Coluna Excel</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><blockquote>
<p>CODIGO_DIRECAO</p>
</blockquote></td>
<td><blockquote>
<p>INPSSIGOF.INSTITUICAO.CODIGO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>NOME_DIRECAO</p>
</blockquote></td>
<td><blockquote>
<p>INPSSIGOF.INSTITUICAO.NOME</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>ID_COLABORADOR</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FUNCIONARIOS.ID_COLABORADOR</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>NOME_COLABORADOR</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FUNCIONARIOS.NOME</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 45%" />
<col style="width: 54%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>TOTAL_DIREITO</p>
</blockquote></th>
<th><blockquote>
<p>SUM(RH_T_FERIAS.NUM_DIA)</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>TOTAL_DIREITO_ANO</p>
</blockquote></td>
<td><blockquote>
<p>AGRUPAR (RH_T_FERIAS. NUM_DIA)|| RH_T_FERIAS.ANO_REFERENTE)</p>
</blockquote></td>
</tr>
</tbody>
</table>

#### Pedido Ferias / Alteração de ferias

![](media/image39.jpeg)

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 7%" />
<col style="width: 37%" />
<col style="width: 39%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Formulario</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Gravação</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="4"><blockquote>
<p><strong>Dados Solicitação</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Nome colaborador</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p>Nome do colaborador que entrara de ferias</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.FUN_ID</p>
<p>RH_T_PEDIDO.FUN_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Ferias Por Gozar</p>
</blockquote></td>
<td><blockquote>
<p>deabled</p>
</blockquote></td>
<td><blockquote>
<p>Verifica se o numero de ferias que ainda falta o colaborador Gozar ,
referente ao mesmo ano corrente</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS.NUM_DIA – RH_T_FERIAS_GOZADAS.NUM_DIA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Inicio</p>
</blockquote></td>
<td><blockquote>
<p>DATE</p>
</blockquote></td>
<td><blockquote>
<p>Data inicio do colaborador</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.DATA_INICIO</p>
<p>RH_T_SUBSTITUICAO.DATA_INICIO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td><blockquote>
<p>DATE</p>
</blockquote></td>
<td><blockquote>
<p>Data fim do colaborador</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.DATA_FIM</p>
<p>RH_T_SUBSTITUICAO.DATA_FIM</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Numero dias</p>
</blockquote></td>
<td><blockquote>
<p>NUMBER</p>
</blockquote></td>
<td><blockquote>
<p>Numero de dias de ferias do colaborador</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.NUM_DIA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Substituído Por:</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p>Identificação do colaborador que substituirá o colaborador de
férias</p>
</blockquote></td>
<td><blockquote>
<p><mark>RH_T_SUBSTITUICAO.SUSBSTITUTO_ID</mark></p>
<p><mark>RH_T_SUBSTITUICAO.SUBSTITUTO_ID</mark></p>
</blockquote></td>
</tr>
<tr>
<td colspan="4"><blockquote>
<p><strong>Alteração De Data Ferias (este separador só aparece em caso
de edição)</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Alteração Ferias</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>faz novo registo com novo registo o id de rh_t_ferias_gozada que deu
origem a essa alteração</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_GOZADA.TIPO_ALTERACAO</p>
<p>RH_T_FERIA_GOZADAS.FERIAS_GOZADA_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Nova Data Fim</p>
</blockquote></td>
<td><blockquote>
<p>DATE</p>
</blockquote></td>
<td><blockquote>
<p>Nova data de ferias</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_GOZADAS.DATA_FIM</p>
</blockquote></td>
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
<th><blockquote>
<p>Motivo</p>
</blockquote></th>
<th><blockquote>
<p>TEXTAREA</p>
</blockquote></th>
<th></th>
<th><blockquote>
<p>RH_T_FERIAS_GOZADAS.MOTIVO_ALTERACAO</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="4"><blockquote>
<p><strong>Informaçoes Sobre conveniêncoa</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Observação</p>
</blockquote></td>
<td><blockquote>
<p>TEXTAREA</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.OBS_INFO_CONVENIENCIA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><strong>Analise Responsável</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Parecer Responsável</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p><strong>DOMAIN</strong> = PARECER_DECISAO, REFERENCIA =
PARECER_RESPONSAVEL</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.DECISAO_RESPONSAVEL</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Responsavel</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p>Pegar da Tabela <strong>RH_T_RESPONSAVEL.FUN_ID</strong> ,
<strong>RH_T_FUNCIONARIO</strong>.ID</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.RESPONSAVEL_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Observação</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.OBS_RESPONSAVEL</p>
</blockquote></td>
</tr>
<tr>
<td></td>
<td><blockquote>
<p>HIDDEN</p>
</blockquote></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p><strong>Validação RH</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Valida</p>
</blockquote></td>
<td><blockquote>
<p>RADIOLIST</p>
</blockquote></td>
<td><blockquote>
<p>Regista o parecer do RH</p>
<p>DOMAIN = SIM_NAO</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.DECISAO_RH</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Observação</p>
</blockquote></td>
<td><blockquote>
<p>TEXTAREA</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FERIA_GOZADAS.OBS_RH</p>
</blockquote></td>
</tr>
<tr>
<td></td>
<td><blockquote>
<p>HIDDEN</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_PEDIDO.TIPO = ‘FERIAS’</p>
</blockquote></td>
</tr>
<tr>
<td></td>
<td><blockquote>
<p>HIDDEN</p>
</blockquote></td>
<td></td>
<td></td>
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
<th><blockquote>
<p><strong>Anexar Documento</strong></p>
</blockquote></th>
<th></th>
<th></th>
<th></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Tipo documento</p>
</blockquote></td>
<td><blockquote>
<p>SELECT</p>
</blockquote></td>
<td><blockquote>
<p>Traz somente tipo documento cujo referencia = ´FERIAS´</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_DOCUMENTO.TP_DOCUMENTO_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Documento</p>
</blockquote></td>
<td><blockquote>
<p>UPLOAD</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_DOCUMENTO.DOC_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>REGRA</p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="4"><ol type="1">
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
<td colspan="4"><blockquote>
<p><strong><mark>O descrição do registo na
RH_T_AUSENCIA</mark></strong></p>
</blockquote>
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
</ul></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 15%" />
<col style="width: 42%" />
<col style="width: 42%" />
</colgroup>
<thead>
<tr>
<th colspan="3"><ul>
<li><p>USER_REGISTO_NAME = nome de utilizador logado</p></li>
</ul></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="3"><blockquote>
<p><strong>Ações</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image17.png" style="width:0.64309in;height:0.27052in"
alt="Uma imagem com Tipo de letra, texto, captura de ecrã, logótipo Os conteúdos gerados por IA podem estar incorretos." /></p>
</blockquote></td>
<td><blockquote>
<p>Novo</p>
<p>1. Gravação na tabela <strong>RH_T_PEDIDO</strong></p>
</blockquote>
<ol start="2" type="1">
<li><p>Gravação na tabela <strong>RH_T_FERIA_GOZADAS</strong></p></li>
<li><p>Gravação na tabela <strong>RH_T_SUBSTITUICAO</strong></p></li>
<li><p>Gravação na <strong>RH_T_AUSENCIA</strong></p></li>
<li><p>regista na tabela <strong>RH_T_DCUMENTO</strong></p></li>
</ol></td>
<td><blockquote>
<p>Alteração</p>
</blockquote>
<ol type="1">
<li><p>Inativa RH_T_PEDIDO ANTERIOR</p></li>
<li><p>Gravação na tabela <strong>RH_T_PEDIDO</strong></p></li>
<li><p>Gravação na tabela <strong>RH_T_FERIA_GOZADAS</strong></p></li>
<li><p><mark>Gravação na tabela <strong>RH_T_SUBSTITUICAO</strong> (caso
for substituído)</mark></p></li>
<li><p><mark>Gravação na <strong>RH_T_AUSENCIA (</strong>caso alterar a
data<strong>)</strong></mark></p></li>
</ol>
<blockquote>
<p>5 regista na tabela <strong>RH_T_DCUMENTO</strong></p>
</blockquote></td>
</tr>
</tbody>
</table>

##### Validação

> Apôs validação o colaborador e recebe uma notificação , confirmando os
> dias que tem de ferias e quanto dias mais falta por gozar.

- Envia Email e Regista na tabela **RH_T_NOTIFICACAO**

#### Ver Mapa

![](media/image40.jpeg)

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 33%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p><strong>Nome colaborador</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Ferias Agendadas</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Ferias Agendadas</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>RH_T_FUNCIONARIO.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_MAPADATA_INICIO</p>
<p>RH_T_FERIAS_MAPA.DATA_FIM</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_GOZADA.DATA_INICIO</p>
<p>RH_T_MAPA_FERIAS.DATA_FIM</p>
</blockquote></td>
</tr>
</tbody>
</table>

#### Enviar Direito

> Permite Enviar email dos direitos de ferias para o colaborador e
> Responsável.

- Pega os dados (Assunto , corpo) de Notificação a partir da tabela
  **RH_T_PARAM_NOTIFICACAO**

- Guarda dados de notificação na tabela **RH_T_NOTIFICACAO**

#### Mapa de Ferias

![](media/image41.jpeg)

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Filtro</strong></p>
</blockquote></th>
<th></th>
<th style="text-align: center;"><p><strong>Descrição</strong></p></th>
<th style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Ano Referente</p>
</blockquote></th>
<th></th>
<th></th>
<th><blockquote>
<p>RH_T_FERIAS_MAPA.ANO_ID</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Ilha</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.LOCAL_TRAB_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Direção</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_TIPOS_RELACIONAMENTO.INSTIT_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Secção</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_SECAO.ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Estado</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p><strong>DOMAIN =</strong> ESTADO_VALIDACAO</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_MAPA.ESTADO_VALIDACAO</p>
</blockquote></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p><strong>Lista</strong></p>
</blockquote></td>
<td><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></td>
<td style="text-align: center;"><p><strong>Descrição</strong></p></td>
<td style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Estado</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>Pendente, validado</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_MAPA.ESTADO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Direcção</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>INPSSIGOF.INSTITTUICOES.NOME||</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total Colaborador</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>COUNT(<strong>RH_T_TIPOS_RELACIONAMENTO</strong>.EST_ACT_ADM = 1
AND</p>
<p><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.INSTIT_ID =</p>
<p><strong>RH_T_FERIAS_MAPA</strong>.INSTIT_ID)</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total Ferias Agendadas</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>Total Colaborador - Total Ferias Agendadas;</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_MAPA.FUN_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><strong>Acções</strong></p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image42.png"
style="width:1.14173in;height:0.24781in" /></p>
</blockquote></td>
<td colspan="3"><blockquote>
<p>Botão que permite importar mapa, caso se importar uma mapa que já
existe, log se deve <strong>inactivar</strong> o outra
(<strong>RH_T_FERIAS_MAPA</strong>.ESTADO = “A”)</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image43.png"
style="width:1.1593in;height:0.23969in" /></p>
</blockquote></td>
<td colspan="3"><blockquote>
<p>Permite exportar mapa por direção com os direitos de cada
colaborador</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p><img src="media/image44.png"
style="width:1.15427in;height:0.25in" /></p>
</blockquote></td>
<td colspan="3"></td>
</tr>
<tr>
<td><blockquote>
<p>Ver Mapa</p>
</blockquote></td>
<td colspan="3"><blockquote>
<p>Invoca a mesma mapa descrita acima (<u>VER MAPA</u>)</p>
</blockquote></td>
</tr>
</tbody>
</table>

##### Importar Mapa

> (pendente RH envia Modelo)

![](media/image45.png)

<table style="width:99%;">
<colgroup>
<col style="width: 40%" />
<col style="width: 59%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Campo</p>
</blockquote></th>
<th></th>
</tr>
</thead>
<tbody>
<tr>
<td></td>
<td></td>
</tr>
</tbody>
</table>

##### Exportar Mapa

![](media/image45.png)

##### Ver detalhe

![](media/image46.jpeg)

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><blockquote>
<p><strong>Lista</strong></p>
</blockquote></th>
<th><blockquote>
<p><strong>Tipo</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Descrição</strong></p>
</blockquote></th>
<th style="text-align: center;"><blockquote>
<p><strong>Fonte dados</strong></p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: center;"><blockquote>
<p>Ferias Agendadas</p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
<p>Nome Colaborador</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FUNCIONARIO.NOME</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS_MAPA.FUN_ID</p>
</blockquote></td>
</tr>
</tbody>
</table>

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 8%" />
<col style="width: 36%" />
<col style="width: 37%" />
</colgroup>
<thead>
<tr>
<th><blockquote>
<p>Total Direito</p>
</blockquote></th>
<th><blockquote>
<p><em>TEXT</em></p>
</blockquote></th>
<th></th>
<th><blockquote>
<p>COUNT(RH_T_FERIAS.NUM_DIA)</p>
</blockquote></th>
</tr>
</thead>
<tbody>
<tr>
<td><blockquote>
<p>Total direito por ano</p>
</blockquote></td>
<td><blockquote>
<p><em>TEXT</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>COUNT(RH_T_FERIAS.NUM_DIA) agrupando por RH_T_FERIAS.NUM_DIA</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Inicio</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FERIAS_MAPA.DATA_INICIO</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Data Fim</p>
</blockquote></td>
<td><blockquote>
<p><em>DATE</em></p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FERIAS_MAPA.DATA_FIM</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Ferias por Agendar</p>
</blockquote></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><blockquote>
<p>Nome colaborador</p>
</blockquote></td>
<td></td>
<td><blockquote>
<p>RH_T_FUNCIONARIO.NOME</p>
<p>Verifica todos colaboradores de mesma Direção (RH_T_FERIAS.INSTIT_ID)
mas que não estão nessa tabela (RH_T_FERIAS_MAPA.INSTIT_ID)</p>
</blockquote></td>
<td><blockquote>
<p>RH_T_FERIAS.INSTIT_ID</p>
<p>RH_T_FERIAS_MAPA.FUN_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total direito</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_FERIAS.NUM_ID</p>
</blockquote></td>
</tr>
<tr>
<td><blockquote>
<p>Total direito por ano</p>
</blockquote></td>
<td></td>
<td></td>
<td><blockquote>
<p>RH_T_FERIAS. NUM_ID, RH_T_FERIAS.ANO_ID</p>
</blockquote></td>
</tr>
</tbody>
</table>

# Modelo Dados

## FALTA

<img src="media/image47.png" style="width:5.4795in;height:4.59375in"
alt="Uma imagem com texto, captura de ecrã, número, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

## HORA EXTRA

## 

> <img src="media/image48.png" style="width:7.61458in;height:3.11458in"
> alt="Uma imagem com texto, captura de ecrã, diagrama, Tipo de letra Os conteúdos gerados por IA podem estar incorretos." />

## DISPENSA

## 

> <img src="media/image49.png" style="width:8.29167in;height:2.59375in" />

## FERIAS

## 

> <img src="media/image50.png" style="width:9.60835in;height:4.46302in" />

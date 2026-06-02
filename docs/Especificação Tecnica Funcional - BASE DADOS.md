<figure>
<img src="media/image1.jpeg" style="width:14.65694in;height:9.77083in"
alt="C:\Users\joelm\Desktop\Imagens\sergey-zolkin-_UeY8aTI6d0-unsplash (2).jpg" />
<figcaption><p>SIPS-RH</p></figcaption>
</figure>

**BASE DE DADOS**

# Enquadramento 

# Tabelas 

## GENERICO

<table>
<colgroup>
<col style="width: 22%" />
<col style="width: 4%" />
<col style="width: 27%" />
<col style="width: 22%" />
<col style="width: 22%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>CAMPOS</strong></th>
<th colspan="2" style="text-align: center;"><strong>TIPO</strong></th>
<th style="text-align: center;"><strong>OBRIGATORIEDADE</strong></th>
<th style="text-align: center;"><strong>RELACAO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><strong>RH_T_DOMAINS</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DOMINIO</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>VALOR</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DESCRICAO</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>REFERENCIA</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong>:</em></p>
<p><em><strong>PK</strong>: PK_DOMAINS</em></p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_DOMAINS</em></p>
<p><em><strong>TRIGGER</strong>: TRG_DOMAIN</em></p></td>
</tr>
<tr>
<td><strong>RH_T_TIPOS_DOCUMENTOS</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>REFERENCIA</em></td>
<td colspan="2"><em>VARCHAR()</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>CODIGO</em></td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NOME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td>DOMINIO = STATUS</td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT:</strong></em></p>
<ul>
<li><p><em>PK: PK_TP_DOC</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_</em>TIPOS_DOC_UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_TIPO_DOCUMENTO</em></p>
<p><em><strong>TRIGGER:</strong> TRG_TIPO_DOCUMENTO</em></p></td>
</tr>
<tr>
<td colspan="2"><strong>RH_T_PARAM_NOTIFICACAO</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>TIPO_NOTIFICACAO</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>ASSUNTO</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>CORPO</em></td>
<td><em>VARCHAR(4000)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td>DOMINIO = STATUS</td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT:</strong> PK:
PK_PARAM_NOTIF</em></p>
<p><em><strong>INDEX :</strong> IX_PARAM_NOTIF_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_PARAM_NOTIF</em></p>
<p><em><strong>TRIGGER</strong>: TRG_PARAM_NOTIF</em></p></td>
</tr>
<tr>
<td colspan="2"><em><strong>RH_T_PARAM_CONFIG</strong></em></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>TIPO_CONFIG</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td><em><strong>ALERTA OU VALIDACAO</strong></em></td>
</tr>
<tr>
<td colspan="2"><em>REFERENCIA</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>DESCRICAO</em></td>
<td><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>FLG_NOTIFICACAO</em></td>
<td><em>VARCHAR(3)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>FLG_ORDEM_SERVICO</em></td>
<td><em>VARCHAR(3)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td>DOMINIO = STATUS</td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT :</strong> PK:
PK_PARAM_CONFIG</em></p>
<p><em><strong>INDEX :</strong> IX_PARAM_CONFIG_</em>UUID
(UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_PARAM_CONFIG</em></p>
<p><em><strong>TRIGGER</strong>: TRG_PARAM_CONFIG</em></p></td>
</tr>
<tr>
<td colspan="2"><strong>RH_T_TEMPLATE_REPORT</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2">LOGOTYPO</td>
<td><em><strong>IMAGEM</strong></em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2">TITULO</td>
<td><em>VARCHAR(100)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2">SUBTITULO</td>
<td><em>VARCHAR(300)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2">CORPO</td>
<td><em>VARCHAR(1000)</em></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

##  PARAMETRIZAÇÃO

<table>
<colgroup>
<col style="width: 27%" />
<col style="width: 2%" />
<col style="width: 20%" />
<col style="width: 4%" />
<col style="width: 16%" />
<col style="width: 6%" />
<col style="width: 22%" />
</colgroup>
<thead>
<tr>
<th colspan="2" style="text-align: center;"><strong>CAMPOS</strong></th>
<th colspan="2" style="text-align: center;"><strong>TIPO</strong></th>
<th style="text-align: center;"><strong>OBRIGATORIEDADE</strong></th>
<th colspan="2"
style="text-align: center;"><strong>RELACAO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><em><strong>RH_T_PARAM_VINCULO</strong></em></td>
<td colspan="2"></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>CODIGO</em></td>
<td colspan="2"><em>VARCHAR2(20 BYTE),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NOME</em></td>
<td colspan="2"><em>VARCHAR2(100 BYTE),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>FLG_CARREIRA</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMINIO = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_SALARIO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMINIO = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_CONTRATO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMINIO = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_TEMPO_SERVICO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMINIO = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMINIO = STATUS</td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_PARAM_VINCULO (ID)</strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PARAM_VINC_</em>UUID (UUID)</p></td>
<td colspan="4"><p><em><strong>SEQUENCIA:</strong> SEQ_
<strong>PARAM_VINCULO</strong></em></p>
<p><em><strong>TRIGGER =</strong> TRG_
<strong>PARAM_VINCULO</strong></em></p></td>
</tr>
<tr>
<td colspan="2"><em><strong>RH_T_PARAM_CONTRATO</strong></em></td>
<td colspan="2"></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><strong>ID</strong></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>CODIGO</em></td>
<td colspan="2"><em>VARCHAR2(20)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NOME</em></td>
<td colspan="2"><em>VARCHAR2(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NATUREZA</em></td>
<td colspan="2"><em>VARCHAR2(50)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>FLG_RENOVAVEL</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMINIO = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>DURACAO_RENOVAVEL</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>MAX_RENOVACAO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>PRAZO_OBRIGATORIO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMINIO = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>PARAM_VINCULO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"><strong>RH_T_PARAM_VINCULO</strong>.ID</td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMINIO = STATUS</td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_PARAM_CONTRATO (ID)</strong></em></p></li>
<li><p><em>FK = FK_PARAM_CONT_VINC
<strong>(</strong>PARAM_VINCULO_ID<strong>)</strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PARAM_CONTR_</em>UUID (UUID)</p></td>
<td colspan="3"><p><em><strong>SEQUENCIA:</strong>
SEQ_PARAM_CONTRATO</em></p>
<p><em><strong>TRIGGER =</strong>
TRG_<strong>PARAM_CONTRATO</strong></em></p></td>
</tr>
<tr>
<td
colspan="7"><p><strong><del><em>RH_T_PARAM_SIT_LABORAL</em></del></strong></p>
<p><em><strong>RH_T_PARAM_SITUACAO</strong></em></p></td>
</tr>
<tr>
<td colspan="2"><strong>ID</strong></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>CODIGO</em></td>
<td colspan="2"><em>VARCHAR(20)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NOME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>CLASSIFICACAO_AREA</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMAIN = CLASSIFICACAO_SITUACAO</td>
</tr>
<tr>
<td colspan="2"><em>FLG_ESTADO_CONTRATO</em></td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMAIN = ESTADO_CONTRATO</td>
</tr>
<tr>
<td colspan="2"><em>FLG_SITUACAO_LABORAL</em></td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>FLG_ABONO_BENEFICIO</em></td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_AUSENCIA</em></td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_FALTA</em></td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>TIPO_FALTA</em></td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_FALTA_DECONTO_SAL</em></td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>TIPO_CONTAGEM_DIAS</em></td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td></td>
<td colspan="2">DOMAIN = TIPO_CONTAGEM_DIAS</td>
</tr>
<tr>
<td colspan="2"><em>NUM_DIAS_ABONOS</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NUM_DIAS_DESCONTO_RH</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NUM_DIAS_NDESCONTO_RH</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>TIPO_SITUACAO</em></td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td colspan="2">DOMAIN = SITUACAO_LABORAL</td>
</tr>
<tr>
<td colspan="2"><em>FLG_REMUNERACAO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_AFETA_CARREIRA</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_REGRESSA_CARREIRA</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_CONTA_TEMP_SERVICO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_CESSA_VINCULO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>FLG_CESSA_PROGRESSAO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMAIN = SIM_NAO_NUMBER</td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMAIN = STATUS</td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_PARAM_SITUACAO (ID)</strong></em></p></li>
<li><p><em><strong>INDEX :</strong> IX_PARAM_SITUAC_</em>UUID
(UUID)</p></li>
</ul></td>
<td colspan="3"><p><em><strong>SEQUENCIA:</strong>
SEQ_PARAM_SITUACAO</em></p>
<p><em><strong>TRIGGER =</strong> TRG_PARAM_SITUACAO</em></p></td>
</tr>
<tr>
<td
colspan="7"><p><em><strong><del>RH_T_PARAM_SIT_LABORAL_DET</del></strong></em></p>
<p><em><strong>RH_T_PARAM_SITUACAO_DET</strong></em></p></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>SITUACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SM</em></td>
<td colspan="2"><em><strong>RH_T_PARAM_SITUACAO</strong>.ID</em></td>
</tr>
<tr>
<td colspan="2"><em>MOTIVO</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em><strong>PK</strong> <strong>=»</strong> PK_SITUACAO_DET
(ID)</em></p></li>
<li><p><em><strong>FK = FK_SITDET_SITUACAO
(</strong>SITUACAO_ID<strong>)</strong></em></p></li>
<li><p><em><strong>INDEX :</strong> IX_SITU_DET_</em>UUID
(UUID)</p></li>
</ul></td>
<td colspan="3"><p><em><strong>SEQUENCIA:</strong>
SEQ_PARAM_SITUACAO_DET</em></p>
<p><em><strong>TRIGGER =</strong> TRG_PARAM_SITUACAO_DET</em></p></td>
</tr>
<tr>
<td colspan="2"><em><strong>RH_T_PARAM_PCCS</strong></em></td>
<td colspan="2"></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_INICIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>OBS</em></td>
<td colspan="2"><em>VARCHAR2(500),</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR (200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR (200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR (100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_PARAM_PCCS (ID)</strong></em></p></li>
<li><p><em><strong>INDEX:</strong> IX_PARAM_PCCS_</em>UUID
(UUID)</p></li>
</ul></td>
<td colspan="3"><p><em><strong>SEQUENCIA:</strong>
SEQ_PARAM_PCCS</em></p>
<p><em><strong>TRIGGER = TRG</strong>_PARAM_PCCS</em></p></td>
</tr>
<tr>
<td colspan="2"><em><strong>RH_T_PARAM_CATEGORIA</strong></em></td>
<td colspan="2"></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>PARAM_CARR_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"><em>RH_T_PARAM_CARREIRA.ID</em></td>
</tr>
<tr>
<td colspan="2"><em>NOME</em></td>
<td colspan="2"><em>VARCHAR2(200),</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_PARAM_CATEG (ID)</strong></em></p></li>
<li><p><em><strong>INDEX :</strong> IX_PARAM_CATEG_</em>UUID
(UUID)</p></li>
<li><p>FK: FK_PARAM_CATEG_CARR</p></li>
</ul></td>
<td colspan="3"><p><em><strong>SEQUENCIA:</strong>
SEQ_PARAM_CATEGORIA</em></p>
<p><em><strong>TRIGGER =</strong> TRG_PARAM_CATEGORIA</em></p></td>
</tr>
<tr>
<td colspan="2"><strong>RH_T_PARAM_CARREIRA</strong></td>
<td colspan="2"></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NOME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>CODIGO</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2">DOMINIO = STATUS</td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=»</strong> PK_PARAM_</em>CARREIRA
<em>(ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PARAM_CARR_</em>UUID (UUID)</p></td>
<td colspan="3"><p><em><strong>SEQUENCIA:</strong>
SEQ_PARAM_CARREIRA</em></p>
<p><em><strong>TRIGGER =</strong> TRG_ PARAM_CARREIRA</em></p></td>
</tr>
<tr>
<td colspan="2"><strong>RH_T_PARAM_CARGO</strong></td>
<td colspan="2"></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NOME</em></td>
<td colspan="2"><em>VARCHAR2(100),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>PARAM_CARR_ ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"><em><strong>RH_T_PARAM_CARREIRA.ID</strong></em></td>
</tr>
<tr>
<td colspan="2"><em>DIRIGENTE</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_PARAM_</strong></em><strong>CARGO
<em>(ID)</em></strong></p></li>
<li><p><em>FK =FK_CARGO_CARR (PARAM_CARR_ ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PARAM_CARG_</em>UUID (UUID)</p></td>
<td colspan="3"><p><em><strong>SEQUENCIA</strong>:
SEQ_PARAM_</em>CARGO</p>
<p><em><strong>TRIGGER =</strong> TRG_PARAM_</em>CARGO</p></td>
</tr>
<tr>
<td colspan="2"><strong>RH_T_PARAM_ESCALAO</strong></td>
<td colspan="2"></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>CODIGO</em></td>
<td colspan="2"><em>VARCHAR(10)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>PARAM_CARR_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"><em><strong>RH_T_PARAM_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td colspan="2"><em>PARAM_CATEGORIA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"><em>RH_T_PARAM_CATEGORIA.ID</em></td>
</tr>
<tr>
<td colspan="2"><em>NIVEL_REFERENCIA</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ESCALAO</em></td>
<td colspan="2"><em>VARCHAR(10)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>VALOR</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_INICIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_ESCALAO (ID)</strong></em></p></li>
<li><p><em>FK = FK_ESC_CARR_PCCS (PARAM_CARR_ ID)</em></p></li>
<li><p><em>Fk<strong>= FK_ESC_CATEGORIA
(PARAM_</strong>CATEGORIA_ID<strong>)</strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PARAM_ESC_</em>UUID (UUID)</p></td>
<td colspan="3"><p><em><strong>SEQUENCIA:</strong>
SEQ_PARAM_ESCALAO</em></p>
<p><em><strong>TRIGGER =</strong> TRG_PARAM_ESCALAO</em></p></td>
</tr>
<tr>
<td colspan="2"><strong>RH_T_PARAM_LOCAL_TRAB</strong></td>
<td colspan="2"></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NOME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>PAIS_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td
colspan="2"><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td colspan="2"><em>ILHA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td
colspan="2"><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td colspan="2"><em>UPS_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"><em><strong>SIPSGLOBAL</strong>.GLB_T_UPS</em></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_</strong></em><strong>LOCAL_BALCAO
<em>(ID)</em></strong></p></li>
<li><p><em><strong><mark>FK =»
FK_LOCAL_TRAB_PAIS</mark></strong></em></p></li>
<li><p><em><strong><mark>FK =»
FK_LOCAL_TRAB_ILHA</mark></strong></em></p></li>
<li><p><em><strong><mark>FK =»
FK_LOCAL_TRAB_UPS</mark></strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PARAM_LTRAB_</em>UUID (UUID)</p></td>
<td colspan="3"><p><em><strong>SEQUENCIA</strong> :
SEQ_LOCAL_BANCAO</em></p>
<p><em><strong>TRIGGER =</strong> TRG_LOCAL_BALCAO</em></p></td>
</tr>
<tr>
<td colspan="2"><strong>RH_T_SECAO</strong></td>
<td colspan="2"></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>NOME</em></td>
<td colspan="2"><em>VARCHAR2(200),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>INSTIT_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"><em><strong>INPSSIGOF</strong>.INSTITUICOES.ID</em></td>
</tr>
<tr>
<td colspan="2"><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="2"><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td colspan="2"></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_SESSAO (ID)</strong></em></p></li>
<li><p><em><mark>FK <strong>=» FK_SESSAO_INSTIT
(</strong>INSTIT_ID<strong>)</strong></mark></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_SECAO_</em>UUID (UUID)</p></td>
<td colspan="3"><p><em><strong>SEQUENCIA</strong>: SEQ_SECAO</em></p>
<p><em><strong>TRIGGER =</strong> TRG_SESSAO</em></p></td>
</tr>
<tr>
<td colspan="7"><strong>RH_T_VINCULO_MOVIMENTO</strong></td>
</tr>
<tr>
<td><em>VINCULO_ID</em></td>
<td colspan="3"><em>NUMBER</em></td>
<td colspan="2"><em><strong>SIM</strong></em></td>
<td></td>
</tr>
<tr>
<td><em>TM_ID</em></td>
<td colspan="3"><em>NUMBER</em></td>
<td colspan="2"><em><strong>SIM</strong></em></td>
<td></td>
</tr>
<tr>
<td><em>TIPO</em></td>
<td colspan="3"><em>VARCHAR(10)</em></td>
<td colspan="2"><em><strong>SIM</strong></em></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="3"><em>VARCHAR2(1),</em></td>
<td colspan="2"><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="3"><em>DATE</em></td>
<td colspan="2"><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="3"><em>NUMBER</em></td>
<td colspan="2"><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="3"><em>VARCHAR(200)</em></td>
<td colspan="2"><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="3"><em>NUMBER</em></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="3"><em>VARCHAR(200)</em></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="3"><em>DATE</em></td>
<td colspan="2"></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="3"><em>VARCHAR(100)</em></td>
<td colspan="2"><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="4"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_VINCULO_MOV (ID)</strong></em></p></li>
<li><p><em><mark>FK <strong>=» FK_VINCULO_MOV
(VINCULO_ID)</strong></mark></em></p></li>
<li><p><em><mark>FK <strong>=» FK_MOV_VINCULO
(TM_ID)</strong></mark></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_VINCULO_MOV</em> (UUID)</p></td>
<td colspan="3"><p><em><strong>SEQUENCIA</strong>:
SEQ_VINCULO_MOVIMENTO</em></p>
<p><em><strong>TRIGGER =</strong> TRG_VINCULO_MOVIMENTO</em></p></td>
</tr>
</tbody>
</table>

## DOSSIER COLABORADOR

<table style="width:100%;">
<colgroup>
<col style="width: 28%" />
<col style="width: 24%" />
<col style="width: 0%" />
<col style="width: 22%" />
<col style="width: 24%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>CAMPOS</strong></th>
<th colspan="2" style="text-align: center;"><strong>TIPO</strong></th>
<th style="text-align: center;"><strong>OBRIGATORIEDADE</strong></th>
<th style="text-align: center;"><strong>RELACAO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><strong>RH_T_FUNCIONARIOS</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TIPO_DOCUMENTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_TIPO_DCOUMENTO</strong>.ID</em></td>
</tr>
<tr>
<td><em>NUM_DOCUMENTO</em></td>
<td colspan="2"><em>VARCHAR (50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NOME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>FOTOGRAFIA</em></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_NASCIMENTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>SEXO</em></td>
<td colspan="2"><em>VARCHAR2(15 BYTE),</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = GENERO</em></td>
</tr>
<tr>
<td><em>NM_MAE</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NM_PAI</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO_CIVIL</em></td>
<td colspan="2"><em>VARCHAR (20)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong>=ESTADO_CIVIL</em></td>
</tr>
<tr>
<td><em>NACIONALIDADE</em></td>
<td colspan="2"><em>VARCHAR2(70 BYTE)</em></td>
<td><em>SIM</em></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>LOC_NASC_ID</em></td>
<td colspan="2"><em>VARCHAR2(50 BYTE),</em></td>
<td><em>SIM</em></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>NIF</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NU_SEG_INPS</em></td>
<td colspan="2"><em>VARCHAR2(10 BYTE)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ENT_ID</em></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID_COLABORADOR</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO_VALIDACAO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR2(200 BYTE),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR2(200 BYTE),</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK =» <strong>PK_FUN</strong> (ID)</em></p></li>
<li><p><em>UQ=»<strong>UQ_UNICO_DOC (</strong>TIPO_DOCUMENTO,
NUM_DOCUMENTO<strong>)</strong></em></p></li>
<li><p><em>FK =» FK_FUN_TIPO_DOC</em></p></li>
<li><p><em>FK =»FK_FUN_LOCAL_NASC</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_FUNC_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_FUNCIONARIO</em></p>
<p><em><strong>TRIGGER:</strong> TRG_FUNCIONARIO</em></p></td>
</tr>
<tr>
<td><strong>RH_T_DOCUMENTO_PESSOAL</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NUM_DOCUMENTO</em></td>
<td colspan="2"><em>VARCHAR(20)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TIPO_DOCUMENTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_TIPO_DOCUMENTO</strong>.ID</em></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIO</strong>.ID</em></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>: PK_DOC_PESSOAL</strong>(ID)</em></p></li>
<li><p><em>FK: <strong>FK</strong>_<strong>DOC_FUN
(</strong>FUN_ID<strong>)</strong></em></p></li>
<li><p><em>FK:FK_DOC_TIPO (TIPO_DOCUMENTO_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_DOC_PESS_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_DOC_PESSOAL</em></p>
<p><em><strong>TRIGGER:</strong> TRG_DOC_PESSOAL</em></p></td>
</tr>
<tr>
<td><strong>RH_T_CONTACTO</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TIPO_CONTACTO</em></td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = TP_CONTACTO</em></td>
</tr>
<tr>
<td><em>CONTACTO</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS.</strong>ID</em></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK =» <strong>PK</strong>_<strong>CONTACTO</strong>
(ID)</em></p></li>
<li><p><em>FK =» <strong>FK</strong>_<strong>FUN_CONTATO</strong>
(FUN_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_CONTAC_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong> SEQ_CONTATO</em></p>
<p><em><strong>TRIGGER:</strong> TRG_CONTATO</em></p></td>
</tr>
<tr>
<td><strong>RH_T_ENDERECO</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>PAIS_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>ILHA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>CONCELHO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>FREGUESIA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>ZONA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>MORADA</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>RH_T_FUNCIONARIOS.ID</em></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK =»<strong>PK</strong>_<strong>ENDERECO
(</strong>ID<strong>)</strong></em></p></li>
<li><p><em>FK =» <strong>FK
_FUN_ENDERECO(</strong>FUN_ID<strong>)</strong></em></p></li>
<li><p><em>FK =»
<strong>FK_</strong>PAIS_<strong>ENDERECO(</strong>PAIS_ID<strong>)</strong></em></p></li>
<li><p><em>FK =»
<strong>FK_</strong>ILHA_<strong>ENDERECO(</strong>ILHA_ID<strong>)</strong></em></p></li>
<li><p><em>FK =»
<strong>FK_</strong>CON_<strong>ENDERECO(</strong>CONCELHO_ID<strong>)</strong></em></p></li>
<li><p><em>FK =»
<strong>FK_</strong>FREG_<strong>ENDERECO(</strong>FREGUESIA_ID<strong>)</strong></em></p></li>
<li><p><em>FK =»
<strong>FK_</strong>ZONA_<strong>ENDERECO(</strong>ZONA_ID<strong>)</strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_ENDER_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong> SEQ_ENDERECO</em></p>
<p><em><strong>TRIGGER:</strong> TRG_ENDERECO</em></p></td>
</tr>
<tr>
<td colspan="3"><strong>RH_T_FAMILIARES</strong></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TP_DOCUMENTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><p><em>RH_T_TIPOS_DOCUMENTO.ID</em></p>
<p><em>REFERENCIA =</em> <strong>DOCUMENTO_PESSOAL’</strong></p></td>
</tr>
<tr>
<td><em>NUM_DOCUMENTO</em></td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NOME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_NASCIMENTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>SEXO</em></td>
<td colspan="2"><em>VARCHAR(15)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = GENERO</em></td>
</tr>
<tr>
<td><em>GDP_ID</em></td>
<td colspan="2"><em>VARCHAR(15)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = GRAUS_DE_PARENTESCO</em></td>
</tr>
<tr>
<td><em>DEPENDENCIA</em></td>
<td colspan="2"><em>VARCHAR(15)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = DEPENDENCIA</em></td>
</tr>
<tr>
<td><em>MEMBRO_AGR</em></td>
<td colspan="2"><em>VARCHAR(15)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS=</strong> MEMBRO_AGR</em></td>
</tr>
<tr>
<td><em>NM_PAI</em></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>NM_MAE</em></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>RH_T_FUNCIONARIOS.ID</em></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK<strong>= PK_FAMILIAR</strong></em></p></li>
<li><p><em>FK <strong>=» FK_FUN_FAMILIAR_FK </strong></em></p></li>
<li><p><em>UQ <strong>=» UQ_UNIQ_FAM</strong> (NOME, NUM_DOCUMENTO,
FUN_ID<strong>)</strong></em></p></li>
<li><p><em><strong>FK =» FK_DOC_FAMILIAR</strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_FAM_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong> SEQ_FAMILIAR</em></p>
<p><em><strong>TRIGGER:</strong> TRG_FAMILIAR</em></p></td>
</tr>
<tr>
<td colspan="3"><strong>RH_T_HABILITACOES_LITERARIAS</strong></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>PAIS_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>ESTABLECIMENTO</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>AREA</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = AREA_FORMACAO</em></td>
</tr>
<tr>
<td><em>NOME_CURSO</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NIVEL</em></td>
<td colspan="2"><em>VARCHAR(20)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = NIVEL_HABILITACOES</em></td>
</tr>
<tr>
<td>DATA_INICIO</td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>DATA_FIM</td>
<td colspan="2"><em>VARCHAR(20)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>CONCLUIDO</td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS =</strong>SIM_NAO_NUMBER</em></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK<strong>= PK_HABILIT_LIT </strong></em></p></li>
<li><p><em>FK <strong>=» FK_FUN_HABILIT_LIT
(FUN_ID)</strong></em></p></li>
<li><p><em>FK <strong>=» FK_PAIS_HABILIT_LIT
(PAIS_ID)</strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_HAB_LIT_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_HABILIT_LIT</em></p>
<p><em><strong>TRIGGER:</strong> TRG_HABILIT_LIT</em></p></td>
</tr>
<tr>
<td><strong>RH_T_TIPOS_RELACIONAMENTO</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>CARGO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_PARAM_CARGO</strong>.ID</em></td>
</tr>
<tr>
<td><em>INSTIT_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>INPSSIGOF.INTITUICOES.ID</em></td>
</tr>
<tr>
<td><em>SECCAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>RH_T_SECAO.ID</em></td>
</tr>
<tr>
<td><em>CARR_PCC_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_PARAM_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td><em>CATEGORIA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_PARAM_CATEGORIA</strong>.ID</em></td>
</tr>
<tr>
<td><em>ESCALAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_PARAM_ESCALAO</strong>.ID</em></td>
</tr>
<tr>
<td><em>VINCULO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_PARAM_VINCULO</strong>.ID</em></td>
</tr>
<tr>
<td><em>REGIME</em></td>
<td colspan="2"><em>VARCHAR(20)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = REGIME_TRABALHO</em></td>
</tr>
<tr>
<td><em>SALARIO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>MOEDA</em></td>
<td colspan="2"><em>VARCHAR(20)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = MOEDA</em></td>
</tr>
<tr>
<td><em>DATA_INICIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>LOC_TRAB_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_PARAM_LOCAL_TRAB</strong>.ID</em></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = STATUS</em></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>CONTRATO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_CONTRATO</strong>.CONTRATO_ID</em></td>
</tr>
<tr>
<td><em>CARREIRA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td><em>MOB_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_MOBILIDADE</strong>.ID</em></td>
</tr>
<tr>
<td><em>REGIME_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_REGIME_TRAB.</strong>ID</em></td>
</tr>
<tr>
<td><em>TIPO_CONTRATO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_PARAM_CONTRATO.</strong>ID</em></td>
</tr>
<tr>
<td><em>TIPREL_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><p><em>Id do registo anterior</em></p>
<p><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></p></td>
</tr>
<tr>
<td><em>REFERENTE</em></td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FLG_PROCESSA</em></td>
<td colspan="2">VARCHAR(3)</td>
<td></td>
<td><em><strong>DOMAINS</strong>: SIM_NAO</em></td>
</tr>
<tr>
<td><em>TIPO_SITUACAO</em></td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td><em><strong>DOMAINS</strong>=</em> TIPO_MOV_LABORAL</td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(300)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ULT_PROC</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>MOTIVO_SIT_LAB</em></td>
<td colspan="2"><em>VARHCAR(200)</em></td>
<td></td>
<td><em><strong>DOMAINS</strong> = MOTIVO_SIT_LABORAL</em></td>
</tr>
<tr>
<td><em>SITUAC_LABORAL_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_SITUACAO_LABORAL</strong>.ID</em></td>
</tr>
<tr>
<td><em>TP_CONTRATO</em></td>
<td colspan="2"><em>VARCHAR(20)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = TP_CONTRATO</em></td>
</tr>
<tr>
<td><em>DATA_INICIO_CONTRATO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_FIM_CONTRATO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em><strong>PK=</strong> PK_TIPREL</em></p></li>
<li><p><em><strong>FK</strong>= FK_TIPREL_CONTRATO
(CONTR_VINCULO_ID)</em></p></li>
<li><p><em><strong>FK</strong>= FK_TIPREL_FUN (FUN_ID)</em></p></li>
<li><p><em><strong>FK =»</strong> FK_ TIPREL _CARR_PCCS
(CARR_PCCS_ID)</em></p></li>
<li><p><em><strong>FK</strong> =» FK_ TIPREL _CARGO
(CARGO_ID)</em></p></li>
<li><p><em><strong>FK =»</strong>FK_ TIPREL _INSTIT
(INSTIT_ID)</em></p></li>
<li><p><em><strong>FK =»</strong>FK_ TIPREL _SECCAO
(SECCAO_ID)</em></p></li>
<li><p><em><strong>FK =»</strong>FK_ TIPREL
_ESCALAO(ESCALAO_ID<strong>)</strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_TP_RELAC_</em>UUID (UUID)</p></td>
<td colspan="2"><ul>
<li><p><em><del><strong>FK =»</strong>FK_ TIPREL
_VINCULO(VINCULO_ID)</del></em></p></li>
<li><p><em>FK =» FK_ TIPREL _LOCAL_TRAB (LOC_TRAB_ID)</em></p></li>
<li><p><em>FK =» FK_ TIPREL _TIPREL (TIPREL_ID)</em></p></li>
<li><p><em>FK = FK_TIPREL_REGIME (REGIME_ID)</em></p></li>
<li><p><em>FK= FK_TIPREL_CARREIRA (CARREIRA_ID)</em></p></li>
<li><p><em>FK = FK_TIPREL_MOBILIDADE (MOB_ID)</em></p></li>
<li><p><em>FK= FK_TIPREL_SITLAB (SITUAC_LABORAL_ID)</em></p></li>
<li><p><em>FK= FK_TIPREL_PARAM_CTR (TIPO_CONTRATO_ID)</em></p></li>
</ul>
<p><em><strong>SEQUENCIA:</strong> SEQ_TIP_REL</em></p>
<p><strong><em>TRIGGER:</em> TRG_ TIPOS_RELACIONAMENTO</strong></p></td>
</tr>
<tr>
<td colspan="3"><strong>RH_T_DEF_REMUNERACOES</strong></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TM_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>INPSSIGOF</strong>.rh_tipo_movimentos</em></td>
</tr>
<tr>
<td><em>PERCENTAGEM</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>VALOR</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>DATA_INICIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>TIPREL_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT :</strong></em></p>
<ul>
<li><p><em>PK<strong>=PK_DEF_REMUN
(</strong>ID<strong>)</strong></em></p></li>
<li><p><em><strong>FK = FK_FUN_DEF_REMUN</strong></em></p></li>
<li><p><em><strong>FK = FK_TIPREL_DEF_REMUN</strong></em></p></li>
<li><p><em><strong>FK = FK_TM_DEF_REMUN</strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_DEF_REM_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong> SEQ_DEF_PAG</em></p>
<p><em><strong>TRIGGER:</strong> TRG_ DEF_REMUNERACAO</em></p></td>
</tr>
<tr>
<td><strong><del>RH_T_REMUN_TIPREL</del></strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td><em><del>SIM</del></em></td>
<td></td>
</tr>
<tr>
<td><em><del>REM_ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td><em><del>SIM</del></em></td>
<td><em><del><strong>RH_T_DEF_REMUNERACAO</strong>.ID</del></em></td>
</tr>
<tr>
<td><em><del>TIPREL_ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td><em><del>SIM</del></em></td>
<td><em><del><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</del></em></td>
</tr>
<tr>
<td><em><del>ESTADO</del></em></td>
<td colspan="2"><em><del>VARCHAR2(1),</del></em></td>
<td><em><del>SIM</del></em></td>
<td></td>
</tr>
<tr>
<td><em><del>DATA_REGISTO</del></em></td>
<td colspan="2"><em><del>DATE</del></em></td>
<td><em><del>SIM</del></em></td>
<td></td>
</tr>
<tr>
<td><em><del>USER_REGISTO_ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td><em><del>SIM</del></em></td>
<td></td>
</tr>
<tr>
<td><em><del>USER_REGISTO_NAME</del></em></td>
<td colspan="2"><em><del>VARCHAR(200)</del></em></td>
<td><em><del>SIM</del></em></td>
<td></td>
</tr>
<tr>
<td><em><del>USER_ALTERACAO_ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>USER_ALTERACAO_NAME</del></em></td>
<td colspan="2"><em><del>VARCHAR(200)</del></em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>DATA_ALTERACAO</del></em></td>
<td colspan="2"><em><del>DATE</del></em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>OBS</del></em></td>
<td colspan="2"><em><del>VARCHAR(400)</del></em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>UUID</del></em></td>
<td colspan="2"><em><del>VARCHAR(100)</del></em></td>
<td><em><del>SIM</del></em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong><del>CONSTRAINT :</del></strong></em></p>
<ul>
<li><p><em><del>PK<strong>=PK_REMUN_TIPREL
(</strong>ID<strong>)</strong></del></em></p></li>
<li><p><em><del>FK <strong>= FK_PEMUN_TIPREL</strong>
(REM_ID)</del></em></p></li>
<li><p><em><del>FK <strong>= FK_TIPREL_REMUN</strong>
(TIPREL_ID)</del></em></p></li>
</ul>
<p><del><em><strong>INDEX :</strong> IX_REM_TIP_</em>UUID
(UUID)</del></p></td>
<td colspan="2"><p><em><del><strong>SEQUENCIA</strong>:
SEQ_REMUN_TIPREL</del></em></p>
<p><em><del><strong>TRIGGER:</strong> TRG_
REMUN_TIPREL</del></em></p></td>
</tr>
<tr>
<td><strong>RH_T_DEF_PAGAMENTOS</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TM_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>INPSSIGOF.RH_TIPOS_MOVIMENTO.ID</em></td>
</tr>
<tr>
<td><em>VALOR</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_INICIO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>TIPREL_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_TIPOS_RELACIONAMENTO.</strong>ID</em></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR2(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT :</strong></em></p>
<ul>
<li><p><em>PK<strong>=PK_DEF_PAGAM
(</strong>ID<strong>)</strong></em></p></li>
<li><p><em>FK <strong>= FK_TIPREL_DEF_PAG
(</strong>TIPREL_ID)</em></p></li>
<li><p><em>FK = FK_TM_DEF_PAG</em></p></li>
<li><p><em>FK = FK_FUN_DEF_PAG</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_DEF_PAG_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_DEF_REM</em></p>
<p><em><strong>TRIGGER:</strong> TRG_ DEF_PAGAMENTOS</em></p></td>
</tr>
<tr>
<td><strong><del>RH_T_PAG_TIPREL</del></strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td><del>SIM</del></td>
<td></td>
</tr>
<tr>
<td><em><del>PAG_ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td><del>SIM</del></td>
<td><del><strong>RH_T_DEF_PAGAMENTO</strong>.ID</del></td>
</tr>
<tr>
<td><em><del>TIPREL_ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td><del>SIM</del></td>
<td><del><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</del></td>
</tr>
<tr>
<td><em><del>ESTADO</del></em></td>
<td colspan="2"><em><del>VARCHAR2(1),</del></em></td>
<td><del>SIM</del></td>
<td></td>
</tr>
<tr>
<td><em><del>DATA_REGISTO</del></em></td>
<td colspan="2"><em><del>DATE</del></em></td>
<td><del>SIM</del></td>
<td></td>
</tr>
<tr>
<td><em><del>USER_REGISTO_ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td><del>SIM</del></td>
<td></td>
</tr>
<tr>
<td><em><del>USER_REGISTO_NAME</del></em></td>
<td colspan="2"><em><del>VARCHAR(200)</del></em></td>
<td><del>SIM</del></td>
<td></td>
</tr>
<tr>
<td><em><del>USER_ALTERACAO_ID</del></em></td>
<td colspan="2"><em><del>NUMBER</del></em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>USER_ALTERACAO_NAME</del></em></td>
<td colspan="2"><em><del>VARCHAR(200)</del></em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>DATA_ALTERACAO</del></em></td>
<td colspan="2"><em><del>DATE</del></em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>OBS</del></em></td>
<td colspan="2"><em><del>VARCHAR(400)</del></em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><del>UUID</del></em></td>
<td colspan="2"><em><del>VARCHAR(100)</del></em></td>
<td><em><del>SIM</del></em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong><del>CONSTRAINT :</del></strong></em></p>
<ul>
<li><p><em><del>PK<strong>=PK_PAG_TIPREL
(</strong>ID<strong>)</strong></del></em></p></li>
<li><p><em><del>FK <strong>= FK_DEF_PAG_TIPREL</strong>
(PAG_ID)</del></em></p></li>
<li><p><em><del>FK <strong>= FK_DEF_TIPREL PAG</strong>
(TIPREL_ID)</del></em></p></li>
</ul>
<p><del><em><strong>INDEX :</strong> IX_PAG_TIPREL_</em>UUID
(UUID)</del></p></td>
<td colspan="2"><p><em><del><strong>SEQUENCIA:</strong>
SEQ_DEF_PAG_TIPREL</del></em></p>
<p><del><em><strong>TRIGGER:</strong></em> TRG_PAG_TIPREL</del></p></td>
</tr>
<tr>
<td><strong>RH_T_CONTRATO_VINCULO</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>VINCULO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_PARAM_VINCULO</strong>.ID</em></td>
</tr>
<tr>
<td><em>TP_CONTRATO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_PARAM_CONTRATO.ID</strong></em></td>
</tr>
<tr>
<td><em>SITUACAO_LABORAL</em></td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DURACAO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA INICIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>VERSAO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>CONTRATO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_CONTRATO</strong>.CONTRATO_ID</em></td>
</tr>
<tr>
<td><em>TP_CONTRATO</em></td>
<td colspan="2"><em>VARCHAR2(20)</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS = TP_CONTRATO</strong></em></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT:</strong></em></p>
<ul>
<li><p><em><strong>PK:</strong> PK_CONTRATO (ID)</em></p></li>
<li><p><em><strong>FK</strong> : FK_CONTRATO_FUN (FUN_ID)</em></p></li>
<li><p><em><strong>FK</strong>: FK_CONTR_CONTR_ID
(CONTRATO_ID)</em></p></li>
<li><p><em>FK: FK_PARAM_VINC_CONTR</em></p></li>
<li><p><em>FK: FK_PARAM_CONTR_CONTR</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_CONTRAT_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong></em> SEQ_CONTRATO</p>
<p><em><strong>TRIGGER:</strong></em> TRG_CONTRATO</p></td>
</tr>
<tr>
<td><strong>RH_T_DADOS_BANCARIOS</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ENT_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>INPSSIGOF</strong>.ENTIDADE</em></td>
</tr>
<tr>
<td><em>NUM_CONTA</em></td>
<td colspan="2"><em>NUMBER(11,0)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_INICIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT :</strong></em></p>
<ul>
<li><p><em>PK<strong>=PK_DADOS_BANCO
(</strong>ID<strong>)</strong></em></p></li>
<li><p><em>FK= <strong>FK_BANCO_FUN</strong> (FUN_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_DD_BANC_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>TRIGGER:</strong> TRG_
DADOS_BANCARIOS</em></p>
<p><em><strong>SEQUENCIA:</strong> SEQ_DADOS_BANCO</em></p></td>
</tr>
<tr>
<td><strong>RH_T_DOCUMENTO</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TP_DOCUMENTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>RH_T_TIPO_DOCUMENTO.ID</em></td>
</tr>
<tr>
<td><em>DOC_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>RH_T_FUNCIONARIOS.ID</em></td>
</tr>
<tr>
<td><em>REFERENCIA_NAME</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>REFERENCIA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT :</strong></em></p>
<ul>
<li><p><em>PK<strong>=PK_DOCUMENTO_ID
(</strong>ID<strong>)</strong></em></p></li>
<li><p><em>FK= <strong>FK_FUN_DOC_ID</strong> (FUN_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_DOCUM_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_DOCUMENTO</em></p>
<p><em><strong>TRIGGER:</strong> TRG_ DOCUMENTO</em></p></td>
</tr>
<tr>
<td><strong>RH_T_VALIDACAO</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TIPO_ACCAO</em></td>
<td colspan="2"><em>VARCHAR2(20),</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = TIPO_ACAO</em></td>
</tr>
<tr>
<td><em>REFERENCIA_NAME</em></td>
<td colspan="2"><em>VARCHAR2(100),</em></td>
<td><em>SIM</em></td>
<td><em><strong>DOMAINS</strong> = ACCAO_REFERENTE</em></td>
</tr>
<tr>
<td><em>REFERENCIA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>TIPREL_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_VALIDACAO
(</strong>ID<strong>)</strong></em></p></li>
<li><p><em>FK<strong>=» FK _FUN_VALID
(</strong>FUN_ID<strong>)</strong></em></p></li>
<li><p><em><strong>FK = FK_TIPREL_VALID
(</strong>TIPREL_ID<strong>)</strong></em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_VALID_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong> SEQ_LOG</em></p>
<p><em><strong>TRIGGER =</strong> TRG_VALIDACAO</em></p></td>
</tr>
<tr>
<td><strong>RH_T_VALIDACAO_DETALHE</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>VALIDACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_VALIDACAO.</strong>ID</em></td>
</tr>
<tr>
<td><em>CAMPO_ALTERADO</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>VALOR_ANTERIOR</em></td>
<td colspan="2"><em>VARCHAR(500)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>VALOR_NOVO</em></td>
<td colspan="2"><em>VARCHAR(500)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TABELA_NAME</em></td>
<td colspan="2"><em>VARCHAR2(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TABELA _ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT:</strong></em></p>
<ul>
<li><p><em><strong>PK: PK</strong></em></p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong></em></p>
<p><em><strong>TRIGGER</strong></em></p></td>
</tr>
<tr>
<td><strong>RH_T_LOG</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TIPO_ACCAO</em></td>
<td colspan="2"><em>VARCHAR2(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TABELA_NAME</em></td>
<td colspan="2"><em>VARCHAR2(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>TABELA _ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>TIPREL_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_LOG_ID
(</strong>ID<strong>)</strong></em></p></li>
<li><p><em>FK =»FK_FUN_LOG_ID (FUN_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_LOG_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA: SEQ_LOG</strong></em></p>
<p><em><strong>TRIGGER = TRG_LOG</strong></em></p></td>
</tr>
<tr>
<td><strong>RH_T_LOG_DETALHE</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>LOG_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_LOG</strong>.ID</em></td>
</tr>
<tr>
<td><em>CAMPO_ALTERADO</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>VALOR_ANTERIOR</em></td>
<td colspan="2"><em>VARCHAR(500)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>VALOR_NOVO</em></td>
<td colspan="2"><em>VARCHAR(500)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DADOS_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_LOG_DET_ID (ID)</strong></em></p></li>
<li><p><em>FK =»FK_LOG_ID (LOG_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_LOG_DET_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_LOG_DETALHE</em></p>
<p><em><strong>TRIGGER =</strong> TRG_LOG_DETALHE</em></p></td>
</tr>
<tr>
<td><strong>RH_T_PROCESSO_DISCIPLINAR</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td colspan="2">NUMBER</td>
<td>SIM</td>
<td></td>
</tr>
<tr>
<td>TIPREL_ID</td>
<td colspan="2">NUMBER</td>
<td>SIM</td>
<td><em><strong>RH_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td>FUN_ID</td>
<td colspan="2">NUMBER</td>
<td></td>
<td><em><strong>RH_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td>NUM_PROCESSO</td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ENTIDADE</td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>TP_PROCESSO</td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>PENA_DISCP</td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATE_INIC_PD</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATE_FIM_PD</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATE_INIC_PENA</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATE_FIM_PENA</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>NUM_BO</td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_PUBL_BO</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>NUM_ORDEM_SERV</td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_ORDEM_SERV</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>NUM_OFA</td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_EMISS_OFA</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_REGISTO</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>USER_REGISTO_ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>USER_REGISTO_NAME</td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>USER_ALTERACAO_ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>USER_ALTERACAO_NAME</td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_ALTERACAO</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=» PK_PROC_DISCP (ID)</strong></em></p></li>
<li><p><em>FK = FK_PROC_DISCC_FUN (FUN_ID)</em></p></li>
<li><p><em>Fk = FK_PROC_DISC_TIPREL (TIPREL_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PROC_DISC_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_PROCESSO_DISCIP</em></p>
<p><em><strong>TRIGGER =</strong> TRG_PROCESSO_DISCIPLINAR</em></p></td>
</tr>
<tr>
<td><strong>RH_T_FORMACAO_FEITOS</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>PAIS_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>SIPSGLOBAL</strong>.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td><em>ESTABELECIMENTO</em></td>
<td colspan="2"><em>VARCHAR (200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>RHTPFOR</em></td>
<td colspan="2"><em>VARCHAR (50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>CURSO</em></td>
<td colspan="2"><em>VARCHAR (200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>NIVEL</em></td>
<td colspan="2"><em>VARCHAR (10)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=»</strong> PK_FORM_FEITO
<strong>(ID)</strong></em></p></li>
<li><p><em>FK= FK_FORM_FUN_ID (FUN_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_FORM_FEITO_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA =</strong>
SEQ_FORMCACAO_FEITO</em></p>
<p><em><strong>TRIGGER =</strong> TRG_FORMACAO_FEITO</em></p></td>
</tr>
<tr>
<td><strong>RH_T_EXPERIENCIA_PROF</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>PAIS_ID</em></td>
<td colspan="2"><em>number</em></td>
<td></td>
<td><em><strong>SIPSGLOBAL.GLB_T_GEOGRAFIA.</strong>ID</em></td>
</tr>
<tr>
<td><em>EMPRESA</em></td>
<td colspan="2"><em>VARCHAR (200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>CARGO</em></td>
<td colspan="2"><em>VARCHAR (200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_INICIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"><em>VARCHAR2(1),</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_FUNCIONARIO</strong>.ID</em></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK <strong>=»</strong> PK_EXP_PROF
<strong>(ID)</strong></em></p></li>
<li><p><em>FK= FK_EXP_PROF (FUN_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_EXP_PROF_</em>UUID (UUID)</p></td>
<td colspan="4"><p><em><strong>SEQUENCIA:</strong> SEQ_
EXPERIENCIA_PROF</em></p>
<p><em><strong>TRIGGER =</strong> TRG_EXPERIENCIA_PROF</em></p></td>
</tr>
<tr>
<td><strong>RH_T_REGIME_TRAB</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>FUN_ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_FUNCIONARIO</strong>.ID</em></td>
</tr>
<tr>
<td>TIPO_REGIME</td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_FIM</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>TIPO_SITUACAO</em></td>
<td colspan="2">VARCHAR(100)</td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK : PK_REGIME_TRAB</em></p></li>
<li><p><em>FK: FK_REGIME_FUN (FUN_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_REGIM_TRAB_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_REGIME_TRAB</em></p>
<p><em><strong>TRIGGER</strong>: TRG_REGIME_TRAB</em></p></td>
</tr>
<tr>
<td><strong>RH_T_REGIME_MODAL</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>REGIME_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_REGIME_TRAB</strong>.ID</em></td>
</tr>
<tr>
<td><em>MODALIDADE</em></td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DIAS_SEMANA</td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>NUM_HORAS</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em><strong>PK</strong> : PK_REGIME_MODAL</em></p></li>
<li><p><em><strong>FK</strong>: FK_REGIME_MODAL_TRAB
(REGIME_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_REGIM_MOD_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_
REGIME_MODAL</em></p>
<p><em><strong>TRIGGER :</strong> TRG_REGIME_MODAL</em></p></td>
</tr>
<tr>
<td><strong>RH_T_MOBILIDADE</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em><strong>ID</strong></em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>INSTID_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em>INPSSIGOF.INSTITUICOES.ID</em></td>
</tr>
<tr>
<td><em>LOCAL_TRAB_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_PARAM_LOCAL_TRAB</strong>.ID</em></td>
</tr>
<tr>
<td><em>SECAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_SECAO</strong>.ID</em></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>TIPO_SITUACAO</em></td>
<td colspan="2">VARCHAR(100)</td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK: PK_MOBILIDADE</em></p></li>
<li><p><em>FK: FK_MOB_FUN (FUN_ID)</em></p></li>
<li><p><em>FK:FK_MOB_INSTIT (INSTIT_ID)</em></p></li>
<li><p><em>FK: FK_MOB_LOCAL_TRAB</em></p></li>
<li><p><em>FK: FK_MOB_SECAO</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_MOBILID_</em>UUID (UUID)</p></td>
<td colspan="2"></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_MOBILIDADE</em></p>
<p><em><strong>TRIGGER</strong>:TRG_MOBILIDADE</em></p></td>
</tr>
<tr>
<td><strong>RH_T_CARREIRA</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td colspan="2">NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td>CARGO_ID</td>
<td colspan="2">NUMBER</td>
<td></td>
<td><em><strong>RH_T_PARAM_CARGO</strong>.ID</em></td>
</tr>
<tr>
<td>CARR_PCCS_ID</td>
<td colspan="2">NUMBER</td>
<td></td>
<td><em><strong>RH_T_PARAM_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td>CATEGORIA_ID</td>
<td colspan="2">NUMBER</td>
<td></td>
<td><em><strong>RH_T_PARAM_CATEGORIA</strong>.ID</em></td>
</tr>
<tr>
<td>ESCALAO_ID</td>
<td colspan="2">NUMBER</td>
<td></td>
<td><em><strong>RH_T_PARAM_ESCALAO</strong>.ID</em></td>
</tr>
<tr>
<td>SALARIO</td>
<td colspan="2">NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td>CONTR_VINCULO_ID</td>
<td colspan="2">NUMBER</td>
<td></td>
<td><em><strong>RH_T_CONTRATO_VINCULO</strong>.ID</em></td>
</tr>
<tr>
<td>FLG_PROCESSA</td>
<td colspan="2">NUMBER</td>
<td></td>
<td><strong>DOMAINS</strong>: SIM_NAO_NUMBER</td>
</tr>
<tr>
<td>ESTADO</td>
<td colspan="2">VARCHAR(1)</td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>TIPO_SITUACAO</em></td>
<td colspan="2">VARCHAR(100)</td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_REGISTO</td>
<td colspan="2">DATE</td>
<td></td>
<td></td>
</tr>
<tr>
<td>USER_REGISTO_ID</td>
<td colspan="2">NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td>USER_REGISTO_NAME</td>
<td colspan="2">VARCHAR(200)</td>
<td></td>
<td></td>
</tr>
<tr>
<td>USER_ALTERACAO_ID</td>
<td colspan="2">NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td>USER_ALTERACAO_NAME</td>
<td colspan="2">VARCHAR(200)</td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_INICIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong>:</em></p>
<ul>
<li><p><em>PK: PK_CARREIRA</em></p></li>
<li><p><em>FK: FK_CARR_CARGO (CARGO_ID)</em></p></li>
<li><p><em>FK : FK_CARR_CARR_PCCS (</em>CARR_PCCS_ID<em>)</em></p></li>
<li><p><em>FK: FK_CARR_CATEG (</em>CATEGORIA_ID<em>)</em></p></li>
<li><p><em>FK: FK_CARR_ESCALAO (</em>ESCALAO_ID<em>)</em></p></li>
<li><p><em>FK: FK_CARR_CONTRAT (</em>CONTR_VINCULO_ID<em>)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_CARREIR_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong> :
SEQ_CARREIRA</em></p>
<p><em><strong>TRIGGER</strong>: TRG_CARREIRA</em></p></td>
</tr>
<tr>
<td><strong>RH_T_SUBSTITUICAO</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>SUBSTITUIDO_TIPREL_ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td>SUBSTITUTO_TIPREL_ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td>DATA_INICIO</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_FIM</td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>MOTIVO</td>
<td colspan="2"><em>VARCHAR2(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong>:</em></p>
<ul>
<li><p><em>PK: PK_SUBSTITUICAO</em></p></li>
<li><p><em>FK: FK_SUBSTIT_TIPRELDE (</em>TIPREL_ID_DE<em>)</em></p></li>
<li><p>FK: <em>FK_ SUBSTIT_ TIPRELPARA (TIPREL_ID_PARA)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_SUBSTIT_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong> :
SEQ_SUBSTITUICAO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_SUBSTITUICAO</em></p></td>
</tr>
<tr>
<td><strong>RH_T_SUBSTITUICAO_DETALHE</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>MES_ANO</em></td>
<td colspan="2"><em>VARCHAR()</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NR_DIAS</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>VALOR_DO_SUBSTITUTO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>VALOR_DO_SUBSTITUIDO</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>SUBSTITUICAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><strong>RH_T_SUBSTITUICAO.</strong>ID</td>
</tr>
<tr>
<td><em>ESTADO</em></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><em><strong>CONSTRAINT</strong>:</em></p>
<ul>
<li><p><em>PK: PK_SUBSTIT_DET</em></p></li>
<li><p><em>FK: FK_SUBS_DET_SUSBT</em></p></li>
</ul>
<p><em><strong>SEQUENCIA:</strong>
SEQ_</em>SUBSTITUICAO_DETALHE</p></td>
<td colspan="3"><em><strong>TRIGGER:</strong>
TGR_</em>SUBSTITUICAO_DETALHE</td>
</tr>
<tr>
<td><strong>RH_T_ORDEM_SERVICO</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>NU_ORDEM</td>
<td colspan="2"><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DESCRICAO</td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>REFERENTE</td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>FUN_ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td>TIREPL_ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td>VALIDACAO_ID</td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_VALIDACAO</strong>.ID</em></td>
</tr>
<tr>
<td>REFERENTE</td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK: PK_ORDEM_SERV</em></p></li>
<li><p><em>FK: FK_ODERM_FUN(FUN_ID)</em></p></li>
<li><p><em>FK: FK_ORDEM_TIPREL(TIPREL_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_ORD_SERV_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_OREDEM_SERVICO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_ORDEM_SERVICO</em></p></td>
</tr>
<tr>
<td colspan="5"><strong>RH_T_SITUACAO_LABORAL
<mark>(AQUI)</mark></strong></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>SITUACAO_LABORAL_ID</em></td>
<td colspan="2"><em>VARHCAR(50)</em></td>
<td><em>SIM</em></td>
<td><em>RH_T_PARAM_SITUACAO.ID</em></td>
</tr>
<tr>
<td><em>MOTIVO_SIT_LAB</em></td>
<td colspan="2"><em>VARHCAR(200)</em></td>
<td></td>
<td><em><strong>DOMAINS</strong> = MOTIVO_SIT_LABORAL</em></td>
</tr>
<tr>
<td><em>MOTIVO_SIT_LAB_ID</em></td>
<td colspan="2"></td>
<td></td>
<td><em><strong>RH_T_PARAM_SITUACAO_DET</strong></em></td>
</tr>
<tr>
<td><em>DATA_INICIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_FIM</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>CONTR_VINCULO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_CONTRATO_VINCULO</strong>.ID</em></td>
</tr>
<tr>
<td>ESTADO</td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>OBS</em></td>
<td colspan="2"><em>VARCHAR(400)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT:</strong></em></p>
<ul>
<li><p><em>PK: PK_SITUACAO_LABORAL</em></p></li>
<li><p><em>FK: FK_SITUACAO_PARAM_SIT (SITUACAO_LABORAL_ID)</em></p></li>
<li><p><em>FK: FK_SITUACAO_PARAM_SIT_DET
(MOTIVO_SIT_LAB_ID)</em></p></li>
<li><p><em>FK: FK_SITUACAO_CONTRAT( CONTRATO_ID )</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_SIT_LAB_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_SITUACAO_LABORAL</em></p>
<p><em><strong>TRIGGER</strong>: TRG_SITUACAO_LABORAL</em></p></td>
</tr>
<tr>
<td><strong>RH_T_ALERTA</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>REFERENCIA</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>REFERENCIA_NAME</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>REFERENCIA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>TIPO_ALERTA</em></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>TIPO_SITUACAO</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td><em>Indica se a alerta é informativo</em></td>
</tr>
<tr>
<td><em>DESCRICAO</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>FLG_NOTIFICACAO</td>
<td colspan="2"><em>VARCHAR(3)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>FLG_TRATAMENTO</td>
<td colspan="2"><em>VARCHAR(3)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>PRIORIDADE</td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT</strong></em></p>
<ul>
<li><p><em>PK: PK_ALERTA</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_ALERTA_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_ALERTA</em></p>
<p><em><strong>TRIGGER</strong>: TRG_ALERTA</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_NOTIFICACAO</strong></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>REFERENCIA</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>MESSAGE</em></td>
<td colspan="2"><em>VARCHAR(4000)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ASSUNTO</em></td>
<td colspan="2"><em>VARCHAR(300)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>EMAIL</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NOME_RECEPTOR</em></td>
<td colspan="2"></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ENVIO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>URL</em></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td colspan="2"><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td colspan="2"><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td colspan="2"><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td colspan="2"><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td colspan="2"><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>ALERTA_ID</em></td>
<td colspan="2"><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_ALERTA</strong>.ID</em></td>
</tr>
<tr>
<td colspan="3"><p><em><strong>CONSTRAINT:</strong></em></p>
<ul>
<li><p><em>PK: PK_NOTIFICACAO</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_NOTIF_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA:</strong>
SEQ_NOTIFICACAO</em></p>
<p><em><strong>TRIGGER:</strong> TRG_NOTIFICACAO</em></p></td>
</tr>
<tr>
<td></td>
<td colspan="2"></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

## PROCESSAMENTO SALARIAL

<table>
<colgroup>
<col style="width: 29%" />
<col style="width: 25%" />
<col style="width: 22%" />
<col style="width: 22%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>CAMPOS</strong></th>
<th style="text-align: center;"><strong>TIPO</strong></th>
<th style="text-align: center;"><strong>OBRIGATORIEDADE</strong></th>
<th style="text-align: center;"><strong>RELACAO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><strong>RH_T_PROC_SALARIOS</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>DATA_DE</td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>DATA_ATE</td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>CC_ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>INPSSIGOF.CENTROS_CUSTO</em></td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>VARCHAR2(5 BYTE),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>DATA_PROC_PROVISORIO</td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_PROC_DEFINITIVO</td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>OBS</td>
<td><em>VARCHAR2(200 BYTE),</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>FLG_FECHADO</td>
<td><em>VARCHAR2(1 BYTE)</em></td>
<td></td>
<td><em>FLG_FECHADO" IS '1= "FECHADO"; NULL = ABERTO'</em></td>
</tr>
<tr>
<td>CAB_1_ID</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>RH_PROC_SALARIOS</td>
<td><em>NUNMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><em><strong>CONSTRAINT:</strong></em></p>
<ul>
<li><p><em><strong>PK</strong>: PK_PRSALS</em></p></li>
<li><p><em><strong>FK</strong>: FK_PROC_CC</em></p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_PROC_SALARIOS</em></p>
<p><em><strong>TRIGGER:</strong> TRG_</em>PROC_SALARIOS</p></td>
</tr>
<tr>
<td><strong>RH_T_PROC_FUNCIONARIOS</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>DATA_PROCESSAMENTO</td>
<td>DATE</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>DATA_REFERENCIA_DE</td>
<td>DATE</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>DATA_REFERENCIA_ATE</td>
<td>DATE</td>
<td></td>
<td></td>
</tr>
<tr>
<td>TOTAL_REMUNERACOES</td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td>TOTAL_PAGAMENTOS</td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td>VARCHAR2(3 BYTE)</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>PRSALS_ID</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td><strong>RH_T_PROC_SALARIOS.ID</strong></td>
</tr>
<tr>
<td>TOT_REMUN_COLLECT</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>TOT_LIQUIDO</td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td>TOT_REMUN_SOCIAL</td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td>TIPREL_ID</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td>RHB_ID</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_DADOS_BANCARIOS</strong>.ID</em></td>
</tr>
<tr>
<td>NU_CONTA</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>NIB</td>
<td>VARCHAR2(21 BYTE),</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>CAB_1_ID</td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_PROC_FUNC</p></li>
<li><p>FK: FK_ PROC_FUNC_PRSALS_ID (PRSALS_ID)</p></li>
<li><p>FK_PROC_FUNC_RHB (RHB_ID)</p></li>
<li><p>FK: FK_ PROC_FUNC_TIPREL(TIPREL_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_PROC_FUNCIONARIO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_ PROC_FUNCIONARIOS</em></p></td>
</tr>
<tr>
<td><strong>RH_T_PAGAMENTOS</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>id</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>valor</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>data_ref</td>
<td>DATE</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>estado</td>
<td>VARCHAR2(3 BYTE)</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>prsal_id</td>
<td>NUMBER</td>
<td><em><strong>SIM</strong></em></td>
<td><em><strong>RH_T_PROC_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td>defp_id</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td><em>RH_T_DEF_PAGAMENTOS.ID</em></td>
</tr>
<tr>
<td>percentagem</td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID_UPLOAD</td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_PAGAMENTO (ID)</p></li>
<li><p>FK: FK_PAG_PROC_FUNC (PRSAL_ID)</p></li>
<li><p>FK: PAG_DEF_PAG (DEFP_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_PAGAMENTOS</em></p>
<p><em><strong>TRIGGER</strong>: TRG_ PAGAMENTOS</em></p></td>
</tr>
<tr>
<td><strong>RH_T_REMUNERACOES</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>VALOR</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>DATA_REF</td>
<td>DATE</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td>VARCHAR2(3 BYTE)</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">PRSAL_ID</td>
<td>NUMBER</td>
<td><em><strong>SIM</strong></em></td>
<td><em>RH_T_PROC_FUNCIONARIOS.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">REM_1_ID</td>
<td>NUMBER</td>
<td><em><strong>SIM</strong></em></td>
<td><em>RH_T_DEF_REMUNERACOES.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">IAC_ID</td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">IAC_DESCRICAO</td>
<td>VARCHAR2(200 BYTE)</td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">VALOR_REAL</td>
<td>NUMBER DEFAULT 0</td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">OBS</td>
<td>VARCHAR2(500 BYTE)</td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">ID_UPLOAD</td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_REMUNERACOES</p></li>
<li><p>FK: FK_REM_PROC_FUN</p></li>
<li><p>FK: FK_REM_DEF_REM</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_REMUNERACOES</em></p>
<p><em><strong>TRIGGER</strong>: TRG_REMUNERACOES</em></p></td>
</tr>
<tr>
<td
style="text-align: left;"><em><strong>RH_T_SUBSIDIO_FERIAS</strong></em></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>ANO_REFERENTE</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_SALARIO_BASE</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>MES_TRAB</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>DIAS_TRAB</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>DIAS_ANUAL</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>DIAS_FERIAS</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_SUBSIDIO</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>ESTADO</em></td>
<td>VARCHAR2(3 BYTE)</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">FUN_ID</td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS.</strong>ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">UUID</td>
<td><em>VARCHAR (100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_SUB_FERIAS</p></li>
<li><p>FK: FK_SUB_FERIA_FUN_ID(FUN_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_SUB_FERIA_</em>UUID (UUID)</p>
<p><em><strong>INDEX :</strong> IX_SUB_FERIA_ESTADO</em>
(ESTADO)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_SUBSIDIO_FERIAS</em></p>
<p><em><strong>TRIGGER</strong>: TRG_ SUBSIDIO_FERIAS</em></p></td>
</tr>
<tr>
<td
style="text-align: left;"><em><strong>RH_T_SUBSIDIO_FERIAS_DET</strong></em></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>ID</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>NUMERO</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_INICIO</em></td>
<td>DATE</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_FIM</em></td>
<td>DATE</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>ESCALAO_ID</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>MES_TRAB</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_MES</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>DIA_TRAB</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_DIA</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">UUID</td>
<td><em>VARCHAR (100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">SUB_FERIAS_ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_SUBSIDIO_FERIAS</strong>.ID</em></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_SUB_FERIAS_DET</p></li>
<li><p>FK: FK_SUB_FERIA_ID (SUB_FERIAS_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_SUB_FERIA_DET_</em>UUID
(UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_SUBSIDIO_FERIAS_DET</em></p>
<p><em><strong>TRIGGER</strong>: TRG_ SUBSIDIO_FERIAS_DET</em></p></td>
</tr>
<tr>
<td
style="text-align: left;"><em><strong>RH_T_SUBSISDIO_NATAL</strong></em></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>ID</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>FUN_ID</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>ANO_REFERENTE</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_SALARIO_BASE</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>MES_TRAB</em></td>
<td>VARCHAR(10)</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>PERC_SALARIO</em></td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>FALTAS</em></td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>PERC_FALTA</em></td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_SUBSIDIO</em></td>
<td>NUMBER</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>CHEQUE_BRINDE</em></td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>PRENDA_NATAL</em></td>
<td>NUMBER</td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>ESTADO</em></td>
<td>VARCHAR(20)</td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_SUB_NATAL</p></li>
<li><p>FK: FK_SUB_NATAL_FUN_ID(<strong>FUN_ID</strong>)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_SUB_NATAL_</em>UUID (UUID)</p>
<p><em><strong>INDEX :</strong> IX_SUB_NATAL_ESTADO</em>
(ESTADO)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_SUBSIDIO_NATAL</em></p>
<p><em><strong>TRIGGER</strong>: TRG_ SUBSIDIO_NATAL</em></p></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

## ASSIDUIDADE

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 24%" />
<col style="width: 21%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>CAMPOS</strong></th>
<th style="text-align: center;"><strong>TIPO</strong></th>
<th style="text-align: center;"><strong>OBRIGATORIEDADE</strong></th>
<th style="text-align: center;"><strong>RELACAO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><strong>RH_MOVIMENTOS</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DT_MOVIMENTO</td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>HORA_MOVIMENTO</td>
<td><em>VARCHAR2(20 BYTE)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID_COLABORADOR</td>
<td><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_FUNCIONARIO</strong>.ID_COLABORADOR</em></td>
</tr>
<tr>
<td>NOME_COLABORADOR</td>
<td><em>VARCHAR2(300 BYTE)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>TP_MOVIMENTO</td>
<td><em>VARCHAR2(20 BYTE)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>TP_MOVIMENTO_DESC</td>
<td><em>VARCHAR2(50 BYTE)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>VERIFY_MODE</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>IN_OUT_MODE</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>WORK_CODE</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DT_REGISTO</td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>USR_REGISTO</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_HORA</td>
<td><em>VARCHAR2(50 BYTE)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>PROCESSADO</td>
<td><em>NUMBER(*,0) DEFAULT 0</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>TP_MOVIMENTO_MAQUINA</td>
<td><em>VARCHAR2(20 BYTE)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>TP_MOVIMENTO_MAQUINA_DESC</td>
<td><em>VARCHAR2(20 BYTE)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID_EQUIP_CONTR_ACESSO</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td
style="text-align: left;"><strong>RH_ASSIDUIDADE_SINTESE_DIARIA</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER(16,0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FUNCIONARIO_ID</td>
<td style="text-align: left;"><em>NUMBER(16,0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_FUNCIONARIOS.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">DATA</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">MES</td>
<td style="text-align: left;"><em>NUMBER(16,0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ANO</td>
<td style="text-align: left;"><em>NUMBER(16,0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORA_PRIMEIRA_ENTRADA</td>
<td style="text-align: left;"><em>INTERVAL DAY (0) TO SECOND
(0),</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORA_ULTIMA_SAIDA</td>
<td style="text-align: left;"><em>INTERVAL DAY (0) TO SECOND
(0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORAS_TRABALHADAS</td>
<td style="text-align: left;"><em>INTERVAL DAY (0) TO SECOND
(0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORA_PRIMEIRA_SAIDA_ALMOCO</td>
<td style="text-align: left;"><em>INTERVAL DAY (0) TO SECOND
(0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORA_ULTIMA_ENTRADA_ALMOCO</td>
<td style="text-align: left;"><em>INTERVAL DAY (0) TO SECOND
(0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORAS_ALMOCO</td>
<td style="text-align: left;"><em>INTERVAL DAY (0) TO SECOND
(0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORAS_EXTRAS</td>
<td style="text-align: left;"><em>INTERVAL DAY (0) TO SECOND
(0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORAS_AUSENCIA</td>
<td style="text-align: left;"><em>INTERVAL DAY (0) TO SECOND
(0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FALTA</td>
<td style="text-align: left;"><em>NUMBER(*,0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR2(100 BYTE)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DT_REGISTO</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">USR_REGISTO</td>
<td style="text-align: left;"><em>NUMBER(16,0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DT_UPDATE</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">USR_UPDATE</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FLAG_RECECAO</td>
<td style="text-align: left;"><em>VARCHAR2(1 BYTE) DEFAULT 1</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_ASSIDUIDADES</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPO_FALTA</td>
<td style="text-align: left;"><em>VARCHAR2(240 BYTE)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_DE</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_ATE</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TF_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPREL_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORA_DE</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORA_ATE</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FERIAS_GOZADAS</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FERIAS_ACUMULADAS</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_TRANSF_FERIAS</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO_TRANSF</td>
<td style="text-align: left;"><em>VARCHAR2(10 BYTE)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORAS_EXTRA</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DS_OBSERVACAO</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_TIPO_FALTAS</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;">ESTADO</td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>UUID</em></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_TIPO_FALTA</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_TIPO_FALTA</em></p>
<p><em><strong>TRIGGER</strong>: TRG_TIPO_FALTA</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_RESPONSAVEL</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">INSTIT_ID</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>INSPSSIGOF.INSTITUICOES.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">FUN_ID</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_FUNCIONARIO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">EMAIL</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">SECAO_ID</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_SECAO.ID</em></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_RESPONSAVEL</p></li>
<li><p>FK: FK_RESPONS_FUN (FUN_ID)</p></li>
<li><p>FK: FK_RESPONS_INSTIT (INSTIT_ID)</p></li>
<li><p>FK: FK_RESPONS_SECAO (SECAO_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_RESPONSAVEL</em></p>
<p><em><strong>TRIGGER</strong>: TRG_RESPONSAVEL</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_FALTA</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DESCRICAO<strong>_</strong>MOTIVO</td>
<td style="text-align: left;"><em>VARCHAR(400)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DECISAO_RESPONSAVEL</td>
<td style="text-align: left;"><em>VARCHAR(20)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><strong>DOMAIN</strong> = PARECER_DECISAO,
REFERENCIA = PARECER_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;">RESPONSAVEL_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_RESPONSAVEL.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">OBS_RESPONSAVEL</td>
<td style="text-align: left;"><em>VARCHAR(400)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DESPACHO_RH</td>
<td style="text-align: left;"><em>VARCHAR(3)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TF_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_TIPO_FALTA.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">HORAS_AUSENCIA</td>
<td style="text-align: left;"><em>INTERVAL DAY(0) TO SECOND(0)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_INICIO</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_FIM</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FLG_DESCONTO_SAl</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em><strong>DOMAINS=</strong>
SIM_NAO_NUMBER</em></td>
</tr>
<tr>
<td style="text-align: left;">FLG_JUSTIFICATIVO</td>
<td style="text-align: left;"><em>VARCHAR(3)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em><strong>DOMAINS=</strong>
SIM_NAO</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>SINTESE_DIARIO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td
style="text-align: left;"><em>RH_ASSIDUIDADE_SINTESE_DIARIA.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">TIPREL_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_TIPOS_RELACIONAMENTO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">PEDIDO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_PEDIDO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_FALTA</p></li>
<li><p>FK: FK_FALTA_TIPREL (TIPREL_ID)</p></li>
<li><p>FK: FK_F_SINTESE_DIARIO (SINTESE_DIARIO_ID)</p></li>
<li><p>FK: FK_FALTA_RESPONSAVEL (RESPONSAVEL_ID)</p></li>
<li><p>FK: FK_FALTA_PEDIDO (PEDIDO_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_FALTA</em></p>
<p><em><strong>TRIGGER</strong>: TRG_FALTA</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_PEDIDO</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ORIGEM</td>
<td style="text-align: left;"><em>VARCHAR(10)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em><strong>RH_T_DOMAINS</strong> =
ORIGEM_REGISTO</em></td>
</tr>
<tr>
<td style="text-align: left;">TIPO_PEDIDO</td>
<td style="text-align: left;"><em>VARCHAR(20)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em><strong>RH_T_DOMAINS</strong> =
TIPO_PEDIDO</em></td>
</tr>
<tr>
<td style="text-align: left;">ETAPA</td>
<td style="text-align: left;"><em>VARCHAR(50)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em><strong>RH_T_DOMAINS</strong> =
ETAPA_PROCESSO</em></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;">ESTADO</td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>UUID</em></td>
</tr>
<tr>
<td style="text-align: left;">FUN_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td
style="text-align: left;"><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_PEDIDO</p></li>
<li><p>FK: FK_PEDIDO_FUN (FUN_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_PEDIDO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_PEDIDO</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_DISPENSA</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>TIPREL_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_TIPOS_RELACIONAMENTO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>PEDIDO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_PEDIDO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>TIPO_DISPENSA</em></td>
<td style="text-align: left;"><em>VARCHAR(50)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;">DOMAIN =MOTIVO_DISPENSA</td>
</tr>
<tr>
<td style="text-align: left;">DESCRICAO_MOTIVO</td>
<td style="text-align: left;"><em>VARCHAR(400)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>HORA_INICIO</em></td>
<td style="text-align: left;"><em>INTERVAL DAY(0) TO SECOND(0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORA_FIM</td>
<td style="text-align: left;"><em>INTERVAL DAY(0) TO SECOND(0)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DECISAO_RESPONSAVEL</td>
<td style="text-align: left;"><em>VARCHAR(20)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><strong>DOMAIN</strong> = PARECER_DECISAO,
REFERENCIA = PARECER_RESPONSAVEL</td>
</tr>
<tr>
<td style="text-align: left;">RESPONSAVEL_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_RESPONSAVEL.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">OBS_RESPONSAVEL</td>
<td style="text-align: left;"><em>VARCHAR(400)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">OBS_RH</td>
<td style="text-align: left;"><em>VARCHAR(400)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_DISPENSA</p></li>
<li><p>FK: FK_DISPENSA_TIPREL (TIPREL_ID)</p></li>
<li><p>FK: FK_DISPENSA_PEDIDO(PEDIDO_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_DISPENSA</em></p>
<p><em><strong>TRIGGER</strong>: TRG_DISPENSA</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_HORA_EXTRA</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPREL_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_TIPOS_RELACIONAMENTO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">PEDIDO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_PEDIDO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">DATA_INICIO</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_FIM</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_HORA_EXTRA</p></li>
<li><p>FK: FK_HORAS_TIPREL (TIPREL_ID)</p></li>
<li><p>FK_HORAS_PEDIDO(PEDIDO_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_HORA_EXTRA</em></p>
<p><em><strong>TRIGGER</strong>: TRG_HORA_EXTRA</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_ANO</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ANO</td>
<td style="text-align: left;"><em>VARCHAR(4)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_ANO</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_ANO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_ANO</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_FERIAS</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ANO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_ANO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">FUN_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">NUM_DIA</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ANO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_ANO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_FERIAS (ID)</p></li>
<li><p>FK: FK_FERIAS_FUN (FUN_ID)</p></li>
<li><p>FK: FK_FERIAS_ANO (ANO_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_FERIAS</em></p>
<p><em><strong>TRIGGER</strong>: TRG_FERIAS</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_FERIAS_MAPA</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FUN_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_TIPOS_RELACIONAMENTO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">DATA_INICIO</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_FIM</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ANO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_ANO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_FERIAS_MAPA (ID)</p></li>
<li><p>FK: FK_FERIAS_MAPA_FUN (FUN_ID)</p></li>
<li><p>FK: FK_FERIAS_MAPA_ANO (ANO_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_FERIAS_MAPA</em></p>
<p><em><strong>TRIGGER</strong>: TRG_FERIAS_MAPA</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_FERIAS_GOZADAS</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ANO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FUN_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">NUM_DIA</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_INICIO</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_FIM</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPREL_ID_SUBSTITUIDO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_TIPO_RELACIONAMENTO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">FERIAS_GOZADAS_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_FERIAS_GOZADAS.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">MOTIVO_ALTERACAO</td>
<td style="text-align: left;"><em>VARCHAR(400)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">OBS_INFO_CONVENIENCIA</td>
<td style="text-align: left;"><em>VARCHAR(400)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DECISAO_RESPONSAVEL</td>
<td style="text-align: left;"><em>VARCHAR(3)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">RESPONSAVEL_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">OBS_RESPONSAVEL</td>
<td style="text-align: left;"><em>VARCHAR(400)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DECISAO_RH</td>
<td style="text-align: left;"><em>VARCHAR(3)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">OBS_RH</td>
<td style="text-align: left;"><em>VARCHAR(400)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_FERIAS_GOZ (ID)</p></li>
<li><p>FK: FK_FERIAS_GOZ_FUN (FUN_ID)</p></li>
<li><p>FK: FK_FERIAS_GOZ_ANO (ANO_ID)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_FERIAS_GOZADA</em></p>
<p><em><strong>TRIGGER</strong>: TRG_FERIAS_GOZADA</em></p></td>
</tr>
<tr>
<td
style="text-align: left;"><strong>RH_T_ABONOS_BENEFICIOS</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_INICIO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_FIM</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>FUN_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_FUNCIONARIOS.ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>PARAM_SIT_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;">RH_T_PARAM_SITUACAO.ID</td>
</tr>
<tr>
<td style="text-align: left;"><em>PARAM_SIT_DET_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;">RH_T_PARAM_SITUACAO_DET.ID</td>
</tr>
<tr>
<td style="text-align: left;"><em>OBS</em></td>
<td style="text-align: left;"><em>VARCHAR(500)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_ABONO_BENEFICIO (ID)</p></li>
<li><p>FK: FK_ABONO_FUN (FUN_ID)</p></li>
<li><p>FK: FK_ABONO_SIT (<em>PARAM_SIT_ID</em>)</p></li>
<li><p>FK: FK_ABONO_SIT_DET (<em>PARAM_SIT_DET_ID</em>)</p></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_ABONO_BENEFICIO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_ABONO_BENEFICIO</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_AUSENCIA</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>PARAM_SIT_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_SITUACAO</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>REFERENCIA_NAME</em></td>
<td style="text-align: left;"><em>VARCAHR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>NOME DE OBJECTO NA QUAL DEU ORIGEM O
REGISTO</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>REFERENCIA_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>ID DE TABELA NA QUAL DEU ORIGEM O
REGISTO</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>OBS</em></td>
<td style="text-align: left;"><em>VARCAHR(300)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_INICIO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_FIM</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_AUSENCIA (ID)</p></li>
<li><p>FK: FK_SIT_AUSENCIA (PARAM_SIT_ID)</p></li>
<li></li>
</ul></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>: SEQ_AUSENCIA</em></p>
<p><em><strong>TRIGGER</strong>: TRG_AUSENCIA</em></p></td>
</tr>
<tr>
<td
style="text-align: left;"><strong>RH_T_REGULARIZACAO_SDO</strong></td>
<td style="text-align: left;"><strong>TIPO</strong></td>
<td style="text-align: left;"><strong>OBRIGATORIEDADE</strong></td>
<td style="text-align: left;"><strong>RELACAO</strong></td>
</tr>
<tr>
<td style="text-align: left;"><em>ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">MES_REFERENTE</td>
<td style="text-align: left;"><em>VARCHAR2(10)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">SDO_RECEBIDO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">VALOR_RETROATIVO_SALARIO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">VALOR_RETROATIVO_SDO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>PROC_FUN_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;">RH_T_PROC_FUNCIONARIOS.ID</td>
</tr>
<tr>
<td style="text-align: left;"><em>ABONO_BENEFICIO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_ABONOS_BANEFICIOS.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p>CONSTRAINTS:</p>
<ul>
<li><p>PK : PK_REGULARIZACAO (ID)</p></li>
<li><p>FK: FK_REGULAPROC_FUN (<em>PROC_FUN_ID</em>)</p></li>
<li><p>FK: FK_REGULAPROC_FUN (<em>ABONO_BENEFICIO_ID</em>)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_REGULARIZA_SDO_</em>UUID
(UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_REGULARIZACAO_SDO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_REGULARIZACAO_SDO</em></p></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
</tbody>
</table>

## EMPRESTIMO

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 23%" />
<col style="width: 21%" />
<col style="width: 25%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>CAMPOS</strong></th>
<th style="text-align: center;"><strong>TIPO</strong></th>
<th style="text-align: center;"><strong>OBRIGATORIEDADE</strong></th>
<th style="text-align: center;"><strong>RELACAO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td
style="text-align: left;"><strong>RH_T_PARAM_EMPRESTIMO</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">CARR_PCCS_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">VALOR_LIMITE</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">NUMERO_LIMITE</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_PARAM_EMPRESTIMO</p></li>
<li><p>FK: FK_CARR_P_EMPREST (CARR_PCCS_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PARAM_EMPREST_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_PARAM_EMPRESTIMO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_PARAM_EMPRESTIMO</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_EMPRESTIMO</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>TIPREL_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_TIPOS_RELACIONAMENTO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>MARCA</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>ANO_FABRICO</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>CILINCRADA</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>TIPO_VIATURA</em></td>
<td style="text-align: left;"><em>VARCHAR(50)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>COMBUSTIVEL</em></td>
<td style="text-align: left;"><em>VARCHAR(50)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>ESTADO_VIATURA</em></td>
<td style="text-align: left;"><em>VARCHAR(50)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_EMPRESTIMO</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_DIVIDA</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>NR_PRESTACAO</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>TIPO_EMPRESTIMO</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_INICIO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_FIM</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>JURO</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">VALOR_PRESTACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DESC_CABIMENTACAO_ORCAMENTAL</em></td>
<td style="text-align: left;"><em>VARCHAR(300)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DESC_TAXA_ESFORCO</em></td>
<td style="text-align: left;"><em>VARCHAR(500)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">EMPRESTIMO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_EMPRESTIMO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">VERSAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PEDIDO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_PEDIDO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">RENOGOCIACAO</td>
<td style="text-align: left;"><em>VARCHAR(10)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>TM_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em>INPSSIGOF.RH_TIPOS_MOVIMENTO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>FINALIDADE</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_ADIANTADO</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>TIPO_RENOGOCIACAO</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>MOTIVO_FECHO</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>VALOR_REFORCO</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em><strong>VALOR_PAGO</strong></em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em><strong>MOTIVO</strong></em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td
style="text-align: left;"><em><strong>TIPO_SITUACAO</strong></em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">BANCO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"><em>RH_T_BANCO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">NIB</td>
<td style="text-align: left;"><em>VARCHAR(21)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">SWIFT</td>
<td style="text-align: left;"><em>VARCHAR(50)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_EMPRESTIMO</p></li>
<li><p>FK: FK_TIPREL_EMPREST (TIPREL_ID)</p></li>
<li><p><em>FK: FK_EMP_EMP_ID (EMPRESTIMO_ID)</em></p></li>
<li><p><em>FK: FK_PEDIDO_EMPREST (PEDIDO_ID)</em></p></li>
<li><p><em>FK: FK_TM_EMPREST (TM_ID)</em></p></li>
<li><p><em>FK: FK_BANC_EMPREST(BANCO_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_EMPREST_</em>UUID (UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_EMPRESTIMO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_EMPRESTIMO</em></p></td>
</tr>
<tr>
<td
style="text-align: left;"><strong>RH_T_PLANO_FINANCEIRO</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>ID</em></td>
<td style="text-align: left;"><em>NUMER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>EMPRESTIMO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">NR_ORDEM_PRESTACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_PAGAMENTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">VALOR_PRINCIPAL</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">VALOR_JUROS</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>FLG_PAGO</strong></td>
<td style="text-align: left;"><em>VARCHAR(10)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><strong>VALOR_PAGO</strong></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DEFP_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_DEF_PAGAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_PLANO_EMPRESTIMO</p></li>
<li><p>FK: FK_DEF_PLANOEMPREST (DEFP_ID)</p></li>
<li><p><em>FK: FK_EMP_</em>PLANOEMPREST
<em>(EMPRESTIMO_ID)</em></p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PLANOEMPREST_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_PLANO_FINANCEIRO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_PLANO_FINANCEIRO</em></p></td>
</tr>
<tr>
<td style="text-align: left;"><strong>RH_T_PEDIDO_DECISAO</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PEDIDO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"><em>RH_T_PEDIDO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">DECISAO</td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">OBS</td>
<td style="text-align: left;"><em>VARCHAR(500)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ETAPA</td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">REFERENCIA</td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_DECISAO_PEDIDO</p></li>
<li><p>FK: FK_PED_DPEDIDO (PEDIDO_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_DECISAO_PEDIDO_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_DECISAO_PEDIDO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_DECISAO_PEDIDO</em></p></td>
</tr>
</tbody>
</table>

## PROGRESSAO / PROMOÇÃO

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 24%" />
<col style="width: 21%" />
<col style="width: 1%" />
<col style="width: 23%" />
</colgroup>
<thead>
<tr>
<th
style="text-align: left;"><strong>RH_T_SIM_EVOLUCAO_CARREIRA</strong></th>
<th style="text-align: left;"></th>
<th style="text-align: left;"></th>
<th colspan="2" style="text-align: left;"></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">CARREIRA_ID_DE</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">ESCALAO_ID_DE</td>
<td style="text-align: left;"><em>VARCHAR2(10)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESCALAO_ID_PARA</td>
<td style="text-align: left;"><em>VARCHAR2(10)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_REFERENTE</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPREL_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">OBSERVACAO</td>
<td style="text-align: left;"><em>VARCHAR2(500)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FLG_HISTORICO</td>
<td style="text-align: left;"><em>VARCHAR2(SIM)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPO</td>
<td style="text-align: left;"><em>VARCHAR2(SIM)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO_MEDIA</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_SIM_EVOL_CARR</p></li>
<li><p>FK: FK_SIM_EVOL_CARR_CARR_DE</p></li>
<li><p>FK: FK_SIM_EVOL_CARR_TIPRE</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_SIM_EVOL_CARR_</em>UUID
(UUID)</p></td>
<td colspan="3"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>SIM_EVOLUCAO_CARREIRA</p>
<p><em><strong>TRIGGER</strong>: TRG_SIM_EVOLUCAO_CARREIRA</em></p></td>
</tr>
<tr>
<td
style="text-align: left;"><strong>RH_T_VAL_EVOLUCAO_CARREIRA</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">CARREIRA_ID_DE</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">ESCALAO_ID_DE</td>
<td style="text-align: left;"><em>VARCHAR2(10)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESCALAO_ID_PARA</td>
<td style="text-align: left;"><em>VARCHAR2(10)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_REFERENTE</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPREL_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_TIPOS_RELACIONAMENTO</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">OBSERVACAO</td>
<td style="text-align: left;"><em>VARCHAR2(500)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPO</td>
<td style="text-align: left;"><em>VARCHAR2(SIM)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FLG_HISTORICO</td>
<td style="text-align: left;"><em>VARCHAR(3)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO_MEDIA</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_VAL_EVOL_CARR</p></li>
<li><p>FK: FK_VAL_EVOL_CARR_CARR_DE</p></li>
<li><p>FK: FK_VAL_EVOL_CARR_TIPREL</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_VAL_EVOL_CARR_</em>UUID
(UUID)</p></td>
<td colspan="3"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_VAL</em>_EVOLUCAO_CARREIRA</p>
<p><em><strong>TRIGGER</strong>: TRG_VAL_EVOLUCAO_CARREIRA</em></p></td>
</tr>
<tr>
<td
style="text-align: left;"><strong>RH_T_EVOLUCAO_CARREIRA</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">CARREIRA_ID_DE</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">CARREIRA_ID_PARA</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td colspan="2"
style="text-align: left;"><em><strong>RH_T_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">ESCALAO_ID_DE</td>
<td style="text-align: left;"><em>VARCHAR2(10)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESCALAO_ID_PARA</td>
<td style="text-align: left;"><em>VARCHAR2(10)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_REFERENTE</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPREL_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2"
style="text-align: left;"><em>RH_T_TIPOS_RELACIONAMENTO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">OBSERVACAO</td>
<td style="text-align: left;"><em>VARCHAR2(500)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">TIPO</td>
<td style="text-align: left;"><em>VARCHAR2(SIM)</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>ORDEM_SERVICO_ID</em></td>
<td style="text-align: left;"><em>VARCHAR(3)</em></td>
<td style="text-align: left;"></td>
<td colspan="2"
style="text-align: left;"><em>RH_T_ORDEM_SERVICO.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO_MEDIA</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td colspan="2" style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_EVOL_CARR</p></li>
<li><p>FK: FK_EVOL_CARR_CARR_DE</p></li>
<li><p>FK: FK_EVOL_CARR_PARA</p></li>
<li><p>FK: FK_EVOL_CARR_TIPREL</p></li>
<li><p>FK: FK_EVOL_CARR_OS</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_EVOL_CARR_</em>UUID (UUID)</p></td>
<td colspan="3"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>EVOLUCAO_CARREIRA</p>
<p><em><strong>TRIGGER</strong>: TRG_EVOLUCAO_CARREIRA</em></p></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"></td>
<td colspan="3" style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td colspan="2" style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
</tbody>
</table>

## AVALIAÇÃO DESEMPENHO

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 24%" />
<col style="width: 23%" />
<col style="width: 23%" />
</colgroup>
<thead>
<tr>
<th>RH_T_PARAM_OBJETIVO_DET</th>
<th></th>
<th style="text-align: left;"></th>
<th style="text-align: left;"></th>
</tr>
</thead>
<tbody>
<tr>
<td><em>ID</em></td>
<td><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td><em>ANO</em></td>
<td><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td>PESO_COMPORTAMENTAIS</td>
<td><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td>PESO_TECNICA</td>
<td><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PONDERACAO_OBJETIVO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PONDERACAO_COMPETENCIA</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PONDERACAO_ATITUDE_PESS</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_PARAM_OBJETIVO_DET</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PARAM_OBJECTO_DET_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>PARAM_OBJETIVO_DET</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>PARAM_OBJETIVO_DET</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_PARAM_OBJETIVO</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>PARAM_OBJ_DET_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_OBJECTIVO_DET.</strong>ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>CARGO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_CARGO</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;"><em>CARR_PCCS_IS</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">NUMERO_ORDEM</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ABRAGENCIA</td>
<td style="text-align: left;"><em>VARCHAR2(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">INSTIT_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>INPSSIGOF.INSTITUICOES.</strong>ID</em></td>
</tr>
<tr>
<td style="text-align: left;">SECCAO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_SECCAO.ID</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">DESCRICAO</td>
<td style="text-align: left;"><em>VARCHAR2 (300)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td style="text-align: left;"><em>VARCHAR2 (300)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PONDERACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">COMPONENTE</td>
<td style="text-align: left;"><em>VARCHAR2 (100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_PARAM_OBJETIVO</p></li>
<li><p>FK: FK_PARAM_OBJETIVO_DET (<em>PARAM_OBJ_DET_ID</em>)</p></li>
<li><p>FK: FK_PARAM_OBJETIVO_CARGO (<em>CARGO_ID</em>)</p></li>
<li><p>FK: FK_PARAM_OBJETIVO_INSTIT (<em>INSTIT_ID</em>)</p></li>
<li><p>FK: FK_PARAM_OBJETIVO_SECC (seccao<em>_ID</em>)</p></li>
<li><p>FK: FK_PARAM_OBJETIVO_carr (CARR_PCCS_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_PARAM_OBJECTO_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>PARAM_OBJETIVO</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>PARAM_OBJETIVO</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_PARAM_ESCALA</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">NIVEL</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">QUALITATIVA</td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DESCRICAO</td>
<td style="text-align: left;"><em>VARCHAR(300)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">QUANTITATIVA_DE</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">QUANTITATIVA_ATE</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_PARAM_ESCALA</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_</em>PARAM_ESCALA<em>_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>PARAM_ESCALA</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>PARAM_ESCALA</p></td>
</tr>
<tr>
<td
style="text-align: left;"><strong>RH_T_PARAM_MANUAL_FUNC</strong></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">CARGO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_CARGO.ID</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">CARR_PCCS_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_CARREIRA.ID</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">SECCAO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_SECCAO.ID</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">INSTIT_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>INPSSIGOF.INSTITUICOES.ID</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">DESCRICAO</td>
<td style="text-align: left;"><em>VARCHAR(300)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_PARAM_MANUAL_F</p></li>
<li><p>FK_PARAM_MANUAL_INST(INSTIT_ID )</p></li>
<li><p>FK_PARAM_MANUAL_SEC(SECCAO_ID)</p></li>
<li><p>FK_PARAM_MANUAL_CARGO(CARGO_ID)</p></li>
<li><p>FK_PARAM_MANUAL_CARGO(CARR_PCCS_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_</em>PARAM_MANUAL_F<em>_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>PARAM_MANUAL_FUNC</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>PARAM_MANUAL_FUNC</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_AVD</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ANO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">INSTIT_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td
style="text-align: left;"><em><strong>INPSSIGOF.INSTITUICOES</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">SECCAO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_SECCAO.ID</strong></em></td>
</tr>
<tr>
<td style="text-align: left;">CARGO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_CARGO</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">CARR_PCCS_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_CARREIRA</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">SEMESTRE</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO_FINAL</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">FUN_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td
style="text-align: left;"><em><strong>RH_T_FUNCIONARIOS.</strong>ID</em></td>
</tr>
<tr>
<td style="text-align: left;">PESO_COMPORTAMENTAIS</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PESO_TECNICA</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO_OBJECTIVO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO_COMPETENCIA</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO_ATITUDE_PESS</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO_QUALITATIVA</td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">OBSERVACAO_GERAL</td>
<td style="text-align: left;"><em>VARCHAR(500)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DESCRICAO_PLANO</td>
<td style="text-align: left;"><em>VARCHAR(500)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DATA_INICIO_ENTREVISTA</td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORA_INICIO_ENTREVISTA</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">HORA_FIM_ENTREVISTA</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PARECER_COLABORADOR</td>
<td style="text-align: left;"><em>VARCHAR(500)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">JUSTIFICACAO_MOTIVO</td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">OBS_COMISSAO_EXEC</td>
<td style="text-align: left;"><em>VARCHAR(500)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_AVD</p></li>
<li><p>FK: FK_AVD_FUN (FUN_ID)</p></li>
<li><p>FK: FK_AVD_INSTIT (INSTIT_ID)</p></li>
<li><p>FK: FK_AVD_CARGO (CARGO_ID)</p></li>
<li><p>FK: FK_AVD_CARR (CARR_PCCS_ID)</p></li>
<li><p>FK: FK_AVD_SECCAO (SECCAO_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_AVD_</em>UUID (UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>AVD</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>AVD</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_AVD_OBJECTIVO</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVD_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;">RH_T_AVD.ID</td>
</tr>
<tr>
<td style="text-align: left;">PARAM_OBJECTIVO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_OBJECTIVO</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">NUMERO_ORDEM</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ABRAGENCIA</td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">OBJECTIVOS</td>
<td style="text-align: left;"><em>VARCHAR(300)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">KPI</td>
<td style="text-align: left;"><em>VARCHAR(300)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">META</td>
<td style="text-align: left;"><em>VARCHAR(300)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">REALIZADO</td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AUTO_REALIZADO</td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AUTO_AVALIACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_AVD_OBJETIVO</p></li>
<li><p>FK: FK_AVD_OBJETIVO_AVD (AVD_ID)</p></li>
<li><p>FK: FK_AVD_PARAM_OBJETIVO (PARAM_OBJECTIVO_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_</em>AVD_OBJETIVO<em>_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>AVD_OBJECTIVO</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>AVD_OBJECTIVO</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_AVD_COMPETENCIA</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVD_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;">RH_T_AVD.ID</td>
</tr>
<tr>
<td style="text-align: left;">NUMERO_ORDEM</td>
<td style="text-align: left;"><em>NUMER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">DESCRICAO</td>
<td style="text-align: left;"><em>VARCHAR(300)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ABRAGENCIA</td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PONDERACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVALIACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">COMPONENTE</td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PARAM_OBJECTIVO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_OBJECTIVO</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">AUTO_AVALIACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_AVD_COMPTEC</p></li>
<li><p>FK: FK_AVD_COMPTEC_AVD (AVD_ID)</p></li>
<li><p>FK: FK_AVD_COMPTEC_OBJETIVO (PARAM_OBJECTIVO_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_</em>AVD_COMPTEC<em>_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>AVD_COMPORTAMENTAL</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>AVD_COMPORTAMENTAL</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_AVD_ATUTUDE_PESSOAL</td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AVD_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;">RH_T_AVD.ID</td>
</tr>
<tr>
<td style="text-align: left;">PONDERACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">ABRAGENCIA</td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">AUTO_AVALIACAO</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">PARAM_OBJECTIVO_ID</td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td
style="text-align: left;"><em><strong>RH_T_PARAM_OBJECTIVO</strong>.ID</em></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO</td>
<td style="text-align: left;"><em>VARCHAR(1)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_REGISTO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_REGISTO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_ID</em></td>
<td style="text-align: left;"><em>NUMBER</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>USER_ALTERACAO_NAME</em></td>
<td style="text-align: left;"><em>VARCHAR(200)</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>DATA_ALTERACAO</em></td>
<td style="text-align: left;"><em>DATE</em></td>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;"><em>UUID</em></td>
<td style="text-align: left;"><em>VARCHAR(100)</em></td>
<td style="text-align: left;"><em>SIM</em></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_AVD_AT_PESS</p></li>
<li><p>FK: FK_AVD_AT_PESS_AVD (AVD_ID)</p></li>
<li><p>FK: FK_AVD_AT_PESS_OBJETIVO (PARAM_OBJECTIVO_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_</em>AVD_AT_PESS<em>_</em>UUID
(UUID)</p></td>
<td colspan="2"
style="text-align: left;"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>AVD_ATITUDE_PESS</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>AVD_ATITUDE_PESS</p></td>
</tr>
</tbody>
</table>

## MISSÃO SERVIÇO

<table style="width:100%;">
<colgroup>
<col style="width: 28%" />
<col style="width: 24%" />
<col style="width: 22%" />
<col style="width: 24%" />
</colgroup>
<thead>
<tr>
<th><em><strong>RH_T_MISSAO_SERVICO</strong></em></th>
<th></th>
<th></th>
<th></th>
</tr>
</thead>
<tbody>
<tr>
<td><em>ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>NR_MISSAO</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>PAIS_DESTINO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>INPSGLOBAL.GLB_T_GEOGRAFIA.ID</em></td>
</tr>
<tr>
<td>FLG_DESTINO</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DESCRICAO_DESTINO</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_INICIO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_FIM</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>NR_DIAS</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>AUTORIZADO_POR</td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>DATA_AUTORIZACAO</td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>ETAPA</td>
<td><em>VARCHAR(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>MOTIVO_CANCELAMENTO</td>
<td><em>VARCHAR(500)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_MISSAO_SERVICO</p></li>
<li><p>FK: FK_MISSAO_SERV_PAIS()</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_MISSAO_SERV_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_MISSAO_SERVICO</em></p>
<p><em><strong>TRIGGER</strong>: TRG_MISSAO_SERVICO</em></p></td>
</tr>
<tr>
<td><strong>RH_T_MISSAO_PRESTADOR</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>ENT_ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em>INPSSIGOF.ENTIDADE.ID</em></td>
</tr>
<tr>
<td>NOME</td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>EMAIL</td>
<td><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>MISSAO_SERV_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_MISSAO_SERVICO.</strong>ID</em></td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_MISSAO_PRESTADOR</p></li>
<li><p>FK: FK_MIS_PRESTADOR_ENTID (ENT_ID)</p></li>
<li><p>FK: FK_MIS_PRESTADOR_SERV (<em>MISSAO_SERV_ID</em>)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_MISSAO_PREST_</em>UUID
(UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_MISSAO_PRESTADOR</em></p>
<p><em><strong>TRIGGER</strong>: TRG_MISSAO_PRESTADOR</em></p></td>
</tr>
<tr>
<td><em><strong>RH_T_MISSAO_COLABORADOR</strong></em></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FUN_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_FUNCIONARIOS</strong>.ID</em></td>
</tr>
<tr>
<td><em>NUM_DOCUMENTO</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>MISSAO_SERV_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td><em><strong>RH_T_MISSAO_SERVICO</strong>.ID</em></td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>VARCHAR(1)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td><em>VARCHAR(100)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_MISSAO_COLABORADOR</p></li>
<li><p>FK: FK_MIS_COLAB_FUN (FUN_ID)</p></li>
<li><p>FK: FK_MIS_PRESTADOR_MISS (<em>MISSAO_SERV_ID</em>)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_MISSAO_COLAB_</em>UUID
(UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_MISSAO_COLABORADOR</em></p>
<p><em><strong>TRIGGER</strong>: TRG_MISSAO_COLABORADOR</em></p></td>
</tr>
<tr>
<td><strong>RH_T_MISSAO_REQUISICAO</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>MISSAO_PREST_ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><strong>RH_T_MISSAO_PRESTADOR.</strong>ID</td>
</tr>
<tr>
<td>MISSAO_COLAB_ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_MISSAO_COLABORADOR</strong>.ID</em></td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td><em>VARCHAR(100)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_MISSAO_REQUISICAO</p></li>
<li><p>FK: FK_MIS_REQUIS_PRES (MISSAO_PREST_ID)</p></li>
<li><p>FK: FK_MIS_REQUIS_COLAB (MISSAO_COLAB_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_MISSAO_REQUIS_</em>UUID
(UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em><strong>MISSAO_REQUISICAO</strong></p>
<p><em><strong>TRIGGER</strong>:
TRG_</em><strong>MISSAO_REQUISICAO</strong></p></td>
</tr>
<tr>
<td><strong>RH_T_MISSAO_LOGISTICA</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>PRESTADOR_SERV_ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><strong>RH_T_MISSAO_PRESTADOR.</strong>ID</td>
</tr>
<tr>
<td>NOME_SEGURADORA</td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ENT_ID</td>
<td><em>NUMBER</em></td>
<td></td>
<td><em><strong>INPSSIGOF.ENTIDADES</strong>.ID</em></td>
</tr>
<tr>
<td>VALOR_TOTAL</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>REFERENCIA</td>
<td><em>VARCHAR(100)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>MOEDA</td>
<td><em>VARCHAR(50)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>LUGAR_HOSPEDAGEM</td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>FLG_ALIMENTACAO</td>
<td><em>VARCHAR(3)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>VALOR_DIARIO</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_INICIO</td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>DATA_FIM</td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>NR_DIAS</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>MISSAO_SERV_ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_MISSAO_SERVICO</strong>.ID</em></td>
</tr>
<tr>
<td>FLG_ALOJAMENTO</td>
<td><em>VARCHAR(3)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>CAB_ID</td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO_CABIMENTO</td>
<td><em>VARCHAR(100)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_MISSAO_lOGISTICA</p></li>
<li><p>FK: FK_MIS_LOGIST_SERV (MISSAO_SERV_ID)</p></li>
<li><p>FK: FK_MIS_LOGIST_PREST (PRESTADOR_SERV_ID )</p></li>
<li><p>FK: FK_MIS_LOGIST_ENT (ENT_ID)</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_MISSAO_LOGIST_</em>UUID
(UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>MISSAO_lOGISTICA</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>MISSAO_lOGISTICA</p></td>
</tr>
<tr>
<td><strong>RH_T_MISSAO_LOGISTICA_DET</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>MISSAO_LOGIST_ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><strong>RH_T_MISSAO_LOGISTICA.</strong>ID</td>
</tr>
<tr>
<td>MISSAO_COLAB_ID</td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><em><strong>RH_T_MISSAO_COLABORADOR</strong></em></td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>VARCHAR(3)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_MISSAO_lOGISTICA_DET</p></li>
<li><p>FK: FK_MIS_LOGIST_DET_LOG(MISSAO_LOGIST_ID)</p></li>
<li><p>FK: FK_MIS_LOGIST_DET_COLAB (MISSAO_COLAB_ID )</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_MISSAO_LOGIST_DET_</em>UUID
(UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>MISSAO_lOGISTICA_DET</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>MISSAO_lOGISTICA_DET</p></td>
</tr>
<tr>
<td></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td></td>
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

## Aumento Salarial 

<table style="width:100%;">
<colgroup>
<col style="width: 28%" />
<col style="width: 24%" />
<col style="width: 22%" />
<col style="width: 24%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>CAMPOS</strong></th>
<th style="text-align: center;"><strong>TIPO</strong></th>
<th style="text-align: center;"><strong>OBRIGATORIEDADE</strong></th>
<th style="text-align: center;"><strong>RELACAO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td><strong>RH_T_AUMENTO_SALARIAL</strong></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DESCRICAO</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>MOTIVO</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REFERENTE</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>FLG_RETROATIVO</em></td>
<td><em>VARCHAR(3)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_RETROATIVO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>FORMA_AUMENTO</em></td>
<td><em>VARCHAR(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>VALOR</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>AUMENTO_GRELHA_SALARIAL</em></td>
<td><em>VARCHAR(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>CRITERIOS_ELEGIBILIDADE</em></td>
<td><em>VARCHAR(50)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_AUMENTO_SALARIAL</p></li>
</ul>
<p><em><strong>INDEX :</strong> IX_</em>AUMENTO_SALARIAL<em>_</em>UUID
(UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>AUMENTO_SALARIAL</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>AUMENTO_SALARIAL</p></td>
</tr>
<tr>
<td>RH_T_AUMENTO_SALARIAL_DET</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>AUMENTO_SALARIAL_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td><strong><em>RH_T_</em>AUMENTO_SALARIAL</strong>.ID</td>
</tr>
<tr>
<td><em>INSTIT_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>SECCAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>VINCULO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>SITUACAO_LABORAL_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>LOCAL_TRAB_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>SALARIO_DE</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>SALARIO_ATE</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td>ESTADO</td>
<td><em>VARCHAR(1)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>DATA_REGISTO</em></td>
<td><em>DATE</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_ID</em></td>
<td><em>NUMBER</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_REGISTO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_ID</em></td>
<td><em>NUMBER</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>USER_ALTERACAO_NAME</em></td>
<td><em>VARCHAR(200)</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>DATA_ALTERACAO</em></td>
<td><em>DATE</em></td>
<td></td>
<td></td>
</tr>
<tr>
<td><em>UUID</em></td>
<td><em>VARCHAR(100)</em></td>
<td><em>SIM</em></td>
<td></td>
</tr>
<tr>
<td colspan="2"><p><strong>CONSTRAINTS</strong>:</p>
<ul>
<li><p>PK : PK_AUMENTO_SALARIAL_DET</p></li>
<li><p>FK: FK_AUMENTO_SALARIAL (<em>AUMENTO_SALARIAL_ID</em>)</p></li>
</ul>
<p><em><strong>INDEX :</strong>
IX_</em>AUMENTO_SALARIAL_DET<em>_</em>UUID (UUID)</p></td>
<td colspan="2"><p><em><strong>SEQUENCIA</strong>:
SEQ_</em>AUMENTO_SALARIAL_DET</p>
<p><em><strong>TRIGGER</strong>: TRG_</em>AUMENTO_SALARIAL_DET</p></td>
</tr>
</tbody>
</table>

# DOMINIO

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 36%" />
<col style="width: 26%" />
<col style="width: 17%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">DOMAINS</th>
<th style="text-align: center;">VALOR</th>
<th style="text-align: center;">DESCRIÇÃO</th>
<th style="text-align: center;">REFERENCIA</th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">GENERO</td>
<td><ul>
<li><p>F</p></li>
<li><p>M</p></li>
</ul></td>
<td><ul>
<li><p>FEMININO</p></li>
<li><p>MASCULINO</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO_CIVIL</td>
<td><ul>
<li><p>S</p></li>
<li><p>C</p></li>
<li><p>D</p></li>
</ul></td>
<td><ul>
<li><p>SOLTEIRA(O)</p></li>
<li><p>CASADA(O)</p></li>
<li><p>DIVORCIADA(O)</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">TP_CONTACTO</td>
<td><ul>
<li><p>TELEMOVEL</p></li>
<li><p>TELEFONE</p></li>
<li><p>EMAIL</p></li>
</ul></td>
<td><ul>
<li><p>Telemóvel</p></li>
<li><p>Telefone</p></li>
<li><p>E-mail</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">GRAUS_DE_PARENTESCO</td>
<td><ul>
<li><p>MAE</p></li>
<li><p>CONJUGE</p></li>
<li><p>CHEFE_DE_AGREGADO</p></li>
<li><p>OUTRO_PARENTESCO</p></li>
<li><p>PAI</p></li>
<li><p>FILHO</p></li>
<li><p>IRMAO</p></li>
<li><p>NETA_O</p></li>
<li><p>NORA</p></li>
<li><p>GENRO</p></li>
<li><p>SOBRINHO</p></li>
<li><p>ENTEADO</p></li>
<li><p>FILHA</p></li>
<li><p>BISNETO</p></li>
<li><p>ENTEADA</p></li>
<li><p>SOBRINHA</p></li>
<li><p>IRMA</p></li>
</ul></td>
<td><ul>
<li><p>Mãe</p></li>
<li><p>Conjuge</p></li>
<li><p>Chefe de Agregado</p></li>
<li><p>Outro parentesco</p></li>
<li><p>Pai</p></li>
<li><p>Filho</p></li>
<li><p>Irmão</p></li>
<li><p>Neta/o</p></li>
<li><p>Nora</p></li>
<li><p>Genro</p></li>
<li><p>Sobrinho</p></li>
<li><p>Enteado</p></li>
<li><p>Filha</p></li>
<li><p>Bisneto</p></li>
<li><p>Enteada</p></li>
<li><p>Sobrinha</p></li>
<li><p>Irmã</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">DEPENDENCIA</td>
<td><p>Subsitituir pelo DOMINIO</p>
<p>SIM_NAO_NUMBER</p></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">MEMBRO_AGR</td>
<td><p>Subsitituir pelo DOMINIO</p>
<p>SIM_NAO_NUMBER</p></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">AREA_FORMACAO</td>
<td><ul>
<li><p>ADMINISTRACAO_GESTAO</p></li>
<li><p>CONTABILIDADE</p></li>
<li><p>ECONOMIA</p></li>
<li><p>GESTAO_DE_RECURSOS_HUMANOS</p></li>
<li><p>GESTAO_PUBLICA</p></li>
<li><p>DIREITO</p></li>
<li><p>RELACOES_INTERNACIONAIS</p></li>
<li><p>MARKETING</p></li>
<li><p>PUBLICIDADE_E_RELACOES_PUBLICAS</p></li>
<li><p>COMUNICACAO_SOCIAL</p></li>
<li><p>JORNALISMO</p></li>
<li><p>LINGUAS_ESTUDOS_LINGUISTICOS</p></li>
<li><p>PSICOLOGIA</p></li>
<li><p>SOCIOLOGIA</p></li>
<li><p>SERVICO_SOCIAL</p></li>
<li><p>EDUCACAO_CIENCIAS_DA_EDUCACAO</p></li>
<li><p>EDUCACAO_FISICA_DESPORTO</p></li>
<li><p>ENGENHARIA_CIVIL</p></li>
<li><p>ENGENHARIA_INFORMATICA_CIENCIAS_DA_COMPUTACAO</p></li>
<li><p>ENGENHARIA_ELETRONICA</p></li>
<li><p>ENGENHARIA_AMBIENTAL</p></li>
<li><p>ENGENHARIA_INDUSTRIAL</p></li>
<li><p>TECNOLOGIAS_DE_INFORMACAO</p></li>
<li><p>SISTEMAS_DE_INFORMACAO</p></li>
<li><p>SEGURANCA_INFORMATICA</p></li>
<li><p>MATEMATICA</p></li>
<li><p>ESTATISTICA</p></li>
<li><p>BIOLOGIA_CIENCIAS_BIOLOGICAS</p></li>
<li><p>ARQUITETURA_URBANISMO</p></li>
<li><p>DESIGN_GRAFICO</p></li>
<li><p>DESIGN_DE_PRODUTO</p></li>
<li><p>ARTES_VISUAIS</p></li>
<li><p>COMERCIO_INTERNACIONAL</p></li>
</ul></td>
<td><ul>
<li><p>Administração / Gestão</p></li>
<li><p>Contabilidade</p></li>
<li><p>Economia</p></li>
<li><p>Gestão de Recursos Humanos</p></li>
<li><p>Gestão Pública</p></li>
<li><p>Direito</p></li>
<li><p>Relações Internacionais</p></li>
<li><p>Marketing</p></li>
<li><p>Publicidade e Relações Públicas</p></li>
<li><p>Comunicação Social</p></li>
<li><p>Jornalismo</p></li>
<li><p>Línguas / Estudos Linguísticos</p></li>
<li><p>Psicologia</p></li>
<li><p>Sociologia</p></li>
<li><p>Serviço Social</p></li>
<li><p>Educação / Ciências da Educação</p></li>
<li><p>Educação Física / Desporto</p></li>
<li><p>Engenharia Civil</p></li>
<li><p>Engenharia Informática / Ciências da Computação</p></li>
<li><p>Engenharia Eletrónica</p></li>
<li><p>Engenharia Ambiental</p></li>
<li><p>Engenharia Industrial</p></li>
<li><p>Tecnologias de Informação</p></li>
<li><p>Sistemas de Informação</p></li>
<li><p>Segurança Informática</p></li>
<li><p>Matemática</p></li>
<li><p>Estatística</p></li>
<li><p>Biologia / Ciências Biológicas</p></li>
<li><p>Arquitetura / Urbanismo</p></li>
<li><p>Design Gráfico</p></li>
<li><p>Design de Produto</p></li>
<li><p>Artes Visuais</p></li>
<li><p>Comércio Internacional</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">NIVEL_HABILITACOES</td>
<td><ul>
<li><p>BACHARELATO</p></li>
<li><p>LICENCIATURA</p></li>
<li><p>MESTRADO</p></li>
<li><p>DOUTORAMENTO</p></li>
</ul></td>
<td><ul>
<li><p>Bacharelato</p></li>
<li><p>Licenciatura,</p></li>
<li><p>Mestrado,</p></li>
<li><p>Doutoramento</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">SIM_NAO</td>
<td><ul>
<li><p>SIM</p></li>
<li><p>NAO</p></li>
</ul></td>
<td><ul>
<li><p>Sim</p></li>
<li><p>Não</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">SIM_NAO_NUMBER</td>
<td><ul>
<li><p>1</p></li>
<li><p>0</p></li>
</ul></td>
<td><ul>
<li><p>Sim</p></li>
<li><p>Não</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">REGIME_TRABALHO</td>
<td><ul>
<li><p><strong>INTEGRAL</strong></p></li>
<li><p><strong>HÍBRIDO</strong></p></li>
<li><p>PARCIAL</p></li>
<li><p><strong>TURNOS</strong></p></li>
<li><p><strong>FLEXÍVEL</strong></p></li>
<li><p><strong>FLEXÍVEL</strong></p></li>
<li><p>TELETRABALHO</p></li>
</ul></td>
<td><ul>
<li><p><strong>Integral</strong> (tempo completo)</p></li>
<li><p><strong>Híbrido</strong>(diasremoto/presencial)</p></li>
<li><p><strong>Turnos</strong> (rotação/escala)</p></li>
<li><p><strong>Flexível</strong> (janelas + “core hours”)</p></li>
<li><p><strong>Teletrabalho</strong> (100% remoto)</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">MOEDA</td>
<td><ul>
<li><p>AUT</p></li>
<li><p>BUA</p></li>
<li><p>CAD</p></li>
<li><p>CHF</p></li>
<li><p>CVE</p></li>
<li><p>EUR</p></li>
<li><p>FUA</p></li>
<li><p>GBP</p></li>
<li><p>JPY</p></li>
<li><p>KWD</p></li>
<li><p>NOK</p></li>
<li><p>NOK</p></li>
<li><p>RUS</p></li>
<li><p>SEK</p></li>
<li><p>SNY</p></li>
<li><p>SRD</p></li>
<li><p>USD</p></li>
<li><p>ZAR</p></li>
</ul></td>
<td><ul>
<li><p>DOLAR - AUSTRALIA</p></li>
<li><p>BUA - BAD</p></li>
<li><p>DOLAR - CANADA</p></li>
<li><p>FRANCO - SUICA</p></li>
<li><p>ESCUDO - CABO_VERDE</p></li>
<li><p>EURO - UNIAO_EUROPEIA</p></li>
<li><p>FUA - FAD</p></li>
<li><p>LIBRA - REINO_UNIDO</p></li>
<li><p>YENE - JAPAO</p></li>
<li><p>DINAR - KOWEIT</p></li>
<li><p>COROA - NORUEGA</p></li>
<li><p>COROA - DINAMARCA</p></li>
<li><p>RUBLO - RUSSIA</p></li>
<li><p>COROA - SUECIA</p></li>
<li><p>YUAN - CHINA</p></li>
<li><p>SRD - FMI</p></li>
<li><p>DOLAR – EUA</p></li>
<li><p>RANDE - AFRICA_DO_SUL</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">TIPO_ACAO</td>
<td><ul>
<li><p>INSERT ,</p></li>
<li><p>UPDATE ,</p></li>
<li><p>DELETE</p></li>
</ul></td>
<td><ul>
<li><p>Registo,</p></li>
<li><p>Atualização,</p></li>
<li><p>Eliminação</p></li>
</ul></td>
<td><ul>
<li></li>
</ul></td>
</tr>
<tr>
<td rowspan="2" style="text-align: left;"><em>ACCAO_REFERENTE</em></td>
<td><ul>
<li><p>REGISTO_COLABORADOR</p></li>
<li><p>ESTADO_COLABORADOR</p></li>
<li><p><em>DADOS_BANCARIOS</em></p></li>
<li><p><em>DADOS_PESSOAIS</em></p></li>
<li><p>FAMILIA</p></li>
<li><p><em>DADOS_ACADEMICOS</em></p></li>
<li><p><em>CONTRATO</em></p></li>
<li><p><em>RENOVACAO_CONTRATO</em></p></li>
<li><p><em>SUBSTITUICAO</em></p></li>
</ul></td>
<td><ul>
<li><p>Registar Colaborador,</p></li>
<li><p>Estado do colaborador</p></li>
<li><p>Dados Bancarios</p></li>
<li><p>Dados Familiar</p></li>
<li><p>Dados academico</p></li>
<li><p>Contrato</p></li>
<li><p>Renovacao do Contrato</p></li>
<li><p>Substituição</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td><ul>
<li><p>MOBILIDADE</p></li>
<li><p>CARREIRA</p></li>
<li><p>REGIME</p></li>
<li><p>SITUACAO_LABORAl</p></li>
</ul></td>
<td><ul>
<li><p>Mobilidade</p></li>
<li><p>Carreira</p></li>
<li><p>Regime</p></li>
<li><p>Situação Laboral</p></li>
</ul></td>
<td>HISTORICO_LABORAL</td>
</tr>
<tr>
<td style="text-align: left;">MOTIVO_SUBSTITUICAO_MOB</td>
<td><ul>
<li><p>FERIAS</p></li>
<li><p>LICENCA</p></li>
</ul></td>
<td><ul>
<li><p>Ferias</p></li>
<li><p>Licença</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">STATUS</td>
<td><ul>
<li><p>A</p></li>
<li><p>I</p></li>
<li><p>P</p></li>
</ul></td>
<td><ul>
<li><p>Ativo</p></li>
<li><p>inativo</p></li>
<li><p>Pendente</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">REFERENCIA_TP_DOC</td>
<td><ul>
<li><p>FAMILIAR</p></li>
<li><p>COLABORADOR</p></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">TP_FORMACAO</td>
<td><ul>
<li><p>FORMACAO_ACADEMICA</p></li>
<li><p>FORMACAO_PROFISSIONAL</p></li>
<li><p>FORMACAO_TECNICO_PROFISSIONAL</p></li>
<li><p>FORMACAO_CONTINUA</p></li>
<li><p>FORMACAO_DE_APERFEICOAMENTO</p></li>
<li><p>FORMACAO_DE_ESPECIALIZACAO</p></li>
<li><p>FORMACAO_CERTIFICADA</p></li>
<li><p>FORMACAO_NAO_CERTIFICADA</p></li>
<li><p>FORMACAO_INTERNA</p></li>
<li><p>FORMACAO_EXTERNA</p></li>
<li><p>FORMACAO_PRESENCIAL</p></li>
<li><p>FORMACAO_A_DISTANCIA_E_LEARNING</p></li>
<li><p>FORMACAO_EM_REGIME_HIBRIDO</p></li>
<li><p>FORMACAO_DE_RECICLAGEM_ATUALIZACAO</p></li>
<li><p>FORMACAO_DE_REQUALIFICACAO</p></li>
</ul></td>
<td><ul>
<li><p>Formação Académica</p></li>
<li><p>Formação Profissional</p></li>
<li><p>Formação Técnico-Profissional</p></li>
<li><p>Formação Contínua</p></li>
<li><p>Formação de Aperfeiçoamento</p></li>
<li><p>Formação de Especialização</p></li>
<li><p>Formação Certificada</p></li>
<li><p>Formação Não-Certificada</p></li>
<li><p>Formação Interna</p></li>
<li><p>Formação Externa</p></li>
<li><p>Formação Presencial</p></li>
<li><p>Formação à Distância / E-learning</p></li>
<li><p>Formação em Regime Híbrido</p></li>
<li><p>Formação de Reciclagem / Atualização</p></li>
<li><p>Formação de Requalificação</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">ESTADO_PROCESSAMENTO</td>
<td><ul>
<li><p>PROCESSADO</p></li>
<li><p>CABIMENTADO</p></li>
<li><p>VALIDADO</p></li>
<li><p>AUTORIZADO</p></li>
<li><p>ELIMINADO</p></li>
</ul></td>
<td><ul>
<li><p><em>Processado</em></p></li>
<li><p><em>Cabimentado</em></p></li>
<li><p><em>Validado</em></p></li>
<li><p><em>Autorizado</em></p></li>
<li><p><em>Eliminado</em></p></li>
</ul></td>
<td><em>PROV, DEF, ‘’, AUT</em></td>
</tr>
<tr>
<td style="text-align: left;">FLG_LICENCA</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">TP_PROCESSO_DISCP</td>
<td><ul>
<li><p>SINDICANCIA</p></li>
<li><p>INQUERITO</p></li>
<li><p>PROCESSO_DISCIPLINAR_SIMPLES</p></li>
<li><p>PROCESSO_DISCIPLINAR_AGRAVADO</p></li>
</ul></td>
<td><ul>
<li><p><em>sindicância</em></p></li>
<li><p><em>inquérito</em></p></li>
<li><p><em>processo disciplinar simples</em></p></li>
<li><p><em>processo disciplinar agravado</em></p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">STATUS_PROCESSO_DISCP</td>
<td><ul>
<li><p>PENDENTE</p></li>
<li><p>EM_INSTRUCAO</p></li>
<li><p>CONCLUIDO</p></li>
<li><p>ARQUIVADO</p></li>
<li><p>EM_RECURSO</p></li>
</ul></td>
<td><ul>
<li><p><em>Pendente</em></p></li>
<li><p><em>Em Instrução</em></p></li>
<li><p><em>Concluído, Arquivado</em></p></li>
<li><p><em>Em Recurso</em></p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">PENA_DISCIP</td>
<td><ul>
<li><p>ADVERTENCIA</p></li>
<li><p>SUSPENSAO</p></li>
<li><p>MULTA</p></li>
<li><p>DESPEDIMENTO</p></li>
</ul></td>
<td><ul>
<li><p><em>advertência</em></p></li>
<li><p><em>suspensão</em></p></li>
<li><p><em>multa</em></p></li>
<li><p><em>despedimento</em></p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>TIPO_PROCESSAMENTO</em></td>
<td><ul>
<li><p>RETRO</p></li>
<li><p>REF_ANT</p></li>
<li><p>SAL</p></li>
<li><p>SUBFER</p></li>
<li><p>SUBNAT</p></li>
</ul></td>
<td><ul>
<li><p><em>Retroativo novo PCCS</em></p></li>
<li><p><em>Reforma Antecipada</em></p></li>
<li><p><em>Salário</em></p></li>
<li><p><em>Subsídio de Férias</em></p></li>
<li><p><em>Subsídio de Natal</em></p></li>
</ul></td>
<td><ul>
<li></li>
</ul></td>
</tr>
<tr>
<td style="text-align: left;"><em>ESTADO_PROCESSAMENTO</em></td>
<td><ul>
<li><p>PROV</p></li>
<li><p>VALIDADO</p></li>
<li><p>DEF</p></li>
<li><p>AUTORIZADO</p></li>
<li><p>PAGO</p></li>
</ul></td>
<td><ul>
<li><p><em>Processado</em></p></li>
<li><p><em>Validado</em></p></li>
<li><p><em>Cabimentado</em></p></li>
<li><p><em>Autorizado</em></p></li>
<li><p><em>Pago</em></p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">HORARIO</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">MODALIDADE_REGIME</td>
<td><ul>
<li><p>PRESENCIAL</p></li>
<li><p>REMOTO</p></li>
</ul></td>
<td><ul>
<li><p><em>Presencial</em></p></li>
<li><p><em>Remoto</em></p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">DIAS_SEMANA</td>
<td><ul>
<li><p>SEGUNDA_SEXTA</p></li>
<li><p>SEGUNDA</p></li>
<li><p>TERCA</p></li>
<li><p>QUARTA</p></li>
<li><p>QUINTA_FEIRA</p></li>
<li><p>SEXTA_FEIRA</p></li>
</ul></td>
<td><ul>
<li><p>Segunda a Sexta</p></li>
<li><p>Segunda</p></li>
<li><p>Terca</p></li>
<li><p>Quarta</p></li>
<li><p>Quinta-Feira</p></li>
<li><p>Sexta-Feira</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td rowspan="5" style="text-align: left;">TIPO_MOV_LABORAL</td>
<td><ul>
<li><p>INICIO</p></li>
<li><p>RENOVACAO</p></li>
<li><p><em>CONTINUIDADE</em></p></li>
<li><p>CONVERSAO_CONTRATO</p></li>
<li><p><em>MUDAR_VINCULO</em></p></li>
</ul></td>
<td><ul>
<li><p><em>Novo Contrato</em></p></li>
<li><p><em>Renovação</em></p></li>
<li><p><em>Continuidade Contrato</em></p></li>
<li><p><em>Mudar vinculo contratual</em></p></li>
</ul></td>
<td>CONTRATO</td>
</tr>
<tr>
<td><ul>
<li><p>INICIO</p></li>
<li><p>SECAO</p></li>
<li><p>LOCAL_TRABALHO</p></li>
<li><p>DIRECAO</p></li>
<li><p>MOBILIDADE_EXTERNA</p></li>
</ul></td>
<td><ul>
<li><p><em>Mobilidade interna (Secção)</em></p></li>
<li><p><em>Mobilidade Regional (Local Trabalho)</em></p></li>
<li><p><em>Mobilidade interna (Direção)</em></p></li>
<li><p><em>Mobilidade Externa</em></p></li>
</ul></td>
<td>MOBILIDADE</td>
</tr>
<tr>
<td><ul>
<li><p>INICIO</p></li>
<li><p>PROGRESSAO</p></li>
</ul>
<ul>
<li><p>PROMOCAO</p></li>
<li><p><strong>CONTINUIDADE</strong></p></li>
<li><p>MUDANCA_CARREIRA</p></li>
<li><p>REPOSICIONAMENTO_PCCS</p></li>
<li><p>AUMENTO_SALARIAL</p></li>
<li><p>NOVO_CARGO</p></li>
</ul></td>
<td><ul>
<li><p>progressão</p></li>
</ul>
<ul>
<li><p>promoção</p></li>
<li><p><strong>Continuidade</strong></p></li>
<li><p>Mudança Carreira</p></li>
<li><p>Reposicionamento pccs</p></li>
<li><p>Novo Cargo</p></li>
</ul></td>
<td>CARREIRA</td>
</tr>
<tr>
<td><ul>
<li><p>MUDANCA_REGIME</p></li>
</ul></td>
<td><ul>
<li><p>Mudança Regime</p></li>
</ul></td>
<td>REGIME</td>
</tr>
<tr>
<td><ul>
<li><p>MUDANCA_SITUACAO_LAB</p></li>
</ul></td>
<td><blockquote>
<p>Mudança situação Laboral</p>
</blockquote></td>
<td>SITUACAO_LABORAL</td>
</tr>
<tr>
<td style="text-align: left;">REFERENCIA_LABORAL</td>
<td><ul>
<li><p>CONTRATO</p></li>
<li><p>VINCULO</p></li>
<li><p>CARREIRA</p></li>
<li><p>MOBILIDADE</p></li>
<li><p>REGIME</p></li>
<li><p>SITUACAO_LABORAL</p></li>
</ul></td>
<td></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">REFERENCIA_LETRA</td>
<td>A,B,C,D,E,F</td>
<td>A,B,C,D,E,F</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">REFERENCIA_NIVEL_PCCS</td>
<td>1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18</td>
<td>1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18</td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">SITUACAO_LABORAL</td>
<td><ul>
<li><p>ATIVO</p></li>
<li><p>SUSPENSO</p></li>
<li><p>CESSADO</p></li>
<li><p>APSOSENTADO</p></li>
<li><p>DISPONIVEL_RESERVA</p></li>
</ul></td>
<td><ul>
<li><p>Ativo</p></li>
<li><p>Suspenso</p></li>
<li><p>Cessado</p></li>
<li><p>Aposentado</p></li>
<li><p>Disponivel / Reserva</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td rowspan="4" style="text-align: left;">MOTIVO_SIT_LABORAL</td>
<td><ul>
<li><p>RETORNO_DE_LICENCA</p></li>
<li><p>RETORNO_DE_LICENCA</p></li>
<li><p>NOMEACAO_DESIGNACAO</p></li>
<li><p>ESTAGIO_CURRICULAR_PROFISSIONAL</p></li>
<li><p>MOBILIDADE</p></li>
<li><p>PROGRESSAO_PROMOCAO</p></li>
</ul></td>
<td><ul>
<li><p>Admissão</p></li>
<li><p><em>retorno de licença</em></p></li>
<li><p><em>retorno de licença</em></p></li>
<li><p><em>Nomeação/designação</em></p></li>
<li><p><em>Estágio curricular/profissional</em></p></li>
<li><p><em><strong>Mobilidade</strong></em></p></li>
<li><p><em>Progressão/Promoção</em></p></li>
</ul></td>
<td>ATIVO</td>
</tr>
<tr>
<td><ul>
<li><p>LICENCA_SEM_VENCIMENTO</p></li>
<li><p>DOENCA</p></li>
<li><p>MATERNIDADE_PATERNIDADE</p></li>
<li><p>ACIDENTE_DE_TRABALHO</p></li>
<li><p>FORMACAO</p></li>
<li><p>OUTROS</p></li>
</ul></td>
<td><ul>
<li><p><em>Licença sem vencimento</em></p></li>
<li><p><em>Doença</em></p></li>
<li><p><em>maternidade/paternidade</em></p></li>
<li><p><em>acidente de trabalho</em></p></li>
<li><p><em>formação</em></p></li>
<li><p><em>outros</em></p></li>
</ul></td>
<td><em>SUSPENSO</em></td>
</tr>
<tr>
<td><ul>
<li><p>APOSENTACAO</p></li>
<li><p>DEMISSAO_VOLUNTARIA</p></li>
<li><p>DESPEDIMENTO</p></li>
<li><p>ACORDO_DE_REVOGACAO</p></li>
<li><p>INVALIDEZ</p></li>
<li><p>FALECIMENTO</p></li>
<li><p>FIM_DA_COMISSAO</p></li>
<li><p>EXONERACAO</p></li>
<li><p>FIM_DO_DESTAQUE</p></li>
<li><p>FIM_DO_DESTAQUE</p></li>
<li><p>NAO_RENOVACAO</p></li>
<li><p>NAO_RENOVACAO</p></li>
<li><p>OUTROS</p></li>
</ul></td>
<td><ul>
<li><p><em>Aposentação</em></p></li>
<li><p><em>demissão voluntária</em></p></li>
<li><p><em>Despedimento</em></p></li>
<li><p><em>acordo de revogação</em></p></li>
<li><p><em>invalidez</em></p></li>
<li><p><em>falecimento</em></p></li>
<li><p><em>Fim da comissão</em></p></li>
<li><p><em>Exoneração</em></p></li>
<li><p><em>Fim do destacamento</em></p></li>
<li><p><em>Fim do destacamento</em></p></li>
<li><p><em>não renovação</em></p></li>
<li><p><em>não renovação</em></p></li>
<li><p><em>outros</em></p></li>
</ul></td>
<td>CESSADO</td>
</tr>
<tr>
<td><ul>
<li><p>REQUALIFICACAO</p></li>
<li><p>QUADRO_DE_EXCEDENTES</p></li>
</ul></td>
<td><ul>
<li><p>Requalificação</p></li>
<li><p>Quadro de excedentes</p></li>
</ul></td>
<td>DISPONIVEL_RESERVA</td>
</tr>
<tr>
<td style="text-align: left;">NATUREZA_VINCULO</td>
<td><ul>
<li><p>DETERMINADO</p></li>
<li><p>INDETERMINADO</p></li>
</ul></td>
<td><ul>
<li><p>Determinado</p></li>
<li><p>Indeterminado</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">CATEGORIA_PCCS</td>
<td><ul>
<li><p>PRINCIPAL</p></li>
<li><p>SENIOR</p></li>
<li><p>ASSISTENTE</p></li>
</ul></td>
<td><ul>
<li><p>Principal</p></li>
<li><p>Senior</p></li>
<li><p>Assistente</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>TP_CONTRATO</em></td>
<td><ul>
<li><p>NOVO_CONTRATO</p></li>
<li><p>RENOVACAO</p></li>
</ul></td>
<td><ul>
<li><p>Novo contratro</p></li>
<li><p>Renovação</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>ESTADO_CONTRATO</em></td>
<td><ul>
<li><p>A</p></li>
<li><p>S</p></li>
<li><p>C</p></li>
</ul></td>
<td><ul>
<li><p>ATIVO</p></li>
<li><p>SUSPENSO</p></li>
<li><p>CESSADO</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">TIPO_NOTIFICACO</td>
<td><ul>
<li><p>RENOVACAO_CONTRATO</p></li>
</ul></td>
<td><ul>
<li><p>Renovacao de contrato</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">ORDEM_SERVICO</td>
<td><ul>
<li><p>CONVERSAO_CONTRATO</p></li>
<li><p>LICENCA_S_VENCIMENTO</p></li>
<li><p>PROGRESSAO_CARGO</p></li>
<li><p>PROGRESSAO_CATEGORIA</p></li>
<li><p>SUBSTITUICAO</p></li>
<li><p>TRANSFERENCIA</p></li>
<li><p>MOBILIDADE_INTERNA</p></li>
<li><p>REQUALIFICACAO</p></li>
<li><p>NOMIACAO</p></li>
<li><p>FIM_COMISSAO_SERVICO</p></li>
</ul></td>
<td><ul>
<li><p>Conversão de Contratos</p></li>
<li><p>Licença sem Vencimento</p></li>
<li><p>Progressão_Cargo</p></li>
<li><p>Progressão_Categoria</p></li>
<li><p>Substituição</p></li>
<li><p>Transferência</p></li>
<li><p>MOBILIDADE INTERNA</p></li>
<li><p>Requalificação</p></li>
<li><p>Nomeação</p></li>
<li><p>Fim de Comissão de Serviço</p></li>
</ul></td>
<td>ORDEM_SERVICO</td>
</tr>
<tr>
<td style="text-align: left;">ORDEM_SERVICO_DECLARACAO</td>
<td><ul>
<li><p>CONVERSAO_CONTRATO</p></li>
<li><p>LICENCA_S_VENCIMENTO</p></li>
<li><p>PROGRESSAO_CARGO</p></li>
<li><p>PROGRESSAO_CATEGORIA</p></li>
<li><p>SUBSTITUICAO</p></li>
<li><p>TRANSFERENCIA</p></li>
<li><p>MOBILIDADE_INTERNA</p></li>
<li><p>REQUALIFICACAO</p></li>
<li><p>NOMIACAO</p></li>
<li><p>FIM_COMISSAO_SERVICO</p></li>
</ul></td>
<td><ul>
<li><p>Conversão de Contratos</p></li>
<li><p>Licença sem Vencimento</p></li>
<li><p>Progressão_Cargo</p></li>
<li><p>Progressão_Categoria</p></li>
<li><p>Substituição</p></li>
<li><p>Transferência</p></li>
<li><p>MOBILIDADE INTERNA</p></li>
<li><p>Requalificação</p></li>
<li><p>Nomeação</p></li>
<li><p>Fim de Comissão de Serviço</p></li>
</ul></td>
<td>ORDEM_SERVICO</td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td><ul>
<li><p>DECLARACAO_CREDITO_HABITACAO</p></li>
<li><p>DECLARACAO_EMEP</p></li>
<li><p>DECLARACAO_CREDITO_BANCARIO</p></li>
<li><p>DECLARACAO_VISTOSs</p></li>
</ul></td>
<td><ul>
<li><p>Declaracao efeito crédito habitação</p></li>
<li><p>declaracao EMEP</p></li>
<li><p>Decalaracao Efeitos crédito bancário</p></li>
<li><p>Decalaracao Efeitos visto</p></li>
</ul></td>
<td>DECLARACAO</td>
</tr>
<tr>
<td rowspan="2" style="text-align: left;">JUSTIFICADA_INJUSTIFICADA</td>
<td><ul>
<li><p>FALTA_JUSTIFICADA</p></li>
<li><p>FALTA_INJUSTIFICADA</p></li>
</ul></td>
<td><ul>
<li><p>Falta justificada</p></li>
<li><p>Falta Injustificada</p></li>
</ul></td>
<td>FALTA</td>
</tr>
<tr>
<td><ul>
<li><p>AUSENCIA_JUSTIFICADA</p></li>
<li><p>AUSENCIA_INJUSTIFICADA</p></li>
</ul></td>
<td><ul>
<li><p>Ausência justificada</p></li>
<li><p>Ausência Injustificada</p></li>
</ul></td>
<td>AUSENCIA</td>
</tr>
<tr>
<td style="text-align: left;">TIPO_PEDIDO</td>
<td><ul>
<li><p>JUSTIFICAR _FALTA</p></li>
<li><p>FERIA</p></li>
<li><p>DISPENSA</p></li>
<li><p>DECLARACAO</p></li>
</ul></td>
<td><ul>
<li><p>JUSTIFICAR Falta</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td rowspan="2" style="text-align: left;">ETAPA_PROCESSO</td>
<td><ul>
<li><p>PEDIDO</p></li>
<li><p>PARECER_RESPONSAVEL</p></li>
<li><p>DESPACHO_RH</p></li>
</ul></td>
<td></td>
<td>JUSTIFICAR _FALTA</td>
</tr>
<tr>
<td><ul>
<li><p>PEDIDO</p></li>
<li><p>PARECER_RESPONSAVEL</p></li>
<li><p>PARECER_RH</p></li>
</ul></td>
<td></td>
<td>DISPENSA</td>
</tr>
<tr>
<td rowspan="2" style="text-align: left;">PARECER_DECISAO</td>
<td><ul>
<li><p>FAVORAVEL</p></li>
<li><p>DESFAVORAVEL</p></li>
</ul></td>
<td><ul>
<li><p>Favorável</p></li>
<li><p>Desfavoravel</p></li>
</ul></td>
<td>PARECER_RESPONSAVEL</td>
</tr>
<tr>
<td><ul>
<li><p>JUSTIFICADA</p></li>
<li><p>INJUSTIFICADA</p></li>
</ul></td>
<td></td>
<td>DESPACHO_RH</td>
</tr>
<tr>
<td style="text-align: left;">ORIGEM_PEDIDO</td>
<td><ul>
<li><p>RH</p></li>
<li><p>PORTAL</p></li>
</ul></td>
<td><ul>
<li><p>Recursos Humanos</p></li>
<li><p>Portal</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">CLASSIFICACAO_SITUACAO</td>
<td><ul>
<li><p>DOENCA_SAUDE</p></li>
<li><p>PARENTALIDADE</p></li>
<li><p>FALECIMENTO_LUTO</p></li>
<li><p>DEVERES_LEGAIS</p></li>
<li><p>SITUACAO_FUNCIONAIS</p></li>
<li><p>OUTROS</p></li>
</ul></td>
<td><ul>
<li><p>Doença e Saude</p></li>
<li><p>Parentalidade</p></li>
<li><p>Falecimento / luto</p></li>
<li><p>Deveres legais e públicos</p></li>
<li><p>Situações funcionais</p></li>
<li><p>Outras</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">ORDEM_SERVICO</td>
<td><ul>
<li><p>CONVERSAO_CONTRATOS</p></li>
<li><p>LICENCA_SEM_VENCIMENTO</p></li>
<li><p>PROGRESSAO_CARGO</p></li>
<li><p>PROGRESSAO_CATEGORIA</p></li>
<li><p>SUBSTITUICAO</p></li>
</ul></td>
<td><ul>
<li><p>Conversão de Contratos</p></li>
<li><p>Licença sem Vencimento</p></li>
<li><p>Progressão_Cargo</p></li>
<li><p>Progressão_Categoria</p></li>
</ul>
<blockquote>
<p>substituição</p>
</blockquote></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td><ul>
<li><p>TRANSFERENCIA</p></li>
<li><p>MOBILIDADE_INTERNA</p></li>
<li><p>REQUALIFICACAO</p></li>
<li><p>NOMEACAO_COORDENADOR</p></li>
<li><p>FIM_COMISSAO_SERVICO</p></li>
</ul></td>
<td><ul>
<li><p>Transferência</p></li>
<li><p>MOBILIDADE INTERNA</p></li>
<li><p>Requalificação</p></li>
<li><p>Nomeação Coordenador</p></li>
<li><p>Fim de Comissão de Serviço</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">TIPO_CONTAGEM_DIAS</td>
<td><blockquote>
<p>DIAS_UTEIS</p>
<p>DIAS_CORRIDO</p>
</blockquote></td>
<td><blockquote>
<p>Dias Uteis</p>
<p>Dias Corrido</p>
</blockquote></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><em>TIPO_VALIDACAO</em></td>
<td><ul>
<li><p>SEM_NIF</p></li>
<li><p>NIB_INCORRETO</p></li>
<li><p>DIFERENCA_LIQUIDA</p></li>
<li><p>TIPO_MOVIMENTO</p></li>
<li><p>REMUN_SUBSIDIO</p></li>
<li><p>DESCONTO</p></li>
<li><p>DIFERENCA_COLABORADOR</p></li>
<li><p>DIFERENCA_PROC_ESC</p></li>
<li><p>FALTA</p></li>
<li><p>HORA_EXTRA</p></li>
<li><p>SUSPENSO</p></li>
<li><p>DUPLICADO</p></li>
<li><p>SEM_IUR</p></li>
<li><p>SEM_INPS</p></li>
</ul></td>
<td><ul>
<li><p>Sem Nif</p></li>
<li><p>Nib Incorreto</p></li>
<li><p>Diferente liquida entre meses</p></li>
<li><p>Tipo Movimento entre meses</p></li>
<li><p>Remuneração / subsidio entre meses</p></li>
<li><p>Descontos entre meses</p></li>
<li><p>Diferença colaborador entre meses</p></li>
<li><p>Diferente Salário base processado e Escalao</p></li>
<li><p>Colaborador com Falta</p></li>
<li><p>Colaborador com Hora Extra</p></li>
<li><p>Colaborador suspenso Processado</p></li>
<li><p>Duplicados</p></li>
<li><p>Sem Desconto IUR</p></li>
<li><p>Sem Desconto INPS</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td
style="text-align: left;"><strong>DIAS_PERCENTAGEM_HORA</strong></td>
<td><ul>
<li><p>DIAS_UTEIS</p></li>
<li><p>DIAS_UTEIS_NAO_UTEIS</p></li>
<li><p>DIAS_NAO_UTEIS</p></li>
</ul></td>
<td><ul>
<li><p>Dias Úteis</p></li>
<li><p>Dias Uteis e Não Úteis</p></li>
<li><p>Dias Não Úteis</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><strong>TIPO_ASSIDUIDADE</strong></td>
<td><ul>
<li><p>HORA_EXTRA</p></li>
<li><p>FALTA</p></li>
<li><p>FERIAS</p></li>
<li><p>DISPENSA</p></li>
</ul></td>
<td><ul>
<li></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><strong>CONFIGURACAO_PRAZO</strong></td>
<td><ul>
<li><p><strong>45</strong></p></li>
<li><p><strong>60</strong></p></li>
<li><p><strong>45</strong></p></li>
<li><p><strong>10</strong></p></li>
</ul></td>
<td><ul>
<li><p>Prazo Renovação contrato</p></li>
<li><p>Conversao de contrato</p></li>
<li><p>Licença sem vencimento</p></li>
<li><p>Licença com vencimento</p></li>
</ul></td>
<td><ul>
<li><p>RENOVACAO</p></li>
<li><p>CONVERSAO</p></li>
<li><p>LICENCA_S_VENCIMENTO</p></li>
<li><p>LICENCA_C_VENCIMENTO</p></li>
</ul></td>
</tr>
<tr>
<td rowspan="2"
style="text-align: left;"><strong>TIPO_ALERTA_NOTIFICACAO</strong></td>
<td><ul>
<li><p><strong>LICENSA_S_VENCIMENTO</strong></p></li>
<li><p><strong>LICENCAS_C_VENCIMENTO</strong></p></li>
</ul></td>
<td><ul>
<li><p>Licença sem vencimento</p></li>
<li><p>Licença com vencimento</p></li>
</ul></td>
<td>LICENCA</td>
</tr>
<tr>
<td><ul>
<li><p><strong>RENOVACAO</strong></p></li>
<li><p><strong>CONVERSAO</strong></p></li>
</ul></td>
<td><ul>
<li><p>Renovação</p></li>
<li><p>Conversão de contrato</p></li>
</ul></td>
<td>CONTRATO</td>
</tr>
<tr>
<td style="text-align: left;"><strong>CALCULO_PROG_PROMO</strong></td>
<td><ul>
<li><p><strong>4.5</strong></p></li>
<li><p>2,5</p></li>
<li><p>4</p></li>
<li><p>2</p></li>
<li><p>2</p></li>
<li><p>6</p></li>
</ul></td>
<td><ul>
<li><p>Avaliação minimo Promocao</p></li>
<li><p>Avaliação minimo Progressão</p></li>
<li><p>Tempo progressao dirigente</p></li>
<li><p>Tempo progressao dirigente Origem</p></li>
<li><p>Tempo promocão</p></li>
<li><p>Numero Limite de Falta anual</p></li>
</ul></td>
<td><ul>
<li><p>AVAL_MIN_PROMO</p></li>
<li><p>AVAL_MIN_PROG</p></li>
<li><p>TEMP_PROG_DIRIGENTE</p></li>
<li><p>TEMP_PROG_DIRIGENTE_ORIG</p></li>
<li><p>TEMPO_PROGRESSAO_ORIG</p></li>
<li><p>TEMP_PROMOCAO</p></li>
<li><p>NUMERO_LIMITE_FALTA</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: left;"><strong>ABRANGENCIA_AVD</strong></td>
<td><ul>
<li><p>INPS</p></li>
<li><p>DIRECAO</p></li>
<li><p>INDIVIDUAL</p></li>
</ul></td>
<td><ul>
<li><p>Comum so INPS</p></li>
<li><p>Comum à Unidade Orgânica</p></li>
<li><p>Individual</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;"><strong>NIVEIS_AVD</strong></td>
<td><ul>
<li><p>1</p></li>
<li><p>2</p></li>
<li><p>3</p></li>
<li><p>4</p></li>
<li><p>5</p></li>
</ul></td>
<td><ul>
<li><p>1</p></li>
<li><p>2</p></li>
<li><p>3</p></li>
<li><p>4</p></li>
<li><p>5</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td
style="text-align: left;"><strong>CLASSIFICACAO_QUALIT_AVD1</strong></td>
<td><ul>
<li><p>EXCELENTE</p></li>
<li><p>MUITO BOM</p></li>
<li><p>BOM</p></li>
<li><p>SUFICIENTE</p></li>
<li><p>INSUFICIENTE</p></li>
<li><p>PRECISA_DESENVOLVIMENTO</p></li>
</ul></td>
<td><ul>
<li><p>Excelente</p></li>
<li><p>Muito Bom</p></li>
<li><p>Bom</p></li>
<li><p>Suficiente</p></li>
<li><p>Insuficiente</p></li>
<li><p>Precisa Desenvolvimento</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">AVD_PONDERACAO_FINAL</td>
<td><ul>
<li><p>50</p></li>
<li><p>50</p></li>
</ul></td>
<td><ul>
<li><p>Semestre 1</p></li>
<li><p>Semestre 2</p></li>
</ul></td>
<td><ul>
<li><p>SEMESTRE1</p></li>
<li><p>SEMESTRE2</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: left;">AUMENTO_GRELHA_SALARIAL</td>
<td><ul>
<li><p>NAO</p></li>
<li><p>SIM</p></li>
<li><p>SIM_CARREIRAS</p></li>
<li><p>SIM_NIVEIS</p></li>
</ul></td>
<td><ul>
<li><p>Não</p></li>
<li><p>Sim</p></li>
<li><p>Sim, Algumas Carreiras</p></li>
<li><p>Sim, Alguns Niveis</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">CRITERIO_LEGIBILIDADE_AUMENTO</td>
<td><ul>
<li><p>GERAL</p></li>
<li><p>PARCIAL</p></li>
</ul></td>
<td><ul>
<li><p>Aumento Geral dos colaboradores</p></li>
<li><p>Aumento Alguns Colaboradores</p></li>
</ul></td>
<td></td>
</tr>
<tr>
<td style="text-align: left;">PERCENTAGEM_VALOR</td>
<td><ul>
<li><p>PERCENTAGEM</p></li>
<li><p>VALOR</p></li>
</ul></td>
<td><ul>
<li><p>Percentagem</p></li>
<li><p>Valor</p></li>
</ul></td>
<td></td>
</tr>
</tbody>
</table>

# QUERY / FUNCÃO

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 30%" />
<col style="width: 41%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">Referente</th>
<th>Nome Função</th>
<th style="text-align: center;">VALOR</th>
</tr>
</thead>
<tbody>
<tr>
<td>Tipo Movimento REMUNERACAO</td>
<td>GET_MOVIMENTO_REMUNERACAO (P_TIPO)</td>
<td><p>SELECT a.id AS ID, a.descricao AS NOME</p>
<p>FROM inpsrhsigof..rh_tipo_movimentos a, inpsrh.itens_actos b,
inpsrh.itens_tipo c</p>
<p>WHERE (a.iac_iac_id = b.iac_id)</p>
<p>AND (b.ite_id = c.id)</p>
<p>-- AND ( (a.tipo = 'REM') OR (a.tipo = 'ABA'))</p>
<p>AND a.tipo IN (P_TIPO)</p>
<p>AND (a.amb_apl_id = 30) --p_amb_apl_id)</p>
<p>and a.estado = 'ACTIVO'</p>
<p>and a.short_desc != 'SALL'</p>
<p>ORDER BY a.descricao;</p>
<p>---------------------------------------------------------</p>
<p>P_TIPO = 'REM' OU 'ABA'</p></td>
</tr>
<tr>
<td>Tipo Movimento REMUNERACAO</td>
<td>GET_MOVIMENTO_DESCONTO (P_TIPO)</td>
<td><p>SELECT a.id, a.descricao AS NOME</p>
<p>NOME</p>
<p>FROM sipsglobal.rh_tipo_movimentos a, sipsglobal.itens_actos b,
sipsglobal.inpsrh.itens_tipo c</p>
<p>WHERE (a.iac_iac_id = b.iac_id)</p>
<p>AND (b.ite_id = c.id)</p>
<p>--AND ( (a.tipo = 'PAG') OR (a.tipo = 'IMP'))</p>
<p>AND a.tipo IN (P_TIPO)</p>
<p>AND (a.amb_apl_id = 30) --p_amb_apl_id) --ambiente da financeira</p>
<p>and a.estado = 'ACTIVO'</p>
<p>ORDER BY a.descricao;</p>
<p>--------------------------------------</p>
<p>P_TIPO = 'PAG'OU 'IMP'</p></td>
</tr>
<tr>
<td>Tipo Vinculo</td>
<td>GET_TIPO_VINCULO</td>
<td>SELECT ID, NOME FROM RH_T_PARAM_VINCULO</td>
</tr>
<tr>
<td>Escalao</td>
<td>GET_ESCALAO</td>
<td>RH_T_PARAM_ESCALAO</td>
</tr>
<tr>
<td>SALARIO</td>
<td>GET_SALARIO</td>
<td>RH_T_TIPOS_RELACIONAMENTO.SALARIO</td>
</tr>
<tr>
<td>DIREÇÃO</td>
<td>GET_DIRECAO_SERVICO</td>
<td><p>SELECT T.ID,</p>
<p>'(' ||T.CODIGO ||') ' ||TRIM(REGEXP_REPLACE(T.nome, '^Processar
Remunerações', '')) AS nome</p>
<p>FROM inspsigof.INSTITUICOES t</p>
<p>WHERE UPPER(t.nome) LIKE 'PROCESSAR REMUNE%'</p>
<p>AND t.nivel = (</p>
<p>SELECT MAX(t2.nivel)</p>
<p>FROM inspsigof.INSTITUICOES t2</p>
<p>WHERE UPPER(t2.nome) LIKE 'PROCESSAR REMUNE%'</p>
<p>AND t2.codigo LIKE t.codigo || '%'</p>
<p>AND NOT EXISTS (SELECT 1 FROM inspsigof.INSTITUICOES t3 WHERE
T3.INSTIT_ID = T2.ID )</p>
<p>);</p></td>
</tr>
<tr>
<td>CENTRO_CUSTO</td>
<td>GET_NOME_CENTRO_CUSTO (P_ INSTIT_ID)</td>
<td><p>Pendente Parametrização</p>
<p>Devolve (ID, NOME)</p>
<p>select a.id , b.nome from INPSSIGOF.centros_custo a,
inpssigof.entidades b</p>
<p>where a.ent_id = b.id</p>
<p>and a.instit_id = :p_instit_id</p></td>
</tr>
<tr>
<td>SECCAO</td>
<td>GET_SECCAO (P_ INSTIT_ID)</td>
<td><p>Pendente Parametrização</p>
<p>Devolve (ID, NOME)</p>
<p>RH_T_SECCAO</p></td>
</tr>
<tr>
<td>CARREIRA</td>
<td>GET_CARREIRA (P_CARGO)</td>
<td><p>Pendente Parametrização</p>
<p>Devolve (ID, NOME)</p>
<p>RH_T_PARAM_CARREIRA</p></td>
</tr>
<tr>
<td>CATEGORIA</td>
<td>GET_CATEGORIA(P_CARREIRA)</td>
<td><p>Pendente Parametrização</p>
<p>Devolve (ID, NOME)</p>
<p>RH_T_PARAM_CATEGORIA</p></td>
</tr>
<tr>
<td>SALARIO BASE</td>
<td>GET_MOVIMENTO_SALL</td>
<td><p>SELECT ID</p>
<p>FROM sipsglobal. rh_tipo_movimentos</p>
<p>WHERE short_desc = 'SALL' AND amb_apl_id = 30;</p></td>
</tr>
<tr>
<td>IUR</td>
<td>GET_MOVIMENTO_IUR</td>
<td><p>SELECT ID</p>
<p>FROM rh_tipo_movimentos</p>
<p>WHERE short_desc = 'IUR' AND amb_apl_id = 30;</p></td>
</tr>
<tr>
<td>INPS</td>
<td>GET_MOVIMENTO_INPS</td>
<td><p>SELECT ID</p>
<p>FROM rh_tipo_movimentos</p>
<p>WHERE short_desc = 'INPS' AND amb_apl_id = 30;</p></td>
</tr>
<tr>
<td>GEOGRAFIA</td>
<td>GET_GEOGRAFIA (P_NIVEL )</td>
<td><p>SELECT ID, NOME FROM SIPSGLOBAL.GLB_T_GEOGRAFIA</p>
<p>WHERE NIVEL_DETALHE = : P_NIVEL</p></td>
</tr>
<tr>
<td>LOCAL DE TRABALHO</td>
<td>GET_LOCAL_TRABALHO</td>
<td>SELECT ID, NOME FROM RH_T_LOCAL_TRABALHO</td>
</tr>
<tr>
<td>PAIS DO LOCAL DE TRABALHO</td>
<td>GET_PAIS_LOCAL_TRAB(P_LOCAL_TRABALHO)</td>
<td>SELEC B.NOME FROM RH_T_LOCAL_TRABALHO A,
SIPSGLOBAL"."GLB_T_GEOGRAFIA B WHERE A.PAIS_ID = B.ID AND A.ID = :
P_LOCAL_TRABALHO</td>
</tr>
<tr>
<td>Local trabalho</td>
<td>GET_PAIS_ILHA_TRAB(P_LOCAL_TRABALHO)</td>
<td>SELEC B.NOME FROM RH_T_LOCAL_TRABALHO A,
SIPSGLOBAL"."GLB_T_GEOGRAFIA B WHERE A.ILHA_ID = B.ID AND A.ID = :
P_LOCAL_TRABALHO</td>
</tr>
<tr>
<td>Nome colaborador</td>
<td>GET_NOME_COLABORADOR(P_ <em>TIPREL_ID</em>)</td>
<td>SELECT NOME FROM RH_V_DOSSIE WHERE TIPREL_ID =: P_
<em>TIPREL_ID</em></td>
</tr>
<tr>
<td>NOME CARGO</td>
<td>GET_NOME_CARGO</td>
<td>SELECT CARGO FROM RH_V_DOSSIE WHERE TIPREL_ID =: P_
<em>TIPREL_ID</em></td>
</tr>
<tr>
<td>Nome de Local</td>
<td>getLocalUserMovimento(</td>
<td><p>function getLocalUserMovimento(p_id_equipamento number)return
varchar2</p>
<p>is</p>
<p>v_local_movimento varchar2(500);</p>
<p>begin</p>
<p>select rhe.Local into v_local_movimento from RH_EQUIP_CONTR_ACESSO
rhe WHERE rhe.id = p_id_equipamento and rownum = 1;</p>
<p>return v_local_movimento;</p>
<p>EXCEPTION WHEN OTHERS THEN</p>
<p>return null;</p>
<p>end;</p></td>
</tr>
<tr>
<td>getIDDC</td>
<td>getIDDC</td>
<td><p>function getIDDC(p_nu_dc number) return number</p>
<p>is</p>
<p>v_id_dec_e number;</p>
<p>begin</p>
<p>select dec.id into v_id_dec_e from sipsv0.sips_T_cc_dec_e dec,
sipsv0.sips_T_cc_movs mov, sipsv0.sips_T_CC_DC_linhas dc where
dec.id_cc_origem = mov.id_cc_origem and dc.id_cc_mov = mov.id and
dc.nu_dc = p_nu_dc;</p>
<p>return v_id_dec_e;</p>
<p>exception when no_data_found then</p>
<p>return null;</p>
<p>end;</p></td>
</tr>
</tbody>
</table>

#  Vista 

<table>
<colgroup>
<col style="width: 28%" />
<col style="width: 30%" />
<col style="width: 41%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">Referente</th>
<th>Nome Função</th>
<th style="text-align: center;">VALOR</th>
</tr>
</thead>
<tbody>
<tr>
<td>ReFERENCIAS DE VISTAS ANTIGAS</td>
<td>V_RH : CADASTRO</td>
<td></td>
</tr>
<tr>
<td>Dados Pessoais e Mobilidade</td>
<td>RH_V_DOSSIE</td>
<td><ul>
<li><p><em>ULTIMO_VINCULO</em></p></li>
<li><p><em>NOME</em></p></li>
<li><p><em>ESTADO_VALIDACAO</em></p></li>
<li><p><em>ESTADO_COLABORADOR</em></p></li>
<li><p><em>FUN_ID</em></p></li>
<li><p><em>ID_COLABORADOR</em></p></li>
<li><p><em>CARGO_DESC</em></p></li>
<li><p><em>CARGO_ID</em></p></li>
<li><p><em>CENTRO_CUSTO_DESC</em></p></li>
<li><p><em>CENTRO_CUSTO_ID</em></p></li>
<li><p><em>DIRECAO_DESC</em></p></li>
<li><p><em>DIRECAO_ID</em></p></li>
<li><p><em>SECCAO_DESC</em></p></li>
<li><p><em>SECCAO_ID</em></p></li>
<li><p><em>CARREIRA_DESC</em></p></li>
<li><p><em>CARREIRA_ID</em></p></li>
<li><p><em>ESCALAO_DESC</em></p></li>
<li><p><em>ESCALAO_ID</em></p></li>
<li><p><em>CATEGORIA_DESC</em></p></li>
<li><p><em>CATEGORIA_ID</em></p></li>
<li><p><em>VINCULO_DESC</em></p></li>
<li><p><em>VINCULO_ID</em></p></li>
<li><p>DATA_INICIO_CONTRATO</p></li>
<li><p>DATA_FIM_CONTRATO</p></li>
<li><p>LOCAL_TRABALHO_DESC</p></li>
<li><p>PAIS_TRAB_DESC</p></li>
<li><p>ILHA_TRAB_DESC</p></li>
<li><p>LOCAL_TRABALHO_ID</p></li>
<li><p>DURACAO_CONTRATO</p></li>
<li><p>TIPO_DOCUMENTO</p></li>
<li><p>NUM_DOCUMENTO</p></li>
<li><p>FOTOGRAFIA</p></li>
<li><p>DATA_NASCIMENTO</p></li>
<li><p>SEXO</p></li>
<li><p>NM_MAE</p></li>
<li><p>NM_PAI</p></li>
<li><p>ESTADO_CIVIL</p></li>
<li><p>NACIONALIDADE</p></li>
<li><p>LOC_NASC_ID</p></li>
<li><p>NIF</p></li>
<li><p>NU_SEG_INPS</p></li>
<li><p>CONTACTO</p></li>
<li><p>ENDERECO</p></li>
<li><p>REGIME_TRABALHO_DESC</p></li>
<li><p>VALOR</p></li>
<li><p>MOEDA</p></li>
<li><p>SITUACAO_LABORAL</p></li>
</ul></td>
</tr>
<tr>
<td>Contacto do funcionario</td>
<td>RH_V_CONTATO</td>
<td></td>
</tr>
<tr>
<td>Lista mobilidade</td>
<td><mark>RH_V_MOBILIDADE</mark></td>
<td><ul>
<li><p><em>TIPO_SITUACAO_DESC</em></p></li>
<li><p>DATA_INICIO</p></li>
<li><p>DATA_FIM</p></li>
<li><p>FUN_ID</p></li>
<li><p>DIRECAO_DESC</p></li>
<li><p>DIRECAO_ID</p></li>
<li><p>CARGO_DESC</p></li>
<li><p>CARGO_ID</p></li>
<li><p>SECAO_DESC</p></li>
<li><p>SECAO_ID</p></li>
<li><p>LOCAL_TRAB_DESC</p></li>
<li><p>CONTRATO_ID</p></li>
<li><p>PROCESSAMENTO</p></li>
</ul></td>
</tr>
<tr>
<td>Lista Carreira</td>
<td><mark>RH_V_CARREIRA</mark></td>
<td><ul>
<li><p>DATA_INICIO</p></li>
<li><p>DATA_FIM</p></li>
<li><p>FUN_ID</p></li>
<li><p>CONTRATO_ID</p></li>
<li><p>PROCESSAMENTO</p></li>
<li><p>TIPO_CARREIRA</p></li>
<li><p>VINCULO_DESC</p></li>
<li><p>VINCULO_ID</p></li>
<li><p>CARREIRA_DESC</p></li>
<li><p>CARREIRA_ID</p></li>
<li><p>CARGO_DESC</p></li>
<li><p>CARGO_ID</p></li>
<li><p>ESCALAO_DESC</p></li>
<li><p>ESCALAO_ID</p></li>
<li><p>SALARIO</p></li>
<li><p><em>TIPO_SITUACAO_DESC</em></p></li>
<li><p>TP_CONTRATO</p></li>
</ul></td>
</tr>
<tr>
<td>Historico Laboral</td>
<td>RH_V_HIST_LABORAL</td>
<td><ul>
<li><p>TIPREL_ID</p></li>
<li><p>CONTRATO_ID</p></li>
<li><p>CARREIRA_ID</p></li>
<li><p>MOB_ID</p></li>
<li><p>FUN_ID</p></li>
<li><p>SITUACAO_LABORAL_ID</p></li>
<li><p>REGIME_ID</p></li>
<li><p>ESTADO_DESC</p></li>
<li><p>ESTADO</p></li>
<li><p><em>VINCULO_DESC</em></p></li>
<li><p><em>VINCULO_ID</em></p></li>
<li><p><em>TIPO_CONTRATO_DESC</em></p></li>
<li><p><em>TIPO_CONTRATO_ID</em></p></li>
<li><p>TIPO_SITUACAO_DESC</p></li>
<li><p><em>REFERENCIA_ESCALAO_DESC</em></p></li>
<li><p><em>REFERENCIA_ESCALAO_ID</em></p></li>
<li><p><em>CARGO_DESC</em></p></li>
<li><p><em>CARGO_ID</em></p></li>
<li><p><em>CARREIRA_DESC</em></p></li>
<li><p><em>CARREIRA_ID</em></p></li>
<li><p><em>CATEGORIA_DESC</em></p></li>
<li><p><em>CATEGORIA_ID</em></p></li>
<li><p><em>SECCAO_DESC</em></p></li>
<li><p><em>SECCAO_ID</em></p></li>
<li><p><em>DIRECAO_DESC</em></p></li>
<li><p><em>DIRECAO_ID</em></p></li>
<li><p>CENTRO_CUSTO_DESC</p></li>
<li><p>CENTRO_CUSTO_ID</p></li>
<li><p><em>DATA_INICIO</em></p></li>
<li><p><em>DATA_FIM</em></p></li>
<li><p>ESTADO_CONTRATO</p></li>
<li><p>TIPO_MOLBILIDADE</p></li>
<li><p>REGIME_TRABALHO_DESC</p></li>
<li><p>SALARIO</p></li>
<li><p>MOEDA</p></li>
<li><p><em>DURACAO_CONTRATO</em></p></li>
<li><p>LOCAL_TRABALHO_DESC</p></li>
<li><p>LOCAL_TRABALHO_ID</p></li>
<li><p>PAIS</p></li>
<li><p>ILHA</p></li>
<li><p>ULTIMO_PROC</p></li>
<li><p>ULTIMO_VINCULO</p></li>
<li><p>SITUACAO_LABORAL_DESC</p></li>
<li><p>REFERENCIA</p></li>
</ul></td>
</tr>
<tr>
<td>RENDIMENTOS E ENCARGOS</td>
<td>RH_V_REND_ENC</td>
<td><ul>
<li><p>ESTADO</p></li>
<li><p>ESTADO_DESC</p></li>
<li><p>MOVIMENTO_DESC</p></li>
<li><p>VALOR</p></li>
<li><p>DATA_INICIO</p></li>
<li><p>DATA_FIM</p></li>
<li><p>ULTIMO_PROC</p></li>
<li><p>TIPO (‘REM’ OU 'PAG' )</p></li>
<li><p>TIPREL_ID</p></li>
<li><p>TIPO_MOBILIDADE</p></li>
<li><p>PERCENTAGEM</p></li>
<li><p>TM_ID</p></li>
<li><p>TIPREL_ID</p></li>
<li><p>ESTADO_CONTRATO</p></li>
</ul></td>
</tr>
</tbody>
</table>

- Grupo auxiliar

- Cargo técnico

- Cargo de apoio

- Grupo gestão

*faz update est_ult_adm = 0 e data \_fim = data inicio do novo registo*

# DOSSIER DO COLABORADOR 

## Registo de colaborador 

Sempre que for registado um esse registo deve ir para validação.

- <span class="mark">O **TIPO DE SITUAÇÃO** em todas tabelas deve ser
  ‘**’INICIO**””</span>

<table>
<colgroup>
<col style="width: 35%" />
<col style="width: 64%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><strong>TABELA</strong></th>
<th style="text-align: center;"><strong>DESCRIÇÃO</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" style="text-align: left;"><strong>DADOS
PESSOAIS</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_FUNCIONARIOS</td>
<td style="text-align: left;">Registos dados pessoais</td>
</tr>
<tr>
<td style="text-align: left;">RH_T_DADOS_PESSOAIS</td>
<td style="text-align: left;">Registo de dado de documento</td>
</tr>
<tr>
<td style="text-align: left;">RH_T_CONTACTO</td>
<td style="text-align: left;">Registo de dados de contacto</td>
</tr>
<tr>
<td style="text-align: left;">RH_T_ENDERECO</td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td colspan="2"
style="text-align: left;"><strong>AGREGADO/DEPENDENTES</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_FAMILIARES</td>
<td style="text-align: left;"><p>Registo de dados de familiar,</p>
<p><strong>Nota</strong>: deve ter em conta que um dependente deve ter
um único responsável, ou seja, não colaborador não pode ser responsável
da mesma pessoa</p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><strong>DADOS ACADÉMICOS E
PROFISSIONAL</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_HABILITACOES_LITERARIAS</td>
<td style="text-align: left;">Registo de habilitações literária</td>
</tr>
<tr>
<td style="text-align: left;">RH_T_FORMACAO_FEITOS</td>
<td style="text-align: left;">Registo de formação</td>
</tr>
<tr>
<td style="text-align: left;">RH_T_EXPERIENCIA_PROF</td>
<td style="text-align: left;">Registo de experiência profissional</td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><strong>DADOS
CONTRATUAIS</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_CONTRATO_VINCULO</td>
<td style="text-align: left;">Registo de <strong>Tipo de
contrato</strong> e <strong>Tipo Vínculo</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_SITUACAO_LABORAL</td>
<td style="text-align: left;">Registo de <strong>Situação
Laboral</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_REGIME_TRAB</td>
<td style="text-align: left;">Registo de <strong>Regime de
Trabalho</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_MOBILIDADE</td>
<td style="text-align: left;">Registo de <strong>Direção</strong>,
<strong>Unidade Orgânica</strong> e <strong>Local de
Trabalho</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_CARREIRA</td>
<td style="text-align: left;"><p>Registo de <strong>Carreira</strong>,
<strong>Escalão</strong>, <strong>Cargo</strong></p>
<p><strong>Nota:</strong> somente deve fazer registo nessa tabela Caso o
Tipo de vínculo selecionado tem carreira
<strong>(RH_T_PARAM_VINCULO.FLG_CARREIRA = 1)</strong></p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_TIPOS_RELACIONAMENTO</td>
<td style="text-align: left;">Registo de associação entre as tabelas
(Mobilidade, Carreira, Situação laboral)</td>
</tr>
<tr>
<td style="text-align: left;"></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_DEF_REMUNERACOES</td>
<td style="text-align: left;"><ol type="1">
<li><p>Faz um registo do Salário</p></li>
<li><p>Faz registos de subsídios (número de registo dependendo do número
de subsídio adicionado)</p></li>
</ol>
<p><strong>Nota</strong>: somente deve registar Nessa tabela caso o
colaborador tem Salário</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_DEF_PAGAMENTOS</td>
<td style="text-align: left;"><ol type="1">
<li><p>Faz 3 registos de desconto (<strong>IUR</strong>, <strong>Valor
Líquido</strong>, <strong>INPS</strong>)</p></li>
<li><p>Faz registos de descontos (número de registo dependendo do número
de subsídio adicionado)</p></li>
</ol>
<p><strong>Nota</strong>: somente deve registar Nessa tabela caso o
colaborador tem salário</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_TIPREL_REM_PAG</td>
<td style="text-align: left;"><p>Essa tabela é responsável de fazer
associação ente a tabela de RH_T_TIPOS_RELACIONAMENTO
RH_T_DEF_PAGAMENTOS E REMUNERACOES</p>
<p><strong>Nota</strong>: somente deve registar Nessa tabela caso o
colaborador tem salário</p></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><strong>DADOS
BANCARIOS</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_DADOS_BANCARIOS</td>
<td style="text-align: left;">Registo de dados bancários</td>
</tr>
<tr>
<td style="text-align: left;"><strong>ANEXAR DOCUMENTO</strong></td>
<td style="text-align: left;"></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_DOCUMENTO</td>
<td style="text-align: left;">Registo de documentos anexados</td>
</tr>
</tbody>
</table>

## Novo Contrato

A criação de um novo contrato deve gerar novos registos em todas as
tabelas associadas, **com exceção das tabelas de Mobilidade e Regime**.

Para **Mobilidade** e **Regime**, apenas deve ser criado um novo registo
quando se verificar uma das seguintes condições:

- existir uma alteração relativamente ao registo atual; ou

- não existir nenhum registo ativo referente a mobilidade e Regime para
  o colaborador.

Caso já exista um registo ativo e não tenha ocorrido qualquer alteração,
**não deve ser criado um novo registo**, mantendo-se o registo
atualmente em vigor.

- O campo **tipo situação** para as tabelas deve Ser “**NOVO_CONTRATO**”

- A criação de um novo Contrato deve passar para **Validação**

<table>
<colgroup>
<col style="width: 34%" />
<col style="width: 65%" />
</colgroup>
<thead>
<tr>
<th colspan="2" style="text-align: left;"><strong>DADOS
CONTRATUAIS</strong></th>
</tr>
</thead>
<tbody>
<tr>
<td style="text-align: left;">RH_T_CONTRATO_VíNCULO</td>
<td style="text-align: left;">Registo de <strong>Tipo de
contrato</strong> e <strong>Tipo Vínculo</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_SITUACAO_LABORAL</td>
<td style="text-align: left;">Registo de <strong>Situação
Laboral</strong></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_REGIME_TRAB</td>
<td style="text-align: left;"><p><strong>Regra de Registo de
Regime</strong></p>
<p>Um novo registo de regime somente deve ser criado quando se verificar
pelo menos uma das seguintes condições:</p>
<ul>
<li><p>houver alteração de regime; ou</p></li>
<li><p>não existir nenhuma <strong>regime ativa</strong> para o
colaborador.</p></li>
</ul>
<p>Caso contrário, isto é, quando já existir um regime ativa e não
houver qualquer alteração, <strong>não deve ser criado um novo registo
de Regime</strong>.</p>
<p>Nesse caso, o sistema deve recuperar o <strong>ID da Regime
ativa</strong> e associá-lo ao novo registo de <strong>Tipo de
Relacionamento</strong> criado para o colaborador.</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_MOBILIDADE</td>
<td style="text-align: left;"><p><strong>Regra de Registo da
Mobilidade</strong></p>
<p>Um novo registo de <strong>Mobilidade</strong> somente deve ser
criado quando se verificar pelo menos uma das seguintes condições:</p>
<ul>
<li><p>houver alteração da <strong>Direção</strong>, <strong>Unidade
Orgânica</strong> ou <strong>Local de Trabalho</strong>; ou</p></li>
<li><p>não existir nenhuma <strong>Mobilidade ativa</strong> para o
colaborador.</p></li>
</ul>
<p>Caso contrário, isto é, quando já existir uma Mobilidade ativa e não
houver qualquer alteração na Direção, Unidade Orgânica ou Local de
Trabalho, <strong>não deve ser criado um novo registo de
Mobilidade</strong>.</p>
<p>Nesse caso, o sistema deve recuperar o <strong>ID da Mobilidade
ativa</strong> e associá-lo ao novo registo de <strong>Tipo de
Relacionamento</strong> criado para o colaborador.</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_CARREIRA</td>
<td style="text-align: left;"><p>Registo de <strong>Carreira</strong>,
<strong>Escalão</strong>, <strong>Cargo</strong></p>
<p><strong>Nota:</strong> somente deve fazer registo nessa tabela Caso o
Tipo de vínculo selecionado tem carreira
<strong>(RH_T_PARAM_VINCULO.FLG_CARREIRA = 1)</strong></p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_TIPOS_RELACIONAMENTO</td>
<td style="text-align: left;"><p>Registo de associação entre as tabelas
(Mobilidade, Carreira, Situação laboral)</p>
<ul>
<li><p>Este registo passa a ser o último Relacionamento (EST_ACT_ADM =
1)</p></li>
<li><p>O campo Tiprel_id: deve ser preenchida com id do
anterior</p></li>
<li><p>O anterior deve ficar para histórico (EST_ACT_ADM = 0)</p></li>
</ul></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_DEF_REMUNERACOES</td>
<td style="text-align: left;"><ol start="3" type="1">
<li><p>Faz um registo do Salário</p></li>
<li><p>Faz registos de subsídios (número de registo dependendo do número
de subsídio adicionado)</p></li>
<li><p>A data início e data Fim deve ser igual ao do novo
Contrato.</p></li>
<li><p>Campos OBS = Novo Contrato</p></li>
</ol>
<p><strong>Nota</strong>: somente deve registar Nessa tabela caso o
colaborador tem Salário</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_DEF_PAGAMENTOS</td>
<td style="text-align: left;"><ol start="3" type="1">
<li><p>Faz 3 registos de desconto (<strong>IUR</strong>, <strong>Valor
Líquido</strong>, <strong>INPS</strong>)</p></li>
<li><p>Faz registos de descontos (número de registo dependendo do número
de subsídio adicionado)</p></li>
<li><p>Campos OBS = Novo Contrato</p></li>
</ol>
<p><strong>Nota</strong>: somente deve registar Nessa tabela caso o
colaborador tem salário</p></td>
</tr>
<tr>
<td style="text-align: left;">RH_T_TIPREL_REM_PAG</td>
<td style="text-align: left;"><p>Essa tabela é responsável de fazer
associação ente a tabela de RH_T_TIPOS_RELACIONAMENTO
RH_T_DEF_PAGAMENTOS E REMUNERACOES</p>
<p><strong>Nota</strong>: somente deve registar Nessa tabela caso o
colaborador tem salário</p></td>
</tr>
</tbody>
</table>

## Renovação de Contrato

A renovação de contrato deve afetar as seguintes tabelas

<table>
<colgroup>
<col style="width: 39%" />
<col style="width: 60%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">TABELA</th>
<th style="text-align: center;">DESCRIÇÃO</th>
</tr>
</thead>
<tbody>
<tr>
<td>RH_T_CONTRATO_HISTORICO</td>
<td><ul>
<li><p>Novo Registo nessa tabela</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_TIPOS_RELACIONAMENTO</td>
<td><ul>
<li><p>Novo Registo nessa tabela (pega todos dados do anterior e somente
muda o Tipo situação = “RENOVACAO_CONTRATO”)</p></li>
</ul>
<p>Update O ANTERIOR:</p>
<ul>
<li><p><strong>EST_ACT_ADM</strong> = Atualiza o campo do registo
anterior para 0</p></li>
<li><p><strong>DATA_FIM</strong>: Data do registo</p></li>
</ul></td>
</tr>
<tr>
<td><p>RH_T_CARREIRA</p>
<p>RH_T_DEF_PAGAMENTOS</p>
<p>RH_T_MOBILIDADE</p>
<p>RH_T_DEF_REMUNERACOES</p>
<p>RH_T_REGIME_TRAB</p>
<p>RH_T_SITUACAO_LABORAL</p></td>
<td><ul>
<li><p>Deve atualizar a data fim nessas tabelas</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_TIPREL_REM_PAG</td>
<td><ul>
<li><p>pega todos os registos de <strong>tiprel_id</strong> anterior e
faz um novo registo com novo <strong>tiprel_id</strong></p></li>
</ul></td>
</tr>
</tbody>
</table>

##  Mobilidade 

Registo de nova mobilidade ou atualização de **Direção**, **Unidade
Orgânica** ou **Local de trabalho**

<span class="mark">**Nota**: registo e alteração passa por
validação</span>

<table>
<colgroup>
<col style="width: 39%" />
<col style="width: 60%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">TABELA</th>
<th style="text-align: center;">DESCRIÇÃO</th>
</tr>
</thead>
<tbody>
<tr>
<td>RH_T_MOBILIDADE</td>
<td><p>Nova mobilidade</p>
<ul>
<li><p>Cria um registo Nessa tabela</p></li>
<li><p>Atualiza o que estava do anterior, ativo para inativo</p></li>
</ul>
<p>Editar uma mobilidade</p>
<ul>
<li><p><mark><strong>update</strong> registo</mark></p></li>
<li><p><mark>O BOTÃO: somente deve ficar visível caso a mobilidade não
tenha um processamento.</mark></p></li>
</ul>
<p><strong>Nota</strong>:</p>
<ul>
<li><p>um colaborador deve ter um único registo de mobilidade
ativo.</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_TIPOS_RELACIONAMENTO</td>
<td><p><strong>Caso houver novo registo de mobilidade</strong></p>
<p>Registo: Cria um registo Nessa tabela</p>
<ul>
<li><p><strong>EST_ACT_ADM</strong> = 1</p></li>
<li><p><strong>TIPREL_ID</strong> = id de RH_T_TIPOS_RELACIONAMENTO do
registo do último vínculo anterior</p></li>
<li><p><strong>DATA_INICIO:</strong> Data do registo</p></li>
<li><p><strong>DATA_FIM</strong> : nulo</p></li>
<li><p><strong>CARREIRA_ID</strong>: mantém o mesmo id</p></li>
<li><p><strong>MOB_ID</strong>: Novo id</p></li>
<li><p><strong>SITUAC_LABORAL_ID</strong>: = mantém o mesmo id</p></li>
<li><p><strong>TIPO_SITUACAO</strong> = referente ao tipo de
mobilidade</p></li>
</ul>
<p>Update:</p>
<ul>
<li><p><strong>EST_ACT_ADM</strong> = Atualiza o campo do registo
anterior para 0</p></li>
<li><p><strong>DATA_FIM</strong>: Data do registo</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_TIPREL_REM_PAG</td>
<td>pega todos os registos de <strong>tiprel_id</strong> anterior e faz
um novo registo com novo <strong>tiprel_id</strong></td>
</tr>
</tbody>
</table>

## Carreira

- registo de um nova Carreira, debe passar pela validação

- Atulização de uma carrera somente passa para validação, caso houver
  alteração no escalão

- Atualizacoes de campos que provocam uma Carreira: **CARGO**\_ID,
  **CARR_PCCS_ID**, **ESCALAO_ID**,

- Atualiçoes que nao implica um nova carreira: ex: DATA_FIM,

<table>
<colgroup>
<col style="width: 36%" />
<col style="width: 63%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">TABELA</th>
<th style="text-align: center;">DESCRIÇÃO</th>
</tr>
</thead>
<tbody>
<tr>
<td>RH_T_CARREIRA</td>
<td><p>Nova carreira</p>
<ul>
<li><p>Cria um registo Nessa tabela</p></li>
<li><p>O Campo <strong>Tipo Carreira</strong>, deve vir preenchido por
defeito com a referência do domino
<strong>CARREIRA_NOVO</strong></p></li>
</ul>
<p>Editar Carreira</p>
<ul>
<li><p>O Campo <strong>Tipo Carreira</strong>, deve vir preenchido por
defeito com a referência do domino
<strong>CARREIRA_EDITAR</strong></p></li>
</ul>
<p><strong>Regras de Alteração da Carreira</strong></p>
<p><strong>1. Caso a Carreira ainda não tenha sido
processada</strong></p>
<p>Quando o registo da Carreira ainda não tiver sido processado, mas
validado,o sistema deve permitir a alteração dos dados e efetuar um
<strong>UPDATE</strong> no registo existente.</p>
<p>Caso seja alterado pelo menos um dos seguintes campos:</p>
<ul>
<li><p><strong>CARGO_ID</strong>;</p></li>
<li><p><strong>CARR_PCCS_ID</strong>;</p></li>
<li><p><strong>ESCALAO_ID</strong>;</p></li>
</ul>
<p>o registo deve ser novamente submetido ao processo de
<strong>validação</strong>.</p>
<p><strong>2. Caso a Carreira já tenha sido processada</strong></p>
<p>Quando o registo da Carreira já tiver sido processado:</p>
<ul>
<li><p>todos os campos do formulário devem ficar <strong>desabilitados
para edição</strong>, com exceção dos campos <strong>Data Fim</strong> e
<strong>Processa Salário</strong>;</p></li>
<li><p>apenas estes dois campos podem ser alterados.</p></li>
</ul>
<p>Caso o campo <strong>FLG_PROCESSA</strong> (<strong>Processa
Salário</strong>) seja alterado de <strong>“Não” para “Sim”</strong>, o
sistema deve:</p>
<ol type="1">
<li><p>criar um novo registo na tabela
<strong>RH_T_TIPOS_RELACIONAMENTO</strong>; e</p></li>
<li><p>criar, consequentemente, o respetivo registo na tabela
<strong>RH_T_TIPREL_REM_PAG</strong>, associado ao novo <strong>Tipo de
Relacionamento</strong> criado.</p></li>
</ol>
<p>A alteração de <strong>“Sim” para “Não”</strong> deve atualizar
diretamente o Tipo de Relacionamento (EST_ACT_ADM = 0).</p>
<p><strong>Nota</strong>: um colaborador pode ter duas carreiras ativas,
mas somente uma delas deve estar marcada para Processar</p>
<p><mark>Progressão / Promoção</mark></p>
<ul>
<li><p>O campo promoção / progressão, deve ficar a ficar visível os
campos de <strong>Tipo Carreira</strong>, <strong>escalão</strong>,
<strong>Data</strong> <strong>início</strong>, <strong>data
Fim</strong>, <strong>processa salário Nesta Carreira</strong></p></li>
<li><p>O Campo Tipo Carreira, deve vir preenchido por defeito com a
referência do domino <strong>CARREIRA_PROG_PROMO</strong></p></li>
<li><p>Ao registar Progressão, deve afetar as tabelas de
<strong>RH_T_CARREIRA</strong>, <strong>RH_T_TIPOS_RELACIONAMENTO.
RH_T_DEF_REMUNERACOES , RH_T_TIPREL_REM_PAG</strong></p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_TIPOS_RELACIONAMENTO</td>
<td><p>Registo de uma nova carreira implica novo registo de tipos
Relacionamento</p>
<p>Registo: Cria um registo Nessa tabela</p>
<ul>
<li><p><strong>EST_ACT_ADM</strong> = 1</p></li>
<li><p><strong>TIPREL_ID</strong> = id de RH_T_TIPOS_RELACIONAMENTO do
registo do último vínculo anterior</p></li>
<li><p><strong>DATA_INICIO:</strong> Data do registo</p></li>
<li><p><strong>DATA_FIM</strong>: nulo</p></li>
<li><p><strong>CARREIRA_ID</strong>: Novo id</p></li>
<li><p><strong>MOB_ID</strong>: mantém o mesmo id</p></li>
<li><p><strong>SITUAC_LABORAL_ID</strong>: = mantém o mesmo id</p></li>
<li><p><strong>TIPO_SITUACAO</strong> = referente ao tipo de
mobilidade</p></li>
<li><p><strong>FLG_PROCESSA = 1</strong></p></li>
</ul>
<p>Update:</p>
<ul>
<li><p><strong>EST_ACT_ADM</strong> = Atualiza o campo do registo
anterior para 0</p></li>
<li><p><strong>DATA_FIM</strong>: Data do registo</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_DEF_REMUNERACOES</td>
<td><p>Registo de uma nova Carreira</p>
<p>O registo de uma nova carreira sempre implica novos registos de
Salário</p>
<ul>
<li><p>Faz o registo referente ao salário</p></li>
<li><p>Faz os registos de subsídios adicionados na carreira</p></li>
<li><p><mark>Campo OBS = Tipo situação de Carreira</mark></p></li>
</ul>
<p>Atualização Carreira</p>
<ul>
<li><p>Faz um novo registo de salário, caso for atualizado o
escalão</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_DEF_PAGAMENTOS</td>
<td><p>Faz um registo nessa tabela somente quando houver o registo de
uma nova carreira</p>
<ol type="1">
<li><p>Faz 3 registos de desconto (<strong>IUR</strong>, <strong>Valor
Líquido</strong>, <strong>INPS</strong>)</p></li>
<li><p>Faz registos de descontos (número de registo dependendo do número
de subsídio adicionado)</p></li>
<li><p><mark>Campo OBS = Tipo situação de Carreira</mark></p></li>
</ol></td>
</tr>
<tr>
<td>RH_T_TIPREL_REM_PAG</td>
<td>pega todos os registos de <strong>tiprel_id</strong> anterior e faz
um novo registo com novo <strong>tiprel_id</strong></td>
</tr>
</tbody>
</table>

## Gestão Laboral 

### Registo de Relação Laboral

<table>
<colgroup>
<col style="width: 43%" />
<col style="width: 56%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">TABELA</th>
<th style="text-align: center;">DESCRIÇÃO</th>
</tr>
</thead>
<tbody>
<tr>
<td>RH_T_SITUACAO LABORAL</td>
<td><p><strong>Regra de Registo e Atualização da Situação
Laboral</strong></p>
<ul>
<li><p>Deve ser criado um <strong>registo</strong> nesta tabela somente
quando houver alteração da <strong>Situação Laboral</strong> ou do
<strong>Motivo</strong>, desde que o registo atual já tenha sido
<strong>processado</strong>.</p></li>
<li><p>Caso haja alteração da <strong>Situação Laboral</strong> ou do
<strong>Motivo</strong> e o registo atual <strong>ainda não tenha sido
processado</strong>, o sistema deve apenas <strong>atualizar (UPDATE) o
registo existente</strong>, não devendo criar um novo registo.</p></li>
<li><p>Caso não haja qualquer alteração na <strong>Situação
Laboral</strong> ou no <strong>Motivo</strong>, não deve ser efetuado
nenhum novo registo nem atualização.</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_MOBILIDADE</td>
<td>Campos do Formulário — Somente de Leitura</td>
</tr>
<tr>
<td>RH_T CARREIRA</td>
<td>Campos do Formulário — Somente de Leitura</td>
</tr>
<tr>
<td>RH_T_TIPOS_RELACIONAMENTO</td>
<td><p>Faz um registo aqui sempre que se fizer registo em uma das
tabelas acima e deve fazer as seguintes ações</p>
<p>Registo: Cria um registo Nessa tabela</p>
<ul>
<li><p><strong>EST_ACT_ADM</strong> = 1</p></li>
<li><p><strong>TIPREL_ID</strong> = id de RH_T_TIPOS_RELACIONAMENTO do
registo do último vínculo anterior</p></li>
<li><p><strong>DATA_INICIO:</strong> Data do registo</p></li>
<li><p><strong>DATA_FIM</strong>: nulo</p></li>
<li><p><strong>CARREIRA_ID</strong>: Novo id, caso houver novo registo
de carreira</p></li>
<li><p><strong>MOB_ID</strong>: Novo id, caso houver novo registo de
mobilidade</p></li>
<li><p><strong>SITUAC_LABORAL_ID</strong>: = : Novo id, caso houver novo
registo de situação laboral</p></li>
<li><p><strong>TIPO_SITUACAO</strong> = referente ao tipo de mobilidade;
carreira ou situação laboral</p></li>
<li><p><mark><strong>FLG_PROCESSA :</strong> Ter cuidado nesse campo,
dependente da situação laboral selecionado pode ficar 0 ou 1
(<strong>Ver se a situação tem remuneração ou não -
<em>RH_T_PARAM_SITUACAO.FLG_REMUNERACAO</em></strong>)</mark></p></li>
</ul>
<p>Update:</p>
<ul>
<li><p><strong>EST_ACT_ADM</strong> = Atualiza o campo do registo
anterior para 0</p></li>
<li><p><strong>DATA_FIM</strong>: Data do registo</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_TIPREL_REM_PAG</td>
<td style="text-align: left;">Sempre que houver novo de
<strong>RH_T_TIPOS_RELACIONAMENTO</strong>, logo, pega todos os registos
de <strong>tiprel_id</strong> anterior e faz um novo registo com novo
<strong>tiprel_id</strong></td>
</tr>
</tbody>
</table>

### ~~Eliminar relação Laboral~~

~~Nota somente se deve o registo caso ainda não esteja validado ou
processado~~

~~Ao eliminar um RH_T_TIPOS_RELACIONAMENTO, logo se deve eliminar em
outras tabelas nas quais provocaram esse registo~~

<table>
<colgroup>
<col style="width: 43%" />
<col style="width: 56%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;"><del>TABELA</del></th>
<th style="text-align: center;"><del>DESCRIÇÃO</del></th>
</tr>
</thead>
<tbody>
<tr>
<td><del>RH_T_MOBILIDADE</del></td>
<td><p><del>Somente se elimina Nessa tabela caso for feito um novo
registo descrito no ponto <strong>1.4.1</strong></del></p>
<p><del>Caso sim:</del></p>
<ul>
<li><p><del>Caso sim deve atualizar o registo para
eliminado</del></p></li>
<li><p><del>Ativa o registo anterior</del></p></li>
</ul></td>
</tr>
<tr>
<td><del>RH_T CARREIRA</del></td>
<td><p><del>Somente se elimina Nessa tabela caso for feito um novo
registo descrito no ponto <strong>1.4.1</strong></del></p>
<p><del>Caso sim:</del></p>
<ul>
<li><p><del>Caso sim deve atualizar o registo para
eliminado</del></p></li>
<li><p><del>Ativa o registo anterior</del></p></li>
</ul></td>
</tr>
<tr>
<td><del>RH_T_SITUACAO LABORAL</del></td>
<td><p><del>Somente se elimina Nessa tabela caso for feito um novo
registo descrito no ponto <strong>1.4.1</strong></del></p>
<p><del>Caso sim:</del></p>
<ul>
<li><p><del>Caso sim deve atualizar o registo para
eliminado</del></p></li>
<li><p><del>Ativa o registo anterior</del></p></li>
</ul></td>
</tr>
<tr>
<td><del>RH_T_TIPOS_RELACIONAMENTO</del></td>
<td><p><del>Deve fazer o seguinte Ações</del></p>
<ul>
<li><p><del>O registo a ser eliminado deve mudar estado para eliminado
RH_T_TIPOS_RELACIONAMENTO.ESTADO =E e EST_ACT_ADM = 0</del></p></li>
<li><p><del>O registo anterior deve ficar com EST_ACT_ADM
=1</del></p></li>
</ul></td>
</tr>
<tr>
<td><del>RH_T_DEF_REMUNERACOES</del></td>
<td><p><del>Somente elimina aqui caso for feito um registo em
1.4.1</del></p>
<ul>
<li><p><del>Caso sim deve atualizar o registo para
eliminado</del></p></li>
</ul></td>
</tr>
<tr>
<td><del>RH_T_DEF_PAGAMENTOS</del></td>
<td><del>Somente elimina aqui caso for feito um registo em
1.4.1</del></td>
</tr>
<tr>
<td><del>RH_T_TIPREL_REM_PAG</del></td>
<td><del>Somente elimina aqui caso for feito um registo em
1.4.1</del></td>
</tr>
</tbody>
</table>

### Situação Laboral

<table>
<colgroup>
<col style="width: 39%" />
<col style="width: 60%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">TABELA</th>
<th style="text-align: center;">DESCRIÇÃO</th>
</tr>
</thead>
<tbody>
<tr>
<td>RH_T_SITUACAO_LABORAL</td>
<td><p>Novo registo</p>
<ul>
<li><p>Faz registo Nessa tabela</p></li>
</ul>
<p>Editar</p>
<ul>
<li><p>Somente faz um novo registo caso for alterado situação laboral ou
motivo (SITUACAO_LABORAL_ID, MOTIVO_SIT_LAB_ID), caso contrário somente
faz update</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_TIPOS_RELACIONAMENTO</td>
<td><p><strong>Caso houver novo registo de situação laboral</strong></p>
<p>Registo: Cria um registo Nessa tabela</p>
<ul>
<li><p><strong>EST_ACT_ADM</strong> = 1</p></li>
<li><p><strong>TIPREL_ID</strong> = id de RH_T_TIPOS_RELACIONAMENTO do
registo do último vínculo anterior</p></li>
<li><p><strong>DATA_INICIO:</strong> Data do registo</p></li>
<li><p><strong>DATA_FIM</strong> : nulo</p></li>
<li><p><strong>CARREIRA_ID</strong>: mantém o mesmo id</p></li>
<li><p><strong>MOB_ID</strong>: Novo id</p></li>
<li><p><strong>SITUAC_LABORAL_ID</strong>: = mantém o mesmo id</p></li>
<li><p><strong>TIPO_SITUACAO</strong> = referente ao tipo de
mobilidade</p></li>
<li><p><mark><strong>FLG_PROCESSA :</strong> Ter cuidado nesse campo,
dependente da situação laboral selecionado pode ficar 0 ou 1
(<strong>Ver se a situação tem remuneração ou não -
<em>RH_T_PARAM_SITUACAO.FLG_REMUNERACAO</em></strong>)</mark></p></li>
</ul>
<p>Update:</p>
<ul>
<li><p><strong>EST_ACT_ADM</strong> = Atualiza o campo do registo
anterior para 0</p></li>
<li><p><strong>DATA_FIM</strong>: Data do registo</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_TIPREL_REM_PAG</td>
<td>pega todos os registos de <strong>tiprel_id</strong> anterior e faz
um novo registo com novo <strong>tiprel_id</strong></td>
</tr>
</tbody>
</table>

## Substituição

- A **Substituição** apenas pode ser submetida para validação quando
  existir, pelo menos, um registo de **Diferença Salarial** associado ao
  substituto, desde que este tenha um valor adicional a receber em
  resultado da substituição.

<!-- -->

- Os registos de **Diferença Salarial** devem ser gerados e registados
  na tabela **RH_T_DEF_REMUNERACOES**, obedecendo às seguintes regras:

  - A soma dos dias de substituição, no respetivo mês de referência,
    deve ser superior a **15 dias**;

  - Deve ser criado um registo de Diferença Salarial para cada mês a que
    o direito diga respeito;

  - Cada registo deve ser associado ao respetivo mês de referência;

  - Caso já exista um processamento salarial concluído para um
    determinado mês, a Diferença Salarial desse mês não deve ser
    registada nesse período, devendo ser registada data do mês seguinte
    e processada no mês seguinte.

<!-- -->

- Caso a Substituição não origine qualquer registo de **Diferença
  Salarial a favor do substituto**, o processo **não pode ser submetido
  para validação**.

<table>
<colgroup>
<col style="width: 36%" />
<col style="width: 63%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">TABELA</th>
<th style="text-align: center;">DESCRIÇÃO</th>
</tr>
</thead>
<tbody>
<tr>
<td>RH_T_SUBSTITUICAO</td>
<td style="text-align: left;">Revista as informações do colaborador a
ser sustituido</td>
</tr>
<tr>
<td>RH_T_SUBSTITUICAO_DETALHE</td>
<td style="text-align: left;">Revista as informados detalhadas
referentes a cada mes do período de substituição</td>
</tr>
<tr>
<td>RH_T_DEF_REMUNERACOES</td>
<td style="text-align: left;"><ul>
<li><p>O registo da diferença salarial deve ser efetuado somente quando
existir uma diferença salarial a favor do colaborador substituto, ou
seja, quando o salário do colaborador substituto for inferior ao salário
do colaborador substituído.</p></li>
<li><p>Neste caso, o sistema deve calcular a diferença entre os dois
salários e registar uma linha na tabela de movimentos, utilizando o
<strong>Tipo de Movimento “Diferença Salarial”</strong> parametrizado no
respetivo <strong>Vínculo</strong>.</p></li>
<li><p>Caso houver diferença esse registo deve passar por
validação</p></li>
<li><p>Campo <strong>OBS</strong> = ‘Substituição’</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_TIPREL_REM_PAG</td>
<td style="text-align: left;">Caso for Feito um registo em
<strong>RH_T_DEF_REMUNERACOES</strong>, logo deve fazer um registo Nessa
tabela</td>
</tr>
</tbody>
</table>

## Remunerações / Desconto

O Registo de uma nova Remuneração ou Desconto deve sempre passar para
validação

| TABELA                      | DESCRIÇÃO                    |
|-----------------------------|------------------------------|
| **Remunerações / subsídio** |                              |
| RH_T_DEF_REMUNERACOES       | Campo OBS = **Novo Registo** |
| RH_T_TIPREL_REM_PAG         |                              |
| **Pagamento / descontos**   |                              |
| RH_T_DEF_PAGAMENTOS         | Campo OBS = **Novo Registo** |
| RH_T_TIPREL_REM_PAG         |                              |

# Processamento Salarial

O Processamento deve seguir as seguintes etapas:

2.  **Fluxo de Estados Recomendado**

3.  **Regras Gerais do Workflow**

<!-- -->

1.  O fluxo dos estados deve ser obrigatoriamente sequencial.

2.  Não deve ser permitido saltar etapas.

3.  Cada ação apenas pode ser executada quando o processamento se
    encontrar no estado imediatamente anterior.

4.  Os botões devem ser automaticamente ativados ou desativados de
    acordo com o estado atual do processamento.

5.  Todas as alterações de estado devem ser registadas em auditoria
    (utilizador, data, hora e ação executada).

6.  Um colaborador (**RH_T_TIPOS_RELACIONAMENTO.ID**) não pode possuir
    mais do que um processamento para o mesmo Centro de Custo e para o
    mesmo mês.

7.  O sistema deve validar a integridade dos dados antes de cada
    transição de estado e impedir qualquer inconsistência no workflow.

No quadro seguinte presenta regras dos botões

<table>
<colgroup>
<col style="width: 30%" />
<col style="width: 69%" />
</colgroup>
<thead>
<tr>
<th>Botões</th>
<th style="text-align: left;">Descrição</th>
</tr>
</thead>
<tbody>
<tr>
<td><strong>Processar</strong></td>
<td style="text-align: left;"><p>Ao clicar no botão
<strong>Processar</strong>, o sistema deve processar todos os
colaboradores pertencentes ao Centro de Custo selecionado.</p>
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
<p><mark>O sistema Deve permitir processar um colaborador, uma Direção
ou Selecionar varias Direções e Processar</mark></p></td>
</tr>
<tr>
<td><strong>Eliminar</strong></td>
<td style="text-align: left;">O processamento apenas pode ser eliminado
quando se encontrar num dos seguintes estados:<br />
<br />
• <strong>PROCESSADO</strong><br />
• <strong>ERRO_PROCESSAMENTO</strong><br />
<br />
<strong>Regras:</strong><br />
• Caso o processamento se encontre num estado diferente dos indicados, a
eliminação não deve ser permitida.<br />
• O botão <strong>Eliminar</strong> deve permanecer desativado
(<em>disabled</em>) para todos os restantes estados.</td>
</tr>
<tr>
<td><strong>Validar Provisorio</strong></td>
<td style="text-align: left;">Um processamento apenas pode ser validado
quando se encontrar no estado:<br />
<br />
•<strong>PROCESSADO</strong><br />
<br />
<strong>Regras:</strong><br />
• O botão <strong>Validar</strong> apenas deve ficar ativo
(<em>enabled</em>) quando o processamento estiver no estado
<strong>PROCESSADO</strong>.<br />
• Caso seja necessário eliminar um processamento que já tenha sido
validado, o sistema deve permitir reverter o seu estado para
<strong>PROCESSADO</strong>, desde que ainda não exista Cabimento
associado.</td>
</tr>
<tr>
<td><strong>Validar Definitivo</strong></td>
<td style="text-align: left;">Um processamento apenas pode ser validado
quando se encontrar no estado:<br />
<br />
•<strong>VALIDADO_PROVISORIO</strong><br />
<br />
<strong>Regras:</strong><br />
• O botão <strong>Validar</strong> apenas deve ficar ativo
(<em>enabled</em>) quando o processamento estiver no estado
<strong>PROCESSADO</strong>.<br />
• Caso seja necessário eliminar um processamento que já tenha sido
validado, o sistema deve permitir reverter o seu estado para
<strong>PROCESSADO</strong>, desde que ainda não exista Cabimento
associado.</td>
</tr>
<tr>
<td><strong>CABIMENTAR</strong></td>
<td style="text-align: left;">Um processamento apenas pode ser
cabimentado quando se encontrar no estado:<br />
<br />
• <strong>VALIDADO_DEFINITIVO</strong><br />
<br />
<strong>Regras:</strong><br />
• Não deve ser possível cabimentar um processamento em qualquer outro
estado.<br />
• Depois de cabimentado, o processamento deixa de poder ser validado
novamente ou eliminado.<br />
• O sistema não deve permitir qualquer alteração de estado enquanto
existir um Cabimento ativo.<br />
• Para alterar o estado do processamento, deverá primeiro ser eliminado
o respetivo Cabimento.</td>
</tr>
<tr>
<td><strong>Eliminar Cabimento</strong></td>
<td style="text-align: left;">O Cabimento apenas pode ser eliminado
quando o processamento se encontrar no estado:<br />
<br />
• <strong>CABIMENTADO</strong><br />
<br />
<strong>Regras:</strong><br />
• Caso o processamento já tenha sido <strong>AUTORIZADO</strong>, a
eliminação do Cabimento não deve ser permitida.<br />
• Caso o processamento ainda não esteja <strong>CABIMENTADO</strong>, o
botão <strong>Eliminar Cabimento</strong> deve permanecer desativado
(<em>disabled</em>).</td>
</tr>
<tr>
<td><strong>Autorizar</strong></td>
<td style="text-align: left;">Um processamento apenas pode ser
autorizado quando se encontrar no estado:<br />
<br />
• <strong>CABIMENTADO</strong><br />
<br />
<strong>Regras:</strong><br />
• O sistema não deve permitir autorizar um processamento que não tenha
sido previamente cabimentado.<br />
• Após a autorização, o processamento passa a ser considerado
definitivo.<br />
• Depois de autorizado, deixa de ser possível executar qualquer uma das
etapas anteriores (<strong>Eliminar</strong>, <strong>Validar</strong>,
<strong>Cabimentar</strong> ou <strong>Eliminar Cabimento</strong>),
salvo se existir um mecanismo de anulação especificamente previsto para
esse efeito.</td>
</tr>
</tbody>
</table>

No quadro seguinte representa as ações em cada tabela em cada etapa:

<table>
<colgroup>
<col style="width: 34%" />
<col style="width: 65%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">TABELA</th>
<th style="text-align: center;">DESCRIÇÃO</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2"><strong>Processar Salário</strong></td>
</tr>
<tr>
<td>RH_T_PROC_SALARIOS</td>
<td><p>Faz um registo nessa tabela por cada direção processado</p>
<p>Relação das chaves estrangeiras:</p>
<ul>
<li><p><strong>CC_ID</strong>: ID INPSSIGOF.CENTROS_CUSTO</p></li>
<li><p><strong>CAB_1_ID</strong>: caso o processamento for cabimento
deve preencher esse cambo com o id de cabimento devolvido no serviço de
cabimento.</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_PROC_FUNCIONARIOS</td>
<td><p>Faz um registo nessa tabela por cada colaborador processado em
uma direção</p>
<p>Relação das chaves estrangeiras:</p>
<ul>
<li><p><strong>RHB_ID</strong> = ID DE RH_T_DEF_PAGAMENTOS</p></li>
<li><p><strong>TIPREL_ID = ID</strong> DE
RH_T_TIPOS_RELACIONAMENTO</p></li>
<li><p><strong>PRSALS_ID =</strong> ID DE RH_T_PROC_SALARIOS</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_REMUNERACOES</td>
<td><p>Regista Nessa tabela cada <strong>salário / subsido</strong> do
colaborador ativo na tabela <strong>RH_T_DEF_REMUNERACOES</strong></p>
<p>Relação das chaves estrangeiras:</p>
<ul>
<li><p><strong>REM_1_ID</strong> = ID DE RH_T_DEF_REMUNERACOES</p></li>
<li><p><strong>PRSAL_ID</strong> = ID de RH_T_PROC_FUNCIONARIOS</p></li>
</ul></td>
</tr>
<tr>
<td>RH_T_PAGAMENTOS</td>
<td><p>Regista nessa tabela cada <strong>Desconto</strong> do
colaborador ativo na tabela RH_T_DEF_PAGAMENTOS.</p>
<p>Relação das chaves estrangeiras:</p>
<ul>
<li><p><strong>DEFP_ID</strong> = ID DE RH_T_DEF_PAGAMENTOS</p></li>
<li><p><strong>PRSAL_ID</strong> = ID de RH_T_PROC_FUNCIONARIOS</p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><strong>Eliminar Salario</strong></td>
</tr>
<tr>
<td>RH_T_PROC_SALARIOS</td>
<td>Elimina dados Nessa tabela</td>
</tr>
<tr>
<td>RH_T_PROC_FUNCIONARIOS</td>
<td>Elimina dados Nessa tabela</td>
</tr>
<tr>
<td>RH_T_REMUNERACOES</td>
<td>Elimina dados Nessa tabela</td>
</tr>
<tr>
<td>RH_T_PAGAMENTOS</td>
<td>Elimina dados Nessa tabela</td>
</tr>
<tr>
<td colspan="2"><strong>Validar Salário Provisório</strong></td>
</tr>
<tr>
<td>RH_T_PROC_SALARIOS</td>
<td><p>Muda o estado:</p>
<ul>
<li><p>RH_T_PROC_SALARIOS.ESTADO =”
<strong>VALIDADO_PROVISORIO</strong>”</p></li>
</ul></td>
</tr>
<tr>
<td><strong>Validar Salário Definitivo</strong></td>
<td></td>
</tr>
<tr>
<td>RH_T_PROC_SALARIOS</td>
<td><p>Muda o estado:</p>
<p>RH_T_PROC_SALARIOS.ESTADO =”
<strong>VALIDADO_DEFINITIVO</strong>”</p></td>
</tr>
<tr>
<td colspan="2"><strong>Cabimentar</strong></td>
</tr>
<tr>
<td>RH_T_PROC_SALARIOS</td>
<td><ul>
<li><p>Invoca um serviço financeiro para cabimentar</p></li>
<li><p>Muda o estado: RH_T_PROC_SALARIOS.ESTADO =”
<strong>CABIMENTADO</strong>”</p></li>
<li><p>UPDATE <strong>RH_T_PROC_SALARIOS</strong>. <strong>CAB_1_ID =
número de cabimento devolvido no serviço</strong></p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><strong>Eliminar Cabimento</strong></td>
</tr>
<tr>
<td>RH_T_PROC_SALARIOS</td>
<td><ul>
<li><p>Invoca um serviço financeiro para eliminar cabimento</p></li>
<li><p>Muda o estado: RH_T_PROC_SALARIOS.ESTADO =”
<strong>VALIDADO</strong>”</p></li>
</ul></td>
</tr>
<tr>
<td colspan="2"><strong>Autorizar</strong></td>
</tr>
<tr>
<td>RH_T_PROC_SALARIOS</td>
<td><ul>
<li><p>Invoca um serviço financeiro para autorizar</p></li>
<li><p>Muda o estado: RH_T_PROC_SALARIOS.ESTADO =”
<strong>AUTORIZADO</strong>”</p></li>
</ul></td>
</tr>
</tbody>
</table>

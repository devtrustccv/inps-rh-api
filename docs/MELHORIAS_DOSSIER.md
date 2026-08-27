# Parametrizacão

## Assiduidade

- Melhoria ordenar a lista por data (ultimo deve ser o primeiro)

<img src="media/image1.png" style="width:5.15493in;height:2.31989in" />

## Relação Laboral

### Tipo Vinculo

- Mudar nome de campo “Conta Tempo Serviço” para “Quadro do INPS?”

- Mudar o dominio do campo “Tem Remuneração” para o domino –
  **TIPO_SALARIO_VINCULO**

<img src="media/image2.png" style="width:6.54225in;height:2.53688in" />

### Unidade Organica

- Refazer a Lista

- Retirar o botao eliminar e responsavel (ficara dentro de editar)

<img src="media/image3.png" style="width:6.19444in;height:3.42801in" />

- Refazer o desenho do botao Novo / Editar

<img src="media/image4.png" style="width:6.26806in;height:3.38889in" />

## 

## Estabelecimento

- Lista (tabela RH_T_ESTABELECIMENTO)

- Filtro

  - Pais (select)

<img src="media/image5.png" style="width:5.47917in;height:3.03703in" />

- Botão Novo / Editar

> <img src="media/image6.png" style="width:5.40278in;height:3.23831in" />

# DOSSIER DO COLABORADOR 

## Registar Colaborador / novo contrato

- Caso o colaborador tenha Salario do PCCS (SIM_PCCS), logo deve mostrar
  os campos de carreira e escalão ao selecionar um vinculo tanto na
  funcionalidade Registo / editar colaborador , como no registo / editar
  contrato.

  **Nota**: más caso o vinculo não tem carreira, logo nao regista na
  carreira (o id de escalao fica registado na tabela
  RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID)

  <img src="media/image7.png" style="width:4.59444in;height:2.48264in" />

  <img src="media/image8.jpeg" style="width:5.53472in;height:2.24306in" />

- Caso o colaborador Salario for a do PCCS (SIM_FORA_PCCS), logo NÃO
  deve mostrar os campos de carreira e escalão ao selecionar um vinculo
  tanto na funcionalidade Registo / editar colaborador, como no registo
  / editar contrato. NOTA: pode registar Salario Manualmente

  <img src="media/image9.jpeg" style="width:5.36111in;height:2.22222in" />

- Caso o colaborador não tem Salario (NAO), a regra sigue igual ao
  anterior, nao mostra os campos carreira, escalao e salario

## Gestão Laboral

### Lista Gestao laboral

- Substituir campo **categoria** por **escalao**.

- Mudar de posicão o campo **Cargo,** apôs **escalao**

> <img src="media/image10.png" style="width:6.26806in;height:1.1in" />

- Acrescentar o botão “Alterar Escalão / Cargo” (**Nota**: esse botão
  fica visivel somente em caso de colaboradores qui tem Salario no PCCS
  mas não tem Carreira)

<img src="media/image11.png" style="width:6.26806in;height:1.06875in" />

<table>
<colgroup>
<col style="width: 21%" />
<col style="width: 34%" />
<col style="width: 43%" />
</colgroup>
<thead>
<tr>
<th style="text-align: center;">Campos</th>
<th style="text-align: center;">Descricao/ Fonte Dados</th>
<th style="text-align: center;">Gravação</th>
</tr>
</thead>
<tbody>
<tr>
<td>Tipo alteração</td>
<td><p>DOMINIO = TIPO_MOV_LABORAL REFERENCIA = GESTAO_LABORAL</p>
<p>Campo é multiselect</p></td>
<td>RH_T_TIPOS_RELACIONAMENTO.TIPO_SITUACAO</td>
</tr>
<tr>
<td>Cargo Anterior</td>
<td>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</td>
<td>-------------------------------</td>
</tr>
<tr>
<td>Novo</td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.CARGO_ID</td>
</tr>
<tr>
<td>Escalão Anterior</td>
<td>RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID</td>
<td>------------------------------------</td>
</tr>
<tr>
<td>Carreira</td>
<td>RH_T_PARAM_CARREIA.NOME</td>
<td>----------------------------</td>
</tr>
<tr>
<td>Novo Escalão</td>
<td></td>
<td>RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID</td>
</tr>
<tr>
<td colspan="3"><p>Caso alterar Escalao, logo deve ir Para Validação</p>
<ul>
<li><p>Apos Validacao deve fechar o Salario anterior e Abrir um novo
<strong>RH_T_DEF_REMUNERACOES</strong></p></li>
<li><p>Pegar associacoes de <strong>RH_T_TIPREL_REM_PAG</strong> e fazer
novo Associacão</p></li>
</ul></td>
</tr>
</tbody>
</table>

### Regime Emprego”

> <img src="media/image12.png" style="width:6.26806in;height:1.13125in" />
>
> <img src="media/image13.png" style="width:6.26806in;height:1.65278in" />

| Campos | Descricão | Gravação |
|----|----|----|
| Tipo Regime |  | RH_T_REGIME_TRAB.TIPO_REGIME |
| Data Inicio |  | RH_T_REGIME_TRAB.DATA_INICIO |
| Data Fim |  | RH_T_REGIME_TRAB.DATA_FIM |
| Estado | O Campo Estado Somente Deve Aparecer em Ediçao | RH_T_REGIME_TRAB.ESTADO |

## Remuneraçoes / Desconto

- A lista deve trazer por defeito somente dados referentes a ultimo
  Vinculo

- No filtro
  <img src="media/image14.png" style="width:1.43379in;height:0.55596in" />
  além assim Contrato / vinculo/ Situacão Laboral/Escalão

## Validacão 

- Mostrar formulario “Detalhe de alterações”

## Notificão / alerta 

- Terminar implementacão de **Conversao de contrato** e **Renovação de
  Contrato**

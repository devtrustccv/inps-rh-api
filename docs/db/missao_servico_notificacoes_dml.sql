-- Missão de Serviço — tipos de notificação (domínio TIPO_NOTIFICACAO) e templates (RH_T_PARAM_NOTIFICACAO).
--
-- Idempotente: só insere o que falta. O template MISSAO_PRESTADOR activo é actualizado (tinha texto de teste).
-- Os textos são PROVISÓRIOS — seguem os textos por defeito do código e devem ser revistos pelo negócio.
-- O cancelamento (MISSAO_CANCELAMENTO) não tem template: o texto leva o motivo e é montado no código.
--
-- Variáveis disponíveis em todos os templates: {nrMissao} {destino} {dataInicio} {dataFim} {nrDias} {nrColaboradores}
--   MISSAO_PRESTADOR, MISSAO_EMISSAO_REQUISICAO, MISSAO_LOGISTICA_COLABORADOR: + {tipoProcesso}
--   MISSAO_EMISSAO_REQUISICAO: + {nrRequisicao} {valorTotal} {colaboradores}
--   MISSAO_AJUDA_CUSTO: + {valorDiario} {nrDiasAjuda} {valorTotal} {referenciaPagamento} {dataPagamento}
--   MISSAO_ALTERACAO: + {alteracoes}
DECLARE
  PROCEDURE tipo(p_valor VARCHAR2, p_descricao VARCHAR2) IS
    n NUMBER;
  BEGIN
    SELECT COUNT(*) INTO n FROM RH_T_DOMAINS WHERE DOMINIO = 'TIPO_NOTIFICACAO' AND VALOR = p_valor;
    IF n = 0 THEN
      INSERT INTO RH_T_DOMAINS (ID, DOMINIO, VALOR, DESCRICAO, REFERENCIA, ESTADO, DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME)
      VALUES (SEQ_DOMAINS.NEXTVAL, 'TIPO_NOTIFICACAO', p_valor, p_descricao, 'TIPO_NOTIFICACAO', 'A', SYSDATE, 1, 'SYSTEM');
    END IF;
  END;

  PROCEDURE template(p_tipo VARCHAR2, p_assunto VARCHAR2, p_corpo VARCHAR2) IS
    n NUMBER;
  BEGIN
    SELECT COUNT(*) INTO n FROM RH_T_PARAM_NOTIFICACAO WHERE TIPO_NOTIFICACAO = p_tipo AND ESTADO = 'A';
    IF n = 0 THEN
      INSERT INTO RH_T_PARAM_NOTIFICACAO (ID, TIPO_NOTIFICACAO, ASSUNTO, CORPO, ESTADO, DATA_REGISTO, USER_REGISTO_ID, USER_REGISTO_NAME, UUID)
      VALUES (SEQ_PARAM_NOTIF.NEXTVAL, p_tipo, p_assunto, p_corpo, 'A', SYSDATE, 1, 'SYSTEM',
              LOWER(REGEXP_REPLACE(RAWTOHEX(SYS_GUID()), '(.{8})(.{4})(.{4})(.{4})(.{12})', '\1-\2-\3-\4-\5')));
    ELSE
      UPDATE RH_T_PARAM_NOTIFICACAO
         SET ASSUNTO = p_assunto, CORPO = p_corpo,
             DATA_ALTERACAO = SYSDATE, USER_ALTERACAO_ID = 1, USER_ALTERACAO_NAME = 'SYSTEM'
       WHERE TIPO_NOTIFICACAO = p_tipo AND ESTADO = 'A'
         AND ASSUNTO LIKE 'Polhover%';   -- só substitui o texto de teste; nunca um template já revisto
    END IF;
  END;
BEGIN
  tipo('MISSAO_PRESTADOR',             'Missão - Pedido de proposta ao prestador');
  tipo('MISSAO_EMISSAO_REQUISICAO',    'Missão - Requisição ao prestador');
  tipo('MISSAO_LOGISTICA_COLABORADOR', 'Missão - Logística da viagem ao colaborador');
  tipo('MISSAO_CANCELAMENTO',          'Missão - Cancelamento');
  tipo('MISSAO_CONFIRMACAO_PEDIDO',    'Missão - Confirmação do pedido ao colaborador');
  tipo('MISSAO_AJUDA_CUSTO',           'Missão - Informação sobre a ajuda de custo');
  tipo('MISSAO_ALTERACAO',             'Missão - Alteração da missão');

  template('MISSAO_PRESTADOR',
    'Pedido de Proposta - {tipoProcesso} - Missão Nº {nrMissao}',
    'Exmo(a) Sr(a),

Solicita-se o envio de proposta (fatura proforma) de {tipoProcesso} para a missão de serviço com os seguintes dados:
- Nº Missão: {nrMissao}
- Destino: {destino}
- Data de partida: {dataInicio}
- Data de regresso: {dataFim}
- Nº de colaboradores: {nrColaboradores}

Aguardamos a vossa proposta.

Com os melhores cumprimentos,
INPS - Recursos Humanos');

  template('MISSAO_EMISSAO_REQUISICAO',
    'Requisição {nrRequisicao} - Missão Nº {nrMissao}',
    'Exmo(a) Sr(a),

Requisita-se o serviço de {tipoProcesso} para a missão de serviço Nº {nrMissao}, conforme a proposta apresentada:
- Nota de encomenda: {nrRequisicao}
- Destino: {destino}
- Datas: {dataInicio} a {dataFim}
- Colaboradores: {colaboradores}
- Valor total: {valorTotal}

Com os melhores cumprimentos,
INPS - Recursos Humanos');

  template('MISSAO_LOGISTICA_COLABORADOR',
    'Detalhes da sua Missão Nº {nrMissao} - {tipoProcesso}',
    'Exmo(a) Colaborador(a),

Informamos que está registado o {tipoProcesso} da sua missão de serviço Nº {nrMissao}.
- Destino: {destino}
- Datas: {dataInicio} a {dataFim}

Os detalhes podem ser consultados no portal RH.

Com os melhores cumprimentos,
INPS - Recursos Humanos');

  template('MISSAO_CONFIRMACAO_PEDIDO',
    'Confirmação da Missão Nº {nrMissao}',
    'Exmo(a) Colaborador(a),

Confirmamos que o pedido da missão de serviço Nº {nrMissao} foi registado e autorizado.
- Destino: {destino}
- Datas: {dataInicio} a {dataFim} ({nrDias} dia(s))

Receberá os detalhes da logística assim que estiverem confirmados.

Com os melhores cumprimentos,
INPS - Recursos Humanos');

  template('MISSAO_AJUDA_CUSTO',
    'Ajuda de Custo - Missão Nº {nrMissao}',
    'Exmo(a) Colaborador(a),

Informamos que foi efetuado o pagamento da ajuda de custo da missão de serviço Nº {nrMissao}.
- Valor diário: {valorDiario}
- Nº de dias: {nrDiasAjuda}
- Valor total: {valorTotal}
- Referência do pagamento: {referenciaPagamento}
- Data do pagamento: {dataPagamento}

Com os melhores cumprimentos,
INPS - Recursos Humanos');

  template('MISSAO_ALTERACAO',
    'Alteração da Missão Nº {nrMissao}',
    'Exmo(a) Sr(a),

Informamos que a missão de serviço Nº {nrMissao} foi alterada:
{alteracoes}

Com os melhores cumprimentos,
INPS - Recursos Humanos');

  COMMIT;
END;
/

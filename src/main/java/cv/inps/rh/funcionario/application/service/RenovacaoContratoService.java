package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ProcessarRenovacaoLoteCommand;
import cv.inps.rh.funcionario.application.commands.RenovarContratoCommand;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import cv.inps.rh.funcionario.application.dto.RenovarContratoReqDTO;
import cv.inps.rh.funcionario.application.dto.RenovarLoteItemReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AlertaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RenovacaoContratoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RenovacaoContratoService.class);

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final AlertaEntityRepository alertaEntityRepository;

  /** Contexto resolvido e validado de uma renovação, pronto a aplicar. */
  private record ContextoRenovacao(FuncionarioEntity funcionario, ContratoEntity contratoAtual,
                                   TiposRelacionamentoEntity tiprelAtual) {}

  @Transactional
  public RenovacaoContratoDTO renovarContrato(RenovarContratoCommand command) {

    var dto = command.getRenovacaocontrato();
    var dadosRenovacao = dto != null ? dto.getDadosRenovacao() : null;

    var ctx = validarRenovacao(command.getIdFuncionario(), dadosRenovacao);
    aplicarRenovacao(ctx, dadosRenovacao);

    funcionarioEntityRepository.saveAndFlush(ctx.funcionario());

    var renovacaoContratoDTO = new RenovacaoContratoDTO();
    renovacaoContratoDTO.setDadosRenovacao(contratoMapper.toRenovacaoContratoReqDTO(ctx.contratoAtual()));
    return renovacaoContratoDTO;
  }

  /**
   * Processa a renovação de um grupo de colaboradores (maker). É ATÓMICO: valida TODOS primeiro e,
   * se algum falhar, faz rollback total e devolve todos os erros de uma vez. Só quando todos passam
   * é que aplica e marca cada alerta de origem como tratado (flg_tratamento='S').
   *
   * <p>Um lote de 1 item cobre o caso individual a partir da grelha de alertas.
   */
  @Transactional
  public SuccessResponseDTO processarRenovacaoLote(ProcessarRenovacaoLoteCommand command) {

    var itens = command.getRenovarLote() != null ? command.getRenovarLote().getItens() : null;
    if (itens == null || itens.isEmpty())
      throw IgrpResponseStatusException.badRequest("A lista de renovações está vazia.");

    // Fase 1 — validar TODOS sem escrever, acumulando erros por colaborador.
    var contextos = new ArrayList<ContextoRenovacao>();
    var itensValidos = new ArrayList<RenovarLoteItemReqDTO>();
    var erros = new ArrayList<String>();
    for (var item : itens) {
      try {
        var ctx = validarRenovacao(item.getFuncionarioId(), item.getDadosRenovacao());
        contextos.add(ctx);
        itensValidos.add(item);
      } catch (IgrpResponseStatusException e) {
        erros.add(descreverErro(item, e));
      }
    }
    if (!erros.isEmpty())
      throw IgrpResponseStatusException.badRequest(
          "Não foi possível processar a renovação em lote. Corrija os seguintes colaboradores:", erros);

    // Fase 2 — aplicar todos e marcar os alertas de origem como tratados.
    for (int i = 0; i < contextos.size(); i++) {
      var ctx = contextos.get(i);
      var item = itensValidos.get(i);
      aplicarRenovacao(ctx, item.getDadosRenovacao());
      funcionarioEntityRepository.saveAndFlush(ctx.funcionario());
      marcarAlertaTratado(item.getAlertaId());
    }

    LOGGER.info("Renovação em lote: {} colaborador(es) enviados para validação.", contextos.size());
    return new SuccessResponseDTO(true, null,
        "Renovação em lote processada: %d colaborador(es) enviados para validação.".formatted(contextos.size()),
        List.of());
  }

  /**
   * Guards da renovação (sem escrever): resolve o funcionário/contrato atual e valida datas,
   * parametrização do tipo de contrato (renovável + nº máximo de renovações) e ausência de validação
   * pendente. Lança IgrpResponseStatusException em qualquer falha.
   */
  private ContextoRenovacao validarRenovacao(String idFuncionario, RenovarContratoReqDTO dadosRenovacao) {

    var idFunc = IdentificadorUnico.from(idFuncionario);
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    if (tipoRelacionamentoAtual == null)
      throw IgrpResponseStatusException.notFound(
          "O funcionário '%s' não possui tipo de relacionamento atual".formatted(funcionario.getNome()));

    var contratoAtual = tipoRelacionamentoAtual.getContrVinculoId();
    if (contratoAtual == null)
      throw IgrpResponseStatusException.notFound(
          "O funcionário '%s' não possui contrato ativo".formatted(funcionario.getNome()));

    // Guard (datas da renovação): a data de início é obrigatória e pode ser no passado (renovações
    // registadas em atraso); a data de fim, se indicada, não pode ser anterior à de início.
    if (dadosRenovacao == null || dadosRenovacao.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("A data de início da renovação é obrigatória.");
    if (dadosRenovacao.getDataFim() != null && dadosRenovacao.getDataFim().isBefore(dadosRenovacao.getDataInicio()))
      throw IgrpResponseStatusException.badRequest("A data de fim não pode ser anterior à data de início.");

    // Guard (parametrização do tipo de contrato): tem de ser renovável e respeitar o nº máximo de
    // renovações. Nº de renovações já validadas = validações UPDATE/RENOVACAO_CONTRATO em estado A
    // deste contrato — o estado da validação regista a decisão e não é sobrescrito por renovações
    // seguintes (rejeitadas ficam em I e não contam), ao contrário do histórico/est_act_adm.
    var tipoContrato = contratoAtual.getTpContratoId();
    if (tipoContrato != null) {
      if (!Integer.valueOf(1).equals(tipoContrato.getFlgRenovavel()))
        throw IgrpResponseStatusException.badRequest(
            "O tipo de contrato '%s' não é renovável.".formatted(tipoContrato.getNome()));

      Integer maxRenovacao = tipoContrato.getMaxRenovacao();
      if (maxRenovacao != null && maxRenovacao > 0) {
        long renovacoesValidadas = validacaoEntityRepository
            .countByReferenciaIdAndReferenciaNameAndTipoAccaoAndEstado(
                contratoAtual.getId(), Referencia.RENOVACAO_CONTRATO.name(), TipoAcao.UPDATE.name(), Estado.A);
        if (renovacoesValidadas >= maxRenovacao)
          throw IgrpResponseStatusException.badRequest(
              "Foi atingido o número máximo de renovações (%d) para este contrato.".formatted(maxRenovacao));
      }
    }

    funcionarioRules.garantirEditavel(contratoAtual.getEstado());

    if (funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.RENOVACAO_CONTRATO))
      throw IgrpResponseStatusException.conflict(
          "O funcionário '%s' possui uma validação pendente de renovação de contrato".formatted(funcionario.getNome()));

    return new ContextoRenovacao(funcionario, contratoAtual, tipoRelacionamentoAtual);
  }

  /**
   * Aplica a renovação (escreve): regista o histórico pendente com as novas datas, fecha o
   * TipoRelacionamento atual e cria um novo (estado P) apontando ao mesmo contrato, e regista a
   * validação pendente. Não faz flush — o chamador decide quando gravar.
   */
  private void aplicarRenovacao(ContextoRenovacao ctx, RenovarContratoReqDTO dadosRenovacao) {

    var funcionario = ctx.funcionario();
    var contratoAtual = ctx.contratoAtual();
    var tipoRelacionamentoAtual = ctx.tiprelAtual();

    // Regista as novas datas propostas no historico (Estado.P) — sem criar novo ContratoEntity
    contratoHistoricoWriteService.registrarRenovacaoPendente(contratoAtual, dadosRenovacao);

    // Fecha o TipoRelacionamento atual e cria novo apontando para o mesmo contrato.
    // Spec da Renovação: o antigo fecha com DATA_FIM = "data inicio do novo registo" MENOS 1 dia (regra
    // do analista: o relacionamento fechado termina em inicio-1, contíguo com o novo sem sobreposição)
    // e o novo nasce com DATA_INICIO = "Data inicio" do formulário — NÃO sysdate (senão o tiprel fica
    // com a data de hoje em vez do início real do contrato, e o antigo pode ficar com período invertido).
    var dataInicioRenovacao = dadosRenovacao.getDataInicio();
    tipoRelacionamentoAtual.setEstActAdm(0);
    tipoRelacionamentoAtual.setDataFim(dataInicioRenovacao.minusDays(1));

    var novoTipoRelacionamento = dadosContratuaisMapper.clone(tipoRelacionamentoAtual);
    novoTipoRelacionamento.setEstActAdm(1);
    novoTipoRelacionamento.setDataInicio(dataInicioRenovacao);
    novoTipoRelacionamento.setEstado(Estado.P);
    novoTipoRelacionamento.setObs("RENOVACAO");
    novoTipoRelacionamento.setTipoSituacao("RENOVACAO");
    novoTipoRelacionamento.setReferente("CONTRATO");
    novoTipoRelacionamento.setContrVinculoId(contratoAtual);
    funcionario.getTiposrelacionamentos().add(novoTipoRelacionamento);

    var valid = dadosContratuaisMapper.toValidacaoInsert(
        TipoAcao.UPDATE.name(), Referencia.RENOVACAO_CONTRATO.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(novoTipoRelacionamento);
    valid.setReferenciaId(contratoAtual.getId());
    valid.setReferenciaUuid(contratoAtual.getUuid());
    funcionario.getValidacoes().add(valid);
  }

  /**
   * Marca o alerta de origem como tratado (flg_tratamento='S') para sair da grelha "por tratar".
   * NO-OP quando a renovação não veio de um alerta (alertaId nulo — ex.: renovação a partir do
   * Dossier). O estado do alerta só transita para 'I' na validação positiva (ver
   * ValidacaoRenovacaoContratoService); numa rejeição o flag volta a 'N' e o alerta reaparece.
   */
  private void marcarAlertaTratado(java.util.UUID alertaId) {
    if (alertaId == null) return;
    alertaEntityRepository.findByUuid(alertaId).ifPresent(a -> a.setFlgTratamento("S"));
  }

  /** Descrição legível de um erro de item para a lista agregada devolvida ao utilizador. */
  private String descreverErro(RenovarLoteItemReqDTO item, IgrpResponseStatusException e) {
    var motivo = e.getBody() != null && e.getBody().getTitle() != null ? e.getBody().getTitle() : e.getMessage();
    return "Colaborador %s: %s".formatted(item.getFuncionarioId(), motivo);
  }
}

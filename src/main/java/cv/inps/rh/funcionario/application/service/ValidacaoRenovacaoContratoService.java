package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRenovacaoContratoCommand;
import cv.inps.rh.funcionario.application.dto.RenovarContratoReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
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
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidacaoRenovacaoContratoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidacaoRenovacaoContratoService.class);

  /** Tipo de alerta gerado pelo JOB para renovação de contrato (ver AlertaWriteService). */
  private static final String TIPO_ALERTA_RENOVACAO = "RENOVACAO_CONTRATO";
  /** Estado terminal do alerta quando a situação é resolvida (doc TRANSVERSAL: P -> I). */
  private static final String ESTADO_ALERTA_INATIVO = "I";
  private static final String FLG_TRATAMENTO_NAO = "N";

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final AlertaEntityRepository alertaEntityRepository;

  @Transactional
  public SuccessResponseDTO validar(ValidarRenovacaoContratoCommand command) {

    var dto = command.getRenovacaocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    // Contrato actual — já não é o filho/draft, é o contrato real que será atualizado
    var contrato = tiposRelacionamento.getContrVinculoId();
    if (contrato == null)
      throw IgrpResponseStatusException.badRequest(
          "O funcionário '%s' não possui contrato ativo".formatted(funcionario.getNome()));

    funcionarioRules.garantirEditavel(contrato.getEstado());

    // CORRIGIR (checker devolve ao maker): a PROPOSTA de renovação pendente é devolvida. Pendente =
    // tiprel novo (P) + histórico da renovação (P) + validação (P); o contrato mantém-se A (vínculo em
    // vigor). Âncora = contrato.uuid (referencia_uuid da validação UPDATE/RENOVACAO_CONTRATO). O maker
    // corrige e reenvia por este mesmo endpoint com validacao=null (C -> P).
    if (EstadoValidacao.CORRIGIR.equals(dto.getValidacao())) {
      if (tiposRelacionamento.getEstado() != Estado.P
          || !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.RENOVACAO_CONTRATO)) {
        throw IgrpResponseStatusException.badRequest("Não há renovação pendente para devolver para correção.");
      }
      funcionarioRules.devolverParaCorrecao(contrato.getUuid(), Estado.P, Referencia.RENOVACAO_CONTRATO);
      tiposRelacionamento.setEstado(Estado.C);
      contratoHistoricoWriteService.marcarRenovacaoPendenteComoCorrecao(contrato);
      funcionarioEntityRepository.saveAndFlush(funcionario);
      LOGGER.info("[CORRIGIR] RENOVACAO_CONTRATO devolvida para correção (contrato={}).", contrato.getUuid());
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(),
          "Renovação de contrato devolvida para correção.", List.of());
    }

    // Maker reenvia a correção (C -> P): repõe o estado pós-registo (tiprel P + histórico P com as datas
    // corrigidas + validação P), SEM consolidar. 'validacao' tem de vir nula.
    boolean estaPorCorrigir = funcionarioRules.temValidacaoPorCorrigir(funcionario.getUuid(), TipoAcao.UPDATE,
        Referencia.RENOVACAO_CONTRATO);
    if (estaPorCorrigir && dto.getValidacao() != null) {
      throw IgrpResponseStatusException.badRequest(
          "Renovação em correção: não pode ser validada. Corrija e reenvie primeiro.");
    }
    if (estaPorCorrigir) {
      tiposRelacionamento.setEstado(Estado.P);
      var validacaoReaberta = funcionarioRules.reabrirParaValidacao(contrato.getUuid(), Referencia.RENOVACAO_CONTRATO);
      // Grava o histórico corrigido e congela o detalhe da renovação contra a validação reaberta.
      contratoHistoricoWriteService.reabrirRenovacaoCorrecao(contrato, dto.getDadosRenovacao(), validacaoReaberta);
      funcionarioEntityRepository.saveAndFlush(funcionario);
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(),
          "Renovação de contrato corrigida e reenviada para validação.", List.of());
    }

    if (dto.getValidacao() != null) {
      // Só se decide (SIM/NAO) sobre uma renovação PENDENTE: tiprel atual em P + validação P. Sem isto,
      // um NAO sem proposta inativava o tiprel em vigor e um SIM reescrevia as datas do contrato.
      if (tiposRelacionamento.getEstado() != Estado.P
          || !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.RENOVACAO_CONTRATO)) {
        throw IgrpResponseStatusException.badRequest("Não há renovação pendente para validar.");
      }
      var aprovado = dto.getValidacao().equals(EstadoValidacao.SIM);
      // As novas datas da renovacao so devem ser gravadas no contrato quando a
      // renovacao e APROVADA. Numa rejeicao o contrato mantem as datas actuais.
      if (aprovado) {
        contratoMapper.toUpdateEntity(contrato, dto.getDadosRenovacao());
        // Estende as datas das DIMENSÕES (tiprel + carreira/mob/regime/situação). Os DEF são
        // estendidos DEPOIS do transferir, para o filtro "não-terminado" avaliar a DATA_FIM ORIGINAL
        // dos def (senão a extensão reviveria os expirados e o filtro não os excluiria).
        estenderDatasDimensoes(tiposRelacionamento, contrato.getDataInicio(), contrato.getDataFim());
        // O tiprel anterior foi fechado no REGISTO em (início proposto − 1). Se o maker corrigiu o início
        // (CORRIGIR → reenvio) ou o checker o mudou, recalcula-se com o início APROVADO: sem isto ficava
        // um buraco (ou sobreposição) entre o tiprel anterior e o renovado.
        var anterior = tiposRelacionamento.getTiprelId();
        if (anterior != null && contrato.getDataInicio() != null)
          anterior.setDataFim(contrato.getDataInicio().minusDays(1));
      }
      mudarEstado(funcionario, aprovado ? Estado.A : Estado.I, aprovado ? dto.getDadosRenovacao() : null);
      if (!aprovado) reverterRegistoRenovacao(tiposRelacionamento, contrato);
      // Alerta de origem (se existir): SIM fecha-o (estado='I', situação resolvida — doc TRANSVERSAL);
      // NÃO repõe flg_tratamento='N' para voltar à grelha "por tratar".
      marcarAlerta(contrato, aprovado);
    }

    funcionarioEntityRepository.saveAndFlush(funcionario);

    // Use case (RH_T_TIPREL_REM_PAG): "pega os registos do TIPREL_ID ANTERIOR e faz novo registo com
    // novo tiprel_id" — copia do tiprel anterior os def A ainda EM VIGOR (o transferir filtra os
    // terminados). NÃO usar associarNovos. Rejeição (NAO) não associa nada ao tiprel rejeitado.
    if (!EstadoValidacao.NAO.equals(dto.getValidacao())) {
      var antigo = tiposRelacionamento.getTiprelId();
      if (antigo != null) {
        var referencia = referenciaNaoTerminado(dto.getDadosRenovacao());
        tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(
            antigo, tiposRelacionamento, java.util.List.of(), java.util.List.of(),
            java.util.Set.of(), java.util.Set.of(), referencia);
        // Use case (DEF_REMUNERACOES/PAGAMENTOS "atualizar data fim"): estende a DATA_FIM dos def que
        // TRANSITARAM (os não-terminados). Depois do transferir → não revive os expirados.
        if (EstadoValidacao.SIM.equals(dto.getValidacao()))
          estenderDatasDefNaoTerminados(antigo, contrato.getDataFim(), referencia);
      }
    }

    var mensagem = EstadoValidacao.SIM.equals(dto.getValidacao()) ? "Renovação de contrato validada."
        : EstadoValidacao.NAO.equals(dto.getValidacao()) ? "Renovação de contrato rejeitada."
        : "Renovação de contrato actualizada.";
    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), mensagem, List.of());
  }

  /**
   * Revert do registo da renovação numa validação NEGATIVA (mesmo padrão de
   * ValidarContratoService.reverterRegistoNovoContrato): o tiprel proposto (já I via mudarEstado)
   * deixa de ser o atual e o tiprel anterior — fechado no registo com est_act_adm=0 e
   * DATA_FIM = início da renovação − 1 — volta a ser o atual. DATA_FIM reposta = a do contrato, que
   * a renovação não altera enquanto pendente. Os DEF nunca saíram do tiprel anterior e o histórico
   * em vigor nunca foi tocado; a proposta fica no histórico em I (registo da rejeição).
   */
  private void reverterRegistoRenovacao(TiposRelacionamentoEntity proposto, ContratoEntity contrato) {
    proposto.setEstActAdm(0);
    var anterior = proposto.getTiprelId();
    if (anterior == null) return;
    anterior.setEstActAdm(1);
    anterior.setDataFim(contrato.getDataFim());
  }

  /**
   * Renovação: ajusta as datas das DIMENSÕES na aprovação.
   *
   * <p>TIPREL — recebe DATA_INICIO + DATA_FIM. O tiprel novo é criado no registo com
   * DATA_INICIO = sysdate; aqui corrigimo-lo para a DATA_INICIO real do contrato (a "Data inicio" do
   * formulário, conforme a spec da Renovação: novo tiprel.DATA_INICIO = data início do formulário).
   *
   * <p>Carreira/mobilidade/regime/situação — só se estende a DATA_FIM. Estas dimensões já são
   * gravadas com o DATA_INICIO correto no registo; NÃO se sobrescreve o DATA_INICIO delas para não
   * estragar casos legítimos com início próprio (ex.: progressão de carreira a meio do contrato).
   */
  private void estenderDatasDimensoes(TiposRelacionamentoEntity tr, LocalDate dataInicio, LocalDate dataFim) {
    tr.setDataInicio(dataInicio);
    tr.setDataFim(dataFim);
    if (tr.getCarreiraId() != null) tr.getCarreiraId().setDataFim(dataFim);
    if (tr.getMobId() != null) tr.getMobId().setDataFim(dataFim);
    if (tr.getRegimeId() != null) tr.getRegimeId().setDataFim(dataFim);
    if (tr.getSituacLaboralId() != null) tr.getSituacLaboralId().setDataFim(dataFim);
  }

  /**
   * Renovação (use case, DEF_REMUNERACOES/PAGAMENTOS "atualizar data fim"): estende a DATA_FIM dos
   * def do tiprel anterior que TRANSITARAM — os que estão A e ainda EM VIGOR (não terminados, mesmo
   * predicado do transferir). Os expirados NÃO transitaram e não se lhes toca (ficam expirados no
   * tiprel anterior). Chamado DEPOIS do transferir (que não altera DATA_FIM), sobre a data original.
   */
  private void estenderDatasDefNaoTerminados(TiposRelacionamentoEntity antigo, LocalDate dataFim,
      LocalDate referencia) {
    funcionarioRules.getRemuneracoesAssociadosAtivos(antigo.getId()).stream()
        .filter(r -> r.getDataFim() == null || !r.getDataFim().isBefore(referencia))
        .forEach(r -> r.setDataFim(dataFim));
    funcionarioRules.getPagamentosDescontosAssociadosAtivos(antigo.getId()).stream()
        .filter(p -> p.getDataFim() == null || !p.getDataFim().isBefore(referencia))
        .forEach(p -> p.setDataFim(dataFim));
  }

  /**
   * Data de referência do critério "não terminado" dos def: o fim do contrato anterior (dia antes do
   * início da renovação) quando é anterior a hoje — renovação retroativa, em que os def do contrato
   * anterior já têm DATA_FIM no passado mas estavam em vigor até à renovação. Caso contrário, hoje
   * (comportamento das renovações correntes).
   */
  private LocalDate referenciaNaoTerminado(RenovarContratoReqDTO dados) {
    var hoje = LocalDate.now();
    if (dados == null || dados.getDataInicio() == null) return hoje;
    var fimAnterior = dados.getDataInicio().minusDays(1);
    return fimAnterior.isBefore(hoje) ? fimAnterior : hoje;
  }

  /**
   * Fecha ou reabre o alerta de renovação de origem conforme a decisão do checker. Localiza o alerta
   * pelo referencia_id (= id do contrato, que se mantém na renovação) e tipo RENOVACAO_CONTRATO.
   * NO-OP quando a renovação não veio de um alerta (nenhum alerta corresponde ao contrato).
   *
   * <ul>
   *   <li>SIM (aprovado): estado='I' — situação resolvida, sai definitivamente da grelha.</li>
   *   <li>NÃO (rejeitado): flg_tratamento='N' — o alerta volta à grelha "por tratar".</li>
   * </ul>
   */
  private void marcarAlerta(ContratoEntity contrato, boolean aprovado) {
    if (contrato == null) return;
    alertaEntityRepository
        .findFirstByReferenciaIdAndTipoAlertaOrderByIdDesc(contrato.getId(), TIPO_ALERTA_RENOVACAO)
        .ifPresent(alerta -> {
          if (aprovado) alerta.setEstado(ESTADO_ALERTA_INATIVO);
          else alerta.setFlgTratamento(FLG_TRATAMENTO_NAO);
        });
  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado, RenovarContratoReqDTO dadosAprovados) {

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity.getUuid());
    if (tr != null) {
      tr.setEstado(estado);

      ContratoEntity contrato = tr.getContrVinculoId();
      if (contrato != null) {
        // Renovação: o contrato mantém-se A (vínculo em vigor); só o histórico da proposta (P)
        // transita. transicionarEstado localizaria o histórico pelo estado do contrato (A) e nunca
        // tocaria na proposta — usar o caminho dedicado à renovação.
        contratoHistoricoWriteService.transicionarRenovacao(contrato, estado, dadosAprovados);
      }
    }

    // Renovação regista validação com TIPO_ACCAO='UPDATE' (conforme especificação)
    funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.UPDATE, Referencia.RENOVACAO_CONTRATO)
        .ifPresent(v -> v.setEstado(estado));
  }
}

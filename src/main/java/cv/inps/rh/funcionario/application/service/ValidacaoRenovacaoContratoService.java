package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRenovacaoContratoCommand;
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

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;

  @Transactional
  public SuccessResponseDTO validar(ValidarRenovacaoContratoCommand command) {

    var dto = command.getRenovacaocontrato();

    // Terceiro caminho da validação (SIM / NAO / CORRIGIR). O fluxo de correção ainda não está
    // implementado: por agora CORRIGIR é um NO-OP — regista no log e devolve 200 com mensagem, SEM
    // validar, actualizar ou mudar qualquer estado. Guard no topo para não tocar em nada.
    if (EstadoValidacao.CORRIGIR.equals(dto.getValidacao())) {
      LOGGER.info("[CORRIGIR] RENOVACAO_CONTRATO (funcionario={}): opção 'Corrigir' ainda não implementada; nenhuma alteração aplicada.",
          command.getIdFuncionario());
      return new SuccessResponseDTO(false, null, ValidationUtil.MSG_CORRIGIR_NAO_IMPLEMENTADO, List.of());
    }

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    // Contrato actual — já não é o filho/draft, é o contrato real que será atualizado
    var contrato = tiposRelacionamento.getContrVinculoId();
    if (contrato == null)
      throw IgrpResponseStatusException.badRequest(
          "Funcionario com id '%s' não possui contrato ativo".formatted(idFunc));

    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(contrato.getEstado());

    if (dto.getValidacao() != null) {
      var aprovado = dto.getValidacao().equals(EstadoValidacao.SIM);
      // As novas datas da renovacao so devem ser gravadas no contrato quando a
      // renovacao e APROVADA. Numa rejeicao o contrato mantem as datas actuais.
      if (aprovado) {
        contratoMapper.toUpdateEntity(contrato, dto.getDadosRenovacao());
        // Estende as datas das DIMENSÕES (tiprel + carreira/mob/regime/situação). Os DEF são
        // estendidos DEPOIS do transferir, para o filtro "não-terminado" avaliar a DATA_FIM ORIGINAL
        // dos def (senão a extensão reviveria os expirados e o filtro não os excluiria).
        estenderDatasDimensoes(tiposRelacionamento, contrato.getDataInicio(), contrato.getDataFim());
      }
      mudarEstado(funcionario, aprovado ? Estado.A : Estado.I);
    }

    funcionarioEntityRepository.saveAndFlush(funcionario);

    // Use case (RH_T_TIPREL_REM_PAG): "pega os registos do TIPREL_ID ANTERIOR e faz novo registo com
    // novo tiprel_id" — copia do tiprel anterior os def A ainda EM VIGOR (o transferir filtra os
    // terminados). NÃO usar associarNovos. Rejeição (NAO) não associa nada ao tiprel rejeitado.
    if (!EstadoValidacao.NAO.equals(dto.getValidacao())) {
      var antigo = tiposRelacionamento.getTiprelId();
      if (antigo != null) {
        tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(
            antigo, tiposRelacionamento, java.util.List.of(), java.util.List.of());
        // Use case (DEF_REMUNERACOES/PAGAMENTOS "atualizar data fim"): estende a DATA_FIM dos def que
        // TRANSITARAM (os não-terminados). Depois do transferir → não revive os expirados.
        if (EstadoValidacao.SIM.equals(dto.getValidacao()))
          estenderDatasDefNaoTerminados(antigo, contrato.getDataFim());
      }
    }

    var mensagem = EstadoValidacao.SIM.equals(dto.getValidacao())
        ? "Renovação de contrato validada."
        : "Renovação de contrato actualizada.";
    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), mensagem, List.of());
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
  private void estenderDatasDefNaoTerminados(TiposRelacionamentoEntity antigo, LocalDate dataFim) {
    var hoje = LocalDate.now();
    funcionarioRules.getRemuneracoesAssociadosAtivos(antigo.getId()).stream()
        .filter(r -> r.getDataFim() == null || !r.getDataFim().isBefore(hoje))
        .forEach(r -> r.setDataFim(dataFim));
    funcionarioRules.getPagamentosDescontosAssociadosAtivos(antigo.getId()).stream()
        .filter(p -> p.getDataFim() == null || !p.getDataFim().isBefore(hoje))
        .forEach(p -> p.setDataFim(dataFim));
  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado) {

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity.getUuid());
    if (tr != null) {
      tr.setEstado(estado);

      ContratoEntity contrato = tr.getContrVinculoId();
      if (contrato != null) {
        contratoHistoricoWriteService.transicionarEstado(contrato, estado);
      }
    }

    // Renovação regista validação com TIPO_ACCAO='UPDATE' (conforme especificação)
    funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.UPDATE, Referencia.RENOVACAO_CONTRATO)
        .ifPresent(v -> v.setEstado(estado));
  }
}

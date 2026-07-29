package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRenovacaoContratoCommand;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ValidacaoRenovacaoContratoService {

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;

  @Transactional
  public RenovacaoContratoDTO validar(ValidarRenovacaoContratoCommand command) {

    var dto = command.getRenovacaocontrato();
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
        // Estende a DATA_FIM das DIMENSÕES (tiprel + carreira/mob/regime/situação). Os DEF são
        // estendidos DEPOIS do transferir, para o filtro "não-terminado" avaliar a DATA_FIM ORIGINAL
        // dos def (senão a extensão reviveria os expirados e o filtro não os excluiria).
        estenderDatasDimensoes(tiposRelacionamento, contrato.getDataFim());
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

    var renovacaoContratoDTO = new RenovacaoContratoDTO();
    renovacaoContratoDTO.setDadosRenovacao(contratoMapper.toRenovacaoContratoReqDTO(contrato));
    return renovacaoContratoDTO;
  }

  /** Renovação: estende a DATA_FIM das DIMENSÕES (tiprel + carreira/mobilidade/regime/situação). */
  private void estenderDatasDimensoes(TiposRelacionamentoEntity tr, LocalDate dataFim) {
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

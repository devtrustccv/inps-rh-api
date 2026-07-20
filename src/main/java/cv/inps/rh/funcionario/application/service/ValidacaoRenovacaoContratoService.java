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
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoMovimentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidacaoRenovacaoContratoService {

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;

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
        // Renovação estende as datas das tabelas associadas; nos MOVIMENTOS só os fixos (doc ponto 3).
        atualizarDatasRenovacao(tiposRelacionamento, funcionario, contrato);
      }
      mudarEstado(funcionario, aprovado ? Estado.A : Estado.I);
    }

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    // Numa REJEICAO (validacao=NAO) nao se associam defs ao tiprel rejeitado — RH_T_TIPREL_REM_PAG
    // nao tem estado, logo a unica forma de nao os ter e nao criar a associacao.
    if (!EstadoValidacao.NAO.equals(dto.getValidacao())) {
      tipoRelRemPagHelper.associarNovos(tiposRelacionamento, saved);
    }

    var renovacaoContratoDTO = new RenovacaoContratoDTO();
    renovacaoContratoDTO.setDadosRenovacao(contratoMapper.toRenovacaoContratoReqDTO(contrato));
    return renovacaoContratoDTO;
  }

  /**
   * Renovação: estende as datas das tabelas associadas ao tiprel. Doc (ponto 3): nos MOVIMENTOS,
   * só os FIXOS do vínculo (salário/vencimento + INPS + IUR + valor líquido) atualizam datas
   * (Data Início + Data Fim); os manuais (subsídios/encargos) NÃO são tocados.
   */
  private void atualizarDatasRenovacao(TiposRelacionamentoEntity tr, FuncionarioEntity funcionario, ContratoEntity contrato) {
    var dataInicio = contrato.getDataInicio();
    var dataFim = contrato.getDataFim();

    // Tabelas associadas (não-movimentos): o próprio tiprel (o GET lê tiprel.getDataFim()) + carreira/
    // mobilidade/regime/situação estendem a DATA_FIM para o novo período.
    tr.setDataFim(dataFim);
    if (tr.getCarreiraId() != null) tr.getCarreiraId().setDataFim(dataFim);
    if (tr.getMobId() != null) tr.getMobId().setDataFim(dataFim);
    if (tr.getRegimeId() != null) tr.getRegimeId().setDataFim(dataFim);
    if (tr.getSituacLaboralId() != null) tr.getSituacLaboralId().setDataFim(dataFim);

    // Movimentos: SÓ os fixos do vínculo atualizam datas (Início + Fim). Manuais não. (doc ponto 3)
    var vinculoId = contrato.getVinculoId() != null ? contrato.getVinculoId().getId() : null;
    if (vinculoId == null) return;
    Set<Long> tmsRem = paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipo(vinculoId, "REM").stream()
        .filter(m -> m.getTmId() != null).map(m -> m.getTmId().getId()).collect(Collectors.toSet());
    Set<Long> tmsPag = paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipo(vinculoId, "PAG").stream()
        .filter(m -> m.getTmId() != null).map(m -> m.getTmId().getId()).collect(Collectors.toSet());

    if (funcionario.getDefinicoesRenumeracoes() != null)
      funcionario.getDefinicoesRenumeracoes().stream()
          .filter(r -> r != null && r.getEstado() == Estado.A && r.getTmId() != null && tmsRem.contains(r.getTmId().getId()))
          .forEach(r -> { r.setDataInicio(dataInicio); r.setDataFim(dataFim); });
    if (funcionario.getDefinicoesPagamentos() != null)
      funcionario.getDefinicoesPagamentos().stream()
          .filter(p -> p != null && p.getEstado() == Estado.A && p.getTmId() != null && tmsPag.contains(p.getTmId().getId()))
          .forEach(p -> { p.setDataInicio(dataInicio); p.setDataFim(dataFim); });
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

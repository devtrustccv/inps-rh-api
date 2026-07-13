package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.RenovarContratoCommand;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RenovacaoContratoService {

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final ValidacaoEntityRepository validacaoEntityRepository;

  @Transactional
  public RenovacaoContratoDTO renovarContrato(RenovarContratoCommand command) {

    var dto = command.getRenovacaocontrato();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    if (tipoRelacionamentoAtual == null)
      throw IgrpResponseStatusException.notFound(
          "Funcionario com id '%s' não possui tipo de relacionamento atual".formatted(idFunc));

    var contratoAtual = tipoRelacionamentoAtual.getContrVinculoId();
    if (contratoAtual == null)
      throw IgrpResponseStatusException.notFound(
          "Funcionario com id '%s' não possui contrato ativo".formatted(idFunc));

    // Guard (datas da renovação): a data de início é obrigatória e não pode ser no passado; a data
    // de fim, se indicada, não pode ser anterior à de início.
    var dadosRenovacao = dto.getDadosRenovacao();
    if (dadosRenovacao == null || dadosRenovacao.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("A data de início da renovação é obrigatória.");
    if (dadosRenovacao.getDataInicio().isBefore(LocalDate.now()))
      throw IgrpResponseStatusException.badRequest("A data de início da renovação não pode ser uma data no passado.");
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

    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(contratoAtual.getEstado());

    if (funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.RENOVACAO_CONTRATO))
      throw IgrpResponseStatusException.conflict(
          "Funcionario com id '%s' possui uma validação pendente de renovação de contrato".formatted(idFunc));

    // Regista as novas datas propostas no historico (Estado.P) — sem criar novo ContratoEntity
    contratoHistoricoWriteService.registrarRenovacaoPendente(contratoAtual, dto.getDadosRenovacao());

    // Fecha o TipoRelacionamento atual e cria novo apontando para o mesmo contrato
    tipoRelacionamentoAtual.setEstActAdm(0);
    tipoRelacionamentoAtual.setDataFim(LocalDate.now());

    var novoTipoRelacionamento = dadosContratuaisMapper.clone(tipoRelacionamentoAtual);
    novoTipoRelacionamento.setEstActAdm(1);
    novoTipoRelacionamento.setDataInicio(LocalDate.now());
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

    funcionarioEntityRepository.saveAndFlush(funcionario);

    var renovacaoContratoDTO = new RenovacaoContratoDTO();
    renovacaoContratoDTO.setDadosRenovacao(contratoMapper.toRenovacaoContratoReqDTO(contratoAtual));
    return renovacaoContratoDTO;
  }
}

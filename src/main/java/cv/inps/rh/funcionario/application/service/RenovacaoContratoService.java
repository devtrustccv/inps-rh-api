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

    if (funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.RENOVACAO_CONTRATO))
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
    novoTipoRelacionamento.setObs("RENOVACAO_CONTRATO");
    novoTipoRelacionamento.setTipoSituacao("RENOVACAO");
    novoTipoRelacionamento.setReferente("CONTRATO");
    novoTipoRelacionamento.setContrVinculoId(contratoAtual);
    funcionario.getTiposrelacionamentos().add(novoTipoRelacionamento);

    var valid = dadosContratuaisMapper.toValidacaoInsert(
        TipoAcao.INSERT.name(), Referencia.RENOVACAO_CONTRATO.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(novoTipoRelacionamento);
    valid.setReferenciaId(contratoAtual.getId());
    funcionario.getValidacoes().add(valid);

    funcionarioEntityRepository.saveAndFlush(funcionario);

    var renovacaoContratoDTO = new RenovacaoContratoDTO();
    renovacaoContratoDTO.setDadosRenovacao(contratoMapper.toRenovacaoContratoReqDTO(contratoAtual));
    return renovacaoContratoDTO;
  }
}

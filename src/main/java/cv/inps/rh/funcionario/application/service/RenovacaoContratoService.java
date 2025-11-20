package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.RenovarContratoCommand;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RenovacaoContratoService {

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper  dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;

  public RenovacaoContratoDTO renovarContrato(RenovarContratoCommand command) {

    var dto = command.getRenovacaocontrato();

    var idFunc = IdentificadorUnico.from(command.getId());

    var funcionario = funcionarioEntityRepository.findByUuid(idFunc.getValor()).orElseThrow(
        () -> IgrpResponseStatusException.notFound("Funcionario com id '%s' não encontrado".formatted(idFunc))
    );

    var contratoPai = funcionarioRules.getPrimeiroContrato(funcionario);
    if (contratoPai == null)
      throw IgrpResponseStatusException.notFound("Funcionario com id '%s' não possui contrato pai".formatted(idFunc));

    var contratoAtual = funcionarioRules.getContratoComMaiorVersao(funcionario);
    if (contratoAtual == null)
      throw IgrpResponseStatusException.notFound("Funcionario com id '%s' não possui contrato ativo".formatted(idFunc));

    var novoContrato = contratoMapper.toRenovarContrato(dto.getDadosRenovacao(), Estado.P);
    novoContrato.setContratoId(contratoPai);
    novoContrato.setVersao(contratoAtual.getVersao() + 1);
    funcionario.getContratos().add(novoContrato);

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);

    if (tipoRelacionamentoAtual == null)
      throw IgrpResponseStatusException.notFound("Funcionario com id '%s' não possui tipo de relacionamento atual".formatted(idFunc));
    tipoRelacionamentoAtual.setEstActAdm(0);
    tipoRelacionamentoAtual.setDataFim(LocalDate.now());

    var novoTipoRelacionamento = dadosContratuaisMapper.clone(tipoRelacionamentoAtual);
    novoTipoRelacionamento.setEstActAdm(1);
    novoTipoRelacionamento.setDataInicio(LocalDate.now());
    novoTipoRelacionamento.setEstado(Estado.P);
    novoTipoRelacionamento.setObs("RENOVACAO_CONTRATO");
    novoTipoRelacionamento.setTipoSituacao("RENOVACAO");
    novoTipoRelacionamento.setReferente("CONTRATO");
    novoTipoRelacionamento.setContratoId(novoContrato);
    funcionario.getTiposrelacionamentos().add(novoTipoRelacionamento);

    var valid = dadosContratuaisMapper.toValidacaoInsert("RENOVACAO_CONTRATO’  ", 1L, Estado.P); //todo resolve id later
    valid.setFunId(funcionario);
    valid.setTiprelId(novoTipoRelacionamento);
    funcionario.getValidacoes().add(valid);

    funcionarioEntityRepository.save(funcionario);

    var renovacaoContratoReqDTO = contratoMapper.toRenovacaoContratoReqDTO(novoContrato);

    var renovacaoContratoDTO = new RenovacaoContratoDTO();
    renovacaoContratoDTO.setDadosRenovacao(renovacaoContratoReqDTO);

    return renovacaoContratoDTO;

  }
}

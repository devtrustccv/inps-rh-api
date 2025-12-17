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
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RenovacaoContratoService {

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosContratuaisMapper  dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final ValidacaoEntityRepository validacaoEntityRepository;

  @Transactional
  public RenovacaoContratoDTO renovarContrato(RenovarContratoCommand command) {

    var dto = command.getRenovacaocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var contratoPai = funcionarioRules.getPrimeiroContrato(funcionario.getUuid());
    if (contratoPai == null)
      throw IgrpResponseStatusException.notFound("Funcionario com id '%s' não possui contrato pai".formatted(idFunc));

    var contratoAtual = funcionarioRules.getContratoComMaiorVersao(funcionario.getUuid());
    if (contratoAtual == null)
      throw IgrpResponseStatusException.notFound("Funcionario com id '%s' não possui contrato ativo".formatted(idFunc));

    var validacaoPendente = funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.RENOVACAO_CONTRATO);
    if (validacaoPendente)
      throw IgrpResponseStatusException.conflict("Funcionario com id '%s' possui uma validação pendente de renovação de contrato".formatted(idFunc));

    var novoContrato = contratoMapper.toRenovarContrato(dto.getDadosRenovacao(), Estado.P);
    novoContrato.setContratoId(contratoPai);
    novoContrato.setVersao(contratoAtual.getVersao() + 1);
    novoContrato.setFunId(funcionario);
    funcionario.getContratos().add(novoContrato);

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

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
    novoTipoRelacionamento.setContrVinculoId(novoContrato);
    funcionario.getTiposrelacionamentos().add(novoTipoRelacionamento);


    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(),Referencia.RENOVACAO_CONTRATO.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(novoTipoRelacionamento);
    funcionario.getValidacoes().add(valid);

    funcionarioEntityRepository.saveAndFlush(funcionario);

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(novoContrato.getId());
          validacaoEntityRepository.save(e);
        });

    var renovacaoContratoReqDTO = contratoMapper.toRenovacaoContratoReqDTO(novoContrato);

    var renovacaoContratoDTO = new RenovacaoContratoDTO();
    renovacaoContratoDTO.setDadosRenovacao(renovacaoContratoReqDTO);

    return renovacaoContratoDTO;

  }
}

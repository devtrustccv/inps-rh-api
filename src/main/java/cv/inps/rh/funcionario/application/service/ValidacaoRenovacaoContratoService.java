package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRenovacaoContratoCommand;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValidacaoRenovacaoContratoService {

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;

  @Transactional
  public RenovacaoContratoDTO validar(ValidarRenovacaoContratoCommand command) {

    var dto = command.getRenovacaocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var contrato = tiposRelacionamento.getContrVinculoId().getContratoId();

    if (contrato == null) {
      throw IgrpResponseStatusException.badRequest("Funcionario com id '%s' não possui contrato ativo".formatted(idFunc));
    }

    contratoMapper.toUpdateEntity(contrato, dto.getDadosRenovacao());

    if (dto.getValidacao() != null) {
      var estado = dto.getValidacao().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      mudarEstado(funcionario, estado);
    }

     funcionarioEntityRepository.save(funcionario);

    var renovacaoContratoReqDTO = contratoMapper.toRenovacaoContratoReqDTO(contrato);

    var renovacaoContratoDTO = new RenovacaoContratoDTO();
    renovacaoContratoDTO.setDadosRenovacao(renovacaoContratoReqDTO);

    return renovacaoContratoDTO;

  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado) {

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity.getUuid());
    if (tr != null) {
      tr.setEstado(estado);

      var contrato = tr.getContrVinculoId().getContratoId();
      if (contrato != null) contrato.setEstado(estado);

    }

    var validacaoPendente =
        funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.UPDATE, Referencia.RENOVACAO_CONTRATO);
    if (validacaoPendente != null) {
      validacaoPendente.setEstado(estado);
    }
  }

}


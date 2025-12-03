package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRenovacaoContratoCommand;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
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
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;

  @Transactional
  public RenovacaoContratoDTO validar(ValidarRenovacaoContratoCommand command) {

    var dto = command.getRenovacaocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.getValor());

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario);

    var contrato = tiposRelacionamento.getContrVinculoId().getContratoId();

    if (contrato == null) {
      throw IgrpResponseStatusException.badRequest("Funcionario com id '%s' não possui contrato ativo".formatted(idFunc));
    }

    contratoMapper.toUpdateEntity(contrato, dto.getDadosRenovacao());

    if (dto.getValidacao() != null) {
      var estado = dto.getValidacao().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      mudarEstado(funcionario, estado);
    }

    var saved = funcionarioEntityRepository.save(funcionario);

    var renovacaoContratoReqDTO = contratoMapper.toRenovacaoContratoReqDTO(contrato);

    var renovacaoContratoDTO = new RenovacaoContratoDTO();
    renovacaoContratoDTO.setDadosRenovacao(renovacaoContratoReqDTO);

    return renovacaoContratoDTO;

  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado) {

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity);
    if (tr != null) {
      tr.setEstado(estado);

      var contrato = tr.getContrVinculoId().getContratoId();
      if (contrato != null) contrato.setEstado(estado);

      //todo perguntar analise se devo mudar o estado do mob, carreira e regime
      var mob = tr.getMobId();
      if (mob != null) mob.setEstado(estado);
      var carreira = tr.getCarreiraId();
      if (carreira != null) carreira.setEstado(estado);
      var regime = tr.getRegimeId();
      if (regime != null) regime.setEstado(estado);
    }


    funcionarioEntity.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P)
        .filter(v -> "RENOVACAO_CONTRATO".equals(v.getReferenciaName()) && "UPDATE".equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(estado));
  }

}


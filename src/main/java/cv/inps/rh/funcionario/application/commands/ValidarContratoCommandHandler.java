package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.dto.NovoContratoDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.domain.models.TiposRelacionamento;
import cv.inps.rh.funcionario.domain.repository.ContratoRepository;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ValidarContratoCommandHandler implements CommandHandler<ValidarContratoCommand, ResponseEntity<DadosContratuaisRespDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarContratoCommandHandler.class);

  private final ContratoRepository contratoRepository;
  private final FuncionarioRepository funcionarioRepository;
  private final ContratoMapper contratoMapper;

  public ValidarContratoCommandHandler(ContratoRepository contratoRepository,
                                       FuncionarioRepository funcionarioRepository,
                                       ContratoMapper contratoMapper) {
    this.contratoRepository = contratoRepository;
    this.funcionarioRepository = funcionarioRepository;
    this.contratoMapper = contratoMapper;
  }

  @IgrpCommandHandler
  public ResponseEntity<DadosContratuaisRespDTO> handle(ValidarContratoCommand command) {

    NovoContratoDTO dto = command.getNovocontrato();

    if (dto.getValidar() == null) {
      throw IgrpResponseStatusException.badRequest("campo validar obrigatorio");
    }

    var contratoId = IdentificadorUnico.from(command.getContratoId());

    var func = funcionarioRepository.findById(IdentificadorUnico.from(command.getId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id" + command.getId()));

    TiposRelacionamento tiposRelacionamento = func.getTipoRelacionamentoByContratoId(contratoId);

    Estado novoEstado = dto.getValidar() == EstadoValidacao.SIM ? Estado.A : Estado.I;
    tiposRelacionamento.mudarEstado(novoEstado);

    funcionarioRepository.save(func);


    return null;
  }

}

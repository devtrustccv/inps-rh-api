package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.carreira.CarreiraWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ValidarCarreiraCommandHandler implements CommandHandler<ValidarCarreiraCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarCarreiraCommandHandler.class);

  private final CarreiraWriteService carreiraWriteService;

  public ValidarCarreiraCommandHandler(CarreiraWriteService carreiraWriteService) {
    this.carreiraWriteService = carreiraWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(ValidarCarreiraCommand command) {

    LOGGER.info("Validar carreira para funcionario: {}", command);

    return ResponseEntity.ok(
        carreiraWriteService.validarCarreira(command.getFuncionarioId(), command.getValidacaocarreira()));
  }

}

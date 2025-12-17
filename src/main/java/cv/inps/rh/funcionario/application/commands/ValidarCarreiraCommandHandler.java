package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.carreira.CarreiraWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ValidarCarreiraCommandHandler implements CommandHandler<ValidarCarreiraCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarCarreiraCommandHandler.class);

  private final CarreiraWriteService carreiraWriteService;

  public ValidarCarreiraCommandHandler(CarreiraWriteService carreiraWriteService) {
    this.carreiraWriteService = carreiraWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ValidarCarreiraCommand command) {

    LOGGER.info("Validar carreira para funcionario: {}", command);

    carreiraWriteService.validarCarreira(command.getFuncionarioId(), command.getValidacaocarreira());

    return ResponseEntity.ok().build();
  }

}

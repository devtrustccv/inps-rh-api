package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class ValidarSubsidioFeriasCommandHandler implements CommandHandler<ValidarSubsidioFeriasCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarSubsidioFeriasCommandHandler.class);

  private final SubsidioService subsidioService;

  public ValidarSubsidioFeriasCommandHandler(SubsidioService subsidioService) {
    this.subsidioService = subsidioService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(ValidarSubsidioFeriasCommand command) {

    LOGGER.debug("ValidarSubsidioFeriasCommand : {}", command);

    subsidioService.validarSubsidioFerias(command.getSubsidioId());

    return ResponseEntity.ok().build();
  }

}

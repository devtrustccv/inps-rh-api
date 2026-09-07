package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class CalcularSubsidioFeriasCommandHandler implements CommandHandler<CalcularSubsidioFeriasCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CalcularSubsidioFeriasCommandHandler.class);

  private final SubsidioService subsidioService;

  public CalcularSubsidioFeriasCommandHandler(SubsidioService subsidioService) {
    this.subsidioService = subsidioService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(CalcularSubsidioFeriasCommand command) {

    LOGGER.debug("CalcularSubsidioFeriasCommand : {}", command);

    subsidioService.calcularSubsidioferias(command.getAno());

    return ResponseEntity.ok().build();
  }

}

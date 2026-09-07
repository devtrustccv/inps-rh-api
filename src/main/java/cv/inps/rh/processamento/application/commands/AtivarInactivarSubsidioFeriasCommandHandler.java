package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AtivarInactivarSubsidioFeriasCommandHandler implements CommandHandler<AtivarInativarSubsidioFeriasCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AtivarInactivarSubsidioFeriasCommandHandler.class);

  private final SubsidioService subsidioService;

  public AtivarInactivarSubsidioFeriasCommandHandler(SubsidioService subsidioService) {
    this.subsidioService = subsidioService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AtivarInativarSubsidioFeriasCommand command) {

    LOGGER.debug("AtivarInativarSubsidioFeriasCommand : {}", command);

    subsidioService.alterarEstadoSubsisioferias(command.getSubsidioId());

    return ResponseEntity.ok().build();
  }

}

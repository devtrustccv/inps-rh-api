package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.models.SubsidioNatalStatus;
import cv.inps.rh.processamento.domain.service.SubsidioNatalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AtivarInactivarSubsidioNatalCommandHandler implements CommandHandler<AtivarInactivarSubsidioNatalCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AtivarInactivarSubsidioNatalCommandHandler.class);

  private final SubsidioNatalService subsidioNatalService;

  public AtivarInactivarSubsidioNatalCommandHandler(SubsidioNatalService subsidioNatalService) {
    this.subsidioNatalService = subsidioNatalService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AtivarInactivarSubsidioNatalCommand command) {

    LOGGER.debug("AtivarInactivarSubsidioNatalCommand : {}", command);

    subsidioNatalService.activateInactivate(
        command.getSubsidioId(),
        command.getAno(),
        command.getFuncionarioId(),
        SubsidioNatalStatus.valueOf(command.getStatus()),
        command.getSubsidioresponsenatal()
    );
    return ResponseEntity.ok().build();
  }

}

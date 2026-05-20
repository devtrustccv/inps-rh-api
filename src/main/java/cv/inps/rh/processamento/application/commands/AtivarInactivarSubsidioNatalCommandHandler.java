package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.models.SubsidioNatalStatus;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class AtivarInactivarSubsidioNatalCommandHandler implements CommandHandler<AtivarInactivarSubsidioNatalCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AtivarInactivarSubsidioNatalCommandHandler.class);

  private final SubsidioService subsidioNatalService;

  public AtivarInactivarSubsidioNatalCommandHandler(SubsidioService subsidioNatalService) {
    this.subsidioNatalService = subsidioNatalService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(AtivarInactivarSubsidioNatalCommand command) {

    LOGGER.debug("AtivarInactivarSubsidioNatalCommand : {}", command);

    var ano = command.getAtivarinativarsubsidionatal().getAno();

    for (var row : command.getAtivarinativarsubsidionatal().getRows()) {
      subsidioNatalService.activateInactivateSubsidioNatal(
          row.getSubsidioId(),
          ano,
          row.getFuncionarioId(),
          SubsidioNatalStatus.valueOf(row.getStatus()),
          row.getData()
      );
    }
    return ResponseEntity.ok().build();
  }

}

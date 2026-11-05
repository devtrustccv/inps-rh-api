package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.SubsidioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class EditarSubsidioNatalCommandHandler implements CommandHandler<EditarSubsidioNatalCommand, ResponseEntity<Map<String, Object>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EditarSubsidioNatalCommandHandler.class);

  private final SubsidioService subsidioService;

  public EditarSubsidioNatalCommandHandler(SubsidioService subsidioService) {
    this.subsidioService = subsidioService;
  }

  @IgrpCommandHandler
  public ResponseEntity<Map<String, Object>> handle(EditarSubsidioNatalCommand command) {

    LOGGER.debug("EditarSubsidioNatalCommand : {}", command);

    var novoId = subsidioService.editarSubsidioNatal(command.getSubsidioId(), command.getRequest());
    return ResponseEntity.ok(Map.of("subsidioId", novoId));
  }

}

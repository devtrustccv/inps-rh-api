package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.processodisciplinar.ProcessoDisciplinarWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class UpdateProcessoDisciplinarCommandHandler implements CommandHandler<UpdateProcessoDisciplinarCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProcessoDisciplinarCommandHandler.class);

  private final ProcessoDisciplinarWriteService processoDisciplinarService;

  public UpdateProcessoDisciplinarCommandHandler(ProcessoDisciplinarWriteService processoDisciplinarService) {
    this.processoDisciplinarService = processoDisciplinarService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(UpdateProcessoDisciplinarCommand command) {

    LOGGER.debug("Handling UpdateProcessoDisciplinarCommand: {}", command);

    processoDisciplinarService.updateProcessoDisciplinar(command.getProcessoDisciplinarId(), command.getProcessodisciplinarrequest());

    return ResponseEntity.ok().build();
  }

}

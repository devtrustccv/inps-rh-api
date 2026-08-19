package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.funcionario.application.service.processodisciplinar.ProcessoDisciplinarWriteService;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class DeleteProcessoDisciplinarCommandHandler implements CommandHandler<DeleteProcessoDisciplinarCommand, ResponseEntity<SuccessResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteProcessoDisciplinarCommandHandler.class);

  private final ProcessoDisciplinarWriteService processoDisciplinarService;

  public DeleteProcessoDisciplinarCommandHandler(ProcessoDisciplinarWriteService processoDisciplinarService) {
    this.processoDisciplinarService = processoDisciplinarService;
  }

  @IgrpCommandHandler
  public ResponseEntity<SuccessResponseDTO> handle(DeleteProcessoDisciplinarCommand command) {

    LOGGER.debug("Handling DeleteProcessoDisciplinarCommand: {}", command);

    return ResponseEntity.ok(
        processoDisciplinarService.deleteProcessoDisciplinar(command.getProcessoDisciplinarId()));
  }

}

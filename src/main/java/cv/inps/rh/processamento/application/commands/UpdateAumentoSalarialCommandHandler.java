package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.AumentoSalarialWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class UpdateAumentoSalarialCommandHandler implements CommandHandler<UpdateAumentoSalarialCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAumentoSalarialCommandHandler.class);

  private final AumentoSalarialWriteService aumentoSalarialService;

  public UpdateAumentoSalarialCommandHandler(AumentoSalarialWriteService aumentoSalarialService) {
    this.aumentoSalarialService = aumentoSalarialService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(UpdateAumentoSalarialCommand command) {

    LOGGER.debug("UpdateAumentoSalarialCommand : {}", command);

    aumentoSalarialService.updateAumentoSalarial(command.getAumentoSalarialId(), command.getAumentosalarialrequest());

    return ResponseEntity.ok().build();
  }

}

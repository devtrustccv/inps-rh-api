package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.processamento.domain.service.processamentosalarial.AumentoSalarialWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class SaveAumentoSalarialCommandHandler implements CommandHandler<SaveAumentoSalarialCommand, ResponseEntity<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaveAumentoSalarialCommandHandler.class);

  private final AumentoSalarialWriteService aumentoSalarialService;

  public SaveAumentoSalarialCommandHandler(AumentoSalarialWriteService aumentoSalarialService) {
    this.aumentoSalarialService = aumentoSalarialService;
  }

  @IgrpCommandHandler
  public ResponseEntity<String> handle(SaveAumentoSalarialCommand command) {

    LOGGER.debug("SaveAumentoSalarialCommand : {}", command);

    aumentoSalarialService.saveAumentoSalarial(command.getAumentosalarialrequest());

    return ResponseEntity.ok().build();
  }

}

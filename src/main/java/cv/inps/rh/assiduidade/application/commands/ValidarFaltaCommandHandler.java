package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.FaltaServiceWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ValidarFaltaCommandHandler implements CommandHandler<ValidarFaltaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarFaltaCommandHandler.class);

  private final FaltaServiceWrite faltaServiceWrite;

  public ValidarFaltaCommandHandler(FaltaServiceWrite faltaServiceWrite) {

    this.faltaServiceWrite = faltaServiceWrite;
  }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidarFaltaCommand command) {

      LOGGER.debug("ValidarFaltaCommand : {}", command);


      return ResponseEntity.ok(faltaServiceWrite.validarFalta(command));
   }

}

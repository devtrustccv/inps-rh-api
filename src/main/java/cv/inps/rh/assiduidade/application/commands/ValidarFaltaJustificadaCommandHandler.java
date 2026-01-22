package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ValidarFaltaJustificadaCommandHandler implements CommandHandler<ValidarFaltaJustificadaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarFaltaJustificadaCommandHandler.class);

   public ValidarFaltaJustificadaCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidarFaltaJustificadaCommand command) {

      LOGGER.debug("ValidarFaltaJustificadaCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
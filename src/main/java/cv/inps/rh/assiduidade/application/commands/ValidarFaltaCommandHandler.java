package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ValidarFaltaCommandHandler implements CommandHandler<ValidarFaltaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarFaltaCommandHandler.class);

   public ValidarFaltaCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidarFaltaCommand command) {

      LOGGER.debug("ValidarFaltaCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
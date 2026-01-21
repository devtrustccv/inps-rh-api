package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ValidarDispensaCommandHandler implements CommandHandler<ValidarDispensaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarDispensaCommandHandler.class);

   public ValidarDispensaCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidarDispensaCommand command) {

      LOGGER.debug("ValidarDispensaCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
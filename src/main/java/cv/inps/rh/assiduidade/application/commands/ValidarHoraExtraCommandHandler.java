package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ValidarHoraExtraCommandHandler implements CommandHandler<ValidarHoraExtraCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarHoraExtraCommandHandler.class);

   public ValidarHoraExtraCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidarHoraExtraCommand command) {

      LOGGER.debug("ValidarHoraExtraCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
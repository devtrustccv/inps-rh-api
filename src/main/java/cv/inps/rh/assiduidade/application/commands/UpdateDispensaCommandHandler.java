package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class UpdateDispensaCommandHandler implements CommandHandler<UpdateDispensaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDispensaCommandHandler.class);

   public UpdateDispensaCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(UpdateDispensaCommand command) {

      LOGGER.debug("UpdateDispensaCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
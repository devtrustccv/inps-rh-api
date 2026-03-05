package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class UpdateOutputDocumentCommandHandler implements CommandHandler<UpdateOutputDocumentCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateOutputDocumentCommandHandler.class);

   public UpdateOutputDocumentCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(UpdateOutputDocumentCommand command) {

      LOGGER.debug("UpdateOutputDocumentCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
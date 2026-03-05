package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class SaveOutputDocumentCommandHandler implements CommandHandler<SaveOutputDocumentCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveOutputDocumentCommandHandler.class);

   public SaveOutputDocumentCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SaveOutputDocumentCommand command) {

      LOGGER.debug("SaveOutputDocumentCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
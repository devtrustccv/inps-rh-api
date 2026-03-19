package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class SaveMissaoServicoCabimentoCommandHandler implements CommandHandler<SaveMissaoServicoCabimentoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveMissaoServicoCabimentoCommandHandler.class);

   public SaveMissaoServicoCabimentoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SaveMissaoServicoCabimentoCommand command) {

      LOGGER.debug("SaveMissaoServicoCabimentoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
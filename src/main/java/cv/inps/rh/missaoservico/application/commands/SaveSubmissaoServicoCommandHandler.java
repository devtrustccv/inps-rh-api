package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class SaveSubmissaoServicoCommandHandler implements CommandHandler<SaveSubmissaoServicoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveSubmissaoServicoCommandHandler.class);

   public SaveSubmissaoServicoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SaveSubmissaoServicoCommand command) {

      LOGGER.debug("SaveSubmissaoServicoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
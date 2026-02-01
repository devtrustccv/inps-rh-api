package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
public class SaveDecisaoAnaliseCommandHandler implements CommandHandler<SaveDecisaoAnaliseCommand, ResponseEntity<String>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveDecisaoAnaliseCommandHandler.class);

   public SaveDecisaoAnaliseCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<String> handle(SaveDecisaoAnaliseCommand command) {

      LOGGER.debug("SaveDecisaoAnaliseCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}

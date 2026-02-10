package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
public class ValidarMovimentoImportadoCommandHandler implements CommandHandler<ValidarMovimentoImportadoCommand, ResponseEntity<String>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarMovimentoImportadoCommandHandler.class);

   public ValidarMovimentoImportadoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<String> handle(ValidarMovimentoImportadoCommand command) {

      LOGGER.debug("ValidarMovimentoImportadoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}

package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
public class AtualizarCarreiraCommandHandler implements CommandHandler<AtualizarCarreiraCommand, ResponseEntity<String>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AtualizarCarreiraCommandHandler.class);

   public AtualizarCarreiraCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<String> handle(AtualizarCarreiraCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}

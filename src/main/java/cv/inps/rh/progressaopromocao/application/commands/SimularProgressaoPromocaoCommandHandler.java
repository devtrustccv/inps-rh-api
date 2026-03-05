package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class SimularProgressaoPromocaoCommandHandler implements CommandHandler<SimularProgressaoPromocaoCommand, ResponseEntity<String>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SimularProgressaoPromocaoCommandHandler.class);

   public SimularProgressaoPromocaoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<String> handle(SimularProgressaoPromocaoCommand command) {

      LOGGER.debug("SimularProgressaoPromocaoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
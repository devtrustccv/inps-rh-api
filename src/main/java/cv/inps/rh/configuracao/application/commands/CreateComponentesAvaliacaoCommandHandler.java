package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class CreateComponentesAvaliacaoCommandHandler implements CommandHandler<CreateComponentesAvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateComponentesAvaliacaoCommandHandler.class);

   public CreateComponentesAvaliacaoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(CreateComponentesAvaliacaoCommand command) {

      LOGGER.debug("CreateComponentesAvaliacaoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
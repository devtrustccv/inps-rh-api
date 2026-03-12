package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class AvaliacaoCommandHandler implements CommandHandler<AvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AvaliacaoCommandHandler.class);

   public AvaliacaoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(AvaliacaoCommand command) {

      LOGGER.debug("AvaliacaoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
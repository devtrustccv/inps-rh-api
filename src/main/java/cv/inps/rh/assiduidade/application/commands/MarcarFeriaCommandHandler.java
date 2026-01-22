package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class MarcarFeriaCommandHandler implements CommandHandler<MarcarFeriaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(MarcarFeriaCommandHandler.class);

   public MarcarFeriaCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(MarcarFeriaCommand command) {

      LOGGER.debug("MarcarFeriaCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
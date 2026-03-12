package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class AutoAvaliacaoCommandHandler implements CommandHandler<AutoAvaliacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AutoAvaliacaoCommandHandler.class);

   public AutoAvaliacaoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(AutoAvaliacaoCommand command) {

      LOGGER.debug("AutoAvaliacaoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
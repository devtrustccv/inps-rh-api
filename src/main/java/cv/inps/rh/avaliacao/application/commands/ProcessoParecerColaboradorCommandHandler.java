package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ProcessoParecerColaboradorCommandHandler implements CommandHandler<ProcessoParecerColaboradorCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ProcessoParecerColaboradorCommandHandler.class);

   public ProcessoParecerColaboradorCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ProcessoParecerColaboradorCommand command) {

      LOGGER.debug("ProcessoParecerColaboradorCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
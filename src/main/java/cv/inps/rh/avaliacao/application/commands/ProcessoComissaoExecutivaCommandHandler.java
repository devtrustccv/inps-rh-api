package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ProcessoComissaoExecutivaCommandHandler implements CommandHandler<ProcessoComissaoExecutivaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ProcessoComissaoExecutivaCommandHandler.class);

   public ProcessoComissaoExecutivaCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ProcessoComissaoExecutivaCommand command) {

      LOGGER.debug("ProcessoComissaoExecutivaCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
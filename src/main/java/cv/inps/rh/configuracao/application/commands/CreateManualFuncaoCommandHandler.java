package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class CreateManualFuncaoCommandHandler implements CommandHandler<CreateManualFuncaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateManualFuncaoCommandHandler.class);

   public CreateManualFuncaoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(CreateManualFuncaoCommand command) {

      LOGGER.debug("CreateManualFuncaoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
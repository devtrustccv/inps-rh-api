package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class AvancarSubmissaoServicoCommandHandler implements CommandHandler<AvancarSubmissaoServicoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AvancarSubmissaoServicoCommandHandler.class);

   public AvancarSubmissaoServicoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(AvancarSubmissaoServicoCommand command) {

      LOGGER.debug("AvancarSubmissaoServicoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
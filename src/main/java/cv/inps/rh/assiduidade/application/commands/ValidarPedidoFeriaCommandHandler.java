package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ValidarPedidoFeriaCommandHandler implements CommandHandler<ValidarPedidoFeriaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidarPedidoFeriaCommandHandler.class);

   public ValidarPedidoFeriaCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidarPedidoFeriaCommand command) {

      LOGGER.debug("ValidarPedidoFeriaCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
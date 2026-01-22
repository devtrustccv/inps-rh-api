package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class AlterarPedidoFeriaCommandHandler implements CommandHandler<AlterarPedidoFeriaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(AlterarPedidoFeriaCommandHandler.class);

   public AlterarPedidoFeriaCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(AlterarPedidoFeriaCommand command) {

      LOGGER.debug("AlterarPedidoFeriaCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
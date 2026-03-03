package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class ValidacaoPedidoDeclaracaoCommandHandler implements CommandHandler<ValidacaoPedidoDeclaracaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidacaoPedidoDeclaracaoCommandHandler.class);

   public ValidacaoPedidoDeclaracaoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(ValidacaoPedidoDeclaracaoCommand command) {

      LOGGER.debug("ValidacaoPedidoDeclaracaoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
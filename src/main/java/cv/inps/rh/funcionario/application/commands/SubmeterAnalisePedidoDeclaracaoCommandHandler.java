package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class SubmeterAnalisePedidoDeclaracaoCommandHandler implements CommandHandler<SubmeterAnalisePedidoDeclaracaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SubmeterAnalisePedidoDeclaracaoCommandHandler.class);

   public SubmeterAnalisePedidoDeclaracaoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SubmeterAnalisePedidoDeclaracaoCommand command) {

      LOGGER.debug("SubmeterAnalisePedidoDeclaracaoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
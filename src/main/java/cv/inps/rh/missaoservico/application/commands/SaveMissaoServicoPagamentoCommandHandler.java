package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class SaveMissaoServicoPagamentoCommandHandler implements CommandHandler<SaveMissaoServicoPagamentoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveMissaoServicoPagamentoCommandHandler.class);

   public SaveMissaoServicoPagamentoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SaveMissaoServicoPagamentoCommand command) {

      LOGGER.debug("SaveMissaoServicoPagamentoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
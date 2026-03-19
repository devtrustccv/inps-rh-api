package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class SaveMissaoServicoAutorizacaoCommandHandler implements CommandHandler<SaveMissaoServicoAutorizacaoCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveMissaoServicoAutorizacaoCommandHandler.class);

   public SaveMissaoServicoAutorizacaoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(SaveMissaoServicoAutorizacaoCommand command) {

      LOGGER.debug("SaveMissaoServicoAutorizacaoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
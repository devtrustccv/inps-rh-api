package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class CancelarMissaoServicoCommandHandler implements CommandHandler<CancelarMissaoServicoCommand, ResponseEntity<String>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CancelarMissaoServicoCommandHandler.class);

   public CancelarMissaoServicoCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<String> handle(CancelarMissaoServicoCommand command) {

      LOGGER.debug("CancelarMissaoServicoCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class MarcarHoraExtraCommandHandler implements CommandHandler<MarcarHoraExtraCommand, ResponseEntity<String>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(MarcarHoraExtraCommandHandler.class);

   public MarcarHoraExtraCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<String> handle(MarcarHoraExtraCommand command) {

      LOGGER.debug("MarcarHoraExtraCommand : {}", command);

      // TODO: Implement the command handling logic here
      return null;
   }

}
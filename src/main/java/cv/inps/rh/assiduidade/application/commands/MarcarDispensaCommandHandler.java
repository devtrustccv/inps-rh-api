package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.assiduidade.application.services.DispensaWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class MarcarDispensaCommandHandler implements CommandHandler<MarcarDispensaCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(MarcarDispensaCommandHandler.class);

   private final DispensaWriteService dispensaWriteService;
   public MarcarDispensaCommandHandler(DispensaWriteService dispensaWriteService) {

     this.dispensaWriteService = dispensaWriteService;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(MarcarDispensaCommand command) {

      LOGGER.debug("MarcarDispensaCommand : {}", command);


      return ResponseEntity.ok(dispensaWriteService.marcarDispensa(command));
   }

}
